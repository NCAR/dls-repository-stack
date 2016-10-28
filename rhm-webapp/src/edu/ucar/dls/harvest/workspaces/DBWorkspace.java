package edu.ucar.dls.harvest.workspaces;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.dbutils.DbUtils;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.resources.DBConnector;

import javax.sql.rowset.serial.SerialBlob;

/**
 * A Database implementation of the workspace. This was written for MySQL but
 * with a few small modifications to the CREATE_TABLE_STMT probably could 
 * switch it over pretty easily to something else
 */
public class DBWorkspace extends Workspace{
	
	// This is the table that is created in the workspace DB to house record info
	// for a particular session
	private static final String CREATE_TABLE_STMT = "CREATE TABLE %s ("+
								  "original_record blob, "+
								  "validated_record blob, "+
								  "transformed_record blob, "+
								  "working_date timestamp NULL DEFAULT NULL, "+
								  "record_id int(11) NOT NULL AUTO_INCREMENT, "+
								  "valid_record tinyint(4) DEFAULT 1, "+
								  "partner_id varchar(255), "+
								  "url varchar(4096), " +
								  "metadatahandle varchar(255), " +
								  "resourcehandle varchar(255), " +
								  "PRIMARY KEY (record_id) "+
								") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci"; 
	
	/*
	 * All queries are listed as static variables for ease of use and if 
	 * one wanted to create a different implementation of workspace. These
	 * are the results that must be returned in order. 
	 */
	private static final String POPULATE_TABLE_STMT = "INSERT INTO %s (partner_id, " +
						"original_record, validated_record, transformed_record, url, working_date) " +
							    "VALUES (?, ?, ?, ?, ?, ?)";
	
	// Node the order by, VERY important for reporting. we use this method in some 
	// processors to do comparison checks agains the repository
	private static final String GET_RECORDS_STMT = "SELECT record_id, %s, partner_id " +
												   "FROM %s " +
												   "WHERE valid_record=1 "+
												   "ORDER BY partner_id";
	
	// Get records all data returns on valid records
	private static final String GET_RECORDS_ALL_DATA_STMT = 
						"SELECT partner_id, original_record, " +
								"validated_record, transformed_record, url, " +
								"record_id, metadatahandle, resourcehandle " +
						"FROM %s " +
						"WHERE valid_record=1";
	
	
	// Get records all data returns on valid records
	private static final String GET_HARVESTED_RECORDS_STMT = 
						"SELECT partner_id, original_record, " +
								"url, working_date, valid_record "  +
						"FROM %s ";
	
	private static final String GET_RECORD_DATA = 
		"SELECT partner_id, original_record, " +
				"validated_record, transformed_record, url, metadatahandle, resourcehandle " +
		"FROM %s " +
		"WHERE record_id=?";
	

	private static final String UPDATE_RECORD_STMT = "UPDATE %s "+
							  "SET %s=? "+
							  "WHERE record_id=?";
	
	// Records can be marked (1) valid record (0) Error record or (-1) for invalid
	private static final String MARK_RECORDS_STMT = "UPDATE %s "+
	  "SET valid_record=? "+
	  "WHERE record_id in (%s)";
	
	private static final String INSERT_URI_STMT = "UPDATE %s "+
	  "SET url=? "+
	  "WHERE record_id=?";
	
	private static final String DROP_TABLE_STMT = "Drop table %s";
	
	private static final String GET_RECORD_COUNT = "SELECT count(*) FROM %s " +
	 "WHERE valid_record=? or valid_record=?";
	
	private static final String GET_DUPLICATE_RECORD_BY_PARTNER_ID_COUNTS =
				"SELECT partner_id, COUNT(*)" +
				"FROM %s " +
				"WHERE valid_record=1 "+
				"GROUP BY partner_id " +
				"HAVING COUNT(*)>1";
	
	private static final String GET_DUPLICATE_RECORD_BY_URL_COUNTS =
		"SELECT url, COUNT(*)" +
		"FROM %s " +
		"WHERE valid_record=1 "+
		"GROUP BY url " +
		"HAVING COUNT(*)>1";
	
	private static final String GET_RECORD_IDS_BY_PARNTER_ID =
		"SELECT record_id " +
		"FROM %s " +
		"WHERE partner_id=? "+
		"ORDER BY working_date %s, record_id %s";
	
	private static final String GET_RECORD_IDS_BY_URL =
		"SELECT record_id " +
		"FROM %s " +
		"WHERE url=? " +
		"ORDER BY working_date %s, record_id %s";;
	
	private static final String GET_PARNTER_IDS_BY_RECORD_IDS =
		"SELECT partner_id " +
		"FROM %s " +
		"WHERE record_id in (%s)"+
		"ORDER BY record_id";
	
	private static final String UPDATE_HANDLES_STMT = "UPDATE %s "+
	  "SET metadatahandle=?, resourcehandle=? "+
	  "WHERE record_id=?";
	
	private Connection dbConnection = null;
	private String dbTableName = null;
	private PreparedStatement populateRecordPstmt = null;
	private PreparedStatement insertURIPstmt = null;
	private PreparedStatement updateHandlesPstmt = null;
	private HashMap<String, PreparedStatement> updateRecordPstmts = new HashMap<String, PreparedStatement>();
	public static String TEMP_INGEST_FORMAT_STRING = "temp_ingest_%s";
	private Blob updateBlob = null;
	private Blob populateBlob = null;
	
	/**
	 * Initialize the workspace via the harvest request. This initializes
	 * the workspace db table we are using.
	 */
	public void initialize(HarvestRequest harvestRequest) throws HarvestException
	{
		super.initialize(harvestRequest);

		setupConnections(harvestRequest.getUuid());
		this.dropTable();
		this.createTempTable();
	}
	
	public void initialize(String mdpHandle, String uuid) throws HarvestException
	{
		super.initialize(mdpHandle, uuid);
		setupConnections(uuid);
	}
	
	/**
	 * Sets up the connections for the dbworkspace, this is outside
	 * of the initialize to make it possible to create the connections
	 * and access to the table without having a harvest request. For 
	 * cleanup if this thread goes awol
	 * @param uuid
	 * @throws HarvestException
	 */
	public void setupConnections(String uuid) throws HarvestException
	{
		this.dbTableName = String.format(TEMP_INGEST_FORMAT_STRING, 
				uuid).replace('-', '_').replace('.', '_').replace(" ", "_");
	
		this.dbConnection = this.getConnection();
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
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		try {
			pstmt = this.dbConnection.prepareStatement(
					String.format(GET_RECORDS_STMT, recordType, this.dbTableName));
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					String.format("Trouble getting %s type records " +
					"from workspace connection", recordType), e );
		}
		return new DBResultsWrapper(rs, pstmt);
	}
	
	/**
	 * Implemented method that is required. This returns all valid records and all the 
	 * data associated with them. This is used for populating the repos
	 */
	public ResultsWrapper getRecordsAllData() throws HarvestException
	{
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		
		try {
			pstmt = this.dbConnection.prepareStatement(
					String.format(GET_RECORDS_ALL_DATA_STMT, this.dbTableName));
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					"Trouble getting all record datas " +
					"from workspace connection", e );
		}
		return new DBResultsWrapper(rs, pstmt);
	}
	
	/**
	 * Get records that were harvested from the workspace. These include ones that
	 * have errored out
	 */
	public ResultsWrapper getHarvestedRecords() throws HarvestException
	{
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		
		try {
			pstmt = this.dbConnection.prepareStatement(
					String.format(GET_HARVESTED_RECORDS_STMT, this.dbTableName));
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					"Trouble getting all harvested record datas " +
					"from workspace connection", e );
		}
		return new DBResultsWrapper(rs, pstmt);
	}
	
	/**
	 * Method that updates a records specified record type
	 */
	public boolean updateRecord(String recordId, String recordType, String updatedRecord) 
				throws HarvestException
	{
		try {
			PreparedStatement pstmt = this.getUpdateRecordPstmt(recordType);
			updateBlob = new SerialBlob(updatedRecord.getBytes(Config.ENCODING));
			pstmt.setBlob(1, updateBlob);
			pstmt.setInt(2, Integer.parseInt(recordId));
			pstmt.execute();
			pstmt.clearParameters();
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					String.format("Trouble updating %s type records " +
					"from workspace connection", recordType), e );
		} catch (UnsupportedEncodingException e) {
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
					"Cannot convert byte string to encoding utf-8", e);
		} 
		
		return true;
	}
	
	private PreparedStatement getUpdateRecordPstmt(String recordType) throws SQLException {
		if(!this.updateRecordPstmts.containsKey(recordType))
		{
			this.updateRecordPstmts.put(recordType, this.dbConnection.prepareStatement(
					String.format(UPDATE_RECORD_STMT, this.dbTableName, recordType)));
		}
		else
		{
			this.updateRecordPstmts.get(recordType).clearParameters();
		}
		return this.updateRecordPstmts.get(recordType);
	}

	/**
	 * Create the temp table defined by this.dbTableName, used for housing 
	 * record data
	 * @throws HarvestException
	 */
	public void createTempTable() throws HarvestException{
		Statement stmt=null;
		try {
			stmt = this.dbConnection.createStatement();
			stmt.execute(String.format(CREATE_TABLE_STMT, this.dbTableName));
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					"Trouble createing temp table for workspace" 
					, e );
		}
		finally
		{
			DbUtils.closeQuietly(stmt);
		}
	}
	
	
	/**
	 * Cleans the current sessions workspace. This is called at the very end
	 * of the ingest proess. This method is very important because we do not
	 * want old session workspace tables hanging around wasting space. Also
	 * note the super.clean(). Base class does its own cleaning
	 */
	public void clean()
	{	
		try {
			this.dropTable();
		} catch (HarvestException e1) {
			e1.printStackTrace();
		}
		
		this.cleanupPreparedStatments();
		super.clean();
	}
	
	/**
	 * try to drop the session table
	 * @throws HarvestException
	 */
	public void dropTable() throws HarvestException
	{
		if (this.dbConnection==null)
			this.dbConnection = this.getConnection();
		if (this.dbConnection!=null)
		{
			Statement stmt = null;
			try
			{
				stmt = this.dbConnection.createStatement();
				stmt.execute(String.format(DROP_TABLE_STMT, this.dbTableName));
			}
			catch (SQLException e) {
				// table doesn't exist, doesn't matter keep on chugging
			}
			finally
			{
				DbUtils.closeQuietly(stmt);
			}
		}
		
	}
	
	/**
	 * Get Connection method for the DBWorkspace, this uses the connection via 
	 * connection pool trough the workspace. This is so we don't have more then
	 * one connection to the workspace database for each harvest running
	 * @return
	 * @throws HarvestException
	 */
	private Connection getConnection() throws HarvestException
	{
		return ((DBConnector)this.getResource(
				DBConnector.class)).getConnection(DBConnector.WORKSPACE_CONNECTION);
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
		try {
			if(this.populateRecordPstmt==null)
				this.populateRecordPstmt = this.dbConnection.prepareStatement(
						String.format(POPULATE_TABLE_STMT, this.dbTableName));
			else
				this.populateRecordPstmt.clearParameters();
			this.populateRecordPstmt.setString(1, partnerId);
			populateBlob = new SerialBlob(recordDocument.getBytes(Config.ENCODING));
			this.populateRecordPstmt.setBlob(2, populateBlob);
			this.populateRecordPstmt.setBlob(3, populateBlob);
			this.populateRecordPstmt.setBlob(4, populateBlob);
			this.populateRecordPstmt.setString(5, url);
			Timestamp timestamp = null;
			if(workingDate!=null)
				timestamp = new Timestamp(workingDate.getTime());

			this.populateRecordPstmt.setTimestamp(6, timestamp);
			this.populateRecordPstmt.execute();
			this.populateRecordPstmt.clearParameters();
		} catch (SQLException e) {
			throw new HarvestException(
				Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					"Trouble inserting record in workspace DB", e );
		} catch (UnsupportedEncodingException e) {
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
					"Cannot convert byte string to encoding utf-8", e);
		}
	}
	
	/**
	 * Mark the list of records as errors
	 */
	public boolean markErrorRecords(String[] recordIds) throws HarvestException {
		return this.markErrorRecords(recordIds, 0);
	}
	
	/**
	 * Mark the list of records as invalid, these are records that should no be 
	 * reported on
	 * @param recordIds An array of record ids that were given by the workspace to the 
	 *                  records
	 */
	public boolean markInvalidRecords(String[] recordIds) throws HarvestException {
		return this.markErrorRecords(recordIds, -1);
	}
	
	/*
	 * Private method that marks the records as a certain value. Value can be
	 * 0 - errored record, 1 - valid record, -1 - invalid record 
	 */
	private boolean markErrorRecords(String[] recordIds, int value) throws HarvestException {
		PreparedStatement pstmt=null;
		try {
			
			// To do this set with unlimited where clause we first append ? marks to the
			// where the as many as we need for the records ids
			StringBuilder inStmtbuilder = new StringBuilder();
			for(String recordId:recordIds)
				inStmtbuilder.append("?,");
			inStmtbuilder.deleteCharAt(inStmtbuilder.length()-1);
			
			pstmt = this.dbConnection.prepareStatement(
					String.format(MARK_RECORDS_STMT, this.dbTableName,
							inStmtbuilder.toString() ));
			
			pstmt.setInt(1, value);
			int index = 2;
			// Now we put them into the pstmt now that the ? marks are in there
			for( String recordId : recordIds ) {
			   pstmt.setString(  index, recordId );
			   index++;
			}
			pstmt.execute();
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					"Trouble marking errors records in " +
					"workspace connection", e );
		}
		finally
		{
			DbUtils.closeQuietly(pstmt);
		}
		return true;
		
	}
	
	/**
	 * Insert URI identifier into record, for use in the workspace later
	 * @param recordId
	 * @param uri
	 */
	public boolean insertURI(String recordId, String uri) throws HarvestException {
		try {
			if(this.insertURIPstmt==null)
				this.insertURIPstmt = this.dbConnection.prepareStatement(
						String.format(INSERT_URI_STMT, this.dbTableName ));
			
			this.insertURIPstmt.setString( 1, uri );
			this.insertURIPstmt.setInt( 2, Integer.parseInt(recordId) );

			this.insertURIPstmt.execute();
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					"Trouble inserting URI into workspace db", e );
		}
		return true;
	}
	
	/**
	 * Get error record count for session
	 */
	public int getErrorRecordCount() throws HarvestException {
		return this.getRecordCount(0, 0);
	}
	
	/**
	 * Get valid record count for session
	 */
	public int getValidRecordCount() throws HarvestException {
		return this.getRecordCount(1, 1);
	}
	
	/**
	 * Get records count for session. This does not include records that are marks as
	 * -1 which is invalid record
	 */
	public int getRecordCount() throws HarvestException {
		return this.getRecordCount(0, 1);
	}
	
	/*
	 * Private method that is called by all count methods. It takes two arguments for
	 * what the valid_record column has to be
	 * @param validRecordValue1
	 * @param validRecordValue2
	 * @return
	 * @throws HarvestException
	 */
	private int getRecordCount(int validRecordValue1, int validRecordValue2) throws HarvestException
	{
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		try {
			pstmt = this.dbConnection.prepareStatement(
					String.format(GET_RECORD_COUNT, this.dbTableName));
			pstmt.setInt(1, validRecordValue1);
			pstmt.setInt(2, validRecordValue2);
			rs = pstmt.executeQuery();
			rs.first();
			return rs.getInt(1);
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					"Trouble getting record count from workspace DB", e );
		}
		finally
		{	
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pstmt);
		}
	}
	
	/**
	 * Get all the data associated with a particular record by its id.
	 * @param recordId
	 */
	public ResultsWrapper getRecordData(String recordId) throws HarvestException
	{
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		try {
			pstmt = this.dbConnection.prepareStatement(
					String.format(GET_RECORD_DATA, this.dbTableName));
			pstmt.setString(1, recordId);
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
				String.format("Trouble getting record data info for record %s", recordId), e );
		}

		return new DBResultsWrapper(rs, pstmt);
	}
	
	/**
	 * Gets duplicate records based on URL. Only will return the record counts
	 * if the url is found more then once
	 */
	public ResultsWrapper getDuplicateRecordCountsByURL() throws HarvestException
	{
		return this.getDuplicateRecordCounts(GET_DUPLICATE_RECORD_BY_URL_COUNTS);
	}
	
	/**
	 * Gets duplicate records based on partner id. Only will return the record counts
	 * if the URL is found more then once
	 */
	public ResultsWrapper getDuplicateRecordCountsByPartnerID() throws HarvestException
	{
		return this.getDuplicateRecordCounts(GET_DUPLICATE_RECORD_BY_PARTNER_ID_COUNTS);
	}
	
	/**
	 * Gets duplicate records based on a duplicate query sent in
	 */
	public ResultsWrapper getDuplicateRecordCounts(String duplicateQuery) throws HarvestException
	{
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		try {
			pstmt = this.dbConnection.prepareStatement(
					String.format(duplicateQuery, this.dbTableName));
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					"Trouble getting duplicate record counts from workspace DB", e );
		}
		return new DBResultsWrapper(rs, pstmt);
	}
	
	/**
	 * Get a result list of partner ids based on record ids
	 */
	public ResultsWrapper getPartnerIdsByRecordIds(String[] recordIds) throws HarvestException
	{
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		try {
			// To do this set with unlimited where clause we first append ? marks to the
			// where the as many as we need for the records ids
			StringBuilder inStmtbuilder = new StringBuilder();
			for(String recordId:recordIds)
				inStmtbuilder.append("?,");
			inStmtbuilder.deleteCharAt(inStmtbuilder.length()-1);
			
			pstmt = this.dbConnection.prepareStatement(
					String.format(GET_PARNTER_IDS_BY_RECORD_IDS, this.dbTableName,
							inStmtbuilder.toString() ));

			int index = 1;
			// Now we put them into the pstmt now that the ? marks are in there
			for( String recordId : recordIds ) {
			   pstmt.setString(  index, recordId );
			   index++;
			}
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					"Trouble recordids by partner id from workspace DB" , e );
		}
		return new DBResultsWrapper(rs, pstmt);
	}
	
	/**
	 * Get record ids based on partner id. This is used during dup processing. A order
	 * must be specified defining in which order the records should be ordered via their
	 * record id. 
	 */
	public ResultsWrapper getRecordIdsByPartnerId(String partnerId, String order) throws HarvestException
	{
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		try {
			pstmt = this.dbConnection.prepareStatement(
					String.format(GET_RECORD_IDS_BY_PARNTER_ID, this.dbTableName, order, order));
			pstmt.setString(1, partnerId);
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					String.format("Trouble recordids by partner id %s " +
							"from workspace DB",partnerId) , e );
		}
		return new DBResultsWrapper(rs, pstmt);
	}
	
	/**
	 * Get record ids based on their url. This is used during dup processing. A order
	 * must be specified defining in which order the records should be ordered via their
	 * record id. 
	 */
	public ResultsWrapper getRecordIdsByURL(String url, String order) throws HarvestException
	{
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		try {
			pstmt = this.dbConnection.prepareStatement(
					String.format(GET_RECORD_IDS_BY_URL, this.dbTableName, order, order));
			pstmt.setString(1, url);
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					String.format("Trouble recordids by partner id %s " +
							"from workspace DB",url) , e );
		}
		return new DBResultsWrapper(rs, pstmt);
	}

	/**
	 * Method that closes all prepared statements and then closes the connection just to 
	 * re-open it again. This is a safty measure used after high volume iterations making sure
	 * all the locks on the Database are realeased before we go on. Might be unnecessary
	 * but no reason not to. It helped testing locally. 
	 */
	public void cleanupAfterIteration() throws HarvestException
	{
		this.cleanupPreparedStatments();
	}
	
	/**
	 * Closes up all the cached prepared statements to release all locks and free some
	 * memory up. Might be unnecessary but i helped with locks during development
	 */
	private void cleanupPreparedStatments()
	{
		DbUtils.closeQuietly(this.insertURIPstmt);
		this.insertURIPstmt = null;
		DbUtils.closeQuietly(this.populateRecordPstmt);
		this.populateRecordPstmt = null;
		DbUtils.closeQuietly(this.updateHandlesPstmt);
		this.updateHandlesPstmt = null;
		
		for(PreparedStatement pstmt:this.updateRecordPstmts.values())
		{
			DbUtils.closeQuietly(pstmt);
		}
		this.updateRecordPstmts = new HashMap<String, PreparedStatement>();
	}
	
	/**
	 * Method that updates a records specified record type
	 */
	public boolean updateHandles(String recordId, String metadatahandle, String resourcehandle) 
				throws HarvestException
	{
		try {
			
			if(this.updateHandlesPstmt==null)
				this.updateHandlesPstmt = this.dbConnection.prepareStatement(
						String.format(UPDATE_HANDLES_STMT, this.dbTableName ));
			
			PreparedStatement pstmt = this.updateHandlesPstmt;

			pstmt.setString(1, metadatahandle);
			pstmt.setString(2, resourcehandle);
			pstmt.setString(3, recordId);
			pstmt.execute();
			pstmt.clearParameters();
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					String.format("Trouble inserting handles for record %s", recordId), e );
		} 
		
		return true;
	}
}
