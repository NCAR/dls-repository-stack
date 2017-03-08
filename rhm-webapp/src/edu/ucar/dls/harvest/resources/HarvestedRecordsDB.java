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
package edu.ucar.dls.harvest.resources;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.workspaces.DBResultsWrapper;
import edu.ucar.dls.harvest.workspaces.ResultsWrapper;
import edu.ucar.dls.harvest.workspaces.Workspace;


/**
 * A Database implementation of HarvestRecordsDB which holds the last successful
 * harvest records for each setSpec, When a new one is added we remove the old one
 */
public class HarvestedRecordsDB extends Resource{
	

	// Query for getting the harvested records out of the DB
	private static String HARVESTED_RECORDS_QUERY = 
		"SELECT identifier, data, url, working_date "+
    	"FROM harvested_records "+
    	"WHERE setspec=? and sessionid=?";
	
	// Query to figure out the latest session for a set spec. There should never
	// be more then one, this is used just to make sure.
	private static String GET_LASTEST_SESSION_QUERY = "SELECT MAX(sessionid) "+
									   "FROM harvested_records " +
									   "WHERE setspec=?";
	
	// Query for inserting harvested records into the Database
	public static String HARVESTED_RECORDS_INSERT_QUERY = 
		"INSERT into harvested_records " +
		        "(setspec, identifier, data, format, sessionid, repository_record, " +
		        		"url, working_date) "+
		        "values (?,?,?,?,?,?,?,?)";
	
	// Remove the harvested records from a particual session.
	private static final String REMOVE_HARVESTED_RECORDS_STMT = 
		"DELETE "+
		"FROM harvested_records " +
		"WHERE setSpec=? and sessionid=?";
	
	private Connection harvestConnection = null;
	private PreparedStatement populateRecordPstmt = null;
	
	public HarvestedRecordsDB(Workspace workspace) throws HarvestException {
		super(workspace);
		harvestConnection = ((DBConnector)workspace.getResource(
				DBConnector.class)).getConnection(DBConnector.HARVEST_CONNECTION);
	}
	
	/**
	 * Helper resource method for getting the last session per setSpec
	 * @param setSpec
	 * @return
	 * @throws HarvestException
	 */
	public String getLatestSession(String setSpec) throws HarvestException
	{
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		try {
			pstmt = this.harvestConnection.prepareStatement(GET_LASTEST_SESSION_QUERY);
			pstmt.setString(1, setSpec);
			rs = pstmt.executeQuery();
			rs.first();
			return rs.getString(1);
		} catch (SQLException e) {
			System.out.println("HarvestedRecordsDB.getLatestSession(): Trouble getting record count from workspace DB using SQL statement '" + pstmt.toString() + "' Error: " + e);
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					"Trouble getting record count from workspace DB using SQL statement '" + pstmt.toString() + "'", e );
		}
		finally
		{	
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pstmt);
		}
	}
	
	/**
	 * Get records for a particular setSpec and sessionid
	 * @param setSpec
	 * @param sessionId
	 * @return
	 * @throws HarvestException
	 */
	public ResultsWrapper getRecords(String setSpec, String sessionId) throws HarvestException
	{
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		
		try {
			pstmt = this.harvestConnection.prepareStatement(HARVESTED_RECORDS_QUERY);
			pstmt.setString(1, setSpec);
			pstmt.setString(2, sessionId);
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					"Trouble getting all record datas " +
					"from workspace connection", e );
		}
		return new DBResultsWrapper(rs, pstmt);
	}
	
	/**
	 * Populate a Harvested Record into the database
	 * @param setspec
	 * @param identifier
	 * @param data
	 * @param format
	 * @param sessionId
	 * @param isRespositoryRecord
	 * @param url
	 * @param workingDate
	 * @throws HarvestException
	 */

	public void populateRecord(String setspec, String identifier, String data, String format, 
				String sessionId, boolean isRespositoryRecord,
			String url, Date workingDate) 
		throws HarvestException
	{	
		try {
			// we use a cached version of the populatePstmt if we can
			if(this.populateRecordPstmt==null)
				this.populateRecordPstmt = this.harvestConnection.prepareStatement(
						HARVESTED_RECORDS_INSERT_QUERY);
			else
				this.populateRecordPstmt.clearParameters();
			this.populateRecordPstmt.setString(1, setspec);
			this.populateRecordPstmt.setString(2, identifier);
			Blob populateBlob=null;
			if(data!=null)
				 populateBlob = new SerialBlob(data.getBytes(Config.ENCODING));
			this.populateRecordPstmt.setBlob(3, populateBlob);
			
			this.populateRecordPstmt.setString(4, format);
			this.populateRecordPstmt.setString(5, sessionId);
			
			int repository_record=0;
			if(isRespositoryRecord)
				repository_record = 1;
			this.populateRecordPstmt.setInt(6, repository_record);
			this.populateRecordPstmt.setString(7, url);

			Timestamp timestamp = null;
			if(workingDate!=null)
				timestamp = new Timestamp(workingDate.getTime());

			this.populateRecordPstmt.setTimestamp(8, timestamp);
			this.populateRecordPstmt.execute();

		} catch (SQLException e) {
			throw new HarvestException(
				Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					String.format("Trouble inserting record in Harvest Records DB. Exception %s\n%s", 
					e.getMessage(),
					ExceptionUtils.getStackTrace(e)), e);
		} catch (UnsupportedEncodingException e) {
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
					"Cannot convert byte string to encoding utf-8", e);
		}
	}
	
	/**
	 * Delete a specified session of harvested records
	 * @param setSpec
	 * @param sessionId
	 * @throws HarvestException
	 */
	public void removeSessionHarvestedRecords(String setSpec, String sessionId) throws HarvestException
	{
		PreparedStatement pstmt=null;
		try {
			pstmt = this.harvestConnection.prepareStatement(REMOVE_HARVESTED_RECORDS_STMT);
			pstmt.setString(1, setSpec);
			pstmt.setString(2, sessionId);
			pstmt.execute();

		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
				String.format("Trouble removing old session from harvested " +
					"records db. SessionId:%s  SetSpec: %s",setSpec, sessionId), e );
		}
		finally
		{	
			DbUtils.closeQuietly(pstmt);
		}
	}
	
	/**
	 * Mandatory method for implementing Resource. This is called by Workspace Clean.
	 * clean anything necessary
	 */
	public void clean()
	{
		DbUtils.closeQuietly(populateRecordPstmt);
	}
	
}
