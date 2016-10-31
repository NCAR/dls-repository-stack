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

import edu.ucar.dls.serviceclients.remotesearch.*;
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.xml.XPathUtils;
import edu.ucar.dls.util.*;
import edu.ucar.dls.util.strings.*;
import edu.ucar.dls.oai.OAIUtils;

import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.text.*;

/**
 *  Utilities for manipulating XPaths, represented as String
 *
 *@author    ostwald
 */
public class RemoteSearchTester {

	private static boolean debug = true;

	
	/**
	 *  The main program for the RemoteSearchTester class
	 *
	 *@param  args           The command line arguments
	 *@exception  Exception  Description of the Exception
	 */
	public static void main(String[] args) throws Exception {

		String targetUrl;
		String serviceBaseUrl;
		String service = "dlese";
		
		if (service == "dcs") {
			targetUrl = "http://www.state.me.us/doc/nrimc/pubedinf/crest/activity/act33.htm";
			serviceBaseUrl = "http://dcs.dlese.org/dcc/services/ddsws1-0";
		}
		else if (service == "dlese") {
			targetUrl = "http://www.pulseplanet.com/archive/Sep04/3281.html";
			serviceBaseUrl = "http://www.dlese.org/dds/services/ddsws1-0";
		}
		else
			throw new Exception ("service (" + service + ") not recognized");
		
		RemoteSearcher rs = new RemoteSearcher (serviceBaseUrl, null);
		
		RemoteResultDoc [] results = rs.searchDocs (targetUrl);
		
	}


	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println(s);
		}
	}
}

