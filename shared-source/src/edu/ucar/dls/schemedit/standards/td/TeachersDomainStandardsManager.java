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
package edu.ucar.dls.schemedit.standards.td;

import edu.ucar.dls.schemedit.standards.StandardsManager;
import edu.ucar.dls.schemedit.standards.adn.util.MappingUtils;

import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.schemedit.*;

import java.io.*;
import java.util.*;

import java.net.*;

/**
 *  StandardsManager for the TeachersDomain Framework.
 *
 * @author    ostwald
 */
public class TeachersDomainStandardsManager implements StandardsManager {
	private static boolean debug = true;

	String version;
	TeachersDomainLexicon standardsDocument = null;
	String xmlFormat;
	String xpath;


	/**
	 *  Constructor for the TeachersDomainStandardsManager object
	 *
	 * @param  source         NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */

	public TeachersDomainStandardsManager(String xmlFormat, String xpath, File source) throws Exception {
		this.xmlFormat = xmlFormat;
		this.xpath = xpath;
		this.standardsDocument = new TeachersDomainLexicon(source);
	}


	/**
	 *  Gets the standardsDocument attribute of the TeachersDomainStandardsManager object
	 *
	 * @return    The standardsDocument value
	 */
	public TeachersDomainLexicon getStandardsDocument() {
		return this.standardsDocument;
	}


	/**
	 *  Gets the xmlFormat attribute of the TeachersDomainStandardsManager object
	 *
	 * @return    The xmlFormat value
	 */
	public String getXmlFormat() {
		// return "comm_core";
		return this.xmlFormat;
	}


	/**
	 *  Gets the xpath attribute of the TeachersDomainStandardsManager object
	 *
	 * @return    The xpath value
	 */
	public String getXpath() {
		return this.xpath;
	}


	/**
	 *  Gets the rendererTag attribute of the TeachersDomainStandardsManager object
	 *
	 * @return    The rendererTag value
	 */
	public String getRendererTag() {
		return "standards_MultiBox";
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug)
			SchemEditUtils.prtln(s, "TeachersDomainStandardsManager");
	}
}
