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
package edu.ucar.dls.harvest.processors.repository;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.repository.Repository;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 * Repository Processor that does two things, It both makes sure that the new session id
 * is not the same as the last and that comparing the new records to the old session
 * records a minimum threshold is reached. For now not going through the threshold
 * is just a warning. 
 *
 */
public class PriorSessionCountValidator extends RepositoryProcessor{
	
	/**
	 * Run the processor making sure that the current session id isn't the same as
	 * tbe previous, should never be. Note for running some of test cases you might have
	 * to comment that check out.
	 */
	public void run(Repository repository) throws HarvestException
	{
		String currentSessionId = repository.getCurrentSessionId();
		
		// todo If the sessions are the same its an error, uncomment out after testing
		if (currentSessionId!=null && currentSessionId.equals(workspace.harvestRequest.getUuid()))
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
					String.format("New session id (%s) cannot be the same as the existing one, since it " +
					"will create duplicate primary keys and error out the sql.",
					currentSessionId), "PriorSessionCountValidator.run()" );
		
		int currentMetaDataCount = repository.getSessionMetaDataCount(currentSessionId);
				
		int possibleNewMetaDataCount = workspace.getValidRecordCount();
		if(possibleNewMetaDataCount < currentMetaDataCount * 
				Config.MIN_RECORD_COUNT_DISCREPANCY_THRESHOLD)
		{
			String errorMsg = String.format(Config.Exceptions.MAX_RECORD_COUNT_ERROR_MSG, 
									currentMetaDataCount,
									possibleNewMetaDataCount, 
									Config.MIN_RECORD_COUNT_DISCREPANCY_THRESHOLD,
									(float)possibleNewMetaDataCount/(float)currentMetaDataCount);
			
			String documentId = String.format("%s->%s", currentSessionId, 
					workspace.harvestRequest.getUuid());
			this.addWarning(documentId, errorMsg);
		}
	}
}
