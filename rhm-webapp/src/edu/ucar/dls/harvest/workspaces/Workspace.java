package edu.ucar.dls.harvest.workspaces;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.util.Date;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.resources.Resource;

/**
 * Abstract class that represents where we store all the record data in 
 * since we can't keep it all in memory. This class is abstract so a workspace
 * could be changed easily to another form besides DB. Like Couch or direct files.
 * There are  15 methods that need to be implemented to create a new type of workspace
 * so its not an easy task but possible
 *
 */
public abstract class Workspace {
	public static String SUCCESS_RESULT = "Success";
	public static String FAILURE_RESULT = "Failed";

	protected File workingFileDirectory=null;
	protected File collectionDirectory=null;
	
	public HarvestRequest harvestRequest = null;

	// Three different types of data need to be stored per record
	// original, validated ie native format, and transformed_record aka target
	public static String ORIGINAL_RECORD_TYPE = "original_record";
	public static String VALIDATED_RECORD_TYPE = "validated_record";
	public static String TRANSFORMED_RECORD_TYPE = "transformed_record";
	public String targetFormat = null;
	protected boolean populateResourceHandles = false;
	protected String harvestDataFormat = null;
	protected HashMap<Class<? extends Resource>, Resource> resources = null;
	

	/**
	 * Initialize the workspace with the harvest request
	 * @param harvestRequest
	 * @throws HarvestException
	 */
	public void initialize(HarvestRequest harvestRequest) throws HarvestException
	{
		this.harvestRequest = harvestRequest;
		this.resources = new HashMap<Class<? extends Resource>, Resource>();
		// working directory is under the format
		// baseDir/Mdp collection handle/session
		this.collectionDirectory = new File(
				Config.BASE_FILE_PATH_STORAGE, harvestRequest.getMdpHandle());
		this.workingFileDirectory = new File(
				this.collectionDirectory,
				this.harvestRequest.getUuid());
		
		// We want to clean it and make directories just in case the same session
		// id was used multiple times. Otherwise we would have issues once we
		// start putting them in the repos
		this.cleanWorkingDirectory();
		this.workingFileDirectory.mkdirs();
		this.createStatusFile();
	}
	
	/**
	 * Initialize the workspace without a harvest request
	 * @param mdpHandle
	 * @param uuid
	 * @throws HarvestException
	 */
	public void initialize(String mdpHandle, String uuid) throws HarvestException
	{
		// working directory is under the format
		// baseDir/Mdp collection handle/session
		this.collectionDirectory = new File(
				Config.BASE_FILE_PATH_STORAGE, mdpHandle);
		this.workingFileDirectory = new File(
				this.collectionDirectory,
				uuid);
		this.resources = new HashMap<Class<? extends Resource>, Resource>();
	}
	
	
	// Note getRecords must return the valid ordered by partnerid. This method
	// is used in procesors for reporting. So sorting by partnerid is very important
	
	/**
	 * Method that accepts the record type which will be ORIGINAL_RECORD_TYPE,
	 * VALIDATED_RECORD_TYPE and TRANSFORMED_RECORD_TYPE. This method must return
	 * a ResultsWrapper with the results, null or throw an exception.
	 * Data should come back as record_id, $recordDataType described above, partner_id
	 */
	public abstract ResultsWrapper getRecords(String recordType) throws HarvestException;
	
	/**
	 * Method that updates a certain record's record type. Again record type must be
	 * ORIGINAL_RECORD_TYPE, VALIDATED_RECORD_TYPE and TRANSFORMED_RECORD_TYPE
	 * Record id is the workspace  id for the reocrd not the partner id
	 * @param recordId
	 * @param recordType
	 * @param updatedRecord
	 * @return
	 * @throws HarvestException
	 */
	public abstract boolean updateRecord(String recordId, String recordType,
										String updatedRecord) throws HarvestException;
	
	/**
	 * Get all valid records and all data associated with it. Note this should only
	 * return valid records, not errored or invalid. Since this is used for populating
	 * the repost
	 * @return
	 * @throws HarvestException
	 */
	public abstract ResultsWrapper getRecordsAllData() throws HarvestException;
	
	public abstract ResultsWrapper getHarvestedRecords() throws HarvestException;
	
	
	/**
	 * The call that puts the record initially into the workspace. Its perfable
	 * that any subclass populated the three types ORIGINAL_RECORD_TYPE, VALIDATED_RECORD_TYPE and 
	 * TRANSFORMED_RECORD_TYPE with recordDocument. Even though it might change them later.
	 * This is preferable just in case a certain case a processor isn't called to populate.
	 * them later on
	 * @param partnerId
	 * @param recordDocument
	 * @param url
	 * @throws HarvestException
	 */
	public abstract void populateRecord(String partnerId, String recordDocument, String url, Date workingDate) throws HarvestException;

	/**
	 * Marks the records as having errors. Record Ids are the ids given to the record by
	 * the workspace, not the partnerid
	 * @param recordIds
	 * @return
	 * @throws HarvestException
	 */
	public abstract boolean markErrorRecords(String[] recordIds) throws HarvestException;
	
	/**
	 * Marks the records as invalid. Record Ids are the ids given to the record by
	 * the workspace, not the partner id. This is different then an error because a
	 * invalid record should not be reported on since. the record in question should
	 * never have been in the harvest anyway. An example of this would be a duplicate
	 * record. One that is in the harvest twice
	 * @param recordIds
	 * @return
	 * @throws HarvestException
	 */
	public abstract boolean markInvalidRecords(String[] recordIds) throws HarvestException;
	
	/**
	 * Gets the invalid record count number in the workspace
	 * @return
	 * @throws HarvestException
	 */
	public abstract int getErrorRecordCount() throws HarvestException;
	
	/**
	 * Gets all the data associated for a certain record. Where record id is the id
	 * given to the record by the workspace not the partner id. Data should come
	 * back in order as partner_id, original_record, validated_record, transformed_record, 
	 * URL
	 * @param recordId
	 * @return
	 * @throws HarvestException
	 */
	public abstract ResultsWrapper getRecordData(String recordId) throws HarvestException;
	
	/**
	 * Gets the record count of the workspace. This count should not include invalid records
	 * only valid and errored
	 * @return
	 * @throws HarvestException
	 */
	public abstract int getRecordCount() throws HarvestException;
	
	/**
	 * Gets the valid record count in the database
	 * @return
	 * @throws HarvestException
	 */
	public abstract int getValidRecordCount() throws HarvestException;
	
	/**
	 * inserts the workspace uri for this record into the workspace for use later one
	 * @param recordId
	 * @param url
	 * @return
	 * @throws HarvestException
	 */
	public abstract boolean insertURI(String recordId, String url) throws HarvestException;
	
	/**
	 * A method to get record ids based on a partner id. This is used to check for duplicates
	 * so this should return any records, invalid valid and errored
	 * @param partnerId
	 * @return
	 * @throws HarvestException
	 */
	public abstract ResultsWrapper getRecordIdsByPartnerId(String partnerId, String order) throws HarvestException;
	
	
	/**
	 * A method to get partner ids by records ids and sorted by record id. Sorting by record
	 * Id might seem weird by record id is how it appears in the harvest file and since this
	 * is only used for putting partner ids into a text file so one can view all the records
	 * this makes it more straightforward for debugging
	 * @param String[] record Ids
	 * @return
	 * @throws HarvestException
	 */
	public abstract ResultsWrapper getPartnerIdsByRecordIds(String[] recordIds) throws HarvestException;
	
	/**
	 * Get duplicate counts in the workspace. This is used for determining duplicate
	 * records. The Results should be object[] = [url, count of records]. And should
	 * only be returned for records that have more then one records associated with them
	 * per url
	 * 
	 * @return
	 * @throws HarvestException
	 */
	public abstract ResultsWrapper getDuplicateRecordCountsByURL() throws HarvestException;
	
	/**
	 * Get duplicate counts in the workspace. This is used for determining duplicate
	 * records. The Results should be object[] = [partnerId, count of records]. And should
	 * only be returned for records that have more then one records associated with them
	 * per partner id
	 * 
	 * @return
	 * @throws HarvestException
	 */
	public abstract ResultsWrapper getDuplicateRecordCountsByPartnerID() throws HarvestException;
	
	/**
	 * Gets record ids by url, This method can also take a param called order which will order
	 * them based on the record id. Can be Desc or ASC and are defined as constants in Config
	 * @param url
	 * @param order
	 * @return
	 * @throws HarvestException
	 */
	public abstract ResultsWrapper getRecordIdsByURL(String url, String order) throws HarvestException;
	
	/**
	 * Updates the handles(metadata and resource) for a specific record defined by recordId
	 * @param recordId
	 * @param metadatahandle
	 * @param resourcehandle
	 * @return
	 * @throws HarvestException
	 */
	public abstract boolean updateHandles(String recordId, String metadatahandle, String resourcehandle) 
	throws HarvestException;
	
	/**
	 * Clean the workspace
	 */
	public void clean()
	{
		if(this.collectionDirectory!=null)
			this.cleanPastSessions();
		
		// Make sure all the resources are cleaned up
		for(Resource resource: this.resources.values())
		{
			resource.clean();
		}
		this.resources.clear();
	}
	
	/**
	 * Clean a specified Resource from the workspace
	 * @param resourceClass
	 */
	public void cleanResource(Class<? extends Resource> resourceClass)
	{
		if(this.resources.containsKey(resourceClass))
		{
			this.resources.get(resourceClass).clean();
			this.resources.remove(resourceClass);
		}
				
	}
	protected void cleanWorkingDirectory()
	{
		try {
			FileUtils.deleteDirectory(this.workingFileDirectory);
		} catch (IOException e) {

		}
	}
	public File getWorkingFileDirectory() {
		return workingFileDirectory;
	}
	
	public void setTargetFormat(String targetFormat) {
		this.targetFormat = targetFormat;
	}
	public boolean isPopulateResourceHandles() {
		return populateResourceHandles;
	}

	public void setPopulateResourceHandles(boolean populateResourceHandles) {
		this.populateResourceHandles = populateResourceHandles;
	}
	
	
	public File getStatusFile() {
		return Workspace.getStatusFile(this.getWorkingFileDirectory());
	}
	
	public static File getStatusFile(File directory)
	{
		return new File(directory, "status.xml");
	}
	
	public String getStatus() {
		return Workspace.getStatus(this.getWorkingFileDirectory());
	}
	
	/**
	 * Determines what the status is of this harvest by taking a look at the status
	 * xml that is maintained in the file directory
	 * @param directory
	 * @return
	 */
	public static String getStatus(File directory)
	{
		try {
			File statusFile = Workspace.getStatusFile(directory);
			Document document=null;
			document = Dom4jUtils.getXmlDocument(statusFile,Config.ENCODING);
			
			Element rootElement = document.getRootElement();
			Element statusElement = (Element)rootElement.selectSingleNode("status");
			if(statusElement==null)
				return "";
			return statusElement.getText();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * Determines a protocol for a given directory structure
	 * @param directory
	 * @return
	 */
	public static String getProtocol(File directory)
	{
		try {
			File statusFile = Workspace.getStatusFile(directory);
			Document document=null;
			document = Dom4jUtils.getXmlDocument(statusFile,Config.ENCODING);
			
			Element rootElement = document.getRootElement();
			Element protocolElement = (Element)rootElement.selectSingleNode("harvestProtocol");
			if (protocolElement==null)
				return "";
			return protocolElement.getText();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void setResult(String status) throws HarvestException {
		try {
			File statusFile = this.getStatusFile();
			Document document = Dom4jUtils.getXmlDocument(statusFile);
			Element rootElement = document.getRootElement();
			Element statusElement = (Element)rootElement.selectSingleNode("status");
			statusElement.setText(status);
			Dom4jUtils.writeDocToFile(document, statusFile);
		} catch (Exception e) {
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
					"Could not set result status in statusFile.", e);
		} 
	}
	
	/**
	 * Create status file that resides in harvested records folder.
	 * @throws HarvestException
	 */
	protected void createStatusFile() throws HarvestException
	{
		try {
			Document document = Dom4jUtils.getXmlDocument(String.format(
					"<document><setSpec>%s</setSpec><status/><harvestProtocol>%s</harvestProtocol></document>", 
					this.harvestRequest.getSetSpec(),this.harvestRequest.getProtocol()));
			Dom4jUtils.writeDocToFile(document, this.getStatusFile());
		} catch (Exception e) {
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
					"Could not create statusFile.", e);
		}
	}
	
	/**
	 * Cleanup method that insures that the files don't get out of hand.
	 * In the end we want the the number of success sessions that is defined
	 * in the Config file and number of failure sessions. A session that wasn't
	 * cleaned up for some reason will have a success of <success/> which is 
	 * considered a failure
	 */
	protected void cleanPastSessions()
	{
		if(!this.collectionDirectory.exists())
			return;
		File[] sessionDirectories = this.collectionDirectory.listFiles();
		if(sessionDirectories!=null && sessionDirectories.length==0)
			return;
		Arrays.sort(sessionDirectories, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		
		int failureCount = 0;
		int successCount = 0;
		boolean nonSourceHarvest = false;
		int nonSourceHarvestCount = 0;
		for(File sessionDirectory:sessionDirectories)
		{
			String workspaceStatus = Workspace.getStatus(sessionDirectory);
			String harvestProtocol = Workspace.getProtocol(sessionDirectory);
			
			// We only want 1 non source harvest and for sources we keep 
			// whatever the max storage sessions is set for
			if(harvestProtocol.equals(Config.Protocols.HARVESTED_RECORDS_DB))
				nonSourceHarvest = true;
			else
				nonSourceHarvest = false;
			if(nonSourceHarvest && nonSourceHarvestCount==0)
			{
				nonSourceHarvestCount++;
				continue;
			}

			if(!nonSourceHarvest && 
					successCount<Config.Reporting.MAX_STORAGE_OF_SUCCESS_SESSIONS && 
					workspaceStatus.equals(SUCCESS_RESULT))
			{
				successCount++;
				continue;
			}
			else if(!nonSourceHarvest && 
					failureCount<Config.Reporting.MAX_STORAGE_OF_FAILURE_SESSIONS && 
					!workspaceStatus.equals(SUCCESS_RESULT))
			{
				
				failureCount++;
				continue;
			}
			// This is a just in case. The current session should be the very first
			// one so either should be used as the success or failure. Just 
			// to make sure for some odd reason, we don't want to delete it
			if(!sessionDirectory.equals(this.workingFileDirectory)) 
			{
				try {
					FileUtils.deleteDirectory(sessionDirectory);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Get a resource or create one if it wasn't already created, this way
	 * processors can share resources and not have to worry about cleaning them up
	 * @param resourceClass
	 * @return
	 * @throws HarvestException
	 */
	public Resource getResource(Class<? extends Resource> resourceClass) throws HarvestException
	{
		if(!this.resources.containsKey(resourceClass))
			try {
				this.resources.put(resourceClass, (Resource)resourceClass.getDeclaredConstructor(
						Workspace.class).newInstance(this));
			} catch (Exception e) {
				throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
						String.format("Unable to instantiate Resource %s", resourceClass.toString()),
								e); 
			}
		return this.resources.get(resourceClass);
	}
	
	/**
	 * Gets defined harvest data format
	 * @return
	 */
	public String getHarvestDataFormat() {
		return harvestDataFormat;
	}
	
	/**
	 * Sets the harvest data format
	 * @param harvestDataFormat
	 */
	public void setHarvestDataFormat(String harvestDataFormat) {
		this.harvestDataFormat = harvestDataFormat;
	}
	public void cleanupAfterIteration() throws HarvestException
	{
		
	}
}
