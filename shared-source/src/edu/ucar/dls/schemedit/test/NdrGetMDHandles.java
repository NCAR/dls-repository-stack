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

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.Files;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.XPath;
import java.io.File;
import java.net.URL;
import java.util.*;


/**
 *  Class for testing dom manipulation with help from {@link edu.ucar.dls.xml.schema.SchemaHelper}
 *
 *@author     ostwald<p>
 $Id $
 */
public class NdrGetMDHandles {

	private static boolean debug = true;
	
	NdrGetMDHandles (String collection) throws Exception {
	}
		
	public static void main (String [] args) throws Exception {
		String MDPHandle = "2200/test.20070219032201392T";
		String apiUrl = "http://ndrtest.nsdl.org/api";
		String command = "listMembers";
		URL url = new URL (apiUrl + "/" + command + "/" + MDPHandle);
		Document doc = Dom4jUtils.getXmlDocument(url);
		doc = Dom4jUtils.localizeXml(doc);
		pp (doc);
		List mdHandles = new ArrayList();
		List mdHandleNodes = doc.selectNodes("//handleList/handle");
		if (mdHandleNodes != null) {
			for (Iterator i= mdHandleNodes.iterator();i.hasNext();) {
				Node node = (Node)i.next();
				mdHandles.add (node.getText());
			}
		}
		prtln ("Handles (" + mdHandles.size() + ")");
		for (Iterator i = mdHandles.iterator();i.hasNext();) {
			prtln ("\t" + (String)i.next());
		}
		
	}

	
	private static void  pp (Node node) {
		prtln (Dom4jUtils.prettyPrint(node));
	}
	
	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			// SchemEditUtils.prtln(s, "NdrGetMDHandles");
			SchemEditUtils.prtln(s, "");
		}
	}
	
}

