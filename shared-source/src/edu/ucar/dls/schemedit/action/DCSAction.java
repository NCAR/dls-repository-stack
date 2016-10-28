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

import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;

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

import edu.ucar.dls.repository.RepositoryManager;
import edu.ucar.dls.schemedit.Constants;
import edu.ucar.dls.schemedit.repository.RepositoryService;
import edu.ucar.dls.schemedit.SessionRegistry;
import edu.ucar.dls.schemedit.FrameworkRegistry;
import edu.ucar.dls.schemedit.MetaDataFramework;
import edu.ucar.dls.schemedit.config.CollectionRegistry;
import edu.ucar.dls.schemedit.SessionBean;
import edu.ucar.dls.schemedit.dcs.DcsDataManager;
import edu.ucar.dls.schemedit.security.user.User;
import edu.ucar.dls.webapps.tools.GeneralServletTools;
import edu.ucar.dls.schemedit.security.access.AccessManager;
import edu.ucar.dls.schemedit.security.access.ActionPath;
import edu.ucar.dls.schemedit.security.access.Roles.Role;
import edu.ucar.dls.schemedit.security.user.UserManager;

/**
 *  Base Action class that sets up access to global structures as instance
 *  variables. NOTE: using instance variables this way in an Action is
 *  dangerous. We have to at least make sure we don't use any session-specific
 *  variables this way, since the Action class is shared by all threads, and the
 *  session-specific vars may be re-assigned by another thread.
 *
 * @author    Jonathan Ostwald
 */
public class DCSAction extends Action {

	private static boolean debug = true;
	protected FrameworkRegistry frameworkRegistry = null;
	protected CollectionRegistry collectionRegistry = null;
	protected SessionRegistry sessionRegistry = null;
	protected RepositoryManager repositoryManager = null;
	protected RepositoryService repositoryService = null;
	protected AccessManager accessManager = null;
	protected UserManager userManager = null;
	protected DcsDataManager dcsDataManager = null;
	protected Role requiredRole = Role.ADMIN;
	protected boolean ndrServiceEnabled = false;
	protected boolean readOnlyMode = false;


	/**
	 *  Processes the specified HTTP request and creates the corresponding HTTP
	 *  response by forwarding to a JSP that will create it. A {@link
	 *  edu.ucar.dls.repository.RepositoryManager} must be available to this class
	 *  via a ServletContext attribute under the key "repositoryManager." Returns
	 *  an {@link org.apache.struts.action.ActionForward} instance which must be
	 *  configured in struts-config.xml to forward to the JSP page that will handle
	 *  the request.
	 *
	 * @param  mapping               The ActionMapping used to select this instance
	 * @param  request               The HTTP request we are processing
	 * @return                       The ActionForward instance describing where
	 *      and how control should be forwarded
	 * @exception  IOException       if an input/output error occurs
	 * @exception  ServletException  if a servlet exception occurs
	 */
	public ActionErrors initializeFromContext(
	                                          ActionMapping mapping,
	                                          HttpServletRequest request)
		 throws IOException, ServletException {
		/*
		 *  Design note:
		 *  Only one instance of this class gets created for the app and shared by
		 *  all threads. To be thread-safe, use only local variables, not instance
		 *  variables (the JVM will handle these properly using the stack). Pass
		 *  all variables via method signatures rather than instance vars.
		 */
		ActionErrors errors = new ActionErrors();
		ServletContext servletContext = servlet.getServletContext();
		// we should test these to make sure they aren't null and do something reasonable if they are
		try {
			repositoryManager =
				(RepositoryManager) getRequiredContextAttributeValue("repositoryManager", errors);

			repositoryService =
				(RepositoryService) getRequiredContextAttributeValue("repositoryService", errors);

			frameworkRegistry =
				(FrameworkRegistry) getRequiredContextAttributeValue("frameworkRegistry", errors);

			collectionRegistry =
				(CollectionRegistry) getRequiredContextAttributeValue("collectionRegistry", errors);

			dcsDataManager =
				(DcsDataManager) getRequiredContextAttributeValue("dcsDataManager", errors);

			sessionRegistry =
				(SessionRegistry) getRequiredContextAttributeValue("sessionRegistry", errors);
			if (sessionRegistry != null) {
				// test to make sure the sessionBean is available
				if (sessionRegistry.getSessionBean(request) == null) {
					errors.add("actionSetupError", new ActionError("dcs.action.object.error", "sessionBean"));
				}
			}

			accessManager =
				(AccessManager) getRequiredContextAttributeValue("accessManager", errors);

			userManager =
				(UserManager) getRequiredContextAttributeValue("userManager", errors);

			ndrServiceEnabled =
				(Boolean) getRequiredContextAttributeValue("ndrServiceEnabled", errors);
				
			readOnlyMode =
				(Boolean) getRequiredContextAttributeValue("readOnlyMode", errors);

			requiredRole = ActionPath.getRole(mapping);

		} catch (Throwable e) {
			prtln("System Error: " + e);
			if (e instanceof NullPointerException)
				e.printStackTrace();
			errors.add("actionSetupError", new ActionError("generic.error", "An unknown system error has occurred"));
		}
		return errors;
	}


	/**
	 *  Gets the requiredContextAttributeValue attribute of the DCSAction object
	 *
	 * @param  attrName       name of attribute to get
	 * @param  errors         place holder to return error message
	 * @return                The requiredContextAttributeValue value
	 * @exception  Exception  if attribute is not found in ServletContext
	 */
	protected Object getRequiredContextAttributeValue(String attrName, ActionErrors errors) throws Exception {
		Object attrValue = servlet.getServletContext().getAttribute(attrName);
		if (attrValue == null)
			errors.add("actionSetupError", new ActionError("dcs.action.attribute.error", attrName));
		return attrValue;
	}


	/**
	 *  Gets the MetaDataFramework from the frameworkRegistry for the specified
	 *  xmlFormat.
	 *
	 * @param  xmlFormat  format of framework to get
	 * @return            framework, or null if specified framework is not found.
	 */
	protected MetaDataFramework getMetaDataFramework(String xmlFormat) {
		return this.frameworkRegistry.getFramework(xmlFormat);
	}


	/**
	 *  Gets the sessionBean using the supplied request ojbect
	 *
	 * @param  request  the Request
	 * @return          The sessionBean value
	 */
	protected SessionBean getSessionBean(HttpServletRequest request) {
		return sessionRegistry.getSessionBean(request);
	}


	/**
	 *  Gets the sessionUser from the supplied SessionBean object.
	 *
	 * @param  sessionBean  the SessionBean
	 * @return              The sessionUser value
	 */
	protected User getSessionUser(SessionBean sessionBean) {
		return sessionBean.getUser();
	}


	/**
	 *  Gets the sessionUser using the supplied Request.
	 *
	 * @param  request  the Request
	 * @return          The sessionUser value
	 */
	protected User getSessionUser(HttpServletRequest request) {
		SessionBean sessionBean = this.getSessionBean(request);
		return (sessionBean != null ? sessionBean.getUser() : null);
	}


	/**
	 *  Returns the userName of the current sessionUser (as determined from the
	 *  supplied Request object) or the UNKNOWN_EDITOR value if a user is not
	 *  found.
	 *
	 * @param  request  the Request
	 * @return          The sessionUserName value
	 */
	protected String getSessionUserName(HttpServletRequest request) {
		User sessionUser = this.getSessionUser(request);
		return (sessionUser != null ?
			sessionUser.getUsername() :
			Constants.UNKNOWN_EDITOR);
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private void prtln(String s) {
		if (debug) {
			System.out.println("DCSAction: " + s);
		}
	}
}

