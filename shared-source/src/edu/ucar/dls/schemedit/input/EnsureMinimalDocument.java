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

import edu.ucar.dls.schemedit.url.UrlHelper;
import edu.ucar.dls.schemedit.action.form.SchemEditForm;
import edu.ucar.dls.schemedit.MetaDataFramework;
import edu.ucar.dls.schemedit.SchemEditUtils;

import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.schema.compositor.*;
import edu.ucar.dls.xml.*;

import java.util.*;
import java.io.*;
import java.text.ParseException;
import java.net.MalformedURLException;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Attribute;
import org.dom4j.Node;

/**
 *  Preprocesses instance documents to ensure required paths are present so even
 *  if the metadata framework changes, the older documents can still be edited.
 *
 *@author    ostwald <p>
 *
 *
 */
public class EnsureMinimalDocument {

	private static boolean debug = false;

	private SchemaHelper schemaHelper;

	private DocMap docMap = null;
	private Document doc = null;
	private Document minimalDoc = null;


	/**
	 *  Constructor for the EnsureMinimalDocument object
	 *
	 *@param  doc           instanceDocument
	 *@param  schemaHelper  the schemaHelper for doc's framework
	 */
	private EnsureMinimalDocument(Document doc, SchemaHelper schemaHelper) {
		this.schemaHelper = schemaHelper;
		this.minimalDoc = this.schemaHelper.getMinimalDocument();
		prtln("----------------");
		prtln("Minimal Doc **\n" + Dom4jUtils.prettyPrint(this.minimalDoc));
		prtln("----------------");
		this.doc = doc;
		this.docMap = new DocMap(doc, schemaHelper);
	}


	/**Static method to process given Document with help of given schemaHelper
	 *
	 *@param  doc           instanceDocument
	 *@param  schemaHelper  the schemaHelper for doc's framework
	 */
	public static void process(Document doc, SchemaHelper schemaHelper) {
		EnsureMinimalDocument ensurer = new EnsureMinimalDocument(doc, schemaHelper);
		ensurer.process();
	}


	/**
	 *  Ensure the document has all required paths.
	 */
	private void process() {
		Element rootElement = docMap.getDocument().getRootElement();
		processTree(rootElement);
	}


	/**
	 *  Recursively processes each subelementn of the provided instanceDoc element, adding required fields
	 when necessary.
	 *
	 *@param  instElement  Description of the Parameter
	 */
	private void processTree(Element instElement) {
		// prtln ("\nprocessTree(" + instElement.getPath() + ")");
		String schemaPath = XPathUtils.normalizeXPath(instElement.getPath());
		Element minElement = (Element) this.minimalDoc.selectSingleNode(schemaPath);

		// make sure the instance doc has at least one of all paths in the minDoc
		for (Iterator i = minElement.elementIterator(); i.hasNext(); ) {
			Element child = (Element) i.next();
			SchemaNode childNode = schemaHelper.getSchemaNode(child.getPath());
			String childName = child.getQualifiedName();
			Element instChild = instElement.element(childName);
			if (instChild == null && childNode.isRequired()) {
				try {
					instChild = (Element) this.docMap.createNewNode(instElement.getPath() + '/' + childName);
				} catch (Exception e) {
					prtln(e.getMessage());
					continue;
				}
				// prtln("created new element at " + child.getPath());
			}
			processTree(instChild);
		}
	}



	/**
	 *  Print a line to standard out.
	 *
	 *@param  s  The String to print.
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "EnsureMinimalDocument");
		}
	}

}

