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

package edu.ucar.dls.serviceclients.asn;

import edu.ucar.dls.standards.asn.AsnStatement;

import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XPathUtils;
import edu.ucar.dls.util.strings.FindAndReplace;
import java.util.regex.*;

import java.util.*;

/**
 *  NOT YET DOCUMENTED
 *
 * @author     Jonathan Ostwald
 */
public class ColoradoStandardDisplay {

	private static boolean debug = true;
		
	static Pattern parentPattern = Pattern.compile ("Standard ([\\d]+):");
	static Pattern targetPattern = Pattern.compile ("([\\d]+).");
	
/* 	static Pattern getPattern (int level) {
		if (level == 1)
			return Pattern.compile(level1Pattern);
		if (level == 2)
			return Pattern.compile(level2Pattern);
		return null;
	} */
	
	static String getDisplayText (AsnStatement std, AsnStatement parent) {

		String baseText = std.getDescription();
		
		String parentNum = ColoradoStandardDisplay.getParentNumber (parent);
		String myNum = ColoradoStandardDisplay.getTargetNumber (std);
		if (parentNum != null && myNum != null) {
			return parentNum + "." + myNum + ". " + 
					getNonNumberText (baseText, targetPattern);
		}
		return baseText;
	}
	
	static String getTargetNumber (AsnStatement std) {
		return getNumber (std.getDescription(), targetPattern);
	}
	
	
	static String getParentNumber (AsnStatement std) {
		return getNumber (std.getDescription(), parentPattern);
	}
	
	static String getNonNumberText (String input, Pattern p) {
		Matcher m = p.matcher(input);
		if (m.find() && m.start() == 0) {
			return input.substring (m.end()).trim();
		}
		else {
			prtln ("NOPE");
		}
		return null;
	}
	static String getNumber (String input, Pattern p) {
		Matcher m = p.matcher(input);
		if (m.find() && m.start() == 0) {
			// prtln ("found: \"" + m.group(1));
			return m.group(1);
		}
		else {
			// prtln ("NOPE");
		}
		return null;
	}

	private static void prtln(String s) {
		if (debug) {
			System.out.println(s);
		}
	}
}
	
