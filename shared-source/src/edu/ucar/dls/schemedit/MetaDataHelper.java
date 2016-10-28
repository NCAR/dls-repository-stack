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

package edu.ucar.dls.schemedit;

import org.dom4j.*;
import java.util.*;
import edu.ucar.dls.xml.schema.SchemaHelper;
import edu.ucar.dls.xml.schema.DocMap;
import edu.ucar.dls.xml.Dom4jUtils;


/**
 *  NOT YET DOCUMENTED
 *
 * @author     Jonathan Ostwald
 * @version    $Id: MetaDataHelper.java,v 1.3 2009/03/20 23:33:55 jweather Exp $
 */
public class MetaDataHelper {

	/**  NOT YET DOCUMENTED */
	public static boolean debug = true;


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  id                   NOT YET DOCUMENTED
	 * @param  pathValueMap         NOT YET DOCUMENTED
	 * @param  collectionFramework  NOT YET DOCUMENTED
	 * @return                      NOT YET DOCUMENTED
	 * @exception  Exception        NOT YET DOCUMENTED
	 */
	public static Document makeCollectionDoc(String id, Map pathValueMap, MetaDataFramework collectionFramework)
		 throws Exception {

		SchemaHelper schemaHelper = collectionFramework.getSchemaHelper();
		if (schemaHelper == null) {
			prtln("makeCollectionDoc: schemaHelper is null!");
		}

		// create a miminal document and then insert values from csForm
		Document doc = collectionFramework.makeMinimalRecord(id);

		// use docMap as wraper for Document
		DocMap docMap = new DocMap(doc, schemaHelper);
		// load the dlese-collect metadata document with values from the form
		// pattern for smartPut: docMap.smartPut(xpath, value)
		for (Iterator i = pathValueMap.keySet().iterator(); i.hasNext(); ) {
			try {
				String xpath = (String) i.next();
				String val = (String) pathValueMap.get(xpath);
				docMap.smartPut(xpath, val);
			} catch (Exception e) {
				prtln(e.getMessage());
			}
		}

		try {
			// now prepare document to write to file by inserting namespace information
			doc = collectionFramework.getWritableRecord (doc);

		} catch (Exception e) {
			throw new Exception("makeCollection error: " + e);
		}
		return doc;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {

			while (s.length() > 0 && s.charAt(0) == '\n') {
				System.out.println("");
				s = s.substring(1);
			}

			System.out.println(s);
		}
	}
}


