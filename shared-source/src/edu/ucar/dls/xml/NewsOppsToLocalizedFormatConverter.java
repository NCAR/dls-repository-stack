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
package edu.ucar.dls.xml;

import edu.ucar.dls.util.*;
import javax.servlet.ServletContext;

/**
 *  Converts from news_opps format to a localized news_opps format, which contains
 *  no namespace declarations. Localized XML may be accessed using XPath notation without
 *  the local-name() function, making it easier to use.
 *
 * @author    John Weatherley
 * @see       XMLConversionService
 */
public class NewsOppsToLocalizedFormatConverter implements XMLFormatConverter {

	/**
	 *  Converts from the news_opps format.
	 *
	 * @return    The String "dlese_collect".
	 */
	public String getFromFormat() {
		return "dlese_collect";
	}


	/**
	 *  Converts to the news_opps-localized format
	 *
	 * @return    The String "news_opps-localized".
	 */
	public String getToFormat() {
		return "news_opps-localized";
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
	 *  Performs XML conversion from news_opps to news_opps localized.
	 *
	 * @param  xml      XML input in the 'news_opps' format.
	 * @param  context  Servlet context
	 * @return          XML in the converted 'news_opps-localized' format.
	 */
	public String convertXML(String xml, ServletContext context) {
		return xml.replaceFirst( "<news\\-oppsRecord.+?>","<news-oppsRecord>" );
	}

	/**
	 * Custom destroy method that may be implemented at a later time to clean up
	 * resources
	 */
	public void destroy() {
		
	}

}

