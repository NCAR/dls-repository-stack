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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.resources.ASNHelper;
import edu.ucar.dls.harvest.workspaces.Workspace;
/**
 * Processor that adds extra elements into a nsdl_dc document that are based off
 * what the ASN record contains. The current available elements are ASN educational level
 * and ASN subject. Note this processor does will not add duplicate elements
 */

@XmlRootElement(name="AddNSDLElementsFromASN")
public class AddNSDLElementsFromASN extends RecordProcessor{
	private String asnElementXpath = null;
	private ASNHelper asnHelper = null;
	
	// The format of the educational level element
	private static final String EDUCATION_LEVEL_ELEMENT_XML = 
		"<dct:educationLevel xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " +
		"xmlns:dct='http://purl.org/dc/terms/' xsi:type='nsdl_dc:NSDLEdLevel'>%s</dct:educationLevel>";
	
	// The format of the subject element
	private static final String SUBJECT_ELEMENT_XML = 
		"<dc:subject xmlns:dc='http://purl.org/dc/elements/1.1/'>%s</dc:subject>";
		
	/**
	 * initialize method that is being used to make sure that the the proecessor
	 * was setup correctly. We want to sure that elementXML was set. 
	 */
	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);
		if(this.asnElementXpath==null)
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					"asn-element-xpath is a required attribute for AddNSDLElementsFromASN processor",
					"AddNSDLElementsFromASN()");
		// Set it once in initialize so that for each record after the first it doesn't have
		// to re-get the conversion docs
		this.asnHelper = new ASNHelper();
		
	}
	
	/**
	 * Run method that actually puts the new element into the document
	 */
	public String run(String documentId, String record) 
		throws HarvestException {
		Document document;
		String asnId = null;
		try {
			document = Dom4jUtils.getXmlDocument(record);
			Element rootElement =  document.getRootElement();
			
			
			List<Element>definedSubjectElements = rootElement.selectNodes("*[name()='dc:subject']");
			List<Element>definedEducationLevel = rootElement.selectNodes("*[name()='dct:educationLevel']");
			
			// We only want to add subjects or ed levels if they are not already present within
			// the document
			boolean addSubjects = false;
			boolean addEdLevels = false;
			if(definedSubjectElements.size()==0)
				addSubjects = true;
			if(definedEducationLevel.size()==0)
				addEdLevels = true;
			
			List<Element>asnElements = rootElement.selectNodes(this.asnElementXpath);
			
			// Using sets to make sure that duplicate elements are never created
			Set<String> elementsAsStrings = new HashSet<String>();
			
			if(addSubjects||addEdLevels)
			{
				for(Element element : asnElements)
				{
					asnId = element.getTextTrim();
					if(!asnId.contains(Config.ASN.ASN_BASE_URL))
						continue;

					if(addEdLevels)
					{
						// Adding educational levels
						List<String> educationalLevels = this.asnHelper.getEducationalLevels(asnId);
						if(educationalLevels!=null)
						{
							for(String educationLevel:educationalLevels)
							{
								elementsAsStrings.add(String.format(EDUCATION_LEVEL_ELEMENT_XML, 
												educationLevel));
												
							}
						}
					}
					

					if(addSubjects)
					{
						// Adding subject
						String subject = this.asnHelper.getSubject(asnId);
						if(subject!=null && !subject.equals(""))
						{
							elementsAsStrings.add(String.format(SUBJECT_ELEMENT_XML, 
									subject));
						}
					}
						
				}
			}
			
			// Now add them to the root document and thus returning the result
			if(elementsAsStrings.size()>0)
			{
				Iterator<String> iterator = elementsAsStrings.iterator();
				while(iterator.hasNext())
				{
					Element newElement = (Element)Dom4jUtils.getXmlDocument(
						iterator.next()).getRootElement().detach();
					rootElement.elements().add(newElement);
				}
				return rootElement.asXML();
			}
			
		} catch (DocumentException e) {
			this.addError(documentId, String.format("Element could not be built for asn (%s)"+
					"in ASNConversion processor. Error that occured was %s", asnId,
					  e.getMessage()));
		}
		catch (Exception e) {
			this.addError(documentId, String.format(
				"Error trying to convert asn to education levels. Error %s at AddEducationLevelFromASN.run()", 
					  e.getMessage()));
		}
		return null;
	}
	
	@XmlElement(name = "asn-element-xpath")
	public String getAsnElementXpath() {
		return asnElementXpath;
	}

	public void setAsnElementXpath(String asnElementXpath) {
		this.asnElementXpath = asnElementXpath;
	}
	
}
