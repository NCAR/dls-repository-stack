package edu.ucar.dls.harvest.processors.repository;

import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.processors.Processor;
import edu.ucar.dls.harvest.repository.Repository;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 * Abstract Processor class that must extended by any record processors that
 * need to be ran. Run is the only method that is abstract and must be implemented
 * in subclasses. This is a subclass of Processor and exists because processors
 * need an extra parameter of repository in order to useful to validating the 
 * repository
 */
public abstract class RepositoryProcessor extends Processor{
	/**
	 * Abstract method Run which must be implemented
	 * @param repository
	 * @param workspace
	 * @throws HarvestException
	 */
	public abstract void run(Repository repository) throws HarvestException;
	
}
