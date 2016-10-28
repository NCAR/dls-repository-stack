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

package edu.ucar.dls.suggest.resource;

import edu.ucar.dls.suggest.SuggestUtils;
import edu.ucar.dls.suggest.SuggestHelper;
import edu.ucar.dls.suggest.SuggestionRecord;

import edu.ucar.dls.suggest.resource.urlcheck.UrlValidator;
import edu.ucar.dls.suggest.resource.urlcheck.ValidatorResults;

import edu.ucar.dls.serviceclients.webclient.WebServiceClient;
import edu.ucar.dls.serviceclients.webclient.WebServiceClientException;

import edu.ucar.dls.xml.schema.SchemaHelper;
import edu.ucar.dls.xml.Dom4jUtils;

import java.io.File;
import java.util.*;

import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentException;

/**
 *  SuggestUrlManger - provides services for suggestor Client.
 *  
 *
 *@author    Jonathan Ostwald
 */
public class SuggestResourceHelper extends SuggestHelper {

	private static boolean debug = true;
	
	private UrlValidator urlValidator = null;

	public String getXmlFormat() {
		return "adn";
	}
	
	/**
	 *  SuggestResourceHelper Constructor. The repositoryDir param points to a
	 *  directory that contains a default record (used for making new records), and
	 *  a records directory (where suggested urls are stored). This stuff is all
	 *  hard coded for now, but properties can be used to supply defaults (see
	 *  {@link edu.ucar.dls.suggest.SuggestResourceHelper}).
	 *
	 *@param  repositoryDir  directory holding default record and records directory
	 */
	public SuggestResourceHelper(File recordTemplate, SchemaHelper schemaHelper) {
		super (recordTemplate, schemaHelper);
	}
	
	public UrlValidator getUrlValidator () {
		return this.urlValidator;
	}
	
	public void setUrlValidator (UrlValidator validator) {
		this.urlValidator = validator;
	}
	
	/**
	 *  Creates a new {@link ResourceRecord} instance by reading from the
	 *  recordTemplate. We don't call readRecord (file) because we don't want the
	 *  id to be set for the new record
	 *
	 *@return    Description of the Return Value
	 */
	public ResourceRecord newRecord() throws Exception {

		Document rawDoc = Dom4jUtils.getXmlDocument(recordTemplate);
		String rootElementName = rawDoc.getRootElement().getName();
		Document doc = Dom4jUtils.localizeXml(rawDoc, rootElementName);
		return new ResourceRecord(doc, schemaHelper);

	}

	public String putRecordToDCS(SuggestionRecord rec) throws Exception {
		throw new Exception ("putRecordToDCS requires a ValidatorResults instance");
	}
	
	/**
	 *  Insert a suggested record into the DCS specified by client configuration.
	 *
	 *@param  sm             Description of the Parameter
	 *@return                ID of record in destination DCS
	 *@exception  Exception  Description of the Exception
	 */
	public String putRecordToDCS(SuggestionRecord rec, ValidatorResults validatorResults)
		throws Exception {

		if (validatorResults.hasSimilarUrls()) {
			dcsStatusNote += validatorResults.similarUrlReportForDcsStatusNote();
		}
				
		prtln("putRecordToDCS(): dcsStatus=" + dcsStatus + ", dcsStatusNote=" + dcsStatusNote);

		String destCollection = this.getDestCollection();
		if (destCollection == null || destCollection.trim().length() == 0) {
			prtln("destCollection not specified, putRecordToDCS exiting ... ");
			return null;
		}
		
		if (rec == null) {
			throw new Exception("SuggestionRecord is null");
		}
		Document doc = rec.getDoc();
		if (doc == null) {
			throw new Exception("doc could not be obtained from SuggestResourceHelper");
		}

		// insert namespace info
		String nameSpaceInfo = SuggestUtils.getNameSpaceInfo(schemaHelper);
		String rootElementName = doc.getRootElement().getName();
		Document adnDoc = Dom4jUtils.delocalizeXml(doc, rootElementName, nameSpaceInfo);

		String newRecId = null;
		try {
			newRecId = getRepositoryServiceClient().doPutRecord(adnDoc.asXML(), 
														   "adn", 
														   getDestCollection(), 
														   dcsStatus, 
														   dcsStatusNote);
		} catch (WebServiceClientException e) {
			throw new Exception("WebService error: " + e.getMessage());
		}

		return newRecId;
	}

	/**
	 *  Sets the debug attribute of the SuggestResourceHelper object
	 *
	 *@param  db  The new debug value
	 */
	public static void setDebug(boolean db) {
		debug = db;
	}


	/**
	 *  Print the string with trailing newline to std output
	 *
	 *@param  s  string to print
	 */
	private static void prtln(String s) {
		if (debug) {
			edu.ucar.dls.schemedit.SchemEditUtils.prtln (s, "SuggestResourceHelper");
		}
	}

}

