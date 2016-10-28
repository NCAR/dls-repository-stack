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

import edu.ucar.dls.schemedit.action.form.SchemEditForm;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.schemedit.*;

import edu.ucar.dls.schemedit.standards.CATServiceHelper;
import edu.ucar.dls.schemedit.standards.CATHelperPlugin;
import edu.ucar.dls.schemedit.standards.StandardsDocument;
import edu.ucar.dls.schemedit.standards.StandardsNode;

import edu.ucar.dls.serviceclients.cat.CATStandard;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.*;

import java.io.*;
import java.util.*;
import org.apache.struts.util.LabelValueBean;

import java.net.*;

/**
 *  Run-time support for CAT suggestion service, which acts as intermediary
 *  between CAT Service client and Form bean/JSP pages.<p>
 *
 *  The CAT service UI involves extraction of several values from the item
 *  record being edited for each framework, such as selected keywords, selected
 *  graderanges, etc. The functionality to extract these values is delegated to
 *  the framework-specific plug-in, which implments {@link CATHelperPlugin}.
 *
 * @author    ostwald
 */
public class TeachersDomainServiceHelper extends CATServiceHelper {
	private static boolean debug = true;


	/**
	 *  Constructor for the TeachersDomainServiceHelper object
	 *
	 * @param  sef              Description of the Parameter
	 * @param  frameworkPlugin  NOT YET DOCUMENTED
	 */
	// public TeachersDomainServiceHelper(SchemEditForm sef, CATHelperPlugin frameworkPlugin) {
	public TeachersDomainServiceHelper(SchemEditForm sef, CATHelperPlugin frameworkPlugin) {
		super (sef, frameworkPlugin);
		this.setServiceIsActive(false);
	}


	/**
	 *  Gets the standardsDocument attribute of the TeachersDomainServiceHelper object
	 *
	 * @return    The standardsDocument value
	 */
	 public TeachersDomainLexicon getStandardsDocument() {
		 return this.getStandardsManager().getStandardsDocument();
	 }


	/**
	 *  Gets the standardsFormat attribute of the TeachersDomainServiceHelper object
	 *
	 * @return    The standardsFormat value
	 */
	 public String getStandardsFormat() {
		 return "td_demo";
	 }


	/**
	 *  Gets the standardsManager attribute of the TeachersDomainServiceHelper object
	 *
	 * @return    The standardsManager value
	 */
	public TeachersDomainStandardsManager getStandardsManager() {
		if (this.getActionForm() == null)
			prtlnErr("getStandardsManager: actionForm is unavailable");
		else if (this.getActionForm().getFramework() == null)
			prtlnErr("getStandardsManager: framework is unavailable");
		return (TeachersDomainStandardsManager) this.getActionForm().getFramework().getStandardsManager();
	}


	/**
	 *  Gets the rootStandardNode attribute of the TeachersDomainServiceHelper object
	 *
	 * @return    The rootStandardNode value
	 */
	public StandardsNode getRootStandardNode() {
		return this.getStandardsDocument().getRootNode();
	}

	/**
	 *  Resolves author from the asnDocument (which it gets from the
	 *  StandardsDocument)
	 *
	 * @return    The author value
	 */
	public String getAuthor() {
		return this.getStandardsDocument().getAuthor();
	}

	protected String getIdFromCATStandard(CATStandard std) {
		return std.getIdentifier();
	}

	public List getAvailableDocs() {
		List docs = new ArrayList();
		docs.add (this.getStandardsDocument());
		return docs;
	}

	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "TeachersDomainServiceHelper");
		}
	}


	private static void prtlnErr(String s) {
		SchemEditUtils.prtln(s, "TeachersDomainServiceHelper");
	}

}

