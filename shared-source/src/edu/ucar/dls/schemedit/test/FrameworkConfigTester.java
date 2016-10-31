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
import edu.ucar.dls.schemedit.dcs.*;
import edu.ucar.dls.schemedit.vocab.layout.*;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Attribute;
import org.dom4j.Node;

/**
 *  Tester for {@link edu.ucar.dls.schemedit.config.FrameworkConfigReader}
 *
 *@author    ostwald
 <p>$Id: FrameworkConfigTester.java,v 1.11 2012/11/22 00:02:33 ostwald Exp $
 */
public class FrameworkConfigTester {
	public FrameworkConfigReader reader = null;
	public MetaDataFramework framework = null;
	FrameworkRegistry registry = null;

	// String configDirPath = "/devel/ostwald/projects/schemedit-project/web/WEB-INF/framework-config";
	String configDirPath;

	/**
	 *  Constructor for the FrameworkConfigTester object
	 */
	public FrameworkConfigTester(String xmlFormat) throws Exception {
		prtln ("xmlFormat: " + xmlFormat);
		configDirPath = TesterUtils.getFrameworkConfigDir();
		File sourceFile = new File(configDirPath, xmlFormat + ".xml");

		if (!sourceFile.exists()) {
			prtln("source File does not exist at " + sourceFile.toString());
			return;
		}
		prtln ("config: " + sourceFile);
		try {
			reader = new FrameworkConfigReader(sourceFile);
			framework = new MetaDataFramework(reader);
		} catch (Exception e) {
			prtln ("Initialization error: " + e.getMessage());
		}
		prtln ("schemaURI: " + framework.getSchemaURI());
	}

	FrameworkRegistry getFrameworkRegistry (String configDirPath) {
		return new FrameworkRegistry(configDirPath, null);
	}

	/**
	 *  Description of the Method
	 */
	public void showFrameworkStuff() {
		prtln("\n-----------------------");
		prtln("FRAMEWORK Stuff");
		prtln ("name: " + framework.getName());
		prtln ("renderer: " + this.framework.getRenderer());
	}

	public void showReaderStuff() {
		prtln("\n-----------------------");
		prtln ("READER Stuff");
		prtln ("name: " + this.reader.getName());
		prtln ("xmlFormat: " + this.reader.getXmlFormat());
		prtln ("renderer: " + this.reader.getRenderer());
	}

	/**
	 *  The main program for the FrameworkConfigTester class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args) throws Exception {
		prtln("FrameworkConfigTester");
		edu.ucar.dls.schemedit.autoform.RendererHelper.setLogging(false);
		String xmlFormat = "adn";

		if (args.length > 0)
			xmlFormat = args[0];

		FrameworkConfigTester tester = new FrameworkConfigTester(xmlFormat);
		tester.showSchemaPathMap();
		// pp (tester.reader.getDocMap().getDocument());

		// tester.showReaderStuff ();
		// tester.showFrameworkStuff();
 		// DocMap docMap = reader.getDocMap();

		pp (tester.getMinimalRecord());

	}

	public void showSchemaPathMap () {
		prtln (reader.getSchemaPathMap().toString());
	}

	public Document getMinimalRecord () {
		Document minnie = null;
		try {
			framework.loadSchemaHelper();
			File file = new File ("C:/Program Files/Apache Software Foundation/Tomcat 5.5/var/dcs_conf/collections/1249343349394.xml");
			CollectionConfig config = new CollectionConfig (file, null);
			if (config == null)
				throw new Exception ("collection config is NULL!");
			minnie = framework.makeMinimalRecord("FOO-ID", config);
			// pp(minnie);
		} catch (Exception e) {
			prtln (e.getMessage());
		}
		return minnie;
	}


	/**
	 *  Utility to show XML in pretty form
	 *
	 *@param  node  Description of the Parameter
	 */
	public static void pp(Node node) {
		prtln(Dom4jUtils.prettyPrint(node));
	}

	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	public static void prtln(String s) {
		System.out.println(s);
	}
}
