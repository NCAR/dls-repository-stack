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
import edu.ucar.dls.schemedit.standards.asn.AsnCATPlugin;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.standards.asn.GradeRangeHelper;
import edu.ucar.dls.xml.Dom4jUtils;

import org.dom4j.*;
import java.util.*;
import org.apache.struts.util.LabelValueBean;
import java.net.*;

/**
 *  CATService FrameworkPlugin providing information specific to the eng_path
 *  framework.
 *
 * @author    ostwald
 */
public class EngPathFrameworkPlugin extends AsnCATPlugin {
	private static boolean debug = false;


	/**
	 *  Gets the optionalCatUIFields attribute of the EngPathFrameworkPlugin object
	 *
	 * @return    The optionalCatUIFields value
	 */
	public List getOptionalCatUIFields() {
		/* return Arrays.asList("keywords"); */
		return Arrays.asList("subjects", "keywords");
	}

	
	/**
	 *  Gets the gradeRangePath attribute of the NcsItemSuggestionServiceHelper
	 *  object
	 *
	 * @return    The gradeRangePath value
	 */
	public String getGradeRangePath() {
		return "/record/educational/grade";
	}


	/**
	 *  Gets the GradeRanges corresponding to the grades searchable in the
	 *  Suggestion service (and which can be specified in the control box).<p>
	 *
	 *  NOTE: the "values" should correspond to what the service expects, while the
	 *  "labels" should correspond to the selectable values from the
	 *  metaDataFramework.
	 *
	 * @return    The gradeRanges value
	 */
	public List getGradeRangeOptions() {
		List options = new ArrayList();
		try {
			for (int i = 0; i <= 12; i++) {
				String num = String.valueOf(i);
				options.add(new LabelValueBean(num, num));
			}
		} catch (Throwable t) {
			prtlnErr("getGradeRangeOptions ERROR: " + t.getMessage());
			// t.printStackTrace();
		}
		return options;
	}


	/**
	 *  NOTE: the schema simply defines these values, and the groups file does not
	 *  offer any alternative screen names.
	 *  <ul>
	 *    <li> groups file: http://ns.nsdl.org/ncs/eng_path/1.00/groups/grade-eng_path-groups-cataloger-en-us.xml
	 *
	 *    <li> schema: http://ns.nsdl.org/ncs/eng_path/1.00/schemas/vocabs/grade.xsd
	 *
	 *  </ul>
	 *
	 *
	 * @return    NOT YET DOCUMENTED
	 */
	protected GradeRangeHelper gradeRangeHelperInit() {
		GradeRangeHelper grh = new GradeRangeHelper();
		grh.addItem("0", 0);
		grh.addItem("1", 1);
		grh.addItem("2", 2);
		grh.addItem("3", 3);
		grh.addItem("4", 4);
		grh.addItem("5", 5);
		grh.addItem("6", 6);
		grh.addItem("7", 7);
		grh.addItem("8", 8);
		grh.addItem("9", 9);
		grh.addItem("10", 10);
		grh.addItem("11", 11);
		grh.addItem("12", 12);
		return grh;
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
	 *  Gets the descriptionPath attribute of the EngPathFrameworkPlugin object
	 *
	 * @return    The descriptionPath value
	 */
	public String getDescriptionPath() {
		return "/record/general/description";
	}


	/**
	 *  Gets the subjectPath attribute of the EngPathFrameworkPlugin object
	 *
	 * @return    The subjectPath value
	 */
	public String getSubjectPath() {
		return "/record/general/specialTopic";
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "EngPathPlugin");
		}
	}
}

