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

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.action.form.SessionsForm;

import java.util.*;
import java.lang.*;
import java.io.*;
import java.text.*;

import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;

/**
 *  Action supporting asynchronous exchange of information between UI and
 *  session, e.g., saving "collectionFilter" information that stored in the
 *  sessionBean and therefore accessible to any jsp page. The collectionFilter
 *  information is sent by the UI as an ajax request, and processed here, but no
 *  forward link is returned and therefore there is no noticable change of state
 *  in the UI.
 *
 * @author    Ostwald
 */
public final class SessionInfoAction extends DCSAction {
	private static boolean debug = true;


	/**
	 *  Process the specified HTTP request, and create the corresponding HTTP
	 *  response (or forward to another web component that will create it). Return
	 *  an <code>ActionForward</code> instance describing where and how control
	 *  should be forwarded, or <code>null</code> if the response has already been
	 *  completed.
	 *
	 * @param  mapping        The ActionMapping used to select this instance
	 * @param  response       The HTTP response we are creating
	 * @param  form           The ActionForm for the given page
	 * @param  req            The HTTP request.
	 * @return                The ActionForward instance describing where and how
	 *      control should be forwarded
	 * @exception  Exception  If error.
	 */
	public ActionForward execute(
	                             ActionMapping mapping,
	                             ActionForm form,
	                             HttpServletRequest req,
	                             HttpServletResponse response)
		 throws Exception {

		/*
		 *  Design note:
		 *  Only one instance of this class gets created for the app and shared by
		 *  all threads. To be thread-safe, use only local variables, not instance
		 *  variables (the JVM will handle these properly using the stack). Pass
		 *  all variables via method signatures rather than instance vars.
		 */
		// Extract attributes we will need

		prtln("");

		ActionErrors errors = initializeFromContext(mapping, req);
		if (!errors.isEmpty()) {
			saveErrors(req, errors);
			return (mapping.findForward("error.page"));
		}
		SessionsForm sessionsForm = (SessionsForm) form;
		ServletContext servletContext = getServlet().getServletContext();

		boolean authEnabled = (Boolean) servletContext.getAttribute("authenticationEnabled");

		SchemEditUtils.showRequestParameters(req);

		String command = req.getParameter("command");
		if (command != null && command.trim().length() > 0) {
			try {
				if (command.equals("updateCollectionFilter")) {
					prtln("Updating collectionFilter");
					updateCollectionFilter(req);
					return null;
				}
			} catch (Throwable e) {
				prtln("SessionInfoAction caught exception: " + e);
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}


	protected void updateCollectionFilter(HttpServletRequest request) {
		String column = request.getParameter("fwcol");
		String value = request.getParameter("fwval");
		if (value != null || column != null)
			getSessionBean(request).updateCollectionFilter(column, value);
	}

	// ---------------------- Debug info --------------------

	/**
	 *  Return a string for the current time and date, sutiable for display in log
	 *  files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	protected final static String getDateStamp() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final void prtlnErr(String s) {
		System.err.println(getDateStamp() + " " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final void prtln(String s) {
		if (debug) {
			System.out.println(getDateStamp() + " SessionInfoAction: " + s);
		}
	}


	/**
	 *  Sets the debug attribute of the object
	 *
	 * @param  db  The new debug value
	 */
	public static void setDebug(boolean db) {
		debug = db;
	}

}

