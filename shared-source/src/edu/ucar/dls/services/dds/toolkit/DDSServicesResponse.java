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
 *  Copyright 2002-2011 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */
package edu.ucar.dls.services.dds.toolkit;

import org.dom4j.*;
import java.util.*;
import java.text.*;

import edu.ucar.dls.xml.Dom4jUtils;

/**
 *  Encapsulates a response from a DDSWS or DDSUpdateWS service request.
 *
 * @author    John Weatherley
 */
public final class DDSServicesResponse {
	private static boolean debug = true;

	private Document responseDocument = null;
	private String requestMade = null;


	/**
	 *  Constructor for the DDSServicesResponse object
	 *
	 * @param  requestMade       The request made to the service
	 * @param  responseDocument  The response
	 */
	protected DDSServicesResponse(String requestMade, Document responseDocument) {
		this.responseDocument = responseDocument;
		this.requestMade = requestMade;
	}


	/**
	 *  Gets the service response.
	 *
	 * @return    The responseDocument value
	 */
	public Document getResponseDocument() {
		return responseDocument;
	}


	/**
	 *  Gets a List of Java Beans returned from a DDSWS Search or GetRecord request. Creates each Bean Object one
	 *  time when it is first accessed and subsequent accesses return a saved instance of the Bean. For a Search
	 *  response the List may contain zero or more Beans, for GetRecord the List may contain zero or one. Beans
	 *  must have been encoded via the java.beans.XMLEncoder class.
	 *
	 * @return    A List of matching Java beans, or empty List if none were found.
	 */
	public List getJavaBeans() {
		return new JavaBeanList(responseDocument);
	}


	/**
	 *  Gets the full request that was made to the service.
	 *
	 * @return    The requestString value
	 */
	public String getRequestString() {
		return requestMade;
	}


	/**
	 *  Gets the response as an XML String in it's original form as returned by the service.
	 *
	 * @return    The response as XML or null if not available.
	 */
	public String getResponseAsXML() {
		if (responseDocument == null)
			return null;
		return responseDocument.asXML();
	}
	
	/**
	 *  Gets the response as an XML String in pretty-print form.
	 *
	 * @return    The response as XML String or null if not available.
	 */
	public String getResponseAsXMLPretty() {
		if (responseDocument == null)
			return null;
		return Dom4jUtils.prettyPrint(responseDocument);
	}	


	/**
	 *  As a String
	 *
	 * @return    As a String
	 */
	public String toString() {
		if (responseDocument == null && requestMade == null)
			return "null";
		return "Request: " + requestMade + "\n\nResponse:\n\n" + responseDocument.asXML();
	}

	// ---------------------- Debug info --------------------

	/**
	 *  Return a string for the current time and date, sutiable for display in log files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	protected final static String getDateStamp() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final static void prtlnErr(String s) {
		System.err.println(getDateStamp() + " DDSServicesResponse Error: " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final static void prtln(String s) {
		if (debug) {
			System.out.println(getDateStamp() + " DDSServicesResponse: " + s);
		}
	}


	/**
	 *  Sets the debug attribute of the object
	 *
	 * @param  db  The new debug value
	 */
	public static void setDebug(boolean db) {
		debug = db;
	}
}

