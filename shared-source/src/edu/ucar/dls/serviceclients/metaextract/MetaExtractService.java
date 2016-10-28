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

package edu.ucar.dls.serviceclients.metaextract;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.TimedURLConnection;
import edu.ucar.dls.util.URLConnectionTimedOutException;
import org.dom4j.*;
import java.util.*;
import java.net.*;

/**
 * @author    Jonathan Ostwald
 */
public class MetaExtractService {
	
/*
metaExtractUrl = "
http://ada.syr.edu:8080/mast/service.do?
method=collect&
query=http://www.nationalgeographic.com/xpeditions/lessons/01/gk2/friends.html&
queryType=URL"

http://ada.syr.edu:8080/mast/service.do&method=collect
&query=http://interactive2.usgs.gov/learningweb/teachers/globalchange_earth.htm
&queryType=URL


*/
	private static boolean debug = true;
	protected final String baseUrl = "http://ada.syr.edu:8080/mast/service.do";
	protected final String METHOD = "collect";
	protected final String QUERY_TYPE = "URL";
	
	protected String username;
	protected String password;
	private static int CONNECTION_TIMEOUT = 60000;


	/**
	 *  Constructor for the MetaExtractService object
	 *
	 * @param  username  NOT YET DOCUMENTED
	 * @param  password  NOT YET DOCUMENTED
	 */
	public MetaExtractService(String username, String password) {
		this.username = username;
		this.password = password;
	}

	protected String getMethod() {
		return METHOD;
	}

	/**
	 *  Submit a request to the CAT service and return the response as a Document
	 *
	 * @param  contraints         query parameters as key/value pairs
	 * @return                The response value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public Document getResponse(String href) throws Exception {
		String queryString = "method=" + this.getMethod();
		queryString += "&query=" + URLEncoder.encode(href);
		queryString += "&queryType=" + QUERY_TYPE;
		
		String url = this.baseUrl + "?" + queryString;	
		
 		try {
			URL tester = new URL(url);
			prtln("\nURL:\n\t" + url.toString());
			prtln ("\nURL(decoded):\n\t" + java.net.URLDecoder.decode (url.toString()) + "\n");
		} catch (Throwable t) {
			throw new Exception("webservice error: " + t);
		}
 
		int millis = this.CONNECTION_TIMEOUT;
		String content = null;
		try {
			content = TimedURLConnection.importURL(url, millis);
		} catch (URLConnectionTimedOutException e) {
			throw new Exception("Connection to " + url + " timed out after " + (millis / 1000) + " seconds");
		}
		// prtln (content);
		Document doc = null;
		try {
			doc = Dom4jUtils.getXmlDocument(content);
		} catch (Exception e) {
			prtln ("\nRESPONSE PARSE ERROR: " + e.getMessage());
			prtln ("\n--------------------------------------\nRESPONSE");
			prtln (content);
			prtln ("---------- END RESPONSE --------------\n");
			throw new Exception ("could not parse MetaExtract response as XML");
		}
		Element error = (Element) doc.selectSingleNode("/MetaExtractWebService/Error");
		if (error != null) {

/* 			prtln ("url: " + url);*/
			prtln("\nERROR RESPONSE");
			pp (doc); 
			prtln ("");
			throw new Exception(getErrorMsg(error));
		}
		return doc;
	}

	/**
	 *  Gets the errorMsg attribute of the MetaExtractService object
	 *
	 * @param  errorElement  NOT YET DOCUMENTED
	 * @return               The errorMsg value
	 */
	protected String getErrorMsg(Element errorElement) {
		String errorMsg = "Unspecified MetaExtractService Error";
		try {
			String statusMsg = errorElement.element("StatusMessage").getTextTrim();
			if (statusMsg.length() > 0)
				errorMsg = statusMsg;
		} catch (Throwable t) {}

/* 		try {
			String returnCode = errorElement.element("ReturnCode").getTextTrim();
			if (returnCode.length() > 0)
				errorMsg += " (returnCode: " + returnCode + ")";
		} catch (Throwable t) {}

		try {
			String helpMessage = errorElement.element("HelpMessage").getTextTrim();
			if (helpMessage.length() > 0) {
				// errorMsg += " HelpMessage: " + helpMessage;
				errorMsg += " " + helpMessage;
			}
		} catch (Throwable t) {} */

		return errorMsg;
	}

	public static void main(String[] args) {

 		MetaExtractService client = new MetaExtractService("test", "p");
		String query = "http://www.nationalgeographic.com/xpeditions/lessons/01/gk2/friends.html";

		Document response = null;
		try {
			response = client.getResponse (query);
		} catch (Throwable t) {
			prtln("ERROR: " + t.getMessage());
			System.exit(1);
		}
		
		pp (response);
	}

	/**
	 *  Description of the Method
	 *
	 * @param  node  Description of the Parameter
	 */
	protected static void pp(Node node) {
		prtln(Dom4jUtils.prettyPrint(node));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		String prefix = "MetaExtractService";
		if (debug) {
			SchemEditUtils.prtln(s, prefix);
		}
	}

}

