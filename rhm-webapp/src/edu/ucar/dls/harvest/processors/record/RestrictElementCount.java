package edu.ucar.dls.harvest.processors.record;

import java.util.ArrayList;
import java.util.Collections;
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
 * Processor that enforces a limitation on element counts defined by xpaths. 
 * here are options that can be changed for the action to be taken when the count is exceeded.
 * you can either throw an error or have the extras removed. This removal process is based
 * on what has priority the first ones or last ones
 */

@XmlRootElement(name="RestrictElementCount")
public class RestrictElementCount extends RecordProcessor{	
	private static final String ERROR_MSG = 
		"%d elements were found in record using xpaths %s but no more then " +
		"%d are allowed to be be present.%s";
	private static final String WARNING_EXTRA = " Extra elements will removed from " +
		"record with the %s having priority.";
	protected String ERROR_PARSING_DOC_MSG = "Error parsing doc trying to find elements " +
	"to retrict their counts. xpaths:%s, Exception %s.";
	
	private static final String PRIORITY_FIRST  = "first";
	private static final String PRIORITY_LAST  = "last";
	
	private String[] xpaths= null;
	
	private int elementCount=1;
	private String priority = PRIORITY_FIRST; 
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
					"RestrictElementCount.contructor()");
		if(!this.priority.equals(PRIORITY_FIRST) && !this.priority.equals(PRIORITY_LAST))
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					String.format("Priority value %s is invalid. Priority must be either " +
							"%s or %s.", 
							this.priority, PRIORITY_FIRST, PRIORITY_LAST),
					"RestrictElementCount.contructor()");
		this.uriXPathsDisplayValue = StringUtils.join(this.xpaths, ", ");
	}
	
	 /**
	 * Run the processor, by going through the xpaths looking for the elements.
	 * Note the first xpaths have priority. Therefore if more then one xpath is
	 * specified the ones found from the first xpath are considered first and
	 * not actually what comes first in the record.
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
					"RestrictElementCount.run()->looping through matched elements");
				}
				HarvestRequest.checkForInterruption(
					"RestrictElementCount.run()->looping through xpaths");
			}
			if(matchedElements.size()>this.elementCount)
			{
				// That means there are more then the restricted amount, handle this
				// per attributes that may or may not have been passed in
				if(this.throwError)
				{
					String errorMsg = String.format(ERROR_MSG, matchedElements.size(),
							this.uriXPathsDisplayValue, this.elementCount, "");
					this.addError(documentId, errorMsg);
					return null;
				}
				if(this.includeWarning)
				{
					String warningExtraMsg = String.format(WARNING_EXTRA, this.priority);
					String errorMsg = String.format(ERROR_MSG, matchedElements.size(),
							this.uriXPathsDisplayValue, this.elementCount, warningExtraMsg);
					this.addWarning(documentId, errorMsg);
				}
				
				// we want to get the items that we want to remove in order from index
				// 0. If the Priority is last its already the direction we want. Where
				// the first ones are first
				if(this.priority.equals(PRIORITY_FIRST))
					Collections.reverse(matchedElements);
				
				while(matchedElements.size()>this.elementCount)
				{
					Element elementToRemove = matchedElements.get(0);
					elementToRemove.detach();
					matchedElements.remove(elementToRemove);
				}
				return rootElement.asXML();
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

	/**
	 * Sets the restricted element count number that is used during
	 * processing
	 * @param elementCount
	 */
	@XmlElement(name = "priority")
	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
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
