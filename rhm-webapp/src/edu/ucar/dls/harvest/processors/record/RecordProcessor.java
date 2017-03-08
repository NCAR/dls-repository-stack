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
package edu.ucar.dls.harvest.processors.record;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.processors.Processor;
import edu.ucar.dls.harvest.processors.ProcessorIssue;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 * Abstract Processor class that must extended by any record processors that
 * need to be ran. Run is the only method that is abstract and must be implemented
 * in subclasses. This is a subclass of Processor and exists because for record 
 * processors we need to send in recordId, record and workspace instead of just
 * workspace
 */
public class RecordProcessor extends Processor{
	protected List<String> includedCollections = null;
	protected List<String> excludedCollections = null;
	
	/**
	 * Abstract method that must implemented by subclasses
	 * @param recordId record id as a string, note this recordId is not partnerid but its id in 
	 *                 workspace
	 * @param record record xml as a string
	 * @param workspace the workspace that is being used for harvesting
	 */
	
	public String run(String recordId, String record) throws HarvestException
	{
		return null;
	}
	
	/**
	 * static method that goes through processors finding all the errors and then creating
	 * @param Collection<RecordProcessor> A collection of processors that must be iterated over
	 *                                    finding the unique list of record ids that have errors
	 */
	public static String[] getUniqueErroredRecordIds(Collection<RecordProcessor> allProcessors) {
		HashSet<String> uniqueRecordIdHashSet = new HashSet<String>();
		for(Processor processor: allProcessors)
		{
			for (ProcessorIssue recordIssue: processor.getErrors())
			{
				uniqueRecordIdHashSet.add(recordIssue.getElementId());
			}
		}
		return (String[]) uniqueRecordIdHashSet.toArray(
				new String[uniqueRecordIdHashSet.size()]);
		
		}
	
	public static void initializeRecordProcessors(
			List<RecordProcessor> processors, Workspace workspace) throws HarvestException {
		
		String setSpec = workspace.harvestRequest.getSetSpec();
		Iterator<RecordProcessor> iterator = processors.iterator();
		while(iterator.hasNext())
		{
			RecordProcessor recordProcessor = iterator.next();
			List<String> includedCollections = recordProcessor.getIncludedCollections();
			List<String> excludedCollections = recordProcessor.getExcludedCollections();
			
			if(includedCollections!=null && excludedCollections!=null)
				throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
						"included-collections and excluded-collections cannot both " +
						"be specified in config file. Remove one of them.", 
						"RecordProcessor.initializeProcessors()");
			else if(includedCollections!=null)
			{
				if(!includedCollections.contains(setSpec))
					iterator.remove();
			}
			else if(excludedCollections!=null)
			{
				if(excludedCollections.contains(setSpec))
					iterator.remove();
			}
		}
		Processor.initializeProcessors(processors, workspace);
	}
	
	@XmlElement(name = "collection")
	@XmlElementWrapper(name="included-collections")
	public List<String> getIncludedCollections() {
		return includedCollections;
	}

	public void setIncludedCollections(List<String> includedCollections) {
		this.includedCollections = includedCollections;
	}
	
	@XmlElement(name = "collection")
	@XmlElementWrapper(name="excluded-collections")
	public List<String> getExcludedCollections() {
		return excludedCollections;
	}

	public void setExcludedCollections(List<String> excludedCollections) {
		this.excludedCollections = excludedCollections;
	}
}
