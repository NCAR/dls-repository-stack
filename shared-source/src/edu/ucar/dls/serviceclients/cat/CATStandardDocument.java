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
package edu.ucar.dls.serviceclients.cat;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.Files;
import org.dom4j.*;
import java.util.*;
import java.net.*;

/**
 *  Wraps "StandardDocument" elements returned by getAllStandardDocuments call.
 *
 * @author    Jonathan Ostwald
 * @see       GetAllStandardsDocuments
 */
public class CATStandardDocument {

	private static boolean debug = true;
	private static String defaultValue = null;
	private Element element;
	private String identifier;
	private String author;
	private String topic;
	private String gradeLevels;
	private String benchmark;


	/**
	 *  Constructor for the CATStandardDocument object
	 *
	 * @param  e  NOT YET DOCUMENTED
	 */
	public CATStandardDocument(Element e) {
		this.element = e;
	}


	/**
	 *  Gets the element attribute of the CATStandardDocument object
	 *
	 * @return    The element value
	 */
	public Element getElement() {
		return this.element;
	}


	/**
	 *  Gets the identifier attribute of the CATStandardDocument object
	 *
	 * @return    The identifier value
	 */
	public String getId() {
		return getElementText("ID");
	}


	/**
	 *  Gets the author attribute of the CATStandardDocument object
	 *
	 * @return    The author value
	 */
	public String getAuthor() {
		return getElementText("Author");
	}


	/**
	 *  Gets the topic attribute of the CATStandardDocument object
	 *
	 * @return    The topic value
	 */
	public String getTopic() {
		return getElementText("Topic");
	}


	/**
	 *  Gets the created attribute of the CATStandardDocument object
	 *
	 * @return    The created value
	 */
	public String getCreated() {
		return getElementText("Created");
	}


	/**
	 *  Gets the title attribute of the CATStandardDocument object
	 *
	 * @return    The benchmark value
	 */
	public String getTitle() {
		return getElementText("Title");
	}


	/**
	 *  Gets the description attribute of the CATStandardDocument object
	 *
	 * @return    The text value
	 */
	public String getDescription() {
		return getElementText("Description");
	}


	/**
	 *  Gets the version attribute of the CATStandardDocument object
	 *
	 * @return    The version value
	 */
	public String getVersion() {
		return getElementText("Version");
	}


	/**
	 *  Utility to get the text of the child element specified by "tag"
	 *
	 * @param  tag  child element tag
	 * @return      The text of the child element
	 */
	private String getElementText(String tag) {
		return getElementText(tag, this.defaultValue);
	}


	/**
	 *  Utility to get the text of the child element specified by "tag" or a
	 *  default value if the element is not found (rather than throwing an
	 *  Exception)
	 *
	 * @param  tag           child element tag
	 * @param  defaultValue  the default value
	 * @return               The text of the child element, or default if child is
	 *      not found
	 */
	private String getElementText(String tag, String defaultValue) {
		try {
			return this.element.elementTextTrim(tag);
		} catch (Throwable t) {}
		return defaultValue;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  node  Description of the Parameter
	 */
	private static void pp(Node node) {
		prtln(Dom4jUtils.prettyPrint(node));
	}

	public String toString() {
		String s = this.getId();
		String NL = "\n\t";
		s += NL + "author: " + this.getAuthor();
		s += NL + "topic: " + this.getTopic();
		s += NL + "created: " + this.getCreated();
		s += NL + "title: " + this.getTitle();
		s += NL + "version: " + this.getVersion();
		return s;
	}

	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		String prefix = null;
		if (debug) {
			SchemEditUtils.prtln(s, prefix);
		}
	}

}

