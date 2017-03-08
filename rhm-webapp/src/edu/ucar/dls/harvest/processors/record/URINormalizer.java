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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.tools.URIutil;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 * Record Processor that deals with URI's within a record. There are options that
 * can be selected to make it behave differently. But in essence it takes in a record
 * and xpaths. Finds any elements that match those xpaths and tries to normalize
 * the url using a URI tool that was brought in from a previous version. After
 * normalization it re-writes the normalized url back into the record. If 
 * workspaceURI is set to true it also adds this url to the workspace for use within
 * other processors and parts of the program. Since this idea of workspace uri depends
 * only on one url being there it will detach any other uri's from the record. So its
 * important to only send an xpath in that will grab at most one URI if workspace-uri 
 * is set. URI required can also be set to true to enforce the rule that a uri has 
 * to be present other otherwise mark the record as an error.
 *
 */

@XmlRootElement(name="URINormalizer")
public class URINormalizer extends RecordProcessor{
	protected String URI_NOT_FOUND_MSG = "Could not find valid uri using xpaths %s.";
	protected String ERROR_PARSING_DOC_MSG = "Error parsing doc trying to find uri - " +
						"xpaths:%s, Exception %s.";
	protected String  uriXPathsDisplayValue = null;
	
	protected String[] uriXPaths= null;
	protected boolean workspaceURI = false;
	protected boolean URIRequired = false;

	/**
	 * Initializer of the Processor which enforces the rule that xpaths must be
	 * set for this processor
	 */
	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);
		if(this.uriXPaths==null)
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					"uriXPaths is an attribute that must be defined for all ingestors.",
					"URIValidator.contructor()");
		this.uriXPathsDisplayValue = StringUtils.join(uriXPaths, ", ");
	}
	
	/**
	 * Using the xpaths try to find uri's that match and normalize them. Along with
	 * settings the workspace url to it if applicable.
	 * @param recordId record id as a string
	 * @param record record xml as a string
	 * @param workspace the workspace that is being used for harvesting
	 */
	public String run(String documentId, String record) throws HarvestException {
		ArrayList<String> urlHandled = new ArrayList<String>();
		try {
			String validURI = null;
			Document document = Dom4jUtils.getXmlDocument(record);
			Element rootElement = document.getRootElement();
			String possibleBackupURI = null;
			// Loop through the many xpaths
			for(String uriXPath: this.uriXPaths)
			{
				List<Element> matchingNodes = rootElement.selectNodes(uriXPath);
				
				// Possible uri is just infering possible uri that must be dealt with
				for( Element possibleUriElement: matchingNodes)
				{
					// If we already handled the url in a prior xpath continue on
					if(urlHandled.contains(possibleUriElement.getUniquePath()))
							continue;
					urlHandled.add(possibleUriElement.getUniquePath());
					
					// Use the tool that we borrowed from the previous version to 
					// normalize the url
					String scrubbedUrl = URIutil.scrubURL(possibleUriElement.getText());
					// If its not a valid uri then it will be null
					if(scrubbedUrl!=null)
					{
						// if it was a url set it back into the document
						possibleUriElement.setText(scrubbedUrl);
						
						// Note this is where the code picks the first url. If more are 
						// there and workspaceURI is set to true they will be removed
						if(validURI==null)
							validURI = scrubbedUrl;
						else if(this.workspaceURI)
						{
							// There is a rule that a metadata can only have one identifier URL
							// Otherwise the dds is confused remove the URI and create a warning
							possibleUriElement.detach();
							this.addWarning(documentId, "There is more then one uri identifiers " +
									"found for this record, which is invalid for the DDS. " +
									"Removing all but the first one.");
						
						}
					}
					else if(this.URIRequired || this.workspaceURI)
					{
						// note the normalizer sometimes will see the url not as a valid url
						// ie if the domain has _ in it. Per old ingest code use it as is. 
						possibleBackupURI = possibleUriElement.getText();
					
					}
					HarvestRequest.checkForInterruption(
					"URIValidator.run()->looping through nodes");
				}
				
					
				HarvestRequest.checkForInterruption(
					"URIValidator.run()->looping through xpaths");
			}
			
			if(validURI==null && possibleBackupURI!=null)
				validURI = possibleBackupURI;
			
			// if there is a valid URI and we want to use it for the workspace.
			// insert it
			if(this.workspaceURI && validURI!=null && workspace!=null)
				workspace.insertURI(documentId, validURI);
			
			// If a url was required but wasn't found mark record as error
			if(validURI==null && this.URIRequired)
			{
				this.addError(documentId, String.format(URI_NOT_FOUND_MSG, 
						this.uriXPathsDisplayValue));
			}
			else
			{
				String output = rootElement.asXML();
				if(!output.equals(record))
					return output;
				else
					return null;
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			this.addError(documentId, String.format(ERROR_PARSING_DOC_MSG, 
					this.uriXPathsDisplayValue, e.getMessage()));
		} 
		return null;
	}
	
	@XmlElement(name = "xpath")
	@XmlElementWrapper(name="xpaths")
	public String[] getUriXPaths() {
		return uriXPaths;
	}
	/**
	 * Method that must be used to set the xpaths
	 * @param uriXPaths
	 */
	public void setUriXPaths(String[] uriXPaths) {
		this.uriXPaths = uriXPaths;
	}
	
	@XmlElement(name = "workspace-uri")
	public boolean isWorkspaceURI() {
		return workspaceURI;
	}
	
	/**
	 * Method that can be used to change the default of workspaceUri to true
	 * @param uriXPaths
	 */
	public void setWorkspaceURI(boolean workspaceURI) {
		this.workspaceURI = workspaceURI;
	}
	
	@XmlElement(name = "uri-required")
	public boolean isURIRequired() {
		return URIRequired;
	}
	
	/**
	 * Method that can be used to change the default of URIRequired to true
	 * @param uriXPaths
	 */
	public void setURIRequired(boolean uRIRequired) {
		URIRequired = uRIRequired;
	}

}
