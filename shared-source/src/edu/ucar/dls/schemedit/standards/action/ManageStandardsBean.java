/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
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

