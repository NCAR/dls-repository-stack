package edu.ucar.dls.harvest.workspaces;

import java.util.Iterator;

import edu.ucar.dls.harvest.exceptions.HarvestException;

/** 
 * Interface that is used to wrap results from a given workspace to the
 * Ingestor process
 *
 */
public interface ResultsWrapper {
	public Iterator getResults() throws HarvestException;
	public void clean();
}
