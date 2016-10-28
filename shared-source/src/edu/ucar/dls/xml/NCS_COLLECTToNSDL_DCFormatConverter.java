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
 *  Converts from ADN format to the OAI DC format. Converts DLESE-specific IDs to URLs.
 *
 * @author    John Weatherley
 * @see       XMLConversionService
 */
public class NCS_COLLECTToNSDL_DCFormatConverter extends NCS_ITEMToNSDL_DCFormatConverter {

	
	/**
	 *  Converts from the ADN format.
	 *
	 * @return    The String "adn".
	 */
	public String getFromFormat() {
		return "ncs_collect";
	}

	protected void getXFormFilesAndIndex(ServletContext context){
		if(index == null)
			index = (SimpleLuceneIndex)context.getAttribute("index");
		if(transform_file == null)
			transform_file = new File(((String) context.getAttribute("xslFilesDirecoryPath")) +
				"/" + context.getInitParameter("ncs-collect-to-nsdl-dc-xsl"));
	}
	
}

