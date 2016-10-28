package edu.ucar.dls.harvest.reporting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.processors.Processor;
import edu.ucar.dls.harvest.processors.ProcessorIssue;
import edu.ucar.dls.harvest.processors.record.RecordProcessor;
import edu.ucar.dls.harvest.workspaces.FileHelper;
import edu.ucar.dls.harvest.workspaces.ResultsWrapper;
import edu.ucar.dls.harvest.workspaces.Workspace;

public class ProcessorReport extends HarvestReport{
	public static final String HARVEST_FILE_PROCESS_STAGE= "Harvesting files";
	public static final String POST_HARVEST_PROCESS_STAGE= "Post Harvest";
	public static final String REPOSITORY_PROCESS_STAGE= "Populating Repository";
	public static final String POST_REPOSITORY_PROCESS_STAGE= "Post PopulateRepository";
	public static final String PRE_PROCESS_STAGE = "Pre Processing";
	public static final String TARGET_FORMAT_RECORD_PROCESS_STAGE= "Target Format Processing";
	public static final String NATIVE_FORMAT_RECORD_PROCESS_STAGE= "Native FormatProcessing";
	
	private static final String RECORD_ISSUE_ERROR_MSG_TEMPLATE = 
		"%s during %s\n" +
		"Stats: %d out of %d records were found to have this issue%s.\n" +
		"%s\n" +
		"Issue happened during the execution of %s.";
	
	private static final String ERROR_RECORD_ISSUE_EXTRA_STATS = 
		 " and not included in the harvest";
	
	private static final String EXAMPLE_MSG_TEMPLATE = 
		"To view these example records go to "+
		"the harvested records tab and then to folder /%s.";
	
	private static final String HARVEST_FILE_ISSUE_MSG_TEMPLATE = 
		"%s during %s\n " +
		"Example File(s): %s. To view these examples files go " +
		"to the harvested records tab and then to folder /harvested_files.\n"+
		"Issue happened during the execution of %s.";
	
	private static final String BASIC_ISSUE_MSG_TEMPLATE = 
		"%s during %s \n" +
		"Issue happened during the execution of %s.";
	
	private int warningCount = 0;
	private int errorCount = 0;
	private int infoCount = 0;
	private int recordCount = -1;
	protected Workspace workspace = null;
	public ProcessorReport(Workspace workspace) throws HarvestException
	{
		super(workspace);
		this.workspace = workspace;
		this.recordCount = workspace.getRecordCount();
	}
	
	
	public void reportOnProcessors(List<? extends Processor> processors, 
							String processingStage) throws HarvestException
	{
		String msgTemplate;
		boolean showExamples;
		if(processingStage==HARVEST_FILE_PROCESS_STAGE)
		{
			msgTemplate = HARVEST_FILE_ISSUE_MSG_TEMPLATE;
			showExamples = true;
		}
		else 
		{
			msgTemplate = BASIC_ISSUE_MSG_TEMPLATE;
			showExamples = false;
		}
		for(Processor processor: processors)
		{
			this.reportOnProcessorIssues(processor, processor.getErrors(), msgTemplate, processingStage,
					showExamples, ProcessorIssue.ERROR_TYPE);
			this.reportOnProcessorIssues(processor, processor.getWarnings(), msgTemplate, processingStage,
					showExamples, ProcessorIssue.WARNING_TYPE);
			this.reportOnProcessorIssues(processor, processor.getInfos(), msgTemplate, processingStage,
					showExamples, ProcessorIssue.INFO_TYPE);
			
			HarvestRequest.checkForInterruption(
				"ProcessorReport.reportOnProcessors()");
		}
	}
	
	private void reportOnProcessorIssues(Processor processor, List<ProcessorIssue> issues, 
			String msgTemplate, String processingStage, 
			boolean showExamples, String issueType) throws HarvestException
	{
		if(issues.size()==0)
			return;
		Map<String, List<String>> issuesByMsgMap = 
			this.groupIssuesByMessage(issues);
		Iterator<Map.Entry<String,List<String>>> issuesIterator = 
			issuesByMsgMap.entrySet().iterator();
		
		while (issuesIterator.hasNext()) {
			
			Map.Entry<String,List<String>> pairs = issuesIterator.next();
			String msg;
			if(!issueType.equals(ProcessorIssue.INFO_TYPE) && showExamples)
			{
				String[] exampleSet = this.getExampleSizeIds(pairs.getValue());
				msg = String.format(msgTemplate, 
						(String)pairs.getKey(), processingStage,  
						StringUtils.join(exampleSet, ", "), processor.getName());
			}
			else if(!issueType.equals(ProcessorIssue.INFO_TYPE) && processingStage!=null )
			{
				 msg = String.format(msgTemplate, 
						 (String)pairs.getKey(), processingStage, processor.getName());
			}
			else
			{
				msg = (String)pairs.getKey();
			}
			
			if(issueType.equals(ProcessorIssue.ERROR_TYPE))
			{
				this.errorCount++;
				this.logger.logError(msg);
			}
			else if(issueType.equals(ProcessorIssue.WARNING_TYPE))
			{
				this.warningCount++;
				this.logger.logWarning(msg);
			}
			else
			{
				this.infoCount++;
				this.logger.logInfo(msg);
			}
			HarvestRequest.checkForInterruption(
					"ProcessorReport.reportOnProcessorIssues()");
		}
	}
	
	private String[] getExampleSizeIds(List<String> elementIds )
	{
		int numberOfExamples = Math.min(Config.Reporting.maxNumberOfExamples, 
				elementIds.size());
		String [] exampleElementIds = new String[numberOfExamples];
		for(int i=0; i<numberOfExamples;i++)
			exampleElementIds[i] = elementIds.get(i);
		return exampleElementIds;
	}

	public void reportOnRecordProcessors(String processingStage, 
			List <RecordProcessor> recordProcessors) throws HarvestException
	{
		for(RecordProcessor recordProcessor: recordProcessors)
		{
			if(recordProcessor.getErrorCount()>0)
				reportOnRecordProcessorIssues(recordProcessor, 
						recordProcessor.getErrors(),
						processingStage, ProcessorIssue.ERROR_TYPE);
			if(recordProcessor.getWarningCount()>0)
				reportOnRecordProcessorIssues(recordProcessor, 
						recordProcessor.getWarnings(),
								processingStage, ProcessorIssue.WARNING_TYPE);
			if(recordProcessor.getInfoCount()>0)
				reportOnRecordProcessorIssues(recordProcessor, 
						recordProcessor.getWarnings(),
								processingStage, ProcessorIssue.INFO_TYPE);
			HarvestRequest.checkForInterruption(
				"ProcessorReport.reportOnRecordProcessors()");
		}
	}
	
	private void reportOnRecordProcessorIssues(RecordProcessor processor, 
			List<ProcessorIssue> processorIssues,
			String recordProcessingStage, String issueType
			) throws HarvestException
	{
		Map<String, List<String>> issuesByMsgMap = 
				this.groupIssuesByMessage(processorIssues);
						
		Iterator<Map.Entry<String,List<String>>> issuesIterator = 
					issuesByMsgMap.entrySet().iterator();
	    
		int issueCount = -1;
		while (issuesIterator.hasNext()) {
			if(issueType.equals(ProcessorIssue.ERROR_TYPE))
			{
	    		this.errorCount++;
	    		issueCount = this.errorCount;
	    	}
			else if(issueType.equals(ProcessorIssue.WARNING_TYPE))
	    	{
	    		this.warningCount++;
	    		issueCount = this.warningCount;
	    	}
			else
			{
				this.infoCount++;
	    		issueCount = this.infoCount;
			}
	        Map.Entry<String,List<String>> pairs = issuesIterator.next();
	        
	        List<String> recordIds = pairs.getValue();
	        
	        String examplesMessage;
	        String examplesFolder = String.format("%s%d", issueType, issueCount);
			File workingDirectory = this.workspace.getWorkingFileDirectory();
			File issueFolder = new File(workingDirectory, examplesFolder);
			issueFolder.mkdirs();
			
			try {
				examplesMessage = this.createSampleSet(recordIds, issueFolder, 
						recordProcessingStage, issueCount);
			} catch (Exception e) {
				e.printStackTrace();
				examplesMessage = "Could not build example files because " + (e.getCause());
			}
	        
			
			String extraStats = "";
			if(issueType.equals(ProcessorIssue.ERROR_TYPE))
				extraStats = ERROR_RECORD_ISSUE_EXTRA_STATS;
			
			String msgToLog = String.format(RECORD_ISSUE_ERROR_MSG_TEMPLATE, 
	        		pairs.getKey(), recordProcessingStage,  
	        			recordIds.size(), this.recordCount, extraStats, 
	        			examplesMessage, processor.getName());
			
			// We need a list of the partner ids for the issues.txt file
			ResultsWrapper resultsWrapper = this.workspace.getPartnerIdsByRecordIds(
					(String[]) recordIds.toArray(
							new String[recordIds.size()]));
			
			Iterator results = resultsWrapper.getResults();
			List<String> partnerIds = new ArrayList<String>();
			while (results.hasNext())
			{
				Object[] recordData = (Object[])results.next();
				partnerIds.add((String)recordData[0]);
			}
			
	        try {
				FileUtils.writeStringToFile(new File(issueFolder, "issue.txt"), 
						String.format("%s\n%s\n%s", msgToLog, 
								"Full list of Partner ids.", 
								StringUtils.join(partnerIds,"\n" )));
			} catch (IOException e) {
				throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
						"Unable to write issue.txt to issues folder "+ issueFolder.getAbsolutePath(), e);
			}
	        
	        if(issueType.equals(ProcessorIssue.ERROR_TYPE))
	    	{
	        	this.logger.logRecordError(msgToLog);
	    	}
	        else if(issueType.equals(ProcessorIssue.WARNING_TYPE))
	        {
	        	this.logger.logRecordWarning(msgToLog);
	        }
	        else
			{
	        	this.logger.logRecordInfo(msgToLog);
			}
	        
	        HarvestRequest.checkForInterruption(
				"ProcessorReport.reportOnRecordProcessorIssues()");   			   
	    }
	}
	
	
	
	
	private String createSampleSet(List<String> recordIds, File issueFolder, 
			String recordProcessingStage, int issueCount) throws DocumentException, Exception
	{

	    for(String recordId: recordIds )
	    {
	    	ResultsWrapper resultsWrapper = this.workspace.getRecordData(recordId);
	    	Iterator results = resultsWrapper.getResults();
	    	Object[] recordData = (Object[])results.next();
	    	
	    	String partnerId = (String)recordData[0];
	    	String originalRecord = new String((byte[])recordData[1], Config.ENCODING);
	    	
	    	this.createExampleFile(issueFolder, partnerId, originalRecord);
	    	
	    	resultsWrapper.clean();
	    	
	    	HarvestRequest.checkForInterruption(
				"ProcessorReport.createSampleSet()"); 
	    }
	    
	    return String.format(EXAMPLE_MSG_TEMPLATE, 
	    					 issueFolder.getName());

	}
	
	private void createExampleFile(File issueFolder, String partnerId, 
			String content) 
				throws DocumentException, Exception
	{
		String harvestDataFormat = this.workspace.getHarvestDataFormat();
		String fileExtension = null;
		
		
		if(harvestDataFormat.equals(Config.DataFormats.XML))
			fileExtension = "xml";
		else if(harvestDataFormat.equals(Config.DataFormats.JSON))
			fileExtension = "txt"; // note you may change this to .json but it was asked to be txt
		else
			fileExtension = "txt";
		
		File testExampleFile = new File(issueFolder, 
				String.format("%s.%s", partnerId, fileExtension));
		
		String legalFileName = FileHelper.findAvailableFileName(
				testExampleFile.getAbsolutePath());

		FileUtils.writeStringToFile(new File(legalFileName), content, Config.ENCODING);
		
	}

	private Map<String, List<String>> groupIssuesByMessage(
				List<ProcessorIssue> processorIssues) throws HarvestException
	{
		HashMap<String, List<String>> issuesByMsgMap = 
			new HashMap<String, List<String>>();

		for(ProcessorIssue processorIssue:processorIssues)
		{
			String issueMsg = processorIssue.getMessage();
			String elementId = processorIssue.getElementId();
			
			if(issuesByMsgMap.containsKey(issueMsg))
				issuesByMsgMap.get(issueMsg).add(elementId);
			else
			{
				List<String> elementIds = new ArrayList<String>();
				elementIds.add(elementId);
				issuesByMsgMap.put(issueMsg, elementIds);
			}
			HarvestRequest.checkForInterruption(
				"ProcessorReport.groupIssuesByMessage()"); 
		}
		return issuesByMsgMap;
	}
	public int getWarningCount() {
		return warningCount;
	}
	public int getErrorCount() {
		return errorCount;
	}
}
