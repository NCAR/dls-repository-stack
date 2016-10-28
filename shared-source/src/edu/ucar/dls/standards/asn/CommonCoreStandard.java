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
 *  Extends AsnStandard to provide custom "description" for CommCore standards,
 *  which have "statementNotation" (e.g., "F-BF.4") defined for leaf nodes.
 *
 * @author    Jonathan Ostwald
 */
public class CommonCoreStandard extends AsnStandard {

	private static boolean debug = false;


	/**
	 *  Constructor for the CommonCoreStandard object
	 *
	 * @param  asnDoc    the parent standards document
	 * @param  asnStmnt  the standard statement that provides data for this AsnStandard object
	 */
	public CommonCoreStandard(AsnStatement asnStmnt, AsnDocument asnDoc) {
		super(asnStmnt, asnDoc);
	}

	Pattern numPat = Pattern.compile("[\\d]+\\. ",
		Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

	/**
	 *  If there is a statementNotation defined for this standard, remove the
	 *  leading number since it is redundant with a portion of the statementNotation
	 *
	 * @return    The description value
	 */
	public String getDescription() {
		String desc = this.getAsnStatement().getDescription();
		if (this.isLeaf()) {
			return numPat.matcher(desc).replaceFirst("");
		}
		else {
			return desc;
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println("CO Bmk: " + s);
		}
	}
}

