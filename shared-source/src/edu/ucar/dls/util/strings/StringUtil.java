/*
 *  License and Copyright:
 *
 *  The contents of this file are subject to the Educational Community License v1.0 (the "License"); you may
 *  not use this file except in compliance with the License. You should have received a copy of the License
 *  along with this software; if not, you may obtain a copy of the License at
 *  http://www.opensource.org/licenses/ecl1.php.
 *
 *  Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 *  either express or implied. See the License for the specific language governing rights and limitations
 *  under the License.
 *
 *  Copyright 2002-2009 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */

package edu.ucar.dls.util.strings;

/**
 *  String utility class that provides sub-string replacement and quoted string
 *  escaping
 *
 *@author    Ryan Deardorff
 */
public final class StringUtil {

	/**
	 *  Escape any single or double quotes within a string (usefull for strings
	 *  that will be placed inside HTML inputs)
	 *
	 *@param  str
	 */
	public static String escapeQuotes( String str ) {
		if ( ( str.indexOf( "'" ) == -1 ) && ( str.indexOf( '"' ) == -1 ) ) {
			return str;
		}
		String ret = replace( str, "\\", "\\\\", false );
		ret = replace( str, "'", "\\'", false );
		ret = replace( ret, "\"", "\\\"", false );
		return ret;
	}

	/**
	 *  SQL strings use ' as delimeter, so inner quotes must be converted to ''
	 *
	 *@param  str
	 */
	public static String escapeQuotesSQL( String str ) {
		if ( str.indexOf( "'" ) == -1 ) {
			return str;
		}
		String ret = replace( str, "'", "''", false );
		return ret;
	}

	/**
	 *  Given an input string, replace all occurences of a string sequence with a
	 *  given replacement string.
	 *
	 *@param  in               input string
	 *@param  find             string sequence match against
	 *@param  replace          string to replace matches with
	 *@param  caseInsensitive  should matching be case insensitive?
	 *@return                  the newly altered string
	 */
	public static String replace( String in,
	                              String find,
	                              String replace,
	                              boolean caseInsensitive ) {
		String match;
		if ( caseInsensitive ) {
			match = in.toLowerCase();
			find = find.toLowerCase();
		}
		else {
			match = in;
		}
		StringBuffer ret = new StringBuffer();
		int ind1 = 0;
		int ind2 = match.indexOf( find );
		int findLength = find.length();
		while ( ind2 > -1 ) {
			ret.append( in.substring( ind1, ind2 ) )
				.append( replace );
			ind1 = ind2 + findLength;
			ind2 = match.indexOf( find, ind1 );
		}
		ret.append( in.substring( ind1 ) );
		return ret.toString();
	}
}

