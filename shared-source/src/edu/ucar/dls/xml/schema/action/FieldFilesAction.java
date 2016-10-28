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
package edu.ucar.dls.xml.schema.action;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.vocab.*;

import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.schema.action.form.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.serviceclients.remotesearch.*;

import edu.ucar.dls.vocab.MetadataVocab;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

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
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;

import java.net.URLEncoder;
import java.net.URLDecoder;

/**
 *  Just a minimal action that will set up a form bean and then forward to a jsp
 *  page this action depends upon an action-mapping in the struts-config file!
 *
 * @author    ostwald
 */
public final class FieldFilesAction extends Action {

	private static boolean debug = true;


	// --------------------------------------------------------- Public Methods

	/**
	 *  Processes the specified HTTP request and creates the corresponding HTTP
	 *  response by forwarding to a JSP that will create it. Returns an {@link
	 *  org.apache.struts.action.ActionForward} instance that maps to the Struts
	 *  forwarding name "xxx.xxx," which must be configured in struts-config.xml to
	 *  forward to the JSP page that will handle the request.
	 *
	 * @param  mapping               Description of the Parameter
	 * @param  form                  Description of the Parameter
	 * @param  request               Description of the Parameter
	 * @param  response              Description of the Parameter
	 * @return                       Description of the Return Value
	 * @exception  IOException       Description of the Exception
	 * @exception  ServletException  Description of the Exception
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
		prtln("\nexecuting");

		FieldFilesForm fff;
		try {
			fff = (FieldFilesForm) form;
		} catch (Throwable e) {
			prtln("FieldFilesAction caught exception. " + e);
			return null;
		}

		String errorMsg;

		// list of available frameworks
		// SHOULD 
		fff.setFrameworks(getRegistry().getAllFormats());
		fff.setFieldsFrameworks (this.getFieldsFrameworks());

		// RemoteSearcher rs = (RemoteSearcher) servlet.getServletContext().getAttribute("RemoteSearcher");

		ActionErrors errors = new ActionErrors();
		ActionMessages messages = new ActionMessages();

		// Query Args
		String xmlFormat = request.getParameter("xmlFormat");
		String command = request.getParameter("command");
		prtParam("command", command);
		prtParam("xmlFormat", xmlFormat);

		SchemEditUtils.showRequestParameters(request);

		// DETERMINE WHAT FRAMEWORK WE'RE TALKING - set it in FormBean
		MetaDataFramework framework = null;
		if (xmlFormat != null && xmlFormat.length() > 0) {
			framework = getMetaDataFramework(xmlFormat);
		}
		else {
			prtln ("using previously loaded framework");
			framework = fff.getFramework();
		}
		
		if (framework == null) {
			prtln("there is no framework in the form bean");
			errorMsg = "there is no framework in the form bean";
			errors.add(errors.GLOBAL_ERROR, new ActionError("generic.error", errorMsg));
			saveErrors(request, errors);
			return mapping.findForward("fields.files");
		}
		else {
			fff.setFramework(framework);
			fff.setSchemaHelper(framework.getSchemaHelper());
			fff.setDirectoryUri(framework.getFieldInfoMap().getDirectoryUri());
		}

		if (command == null) {
			return mapping.findForward("fields.files");
		}

		if (command.equals("list")) {
			prtln ("handling list command");
			
			String sortBy = fff.getSortListingBy();

			List sortedFields = new ArrayList();
			try {
				if (framework == null)
					throw new Exception("Framework not initialized");
				sortedFields = framework.getFieldInfoMap().getAllFieldInfo();

				Collections.sort(sortedFields, new FieldInfoComparator(sortBy, 0));

			} catch (Throwable t) {
				prtln("ERROR: " + t.getMessage());
				t.printStackTrace();
			}
			fff.setFieldsFiles(sortedFields);
		}
		
		else if (command.equals("check")) {
			prtln ("handling list command");

			try {
				FieldFilesCheck checker =
					new FieldFilesCheck(framework.getFieldInfoMap().getDirectoryUri(),
										framework.getSchemaURI(),
										framework.getRootElementName());
				checker.doCheck();
				fff.setFieldFilesChecker(checker);
			} catch (Exception e) {
				errorMsg = "check error: " + e;
				errors.add(errors.GLOBAL_ERROR, new ActionError("generic.error", errorMsg));
			}
		}

		
		// reloadFieldInfo is UNTESTED!
		else if (command.equals("reload")) {
			prtln("reload Field Info()");
			prtln("framework name: " + framework.getName());
			FieldInfoMap fieldInfoMap = framework.getFieldInfoMap();
			if (fieldInfoMap == null || fieldInfoMap.getKeySet().size() == 0) {
				errors.add("pageErrors",
					new ActionError("generic.error", "No Field Info found for this framework"));
			}
			else {
				try {
					fieldInfoMap.reload();
					messages.add("message",
						new ActionError("generic.message", "Field Info reloaded"));
				} catch (Exception e) {
					errors.add("pageErrors", new ActionError("generic.error", e.getMessage()));
				}
			}
		}

		// TESTING How errors and messages are handled
/* 		errors.add("message", new ActionError("generic.error", "I am message (generic.error)"));
		errors.add("pageErrors", new ActionError("generic.error", "I am pageError (generic.error)"));
		errors.add(errors.GLOBAL_ERROR, new ActionError("generic.error", 
			"I am a errors.GLOBAL_ERROR (generic.error)")); */
		
		saveErrors(request, errors);	
		return mapping.findForward("fields.files");
	}

	private List getFieldsFrameworks() {
		List fieldsFrameworks = new ArrayList();
		List allFormats = null;
		try {
			allFormats = getRegistry().getAllFormats();
		} catch (Exception e) {
			prtln ("ERROR getting all formats: " + e);
			return fieldsFrameworks;
		}
		for (Iterator i=allFormats.iterator();i.hasNext();) {
			String xmlFormat = (String)i.next();
			try {
				MetaDataFramework framework = this.getMetaDataFramework(xmlFormat);
				if (!framework.getFieldInfoMap().getFields().isEmpty())
					fieldsFrameworks.add (xmlFormat);
			} catch (Exception e) {
				prtln ("WARNING: could not get framework for " + xmlFormat + ": " + e);
			}
		}
		return fieldsFrameworks;
	}

	/**
	 *  Gets the registry attribute of the FieldFilesAction object
	 *
	 * @return                       The registry value
	 * @exception  ServletException  NOT YET DOCUMENTED
	 */
	private FrameworkRegistry getRegistry() throws ServletException {
		FrameworkRegistry reg = (FrameworkRegistry) servlet.getServletContext()
			.getAttribute("frameworkRegistry");
		if (reg == null) {
			throw new ServletException("frameworkRegistry not found in servletContext");
		}
		else {
			return reg;
		}
	}


	/**
	 *  Returns the metaDataFramework specified by xmlFormat
	 *
	 * @param  xmlFormat             the xmlFormat of framework to get
	 * @return                       The metaDataFramework value
	 * @exception  ServletException  NOT YET DOCUMENTED
	 */
	private MetaDataFramework getMetaDataFramework(String xmlFormat) throws ServletException {
		return getRegistry().getFramework(xmlFormat);
	}


	/**
	 *  debugging - print a name and value pair
	 *
	 * @param  name   NOT YET DOCUMENTED
	 * @param  value  NOT YET DOCUMENTED
	 */
	private void prtParam(String name, String value) {
		if (value == null)
			prtln(name + " is null");
		else
			prtln(name + " is " + value);
	}


	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	private void prtln(String s) {
		if (debug) {
			// System.out.println("FieldFilesAction: " + s);
			SchemEditUtils.prtln(s, "FieldFilesAction");
		}
	}

}

