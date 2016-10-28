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



import edu.ucar.dls.index.*;

import edu.ucar.dls.dds.*;

import edu.ucar.dls.dds.action.form.*;

import edu.ucar.dls.vocab.*;

import edu.ucar.dls.vocab.MetadataVocab;

import edu.ucar.dls.repository.*;



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



/**

 *  Implementation of <strong>Action</strong> that handles administration of the DDS.

 *

 * @author     John Weatherley

 */

public final class DDSAdminAction extends Action {



	// --------------------------------------------------------- Public Methods



	/**

	 *  Process the specified HTTP request, and create the corresponding HTTP response (or forward to another web

	 *  component that will create it). Return an <code>ActionForward</code> instance describing where and how

	 *  control should be forwarded, or <code>null</code> if the response has already been completed.

	 *

	 * @param  mapping               The ActionMapping used to select this instance

	 * @param  request               The HTTP request we are processing

	 * @param  response              The HTTP response we are creating

	 * @param  form                  The ActionForm for the given page

	 * @return                       The ActionForward instance describing where and how control should be

	 *      forwarded

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

		// Extract attributes we will need

		//Locale locale = getLocale(request);

		//MessageResources messages = getResources();





		RepositoryManager rm =

			(RepositoryManager) servlet.getServletContext().getAttribute("repositoryManager");

		SimpleLuceneIndex index =

			(SimpleLuceneIndex) servlet.getServletContext().getAttribute("index");

		MetadataVocab vocab = (MetadataVocab) servlet.getServletContext().getAttribute("MetadataVocab");



		DDSAdminForm adminForm = (DDSAdminForm) form;



		adminForm.setIndex(index);

		adminForm.setMetadataVocab(vocab);



		String paramVal = null;

		try {



			// Handle admin actions:

			if (request.getParameter("command") != null) {

				paramVal = request.getParameter("command");



				if (paramVal.equals("Update index")) {

					rm.indexFiles(null,false);

					adminForm.setShowNumChanged(true);

					adminForm.setMessage(

						DDSServlet.getDateStamp() +

						". Discovery is synchronizing it's index with the metadata files...");

					return mapping.findForward("admin.index");

				}

				if (paramVal.equals("Start File Tester")) {

					adminForm.setMessage("Stoped moving files");

					//fileIndexingService.startTester(servlet.getServletContext().getRealPath("/"));

					return mapping.findForward("admin.index");

				}

				if (paramVal.equals("Stop File Tester")) {

					adminForm.setMessage("Stoped moving files");

					//fileIndexingService.stopTester();

					return mapping.findForward("admin.index");

				}

			}



			// No recognizable param existed:

			else if (request.getParameterNames().hasMoreElements()) {

				adminForm.setMessage("The request is not valid in this context.");

				return mapping.findForward("admin.index");

			}



			// If there were no parameters at all:

			return mapping.findForward("admin.index");

		} catch (Throwable t) {

			adminForm.setMessage("There was a server problem: " + t.getMessage());

			return mapping.findForward("admin.index");

		}



	}





	/**

	 *  DESCRIPTION

	 *

	 * @param  s  DESCRIPTION

	 */

	private void prtln(String s) {

		System.out.println(s);

	}

}



