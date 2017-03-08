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
