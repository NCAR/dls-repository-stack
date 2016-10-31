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

import  edu.ucar.dls.schemedit.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.util.Files;

import org.dom4j.*;

import java.util.*;
import java.util.regex.*;
import java.net.*;

public class GetMirrorsTester {
	
	private static boolean debug = true;
	
	public static void main (String[] args) {
		String path = "/devel/ostwald/tmp/adn-record.xml";
		List mirrors = new ArrayList();
		try {
			StringBuffer xmlBuff = Files.readFile (path);
			String xml = Dom4jUtils.localizeXml (xmlBuff.toString(), "itemRecord");
			mirrors = SchemEditUtils.getMirrorUrls (xml);
			if (mirrors != null && mirrors.size() > 0) {
				prtln (mirrors.size() + " mirrors found");
				for (Iterator i=mirrors.iterator();i.hasNext();) {
					prtln ("\t" + (String)i.next());
				}
			}
			else
				prtln ("NO mirrors found");
		} catch (Exception e) {
			prtln ("error: " + e.getMessage());
		}
	}
	
	private static void prtln(String s) {
		if (debug) {
			System.out.println(s);
		}
	}
	
}
