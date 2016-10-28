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

package edu.ucar.dls.util.uri;

import edu.ucar.dls.webapps.tools.*;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 *  Utility servlet for mapping "static" URIs (.htm) to dynamic requests (.do?)
 *  using regular expressions
 *
 * @author     Ryan Deardorff
 * @created    May 16, 2006
 */
public class UriSpoofServlet extends HttpServlet {

	private final static String XML_PARSER = "org.apache.xerces.parsers.SAXParser";
	private static UriMappings uriMappings = null;
	private String xmlConfig = null;


	/**
	 *  Initialize the servlet
	 *
	 * @param  config
	 * @exception  ServletException
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext servletContext = getServletContext();
		uriMappings = new UriMappings(servletContext, XML_PARSER);
		xmlConfig = (String) servletContext.getInitParameter("UriSpoof.config");
		if (xmlConfig.indexOf("/") == 0) {
			xmlConfig = xmlConfig.substring(1, xmlConfig.length());
		}
		xmlConfig = servletContext.getRealPath("/") + xmlConfig;
		uriMappings.loadMappings(xmlConfig);
	}


	/**
	 *  Look at the requested URI and translate it to dynamic (but hidden to
	 *  client) request
	 *
	 * @param  request
	 * @param  response
	 * @exception  ServletException
	 * @exception  IOException
	 */
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (uriMappings != null) {
			String forwardPage = uriMappings.getForwardPage(request);
			gotoPage(forwardPage, request, response);
		} else {
			ServletException e = new ServletException("UriSpoofServlet.urlMappings is null");
			throw e;
		}
	}


	/**
	 *  Hand control off to doGet (to easily support both at once)
	 *
	 * @param  request
	 * @param  response
	 * @exception  ServletException
	 * @exception  IOException
	 */
	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
			 throws ServletException, IOException {
		doGet(request, response);
	}


	/**
	 *  Forward to the given address (akin to Struts.action.forward)
	 *
	 * @param  address
	 * @param  request
	 * @param  response
	 * @exception  ServletException
	 * @exception  IOException
	 */
	private void gotoPage(String address,
			HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(address);
		if (dispatcher == null) {
			System.out.println("UriSpoofServlet dispatcher is null");
		} else {
			dispatcher.forward(request, response);
		}
	}
}

