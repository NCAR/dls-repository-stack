/*
 *  License and Copyright:
 *
 *  The contents of this file are subject to the Educational Community License v1.0 (the "License"); you may
 *  not use this file except in compliance with the License. You should have received a copy of the License
 *  along with this software; if not, you may obtain a copy of the License at
 *  http://www.opensource.org/licenses/ecl1.php.
 *
 *  Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 *  either express or implied. See the License for the specific language governing rights and limitations
 *  under the License.
 *
 *  Copyright 2002-2009 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */

package edu.ucar.dls.xml;

import edu.ucar.dls.util.*;
import javax.servlet.ServletContext;
import edu.ucar.dls.webapps.tools.*;
import javax.xml.transform.Transformer;
import edu.ucar.dls.index.*;
import edu.ucar.dls.index.reader.*;
import java.io.*;
import java.util.*;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


/**
 *  Converts from NCS_ITEM to NSDL_DC format.
 *
 * @author    Ostwald
 * @see       XMLConversionService
 */
public class NCS_ITEMToNSDL_DCFormatConverter implements XMLDocumentFormatConverter {

	protected File transform_file = null;
	protected SimpleLuceneIndex index = null; 

	/**
	 *  Converts from the ncs_item format.
	 *
	 * @return    The String "ncs_item".
	 */
	public String getFromFormat() {
		return "ncs_item";
	}


	/**
	 *  Converts to the nsdl_dc format.
	 *
	 * @return    The String "nsdl_dc".
	 */
	public String getToFormat() {
		return "nsdl_dc";
	}


	/**
	 *  Gets the time this converter code was last modified. If unknown, this method should
	 *  return -1.
	 *
	 * @param  context  The context in which this is running.
	 * @return          The time this converter code was last modified.
	 */
	public long lastModified(ServletContext context) {
		getXFormFilesAndIndex(context);
		return transform_file.lastModified();
	}


	/**
	 *  Performs XML conversion from ADN to oai_dc format. Characters are encoded as UTF-8.
	 *
	 * @param  xml        XML input in the 'adn' format.
	 * @param  docReader  A lucene doc reader for this record.
	 * @param  context    The servlet context where this is running.
	 * @return            XML in the converted 'oai_dc' format.
	 */
	public String convertXML(String xml, XMLDocReader docReader, ServletContext context) {
		getXFormFilesAndIndex(context);
		try {
						
			Transformer transformer = XSLTransformer.getTransformer(transform_file.getAbsolutePath());
			String transformed_content = XSLTransformer.transformString(xml, transformer);
			
			SAXReader reader = new SAXReader();
			Document document = DocumentHelper.parseText(transformed_content);

			// Dom4j automatically writes using UTF-8, unless otherwise specified.
			OutputFormat format = OutputFormat.createPrettyPrint();
			StringWriter outputWriter = new StringWriter();
			XMLWriter writer = new XMLWriter(outputWriter, format);
			writer.write(document);
			outputWriter.close();
			writer.close();
			return outputWriter.toString();			
		} catch (Throwable e) {
			System.err.println("NCS_ITEMToNSDL_DCFormatConverter was unable to produce transformed file: " + e);
			e.printStackTrace();
			return "";
		}
	}

	
	protected void getXFormFilesAndIndex(ServletContext context){
		if(index == null)
			index = (SimpleLuceneIndex)context.getAttribute("index");
		if(transform_file == null)
			transform_file = new File(((String) context.getAttribute("xslFilesDirecoryPath")) +
				"/" + context.getInitParameter("ncs-item-to-nsdl-dc-xsl"));
	}


	/**
	 * Custom destroy method that may be implemented at a later time to clean up
	 * resources
	 */
	public void destroy() {
		
	}	
	
}

