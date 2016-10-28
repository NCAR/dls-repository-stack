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

import edu.ucar.dls.standards.asn.AsnDocument;

import edu.ucar.dls.schemedit.standards.asn.AsnDocKey;
import edu.ucar.dls.standards.asn.AsnConstants;
import edu.ucar.dls.util.strings.FindAndReplace;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.util.TimedURLConnection;
import edu.ucar.dls.util.URLConnectionTimedOutException;
import org.dom4j.*;
import edu.ucar.dls.xml.Dom4jUtils;
import java.util.*;
import java.net.*;

/**
 *  Provides static methods for working with the ASN standards via webservices.
 *
 * @author    Jonathan Ostwald
 */
public class AsnClientToolkit {

	private static boolean debug = true;
	private static int CONNECTION_TIMEOUT = 5000;

	/**
	 *  Return an XML Document fetched from the ASN webservice for the provided docKey
	 *
	 * @param  docKey         Local identifier for an ASN Document (e.g., "NGSS.Science.2013.D2454348")
	 * @return                The XML Document corresponding to provided docKey
	 * @exception  Exception  if the document could not be fetched
	 */
	public static Document fetchAsnSourceForKey(String docKey) throws Exception {
		prtln("\nfetchAsnSourceForKey (" + docKey + ")");
		AsnDocKey asnDocKey = AsnDocKey.makeAsnDocKey(docKey);
		String asnDocId = asnDocKey.getAsnId();
		return fetchAsnId(asnDocId);
	}


	/**
	 * Return an XML Document fetched from the ASN webservice for the provided ASN ID.
	 * Uses the "_full.xml" suffix on asn id to fetch the full XML document
	 *
	 * @param  asnId          ASN Idenfier - e.g., http://asn.jesandco.org/resources/S2454420
	 * @return                The XML Document for provided asnId
	 * @exception  Exception  if the document could not be fetched
	 */
	public static Document fetchAsnId(String asnId) throws Exception {
		// prtln("\nfetchAsnId (" + asnId + ")");
		
		// treat WMO standards differently for now ...
		asnId = AsnConstants.normalizeForWMO (asnId);
		
		String urlStr = asnId + "_full.xml";

		int millis = CONNECTION_TIMEOUT;
		String content = null;
		try {
			content = TimedURLConnection.importURL(urlStr, "UTF-8", millis);
		} catch (URLConnectionTimedOutException e) {
			throw new Exception("Connection to " + urlStr + " timed out after " + (millis / 1000) + " seconds");
		}
		return Dom4jUtils.getXmlDocument(content);
	}


	/**
	 *  Returns an {@link AsnDocument} instance for the provided asnId
	 *
	 * @param  asnId          ASN Idenfier - e.g., http://asn.jesandco.org/resources/S2454420
	 * @return                the AsnDocument
	 * @exception  Exception  if the AsnDocument cannot be obtained
	 */
	public static AsnDocument fetchAsnDocument(String asnId) throws Exception {
		prtln("\nfetchAsnDocument (" + asnId + ")");
		String urlStr = asnId + "_full.xml";

		Document doc = fetchAsnId(asnId);
		return new AsnDocument(doc, null);
	}


	/**
	 *  The main program for the AsnClientToolkit class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		// TEST GetDocForStdId
		// String asnId = "http://purl.org/ASN/resources/S1021C5B";
		// String docId = getDocIdForStdId(asnId);
		// prtln("docId: " + docId);
		
		String asnId = "http://asn.desire2learn.com/resources/D2605823";
		if (args.length > 0)
			asnId = args[0];
		prtln ("ASN ID: " + asnId);
		Document doc = fetchAsnId (asnId);
	}

	/**
	 *  Gets the id of the standards document that contains the provided stdId.
	 *
	 * @param  asnStdId       asn standard id - e.g., http://asn.jesandco.org/resources/S2454420
	 * @return                doc id for the document containing the std
	 * @exception  Exception  if the docId cannot be obtained
	 */
	public static String getDocIdForStdId(String asnStdId) throws Exception {
		// prtln ("getDocIdForStdId: " + asnStdId);
		URL purl = new URL(asnStdId + ".xml");
		AsnResolutionResponse arr = new AsnResolutionResponse(purl);
		return arr.getDocId();
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println("AsnClientToolkit: " + s);
			// System.out.println(s);
		}
	}
}

