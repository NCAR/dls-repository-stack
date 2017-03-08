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
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 * Processor that enforces that a record has at least one element that is found by
 * a list of xpaths
 */

@XmlRootElement(name="RequireElement")
public class RequireElement extends RecordProcessor{	
	private static final String ERROR_MSG = 
		"%d elements were found in record using xpaths %s but at least %d are required";
	protected String ERROR_PARSING_DOC_MSG = "Error parsing doc trying to find elements " +
	"to retrict their counts. xpaths:%s, Exception %s.";
	
	private String[] xpaths= null;
	
	private int elementCount=1;
	private boolean includeWarning = true;
	private boolean throwError = false;
	private String  uriXPathsDisplayValue = null;
	/**
	 * Initializer of the Processor which enforces the rule that xpaths must be
	 * set for this processor
	 */
	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);
		if(this.xpaths==null)
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					"xpaths are a required attribute that must be defined for all ingestors.",
					"RequireElement.contructor()");
		this.uriXPathsDisplayValue = StringUtils.join(this.xpaths, ", ");
	}
	
	 /**
	 * Run the processor, by going through the xpaths looking for the elements.
	 * If they are under the deired number then either throw a warning or error
	 */
	public String run(String documentId, String record) 
		throws HarvestException {
		ArrayList<Element> matchedElements = new ArrayList<Element>();
		ArrayList<String> elementPathsHandled = new ArrayList<String>();
		try {
			Document document = Dom4jUtils.getXmlDocument(record);
			Element rootElement = document.getRootElement();
			// Loop through the many xpaths
			
			for(String xpath: this.xpaths)
			{
				List<Element> matchingNodes = rootElement.selectNodes(xpath);
				
				for( Element matchedElement: matchingNodes)
				{
					if(elementPathsHandled.contains(matchedElement.getUniquePath()))
						continue;
					elementPathsHandled.add(matchedElement.getUniquePath());
					matchedElements.add(matchedElement);
					HarvestRequest.checkForInterruption(
					"RequireElement.run()->looping through matched elements");
				}
				HarvestRequest.checkForInterruption(
					"RequireElement.run()->looping through xpaths");
			}
			if(matchedElements.size()<this.elementCount)
			{
				// That means there are not enough elements
				String errorMsg = String.format(ERROR_MSG, matchedElements.size(),
						this.uriXPathsDisplayValue, this.elementCount);
				if(this.throwError)
				{
					this.addError(documentId, errorMsg);
				}
				else if(this.includeWarning)
				{
					this.addWarning(documentId, errorMsg);
				}
			
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
	public String[] getXpaths() {
		return xpaths;
	}

	/**
	 * Set method that sets the xpaths
	 * @param xpaths
	 */
	public void setXpaths(String[] xpaths) {
		this.xpaths = xpaths;
	}

	@XmlElement(name = "element-count")
	public int getElementCount() {
		return elementCount;
	}

	/**
	 * Sets the restricted element count number that is used during
	 * processing
	 * @param elementCount
	 */
	public void setElementCount(int elementCount) {
		this.elementCount = elementCount;
	}
	@XmlElement(name = "include-warning")
	public boolean isIncludeWarning() {
		return includeWarning;
	}

	public void setIncludeWarning(boolean includeWarning) {
		this.includeWarning = includeWarning;
	}

	@XmlElement(name = "throw-error")
	public boolean isThrowError() {
		return throwError;
	}

	public void setThrowError(boolean throwError) {
		this.throwError = throwError;
	}
}
