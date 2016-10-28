package edu.ucar.dls.harvest.processors.record;

import java.util.Iterator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.workspaces.ResultsWrapper;
import edu.ucar.dls.harvest.workspaces.Workspace;
/**
  Processor that enables one to force an element onto a document. It can either 
  be a static element or an element that can take parameters that are set for the 
  record, ie $url, $partner_id, $resource_handle, or $metadata_handle. If the element 
  you are trying to add contains one of these replaces but that field does not exist,
   the element will not be added to the document. So if for example you try to add a 
   element into a comm_para record of <resourceHandle>$resource_handle</resourceHandle>. 
   Nothing will be added since we do not fetch resourceHandles for comm_para records per
    the  <populate-resource-handles> attribute in the config file. NOTE very important,
     handles are not retrieved until after native-format-processing. Therefore the only 
     places you could use $resource_handle, or $metadata_handle would be in 
     target-format-processors or post-native-format-processors.

Care should be taken when specifying the element for addition, you must wrap the XML in
 CDATA tag and all namespaces MUST be specified since the element will be created first
  then added to the document.
 */

@XmlRootElement(name="AddElement")
public class AddElement extends RecordProcessor{
	private static String BOTTOM = "bottom";
	private static String TOP = "top";
	private static String URL_SUBSTITUTION = "$url";
	private static String PARTNER_ID_SUBSTITUTION = "$partner_id";
	private static String RESOURCE_HANDLE_SUBSTITUTION = "$resource_handle";
	private static String METADATA_HANDLE_SUBSTITUTION = "$metadata_handle";
	
	private String elementXML = null;
	private String location = BOTTOM;
	private boolean pullRecordData = false;
	private int locationPosition = -1;
	private boolean replace = false;

	/**
	 * initialize method that is being used to make sure that the the proecessor
	 * was setup correctly. We want to sure that elementXML was set. 
	 */
	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);
		if(this.elementXML==null)
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					"element-xml is a required attribute for AddElement processor",
					"AddElement.contructor()");
		
		if(!(this.location.equals(BOTTOM)||this.location.equals(TOP)))
		{
			try
			{
				this.locationPosition = Integer.parseInt(this.location);
				if(this.locationPosition<0)
					throw new Exception("Integer value must be a positive value");
			}
			catch (Exception e)
			{
				throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
						"AddElement location attribute must either be bottom, top or a valid integer",
						e);
			}
		}
			
		if(this.elementXML.contains(URL_SUBSTITUTION)||
				this.elementXML.contains(PARTNER_ID_SUBSTITUTION)||
				this.elementXML.contains(RESOURCE_HANDLE_SUBSTITUTION)||
				this.elementXML.contains(METADATA_HANDLE_SUBSTITUTION))
			this.pullRecordData = true;
	}
	
	/**
	 * Run method that actually puts the new element into the document
	 */
	public String run(String documentId, String record) 
		throws HarvestException {
		
		
		Document document;
		try {
			document = Dom4jUtils.getXmlDocument(record);
		
			String elementXMLToAdd = this.elementXML;
			// If a substitution is required need to pull the records info via workspace
			if(this.pullRecordData)
			{
				ResultsWrapper resultsWrapper = workspace.getRecordData(documentId);
		    	Iterator results = resultsWrapper.getResults();
		    	Object[] recordData = (Object[])results.next();
		    	resultsWrapper.clean();
		    	
		    	String partnerId = (String)recordData[0];
		    	String url = (String)recordData[4];
		    	String metadatahandle = (String)recordData[5];
		    	Object resourcehandleObj = recordData[6];
		    	String resourcehandle = null;
		    	if(resourcehandleObj!=null)
		    		resourcehandle = (String)resourcehandleObj;


		    	if(url!=null)
		    		elementXMLToAdd = elementXMLToAdd.replace(URL_SUBSTITUTION, url);
		    	if(partnerId!=null)
		    		elementXMLToAdd = elementXMLToAdd.replace(PARTNER_ID_SUBSTITUTION, partnerId);
		    	if(metadatahandle!=null)
		    		elementXMLToAdd = elementXMLToAdd.replace(METADATA_HANDLE_SUBSTITUTION, metadatahandle);
		    	if(resourcehandle!=null)
		    		elementXMLToAdd = elementXMLToAdd.replace(RESOURCE_HANDLE_SUBSTITUTION, resourcehandle);


		    	// If a replace value didn't exist then we don't want to add the element
		    	if(elementXMLToAdd.contains(URL_SUBSTITUTION)||
		    			elementXMLToAdd.contains(PARTNER_ID_SUBSTITUTION)||
		    			elementXMLToAdd.contains(RESOURCE_HANDLE_SUBSTITUTION)||
		    			elementXMLToAdd.contains(METADATA_HANDLE_SUBSTITUTION))
		    	{
		    		return null;
		    	}
			}
			Element rootElement = document.getRootElement();
			
			// Use dom4j to create the element from text. That way we have a working
			// element to add that has the correct namespace
			Document newElementDocument = Dom4jUtils.getXmlDocument(elementXMLToAdd);
			Element newElement = newElementDocument.getRootElement();

			// If replace is set, we need to remove all elements with that name
			if(this.replace)
			{
				for(Object e: rootElement.selectNodes(
						String.format("*[name()='%s']", newElement.getName())))
				{
					((Element)e).detach();
				}
			}
			// Based on what the position was set to either append it or insert it at 0
			if(this.locationPosition>=0 && rootElement.elements().size()>this.locationPosition)
				rootElement.elements().add(this.locationPosition, newElement);
			else if(this.location.equals(TOP) && rootElement.elements().size()>0)
				rootElement.elements().add(0, newElement);
			else
				rootElement.elements().add(newElement);
			
			String output = rootElement.asXML();
			// Only if it changed return it otherwise return null, so we don't waste
			// a update to the record
			if (!output.equals(record))
			{	
				return output;
			}
		} catch (DocumentException e) {
			this.addError(documentId, String.format("Element could not be built via element-xml (%s)"+
					"in AddElement processor. Error that occured was %s", this.elementXML,
					  e.getMessage()));
		}
		return null;
	}
	
	@XmlElement(name = "element-xml")
	public String getElementXML() {
		return elementXML;
	}

	public void setElementXML(String elementXML) {
		this.elementXML = elementXML;
	}
	
	@XmlElement(name = "location")
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	@XmlElement(name = "replace")
	public boolean getReplace() {
		return this.replace;
	}
	
	public void setReplace(boolean replace) {
		this.replace = replace;
	}

}