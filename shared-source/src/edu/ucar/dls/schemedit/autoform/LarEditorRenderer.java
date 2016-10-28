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
package edu.ucar.dls.schemedit.autoform;

import edu.ucar.dls.schemedit.autoform.mde.*;
import edu.ucar.dls.schemedit.standards.StandardsManager;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;

import java.util.*;
import java.util.regex.*;

import org.dom4j.Node;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

/**
 *  Includes lar-specific kludges, most notably the /record/general/subjects
 *  field, which requires an editing approach OTHER than than implied by the
 *  schema ...<p>
 *
 *  Elements created by the LarEditorRenderer starting with "lar__" (e.g.,
 *  "lar__subjects") are rendered tag calls (e.g., "msg:subjects") which are
 *  handled by tag files (e.g., "subjects.tag") in the tags/lar directory.
 *
 * @author    ostwald
 */
public class LarEditorRenderer extends DleseEditorRenderer {
	/**  Description of the Field  */
	private static boolean debug = false;

	String asnStandardPath = "/record/standard";

	/**
	 *  Intercept renderNode calls for certain paths and use jsp tags instead of
	 *  autoform
	 */
	public void renderNode() {
		if (asnStandardPath.equals(xpath)) {
			prtln("LarEditorRenderer got STANDARDS node");
			renderStandardNode();
		}
		else {
			super.renderNode();
		}
	}


	/**  Custom renderer for the Subjects node using a tag file.  */
	protected void renderStandardNode() {
		prtln("renderStandardNode()");
		parent.add(DocumentHelper.createElement("lar__asn-standards"));
		parent.add(DocumentHelper.createElement("lar__other-standards"));
	}

	/**
	 *  Sets the debug attribute of the LarEditorRenderer class
	 *
	 * @param  bool  The new debug value
	 */
	public static void setDebug(boolean bool) {
		debug = bool;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			while (s.length() > 0 && s.charAt(0) == '\n') {
				System.out.println("");
				s = s.substring(1);
			}
			System.out.println("LarEditorRenderer: " + s);
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private final void prtlnErr(String s) {
		System.err.println("LarEditorRenderer: " + s);
	}

}
