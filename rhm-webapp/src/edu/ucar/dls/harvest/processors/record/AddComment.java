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

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.workspaces.Workspace;
/** Custom processor that adds a comment into the document.*/

@XmlRootElement(name="AddComment")
public class AddComment extends RecordProcessor{

	private String comment = null;
	
	/**
	 * Initialize the processor. if a comment isn't specified an error will be thrown
	 */
	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);
		if(this.comment==null)
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					"comment is a required attribute for AddElement processor",
					"AddElement.contructor()");
	}
	
	/**
	 * Run method that places the comment into the document. Note that the comment will be placed
	 * at the bottom of the document and will only be placed if the comment doesn't already exist
	 */
	public String run(String documentId, String record) 
		throws HarvestException {
		
		
		Document document;
		try {
			document = Dom4jUtils.getXmlDocument(record);
			Element rootElement = document.getRootElement();

			// cannot use xpath here because it doesn't match comments for some reason
			Iterator<Node> nodeIterator = rootElement.nodeIterator();
			while(nodeIterator.hasNext())
			{
				Node node = nodeIterator.next();
				if(node.getNodeType()==Node.COMMENT_NODE && node.getText().equals(this.comment))
					return null;
			}

			rootElement.addComment(this.comment);
			
			String output = rootElement.asXML();
			return output;
			
		} catch (DocumentException e) {
			this.addError(documentId, String.format("Comment %s could not be added to document. "+
					"Error was %s", this.comment, e.getMessage()));
		}
		return null;
	}
	
	@XmlElement(name = "comment")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
