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

package edu.ucar.dls.xml.schema;

import edu.ucar.dls.suggest.*;
import edu.ucar.dls.serviceclients.remotesearch.RemoteSearcher;

import org.dom4j.Document;

import edu.ucar.dls.webapps.tools.*;
import edu.ucar.dls.vocab.*;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.net.URL;

// Enterprise imports
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * Servlet to visualize XML Schemas
 *
 * @author Jonathan
 */

public final class SchemaViewerServlet extends HttpServlet {

	private boolean debug = true;
	
	public void init(ServletConfig conf) 
    		 throws ServletException {
		System.out.println(getDateStamp() + " SchemaViewerServlet starting");
		String initErrorMsg = "";
		try {
			super.init(conf);
		} catch (Throwable exc) {
			initErrorMsg = "SchemaViewerServlet Initialization Error:\n  " + exc;
			prtlnErr (initErrorMsg);
		}
		
		ServletContext servletContext = getServletContext();
		
		MetadataVocab vocab = (MetadataVocab)servletContext.getAttribute( "MetadataVocab" );
		if (vocab == null) {
			throw new ServletException ("MetadataVocab not found in servlet context");
		}

		// set up remote searcher
		/* RemoteSearcher rs = (RemoteSearcher) servletContext.getAttribute ("RemoteSearcher");
		if (rs == null) {
			throw new ServletException ("RemoteSearcher not found in servlet context");
		} */
		
		System.out.println(getDateStamp() + " SchemaViewerServlet initialized.");
    }
    
	/**  Performs shutdown operations.  */
	public void destroy() {
		System.out.println(getDateStamp() + " SchemaViewerServlet stopped");
	}

	//================================================================
	// stuff swiped from OAIProviderServlet

	/**
	 *  Gets the absolute path to a given file or directory. Assumes the path
	 *  passed in is eithr already absolute (has leading slash) or is relative to
	 *  the context root (no leading slash). If the string passed in does not begin
	 *  with a slash ("/"), then the string is converted. For example, an init
	 *  parameter to a config file might be passed in as
	 *  "WEB-INF/conf/serverParms.conf" and this method will return the
	 *  corresponding absolute path "/export/devel/tomcat/webapps/myApp/WEB-INF/conf/serverParms.conf."
	 *  <p>
	 *
	 *  If the string that is passed in already begings with "/", nothing is done.
	 *  <p>
	 *
	 *  Note: the super.init() method must be called prior to using this method,
	 *  else a ServletException is thrown.
	 *
	 * @param  fname                 An absolute or relative file name or path
	 *      (relative the the context root).
	 * @return                       The absolute path to the given file or path.
	 * @exception  ServletException  An exception related to this servlet
	 */
	private String getAbsolutePath( String fname )
		 throws ServletException {
		return GeneralServletTools.getAbsolutePath( fname, getServletContext() );
	}
	
	/**
	 *  Return a string for the current time and date, sutiable for display in log files and
	 *  output to standout:
	 *
	 * @return    The dateStamp value
	 */
	public static String getDateStamp() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final void prtlnErr(String s) {
		System.err.println(getDateStamp() + " SchemaViewerServlet: " + s);
	}

	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final void prtln(String s) {
		if (debug)
			System.out.println(getDateStamp() + " SchemaViewerServlet: " + s);
	}

		
	/**
	 *  Gets the absolute path to a given file or directory. Assumes the path
	 *  passed in is eithr already absolute (has leading slash) or is relative to
	 *  the context root (no leading slash). If the string passed in does not begin
	 *  with a slash ("/"), then the string is converted. For example, an init
	 *  parameter to a config file might be passed in as
	 *  "WEB-INF/conf/serverParms.conf" and this method will return the
	 *  corresponding absolute path "/export/devel/tomcat/webapps/myApp/WEB-INF/conf/serverParms.conf."
	 *  <p>
	 *
	 *  If the string that is passed in already begings with "/", nothing is done.
	 *  <p>
	 *
	 *  Note: the super.init() method must be called prior to using this method,
	 *  else a ServletException is thrown.
	 *
	 * @param  fname    An absolute or relative file name or path (relative the the
	 *      context root).
	 * @param  docRoot  The context document root as obtained by calling
	 *      getServletContext().getRealPath("/");
	 * @return          The absolute path to the given file or path.
	 */

	 private String getAbsolutePath( String fname, String docRoot ) {
		return GeneralServletTools.getAbsolutePath( fname, docRoot );
	}

	

}
