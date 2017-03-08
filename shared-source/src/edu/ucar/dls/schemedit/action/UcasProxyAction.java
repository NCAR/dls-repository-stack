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
package edu.ucar.dls.schemedit.action;

import java.io.IOException;
import java.util.*;

import org.dom4j.Document;
import org.json.XML;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

import edu.ucar.dls.schemedit.action.form.UcasProxyForm;
import edu.ucar.dls.schemedit.SchemEditUtils;

import edu.ucar.dls.serviceclients.peopledb.PositionHistory;


/**
 *  Controller for proxing the UCAS PeopleDB REST service.
 *
 * @author    Jonathan Ostwald
 */

public final class UcasProxyAction extends Action {

	private static boolean debug = true;

	/**
	 *  Process the specified HTTP request, and create the corresponding HTTP
	 *  response (or forward to another web component that will create it). Return
	 *  an <code>ActionForward</code> instance describing where and how control
	 *  should be forwarded, or <code>null</code> if the response has already been
	 *  completed.
	 *
	 * @param  mapping               The ActionMapping used to select this instance
	 * @param  request               The HTTP request we are processing
	 * @param  response              The HTTP response we are creating
	 * @param  form                  NOT YET DOCUMENTED
	 * @return                       NOT YET DOCUMENTED
	 * @exception  IOException       if an input/output error occurs
	 * @exception  ServletException  if a servlet exception occurs
	 */
	public ActionForward execute(ActionMapping mapping,
	                             ActionForm form,
	                             HttpServletRequest request,
	                             HttpServletResponse response)
		 throws IOException, ServletException {

		ActionErrors errors = new ActionErrors();

		UcasProxyForm ucasForm = (UcasProxyForm) form;

		// Query Args
		String command = request.getParameter("command");

		SchemEditUtils.showRequestParameters(request);

		try {
			if (command.equals ("peopleDB2Search")){
				return doPeopleDB2Search(mapping, form, request, response);
			}
			if (command.equals ("peopleSearch")){
				return doPeopleSearch(mapping, form, request, response);
			}
			else if (command.equals ("getPerson")) {
				return doGetPerson(mapping, form, request, response);
			}
			else if (command.equals ("getHistory")) {
				return doPositionHistory(mapping, form, request, response);
			}			
		} catch (Throwable t) {
			t.printStackTrace();
			errors.add("error",
				new ActionError("generic.error", "System Error: " + t.getMessage()));
			saveErrors(request, errors);
		}

		// Forward control to the specified success URI
		return (mapping.findForward("error.page"));
	}

	private ActionForward doPeopleDB2Search(ActionMapping mapping,
	                                      ActionForm form,
	                                      HttpServletRequest request,
	                                      HttpServletResponse response) {

		prtln("\ndoPeopleDB2Search()");
		
		
		UcasProxyForm ucasForm = (UcasProxyForm) form;
		Document responseDoc = null;

		try {
			
			String peopleDBUrl = request.getParameter("url");
			if (peopleDBUrl == null)
				throw new Exception ("url parameter required");
			prtln ("peopleDBUrl: " + peopleDBUrl);
			
			String json = edu.ucar.dls.util.TimedURLConnection.importURL(peopleDBUrl, 5000);
			// prtln("json: " + json);
			ucasForm.setJson(json);
		} catch (Exception e) {
			prtln("doPeopleSearch error: " + e.getMessage());
			e.printStackTrace();
			ucasForm.setJson("ERROR: " + e.getMessage());
		}
		return mapping.findForward("ucas.info");
	}



	/**
	 *  Queries the UCAS People DB via the Rest API and returns results as json.
	 *
	 * @param  mapping   the mapping
	 * @param  form      the form
	 * @param  request   the request
	 * @param  response  the response
	 * @return           forward to jsp that accesses json from ActionForm.
	 */
	private ActionForward doPeopleSearch(ActionMapping mapping,
	                                      ActionForm form,
	                                      HttpServletRequest request,
	                                      HttpServletResponse response) {

		prtln("\ndoPeopleSearch()");

		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		UcasProxyForm ucasForm = (UcasProxyForm) form;
		Document responseDoc = null;

		try {
			String url = "https://api.ucar.edu/people/internalPersons?";
			if (firstName != null) {
				url += "firstName=" + firstName;
				if (lastName != null)
					url += "&";
			}
			if (lastName != null) {
				url += "lastName=" + lastName;
			}
			prtln ("url: " + url);
			
			String json = edu.ucar.dls.util.TimedURLConnection.importURL(url, 5000);
			// prtln("json: " + json);
			ucasForm.setJson(json);
		} catch (Exception e) {
			prtln("doPeopleSearch error: " + e.getMessage());
			e.printStackTrace();
			ucasForm.setJson("ERROR: " + e.getMessage());
		}
		return mapping.findForward("ucas.info");
	}

	/**
	 *  Queries the UCAS People DB via the Rest API and returns results as json.
	 *
	 * @param  mapping   the mapping
	 * @param  form      the form
	 * @param  request   the request
	 * @param  response  the response
	 * @return           forward to jsp that accesses json from ActionForm.
	 */
	private ActionForward doGetPerson(ActionMapping mapping,
	                                      ActionForm form,
	                                      HttpServletRequest request,
	                                      HttpServletResponse response) {

		prtln("\ndoGetPerson()");

		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		UcasProxyForm ucasForm = (UcasProxyForm) form;
		Document responseDoc = null;

		try {
			String upid = request.getParameter("upid");
			if (upid == null || upid.trim().length() == 0)
				throw new Exception ("upid parameter is required");
			String url = "https://api.ucar.edu/people/internalPersons/"+upid;
			prtln ("url: " + url);
			
			String json = edu.ucar.dls.util.TimedURLConnection.importURL(url, 5000);
			prtln("json: " + json);
			ucasForm.setJson(json);
		} catch (Exception e) {
			prtln("doGetPerson error: " + e.getMessage());
			e.printStackTrace();
			ucasForm.setJson("ERROR: " + e.getMessage());
		}
		return mapping.findForward("ucas.info");
	}

	/**
	 *  Queries the UCAS People DB via the Rest API and returns results as json.
	 *
	 * @param  mapping   the mapping
	 * @param  form      the form
	 * @param  request   the request
	 * @param  response  the response
	 * @return           forward to jsp that accesses json from ActionForm.
	 */
	private ActionForward doPositionHistory (ActionMapping mapping,
	                                      ActionForm form,
	                                      HttpServletRequest request,
	                                      HttpServletResponse response) {

		prtln("\ndoPeopleSearch()");

		String upid = request.getParameter("upid");
		UcasProxyForm ucasForm = (UcasProxyForm) form;
		Document responseDoc = null;

		try {
			PositionHistory positionHistory = new PositionHistory(upid);
			String json = positionHistory.getHistoryJson();
			
			// prtln("json: " + json);
			ucasForm.setJson(json);
		} catch (Exception e) {
			prtln("doPositionHistory error: " + e.getMessage());
			e.printStackTrace();
			ucasForm.setJson("ERROR: " + e.getMessage());
		}
		return mapping.findForward("ucas.info");
	}

	
	/**
	 *  Gets the request referer, attempting to overcome IE but that referer is not
	 *  sent in the header from event handlers. First looks for a request param
	 *  named "referer" and uses this if found, otherwise uses the "referer" from
	 *  the header.
	 *
	 * @param  request  NOT YET DOCUMENTED
	 * @return          The referer value
	 */
	private String getReferer(HttpServletRequest request) {
		String referer = request.getParameter("referer");
		return (referer != null) ? referer : request.getHeader("referer");
	}


	/**
	 *  Debugging 
	 *
	 * @param  request  the request
	 */
	private void showHeaders(HttpServletRequest request) {
		prtln("\n REQUEST HEADERS");
		Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = (String) headerNames.nextElement();
			prtln(name + ": " + request.getHeader(name));
		}
		prtln("-----------\n");
	}



	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	static void prtln(String s) {
		if (debug)
			SchemEditUtils.prtln(s, "UcasProxyAction");
	}

}

