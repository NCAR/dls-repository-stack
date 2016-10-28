package edu.ucar.dls.harvest.processors.harvest;
import org.apache.commons.lang3.StringUtils;

import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.harvesters.OAI_PMHHarvester;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 *  Processor class used by OAI Harvesters to log initial information about the 
 *  harvest prior to harvesting to enable users to see that it is being harvested
 */

public class OAIHarvestStartedInfo extends HarvestStartedInfo {
	
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
	public void createMessage() throws HarvestException {
		String [] specifiedCollectionSets = workspace.harvestRequest.getCollection_sets();
		String setsString = "";
		String paramString = null;
		if(specifiedCollectionSets!=null && specifiedCollectionSets.length>0)
		{
			setsString = String.format("Set(s): %s\n", 
					StringUtils.join(specifiedCollectionSets, ", "));
			paramString = OAI_PMHHarvester.createParamString(
					specifiedCollectionSets[0], workspace.harvestRequest.getMetadataPrefix());
		}
		else
		{
			paramString = OAI_PMHHarvester.createParamString(null, 
					workspace.harvestRequest.getMetadataPrefix());
		}
		
		String initialServiceRequestUrl = 
			workspace.harvestRequest.getBaseUrl() + paramString;

		// Special case usually we don't want processors to log to the report directly
		// But users want this to appear immediately
		this.setMessage(String.format(INITIAL_LOG_MSG, setsString, initialServiceRequestUrl));
	}
}