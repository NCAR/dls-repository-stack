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

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.xml.XPathUtils;
import edu.ucar.dls.util.*;
import edu.ucar.dls.util.strings.*;
import edu.ucar.dls.oai.OAIUtils;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.text.*;

/**
 *  Utilities for manipulating XPaths, represented as String
 *
 *@author    ostwald
 */
public class PathTester {

	private static boolean debug = true;

	
	/**
	 *  The main program for the PathTester class
	 *
	 *@param  args           The command line arguments
	 *@exception  Exception  Description of the Exception
	 */
	public static void main(String[] args) throws Exception {

		String path1 = "/dpc/tremor/devel/ostwald/records-bog/dlese_collect/collect/..";
		String path2 = "/devel/ostwald/records-bog/dlese_collect";
		
		String bogpath = "/devel/ostwald/folseir";
		if (! (new File (bogpath).exists()))
			prtln ("bog file doesnt exist");
		
		File file1 = new File (path1);
		File file2 = new File (path2);
		
		prtln ("Absolute Paths");
		prtln ("1 " + file1.getAbsolutePath());
		prtln ("2 " + file2.getAbsolutePath());
		
		prtln ("canonicalPaths");
		prtln ("1 " + file1.getCanonicalPath());
		prtln ("2 " + file2.getCanonicalPath());
		
		prtln ("file comparison");
		if (file1.equals(file2))
			prtln ("the files are equal");
		else
			prtln ("the files are DIFFERENT");
		
		prtln ("Canonical file comparison");
		if (file1.getCanonicalFile().equals(file2.getCanonicalFile()))
			prtln ("the files are equal");
		else
			prtln ("the files are DIFFERENT");

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

