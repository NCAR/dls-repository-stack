package edu.ucar.dls.harvest.processors.harvest;
import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.processors.Processor;

/**
 * Processor that validates that there is at least 1 record that was harvested.
 * if not it throws a Harvest Exception
 */
public class RecordCountValidator extends Processor {
	
	String validatedErrorMsg = "0 records were found in the harvest.";

	/**
	 * Over ridden run method that throws a HarvestException if there are 0 records
	 */
	public void run() throws HarvestException {
		if(workspace.getRecordCount()==0)
			throw new HarvestException(Config.Exceptions.OAI_RESPONSE_ERROR_CODE,
					validatedErrorMsg, "HarvestedRecordCountValidator.run()" );
	}
}