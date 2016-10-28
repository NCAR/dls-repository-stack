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

package edu.ucar.dls.schemedit.standards.adn.util;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.standards.adn.*;

import edu.ucar.dls.xml.schema.SchemaHelper;
import java.util.*;
import java.net.*;

public class MappingUtils {
	private static boolean debug = true;
	
	public static SchemaHelper getSchemaHelper(String schemaURLStr, String rootElementName) {

		URL schemaURL = null;
		SchemaHelper sh = null;
		try {
			schemaURL = new URL(schemaURLStr);
			sh = new SchemaHelper(schemaURL, rootElementName);
		} catch (MalformedURLException urlExc) {
			prtln("unable to create URL from " + schemaURLStr);
			return null;
		} catch (Exception e) {
			prtln("failed to instantiate SchemaHelper: " + e);
			return null;
		}
		return sh;
	}
	
	public static DleseStandardsDocument getDleseStandardsDocument () {
		String schemaURLStr = "http://dev.dlese.org:7080/Metadata/adn-item/0.7.00/record.xsd";
		SchemaHelper sh = getSchemaHelper (schemaURLStr, "itemRecord");
		return getDleseStandardsDocument (sh);
	}
		
	public static DleseStandardsDocument getDleseStandardsDocument (SchemaHelper sh) {
		String xpath = "/itemRecord/educational/contentStandards/contentStandard";
		List dataTypeNames = new ArrayList();
		dataTypeNames.add ("NCGEgeographyContentStandardsType");
		dataTypeNames.add ("NSESscienceContentStandardsAllType");
		List adnStandards = new ArrayList();
		
		DleseStandardsDocument sm = null;
		try {
			// t.setMaxNodes (101);
			sm = new DleseStandardsDocument(sh, dataTypeNames);
		} catch (Exception e) {
			prtln ("getDleseStandardsDocument initialization error: " + e.getMessage());
			e.printStackTrace(); 
			return null;
		}
		return sm;
	}
	
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("MappingUtils: " + s);
			System.out.println(s);
		}
	}
}
