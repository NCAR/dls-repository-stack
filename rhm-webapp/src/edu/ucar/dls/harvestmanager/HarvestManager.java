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
package edu.ucar.dls.harvestmanager;

import org.apache.commons.lang3.StringUtils;
import org.dlese.dpc.util.Utils;
import org.dlese.dpc.xml.XMLUtils;
import org.dlese.dpc.util.TimedURLConnection;
import org.dlese.dpc.xml.Dom4jUtils;
import org.dlese.dpc.oai.OAIUtils;
import org.dlese.dpc.util.Files;
import org.dlese.dpc.email.SendEmail;
import org.dom4j.*;
import org.dom4j.tree.*;

import java.util.TimerTask;
import java.util.Timer;
import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.net.URLEncoder;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.tools.IngestorUtil;

import java.lang.Thread;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/**
 *  Manages and performs harvests of remote metadata repositories and ingests the metadata into a database.
 *
 * @author    John Weatherley
 * @param
 */
public final class HarvestManager {

	private boolean debug = true;

    // Set to true to spread harvests across the monthfalse to perform all harvests on the day they are scheduled
	private boolean _doSpreadHarvestSessionsAcrossTheMonth = false;

    // Maximum number of harvests to trigger each timer session (if _doSpreadHarvestSessionsAcrossTheMonth is set to false).
    private int _maxHarvestsPerSession = 100;

	private HarvestManagerPersistence harvestManagerPersistence = null;
	private Timer _harvestTimer = null;
	private String _ncsWebServiceURL = null;
	private File _harvestTriggerFileDir = null;
	private Document _collections = new DefaultDocument();
	//private boolean _runInTestMode = false;
	// Trigger real harvests or test harvests only (no ingest)

	// E-mailer (leave values null to disable):
	private String _mailServer = null;
	// Example: mail.dls.ucar.edu
	private String _mailType = null;
	// Values: store (imap) or transport (smtp)
	private String[] _toEmails = null;
	private String _fromEmail = null;
	private String _harvestManagerUrl = null;
	private String _ncsViewEditRecordUrl = null;
	private String _ucarCollectionsQuery = null;
	private ConcurrentHashMap<String, Thread> _ingestorThreads = 
				new ConcurrentHashMap<String, Thread>();
	private int _maxIngestorThreads = 50;
	private HashMap _collectionNameMappings = null;
    private boolean _doReprocessCollectionsMonthly = false;
	private List<HarvestCollectionDAO> _ingestableCollections = null;
	private Reharvester reharvester = null;
	private Thread reharvesterThread = null;
	/**
	 *  Constructor for the HarvestManager object
	 *
	 * @param  ncsWebServiceURL       The ncsWebServiceURL
	 * @param  harvestTriggerFileDir  The harvestTriggerFileDir
	 * @param  persistentDataDir      The persistentDataDir
	 * @param  ucarCollectionsQuery   The query used to limit the search to fetch the ncs_collect records
	 * @exception  Exception          If error
	 */
	public HarvestManager(  String ncsWebServiceURL,
                            String ucarCollectionsQuery,
                            String harvestTriggerFileDir,
                            String persistentDataDir,
                            int maxIngestorThreads,
                            boolean doReprocessCollectionsMonthly) throws Exception {
		harvestManagerPersistence = new HarvestManagerPersistence(persistentDataDir);

        this._doReprocessCollectionsMonthly = doReprocessCollectionsMonthly;

		_harvestTriggerFileDir = new File(harvestTriggerFileDir);
		if (!_harvestTriggerFileDir.exists() && !_harvestTriggerFileDir.mkdirs())
			throw new Exception("HarvestManager unable to create the trigger file location '" + _harvestTriggerFileDir + "'");

		_harvestTriggerFileDir.mkdirs();

		_ncsWebServiceURL = ncsWebServiceURL;
		
		_ucarCollectionsQuery = ucarCollectionsQuery;
		this._maxIngestorThreads = maxIngestorThreads;
		// Load and cache the local collections from the DCS:
		try {
			updateCollectionsFromDCS();
		} catch (Throwable t) {
			prtlnErr("Unable to fetch collection records from the DCS: " + t);
			t.printStackTrace();
		}
		this.reharvester = new Reharvester(this);
	}


	/**
	 *  Pass the String 'test' to run harvests in test mode only (no ingest). Pass any other value to run in
	 *  standard mode.
	 *
	 * @return    The collectionsDocument value
	 */
	/* public void setRunMode(String runMode) {
		if (runMode != null && runMode.equalsIgnoreCase("test"))
			_runInTestMode = true;
		else
			_runInTestMode = false;
	} */
	/**
	 *  Gets the collections Document containing all harvest collection from the DCS. If no response from the
	 *  DCS, will return a local cached instance of the Document, if avialable.
	 *
	 * @return    The collectionsDocument value
	 */
	public Document getCollectionsDocument() {

		synchronized (_collections) {

			if (!_collections.hasContent()) {

				// Load and cache the local collections from the DCS:
				try {
					updateCollectionsFromDCS();
				} catch (Throwable t) {
					try {
						//prtlnErr("Unable to fetch collection records from the NCS: " + t);
						//prtln("updating collections from persistant cache");
						_collections = harvestManagerPersistence.getCollectionsDocument();
					} catch (Throwable t2) {
						prtlnErr("Unable to retrieve collection records XML from persistent cache: " + t2);
					}
				}
			}

			if (_collections == null) {
				//prtln("No collections available. Using empty document...");
				_collections = new DefaultDocument();
			}
			return _collections;
		}
	}
	
	public HashMap<String,String> getCollectionNameMappings() {

		synchronized (this._collectionNameMappings) {

			if (this._collectionNameMappings==null) {
				// Load and cache the local collections from the DCS:
				this.updateCollectionNameMappings(this.getCollectionsDocument());
				
			}
		}
		return this._collectionNameMappings;
	}
	
	public List<HarvestCollectionDAO> getIngestableCollections() {

		synchronized (this._ingestableCollections) {

			if (this._ingestableCollections==null) {
				// Load and cache the local collections from the DCS:
				this.updateIngestableCollections(this.getCollectionsDocument());
				
			}
		}
		return this._ingestableCollections;
	}
	
	public HarvestCollectionDAO getCollectionDAO(String collectionId)
	{
		for(HarvestCollectionDAO collection:this.getIngestableCollections())
		{
			if(collection.getCollectionId().equals(collectionId))
				return collection;
		}
		return null;
	}
	
	
	private void updateIngestableCollections(Document collectionDocument)
	{

			List<Element> recordNodes = collectionDocument.selectNodes("/DDSWebService/Search/results/record");
			List<HarvestCollectionDAO> ingestableCollections = new ArrayList<HarvestCollectionDAO>();
			
			if(recordNodes!=null && recordNodes.size()>0)
			{
				for(Element record: recordNodes)
				{
					Element ingestProtocolElement = (Element)record.selectNodes("metadata/record/collection/ingest/*").get(0);
					String ingestProtocol = ingestProtocolElement.getName();
					
					String baseURL = null;
					String metadataPrefix = null;
					if(ingestProtocol.equals(Config.Protocols.OAI))
					{
						 baseURL = ingestProtocolElement.attributeValue("baseURL");
						 metadataPrefix = ingestProtocolElement.attributeValue("metadataPrefix");
					}
					else if(ingestProtocol.equals(Config.Protocols.LR_SLICE))
						 baseURL = ingestProtocolElement.attributeValue("nodeURL");
					
					String frequency = ingestProtocolElement.attributeValue("frequency");
					String libraryFormat = ingestProtocolElement.attributeValue("libraryFormat");
					String title = record.valueOf("metadata/record/general/title");
					String setSpec = record.valueOf("metadata/record/collection/setSpec");
					String larReadiness = record.valueOf("metadata/record/collection/LAR");
					String collectionId = record.valueOf("head/id");
					String collectionHandle = record.valueOf("head/additionalMetadata/collection_info/metadataHandle");
					String url = record.valueOf("metadata/record/general/url");
					String description = record.valueOf("metadata/record/general/description");
					String imageHeight = record.valueOf("metadata/record/collection/imageHeight");
					String brandURL = record.valueOf("metadata/record/collection/brandURL");

                    List<String> viewContexts = new ArrayList<String>();
                    List<Element> viewContextNodes = record.selectNodes("metadata/record/collection/viewContexts/viewContext");
                    for(Element viewContextElement: viewContextNodes)
                        viewContexts.add(viewContextElement.getText());

                    Collections.sort(viewContexts);
					
					HarvestCollectionDAO collection = new HarvestCollectionDAO(ingestProtocol,
							collectionId, collectionHandle, title, setSpec, frequency, libraryFormat, 
							baseURL, viewContexts, url, description, imageHeight, brandURL, larReadiness);
					
					collection.setMetadataPrefix(metadataPrefix);
					ingestableCollections.add(collection);
				}
			}
			Collections.sort(ingestableCollections);
			this._ingestableCollections = ingestableCollections;
	}
	
	private void updateCollectionNameMappings(Document collectionDocument)
	{

			List<Element> recordNodes = collectionDocument.selectNodes("/DDSWebService/Search/results/record");
			HashMap<String, String> collectionNameMappings = new HashMap<String, String>();
			
			if(recordNodes!=null && recordNodes.size()>0)
			{
				for(Element record: recordNodes)
				{
					String setSpec = record.valueOf("metadata/record/collection/setSpec");
					String title = record.valueOf("metadata/record/general/title");
					collectionNameMappings.put(setSpec, title);
				}
			}
			this._collectionNameMappings = collectionNameMappings;
	}


	/**
	 *  Gets the collectionRecord Node for the given collection record ID
	 *
	 * @param  id  Collection ID
	 * @return     The collectionRecordNode value
	 */
	public Node getCollectionRecordNode(String id) {
		Document colls = getCollectionsDocument();
		if (colls == null)
			return null;
		return colls.selectSingleNode("/DDSWebService/Search/results/record[head/id='" + id + "']");
	}


	/**
	 *  Gets the last successful production harvest triggerEvent for the given collection, or null if no
	 *  successful harvest has been logged yet for the given collection.
	 *
	 * @param  id  Collection ID
	 * @return     The last successful triggerEvent Node, or null
	 */
	public Node getLastSuccessfulHarvestTriggerEvent(String id) {
		Document harvestHistoryDocument = null;
		try {
			harvestHistoryDocument = getHarvestTriggerDocument(id);
		} catch (Throwable t) {
			prtlnErr("getLastSuccessfulHarvestTriggerEvent() 1: " + t);
		}

		if (harvestHistoryDocument == null)
			return null;

		List triggerEventNodes = harvestHistoryDocument.selectNodes("/TriggerHistory/triggerEvents/triggerEvent");
		if (triggerEventNodes == null)
			return null;

		for (int i = triggerEventNodes.size() - 1; i >= 0; i--) {
			Node triggerEventNode = (Node) triggerEventNodes.get(i);
			String status = triggerEventNode.valueOf("harvestStatus/statusCode");
			try {
				String runType = triggerEventNode.valueOf("./*[local-name()='triggerFile']/*[local-name()='harvestRequest']/*[local-name()='runType']");
				if (runType != null && runType.trim().length() > 0 && !runType.equals("check")) {
					if (status != null && (status.equals("2") || status.equals("3")))
						return triggerEventNode;
				}
			} catch (Throwable t) {
				prtlnErr("getLastSuccessfulHarvestTriggerEvent() 2: " + t);
				t.printStackTrace();
			}
		}
		return null;
	}


	/**
	 *  Updates the local collections Document from the DCS, if available, and saves a copy to persistent
	 *  storage.
	 *
	 * @exception  Exception  If error
	 */
	public void updateCollectionsFromDCS() throws Exception {
		int timoutMs = 6000;
		int s = 0;
		int n = 200;

		prtln("updating collections from DCS");

		Document collections = null;

		// Loop until all collection records have been fetched from the DCS Web service, and build a DOM:
		String serviceRequestUrl = null;
		for (int i = 0; i < 200; i++) {
			
			String query = URLEncoder.encode(_ucarCollectionsQuery,"UTF-8");
			
			serviceRequestUrl = _ncsWebServiceURL + "?verb=Search&xmlFormat=ncs_collect&dcsStatus=In+Progress&dcsStatus=Done&q=" + query + "&n=" + n + "&s=" + s;
			
			Document fetched = Dom4jUtils.getXmlDocument(Dom4jUtils.localizeXml(TimedURLConnection.importURL(serviceRequestUrl, timoutMs)));

			String numResults = fetched.valueOf("/DDSWebService/Search/resultInfo/totalNumResults");
			String numReturned = fetched.valueOf("/DDSWebService/Search/resultInfo/numReturned");
			String offset = fetched.valueOf("/DDSWebService/Search/resultInfo/offset");
			String errorCode = fetched.valueOf("/DDSWebService/error/@code");

			if (errorCode != null && errorCode.trim().length() != 0)
				throw new Exception("No collections records available from DCS. Web service error code returned: '" + errorCode + "' for request: " + serviceRequestUrl);
			if (numResults == null || numResults.trim().length() == 0 || numResults.equals("0"))
				throw new Exception("No collections records available from DCS (numResults=0) for request: " + serviceRequestUrl);

			if (collections == null)
				collections = fetched;
			else {
				List recordNodes = fetched.selectNodes("/DDSWebService/Search/results/record");
				//prtln("Fetch num recordNodes:" + recordNodes.size());

				if (recordNodes == null || recordNodes.size() == 0)
					break;

				// Build a DOM of all collection records:
				Branch collectionResults = (Branch) collections.selectSingleNode("/DDSWebService/Search/results");
				for (int j = 0; j < recordNodes.size(); j++)
				{
					collectionResults.add(((Node) recordNodes.get(j)).detach());
					
				}
			}

			//prtln("Fetch numReturned:" + numReturned + " offset:" + offset + " s:" + s);
			if (numReturned == null || numReturned.equals("0"))
				break;
			s += n;
		}

		// Re-write the num totals in the Document:
		List recordNodes = collections.selectNodes("/DDSWebService/Search/results/record");
		collections.selectSingleNode("/DDSWebService/Search/resultInfo/totalNumResults").setText(Integer.toString(recordNodes.size()));
		collections.selectSingleNode("/DDSWebService/Search/resultInfo/numReturned").setText(Integer.toString(recordNodes.size()));
		Branch ri = (Branch) collections.selectSingleNode("/DDSWebService/Search/resultInfo");
		ri.add(new DefaultComment("These collection records were fetched from the DCS on " + new Date() + ". Requested url: " + serviceRequestUrl));

		// Persist the collections XML:
		synchronized (_collections) {
			harvestManagerPersistence.setCollectionsDocument(collections);
			_collections = collections;
			updateCollectionNameMappings(_collections);
			updateIngestableCollections(_collections);
		}
	}


	/**
	 *  Creates a UUID from a harvest collection record ID.
	 *
	 * @param  id  Collection record ID
	 * @return     UUID String
	 */
	public String idToUuid(String id) {
		return id + "-" + getRightNowDate().getTime();
	}


	/**
	 *  Extract the collection record ID from a UUID.
	 *
	 * @param  uuid  The UUID
	 * @return       The collection record ID
	 */
	public static String getIdFromUuid(String uuid) {
		return uuid.substring(0, uuid.lastIndexOf('-'));
	}


	/**
	 *  Extract the UUID creation Date from a UUID String.
	 *
	 * @param  uuid  The UUID
	 * @return       The Date the UUID was created
	 */
	public static Date getUuidCreationDate(String uuid) {
		long time = Long.parseLong(uuid.substring(uuid.lastIndexOf('-') + 1, uuid.length()));
		return new Date(time);
	}


	/**
	 *  Gets a standard long display String represntation of a Date to show to users.
	 *
	 * @param  date  A Date Object
	 * @return       A long date String
	 */
	public static String getDateLongDisplayString(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy h:mm:ss a z");
		//df.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
		String datestg = df.format(date);
		return datestg;
	}


	/**
	 *  Triggers all harvests for a given daily session, assumes one session per day called by the timer.
	 */
	private void doHarvestSession() {
        int numTriggers = _maxHarvestsPerSession;

        /*
            If indicated, perform the minimum number of harvests per session in order to complete all harvests
            in a given month, evenly distributing harvests across the whole month.
         */
        if(_doSpreadHarvestSessionsAcrossTheMonth)
            numTriggers = getNumHarvestsPerDay();

		String id = null;
		for (int i = 0; i < numTriggers; i++) {
			id = getNextCollectionToHarvest();
			try {
				if (id != null) {
					String runType = "full_reharvest";
					triggerHarvest(id, runType, false, null);
				}
			} catch (Throwable t) {
				prtlnErr("Unable to trigger harvest for id: " + id + ". Reason: " + t);
			}
		}

		doCheckForStaleHarvests();
		//doCheckOaiVitality();
	}

	/**  Check if the harvest is stale and if so, notify staff of failed harvest. */
	private void doCheckForStaleHarvests() {

		List recordNodes = (List) getCollectionsDocument().selectNodes("/DDSWebService/Search/results/record");

		if (recordNodes == null || recordNodes.size() == 0)
			return;

		//prtln("doCheckForStaleHarvests() num collections: " + recordNodes.size());
		int numStaleNotified = 0;
		for (int j = 0; j < recordNodes.size(); j++) {
			String collectionId = ((Node) recordNodes.get(j)).valueOf("head/id");
			
			String mdp_handle = ((Node) recordNodes.get(j)).valueOf("head/additionalMetadata/collection_info/metadataHandle");
			// Make a collection_na that is file system safe (used in directory name):
			mdp_handle = mdp_handle.replaceAll("/", "-");
			
			// If the last harvest triggered is greater than 3 days old and has no status, notify staff and apply status of stale:
			LastTriggeredDAO lastTriggeredDAO = getLastTriggeredDAO(collectionId);
			if (lastTriggeredDAO != null &&
				(lastTriggeredDAO.getLastTriggeredStatus() == null || lastTriggeredDAO.getLastTriggeredStatus().trim().length() == 0) &&
				HarvestManagerUtils.isHarvestStale(lastTriggeredDAO.getLastTriggeredDate())) {
				String status = "5";
				String uuid = lastTriggeredDAO.getUuid();
				
				// If the thread is still running kill it
				boolean wasInterrupted = this.interruptIngestorThread(collectionId);
				if (!wasInterrupted)
				{
					// If it couldn't be interrupted, ie thread wasn't alive then log status here,
					// otherwise if the thread was interrupted , the thread will notify the status change
					
					// we also want to cleanup the workspace since the thread was possibly stopped
					// before it was able to
					IngestorUtil.timoutOccuredCleanupUuid(uuid, mdp_handle, 3);
					String timestamp = OAIUtils.getDatestampFromDate(new Date());
					try {
						//prtln("notifying stale harvest for: " + uuid + " status: " + lastTriggeredDAO.getLastTriggeredStatus());
						logHarvestStatus(status, "ingest process did not complete after waiting 3 or more days.", uuid, timestamp);
						numStaleNotified++;
					} catch (Throwable t) {
						prtlnErr("doCheckForStaleHarvests(): s" + t);
					}
				}
			}
		}
		//prtln("doCheckForStaleHarvests() num stale collections notifications: " + numStaleNotified);
	}


	/**
	 *  Check if the configured sets are available and whether the data provider's sets have changed since the
	 *  last check.
	 */
	private void doCheckOaiVitality() {
		List recordNodes = (List) getCollectionsDocument().selectNodes("/DDSWebService/Search/results/record");

		if (recordNodes == null || recordNodes.size() == 0)
			return;

		prtln("doCheckOaiVitality() num collections: " + recordNodes.size());
		int numChecked = 0;
		for (int j = 0; j < recordNodes.size(); j++) {
			Node recordNode = ((Node) recordNodes.get(j));
			String recordId = recordNode.valueOf("head/id");
			String collectionName = recordNode.valueOf("metadata/record/general/title");

			List oaiNodes = recordNode.selectNodes("metadata/record/collection/ingest/oai");

			// Assume there might (one day) be more than one OAI config per collection:
			for (int jj = 0; jj < oaiNodes.size(); jj++) {
				Node oaiNode = ((Node) oaiNodes.get(jj));
				String baseUrl = oaiNode.valueOf("@baseURL");
				String format = oaiNode.valueOf("@format");

				if (baseUrl == null || baseUrl.trim().length() == 0 || format == null || format.trim().length() == 0)
					continue;

				List setSpecNodes = oaiNode.selectNodes("set/@setSpec");
				String setsStr = "";
				List setSpecs = new ArrayList();
				if (setSpecNodes != null) {
					setSpecs = new ArrayList(setSpecNodes.size());
					for (int i = 0; i < setSpecNodes.size(); i++)
						setSpecs.add(((Node) setSpecNodes.get(i)).valueOf("."));
				}
				//prtln("doCheckOaiVitality() collection: " + collectionName + " baseUrl: " + baseUrl + " format: " + format + " num setSpecs: " + setSpecs.size() + " sets: " + Arrays.toString(setSpecs.toArray(new String[]{})));
				numChecked++;
			}
		}

		prtln("doCheckOaiVitality() done. Num processed: " + numChecked);

	}


	/**
	 *  Gets the last trigger DAO of a given collection not including test harvests, or null if none.
	 *
	 * @param  id  Collection ID
	 * @return     The LastTriggeredDAO value
	 */
	public LastTriggeredDAO getLastTriggeredDAO(String id) {
		return harvestManagerPersistence.getLastTriggeredDAO(id);
	}


	/**
	 *  Gets the next trigger date of a given collection, or null if none.
	 *
	 * @param  id  Collection ID
	 * @return     The nextTriggerDate value
	 */
	public Date getNextTriggerDate(String id) {
		Date nextTriggerDate = null;
		LastTriggeredDAO lastTriggeredDAO = getLastTriggeredDAO(id);

		Node collectionRecord = getCollectionRecordNode(id);
		if (lastTriggeredDAO != null && collectionRecord != null) {
			String frequency = collectionRecord.valueOf("metadata/record/collection/ingest/oai/@frequency");

			float freq = 0f;
			try {
				freq = Float.parseFloat(frequency);
			} catch (NumberFormatException e) {}

			return nextHarvestDate(freq, lastTriggeredDAO.getLastTriggeredDate());
		}
		return nextTriggerDate;
	}

	/**
	 *  Triggers a harvest for the given collection. RunType of 'full_reharvest' performs a havest with ingest to
	 *  the NFR, 'incremental_reharvest' performs incremantal harvest to NFR requesting changes from the given
	 *  fromDate (this need verification), 'check' performs the harvest with no ingest. This is done by writing a trigger file conforming
	 *  to http://ns.nsdl.org/schemas/MRingest/harvest_v1.00.xsd with a file name of the form
	 *  [collection-id]_[datestamp]_harvest.xml for example: 3694837_2008-05-11T23-00-02Z_harvest.xml to the
	 *  appropriate directory monitored by the harvest ingest processor.
	 *
	 * @param  id             Collection ID
	 * @param  isManual       True if this is a manual trigger
	 * @param  runType        One of 'full_reharvest' | 'incremental_reharvest' | 'check'
     * @param  protocol       One of '' | 'harvestedRecordsDB'
	 * @exception  Exception  If error
	 */
	public void triggerHarvest(String id, String runType, boolean isManual, String protocol) throws Exception {
		if (runType == null || (!runType.equals("full_reharvest") && !runType.equals("test") ))
			throw new Exception("Invalid value for runType '" + runType + "'. Valid values are 'test' | 'full_reharvest'");
		
		synchronized (_harvestTriggerFileDir) {
			// Clean ingestor threads, which just removes the key value pair in a hash map
			// if the thread is finished. So we can test it to see if a collection is already
			// running

			if (this.getIngestorThreads().containsKey(id))
				throw new Exception("A harvest is already in progress for this collection, " +
						"cannot trigger another harvest until the other one is complete.");

			
			prtln("triggerHarvest() id: " + id + " protocol:'" + protocol + "'");

			String uuid = idToUuid(id);
			Date triggerDate = getUuidCreationDate(uuid);
			String triggerDateString = OAIUtils.getDatestampFromDate(triggerDate);

			Node collectionRecord = getCollectionRecordNode(id);
			if (collectionRecord == null)
				throw new Exception("Unable to find collection record for id '" + id + "'");

			Element ingestElement = (Element)collectionRecord.selectNodes("metadata/record/collection/ingest").get(0);
			
			Element protocolElement = (Element)ingestElement.elements().get(0);
			String nativeFormat = protocolElement.attributeValue("libraryFormat");
			
			if(protocol==null || protocol.equals(""))
			{
				protocol = protocolElement.getName();
			}

			String setSpec = collectionRecord.valueOf("metadata/record/collection/setSpec");
			
			String mdp_handle = collectionRecord.valueOf("head/additionalMetadata/collection_info/metadataHandle");
			String collection_id = collectionRecord.valueOf("head/id");

			
			// Make a collection_na that is file system safe (used in directory name):
			mdp_handle = mdp_handle.replaceAll("/", "-");
			//mdp_handle = mdp_handle.substring(mdp_handle.lastIndexOf('/') + 1, mdp_handle.length());

			// --- Create the trigger file --- :
			String triggerXML = Files.readFileFromJarClasspath("/edu/ucar/dls/harvestmanager/TRIGGER_FILE_TEMPLATE.xml").toString();
			// Remove all comments from the template XML, and insert the appropriate values for each datum:
			triggerXML = XMLUtils.removeXMLComments(triggerXML);
			
			triggerXML = triggerXML.replaceFirst("@COLLECTION_ID@", collection_id);
			triggerXML = triggerXML.replaceFirst("@COLLECTION_NA@", mdp_handle);
			triggerXML = triggerXML.replaceFirst("@SET_SPEC@", setSpec);			
			triggerXML = triggerXML.replaceFirst("@UUID@", uuid);
			triggerXML = triggerXML.replaceFirst("@RUN_TYPE@", runType);
			triggerXML = triggerXML.replaceFirst("@PROTOCOL@", protocol);
			triggerXML = triggerXML.replaceFirst("@NATIVE_FORMAT@", nativeFormat);
			
			if(protocol.equals("oai"))
			{
				String baseUrl = protocolElement.attributeValue("baseURL");
				String metadataPrefix = protocolElement.attributeValue("metadataPrefix");
				// If nothing in @metadataPrefix, use @format (the old way of doing it - can remove once catalog has been updated)
				if(metadataPrefix == null || metadataPrefix.length() == 0)
					metadataPrefix = protocolElement.attributeValue("format");
				triggerXML = triggerXML.replaceFirst("@BASE_URL@", baseUrl);
				triggerXML = triggerXML.replaceFirst("@FORMAT@", metadataPrefix);
			}
			else if(protocol.equals("lrSlice"))
			{
				String nodeUrl = protocolElement.attributeValue("nodeURL");
				triggerXML = triggerXML.replaceFirst("@BASE_URL@", nodeUrl);
				triggerXML = triggerXML.replaceFirst("@FORMAT@", "");
			}
			
			Document triggerFileDoc = Dom4jUtils.getXmlDocument(triggerXML);

			if(protocol.equals(Config.Protocols.OAI))
			{
				List mySets = collectionRecord.selectNodes("metadata/record/collection/ingest/oai/set");
				Element setsElement = (Element) triggerFileDoc.selectSingleNode("/*[local-name()='harvestRequest']/*[local-name()='sets']");
				if (mySets == null || mySets.size() == 0)
					setsElement.detach();
				else {
					for (int i = 0; i < mySets.size(); i++)
						setsElement.addElement("set").setText(((Element) mySets.get(i)).valueOf("@setSpec"));
				}
			}
			else if(protocol.equals(Config.Protocols.LR_SLICE))
			{
				Element rootElement = triggerFileDoc.getRootElement();
				Element publicKeyFilters = (Element)collectionRecord.selectNodes("metadata/record/collection/ingest/lrSlice/publicKeyFilters").get(0);
				rootElement.elements().add(publicKeyFilters.detach());
				Element sliceRequests = (Element)collectionRecord.selectNodes("metadata/record/collection/ingest/lrSlice/sliceRequests").get(0);
				rootElement.elements().add(sliceRequests.detach());
			}
			
			/*
			File name must be of the form [collection-id]_[datestamp]_harvest.xml
			for example: 3694837_2008-05-11T23-00-02Z_harvest.xml
			*/
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss'Z'");
			df.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
			String file_datestg = df.format(triggerDate);
			
			// Write the trigger file:
			Dom4jUtils.writePrettyDocToFile(triggerFileDoc, new File(_harvestTriggerFileDir, mdp_handle + "_" + file_datestg + "_harvest.xml"));
			
			this.startIngest(triggerFileDoc);
			
			String harvestType = (isManual ? "manual" : "automatic");
			harvestManagerPersistence.logTriggerEvent(collectionRecord.valueOf("metadata/record/general/title"), id, uuid, triggerDateString, harvestType, runType, protocol, triggerFileDoc);
		}
	}
	
	

	private void startIngest(Document triggerFileDoc) throws Exception {
			
			Element rootElement = Dom4jUtils.localizeXml(triggerFileDoc.getRootElement());
			
			String protocol = rootElement.valueOf("protocol");
			
			HarvestRequest harvestRequest = new HarvestRequest();
			
			harvestRequest.setBaseUrl(rootElement.valueOf("baseURL"));
			harvestRequest.setNativeFormat(rootElement.valueOf("nativeFormat"));
			harvestRequest.setSetSpec(rootElement.valueOf("collectionSetSpec"));
			harvestRequest.setMdpHandle(rootElement.valueOf("collectionNA"));
			String uuid = rootElement.valueOf("uuid");
			harvestRequest.setUuid(rootElement.valueOf("uuid"));
			harvestRequest.setRunType(rootElement.valueOf("runType"));
			//this should eventually be included in the trigger file
			harvestRequest.setProtocol(rootElement.valueOf("protocol"));
			String collectionId = rootElement.valueOf("collectionId");
			harvestRequest.setCollectionId(collectionId);
			
			if(protocol.equals(Config.Protocols.OAI))
			{
				harvestRequest.setMetadataPrefix(rootElement.valueOf("formats/format"));
						
				List<Element> mySets = rootElement.selectNodes("sets/set");			
				
				if(mySets!=null && mySets.size()!=0)
				{
					String[] setsArray = new String[mySets.size()];
					for (int i = 0; i < mySets.size(); i++)
					{
						setsArray[i] = ((Element) mySets.get(i)).getText();
					}
					harvestRequest.setCollection_sets(setsArray);
				}
			}
			else if(protocol.equals(Config.Protocols.LR_SLICE))
			{
				List<Element> publicKeyElements = rootElement.selectNodes("publicKeyFilters/publicKey");
				List<Map<String, Object>> publicKeys = new ArrayList<Map<String, Object>>();
				for(Element publicKeyElement:publicKeyElements)
				{
					HashMap<String, Object> publicKey = new HashMap<String, Object>();
					publicKey.put("url", publicKeyElement.getTextTrim());
					String checkHash = publicKeyElement.attributeValue("checkHash");
					
					if (checkHash==null || checkHash=="")
						checkHash = "false";
					publicKey.put("checkHash", Boolean.valueOf(checkHash));
					publicKeys.add(publicKey);
				}
				harvestRequest.setPublicKeys(publicKeys);
				
				List<Element> sliceRequestElements = rootElement.selectNodes("sliceRequests/sliceRequest");
				HashMap<String, List<List<String>>> sliceRequests = new HashMap<String, List<List<String>>>();
				
				for(Element sliceRequestElement:sliceRequestElements)
				{
					String requestParams = sliceRequestElement.valueOf("sliceRequestParams/text()");
					
					List<Element> payloadSchemaFilterElements = sliceRequestElement.selectNodes("payloadSchemaFilter");
					List<String> payloadSchemaFilters = new ArrayList<String>();
					
					for(Element payloadSchemaFilterElement:payloadSchemaFilterElements)
					{
						payloadSchemaFilters.add(payloadSchemaFilterElement.getText());
					}
					if(sliceRequests.containsKey(requestParams))
					{
						sliceRequests.get(requestParams).add(payloadSchemaFilters);
					}
					else
					{
						List<List<String>> payloadSchemaFiltersContainer = new ArrayList<List<String>>();
						payloadSchemaFiltersContainer.add(payloadSchemaFilters);
						sliceRequests.put(requestParams, payloadSchemaFiltersContainer);
					}
				}
				harvestRequest.setSliceRequests(sliceRequests);
				
			}
			Thread aThread = new Thread(harvestRequest);
			this._ingestorThreads.put(collectionId, aThread);
			aThread.start();
	}

	
	public void cleanIngestorThreads() throws Exception
	{
		
		for(Entry<String, Thread> ingestorThreadEnry: this._ingestorThreads.entrySet())
		{
			if(!ingestorThreadEnry.getValue().isAlive())
				this._ingestorThreads.remove(ingestorThreadEnry.getKey());
		}
		
		if(this._ingestorThreads.size()>=this._maxIngestorThreads)
		{
			throw new Exception(String.format("Max number(%d) of Harvest Ingestors has been reached. " +
					"This indiciates that threads are being hung up and not properly being interupted", this._maxIngestorThreads));
		}
	}
	
	private boolean interruptIngestorThread(String collectionId)
	{
		ConcurrentHashMap<String, Thread> ingestorThreads = null;
		try {
			ingestorThreads = this.getIngestorThreads();
		} catch (Exception e) {
			return false;
		}
		if(ingestorThreads.containsKey(collectionId))
		{
			// That means it is still alive
			Thread ingestorThread = ingestorThreads.get(collectionId);
			if(ingestorThread.isAlive())
			{
				ingestorThread.interrupt();
				return true;
			}
		}
		return false;
	}
	
	public void interruptAllIngestorThreads()
	{
		try 
		{
			for(Thread ingestorThread: this.getIngestorThreads().values())
			{
				if(ingestorThread.isAlive())
				{
					ingestorThread.interrupt();
				}
			}

			if(this.reharvesterThread!=null && this.reharvesterThread.isAlive())
				this.reharvesterThread.interrupt();
		
		// Give the threads five seconds to try to clean themselves up

			Thread.sleep(5000);
		}
		catch (Exception e1) {}
	}
	
	public void reharvestCollections(boolean isManual) throws Exception
	{
		if(this.reharvesterThread!=null && this.reharvesterThread.isAlive())
			throw new Exception("A reharvest for all collections is already in progress "+
					"cannot trigger another one until the other one is complete.");
		this.reharvesterThread = new Thread(this.reharvester);
		this.reharvesterThread.start();
	}
	
	/**
	 *  Sets the emailer used to notify when a harvest has been completed or has failed.
	 *
	 * @param  mailServer            The e-mail server, for example 'dls.ucar.edu'
	 * @param  mailType              One of 'store' (imap) or 'transport' (smtp)
	 * @param  toEmails              Array of e-mail addresses
	 * @param  fromEmail             The from address, for example "UCAR Metadata Repository"
	 *      <mr-ingest@ucar.org>
	 * @param  harvestManagerUrl     The URL to the HarvestManager web app, for example http://localhost/hm
	 * @param  ncsViewEditRecordUrl  URL to the DCS to pull up a single record for viewing/editing by appending
	 *      the NCS record ID.
	 */
	public void setEmailer(String mailServer, String mailType, String[] toEmails, String fromEmail, String harvestManagerUrl, String ncsViewEditRecordUrl) {
		_mailServer = mailServer;
		_mailType = mailType;
		_toEmails = toEmails;
		_fromEmail = fromEmail;
		_harvestManagerUrl = harvestManagerUrl;
		_ncsViewEditRecordUrl = ncsViewEditRecordUrl;
	}


	/**
	 *  Logs the status of a given harvest that was previously triggered and sends an e-mail notification. This
	 *  method is called by the harvest ingest processes to indicate the ultimate status of the harvest.
	 *
	 * @param  statusCode     The status 2=completion; 3=completion with errors or warnings; 4=failure
	 * @param  uuid           The UUID of the harvest that was indicated by the HarvestManager in the trigger
	 *      file
	 * @param  timeStamp      Timestamp
	 * @exception  Exception  If invalid UUID or other error processing
	 */
	public void logHarvestStatus(String statusCode, String description, String uuid, String timeStamp) throws Exception {

		if (uuid == null)
			throw new Exception("UUID was empty (null)");

		String id = getIdFromUuid(uuid);

		// Check to make sure there is a trigger for this UUID:
		Document harvestHistoryDocument = Dom4jUtils.localizeXml(harvestManagerPersistence.getHarvestTriggerDocument(id));

		// If no Document exists, error:
		if (harvestHistoryDocument == null)
			throw new Exception("The UUID '" + uuid + "' is not valid (1)");

		// If the triggerEvent does not exist, error:
		Element triggerEvent = (Element) harvestHistoryDocument.selectSingleNode("/TriggerHistory/triggerEvents/triggerEvent[@uuid='" + uuid + "']");
		if (triggerEvent == null)
			throw new Exception("The UUID '" + uuid + "' is not valid (2)");

		String prevStatus = null;
		Element harvestStatus = (Element) triggerEvent.selectSingleNode("harvestStatus");
		if (harvestStatus != null) {
			prevStatus = harvestStatus.valueOf("statusCode");
			//throw new Exception("The UUID '" + uuid + "' has already logged a status.");
		}

		boolean sendEmail = true;
		// the only time we don't send an email is a success that wasn't manually triggered
		if(!triggerEvent.attributeValue("harvestType").equals("manual") && statusCode.equals("2"))
			sendEmail = false;

		
		// If the e-mailer has been configured, attempt to send e-mail:
		// we do not email successful harvests
		if (sendEmail &&_mailServer != null && _fromEmail != null && _toEmails != null) {
			Node collectionRecord = getCollectionRecordNode(id);

			// From collection record/trigger event:
			String collectionTitle = collectionRecord.valueOf("metadata/record/general/title");
			String collectionSetSpec = 	collectionRecord.valueOf("metadata/record/collection/setSpec");
			String ncs_id = collectionRecord.valueOf("head/id");
			String mdp_handle = collectionRecord.valueOf("head/additionalMetadata/collection_info/metadataHandle");

			String baseUrl = triggerEvent.valueOf("triggerFile/harvestRequest/baseURL");
			String runType = triggerEvent.valueOf("triggerFile/harvestRequest/runType");

			String modeSubject = (runType != null && runType.equals("test") ? "(test mode)" : "");

			String subject = "Harvest Success " + modeSubject + " - " + collectionTitle;
			if (statusCode.equals("4") || statusCode.equals("5"))
				subject = "Harvest Failure " + modeSubject + " - " + collectionTitle;

			String mode = "A full harvest";
			if (runType != null && runType.equals("incremental_reharvest"))
				mode = "An incremental harvest";
			else if (runType != null && runType.equals("check"))
				mode = "A test harvest (no ingest)";
			String message = mode + " for collection '" + collectionTitle + "'\n\n";
			if (statusCode.equals("2"))
				message += "was successful.";
			else if (statusCode.equals("3"))
				message += "was successful, but with warnings.";
			else if (statusCode.equals("4"))
			{
				if(description==null)
					message += "failed.";
				else
					message += String.format("failed - %s.", description);
			}
			else if (statusCode.equals("5"))
				message += "failed (timout) - ingest process did not complete after waiting 3 or more days.";
			else if (statusCode.equals("6"))
				message += "was successful, but with record errors.";

			if (prevStatus != null && !prevStatus.equals(statusCode)) {
				message += "\n\nThe status for this harvest has changed. The previous status was ";
				if (prevStatus.equals("2"))
					message += "success.";
				else if (prevStatus.equals("3"))
					message += "success, with warning.";
				else if (prevStatus.equals("4"))
					message += "falure.";
				else if (prevStatus.equals("5"))
					message += "falure (timeout).";
				else if (prevStatus.equals("6"))
					message += "success, with record error.";
				message += "\n\nNew details about this harvest are now available.";
			}
			else if (prevStatus != null) {
				message += "\n\nNotification e-mail was previously sent for this harvest, however new details are now available.";
			}

			Date timeStampDate = null;
			try {
				timeStampDate = HarvestManagerUtils.getDateFromTimestamp(timeStamp);
			} catch (Throwable t) {}
			String timeStampDisplay = null;
			if (timeStampDate != null)
				timeStampDisplay = getDateLongDisplayString(timeStampDate);
			else
				timeStampDisplay = timeStamp;

			message += "\n\nDetails about this harvest may be viewed at:\n";
			message += _harvestManagerUrl + "/harvest_details.jsp?uuid=" + uuid;

			message += "\n\n\nwas triggered on " + getDateLongDisplayString(getUuidCreationDate(uuid)) + ",";
			message += "\nand completed on " + timeStampDisplay + ",";
			message += "\nproviding metadata for the UCAR collection:\n\n'" + collectionTitle + "'";
			message += "\nCollection Handle: " + mdp_handle;
			message += "\nDCS ID: " + ncs_id;
			message += "\nUCAR setSpec: " + collectionSetSpec;

			message += "\n\n\n";
			// The details are just the logs files
			message += "Harvest Details:\n";
			for(String logDetail: IngestorUtil.getLogDetailList(uuid))
				message += logDetail +"\n\n";
			prtln("Sending e-mail message (via mail server '" + _mailServer + "'):\n\nSubject: " + subject + "\n\n" + message);
			
			if(_mailServer.equals("dev"))
				this.sendDevEmail(subject, message);
			else
			{
				SendEmail emailer = new SendEmail(_mailType, _mailServer);
				try {
					emailer.doSendEmail(_toEmails, _fromEmail, subject, message);
				} catch (Throwable t) {
					prtlnErr("Unable to send e-mail: " + t);
				}
			}
		}

		harvestManagerPersistence.logHarvestStatus(statusCode, description, uuid, timeStamp);
	}

	
	private void sendDevEmail(String subject, String message)
	{
		// Set the next 4 lines to your external exchange server. Example here
		// is for gmail
		
		String emailHost = "smtp.gmail.com";
		String emailPort = "587";
		final String emailUser = "finke.dave@gmail.com";
		final String emailPassword = "ducati999r";
		
		Properties props = System.getProperties();
		props.put("mail.smtp.host", emailHost);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", emailPort);
		props.put("mail.store.protocol", "pop3");
	    props.put("mail.transport.protocol", "smtp");
	    
		Session session = Session.getInstance(props,
				  new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(emailUser, emailPassword);
					}
				  });
		
		Message mailMessage = new MimeMessage(session);
		try {
			mailMessage.setFrom(new InternetAddress(this._fromEmail));
		
			InternetAddress[] addressTo = 
				new javax.mail.internet.InternetAddress[this._toEmails.length];

			for (int i = 0; i < this._toEmails.length; i++)
			{
			    addressTo[i] = new javax.mail.internet.InternetAddress(this._toEmails[i]);
			}
			
			mailMessage.setRecipients(Message.RecipientType.TO,
					addressTo);
			mailMessage.setSubject(subject);
			mailMessage.setText(message);
			Transport.send(mailMessage);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 *  Helper method for the emailWeeklyHarvestSummary, this goes through the LastTriggeredData
	 *  document and groups the status code and statusCodeDescriptions in a way that can easily be
	 *  looped over. These groups will only contain triggered harvests that have happened in the last
	 *  seven days.
	 *  
	 *  @return HashMap<String, HashMap<String,Integer>> Hashmap containing the status code(1-6) and the
	 *                                                   corresponding counts for the statusCode descriptions
	 */
	private HashMap<String, HashMap<String,List<String>>> createSummaryMap() throws Exception
	{
		HashMap<String, HashMap<String,List<String>>> summaryGroupings = new HashMap<String, HashMap<String, List<String>>>();
		Document lastTriggerdDataDocument = harvestManagerPersistence.getLastTriggerdDataDocument();
		if(lastTriggerdDataDocument != null) {
			
			Element root = lastTriggerdDataDocument.getRootElement();
			List<Element> triggerElements = (List<Element>) root.selectNodes("HarvestTriggered");
			
			for(Element triggerElement: triggerElements)
			{
				String statusCode = triggerElement.attributeValue("statusCode");
				String statusCodeDscription = triggerElement.attributeValue("statusCodeDescription");
				String collectionID = triggerElement.attributeValue("id");
				
				// if the harvest is still processing don't add it
				if(statusCode==null || statusCode=="")
					continue;
				String timestampString = triggerElement.getText();

				Date timestamp = OAIUtils.getDateFromDatestamp(timestampString);
				
				Calendar triggerDate = Calendar.getInstance();
				triggerDate.setTime(timestamp);
				
				// This report is for a week worth of harvests
				triggerDate.add(Calendar.DAY_OF_YEAR, 7);
				Calendar rightNow = Calendar.getInstance();
				
				if (rightNow.compareTo(triggerDate)<1)
				{
					// status was changed this week add to maps
					HashMap<String, List<String>> descriptionMap = null;
					if(summaryGroupings.containsKey(statusCode))
					{
						descriptionMap = summaryGroupings.get(statusCode);
						List<String> idList = descriptionMap.get("all");
						idList.add(collectionID);
					}
					else
					{
						descriptionMap = new HashMap<String, List<String>>();
						List<String> idList = new ArrayList<String>();
						idList.add(collectionID);
						descriptionMap.put("all", idList);
						summaryGroupings.put(statusCode, descriptionMap);
					}
					
					if(descriptionMap.containsKey(statusCodeDscription))
					{
						List<String> idList = descriptionMap.get(statusCodeDscription);
						idList.add(collectionID);
					}
					else
					{
						List<String> idList = new ArrayList<String>();
						idList.add(collectionID);
						descriptionMap.put(statusCodeDscription, idList);
					}
				}
			}
		}
		return summaryGroupings;
	}
	/**
	 *  Emails a report summarizing all harvests that happened over a 7 day time period.
	 */
	 
	public void emailWeeklyHarvestSummary()
	{
		if (_mailServer == null || _fromEmail == null || _toEmails == null)
			return;
			
		HashMap<String, HashMap<String,List<String>>> summaryCountsMap;
		summaryCountsMap = null;
		try {
			summaryCountsMap = this.createSummaryMap();
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		 StringBuilder stringBuilder = new StringBuilder();
		 
		 // Definitions on how to display each of the status codes
		 String[][] definitions = {{"4", "Harvest(s) Failed", "true"},
				   				   {"5", "Harvest(s) Timed Out", "false"},
				 				   {"6", "Successful Harvest(s) with record errors", "false"},
				 				   {"3", "Successful Harvest(s) with warnings", "false"},
				 				   {"2", "Successful Harvest(s)", "false"},
				 				   };
		 
		 int totalHarvests = 0;
		 for(int defIndex=0; defIndex<definitions.length; defIndex++)
		 {
			 String statusCode = definitions[defIndex][0];
			 String title = definitions[defIndex][1];
			 boolean showStatusDecriptionsSummary = new Boolean(
					 definitions[defIndex][2]).booleanValue();
			 
			 HashMap<String,List<String>> descriptionMap = null;
			 int count = 0;
			 if(summaryCountsMap.containsKey(statusCode))
			 {
				 descriptionMap = summaryCountsMap.get(statusCode);
				 count = descriptionMap.get("all").size();
				 totalHarvests += count;
			 }
			 if(count!=0)
			 {
				 // Show the status code description and the count of how many
				 stringBuilder.append(String.format("%d %s\n", count, title));
				 
				 if(showStatusDecriptionsSummary)
				 {
					 // If for this status we want to give the break down per description loop through 
					 // them
					 for(Entry<String, List<String>> descriptionEntry: descriptionMap.entrySet())
					 {
						 // We don't want to show the all category, just everything else
						 if(descriptionEntry.getKey()!="all")
						 {
							 stringBuilder.append(String.format("\t%d - %s\n",
								 descriptionEntry.getValue().size(), descriptionEntry.getKey()));
							 stringBuilder.append(String.format("\t%s\n\n",
									 createCollectionListDisplay(descriptionEntry.getValue())));
						 }
					 }
				 }
				 else
				 {
					 stringBuilder.append(String.format("\t%s\n\n",
							 createCollectionListDisplay(descriptionMap.get("all"))));
				 }
			 } 
			 
		}
		stringBuilder.append(String.format(
				 "\nDetails about these harvests may be viewed at \n%s",
				 		_harvestManagerUrl));

		SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");

		
		String dateAsString = sdf.format(new java.util.Date());
		stringBuilder.insert(0, String.format("Harvest Summary for week ending %s:\n\n%d harvest(s) were triggered.\n\n", dateAsString, totalHarvests));
		
		String subject = "!!! WEEKLY HARVEST SUMMARY !!!";
		if(_mailServer.equals("dev"))
			this.sendDevEmail(subject, stringBuilder.toString());
		else
		{
			SendEmail emailer = new SendEmail(_mailType, _mailServer);
			try {
				emailer.doSendEmail(this._toEmails, this._fromEmail, subject, stringBuilder.toString());
			} catch (Throwable t) {
				prtlnErr("Unable to send e-mail: " + t);
			}
		}
		
		 
	}
	
	
	private String createCollectionListDisplay(List<String> collectionIds) {
		List<String> collectionNames = new ArrayList<String>();
		for(String collectionID: collectionIds )
		{
			Node collectionRecord = getCollectionRecordNode(collectionID);

			if(collectionRecord == null)
			{
				continue;
			}
			// From collection record/trigger event:
			String collectionTitle = collectionRecord.valueOf("metadata/record/general/title");
			collectionNames.add(collectionTitle);
		}
		Collections.sort(collectionNames);
		return StringUtils.join(collectionNames, "\n\t");
	}


	/**
	 *  Gets the ID of the next collection to harvest based on past harvests logged.
	 *
	 * @return    The nextCollectionToHarvest value
	 */
	private String getNextCollectionToHarvest() {
		// Returns the ID of the next collection to harvest, or null if none are ready/stale now

		// The list of nodes that contain a non-empty baseURL, sorted by frequency (most frequent first)
		List recordNodes = (List) getCollectionsDocument().selectNodes("/DDSWebService/Search/results/record[string-length(metadata/record/collection/ingest/oai/@baseURL)>0]", "metadata/record/collection/ingest/oai/@frequency");

		if (recordNodes == null || recordNodes.size() == 0)
			return null;

		for (int j = 0; j < recordNodes.size(); j++) {
			Node recordNode = (Node) recordNodes.get(j);
			String id = recordNode.valueOf("head/id");

			String frequency = recordNode.valueOf("metadata/record/collection/ingest/oai/@frequency");

			float freq = 0f;
			try {
				freq = Float.parseFloat(frequency);
			} catch (NumberFormatException e) {}

			Date lastTriggerDate = null;
			LastTriggeredDAO lastTriggeredDAO = getLastTriggeredDAO(id);
			if (lastTriggeredDAO != null)
				lastTriggerDate = lastTriggeredDAO.getLastTriggeredDate();

			boolean needsHarvest = isNeedingHarvest(freq, lastTriggerDate);

			//if(freq > 0)
			//prtln("id: " + id + " freqency: " + frequency + " lastHarvest: " + lastTriggerDate + " isNeedignHarvest: " + needsHarvest);

			if (needsHarvest)
				return id;
		}
		return null;
	}


	/**
	 *  True if a collection needs a harvest based on the number of months between harvests (frequency) and the
	 *  last harvest date.
	 *
	 * @param  numMonthsBetweenHarvests  Num months between harvests
	 * @param  lastHarvest               Last harvest date
	 * @return                           The needingHarvest value
	 */
	private boolean isNeedingHarvest(float numMonthsBetweenHarvests, Date lastHarvest) {
		if (numMonthsBetweenHarvests <= 0)
			return false;
		if (lastHarvest == null)
			return true;

		// Calculate the date of the next harvest based on the last:
		Date nextHarvestDate = nextHarvestDate(numMonthsBetweenHarvests, lastHarvest);

		boolean doHarvest = false;
		Date rightNow = getRightNowDate();

		if (nextHarvestDate.compareTo(rightNow) > 0)
			doHarvest = false;
		else
			doHarvest = true;

		//prtln("isNeedingHarvest() freq: " + numMonthsBetweenHarvests + " [last:" + lastHarvest + "] [today:" + rightNow.getTime() + "] [next:" + nextHarvestDate.getTime() + "]: " + doHarvest);

		return doHarvest;
	}


	/**
	 *  Gets the next harvest date based on the number of months between harvests (frequency) and the last
	 *  harvest date.
	 *
	 * @param  numMonthsBetweenHarvests  Num months between harvests
	 * @param  lastHarvest               Last harvest date
	 * @return                           Next harvest date
	 */
	private Date nextHarvestDate(float numMonthsBetweenHarvests, Date lastHarvest) {
		if (numMonthsBetweenHarvests <= 0 || lastHarvest == null)
			return null;

		// Calculate the date of the next harvest based on the last:
		Calendar nextHarvestDate = Calendar.getInstance();
		nextHarvestDate.setTime(lastHarvest);

		// Convert months to days-per-year and add that many days:
		nextHarvestDate.add(Calendar.DAY_OF_YEAR, Math.round((365 / 12) * numMonthsBetweenHarvests));
		return nextHarvestDate.getTime();
	}


	/**
	 *  Returns the number of harvests we need to do each day to satisfy the required number each month, evenly
     *  distributing harvests across the whole month.
	 *
	 * @return    The numHarvestsPerDay value
	 */
	private int getNumHarvestsPerDay() {

		List recordNodes = (List) getCollectionsDocument().selectNodes("/DDSWebService/Search/results/record");
		//prtln("Fetch num recordNodes:" + recordNodes.size());

		if (recordNodes == null || recordNodes.size() == 0)
			return 0;

		double total = 0d;
		for (int j = 0; j < recordNodes.size(); j++) {
			String frequency = ((Node) recordNodes.get(j)).valueOf("metadata/record/collection/ingest/oai/@frequency");

			double freq = 0d;
			try {
				freq = Double.parseDouble(frequency);
			} catch (NumberFormatException e) {}

			//prtln("freqency: " + frequency + ", num per month: " + (1d/freq));
			if (freq > 0d)
				total += (1d / freq);
		}

		//prtln( "getNumHarvestsPerDay() total: " + total);
		return (int) (Math.round(Math.ceil(total / 28d)));
	}


	/**
	 *  Gets the harvest trigger Document for the given collection, which contains the full history of harvest
	 *  triggers.
	 *
	 * @param  id             The collection ID
	 * @return                The harvestTriggerDocument value
	 * @exception  Exception  If error
	 */
	public Document getHarvestTriggerDocument(String id) throws Exception {
		return harvestManagerPersistence.getHarvestTriggerDocument(id);
	}


	/**
	 *  True if the given collection has a previous harvest history.
	 *
	 * @param  id  The collection ID
	 * @return     The hasHarvestTriggerHistory value
	 */
	public boolean getHasHarvestTriggerHistory(String id) {
		return harvestManagerPersistence.getHasHarvestTriggerHistory(id);
	}


	/**  Shut down the HarvestManager */
	public void destroy() {
		this.interruptAllIngestorThreads();
		stopHarvestTimer();
	}


	/**
	 *  Start or restart the automatic harvest timer with the given update frequency, beginning at the specified
	 *  time. Valid frequencies currently are 0 (never) or 86400 (every 24 hours). If the requested start time
	 *  has already past the timer will start at then next appropriate interval. Set frequency to 0 to disable
	 *  automatic harvests.
	 *
	 * @param  frequency      The number of seconds between harvest updates, or 0 to disable
	 * @param  startTime      The time of day at which start the harvest process in H:mm, for example 0:35 or
	 *      23:35. Use null to start immediately.
	 * @exception  Exception  If error
	 */
	public synchronized void startHarvestTimer(long frequency, String startTime) throws Exception {
		// Make sure the timer is stopped before starting...
		stopHarvestTimer();

		// 86400 seconds is 24 hours...

		if (frequency == 86400) {

			Date startTimeDate = null;

			// Start the indexer timer
			if (startTime != null) {
				Date currentTime = new Date();
				int dayInYear = Integer.parseInt(Utils.convertDateToString(currentTime, "D"));
				int year = Integer.parseInt(Utils.convertDateToString(currentTime, "yyyy"));
				startTimeDate = Utils.convertStringToDate(year + " " + dayInYear + " " + startTime, "yyyy D H:mm");
				// Make sure the time to start is not in the past:
				while (currentTime.compareTo(startTimeDate) > 0)
					startTimeDate = new Date(startTimeDate.getTime() + frequency * 1000);
			}
			else
				startTimeDate = new Date();

			// Set daemon to true to stop with JVM:
			_harvestTimer = new Timer(true);

			// Convert seconds to milliseeconds
			long freq = ((frequency > 0) ? (frequency * 1000) : 60000);

			// Start the auto harvester at regular intervals beginning at the specified Date/Time
			try {
				prtln("Automatic harvest timer is scheduled to start " +
					Utils.convertDateToString(startTimeDate, "EEE, MMM d, yyyy h:mm a zzz") +
					", and run once every 24 hours.");
			} catch (ParseException e) {}

			_harvestTimer.scheduleAtFixedRate(new HarvestTriggerTimer(), startTimeDate, freq);

			prtln("Automatic harvest timer started");
		}
		else if (frequency == 0) {
			prtln("Automatic harvest timer disabled. Manual harvests only.");
		}
		else {
			throw new Exception("Frequency must be 0 or 86400");
		}
	}


	/**  Stops the harvest timer. */
	public synchronized void stopHarvestTimer() {
		if (_harvestTimer != null) {
			_harvestTimer.cancel();
			_harvestTimer = null;
			prtln("Automatic harvest timer stopped");
		}
	}


	/**
	 *  Triggers harvests at the appropriate intervals.
	 *
	 * @author    John Weatherley
	 */
	private class HarvestTriggerTimer extends TimerTask {
		/**  Main processing method for this thread. */
		public void run() {
			// On friday send the summary report out
			
			Calendar calendarDay = Calendar.getInstance();
			if(calendarDay.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY)
			{
				emailWeeklyHarvestSummary();
			}
			
			prtln("Harvest trigger timer run()...");
			try {
				updateCollectionsFromDCS();
			} catch (Throwable t) {
				prtlnErr("HarvestTriggerTimer Unable to fetch collection records from the DCS: " + t);
			}
			doHarvestSession();
			
			// The last day of the month will automatically re-harvest all collections from the harvested
			// Records database if configured to do so:
			if (_doReprocessCollectionsMonthly && (calendarDay.get(Calendar.DAY_OF_MONTH) == calendarDay.getActualMaximum(Calendar.DAY_OF_MONTH)))
			{
				try {
					reharvestCollections(false);
				} catch (Exception e) {
					prtlnErr("unable to initiate a re-harvest of all collections: " + e);
				}
			}
		}
	}


	//========= Testing methods and classed ==========================================

	// A Calendar/Date for right now that can be incremented for testing

	// This method is also used in normal operation mode to get the current date/time
	private Calendar getRightNowCalendar() {
		Calendar rightNow = Calendar.getInstance();
		if (numDaysToIncrementForTesting > 0)
			rightNow.add(Calendar.DAY_OF_MONTH, numDaysToIncrementForTesting);
		return rightNow;
	}

	// This method is also used in normal operation mode to get the current date
	private Date getRightNowDate() {
		return getRightNowCalendar().getTime();
	}


	private TesterThread testerThread = null;


	/**
	 *  Simulates harvests being run for a given number of days. Runs as a thread in the background.
	 *
	 * @param  numDaysToSimulate  Number of simulated days to run harvests
	 * @return                    A confirm message
	 */
	public synchronized String runSimulatedHarvests(int numDaysToSimulate) {
		// Do some tests for (n) days:
		if (testerThread == null) {
			testerThread = new TesterThread(numDaysToSimulate);
			testerThread.start();
			return "Simulating harvests run for " + numDaysToSimulate + " days.";
		}
		return " No action performed. Simulatation was previously initiated for " + numDaysToSimulate + " days.";
	}


	private int numDaysToIncrementForTesting = 0;


	private void testTriggers(int numDays) throws Exception {

		System.out.println(getDateStamp() + " start testTriggers()");
		/* 	To-Do List:
			-check / debug the isNeedingHarvest() method!
			- Get the metadataProvider handle and place it in the collectionNA
			element in the trigger file (instead of DCS-id)
			- Test!
			Done List:
			- Implemented writing of the trigger file, the last harvest date log
			and the history of all harvests for a given collection log.
		*/
		// Test triggering some for xxx days...
		for (int i = 0; i < numDays; i++) {
			doHarvestSession();
			numDaysToIncrementForTesting++;
		}
		

		System.out.println(getDateStamp() + " end testTriggers()");
	}


	private class TesterThread extends Thread {
		private int _numDays = 0;


		/**
		 *  Constructor for the TesterThread object
		 *
		 * @param  numDays  Num days to of summulated harvests
		 */
		public TesterThread(int numDays) {
			_numDays = numDays;
		}


		/**  Main processing method for the TesterThread object */
		public void run() {
			try {
				sleep(1500);
				testTriggers(_numDays);
			} catch (Throwable th) {}
		}
	}


	//=========== Debugging/logging ===============================================


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
	private final void prtlnErr(String s) {
		System.err.println(getDateStamp() + " HarvestManager Error: " + s);
	}



	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final void prtln(String s) {
		if (debug)
			System.out.println(getDateStamp() + " HarvestManager: " + s);
	}


	/**
	 *  Sets the debug attribute.
	 *
	 * @param  db  The new debug value
	 */
	public final void setDebug(boolean db) {
		debug = db;
	}
	
	public ConcurrentHashMap<String, Thread> getIngestorThreads() throws Exception {
		this.cleanIngestorThreads();
		return this._ingestorThreads;
	}
	public Reharvester getReharvester() {
		return reharvester;
	}
}

