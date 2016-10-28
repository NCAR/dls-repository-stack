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
package edu.ucar.dls.serviceclients.cat;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.propertiesmgr.PropertiesManager;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.TimedURLConnection;
import edu.ucar.dls.util.URLConnectionTimedOutException;
import org.dom4j.*;
import java.util.*;
import java.net.*;
import java.io.File;

/**
 * @author    Jonathan Ostwald
 */
public class CATServiceToolkit {

	private static boolean debug = true;

	public static File propertiesFile;
	
	private String username;
	private String password;
	private String baseUrl;
	
	public CATServiceToolkit() {
		try {
			PropertiesManager props = new PropertiesManager(propertiesFile.getAbsolutePath());
			username = props.getProp("cat.service.username", null);
			if (username == null)
				throw new Exception ("username property not found");
			password = props.getProp("cat.service.password", null);
			if (password == null)
				throw new Exception ("password property not found");
			baseUrl = props.getProp("cat.service.baseurl", null);
			if (baseUrl == null)
				throw new Exception ("baseUrl property not found");

		} catch (Throwable t) {
			prtlnErr("CATServiceToolkit properties error: " + t.getMessage());
		}
	}
	
	/**
	 *  Constructor for the CATServiceToolkit object
	 *
	 * @param  username  username for authenticating to the CAT Web Service
	 * @param  password  password for authenticating to the CAT Web Service
	 */
	public CATServiceToolkit(String username, String password, String baseUrl) {
		this.username = username;
		this.password = password;
		this.baseUrl = baseUrl;
	}

	public Map getAllCatDocs () throws Exception {
		GetAllStandardsDocuments client = new GetAllStandardsDocuments (this.username, this.password, this.baseUrl);
		return client.getAllDocsMap();
	}
		
	public List getSuggestions (CATRequestConstraints constraints) throws Exception {
		SuggestStandards client = new SuggestStandards (this.username, this.password, this.baseUrl);
		return client.getSuggestions(constraints);
	}
	
	/**
	 *  Description of the Method
	 *
	 * @param  node  Description of the Parameter
	 */
	protected static void pp(Node node) {
		prtln(Dom4jUtils.prettyPrint(node));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		String prefix = "CATServiceToolkit";
		if (debug) {
			SchemEditUtils.prtln(s, prefix);
		}
	}
	
	private static void prtlnErr(String s) {
		String prefix = "CATServiceToolkit";
		SchemEditUtils.prtln(s, prefix);
	}

}

