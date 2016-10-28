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
package edu.ucar.dls.schemedit.standards.action;

import edu.ucar.dls.standards.asn.util.AsnCatalog;

import edu.ucar.dls.schemedit.FrameworkRegistry;
import edu.ucar.dls.schemedit.MetaDataFramework;
import edu.ucar.dls.schemedit.standards.StandardsRegistry;
import edu.ucar.dls.schemedit.standards.StandardsManager;
import edu.ucar.dls.schemedit.standards.asn.AsnStandardsManager;
import edu.ucar.dls.schemedit.standards.asn.AsnDocInfo;
import edu.ucar.dls.xml.*;

import org.dom4j.Document;

import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;


/**
 *  Bean exposing information about the loaded/managed standards across all
 *  frameworks.<p>
 *
 *  Builds a mapping from xmlFormat to StandardsManagerBean for each configured
 *  framework.
 *
 *@author    Jonathan Ostwald
 */
public final class ManageStandardsBean {

	private static boolean debug = true;

	private AsnCatalog asnCatalog = null;
	private Map standardsManagerMap = null;
	List xmlFormats = null;

	// public ManageStandardsBean (AsnCatalog asnCatalog, FrameworkRegistry frameworkRegistry) {
	/**
	 *  Constructor for the ManageStandardsBean object requiring a FrameworkRegistry instance.
	 *
	 *@param  frameworkRegistry  Description of the Parameter
	 */
	public ManageStandardsBean(FrameworkRegistry frameworkRegistry) {
		this.standardsManagerMap = new HashMap();// xmlFormt -> standardsManager

		for (Iterator i = frameworkRegistry.getItemFormats().iterator(); i.hasNext(); ) {
			String xmlFormat = (String) i.next();
			MetaDataFramework framework = frameworkRegistry.getFramework(xmlFormat);
			StandardsManager stdmgr = framework.getStandardsManager();
			if (stdmgr != null && stdmgr instanceof AsnStandardsManager) {
				this.standardsManagerMap.put(xmlFormat, new StandardsManagerBean((AsnStandardsManager) stdmgr));
			}
		}
	}


	/**
	 *  Returns a mapping from xmlFormat to StandardsManagerBean for each configured framework.
	 *
	 *@return    The standardsManagerBeanMap value
	 */
	public Map getStandardsManagerBeanMap() {
		return this.standardsManagerMap;
	}
	
	public AsnDocInfo get (String arg) {
		return StandardsRegistry.getInstance().get(arg);
	}


	/**
	 *  Gets the xmlFormats configured for standards management.
	 *
	 *@return    The xmlFormats value
	 */
	public List getXmlFormats() {
		if (xmlFormats == null) {
			xmlFormats = new ArrayList();
			for (Iterator i = this.standardsManagerMap.keySet().iterator(); i.hasNext(); ) {
				xmlFormats.add((String) i.next());
			}
			Collections.sort(xmlFormats);
		}
		return xmlFormats;
	}



	// -------------- Debug ------------------

	/**
	 *  Sets the debug attribute of the ManageStandardsBean class
	 *
	 *@param  isDebugOutput  The new debug value
	 */
	public static void setDebug(boolean isDebugOutput) {
		debug = isDebugOutput;
	}


	/**
	 *  Print a line to standard out.
	 *
	 *@param  s  The String to print.
	 */
	private void prtln(String s) {
		if (debug) {
			System.out.println("ManageStandardsBean: " + s);
		}
	}
}

