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

import java.io.*;
import java.util.*;
import java.text.*;
import java.net.URI;
import org.dom4j.Node;
import edu.ucar.dls.schemedit.vocab.*;
import edu.ucar.dls.schemedit.*;

import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.Dom4jUtils;




/**
 *  Tester for {@link edu.ucar.dls.schemedit.vocab.FieldInfoMap}
 *
 *@author    ostwald
 */
public class FieldsFileTester {
	MetaDataFramework framework = null;
	FieldInfoMap fieldInfoMap = null;

	/**
	 *  Constructor for the FieldsFileTester object
	 */
	public FieldsFileTester(String uri) throws Exception {
		fieldInfoMap = new FieldInfoMap (uri);
		fieldInfoMap.init();
			
	}
	
	FieldInfoReader getFieldInfoReader (String xpath) throws Exception {
		FieldInfoReader reader = fieldInfoMap.getFieldInfo(xpath);
		if (reader != null)
			prtln (reader.toString());
		else 
			prtln ("FieldInfoReader not found for " + xpath);
		return reader;
	}
	
	void showFieldInfoReaders () {
		prtln ("Field Info Readers");
		for (Iterator i=fieldInfoMap.getFields().iterator();i.hasNext();) {
			prtln ("\t" + (String)i.next());
		}
	}
	
	public static void main (String [] args) throws Exception {
		// TesterUtils.setSystemProps();
		// URI uri = new URI ("file:///C:/Users/ostwald/devel/projects/nsdl-schemas-project/ncs/lar/1.00/build/fields-list.xml");
		URI uri = new URI ("http://ns.nsdl.org/ncs/lar/1.00/build/fields-list.xml");
		FieldsFileTester tester = new FieldsFileTester (uri.toString());
		String xpath = "/record/educational/standards/ASNstandard";
/* 		FieldInfoReader reader = tester.getFieldInfoReader (xpath);
		if (reader != null)
			prtln ("READER FOUND"); */
		
		// tester.showFieldInfoReaders();
		
	}
	
		
	static void pp (Node node) {
		prtln (Dom4jUtils.prettyPrint(node));
	}
	
	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	public static void prtln(String s) {
		SchemEditUtils.prtln (s, "");
	}
}

