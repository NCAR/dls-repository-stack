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
package edu.ucar.dls.serviceclients.asn;

import edu.ucar.dls.serviceclients.webclient.WebServiceClient;
import edu.ucar.dls.serviceclients.webclient.WebServiceClientException;

import edu.ucar.dls.standards.asn.AsnConstants;

import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XPathUtils;
import edu.ucar.dls.util.Files;
import org.dom4j.*;
import java.util.*;
import java.util.regex.*;
import java.io.File;
import java.net.*;

/**
 *  Given an asnId (e.g., http://asn.jesandco.org/resources/S10154EF) build a string of the form asnId,
 *  parent, ..., docId
 *
 * @author    Jonathan Ostwald
 */
public class AsnResolutionServiceClient extends WebServiceClient {

	private static boolean debug = false;

	

	/**
	 *  Constructor for the AsnResolutionServiceClient object
	 *
	 * @param  baseWebServiceUrl  NOT YET DOCUMENTED
	 */
	public AsnResolutionServiceClient(String baseWebServiceUrl) {
		super(baseWebServiceUrl);
		this.setTimeOutSecs(5);
	}


	/**
	 *  Gets the GetStadard response from webservice client for provided asnId
	 *
	 * @param  asnId          NOT YET DOCUMENTED
	 * @return                response form the GetStandard request
	 * @exception  Exception  There is a ASNWebservice error
	 */
	public Document getStandardResponse(String asnId) throws Exception {

		String serviceUrl = getBaseUrl() + "?verb=GetStandard&id=" + asnId;

		URL url = new URL(serviceUrl);
		Document response = getResponseDoc(url);
		Element error = (Element) response.selectSingleNode("/ASNWebService/error");
		if (error != null)
			throw new Exception("Error: " + error.getText());
		response = Dom4jUtils.localizeXml(response);
		return response;
	}


	/**
	 *  Gets the standardNumber from an asnId, e.g., from http://asn.jesandco.org/resources/S100DF75 we would
	 *  return "S100DF75"
	 *
	 * @param  asnId  NOT YET DOCUMENTED
	 * @return        The standardNumber value or null if one can't be found
	 */
	public static String getUniqueAsnIdPart(String asnId) {
		return AsnConstants.getAsnUid(asnId);
	}


	/**
	 *  Gets a hierarchical string of standards IDs. String is of the form grandparentID:parentID:childID, for
	 *  example StateStandardBodyID:scienceID:chemistryID:conceptID.
	 *
	 * @param  stdId          ASN standard ID
	 * @return                Gets a hierarchical string of standards IDs
	 * @exception  Exception  If error
	 */
	public String getAsnIdIndexFieldValue(String stdId) throws Exception {
		prtln("getAsnIdIndexFieldValue() with " + stdId);
		Document response = this.getStandardResponse(stdId);

		Element result = (Element) response.selectSingleNode("/ASNWebService/GetStandard/result");
		if (result == null)
			throw new Exception("result not found in service response");

		List ancestors = new ArrayList();
		Element ptr = (Element) result.selectSingleNode("Standard");
		String fieldValue = "";
		while (ptr != null) {
			String asnId = ptr.selectSingleNode("Identifier").getText();
			ancestors.add(getUniqueAsnIdPart(asnId));
			ptr = (Element) ptr.selectSingleNode("Parent/Standard");
		}
		Collections.reverse(ancestors);
		for (Iterator i = ancestors.iterator(); i.hasNext(); ) {
			fieldValue += (String) i.next();
			if (i.hasNext())
				fieldValue += ":";
		}
		prtln("fetched fieldValue: " + fieldValue);
		return fieldValue;
	}


	/**
	 *  Gets the asnFieldIndexFieldValue attribute of the AsnResolutionServiceClient object
	 *
	 * @param  stdId          NOT YET DOCUMENTED
	 * @return                The asnFieldIndexFieldValue value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public String getAsnTextIndexFieldValue(String stdId) throws Exception {
		prtln("getAsnTextIndexFieldValue() with " + stdId);
		Document response = this.getStandardResponse(stdId);

		Element result = (Element) response.selectSingleNode("/ASNWebService/GetStandard/result");
		if (result == null)
			throw new Exception("result not found in service response");

		List ancestors = new ArrayList();
		Element ptr = (Element) result.selectSingleNode("Standard");
		String fieldValue = "";
		while (ptr != null) {
			String text = ptr.selectSingleNode("Text").getText();
			fieldValue = text + " " + fieldValue;
			ptr = (Element) ptr.selectSingleNode("Parent/Standard");
		}
		prtln("fetched fieldValue: " + fieldValue);
		return fieldValue;
	}



	/**
	 *  The main program for the AsnResolutionServiceClient class
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(String[] args) {
		prtln("AsnResolutionServiceClient");
		AsnResolutionServiceClient client = null;
		String serviceUrl = "http://nsdldev.org/asn/service.do";
		String asnId = "http://asn.jesandco.org/resources/S10154EF";
		List results = null;
		Document response = null;
		try {
			client = new AsnResolutionServiceClient(serviceUrl);
			String indexFieldValue = client.getAsnTextIndexFieldValue(asnId);
			prtln("indexFieldValue: " + indexFieldValue);
		} catch (Throwable t) {
			prtln(t.getMessage());
			return;
		}

		// pp (response);
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  n  NOT YET DOCUMENTED
	 */
	private static void pp(Node n) {
		prtln(Dom4jUtils.prettyPrint(n));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println(s);
		}
	}
}

