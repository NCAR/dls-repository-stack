/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package edu.ucar.dls.dds.nr;

import edu.ucar.dls.repository.indexing.*;

import java.io.*;
import java.util.*;
import java.net.URL;

import edu.ucar.dls.index.*;
import edu.ucar.dls.index.writer.*;
import java.text.*;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.sql.Blob;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.codec.binary.StringUtils;

import edu.ucar.dls.propertiesmgr.PropertiesManager;
import edu.ucar.dls.util.TimedURLConnection;
import edu.ucar.dls.repository.PutCollectionException;
import edu.ucar.dls.util.*;

import org.dom4j.*;
import edu.ucar.dls.xml.Dom4jUtils;
import org.apache.lucene.document.Field;
import java.net.URLEncoder;

/**
 *  Indexes and configures collections from the NSDL Repository (NR) (MySQL implementation).
 *  8/8/2013 - This file is the NRIndexer.java minus the normalization steps for lar and nsdl_dc.
 *  The normalization steps were removed and placed in the Harvest Manager to make all normalization
 *  and error handling happen in the same place
 * @author    John Weatherley
 */
public class NRIndexer2 implements ItemIndexer {
	private static boolean debug = true;

	private int MAX_NUM_COLLECTIONS = 40000;
	// The max number of collections DDS will suck in from NR (limit for testing...)
	private int MAX_RECORDS_PER_COLLECTION = 900000;
	// The max number of records DDS will suck in from NR per collection (limit for testing...)
	private boolean generateDupResourceUrls = false;
	// Generate duplicate resource URLs for testing?

	private float maxIndexingErrorRate = 1.00f;
	// The maximum number or indexing errors per individual collection allowed. If higher, deletions and index commits will not be processed.

	private CollectionIndexer collectionIndexer = null;
	private boolean abortIndexing = false;
	private boolean indexingIsActive = false;
	private File configFile = null;
	private File myPersistanceDir = null;
	private Document configXmlDoc = null;

	private String allowZeroRecordsInCollections = "true";

	private BasicDataSource dataSource = null;
	public static final String INDEX_CONFIG_NAME = "NRIndexer_conf.xml";

	// Should verbose comments be written into the normalized metadata?

	/**  Constructor for the NRIndexer object */
	public NRIndexer2() { }


	/**
	 *  Sets the configDirectory attribute of the NRIndexer object
	 *
	 * @param  configDir       The new configDirectory value
	 * @param  persistanceDir  The new configDirectory value
	 * @exception  Exception   If error
	 */
	public void setConfigDirectory(File configDir, File persistanceDir) throws Exception {
		if (configDir == null)
			throw new Exception("The configDir is null");
		this.configFile = new File(configDir, "NRIndexer_conf.xml");
		myPersistanceDir = new File(persistanceDir, "NRIndexer");
		myPersistanceDir.mkdirs();
	}


	/**
	 *  Initialize and configure. This gets called once when the class is first created and is called by
	 *  updateCollections() when re-loading the list of collections.
	 *
	 * @exception  Exception  If error
	 */
	private void configureAndInitialize() throws Exception {
		synchronized (this) {
			try {
				prtln("reading config file located at: " + configFile);
				configXmlDoc = Dom4jUtils.getXmlDocument(configFile);
			} catch (Throwable t) {
				String msg = "Unable to read the NR configuration file '" + configFile + "'. Message: " + t.getMessage();
				prtlnErr(msg);
				throw new Exception(msg);
			}

			// Test a connection to the DB and report error if problems arise:
			BasicDataSource dataSource = setupDataSource();

			Connection con = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			try {
				con = dataSource.getConnection();
				pst = con.prepareStatement("SELECT setspec FROM session");
				rs = pst.executeQuery();
				int numSets = 0;
				while (rs.next()) {
					numSets++;
				}

				printStatusMessage("Pinging NR: DB contains " + numSets + " sets/collections (setSpecs)");

			} catch (SQLException ex) {
				String msg = "Unable to connect to NR database: " + ex;
				printStatusMessageError(msg);
				throw new Exception(msg);
			} finally {

				try {
					if (rs != null) {
						rs.close();
					}
					if (pst != null) {
						pst.close();
					}
					if (con != null) {
						con.close();
					}
					if (dataSource != null) {
						dataSource.close();
					}
				} catch (SQLException ex) {
					String msg = "Unable to close NR database: " + ex;
					printStatusMessageError(msg);
					throw new Exception(msg);
				}
			}

			// Allow zero records in collections (otherwise fatal error)?:
			allowZeroRecordsInCollections = configXmlDoc.valueOf("/NR_Collections/allowZeroRecordsInCollections");
			if (allowZeroRecordsInCollections == null)
				allowZeroRecordsInCollections = "true";

			// Set up some values for testing stuff:
			try {
				MAX_NUM_COLLECTIONS = Integer.parseInt(configXmlDoc.valueOf("/NR_Collections/maxNumCollections"));
			} catch (Throwable t) {}

			try {
				MAX_RECORDS_PER_COLLECTION = Integer.parseInt(configXmlDoc.valueOf("/NR_Collections/maxNumRecordsPerCollection"));
			} catch (Throwable t) {}

			String generateDupResourceUrls = configXmlDoc.valueOf("/NR_Collections/generateDupResourceUrls");
			if (generateDupResourceUrls != null && generateDupResourceUrls.equals("true"))
				this.generateDupResourceUrls = true;

		}
	}


	private BasicDataSource setupDataSource() throws SQLException {
		if (dataSource != null)
			dataSource.close();

		return createDataSource(configXmlDoc);
	}

	public static BasicDataSource createDataSource(Document configXmlDoc) throws SQLException {
		String dbUsername = configXmlDoc.valueOf("/NR_Collections/dbUsername");
		String dbPassword = configXmlDoc.valueOf("/NR_Collections/dbPassword");
		String dbUrl = configXmlDoc.valueOf("/NR_Collections/dbUrl");
		String dbDriver = configXmlDoc.valueOf("/NR_Collections/dbDriver");

		BasicDataSource dataSource = new BasicDataSource();

		dataSource.setDriverClassName(dbDriver);
		dataSource.setUsername(dbUsername);
		dataSource.setPassword(dbPassword);
		dataSource.setUrl(dbUrl);

		return dataSource;
	}
	
	public static BasicDataSource createDataSource(File configDir) throws Exception  {
		Document configXmlDoc = Dom4jUtils.getXmlDocument(new File(configDir, INDEX_CONFIG_NAME));
		return createDataSource(configXmlDoc);
	}


	/**
	 *  Perform indexing when requested
	 *
	 * @param  indexingEvent  The event fired
	 * @exception  Exception  If error
	 */
	public void indexingActionRequested(IndexingEvent indexingEvent)
		 throws Exception {
		collectionIndexer = indexingEvent.getCollectionIndexer();
		switch (indexingEvent.getType()) {

						case IndexingEvent.BEGIN_INDEXING_ALL_COLLECTIONS:
						{
							//prtln("NRIndexer started BEGIN_INDEXING_ALL_COLLECTIONS." + "\n\n");
							indexCollections(null);
							break;
						}

						case IndexingEvent.INDEXER_READY:
						{
							//prtln("NRIndexer started INDEXER_READY!" + "\n\n");
							return;
						}

						case IndexingEvent.BEGIN_INDEXING_COLLECTION:
						{
							//prtln("NRIndexer started BEGIN_INDEXING_COLLECTION!" + "\n\n");
							String key = indexingEvent.getCollectionKey();
							if (key != null && key.trim().length() > 0)
								indexCollections(key);
							return;
						}

						case IndexingEvent.ABORT_INDEXING:
						{
							//prtln("NRIndexer ABORT_INDEXING!" + "\n\n");
							printStatusMessage("Stopping indexing (may take a while)...");
							abortIndexing = true;
							return;
						}

						case IndexingEvent.UPDATE_COLLECTIONS:
						{
							prtln("NRIndexer started UPDATE_COLLECTIONS!" + "\n\n");
							updateCollections();
							return;
						}
						case IndexingEvent.CONFIGURE_AND_INITIALIZE:
						{
							prtln("NRIndexer updating configuration" + "\n\n");
							configureAndInitialize();
							return;
						}
		}
	}


	/**
	 *  Index a collection or all collections.
	 *
	 * @param  collectionKey  The collection key/id, or null for all
	 * @exception  Exception  If error
	 */
	public void indexCollections(String collectionKey) throws Exception {
		// If collectionKey is null, index all collections, if not, index only that collection...

		synchronized (this) {
			if (indexingIsActive)
				throw new IndexingInProgressException("NRIndexer is already running.");
			abortIndexing = false;
			indexingIsActive = true;
		}

		try {
			// Update collections and configurations:
			updateCollections();

			prtln("NRIndexer started." + "\n\n");
			printStatusMessage("Indexing started.");

			IndexingStatusBean indexingStatusBean = getIndexingStatusBean();

			// Initialize required NR attributes and Sets TransformerFactory to a XSL 1.0 version:
			//NdrUtils.ndrTestSetup();

			Throwable errorThrown = null;

			// Configure all collections defined in the NCS:
			List nrCollectionInfos = new ArrayList();

			nrCollectionInfos = getCollectionsFromRemoteNCS();

			int numSkippedFromPreviousSession = 0;
			int numCollectionErrorsThrown = 0;
			boolean displayNumSkippedFromPreviousSessionMsg = false;

			if (!abortIndexing) {
				for (int i = 0; i < nrCollectionInfos.size(); i++) {
					try {
						CollectionInfo collectionInfo = (CollectionInfo) nrCollectionInfos.get(i);

						String currentCollectionKey = collectionInfo.getCollectionKey();

						// If we are indexing all collections:
						if (collectionKey == null) {
							if (!indexingStatusBean.hasCompletedCollection(currentCollectionKey)) {
								if (displayNumSkippedFromPreviousSessionMsg) {
									displayNumSkippedFromPreviousSessionMsg = false;
									printStatusMessage("Skipped " + numSkippedFromPreviousSession + " collections that were completed successfully in a previous indexing session...");
									printStatusMessage("Continuing to index " + (nrCollectionInfos.size() - numSkippedFromPreviousSession) + " collections that were aborted or had errors...");
								}

								indexItemsInCollection(collectionInfo);

								// If all is well, indicate status success for this collection:
								indexingStatusBean.addCompletedCollection(currentCollectionKey);
								saveIndexingStatusBean(indexingStatusBean);
							}
							else {
								numSkippedFromPreviousSession++;
								displayNumSkippedFromPreviousSessionMsg = true;
							}
						}
						// If we are indexing one collection only:
						else if (collectionKey.equals(currentCollectionKey)) {
							indexItemsInCollection(collectionInfo);
							break;
						}

						//indexNdrCollection( currentCollection, collectionKey );

						if (abortIndexing)
							nrCollectionInfos.clear();

					} catch (Throwable t) {
						t.printStackTrace();
						errorThrown = t;
						numCollectionErrorsThrown++;
						String msg = "Error indexing a collection. " + numCollectionErrorsThrown + " collection(s) have had serious errors. Most recent collection error: " + t.getMessage();
						printStatusMessageError(msg);
					}
				}
			}

			if (errorThrown != null) {
				String msg = "Indexing of collections complete but changes not committed. There were serious indexing errors with " + numCollectionErrorsThrown + " collections(s). Most recent: " + errorThrown.getMessage();
				printStatusMessageError(msg);
			}
			else {
				if (abortIndexing) {
					printStatusMessage("Indexing aborted by request. Some collections may not have finished indexing.");
				}
				else {
					printStatusMessage("Indexing completed. Committing changes.");
					indexingStatusBean.setProgressStatus(IndexingStatusBean.PROGRESS_STATUS_INDEXING_COMPLETE);
					saveIndexingStatusBean(indexingStatusBean);
					collectionIndexer.commitIndex();
				}
			}

		} catch (Throwable t) {
			t.printStackTrace();
			String msg = "Error processing collections: " + t;
			printStatusMessageError(msg);
		} finally {
			synchronized (this) {
				indexingIsActive = false;
			}
			prtln("NRIndexer completed." + "\n\n");
		}
	}



	/**
	 *  Update the collections that are configured in DDS (does not index the items in the collections).
	 *
	 * @exception  Exception  If error
	 */
	public void updateCollections() throws Exception {

		// Reload the config files:
		configureAndInitialize();

		// Start status message
		printStatusMessage("Begin adding/updating NR collections...");

		// Add collections that I know about:
		//List ncsCollections = getNcsCollections();
		//prtln("getNcsCollections() returned: " + Arrays.toString(ncsCollections.toArray()));

		Throwable collectionError = null;
		List nrCollections = new ArrayList();

		// Configure collections defined in the NCS:
		List nrCollectionInfos = new ArrayList();
		try {
			nrCollectionInfos = getCollectionsFromRemoteNCS();
		} catch (Throwable t) {
			String msg = "Error getting NR collections from NCS: " + t.getMessage();
			printStatusMessageError(msg);
			return;
		}

		nrCollections = new ArrayList();
		for (int i = 0; i < nrCollectionInfos.size(); i++) {
			CollectionInfo collectionInfo = null;
			try {
				collectionInfo = (CollectionInfo) nrCollectionInfos.get(i);
				//Document ncsCollectDoc = collectionInfo.getNcsRecordDoc();

				String ncsRecordXml = collectionInfo.getNcsRecordXml();
				String collectionKey = collectionInfo.getCollectionKey();
				String formatToIndex = collectionInfo.getFormatToIndex();

				// Check if the collection already exists in DDS, if so, do nothing:
				String collRecString = collectionIndexer.getCollectionRecord(collectionKey);
				if (collRecString != null) {
					Document existingCollectDoc = Dom4jUtils.getXmlDocument(collRecString);
					String existingNcsRecordXml = Dom4jUtils.prettyPrint(existingCollectDoc.selectSingleNode("/*[local-name()='collectionRecord']/*[local-name()='additionalMetadata']/*[local-name()='ncsCollectionRecord']"));
					String existingFormat = existingCollectDoc.valueOf("/*[local-name()='collectionRecord']/*[local-name()='access']/*[local-name()='key']/@libraryFormat");
					if (ncsRecordXml.trim().equals(existingNcsRecordXml.trim()) && formatToIndex.equals(existingFormat)) {
						String collectionName = existingCollectDoc.valueOf("/*[local-name()='collectionRecord']/*[local-name()='general']/*[local-name()='fullTitle']");
						String msg = "Verified collection '" + collectionName + "' is in the repository";
						printStatusMessage(msg);
						nrCollections.add(collectionKey);
						continue;
					}
				}

				// Add the collection to DDS:
				nrCollections.add(collectionKey);
				doPutCollection(collectionInfo);
			} catch (Throwable t) {
				collectionError = t;
				String msg = "Error processing collection '" + collectionInfo.getCollectionName() + "'. Message: " + t.getMessage();
				printStatusMessageError(msg);
			}
		}

		// Do not delete/remove collections if there has been a serious error processing one of the collections:
		if (collectionError != null) {
			printStatusMessageError("One or more serious errors occured while updating the collections. No collections will be deleted. Most recent error message: " + collectionError);
			return;
		}

		// Delete all collections in the repository that I don't know about:
		List currentConfiguredCollections = collectionIndexer.getConfiguredCollections();
		for (int i = 0; i < currentConfiguredCollections.size(); i++) {
			String collKey = (String) currentConfiguredCollections.get(i);
			if (!nrCollections.contains(collKey) && !nrCollectionInfos.contains(collKey)) {
				String deletedColString = collectionIndexer.deleteCollection(collKey);
				String collName = collKey;
				if (deletedColString != null) {
					Document deletedCollectionDoc = Dom4jUtils.getXmlDocument(deletedColString);
					collName = deletedCollectionDoc.valueOf("/*[local-name()='collectionRecord']/*[local-name()='general']/*[local-name()='fullTitle']");
				}
				String msg = "Deleted collection '" + collName + "' (key:" + collKey + ")";
				printStatusMessage(msg);
			}
		}

		// Final status message
		printStatusMessage("Finished adding/updating NR collections.");
	}


	/**
	 *  Put a collection in DDS (does not index the items in the collections, just establishes the collection).
	 *
	 * @param  collectionInfo  NOT YET DOCUMENTED
	 * @exception  Exception   If error
	 */
	private void doPutCollection(CollectionInfo collectionInfo) throws Exception {

		String collectionKey = collectionInfo.getCollectionKey();
		String collectionFormat = collectionInfo.getFormatToIndex();
		String ncsRecordXml = collectionInfo.getNcsRecordXml();
		String collectionName = collectionInfo.getCollectionName();
		String collectionDescription = collectionInfo.getCollectionDescription();

		// If unable to obtain name or description from the NCS collection record, use default values:
		if (collectionName.trim().length() == 0)
			collectionName = "Name not available from NCS record " + collectionKey;
		if (collectionDescription.trim().length() == 0)
			collectionDescription = "Description not available from NCS record " + collectionKey;

		prtln("doPutCollection() collectionKey:" + collectionKey + " collectionFormat: " + collectionFormat);
		//if(!collectionIndexer.isCollectionConfigured(collectionKey))

		try {
			collectionIndexer.putCollection(collectionKey, collectionFormat, collectionName, collectionDescription, ncsRecordXml);
		} catch (PutCollectionException pce) {
			if (pce.getErrorCode().equals(PutCollectionException.ERROR_CODE_COLLECTION_EXISTS_IN_ANOTHER_FORMAT)) {
				printStatusMessage("Collection '" + collectionName + "' is in the repository but in a different format. Deleting...");
				collectionIndexer.deleteCollection(collectionKey);
				printStatusMessage("Adding collection '" + collectionName + " with the new format (key:" + collectionKey + " new format:" + collectionFormat + ")");
				collectionIndexer.putCollection(collectionKey, collectionFormat, collectionName, collectionDescription, ncsRecordXml);
			}
		}
	}


	private boolean printOutput = true;


	private void indexItemsInCollection(CollectionInfo collectionInfo) throws Exception {
		if (abortIndexing)
			return;

		String collectionKey = collectionInfo.getCollectionKey();
		String collectionFormat = collectionInfo.getFormatToIndex();
		String additionalMetadata = collectionInfo.getNcsRecordXml();
		String collectionName = collectionInfo.getCollectionName();
		String collectionDescription = collectionInfo.getCollectionDescription();

		if (!collectionIndexer.isCollectionConfigured(collectionKey))
			collectionIndexer.putCollection(collectionKey, collectionFormat, collectionName, collectionDescription, additionalMetadata);

		if (abortIndexing)
			return;

		// Index the items:
		doIndexItemsInCollection(collectionInfo);
	}



	/**
	 *  Index the itemRecords for a given collection.
	 *
	 * @param  collectionInfo  The collection to index
	 * @exception  Exception   If error
	 */
	public void doIndexItemsInCollection(CollectionInfo collectionInfo) throws Exception {
		if (abortIndexing)
			return;

		String collectionKey = collectionInfo.getCollectionKey();
		String formatToIndex = collectionInfo.getFormatToIndex();
		String collectionName = collectionInfo.getCollectionName();
		String collectionDirectoryName = collectionInfo.getCollectionDirectory();
		String larStatus = collectionInfo.getLarStatus();
		boolean isRecastAsLarRelatedMetadataCollection = collectionInfo.isRecastAsLarRelatedMetadataCollection();

		// For messages...
		String replaceStr = " ";

		CollectionIndexingSession collectionIndexingSession = collectionIndexer.getNewCollectionIndexingSession(collectionKey);

		printStatusMessage("Fetching list of records for collection '" + collectionInfo.getCollectionName() + "'");

		int numRecords = 0;
		int numSuccess = 0;
		int numErrors = 0;
		int numProcessed = 0;
		int numSkippedNonFatal = 0;
		int numSkippedRequiredXpath = 0;
		int numMissingResourceHandle = 0;

		BasicDataSource dataSource = setupDataSource();

		Connection con = null;
		PreparedStatement pstMetadata = null;
		ResultSet metadataRecordResults = null;

		PreparedStatement pstResourceHandle = null;
		ResultSet resourceHandleResults = null;
		try {
			con = dataSource.getConnection();

			String sessionid = null;
			String setSpec = collectionKey.replace("-lar", "");

			PreparedStatement pstSessionId = null;
			ResultSet sessionIdResult = null;
			try {
				pstSessionId = con.prepareStatement("SELECT sessionid FROM session WHERE setspec='" + setSpec + "'");
				sessionIdResult = pstSessionId.executeQuery();
				boolean hasData = sessionIdResult.first();
				if (hasData) {
					sessionid = sessionIdResult.getString("sessionid");
				}
				else {
					printStatusMessageWarning("Collection '" + collectionInfo.getCollectionName() + "' (setSpec '" + setSpec + ")' has no active sessions in the NR database.");
				}
			} catch (SQLException sq1) {
				printStatusMessageError("Error fetching sessionId from DB: '" + sq1);
			} finally {
				if (pstSessionId != null)
					pstSessionId.close();
				if (sessionIdResult != null)
					sessionIdResult.close();
			}

			// Count the number of records in this collection:
			if (sessionid != null) {
				PreparedStatement pstRecordCount = null;
				ResultSet recordCountResult = null;
				try {
					pstRecordCount = con.prepareStatement("SELECT count(*) FROM metadata WHERE setspec='" + setSpec + "' AND sessionid='" + sessionid + "'");
					recordCountResult = pstRecordCount.executeQuery();
					recordCountResult.first();
					numRecords = recordCountResult.getInt(1);
				} catch (SQLException sq2) {
					printStatusMessageError("Error fetching record count from DB: '" + sq2);
				} finally {
					if (pstRecordCount != null)
						pstRecordCount.close();
					if (recordCountResult != null)
						recordCountResult.close();
				}
			}

			if (numRecords == 0)
				printStatusMessageWarning("Collection '" + collectionInfo.getCollectionName() + "' (setSpec '" + setSpec + "' has 0 records.");

			// Allow zero records in collections (otherwise fatal error)?:
			if (!allowZeroRecordsInCollections.equals("true")) {
				if (numRecords == 0)
					throw new Exception("There were 0 records available in the NR DB for collection '" + collectionInfo.getCollectionName() + "' (collectionKey/setSpec '" + collectionKey + "' has 0 records.");
			}

			String numMsg = null;
			if (numRecords > MAX_RECORDS_PER_COLLECTION)
				numMsg = MAX_RECORDS_PER_COLLECTION + " of " + numRecords;
			else
				numMsg = Integer.toString(numRecords);

			printStatusMessage("Indexing " + numMsg + " records for collection '" + collectionInfo.getCollectionName() + "' (setSpec'" + collectionKey + "' sessionid='" + sessionid + "')");

			prtln("\n------------\nthere are " + numRecords + " metadata records");

			int messagesFrequency = 100;
			// How often to display status message of progress (num records)

			Throwable errorThrown = null;

			String metadataBlobToFetch = "target_xml";
			if (formatToIndex.equals("lar"))
				metadataBlobToFetch = "native_xml";

			// Fetch all records for this collection in the active session:
			if (numRecords > 0) {
				String sqlQueryString = "SELECT metadatahandle, partnerid, nativeformat, targetFormat, " + metadataBlobToFetch + " FROM metadata WHERE setspec='" + setSpec + "' AND sessionid='" + sessionid + "' LIMIT " + MAX_RECORDS_PER_COLLECTION;
				prtln("Fetching records from DB using query '" + sqlQueryString + "'");
				pstMetadata = con.prepareStatement(sqlQueryString);
				metadataRecordResults = pstMetadata.executeQuery();
			}

			// Select the resource handle for each metadata record (statement is reused for each metadata record):
			pstResourceHandle = con.prepareStatement("SELECT resourcehandle FROM resource WHERE metadatahandle=? AND sessionid='" + sessionid + "'");

			int count = 0;
			while (numRecords > 0 && metadataRecordResults.next() && !abortIndexing && numProcessed < MAX_RECORDS_PER_COLLECTION) {
				String metadataHandle = null;
				String partnerId = null;
				Document itemRecord = null;
				//Document metaMataRecord = null;
				try {
					//metaMataRecord = Dom4jUtils.getXmlDocument(new File(metadataItemDirs[j], "meta_meta.xml"), "UTF-8");

					metadataHandle = metadataRecordResults.getString("metadatahandle");

					prtln("Processing for handle " + metadataHandle + " starting... format: " + formatToIndex);

					boolean skipProcessingRecord = false;

					// All records should be processed (but if we need to skip some, here would be how)
					if (!skipProcessingRecord) {

						// Fetch the resource handle(s) for nsdl_dc and lar (used below)...
						List resourceHandles = null;
						if (formatToIndex.equals("nsdl_dc") || formatToIndex.equals("lar")) {
							resourceHandles = new ArrayList();
							pstResourceHandle.setString(1, metadataHandle);
							resourceHandleResults = pstResourceHandle.executeQuery();
							while (resourceHandleResults.next())
								resourceHandles.add(resourceHandleResults.getString("resourcehandle"));
							resourceHandleResults.close();

							if (resourceHandles.size() == 0) {
								numMissingResourceHandle++;
								printStatusMessageWarning("metadataHandle '" + metadataHandle + "' has 0 resource handles (using query='" + pstResourceHandle + "')");
							}
						}

						partnerId = metadataRecordResults.getString("partnerid");
						String itemRecordXmlString = null;
						Blob contentBlob = metadataRecordResults.getBlob(metadataBlobToFetch);

						itemRecordXmlString = StringUtils.newStringUtf8(contentBlob.getBytes(1, (int) contentBlob.length()));
						if(itemRecordXmlString == null || itemRecordXmlString.trim().length() == 0)
							throw new Exception("Record XML is empty for partner ID '" + partnerId + "', metadataHandle '" + metadataHandle + "'");
						
						
						itemRecord = Dom4jUtils.getXmlDocument(itemRecordXmlString);
						
						// If this is a cast to LAR we need to give it a unique recordID by appending -LAR in front of it:
						// This is the one piece of nomalization that has to be done here since this only happens
						// if its a recast
						if (formatToIndex.equals("lar") && isRecastAsLarRelatedMetadataCollection) {
							Element recordIDNode = (Element) itemRecord.selectSingleNode("/*[local-name()='record']/*[local-name()='recordID']");
							
							if (recordIDNode != null) {
								String newId = recordIDNode.getTextTrim() + "-LAR";
								recordIDNode.setText(newId);
							}
							else {
								throw new Exception("Unable to rewrite LAR recordID to append '-LAR' to the end. No recordID found at xPath'");
							}
						}


						prtln("Handle: " + metadataHandle + " partnerId: " + partnerId);

						// Generate dup resource URLs if directed for testing:
						if (generateDupResourceUrls && (count == 0 || count == 2) && !metadataHandle.equals("2200/20100409094800622T")) {
							Node identifierNode = itemRecord.selectSingleNode("/nsdl_dc:nsdl_dc/dc:identifier");
							if (identifierNode != null) {
								String testUrl = "http://www.test.edu/test_duplicate_url_" + count + ".html";
								prtln("Setting test URL to " + testUrl);
								identifierNode.setText(testUrl);
							}
							Node titleNode = itemRecord.selectSingleNode("/nsdl_dc:nsdl_dc/dc:title");
							if (titleNode != null) {
								String testTitle = "Dup " + count + " from '" + collectionInfo.getCollectionName() + "'";
								titleNode.setText(testTitle);
							}
						}

						//String ddsId = mdReader.getRecordId();
						String ddsId = metadataHandle;
						//if (ddsId == null)
						//ddsId = metadataHandle;

						//prtln("Put record for collection:" + collectionKey + " format:" + formatToIndex + " no: " + (++count) + " id:" + ddsId);
						collectionIndexer.putRecord(Dom4jUtils.prettyPrint(itemRecord), collectionKey, ddsId, collectionIndexingSession);
						numSuccess++;
						numProcessed++;
					}
					// If status not done:
					else {
						String msg = "Record " + metadataHandle + " was skipped for non-critical reason...";
						prtln(msg);
						numSkippedNonFatal++;
						numProcessed++;
					}
				} catch (Exception e) {
					errorThrown = e;
					prtlnErr("could not get itemRecord for " + metadataHandle + ": " + e);
					numErrors++;
					numProcessed++;
				} finally {
					prtln("Processing for handle " + metadataHandle + " completed...\n\n");
				}

				if (numProcessed > 0 && numProcessed % messagesFrequency == 0) {
					String msg = "Processed " + numProcessed +
						" of " + numRecords +
						" records for collection '" + collectionInfo.getCollectionName() +
						"' thus far (" + numSuccess + " successful, " + numErrors + " errors, " + numSkippedNonFatal + " skipped for non-critical reason, " + numSkippedRequiredXpath + " skipped missing required XPath)";
					if (errorThrown != null) {
						msg += ". One or more records could not be indexed because of errors. Most recent error message: " + errorThrown;
						printStatusMessageError(msg);
					}
					else {
						printStatusMessage(msg);
					}
				}
			}

			String msg = "Finished indexing collection '" + collectionInfo.getCollectionName() + "'. Processed " + numProcessed + " records (" + numSuccess + " successful, " + numErrors + " errors, " + numSkippedNonFatal + " skipped for non-critical reason, " + numSkippedRequiredXpath + " skipped missing required XPath, " + numMissingResourceHandle + " were missing resource handles in the DB.)";
			if (errorThrown != null) {
				msg += ". One or more records could not be indexed because of errors. Most recent error message: " + errorThrown;
				printStatusMessageError(msg);
			}
			else {
				printStatusMessage(msg);
			}

			if (numSkippedRequiredXpath > 0) {
				msg = "WARNING-TOTAL: Found a total of " + numSkippedRequiredXpath + " records MissingRequiredXpath, which were omitted from index for: " + collectionName.replaceAll(",", replaceStr) + "," + collectionKey.replaceAll(",", replaceStr);
				printStatusMessageWarning(msg);
			}

			if (numMissingResourceHandle > 0) {
				msg = "WARNING-TOTAL: Found a total of " + numMissingResourceHandle + " records MissingResourceHandleFromDB, which were inserted in the index for: " + collectionName.replaceAll(",", replaceStr) + "," + collectionKey.replaceAll(",", replaceStr);
				printStatusMessageWarning(msg);
			}
		} catch (SQLException ex) {
			String msg = "Unable to connect to NR database: " + ex;
			printStatusMessageError(msg);
			ex.printStackTrace();
			throw new Exception(msg);
		} finally {

			try {
				if (metadataRecordResults != null) {
					metadataRecordResults.close();
				}
				if (resourceHandleResults != null) {
					resourceHandleResults.close();
				}
				if (pstMetadata != null) {
					pstMetadata.close();
				}
				if (pstResourceHandle != null) {
					pstResourceHandle.close();
				}
				if (con != null) {
					con.close();
				}
				if (dataSource != null) {
					dataSource.close();
				}
			} catch (SQLException ex) {
				String msg = "Unable to close NR database object(s): " + ex;
				printStatusMessageError(msg);
				throw new Exception(msg);
			}
		}

		if (abortIndexing)
			return;

		// If error rate is too high, don't continue. Throw exception to stop processing and prevent index commit:
		float errorPercentage = 0f;
		if (numProcessed > 0f)
			errorPercentage = numErrors / numProcessed;

		if (errorPercentage > maxIndexingErrorRate)
			throw new Exception("Collection '" + collectionInfo.getCollectionName() + "' had " + (errorPercentage * 100) + "% error rate (above allowed limit, no deletions or index commits will be made).");

		String msg = "Deleting records from previous sessions for '" + collectionInfo.getCollectionName() + "'.";
		printStatusMessage(msg);
		collectionIndexer.deletePreviousSessionRecords(collectionIndexingSession);
	}


	private IndexingStatusBean getIndexingStatusBean() {
		IndexingStatusBean indexingStatusBean = null;

		try {
			indexingStatusBean = (IndexingStatusBean) Beans.file2Bean(new File(myPersistanceDir, "NDRIndexingStatusBean.xml"));
		} catch (java.io.FileNotFoundException fe) {
			indexingStatusBean = new IndexingStatusBean();
		} catch (Throwable t) {
			prtlnErr("Unable to fetch IndexingStatusBean: " + t);
			t.printStackTrace();
		}

		if (indexingStatusBean == null)
			indexingStatusBean = new IndexingStatusBean();
		return indexingStatusBean;
	}


	private void saveIndexingStatusBean(IndexingStatusBean indexingStatusBean) {
		try {
			Beans.bean2File(indexingStatusBean, new File(myPersistanceDir, "NDRIndexingStatusBean.xml"));
		} catch (Throwable t) {
			prtlnErr("Unable to save IndexingStatusBean: " + t);
			t.printStackTrace();
		}
	}


	/**
	 *  Fetches collections from the NCS to determine which ones to index.
	 *
	 * @return                List of CollectionInfo Objects for each collection.
	 * @exception  Exception  If error
	 */
	private List getCollectionsFromRemoteNCS() throws Exception {
		int timoutMs = 6000;
		int s = 0;
		int n = 1000;
		// Number of collection records to fetch per loop

		prtln("updating collections from NCS");

		ArrayList collectionInfos = new ArrayList();
		if (configXmlDoc == null)
			throw new Exception("Unable to configure NR collections. No config file found at " + configFile);

		String ncsSearchApiBaseUrl = configXmlDoc.valueOf("/NR_Collections/ncsManagedCollections/ncsSearchApiBaseUrl");
		if (ncsSearchApiBaseUrl == null || ncsSearchApiBaseUrl.trim().length() == 0)
			return collectionInfos;

		// Filter by DCS status as indicated:
		List nodes = configXmlDoc.selectNodes("/NR_Collections/ncsManagedCollections/ncsCollectionStatusFilter/ncsCollectionStatus");
		String nscStatusParams = "";
		// Construct status flag:
		for (int i = 0; i < nodes.size(); i++) {
			try {
				String status = (String) ((Node) nodes.get(i)).getText();
				if (status != null && status.length() > 0)
					nscStatusParams += "&dcsStatus=" + URLEncoder.encode(status, "utf-8");
			} catch (UnsupportedEncodingException e) {
				prtlnErr("Error encoding dcsStatus: " + e);
			}
		}

		String searchQueryConstraint = configXmlDoc.valueOf("/NR_Collections/ncsManagedCollections/searchQueryConstraint");
		try {
			if (searchQueryConstraint != null && searchQueryConstraint.trim().length() > 0)
				searchQueryConstraint = "&q=" + URLEncoder.encode(searchQueryConstraint, "utf-8");
			else
				searchQueryConstraint = "";
		} catch (UnsupportedEncodingException e) {
			prtlnErr("Error encoding searchQueryConstraint: " + e);
		}

		// Loop until all collection records have been fetched from NCS, and build a DOM:
		String serviceRequestUrl = null;
		for (int i = 0; i < 200; i++) {

			// Create a service request for the first/next segment:
			serviceRequestUrl = ncsSearchApiBaseUrl + "?verb=Search&ky=1201216476279&xmlFormat=ncs_collect" + nscStatusParams + "&n=" + n + "&s=" + s + searchQueryConstraint;

			prtln("Fetching collections from NCS. Request: " + serviceRequestUrl);
			//Document fetched = Dom4jUtils.getXmlDocument(Dom4jUtils.localizeXml(TimedURLConnection.importURL(serviceRequestUrl, timoutMs)));
			Document fetched = Dom4jUtils.getXmlDocument(Dom4jUtils.localizeXml(TimedURLConnection.importURL(serviceRequestUrl, "UTF-8", timoutMs)));
			String numResults = fetched.valueOf("/DDSWebService/Search/resultInfo/totalNumResults");
			String numReturned = fetched.valueOf("/DDSWebService/Search/resultInfo/numReturned");
			String offset = fetched.valueOf("/DDSWebService/Search/resultInfo/offset");
			String ddsErrorCode = fetched.valueOf("/DDSWebService/error/@code");

			if (ddsErrorCode != null && ddsErrorCode.equals("noRecordsMatch")) {
				prtln("No matching collections found in the NCS (noRecordsMatch). Request made was: " + serviceRequestUrl);
				break;
			}

			if (numResults == null || numResults.trim().length() == 0 || numResults.equals("0"))
				throw new Exception("No collections records available from NCS (numResults element was missing or empty... must be an error). Request made was: " + serviceRequestUrl);

			else {
				//List metadataProviderHandles = fetched.selectNodes("/DDSWebService/Search/results/record/head/additionalMetadata/ndr_info/metadataProviderHandle");
				//prtln("Fetch num recordNodes:" + recordNodes.size());

				List ncsCollectRecords = fetched.selectNodes("/DDSWebService/Search/results/record");

				if (ncsCollectRecords == null || ncsCollectRecords.size() == 0)
					break;

				Map foundSetSpecs = new HashMap();

				// Add all Collections:
				for (int j = 0; j < ncsCollectRecords.size() && collectionInfos.size() < MAX_NUM_COLLECTIONS; j++) {
					Element fullNcsRecord = (Element) ((Element) ncsCollectRecords.get(j)).clone();
					String collectionId = fullNcsRecord.valueOf("head/id");

					// Check for the existance of the collection handle and report if not found (handle is not needed for indexing):
					String collectionHandle = fullNcsRecord.valueOf("head/additionalMetadata/collection_info/metadataHandle");
					if (collectionHandle == null || collectionHandle.trim().length() == 0) {
						String setSpec = fullNcsRecord.valueOf("metadata/record/collection/setSpec");
						String collectionName = fullNcsRecord.valueOf("metadata/record/general/title");
						prtlnErr("Warning: collection handle is missing for '" + collectionName + "' (ID:" + collectionId + " setSpec:" + setSpec + ").");
					}

					// Error checking for setSpec values:
					String setSpec = fullNcsRecord.valueOf("metadata/record/collection/setSpec");
					String collectionName = fullNcsRecord.valueOf("metadata/record/general/title");

					// Ensure the setSpec is not empty:
					if (setSpec == null || setSpec.trim().length() == 0)
						throw new Exception("The setSpec for '" + collectionName + "' (collectionId:" + collectionId + ") is empty. Please edit the NCS collection record(s) to fix this error.");

					// Ensure each setSpec is unique across all collection records:
					if (foundSetSpecs.containsKey(setSpec))
						throw new Exception("Duplicate setSpec found '" + setSpec + "' in collection IDs '" + foundSetSpecs.get(setSpec) + "' and '" + collectionId + "'. Please edit the NCS collection record(s) to fix this error.");
					else
						foundSetSpecs.put(setSpec, collectionId);

					Document ncsCollectionRecord = DocumentHelper.createDocument();
					Element root = ncsCollectionRecord.addElement("ncsCollectionRecord");
					root.appendContent(fullNcsRecord);

					//String ncsRecordXml = Dom4jUtils.prettyPrint(ncsCollectionRecord);
					//ncsRecordXml =  null;

					if (collectionId != null && collectionId.trim().length() > 0) {
						boolean isRecastAsLarRelatedMetadataCollection = false;
						CollectionInfo collectionInfo = new CollectionInfo(collectionId.trim(), ncsCollectionRecord, isRecastAsLarRelatedMetadataCollection);

						// Make a separate NSDL_DC collection for LAR, if configured:
						boolean makeNsdlDcCollectionForLar = false;
						String makeNsdlDcCollectionForLarSetting = configXmlDoc.valueOf("/NR_Collections/ncsManagedCollections/makeNsdlDcCollectionForLar");
						if (makeNsdlDcCollectionForLarSetting != null && makeNsdlDcCollectionForLarSetting.equals("true"))
							makeNsdlDcCollectionForLar = true;

						// Special handling for LAR collecitons: Index them as NSDL_DC and LAR for Search API, just LAR for OAI:
						if (makeNsdlDcCollectionForLar && collectionInfo.getLibraryFormat().equals("lar")) {
							// Change the primary colleciton to nsdl_dc:
							collectionInfo.setLibraryFormat("nsdl_dc");

							// Create a second parallel colleciton to pull in the LAR metadata:
							Document ncsCollectionRecord2 = DocumentHelper.createDocument();
							Element root2 = ncsCollectionRecord2.addElement("ncsCollectionRecord");
							root2.appendContent((Element) fullNcsRecord.clone());

							// Create the LAR collectionInfo: Note that the CollectionInfo class has special processing for LAR collections:
							isRecastAsLarRelatedMetadataCollection = true;
							CollectionInfo collectionInfo2 = new CollectionInfo(collectionId.trim(), ncsCollectionRecord2, isRecastAsLarRelatedMetadataCollection);
							collectionInfos.add(collectionInfo2);
						}
						collectionInfos.add(collectionInfo);
					}
					else {
						throw new Exception("Collection ID is missing for '" + collectionName + "' (setSpec:" + setSpec + "). Unable to proceed.");
					}
				}
			}

			if (numReturned == null || numReturned.equals("0"))
				break;
			s += n;
		}

		//prtln("getCollectionsFromRemoteNCS() returning: " + Arrays.toString(collectionInfos.toArray()));
		return collectionInfos;
	}


	/**
	 *  Gets the format type, one of 'canonical_nsdl_dc' or 'native'.
	 *
	 * @return    The formatTypeToIndex value
	 */
	private String getFormatTypeToIndex() {
		// Format type 'canonical_nsdl_dc' or 'native'
		String formatType = configXmlDoc.valueOf("/NR_Collections/ncsManagedCollections/formatType");
		if (formatType == null || formatType.trim().length() == 0 || !(formatType.equalsIgnoreCase("canonical_nsdl_dc") || formatType.equalsIgnoreCase("native")))
			formatType = "canonical_nsdl_dc";
		return formatType;
	}


	private PropertiesManager getProperties(File configDir) throws Exception {
		// First look in the configuration directory...
		if (configDir != null && configDir.isDirectory())
			return new PropertiesManager(new File(configDir, "edu.ucar.dls.dds.ndr.NRIndexer.properties").toString());
		// Then look in the classpath...
		else
			return new PropertiesManager("edu.ucar.dls.dds.ndr.NRIndexer.properties");
	}


	private void printStatusMessage(String msg) {
		collectionIndexer.printStatusMessage("NRIndexer: " + msg, CollectionIndexer.MSG_TYPE_INFO);
		prtln(msg);
	}


	private void printStatusMessageWarning(String msg) {
		collectionIndexer.printStatusMessage("NRIndexer: " + msg, CollectionIndexer.MSG_TYPE_WARNING);
		prtlnWarning(msg);
	}


	private void printStatusMessageError(String msg) {
		collectionIndexer.printStatusMessage("NRIndexer: " + msg, CollectionIndexer.MSG_TYPE_ERROR);
		prtlnErr(msg);
	}


	private class CollectionInfo {
		private String _collection_id = null;
		private Document _ncsRecordDoc = null;
		private String _format_to_index = null;
		private String _library_format = null;
		private String _collection_name = null;
		private String _collection_key = null;
		private String _collection_directory = null;
		private boolean _recast_as_lar_related_metadata_collection = false;


		private CollectionInfo(String id, Document ncsRecordDoc, boolean isRecastAsLarRelatedMetadataCollection) {
			_ncsRecordDoc = ncsRecordDoc;
			_recast_as_lar_related_metadata_collection = isRecastAsLarRelatedMetadataCollection;
			if (isRecastAsLarRelatedMetadataCollection())
				_collection_id = id + "-LAR";
			else
				_collection_id = id;
		}


		/**
		 *  Gets the collectionName attribute of the CollectionInfo object
		 *
		 * @return    The collectionName value
		 */
		public String getCollectionName() {
			if (_collection_name == null) {
				_collection_name = _ncsRecordDoc.valueOf("/ncsCollectionRecord/metadata/record/general/title");
				if (isRecastAsLarRelatedMetadataCollection())
					_collection_name += " (LAR metadata)";

			}
			return _collection_name;
		}


		/**
		 *  Gets the collectionDescription attribute of the CollectionInfo object
		 *
		 * @return    The collectionDescription value
		 */
		public String getCollectionDescription() {
			return _ncsRecordDoc.valueOf("/ncsCollectionRecord/metadata/record/general/description");
		}


		/**
		 *  Gets the id attribute of the CollectionInfo object
		 *
		 * @return    The id value
		 */
		public String getId() {
			return _collection_id;
		}


		/**
		 *  Gets the LAR status for this collection as indicated in the collection record.
		 *
		 * @return    The LAR status
		 */
		public String getLarStatus() {
			return _ncsRecordDoc.valueOf("/ncsCollectionRecord/metadata/record/collection/LAR");
		}


		/**
		 *  True if this collection should be recast as a lar related metadata collection.
		 *
		 * @return    The larRelatedMetadataCollection value
		 */
		public boolean isRecastAsLarRelatedMetadataCollection() {
			return (getLibraryFormat().equals("lar") && _recast_as_lar_related_metadata_collection);
		}


		/**
		 *  Gets the ncsRecordXml attribute of the CollectionInfo object
		 *
		 * @return    The ncsRecordXml value
		 */
		public String getNcsRecordXml() {
			return Dom4jUtils.prettyPrint(_ncsRecordDoc);
		}


		/**
		 *  Gets the ncsRecordDoc attribute of the CollectionInfo object
		 *
		 * @return    The ncsRecordDoc value
		 */
		public Document getNcsRecordDoc() {
			return _ncsRecordDoc;
		}


		/**
		 *  Gets the collectionKey (setSpec) for the collection.
		 *
		 * @return    The collectionKey value
		 */
		public String getCollectionKey() {
			if (_collection_key == null) {
				_collection_key = getSetSpec();

				if (isRecastAsLarRelatedMetadataCollection())
					_collection_key += "-lar";
			}
			return _collection_key;
		}


		/**
		 *  Gets the directory name where the collection's file reside, which is the same as the setSpec.
		 *
		 * @return    The collectionDirectory value
		 */
		public String getCollectionDirectory() {
			if (_collection_directory == null) {
				_collection_directory = getSetSpec();
			}
			return _collection_directory;
		}


		/**
		 *  Gets the setSpec from the NCS collection record.
		 *
		 * @return    The setSpec value
		 */
		private String getSetSpec() {
			return _ncsRecordDoc.valueOf("/ncsCollectionRecord/metadata/record/collection/setSpec");
		}


		/**
		 *  Gets the library format for this collection.
		 *
		 * @return    The library format, for example 'nsdl_dc' or 'comm_anno'
		 */
		public String getLibraryFormat() {
			if (_library_format == null) {
				_library_format = _ncsRecordDoc.valueOf("/ncsCollectionRecord/metadata/record/collection/ingest/oai/@libraryFormat");
				if (_library_format == null || _library_format.trim().length() == 0)
					_library_format = _ncsRecordDoc.valueOf("/ncsCollectionRecord/metadata/record/collection/ingest/ncs/@libraryFormat");
			}
			return _library_format;
		}


		/**
		 *  Sets the library format for this collection. The library format, for example 'nsdl_dc' or 'comm_anno'
		 *
		 * @param  xmlFormat  The new libraryFormat value
		 */
		public void setLibraryFormat(String xmlFormat) {
			_library_format = xmlFormat;
			_format_to_index = null;
			// Do we need/want to change the collection record too or just leave it?

			/* if (_library_format == null) {
				_library_format = _ncsRecordDoc.valueOf("/ncsCollectionRecord/metadata/record/collection/ingest/oai/@libraryFormat");
				if (_library_format == null || _library_format.trim().length() == 0)
					_library_format = _ncsRecordDoc.valueOf("/ncsCollectionRecord/metadata/record/collection/ingest/ncs/@libraryFormat");
			}
			return _library_format; */
		}


		/**
		 *  Gets the format that should be indexed for this collection, for example 'nsdl_dc' or 'comm_anno'.
		 *
		 * @return    The formatToIndex value
		 */
		public String getFormatToIndex() {
			if (_format_to_index == null) {
				// Format type 'canonical_nsdl_dc' or 'native'
				String formatType = getFormatTypeToIndex();

				// List of related formats to include with canonical_nsdl_dc:
				List relatedCanonicalNsdlDcFormatsVals = configXmlDoc.selectNodes("/NR_Collections/ncsManagedCollections/canonicalNsdlDcRelatedFormats/relatedFormat");
				Map relatedCanonicalNsdlDcFormats = new HashMap();
				for (int i = 0; i < relatedCanonicalNsdlDcFormatsVals.size(); i++)
					relatedCanonicalNsdlDcFormats.put(((Node) relatedCanonicalNsdlDcFormatsVals.get(i)).getText(), new Object());

				_format_to_index = "nsdl_dc";
				String libraryFormat = getLibraryFormat();

				// Handle native:
				if (formatType.equalsIgnoreCase("native")) {
					_format_to_index = libraryFormat;
				}
				// Handle canonical_nsdl_dc:
				else {
					if (relatedCanonicalNsdlDcFormats.containsKey(libraryFormat))
						_format_to_index = libraryFormat;
				}
			}
			return _format_to_index;
		}


		/**
		 *  Equal if the handles are not null and are the same.
		 *
		 * @param  obj  The other Object
		 * @return      True if the handles are the same
		 */
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			CollectionInfo other = (CollectionInfo) obj;
			if (this.getId() == null || other.getId() == null)
				return false;
			return this.getId().equals(other.getId());
		}
	}


	private boolean isEmpty(String s) {
		return (s == null || s.trim().length() == 0);
	}


	/* ---------------------- Debug methods ----------------------- */
	/**
	 *  Return a string for the current time and date, sutiable for display in log files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	public static String getDateStamp() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final static void prtlnErr(String s) {
		System.err.println(getDateStamp() + " NRIndexer Error: " + s);
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final static void prtlnWarning(String s) {
		System.err.println(getDateStamp() + " NRIndexer Warning: " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final static void prtln(String s) {
		if (debug) {
			System.out.println(getDateStamp() + " NRIndexer: " + s);
		}
	}


	/**
	 *  Sets the debug attribute of the NRIndexer object
	 *
	 * @param  db  The new debug value
	 */
	public final void setDebug(boolean db) {
		debug = db;
	}
}


