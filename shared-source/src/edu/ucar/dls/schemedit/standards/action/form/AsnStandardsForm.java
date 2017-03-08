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
package edu.ucar.dls.schemedit.standards.action.form;

import edu.ucar.dls.schemedit.standards.action.ManageStandardsBean;
import edu.ucar.dls.schemedit.standards.action.StandardsManagerBean;
import edu.ucar.dls.schemedit.standards.StandardsRegistry;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.dcs.DcsSetInfo;
import edu.ucar.dls.util.*;

import edu.ucar.dls.standards.asn.util.AsnCatalog;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.io.*;
import java.text.*;

/**
 *  Supports the collection browser of the DCS. This class works in conjunction
 *  with {@link edu.ucar.dls.schemedit.action.DCSBrowseAction}.
 *
 *@author    Jonathan Ostwald
 */
public class AsnStandardsForm extends ActionForm implements Serializable {
	private static boolean debug = true;

	private HttpServletRequest request;
	private Map standardsManagers = null;
	private ManageStandardsBean manageStandardsBean = null;
	private StandardsManagerBean standardsManagerBean = null;

	/**
	 *  Constructor for the AsnStandardsForm object
	 */
	public AsnStandardsForm() { }

	public void setStandardsManagerBean (StandardsManagerBean stdmgr) {
		this.standardsManagerBean = stdmgr;
	}
	
	public StandardsManagerBean getStandardsManagerBean () {
		return this.standardsManagerBean;
	}
	
	public StandardsRegistry getStandardsRegistry () {
		return StandardsRegistry.getInstance();
	}
	
	public void setManageStandardsBean (ManageStandardsBean bean) {
		this.manageStandardsBean = bean;
	}
	
	public ManageStandardsBean getManageStandardsBean () {
		return this.manageStandardsBean;
	}

	/**
	 *  Sets the standardsManagers attribute of the AsnStandardsForm object
	 *
	 *@param  mgrs  The new standardsManagers value
	 */
	public void setStandardsManagers(Map mgrs) {
		this.standardsManagers = mgrs;
	}


	/**
	 *  Gets a mapping of xmlFormat to AsnStandardsManager instances
	 *
	 *@return    The standardsManagers value
	 */
	public Map getStandardsManagers() {
		return this.standardsManagers;
	}

	private Map allDocsSubjectMap = null;
	
	public Map getAllDocsSubjectMap () {
		prtln ("\ngetAllDocsSubjectMap()");
		if (this.asnCatalog == null) {
			prtln ("  asnCatalog is NULL, returning null");
			return null;
		}
		prtln ("   calling asnCatalog.getSubjectItems()");
		return this.asnCatalog.getSubjectItemsMap();
	}
	
	private AsnCatalog asnCatalog = null;
	
	public void setAsnCatalog (AsnCatalog cat) {
		this.asnCatalog = cat;
	}
	
	public AsnCatalog getAsnCatalog () {
		return this.asnCatalog;
	}
	// -------- Fetch service stuff
	
	private boolean isFetching = false; // is the fetchingServiceActive
	
	public boolean getIsFetching () {
		return this.isFetching;
	}
	
	public void setIsFetching (boolean bool) {
		this.isFetching = bool;
	}
	
	private String fetchingSession = null;
	
	public String getFetchingSession () {
		return this.fetchingSession;
	}
	
	public void setFetchingSession(String sessionId) {
		this.fetchingSession = sessionId;
	}
		
	private String progress;
	
	public String getProgress () {
		return this.progress;
	}
	
	public void setProgress (String rpt) {
		this.progress = rpt;
	}
	
	// - end Fetch service stuff
	
	private String xmlFormat = null;
	
	public void setXmlFormat (String format) {
		this.xmlFormat = format;
	}
	
	public String getXmlFormat () {
		return this.xmlFormat;
	}
	
	private String subject = null;
	
	public void setSubject (String format) {
		this.subject = format;
	}
	
	public String getSubject () {
		return this.subject;
	}
	
	/**
	 *  DESCRIPTION
	 *
	 *@param  mapping  DESCRIPTION
	 *@param  request  DESCRIPTION
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {

	}


	/**
	 *  Sets the request attribute of the AsnStandardsForm object.
	 *
	 *@param  request  The new request value
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	//================================================================

	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 *@param  s  The String that will be output.
	 */
	private final void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "AsnStandardsForm");
		}
	}


	/**
	 *  Return a string for the current time and date, sutiable for display in log
	 *  files and output to standout:
	 *
	 *@return    The dateStamp value
	 */
	private final static String getDateStamp() {
		return
				new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Sets the debug attribute of the object
	 *
	 *@param  db  The new debug value
	 */
	public static void setDebug(boolean db) {
		debug = db;
	}

}


