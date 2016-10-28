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
 *  Converts from NSDL_DC format to the NCS_COLLECT format. NOTE: assumes nsdl_dc is version 1.02 and
 first transforms to nsdl_dc 1.03 before converting to ncs_collect.
 *
 * @author    Ostwald
 * @see       XMLConversionService
 */
public class NSDL_DCToNCS_COLLECTFormatConverter extends NSDL_DCToNCS_ITEMFormatConverter {

	
	/**
	 *  Converts from the ADN format.
	 *
	 * @return    The String "adn".
	 */
	public String getToFormat() {
		return "ncs_collect";
	}

	protected void getXFormFilesAndIndex(ServletContext context){
		if(index == null)
			index = (SimpleLuceneIndex)context.getAttribute("index");
		if (version_transform_file == null)
			version_transform_file = new File(((String) context.getAttribute("xslFilesDirecoryPath")) +
				"/" + context.getInitParameter("nsdl-dc-v1.02-to-nsdl-dc-v1.03-xsl"));
		if(format_transform_file == null)
			format_transform_file = new File(((String) context.getAttribute("xslFilesDirecoryPath")) +
				"/" + context.getInitParameter("nsdl-dc-to-ncs-collect-xsl"));
	}
	
}

