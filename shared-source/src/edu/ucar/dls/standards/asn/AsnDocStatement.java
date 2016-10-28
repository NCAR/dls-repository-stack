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
package edu.ucar.dls.standards.asn;

import org.dom4j.Element;

import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XPathUtils;
import edu.ucar.dls.util.strings.FindAndReplace;
import java.util.regex.*;

import java.util.*;

/**
 *  Extends AsnStatement to capture document-level information from the
 *  "asn:StandardDocument" statement of a ASN resolver response or a ASN
 *  standards document file.
 *
 * @author    Jonathan Ostwald
 */
public class AsnDocStatement extends AsnStatement {

	private static boolean debug = true;
	private String title;
	private String fileCreated;
	private String created;
	private String exportVersion;
	private String jurisdiction;


	/**
	 *  Constructor for the AsnDocStatement object
	 *
	 * @param  e  NOT YET DOCUMENTED
	 */
	public AsnDocStatement(Element docStdEl, Element docDescEl) {
		super(docStdEl);
		this.title = getSubElementText(docStdEl, "title");
		this.fileCreated = getSubElementText("repositoryDate");
		this.created = getSubElementText("valid");
		this.exportVersion = getSubElementResource(docDescEl, "exportVersion");
		this.jurisdiction = getSubElementResource("jurisdiction");
	}


	/**
	 *  Gets the title attribute of the AsnDocStatement object
	 *
	 * @return    The title value
	 */
	public String getTitle() {
		return this.title;
	}


	/**
	 *  Gets the fileCreated attribute of the AsnDocStatement object
	 *
	 * @return    The fileCreated value
	 */
	public String getFileCreated() {
		return this.fileCreated;
	}


	/**
	 *  Gets the created attribute of the AsnDocStatement object
	 *
	 * @return    The created value
	 */
	public String getCreated() {
		return this.created;
	}


	/**
	 *  Gets the exportVersion attribute of the AsnDocStatement object
	 *
	 * @return    The exportVersion value
	 */
	public String getExportVersion() {
		return this.exportVersion;
	}

	public String getJurisdiction() {
		return this.jurisdiction;
	}
	
	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @return    NOT YET DOCUMENTED
	 */
	public String toString() {
		String s = "\ntitle: " + this.getTitle();
		s += "\n\t" + "fileCreated: " + this.getFileCreated();
		s += "\n\t" + "created: " + this.getCreated();
		s += "\n\t" + "exportVersion: " + this.getExportVersion();
		s += "\n\t" + "jurisdiction: " + this.getJurisdiction();

		s += "\n\n" + "Statement info";
		s += super.toString();

		return s;
	}


	private static void prtln(String s) {
		if (debug) {
			System.out.println("AsnDocStatement: " + s);
		}
	}

}

