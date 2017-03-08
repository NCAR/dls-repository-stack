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
package edu.ucar.dls.harvest.scripts;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;

import edu.ucar.dls.harvest.resources.DBConnector;

/**
 * Script that does the initial publish to the HarvestedRecords DB with what was in native_xml
 * column
 * @author dfinke
 *
 */
public class HarvestedRecordsInitalPublish extends Script{
	public static String META_DATA_QUERY = "SELECT setspec, partnerid, native_xml, "+
											       "nativeformat, sessionid " +
										   "FROM metadata ";
	public static String HARVESTED_RECORDS_INSERT_QUERY = 
				"INSERT into harvested_records " +
				        "(setspec, identifier, data, format, sessionid, repository_record) "+
				        "values (?,?,?,?,?,1)";
	public static String HARVESTED_RECORDS_DELETE_ALL_QUERY = 
		"DELETE from harvested_records";
	public String run() throws Exception
	{
		Connection harvestConnection = null;
		Connection repositoryConnection = null;
		Statement metadataStmt = null;
		PreparedStatement deletePstmt = null;
		PreparedStatement harvestedRecordsInsertPstmt = null;
		ResultSet rs = null;
		try
		{
			harvestConnection = DBConnector.createHarvestConnection();
			repositoryConnection = DBConnector.createRepositoryConnection();
			metadataStmt = repositoryConnection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
		              java.sql.ResultSet.CONCUR_READ_ONLY);
			
			metadataStmt.setFetchSize(Integer.MIN_VALUE);
			harvestedRecordsInsertPstmt = harvestConnection.prepareStatement(
					HARVESTED_RECORDS_INSERT_QUERY);
			
			rs = metadataStmt.executeQuery(META_DATA_QUERY);
			
			deletePstmt = harvestConnection.prepareStatement(
					HARVESTED_RECORDS_DELETE_ALL_QUERY);
			deletePstmt.execute();
			DbUtils.closeQuietly(deletePstmt);
			
			int count = 0;
			while(rs.next())
			{
				String setSpec = rs.getString(1);
				String partnerId = rs.getString(2);
				Blob nativeXML = rs.getBlob(3);
				if(nativeXML.length()==0)
					nativeXML = null;
				String nativeFormat = rs.getString(4);
				String sessionId = rs.getString(5);
				
				harvestedRecordsInsertPstmt.clearParameters();
				
				harvestedRecordsInsertPstmt.setString(1, setSpec);
				harvestedRecordsInsertPstmt.setString(2, partnerId);
				harvestedRecordsInsertPstmt.setBlob(3, nativeXML);
				harvestedRecordsInsertPstmt.setString(4, nativeFormat);
				harvestedRecordsInsertPstmt.setString(5, sessionId);

				harvestedRecordsInsertPstmt.addBatch();
				
				count++;
				if(count>1000)
				{
					harvestedRecordsInsertPstmt.executeBatch();
					count = 0;
				}
			}
			if(count>0)
				harvestedRecordsInsertPstmt.executeBatch();
		}
		finally
		{
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(metadataStmt);
			DbUtils.closeQuietly(harvestedRecordsInsertPstmt);
			DbUtils.closeQuietly(repositoryConnection);
			DbUtils.closeQuietly(harvestConnection);
			DbUtils.closeQuietly(deletePstmt);
		}
		return "All harvested records' native_xml were successfully copied into the " +
			"harvested_records database after harvested records db was cleaned out.";
	}
	
	public static void main(String [] args) throws Exception
	{
		new HarvestedRecordsInitalPublish().run();
	}

}
