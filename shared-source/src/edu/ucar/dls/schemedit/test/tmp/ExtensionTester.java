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

package edu.ucar.dls.schemedit.test.tmp;

import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.schema.compositor.Compositor;
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.util.strings.*;

import java.io.File;
import java.util.*;
import java.net.URL;
import org.dom4j.*;

public class ExtensionTester {
	private static boolean debug = true;
	
	SchemaHelper schemaHelper = null;
	Document doc = null;
	DocMap docMap = null;
	
	public ExtensionTester (String instanceDocPath) {
		SchemaHelper.setDebug (true);
		try {
			doc = Dom4jUtils.getXmlDocument(new File (instanceDocPath));
		} catch (Exception e) {
			prtln ("Couldn't parse document at " + instanceDocPath);
			System.exit(1);
		}
		
		String uri = "http://www.dpc.ucar.edu/people/ostwald/Metadata/AttributeGroupTester/namespaces/extension-tester-ns.xsd";
		try {
			URL schemaUrl = new URL (uri);
			schemaHelper = new SchemaHelper(schemaUrl, "records");
		} catch (Throwable t) {
			prtln ("could not process schema at " + uri);
			System.exit(1);
		}
		
		doc = schemaHelper.getMinimalDocument();
		
		docMap = new DocMap (doc, schemaHelper);
	}
	
	void putValue (String xpath, String value) {
		try {
			docMap.smartPut(xpath, value);
		} catch (Exception e) {
			prtln ("WARNING: putValue failed: " + e);
		}
	}
	
	/* test the elements for "hasSequenceCompositor"
	then test for getMemebers
	*/
	void seqTest () {
		for (Iterator i=doc.getRootElement().elementIterator();i.hasNext();) {
			Element child = (Element)i.next();
			prtln (child.getName());
			String path = child.getPath();
			SchemaNode schemaNode = this.schemaHelper.getSchemaNode(path);
			if (schemaNode.hasSequenceCompositor())
				prtln ("\tHAS sequence compositor");
			else
				prtln ("\tdoes NOT have sequence compositor");
		}
	}
	
	public static void main (String [] args) throws Exception {
		String instanceDocPath = "L:/ostwald/tmp/validate-sandbox/extension-tester.xml";
		ExtensionTester tester = new ExtensionTester (instanceDocPath);
		
 		prtln ("\nMINIMAL DOCUMENT");
		pp (tester.doc);
		
		// tester.seqTest ();
		
		String xpath = "/this:records/this:record/rt:trailer";
		String value = "net rrailer";
		tester.putValue (xpath, value);
		
		pp (tester.doc);
		
		
		Dom4jUtils.writeDocToFile(tester.doc, new File ("L:/ostwald/tmp/validate-sandbox/ExtensionTesterOut.xml"));
	}
	
	private static void pp(Node node) {
		prtln(Dom4jUtils.prettyPrint(node));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		// System.out.println("SimpleSchemaHelperTester: " + s);
		System.out.println(s);
	}
}

