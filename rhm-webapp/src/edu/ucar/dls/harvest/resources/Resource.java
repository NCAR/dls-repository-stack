package edu.ucar.dls.harvest.resources;

import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 * Abstract class that should be extended to add new resources for the workspace to use
 */

public abstract class Resource {
	protected Workspace workspace = null;
	
	/**
	 * Clean method that should overwritten to make sure all dependcies are taken
	 * care of. Clean for a resource means that the system is doing using it and
	 * chances are no objects reference it anymore
	 */
	public void clean()
	{
		
	}
	
	/** 
	 * The main constructor for all resources
	 * @param workspace
	 */
	public Resource(Workspace workspace)
	{
		this.workspace = workspace;
	}
}