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

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.dcs.*;
import edu.ucar.dls.schemedit.threadedservices.*;
import edu.ucar.dls.schemedit.config.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.util.*;
import edu.ucar.dls.repository.*;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Attribute;
import org.dom4j.Node;

/**
 *  Tester for {@link edu.ucar.dls.schemedit.dcs.DcsDataRecord} and related classes.
 *
 *@author    ostwald
 <p>$Id: ValidationReportTester.java,v 1.11 2011/07/05 22:05:38 ostwald Exp $
 */
public class ValidationReportTester {
	static boolean debug = false;
	FrameworkConfigReader reader = null;
	MetaDataFramework dcsDataFramework = null;

	String NewDcsDir = "/dpc/tremor/devel/ostwald/metadata-frameworks/dcs-data";

	/**
	 *  Constructor for the ValidationReportTester object
	 */
	public ValidationReportTester(String format) {
		String errorMsg;
		String configFileName = format+".xml";

		String configDirPath = NewDcsDir;
		// String configDirPath = "/devel/ostwald/metadata-frameworks/dcs-framework/records";
		// String configDirPath = "/devel/ostwald/tomcat/tomcat/webapps/schemedit/WEB-INF/framework-config";
		File sourceFile = new File(configDirPath, configFileName);
		if (!sourceFile.exists()) {
			prtln("source File does not exist at " + sourceFile.toString());
			return;
		}
		else {
			prtln ("reading frameworkconfig file from: " + sourceFile.toString());
		}

 		try {
			reader = new FrameworkConfigReader(sourceFile);
			dcsDataFramework = new MetaDataFramework(reader);
			dcsDataFramework.loadSchemaHelper();
		} catch (Exception e) {
			errorMsg = "Error loading Schema Helper: " + e.getMessage();
			prtln (errorMsg);
		}
	}


	/**
	 *  The main program for the ValidationReportTester class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args) throws Exception {
		prtln("ValidationReportTester");
		String format = "dcs_data";
		BadCharChecker bcChecker = new BadCharChecker();
		ValidationReportTester tester = new ValidationReportTester(format);
		ValidationReport report = new ValidationReport (null, null);
		long start = new Date().getTime();
		String records = "/devel/ostwald/records";
		String collection = "1107387620842";
		File adnRecords = new File (records + "/adn/" + collection);
		File dcsRecords = new File (records + "/dcs_data/adn/" + collection);
		File [] adnFiles = adnRecords.listFiles();
		for (int i=0; i< adnFiles.length;i++) {
			File adnFile = adnFiles[i];
			prtln ("adnFile: " + adnFile.toString());
			String fileName = adnFile.getName();
			String xmlRecord = "";
			try {
				StringBuffer buff = Files.readFile(adnFile);
				xmlRecord = buff.toString();
			} catch (IOException e) {
				prtln ("ERROR: " + e.getMessage());
				return;
			}
			String vRpt = edu.ucar.dls.xml.XMLValidator.validateString(xmlRecord);
			String localizedXmlRecord = Dom4jUtils.localizeXml(xmlRecord, "itemRecord");
			prtln (localizedXmlRecord);
			List badChars = bcChecker.check(localizedXmlRecord);
			prtln (badChars.size() + " elements with bad chars found");
			File dcsDataFile = new File (dcsRecords, fileName);
			prtln ("dcsDataFile: " + dcsDataFile.toString());
			DcsDataRecord dcsDataRecord = new DcsDataRecord (dcsDataFile, tester.dcsDataFramework, null, null);
			dcsDataRecord.setValidationReport(vRpt);

			//prtln (Dom4jUtils.prettyPrint(dcsDataRecord.getDocument()));
			// prtln ("\n\n-----------------------------------\n\n");
			report.addEntry(dcsDataRecord);
		}
		report.recordsProcessed = adnFiles.length;
		report.processingTime = new Date().getTime() - start;
		System.out.println ("\n--------------------");
		System.out.println (report.getReport());
	}

	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	public static void prtln(String s) {
		if (debug)
			System.out.println(s);
	}
}
