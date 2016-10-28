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
import edu.ucar.dls.schemedit.repository.RepositoryService;
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
 *  Provides services over collections, such as Validation and Export, that run
 *  in their own thread. Also manages reports generated by the services.
 *
 * @author    ostwald
 */
public abstract class ThreadedService extends MonitoredTask {

	protected static boolean debug = false;
	protected final static int NUM_STATUS_MESSAGES = 12;

	protected DcsSetInfo dcsSetInfo = null; // represents the collection currently being validated
	protected SessionBean sessionBean = null;
	protected SimpleDataStore dataStore = null;
	protected ServletContext servletContext = null;
	// protected SimpleLuceneIndex index;
	protected DcsDataManager dcsDataManager;
	protected boolean validateFiles = true;
	protected boolean indexOnValidation = true;
	protected String[] statuses;

	protected boolean isProcessing = false;
	protected boolean stopProcessing = false;


	/**
	 *  Constructor for the ThreadedService object
	 *
	 * @param  servletContext          NOT YET DOCUMENTED
	 * @param  threadedServiceDataDir  NOT YET DOCUMENTED
	 */
	public ThreadedService(ServletContext servletContext, String threadedServiceDataDir) {
		super();
		
		this.servletContext = servletContext;
		this.dcsDataManager =
			(DcsDataManager) servletContext.getAttribute("dcsDataManager");

			
		try {
			File f = new File(threadedServiceDataDir);
			f.mkdir();
			dataStore = new SimpleDataStore(f.getAbsolutePath(), true);
		} catch (Exception e) {
			prtlnErr("Error initializing SimpleDataStore: " + e);
		}
		
		clearServiceReport();
	}

	protected RepositoryManager getRepositoryManager () {
		return (RepositoryManager) servletContext.getAttribute("repositoryManager");
	}

	protected SimpleLuceneIndex getIndex() {
		try {
			return this.getRepositoryManager().getIndex();
		} catch (Throwable t) {
			t.printStackTrace();
			prtlnErr("getIndex() could not obtain index: " + t);
		}
		return null;
	}
	
	/**
	 *  Gets the isProcessing attribute of the ThreadedService object
	 *
	 * @return    The isProcessing value
	 */
	public boolean getIsProcessing() {
		return isProcessing;
	}


	/**
	 *  Gets the indexOnValidation attribute of the ThreadedService object
	 *
	 * @return    The indexOnValidation value
	 */
	public boolean getIndexOnValidation() {
		return indexOnValidation;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  docReader      NOT YET DOCUMENTED
	 * @param  dcsDataRecord  NOT YET DOCUMENTED
	 */
	protected void validate(XMLDocReader docReader, DcsDataRecord dcsDataRecord) {
		RepositoryService repositoryService =
			(RepositoryService) servletContext.getAttribute("repositoryService");
		if (repositoryService == null) {
			prtlnErr("\nWARNING: ThreadedService cannot access repositoryService!");
		}
		File file = docReader.getFile();
		/* we were getting different results when passing a file or a string. 
		   the string (encoded as 'utf-8' gives us results consistent with command-line
		   parsers, as well as Xerces.
		*/
		// repositoryService.validateRecord(docReader.getFile(), dcsDataRecord, docReader.getNativeFormat());
		
		try {
			StringBuffer xml = Files.readFileToEncoding(file, "utf-8");
			repositoryService.validateRecord(xml.toString(), dcsDataRecord, docReader.getNativeFormat());
		} catch (Exception e) {
			prtlnErr ("could not validate: " + e.getMessage());
		}

		// perform badcharacter check on localized Document
		String xmlRecord = docReader.getXmlLocalized();
		try {
			if (indexOnValidation) {
				prtln("indexing " + docReader.getId());
				repositoryService.updateRecord(docReader.getId());
			}
			dcsDataRecord.flushToDisk();
		} catch (Exception e) {
			prtlnErr ("WARNING: failed to write dcsDataRecord to disk: " + e);
			e.printStackTrace();
		}
	}


	/**
	 *  convience method to retrieve the docReader given a record id
	 *
	 * @param  id  NOT YET DOCUMENTED
	 * @return     The docReader value
	 */
	protected XMLDocReader getDocReader(String id) {
		String query = "id:" + SimpleLuceneIndex.encodeToTerm(id, false);
		ResultDocList results = getIndex().searchDocs(query);
		if (results != null || results.size() > 1) {
			return (XMLDocReader) ((ResultDoc)results.get(0)).getDocReader();
		}
		else
			return null;
	}


	/**
	 *  Test to see if record has been modified outside of DCS.
	 *
	 * @param  sourceFile  file on disk
	 * @param  docReader   the docReader
	 * @return             true if file has changed on disk
	 */
	protected boolean validationIsStale(File sourceFile, XMLDocReader docReader) {
		long granularity = 2000;
		// prtln ("\nvalidationIsStale()");

		long diff = Math.abs(sourceFile.lastModified() - docReader.getLastModified());
		/* 		if (diff > 0) {
			prtln ("\tsourceFile.lastModified(): " + sourceFile.lastModified());
			prtln ("\tdocReader.getLastModified(): " + docReader.getLastModified());
			prtln ("\t --> diff: " + diff);
		} */
		return diff > granularity;
	}


	/**
	 *  Build a list of record ids from the results of a query for specified
	 *  collection and statuses. This list is used by validate and export to
	 *  iterate over items to process. The reason we don't use the resultDocs
	 *  obtained by querying the index is that, for long result lists, the index
	 *  may change out from under the resultDocs, causing the resultDocs to be
	 *  unreliable.
	 *
	 * @param  collection  NOT YET DOCUMENTED
	 * @param  statuses    NOT YET DOCUMENTED
	 * @return             The idList value
	 */
	protected List getIdList(String collection, String[] statuses) {
		List idList = new ArrayList();
		
		String query = buildQuery(dcsSetInfo.getSetSpec(), statuses);
		ResultDocList results = getIndex().searchDocs(query);
		if (results != null && results.size() > 0) {
			for (int i = 0; i < results.size() && !stopProcessing; i++) {
				XMLDocReader docReader = (XMLDocReader) ((ResultDoc)results.get(i)).getDocReader();
				idList.add(docReader.getId());
			}
		}
		prtln ("getIdList returning " + idList.size() + " items");
		return idList;
	}


	/**
	 *  Creates a delimited string from the provided list
	 *
	 * @param  list       a list to be coverted
	 * @param  delimiter  delimiter used to separate items
	 * @return            delmited string of list items
	 */
	protected String list2delimitedString(List list, String delimiter) {
		String ret = "";
		if (list == null || list.size() == 0)
			return ret;
		for (Iterator i = list.iterator(); i.hasNext(); ) {
			ret += (String) i.next();
			if (i.hasNext())
				ret += ",";
		}
		return ret;
	}


	/**
	 *  Gets the sessionId attribute of the ThreadedService object
	 *
	 * @return    The sessionId value
	 */
	public String getSessionId() {
		if (isProcessing && sessionBean != null)
			return sessionBean.getId();
		else
			return null;
	}


	/**  Halts a threaded service. */
	public void stopProcessing() {
		stopProcessing = true;
		// Signal threads to stop indexing
		try {
			Thread.sleep(5000);
		} catch (Throwable e) {}
		stopProcessing = false;
		isProcessing = false;
		sessionBean = null;
		prtln("Processing has stopped...");
	}


	/**
	 *  Return a query string that will find records for the specified collection.
	 *
	 * @param  collection  collectionKey
	 * @param  statuses    statuses to be included in query
	 * @return             a query string to find items in collection with
	 *      specified statuses
	 */
	protected String buildQuery(String collection, String[] statuses) {
		String query = "(collection:0*) AND collection:0" + collection;

		/* handle status selection options */
		if (statuses != null &&
			statuses.length > 0) {
			query = "(" + query + ") AND (dcsstatus:" + SchemEditUtils.quoteWrap(statuses[0]);
			for (int i = 1; i < statuses.length; i++) {
				query += " OR dcsstatus:" + SchemEditUtils.quoteWrap(statuses[i]);
			}
			query += ")";
		}

		prtln("query: " + query);
		return query;
	}


	/**  Clear status messages */
	protected void clearStatusMessages() {
		dataStore.remove("STATUS_MESSAGES");
	}


	/**
	 *  Adds a feature to the ValidatingMessage attribute of the ThreadedService
	 *  object
	 *
	 * @param  msg  The feature to be added to the ValidatingMessage attribute
	 */
	protected void addStatusMessage(String msg) {
		prtln(getSimpleDateStamp() + ": " + msg);

		ArrayList statusMessages =
			(ArrayList) dataStore.get("STATUS_MESSAGES");
		if (statusMessages == null) {
			statusMessages = new ArrayList(NUM_STATUS_MESSAGES);
		}
		statusMessages.add(getSimpleDateStamp() + " " + msg);
		if (statusMessages.size() > NUM_STATUS_MESSAGES) {
			statusMessages.remove(0);
		}
		dataStore.put("STATUS_MESSAGES", statusMessages);
	}


	/**
	 *  Gets the statusMessages attribute of the ThreadedService object
	 *
	 * @return    The statusMessages value
	 */
	public ArrayList getStatusMessages() {
		ArrayList statusMessages =
			(ArrayList) dataStore.get("STATUS_MESSAGES");
		if (statusMessages == null) {
			statusMessages = new ArrayList();
			statusMessages.add("No status messages logged yet...");
		}
		return statusMessages;
	}


	/**
	 *  Gets the serviceReport attribute of the ThreadedService object
	 *
	 * @return    The serviceReport value
	 */
	public Report getServiceReport() {
		Report statusReport = (Report) dataStore.get("SERVICE_REPORT");
		return statusReport;
	}


	/**
	 *  Sets the serviceReport attribute of the ThreadedService object
	 *
	 * @param  report  The new serviceReport value
	 */
	public void setServiceReport(Report report) {
		dataStore.put("SERVICE_REPORT", report);
	}


	/**
	 *  Sets the dcsSetInfo attribute of the ThreadedService object
	 *
	 * @param  setInfo        The new dcsSetInfo value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	protected void setDcsSetInfo(DcsSetInfo setInfo) throws Exception {
		if (this.getIsProcessing()) {
			throw new Exception("attempting to set DcsSetInfo while service is excecuting");
		}
		this.dcsSetInfo = setInfo;
	}


	/**
	 *  Sets the statuses attribute of the ThreadedService object
	 *
	 * @param  statuses       The new statuses value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	protected void setStatuses(String[] statuses) throws Exception {
		if (this.getIsProcessing()) {
			throw new Exception("attempting to set Statuses while service is excecuting");
		}
		this.statuses = statuses;
	}


	/**  NOT YET DOCUMENTED */
	public void clearServiceReport() {
		dataStore.remove("SERVICE_REPORT");
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  report  NOT YET DOCUMENTED
	 */
	protected void archiveServiceReport(Report report) {
		if (dcsSetInfo != null)
			dataStore.put("ARCHIVED_REPORT_" + dcsSetInfo.getSetSpec(), report);
		else
			prtlnErr("WARNING: archiveServiceReport could not obtain setSpec");
	}


	/**
	 *  Gets the archivedReport attribute of the ThreadedService object
	 *
	 * @param  collection  NOT YET DOCUMENTED
	 * @return             The archivedReport value
	 */
	public Report getArchivedReport(String collection) {
		String id = "ARCHIVED_REPORT_" + collection;
		return (Report) dataStore.get(id);
	}


	/**
	 *  Gets the archivedReports attribute of the ThreadedService object
	 *
	 * @return    The archivedReports value
	 */
	public List getArchivedReports() {
		String[] ids = dataStore.getIDsSorted();
		ArrayList reports = new ArrayList();
		for (int i = 0; i < ids.length; i++) {
			String id = ids[i];
			if (id.startsWith("ARCHIVED_REPORT_")) {
				Report rpt = (Report) dataStore.get(id);
				if (rpt != null) {
					reports.add(rpt);
				}
				else {
					prtln("getArchivedReports failed to retrieve " + id);
					if (dataStore.delete(id))
						prtln(" ... report deleted");
					else
						prtln("  ... report NOT deleted!");
				}
			}
		}
		Collections.sort(reports, new SortReports());
		return reports;
	}



	/**
	 *  Sets whether or not to validate the files being indexed. If set to true,
	 *  the files will be validated, otherwise they will not. Default is true.
	 *
	 * @param  validateFiles  True to validate, else false.
	 * @see                   edu.ucar.dls.index.writer.FileIndexingServiceWriter#getValidationReport()
	 */
	public void setValidationEnabled(boolean validateFiles) {
		this.validateFiles = validateFiles;
	}


	/**
	 *  Sets the indexOnValidation attribute of the ThreadedService object
	 *
	 * @param  indexOnValidation  The new indexOnValidation value
	 */
	public void setIndexOnValidation(boolean indexOnValidation) {
		this.indexOnValidation = indexOnValidation;
	}


	/**
	 *  Return a string for the current time and date, sutiable for display in log
	 *  files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	public static String getSimpleDateStamp() {
		try {
			return
				Utils.convertDateToString(new Date(), "EEE, MMM d h:mm:ss a");
		} catch (ParseException e) {
			return "";
		}
	}


	/**
	 *  Return a string for the current time and date, sutiable for display in log
	 *  files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	public static String getDateStamp() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln (s, "ThreadedService");
		}
	}

	static void prtlnErr(String s) {
		SchemEditUtils.prtln (s, "ThreadedService");
	}
	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @author     Jonathan Ostwald
	 * @version    $Id: ThreadedService.java,v 1.17 2009/03/23 07:25:38 ostwald Exp
	 *      $
	 */
	public class SortReports implements Comparator {

		/**
		 *  Provide comparison for sorting Sugest a URL Records by "lastModified"
		 *  property
		 *
		 * @param  o1  document 1
		 * @param  o2  document 2
		 * @return     DESCRIPTION
		 */
		public int compare(Object o1, Object o2) {
			Date dateOne;
			Date dateTwo;
			try {
				dateOne = ((Report) o1).getTimeStamp();
				dateTwo = ((Report) o2).getTimeStamp();
			} catch (Exception e) {
				prtln("Error: unable to find last modified date: " + e.getMessage());
				return 0;
			}
			return dateTwo.compareTo(dateOne);
		}

	}

}

