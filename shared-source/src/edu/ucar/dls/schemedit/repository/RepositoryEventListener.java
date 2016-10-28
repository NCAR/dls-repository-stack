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
package edu.ucar.dls.schemedit.repository;

import edu.ucar.dls.schemedit.SchemEditUtils;
import javax.servlet.ServletContext;
import java.util.*;

/**
 *  A Simple, servletContext-aware, listener for RepositoryEvents that prints
 *  event info to the log. <p>
 *
 *  RepositoryEventListeners are configured in context descriptor (web.xml or
 *  server.xml). To configure a RepositoryEventListener, specify an
 *  context-param that has a unique param-name that begins with the string
 *  'repositoryeventlistener'. The param-value must be of the form [fully
 *  qualified Java class] to specify a listener. The Java class must implement
 *  RepositoryEventListener.
 *
 * @author    jonathan
 */
public class RepositoryEventListener implements RepositoryEventListenerInterface {

	private static boolean debug = true;
	private ServletContext servletContext = null;


	/**  Constructor for the RepositoryEventListener object */
	public RepositoryEventListener() { }


	/**
	 *  Constructor for the RepositoryEventListener object
	 *
	 * @param  servletContext  the servletContext
	 */
	public RepositoryEventListener(ServletContext servletContext) {
		this.servletContext = servletContext;
	}


	/**
	 *  Sets the servletContext attribute of the RepositoryEventListener object
	 *
	 * @param  servletContext  The new servletContext value
	 */
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ServletContext getServletContext() {
		return this.servletContext;
	}

	/**
	 *  Invoked when a repositoryEvent has occured.
	 *
	 * @param  repositoryEvent  Description of the Parameter
	 */
	public void handleEvent(RepositoryEvent repositoryEvent) {
		System.out.println("RepositoryEventListener received a repositoryEvent: " + repositoryEvent.getName());
		System.out.println(repositoryEvent.toString());

	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "RepositoryEventListener");
		}
	}
}

