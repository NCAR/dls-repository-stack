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
 *  Copyright 2002-2011 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */
package edu.ucar.dls.repository;

import edu.ucar.dls.util.Files;
import edu.ucar.dls.repository.action.form.SetDefinitionsForm;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.Dom4jNodeListComparator;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.index.reader.DocReader;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.dom4j.Element;
import org.dom4j.Attribute;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import org.apache.lucene.queryParser.*;
import org.apache.lucene.search.*;
import org.apache.lucene.index.Term;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 *  Reads the XML configuration file used to define OAI sets in the {@link RepositoryManager} and creates bean
 *  methods for each set that is defined using matching field/value pairs. This class does not handle the sets
 *  in the file that are defined using search queries. Those are handled by VirtualSearchFieldMapper instead.
 *  <p>
 *
 *  A record is considered in the set if at least one of the field/value pairs is stored in the Lucene
 *  Document for that record. <p>
 *
 *  Note that a new instance should be created each time the underlying file changes.<p>
 *
 *  See <a href="../../../../javadoc-includes/ListSets-config-sample.xml">sample ListSets XML config file</a>
 *  .
 *
 * @author    John Weatherley
 */
public class OAISetsMatchingFieldsBean {
	private static boolean debug = false;

	private Document listSetsDom = null;
	private List matchingFieldsSetsList = null;
	private Map fieldValuesMaps = null;


	/**
	 *  Constructs the Bean. Note that a new instance should be created each time the underlying file changes.
	 *
	 * @param  listSetsXML  The ListSets XML
	 */
	OAISetsMatchingFieldsBean(String listSetsXML) {
		try {
			listSetsDom = Dom4jUtils.getXmlDocument(listSetsXML);

			matchingFieldsSetsList = listSetsDom.selectNodes("/ListSets/set[string-length(matchingFieldValues/matchingFieldValue/@field) > 0]");

			prtln("matchingFieldsSetsList.length = " + matchingFieldsSetsList.size());
		} catch (Throwable t) {
			prtlnErr("Unable to initialize OAISetsMatchingFieldsBean: " + t);
		}
	}


	OAISetsMatchingFieldsBean() { }


	/**
	 *  Gets the numSets attribute of the OAISetsMatchingFieldsBean object
	 *
	 * @return    The numSets value
	 */
	public int getNumSets() {
		if (matchingFieldsSetsList == null)
			return 0;
		return matchingFieldsSetsList.size();
	}


	/**
	 *  Determine of the give setSpec is defined.
	 *
	 * @param  setSpec  setSpec
	 * @return          True if defined
	 */
	public boolean hasSetDefined(String setSpec) {
		if (listSetsDom == null)
			return false;
		return listSetsDom.matches("/ListSets/set[string-length(matchingFieldValues/matchingFieldValue/@field) > 0][setSpec='" + setSpec + "']");
	}


	/**
	 *  Gets the oaiSetSpecs attribute of the OAISetsMatchingFieldsBean object
	 *
	 * @return    The oaiSetSpecs value
	 */
	public List getOaiSetSpecs() {
		List setSpecs = new ArrayList();
		if (matchingFieldsSetsList != null) {
			for (int i = 0; i < matchingFieldsSetsList.size(); i++) {
				String setSpec = ((Node) matchingFieldsSetsList.get(i)).valueOf("setSpec");
				setSpecs.add(setSpec);
			}
		}
		//prtln("getOaiSetSpecs(): " + setSpecs);
		return setSpecs;
	}


	/**
	 *  Gets the OAI SetSpecs associated with the given Lucene Document. The record is considered in the set if
	 *  at least one of the field/value pairs is stored in the Lucene Document for that record.
	 *
	 * @param  luceneDoc  Lucene Document for the record
	 * @return            The oaiSetSpecs value
	 */
	public List getOaiSetSpecs(org.apache.lucene.document.Document luceneDoc) {
		// Generate the fieldValuesMaps, if necessary (one time then cache):
		if (fieldValuesMaps == null) {
			fieldValuesMaps = new TreeMap();
			if (matchingFieldsSetsList != null) {
				for (int i = 0; i < matchingFieldsSetsList.size(); i++) {
					Node setNode = (Node) matchingFieldsSetsList.get(i);
					String setSpec = ((Node) matchingFieldsSetsList.get(i)).valueOf("setSpec");
					List matchingFieldValueNodes = setNode.selectNodes("matchingFieldValues/matchingFieldValue");
					for (int j = 0; j < matchingFieldValueNodes.size(); j++) {
						Node matchingFieldValueNode = (Node) matchingFieldValueNodes.get(j);
						String field = matchingFieldValueNode.valueOf("@field");
						String value = matchingFieldValueNode.valueOf("@value");
						// Grab the field values map for this field:
						Map fieldValuesMap = (Map) fieldValuesMaps.get(field);
						if (fieldValuesMap == null)
							fieldValuesMap = new TreeMap();
						// Grab the value sets list for this field/value:
						List valueSetsList = (List) fieldValuesMap.get(value);
						if (valueSetsList == null)
							valueSetsList = new ArrayList();
						if (!valueSetsList.contains(setSpec))
							valueSetsList.add(setSpec);
						// Insert new values into the Maps:
						fieldValuesMap.put(value, valueSetsList);
						fieldValuesMaps.put(field, fieldValuesMap);
					}
				}
			}
			//prtln("fieldValuesMaps is: " + fieldValuesMaps);
		}

		// Gather the list of sets for the Document:
		List setList = new ArrayList();
		Iterator fields = fieldValuesMaps.keySet().iterator();
		while (fields.hasNext()) {
			String field = (String) fields.next();

			String[] myValues = luceneDoc.getValues(field);
			//prtln("field:" + field + " myValues: " + Arrays.toString(myValues));

			if (myValues != null) {
				for (int jj = 0; jj < myValues.length; jj++) {
					String myValue = myValues[jj];
					Map fieldValuesMap = (Map) fieldValuesMaps.get(field);
					List valueSetsList = (List) fieldValuesMap.get(myValue);
					if (valueSetsList != null) {
						for (int i = 0; i < valueSetsList.size(); i++) {
							Object setSpec = valueSetsList.get(i);
							if (!setList.contains(setSpec))
								setList.add(setSpec);
						}
					}
				}
			}
		}
		//prtln("getOaiSetSpecs(): " + setList);
		return setList;
	}


	/**
	 *  Gets the Lucene Query for the given setSpec. A record is considered in the set if at least one of the
	 *  field/value pairs is stored in the Lucene Document for that record.
	 *
	 * @param  setSpec  The setSpec
	 * @return          The queryForSet value
	 */
	public Query getQueryForSet(String setSpec) {
		try {
			if (listSetsDom == null)
				return null;
			Node matchingSetElm = listSetsDom.selectSingleNode("/ListSets/set[string-length(matchingFieldValues/matchingFieldValue/@field) > 0][setSpec='" + setSpec + "']");
			if (matchingSetElm == null)
				return null;
			List matchingFieldValueNodes = matchingSetElm.selectNodes("matchingFieldValues/matchingFieldValue");
			if (matchingFieldValueNodes == null || matchingFieldValueNodes.size() == 0)
				return null;

			BooleanQuery bq = new BooleanQuery();
			for (int i = 0; i < matchingFieldValueNodes.size(); i++) {
				String field = ((Node) matchingFieldValueNodes.get(i)).valueOf("@field");
				String term = ((Node) matchingFieldValueNodes.get(i)).valueOf("@value");
				bq.add(new TermQuery(new Term(field, term)), BooleanClause.Occur.MUST);
			}
			//prtln("getQueryForSet() query: " + bq);
			return bq;
		} catch (Throwable t) {
			prtlnErr("Error getQueryForSet(): " + t);
			return null;
		}
	}


	//================================================================


	/**
	 *  Return a string for the current time and date, sutiable for display in log files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	private final static String getDateStamp() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final static void prtlnErr(String s) {
		System.err.println(getDateStamp() + " OAISetsMatchingFieldsBean ERROR: " + s);
	}



	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final static void prtln(String s) {
		if (debug) {
			System.out.println(getDateStamp() + " OAISetsMatchingFieldsBean: " + s);
		}
	}


	/**
	 *  Sets the debug attribute of the object
	 *
	 * @param  db  The new debug value
	 */
	public static void setDebug(boolean db) {
		debug = db;
	}

}

