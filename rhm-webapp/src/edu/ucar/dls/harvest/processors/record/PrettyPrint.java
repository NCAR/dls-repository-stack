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
import javax.xml.bind.annotation.XmlRootElement;

import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import edu.ucar.dls.harvest.exceptions.HarvestException;

/**
 * Simple Record processor that pretty much just calls Dom4JUtils.prettyPrint on the
 * record. Thus normalizing the XML we put in the DB
 */
@XmlRootElement(name="PrettyPrint")
public class PrettyPrint extends RecordProcessor{
	protected String ERROR_PARSING_DOC_MSG = "Error parsing doc trying to pretty "+
	"print nodes.";
	
	/**
	 * Execute Dom4JUtils.prettyPrint on the record and return it, if it changed
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
			Dom4jUtils.prettyPrint(rootElement);
			String output = Dom4jUtils.prettyPrint(rootElement);
			if(!output.equals(record))
				return output;
			else
				return null;
		} catch (DocumentException e) {{
			this.addError(documentId, String.format(ERROR_PARSING_DOC_MSG, 
					e.getMessage()));
			return null;
		}
		}
		
		
	}
}
