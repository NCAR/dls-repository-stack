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
 *  Renders instName and instDiv fields of OSM framework as asyncronous
 *  vocabLayouts to ooptimize page load times.
 *
 * @author    ostwald
 */
public class OsmEditorRenderer extends DleseEditorRenderer {
	/**  Description of the Field */
	private static boolean debug = false;


	/**
	 *  Render all "instDivision" fields using asycronous multiBox vocabLayout -
	 *  but only if there is a vocabLayout (groups file) defined for this field.
	 *
	 * @param  xpath  xpath for which we are rendering multiBox input
	 * @return        The multiBoxInput value
	 */
	public Element getMultiBoxInput(String xpath) {
		String normalizedXPath = RendererHelper.normalizeXPath(xpath);

		Element multiBoxInput = null;

		if (rhelper.hasVocabLayout(normalizedXPath) &&
			(normalizedXPath.equals("/record/contributors/person/affiliation/instDivision") ||
			(normalizedXPath.equals("/record/contributors/organization/affiliation/instDivision")))) {

			Element layoutMultiBox = DocumentHelper.createElement("vl__asyncMultiBox")
				.addAttribute("elementPath", "enumerationValuesOf(" + XPathUtils.getSiblingXPath(xpath) + ")");
			multiBoxInput = layoutMultiBox;
		}
		else {
			multiBoxInput = super.getMultiBoxInput(xpath);
		}
		return multiBoxInput;
	}


	/**
	 *  Render all "instName" fields using asycronous singleSelect vocabLayout but
	 *  only if there is a vocabLayout (groups file) defined for this field.
	 *
	 * @param  xpath  xpath for which we are rendering selectInput input
	 * @return        The selectInput value
	 */
	public Element getSelectInput(String xpath) {
		String normalizedXPath = RendererHelper.normalizeXPath(xpath);

		if (rhelper.hasVocabLayout(normalizedXPath) &&
			(normalizedXPath.equals("/record/contributors/person/affiliation/instName") ||
			normalizedXPath.equals("/record/contributors/organization/affiliation/instName"))) {

			Element layoutSingleSelect = DocumentHelper.createElement("vl__asyncSingleSelect")
				.addAttribute("elementPath", "valueOf(" + xpath + ")");
			return layoutSingleSelect;
		}
		return super.getSelectInput(xpath);
	}


	/**
	 *  Sets the debug attribute of the OsmEditorRenderer class
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
			System.out.println("OsmEditorRenderer: " + s);
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private final void prtlnErr(String s) {
		System.err.println("OsmEditorRenderer: " + s);
	}

}
