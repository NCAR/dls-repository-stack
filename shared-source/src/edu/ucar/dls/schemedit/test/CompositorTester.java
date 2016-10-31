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
import edu.ucar.dls.xml.schema.compositor.*;
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

/*
 *  Eventually we will have to account for the parent element (after all this is necessary in determining
 *  whether a child choice can be added). But for now we concentrate on determining what choices have already been made.
 *  1 - determine the choices that have already been made
 *  we need to test an existing XML element against a type definition (ComplexType instance)
 *  - a ComplexType instance can be read from a file containing
 *  - XML element to be tested can be obtained in the same way
 */

/**
 *  Provides Helpful methods for creating and testing Compositors and
 *  CompositorGuards<p>
 *
 *  Builds a compositor given:
 *  <ul>
 *    <li> schemaURI - specifying a schema
 *    <li> instanceDocPath - path to xml record on disk, and instance Document
 *    for the Schema
 *    <li> xpath - determines the instanceElement to be controlled by the
 *    Compositor
 *    <ul>
 *
 *@author     Jonathan Ostwald
 *@created    July 19, 2006
 *@version    $Id: CompositorTester.java,v 1.7 2012/11/22 00:02:32 ostwald Exp $
 */
public class CompositorTester {

	/**
	 *  The compositor created by CompositorTester for the specified schema, instanceDocument, and xpath;
	 */
	public Compositor compositor;
	String instanceDocPath;
	String xpath;
	SchemaHelper schemaHelper;
	Document instanceDoc = null;
	DocMap docMap = null;

	/**
	 *  Constructor for the CompositorTester object
	 *
	 *@param  schemaLocation   Description of the Parameter
	 *@param  instanceDocPath  Description of the Parameter
	 *@param  xpath            Description of the Parameter
	 *@exception  Exception    Description of the Exception
	 */
	public CompositorTester(String schemaName,
							String xpath) throws Exception {
		this.xpath = xpath;

		SimpleSchemaHelperTester ssht = new SimpleSchemaHelperTester(schemaName);
		this.schemaHelper = ssht.sh;

		prtln("\n\n=========================================");
		prtln("Compositor Tester\n-----------------");
		prtln("schemaName:\n\t" + schemaName);
		prtln("instance Doc path:\n\t " + instanceDocPath);

		compositor = getCompositor(xpath);

		prtln("\n=========================================\n");

	}


	public CompositorGuard getCompositorGuard (String instanceDocPath) {
		this.setInstanceDocument(instanceDocPath);
		// prtln ("\ngetCompositorGuard: " + xpath);
		Element e = this.getInstanceElement(xpath);
		return CompositorGuard.getInstance (this.compositor, e);
	}

	/**
	 *  Gets the compositor for the specified xpath, using the tester's instance document
	 *
	 *@param  xpath  Description of the Parameter
	 *@return        The compositor value
	 */
	Compositor getCompositor(String xpath) {
		prtln ("\ngetCompositor()");
		Compositor compositor = null;

		try {
			ComplexType ctype = (ComplexType) schemaHelper.getGlobalDefFromXPath(xpath);

			if (ctype == null) {
				throw new Exception("typeDef not found at " + xpath);
			}

			prtln("\nCompositor.parent:\n--------------------\n" + ctype.toString());

			compositor = (Compositor) ctype.getCompositor();
			if (compositor == null) {
				throw new Exception("compositor not found");
			}
		} catch (Exception e) {
			prtln("getCompositor error: " + e.getMessage());
			return null;
		} catch (Throwable t) {
			prtln("getCompositor unknown error: " + t.getMessage());
			t.printStackTrace();
			return null;
		}

		prtln("\n--------------------\n" + compositor.toString());
		return compositor;
	}


	/**
	 *  Reads an xml record, and sets the Tester's instanceDoc and DocMap attributes
	 *
	 *@param  instanceDocPath  path to an xml file on disk
	 */
	void setInstanceDocument(String instanceDocPath) {
		try {
			instanceDoc = Dom4jUtils.getXmlDocument(new File(instanceDocPath));
			if (!schemaHelper.getNamespaceEnabled()) {
				instanceDoc = Dom4jUtils.localizeXml(instanceDoc);
			}
			docMap = new DocMap(instanceDoc, schemaHelper);
		} catch (Exception e) {
			prtln("setInstanceDocument error: " + e.getMessage());
			return;
		} catch (Throwable t) {
			prtln("setInstanceDocument unknown error: " + t.getMessage());
			t.printStackTrace();
			return;
		}

		// prtln("\nInstanceDocument:\n--------------------" + Dom4jUtils.prettyPrint(instanceDoc));
	}


	/**
	 *  Obtains an instanceElement from the current instanceDocument
	 * at the specified path
	 *
	 *@param  xpath  xpath used to select instanceElement
	 *@return        The instanceElement
	 */
	Element getInstanceElement(String xpath) {
		Element instanceElement;
		try {
			instanceElement = (Element) instanceDoc.selectSingleNode(xpath);
			if (instanceElement == null) {
				throw new Exception("instanceElement not found for " + xpath);
			}
		} catch (Exception e) {
			prtln("getInstanceElement error: " + e.getMessage());
			return null;
		} catch (Throwable t) {
			prtln("getInstanceElement unknown error: " + t.getMessage());
			t.printStackTrace();
			return null;
		}
		// prtln("\nInstanceElement:\n--------------------" + Dom4jUtils.prettyPrint(instanceElement));
		return instanceElement;
	}


	/**
	 *  Create a CompositorTester instance
	 *
	 *@param  args           The command line arguments
	 *@exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		prtln("CompositorTester");

		// mets();
		// dlese_anno();

		smile_item();

/* 		String schemaName = "mets";
		String instanceDocPath = "C:/tmp/mets_record.xml";
		String xpath = "/this:mets/this:fileSec/this:fileGrp/this:file/this:FContent/this:xmlData";

		CompositorTester cgt = new CompositorTester(schemaName, xpath); */

/* 		CompositorGuard guard = cgt.getCompositorGuard(instanceDocPath);
		prtln ("\nPrinting Instance Members");
		guard.printInstanceMembers(); */
	}

	static void dlese_anno () throws Exception {
		String schemaName = "dlese_anno_TEST";
		String instanceDocPath = "C:/tmp/dlese_anno_record.xml";
		String xpath = "/record/moreInfo";

		CompositorTester cgt = new CompositorTester(schemaName, xpath);
	}

	static void mets () throws Exception {
		String schemaName = "mets";
		String instanceDocPath = "C:/tmp/mets_record.xml";
		String xpath = "/this:mets/this:fileSec/this:fileGrp/this:file/this:FContent/this:xmlData";

		CompositorTester cgt = new CompositorTester(schemaName, xpath);
	}

	static void smile_item () throws Exception {
		String schemaName = "mets";
		String instanceDocPath = "C:/tmp/SMILE-14-000-000-000-001.xml";
		String xpath = "/smileItem/authorshipRights/copyright/license";

		CompositorTester cgt = new CompositorTester(schemaName, xpath);
	}

	/**
	 *  Tester for a Compositor's getMember method - retrieves a CompositorMember instance
	 for the specified memberName
	 *
	 *
	 */
	static CompositorMember findCompositorMember(String memberName, Compositor compositor) {
		prtln("looking for " + memberName);
		CompositorMember member = compositor.getMember(memberName);
		if (member != null) {
			prtln("\t... found member: " + member.getInstanceQualifiedName() +
				  " - maxOccurs: " + member.maxOccurs +
				  " - minOccurs: " + member.minOccurs);
			return member;
		}
		else {
			prtln("ChoiceMember for " + memberName + " not found");
			return null;
		}
	}

	/**
	 *  Create and insert a new element into the instanceDocument at
	 * a specified xpath.
	 *
	 *@param  path           Description of the Parameter
	 *@exception  Exception  Description of the Exception
	 */
	void attachNewElement(String path) throws Exception {
		prtln("attachNewElement() path: " + path);
		Element newElement = schemaHelper.getNewElement(path);
		if (newElement == null) {
			throw new Exception("getNewElement failed");
		} else {
			if (!docMap.addElement(newElement, path)) {
				throw new Exception("docMap.addElement failed");
			}
		}
		prtln("\n after attachment\n" + pp(newElement.getParent()));
		/*
		 *  pp (docMap.selectSingleNode(XPathUtils.getParentXPath(path)));
		 */
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 *@param  node  NOT YET DOCUMENTED
	 *@return       NOT YET DOCUMENTED
	 */
	static String pp(Node node) {
		try {
			return (Dom4jUtils.prettyPrint(node));
		} catch (Exception e) {
			return (e.getMessage());
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 *@param  s  NOT YET DOCUMENTED
	 */
	static void prtln(String s) {
		while (s.charAt(0) == '\n') {
			System.out.println("");
			s = s.substring(1);
		}
		System.out.println(s);
	}

}
