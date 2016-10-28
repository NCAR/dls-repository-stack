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

package edu.ucar.dls.schemedit.action.form;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.dcs.*;
import edu.ucar.dls.repository.*;
import edu.ucar.dls.index.ResultDoc;
import edu.ucar.dls.index.reader.*;
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
import java.io.*;
import java.text.*;
import java.net.*;
import java.util.regex.*;

/**
 *  ActionForm bean for handling batchRecordOperations.<p>
 *
 *  Works in conjunction with {@link edu.ucar.dls.schemedit.action.BatchOperationsAction}.
 *
 */
public class BatchOperationsForm extends ActionForm {

	private boolean debug = true;
	private HttpServletRequest request;

	private List sets = null;
	/* limites the number of records listed in UI */
	private int MAX_RECORDS_TO_LIST = 100; 

	/**
	 *  Constructor
	 */
	public BatchOperationsForm() { }

	public void clear() {
		this.setRecordList(null);
		this.clearStatusAttributes();
		this.setFailedRecordList(null);
	}
	
	private RecordList recordList = null;


	/**
	 *  Sets the records to be operated on in the batch operation
	 *
	 *@param  results  The new recordList value
	 */
	public void setRecordList(RecordList results) {
		recordList = results;
	}


	/**
	 *  Gets the records to be operated on in the batch operation
	 *
	 *@return    The recordList value
	 */
	public RecordList getRecordList() {
		if (recordList == null)
			recordList = new RecordList();
		return recordList;
	}


	private RecordList failedRecordList = null;


	/**
	 *  Records that failed batch op
	 *
	 *@param  results  The new failedRecordList value
	 */
	public void setFailedRecordList(RecordList results) {
		failedRecordList = results;
	}


	/**
	 *  Records that failed batch op
	 *
	 *@return    The failedRecordList value
	 */
	public RecordList getFailedRecordList() {
		return failedRecordList;
	}

	public int getMaxRecordsToList () {
		return MAX_RECORDS_TO_LIST;
	}

	/* ----------------------------------------------
		Status Entry Attributes
	*/
	private String editor = null;
	private String status = null;
	private String statusNote = null;


	/**
	 *  Clears the status attributes (editor, status, statusNote) for batch status change.
	 */
	public void clearStatusAttributes() {
		editor = null;
		status = null;
		statusNote = null;
	}


	/**
	 *  Gets the editor attribute for batch status change.
	 *
	 *@return    The editor value
	 */
	public String getEditor() {
		return editor;
	}


	/**
	 *  Sets the editor attribute for batch status change.
	 *
	 *@param  editor  The new editor value
	 */
	public void setEditor(String editor) {
		this.editor = editor;
	}


	/**
	 *  Gets the status attribute for batch status change
	 *
	 *@return    The status value
	 */
	public String getStatus() {
		return status;
	}


	/**
	 *  Sets the status attribute for batch status change.
	 *
	 *@param  status  The new status value
	 */
	public void setStatus(String status) {
		this.status = status;
	}


	/**
	 *  Geets the statusNote attribute for batch status change.
	 *
	 *@return    The statusNote value
	 */
	public String getStatusNote() {
		return statusNote;
	}


	/**
	 *  Sets the statusNote attribute for batch status change.
	 *
	 *@param  statusNote  The new statusNote value
	 */
	public void setStatusNote(String statusNote) {
		this.statusNote = statusNote;
	}


	// end of Status Entry Attributes

	private String editRec = "";


	/**
	 *  editRec parameter is used by handleMoveRecord to specify whether control is
	 *  forwarded back to editor. Seems there should be an easier way ...
	 *
	 *@return    The editRec value
	 */
	public String getEditRec() {
		return editRec;
	}


	/**
	 *  Sets the editRec attribute of the BatchOperationsForm object
	 *
	 *@param  s  The new editRec value
	 */
	public void setEditRec(String s) {
		editRec = s;
	}


	private List statusFlags = null;


	/**
	 *  A list of {@link edu.ucar.dls.schemedit.dcs StatusFlag} beans, each of
	 *  contain status and description attributes.
	 *
	 *@return    The statusFlags value
	 */
	public List getStatusFlags() {
		return statusFlags;
	}


	/**
	 *  Sets the statusFlags attribute of the BatchOperationsForm object
	 *
	 *@param  flags  The new statusFlags value
	 */
	public void setStatusFlags(List flags) {
		this.statusFlags = flags;
	}


	/**
	 *  Gets the sets attribute of the BatchOperationsForm object
	 *
	 *@return    The sets value
	 */
	public List getSets() {
		if (sets == null) {
			return new ArrayList();
		}
		return sets;
	}


	/**
	 *  Sets the sets attribute of the RepositoryAdminForm object
	 *
	 *@param  sets  The new sets value
	 */
	public void setSets(List sets) {
		this.sets = sets;
	}


	private String collection;


	/**
	 *  Gets the collection attribute of the BatchOperationsForm object
	 *
	 *@return    The collection value
	 */
	public String getCollection() {
		return collection;
	}


	/**
	 *  Sets the collection attribute of the BatchOperationsForm object
	 *
	 *@param  collection  The new collection value
	 */
	public void setCollection(String collection) {
		this.collection = collection;
	}


	private DcsSetInfo setInfo = null;


	/**
	 *  Gets the dcsSetInfo attribute of the BatchOperationsForm object
	 *
	 *@return    The dcsSetInfo value
	 */
	public DcsSetInfo getDcsSetInfo() {
		return setInfo;
	}


	/**
	 *  Sets the dcsSetInfo attribute of the BatchOperationsForm object
	 *
	 *@param  setInfo  The new dcsSetInfo value
	 */
	public void setDcsSetInfo(DcsSetInfo setInfo) {
		this.setInfo = setInfo;
	}

	// ------------------------------

	private String formatOfRecords = "adn";


	/**
	 *  Gets the formatOfRecords attribute of the BatchOperationsForm object
	 *
	 *@return    The formatOfRecords value
	 */
	public String getFormatOfRecords() {
		return formatOfRecords;
	}


	/**
	 *  Sets the formatOfRecords attribute of the BatchOperationsForm object
	 *
	 *@param  format  The new formatOfRecords value
	 */
	public void setFormatOfRecords(String format) {
		formatOfRecords = format;
	}



	/**
	 *  Sets the request attribute of the BatchOperationsForm object.
	 *
	 *@param  request  The new request value
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 *@param  s  The String that will be output.
	 */
	protected final void prtln(String s) {
		if (debug) {
			System.out.println("BatchOperationsForm: " + s);
		}
	}

}

