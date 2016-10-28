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

/**
 *  Converts from ADN format to a localized ADN format, which contains no namespace declarations. 
 Localized XML may be accessed using XPath notation without the local-name() function, making it 
 easier to use.
 *
 * @author    John Weatherley
 * @see       XMLConversionService
 */
public class ADNToLocalizedFormatConverter implements XMLFormatConverter {

	/**
	 *  Converts from the ADN format.
	 *
	 * @return    The String "adn".
	 */
	public String getFromFormat() {
		return "adn";
	}


	/**
	 *  Converts to the adn-localized format
	 *
	 * @return    The String "adn-localized".
	 */
	public String getToFormat() {
		return "adn-localized";
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
	 *  Performs XML conversion from ADN to ADN localized.
	 *
	 * @param  xml  XML input in the 'adn' format.
	 * @return      XML in the converted 'adn-localized' format.
	 */
	public String convertXML(String xml,ServletContext context) {
		return Dom4jUtils.localizeXml(xml);	
	}


	/**
	 * Custom destroy method that may be implemented at a later time to clean up
	 * resources
	 */
	public void destroy() {
		
	}

}

