package edu.ucar.dls.harvest.harvesters;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
/**
 *  Factory class that creates a harvester based on a specified protocol. Any
 *  new harvester that is created should be added here
 *  Authored 4/2/2013
 */
public class HarvesterFactory {
	public static Harvester createHarvester(String protocol) throws HarvestException
	{
		if(protocol.equals(Config.Protocols.OAI))
			return new OAI_PMHHarvester();
		else if(protocol.equals(Config.Protocols.LR_SLICE))
			return new LRHarvester();
		else if(protocol.equals(Config.Protocols.HARVESTED_RECORDS_DB))
			return new HarvestedRecordsDBHarvester();
		else
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					String.format("Harvest Protocol %s is not implemented in HarvestFactory.", 
							protocol ),
					"HarvestFactory()");
	}
}
