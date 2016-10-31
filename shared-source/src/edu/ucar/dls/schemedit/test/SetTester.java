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
import java.net.*;
import java.text.*;

/**
 *  Utilities for manipulating XPaths, represented as String
 *
 *@author    ostwald
 */
public class SetTester {

	private static boolean debug = true;

	/**
	 *  The main program for the SetTester class
	 *
	 *@param  args           The command line arguments
	 *@exception  Exception  Description of the Exception
	 */
	public static void main(String[] args) throws Exception {
		HashMap map = new HashMap ();
		map.put ("one", "1");
		map.put ("two", "2");
		map.put ("three", "3");
		map.put ("zero", "0");
		
		List pruneList = new ArrayList ();
		pruneList.add ("two");
		pruneList.add ("three");
		
		showMap (map);
		
		map = pruneMap (map, pruneList);
		prtln (" --------------------------------");
		showMap (map);

	}

	static HashMap pruneMap (HashMap map, List pruneList) {
		for (Iterator i=pruneList.iterator();i.hasNext();) {
			String key = (String) i.next();
			map.remove(key);
		}
		return map;
	}
	
	static void showMap (HashMap map) {
		Set keys = map.keySet();
		for (Iterator i=keys.iterator();i.hasNext();) {
			String key = (String) i.next();
			String value = (String) map.get(key);
			prtln (key + ": " + value);
		}
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

