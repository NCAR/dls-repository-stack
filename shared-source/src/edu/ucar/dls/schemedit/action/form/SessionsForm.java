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

package edu.ucar.dls.schemedit.action.form;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.webapps.tools.*;
import edu.ucar.dls.util.*;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.io.*;
import java.text.*;

/**
 *  This class uses the getter methods of the ProviderBean and then adds setter methods
 *  for editable fields.
 *
 * @author    Jonathan Ostwald
 */
public final class SessionsForm extends ActionForm implements Serializable {

	private Map lockedRecords = null;
	private List sessionBeans = null;
	private boolean showAnonymousSessions = false;

	public SessionsForm () {}
	
	public List getSessionBeans () {
		return sessionBeans;
	}
	
	public void setSessionBeans (List sessionBeans) {
		this.sessionBeans = sessionBeans;
	}
	
	public void setLockedRecords (Map lockedRecords) {
		this.lockedRecords = lockedRecords;
	}
	
	public Map getLockedRecords () {
		return lockedRecords;
	}
	
	public void setShowAnonymousSessions (boolean show) {
		showAnonymousSessions = show;
	}
	
	public boolean getShowAnonymousSessions () {
		return showAnonymousSessions;
	}
}


