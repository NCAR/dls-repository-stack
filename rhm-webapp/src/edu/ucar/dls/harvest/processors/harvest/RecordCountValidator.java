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
