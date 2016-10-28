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

package edu.ucar.dls.dds.action;

import edu.ucar.dls.repository.*;
import edu.ucar.dls.index.*;
import edu.ucar.dls.dds.action.form.*;
import edu.ucar.dls.index.reader.*;
import edu.ucar.dls.xml.*;
import org.apache.lucene.search.*;
import edu.ucar.dls.oai.*;
import edu.ucar.dls.vocab.*;

import java.util.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Locale;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;
import java.net.URLEncoder;

/**
 *  A Struts Action for managing the items in the DDS collections.
 *
 *
 * @author    John Weatherley
 */
public final class DDSManageCollectionsAction extends Action {

	private static boolean debug = false;


	// --------------------------------------------------------- Public Methods

	/**
	 *  Processes the specified HTTP request and creates the corresponding HTTP response by
	 *  forwarding to a JSP that will create it. A {@link
	 *  edu.ucar.dls.index.SimpleLuceneIndex} must be available to this class via a
	 *  ServletContext attribute under the key "index." Returns an {@link
	 *  org.apache.struts.action.ActionForward} instance that maps to the Struts forwarding
	 *  name "simple.query," which must be configured in struts-config.xml to forward to the
	 *  JSP page that will handle the request.
	 *
	 * @param  mapping               The ActionMapping used to select this instance
	 * @param  request               The HTTP request we are processing
	 * @param  response              The HTTP response we are creating
	 * @param  form                  The ActionForm for the given page
	 * @return                       The ActionForward instance describing where and how
	 *      control should be forwarded
	 * @exception  IOException       if an input/output error occurs
	 * @exception  ServletException  if a servlet exception occurs
	 */
	public ActionForward execute(
	                             ActionMapping mapping,
	                             ActionForm form,
	                             HttpServletRequest request,
	                             HttpServletResponse response)
		 throws IOException, ServletException {
		/*
		 *  Design note:
		 *  Only one instance of this class gets created for the app and shared by
		 *  all threads. To be thread-safe, use only local variables, not instance
		 *  variables (the JVM will handle these properly using the stack). Pass
		 *  all variables via method signatures rather than instance vars.
		 */
		DDSManageCollectionsForm mf = (DDSManageCollectionsForm) form;
		

		RepositoryManager rm =
			(RepositoryManager) servlet.getServletContext().getAttribute("repositoryManager");

		
		// Handle editing/changing record status
		if (request.getParameter("recs") != null) {
			return handleRecordStatusEditing(mapping, (DDSManageCollectionsForm) form, request, response, rm);
		}
		
		return mapping.findForward("edit.record.status");
	}

	/**
	 *  Handle a request to search over metadata collections and forwared to the appropriate
	 *  jsp page to render the response.
	 *
	 * @param  mapping               The ActionMapping used to select this instance
	 * @param  request               The HTTP request we are processing
	 * @param  response              The HTTP response we are creating
	 * @param  mf             		The DDSManageCollectionsForm
	 * @param  rm                    The RepositoryManager
	 * @return                       The ActionForward instance describing where and how
	 *      control should be forwarded
	 * @exception  IOException       if an input/output error occurs
	 * @exception  ServletException  if a servlet exception occurs
	 */
	public ActionForward handleRecordStatusEditing(
	                                                 ActionMapping mapping,
	                                                 DDSManageCollectionsForm mf,
	                                                 HttpServletRequest request,
	                                                 HttpServletResponse response,
	                                                 RepositoryManager rm)
		 throws IOException, ServletException {
			 
		ActionErrors errors = new ActionErrors();
		
		SimpleLuceneIndex index =
			(SimpleLuceneIndex) servlet.getServletContext().getAttribute("index");

		if (index == null)
			throw new ServletException("The attribute \"index\" could not be found in the Servlet Context.");
		
		errors.add("message", new ActionError("generic.message",
			"editing record number " + request.getParameter("recs")));
		saveErrors(request, errors);


		return mapping.findForward("edit.record.status");		
	}



	// -------------- Debug ------------------


	public static void setDebug(boolean isDebugOutput) {
		debug = isDebugOutput;
	}



	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	private void prtln(String s) {
		if (debug)
			System.out.println("DDSManageCollectionsAction: " + s);
	}
}

