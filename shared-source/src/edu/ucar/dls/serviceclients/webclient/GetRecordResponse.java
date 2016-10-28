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

package edu.ucar.dls.serviceclients.webclient;

import edu.ucar.dls.xml.*;

import java.util.*;
import java.io.*;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *  GetRecordResponse class wraps the response from the <a
 *  href="http://swiki.dpc.ucar.edu/Project-Discovery/108#GetRecord">GetRecord
 *  Web Service (version 1.0)</a>
 *
 *@author    ostwald
 */
public class GetRecordResponse {

	private static boolean debug = true;

	private Document document = null;
	private Document itemRecord = null;


	/**
	 *  Constructor for the GetRecordResponse object
	 *
	 *@param  responseStr  Description of the Parameter
	 */
	public GetRecordResponse(String responseStr) {
		String localizedResponseStr = Dom4jUtils.localizeXml(responseStr, "itemRecord");
		try {
			document = Dom4jUtils.getXmlDocument(localizedResponseStr);
		} catch (Exception e) {
			prtln("failed to parse xml: " + e);
			return;
		}
	}


	/**
	 *  Gets the document attribute of the GetRecordResponse object. This is a
	 *  {@link org.dom4j.Document} representation of the GetRecord Service
	 *  response.
	 *
	 *@return    The document value
	 */
	public Document getDocument() {
		return document;
	}


	/**
	 *  Gets the itemRecord attribute of the GetRecordResponse class. The
	 *  itemRecord is the ADN item-level record contained in the GetRecord Service
	 *  Response.
	 *
	 *@return                                The itemRecord value
	 *@exception  WebServiceClientException  Description of the Exception
	 */
	public Document getItemRecord()
		throws WebServiceClientException {
		if (itemRecord == null) {
			// now extract the itemRecord element
			String rootXPath = "/DDSWebService/GetRecord/record/metadata/itemRecord";
			try {
				Node rootNode = document.selectSingleNode(rootXPath);
				if (rootNode == null) {
					String msg = "getItemRecord() failed to find itemRecord";
					prtln(msg);
					throw new WebServiceClientException(msg);
					// prtln(Dom4jUtils.prettyPrint(document));
				}

				// clone the root and create a new document with it
				Element rootClone = (Element) rootNode.clone();
				itemRecord = DocumentFactory.getInstance().createDocument(rootClone);
			} catch (Throwable e) {
				throw new WebServiceClientException(e.getMessage());
			}
		}
		return itemRecord;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println(s);
		}
	}
}

