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
