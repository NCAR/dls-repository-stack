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

package edu.ucar.dls.serviceclients.asn.acsr;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.Files;

import java.io.*;
import java.util.*;
import java.net.*;
import org.dom4j.*;

/**
 *  Use the ACSRService (http://www.jesandco.net/asn/asnwebservice/acsrservice.asmx) to obtain 
 a Standard Document
 *
 * @author    Jonathan Ostwald
 */
public class GetJurisdictions extends ACSRClient {
	private static boolean debug = false;
	
	private Map argMap = null;
	
	/**  Constructor for the GetJurisdictions object */
	public GetJurisdictions() { 
		setDebug (debug);
		argMap = new HashMap();
	}

	public Map getArgMap () {
		return this.argMap;
	}
	
	public String getCommand () {
		return "GetJuris";
	}
	
	public List getResults () throws Exception {
		String path = "//*[local-name()='Jurisdiction'";
		List docElements = this.getResponse().selectNodes (path);
		
		List results = new ArrayList();
		for (Iterator i=docElements.iterator();i.hasNext();) {
			JurisBean result = new JurisBean ((Element)i.next());
			results.add (result.organizationJurisdiction);
		}
		return results;
	}
	
	public List getDetailedResults () throws Exception {
		String path = "//*[local-name()='Jurisdiction'";
		List docElements = this.getResponse().selectNodes (path);
		
		List results = new ArrayList();
		for (Iterator i=docElements.iterator();i.hasNext();) {
			results.add (new JurisBean ((Element)i.next()));
		}
		return results;
	}
	
	/**
	 *  The main program for the GetJurisdictions class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		String acsrId = "00a6d3fc-0cad-4048-bafa-bc0be9d2c2cd";
		
		if (args.length > 0)
			acsrId = args[0];
		
		GetJurisdictions client = new GetJurisdictions();
		
		for (Iterator i=client.getDetailedResults().iterator();i.hasNext();) {
			JurisBean result = (JurisBean)i.next();
			prtln (result.organizationAlias + " - " + result.organizationJurisdiction);
		}
	}

	public class JurisBean {
		public String organizationName;
		public String organizationAlias;
		public String organizationJurisdiction;
		
		public JurisBean (Element e) {
			this.organizationName = e.element("organizationName").getTextTrim();
			this.organizationAlias = e.element("organizationAlias").getTextTrim();
			this.organizationJurisdiction = e.element("organizationJurisdiction").getTextTrim();
		}
		
		public String getJurisdiction () {
			return this.organizationJurisdiction;
		}
		
		public String getName () {
			return this.organizationName;
		}
	}
	
}


