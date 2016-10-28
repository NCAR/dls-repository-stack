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

import edu.ucar.dls.xml.XMLConversionService;
import java.io.*;
import org.json.XML;
import org.apache.commons.lang.StringEscapeUtils;

/**
 *  This class holds static methods for use in XML processing.
 *
 * @author    John Weatherley
 */
public class XMLUtils {

	/**
	 *  Removes all XML comments from a String.
	 *
	 * @param  input  XML String
	 * @return        XML with all comments removed
	 */
	public static String removeXMLComments(String input) {
		return input.replaceAll("(?s)<!--.*?-->", "");
	}


	/**
	 *  Strips the XML declaration and DTD declaration from the given XML. The resulting content is sutable for
	 *  insertion inside an existing XML element.
	 *
	 * @param  rdr              A BufferedReader containing XML.
	 * @return                  Content with the XML and DTD declarations stipped out.
	 * @exception  IOException  If error
	 */
	public final static StringBuffer stripXmlDeclaration(BufferedReader rdr) throws IOException {
		return XMLConversionService.stripXmlDeclaration(rdr);
	}


	/**
	 *  Convert XML to JSON.
	 *
	 * @param  xml  An XML String.
	 * @return      JSON serialization of the XML or empty string if error.
	 */
	public final static String xml2json(String xml) {
		try {
			return XML.toJSONObject(xml).toString(3);
		} catch (Throwable e) {
			System.err.println("Error converting XML to JSON: " + e);
			return "";
		}
	}


	/**
	 *  Escapes the characters in a String using XML entities.
	 *
	 * @param  xml  The String to escape, may be null
	 * @return      A new escaped String, null if null string input
	 */
	public final static String escapeXml(String xml) {
		return StringEscapeUtils.escapeXml(xml);
	}


	private static void prtln(String s) {
		System.out.println(s);
	}

}


