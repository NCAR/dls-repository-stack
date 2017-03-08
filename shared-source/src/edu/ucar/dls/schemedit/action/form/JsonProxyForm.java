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

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.*;

/**
 *  ActionForm supporting ProxyAction
 *
 *@author    ostwald 
 
 */
public class JsonProxyForm extends ActionForm {

	private boolean debug = false;
	private HttpServletRequest request;

	private String proxyResponse;


	/* private SetInfo setInfo = null; */
	// input params
	/**
	 *  Constructor
	 */
	public JsonProxyForm() { }


	/**
	 *  Description of the Method
	 */
	public void clear() {

	}

	public String getProxyResponse () {
		return this.proxyResponse;
	}
	
	public void setProxyResponse (String proxyResponse) {
		this.proxyResponse = proxyResponse;
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 *@param  s  The String that will be output.
	 */
	protected final void prtln(String s) {
		if (debug) {
			System.out.println("JsonProxyForm: " + s);
		}
	}

}

