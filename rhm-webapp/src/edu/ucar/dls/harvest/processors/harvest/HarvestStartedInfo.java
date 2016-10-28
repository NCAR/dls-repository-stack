package edu.ucar.dls.harvest.processors.harvest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.processors.Processor;
import edu.ucar.dls.harvest.resources.DBLogger;

/**
 *  Processor class used by LR Harvester to log initial information about the 
 *  harvest prior to harvesting to enable users to see that it is being harvested
 */

public class HarvestStartedInfo extends Processor {
	protected String message = null;

	public final static String INITIAL_LOG_MSG = 
		"Begining to harvest collection\n" +
		"%s" +
		"Initial harvest url: %s";
	
	
	/**
	 *  Log message to the DBLogger
	 *  @throws HarvestException
	 */
	public void run() throws HarvestException {

		// Special case usually we don't want processors to log to the report directly
		// But users want this to appear immediately
		((DBLogger)workspace.getResource(
				DBLogger.class)).logInfo(
						message);
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}