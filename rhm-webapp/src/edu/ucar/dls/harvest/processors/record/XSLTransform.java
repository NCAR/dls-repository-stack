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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.bind.annotation.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.tools.CustomURIResolver;
import edu.ucar.dls.harvest.workspaces.Workspace;


/**
 * The main transform processor that is used for all transformations. This transform 
 * takes a record and transforms it using a XSL file. For efficiency this processor
 * caches the compiled xsl sheets for use for records after the first.
 */
@XmlRootElement(name="XSLTransform")
public class XSLTransform extends RecordProcessor{
	private URI xslURI = null;
	private String xslURIString = null;
	private Transformer transformer = null;
	
	/**
	 * initialize the processor by making sure that the xsl exists and creating
	 * a cached source for the xsl file to make it so we only have to fetch the
	 * xsl file and includes once
	 */
	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);
		StreamSource xsltSource = null;
		
		try {
			this.xslURI = new URI(this.xslURIString);
			URL aURL = this.xslURI.toURL();
			xsltSource = new StreamSource(aURL.openStream());
			
		} catch (MalformedURLException e1) {
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
					String.format("Transform URI %s is malformed.", this.xslURI.toString()),
					e1);
		} catch (IOException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					String.format("URI %s could not be found.", this.xslURI.toString()),
					e);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TransformerFactory transFact = TransformerFactory.newInstance();

		transFact.setURIResolver(new CustomURIResolver(this.xslURI));
		Templates cachedXSLT;
		try {
			cachedXSLT = transFact.newTemplates(xsltSource);
			this.transformer = cachedXSLT.newTransformer();
			this.transformer.setOutputProperty(OutputKeys.ENCODING, Config.ENCODING);
			
		} catch (TransformerConfigurationException e) {
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
					String.format("Transform URI %s, could not be compiled as a XSL", 
							this.xslURI.toString()),
					e);
		}
	}
	
	/**
	 * Transform the record using the cached version of the xsl sheet
	 * @param recordId record id as a string
	 * @param record record xml as a string
	 * @param workspace the workspace that is being used for harvesting
	 */
	public String run(String recordId, String record) 
									throws HarvestException
	{
		StringWriter writer = new StringWriter();
		StreamSource source = new StreamSource(new StringReader(record));
		try {
			this.transformer.transform(source, new StreamResult(writer));
		} catch (TransformerException e) {
			this.addError(recordId, 
				String.format("Record could not be transformed using URI %s, Exception: %s ", 
					xslURI.toString(), e.getMessage()));
		}
		String output = writer.toString();
		if(!output.equals(record))
			return output;
		else
			return null;
	}
	
	@XmlElement(name = "uri")
	public String getXslURIString() {
		return xslURIString;
	}
	
	/**
	 * Method that must be called to set the xsl uri path into the processor
	 * @param uriString
	 */
	public void setXslURIString(String uriString)
	{
		this.xslURIString = uriString;
	}
}
