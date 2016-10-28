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

import edu.ucar.dls.serviceclients.asn.acsr.GetJurisdictions.JurisBean;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.Files;

import java.io.*;
import java.util.*;
import java.net.*;
import org.dom4j.*;

/**
 *  Creates a mapping from abreviation to jurisdiction (e.g., "AL" : "Alabama")
 *  for all jurisdictions supplied by ACSR getJurisdictions command.
 *
 * @author    Jonathan Ostwald
 */
public class JurisMapper {
	private static boolean debug = true;

	private List data = null;
	private Map nameMap = null;
	private Map jurisMap = null;
	private static JurisMapper instance = null;


	/**
	 *  Gets the instance attribute of the JurisMapper class
	 *
	 * @return    The instance value
	 */
	public static JurisMapper getInstance() {
		if (instance == null) {
			instance = new JurisMapper();
		}
		return instance;
	}


	/**  Constructor for the JurisMapper object */
	private JurisMapper() {
		GetJurisdictions client = new GetJurisdictions();
		nameMap = new HashMap();
		jurisMap = new HashMap();
		try {
			data = client.getDetailedResults();
		} catch (Exception e) {
			prtln("Error: " + e.getMessage());
		}
		for (Iterator i = data.iterator(); i.hasNext(); ) {
			JurisBean jurisBean = (JurisBean) i.next();
			String juris = jurisBean.getJurisdiction();
			String name = jurisBean.getName();
			nameMap.put(name, juris);
			jurisMap.put(juris, name);
		}
	}


	/**
	 *  Gets the full version for provided jurisdiction name attribute of the JurisMapper object
	 *
	 * @param  juris  NOT YET DOCUMENTED
	 * @return        The name value
	 */
	public String getName(String juris) {
		return (String) jurisMap.get(juris);
	}


	/**
	 *  Gets the juris abrev for provided full jurisdiction
	 *
	 * @param  name  NOT YET DOCUMENTED
	 * @return       The juris value
	 */
	public String getJuris(String name) {
		return (String) nameMap.get(name);
	}


	/**
	 *  The main program for the JurisMapper class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		// JurisMapper mapper = new JurisMapper();
		JurisMapper mapper = getInstance();
		for (Iterator i = mapper.data.iterator(); i.hasNext(); ) {
			JurisBean juris = (JurisBean) i.next();
			prtln(juris.getJurisdiction() + ": " + juris.getName());
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			// SchemEditUtils.prtln(s, "AsnDocument");
			SchemEditUtils.prtln(s, "");
		}
	}
}


