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
public class GetStandard extends CATWebService {

	private static boolean debug = true;
	private final static String METHOD = "getStandards";


	/**  Constructor for the GetStandard object */
 	public GetStandard(File propsFile) {
		super(propsFile);
	}


	/**
	 *  Constructor for the GetStandard object supplying username, password and
	 *  baseUrl
	 *
	 * @param  username  the client username
	 * @param  password  the client password
	 * @param  baseUrl   the service baseUrl
	 */
	public GetStandard(String username, String password, String baseUrl) {
		super(username, password, baseUrl);
	}

	/**
	* returns "getStandards"
	*/
	protected String getMethod() {
		return METHOD;
	}


	/**
	 * @param  identifier     id of standard to get
	 * @return                CATStandard instance for specified id
	 * @exception  Exception  
	 */
	public CATStandard get(String identifier) throws Exception {
		Map args = new HashMap();
		args.put("username", this.username);
		args.put("password", this.password);
		args.put("identifier", identifier);
		args.put("method", METHOD);

		Document response = null;
		try {
			response = this.getResponse(args);
		} catch (Throwable t) {
			throw new Exception("webservice error: " + t);
		}

		Element result = (Element) response.selectSingleNode("/CATWebService/Standards/Standard");
		return new CATStandard(result);
	}


	/**
	 *  The main program for the GetStandard class
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(String[] args) {
		File propsFile = new File ("C:/mykeys/cat_service.properties");
		GetStandard client = new GetStandard(propsFile);
		String identifier = "http://purl.org/ASN/resources/S100EAA1";

		CATStandard result = null;
		try {
			result = client.get(identifier);
		} catch (Throwable t) {
			prtln("ERROR: " + t.getMessage());
			System.exit(1);
		}
		prtln("fetched result id: " + result.getAsnId());
		prtln(Dom4jUtils.prettyPrint(result.getElement()));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		String prefix = null;
		if (debug) {
			SchemEditUtils.prtln(s, prefix);
		}
	}

}

