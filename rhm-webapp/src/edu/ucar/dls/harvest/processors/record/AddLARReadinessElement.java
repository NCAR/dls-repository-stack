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

import javax.xml.bind.annotation.XmlRootElement;

import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.workspaces.Workspace;
import edu.ucar.dls.harvestmanager.HarvestCollectionDAO;
/**
 Processor that adds LarRediness Element to the document. It gets this information from the
 collection document inside the DCS. Under the LAR attribute. If this attribute isn't found
 it uses Not Ready. Any previous lar rediness element is removed from the document
 */

@XmlRootElement(name="AddLARReadinessElement")
public class AddLARReadinessElement extends RecordProcessor{
	private Element larReadinessElement = null;

	private static final String LAR_READINESS_XML = 
		"<lar:readiness xsi:type='lar:Ready' xmlns:lar='http://ns.nsdl.org/schemas/dc/lar' " +
			"xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>%s</lar:readiness>";
	
	
	/**
	 * initialize method that is being used to make sure that the the processor
	 * was setup correctly. We want to sure that the collectionDAO in the harvest manager
	 * was setup correctly and that the syntax of the LAR_READINESS_XML is correct
	 */
	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);
		String collectionId = this.workspace.harvestRequest.getCollectioId();
		HarvestCollectionDAO collectionDAO = Config.Reporting.harvestManager.getCollectionDAO(
				collectionId);
		
		
		if(collectionDAO==null)
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					String.format("Collection DAO %s could not be found as a valid collection in the DCS.",
							collectionId), "AddLAReadiness->initialize()");
		String larReadinessString = collectionDAO.getLarReadiness();
		if(larReadinessString==null)
			larReadinessString = "Not ready";
		
		
		try {
			Element newElement = (Element)Dom4jUtils.getXmlDocument(
					String.format(LAR_READINESS_XML, larReadinessString)).getRootElement().detach();
			this.larReadinessElement = newElement;
		} catch (DocumentException e) {
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					String.format("Couldn't create a valid LAR Readiness tag for collection %s.", 
							collectionId), "AddLAReadiness->initialize()");
		}
	}
	
	/**
	 * Run method that removes any previous LAR Readiness elements and then adds the new one 
	 */
	public String run(String documentId, String record) 
		throws HarvestException {
		Document document;
		try {
			document = Dom4jUtils.getXmlDocument(record);
		
			Element rootElement =  document.getRootElement();
			
			List<Element> existingReadinessElements = rootElement.selectNodes("*[name()='lar:readiness']");
			for (Element readinessElement: existingReadinessElements)
				readinessElement.detach();
			
			// we clone it since this run method will be called multiple times
			rootElement.elements().add(this.larReadinessElement.clone());
			
			return rootElement.asXML();
		} catch (DocumentException e) {
			this.addError(documentId, String.format("Element could not be built for LAR Readiness " +
					"for record %s. Error %s.", documentId,
					  e.getMessage()));
		}
		return null;
	}
}
