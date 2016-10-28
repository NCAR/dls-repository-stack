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

package edu.ucar.dls.schemedit.ndr.action.form;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.ndr.SyncReport;
import edu.ucar.dls.schemedit.ndr.SyncService;
import edu.ucar.dls.schemedit.dcs.*;
import edu.ucar.dls.ndr.apiproxy.NDRConstants;
import edu.ucar.dls.ndr.reader.AgentReader;
import edu.ucar.dls.repository.*;
import edu.ucar.dls.index.ResultDoc;
import edu.ucar.dls.index.reader.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.*;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.LabelValueBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.io.*;
import java.text.*;
import java.net.*;
import java.util.regex.*;

/**
 *  ActionForm bean for handling interactions with the NDR.
 *
 */
public class NDRForm extends ActionForm {

	private boolean debug = true;
	private HttpServletRequest request;
	private SyncReport syncReport = null;
	private String proxyResponse = null;
	private String handle = null;
	private List browserHandles = null;
	private List ndrCollections = null;
	private List dcsCollections = null;
	private Map mdpHandleMap = null;
	private AgentReader appAgent = null;
	private SyncService syncService = null;

	/**
	 *  Constructor
	 */
	public NDRForm() { }
	
	public void setSyncService (SyncService svc) {
		this.syncService = svc;
	}
	
	public SyncService getSyncService () {
		return this.syncService;
	}
	
	public boolean getIsSyncing () {
		return (this.syncService == null ? false : this.syncService.getIsProcessing());
	}
	
	/** Stores results of a Sync operation */
 	public void setSyncReport (SyncReport report) {
		this.syncReport = report;
	}
	
	/** Gets results of a Sync operation */
	public SyncReport getSyncReport () {
		return (this.syncReport);
	}
	
	private String progress = null;
	
	public void setProgress (String progress) {
		this.progress = progress;
	}
	
	public String getProgress () {
		return this.progress;
	}
	
	public void setHandle (String handle) {
		this.handle = handle;
	}
	
	public String getHandle () {
		return this.handle;
	}
	
	public String getNdrApiBaseUrl () {
		return NDRConstants.getNdrApiBaseUrl();
	}

	public String getNcsAgentHandle () {
		return NDRConstants.getNcsAgent ();
	}
	
	public void setAppAgent (AgentReader agentReader) {
		this.appAgent = agentReader;
	}
	
	public String getAppAgentIdentity () {
		if (this.appAgent != null)
			return this.appAgent.getIdentifier();
		else
			return null;
	}
	
	public String getAppAgentIdentityType () {
		if (this.appAgent != null)
			return this.appAgent.getIdentifierType();
		else
			return null;
	}

	/** Stores result of async call to NDR */
	public void setProxyResponse (String response) {
		this.proxyResponse = response;
	}
	
	/** Get result of async call to NDR. Can be json object, xml, or a simple String */
	public String getProxyResponse () {
		return this.proxyResponse;
	}
	
	/** Handles to either aggregator or mdp objects for use in NDR Browser */
 	public void setBrowserHandles (List handles) {
		this.browserHandles = handles;
	}
	
	public List getBrowserHandles () {
		return this.browserHandles;
	} 
	
	/** handleMap associates a setSpec with the corresponding mdpHandle */
	public void setMdpHandleMap (Map handleMap) {
		this.mdpHandleMap = handleMap;
	}
	
	/** Gets Map associating a setSpec with the corresponding mdpHandle */
	public Map getMdpHandleMap () {
		return this.mdpHandleMap;
	}
	
	/** A list of setInfo instances for each collection registered with NDR */
	public void setNdrCollections (List sets) {
		this.ndrCollections = sets;
	}
	
	/** Gets list of setInfo instances for each collection registered with NDR */
	public List getNdrCollections () {
		return this.ndrCollections;
	}
	
	/** A list of setInfo instances for each collection NOT registered with NDR */
	public void setDcsCollections (List sets) {
		this.dcsCollections = sets;
	}
	
	/** Gets setInfo instances for each collection NOT registered with NDR */
	public List getDcsCollections () {
		return this.dcsCollections;
	}
	
	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 *@param  s  The String that will be output.
	 */
	protected final void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln (s, "NDRForm");
		}
	}

}

