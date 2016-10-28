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

package edu.ucar.dls.schemedit.config;

import java.util.*;
import java.io.Serializable;
import org.dom4j.Element;

/**
 *  Hold a tuple of status value, label and accompanying decription.<p>
 *
 *  For all but "Final status flags", the value and label are the same. For
 *  Final flags, the value is an encoded string that allows the system to
 *  identify the flag as a Final (see StatusFlags).
 *
 *@author    ostwald<p>
 *
 *      $Id $
 */
public class StatusFlag implements Serializable {

	private static boolean debug = true;
	private String label = null;
	private String value = null;
	private String description = null;


	/**
	 *  Constructor for the StatusFlag object
	 *
	 *@param  label        Description of the Parameter
	 *@param  description  Description of the Parameter
	 */
	public StatusFlag(String label, String description) {
		this(label, label, description);
	}


	/**
	 *  Constructor for the StatusFlag object
	 *
	 *@param  label        Description of the Parameter
	 *@param  value        Description of the Parameter
	 *@param  description  Description of the Parameter
	 */
	public StatusFlag(String label, String value, String description) {
		this.label = label;
		this.value = value;
		this.description = description;
	}


	/**
	 *  Constructor for the StatusFlag object that accepts a {@link
	 *  org.dom4j.Element} and parses it into label and description values.
	 *
	 *@param  statusFlagElement  Description of the Parameter
	 *@exception  Exception      if unable to parse into label and description
	 *      values
	 */
	public StatusFlag(Element statusFlagElement)
		throws Exception {
		// prtln("StatusFlag with \n" + statusFlagElement.asXML());
		label = statusFlagElement.element("status").getText();
		value = label;

		// description is optional, so we have to check for existance first
		Element descElement = statusFlagElement.element("description");
		if (descElement != null) {
			description = descElement.getText();
		}
		else {
			description = "";
		}
	}


	/**
	 *  Gets the label attribute of the StatusFlag object
	 *
	 *@return    The label value
	 */
	public String getLabel() {
		return label;
	}


	/**
	 *  Gets the value attribute of the StatusFlag object
	 *
	 *@return    The value value
	 */
	public String getValue() {
		return value;
	}


	/**
	 *  Gets the description attribute of the StatusFlag object
	 *
	 *@return    The description value
	 */
	public String getDescription() {
		return description;
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public String toString() {
		String ret = "StatusFlag";
		ret += "\n\t label: " + label;
		ret += "\n\t value: " + value;
		ret += "\n\t description: " + description;
		return ret;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	private void prtln(String s) {
		System.out.println(s);
	}

}

