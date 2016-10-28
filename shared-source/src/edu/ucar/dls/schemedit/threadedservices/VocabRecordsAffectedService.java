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
package edu.ucar.dls.schemedit.threadedservices;

import edu.ucar.dls.schemedit.repository.RepositoryUtils;
import edu.ucar.dls.repository.RepositoryManager;
import edu.ucar.dls.index.*;
import edu.ucar.dls.index.reader.*;
import edu.ucar.dls.xml.nldr.VocabRecord;
import edu.ucar.dls.services.dds.toolkit.*;

import edu.ucar.dls.schemedit.dcs.*;

import edu.ucar.dls.util.*;
import edu.ucar.dls.datamgr.*;

import java.util.*;
import java.text.*;
import java.io.*;

import javax.servlet.ServletContext;

import org.dom4j.Document;

/**
 *  Threaded Service for validating records.
 *
 * @author    ostwald
 *
 *
 */
public class VocabRecordsAffectedService extends ThreadedService {

	private static boolean debug = true;

	/**
	 *  Constructor for the VocabRecordsAffectedService object
	 *
	 * @param  serviceDataDir  Description of the Parameter
	 * @param  servletContext            NOT YET DOCUMENTED
	 */
	public VocabRecordsAffectedService(ServletContext servletContext, String serviceDataDir) {
		super(servletContext, serviceDataDir);
	}


	/**
	 *  Gets the validationReport attribute of the VocabRecordsAffectedService object
	 *
	 * @return    The validationReport value
	 */
	public Report getReport() {
		// return (Report) super.getServiceReport();
		return null;
	}

	/**
	 *  Update the "recordsAffected" field in the vocab records whose ids are provided.
	 *
	 * @exception  VocabRecordsAffectedServiceException  NOT YET DOCUMENTED
	 */
	public void updateVocabRecordsAffected(List docIds, String collection) throws ValidatingServiceException {
		prtln("updateVocabRecordsAffected to fetch " + docIds.size() + " docs");
		
		if (isProcessing) {
			prtln("ALERT: isProcessing!");
			return;
		}
		isProcessing = true;

		// Here is where we would obtain lock on records to validate!

		try {
			new VocabUpdateThread(docIds, collection).start();
		} catch (Throwable t) {
			prtln("WARNING: validate Records: " + t.getMessage());
		}
	}


	private int getAffectedVocabRecords (String collection, String term) throws Exception {
		// prtln ("getAffectedVocabRecords");
		if (collection == null || collection.trim().length() == 0)
			return 0;
		
		NLDRProperties nldrProps = 
			(NLDRProperties)servletContext.getAttribute("nldrProperties");
		String baseUrl = nldrProps.BASE_DDS_SERVICE_URL;
		// prtln ("baseUrl: " + baseUrl);
		DDSServicesToolkit toolkit = new DDSServicesToolkit(baseUrl, null, null);
		
		String query = "";
		if ("pubname".equals(collection)) {
			query = "/key//record/general/pubName:\"" + term+ "\"";
		}
		else if ("event".equals(collection)) {
			query = "/key//record/general/eventName:\"" + term + "\"";
		}
		else if (("inst").equals(collection)) {
			query = "/key//record/contributors/organization/affiliation/instName:\"" + term + "\"";
			query += " OR ";
			query += "/key//record/contributors/person/affiliation/instName:\"" + term + "\"";
		}
		else {
			throw new Exception ("unrecognized collection: " + collection);
		}
		// query = "(" + query + ") AND xml_format:osm";
			
		// prtln ("query: " + query);
		
		int sortOrder = -1;
		int startOffset = 0;	
		String formatOfRecords = "osm";
		String sortByField = null;
		int numReturns = 1; // we only need a count of hits, which is in the header
		String showRelation = null;
		Map additionalRequestParams = null;
		boolean soAllRecords = false; // necessary to get collection records, and info from "disabled" collections
		boolean localizeXml = false; // necessary to get valid item records

		DDSServicesResponse response = null;
		try {
			response =
				toolkit.search(query, formatOfRecords, startOffset, numReturns, sortByField,
				sortOrder, showRelation, additionalRequestParams, soAllRecords, localizeXml);
				
			// prtln (Dom4jUtils.prettyPrint (response.getResponseDocument()));
				
			String[] error = checkForErrorResponseDDSWS(response.getResponseDocument());
			if (error != null)
				throw new Exception(error[0] + ": " + error[1]);
		} catch (Exception e) {
			String msg = "ERROR from service response:" + e.getMessage();
			throw new Exception(msg);
			// prtln (msg);
			// continue;
		}
		
		int numAffected = 0;

		try {
			Document doc = response.getResponseDocument();
			String numReturned = doc.valueOf("/*[local-name()='DDSWebService']/*[local-name()='Search']/*[local-name()='resultInfo']/*[local-name()='totalNumResults']");
			// prtln(recordNodes.size() + " records read");
			numAffected = Integer.parseInt(numReturned);
		} catch (Exception e) {
			throw new Exception("could not process response document: " + e.getMessage());
		}

		prtln (numAffected + " affected for \"" + term + "\"");
		
		return numAffected;
	}

		protected String[] checkForErrorResponseDDSWS(Document ddswsResponse) {
		if (ddswsResponse != null && ddswsResponse.selectSingleNode("/*[local-name()='DDSWebService']/*[local-name()='error']") != null) {
			String code = ddswsResponse.valueOf("/*[local-name()='DDSWebService']/*[local-name()='error']/@code");
			String msg = ddswsResponse.valueOf("/*[local-name()='DDSWebService/*[local-name()='error']");
			if (code == null)
				code = "";
			if (msg == null)
				msg = "";
			return new String[]{code, msg};
		}
		return null;
	}
	

	/**
	 *  Description of the Method
	 *
	 * @param  asnDocIds  NOT YET DOCUMENTED
	 */
	private void doUpdateVocabRecords(List vocabRecordIds, String collection) {
		prtln ("doUpdateVocabRecords: " + collection);
/*  		int numNotValid = 0; // count of invalid records
		int numValid = 0; // count of valid records
		 // count of records that could not be validated */
		
		int numUpdated = 0; // count of records found to be either valid or invalid
		int numToUpdate = vocabRecordIds.size();
		int numUpdateErrors = 0;
		
		int report_granularity = 2;
		
		TaskProgress progress = this.getTaskProgress();
		RepositoryManager repositoryManager =
			(RepositoryManager) servletContext.getAttribute("repositoryManager");

		String errorMsg = "";
		String msg = "";
		clearStatusMessages();
		clearServiceReport();
		progress.init(numToUpdate, "Fetching Update Vocab Records");
		addStatusMessage("Starting to update " + numToUpdate + " records");

		long start = new Date().getTime();

		for (int i = 0; i < vocabRecordIds.size() && !stopProcessing; i++) {
			String vocabRecordId = (String) vocabRecordIds.get(i);

			prtln (i + "/" + vocabRecordIds.size() + " - " + vocabRecordId);
			
			progress.setDone(i + 1); // in the end, we want it to be 100%

			if (i % report_granularity == 0 && i != 0) {
				// msg = "Validated " + i + " of " + records.length + " items";
				msg = "Updated " + (i - 1) + " of " + numToUpdate + " items";
				addStatusMessage(msg);
			}
			
			VocabRecord vocabRecord = null;
			// ResultDoc resultDoc = recordBatch.getResultDoc(vocabRecordId);
			
			try {
				XMLDocReader docReader = RepositoryUtils.getXMLDocReader (vocabRecordId, repositoryManager);
				String recordXml = docReader.getXml();
				vocabRecord = new VocabRecord (recordXml);
				// prtln ("vocabRecord: " + vocabRecord.getId() + " " + vocabRecord.getTerm());
				int recordsAffected = this.getAffectedVocabRecords (collection, vocabRecord.getTerm());
				vocabRecord.setRecordsAffected(recordsAffected);
			} catch (Exception e) {
				prtlnErr ("could not setRecordsAffected for " + vocabRecordId + ": " + e.getMessage());
				continue;
			}
			
			// Write vocab record to repository
			try {
				repositoryManager.putRecord(vocabRecord.getXml(), "vocabs", collection, vocabRecordId, true);
			} catch (Exception e) {
				prtlnErr ("could not putRecord for " + vocabRecordId + ": " + e.getMessage());
				continue;
			}


			numUpdated++;
			// report.addEntry();

			Thread.yield();
			// prtln("validateRecords() stopProcessing: " + stopProcessing + ", " + i +"/"+records.length);

		}

		if (stopProcessing) {
			addStatusMessage("Asn Fetch stopped by user");
			return;
		}
		String summary = "\n" + numUpdated + " total records fetched; ";
		if (numUpdateErrors > 0)
			summary += "  NOTE: " + numUpdateErrors + " record could not be fetched";
		addStatusMessage("Completed Fetching Asn Standards Documents: " + summary);

		// close out report
/* 		report.recordsProcessed = numToUpdate;
		report.processingTime = new Date().getTime() - start;
		report.setProp("numToUpdate", Integer.toString(numToUpdate));
		report.setProp("numUpdated", Integer.toString(numUpdated));
		report.setProp("numUpdateErrors", Integer.toString(numUpdateErrors));
		report.setProp("numPreviouslyLoaded", Integer.toString(numPreviouslyLoaded));

		setServiceReport(report);
		archiveServiceReport(report); */

	}


	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	protected static void prtln(String s) {
		if (debug) {
			System.out.println("VocabRecordsAffectedService: " + s);
		}
	}


	/**
	 *  Description of the Class
	 *
	 * @author    ostwald
	 */
	private class VocabUpdateThread extends Thread {
		List idList;
		String collection;


		/**
		 *  Constructor for the FetchThread object
		 *
		 * @param  idList      NOT YET DOCUMENTED
		 */
		public VocabUpdateThread(List idList, String collection) {
			this.idList = idList;
			this.collection = collection;
			setDaemon(true);
		}


		/**  Main processing method for the FetchThread object  */
		public void run() {
			try {
				doUpdateVocabRecords(idList, collection);
			} catch (Throwable e) {
				e.printStackTrace();
				// addStatusMessage(e.toString());
				String errorMsg = "Error updated records for \"" + collection + "\": " + e;
				addStatusMessage(errorMsg);
				prtln("ERROR fetching records: " + e);
			} finally {
				isProcessing = false;
			}
		}
	}

}

