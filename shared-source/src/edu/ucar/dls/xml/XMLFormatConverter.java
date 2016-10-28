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
 *  Implementations of this interface are used by the {@link XMLConversionService} to
 *  convert XML from one format to another.
 *
 * @author    John Weatherley
 * @see       XMLConversionService
 */
public interface XMLFormatConverter {

	/**
	 *  The metadataPrefix of the format from which this XMLFormatConverter converts, for
	 *  example "dlese_ims," "adn" or "oai_dc".
	 *
	 * @return    The metadataPrefix of the format being converted.
	 */
	public String getFromFormat();


	/**
	 *  The metadataPrefix of the format to which this XMLFormatConverter converts, for
	 *  example "dlese_ims," "adn" or "oai_dc".
	 *
	 * @return    The metadataPrefix of the format being output.
	 */
	public String getToFormat();


	/**
	 *  Performs XML conversion from the input format to the output format. This method
	 *  should retrun null if the conversion fails for any reason.
	 *
	 * @param  xml      XML input in the 'from' format.
	 * @param  context  The context in which this is running.
	 * @return          XML in the converted 'to' format.
	 */
	public String convertXML(String xml, ServletContext context);


	/**
	 *  Gets the time this converter code was last modified. If unknown, this method should
	 *  return -1.
	 *
	 * @param  context  The context in which this is running.
	 * @return          The time this converter code was last modified.
	 */
	public long lastModified(ServletContext context);


	/**
	 * Custom destroy method that may be implemented at a later time to clean up
	 * resources
	 */
	public void destroy();

}

