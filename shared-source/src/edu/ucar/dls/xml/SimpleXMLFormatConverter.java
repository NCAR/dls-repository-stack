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

import javax.servlet.ServletContext;

/**
 *  A simple demonstraion implementation of the {@link XMLFormatConverter} interface. Just
 *  appends a comment to the end of the XML content. Can be used on any XML format for
 *  demonstration and testing.
 *
 * @author    John Weatherley
 * @see       XMLConversionService
 */
public class SimpleXMLFormatConverter implements XMLFormatConverter {

	/**
	 *  This converter can convert from any format.
	 *
	 * @return    An empty string, since there is no format association.
	 */
	public String getFromFormat() {
		return "";
	}


	/**
	 *  This converter can convert to any format.
	 *
	 * @return    An empty string, since there is no format association.
	 */
	public String getToFormat() {
		return "";
	}

	/**
	 *  Gets the time this converter code was last modified. If unknown, this method should
	 *  return -1.
	 *
	 * @return    The time this converter code was last modified.
	 */
	public long lastModified(ServletContext context)
	{
		return -1;				
	}
	
	/**
	 *  Performs XML conversion from the input format to the output format by simply adding a
	 *  coment to the end of the input XML record.
	 *
	 * @param  xml  XML input in the 'from' format.
	 * @return      XML in the converted 'to' format.
	 */
	public String convertXML(String xml, ServletContext context) {
		return xml + "\n<!-- SimpleXMLFormatConverter created this XML -->";
	}


	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}

