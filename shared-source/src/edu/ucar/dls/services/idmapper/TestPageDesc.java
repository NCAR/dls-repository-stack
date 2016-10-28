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

package edu.ucar.dls.services.idmapper;

import java.io.BufferedReader;
import java.io.FileReader;

import edu.ucar.dls.util.DpcErrors;



class TestPageDesc {

public static void main( String[] args) {
	new TestPageDesc( args);
}


void badparms( String msg) {
	prtln("Error: " + msg);
	prtln("Parms: timeoutSeconds infile");
	prtln("Each line of infile contains a url to be tested.");
	prtln("Blank lines and those starting with \"#\" are ignored.");
	prtln("Example:");
	prtln("    java edu.ucar.dls.services.idmapper.TestPageDesc infile");
	System.exit(1);
}




TestPageDesc( String[] args) {
	if (args.length != 2) badparms("wrong num parms");
	int iarg = 0;
	String timeoutstg = args[iarg++];
	String infname = args[iarg++];

	int timeout = 0;
	try { timeout = Integer.parseInt( timeoutstg); }
	catch( NumberFormatException nfe) {
		badparms("invalid timeout: " + timeoutstg);
	}

	String collKey = "someCollKey";
	String metastyle = "someMetastyle";
	String dirPath = "someDirPath";
	String fileName = "someFileName";
	String[] urlOnlyTests = null;
	int bugs = 100;
	int threadId = 9999;


	try {
		BufferedReader rdr = new BufferedReader( new FileReader( infname));
		while (true) {
			String inline = rdr.readLine();
			if (inline == null) break;
			String urlstg = inline.trim();
			if (urlstg.length() > 0 && ! urlstg.startsWith("#")) {
				prtln("\n===== begin test of \"" + urlstg + "\" =====\n");
				ResourceDesc rsd = new ResourceDesc( collKey, metastyle,
					dirPath, fileName, urlOnlyTests);

				PageDesc page = new PageDesc(
					rsd, "test/xpath", "primary-url", urlstg,
					PageDesc.CKSUMTYPE_STD);
				rsd.addPage( page);

				page.processPage( bugs, threadId, timeout);
				prtln("\nTestPageDesc: respcode: " + page.respcode
					+ " (" + DpcErrors.getMessage( page.respcode) + ")"
					+ "  url: \"" + urlstg + "\""
					+ "  pagewarning: " + page.pagewarning);

				// Cannot print or use the page contents,
				// since the contents are cleared at end of processPage.
			}
		}
	}
	catch( Exception exc) {
		prtln("TestPageDesc: caught: " + exc);
		exc.printStackTrace();
	}
} // end constructor





void prtln( String msg) {
	System.out.println( msg);
}


} // end class TestPageDesc
