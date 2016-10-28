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
package edu.ucar.dls.serviceclients.asn.acsr;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.Files;

import java.io.*;
import java.util.*;
import java.net.*;
import org.dom4j.*;

/**
 *  Abstract class to interact with the SOAP-BASED ACSR service implemented by
 *  ASN (JesAndCo).<p>
 *
 *  Based on SOAPClient4XG by Bob DuCharme (http://www.ibm.com/developerworks/xml/library/x-soapcl/).
 *
 * @author    Jonathan Ostwald
 */
public abstract class ACSRClient {
	private static boolean debug = false;

	final static String SOAPUrl = "http://www.jesandco.net/asn/asnwebservice/acsrservice.asmx";
	// soapTemplatePath used only when template doc can't be found in jar (i.e., during debugging)
	// private String soapTemplatePath = "C:/Documents and Settings/ostwald/devel/projects/dcs-project/src/ACSR-SOAP-ENVELOPE-TEMPLATE.xml";
	private String soapTemplatePath = "/Users/ostwald/devel/projects/dcs-project/src/ACSR-SOAP-ENVELOPE-TEMPLATE.xml";
	final static String SERVICE_NAME = "ACSRService";


	/**  Constructor for the ACSRClient object */
	public ACSRClient() { }


	/**
	 *  Gets the argMap attribute of the ACSRClient object
	 *
	 * @return    The argMap value
	 */
	public abstract Map getArgMap();


	/**
	 *  Gets the command attribute of the ACSRClient object
	 *
	 * @return    The command value
	 */
	public abstract String getCommand();


	/**
	 *  Gets the soapAction attribute of the ACSRClient object
	 *
	 * @return    The soapAction value
	 */
	public String getSoapAction() {
		return SERVICE_NAME + "/" + this.getCommand();
	}


	/**
	 *  Gets response from the ACSRService as an XML Document
	 *
	 * @return                The response value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public Document getResponse() throws Exception {

		/* ASN GetJuris Invocation:
		soap 'http://www.jesandco.net/asn/asnwebservice/acsrservice.asmx' c:/tmp/get-juris-request.xml ACSRService/GetJuris
			- SOAPUrl - http://www.jesandco.net/asn/asnwebservice/acsrservice.asmx
			- xmlFile2Send - c:/tmp/get-juris-request.xml
			- SOAPAction - ACSRService/GetJuris  (NOTE: required for ASN Service)
	*/
		// Create the connection where we're going to send the file.
		URL url = new URL(SOAPUrl);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) connection;
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);

		// get requestDoc (to be sent as postData)
		Document requestDoc = getRequestDoc();
		String postData = requestDoc.asXML();

		// Set the appropriate HTTP parameters.

		// not strictly necessary to set Content-Length ... but why not?
		httpConn.setRequestProperty("Content-Length",
			String.valueOf(postData.getBytes("utf-8")));
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		httpConn.setRequestProperty("SOAPAction", this.getSoapAction());
		httpConn.setRequestMethod("POST");

		// write postData to connection
		OutputStreamWriter wr = new OutputStreamWriter(httpConn.getOutputStream());
		wr.write(postData);
		wr.flush();

		// check for a 200 HTTP response code
		int respcode = ((HttpURLConnection) httpConn).getResponseCode();
		if (respcode < 200 || respcode > 299)
			throw new IOException("Invalid HTTP response code: " + respcode);

		InputStream istm = httpConn.getInputStream();

		InputStreamReader inr = new InputStreamReader(istm, "utf-8");
		BufferedReader in = new BufferedReader(inr);
		int c;
		StringBuffer content = new StringBuffer();
		while ((c = in.read()) != -1)
			content.append((char) c);

		istm.close();
		in.close();
		inr.close();

		String response = content.toString();
		// prtln(response);
		return Dom4jUtils.getXmlDocument(response);
	}


	/**
	 *  Get SOAP request Document
	 *
	 * @return                The requestDoc value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	Document getRequestDoc() throws Exception {
		Document template = null;
		try {
			template = Dom4jUtils.getXmlDocument(Files.readFileFromJarClasspath("ACSR-SOAP-ENVELOPE-TEMPLATE.xml").toString());
		} catch (Throwable t) {
			prtln("reading template from disk");
			template = Dom4jUtils.getXmlDocument(new File(soapTemplatePath));
		}

		// template = Dom4jUtils.getXmlDocument (new File (soapTemplatePath));

		Document requestDoc = template;
		Element bodyEl = (Element) requestDoc.selectSingleNode("//*[local-name()='Body']");

		Element cmdEl = bodyEl.addElement("ns0:" + this.getCommand(), "ACSRService");

		Map argMap = this.getArgMap();
		for (Iterator i = argMap.keySet().iterator(); i.hasNext(); ) {
			String name = (String) i.next();
			String value = (String) argMap.get(name);

			Element myEl = cmdEl.addElement("ns0:" + name, "ACSRService");
			myEl.setText(value);
		}

		// prtln (Dom4jUtils.prettyPrint(requestDoc));
		return requestDoc;
	}


	/**
	 *  Sets the debug attribute of the ACSRClient class
	 *
	 * @param  db  The new debug value
	 */
	public static void setDebug(boolean db) {
		debug = db;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	public static void prtln(String s) {
		if (debug) {
			// SchemEditUtils.prtln(s, "AsnDocument");
			SchemEditUtils.prtln(s, "");
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  n              NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void pp(Node n) throws Exception {
		prtln(Dom4jUtils.prettyPrint(n));
	}

}


