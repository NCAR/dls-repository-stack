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
package edu.ucar.dls.dds.nfr;

import edu.ucar.dls.repository.indexing.*;

import java.io.*;
import java.util.*;
import java.net.URL;

import edu.ucar.dls.index.*;
import edu.ucar.dls.index.writer.*;
import java.text.*;

/* import edu.ucar.dls.ndr.*;
import edu.ucar.dls.ndr.reader.*;
import edu.ucar.dls.ndr.request.*;
import edu.ucar.dls.ndr.apiproxy.*;
import edu.ucar.dls.ndr.apiproxy.InfoXML; */
import edu.ucar.dls.propertiesmgr.PropertiesManager;
import edu.ucar.dls.util.TimedURLConnection;
import edu.ucar.dls.repository.PutCollectionException;
import edu.ucar.dls.xml.NSDLDCNormalizer;
import edu.ucar.dls.xml.NSDLDCNormalizerXpathException;
import edu.ucar.dls.util.*;

import org.dom4j.*;
import edu.ucar.dls.xml.Dom4jUtils;
import org.apache.lucene.document.Field;
import java.net.URLEncoder;

/**
 *  Indexes and configures collections from the NSDL File Repository (NFR).
 *
 * @author    John Weatherley
 */
public class NFRIndexer implements ItemIndexer {
	private static boolean debug = true;

	private int MAX_NUM_COLLECTIONS = 40000;
	// The max number of collections DDS will suck in from NFR (limit for testing...)
	private int MAX_RECORDS_PER_COLLECTION = 900000;
	// The max number of records DDS will suck in from NFR per collection (limit for testing...)
	private boolean generateDupResourceUrls = false;
	// Generate duplicate resource URLs for testing?

	private float maxIndexingErrorRate = 0.10f;
	// The maximum number or indexing errors per individual collection allowed. If higher, deletions and index commits will not be processed.

	private CollectionIndexer collectionIndexer = null;
	private boolean abortIndexing = false;
	private boolean indexingIsActive = false;
	private File configFile = null;
	private File myPersistanceDir = null;
	private Document configXmlDoc = null;
	private String nsdlDcVocabSelections = null;
	// URL to the NSDL DC normalizer vocab groups def files
	private String writeVerboseComments = null;
	private String allowZeroRecordsInCollections = "true";

	private File nfrBaseDirFile = null;


	// Should verbose comments be written into the normalized metadata?

	/**  Constructor for the NFRIndexer object */
	public NFRIndexer() { }


	/**
	 *  Sets the configDirectory attribute of the NFRIndexer object
	 *
	 * @param  configDir       The new configDirectory value
	 * @param  persistanceDir  The new configDirectory value
	 * @exception  Exception   If error
	 */
	public void setConfigDirectory(File configDir, File persistanceDir) throws Exception {
		if (configDir == null)
			throw new Exception("The configDir is null");
		this.configFile = new File(configDir, "NFRIndexer_conf.xml");
		myPersistanceDir = new File(persistanceDir, "NFRIndexer");
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
				String msg = "Unable to read the NFR configuration file '" + configFile + "'. Message: " + t.getMessage();
				prtlnErr(msg);
				throw new Exception(msg);
			}

			// Set up the NFRIndexer to index NFR collections:
			String nfrBaseDir = configXmlDoc.valueOf("/NFR_Collections/nfrBaseDir");
			if (nfrBaseDir == null || nfrBaseDir.length() == 0) {
				String msg = "Unable to configure NFR collections. Missing required config element: 'nfrBaseDir'";
				printStatusMessageError(msg);
				throw new Exception(msg);
			}

			// Allow zero records in collections (otherwise fatal error)?:
			allowZeroRecordsInCollections = configXmlDoc.valueOf("/NFR_Collections/allowZeroRecordsInCollections");
			if (allowZeroRecordsInCollections == null)
				allowZeroRecordsInCollections = "true";

			nfrBaseDirFile = new File(nfrBaseDir);
			if (!nfrBaseDirFile.exists()) {
				String msg = "The NFR base directory '" + nfrBaseDir + "' does not exist.";
				printStatusMessageError(msg);
				throw new Exception(msg);
			}
			if (!nfrBaseDirFile.canRead()) {
				String msg = "The NFR base directory '" + nfrBaseDir + "' does not have read access.";
				printStatusMessageError(msg);
				throw new Exception(msg);
			}

			// Set up some values for testing stuff:
			try {
				MAX_NUM_COLLECTIONS = Integer.parseInt(configXmlDoc.valueOf("/NFR_Collections/maxNumCollections"));
			} catch (Throwable t) {}

			try {
				MAX_RECORDS_PER_COLLECTION = Integer.parseInt(configXmlDoc.valueOf("/NFR_Collections/maxNumRecordsPerCollection"));
			} catch (Throwable t) {}

			String generateDupResourceUrls = configXmlDoc.valueOf("/NFR_Collections/generateDupResourceUrls");
			if (generateDupResourceUrls != null && generateDupResourceUrls.equals("true"))
				this.generateDupResourceUrls = true;

			// NSDL DC normilzation vocab groups definitions:
			nsdlDcVocabSelections = configXmlDoc.valueOf("/NFR_Collections/nsdlDcVocabSelections");
			writeVerboseComments = configXmlDoc.valueOf("/NFR_Collections/writeVerboseComments");
		}
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
							//prtln("NFRIndexer started BEGIN_INDEXING_ALL_COLLECTIONS." + "\n\n");
							indexCollections(null);
							break;
						}

						case IndexingEvent.INDEXER_READY:
						{
							//prtln("NFRIndexer started INDEXER_READY!" + "\n\n");
							return;
						}

						case IndexingEvent.BEGIN_INDEXING_COLLECTION:
						{
							//prtln("NFRIndexer started BEGIN_INDEXING_COLLECTION!" + "\n\n");
							String key = indexingEvent.getCollectionKey();
							if (key != null && key.trim().length() > 0)
								indexCollections(key);
							return;
						}

						case IndexingEvent.ABORT_INDEXING:
						{
							//prtln("NFRIndexer ABORT_INDEXING!" + "\n\n");
							printStatusMessage("Stopping indexing (may take a while)...");
							abortIndexing = true;
							return;
						}

						case IndexingEvent.UPDATE_COLLECTIONS:
						{
							prtln("NFRIndexer started UPDATE_COLLECTIONS!" + "\n\n");
							updateCollections();
							return;
						}
						case IndexingEvent.CONFIGURE_AND_INITIALIZE:
						{
							prtln("NFRIndexer updating configuration" + "\n\n");
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
				throw new IndexingInProgressException("NFRIndexer is already running.");
			abortIndexing = false;
			indexingIsActive = true;
		}

		try {
			// Update collections and configurations:
			updateCollections();

			prtln("NFRIndexer started." + "\n\n");
			printStatusMessage("Indexing started.");

			IndexingStatusBean indexingStatusBean = getIndexingStatusBean();

			// Initialize required NFR attributes and Sets TransformerFactory to a XSL 1.0 version:
			//NdrUtils.ndrTestSetup();

			Throwable errorThrown = null;

			// Configure all collections defined in the NCS:
			List nfrCollectionInfos = new ArrayList();

			nfrCollectionInfos = getCollectionsFromRemoteNCS();

			int numSkippedFromPreviousSession = 0;
			int numCollectionErrorsThrown = 0;
			boolean displayNumSkippedFromPreviousSessionMsg = false;

			if (!abortIndexing) {
				for (int i = 0; i < nfrCollectionInfos.size(); i++) {
					try {
						CollectionInfo collectionInfo = (CollectionInfo) nfrCollectionInfos.get(i);

						String currentCollectionKey = collectionInfo.getCollectionKey();

						// If we are indexing all collections:
						if (collectionKey == null) {
							if (!indexingStatusBean.hasCompletedCollection(currentCollectionKey)) {
								if (displayNumSkippedFromPreviousSessionMsg) {
									displayNumSkippedFromPreviousSessionMsg = false;
									printStatusMessage("Skipped " + numSkippedFromPreviousSession + " collections that were completed successfully in a previous indexing session...");
									printStatusMessage("Continuing to index " + (nfrCollectionInfos.size() - numSkippedFromPreviousSession) + " collections that were aborted or had errors...");
								}

								indexCollection(collectionInfo);

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
							indexCollection(collectionInfo);
							break;
						}

						//indexNdrCollection( currentCollection, collectionKey );

						if (abortIndexing)
							nfrCollectionInfos.clear();

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
			prtln("NFRIndexer completed." + "\n\n");
		}
	}



	/**
	 *  Update the collections that are configured in DDS.
	 *
	 * @exception  Exception  If error
	 */
	public void updateCollections() throws Exception {

		// Reload the config files:
		configureAndInitialize();

		// Start status message
		printStatusMessage("Begin adding/updating NFR collections...");

		// Add collections that I know about:
		//List ncsCollections = getNcsCollections();
		//prtln("getNcsCollections() returned: " + Arrays.toString(ncsCollections.toArray()));

		Throwable collectionError = null;
		List nfrCollections = new ArrayList();

		// Configure collections defined in the NCS:
		List nfrCollectionInfos = new ArrayList();
		try {
			nfrCollectionInfos = getCollectionsFromRemoteNCS();
		} catch (Throwable t) {
			String msg = "Error getting NFR collections from NCS: " + t.getMessage();
			printStatusMessageError(msg);
			return;
		}

		nfrCollections = new ArrayList();
		for (int i = 0; i < nfrCollectionInfos.size(); i++) {
			CollectionInfo collectionInfo = null;
			try {
				collectionInfo = (CollectionInfo) nfrCollectionInfos.get(i);
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
						nfrCollections.add(collectionKey);
						continue;
					}
				}

				// Add the collection to DDS:
				nfrCollections.add(collectionKey);
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
			if (!nfrCollections.contains(collKey) && !nfrCollectionInfos.contains(collKey)) {
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
		printStatusMessage("Finished adding/updating NFR collections.");
	}


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


	private void indexCollection(CollectionInfo collectionInfo) throws Exception {
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
		indexItemRecords(collectionInfo);
	}


	/**
	 *  Index the itemRecords for a given collection.
	 *
	 * @param  collectionInfo  The collection to index
	 * @exception  Exception   If error
	 */
	public void indexItemRecords(CollectionInfo collectionInfo) throws Exception {
		if (abortIndexing)
			return;

		String collectionKey = collectionInfo.getCollectionKey();
		String formatToIndex = collectionInfo.getFormatToIndex();
		String collectionName = collectionInfo.getCollectionName();
		String collectionDirectoryName = collectionInfo.getCollectionDirectory();
		String larStatus = collectionInfo.getLarStatus();
		boolean isRecastAsLarRelatedMetadataCollection = collectionInfo.isRecastAsLarRelatedMetadataCollection();

		CollectionIndexingSession collectionIndexingSession = collectionIndexer.getNewCollectionIndexingSession(collectionKey);

		printStatusMessage("Fetching list of record handles for collection '" + collectionInfo.getCollectionName() + "'");

		NSDLDCNormalizer nsdlDcNormalizer = null;

		String formatTypeToIndex = getFormatTypeToIndex();
		if (formatTypeToIndex.equals("canonical_nsdl_dc") && formatToIndex.equals("nsdl_dc")) {
			boolean doWriteVerboseComments = (writeVerboseComments != null && writeVerboseComments.equalsIgnoreCase("true"));

			if (nsdlDcVocabSelections != null && nsdlDcVocabSelections.trim().length() > 0)
				nsdlDcNormalizer = new NSDLDCNormalizer(nsdlDcVocabSelections.trim(), doWriteVerboseComments);
		}

		File[] metadataItemDirs = null;
		File collectionDirectory = null;

		// Check for file list multiple times (in case of Exception):
		for (int i = 0; i < 3; i++) {
			try {
				collectionDirectory = new File(nfrBaseDirFile, collectionDirectoryName);
				metadataItemDirs = collectionDirectory.listFiles();
			} catch (Exception e) {
				prtln("Error: Could not read the metadata items directory '" + collectionDirectory + "' for collection '" + collectionInfo.getCollectionName() + "': " + e);
			}
			// If we have a non-empty list of handles, continue processing:
			if (metadataItemDirs != null && metadataItemDirs.length > 0)
				break;
			Thread.sleep(200);
		}

		// Get the number of records (metadataItemDirs can be null):
		int numRecords = (metadataItemDirs == null) ? 0 : metadataItemDirs.length;

		if (numRecords == 0)
			printStatusMessageWarning("Warning: Collection '" + collectionInfo.getCollectionName() + "' has 0 records. Metadata file directory " + collectionDirectory.getAbsolutePath() + ((metadataItemDirs == null) ? " does not exist." : " exists but is empty."));

		// Allow zero records in collections (otherwise fatal error)?:
		if (!allowZeroRecordsInCollections.equals("true")) {
			if (numRecords == 0)
				throw new Exception("There were 0 records available in the file repository directory '" + collectionDirectory + "' for collection '" + collectionInfo.getCollectionName() + "' . This may be disc read error. If not, please remove the collection if it is no longer active (0 records not allowed).");
		}

		printStatusMessage("Indexing " + numRecords + " records for collection '" + collectionInfo.getCollectionName() + "'...");

		prtln("\n------------\nthere are " + numRecords + " metadata records");

		int messagesFrequency = 100;
		// How often to display status message of progress (num records)

		int numSuccess = 0;
		int numErrors = 0;
		int numProcessed = 0;
		int numSkippedNonFatal = 0;
		int numSkippedRequiredXpath = 0;

		Throwable errorThrown = null;

		int count = 0;
		for (int j = 0; j < numRecords && !abortIndexing && numProcessed < this.MAX_RECORDS_PER_COLLECTION; j++) {
			String metadataHandle = null;
			String partnerId = null;
			Document itemRecord = null;
			Document metaMataRecord = null;
			try {
				metaMataRecord = Dom4jUtils.getXmlDocument(new File(metadataItemDirs[j], "meta_meta.xml"), "UTF-8");

				metadataHandle = metaMataRecord.valueOf("/metaMetadata/metadataHandle");

				prtln("Processing for handle " + metadataHandle + " starting... format: " + formatToIndex);

				boolean skipProcessingRecord = false;
				// All records should be processed (but if we need to skip some, here would be how)
				if (!skipProcessingRecord) {

					List resourceHandles = metaMataRecord.selectNodes("string(/metaMetadata/resources/resource/@resourceHandle)");
					partnerId = metaMataRecord.valueOf("/metaMetadata/partnerId");

					// Canonical NSDL DC format:
					if (formatTypeToIndex.equals("canonical_nsdl_dc") && formatToIndex.equals("nsdl_dc")) {
						itemRecord = Dom4jUtils.getXmlDocument(new File(metadataItemDirs[j], "nsdl_dc.xml"), "UTF-8");
						if (nsdlDcNormalizer != null) {
							List requiredXpaths = new ArrayList();
							requiredXpaths.add("/nsdl_dc:nsdl_dc/dc:identifier[starts-with(.,'http://') or starts-with(.,'https://') or starts-with(.,'ftp://')]");
							requiredXpaths.add("/nsdl_dc:nsdl_dc/dc:title");
							requiredXpaths.add("/nsdl_dc:nsdl_dc/dc:description");
							itemRecord = nsdlDcNormalizer.normalizeNsdlDcRecord(itemRecord, metadataHandle, resourceHandles, partnerId, larStatus, requiredXpaths);
						}
					}
					// Other formats:
					else {
						// Construct the file name (clean up bad data if the record has native_ in it):
						String nativeFormatFileName = "native_" + metaMataRecord.valueOf("/metaMetadata/nativeFormat").trim().replaceFirst("native_", "") + ".xml";
						File nativeFormatFile = new File(metadataItemDirs[j], nativeFormatFileName);

						itemRecord = Dom4jUtils.getXmlDocument(nativeFormatFile, "UTF-8");
						
						// LAR format:
						if(formatToIndex.equals("lar")) {
							
							// Re-write the ID for LAR records, and insert required elements/data:
							Node recordIDNode = itemRecord.selectSingleNode("/*[local-name()='record']/*[local-name()='recordID']");
							if(recordIDNode == null)
								recordIDNode = itemRecord.getRootElement().addElement("recordID");
							if (recordIDNode != null) {
								String originalRecordID = recordIDNode.getText();
								//prtln("Setting LAR recordID to handle... ");
								
								if(isRecastAsLarRelatedMetadataCollection)
									recordIDNode.setText(metadataHandle+"-LAR");
								else
									recordIDNode.setText(metadataHandle);
							}
							else {
								throw new Exception("Unable to rewrite LAR recordID to append '-LAR' to the end. No recordID found at xPath'");	
							}

							// Attach/enforce metadata TOU:
							List existingTouElms = itemRecord.selectNodes("/*[local-name()='record']/*[local-name()='metadataTerms']");
							for (int ii = 0; ii < existingTouElms.size(); ii++)
								((Node) existingTouElms.get(ii)).detach();
												
							Element newElm = itemRecord.getRootElement().addElement("metadataTerms");
							newElm.setText(NSDLDCNormalizer.NSDL_TOU);
							newElm.addAttribute("holder", "University Corporation for Atmospheric Research (UCAR)");
							newElm.addAttribute("URL", "http://nsdl.org/help/terms-of-use");
							
							// Attach the various identifiers:
							if(partnerId != null)
								itemRecord.getRootElement().addElement("partnerID").setText(partnerId);
							if(resourceHandles != null) {
								for(int i = 0; i < resourceHandles.size(); i++) {
									itemRecord.getRootElement().addElement("resourceHandle").setText("hdl:" + ((String)resourceHandles.get(i)).trim());
								}
							}							
							if(metadataHandle != null)
								itemRecord.getRootElement().addElement("metadataHandle").setText("hdl:" + metadataHandle.trim());
							
							// Standard comment:
							itemRecord.getRootElement().addComment(NSDLDCNormalizer.NSDL_COMMENT);
														
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
			} catch (NSDLDCNormalizerXpathException ne) {
				// Log message if missing required XPath is encountered:
				String title = itemRecord.valueOf("/nsdl_dc:nsdl_dc/dc:title");
				if (title == null || title.trim().length() == 0)
					title = "n/a";
				String errType = ne.getMissingRequiredXpath();
				if (errType.indexOf("title") >= 0)
					errType = "title";
				else if (errType.indexOf("description") >= 0)
					errType = "description";
				else if (errType.indexOf("identifier") >= 0)
					errType = "url";
				String replaceStr = " ";
				String msg = "MissingRequiredXpath:" + collectionName.replaceAll(",", replaceStr) + "," + collectionKey.replaceAll(",", replaceStr) + "," + errType.replaceAll(",", replaceStr) + "," + metadataHandle + "," + partnerId.replaceAll(",", replaceStr) + "," + title.replaceAll(",", replaceStr);
				printStatusMessageWarning(msg);
				numSkippedRequiredXpath++;
				numProcessed++;
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

		String msg = "Finished indexing collection '" + collectionInfo.getCollectionName() + "'. Processed " + numProcessed + " records (" + numSuccess + " successful, " + numErrors + " errors, " + numSkippedNonFatal + " skipped for non-critical reason, " + numSkippedRequiredXpath + " skipped missing required XPath)";
		if (errorThrown != null) {
			msg += ". One or more records could not be indexed because of errors. Most recent error message: " + errorThrown;
			printStatusMessageError(msg);
		}
		else {
			printStatusMessage(msg);
		}

		if (abortIndexing)
			return;

		// If error rate is too high, don't continue. Throw exception to stop processing and prevent index commit:
		float errorPercentage = 0f;
		if (numProcessed > 0f)
			errorPercentage = numErrors / numProcessed;

		if (errorPercentage > maxIndexingErrorRate)
			throw new Exception("Collection '" + collectionInfo.getCollectionName() + "' had " + (errorPercentage * 100) + "% error rate (above allowed limit, no deletions or index commits will be made).");

		msg = "Deleting records from previous sessions for '" + collectionInfo.getCollectionName() + "'.";
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
			throw new Exception("Unable to configure NFR collections. No config file found at " + configFile);

		String ncsSearchApiBaseUrl = configXmlDoc.valueOf("/NFR_Collections/ncsManagedCollections/ncsSearchApiBaseUrl");
		if (ncsSearchApiBaseUrl == null || ncsSearchApiBaseUrl.trim().length() == 0)
			return collectionInfos;

		// Filter by DCS status as indicated:
		List nodes = configXmlDoc.selectNodes("/NFR_Collections/ncsManagedCollections/ncsCollectionStatusFilter/ncsCollectionStatus");
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

		String searchQueryConstraint = configXmlDoc.valueOf("/NFR_Collections/ncsManagedCollections/searchQueryConstraint");
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
					if(foundSetSpecs.containsKey(setSpec))
						throw new Exception("Duplicate setSpec found '" + setSpec + "' in collection IDs '" + foundSetSpecs.get(setSpec) + "' and '" + collectionId + "'. Please edit the NCS collection record(s) to fix this error.");	
					else
						foundSetSpecs.put(setSpec,collectionId);
					
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
						String makeNsdlDcCollectionForLarSetting = configXmlDoc.valueOf("/NFR_Collections/ncsManagedCollections/makeNsdlDcCollectionForLar");
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
		String formatType = configXmlDoc.valueOf("/NFR_Collections/ncsManagedCollections/formatType");
		if (formatType == null || formatType.trim().length() == 0 || !(formatType.equalsIgnoreCase("canonical_nsdl_dc") || formatType.equalsIgnoreCase("native")))
			formatType = "canonical_nsdl_dc";
		return formatType;
	}


	private PropertiesManager getProperties(File configDir) throws Exception {
		// First look in the configuration directory...
		if (configDir != null && configDir.isDirectory())
			return new PropertiesManager(new File(configDir, "edu.ucar.dls.dds.ndr.NFRIndexer.properties").toString());
		// Then look in the classpath...
		else
			return new PropertiesManager("edu.ucar.dls.dds.ndr.NFRIndexer.properties");
	}


	private void printStatusMessage(String msg) {
		collectionIndexer.printStatusMessage("NFRIndexer: " + msg, CollectionIndexer.MSG_TYPE_INFO);
		prtln(msg);
	}


	private void printStatusMessageWarning(String msg) {
		collectionIndexer.printStatusMessage("NFRIndexer: " + msg, CollectionIndexer.MSG_TYPE_WARNING);
		prtlnErr(msg);
	}


	private void printStatusMessageError(String msg) {
		collectionIndexer.printStatusMessage("NFRIndexer: " + msg, CollectionIndexer.MSG_TYPE_ERROR);
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
				List relatedCanonicalNsdlDcFormatsVals = configXmlDoc.selectNodes("/NFR_Collections/ncsManagedCollections/canonicalNsdlDcRelatedFormats/relatedFormat");
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
		System.err.println(getDateStamp() + " NFRIndexer Error: " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final static void prtln(String s) {
		if (debug) {
			System.out.println(getDateStamp() + " NFRIndexer: " + s);
		}
	}


	/**
	 *  Sets the debug attribute of the NFRIndexer object
	 *
	 * @param  db  The new debug value
	 */
	public final void setDebug(boolean db) {
		debug = db;
	}
}

