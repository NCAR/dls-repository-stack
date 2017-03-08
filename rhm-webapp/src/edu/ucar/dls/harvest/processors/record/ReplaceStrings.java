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

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.processors.record.containers.Replacement;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
  Custom processor that enables one to replace specified strings within a record
 */

@XmlRootElement(name="ReplaceStrings")
public class ReplaceStrings extends RecordProcessor{
	private List<Replacement> replacements = null;
	
	/**
	 * initialize method that is being used to make sure that the the proecessor
	 * was setup correctly. We want to make sure that if an error is going
	 * to happen we want it to happen here. not in run
	 */
	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);
		if(this.replacements.size()==0)
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					"Replacements wrapper with Replacement objects is requried for. "+
					"ReplaceStrings processor",
					"ReplaceStrings.contructor()");
		
		for(Replacement replacement: this.replacements)
		{
			if(replacement.getTarget()==null)
				throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
						"Specifying a Replacement inside ReplaceStrings processor requires " +
						"you to at least have a target set.", "ReplaceStrings.contructor()");
		}
	}
	
	/**
	 * Method called by the ingest processor that goes through all the replacements
	 * that are given and replaces the record with the new string
	 * @param recordId record id as a string
	 * @param record record xml as a string
	 * @param workspace the workspace that is being used for harvesting
	 */
	public String run(String documentId, String record) 
		throws HarvestException {
		
		String formatedRecord = new String(record);
		
		for(Replacement replacement : this.replacements)
		{
			formatedRecord = formatedRecord.replaceAll(replacement.getTarget(), 
						replacement.getReplacement());
		}
		// Only if it changed return it otherwise return null, so we don't waste
		// a update to the record
		if (!formatedRecord.equals(record))
		{	
			return formatedRecord;
		}
		
		return null;
	}
	
	/**
	 * Wrapper attribute that is used by jaxb to set and get the replacements via
	 * the config file
	 * @return
	 */
	@XmlElementWrapper(name="replacements")
	@XmlElementRef
	public List<Replacement> getReplacements() {
		return replacements;
	}
	
	public void setReplacements(List<Replacement> replacements) {
		this.replacements = replacements;
	}
}
