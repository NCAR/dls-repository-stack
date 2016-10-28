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
 *  Root standard of the Sandards Document representing the top-most standard in
 *  the hierarchy for the Document. Almost identical to the AsnStandard, but
 *  gets its text content from a different element.
 *
 *@author    Jonathan Ostwald
 */
public class RootAsnStandard extends AsnStandard {

	private static boolean debug = false;


	/**
	 *  Constructor for the RootAsnStandard object
	 *
	 *@param  e       NOT YET DOCUMENTED
	 *@param  asnDoc  Description of the Parameter
	 */
	public RootAsnStandard(AsnDocStatement asnStmnt, AsnDocument asnDoc) {
		super(asnStmnt, asnDoc);
	}
	
	public AsnDocStatement getAsnStatement () {
		return (AsnDocStatement)super.getAsnStatement();
	}
	
	public String getDescription () {
		return this.getAsnStatement().getTitle();
	}
	
	/**
	 *  Gets the level attribute of the RootAsnStandard object
	 *
	 *@return    The level value
	 */
	public int getLevel() {
		return 0;
	}


	/**
	 *  The textual content of this RootAsnStandard
	 *
	 *@return    The displayText value
	 */
	public String getDisplayText() {

		String s = FindAndReplace.replace(this.getDescription(), "<br>", "\n", true);
		return removeEntityRefs(s);
	}


	/**
	 *  Gets the ancestors (an empty list) of the AsnNode object
	 *
	 *@return    The ancestors value
	 */
	public List getAncestors() {
		return new ArrayList();
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 *@param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println("AsnNode: " + s);
		}
	}
}

