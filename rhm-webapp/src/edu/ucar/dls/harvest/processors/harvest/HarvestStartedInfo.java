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
