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
