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

package edu.ucar.dls.standards.asn;

import org.dom4j.Element;

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
		
	static String level1Pattern = "Standard ([\\d]+):";
	static String level2Pattern = "([\\d]+).";
	
	public static void main (String [] args) throws Exception {
		String in = "Standard 44: ";
		// String in = "2. ";
		// prtln (getNumber (in, 1));
		// prtln (getNumber (in, 2));
		
		in = "2. the theory of plate tectonics he";
		prtln ("in: " + in);
		prtln ("non number: \"" + getNonNumberText (in, 1) + "\"");
		prtln ("non number: \"" + getNonNumberText (in, 2) + "\"");
		
		in = "Standard 32: The rain in spain ... ";
		prtln ("in: " + in);
		prtln ("non number: \"" + getNonNumberText (in, 1) + "\"");
		prtln ("non number: \"" + getNonNumberText (in, 2) + "\"");

	}
	
	static Pattern getPattern (int level) {
		if (level == 1)
			return Pattern.compile(level1Pattern);
		if (level == 2)
			return Pattern.compile(level2Pattern);
		return null;
	}
	
	static String getDisplayText (AsnStandard std) {
		int level = std.getLevel();
		String baseText = std.getDescription();
		if (level != 2)
			return baseText;
		
		AsnStandard parent = std.getParentStandard();
		String parentNum = ColoradoStandardDisplay.getNumber (parent);
		String myNum = ColoradoStandardDisplay.getNumber (std);
		if (parentNum != null && myNum != null) {
			return parentNum + "." + myNum + ". " + 
					getNonNumberText (baseText, level);
		}
		return baseText;
	}
	
	static String getNumber (AsnStandard std) {
		return getNumber (std.getDescription(), std.getLevel());
	}

	
	/**
	* using regular expression like this is not all that solid (what
	if there are other occurances?
	*/
	static String getNumber (String input, int level) {
		try {
			return getNumber (input, getPattern (level));
		} catch (Exception e) {
			prtln ("getNumber: " + e.getMessage());
		}
		return null;
	}
	
	static String getNonNumberText (String input, int level) {
		try {
			return getNonNumberText (input, getPattern (level));
		} catch (Exception e) {
			prtln ("getNumber: " + e.getMessage());
		}
		return null;
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
	
