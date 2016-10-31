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

import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.schema.compositor.Compositor;
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.util.strings.*;

import java.io.*;
import java.util.*;
import java.text.*;
import java.util.regex.*;

import java.net.*;
import org.dom4j.Node;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.sun.msv.datatype.xsd.*;
import org.relaxng.datatype.*;

/**
 *  Description of the Class
 *
 * @author    ostwald
 */
public class InsertTester {

	public SimpleSchemaHelperTester ssht;
	public DocMap docMap;
	public SchemaHelper sh;


	public InsertTester(String schemaName, String instanceDocPath) throws Exception {
		try {
			ssht = new SimpleSchemaHelperTester(schemaName);
		} catch (Exception e) {
			// prtln ("SchemaHelper threw exeption (" + e.getClass().getName() + ") " + e);
			prtln("InsertTester error: " + e.getMessage());
			e.printStackTrace();
			return;
		}
		sh = ssht.sh;
		Document doc = Dom4jUtils.getXmlDocument(new File (instanceDocPath));
		docMap = new DocMap (doc, sh);

		prtln (Dom4jUtils.prettyPrint (doc));

	}


	private void testInsert (String xpath) {
		Element newElement = sh.getNewElement(xpath);
		newElement.setText("i am but a test");
		boolean b = docMap.addElement(newElement, xpath);
	}

	/**
	 *  The main program for the InsertTester class
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(String[] args) throws Exception {

		String schemaName = "nsdl-oai";
		String instanceDocPath = "/Users/ostwald/devel/lab-records/cd/1151381938602/CD-000-000-000-020.xml";
		String xpath = "/cd:cd/cd:concrete2";

		InsertTester it = new InsertTester (schemaName, instanceDocPath);
		it.testInsert (xpath);

		prtln ("after insert");
		pp (it.docMap.getDocument());


		it.showSchemaNodes();
	}

	public void showSchemaNodes () {
		SchemaNodeMap schemaNodeMap = sh.getSchemaNodeMap();
		String s = "\n--------------------";
		s += "\nSchemaNodeMap:\n";
		Iterator i = schemaNodeMap.getKeys().iterator();
		while (i.hasNext()) {
			String path = (String)i.next();
			SchemaNode schemaNode = (SchemaNode)schemaNodeMap.getValue (path);
			s +=  "\n" + "path: " + path ;
			s += "\n\t" + "headElementName: " + schemaNode.getHeadElementName();

		}
		prtln (s + "\n-----------------------------\n");
	}

	/**
	 *  Description of the Method
	 *
	 * @param  node  Description of the Parameter
	 */
	private static void pp(Node node) {
		prtln(Dom4jUtils.prettyPrint(node));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		// System.out.println("InsertTester: " + s);
		System.out.println(s);
	}
}
