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

package edu.ucar.dls.services.dcs;

import edu.ucar.dls.xml.*;
import edu.ucar.dls.index.*;
import edu.ucar.dls.util.*;


import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.dcs.*;

import java.util.*;
import java.text.*;
import java.io.*;

import org.dom4j.*;

public class PutRecordData  {
	Document doc = null;
	String id;
	String format;
	String collection;
	MetaDataFramework framework = null;; 
	
	public PutRecordData () {
	}
	
	public String getId () {
		return id;
	}
	
	public String getCollection () {
		return collection;
	}
	
	public String getFormat () {
		return format;
	}
	
	public Document getDocument () {
		return doc;
	}
	
	public void init (String recordXml, String format, String collection, FrameworkRegistry frameworks) throws Exception {
		this.format = format;
		this.collection = collection;

		framework = frameworks.getFramework(format);
		if (framework == null)
			throw new Exception ("framework not found for \"" + format + "\" format");
			
		try {
			doc = Dom4jUtils.localizeXml(Dom4jUtils.getXmlDocument(recordXml), framework.getRootElementName());
		} catch (Exception e) {
			throw new Exception ("recordXml could not be read as XML");
		}
		
		// validate the id. ignore the parameter and use the id in the record
		String idPath = framework.getIdPath();
		try {
			id = doc.selectSingleNode(idPath).getText();
		} catch (Throwable t) {
			throw new Exception ("id could not be found in xmlRecord");
		}
	}
}

