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
package edu.ucar.dls.harvest.workspaces;

import java.util.Date;
import java.util.HashMap;

import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.resources.Resource;


/**
 * A Database implementation of the workspace. This was written for MySQL but
 * with a few small modifications to the CREATE_TABLE_STMT probably could 
 * switch it over pretty easily to something else
 */
public class DummyWorkspace extends Workspace{
	
	/**
	 * Initialize the workspace via the harvest request. This initializes
	 * the workspace db table we are using.
	 */
	public void initialize(HarvestRequest harvestRequest) throws HarvestException
	{
		this.harvestRequest = harvestRequest;
		this.resources = new HashMap<Class<? extends Resource>, Resource>();
	}
	
	public void initialize(String mdpHandle, String uuid) throws HarvestException
	{

	}
	
	
	
	/**
	 * Implemented method that is required, Returns records with their data
	 * defined by record type. Record type must be Constants
	 * ORIGINAL_RECORD_TYPE, VALIDATED_RECORD_TYPE, or TRANSFORMED_RECORD_TYPE
	 * @return
	 * @throws HarvestException
	 */
	public ResultsWrapper getRecords(String recordType) throws HarvestException
	{
		return null;
	}
	
	/**
	 * Implemented method that is required. This returns all valid records and all the 
	 * data associated with them. This is used for populating the repos
	 */
	public ResultsWrapper getRecordsAllData() throws HarvestException
	{
		return null;
	}
	
	/**
	 * Get records that were harvested from the workspace. These include ones that
	 * have errored out
	 */
	public ResultsWrapper getHarvestedRecords() throws HarvestException
	{
		return null;
	}
	
	/**
	 * Method that updates a records specified record type
	 */
	public boolean updateRecord(String recordId, String recordType, String updatedRecord) 
				throws HarvestException
	{
		return true;
	}
	
	

	
	
	
	/**
	 * Cleans the current sessions workspace. This is called at the very end
	 * of the ingest proess. This method is very important because we do not
	 * want old session workspace tables hanging around wasting space. Also
	 * note the super.clean(). Base class does its own cleaning
	 */
	public void clean()
	{	

		super.clean();
	}
	
	
	
	
	
	/**
	 * Method that initially populates the record into the workspace, for
	 * consistency sake we populate all the record types with the original 
	 * document ORIGINAL_RECORD_TYPE, VALIDATED_RECORD_TYPE, and TRANSFORMED_RECORD_TYPE
	 * We do this just in case another processor doesn't populate on that we expect to 
	 * be there.
	 */
	public void populateRecord(String partnerId, String recordDocument, 
			String url, Date workingDate) 
		throws HarvestException
	{

	}
	
	/**
	 * Mark the list of records as errors
	 */
	public boolean markErrorRecords(String[] recordIds) throws HarvestException {
		return true;
	}
	
	/**
	 * Mark the list of records as invalid, these are records that should no be 
	 * reported on
	 * @param recordIds An array of record ids that were given by the workspace to the 
	 *                  records
	 */
	public boolean markInvalidRecords(String[] recordIds) throws HarvestException {
		return true;
	}
	
	/*
	 * Private method that marks the records as a certain value. Value can be
	 * 0 - errored record, 1 - valid record, -1 - invalid record 
	 */
	private boolean markErrorRecords(String[] recordIds, int value) throws HarvestException {
		
		return true;
		
	}
	
	/**
	 * Insert URI identifier into record, for use in the workspace later
	 * @param recordId
	 * @param uri
	 */
	public boolean insertURI(String recordId, String uri) throws HarvestException {
		
		return true;
	}
	
	/**
	 * Get error record count for session
	 */
	public int getErrorRecordCount() throws HarvestException {
		return 0;
	}
	
	/**
	 * Get valid record count for session
	 */
	public int getValidRecordCount() throws HarvestException {
		return 0;
	}
	
	/**
	 * Get records count for session. This does not include records that are marks as
	 * -1 which is invalid record
	 */
	public int getRecordCount() throws HarvestException {
		return 0;
	}
	
	/*
	 * Private method that is called by all count methods. It takes two arguments for
	 * what the valid_record column has to be
	 * @param validRecordValue1
	 * @param validRecordValue2
	 * @return
	 * @throws HarvestException
	 */
	
	
	/**
	 * Get all the data associated with a particular record by its id.
	 * @param recordId
	 */
	public ResultsWrapper getRecordData(String recordId) throws HarvestException
	{
		return null;
	}
	
	/**
	 * Gets duplicate records based on URL. Only will return the record counts
	 * if the url is found more then once
	 */
	public ResultsWrapper getDuplicateRecordCountsByURL() throws HarvestException
	{
		return null;	
	}
	
	/**
	 * Gets duplicate records based on partner id. Only will return the record counts
	 * if the URL is found more then once
	 */
	public ResultsWrapper getDuplicateRecordCountsByPartnerID() throws HarvestException
	{
		return null;	
	}
	
	/**
	 * Gets duplicate records based on a duplicate query sent in
	 */
	public ResultsWrapper getDuplicateRecordCounts(String duplicateQuery) throws HarvestException
	{
		return null;
	}
	
	/**
	 * Get a result list of partner ids based on record ids
	 */
	public ResultsWrapper getPartnerIdsByRecordIds(String[] recordIds) throws HarvestException
	{
		return null;
	}
	
	/**
	 * Get record ids based on partner id. This is used during dup processing. A order
	 * must be specified defining in which order the records should be ordered via their
	 * record id. 
	 */
	public ResultsWrapper getRecordIdsByPartnerId(String partnerId, String order) throws HarvestException
	{
		return null;
	}
	
	/**
	 * Get record ids based on their url. This is used during dup processing. A order
	 * must be specified defining in which order the records should be ordered via their
	 * record id. 
	 */
	public ResultsWrapper getRecordIdsByURL(String url, String order) throws HarvestException
	{
		return null;
	}

	/**
	 * Method that closes all prepared statements and then closes the connection just to 
	 * re-open it again. This is a safty measure used after high volume iterations making sure
	 * all the locks on the Database are realeased before we go on. Might be unnecessary
	 * but no reason not to. It helped testing locally. 
	 */
	public void cleanupAfterIteration() throws HarvestException
	{

	}
	
	
	
	/**
	 * Method that updates a records specified record type
	 */
	public boolean updateHandles(String recordId, String metadatahandle, String resourcehandle) 
				throws HarvestException
	{
		
		return true;
	}
}
