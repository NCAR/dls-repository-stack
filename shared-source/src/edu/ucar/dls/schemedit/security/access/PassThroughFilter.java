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

package edu.ucar.dls.schemedit.security.access;

import java.util.*;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.struts.Globals;
import org.apache.struts.action.*;
import org.apache.struts.util.RequestUtils;


/**
 *  NOT YET DOCUMENTED
 *
 * @author     Jonathan Ostwald
 */
public class PassThroughFilter implements Filter {
	
	private static boolean debug = false;

	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  filterConfig          NOT YET DOCUMENTED
	 * @exception  ServletException  NOT YET DOCUMENTED
	 */
	public void init(FilterConfig filterConfig)
		 throws ServletException {
		prtln ("init()");
	}

	/**
	 *  This filter gets every webservices request, and simply passes it on without authorizing.
	 *
	 * @param  request               NOT YET DOCUMENTED
	 * @param  response              NOT YET DOCUMENTED
	 * @param  chain                 NOT YET DOCUMENTED
	 * @exception  IOException       NOT YET DOCUMENTED
	 * @exception  ServletException  NOT YET DOCUMENTED
	 */
	public void doFilter(ServletRequest request,
	                     ServletResponse response,
	                     FilterChain chain)
		 throws IOException, ServletException {
			 
		prtln ("\n---------------------\ndoFilter()");
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		
		ActionErrors errors = new ActionErrors();

		
		showRequestInfo(req);
		
		if (req.getServletPath().equals("/content")) {
			// don't pass through! send along to content servlet
			chain.doFilter(req, res);
		}
		else
			req.getRequestDispatcher(req.getServletPath()).forward(req, res);
	
	}

	private void showRequestInfo (HttpServletRequest request) {
		String s = "\n Request Info:";
		s += "\n" + "\t requestURI: " + request.getRequestURI();
		s += "\n" + "\t queryString: " + request.getQueryString();
		s += "\n" + "\t servletPath: " + request.getServletPath();
		prtln (s + "\n");
	}
	
	/**  NOT YET DOCUMENTED */
	public void destroy() {
	}
	
	private void showRequestInfoVerbose (HttpServletRequest request) {
		prtln ("\n Request Info:");
		prtln ("\t requestURI: " + request.getRequestURI());
		prtln ("\t queryString: " + request.getQueryString());
		prtln ("\t requestURL: " + request.getRequestURL().toString());
		prtln ("\t servletPath: " + request.getServletPath());
		prtln ("\t contextPath: " + request.getContextPath());
		prtln ("\t pathTranslated: " + request.getPathTranslated());
		prtln ("\t pathInfo: " + request.getPathInfo());
		prtln ("\nrequest attributes");
		for (Enumeration e=request.getAttributeNames();e.hasMoreElements();) {
			String name = (String)e.nextElement();
			Object attribute = (Object)request.getAttribute(name);
			prtln ("\t name: " + name + "  class: " + attribute.getClass().getName());
		}
		prtln ("------------------------------\n");
	}

	
	static void prtln(String s) {
		if (debug) {
			while (s.length() > 0 && s.charAt(0) == '\n') {
				System.out.println ("");
				s = s.substring(1);
			}
			System.out.println("PassThroughFilter: " + s);
		}
	}
	
}
