/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
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
	
