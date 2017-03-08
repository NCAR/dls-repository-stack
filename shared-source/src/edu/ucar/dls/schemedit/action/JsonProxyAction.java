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

import edu.ucar.dls.schemedit.action.form.JsonProxyForm;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.util.TimedURLConnection;


import java.io.IOException;
import java.util.*;
import java.net.URL;
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


/**
 *  Simple proxy that fetches a provided URL and returns the response as a String.
 *
 * @author    Jonathan Ostwald
 */

public final class JsonProxyAction extends Action {

	private static boolean debug = false;

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

			 
		prtln ("JsonProxyAction executing");
			 
		ActionErrors errors = new ActionErrors();

		JsonProxyForm jsonProxyForm = (JsonProxyForm) form;

		// Query Args

		SchemEditUtils.showRequestParameters(request);

		try {
			return handleProxy(mapping, form, request, response);
		} catch (Throwable t) {
			jsonProxyForm.setProxyResponse("proxyAction error: " + t.getMessage());
		}

		// Forward control to the specified success URI
		return (mapping.findForward("json.response"));
	}

	private ActionForward handleProxy(ActionMapping mapping,
	                                         ActionForm form,
	                                         HttpServletRequest request,
	                                         HttpServletResponse response) throws Exception {
		JsonProxyForm jsonProxyForm = (JsonProxyForm) form;
		// prtln("processing general proxy");
		jsonProxyForm.setProxyResponse("");
		String uri = request.getParameter("uri");
		if (uri == null || uri.trim().length() == 0)
			throw new Exception("proxy handler did not receive a URI");

		prtln ("\nabout to hit: " + uri);
		
		String proxyResponse = null;
		try {
			proxyResponse = TimedURLConnection.importURL(uri, 10000);
			prtln("\n RAW RESPONSE\n" + proxyResponse);
			jsonProxyForm.setProxyResponse (proxyResponse);
			prtln("placed response in form");
		} catch (Exception e) {
			String errMsg = "proxy response error: " + e.getMessage();
			prtln(errMsg);
			// e.printStackTrace();
			jsonProxyForm.setProxyResponse(errMsg);
		}		
		return mapping.findForward("json.response");
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	static void prtln(String s) {
		if (debug)
			SchemEditUtils.prtln(s, "JsonProxyAction");
	}

}

