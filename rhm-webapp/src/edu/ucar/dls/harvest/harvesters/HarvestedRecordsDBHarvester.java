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

import org.apache.commons.io.FileUtils;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.harvesters.Harvester;
import edu.ucar.dls.harvest.processors.harvest.HarvestStartedInfo;
import edu.ucar.dls.harvest.resources.HarvestedRecordsDB;
import edu.ucar.dls.harvest.workspaces.ResultsWrapper;
import edu.ucar.dls.harvest.workspaces.Workspace;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;

/**
 *  Harvester that harvests the original records from a table that houses past
 *  successful harvests. This table is also maintained by the ingestor once a
 *  harvest that is not from this harvester completes and is successful
 */

public class HarvestedRecordsDBHarvester extends Harvester{
	
	private static String HARVEST_STARTED_MESSAGE = "Begining to harvest collection\n" +
								"Source: HarvestedRecords Database.\n" +
								"Session Id: %s.";
	private static String NO_HARVESTED_FILES_MESSAGE = "Harvesting from the HarvestedRecord means "+
	                                "that the records beings harvested are from the last successful external harvest; " +
	                                "therefore no files are associated with this session. If you need to see "+
	                                "the corresponding harvested files for these records, view session harvest " +
	                                "%s which was the last successful harvest";
	
	private String latestSession = null;
	
	/**
	 * Initialize the Harvester to make sure that the setSpec in the harvestRequest
	 * even has a session inside the HarvestRecords table.
	 */
	public void initialize(Workspace workspace) throws HarvestException{
		super.initialize(workspace);
		String setSpec = this.workspace.harvestRequest.getSetSpec();		
		
		HarvestedRecordsDB harvestedRecordsDBResource = 
					(HarvestedRecordsDB)this.workspace.getResource(HarvestedRecordsDB.class);
		this.latestSession = harvestedRecordsDBResource.getLatestSession(setSpec);
		
		// If it doesn't have one stop the harvest now.
		if(this.latestSession==null)
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					String.format("No previous harvest found in HarvestedRecord table for "+
					"setSpec %s. Cannot harvest", setSpec), 
					"HarvestedRecordsDBHarvester->populateRecords()");

		
	}
	
	protected void setPreProcessors() throws HarvestException
	{
		super.setPreProcessors();
		
		// Adding on pre-processor which creates the message in the logger
		HarvestStartedInfo processor = new HarvestStartedInfo();
		processor.setMessage(String.format(HARVEST_STARTED_MESSAGE, this.latestSession));
		this.preProcessors.add(processor);
	}

	/**
	 * Method for downloading files which is required for a harvester. Since
	 * we are pulling from a database we don't download anything.
	 */
	protected void downloadFiles() throws HarvestException
	{
		// No need to download any files since all records are in the db
	}
	
	/**
	 * Populate the workspace with the records from the database
	 */
	public void populateRecords() throws HarvestException{

		String setSpec = this.workspace.harvestRequest.getSetSpec();
		try
		{
			File harvestDirectory = this.getHarvestDirectory();
			File filePath = new File(harvestDirectory, "no_harvested_files.txt");
			FileUtils.writeStringToFile(filePath, 
					String.format(NO_HARVESTED_FILES_MESSAGE, this.latestSession),
					Config.ENCODING);
			
			HarvestedRecordsDB harvestedRecordsDBResource = (HarvestedRecordsDB)this.workspace.getResource(
					HarvestedRecordsDB.class);
			ResultsWrapper resultsWrapper = harvestedRecordsDBResource.getRecords(setSpec, this.latestSession);
			Iterator results = resultsWrapper.getResults();
			
			if(results==null)
				return;
			while (results.hasNext())
			{
				Object[] recordData = (Object[])results.next();
				String data = null;
				
				try {
					data = new String((byte[])recordData[1], Config.ENCODING);
				} catch (UnsupportedEncodingException e) {
					throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
							"Cannot convert byte string to encoding utf-8", e);
				}
				
				// populating the workspace with args, identifer, data, url, working_date
				this.workspace.populateRecord((String)recordData[0], 
											  data,
											  (String)recordData[2],
											  (Date)recordData[3]);
				HarvestRequest.checkForInterruption(
					"Harvester.populateRecords()");
			}
			resultsWrapper.clean();
			this.workspace.cleanupAfterIteration();
		}
		catch (HarvestException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
					String.format("Error populating workspace with records from "+
							"harvested records table for setSpec %s.", setSpec), e);
		}
		
	}
}
