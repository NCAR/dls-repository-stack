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

import javax.servlet.*;

/**
 *  This abstract class implements RepositoryWriterPlugin to provide access to the {@link
 *  javax.servlet.ServletContext} during the indexing process. This class should be used
 *  when using a RepositoryWriterPlugin in a Servlet environment.
 *
 * @author     John Weatherley, Jonathan Ostwald
 * @see        RepositoryWriterServiceWriter <p>
 *
 *
 */
public abstract class ServletContextRepositoryWriterPlugin implements RepositoryWriterPlugin {
	private static ServletContext servletContext = null;


	/**
	 *  Sets the ServletContext to make it available to this plugin during the indexing
	 *  process.
	 *
	 * @param  context  The ServletContext
	 */
	public static void setServletContext(ServletContext context) {
		servletContext = context;
	}


	/**
	 *  Gets the ServletContext for use during the indexing process.
	 *
	 * @return    The ServletContext
	 */
	public static ServletContext getServletContext() {
		return servletContext;
	}
}

