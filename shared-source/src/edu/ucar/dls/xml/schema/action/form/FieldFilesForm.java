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

package edu.ucar.dls.xml.schema.action.form;

import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.schema.action.GlobalDefReporter;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.serviceclients.remotesearch.RemoteResultDoc;

import edu.ucar.dls.schemedit.MetaDataFramework;
import edu.ucar.dls.schemedit.vocab.FieldFilesCheck;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Namespace;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.LabelValueBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.io.*;
import java.text.*;
import java.net.*;
import java.util.regex.*;

/**
 *  Controller for the SchemaViewer app.
 *
 *@author    ostwald
 */
public class FieldFilesForm extends ActionForm {

	private boolean debug = true;
	private SchemaHelper schemaHelper = null;
	private MetaDataFramework framework = null;

	private String xmlFormat = null;
	private List frameworks = null; // xmlFormats of loaded frameworks
	private List fieldsFrameworks = null; // xmlFormats of frameworks having fields info
	private List fieldsFiles = null;
	private FieldFilesCheck fieldFilesChecker = null;
	
	private String reportFunction = null;
	private String [] selectedFrameworks; // selected frameworks
	private Map report = null;
	private String directoryUri = null;
	private String sortListingBy = null;

	/**
	 *  Constructor
	 */
	public FieldFilesForm() {
		// path = "/itemRecord";
		// typeName = "geospatialCoverageType";
		// writer = getXMLWriter();
	}
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		prtln ("reset()");
		// selectedFrameworks = new String[]{};
		selectedFrameworks = null;
	}
	
	public List getFieldsFiles () {
		return this.fieldsFiles;
	}
	
	public void setFieldsFiles (List fields) {
		this.fieldsFiles = fields;
	}
	
	public String getSortListingBy () {
		if (this.sortListingBy == null)
			return "name";
		else
			return this.sortListingBy;
	}
	
	public void setSortListingBy (String sortBy) {
		this.sortListingBy = sortBy;
	}
	
	public FieldFilesCheck getFieldFilesChecker () {
		return this.fieldFilesChecker;
	}
	
	public void setFieldFilesChecker (FieldFilesCheck checker) {
		this.fieldFilesChecker = checker;
	}
	
	/**
	 *  Gets the path attribute of the SchemaViewerForm object
	 *
	 *@return    The path value
	 */
	public String getDirectoryUri() {
		return directoryUri;
	}


	/**
	 *  Sets the directoryUri attribute of the SchemaViewerForm object
	 *
	 *@param  directoryUri  The new directoryUri value
	 */
	public void setDirectoryUri(String directoryUri) {
		this.directoryUri = directoryUri;
	}
	
	/**
	 *  Gets list of xmlFormats for all loaded frameworks
	 *
	 *@return    The frameworks value
	 */
	public List getFrameworks() {
		if (frameworks == null)
			frameworks = new ArrayList();
		return frameworks;
	}
	/**
	 *  Sets the frameworks attribute of the FieldFilesForm object
	 *
	 *@param  list  The new frameworks value
	 */
	public void setFrameworks(List list) {
		frameworks = list;
	}
	
	/**
	 *  Gets list of xmlFormats for all loaded frameworks having fields files
	 *
	 *@return    The frameworks value
	 */
	public List getFieldsFrameworks() {
		if (fieldsFrameworks == null)
			fieldsFrameworks = new ArrayList();
		return fieldsFrameworks;
	}
	
	/**
	 *  Sets the fieldsFrameworks attribute of the FieldFilesForm object
	 *
	 *@param  list  The new fieldsFrameworks value
	 */
	public void setFieldsFrameworks(List list) {
		fieldsFrameworks = list;
	}

	
	
	public String [] getSelectedFrameworks () {
		return selectedFrameworks;
	}
	
	public void setSelectedFrameworks (String [] sf) {
		selectedFrameworks = sf;
	}


	/**
	 *  Gets the globalDef attribute of the FieldFilesForm object
	 *
	 *@return    The globalDef value
	 */
	public MetaDataFramework getFramework() {
		return framework;
	}


	/**
	 *  Sets the framework attribute of the FieldFilesForm object
	 *
	 *@param  mdf  The new framework value
	 */
	public void setFramework(MetaDataFramework mdf) {
		framework = mdf;
	}

	/**
	 *  Sets the schemaHelper attribute of the FieldFilesForm object
	 *
	 *@param  schemaHelper  The new schemaHelper value
	 */
	public void setSchemaHelper(SchemaHelper schemaHelper) {
		this.schemaHelper = schemaHelper;
	}


	/**
	 *  Gets the schemaHelper attribute of the FieldFilesForm object
	 *
	 *@return    The schemaHelper value
	 */
	public SchemaHelper getSchemaHelper() {
		return schemaHelper;
	}

	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 *@param  s  The String that will be output.
	 */
	protected final void prtln(String s) {
		if (debug) {
			System.out.println("FieldFilesForm: " + s);
		}
	}

}

