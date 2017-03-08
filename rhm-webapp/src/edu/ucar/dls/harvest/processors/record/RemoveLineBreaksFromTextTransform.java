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
import org.dom4j.Node;

import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 * Record Processor that removes line breaks \r and \n from element nodes. Even though
 * PrettyPrint utility does this, we still might need to do this in the native format
 * processing so we can validate the schema. Some schemas do not allow line feeds
 * which are considered children in text nodes
 *
 */
@XmlRootElement(name="RemoveLineBreaksFromTextTransform")
public class RemoveLineBreaksFromTextTransform extends RecordProcessor{
	protected String ERROR_PARSING_DOC_MSG = "Error parsing doc trying to remove "+
		"empty nodes.";
	
	/**
	 * Run the record removing any line breaks from text elements
	 * @param recordId record id as a string
	 * @param record record xml as a string
	 * @param workspace the workspace that is being used for harvesting
	 */
	public String run(String documentId, String record) throws HarvestException
	{
		Document document=null;
		Element rootElement=null;
		try {
			document = Dom4jUtils.getXmlDocument(record);
			rootElement = document.getRootElement();
			checkForLineBreaks(rootElement);
		} catch (DocumentException e) {{
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
	 * Recursive method that checks for line breaks in the elements and removes 
	 * them
	 * @param node
	 */
	private void checkForLineBreaks(Node node) {
		if(node.getNodeType() == Node.ELEMENT_NODE)
		{
			Element element = (Element)node;
			List<Element> elements = element.elements();
			
			for(int i=0;i<elements.size();i++)
			{
				checkForLineBreaks(elements.get(i));
			}
			
			if( element.elements().size()==0)
			{
				String text = element.getTextTrim();
				if(text!=null && text!="")
				{
					text = text.replace("\n", "").replace("\r", "");
					element.setText(text);
				}
			}
		}
	}
}
