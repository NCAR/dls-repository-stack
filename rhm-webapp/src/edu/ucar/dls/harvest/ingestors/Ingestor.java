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
package edu.ucar.dls.harvest.ingestors;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.exceptions.MaxErrorThresholdException;
import edu.ucar.dls.harvest.harvesters.Harvester;
import edu.ucar.dls.harvest.harvesters.HarvesterFactory;
import edu.ucar.dls.harvest.processors.Processor;
import edu.ucar.dls.harvest.processors.record.RecordProcessor;
import edu.ucar.dls.harvest.reporting.HarvestReport;
import edu.ucar.dls.harvest.reporting.ProcessorReport;
import edu.ucar.dls.harvest.repository.DBRepository;
import edu.ucar.dls.harvest.resources.HandleService;
import edu.ucar.dls.harvest.workspaces.DBWorkspace;
import edu.ucar.dls.harvest.workspaces.DummyWorkspace;
import edu.ucar.dls.harvest.workspaces.ResultsWrapper;
import edu.ucar.dls.harvest.workspaces.Workspace;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.*;

/**
 *  Class that is instantiated by config xml files and harvests records
 *  into the repository accordingly.
 *  Authored 4/2/2013
 */

@XmlRootElement(name="Ingestor")
public class Ingestor {
	// Set in harvestRequest prior to it calling ingest()
	protected HarvestRequest harvestRequest = null;
	
	// created by ingest() from params sent in via harvestRequest
	protected Workspace workspace = null;
	
	// populated via XML using JAXB per different native format
	protected String targetFormat = null;
	protected boolean populateResourceHandles = false;
	protected String harvestDataFormat = Config.DataFormats.XML;
	
	// Record Processors
	@XmlElementWrapper(name="pre-processors")
	@XmlElementRef
	public List<RecordProcessor> preProcessors = 
					new ArrayList<RecordProcessor>();
	
	@XmlElementWrapper(name="target-format-processors")
	@XmlElementRef
	public List<RecordProcessor> targetFormatProcessors = 
		new ArrayList<RecordProcessor>();
	
	@XmlElementWrapper(name="native-format-processors")
	@XmlElementRef
	public List<RecordProcessor> nativeFormatProcessors = 
					new ArrayList<RecordProcessor>();
	
	@XmlElementWrapper(name="post-harvest-processors")
	@XmlElementRef
	public List<Processor> postHarvestProcessors = 
		new ArrayList<Processor>();
	
	// harvester
	public Harvester harvester = null;
	
	//Repository
	public DBRepository repository = null;

	
	public void initForSingleTransforms(String nativeFormat, String collectionId) throws Exception
	{
		try
		{
			HarvestRequest hr = new HarvestRequest();
			hr.setNativeFormat(nativeFormat);
			hr.setSetSpec(collectionId);
			this.workspace = new DummyWorkspace();
			workspace.initialize(hr);
			
			// initialize and run native format processors
			RecordProcessor.initializeRecordProcessors(this.preProcessors,
					workspace);
			// initialize and run native format processors
			RecordProcessor.initializeRecordProcessors(this.targetFormatProcessors,
					workspace);
		}
		catch(HarvestException e)
		{
			throw new Exception(e.getError());
		}
	}
	
	public String normalizeRecord(String recordId, String record) throws Exception
	{	
			try
			{
				record = runRecordProcessorsForIndiviualRecord(this.preProcessors, recordId, record);
				record = runRecordProcessorsForIndiviualRecord(this.targetFormatProcessors, recordId, record);
				return record;
			}
			catch(HarvestException e)
			{
				throw new Exception(e.getError());
			}
	}
	/**
	 *  Method for running record processors and updating their workspace data
	 *  if the record changed in the processor
	 * @param recordProcessors record processors to be executed
	 */
	protected String runRecordProcessorsForIndiviualRecord(List<RecordProcessor> recordProcessors,
									String recordId, String record
									) 
			throws HarvestException
	{
		if(recordProcessors.size()==0)
			return record;
		
		for(RecordProcessor recordProcessor:recordProcessors)
		{
			String updatedRecord = recordProcessor.run(recordId, record);
			
			if(recordProcessor.getErrorCount()>0)
			{
				String message = recordProcessor.getErrors().get(0).getMessage();
				recordProcessor.clear();
				throw new HarvestException(null, message , recordProcessor.getName());
				
			}
			// If the updated record is not null that means it was changed. Mark the
			// record as should e updated
			if (updatedRecord!=null)
			{
				record = updatedRecord;
			}
		}
		return record;
	}
	
	/**
	 *  The main call to start the ingest process, This initializes the harvester,
	 *  repository, workspace and processors. Make the calls in the correct order and
	 *  either reports a sucessful harvest or reports on the errors. After all processes
	 *  are finished it cleans everything that it created. This is important because alot
	 *  of these objects contains connections to databases
	 * @throws HarvestException 
	 */
	
	public void ingest() throws HarvestException
	{
		prtln("ingest() called...");

		this.workspace = new DBWorkspace();
		workspace.initialize(this.harvestRequest);

		try
		{

			// Setting target format and populate resource handles 
			// in the workspace so other parts of the program can access them
			this.workspace.setTargetFormat(this.targetFormat);
			this.workspace.setPopulateResourceHandles(this.populateResourceHandles);
			this.workspace.setHarvestDataFormat(this.harvestDataFormat);
			
			Harvester harvester = HarvesterFactory.createHarvester(
					this.harvestRequest.getProtocol());
			harvester.setPostProcessors(this.postHarvestProcessors);


			prtln("Initializing harvester");

			harvester.initialize(this.workspace);
			this.harvester = harvester;
			this.harvester.harvest();
			
			// initialize and run native format processors
			RecordProcessor.initializeRecordProcessors(this.preProcessors,
					workspace);
			this.runRecordProcessors(this.preProcessors,
					DBWorkspace.VALIDATED_RECORD_TYPE, 
					DBWorkspace.VALIDATED_RECORD_TYPE);
			this.checkErrorThreshold();

			prtln("Finished running record pre-processors...");

			// Now that the records have been validated that they are good
			// records and the URI might be set, we grab the handles. These
			// could be grabbed via a handle in XML, but it would be extra
			// queries against the DB and all records need a metadata handle
			// therefore we will just have direct code do it.
			RecordProcessor handlesProcessor = new RecordProcessor();
			handlesProcessor.initialize(workspace);
			this.populateHandles(handlesProcessor);
			List<RecordProcessor>processorList = new ArrayList<RecordProcessor>();
			processorList.add(handlesProcessor);
			this.markErrorRecords(processorList);
			this.targetFormatProcessors.add(handlesProcessor);
			this.checkErrorThreshold();
			
			// initialize and run target format processors
			RecordProcessor.initializeRecordProcessors(this.targetFormatProcessors,
					workspace);
			this.runRecordProcessors(
					this.targetFormatProcessors,
					DBWorkspace.VALIDATED_RECORD_TYPE, 
					DBWorkspace.TRANSFORMED_RECORD_TYPE
					);
			
			this.checkErrorThreshold();

			
			// These processors are run after target processors have ran since
			// These will affect the native xml but we don't want it to go through
			// to the target processors. The only thing that uses these is normalizing
			// the lar record.
			
			// initialize and run post native format processors
			RecordProcessor.initializeRecordProcessors(this.nativeFormatProcessors,
					workspace);
			this.runRecordProcessors(
					this.nativeFormatProcessors,
					DBWorkspace.VALIDATED_RECORD_TYPE, 
					DBWorkspace.VALIDATED_RECORD_TYPE
					);
			
			this.checkErrorThreshold();

			// Time to populate the repository
			this.repository = new DBRepository();
			
			// Last argument is commit session. Which is true as long as this is not
			// a test
			this.repository.initialize(this.workspace, this.harvestRequest.getIsTest());
			this.repository.populate();
			
			// We also mark the workspace as success so it knows how to clean up after 
			// itself
			this.workspace.setResult(Workspace.SUCCESS_RESULT);
			
			// If it makes it all the way here without a harvestException that means it
			// was successful. Therefore report on the processor issues and then report
			// success
			
			int[] issueCounts = this.reportProcessorIssues();

			new HarvestReport(workspace).reportHarvestSuccess(this.workspace,
					issueCounts);

			
		}
		catch(MaxErrorThresholdException e)
		{
			// For MaxErrorThresholdException we want to also report on the processor 
			// issues since those are the reason this harvest failed
			this.reportProcessorIssues();
			
			// let the workspace now it failed so it knows how to clean
			this.workspace.setResult(Workspace.FAILURE_RESULT);
			
			// Now we can report on the harvest failure
			new HarvestReport(workspace).reportHarvestFailure((HarvestException)e);
			
		}
		catch(HarvestException e)
		{
			e.printStackTrace();
			
			// let the workspace now it failed so it knows how to clean
			this.workspace.setResult(Workspace.FAILURE_RESULT);
			new HarvestReport(workspace).reportHarvestFailure(e);
		} 
		catch(Exception e)
		{
			// Unexpected exceptions can happen. That means we didn't realize it
			// might happen and wrap it up in a harvest exception. Once these happen
			// Code should be added to the code that threw it to wrap it up in a
			// harvest exception
			e.printStackTrace();
			
			// let the workspace now it failed so it knows how to clean
			this.workspace.setResult(Workspace.FAILURE_RESULT);
			new HarvestReport(workspace).reportHarvestFailure(e);
		}
		finally
		{
			this.clean();
		}
	}
	
	/**
	 * Populate the handles for the entire workspace database. We will always populate
	 * resource handle but not necessarily  the resource url. Wheter or not that is set
	 * is based on the config file's populate-resource-handles attribute 
	 * @param handlesProcessor
	 * @throws HarvestException
	 */
	private void populateHandles(RecordProcessor handlesProcessor) throws HarvestException {
		ResultsWrapper resultsWrapper = this.workspace.getRecordsAllData();
		Iterator results = resultsWrapper.getResults();
		
		while (results.hasNext())
		{	
			Object[] recordData = (Object[])results.next();
			
			String partnerId = (String)recordData[0];
			String recordId = ((Integer)recordData[5]).toString();
			String url = (String)recordData[4];
			
			String metadataHandle = getMetadataHandle(recordId, partnerId, handlesProcessor);
			
			String resourceHandle = null;
			if(this.workspace.isPopulateResourceHandles())
				resourceHandle = this.getResourceHandle(recordId, url, handlesProcessor);
			
			this.workspace.updateHandles(recordId, metadataHandle, resourceHandle);
			HarvestRequest.checkForInterruption(
					"Ingestor.populateHandles");
		}
		resultsWrapper.clean();
		this.workspace.cleanupAfterIteration();
		
	}

	/** 
	 * Retrieve the metadata handle for a particular partner id and set spec
	 * @param recordId
	 * @param partnerId
	 * @param handlesProcessor
	 * @return
	 * @throws HarvestException
	 */
	private String getMetadataHandle(String recordId, String partnerId, 
			Processor handlesProcessor) throws HarvestException
	{
		String metadataHandle = null;
		try
		{
			// Try to fetch the metadatandle from the service
			metadataHandle = HandleService.fetchMetadataHandle(partnerId,
					workspace.harvestRequest.getSetSpec());
		}
		catch(HarvestException e)
		{
			// The exception can be 1 of two things, 1 the service couldn't retrieve
			// or create the handle, that is the case for RESOURCE_UNAVAILABLE_FOR_RECORD_ERROR_CODE
			// Otherwise the handle service may be down or a change was made in 
			// the HandleService helper that is breaking the code. In these cases
			// its a HarvestException since it will happen for the rest of the records too
			if(e.getErrorCode().equals(
					Config.Exceptions.RESOURCE_UNAVAILABLE_FOR_RECORD_ERROR_CODE))
			{
				handlesProcessor.addError(recordId, e.getError());
			}
			else
			{
				// Otherwise its a fatal Exception, one that will affect all records
				throw e;
			}
		}
		return metadataHandle;
	}
	
	/**
	 * Fetch the resource handle for a particular url
	 * @param recordId
	 * @param url
	 * @param handlesProcessor
	 * @return
	 * @throws HarvestException
	 */
	private String getResourceHandle(String recordId, String url, Processor handlesProcessor) throws HarvestException
	{
		String resourceHandle = null;
		try
		{
			resourceHandle = HandleService.fetchResourceHandle(url);
		}
		catch(HarvestException e)
		{
			// We handle the exception the same we we do for metadatahandles,
			// read above for an explanation of why we check the error code
			if(e.getErrorCode().equals(
					Config.Exceptions.RESOURCE_UNAVAILABLE_FOR_RECORD_ERROR_CODE))
			{
				handlesProcessor.addError(recordId, e.getError());
			}
			else
			{
				throw e;
			}
		}
		return resourceHandle;
	}
	
	/**
	 *  Method for reporting on all processors that were used during the harvest process.
	 *  These include record processors(native and target), harvest, and repository
	 * @return    Whether or not errors or warnings appeared in any of the processors
	 */
	protected int[] reportProcessorIssues() throws HarvestException {
		ProcessorReport report = new ProcessorReport(this.workspace);

		// Note we send in the stage that the processors were executed in
		// This is because the same processor could have been used in two 
		// different places ie: Record Shcema validator. This enables the log to be more
		// specific
		
		if(this.harvester!=null)
		{
			report.reportOnProcessors(
					this.harvester.getPreProcessors(), 
					null);
			report.reportOnProcessors(
					this.harvester.getHarvestFileProcessors(), 
					ProcessorReport.HARVEST_FILE_PROCESS_STAGE);
			report.reportOnProcessors(
					this.harvester.getPostProcessors(), 
					ProcessorReport.POST_HARVEST_PROCESS_STAGE);
		}
		report.reportOnRecordProcessors(
				ProcessorReport.PRE_PROCESS_STAGE, 
				this.preProcessors);
		report.reportOnRecordProcessors(
				ProcessorReport.TARGET_FORMAT_RECORD_PROCESS_STAGE, 
				this.targetFormatProcessors);
		report.reportOnRecordProcessors(
				ProcessorReport.NATIVE_FORMAT_RECORD_PROCESS_STAGE, 
				this.nativeFormatProcessors);
		
		if(this.repository!=null)
		{
			report.reportOnProcessors(this.repository.getPreProcessors(), 
					ProcessorReport.REPOSITORY_PROCESS_STAGE);
			report.reportOnProcessors(this.repository.getPostProcessors(), 
					ProcessorReport.POST_REPOSITORY_PROCESS_STAGE);
		}
		
		int[] issueCounts = {report.getErrorCount(), report.getWarningCount()};
		
		// Clean the report, ie release handles to the db
		report.clean();
		return issueCounts;
		
	}

	/**
	 *  Method for running record processors and updating their workspace data
	 *  if the record changed in the processor
	 * @param recordProcessors record processors to be executed
	 * @param inputRecordField the record workspace field to execute the
	 * 			 processor on ie(original, validated, transformed
	 * @param outputRecordField the record workspace field to write the updated record to
	 * 					if the processor updated it
	 */
	protected void runRecordProcessors(List<RecordProcessor> recordProcessors,
									String inputRecordField,
									String outputRecordField
									) 
			throws HarvestException
	{
		if(recordProcessors.size()==0)
			return;
		
		ResultsWrapper resultsWrapper = this.workspace.getRecords(inputRecordField);
		Iterator results = resultsWrapper.getResults();
		String record=null;
		String updatedRecord = null;
		if(results==null)
			return;
		while (results.hasNext())
		{
			Object[] recordData = (Object[])results.next();
			
			String recordId = ((Integer)recordData[0]).toString();
			try {
				record = new String((byte[])recordData[1], Config.ENCODING);
			} catch (UnsupportedEncodingException e) {
				throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
						"Cannot convert byte string to encoding utf-8", e);
			}
			
			
			boolean changed = false;
			updatedRecord = null;
			
			for(RecordProcessor recordProcessor:recordProcessors)
			{
				updatedRecord = recordProcessor.run(recordId, record);
				
				// If the updated record is not null that means it was changed. Mark the
				// record as should e updated
				if (updatedRecord!=null)
				{
					record = updatedRecord;
					changed = true;
				}
				HarvestRequest.checkForInterruption(
						"Ingestor.runRecordProcessors->"+outputRecordField);
			}
			if (changed)
			{
				this.workspace.updateRecord(recordId, outputRecordField, record);
			}
			
			HarvestRequest.checkForInterruption(
				"Ingestor.runRecordProcessors->"+outputRecordField);
			
			record = null;
			recordData = null;
			recordId = null;
		}
		resultsWrapper.clean();
		this.workspace.cleanupAfterIteration();
		this.markErrorRecords(recordProcessors);
	}
									
	
	/**
	 *  Method marking records as having errors and shouldn't be used any further in the
	 *  processing
	 * @param processors record processors to be checked for errors and therefore marking 
	 *          their record ids as errors
	 */
	protected void markErrorRecords(List<RecordProcessor> processors) throws HarvestException
	{
		String [] erroredRecordIds = RecordProcessor.getUniqueErroredRecordIds(processors);
		if (erroredRecordIds.length>0)
			this.workspace.markErrorRecords(erroredRecordIds);
	}
	
	/**
	 *  Method for checking the workspace for how many error records there are compared to
	 *  valid records, If this number exceeds a certain amount then throw a MAX_ERROR_THRESHOLD
	 *  Exception
	 * @exception HarvestException 
	 */
	protected void checkErrorThreshold() throws HarvestException
	{
		int recordCount = this.workspace.getRecordCount();
		int invalidRecordCount = this.workspace.getErrorRecordCount();
		
		if(invalidRecordCount > recordCount*Config.MAX_ERROR_THRESHOLD)
		{
			float ratio = (float)invalidRecordCount/recordCount;
			throw new MaxErrorThresholdException(ratio);
			
		}
	}
	
	/**
	 *  Method for setting getting the targetFormat, This method is used within JAXB
	 *  as the attribute for which to be able to initialize. Note the 
	 *  @XmlElement(name = "target-format")
	 * @return targetFormat target format that is specified by the ingestor
	 */
	
	@XmlElement(name = "target-format")
	public String getTargetFormat() {
		return targetFormat;
	}

	public void setTargetFormat(String targetFormat) {
		this.targetFormat = targetFormat;
	}

	public void setHarvestRequest(HarvestRequest harvestRequest) {
		this.harvestRequest = harvestRequest;
	}
	
	@XmlElement(name = "populate-resource-handles")
	public boolean isPopulateResourceHandles() {
		return populateResourceHandles;
	}

	public void setPopulateResourceHandles(boolean populateResourceHandles) {
		this.populateResourceHandles = populateResourceHandles;
	}

	@XmlElement(name = "harvest-data-format")
	public String getHarvestDataFormat() {
		return this.harvestDataFormat;
	}

	public void setHarvestDataFormat(String harvestDataFormat) {
		this.harvestDataFormat = harvestDataFormat;
	}
	
	public void setPostHarvestProcessors(List<Processor> portHarvestProcessors) {
		this.postHarvestProcessors = portHarvestProcessors;
	}
	
	/**
	 *  Cleans the Ingestor and all components that also need to be cleaned for the harvest.
	 *  These include the harvester, workspace, and repository
	 * @throws HarvestException 
	 */
	public void clean() throws HarvestException
	{
		if(this.harvester!=null)
			this.harvester.clean();
		if(this.repository!=null)
			this.repository.clean();
		if(this.workspace!=null)
			this.workspace.clean();
	}


	//=========== Debugging/logging ===============================================

	private boolean debug = true;

	/**
	 *  Return a string for the current time and date, sutiable for display in log files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	public static String getDateStamp() {
		return
				new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final void prtlnErr(String s) {
		System.err.println(getDateStamp() + " Ingestor Error: " + s);
	}



	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final void prtln(String s) {
		if (debug)
			System.out.println(getDateStamp() + " Ingestor: " + s);
	}


	/**
	 *  Sets the debug attribute.
	 *
	 * @param  db  The new debug value
	 */
	public final void setDebug(boolean db) {
		debug = db;
	}



}
