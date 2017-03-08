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
package edu.ucar.dls.dds.ndr;

import edu.ucar.dls.repository.indexing.*;

import java.io.*;
import java.util.*;
import java.net.URL;

import edu.ucar.dls.index.*;
import edu.ucar.dls.index.writer.*;
import java.text.*;

import edu.ucar.dls.ndr.*;
import edu.ucar.dls.ndr.reader.*;
import edu.ucar.dls.ndr.request.*;
import edu.ucar.dls.ndr.apiproxy.*;
import edu.ucar.dls.ndr.apiproxy.InfoXML;
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
 *  Indexes and configures collections from the NSDL Data Repository (NDR).
 *
 * @author    John Weatherley
 */
public class NDRIndexer implements ItemIndexer {
	private static boolean debug = true;

	private int MAX_NUM_COLLECTIONS = 40000;
	// The max number of collections DDS will suck in from NDR (limit for testing...)
	private int MAX_RECORDS_PER_COLLECTION = 900000;
	// The max number of records DDS will suck in from NDR per collection (limit for testing...)
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


	// Should verbose comments be written into the normalized metadata?

	/**  Constructor for the NDRIndexer object */
	public NDRIndexer() { }


	/**
	 *  Sets the configDirectory attribute of the NDRIndexer object
	 *
	 * @param  configDir       The new configDirectory value
	 * @param  persistanceDir  The new configDirectory value
	 * @exception  Exception   If error
	 */
	public void setConfigDirectory(File configDir, File persistanceDir) throws Exception {
		if (configDir == null)
			throw new Exception("The configDir is null");
		this.configFile = new File(configDir, "edu.ucar.dls.dds.ndr.NDRIndexer.NDR_indexer_collections.xml");
		myPersistanceDir = new File(persistanceDir, "NDRIndexer");
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
				String msg = "Unable to read the NDR configuration file '" + configFile + "'. Message: " + t.getMessage();
				prtlnErr(msg);
				throw new Exception(msg);
			}

			// Set up the NDRIndexer to index NDR collections:
			String ndrApiBaseUrl = configXmlDoc.valueOf("/NDR_Collections/ndrApiBaseUrl");
			if (ndrApiBaseUrl == null || ndrApiBaseUrl.length() == 0) {
				String msg = "Unable to configure NDR collections. Missing required config element: 'ndrApiBaseUrl'";
				printStatusMessageError(msg);
				throw new Exception(msg);
			}

			// Set up some values for testing stuff:
			try {
				MAX_NUM_COLLECTIONS = Integer.parseInt(configXmlDoc.valueOf("/NDR_Collections/maxNumCollections"));
			} catch (Throwable t) {}

			try {
				MAX_RECORDS_PER_COLLECTION = Integer.parseInt(configXmlDoc.valueOf("/NDR_Collections/maxNumRecordsPerCollection"));
			} catch (Throwable t) {}

			String generateDupResourceUrls = configXmlDoc.valueOf("/NDR_Collections/generateDupResourceUrls");
			if (generateDupResourceUrls != null && generateDupResourceUrls.equals("true"))
				this.generateDupResourceUrls = true;

			// NSDL DC normilzation vocab groups definitions:
			nsdlDcVocabSelections = configXmlDoc.valueOf("/NDR_Collections/nsdlDcVocabSelections");
			writeVerboseComments = configXmlDoc.valueOf("/NDR_Collections/writeVerboseComments");

			// HTTP User-Agent sent in requests:
			String userAgent = configXmlDoc.valueOf("/NDR_Collections/userAgent");
			if (userAgent == null || userAgent.trim().length() == 0)
				userAgent = "DDS Indexer";
			NDRConstants.init(ndrApiBaseUrl, null, null, userAgent);
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
							//prtln("NDRIndexer started BEGIN_INDEXING_ALL_COLLECTIONS." + "\n\n");
							indexCollections(null);
							break;
						}

						case IndexingEvent.INDEXER_READY:
						{
							//prtln("NDRIndexer started INDEXER_READY!" + "\n\n");
							return;
						}

						case IndexingEvent.BEGIN_INDEXING_COLLECTION:
						{
							//prtln("NDRIndexer started BEGIN_INDEXING_COLLECTION!" + "\n\n");
							String key = indexingEvent.getCollectionKey();
							if (key != null && key.trim().length() > 0)
								indexCollections(key);
							return;
						}

						case IndexingEvent.ABORT_INDEXING:
						{
							//prtln("NDRIndexer ABORT_INDEXING!" + "\n\n");
							printStatusMessage("Stopping indexing (may take a while)...");
							abortIndexing = true;
							return;
						}

						case IndexingEvent.UPDATE_COLLECTIONS:
						{
							prtln("NDRIndexer started UPDATE_COLLECTIONS!" + "\n\n");
							updateCollections();
							return;
						}
						case IndexingEvent.CONFIGURE_AND_INITIALIZE:
						{
							prtln("NDRIndexer updating configuration" + "\n\n");
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
				throw new IndexingInProgressException("NDRIndexer is already running.");
			abortIndexing = false;
			indexingIsActive = true;
		}

		// Update collections and configurations:
		updateCollections();

		prtln("NDRIndexer started." + "\n\n");
		printStatusMessage("Indexing started.");

		IndexingStatusBean indexingStatusBean = getIndexingStatusBean();

		// Initialize required NDR attributes and Sets TransformerFactory to a XSL 1.0 version:
		//NdrUtils.ndrTestSetup();

		Throwable errorThrown = null;

		// Configure all collections defined in the NCS:
		List ndrCollectionInfos = new ArrayList();

		ndrCollectionInfos = getCollectionsFromRemoteNCS();

		int numSkippedFromPreviousSession = 0;
		int numCollectionErrorsThrown = 0;
		boolean displayNumSkippedFromPreviousSessionMsg = false;

		if (!abortIndexing) {
			for (int i = 0; i < ndrCollectionInfos.size(); i++) {
				try {
					CollectionInfo collectionInfo = (CollectionInfo) ndrCollectionInfos.get(i);

					String currentCollectionKey = collectionInfo.getCollectionKey();

					// If we are indexing all collections:
					if (collectionKey == null) {
						if (!indexingStatusBean.hasCompletedCollection(currentCollectionKey)) {
							if (displayNumSkippedFromPreviousSessionMsg) {
								displayNumSkippedFromPreviousSessionMsg = false;
								printStatusMessage("Skipped " + numSkippedFromPreviousSession + " collections that were completed successfully in a previous indexing session...");
								printStatusMessage("Continuing to index " + (ndrCollectionInfos.size() - numSkippedFromPreviousSession) + " collections that were aborted or had errors...");
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
						ndrCollectionInfos.clear();

				} catch (Throwable t) {
					errorThrown = t;
					numCollectionErrorsThrown++;
					String msg = "Error indexing a collection. " + numCollectionErrorsThrown + " collection(s) have had serious errors. Most recent collection error: " + t.getMessage();
					printStatusMessageError(msg);
				}
			}
		}

		prtln("NDRIndexer completed." + "\n\n");

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

		synchronized (this) {
			indexingIsActive = false;
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
		printStatusMessage("Begin adding/updating NDR collections...");

		// Add collections that I know about:
		//List ncsCollections = getNcsCollections();
		//prtln("getNcsCollections() returned: " + Arrays.toString(ncsCollections.toArray()));

		boolean ndrErrorHasOccured = false;
		List ndrCollections = new ArrayList();

		// Configure collections defined in the NCS:
		List ndrCollectionInfos = new ArrayList();
		try {
			ndrCollectionInfos = getCollectionsFromRemoteNCS();

			if (ndrErrorHasOccured == false) {
				ndrCollections = new ArrayList();
				for (int i = 0; i < ndrCollectionInfos.size(); i++) {

					CollectionInfo collectionInfo = (CollectionInfo) ndrCollectionInfos.get(i);
					Document ncsCollectDoc = collectionInfo.getNcsRecordDoc();
					String collectionKey = ncsCollectDoc.valueOf("/ncsCollectionRecord/head/additionalMetadata/ndr_info/setSpec");
					if (collectionKey.equals("ncs-NSDL-COLLECTION-000-003-112-004")) {
						Node titleNode = ncsCollectDoc.selectSingleNode("/ncsCollectionRecord/metadata/record/general/title");
						//titleNode.setText("JOHN TEST!");
					}
					String ncsRecordXml = collectionInfo.getNcsRecordXml();

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
							ndrCollections.add(collectionKey);
							continue;
						}
					}

					// Add the collection to DDS:
					ndrCollections.add(collectionKey);
					doPutCollection(collectionInfo);
				}
			}
		} catch (Throwable t) {
			ndrErrorHasOccured = true;
			String msg = "Error getting NDR collections from NCS: " + t.getMessage();
			printStatusMessageError(msg);
		}

		// Do not delete/remove collections if there has been an error collecting the list of collections.
		if (ndrErrorHasOccured) {
			printStatusMessageError("One or more errors occured while updating the collections. No collections will be deleted...");
			return;
		}

		// Delete all collections in the repository that I don't know about:
		List currentConfiguredCollections = collectionIndexer.getConfiguredCollections();
		for (int i = 0; i < currentConfiguredCollections.size(); i++) {
			String collKey = (String) currentConfiguredCollections.get(i);
			if (!ndrCollections.contains(collKey) && !ndrCollectionInfos.contains(collKey)) {
				String deletedColString = collectionIndexer.deleteCollection(collKey);
				String collName = collKey;
				if (deletedColString != null) {
					Document deletedCollectionDoc = Dom4jUtils.getXmlDocument(deletedColString);
					collName = deletedCollectionDoc.valueOf("/*[local-name()='collectionRecord']/*[local-name()='general']/*[local-name()='fullTitle']");
				}
				String msg = "Deleted collection '" + collName + "'";
				printStatusMessage(msg);
			}
		}

		// Final status message
		printStatusMessage("Finished adding/updating NDR collections.");
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


	private MetadataProviderReader getMetadataProviderFromHandle(String mdpHandle) throws Exception {
		MetadataProviderReader mdpReader = null;
		mdpReader = new MetadataProviderReader(mdpHandle);
		prtln("MetadataProvider handle " + mdpHandle);
		prtln("\t" + "setName: " + mdpReader.getSetName());
		prtln("\t" + "setSpec: " + mdpReader.getSetSpec());
		prtln("\t" + "nativeFormat: " + mdpReader.getNativeFormat());
		return mdpReader;
	}


	/**
	 *  Gets a metadataProviderReader given a collection key.
	 *
	 * @param  collectionKey  collection key
	 * @return                The metadataProvider value
	 * @exception  Exception  If error
	 */
	private MetadataProviderReader getMetadataProviderFromCollectionKey(String collectionKey) throws Exception {
		return NdrUtils.getMetadataProvider(collectionKey);
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
		String larStatus = collectionInfo.getLarStatus();

		CollectionIndexingSession collectionIndexingSession = collectionIndexer.getNewCollectionIndexingSession(collectionKey);

		printStatusMessage("Fetching list of record handles for collection '" + collectionInfo.getCollectionName() + "'");

		NSDLDCNormalizer nsdlDcNormalizer = null;

		String formatTypeToIndex = getFormatTypeToIndex();
		if (formatTypeToIndex.equals("canonical_nsdl_dc") && formatToIndex.equals("nsdl_dc")) {
			boolean doWriteVerboseComments = (writeVerboseComments != null && writeVerboseComments.equalsIgnoreCase("true"));

			if (nsdlDcVocabSelections != null && nsdlDcVocabSelections.trim().length() > 0)
				nsdlDcNormalizer = new NSDLDCNormalizer(nsdlDcVocabSelections.trim(), doWriteVerboseComments);
		}
		
		List mdHandles = new ArrayList();
		
		// Check for handles list multiple times (NDR sometimes sends false empty list):
		for(int i = 0; i < 5; i++) {
			try {
				MetadataProviderReader mdp = getMetadataProviderFromHandle(collectionInfo.getHandle());
				mdHandles = mdp.getItemHandles();
			} catch (Exception e) {
				prtln("Error: Could not get itemHandles from MetadataProviderReader for '" + collectionInfo.getCollectionName() + "': " + e);
			}
			// If we have a non-empty list of handles, continue processing:
			if(mdHandles.size() > 0)
				break;
			Thread.sleep(1000);
		}
		if(mdHandles.size() == 0)
			throw new Exception("NDR ListMembers response shows 0 records for collection '" + collectionInfo.getCollectionName() + "' . This may be an NDR error. If not, please remove the collection if it is no longer active (0 records not allowed).");

		printStatusMessage("Indexing " + mdHandles.size() + " records for collection '" + collectionInfo.getCollectionName() + "'...");

		/*
			iterate through metadata handles, grabbing itemRecords (from the datastream
			associated with the collection's "nativeFormat".
		*/
		int numHandles = (mdHandles == null ? 0 : mdHandles.size());
		prtln("\n------------\nthere are " + numHandles + " itemRecords");

		int messagesFrequency = 100;
		// How often to display status message of progress (num records)

		int numSuccess = 0;
		int numErrors = 0;
		int numProcessed = 0;
		int numSkipped = 0;
		int numSkippedRequiredXpath = 0;

		Throwable errorThrown = null;

		int count = 0;
		for (Iterator i = mdHandles.iterator(); i.hasNext() && !abortIndexing && numProcessed < this.MAX_RECORDS_PER_COLLECTION; ) {
			String mdHandle = (String) i.next();
			String uniqueId = null;
			Document itemRecord = null;
			try {
				//prtln("Processing for handle " + mdHandle + " starting... format: " + formatToIndex);

				MetadataReader mdReader = new MetadataReader(mdHandle, formatToIndex);

				// Is status 'done': Make sure we're grabbing final, valid records only for NCS collections:
				boolean isStatusDone = (!mdReader.isNcsMetadata() || (mdReader.getIsFinal() && mdReader.getIsValid()));
				if (isStatusDone) {

					List resourceHandles = mdReader.getRelationshipValues("nsdl:metadataFor");
					uniqueId = mdReader.getUniqueID();

					// Canonical NSDL DC format:
					if (formatTypeToIndex.equals("canonical_nsdl_dc") && formatToIndex.equals("nsdl_dc")) {
						itemRecord = mdReader.getCanonicalNsdlDcItemRecord();
						if (nsdlDcNormalizer != null) {
							List requiredXpaths = new ArrayList();
							requiredXpaths.add("/nsdl_dc:nsdl_dc/dc:identifier[starts-with(.,'http://') or starts-with(.,'https://') or starts-with(.,'ftp://')]");
							requiredXpaths.add("/nsdl_dc:nsdl_dc/dc:title");
							requiredXpaths.add("/nsdl_dc:nsdl_dc/dc:description");
							itemRecord = nsdlDcNormalizer.normalizeNsdlDcRecord(itemRecord, mdHandle, resourceHandles, uniqueId, larStatus, requiredXpaths);
						}
					}
					// Native format:
					else {
						itemRecord = mdReader.getItemRecord();
					}

					prtln("Handle: " + mdHandle + " ID: " + mdReader.getRecordId() + " status: " + mdReader.getStatus() + " isValid: " + mdReader.getIsValid());

					// Generate dup resource URLs if directed for testing:
					if (generateDupResourceUrls && (count == 0 || count == 2) && !mdHandle.equals("2200/20100409094800622T")) {
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
					String ddsId = mdHandle;
					//if (ddsId == null)
					//ddsId = mdHandle;

					//prtln("Put record for collection:" + collectionKey + " format:" + formatToIndex + " no: " + (++count) + " id:" + ddsId);
					collectionIndexer.putRecord(Dom4jUtils.prettyPrint(itemRecord), collectionKey, ddsId, collectionIndexingSession);
					numSuccess++;
					numProcessed++;
				}
				// If status not done:
				else {
					String msg = "NCS-managed record " + mdHandle + " does not meet NCS condition (isFinal && isValid). Record status:'" + mdReader.getStatus() + "'. Skipping this record...";
					prtln(msg);
					numSkipped++;
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
				String msg = "MissingRequiredXpath:" + collectionName.replaceAll(",", replaceStr) + "," + collectionKey.replaceAll(",", replaceStr) + "," + errType.replaceAll(",", replaceStr) + "," + mdHandle + "," + uniqueId.replaceAll(",", replaceStr) + "," + title.replaceAll(",", replaceStr);
				printStatusMessageWarning(msg);
				numSkippedRequiredXpath++;
				numProcessed++;
			} catch (Exception e) {
				errorThrown = e;
				prtlnErr("could not get itemRecord for " + mdHandle + ": " + e);
				numErrors++;
				numProcessed++;
			} finally {
				prtln("Processing for handle " + mdHandle + " completed...\n\n");
			}

			if (numProcessed > 0 && numProcessed % messagesFrequency == 0) {
				String msg = "Processed " + numProcessed +
						" of " + mdHandles.size() +
						" records for collection '" + collectionInfo.getCollectionName() +
						"' thus far (" + numSuccess + " successful, " + numErrors + " errors, " + numSkipped + " skipped NCS status not done, " + numSkippedRequiredXpath + " skipped missing required XPath)";
				if (errorThrown != null) {
					msg += ". One or more records could not be indexed because of errors. Most recent error message: " + errorThrown;
					printStatusMessageError(msg);
				}
				else {
					printStatusMessage(msg);
				}
			}
		}

		String msg = "Finished indexing collection '" + collectionInfo.getCollectionName() + "'. Processed " + numProcessed + " records (" + numSuccess + " successful, " + numErrors + " errors, " + numSkipped + " skipped NCS status not done, " + numSkippedRequiredXpath + " skipped missing required XPath)";
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
		if(numProcessed > 0f)
			errorPercentage = numErrors / numProcessed;

		if (errorPercentage > maxIndexingErrorRate)
			throw new Exception("Collection '" + collectionInfo.getCollectionName() + "' had " + (errorPercentage*100) + "% error rate (above allowed limit, no deletions or index commits will be made).");

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
			throw new Exception("Unable to configure NDR collections. No config file found at " + configFile);

		String ncsSearchApiBaseUrl = configXmlDoc.valueOf("/NDR_Collections/ncsManagedCollections/ncsSearchApiBaseUrl");
		if (ncsSearchApiBaseUrl == null || ncsSearchApiBaseUrl.trim().length() == 0)
			return collectionInfos;

		// Filter by DCS status as indicated:
		List nodes = configXmlDoc.selectNodes("/NDR_Collections/ncsManagedCollections/ncsCollectionStatusFilter/ncsCollectionStatus");
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

		String searchQueryConstraint = configXmlDoc.valueOf("/NDR_Collections/ncsManagedCollections/searchQueryConstraint");
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
			serviceRequestUrl = ncsSearchApiBaseUrl + "?verb=Search&xmlFormat=ncs_collect" + nscStatusParams + "&n=" + n + "&s=" + s + searchQueryConstraint;

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

				// Add all metadataProviders (Collections):
				for (int j = 0; j < ncsCollectRecords.size() && collectionInfos.size() < MAX_NUM_COLLECTIONS; j++) {
					Element fullNcsRecord = (Element) ((Element) ncsCollectRecords.get(j)).clone();
					String handle = fullNcsRecord.valueOf("head/additionalMetadata/ndr_info/metadataProviderHandle");
					//prtln("NCS OAI collection handle: " + handle);

					Document ncsCollectionRecord = DocumentHelper.createDocument();
					Element root = ncsCollectionRecord.addElement("ncsCollectionRecord");
					root.appendContent(fullNcsRecord);

					//String ncsRecordXml = Dom4jUtils.prettyPrint(ncsCollectionRecord);
					//ncsRecordXml =  null;

					if (handle != null && handle.trim().length() > 0)
						collectionInfos.add(new CollectionInfo(handle.trim(), ncsCollectionRecord));
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
		String formatType = configXmlDoc.valueOf("/NDR_Collections/ncsManagedCollections/formatType");
		if (formatType == null || formatType.trim().length() == 0 || !(formatType.equalsIgnoreCase("canonical_nsdl_dc") || formatType.equalsIgnoreCase("native")))
			formatType = "canonical_nsdl_dc";
		return formatType;
	}


	private PropertiesManager getProperties(File configDir) throws Exception {
		// First look in the configuration directory...
		if (configDir != null && configDir.isDirectory())
			return new PropertiesManager(new File(configDir, "edu.ucar.dls.dds.ndr.NDRIndexer.properties").toString());
		// Then look in the classpath...
		else
			return new PropertiesManager("edu.ucar.dls.dds.ndr.NDRIndexer.properties");
	}

	private void printStatusMessage(String msg) {
		collectionIndexer.printStatusMessage("NRDIndexer: " + msg, CollectionIndexer.MSG_TYPE_INFO);
		prtln(msg);
	}

	private void printStatusMessageWarning(String msg) {
		collectionIndexer.printStatusMessage("NRDIndexer: " + msg, CollectionIndexer.MSG_TYPE_WARNING);
		prtlnErr(msg);
	}

	private void printStatusMessageError(String msg) {
		collectionIndexer.printStatusMessage("NRDIndexer: " + msg, CollectionIndexer.MSG_TYPE_ERROR);
		prtlnErr(msg);
	}	

	private class CollectionInfo {
		private String _ndr_handle = null;
		private Document _ncsRecordDoc = null;
		private String _format_to_index = null;
		private String _library_format = null;


		private CollectionInfo(String handle, Document ncsRecordDoc) {
			_ndr_handle = handle;
			_ncsRecordDoc = ncsRecordDoc;
		}


		/**
		 *  Gets the collectionName attribute of the CollectionInfo object
		 *
		 * @return    The collectionName value
		 */
		public String getCollectionName() {
			return _ncsRecordDoc.valueOf("/ncsCollectionRecord/metadata/record/general/title");
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
		 *  Gets the handle attribute of the CollectionInfo object
		 *
		 * @return    The handle value
		 */
		public String getHandle() {
			return _ndr_handle;
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
		 *  Gets the collectionKey attribute of the CollectionInfo object
		 *
		 * @return    The collectionKey value
		 */
		public String getCollectionKey() {
			return _ncsRecordDoc.valueOf("/ncsCollectionRecord/head/additionalMetadata/ndr_info/setSpec");
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
		 *  Gets the format that should be indexed for this collection, for example 'nsdl_dc' or 'comm_anno'.
		 *
		 * @return    The formatToIndex value
		 */
		public String getFormatToIndex() {
			if (_format_to_index == null) {
				// Format type 'canonical_nsdl_dc' or 'native'
				String formatType = getFormatTypeToIndex();

				// List of related formats to include with canonical_nsdl_dc:
				List relatedCanonicalNsdlDcFormatsVals = configXmlDoc.selectNodes("/NDR_Collections/ncsManagedCollections/canonicalNsdlDcRelatedFormats/relatedFormat");
				Map relatedCanonicalNsdlDcFormats = new HashMap();
				for (int i = 0; i < relatedCanonicalNsdlDcFormatsVals.size(); i++)
					relatedCanonicalNsdlDcFormats.put(((Node) relatedCanonicalNsdlDcFormatsVals.get(i)).getText(), new Object());

				_format_to_index = "nsdl_dc";
				String libraryFormat = getLibraryFormat();
				if (libraryFormat == null || libraryFormat.trim().length() == 0) {
					MetadataProviderReader mdpReader = null;
					try {
						mdpReader = getMetadataProviderFromHandle(getHandle());
						libraryFormat = mdpReader.getNativeFormat();
						prtln("library format was not found in NCS record for '" + getCollectionName() + "'. Fetched from NDR: " + libraryFormat);
					} catch (Exception e) {
						String msg = "Error retrieving NDR MetadataProvider. Skipping collection '" + getHandle() + "'. Message: " + e.getMessage();
						printStatusMessageError(msg);
						return null;
					}
				}

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
			if (this.getHandle() == null || other.getHandle() == null)
				return false;
			return this.getHandle().equals(other.getHandle());
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
		System.err.println(getDateStamp() + " NDRIndexer Error: " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final static void prtln(String s) {
		if (debug) {
			System.out.println(getDateStamp() + " NDRIndexer: " + s);
		}
	}


	/**
	 *  Sets the debug attribute of the NDRIndexer object
	 *
	 * @param  db  The new debug value
	 */
	public final void setDebug(boolean db) {
		debug = db;
	}
}

