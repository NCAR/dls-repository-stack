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
package edu.ucar.dls.harvest.repository;

import java.io.File;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.dbutils.DbUtils;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.exceptions.MaxErrorThresholdException;
import edu.ucar.dls.harvest.resources.DBConnector;
import edu.ucar.dls.harvest.workspaces.DBResultsWrapper;
import edu.ucar.dls.harvest.workspaces.ResultsWrapper;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 * Database implementation of the abstract Repository class. This was tested
 * with MySQL but no MySQL specific classes/ queries were used so this should 
 * work with other database types. 
 *
 */
public class DBRepository extends Repository{
	protected File workingDirectory = null;

	private Connection dbConnection;
	
	/* All queries are class variables so then can be viewed easily  */
	private static final String META_DATA_INSERT_STMT = 
		"INSERT INTO metadata " +
			"(metadatahandle, " +
		 	"setspec, " +
			"partnerid, " +
			"nativeformat, " +
			"native_xml, " +
			"targetformat, " +
			"target_xml, " +
			"sessionid, " +
			"agent)" +
		"VALUES " +
			"(?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String RESOURCE_DATA_INSERT_STMT = 
		"INSERT INTO resource " +
			"(resourcehandle, " +
		 	"setspec, " +
			"metadatahandle, " +
			"url, " +
			"sessionid, " +
			"agent) " +
		"VALUES " +
			"(?, ?, ?, ?, ?, ?)";
	
	private static final String SESSION_UPDATE_STMT = 
		"UPDATE session " +
			"SET sessionid=?, last_update_ts=CURRENT_TIMESTAMP " +
			"WHERE setSpec=?";
	
	private static final String SESSION_DELETE_STMT = 
		"DELETE " +
		"FROM session " +
		"WHERE setSpec=?";
	
	private static final String SESSION_INSERT_STMT = 
		"INSERT INTO  session" +
			"(sessionid, setspec, last_update_ts) " +
		"VALUES " +
			"(?, ?, CURRENT_TIMESTAMP)";
	
	private static final String REMOVE_METADATA_SESSION_DATA_STMT = 
		"DELETE "+
		"FROM metadata " +
		"WHERE setSpec=? and sessionid=?";
	
	private static final String REMOVE_RESOURCE_SESSION_DATA_STMT = 
		"DELETE " +
		"FROM resource " +
		"WHERE setSpec=? and sessionid=?";
	
	private static final String GET_CURRENT_SESSION_STMT = 
		"SELECT sessionid " +
		"FROM session " +
		"WHERE setSpec=?";
	
	private static final String GET_RECORD_COUNT_STMT = 
		"SELECT count(*) " +
		"FROM metadata " +
		"WHERE setSpec=? and sessionid=?";
	
	// Notice the order by partnerid. That is very important for future references
	// if other repos are used in the future. That has to be there for processor
	// usage. 
	private final static String GET_SESSION_META_DATA = 
		"SELECT partnerid, native_xml, target_xml " +
		"FROM metadata " +
		"WHERE setSpec=? and sessionid=? " +
		"ORDER BY partnerid";
	
	/**
	 * initialize the repository by sending in the workspace. This also creates
	 * the one and only connection to the repository db
	 */
	public void initialize(Workspace workspace, boolean isTest) 
					throws HarvestException
	{
		super.initialize(workspace, isTest);
		this.dbConnection = getConnection();
	}
	
	/**
	 * Main implemented method that is called from the Repository.populate method
	 * this should take all the valid records in the workspace and populate the 
	 * repository.
	 */
	protected void createNewSessionData() throws HarvestException
	{
		String newSessionId = this.workspace.harvestRequest.getUuid();
		String currentSessionId = null;
		
		// These booleans are you used to know what to do if an exception happens
		boolean rollbackDataInserts = false;
		boolean rollbackSessionChange = false;
		try {
			currentSessionId = this.getCurrentSessionId();
						
			rollbackDataInserts = true;
			this.populateMetadataTables( newSessionId);
			rollbackSessionChange = true;
			this.updateSessionTable(currentSessionId, 
					newSessionId, false); // False means this is not a rollback
			
			// session table is updated no reason to rollback anymore
			rollbackSessionChange = false;
			rollbackDataInserts = false;
			
			// Once this is called there is no return. The old session is removed
			this.removeMetadataBySessionId(currentSessionId);
			
		} catch (Exception e) {
			// Drop connection and try to re-establish it
			try {
				this.dbConnection.close();
			} catch (SQLException e1) {
				// Maybe it was forced to be closed, just try to re-open it
			}
			
			try {
				this.dbConnection = getConnection();
				if(rollbackSessionChange)
					this.updateSessionTable(currentSessionId, 
							newSessionId, true);  // true means its a rollback
				if(rollbackDataInserts)
					this.removeMetadataBySessionId(newSessionId);
				
			} catch (Exception e1) {
				throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE, 
						String.format("URGENT! Unable to populate Metadata/Resource table, Rolled back failed " +
						"for session %s back to %s, unable to figure out current DB state.", newSessionId, 
						currentSessionId), e1);
			}
			
			// if its a harvest exception just re-raise it, it will be more informative then the generic
			// one we through in the else
			if (e.getClass()==HarvestException.class||e.getClass()==MaxErrorThresholdException.class)
				throw (HarvestException)e;
			else
				throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE, 
					String.format("Unable to populate Metadata/Resource table, Rolled back all inserts " +
					"for session %s", newSessionId), e);
		}
	}
	
	/**
	 * Populate metadata tables with workspace records
	 * @param sessionId
	 * @throws SQLException
	 * @throws HarvestException
	 */
	private void populateMetadataTables( 
			String sessionId) throws SQLException, HarvestException
	{
		ResultsWrapper resultsWrapper = this.workspace.getRecordsAllData();
		Iterator results = resultsWrapper.getResults();
		
		PreparedStatement metadata_table_pstmt = this.dbConnection.prepareStatement(
				META_DATA_INSERT_STMT);
		PreparedStatement resource_table_pstmt = this.dbConnection.prepareStatement(
				RESOURCE_DATA_INSERT_STMT);
		
		// Global values for all rows inserted
		metadata_table_pstmt.setString(2, this.workspace.harvestRequest.getSetSpec());
		metadata_table_pstmt.setString(4, this.workspace.harvestRequest.getNativeFormat());
		metadata_table_pstmt.setString(6, this.workspace.targetFormat);
		metadata_table_pstmt.setString(8, sessionId);
		metadata_table_pstmt.setString(9, this.agent);
		
		resource_table_pstmt.setString(2, this.workspace.harvestRequest.getSetSpec());
		resource_table_pstmt.setString(5, sessionId);
		resource_table_pstmt.setString(6, this.agent);
		
		Blob validatedRecord = null;
		Blob transformedRecord = null;
		while (results.hasNext())
		{	
			Object[] recordData = (Object[])results.next();
			
			String partnerId = (String)recordData[0];
			validatedRecord = new SerialBlob((byte[])recordData[2]);
			byte[] transformedRecordByteArr = (byte[])recordData[3];
			transformedRecord = new SerialBlob(transformedRecordByteArr);
			String url = (String)recordData[4];
			String recordId = ((Integer)recordData[5]).toString();
			String metadataHandle = (String)recordData[6];

			metadata_table_pstmt.setString(1, metadataHandle);
			metadata_table_pstmt.setString(3, partnerId);
			metadata_table_pstmt.setBlob(5, validatedRecord);
			metadata_table_pstmt.setBlob(7, transformedRecord);
			
			// if it was set to populate resource handles
			// populate this table too. If it was set to true but no 
			// resource handle or url is set. this execute will throw an sql 
			// exception
			if(this.workspace.isPopulateResourceHandles())
			{
				String resourceHandle = (String)recordData[7];
				resource_table_pstmt.setString(1, resourceHandle);
				resource_table_pstmt.setString(3, metadataHandle);
				resource_table_pstmt.setString(4, url);
				resource_table_pstmt.execute();
			}
			
			// note that we wait till we make sure that we can add the resource handle
			// if applicable before we insert into the metadata table. We don't want it
			// in the metadata table unless the resource was added successfully
			metadata_table_pstmt.execute();
			
			HarvestRequest.checkForInterruption(
				"Repository.populateMetadataTables()");
		}
		resultsWrapper.clean();
		
		DbUtils.close(metadata_table_pstmt);
		DbUtils.close(resource_table_pstmt);
	}

		
	/**
	 * Update the session table, Care is taken here because there are a lot
	 * of different cases that can come through.
	 * 1) its the first successful harvests so no sessions exist yet
	 * 2) A session exists and we are replacing it
	 * 3) A session exists and we replaced it but now we want to rollback
	 * @param currentSessionId
	 * @param newSessionId
	 * @param isRollBack
	 * @return
	 * @throws SQLException
	 */
	private boolean updateSessionTable(String currentSessionId, 
			String newSessionId, boolean isRollBack) throws SQLException {
		String setSpec = this.workspace.harvestRequest.getSetSpec();
		boolean returnValue = false;
		if(currentSessionId!=null)
		{
			// in the cases where there is a current session in there. In this
			// case just replace the session id
			PreparedStatement updateSessionTablePstmt=null;
			try {
				updateSessionTablePstmt = this.dbConnection.prepareStatement(
						SESSION_UPDATE_STMT);
				if(isRollBack)
					updateSessionTablePstmt.setString(1, currentSessionId);
				else
					updateSessionTablePstmt.setString(1, newSessionId);
				updateSessionTablePstmt.setString(2, setSpec);
				returnValue = updateSessionTablePstmt.execute();
				updateSessionTablePstmt.close();
			} 
			finally
			{
				DbUtils.close(updateSessionTablePstmt);
			}
		}
		else
		{
			// Else this is a first time. 
			
			// If its a rollback we just remove the session 
			if(isRollBack)
			{
				PreparedStatement deleteSessionTablePstmt=null;
				try {
					deleteSessionTablePstmt = this.dbConnection.prepareStatement(
							SESSION_DELETE_STMT);
					deleteSessionTablePstmt.setString(1, setSpec);
					returnValue = deleteSessionTablePstmt.execute();
					
				} 
				finally
				{
					DbUtils.close(deleteSessionTablePstmt);
				}
			}
			else
			{
				// else add this new session and setspec to the table
				PreparedStatement insertSessionTablePstmt=null;
				try {
					insertSessionTablePstmt = this.dbConnection.prepareStatement(
							SESSION_INSERT_STMT);
					insertSessionTablePstmt.setString(1, newSessionId);
					insertSessionTablePstmt.setString(2, setSpec);
					
					returnValue = insertSessionTablePstmt.execute();
				}
				finally
				{
					DbUtils.close(insertSessionTablePstmt);
				}
				
			}
		}
		return returnValue;
	}
	
	/**
	 * This is called in two places. If there is a rollback and we need to get rid of
	 * the new session and once a successful population has been made. Removing the old
	 * session.
	 * @param sessionId
	 * @return
	 * @throws SQLException
	 */
	public boolean removeMetadataBySessionId(String sessionId) throws SQLException {
		
		// Remove the meatadata items
		PreparedStatement removeMetadataSessionDataPstmt=null;
		try {
			removeMetadataSessionDataPstmt = dbConnection.prepareStatement(
					REMOVE_METADATA_SESSION_DATA_STMT);
			removeMetadataSessionDataPstmt.setString(1, this.workspace.harvestRequest.getSetSpec());
			removeMetadataSessionDataPstmt.setString(2, sessionId);
			removeMetadataSessionDataPstmt.execute();
			
		} 
		finally
		{
			DbUtils.close(removeMetadataSessionDataPstmt);
		}
		
		// Remove the resource items
		PreparedStatement removeResourceDataPstmt=null;
		try {
			removeResourceDataPstmt = this.dbConnection.prepareStatement(
					REMOVE_RESOURCE_SESSION_DATA_STMT);
		
			removeResourceDataPstmt.setString(1, this.workspace.harvestRequest.getSetSpec());
			removeResourceDataPstmt.setString(2, sessionId);
			removeResourceDataPstmt.execute();
			
		} finally
		{
			DbUtils.close(removeResourceDataPstmt);
		}
		return true;
	}
	
	/**
	 * Get the current session ID from the session table
	 * @return 
	 * @throws SQLException
	 */
	public String getCurrentSessionId() throws HarvestException {
		String currentSessionId = null;
		ResultSet rs = null;
		PreparedStatement currentSessionDataPstmt = null;
		try {
			currentSessionDataPstmt = this.dbConnection.prepareStatement(
					GET_CURRENT_SESSION_STMT);
		
			currentSessionDataPstmt.setString(1, 
					this.workspace.harvestRequest.getSetSpec());
			rs = currentSessionDataPstmt.executeQuery();
			boolean hasResult = rs.first();
			if(hasResult)
				currentSessionId = rs.getString(1);
			// If hasResult is null, that means its a new setSpec. so currentSessionId should be null
						
			return currentSessionId;
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
						"Could not retrieve current session id.", e);
		}
		finally
		{
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(currentSessionDataPstmt);
		}
	}
	
	/**
	 * Gets the session Metadata count by session id. This is used for processors
	 * that report on the changes being made to the repos
	 * @param sessionId sessionid for which you want the metadata count
	 * @return Count of metadata for the session
	 */
	public int getSessionMetaDataCount(String sessionId) throws HarvestException {
		ResultSet rs = null;
		PreparedStatement recordCountPstmt = null;
		try
		{
			int recordCount = 0;
			recordCountPstmt = dbConnection.prepareStatement(
					GET_RECORD_COUNT_STMT);
			recordCountPstmt.setString(1, this.workspace.harvestRequest.getSetSpec());
			recordCountPstmt.setString(2, sessionId);
			
			rs = recordCountPstmt.executeQuery();
			boolean hasResult = rs.first();
			if(hasResult)
				recordCount = rs.getInt(1);
			// no result means 0, the count(*) should really come back as 0 anyway
			
			return recordCount;
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
						"Could not retrieve current session id.", e);
		}
		finally
		{
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(recordCountPstmt);
		}
	}

	/**
	 * Get the session meatadata results by sessionid
	 * @param sessionId sessionid for which you want the metadata count
	 */
	public ResultsWrapper getSessionMetadata(String sessionId) throws HarvestException
	{
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		
		try {
			pstmt = this.dbConnection.prepareStatement(
					String.format(GET_SESSION_META_DATA));
			pstmt.setString(1, this.workspace.harvestRequest.getSetSpec());
			pstmt.setString(2, sessionId);
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					"Could not retrieve session metadata.", e);
		}
		return new DBResultsWrapper(rs, pstmt);
	}
	
	/**
	 * Create the connection to the DB Repository 
	 * @return
	 * @throws HarvestException 
	 */
	public Connection getConnection() throws HarvestException
	{
		return ((DBConnector)this.workspace.getResource(
				DBConnector.class)).getConnection(DBConnector.REPOSITORY_CONNECTION);
	}

	
	/* Todo not sure if we want to do this anymore
	private String getTargetSummaryText(byte[] record)
	{
		try
		{
			String recordString = new String(record, Config.ENCODING);
			Document document = Dom4jUtils.getXmlDocument(recordString);
			List<Element> matchingElements = document.getRootElement().selectNodes(XPATH);
			List<String> keywords = new ArrayList<String>();
			for(Element element: matchingElements)
			{
				String text = element.getTextTrim();
				if(text!=null && !text.equals(""))
					keywords.add(text);
			}
			if(keywords.size()>0)
				return StringUtils.join(keywords, " ");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
		
	}
	*/
	
	
}
