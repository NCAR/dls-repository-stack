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
package edu.ucar.dls.harvest.repository;

import java.util.ArrayList;
import java.util.List;

import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.processors.Processor;
import edu.ucar.dls.harvest.processors.repository.PopulateHarvestedRecordsDB;
import edu.ucar.dls.harvest.processors.repository.PriorSessionCountValidator;
import edu.ucar.dls.harvest.processors.repository.RepositoryProcessor;
import edu.ucar.dls.harvest.processors.repository.UpdatedSessionRecordComparison;
import edu.ucar.dls.harvest.workspaces.ResultsWrapper;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 * Abstract class that should be extended for different repository locations,
 * All abstract need to be implemented if a new Repository wants to be 
 * switched too. Some of the methods are used by processors so if you removed
 * the processors you also could remove the methods
 *
 */
public abstract class Repository {
	// Defined by spec agent populated in DB should be ingest
	protected static final String INGEST_AGENT = "ingest";
	protected String agent = INGEST_AGENT;
	protected Workspace workspace = null;
	
	protected List<RepositoryProcessor> preProcessors = 
			new ArrayList<RepositoryProcessor>();
	protected List<RepositoryProcessor> postProcessors = 
		new ArrayList<RepositoryProcessor>();
	protected boolean isTest = false;
	
	/**
	 * The main method that is called by the ingestor to start the 
	 * population of the repos. For the most part this method should not be
	 * overwritten. instead createNewSessionData should be implemented along
	 * with adding processors
	 * @throws HarvestException
	 */
	public void populate() throws HarvestException
	{
		// Initialize the processors
		this.setProcessors();
		Processor.initializeProcessors(this.preProcessors, this.workspace);
		
		// Run through them prior to creation of new session data
		for( RepositoryProcessor processor: this.preProcessors )
		{
			processor.run(this);
			HarvestRequest.checkForInterruption(
				"Repository.populate()->running processors");
		}
		
		if (!this.isTest )
		{
			this.createNewSessionData();
			
			this.setPostProcessors();
			Processor.initializeProcessors(this.postProcessors, this.workspace);

			// Run through them prior to creation of new session data
			for( RepositoryProcessor processor: this.postProcessors )
			{
				try
				{
					processor.run(this);
				}
				catch(HarvestException e)
				{
					System.out.println("Repository.populate() Error: " + e);
					e.printStackTrace();
					processor.addError("Repository->populate():postProcessor->run()",
					    String.format(
				"Uncaught Harvest Exception for Post Repository processor. Programmer errror. " +
			    "These processors SHOULD not throw harvest exceptions since the harvest " +
			    "went through and cannot be reverted. Error: %s.", e.getMessage()));
				}
				HarvestRequest.checkForInterruption(
					"Repository.populate()->running processors");
			}
		}
	}
	
	/**
	 * Initialize the Repository
	 * @param workspace
	 * @throws HarvestException
	 */
	public void initialize(Workspace workspace, boolean isTest) 
			throws HarvestException {
		this.isTest = isTest;
		this.workspace = workspace;
	}
	
	/**
	 * Set the processors that are needed. Note if this method is overwritten
	 * in subclass you should make sure to call super.setProcoessors()
	 * @throws HarvestException
	 */
	protected void setProcessors() throws HarvestException{	
		this.preProcessors.add(new PriorSessionCountValidator());
		this.preProcessors.add(new UpdatedSessionRecordComparison());
	}
	
	/**
	 * Get the processors from the repository
	 * @return
	 */
	public List<RepositoryProcessor> getPreProcessors() {
		return preProcessors;
	}
	
	public void setPostProcessors() throws HarvestException{	
		this.postProcessors.add(new PopulateHarvestedRecordsDB());
	}
	
	public List<RepositoryProcessor> getPostProcessors() {
		return this.postProcessors;
	}
	
	/**
	 * Abstract method that stores the workspace records in the repository.
	 * This must be implemented by sub classes
	 */
	protected abstract void createNewSessionData() throws HarvestException;
	
	/**
	 * Abstract method that gets the current session id from the repos, if
	 * one doesn't exits it should return null. This method is called from
	 * processors
	 */
	public abstract String getCurrentSessionId() throws HarvestException;
	
	/**
	 * Abstract method that must be implemented by subclasses which gets the record
	 * count from a particular session. This is called by processors
	 * @param sessionId
	 * @return
	 * @throws HarvestException
	 */
	public abstract int getSessionMetaDataCount(String sessionId) throws HarvestException;
	
	/**
	 * Abstract method that gets a sessions records via sessionId. Note this should
	 * return ordered by partnerid
	 * @param sessionId
	 * @return
	 * @throws HarvestException
	 */
	public abstract ResultsWrapper getSessionMetadata(String sessionId) throws HarvestException;
	
	/**
	 * Clean method that may or may not be overwritten, if it is that method should call
	 * super.clean()
	 */
	public void clean() {
		
	}
	
}
