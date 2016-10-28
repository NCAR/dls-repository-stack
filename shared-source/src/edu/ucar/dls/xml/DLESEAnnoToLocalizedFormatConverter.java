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
 *  Converts from DLESE Anno format to a localized DLESE Anno format, which contains
 *  no namespace declarations. Localized XML may be accessed using XPath notation without
 *  the local-name() function, making it easier to use.
 *
 * @author    John Weatherley
 * @see       XMLConversionService
 */
public class DLESEAnnoToLocalizedFormatConverter implements XMLFormatConverter {

	/**
	 *  Converts from the DLESE Anno format.
	 *
	 * @return    The String "dlese_anno".
	 */
	public String getFromFormat() {
		return "dlese_anno";
	}


	/**
	 *  Converts to the dlese_anno-localized format
	 *
	 * @return    The String "dlese_anno-localized".
	 */
	public String getToFormat() {
		return "dlese_anno-localized";
	}


	/**
	 *  Gets the time this converter code was last modified. If unknown, this method should
	 *  return -1.
	 *
	 * @param  context  Servlet context
	 * @return          The time this converter code was last modified.
	 */
	public long lastModified(ServletContext context) {
		return -1;
	}


	/**
	 *  Performs XML conversion from DLESE Anno to DLESE Anno  localized.
	 *
	 * @param  xml      XML input in the 'dlese_anno' format.
	 * @param  context  Servlet context
	 * @return          XML in the converted 'dlese_anno-localized' format.
	 */
	public String convertXML(String xml, ServletContext context) {
		//return Dom4jUtils.localizeXml(xml.replaceFirst("<\\?xml.+version=.+encoding=.+\\?>", ""), "itemRecord");
		// return Dom4jUtils.localizeXml(xml, "annotationRecord");
		return Dom4jUtils.localizeXml(xml);
	}


	/**
	 * Custom destroy method that may be implemented at a later time to clean up
	 * resources
	 */
	
	public void destroy() {
	}

}

