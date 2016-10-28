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

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.dcs.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.index.*;
import edu.ucar.dls.index.reader.*;

import edu.ucar.dls.util.*;
import edu.ucar.dls.datamgr.*;
import edu.ucar.dls.repository.*;

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
public class ValidatingService extends ThreadedService {

	private boolean ignoreCachedValidation = true;


	/**
	 *  Constructor for the ValidatingService object
	 *
	 * @param  validatingServiceDataDir  Description of the Parameter
	 * @param  servletContext            NOT YET DOCUMENTED
	 */
	public ValidatingService(ServletContext servletContext, String validatingServiceDataDir) {
		super(servletContext, validatingServiceDataDir);
	}


	/**
	 *  Gets the validationReport attribute of the ValidatingService object
	 *
	 * @return    The validationReport value
	 */
	public ValidationReport getValidationReport() {
		return (ValidationReport) super.getServiceReport();
	}

	public DcsSetInfo getValidatingSetInfo() {
		if (isProcessing && dcsSetInfo != null)
			return dcsSetInfo;
		else
			return null;
	}

	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  dcsSetInfo                      NOT YET DOCUMENTED
	 * @param  statuses                        NOT YET DOCUMENTED
	 * @param  sessionBean                     NOT YET DOCUMENTED
	 * @exception  ValidatingServiceException  NOT YET DOCUMENTED
	 */
	public void validateRecords(DcsSetInfo dcsSetInfo, String[] statuses,
	                            SessionBean sessionBean) throws ValidatingServiceException {
		validateRecords(dcsSetInfo, statuses, sessionBean, false);
	}

	public void validateRecords(DcsSetInfo dcsSetInfo, 
								String[] statuses,
	                            SessionBean sessionBean,
								boolean ignoreCachedValidation) throws ValidatingServiceException {
		validateRecords(dcsSetInfo, statuses, sessionBean, ignoreCachedValidation, null);
	}

	/**
	 *  Validate a set of records in a separate thread.
	 *
	 * @param  dcsSetInfo                      NOT YET DOCUMENTED
	 * @param  statuses                        NOT YET DOCUMENTED
	 * @param  sessionBean                     NOT YET DOCUMENTED
	 * @param  ignoreCachedValidation          NOT YET DOCUMENTED
	 * @exception  ValidatingServiceException  NOT YET DOCUMENTED
	 */
	public void validateRecords(DcsSetInfo dcsSetInfo, 
								String[] statuses,
	                            SessionBean sessionBean,
	                            boolean ignoreCachedValidation,
								ThreadedServiceObserver observer) throws ValidatingServiceException {
		prtln("validateRecords() ignoreCachedValidation: " + ignoreCachedValidation);
		
		if (isProcessing) {
			prtln("ALERT: isProcessing!");
			return;
		}
		isProcessing = true;
		this.sessionBean = sessionBean;
		this.dcsSetInfo = dcsSetInfo;
		this.statuses = statuses;
		this.ignoreCachedValidation = ignoreCachedValidation;

		List idList = getIdList(dcsSetInfo.getSetSpec(), statuses);

		// Here is where we would obtain lock on records to validate!

		try {
			new ValidateThread(idList, dcsSetInfo, observer).start();
		} catch (Throwable t) {
			prtln("WARNING: validate Records: " + t.getMessage());
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  idsToValidate  NOT YET DOCUMENTED
	 */
	private void doValidateRecords(List idsToValidate, ThreadedServiceObserver observer) {

		// validation stats (numValidated = numNotValid + numValid)
		int numNotValid = 0; // count of invalid records
		int numValid = 0; // count of valid records
		int numValidated = 0; // count of records found to be either valid or invalid

		// (numValidated + numNotValidated = numToValidate)
		int numNotValidated = 0; // count of records that could not be validated
		int numToValidate = idsToValidate.size();
		int granularity = 50;

		TaskProgress progress = this.getTaskProgress();

		String errorMsg = "";
		String msg = "";
		clearStatusMessages();
		clearServiceReport();
		progress.init(numToValidate, "Validating " + this.dcsSetInfo.getName());
		addStatusMessage("Starting to Validate " + numToValidate + " records");

		// Validation Report
		ValidationReport report = new ValidationReport(dcsSetInfo, statuses);
		long start = new Date().getTime();

		for (int i = 0; i < idsToValidate.size() && !stopProcessing; i++) {
			String id = (String) idsToValidate.get(i);
			prtln ("validating " + id);
			progress.setDone(i + 1); // in the end, we want i to be 100%

			if (i % granularity == 0 && i != 0) {
				// msg = "Validated " + i + " of " + records.length + " items";
				msg = "Validated " + (i - 1) + " of " + numToValidate + " items";
				msg += " - " + numValid + " valid, " + numNotValid + " invalid";
				addStatusMessage(msg);
			}

			XMLDocReader docReader = getDocReader(id);
			if (docReader == null) {
				errorMsg = "ERROR: could not find record for " + id + " in index";
				prtln(errorMsg);
				addStatusMessage(errorMsg);
				numNotValidated++;
				continue;
			}

			File sourceFile = docReader.getFile();
			// prtln ("\n\n" + id + "     " + sourceFile.getAbsolutePath());

			if (!sourceFile.exists()) {
				errorMsg = "ERROR: source file does not exist at " + sourceFile.getAbsolutePath();
				prtln(errorMsg);
				addStatusMessage(errorMsg);
				numNotValidated++;
				continue;
			}

			RepositoryManager rm = docReader.getRepositoryManager();
			DcsDataRecord dcsDataRecord = null;
			try {
				dcsDataRecord = dcsDataManager.getDcsDataRecord(id, rm);
			} catch (Exception e) {
				errorMsg = "ERROR: dcs data (" + id + ") could not be obtained: " + e.getMessage();
				prtln(errorMsg);
				addStatusMessage(errorMsg);
				numNotValidated++;
				continue;
			}				
			
			boolean validationIsStale = true;
			try {
				validationIsStale = this.validationIsStale(sourceFile, docReader);
			} catch (Throwable t) {
				prtlnErr ("could not validationIsStale for " + id + ": " + t);
				t.printStackTrace();
			}
			
			boolean validityUnknown = true;
			try {
				validityUnknown = dcsDataRecord.getIsValidityUnknown();
			} catch (Throwable t) {
				prtlnErr ("could not getIsValidityUnknown for " + id + ": " + t);
				t.printStackTrace();
			}

			// perform validation and bad char checking if this record's file has
			// been modified outside of the DCS
			if (validationIsStale || validityUnknown || this.ignoreCachedValidation) {
				if (validityUnknown)
					prtln(id + " validity is unknown");
				if (validationIsStale) {
					prtln(id + " file mod does not match with reader mod ... validating ...");
					try {
						/* record on disk has been changed, so we first index it so that the
						index is consistent with the file on disk
						- RATIONALE - files on disk are primary!
						*/
						String recordXml = Files.readFile(sourceFile).toString();
						String collection = docReader.getCollectionKey();
						rm.putRecord (recordXml, docReader.getNativeFormat(), collection, id, true);
					} catch (Throwable t) {
						prtlnErr ("ERROR (" + id + ") :" + t.getMessage());
					}
				}
				if (ignoreCachedValidation)
					prtln("ignoring cashed validation");

				// ---------- VALIDATE -------------
				// this is validating from indexed record, NOT record on disk!
				validate(docReader, dcsDataRecord);

				if (dcsDataRecord.getIsValidityUnknown())
					prtlnErr ("ERROR: " + id + " validity is unknown AFTER validation");
			}

			if (dcsDataRecord.getIsValid() == "false") {
				errorMsg = id + " is NOT VALID";
				numNotValid++;
			}
			else {
				numValid++;
			}
			numValidated++;
			report.addEntry(dcsDataRecord);

			Thread.yield();
			// prtln("validateRecords() stopProcessing: " + stopProcessing + ", " + i +"/"+records.length);

		}

		if (stopProcessing) {
			msg = "Validation stopped by user";
			addStatusMessage(msg);
			if (observer != null)
				observer.serviceCompleted(ThreadedServiceObserver.SERVICE_COMPLETED_ABORTED, msg);
			return;
		}
		String validationSummary = "\n" + numValidated + " total records validated; ";
		validationSummary += numValid + " valid records, " + numNotValid + " NOT valid";
		if (numNotValidated > 0)
			validationSummary += "  NOTE: " + numNotValidated + " record could not be validated due to system errors";
		msg = "Completed Validating records: " + validationSummary;
		addStatusMessage(msg);

		// close out report
		report.recordsProcessed = numToValidate;
		report.processingTime = new Date().getTime() - start;
		report.setProp("numToValidate", Integer.toString(numToValidate));
		report.setProp("numValidated", Integer.toString(numValidated));
		report.setProp("numValid", Integer.toString(numValid));
		report.setProp("numNotValid", Integer.toString(numNotValid));
		report.setProp("numNotValidated", Integer.toString(numNotValidated));

		setServiceReport(report);
		archiveServiceReport(report);
		if (observer != null)
			observer.serviceCompleted(ThreadedServiceObserver.SERVICE_COMPLETED_SUCCESS, msg);
	}


	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	protected static void prtln(String s) {
		if (debug) {
			System.out.println("ValidatingService: " + s);
		}
	}


	/**
	 *  Description of the Class
	 *
	 * @author    ostwald
	 */
	private class ValidateThread extends Thread {
		List idList;
		DcsSetInfo dcsSetInfo;
		ThreadedServiceObserver observer;


		/**
		 *  Constructor for the ValidateThread object
		 *
		 * @param  idList      NOT YET DOCUMENTED
		 * @param  dcsSetInfo  NOT YET DOCUMENTED
		 */
		public ValidateThread(List idList, DcsSetInfo dcsSetInfo, ThreadedServiceObserver observer) {
			this.idList = idList;
			this.dcsSetInfo = dcsSetInfo;
			this.observer = observer;
			setDaemon(true);
		}


		/**  Main processing method for the ValidateThread object  */
		public void run() {
			try {
				doValidateRecords(idList, observer);
			} catch (Throwable e) {
				e.printStackTrace();
				// addStatusMessage(e.toString());
				String errorMsg = "Error validating records: " + e;
				addStatusMessage(errorMsg);
				prtln("ERROR validating records: " + e);
			} finally {
				prtln ("IN FINALLY CLAUSE");
				isProcessing = false;
				sessionBean = null;
				dcsSetInfo = null;
			}
		}
	}

}

