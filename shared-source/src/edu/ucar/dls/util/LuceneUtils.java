/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package edu.ucar.dls.util;

import java.util.Date;
import java.util.Random;
import java.util.Enumeration;
import java.util.*;
import java.text.*;
import java.net.URLEncoder;
import java.io.*;

import org.apache.lucene.queryParser.QueryParser;
import edu.ucar.dls.index.document.DateFieldTools;
import edu.ucar.dls.index.*;

/**
 *  Utility methods for working with Lucene.
 *
 * @author    John Weatherley
 */
public class LuceneUtils {

	/**
	 *  Make a Lucene boolean clause suitable for use with the Lucene QueryParser. <p>
	 *
	 *  Examples:<p>
	 *
	 *  input: strings=['car','train station','airplane'], operator=OR, asPhrases=true, escape=false<br/>
	 *  output: ("car" OR "train station" OR "airplane")<p>
	 *
	 *  input: strings=['car','train station','airplane'], operator=AND, asPhrases=false, escape=false<br/>
	 *  output: ((car) AND (train station) AND (airplane))
	 *
	 * @param  strings        One or more Strings
	 * @param  operator       The String OR, AND, or NOT
	 * @param  asPhrases      True to treat each String as a phrase (e.g. surround in quotes)
	 * @param  escape         True to return a String where characters that QueryParser expects to be escaped are
	 *      escaped by a preceding \.
	 * @return                A boolean clause
	 * @exception  Exception  If error
	 * @see                   org.apache.lucene.queryParser.QueryParser
	 */
	public final static String makeBooleanClause(String[] strings, String operator, boolean asPhrases, boolean escape) throws Exception {
		if (operator == null || (!operator.equals("OR") && !operator.equals("AND") && !operator.equals("NOT")))
			throw new Exception("boolean operator must be 'OR' or 'AND' or 'NOT'");

		if (strings == null || strings.length == 0)
			return "";
		String queryClause = "(";
		for (int i = 0; i < strings.length; i++) {
			if (escape)
				queryClause += (asPhrases ? "\"" : "(") + QueryParser.escape(strings[i]) + (asPhrases ? "\"" : ")");
			else
				queryClause += (asPhrases ? "\"" : "(") + strings[i] + (asPhrases ? "\"" : ")");
			if (i < strings.length - 1)
				queryClause += " " + operator + " ";
		}
		queryClause += ")";
		return queryClause;
	}

	
	/**
	*  Same as {@link #makeBooleanClause}.
	 *
	 * @param  strings        A List that of one or more Strings
	 * @param  operator       The String OR, AND, or NOT
	 * @param  asPhrases      True to treat each String as a phrase (e.g. surround in quotes)
	 * @param  escape         True to return a String where characters that QueryParser expects to be escaped are
	 *      escaped by a preceding \.
	 * @return                A boolean clause
	 * @exception  Exception  If error
	 * @see                   org.apache.lucene.queryParser.QueryParser
	 */
	public final static String makeBooleanClause(List strings, String operator, boolean asPhrases, boolean escape) throws Exception {
		return makeBooleanClause((String[])strings.toArray(new String[]{}), operator, asPhrases, escape);
	}	
	

	/**
	 *  Escapes all Lucene QueryParser reserved characters with a preceeding \. The resulting String will be
	 *  interpereted by the QueryParser as a single term.
	 *
	 * @param  term  The original String
	 * @return       The escaped term
	 * @see          org.apache.lucene.queryParser.QueryParser#escape(String)
	 */
	public final static String escape(String term) {
		return SimpleLuceneIndex.escape(term);
	}


	/**
	 *  Escapes the Lucene QueryParser reserved characters with a preceeding \ except those included in
	 *  preserveChars.
	 *
	 * @param  term           The original String
	 * @param  preserveChars  List of characters NOT to escape
	 * @return                The escaped term
	 * @see                   org.apache.lucene.queryParser.QueryParser#escape(String)
	 */
	public final static String escape(String term, String preserveChars) {
		return SimpleLuceneIndex.escape(term, preserveChars);
	}


	/**
	 *  Converts a Lucene String-encoded date to a Date Object. If the String can not be converted, returns null.
	 *
	 * @param  dateString  A Lucene String-encoded date
	 * @return             A Date Object, or null
	 */
	public final static Date luceneStringToDate(String dateString) {
		try {
			return DateFieldTools.stringToDate(dateString);
		} catch (Throwable t) {
			return null;
		}
	}


	/**
	 *  Converts a date String of the form YYYY-mm-dd, YYYY-mm, YYYY or yyyy-MM-ddTHH:mm:ssZ to a searchable
	 *  Lucene (v2.x) lexical date String of the form 'yyyyMMddHHmmss', or null if unable to parse the date
	 *  String.
	 *
	 * @param  dateString  A date String
	 * @return             The corresponding lexicalDateString value
	 */
	public final static String getLexicalDateString(String dateString) {
		try {
			return DateFieldTools.dateToString(MetadataUtils.parseDate(dateString));
		} catch (Throwable t) {
			return null;
		}
	}


	/**
	 *  Encodes a String to a single term for searching over fields that have been indexed encoded. Encodes
	 *  spaces but leaves the wild card '*' un-encoded for searching.
	 *
	 * @param  string  A String to encode
	 * @return         An encoded String that may be used for searches
	 * @see            edu.ucar.dls.index.SimpleLuceneIndex#encodeToTerm(String, boolean, boolean)
	 */
	public final static String encodeToSearchTerm(String string) {
		return SimpleLuceneIndex.encodeToTerm(string, false, true);
	}


	/**
	 *  Encodes a String to a String for searching over fields that have been indexed encoded. Preserves the wild
	 *  card '*' and spaces ' '.
	 *
	 * @param  string  The String to encode
	 * @return         An encoded String that may be used for searches
	 * @see            edu.ucar.dls.index.SimpleLuceneIndex#encodeToTerm(String, boolean, boolean)
	 */
	public final static String encodeToSearchTerms(String string) {
		return SimpleLuceneIndex.encodeToTerm(string, false, false);
	}

}

