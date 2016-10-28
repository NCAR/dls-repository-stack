package edu.ucar.dls.harvest.processors.harvest_files;
import java.io.File;

import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.processors.Processor;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 *  Abstract class for processors that deal with harvest files. A run method
 *  must be implemented for all subclasses 
 */

public abstract class HarvestFileProcessor extends Processor{
	public abstract void run(File harvestFile) throws HarvestException;
}
