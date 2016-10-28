package edu.ucar.dls.harvest.resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;

import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 * Class that encapsulates the writing of logs to the log db with the correct uuid so it
 * can be fetched for a certain harvest
 * @author dfinke
 *
 */
public class DBLogger extends Resource{
	private String uuid = null;
	private boolean createdOutsideWorkspace = false;
	private Connection dbConnection = null;
	
	public static final String INFORMATION = "INFORMATION";
	public static final String WARNING = "WARNING";
	public static final String ERROR = "ERROR";
	public static final String RECORD_ERROR = "Record Error";
	public static final String RECORD_WARNING = "Record Warning";
	public static final String RECORD_INFO = "Record Info";
	
	public static final String FINAL_RESULT_SUCCESS = "Final Result - Success";
	public static final String FINAL_RESULT_SUCCESS_W_WARNINGS = 
		"Final Result - Success with warnings";
	public static final String FINAL_RESULT_SUCCESS_W_ERRORS = 
		"Final Result - Success with errors";
	public static final String FINAL_RESULT_FAILURE = 
		"Final Result - Failure";

	private static final String LOG_INSERT_STMT = 
		"INSERT INTO  ingest_log " +
			"(uuid, message_text, message_level, log_timestamp, role, program_name) " +
		"VALUES " +
			"(?, ?, ?, CURRENT_TIMESTAMP, 'DIAGNOSTIC', 'ingest')";
	
	private static final String LOG_DETAILS = 
		"SELECT message_level, message_text " +
	    "FROM ingest_log " +
	    "WHERE uuid=? " +
	    "ORDER BY log_id desc";
	
	/**
	 * One of two constructors. This one takes the workspace. So it can use the connection
	 * pool.
	 * @param workspace
	 * @throws HarvestException
	 */
	public DBLogger(Workspace workspace) throws HarvestException {
		super(workspace);
		this.uuid = workspace.harvestRequest.getUuid();
		this.dbConnection  = ((DBConnector)workspace.getResource(
				DBConnector.class)).getConnection(DBConnector.HARVEST_CONNECTION);
	}
	
	/**
	 * The other constructor that is to be used when a workspace was not created first.
	 * In this case it will need to create its own connection to the DB. The thread
	 * calling this method should call logger.clean to close the connection
	 * @param uuid
	 * @throws HarvestException
	 */
	public DBLogger(String uuid) throws HarvestException
	{
		super(null);
		createdOutsideWorkspace = true;
		this.dbConnection = DBConnector.createHarvestConnection();
		this.uuid = uuid;
	}
	public void logInfo(String msg)
	{
		this.log(msg, INFORMATION);
	}
	public void logError(String msg)
	{
		this.log(msg, ERROR);
	}
	public void logWarning(String msg)
	{
		this.log(msg, WARNING);
	}
	public void logRecordWarning(String msg)
	{
		this.log(msg, RECORD_WARNING);
	}
	public void logRecordError(String msg)
	{
		this.log(msg, RECORD_ERROR);
	}
	public void logRecordInfo(String msg)
	{
		this.log(msg, RECORD_INFO);
	}
	
	public void logFinalResultFailure(String msg)
	{
		this.log(msg, FINAL_RESULT_FAILURE);
	}
	public void logFinalResultSuccess(String msg)
	{
		this.log(msg, FINAL_RESULT_SUCCESS);
	}
	public void logFinalResultSuccessWithWarnings(String msg)
	{
		this.log(msg, FINAL_RESULT_SUCCESS_W_WARNINGS);
	}
	public void logFinalResultSuccessWithErrors(String msg)
	{
		this.log(msg, FINAL_RESULT_SUCCESS_W_ERRORS);
	}

	/**
	 * Main log method that everything else calls, This puts the message into the db
	 * @param msg
	 * @param level
	 */
	private void log(String msg, String level)
	{
		PreparedStatement insert_log_pstmt=null;
		try {
			insert_log_pstmt = this.dbConnection.prepareStatement(
					LOG_INSERT_STMT);
			insert_log_pstmt.setString(1, this.uuid);
			insert_log_pstmt.setString(2, msg);
			insert_log_pstmt.setString(3, level);
			insert_log_pstmt.execute();
			insert_log_pstmt.close();
		} catch (SQLException e) {
			// TODO we got some massive issues, what to do if we can't log to db
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(insert_log_pstmt);
		}
		
	}
	
	/**
	 * Gets all current details for the current harvest UUID. The uuid is set by the
	 * constructor. Not UUID is required to use the DBLogger
	 * @return
	 */
	public List<String> getLogDetails()
	{
		List<String> details = new ArrayList<String>();
		PreparedStatement logDetailsPstmt=null;
		try {
			logDetailsPstmt = this.dbConnection.prepareStatement(
					LOG_DETAILS);
			logDetailsPstmt.setString(1, this.uuid);
			
			ResultSet rs = logDetailsPstmt.executeQuery();
			while(rs.next())
			{
				details.add(String.format("%s - %s" , rs.getString(1), rs.getString(2)));
			}
			rs.close();
		} catch (SQLException e) {
			// can't really do anything, Just return empty details
		}
		finally
		{
			DbUtils.closeQuietly(logDetailsPstmt);
		}
		return details;
		
	}
	
	/** 
	 * Clean the logger. In this case it will close the connection if the DBLogger was not created
	 * via the workspace
	 */
	public void clean()
	{
		if(createdOutsideWorkspace)
			DbUtils.closeQuietly(this.dbConnection);
	}
}
