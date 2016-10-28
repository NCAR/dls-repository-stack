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
import edu.ucar.dls.schemedit.SchemEditUtils;

import org.dom4j.*;
import java.util.*;
import org.apache.struts.util.LabelValueBean;
import java.net.*;

/**
 *  CATService FrameworkPlugin providing information specific to the res_qual
 *  framework.
 *
 * @author    ostwald
 */
public class ResQualFrameworkPlugin extends NcsItemFrameworkPlugin {
	private static boolean debug = false;


	/**
	 *  Gets the optionalCatUIFields attribute of the ResQualFrameworkPlugin object
	 *
	 * @return    The optionalCatUIFields value
	 */
	public List getOptionalCatUIFields() {
		return Arrays.asList("keywords");
	}

	/**
	 *  Gets the gradeRangePath attribute of the NcsItemSuggestionServiceHelper
	 *  object
	 *
	 * @return    The gradeRangePath value
	 */
	public String getGradeRangePath() {
		return "/record/educational/educationLevel";
	}


	/**
	 *  Gets the keywordPath attribute of the NcsItemSuggestionServiceHelper object
	 *
	 * @return    The keywordPath value
	 */
	public String getKeywordPath() {
		return "/record/general/keyword";
	}


	/**
	 *  Gets the descriptionPath attribute of the ResQualFrameworkPlugin object
	 *
	 * @return    The descriptionPath value
	 */
	public String getDescriptionPath() {
		return "/record/general/description";
	}


	/**
	 *  Gets the subjectPath attribute of the ResQualFrameworkPlugin object
	 *
	 * @return    The subjectPath value
	 */
	public String getSubjectPath() {
		return "";
	}
	
	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "ResQualPlugin");
		}
	}
}

