package edu.ucar.dls.harvest.workspaces;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.commons.dbutils.DbUtils;

import edu.ucar.dls.harvest.exceptions.HarvestException;

/**
 * Wrapper class that is returned from the workspace to hide the fact that
 * the workspace is in a DB. If we switched to lets say couch, you would just
 * create a CouchResults Wrapper that contains a getResults and a clean method.
 * No reason to make changes in the ingestor.
 *
 */
public class DBResultsWrapper implements ResultsWrapper{
	private ResultSet resultSet = null;
	private PreparedStatement preparedStatement = null;
	
	public DBResultsWrapper(ResultSet rs, PreparedStatement ps)
	{
		this.resultSet = rs;
		this.preparedStatement = ps;
	}
	
	/**
	 * Get the results from the wrapper which should be an 
	 * Iterater object[] containing Strings and byte arrays
	 */
	public Iterator getResults() throws HarvestException
	{
		return new ResultSetIterator(this.resultSet);

	}
	
	/**
	 * Clean the results wrapper, in this case we clean the resultset
	 * and prepared statement
	 */
	public void clean()
	{
		DbUtils.closeQuietly(this.resultSet);
		DbUtils.closeQuietly(this.preparedStatement);
	}
}
