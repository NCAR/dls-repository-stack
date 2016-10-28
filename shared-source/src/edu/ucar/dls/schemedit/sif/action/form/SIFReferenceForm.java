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

package edu.ucar.dls.schemedit.sif.action.form;

// import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.display.CollapseUtils;
import edu.ucar.dls.repository.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.*;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

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
// import java.io.*;
// import java.text.*;
// import java.net.*;
// import java.util.regex.*;

/**
 *
 *@author    ostwald 
 */
public class SIFReferenceForm extends ActionForm {

	private boolean debug = true;
	private String command = null;
	private String recId = null;
	private String newRecId = null;
	private String elementId = null;
	private String[] sifTypes = null;
	private String searchString = null;
	private Map objectMap = null;
	private HttpServletRequest request;
	private String selectedType = null;
	private List typeOptions = null;
	private String description = null;
	private String title = null;
	private Map setMap = null;
	private String collection = null;


	/* private SetInfo setInfo = null; */
	// input params
	/**
	 *  Constructor
	 */
	public SIFReferenceForm() { }


	/**
	 *  Description of the Method
	 */
	public void clear() {
		objectMap = null;
		command = null;
		recId = null;
		searchString = null;
		sifTypes = null;
		collection = null;
		selectedType = null;
		title = null;
	}

	public Map getObjectMap () {
		return this.objectMap;
	}
	
	public void setObjectMap (Map map) {
		this.objectMap = map;
	}
	
	public String getCommand () {
		return command;
	}
	
	public void setCommand (String cmd) {
		command = cmd;
	}
	
	/**
	 *  Gets the recId attribute of the SIFReferenceForm object
	 *
	 *@return    The recId value
	 */
	public String getRecId() {
		return recId;
	}

	/**
	 *  Sets the recId attribute of the SIFReferenceForm object
	 *
	 *@param  id  The new recId value
	 */
	public void setRecId(String id) {
		recId = id;
	}
	
	/**
	 *  Gets the newRecId attribute of the SIFReferenceForm object
	 *
	 *@return    The newRecId value
	 */
	public String getNewRecId() {
		return newRecId;
	}

	/**
	 *  Sets the newRecId attribute of the SIFReferenceForm object
	 *
	 *@param  id  The new newRecId value
	 */
	public void setNewRecId(String id) {
		newRecId = id;
	}

	public String getElementId () {
		return this.elementId;
	}
	
	public void setElementId (String id) {
		this.elementId = id;
	}
	
	public String getElementPath () {
		prtln ("getElementsPath: " + CollapseUtils.idToPath(this.elementId));
		return CollapseUtils.idToPath(this.elementId);
	}
	
	public String getRefTypeSelectId () {
		prtln ("getRefTypeSelectId: " + CollapseUtils.pathToId(this.getElementPath() + "/@SIF_RefObject"));
		return CollapseUtils.pathToId(this.getElementPath() + "/@SIF_RefObject");
	}
	
	public String [] getSifTypes () {
		return this.sifTypes;
	}
	
	public void setSifTypes (String [] types) {
		this.sifTypes = types;
	}

	public String getSearchString () {
		return this.searchString;
	}
	
	public void setSearchString (String s) {
		this.searchString = s;
	}
	
	public String getSelectedType () {
		return selectedType;
	}
	
	public void setSelectedType (String type) {
		selectedType = type;
	}
	
	public List getTypeOptions () {
		return this.typeOptions;
	}
	
	public void setTypeOptions (List options) {
		this.typeOptions = options;
	}
	
	public String getDescription () {
		return this.description;
	}
	
	public void setDescription (String s) {
		this.description = s;
	}
	
	public String getTitle () {
		return this.title;
	}
	
	public void setTitle (String s) {
		this.title = s;
	}
	
	public Map getSetMap () {
		return setMap;
	}
	
	public void setSetMap (Map map) {
		this.setMap = map;
	}
	
	public String getCollection () {
		return this.collection;
	}
	
	public void setCollection (String col) {
		this.collection = col;
	}
	
	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 *@param  s  The String that will be output.
	 */
	protected final void prtln(String s) {
		if (debug) {
			System.out.println("SIFReferenceForm: " + s);
		}
	}

}

