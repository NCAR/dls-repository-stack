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

import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.util.*;

import java.io.*;
import java.lang.*;

public class XSDValidatorTester {

	static String [] testers = { "0", "999", "9999", "99999", "0001", "-0001", "-1"};
	
	public static void main (String[] args) throws Exception {
		// build schemaHelper for given framework
		File schemaFile = new File ("/devel/ostwald/metadata-frameworks/adn-item-project/record.xsd");
		SchemaHelper sh = new SchemaHelper (schemaFile);
		
		String type = "BCType";
		
		for (int i=0;i<testers.length;i++) {
			String value = testers[i];
			prtln ("\n\n ** year: " + value + " ***");
			try {
				if (sh.checkValidValue(type, value))
					prtln (value + " is a valid " + type);
				else
					prtln (value + " is NOT a valid " + type);
			} catch (Exception e) {
				prtln ("ERROR: " + e.getMessage());
			}
		}
	}
		
	private static void prtln (String s) {
		System.out.println (s);
	}
}
