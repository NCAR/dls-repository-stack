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

package edu.ucar.dls.schemedit.standards.util;

import edu.ucar.dls.schemedit.SchemEditUtils;

import edu.ucar.dls.util.Files;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XPathUtils;
import edu.ucar.dls.xml.XMLFileFilter;


import org.dom4j.*;

import java.io.File;

public class AsnUtils {
	private static boolean debug = true;
	
	public static void beautify (String path) {
		beautify (new File (path));
	}
	
	public static void beautify (File file) {
		Document doc = null;
		try {
			doc = Dom4jUtils.getXmlDocument (file);
		} catch (Exception e) {
			prtln (e.getMessage());
		}
		String xml = Dom4jUtils.prettyPrint(doc);
		try {
			// prtln (xml);
			Files.writeFile(xml, file);
			prtln ("wrote to " + file);
		} catch (Exception e) {
			prtln ("could not beautify (" + file + "): " + e.getMessage());
		}
	}
	
	public static void beautifyDir (String path) throws Exception {
		File dir = new File (path);
		if (!dir.isDirectory())
			throw new Exception ("Directory required (" + path + ")");
		File [] files = dir.listFiles(new XMLFileFilter());
		for (int i=0;i<files.length;i++) {
			try {
				beautify (files[i]);
			} catch (Exception e) {
				prtln ("Beautify error: " + e.getMessage());
			}
		}
	}
			
	
	public static void main (String [] args) throws Exception {
		prtln ("Utils");
		// String dir = "/Documents/Work/DLS/ASN/mast-docs";
		String dir = "/Documents/Work/DLS/ASN/standards-documents/v1.4.0/math";
/* 		String filename = "1995-National Science Education Standards (NSES)-Science-National Science Education Standard.xml";
		beautify (new File (dir, filename)); */
		beautifyDir (dir);

	}
	
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "CATServiceHelper");
		}
	}
}
