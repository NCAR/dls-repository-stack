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

import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.io.*;
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.xml.XSLTransformer;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.util.Files;
import org.dom4j.*;
import org.dom4j.io.*;


/**
 *  Class for testing dom manipulation with help from {@link edu.ucar.dls.xml.schema.SchemaHelper}
 *
 * @author     ostwald<p>
 *
 *      $Id $
 * @version    $Id: SimpleDomTester.java,v 1.3 2009/03/20 23:33:58 jweather Exp $
 */
public class SimpleDomTester {

	private static boolean debug = true;

	Document doc = null;
	DocMap docMap = null;
	String path = null;

	/**
	 *  Constructor for the SimpleDomTester object
	 *
	 * @param  path  NOT YET DOCUMENTED
	 */
	SimpleDomTester(String path) throws Exception {
		this.path = path;
		Document testDoc = null;
		prtln ("\nSimpleDomTester");
		prtln ("reading from\n\t" + path);
		doc = parseWithSAX(new File(path));
		docMap = new DocMap(doc, null);
	}

	String localizerTrans ()  {
		String xml = null;
		String xslDir = "/devel/ostwald/projects/schemedit-project/web/WEB-INF/xsl_files";
		String namespaceOut = "namespace-out.xsl";
		String localize = "remove-namespaces-and-localize.xsl";
		
		String xslPath = new File (xslDir, localize).toString();
		javax.xml.transform.Transformer transformer = null;
		try {
			transformer = XSLTransformer.getTransformer(xslPath);
			xml = XSLTransformer.transformFile (path, transformer);
		} catch (Exception e) {
			prtln (e.getMessage());
		}
		return xml;
	}

	String localizer ()  {
		String localizedXml = null;
		try {
			String xml = Files.readFile (path).toString();
			localizedXml = Dom4jUtils.localizeXml (xml);
		} catch (Exception e) {
			prtln ("localizer: " + e.getMessage());
		}
		return localizedXml;
	}
	
	void unlocalizedTest () {
		Element root = doc.getRootElement();
		prtln (root.getPath());
		doProbe();
	}
	
	
	void localizedTest () {
		String xml = localizer();
		prtln ("Localized xml\n" + xml);
	}
	
	
	/**
	 *  The main program for the SimpleDomTester class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		String path = "/devel/ostwald/SchemEdit/NameSpacesPlay/explicit-default.xml";
		if (args.length > 0)
			path = args[0];
		SimpleDomTester t = null;
		try {
			t = new SimpleDomTester (path);
		} catch (Exception e) {
			prtln ("ERROR: " + e.getMessage());
			return;
		}

		String xml = t.localizer ();
		prtln (xml);

	}
		
	void doProbe () {
		nodeProbe ("/cd");
		nodeProbe ("/cd/id");
		nodeProbe ("/cd/sh:album");
		nodeProbe ("/cd/info");

		nodeProbe ("/cd:cd");
		nodeProbe ("/cd:cd/cd:id");
		nodeProbe ("/cd:cd/sh:album");
		nodeProbe ("/cd:cd/cd:info");
		
	}
	
	void nodeProbe (String path) {
		Node n = doc.selectSingleNode (path);
		if (n == null)
			prtln("node NOT found at " + path);
		else
			prtln ("node FOUND at " + path);
	}

	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  n  NOT YET DOCUMENTED
	 */
	private static void pp(Node n) {
		prtln(Dom4jUtils.prettyPrint(n));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("SimpleDomTester: " + s);
			System.out.println(s);
		}
	}

	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  aFile                  NOT YET DOCUMENTED
	 * @return                        NOT YET DOCUMENTED
	 * @exception  DocumentException  NOT YET DOCUMENTED
	 */
	public static Document parseWithSAX(File aFile) throws Exception {
		SAXReader xmlReader = new SAXReader();
		return xmlReader.read(aFile);
	}
	
	/**
	 *  Gets the localizedDoc attribute of the SimpleDomTester object
	 *
	 * @param  file  NOT YET DOCUMENTED
	 * @return       The localizedDoc value
	 */
	Document localizerOld() {
		prtln("localizerOld ... ");
		doc = null;
		try {
			doc = Dom4jUtils.getXmlDocument(path);
			if (doc == null)
				throw new Exception("File could not be parsed: " + path);
			String rootElementName = doc.getRootElement().getName();
			prtln ("rootElementName: " + rootElementName);
			doc = Dom4jUtils.localizeXml(doc, rootElementName);
			if (doc == null) {
				throw new Exception("doc could not be localized - please unsure the record's root element contains namespace information");
			}
		} catch (Exception e) {
			prtln("DOM read error: " + e.getMessage());
		}
		return doc;
	}

	
}

