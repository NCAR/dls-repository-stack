
/**
 *  Copyright 2002, 2003 DLESE Program Center/University Corporation for
 *  Atmospheric Research (UCAR), P.O. Box 3000, Boulder, CO 80307,
 *  support@dlese.org.<p>
 *
 *  This file is part of the DLESE Tools Project.<p>
 *
 *  The DLESE Tools Project is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or (at your
 *  option) any later version.<p>
 *
 *  The DLESE Tools Project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 *  Public License for more details.<p>
 *
 *  You should have received a copy of the GNU General Public License along with
 *  The DLESE System; if not, write to the Free Software Foundation, Inc., 59
 *  Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package edu.ucar.dls.schemedit.ndr;

import java.util.*;
import java.text.*;
import edu.ucar.dls.util.Utils;
import org.dom4j.*;

import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.SessionBean;
import edu.ucar.dls.schemedit.MetaDataFramework;
import edu.ucar.dls.schemedit.FrameworkRegistry;
import edu.ucar.dls.schemedit.repository.CollectionIndexingObserver;
import edu.ucar.dls.schemedit.repository.RepositoryService;
import edu.ucar.dls.schemedit.config.CollectionRegistry;
import edu.ucar.dls.schemedit.config.CollectionConfig;
import edu.ucar.dls.schemedit.threadedservices.TaskProgress;
import edu.ucar.dls.schemedit.threadedservices.MonitoredTask;
import edu.ucar.dls.schemedit.dcs.DcsSetInfo;
import edu.ucar.dls.schemedit.dcs.DcsDataManager;
import edu.ucar.dls.schemedit.dcs.DcsDataRecord;
import edu.ucar.dls.schemedit.ndr.writer.MetadataWriter;
import edu.ucar.dls.schemedit.ndr.writer.NSDLCollectionWriter;
import edu.ucar.dls.schemedit.ndr.writer.MetadataProviderWriter;
import edu.ucar.dls.ndr.reader.MetadataProviderReader;
import edu.ucar.dls.ndr.NdrUtils;
import edu.ucar.dls.repository.RepositoryManager;
import edu.ucar.dls.repository.UpdateNotPermittedException;
import edu.ucar.dls.index.ResultDoc;
import edu.ucar.dls.index.ResultDocList;
import edu.ucar.dls.index.reader.XMLDocReader;
import edu.ucar.dls.index.SimpleFileIndexingObserver;
import javax.servlet.ServletContext;

/**
 *  Threaded version of NDRSync
 *
 * @author    ostwald <p>
 *
 *
 */
public class SyncService extends MonitoredTask {

	/**  NOT YET DOCUMENTED */
	protected static boolean debug = true;
	/**  NOT YET DOCUMENTED */
	protected boolean isProcessing = false;
	/**  NOT YET DOCUMENTED */
	protected boolean stopProcessing = false;
	/**  NOT YET DOCUMENTED */

	ServletContext servletContext;
	String collectionKey;
	SessionBean sessionBean;
	CollectionConfig collectionConfig;
	DcsDataManager dcsDataManager;
	RepositoryManager repositoryManager;
	String urlPath = null;
	FrameworkRegistry frameworkRegistry = null;
	CollectionRegistry collectionRegistry = null;

	String aggregatorHandle = null;
	String metadataProviderHandle = null;
	SyncReport report = null;

	// SyncReport syncReport = null;

	boolean deleteUnmatchedNDRRecords = false; // when true, removes unmatched records from the NDR


	/**
	 *  Constructor for the SyncService object
	 *
	 * @param  servletContext  NOT YET DOCUMENTED
	 * @param  sessionBean     NOT YET DOCUMENTED
	 * @exception  Exception   NOT YET DOCUMENTED
	 */
	public SyncService(SessionBean sessionBean, ServletContext servletContext) throws Exception {
		// set up
		super();
		this.servletContext = servletContext;

		this.sessionBean = sessionBean;
		try {
			repositoryManager = (RepositoryManager) getServletContextAttribute("repositoryManager");
			dcsDataManager = (DcsDataManager) getServletContextAttribute("dcsDataManager");

			frameworkRegistry =
				(FrameworkRegistry) getServletContextAttribute("frameworkRegistry");

			collectionRegistry = (CollectionRegistry) getServletContextAttribute("collectionRegistry");
		} catch (Throwable e) {
			// e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}


	/**
	 *  Gets the isProcessing attribute of the SyncService object
	 *
	 * @return    The isProcessing value
	 */
	public boolean getIsProcessing() {
		return this.isProcessing;
	}


	/**
	 *  A record is out of sync if the metadata has been changed, or the status has
	 *  been touched since it was last written (synced) to the NDR.<p>
	 *
	 *  NOTE: if a record is modified when the NDR is not enabled, it's fileMod
	 *  will be greater than the lastSync date, and therefore should be synced.
	 *
	 * @param  dcsDataRecord  the dcsDataRecord
	 * @param  docReader      NOT YET DOCUMENTED
	 * @return                true if the metadata record is out of sync with the
	 *      ndr
	 */
	private boolean recordOutOfSync(DcsDataRecord dcsDataRecord, XMLDocReader docReader) {

		long fileMod = docReader.getFile().lastModified();
		Date lastSyncDate = dcsDataRecord.getLastSyncDateDate();
		Date changeDate = dcsDataRecord.getChangeDateDate();
		Date lastTouchDate = dcsDataRecord.getLastTouchDateDate();
		/* 		prtln("\nlastSyncDate: " + SchemEditUtils.fullDateString(lastSyncDate));
		prtln("\t changeDate: " + SchemEditUtils.fullDateString(changeDate));
		prtln("\t lastTouch: " + SchemEditUtils.fullDateString(lastTouchDate));
		prtln("\t fileMod: " + fileMod + " (" + SchemEditUtils.fullDateString(new Date(fileMod)) + ")");
		prtln("\t fileMod - lastSyncDate: " + (fileMod - lastSyncDate.getTime())); */
		// HERE we decide whether this record has changed or not ...
		return ((fileMod - lastSyncDate.getTime() > 2000) ||
			changeDate.after(lastSyncDate) || lastTouchDate.after(lastSyncDate));
	}


	/**
	 *  Write collection-level info (metadataProvider and Aggregator) for the
	 *  specified collection (set) to the NDR.
	 *
	 * @param  setInfo        Data structure holding collection information
	 * @param  syncReport     NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public void writeCollectionInfo(DcsSetInfo setInfo, SyncReport syncReport) throws Exception {
		DcsDataRecord dcsDataRecord = dcsDataManager.getDcsDataRecord(setInfo.getId(), repositoryManager);
		prtln("\nwriteCollectionInfo() setInfo.getId: " + setInfo.getId());
		MetadataProviderWriter writer = new MetadataProviderWriter(servletContext);
		SyncReport mpwReport = writer.write(setInfo.getId(), collectionConfig, dcsDataRecord);
		if (syncReport == null) {
			prtln("SyncReport is null - bailing");
			return;
		}
		try {
			prtln("mpwReport entries: " + mpwReport.getEntryList().size());
			for (Iterator i = mpwReport.getEntryList().iterator(); i.hasNext(); ) {
				ReportEntry entry = (ReportEntry) i.next();
				if (entry == null) {
					prtln("\t entry is null");
					continue;
				}
				syncReport.addEntry((ReportEntry) i.next());
			}
		} catch (Throwable t) {
			prtln("writeCollectionInfo error: " + t.getMessage());
			t.printStackTrace();
		}
	}


	/**
	 *  Gets the metadataWriter attribute of the NDRSync object
	 *
	 * @return                The metadataWriter value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	private MetadataWriter getMetadataWriter() throws Exception {
		if (this.getXmlFormat().equals("ncs_collect")) {
			return new NSDLCollectionWriter(this.servletContext);
		}
		else {
			return new MetadataWriter(this.servletContext);
		}
	}


	/**
	 *  Initializes data structures needed to sync collection, and then kicks off a
	 *  new thread to do the actual sync (see runSync).
	 *
	 * @param  collectionKey  NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public void sync(String collectionKey) throws Exception {
		prtln("\n======  SYNC() ===============");

		if (isProcessing) {
			prtln("ALERT: isProcessing!");
			return;
		}

		// get a new progress object

		TaskProgress progress = this.getTaskProgress();
		progress.reset();
		this.collectionKey = collectionKey;
		try {
			collectionConfig = getCollectionConfig(collectionKey);
			if (collectionConfig == null)
				throw new Exception("collection config not found for " + collectionKey);

			MetaDataFramework itemFramework = frameworkRegistry.getFramework(collectionConfig.getXmlFormat());
			if (itemFramework == null) {
				throw new Exception("collection cannot be synced because \"" +
					collectionConfig.getXmlFormat() + "\" is not loaded");
			}
			urlPath = itemFramework.getUrlPath();
			if (urlPath == null) {
				String msg = "item framework (" + itemFramework.getName() + ") ";
				msg += "does not define a URL path and therefore cannot be synced wtih the NDR";
				throw new Exception(msg);
			}
		} catch (Throwable e) {
			// e.printStackTrace();
			throw new Exception(e.getMessage());
		}

		isProcessing = true;
		report = new SyncReport(this.collectionConfig, this.getCollectionName());
		DcsSetInfo setInfo = this.collectionConfig.getSetInfo(repositoryManager);
		try {
			new SyncThread(setInfo, report, sessionBean).start();
		} catch (Throwable t) {
			prtlnErr("WARNING: sync error: " + t.getMessage());
		}
	}


	/**
	 *  Sync the collection and item level info for the collection specified by
	 *  "setInfo". First updates collection-level (metadataProvider, aggregator)
	 *  information in the NDR and then updates outOfSync metadata with the NDR.
	 *
	 * @param  setInfo        represents the collection to be synced
	 * @param  report         reports actions taken by sync process
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public void runSync(DcsSetInfo setInfo, SyncReport report) throws Exception {
		prtln("RUNNING SYNC");
		writeCollectionInfo(setInfo, report);
		syncItems(report, false);
		prtln("SYNC COMPLETE");
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public void syncItems() throws Exception {
		SyncReport report = new SyncReport(this.collectionConfig, this.getCollectionName());
		syncItems(report, true);
	}


	/**
	 *  Sync the item-level records with the NDR.<p>
	 *
	 *  First, write all the indexed records that are out of sync to the NDR. Then,
	 *  (optionally - depending on the deleteUnmatchedNDRRecords attribute) delete
	 *  all the objects in the NDR associated with this collection that are NOT
	 *  indexed.
	 *
	 * @param  syncReport     NOT YET DOCUMENTED
	 * @param  syncAll        NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	private void syncItems(SyncReport syncReport, boolean syncAll) throws Exception {

		MetadataWriter metadataWriter = null;
		try {
			metadataWriter = getMetadataWriter();
		} catch (Exception e) {
			throw new Exception("Unable to initialize metadata writer: " + e.getMessage());
		}

		RepositoryService repositoryService = (RepositoryService)
			this.servletContext.getAttribute("repositoryService");
		if (repositoryService == null)
			throw new Exception("Unable to obtain repositoryService from servlet context");

		List idList = getIdList(); // this collection's items from index (local)
		List indexedHandles = new ArrayList();
		TaskProgress progress = this.getTaskProgress();
		progress.init(idList.size(), "Syncing Metadata");
		int counter = 0;
		for (Iterator i = idList.iterator(); i.hasNext() && !stopProcessing; ) {
			String id = (String) i.next();
			prtln("\n" + id);
			DcsDataRecord dcsDataRecord = dcsDataManager.getDcsDataRecord(id, repositoryManager);
			indexedHandles.add(dcsDataRecord.getNdrHandle());

			// HERE we decide whether this record has changed or not ...
			try {
				/*
					NOTE: we have to account for the case in which the record's file
						has been modified/touched without being indexed
						if the record is out of sync with the index,
						we must first update the index, THEN resync
				*/
				boolean indexOutOfSync = false;
				XMLDocReader docReader = repositoryService.getXMLDocReader(id);
				try {
					if (repositoryService.indexedRecordIsStale(id)) {
						prtln("\tIndex is out of sync with record on disk .. indexing record");
						indexOutOfSync = true;
						String xmlRecord = edu.ucar.dls.util.Files.readFile(docReader.getFile()).toString();
						// docReader.getFullContent() doesn't work

						// if we can't parse the record, then we don't want to index it. this will
						// throw exception so the putRecord call is never reached.
						Document wellFormed = Dom4jUtils.getXmlDocument(xmlRecord);

						repositoryManager.putRecord(xmlRecord,
							docReader.getNativeFormat(),
							docReader.getCollection(),
							id,
							true);
					}
				} catch (Exception e) {
					throw new Exception("Failed to index record: " + e.getMessage());
				}

				if (indexOutOfSync || recordOutOfSync(dcsDataRecord, docReader) || syncAll) {
					prtln("\t ... syncing");
					try {
						dcsDataRecord.clearSyncErrors();
						SyncReportEntry entry = metadataWriter.write(id, dcsDataRecord);
						syncReport.addEntry(entry);
						if (entry.getCommand().startsWith("add"))
							indexedHandles.add(entry.getMetadataHandle());
					} catch (Exception e) {
						prtlnErr("SyncError: " + e.getMessage());
						e.printStackTrace();
						syncReport.addEntry(new SyncReportEntry(id, e.getMessage()));
						dcsDataRecord.setNdrSyncError(e.getMessage());
						dcsDataRecord.flushToDisk();
					}
				}
				else {
					prtln("\t ... NOT syncing");
				}

			} catch (Exception e) {
				String errMsg = "Could not sync " + id + ": " + e.getMessage();
				prtlnErr(errMsg);
				SyncReportEntry entry = new SyncReportEntry(id, errMsg);
				syncReport.addEntry(entry);
			}

			progress.setDone(++counter);
		}

		// DELETE records in NDR that are NOT in NCS
		if (deleteUnmatchedNDRRecords && !stopProcessing) {
			List ndrHandles = getNdrHandles();
			counter = 0;
			progress.init(ndrHandles.size(), "Deleting orphan NDR objects");
			for (Iterator i = ndrHandles.iterator(); i.hasNext(); ) {
				String handle = (String) i.next();
				if (!indexedHandles.contains(handle)) {
					prtln("about to delete " + handle + " from NDR");
					try {
						syncReport.addEntry(new SyncReportEntry(handle, "delete", "", NdrUtils.deleteNDRObject(handle)));
					} catch (Exception e) {
						syncReport.addEntry(new SyncReportEntry(handle, e.getMessage()));
					}
				}
				progress.setDone(++counter);
			}
		}
	}


	/**
	 *  Gets the syncReport attribute of the SyncService object
	 *
	 * @return    The syncReport value
	 */
	public SyncReport getSyncReport() {
		return this.report;
	}


	/**
	 *  Gets the handles of Metadata Objects in the NDR for this collection.
	 *
	 * @return    The ndrHandles value
	 */
	private List getNdrHandles() {
		try {
			String mdpHandle = collectionConfig.getMetadataProviderHandle();
			MetadataProviderReader mdpReader = new MetadataProviderReader(mdpHandle);
			return mdpReader.getItemHandles();
		} catch (Throwable t) {
			prtln("getNdrHandles error: " + t);
		}
		return new ArrayList();
	}


	/**
	 *  Return a query string that will find records for the specified collection.
	 *
	 * @param  collection  NOT YET DOCUMENTED
	 * @param  statuses    NOT YET DOCUMENTED
	 * @return             NOT YET DOCUMENTED
	 */
	private String buildQuery(String collection, String[] statuses) {
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


	/**
	 *  Obtain list of itemRecord ids from the index for collection to be exported.
	 *
	 * @return    A list of record Ids
	 */
	private List getIdList() {
		return getIdList(null);
	}


	/**
	 *  Obtain list of itemRecord ids for records having specified status from the
	 *  index for collection to be exported.
	 *
	 * @param  statuses  Statuses that will be searched over
	 * @return           A list of record Ids
	 */
	private List getIdList(String[] statuses) {
		List idList = new ArrayList();
		String query = buildQuery(collectionKey, statuses);
		ResultDocList results = repositoryManager.getIndex().searchDocs(query);
		if (results != null && results.size() > 0) {
			for (int i = 0; i < results.size(); i++) {
				XMLDocReader docReader = (XMLDocReader) ((ResultDoc)results.get(i)).getDocReader();
				idList.add(docReader.getId());
			}
		}
		prtln("getIdList returning " + idList.size() + " items");
		return idList;
	}


	/**
	 *  Gets the collectionConfig instance for the collection to be exported. Also
	 *  ensures that the collection config's "authority" field is set to "ndr".
	 *
	 * @param  collectionKey  NOT YET DOCUMENTED
	 * @return                CollectionConfig instance
	 * @exception  Exception  If collectionConfig or setInfo cannot be found for
	 *      collectionKey
	 */
	private CollectionConfig getCollectionConfig(String collectionKey) throws Exception {
		// do NOT create new collection info if it doesn't already exist
		CollectionConfig config = collectionRegistry.getCollectionConfig(collectionKey, false);
		if (config == null)
			throw new Exception("collection not found for \"" + collectionKey + "\"");

		// ensure that the repository manager knows about this collection
		if (config.getSetInfo(repositoryManager) == null)
			throw new Exception("set config not found for  for \"" + collectionKey + "\"");
		config.setAuthority("ndr");
		return config;
	}


	/**
	 *  Convenience method to obtain a servletContext attribute.
	 *
	 * @param  att            attribute name
	 * @return                named object from ServletContext
	 * @exception  Exception  if not found in ServletContext
	 */
	private Object getServletContextAttribute(String att) throws Exception {
		Object o = servletContext.getAttribute(att);
		if (o == null)
			throw new Exception(att + " not found in servletContext");
		return o;
	}


	/**
	 *  Gets the xmlFormat attribute of the collection to be exported.
	 *
	 * @return    The xmlFormat value
	 */
	String getXmlFormat() {
		return this.collectionConfig.getXmlFormat();
	}


	/**
	 *  Gets the name (i.e., "short title") attribute of the collection to be
	 *  exported.
	 *
	 * @return    The collectionName value
	 */
	String getCollectionName() {
		return this.collectionConfig.getSetInfo(repositoryManager).getName();
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  node  NOT YET DOCUMENTED
	 */
	private static void pp(Node node) {
		System.out.println(Dom4jUtils.prettyPrint(node));
	}


	/**  Halts a threaded service. */
	public void stopProcessing() {
		stopProcessing = true;
		// Signal threads to stop indexing
		prtln("stopProcessing() stopProcessing: " + stopProcessing);
		try {
			Thread.sleep(5000);
		} catch (Throwable e) {}
		stopProcessing = false;
		prtln("stopProcessing() stopProcessing: " + stopProcessing);
		isProcessing = false;
		prtln("Processing has stopped...");
	}


	/**
	 *  Return a string for the current time and date, sutiable for display in log
	 *  files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	public static String getSimpleDateStamp() {
		try {
			return Utils.convertDateToString(new Date(), "EEE, MMM d h:mm:ss a");
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
			System.out.println("SyncService: " + s);
		}
	}


	static void prtlnErr(String s) {
		System.out.println("SyncService: " + s);
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @author    Jonathan Ostwald
	 */

	/**
	 *  Description of the Class
	 *
	 * @author    ostwald
	 */
	private class SyncThread extends Thread {
		DcsSetInfo dcsSetInfo;
		SyncReport report;
		SessionBean sessionBean;


		/**
		 *  Constructor for the SyncThread object
		 *
		 * @param  dcsSetInfo   NOT YET DOCUMENTED
		 * @param  report       NOT YET DOCUMENTED
		 * @param  sessionBean  NOT YET DOCUMENTED
		 */
		public SyncThread(DcsSetInfo dcsSetInfo, SyncReport report, SessionBean sessionBean) {
			this.dcsSetInfo = dcsSetInfo;
			this.report = report;
			this.sessionBean = sessionBean;
			setDaemon(true);
		}


		/**  Main processing method for the SyncThread object */
		public void run() {
			try {
				runSync(dcsSetInfo, report);
			} catch (Throwable e) {
				// e.printStackTrace();
				// addStatusMessage(e.toString());
				String errorMsg = "Error syncing records: " + e;
				prtlnErr(errorMsg);
				e.printStackTrace();
			} finally {
				isProcessing = false;
				if (this.report.getEntryList().size() > 0) {
					prtln("NOW INDEXING ...");
					try {
						repositoryManager.indexCollection(
							collectionKey,
							new CollectionIndexingObserver(collectionKey, collectionRegistry, repositoryManager), true);
					} catch (UpdateNotPermittedException re) {
						String errorMsg = "Error syncing records: " + re;
						prtlnErr(errorMsg);
						re.printStackTrace();
					}
				}
				this.sessionBean.releaseAllLocks();
			}
		}
	}

}
