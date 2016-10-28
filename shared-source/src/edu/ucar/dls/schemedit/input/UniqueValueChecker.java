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
package edu.ucar.dls.schemedit.input;

import edu.ucar.dls.schemedit.FrameworkRegistry;
import edu.ucar.dls.schemedit.MetaDataFramework;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.config.*;
import edu.ucar.dls.repository.RepositoryManager;
import edu.ucar.dls.index.SimpleLuceneIndex;
import edu.ucar.dls.index.ResultDoc;
import edu.ucar.dls.index.ResultDocList;
import edu.ucar.dls.index.reader.XMLDocReader;

import java.util.*;
import javax.servlet.ServletContext;

/**
 *  Checks for duplicate values in the same record as a "reference" record at a
 *  specified path.<p>
 *
 *  Used to enforce SchemaPaths that are configured in the framework
 *  configuration as "uniqueValue" paths.
 *
 * @author    ostwald
 */
public class UniqueValueChecker {

	static boolean debug = false;
	ServletContext servletContext;
	SimpleLuceneIndex index = null;


	/**
	 *  Constructor for the UniqueValueChecker object, requiring ServletContext.
	 *
	 * @param  servletContext
	 * @exception  Exception   if required helper objects cannot be found in the
	 *      servlet context initialized.
	 */
	public UniqueValueChecker(ServletContext servletContext) throws Exception {
		this.servletContext = servletContext;
		RepositoryManager rm = (RepositoryManager) servletContext.getAttribute("repositoryManager");
		if (rm == null) {
			throw new Exception("RepositoryManger not found");
		}

		index = rm.getIndex();
		if (index == null) {
			throw new Exception("Index not found");
		}
	}


	// ============== Private Helper methods ========================


	/**
	 *  Returns a list of RecordIds in a that contain a value equal to provided
	 *  referenceValue at the provided xpath<p>
	 *
	 *  NOTE: This check is only performed if the framework configuration defines a
	 *  schemaPath with valueType of "uniqueValue".
	 *
	 * @param  referenceValue      The value for which we try to find dups
	 * @param  referenceDocReader  docReader for the record containing
	 *      referenceValue
	 * @param  xpath               xpath at which value is found
	 * @return                     List of DupSim instances
	 */
	public List getDupValues(String referenceValue, XMLDocReader referenceDocReader, String xpath) {
		prtln("getDupValues ... ");

		String collection = referenceDocReader.getCollection();
		String referenceID = referenceDocReader.getId();

		List dups = new ArrayList();

		String query = "((collection:0*) AND collection:0" + collection + ") AND ";
		query += "/key/" + xpath + ":" + SchemEditUtils.quoteWrap(referenceValue);

		// prtln("uniqueValue query: " + query);

		// search for results having the specified value at the specified xpath.
		ResultDocList results = index.searchDocs(query);
		prtln(results.size() + " results found");
		if (results != null) {
			for (int i = 0; i < results.size(); i++) {
				XMLDocReader docReader = (XMLDocReader) ((ResultDoc)results.get(i)).getDocReader();
				String id = docReader.getId();
				if (!referenceID.equals(id))
					dups.add(docReader.getId());
			}
		}
		prtln("  ...returning " + dups.size() + " dups");
		return dups;
	}


	/**
	 *  Gets a formated date string for current time.
	 *
	 * @return    The dateString value
	 */
	public static String getDateString() {
		return SchemEditUtils.fullDateString(new Date());
	}


	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "UniqueValueChecker");
		}
	}


	/**
	 *  Output message to the console
	 *
	 * @param  s  string to be printed
	 */
	static void prtlnErr(String s) {
		SchemEditUtils.prtln(s, "UniqueValueChecker");
	}
}

