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

package edu.ucar.dls.schemedit.action;

import edu.ucar.dls.repository.*;
import edu.ucar.dls.index.*;
import edu.ucar.dls.schemedit.action.form.*;
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.dcs.*;
import edu.ucar.dls.schemedit.config.*;
import edu.ucar.dls.index.reader.*;
import edu.ucar.dls.xml.*;
import org.apache.lucene.search.*;
import edu.ucar.dls.oai.*;
import edu.ucar.dls.webapps.tools.GeneralServletTools;

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
import org.dom4j.Document;
import org.dom4j.DocumentException;

/**
 *  A Struts Action for handling query requests that access a {@link
 *  edu.ucar.dls.index.SimpleLuceneIndex}. This class works in conjunction with the
 *  {@link edu.ucar.dls.schemedit.action.form.StaticRecordForm} Struts form bean class.
 *
 *
 *
 * @author    Jonathan Ostwald
 */
public final class StaticRecordAction extends DCSAction {

	private static boolean debug = false;

	// --------------------------------------------------------- Public Methods

	/**
	 *  Processes the specified HTTP request and creates the corresponding HTTP response by
	 *  forwarding to a JSP that will create it. A {@link
	 *  edu.ucar.dls.index.SimpleLuceneIndex} must be available to this class via a
	 *  ServletContext attribute under the key "index." Returns an {@link
	 *  org.apache.struts.action.ActionForward} instance that maps to the Struts forwarding
	 *  name "browse.query," which must be configured in struts-config.xml to forward to the
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
		StaticRecordForm srForm = (StaticRecordForm) form;
		 
		ActionErrors errors = initializeFromContext (mapping, request);
		if (!errors.isEmpty()) {
			saveErrors (request, errors);
			return (mapping.findForward("error.page"));
		}	
		
		SchemEditUtils.showRequestParameters(request);

		
		try {
			String id = request.getParameter ("id");
			
			if (id != null && id.length() > 0) {
				return viewRecordById (mapping, form, request, response);
			}
			
			else  {
				srForm.setResult (null);
					errors.add("message", new ActionError("generic.message",
						"No ID indicated. Please supply a record ID."));
					saveErrors(request, errors);
					return mapping.findForward("static.record");
				}

		} catch (NullPointerException e) {
			prtln("StaticRecordAction caught exception.");
			e.printStackTrace();
			return mapping.findForward("error.page");
		} catch (Throwable e) {
			prtln("StaticRecordAction caught exception: " + e);
			return mapping.findForward("error.page");
		}
	}
	
	private ActionForward viewRecordById ( ActionMapping mapping,
	                             ActionForm form,
	                             HttpServletRequest request,
	                             HttpServletResponse response ) throws ServletException {
		
		StaticRecordForm srForm = (StaticRecordForm) form;
		ActionErrors errors = new ActionErrors ();

		String id = request.getParameter ("id");
		
		SearchHelper searchHelper = getSessionBean(request).getSearchHelper();
		ResultDoc result = searchHelper.getResultDoc (id);
		
		if (result == null) {
			srForm.setResult (null);
			errors.add("message", new ActionError("generic.message",
				"No record was found in the index for ID \"" + id + "\""));
			saveErrors(request, errors);
			return mapping.findForward("static.record");
		}
		
		// initialize the srForm.docMap
		XMLDocReader resultDocReader = (XMLDocReader) result.getDocReader();
 		String xmlFormat = resultDocReader.getNativeFormat();
		// String collection = resultDocReader.getCollection();
 		MetaDataFramework framework = this.getMetaDataFramework(xmlFormat);

		srForm.setResult(result);
		srForm.setFramework(framework);
		
		XMLDocReader annotatedItem = null;
		prtln ("xmlFormat: " + xmlFormat);
		if (xmlFormat.equals ("dlese_anno")) {
			DleseAnnoDocReader annoReader = (DleseAnnoDocReader)resultDocReader;
			String annotatedItemId = annoReader.getItemId();
			ResultDoc annotatedItemResult = searchHelper.getResultDoc (annotatedItemId);
			if (annotatedItemResult != null) {
				annotatedItem = (XMLDocReader) annotatedItemResult.getDocReader();
				if (annotatedItem != null)
					prtln ("annotatedItem: " + annotatedItem.getId());
			}
			else {
				prtln ("annotedItemResult not found for " + annoReader.getItemId());
			}
		}
		srForm.setAnnotatedItem (annotatedItem);
			
		return mapping.findForward("static.record");
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
			System.out.println("StaticRecordAction: " + s);
	}
}

