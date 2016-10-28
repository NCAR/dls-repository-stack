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

public class LessonReader extends  SIFDocReader {
	private static boolean debug = true;
	
	public LessonReader (String xml) throws Exception {
		super (xml);
	}
	
	public String getHeaderText () {
		return getTitle();
	}
	
	public String getDescriptiveText() {
		return getDescription();
	}
	
	public String getXmlFormat () {
		return "sif_lesson";
	}
	
	public String getFormatName () {
		return "Lesson";
	}		
	
	public String getTitle () {
		return getNodeText ("/sif:Lesson/sif:Title");
	}
	
	public String getDescription () {
		return getNodeText ("/sif:Lesson/sif:Description");
	}
	
 	public static void main (String [] args) throws Exception {
		String records = "C:/Documents and Settings/ostwald/devel/dcs-instance-data/ccs/records/";
		String path = records + "sif_lesson/1210372130580/LESSON-000-000-000-001.xml";
		
		String xml = Files.readFile(path).toString();
		
		LessonReader reader = new LessonReader (xml);
		prtln ("xmlFormat: " + reader.getXmlFormat());
		prtln ("formatName: " + reader.getFormatName());
		prtln ("refId: " + reader.getRefId());
		prtln ("title: " + reader.getTitle());
		prtln ("description: " + reader.getDescription());
	}
	
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "LessonReader: ");
		}
	}
}
