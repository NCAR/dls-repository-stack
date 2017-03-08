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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.harvesters.LRHarvester;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 *  Processor class used by LR Harvester to log initial information about the 
 *  harvest prior to harvesting to enable users to see that it is being harvested
 */

public class LRHarvestStartedInfo extends HarvestStartedInfo {
	
	public final static String INITIAL_LOG_MSG = 
		"Begining to harvest collection\n" +
		"%s" +
		"Initial harvest url: %s";

	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);
		this.createMessage();
	}
	
	/**
	 *  Use the workspace and harvester to create a string message containing 
	 *  what sets are being harvested and the harvest url. Special note, most
	 *  reporting is done after after harvesting by using this.add info instead of
	 *  log. This case is special because users want the ability to know that the 
	 *  harvester is running 
	 *  @throws HarvestException
	 */
	private void createMessage() throws HarvestException {
		HashMap<String, List<List<String>>> specifiedCollectionSets = 
			workspace.harvestRequest.getSliceRequests();
		String setsString = "";
		String paramString = "";
		if(specifiedCollectionSets!=null && specifiedCollectionSets.size()>0)
		{
			Set<String> requestParams = specifiedCollectionSets.keySet();
			
			if(requestParams.size()>0)
			{
				setsString = String.format("Request Params(s): %s\n", 
					StringUtils.join(requestParams, ", "));
			
			
				paramString = LRHarvester.createParamString(
						requestParams.iterator().next());
			}
		}
		
		
		String initialServiceRequestUrl = 
			workspace.harvestRequest.getBaseUrl() + "/slice"+paramString;

		// Special case usually we don't want processors to log to the report directly
		// But users want this to appear immediately
		this.setMessage(
				String.format(INITIAL_LOG_MSG, setsString, initialServiceRequestUrl));
	}
}
