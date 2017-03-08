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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.*;

import org.dlese.dpc.util.TimedURLConnection;
import org.dlese.dpc.xml.Dom4jUtils;
import org.dlese.dpc.xml.XPathUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.workspaces.Workspace;


/**
  Processor that transforms vocab within a document to standardized values. It does this
  by using a group file. That is sent into the processor. This code was directly taken out
  from the indexer since its worked so long. An example of the group file can be seen at
  http://ns.nsdl.org/ncs/ddsws/1-1/groupsNormal/vocab_selections.xml
 */
@XmlRootElement(name="TransformViaGroupFiles")
public class TransformViaGroupFiles extends RecordProcessor{
	private String groupFileURIString = null;
	
	private boolean writeComments = false;
	
	 private Map xpathsToModifyKeepExisting = null;
	 private Map groupsFileDoms = null;
	 private Map fromXpathsLists = null; 
	 private List xpathsToModify = null;
	 
	 private HashMap<String, List<HashMap<String,String>>> transformValueMap = null;
	/**
	 * initialize the processor by making sure that a group file URI was 
	 * sent in and that it is of format that we need.
	 */
	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);

		if(this.groupFileURIString==null)
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					"group-files-url is a required attribute for TransformViaGroupFiles processor",
					"TransformViaGroupFiles.contructor()");
		try {
			
			Document document = Dom4jUtils.getXmlDocument(
					Dom4jUtils.localizeXml(TimedURLConnection.importURL(
							this.groupFileURIString, 5000)));
			List<Node> groupsFileNodes = document.selectNodes("/groupsFiles/groupsFile");

			this.xpathsToModify = new ArrayList();
			this.xpathsToModifyKeepExisting = new TreeMap();
			this.groupsFileDoms = new TreeMap();
			this.fromXpathsLists = new TreeMap();
			
			for (Node groupsFileNode : groupsFileNodes) {
				String toXpath = groupsFileNode.valueOf("toXpath").trim();
				String keepExistingValues = groupsFileNode.valueOf("keepExistingValues").trim();
				if (keepExistingValues != null && keepExistingValues.equalsIgnoreCase("true"))
					this.xpathsToModifyKeepExisting.put(toXpath, new Object());
				String url = groupsFileNode.valueOf("url").trim();
				List fromXpaths = groupsFileNode.selectNodes("fromXpaths/fromXpath");
				if (toXpath.length() > 0 && url.length() > 0 && fromXpaths.size() > 0) {
					Document groupsDom = null;
					try {
						groupsDom =  Dom4jUtils.getXmlDocument(
									Dom4jUtils.localizeXml(TimedURLConnection.importURL(
											url, 5000)));
					
					} catch (Throwable t) {

						continue;
					}
					List fromXpathsStrings = new ArrayList();
					for (int j = 0; j < fromXpaths.size(); j++)
						fromXpathsStrings.add(((Node) fromXpaths.get(j)).getText().trim());
					this.xpathsToModify.add(toXpath);
					this.groupsFileDoms.put(toXpath, groupsDom);
					this.fromXpathsLists.put(toXpath, fromXpathsStrings);
				}
			}
		}
		catch(Exception e)
		{
			
		}
		this.transformValueMap = new HashMap<String, List<HashMap<String,String>>>();
	}
	
	/**
	 * Transform the record in question with the vocab that was defined
	 * in groupFileURIString
	 * @param recordId record id as a string
	 * @param record record xml as a string
	 * @param workspace the workspace that is being used for harvesting
	 */
	public String run(String recordId, String record) 
									throws HarvestException
	{
		

		Document originalDoc;
		try {
			originalDoc = Dom4jUtils.getXmlDocument(record);
		
			Document newDoc = (Document) originalDoc.clone();
			for (int i = 0; i < this.xpathsToModify.size(); i++) {
				Map foundValues = new HashMap();
				// Track values that have been created to avoid duplicating
				String xPathToModify = (String) this.xpathsToModify.get(i);
				boolean keepExistingValues = this.xpathsToModifyKeepExisting.containsKey(xPathToModify);
	
				// First remove all the existing elements from the new Document corresponding to this XPath, if indicated:
				if (!keepExistingValues) {
					List existingElms = newDoc.selectNodes(xPathToModify);
					for (int ii = 0; ii < existingElms.size(); ii++)
						((Node) existingElms.get(ii)).detach();
				}
	
				Document groupsDom = (Document) this.groupsFileDoms.get(xPathToModify);
				List fromXpathsList = (List) this.fromXpathsLists.get(xPathToModify);
	
				// Only process if we've got something to process...
				if (groupsDom != null && fromXpathsList != null || fromXpathsList.size() > 0) {
	
					// For each xPath to pull from, grab it's value and translate to the new value:
					for (int j = 0; j < fromXpathsList.size(); j++) {
						String fromXpath = (String) fromXpathsList.get(j);
						//prtln("fromXpath: " + fromXpath);
						List existingDCElms = originalDoc.selectNodes(fromXpath);
						for (int k = 0; k < existingDCElms.size(); k++) {
							String existingValue = ((Node) existingDCElms.get(k)).getText();
							//prtln("existingValue: " + existingValue);
							//prtln("groupsDom:\n" + groupsDom.asXML());
	
							
							List<HashMap<String, String>>  tranformValues = null;
							if(!transformValueMap.containsKey(existingValue))
							{
								
								tranformValues = new ArrayList<HashMap<String, String>>();
								// Fetch the outline nodes that contain the existing value from the record:
								List outlineNodes = groupsDom.selectNodes("/opml/body//outline[@vocab='" + existingValue + "']");
								//prtln("numOutlineNodes for existingValue: " + outlineNodes.size());
								for (int m = 0; m < outlineNodes.size(); m++) {
		
									Node outlineNode = (Node) outlineNodes.get(m);
									//prtln("outlineNode vocab: " + outlineNode.valueOf("@vocab").trim());
		
									// Fetch the nodes that define the new value for the term:
									List newValueOutlineNodes = outlineNode.selectNodes(".[@type='group']|..[@type='group']|../..[@type='group']");
									// Go one and two levels up, for nested vocabs like EdLevel
									//prtln("numNewValueOutlineNodes: " + newValueOutlineNodes.size());
									for (int n = 0; n < newValueOutlineNodes.size(); n++) {
										Node newValueOutlineNode = (Node) newValueOutlineNodes.get(n);
										String newValue = newValueOutlineNode.valueOf("@vocab").trim();
										String xsiType = newValueOutlineNode.valueOf("@attribution").trim();
										HashMap values = new HashMap<String, String>();
										values.put("xsiType", xsiType);
										values.put("newValue", newValue);
										tranformValues.add(values);
									}
								}
								transformValueMap.put(existingValue, tranformValues);
							}	
							else
							{
								tranformValues = transformValueMap.get(existingValue);
								
							}	
							
							for(HashMap<String,String> transformValue:tranformValues)
							{
								String newValue = transformValue.get("newValue");
								String xsiType = transformValue.get("xsiType");
								if (!foundValues.containsKey(newValue)) {
									foundValues.put(newValue, new Object());
									//prtln("xPathToModify:" + xPathToModify + " newElmName:" + newElmName + " existingValue:'" + existingValue + "' newValue:'" + newValue + "' xsiType:'" + xsiType + "'");

									// Remove existing terms if necessary, and add comments:
									if (existingValue.equals(newValue)) {
										if (this.writeComments)
											newDoc.getRootElement().addComment("'" + existingValue + "' is a standard vocabulary term");
										// If keeping existing values, remove all the existing elements for this term to avoid duplicate terms:
										if (keepExistingValues) {
											List existingElms = newDoc.selectNodes(xPathToModify + "[. = '" + existingValue + "']");
											for (int ii = 0; ii < existingElms.size(); ii++)
												((Node) existingElms.get(ii)).detach();
										}
									}
									else {
										if (this.writeComments)
											newDoc.getRootElement().addComment("'" + existingValue + "' was normalized to standard vocabulary term '" + newValue + "'");
									}

									String newElmName = XPathUtils.getNodeName(xPathToModify);
									Element newElm = newDoc.getRootElement().addElement(newElmName);
									newElm.setText(newValue);
									if (xsiType.length() > 0)
										newElm.addAttribute("xsi:type", xsiType);
								}
							}
						}
					}
				}
			}
			String output = newDoc.getRootElement().asXML();
			if(!output.equals(record))
				return output;
			else
				return null;
		}
		catch(Exception e)
		{
			this.addError(recordId, String.format(
				"Elements could not be transformed using group file %s. Error was %s", 
					this.groupFileURIString,
					  e.getMessage()));
			return null;
		}
		
	}
	
	@XmlElement(name = "group-file-uri")
	public String getGroupFileURIString() {
		return groupFileURIString;
	}

	public void setGroupFileURIString(String groupFileURIString) {
		this.groupFileURIString = groupFileURIString;
	}

	@XmlElement(name = "write-comments")
	public boolean isWriteComments() {
		return writeComments;
	}

	public void setWriteComments(boolean writeComments) {
		this.writeComments = writeComments;
	}

}
