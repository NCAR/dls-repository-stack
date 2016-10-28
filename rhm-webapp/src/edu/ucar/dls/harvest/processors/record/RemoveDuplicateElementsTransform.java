package edu.ucar.dls.harvest.processors.record;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.util.NodeComparator;

import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.workspaces.Workspace;

/** Record Processor that finds any duplicate elements inside the record and removes
 * all but the last. A duplicate element is defined as having the same namespace, 
 * prefix, text and attributes. There is a flag called ignoreAttributes that can be changed
 * changed to true which will ignore attributes when deciding if two elements are equal 
 */

@XmlRootElement(name="RemoveDuplicateElementsTransform")
public class RemoveDuplicateElementsTransform extends RecordProcessor{
	protected String ERROR_PARSING_DOC_MSG = "Error parsing doc trying to remove "+
		"empty duplicate elements.";
	
	private boolean ignoreAttributes = false;
	
	/**
	 * Find the duplicate elements in the record and detach them from the document.
	 * All but the last duplicate element will be removed
	 */
	public String run(String documentId, String record) throws HarvestException
	{
		Document document=null;
		Element rootElement=null;
		try {
			document = Dom4jUtils.getXmlDocument(record);
			rootElement = document.getRootElement();
			checkForDuplicateElements(rootElement);
		} catch (DocumentException e) {{
			e.printStackTrace();
			this.addError(documentId, String.format(ERROR_PARSING_DOC_MSG, 
					e.getMessage()));
			return null;
		}
		}
		String output = rootElement.asXML();
		
		if(!output.equals(record))
			return output;
		else
			return null;
		
	}
	
	/**
	 * Recursive method that goes through all of an elements children checking
	 * to make sure the elements have no duplicate elements that are further down
	 * in the xml
	 * @param node Element node that inner children should be checked for duplicate 
	 * 					   elements
	 */
	private void checkForDuplicateElements(Node node) {
		if(node.getNodeType() == Node.ELEMENT_NODE)
		{
			
			Element element = (Element)node;
			List<Element> elements = element.elements();
			//element.
			
			List<Element> elementsToRemove = new ArrayList<Element>();
			for(int i=0;i<elements.size();i++)
			{
				Element currentElement = elements.get(i);
				checkForDuplicateElements(currentElement);
				
				// Compare this element with all other sibling children, notice
				// we remove the current not the one later down. The j=i+1 is
				// just saying that we are just checking current element and
				// the next sibling children the prior children
				for(int j=i+1; j<elements.size(); j++)
				{
					Element elementToCompare = elements.get(j);
					if ( this.elementsAreEqual(currentElement, elementToCompare) ) {
				        elementsToRemove.add(currentElement);
				        break;
				    }
				}
			}
			
			// Finally remove all nodes that were added to the elementsToRemoveList
			for(int i=0;i<elementsToRemove.size();i++)
			{
				element.remove(elementsToRemove.get(i));
			}
		}
	}
	
	/**
	 * Private method that decides if two elements are equal. They are equal
	 * only if their names are the same, their namespace prefix and attributes
	 * are the same. Attributes is an option though that can be turned off for 
	 * comparison
	 * @param element1
	 * @param element2
	 * @return
	 */
	private boolean elementsAreEqual(Element element1,
			Element element2) {
		boolean elementsEqual = false;
		if(!this.ignoreAttributes)
		{	
			NodeComparator comparator = new NodeComparator();
			if(element1.getName().equals(element2.getName()) &&
					element1.getText().equals(element2.getText())&&
					element1.getNamespacePrefix().equals(
							element2.getNamespacePrefix()))
			{
				if(!this.ignoreAttributes)
				{
					int c1 = element1.attributeCount();
					int c2 = element2.attributeCount();
					int answer = c1 - c2;
					
					// to be duplicate the attribute count must be the same
					// too
					if (answer == 0) {
						elementsEqual = true;
						for (int i = 0; i < c1; i++) {
							Attribute a1 = element1.attribute(i);
							Attribute a2 = element2.attribute(a1.getQName());
							answer = comparator.compare(a1, a2);
						  
							if (answer != 0) 
							{
								elementsEqual = false;
								break;
							}
						}
					}
				}
				else
					elementsEqual = true;
			}
			if ( comparator.compare( element1, 
					element2 ) == 0 ) {
		        elementsEqual = true;
			}
		}
		else
		{
			if(element1.getName().equals(element2.getName()) &&
					element1.getText().equals(element2.getText())&&
					element1.getNamespacePrefix().equals(
							element2.getNamespacePrefix()))
			{
				elementsEqual = true;
			}
		}
		return elementsEqual;
		
	}
	
	@XmlElement(name = "ignore-attributes")
	public boolean isIgnoreAttributes() {
		return ignoreAttributes;
	}
	
	/**
	 * Method that can be used to change the default of ignoreAttributes
	 * to true
	 * @param ignoreAttributes
	 */
	public void setIgnoreAttributes(boolean ignoreAttributes) {
		this.ignoreAttributes = ignoreAttributes;
	}
}
