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

package edu.ucar.dls.schemedit.sif;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.standards.asn.NameSpaceXMLDocReader;
import edu.ucar.dls.util.Files;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

public class LearningResourceReader extends  SIFDocReader {
	private static boolean debug = true;
	
	public LearningResourceReader (String xml) throws Exception {
		super (xml);
	}
	
	public String getHeaderText () {
		return getName();
	}
	
	public String getDescriptiveText() {
		return getDescription();
	}
	
	public String getXmlFormat () {
		return "sif_learning_resource";
	}
	
	public String getFormatName () {
		return "LearningResource";
	}		
	
	public String getName () {
		return getNodeText ("/sif:LearningResource/sif:Name");
	}
	
	public String getDescription () {
		return getNodeText ("/sif:CurriculumStructure/sif:Description");
	}
	
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "LearningResourceReader: ");
		}
	}
}
