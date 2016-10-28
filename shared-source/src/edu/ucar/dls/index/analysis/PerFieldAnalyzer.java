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
package edu.ucar.dls.index.analysis;

import edu.ucar.dls.index.SimpleLuceneIndex;
import java.io.Reader;
import java.util.*;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;

/**
 *  This Analyzer is used to facilitate scenarios where different fields require different analysis
 *  techniques. Use {@link #addAnalyzer} to add a non-default Analyzer or {@link #addAnalyzersInBundle} to
 *  provide a ResourceBundle to configure Analyzers on a field name basis. The ResourceBundle should contain
 *  className=field1,field2,... pairs, where the field names are a comma-separated list, for example:<p>
 *
 *  <pre>
 *org.apache.lucene.analysis.snowball.SnowballAnalyzer=stems,titlestems
 *</pre> <p>
 *
 *  Use the class name edu.ucar.dls.index.analysis.BigTokenAnalyzer to indicate a StandardAnalyzer that
 *  allows tokens up to 1024 characters (default is 256). Note that this class does not exist but is rather
 *  just an instance of StandardAnalyzer with a max token size set to 1024.
 *
 * @author    John Weatherley
 */
public class PerFieldAnalyzer extends Analyzer {

	private Analyzer defaultAnalyzer;
	private Map analyzerMap = new HashMap();

	/* Max length for a single token for the standard analyzer long tokens instance (default is 256)*/
	private final int MAX_TOKEN_LEN_FOR_LONG_STANDARD_TOKENIZER = 1024;

	private org.apache.lucene.analysis.standard.StandardAnalyzer standardAnalyzer = new org.apache.lucene.analysis.standard.StandardAnalyzer(SimpleLuceneIndex.getLuceneVersion());
	private org.apache.lucene.analysis.standard.StandardAnalyzer standardAnalyzerLongTokens = new org.apache.lucene.analysis.standard.StandardAnalyzer(SimpleLuceneIndex.getLuceneVersion());
	{ // Initialize global analyzer defaults:
		standardAnalyzerLongTokens.setMaxTokenLength(MAX_TOKEN_LEN_FOR_LONG_STANDARD_TOKENIZER);
	}

	/**  Default text analyzer */
	public final static String TEXT_ANALYZER = "org.apache.lucene.analysis.standard.StandardAnalyzer";
	/**  Default keyword analyzer */
	public final static String KEYWORD_ANALYZER = "org.apache.lucene.analysis.KeywordAnalyzer";
	/**  Default stemming analyzer */
	public final static String STEMS_ANALYZER = "org.apache.lucene.analysis.snowball.SnowballAnalyzer";

	// These default analyzers should match the above:
	private Analyzer defaultTextFieldAnalyzer = standardAnalyzer;
	private Analyzer defaultKeywordFieldAnalyzer = new org.apache.lucene.analysis.KeywordAnalyzer();
	private Analyzer defaultStemsFieldAnalyzer = new org.apache.lucene.analysis.snowball.SnowballAnalyzer(SimpleLuceneIndex.getLuceneVersion(), "English");


	/**
	 *  Constructs with the given Analyzer to use as a default for fields not otherwise configured. If null, a
	 *  {@link org.apache.lucene.analysis.standard.StandardAnalyzer} will be used as the default.
	 *
	 * @param  defaultAnalyzer  Any fields not specifically defined to use a different analyzer will use the one
	 *      provided here.
	 */
	public PerFieldAnalyzer(Analyzer defaultAnalyzer) {
		prtln("PerFieldAnalyzer() constructor");
		if (defaultAnalyzer == null)
			this.defaultAnalyzer = new StandardAnalyzer(SimpleLuceneIndex.getLuceneVersion());
		this.defaultAnalyzer = defaultAnalyzer;
	}


	/**
	 *  Constructs using a {@link org.apache.lucene.analysis.standard.StandardAnalyzer} as the default for fields
	 *  not otherwise configured.
	 */
	public PerFieldAnalyzer() {
		prtln("PerFieldAnalyzer() constructor");
		defaultAnalyzer = new StandardAnalyzer(SimpleLuceneIndex.getLuceneVersion());
	}


	/**
	 *  Sets the Analyzer to use for the specified search field, overridding the previous one if it existed.
	 *
	 * @param  fieldName                   field name requiring a non-default analyzer.
	 * @param  analyzerClassName           Name of Analyzer class to use for the field
	 * @exception  ClassNotFoundException  If error
	 * @exception  InstantiationException  If error
	 * @exception  IllegalAccessException  If error
	 */
	public void setAnalyzer(String fieldName, String analyzerClassName)
		 throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		// Instantiate only one of any given Analyzer:
		Object analyzer = null;
		// Check first to see if we already have an instance of the given Analyzer
		Object[] currentAnalyzers = analyzerMap.values().toArray();
		for (int i = 0; i < currentAnalyzers.length; i++) {
			if (currentAnalyzers[i].getClass().getName().equals(analyzerClassName)) {
				analyzer = currentAnalyzers[i];
				break;
			}
		}

		// Instantiate the Analyzer if it does not already exist:
		if (analyzer == null) {
			if (analyzerClassName.equals(standardAnalyzer.getClass().getName()))
				analyzer = standardAnalyzer;
			// Use our existing SnowballAnalyzer for the stemmer class (initialized as needed):
			else if (analyzerClassName.equals(defaultStemsFieldAnalyzer.getClass().getName()))
				analyzer = defaultStemsFieldAnalyzer;
			// Catch the old SnowballAnalyzer class in case it is still configured somewhere:
			else if (analyzerClassName.equals("edu.ucar.dls.index.analysis.SnowballAnalyzer"))
				analyzer = defaultStemsFieldAnalyzer;	
			// Use standardAnalyzerLongTokens Analyzer for long tokens:
			else if (analyzerClassName.equals("edu.ucar.dls.index.analysis.BigTokenAnalyzer"))
				analyzer = standardAnalyzerLongTokens;			
			else {
				Class analyzerClass = Class.forName(analyzerClassName);
				analyzer = analyzerClass.newInstance();
			}
		}

		analyzerMap.put(fieldName, analyzer);
	}


	/**
	 *  String representation of the configured analyzer Map.
	 *
	 * @return    String representation of the configured analyzer Map.
	 */
	public String toString() {
		return analyzerMap.toString();
	}


	/**
	 *  Adds the Analyzers to use for given fields, using the field=className pairs provided in the
	 *  ResourceBundle, overrridding any previous ones if they existed. The ResourceBundle should contain
	 *  className=field1,field2,... pairs, where the field names are a comma-separated list, for example:<p>
	 *
	 *  <pre>
	 *org.apache.lucene.analysis.snowball.SnowballAnalyzer=stems,titlestems
	 *</pre>
	 *
	 * @param  fieldAnalyzerBundle         A resource bundle containing className=field1,field2,etc. pairs
	 * @exception  ClassNotFoundException  If error
	 * @exception  InstantiationException  If error
	 * @exception  IllegalAccessException  If error
	 */
	public void addAnalyzersInBundle(ResourceBundle fieldAnalyzerBundle)
		 throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (fieldAnalyzerBundle != null) {
			Enumeration analyzerClassNames = fieldAnalyzerBundle.getKeys();
			while (analyzerClassNames.hasMoreElements()) {
				String className = (String) analyzerClassNames.nextElement();

				String[] fieldNames = fieldAnalyzerBundle.getString(className).trim().split(",");
				for (int i = 0; i < fieldNames.length; i++) {
					setAnalyzer(fieldNames[i].trim(), className);
				}
			}
		}
	}


	/**
	 *  Gets the Analyzer configured for the given field, or null if none exists.
	 *
	 * @param  fieldName  The field name
	 * @return            The Analyzer
	 */
	public Analyzer getAnalyzer(String fieldName) {
		if (fieldName == null)
			return null;
		Analyzer zer = (Analyzer) analyzerMap.get(fieldName);
		if (zer != null)
			return zer;
		if (fieldName.indexOf("/text//") != -1)
			return defaultTextFieldAnalyzer;
		if (fieldName.indexOf("/stems//") != -1)
			return defaultStemsFieldAnalyzer;
		if (fieldName.indexOf("/key//") != -1)
			return defaultKeywordFieldAnalyzer;
		if (fieldName.startsWith("assignsRelationshipById"))
			return defaultKeywordFieldAnalyzer;
		if (fieldName.startsWith("assignsRelationshipByUrl"))
			return defaultKeywordFieldAnalyzer;
		if (fieldName.startsWith("indexedRelationIds"))
			return defaultKeywordFieldAnalyzer;
		if (fieldName.startsWith("isRelatedToByCollectionKey"))
			return defaultKeywordFieldAnalyzer;
		return null;
	}


	/**
	 *  Gets the default Analyzer being used.
	 *
	 * @return    The default Analyzer
	 */
	public Analyzer getDefaultAnalyzer() {
		return defaultAnalyzer;
	}


	/**
	 *  Sets the default Analyzer to use from here forth.
	 *
	 * @param  analyzer  The new default Analyzer
	 */
	public void setDefaultAnalyzer(Analyzer analyzer) {
		if (analyzer != null)
			defaultAnalyzer = analyzer;
	}


	/**
	 *  Determines if an Analyzer is configured for the given field.
	 *
	 * @param  fieldName  The field name
	 * @return            True if an Analyzer is configured for the given field
	 */
	public boolean containsAnalyzer(String fieldName) {
		if (fieldName == null)
			return false;
		if (fieldName.startsWith("/text/") || fieldName.startsWith("/key/"))
			return true;
		return analyzerMap.containsKey(fieldName);
	}


	/**
	 *  Removes the Analyzer that is configured for the given field, if one exists. After removing, the given
	 *  will will use the default Analyzer.
	 *
	 * @param  fieldName  The field name
	 * @return            The Analyzer that was configured, or null
	 */
	public Analyzer removeAnalyzer(String fieldName) {
		if (fieldName == null)
			return null;
		return (Analyzer) analyzerMap.remove(fieldName);
	}


	/**
	 *  Generates a token stream for the given field.
	 *
	 * @param  fieldName  The field name
	 * @param  reader     The Reader
	 * @return            The TokenStream appropriate for this field
	 */
	public TokenStream tokenStream(String fieldName, Reader reader) {
		Analyzer zer = getAnalyzer(fieldName);
		if (zer == null)
			zer = defaultAnalyzer;

		prtln("Using: " + zer.getClass().getName() + " for field: " + fieldName);

		return zer.tokenStream(fieldName, reader);
	}


	private final static void prtln(String s) {
		//System.out.println("PerFieldAnalyzer: " + s);
	}


	private final static void prtlnErr(String s) {
		System.err.println("PerFieldAnalyzer Error: " + s);
	}
}

