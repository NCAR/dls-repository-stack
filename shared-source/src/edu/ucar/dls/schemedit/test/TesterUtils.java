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

import edu.ucar.dls.schemedit.config.FrameworkConfigReader;
import edu.ucar.dls.schemedit.config.CollectionConfigReader;

import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.schema.compositor.Compositor;
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.util.strings.*;
import edu.ucar.dls.util.EnvReader;

import java.io.*;
import java.util.*;
import java.text.*;
import java.util.regex.*;

import java.net.*;
import org.dom4j.Node;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.sun.msv.datatype.xsd.*;
import org.relaxng.datatype.*;

/**
 *  Methods to help tester classes use information in the framework config files.
 *
 *@author     ostwald
 *@created    June 27, 2006
 */
public class TesterUtils {

	public static MetaDataFramework getFramework (String xmlFormat) throws Exception {
		String configFileDir = TesterUtils.getFrameworkConfigDir();
		String docRoot = TesterUtils.getDocRoot();

		File configFile = new File(configFileDir, xmlFormat + ".xml");
		String configFilePath = configFile.toString();
		if (!configFile.exists()) {
			throw new Exception ("configFile doesn't exist at " + configFilePath);
		}
		MetaDataFramework framework = new MetaDataFramework(configFilePath, docRoot);

		try {
			framework.loadSchemaHelper();
		} catch (Exception e) {
			throw new Exception ("failed to instantiate SchemaHelper: " + e);
		}

		return framework;
	}

	public static String getTomcatDir () throws Exception {
		String host = EnvReader.getProperty("HOST").toLowerCase();

		if (host.indexOf("maryjane") == 0 ) {
			return "/Library/Java/Extensions/tomcat/tomcat/";
		}

		if (host.indexOf("taos") == 0) {
			return "/Library/Java/Extensions/tomcat/tomcat/";
		}
		if (host.indexOf("acorn") == 0) {
			return "/devel/ostwald/tomcat/tomcat/";
		}
		if (host.indexOf("mtsherman") == 0 || host.indexOf("dlss-macbook-pro") == 0) {
			return "/Library/Java/Extensions/tomcat/tomcat/";
		}

		if (host.indexOf("dls-pyramid") == 0) {
			return "C:/Program Files/Apache Software Foundation/Tomcat 6.0.35/";
		}

		throw new Exception ("ERROR: Unknown host: " + host);
	}

	public static String getTmpDir () throws Exception {
		String host = EnvReader.getProperty("HOST").toLowerCase();

		if (host.indexOf("maryjane") == 0 ) {
			return "/Users/ostwald/devel/tmp";
		}

		if (host.indexOf("taos") == 0) {
			return "/Users/ostwald/devel/tmp";
		}
		if (host.indexOf("dlss-macbook-pro") == 0) {
			return "/Users/ostwald/tmp";
		}
		if (host.indexOf("mtsherman") == 0) {
			return "/Users/ostwald/devel/tmp";
		}

		if (host.indexOf("dls-pyramid") == 0) {
			return "C:/Users/ostwald/tmp";
		}
		

		throw new Exception ("ERROR: Unknown host: " + host);
	}

	public static String getCannedCMPath () throws Exception {
		String host = EnvReader.getProperty("HOST").toLowerCase();
		if (host.indexOf("tremor") == 0) {
			return "/home/ostwald/python-lib/ndr/CannedCollectionMetadata.xml";
		}
		if (host.indexOf("mtsherman") == 0) {
			return "/Users/jonathan/devel/tmp/CannedCollectionMetadata.xml";
		}
		if (host.indexOf("dls-pyramid") == 0) {
			// return "C:ndr\\CannedCollectionMetadata.2.xml"
			return "C:/ndr/CannedCollectionMetadata.jlo.xml";
		}
		throw new Exception ("could not get CannedCMPath for host \"" + host + "\"");
	}

	public static String getRecordsPath () throws Exception {
		String host = EnvReader.getProperty("HOST").toLowerCase();
		if (host.indexOf("tremor") == 0) {
			return "/devel/ostwald/records";
		}

		if (host.indexOf("MtSherman") == 0) {
			return "/Users/jonathan/Devel/dcs-records/lean-records";
		}

		if (host.indexOf("dls-pyramid") == 0) {
			return "C:/Documents and Settings/ostwald/My Documents/devel/records";
		}

		throw new Exception ("recordsPath not defined for host \"" + host + "\"");
	}

	public static String getDocRoot () throws Exception {
		return getTomcatDir() + "webapps/schemedit";
	}

	 public static String getFrameworkConfigDir ()  throws Exception {
		 String host = EnvReader.getProperty("HOST").toLowerCase();
		 prtln ("host: " + host);
		 if (host.indexOf("dls-pyramid") == 0) {
			 // return getTomcatDir() + "var/dcs_conf/frameworks";
			 return "C:/Users/ostwald/devel/cyberPD/repo/dcs_conf/frameworks";
		 }
		 else if (host.indexOf("taos") == 0) {
			 // return getTomcatDir() + "var/dcs_conf/frameworks";
			 return "/Users/ostwald/devel/dcs-repos/cyberpd/dcs_conf/frameworks";
		 }
		 else if (host.indexOf("dlss-macbook-pro") == 0)
		 	 return "/Users/ostwald/devel/dcs-repos/dcs_release_test/dcs_conf/frameworks";
		 
		 else if (host.indexOf("acornOFF") == 0)
			 return getTomcatDir() + "webapps/schemedit/WEB-INF/dcs-config/frameworks";
		 else
			return getTomcatDir() + "dcs_conf/frameworks";
	 }

	 public static String getCollectionConfigDir ()  throws Exception {
		return getTomcatDir() + "var/dcs_conf/collections";
		// return "C:/Documents and Settings/ostwald/devel/dcs-instance-data/dev/config/collections";
	 }

	public static CollectionConfigReader getCollectionConfigReader(String collection)
	 throws Exception {
		File source = new File(getCollectionConfigDir(), collection + ".xml");
		prtln ("looking for file at " + source.toString());
		return getCollectionConfigReader(source);
	}

	/**
	 *  Gets the frameworkConfigReader attribute of the TesterUtils class
	 *
	 *@param  framework  Description of the Parameter
	 *@return            The frameworkConfigReader value
	 */
	public static FrameworkConfigReader getFrameworkConfigReader(String framework)
	 throws Exception {
		File source = new File(getFrameworkConfigDir(), framework + ".xml");
		prtln ("looking for file at " + source.toString());
		return getFrameworkConfigReader(source);
	}

	/**
	 *  Gets the collectionConfigReader attribute of the TesterUtils class
	 *
	 *@param  source  Description of the Parameter
	 *@return         The collectionConfigReader value
	 */
	public static CollectionConfigReader getCollectionConfigReader(File source) {

		if (!source.exists()) {
			prtln("WARNING: config file does not exist at " + source.toString());
			return null;
		}
		try {
			return new CollectionConfigReader(source);
		} catch (Exception e) {
			prtln (e.getMessage());
			return null;
		}
	}



	/**
	 *  Gets the frameworkConfigReader attribute of the TesterUtils class
	 *
	 *@param  source  Description of the Parameter
	 *@return         The frameworkConfigReader value
	 */
	public static FrameworkConfigReader getFrameworkConfigReader(File source) {

		if (!source.exists()) {
			prtln("WARNING: config file does not exist at " + source.toString());
			return null;
		}
		try {
			return new FrameworkConfigReader(source);
		} catch (Exception e) {
			prtln (e.getMessage());
			return null;
		}
	}

	public static String getSchemaUri (String xmlFormat) {
		prtln ("getSchemaUri() with xmlFormat: " + xmlFormat);
		FrameworkConfigReader fcr = null;
		try {
			fcr = getFrameworkConfigReader (xmlFormat);
		} catch (Exception e) {
			prtln (e.getMessage());
		}
		if (fcr == null)
			return "";
		else
			return fcr.getSchemaURI ();
	}



	/**
	 *  The main program for the TesterUtils class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args) {
		prtln("Hello from Tester Utils");
		String xmlFormat = "cd";
		String schemaUri = getSchemaUri (xmlFormat);
		prtln ("schemaUri: " + schemaUri);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  node  Description of the Parameter
	 */
	private static String pp(Node node) {
		return Dom4jUtils.prettyPrint(node);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		// System.out.println("TesterUtils: " + s);
		System.out.println(s);
	}
}
