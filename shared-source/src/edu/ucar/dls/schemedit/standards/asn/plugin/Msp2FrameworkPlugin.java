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
package edu.ucar.dls.schemedit.standards.asn.plugin;

import edu.ucar.dls.schemedit.standards.asn.AsnSuggestionServiceHelper;
import edu.ucar.dls.util.strings.FindAndReplace;

import java.util.*;
import org.apache.struts.util.LabelValueBean;

import org.dom4j.Element;

/**
 *  CATService FrameworkPlugin providing information specific to the "msp2"
 *  framework.<p>
 *
 *  The msp2 framework has multiple subject fields and a repeating keywords
 *  field.
 *
 * @author    ostwald
 */
public class Msp2FrameworkPlugin extends NcsItemFrameworkPlugin {

	/**
	 *  Gets the optionalCatUIFields attribute of the Msp2FrameworkPlugin object
	 *
	 * @return    The optionalCatUIFields value
	 */
	public List getOptionalCatUIFields() {
		return Arrays.asList("subjects", "keywords");
	}


	/**
	 *  Gets the gradeRangePath of the msp2 framework
	 *
	 * @return    The gradeRangePath value
	 */
	public String getGradeRangePath() {
		return "/record/educational/educationLevel";
	}


	/**
	 *  Gets the collected recordSubjects defined under three xpaths.
	 *
	 * @return    The recordSubjects value
	 */
	public String[] getRecordSubjects() {
		List subjectPaths = Arrays.asList(
			"/record/general/subjects/scienceSubject",
			"/record/general/subjects/mathSubject",
			"/record/general/subjects/educationalSubject"
			);
		try {
			return getRecordSubjects(subjectPaths);
		} catch (Throwable t) {
			prtln("getRecordSubjects ERROR: " + t.getMessage());
			t.printStackTrace();
		}
		return null;
	}


	/**
	 *  Remove semicolons from subject values. <P>
	 *
	 *  NOTE: we may want to tak only the leaf, since the upper levels of the
	 *  hierarchical msp2 subject values can be noise that drowns out the signal
	 *  provided by the leaf.
	 *
	 * @param  value  NOT YET DOCUMENTED
	 * @return        NOT YET DOCUMENTED
	 */
	protected String normalizeSubjectValue(String value) {
		return FindAndReplace.replace(value, ":", " ", true);
	}


	/**
	 *  Gets the keywordPath of the msp2 framework
	 *
	 * @return    The keywordPath value
	 */
	public String getKeywordPath() {
		return "/record/general/keyword";
	}


	/**
	 *  Gets the descriptionPath  of the msp2 framework
	 *
	 * @return    The descriptionPath value
	 */
	public String getDescriptionPath() {
		return "/record/general/description";
	}


	/**
	 *  Gets the subjectPath  of the msp2 framework
	 *
	 * @return    The subjectPath value
	 */
	public String getSubjectPath() {
		return "/record/general/subjects/scienceSubject";
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		System.out.println(s);
	}

}

