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

package edu.ucar.dls.schemedit.test;

import java.io.*;
import java.util.*;
import java.text.*;

import edu.ucar.dls.xml.*;
import edu.ucar.dls.util.*;
import edu.ucar.dls.repository.*;
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.config.*;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Attribute;
import org.dom4j.Node;

/**
 *  Tester for {@link edu.ucar.dls.schemedit.config.FrameworkConfigReader}.
 *
 *@author    ostwald
 <p>$Id: CollectionConfigTester.java,v 1.11 2012/11/22 00:02:32 ostwald Exp $
 */
public class CollectionConfigTester {
	CollectionConfigReader reader = null;
	CollectionConfig collectionConfig = null;
	CollectionRegistry registry = null;


	/**
	 *  Constructor for the CollectionConfigTester object
	 */
	public CollectionConfigTester(String configFileName) throws Exception {
		// String configDirPath = "/devel/ostwald/tomcat/tomcat/webapps/schemedit/WEB-INF/collection-config";
		String configDirPath = TesterUtils.getCollectionConfigDir();
		File sourceFile = new File(configDirPath, configFileName);
		if (!sourceFile.exists()) {
			prtln("source File does not exist at " + sourceFile.toString());
			return;
		}
 		reader = new CollectionConfigReader(sourceFile);
		prtln ("hellooo");
		collectionConfig = new CollectionConfig(reader);

		// collectionConfig = new CollectionConfig (sourceFile, null);

		// registry = new CollectionRegistry(new File (configDirPath));
	}

	public CollectionConfig getCollectionConfig() {
		return collectionConfig;
	}

	/**
	 *  Description of the Method
	 */
	public void showCollectionConfig() {
		prtln("CollectionConfig");
		prtln(collectionConfig.toString());
	}


	/**
	 *  Description of the Method
	 */
	public void showRegistry() {
		prtln("REGISTRY");
		prtln(registry.toString());
	}


	/**
	 *  Description of the Method
	 */
	public void showTester() {
		prtln ("id: " + reader.getId());
		Map statusMap = reader.getStatusMap();
		prtln("statuses");
		for (Iterator i = statusMap.keySet().iterator();i.hasNext();) {
			String collection = (String)i.next();
			String description = (String) statusMap.get(collection);
			prtln (collection + ": " + description);
		}
	}

	void showRegisteredCollections () {
		Set ids = registry.getIds();
		prtln ("Registered Collections (" + ids.size() + ")");
		for (Iterator i=ids.iterator();i.hasNext();) {
			String id = (String)i.next();
			CollectionConfig info = registry.getCollectionConfig (id);
			CollectionConfigReader configReader = info.getConfigReader();
			prtln (id);
		}
	}

	void inspect (String id) {
		CollectionConfig info = registry.getCollectionConfig(id);

		String name = "my tuple name";
		prtln (name + " : " + info.getTupleValue(name));
		info.setTupleValue (name, "i'm changed");
		prtln (name + " : " + info.getTupleValue(name));
	}
	/**
	 *  The main program for the CollectionConfigTester class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args) {

		prtln("CollectionConfigTester");
		CollectionConfigTester tester = null;
		String configFileName = "1102611887199.xml";
		try {
			tester = new CollectionConfigTester(configFileName);
		} catch (Exception e) {
			prtln (e.getMessage());
			return;
		}

		tester.showCollectionConfig();

		prtln ("Collection config doc at " + tester.reader.getSourcePath());
		Document doc = tester.reader.getDocument();
		prtln (Dom4jUtils.prettyPrint(doc));

		tester.showTupleMap();

		prtln ("termsOfUse: " + tester.collectionConfig.getTupleValue("termsOfUse"));

		/*
		String id = "1113334213468";
		tester.inspect (id);
		*/
	}

	void showTupleMap () {
		prtln ("\nShowTupleMap");
		Map tupleMap = collectionConfig.getTupleMap();
		if (tupleMap == null) {
			prtln ("tupleMap is NULL");
			return;
		}
		prtln (tupleMap.size() + " entries:");
		for (Iterator i=tupleMap.keySet().iterator();i.hasNext();) {
			String pathName = (String)i.next();
			String pathValue = (String)tupleMap.get (pathName);
			prtln ("\t" + pathName + ": " + pathValue);
		}
	}

	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	public static void prtln(String s) {
		SchemEditUtils.prtln (s, "");
	}
}
