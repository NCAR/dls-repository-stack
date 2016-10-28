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
 *  Copyright 2002-2011 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */
package edu.ucar.dls.xml;

/**
 *  Indicates a required XPath was missing or empty in the NSDL DC record.
 *
 * @author    John Weatherley
 */
public class NSDLDCNormalizerXpathException extends Exception {
	private String missingRequiredXpath = null;

	/**
	 *  Constructor for the NSDLDCNormalizerXpathException object
	 *
	 * @param  missingRequiredXpath  The XPath that was missing or empty
	 */
	public NSDLDCNormalizerXpathException(String missingRequiredXpath) {
		super("Required XPath was missing or empty: " + missingRequiredXpath);
		this.missingRequiredXpath = missingRequiredXpath;
	}

	/**
	 *  Gets the XPath that was missing or empty.
	 *
	 * @return    The XPath that was missing or empty
	 */
	public String getMissingRequiredXpath() {
		return missingRequiredXpath;
	}
}

