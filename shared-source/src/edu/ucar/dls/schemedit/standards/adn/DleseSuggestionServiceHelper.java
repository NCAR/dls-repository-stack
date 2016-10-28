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

package edu.ucar.dls.schemedit.standards.adn;

import edu.ucar.dls.schemedit.standards.*;

import edu.ucar.dls.schemedit.MetaDataFramework;
import edu.ucar.dls.schemedit.action.form.SchemEditForm;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.schemedit.*;

import edu.ucar.dls.serviceclients.cat.CATStandard;
import edu.ucar.dls.serviceclients.cat.CATRequestConstraints;

import javax.servlet.ServletContext;

import org.dom4j.*;

import java.io.*;
import java.util.*;

import java.net.*;

/**
 *  CATServiceHelper that converts ASN standards to ADN
 *  representation and back for use with the ADN metadataFramework.
 *
 * @author    ostwald
 */
public class DleseSuggestionServiceHelper extends CATServiceHelper {
	private static boolean debug = true;

	AsnToAdnMapper standardsMapper = null;
	DleseStandardsDocument standardsDocument = null;

	/**
	 *  Constructor for the DleseSuggestionServiceHelper object
	 *
	 * @param  sef             SchemEditForm instance
	 * @param  servletContext  ServletContext providing access to suggestion
	 *      service and asfToAdnMapper
	 */
	public DleseSuggestionServiceHelper(SchemEditForm sef, CATHelperPlugin frameworkPlugin) {
		super (sef, frameworkPlugin);
		try {
			ServletContext servletContext = sef.getServletContext();
			standardsMapper = (AsnToAdnMapper) servletContext.getAttribute("standardsMapper");
			if (standardsMapper == null)
				throw new Exception("Could not obtain \"standardsMapper\" from servletContext");
		} catch (Throwable e) {
			prtln("DleseSuggestionServiceHelper init ERROR: " + e.getMessage());
		}
		prtln("Instantiated");
	}

	public DleseStandardsManager getStandardsManager () {
		return (DleseStandardsManager)super.getStandardsManager();
	}

	/**
	 *  Constructor for the DleseSuggestionServiceHelper object
	 *
	 * @param  standardsMapper  NOT YET DOCUMENTED
	 */
	public DleseSuggestionServiceHelper(AsnToAdnMapper standardsMapper) {
		this(null, null);
		this.standardsMapper = standardsMapper;
	}
	
	public StandardsDocument getStandardsDocument () {
		if (this.standardsDocument == null) {
			this.standardsDocument = this.getStandardsManager().getStandardsDocument();
		}
		return this.standardsDocument;
	}

	public String getStandardsFormat () {
		return "dlese";
	}
	
	public AsnToAdnMapper getStandardsMapper () {
		return this.standardsMapper;
	}

	/**
	 *  Returns the AdnText corresponding to the provided standard's identifier
	 *  (via the standardsMapper instance).
	 *
	 * @param  std  NOT YET DOCUMENTED
	 * @return      The idFromCATStandard value
	 */
	protected String getIdFromCATStandard(CATStandard std) {
		return this.standardsMapper.getAdnText(std.getIdentifier());
	}


	/**
	 *  The main program for the DleseSuggestionServiceHelper class
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(String[] args) {

		String path = "/devel/ostwald/projects/schemedit-project/web/WEB-INF/data/Adn-to-Asf-mappings.xml";
		AsnToAdnMapper mapper = new AsnToAdnMapper(path);

		DleseSuggestionServiceHelper helper = new DleseSuggestionServiceHelper(mapper);
		String[] gr = {"DLESE:Primary elementary", "DLESE:Intermediate Elementary"};
		helper.setSelectedGradeRanges(gr);

		CATRequestConstraints c = new CATRequestConstraints();
/* 		c.setStartGrade(helper.getStartGrade());
		c.setEndGrade(helper.getEndGrade()); */
		prtln(c.toString());

		try {
			List options = helper.getGradeRangeOptions();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}



	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug)
			SchemEditUtils.prtln(s, "DleseSuggestionServiceHelper");
	}
}

