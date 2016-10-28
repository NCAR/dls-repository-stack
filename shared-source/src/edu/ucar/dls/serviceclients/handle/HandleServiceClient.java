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
package edu.ucar.dls.serviceclients.handle;

import edu.ucar.dls.xml.Dom4jUtils;

import org.dom4j.*;

import java.net.*;
import java.util.*;
import java.text.*;

/**
 *  A client for a HandleService, which returns a metadataHandle for a unique
 *  partnerId and setSpec, creating a new handle if necessary.
 *
 * @author    Jonathan Ostwald
 */
public class HandleServiceClient {
	private static boolean debug = true;

	private String userAgent = "Generic Handle Service Client";

	private String baseUrl = null;


	/**
	 *  Constructor for the HandleServiceClient object
	 *
	 * @param  baseUrl  service baseUrl
	 */
	public HandleServiceClient(String baseUrl) {
		this(baseUrl, null);
	}


	/**
	 *  Constructor for the HandleServiceClient object
	 *
	 * @param  baseUrl     service baseUrl
	 * @param  _userAgent  The http User-Agent header sent with the requests, or
	 *      null to use default 'Generic Handle Service Client'
	 */
	public HandleServiceClient(String baseUrl, String _userAgent) {
		this.baseUrl = baseUrl;
		if (_userAgent != null)
			this.userAgent = _userAgent;
	}


	/**
	 *  Gets the metadataHandle for provided partnerId and setSpec, both of which
	 *  must not be null.
	 *
	 * @param  partnerId                                the partnerId
	 * @param  setSpec                                  the setSpec (aka
	 *      collectionKey)
	 * @return                                          The metadataHandle value
	 * @exception  HandleServiceErrorResponseException  if an exception is returned
	 *      from the handle service
	 * @exception  Exception                            if a request cannot be
	 *      created and encoded
	 */
	public String getMetadataHandle(String partnerId, String setSpec) throws HandleServiceErrorResponseException, Exception {

		if (partnerId == null)
			partnerId = "";

		if (setSpec == null)
			setSpec = "";

		String serviceRequest = baseUrl +
			"?verb=GetMetadataHandle" +
			"&setSpec=" + URLEncoder.encode(setSpec, "UTF-8") +
			"&partnerId=" + URLEncoder.encode(partnerId, "UTF-8");

		prtln("GetMetadataHandle serviceRequest: " + serviceRequest);
		Document response = Dom4jUtils.getXmlDocument(new URL(serviceRequest), 60000, userAgent);

		String[] errorResponse = checkForErrorResponse(response);
		if (errorResponse != null)
			throw new HandleServiceErrorResponseException(errorResponse[0], errorResponse[1], "GetMetadataHandle");

		return response.valueOf("/*[local-name()='HandleResolutionService']/*[local-name()='GetMetadataHandle']/*[local-name()='metadataHandle']");
	}


	/**
	 *  Checks for the existance of an error response from a DDS service request
	 *  (DDSWS). If no error was recieved, this method returns null, otherwise
	 *  returns the error code and message.
	 *
	 * @param  response  the handle service response
	 * @return           The error code and message [code,message], or null if none
	 */
	protected String[] checkForErrorResponse(Document response) {
		if (response != null && response.selectSingleNode("/*[local-name()='HandleResolutionService']/*[local-name()='error']") != null) {
			String code = response.valueOf("/*[local-name()='HandleResolutionService']/*[local-name()='error']/@code");
			String msg = response.valueOf("/*[local-name()='HandleResolutionService/*[local-name()='error']");
			if (code == null)
				code = "";
			if (msg == null)
				msg = "";
			return new String[]{code, msg};
		}
		return null;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug)
			System.out.println(s);
	}
}

