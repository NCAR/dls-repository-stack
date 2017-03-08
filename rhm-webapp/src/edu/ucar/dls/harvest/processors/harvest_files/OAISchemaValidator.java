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
package edu.ucar.dls.harvest.processors.harvest_files;
import java.io.File;
import java.util.List;

import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.tools.XMLDocumentValidator;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 *  Class that implements a way to schema validate the OAI files.
 */

public class OAISchemaValidator extends HarvestFileProcessor {
	
	private String VALIDATE_ERROR_MSG = "During OAI Schema Validation for file %s: %s ";
	
	private XMLDocumentValidator xmlDocumentValidator = null;

	/**
	 * Initializes the xml validator. Very important we only want to initialize this once
	 * not for every record. Doing this enables us to cache the schemas so once it runs
	 * on the first record it has all the schemas for the other records. This makes this 
	 * process run way faster
	 * @throws HarvestException 
	 */
	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);
		this.xmlDocumentValidator = new XMLDocumentValidator();
	}
	
	/**
	 * Validates the oai schema of the file. To make the error messages make sense
	 * this method does not validate the metadata. We detach that and the about segment
	 * then validate the schema. Metadata schema is validated in a different processor.
	 */
	public void run(File file) throws HarvestException {
		String fileName = file.getName();
		try {
			Document document = Dom4jUtils.getXmlDocument(file, Config.ENCODING);
			
			// detach all record elements and about segments
			List <Element>recordNodes = document.selectNodes(
					"/*[local-name()='OAI-PMH']/*[local-name()='ListRecords']/*[local-name()='record']");
			for (Element record: recordNodes)
			{				
				Element metaDataElement = (Element)((Element)record.selectSingleNode(
				"*[local-name()='metadata']"));
				
				// We do not want to validate inner schemas for this one, so detach the metadata
				if (metaDataElement!=null)
					metaDataElement.detach();
				
				// We also don't want to validate about in schema
				Element aboutDataElement = (Element)((Element)record.selectSingleNode(
				"*[local-name()='about']"));
				
				// We do not want to validate inner schemas for this one, so detach the metadata
				if (aboutDataElement!=null)
					aboutDataElement.detach();
				
				HarvestRequest.checkForInterruption(
					"OAISchemaValidator.run()");
			}
			int resultValue = xmlDocumentValidator.validateString(document.asXML(), true, true);
			
			if(resultValue==XMLDocumentValidator.NOT_VALID_RESULT || 
					resultValue==XMLDocumentValidator.NOT_WELL_FORMED_RESULT  )
			{
				// Not following OAI schema is an automatic failure throw an Exception

				String errorMsg = xmlDocumentValidator.outputBuffer.toString();
				throw new HarvestException(
						Config.Exceptions.OAI_RESPONSE_ERROR_CODE,
						String.format(VALIDATE_ERROR_MSG, fileName, errorMsg), 
						"OAISchemaValidator.run()");
			}
			else if(resultValue==XMLDocumentValidator.VALID_W_WARNINGS_RESULT)
			{
				// if its a warning just add a warning to the processor
				this.addWarning(fileName, xmlDocumentValidator.outputBuffer.toString());
			}
			// Else the result is XMLDocumentValidator.VALID_RESULT, so its valid
		} 
		catch (HarvestException e)
		{
			throw e;
		}
		catch (Exception e) {
			throw new HarvestException(
					Config.Exceptions.OAI_RESPONSE_ERROR_CODE,
					String.format(VALIDATE_ERROR_MSG, fileName, ""), 
					e);
		} 
	}
}
