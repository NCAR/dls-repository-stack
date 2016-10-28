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

package edu.ucar.dls.schemedit;

import edu.ucar.dls.serviceclients.remotesearch.reader.*;
import edu.ucar.dls.serviceclients.webclient.*;
import edu.ucar.dls.xml.*;

import java.io.*;
import java.util.*;

import org.dom4j.Node;
import org.dom4j.Element;
import org.dom4j.Document;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import edu.ucar.dls.index.ResultDoc;

public class ADNFragDocReader extends ADNItemDocReader {

	protected static boolean debug = true;
	protected String id = "";
	protected String collection = "";
	protected String readerType = "ADNFragDocReader";
	//String content = null;
	protected String [] EMPTY_ARRAY = new String[]{};
	protected String DEFAULT = "-- unspecified --";
	protected ArrayList EMPTY_LIST = new ArrayList();


	/**
	 * ADNFragDocReader constructor requiring an itemRecordDoc
	 */
	public ADNFragDocReader (String id, String collection, Document itemRecordDoc) {
		super (id, collection, itemRecordDoc, null);  // vocab=null;
		doc = Dom4jUtils.localizeXml(doc, "itemRecord");
		if (doc == null) {
			prtln (" constructor: doc never set");
		}
		else {
			// prtln (Dom4jUtils.prettyPrint (doc));
		}

	}

	// ----------------------- begin getters --------------------

	public String getMetadataPrefix() {
		return "adn_frag";
	}

	public static void prtln (String s) {
		if (debug)
			System.out.println("ADNFragDocReader: " + s);
	}



}
