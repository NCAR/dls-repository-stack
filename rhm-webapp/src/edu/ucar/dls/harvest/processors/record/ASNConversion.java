package edu.ucar.dls.harvest.processors.record;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.resources.ASNHelper;
import edu.ucar.dls.harvest.tools.URIutil;
import edu.ucar.dls.harvest.workspaces.Workspace;
/**
 Processor that tries to convert any other standard into ASN standard. But using RDF
 files that are provided by JessCo. Any added standard just need to place their 
 RDF that JessCo has here http://asn.jesandco.org/resources/ASNJurisdiction.
 */

@XmlRootElement(name="ASNConversion")
public class ASNConversion extends RecordProcessor{
	private String elementXpath = null;
	private ASNHelper asnHelper = null;
	private static final String ASN_ELEMENT_XML = 
		"<dct:conformsTo xsi:type='dct:URI' xmlns:dct='http://purl.org/dc/terms/' " +
			"xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>%s</dct:conformsTo>";
	/**
	 * initialize method that is being used to make sure that the the proecessor
	 * was setup correctly. We want to sure that elementXML was set. 
	 */
	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);
		if(this.elementXpath==null)
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					"element-xpath is a required attribute for ASNConversion processor",
					"ASNConversion()");
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
		String value = null;
		try {
			document = Dom4jUtils.getXmlDocument(record);
			Element rootElement =  document.getRootElement();
			List<Element>alignmentElements = rootElement.selectNodes(this.elementXpath);
			
			for(Element element : alignmentElements)
			{
				value = element.getTextTrim();
				asnId = null;
				if(value==null | value.equals(""))
					continue;
				
				if(URIutil.isURI(value))
				{
					// If its already a ASN ignore it
					if(value.contains(Config.ASN.ASN_BASE_URL))
						continue;
					else if(value.contains(Config.ASN.OLD_ASN_BASE_URL))
					{ 
						element.setText(value.replace(Config.ASN.OLD_ASN_BASE_URL, 
								Config.ASN.ASN_BASE_URL));
					    continue;
					}
					else
					{
						// if its a uri though try to convert it
						asnId = this.asnHelper.fetchASNIdExternalURL(value);
					}
				}
				else
				{
					// else try to get it by stmt notation then by description
					// The order is also decided on the trasnformation level too.
					
					asnId = this.asnHelper.fetchASNIdByStmtNotation(value);
					if(asnId==null)
						asnId = this.asnHelper.fetchASNIdByDescription(value);
					
					// In the case of standard dot notation and description. If we
					// find the asn, we remove the description/notation. Only 
					// for url the if before this. Do we leave both
					if(asnId!=null)
						element.detach();
				}
				if(asnId!=null)
				{
					Element newElement = (Element)Dom4jUtils.getXmlDocument(
							String.format(ASN_ELEMENT_XML, asnId)).getRootElement().detach();
					rootElement.elements().add(newElement);
				}
			}
			
			
			
			String output = rootElement.asXML();
			// Only if it changed return it otherwise return null, so we don't waste
			// a update to the record
			if (!output.equals(record))
			{	
				return output;
			}
		} catch (DocumentException e) {
			this.addError(documentId, String.format("Element could not be built for asn (%s)"+
					"in ASNConversion processor. Error that occured was %s", asnId,
					  e.getMessage()));
		}
		catch (Exception e) {
			this.addError(documentId, String.format("Error trying to convert conforms to "+
					"into an ASN using ASN Helper. For conforms to %s, Error that occured was %s", value,
					  e.getMessage()));
		}
		return null;
	}
	
	@XmlElement(name = "element-xpath")
	public String getElementXpath() {
		return elementXpath;
	}

	public void setElementXpath(String elementXpath) {
		this.elementXpath = elementXpath;
	}
	
}