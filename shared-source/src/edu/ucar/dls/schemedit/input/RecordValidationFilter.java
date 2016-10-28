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

package edu.ucar.dls.schemedit.input;

import edu.ucar.dls.webapps.servlets.filters.FilterCore;
import edu.ucar.dls.webapps.servlets.filters.CharArrayWrapper;
import edu.ucar.dls.webapps.tools.*;

import edu.ucar.dls.schemedit.repository.RepositoryService;
import edu.ucar.dls.schemedit.FrameworkRegistry;
import edu.ucar.dls.schemedit.MetaDataFramework;
import edu.ucar.dls.schemedit.dcs.DcsDataManager;
import edu.ucar.dls.schemedit.dcs.DcsDataRecord;
import edu.ucar.dls.index.reader.XMLDocReader;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.text.SimpleDateFormat;
import org.json.XML;
import org.json.JSONStringer;

import edu.ucar.dls.xml.*;
import edu.ucar.dls.webapps.servlets.filters.XMLPostProcessingFilter;

import org.dom4j.*;

/**
 *  Filter that validates XML and updates dcsDataRecord with the results of
 *  validation. Based on {@link edu.ucar.dls.webapps.servlets.filters.XMLValidationFilter}
 *
 * @author    Jonathan Ostwald
 */
public final class RecordValidationFilter extends XMLPostProcessingFilter {
	private static boolean debug = false;
	private ServletContext context = null;


	/**
	 *  Performs XML post-processing and gzipping of the response.
	 *
	 * @param  request               The request
	 * @param  response              The response
	 * @param  chain                 The chain of Filters
	 * @exception  ServletException  Iff error
	 * @exception  IOException       Iff IO error
	 */
	 protected String getValidationMessage (String xml, ServletRequest req) {
		prtln ("getValidationMessage(): recVal");
		String message = null;
		String prettyDoc = null;

		try {
			Document doc = DocumentHelper.parseText(xml);
			prettyDoc = Dom4jUtils.prettyPrint(doc);
		} catch (Throwable t) {}

		try {
			String recId = req.getParameter("fileid");

			// Get a cached validator if at all possible
			RepositoryService repositoryService =
				(RepositoryService) this.context.getAttribute("repositoryService");
			if (repositoryService == null)
				throw new Exception("RepositoryService not found");

			XMLDocReader docReader = repositoryService.getXMLDocReader(recId);
			if (docReader == null)
				throw new Exception("indexed record not found for \"" + recId + "\"");
			String xmlFormat = docReader.getNativeFormat();

			// make sure framework is loaded
			FrameworkRegistry frameworkRegistry =
				(FrameworkRegistry) this.context.getAttribute("frameworkRegistry");
			if (frameworkRegistry == null)
				throw new Exception("Framework Repository not found");
			if (frameworkRegistry.getFramework(xmlFormat) == null)
				throw new Exception("Framework not loaded for " + xmlFormat);

			DcsDataRecord dcsDataRecord = repositoryService.getDcsDataRecord(recId);
			String oldMessage = dcsDataRecord.getValidationReport();
			repositoryService.validateRecord(xml, dcsDataRecord, xmlFormat);
			message = dcsDataRecord.getValidationReport();
			prtln ("old message: " + oldMessage);
			prtln ("new message: " + message);

			if (!oldMessage.equals(message)) {
				dcsDataRecord.flushToDisk();
				repositoryService.updateRecord(recId);
				prtln("record indexed");
			}

			// finish up by normalizing validation message (code below expects message to be null
			// if record is valid
			if (dcsDataRecord.isValid()) {
				prtln("normalizing message for valid record");
				message = null;
			}
		} catch (Throwable t) {
			prtlnErr("Validation WARNING: " + t.getMessage());
			// Run non-cached XML validation over the content
			if (message == null)
				message = XMLValidator.validateString(xml, true);

		}
		return message;
	 }
	 
	 protected String xmlToHtml(String xml) {
		String prettyDoc = null;
		try {
			Document doc = DocumentHelper.parseText(xml);
			prettyDoc = Dom4jUtils.prettyPrint(doc);
		} catch (Throwable t) {}
		
		return OutputTools.xmlToHtml(prettyDoc != null ? prettyDoc : xml);
	}


	/**
	 *  Init is called once at application start-up.
	 *
	 * @param  config                The FilterConfig object that holds the
	 *      ServletContext and init information.
	 * @exception  ServletException  If an error occurs
	 */
	public void init(FilterConfig config) throws ServletException {
		if (context == null) {
			try {
				context = config.getServletContext();
/* 				if (((String) context.getInitParameter("debug")).toLowerCase().equals("true")) {
					debug = true;
					prtln("Outputing debug info");
				} */
				debug=false;
				System.out.println ("RecordValidationFilter initialized, debug is " + debug);
			} catch (Throwable e) {}
		}
	}


	/**  Destroy is called at application shut-down time. */
	public void destroy() { }



	//================================================================

	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
/* 	protected final void prtlnErr(String s) {
		System.err.println(getDateStamp() + " " + s);
	} */



	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 * @param  s  The String that will be output.
	 */
	protected void prtln(String s) {
		if (debug)
			System.out.println(getDateStamp() + " RecordValidationFilter: " + s);
	}


	/**
	 *  Sets the debug attribute of the RecordValidationFilter object
	 *
	 * @param  db  The new debug value
	 */
	protected void setDebug(boolean db) {
		debug = db;
	}

}

