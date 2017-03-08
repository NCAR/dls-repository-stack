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
package edu.ucar.dls.schemedit.action.form;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.dcs.*;

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
 *  ActionForm bean for handling requests for Collections-based operations, such
 *  as creating, deleting, exporting and validating collections.
 *
 * @author    ostwald
 */
public class NLDRToolsForm extends ActionForm {

	private boolean debug = true;

	// private HttpServletRequest request;
	public NLDRToolsForm () {}

	// -------- Fetch service stuff
	
	private boolean isUpdating = false; // is the updatingServiceActive
	
	public boolean getIsUpdating () {
		return this.isUpdating;
	}
	
	public void setIsUpdating (boolean bool) {
		this.isUpdating = bool;
	}
	
	private String updatingSession = null;
	
	public String getUpdatingSession () {
		return this.updatingSession;
	}
	
	public void setUpdatingSession(String sessionId) {
		this.updatingSession = sessionId;
	}
		
	private String progress;
	
	public String getProgress () {
		return this.progress;
	}
	
	public void setProgress (String rpt) {
		this.progress = rpt;
	}

		
	private String updateReport;
	
	public String getUpdateReport () {
		return this.updateReport;
	}
	
	public void setUpdateReport (String rpt) {
		this.updateReport = rpt;
	}
	
	
	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 * @param  s  The String that will be output.
	 */
	protected final void prtln(String s) {
		if (debug) {
			System.out.println("NLDRToolsForm: " + s);
		}
	}

}

