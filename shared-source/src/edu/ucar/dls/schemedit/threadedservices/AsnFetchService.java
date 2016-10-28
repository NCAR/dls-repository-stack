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

import edu.ucar.dls.serviceclients.asn.AsnClientToolkit;
import edu.ucar.dls.schemedit.standards.StandardsRegistry;
import edu.ucar.dls.schemedit.standards.asn.AsnDocInfo;
import edu.ucar.dls.standards.asn.AsnDocument;

import edu.ucar.dls.schemedit.*;
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
public class AsnFetchService extends ThreadedService {

	private boolean ignoreCachedValidation = true;
	private StandardsRegistry standardsRegistry = null;


	/**
	 *  Constructor for the AsnFetchService object
	 *
	 * @param  validatingServiceDataDir  Description of the Parameter
	 * @param  servletContext            NOT YET DOCUMENTED
	 */
	public AsnFetchService(ServletContext servletContext, String validatingServiceDataDir) {
		super(servletContext, validatingServiceDataDir);
		this.standardsRegistry = StandardsRegistry.getInstance();
	}


	/**
	 *  Gets the validationReport attribute of the AsnFetchService object
	 *
	 * @return    The validationReport value
	 */
	public Report getReport() {
		// return (Report) super.getServiceReport();
		return null;
	}

	/**
	 *  Validate a set of records in a separate thread.
	 *
	 * @exception  AsnFetchServiceException  NOT YET DOCUMENTED
	 */
	public void fetchStandardsDocs(List docIds) throws ValidatingServiceException {
		prtln("fetchStandardsDocs to fetch " + docIds.size() + " docs");
		
		if (isProcessing) {
			prtln("ALERT: isProcessing!");
			return;
		}
		isProcessing = true;
		this.ignoreCachedValidation = ignoreCachedValidation;

		// Here is where we would obtain lock on records to validate!

		try {
			new AsnFetchThread(docIds).start();
		} catch (Throwable t) {
			prtln("WARNING: validate Records: " + t.getMessage());
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  asnDocIds  NOT YET DOCUMENTED
	 */
	private void doFetchAsnDocuments(List asnDocIds) {

		// validation stats (numFetched = numNotValid + numValid)
/*  		int numNotValid = 0; // count of invalid records
		int numValid = 0; // count of valid records
		 // count of records that could not be validated */
		
		int numFetched = 0; // count of records found to be either valid or invalid
		int numToFetch = asnDocIds.size();
		int numFetchErrors = 0;
		int numPreviouslyLoaded = 0;
		
		int report_granularity = 2;
		
		List docInfosFetched = new ArrayList();

		TaskProgress progress = this.getTaskProgress();

		String errorMsg = "";
		String msg = "";
		clearStatusMessages();
		clearServiceReport();
		progress.init(numToFetch, "Fetching Standards Docs from ASN web service");
		addStatusMessage("Starting to Fetch " + numToFetch + " records");

		long start = new Date().getTime();

		for (int i = 0; i < asnDocIds.size() && !stopProcessing; i++) {
			String asnDocId = (String) asnDocIds.get(i);

			prtln (i + "/" + asnDocIds.size() + " - " + asnDocId);
			
			progress.setDone(i + 1); // in the end, we want it to be 100%

			if (i % report_granularity == 0 && i != 0) {
				// msg = "Validated " + i + " of " + records.length + " items";
				msg = "Fetched " + (i - 1) + " of " + numToFetch + " items";
				addStatusMessage(msg);
			}

			AsnDocInfo docInfo = null;
			if (standardsRegistry.getDocIsRegistered(asnDocId)) {
				// this doc is already loaded
				docInfo = standardsRegistry.getDocInfo(standardsRegistry.getKey(asnDocId));
				numPreviouslyLoaded++;
				prtln ("  - previously loaded");
			}
			else {
			
				try {
					prtln ("  ... fetching");
					AsnDocument asnDoc = AsnClientToolkit.fetchAsnDocument(asnDocId);
					if (asnDoc == null)
						throw new Exception ("could not fetch ASN doc from ASN Service for " + asnDocId);
					
					docInfo = this.standardsRegistry.register (asnDoc);
					prtln ("  ... fetched and registered");
	
				} catch (Exception e) {
					prtln ("fetch error: " + e.getMessage());
					numFetchErrors++;
				}
			}
			
			if (docInfo != null)
				docInfosFetched.add (docInfo);
			numFetched++;
			// report.addEntry();

			Thread.yield();
			// prtln("validateRecords() stopProcessing: " + stopProcessing + ", " + i +"/"+records.length);

		}

		if (stopProcessing) {
			addStatusMessage("Asn Fetch stopped by user");
			return;
		}
		String summary = "\n" + numFetched + " total records fetched; ";
		if (numFetchErrors > 0)
			summary += "  NOTE: " + numFetchErrors + " record could not be fetched";
		addStatusMessage("Completed Fetching Asn Standards Documents: " + summary);

		// close out report
/* 		report.recordsProcessed = numToFetch;
		report.processingTime = new Date().getTime() - start;
		report.setProp("numToFetch", Integer.toString(numToFetch));
		report.setProp("numFetched", Integer.toString(numFetched));
		report.setProp("numFetchErrors", Integer.toString(numFetchErrors));
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
			System.out.println("AsnFetchService: " + s);
		}
	}


	/**
	 *  Description of the Class
	 *
	 * @author    ostwald
	 */
	private class AsnFetchThread extends Thread {
		List idList;


		/**
		 *  Constructor for the FetchThread object
		 *
		 * @param  idList      NOT YET DOCUMENTED
		 */
		public AsnFetchThread(List idList) {
			this.idList = idList;
			setDaemon(true);
		}


		/**  Main processing method for the FetchThread object  */
		public void run() {
			try {
				doFetchAsnDocuments(idList);
			} catch (Throwable e) {
				e.printStackTrace();
				// addStatusMessage(e.toString());
				String errorMsg = "Error fetching records: " + e;
				addStatusMessage(errorMsg);
				prtln("ERROR fetching records: " + e);
			} finally {
				isProcessing = false;
			}
		}
	}

}

