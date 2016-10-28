package edu.ucar.dls.harvest.resources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.dbutils.DbUtils;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.resources.Resource;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 * DB Connector class that is used within all classes to create a mysql connection
 * this is also the one place that you would have to change if you were changing 
 * the database type ie. mysql->oracle
 *
 */
public class DBConnector extends Resource{

	public final static String WORKSPACE_CONNECTION = "workspace";
	public final static String HARVEST_CONNECTION = "harvest";
	public final static String REPOSITORY_CONNECTION = "repository";
	
	private HashMap<String, Connection> connections = new HashMap<String, Connection>();
	
	public DBConnector(Workspace workspace) {
		super(workspace);
	}
	
	public static Connection getConnection(String dbURL, String dbUser, String dbPass) 
					throws HarvestException
	{
		Connection connection = null;	   
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(dbURL, 
							dbUser,
							dbPass);
		}
	    catch (ClassNotFoundException e) {
				throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
						"Could not find the driver requested.", e);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
					"Could not create connection given auth params.", e);
		}
		return connection;
	}
	
	/**
	 * Get or create a connection given a name. This in essence can be considered a connection
	 * pool for the harvest. Its done as resource so that a workspace does not need to know
	 * about connections
	 * @param name
	 * @return
	 * @throws HarvestException
	 */
	public Connection getConnection(String name) throws HarvestException
	{
		try {
			if(!this.connections.containsKey(name)||this.connections.get(name).isClosed())
			{
				Connection connection = null;
				if(name.equals(WORKSPACE_CONNECTION))
					connection = createWorkspaceConnection();
				else if(name.equals(HARVEST_CONNECTION))
					connection = createHarvestConnection();
				else if(name.equals(REPOSITORY_CONNECTION))
					connection = createRepositoryConnection();
				else
					throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
							"Connection name %s not found in DBConnector resource.", 
							"DBConnector.getConnection");
				this.connections.put(name, connection);
			}
		} catch (SQLException e) {
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
					"An issued happend with Connection name %s. Could not test wether it "+
					"was closed yet.", e);
		}
		return this.connections.get(name);
	}
	
	/**
	 * Cleaning the resource, by closing all the DB connections
	 */
	public void clean()
	{
		for(Connection connection: this.connections.values())
		{
			DbUtils.closeQuietly(connection);
		}
	}
	
	/**
	 * Create a workspace connection
	 * @return
	 * @throws HarvestException
	 */
	public static Connection createWorkspaceConnection() throws HarvestException
	{
		return DBConnector.getConnection(Config.DBWorkspace.DB_URL, 
				Config.DBWorkspace.DB_USER,
				Config.DBWorkspace.DB_PASS);
	}
	
	/**
	 * Create a repository connection
	 * @return
	 * @throws HarvestException
	 */
	public static Connection createRepositoryConnection() throws HarvestException
	{
		return DBConnector.getConnection(Config.DBRepository.DB_URL, 
				Config.DBRepository.DB_USER,
				Config.DBRepository.DB_PASS);
	}
	
	/** 
	 * Create a harvest connection
	 * @return
	 * @throws HarvestException
	 */
	public static Connection createHarvestConnection() throws HarvestException
	{
		return DBConnector.getConnection(Config.DBHarvest.DB_URL, 
				Config.DBHarvest.DB_USER,
				Config.DBHarvest.DB_PASS);
	}
}
