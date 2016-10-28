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

package edu.ucar.dls.services.dcs.action;

import edu.ucar.dls.services.dcs.PutRecordData;
import edu.ucar.dls.services.dcs.CollectionIndexingObserver;
import edu.ucar.dls.services.dcs.action.form.DCSServicesForm;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.repository.RepositoryManager;
import edu.ucar.dls.repository.RecordUpdateException;
import edu.ucar.dls.index.reader.*;
import edu.ucar.dls.index.SimpleLuceneIndex;
import edu.ucar.dls.index.ResultDoc;
import edu.ucar.dls.index.ResultDocList;
import edu.ucar.dls.vocab.MetadataVocab;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.repository.RepositoryService;
import edu.ucar.dls.schemedit.repository.RepositoryUtils;
import edu.ucar.dls.schemedit.threadedservices.ExportingService;
import edu.ucar.dls.schemedit.threadedservices.ValidatingService;
import edu.ucar.dls.schemedit.dcs.*;
import edu.ucar.dls.schemedit.config.*;

import java.util.*;
import java.text.*;
import java.io.*;

import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;

import org.dom4j.*;

/**
 *  An <strong>Action</strong> that handles DCS related web service requests.
 <ul>
 <li>GetId</li>
 <li>PutRecord</li>
 <li>DeleteRecord</li>
 <li>UpdateStatus</li>
 <li>ExportCollection</li>
 <li>ValidateCollection</li>
 <li>UrlCheck</li>
 <li>IngestCollection</li>
 <li>UpdateStatus</li>
 
 *
 * @author    Jonathan Ostwald
 */
public final class DCSServicesAction extends Action {
	private static boolean debug = true;

	private final static int MAX_SEARCH_RESULTS = 500;

	public final static String GET_ID_VERB = "GetId";
	public final static String PUT_RECORD_VERB = "PutRecord";
	public final static String DELETE_RECORD_VERB = "DeleteRecord";
	public final static String UPDATE_STATUS_VERB = "UpdateStatus";
	public final static String EXPORT_COLLECTION_VERB = "ExportCollection";
	public final static String VALIDATE_COLLECTION_VERB = "ValidateCollection";
	public final static String URL_CHECK_VERB = "UrlCheck";
	public final static String INGEST_COLLECTION_VERB = "IngestCollection";

	private FrameworkRegistry frameworkRegistry = null;
	private CollectionRegistry collectionRegistry = null;
	private RepositoryService repositoryService = null;


	// --------------------------------------------------------- Public Methods

	/**
	 *  Processes the DDS web service request by forwarding to the appropriate
	 *  corresponding JSP page for rendering.
	 *
	 * @param  mapping        The ActionMapping used to select this instance
	 * @param  request        The HTTP request we are processing
	 * @param  response       The HTTP response we are creating
	 * @param  form           The ActionForm for the given page
	 * @return                The ActionForward instance describing where and how
	 *      control should be forwarded
	 * @exception  Exception  If error.
	 */
	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			 throws Exception {
		
		/*
		 *  Design note:
		 *  Only one instance of this class gets created for the app and shared by
		 *  all threads. To be thread-safe, use only local variables, not instance
		 *  variables (the JVM will handle these properly using the stack). Pass
		 *  all variables via method signatures rather than instance vars.
		 */
		DCSServicesForm dcssf = null;
		try {

			dcssf = (DCSServicesForm) form;
			dcssf.setVocab( (MetadataVocab) servlet.getServletContext().getAttribute("MetadataVocab") );

			RepositoryManager rm =
					(RepositoryManager) servlet.getServletContext().getAttribute("repositoryManager");
					
			frameworkRegistry = (FrameworkRegistry) servlet.getServletContext().getAttribute("frameworkRegistry");
			if (frameworkRegistry == null)
				throw new ServletException ("attribute \"frameworkRegistry\" not found in servlet context");
					
			collectionRegistry = (CollectionRegistry) servlet.getServletContext().getAttribute("collectionRegistry");
			if (collectionRegistry == null)
				throw new ServletException ("attribute \"collectionRegistry\" not found in servlet context");
			
			repositoryService = (RepositoryService) servlet.getServletContext().getAttribute("repositoryService");
			if (repositoryService == null)
				throw new Exception ("attribute \"collectionRegistry\" not found in servlet context");

			// only trusted ips may perform these operations
			boolean doAuthorize = true;
			if (doAuthorize) {
				if (isAuthorized(request, "trustedIp", rm)) {
					dcssf.setAuthorizedFor("trustedUser");
				}
				else {
					dcssf.setErrorMsg("You are not authorized to use this service");
					return mapping.findForward("dcsservices.error");
				}
			}
			
			// Grab the DDS service request verb:
			String verb = request.getParameter("verb");
			if (verb == null) {
				dcssf.setErrorMsg("The verb argument is required. Please indicate the request verb");
				return (mapping.findForward("dcsservices.error"));
			}

			// Handle get id request:
			else if (verb.equals(GET_ID_VERB)) {
				return doGetId(request, response, rm, dcssf, mapping);
			}
			// Handle put record request:
			else if (verb.equals(PUT_RECORD_VERB)) {
				return doPutRecord(request, response, rm, dcssf, mapping);
			}
			else if (verb.equals(DELETE_RECORD_VERB)) {
				prtln ("execute2: " + request.getParameter("dcsStatusNote"));
				return doDeleteRecord(request, response, rm, dcssf, mapping);
			}			
			// Handle update status request:
			else if (verb.equals(UPDATE_STATUS_VERB)) {
				return doUpdateStatus(request, response, rm, dcssf, mapping);
			}	
			// Handle export request:
			else if (verb.equals(EXPORT_COLLECTION_VERB)) {
				return doExportCollection(request, response, rm, dcssf, mapping);
			}
			// Handle validate request:
			else if (verb.equals(VALIDATE_COLLECTION_VERB)) {
				return doValidateCollection(request, response, rm, dcssf, mapping);
			}
			// Handle urlCheck request:
			else if (verb.equals(URL_CHECK_VERB)) {
				return doUrlCheck(request, response, rm, dcssf, mapping);
			}
			// Handle ingestCollection request:
			else if (verb.equals(INGEST_COLLECTION_VERB)) {
				return doIngestCollection(request, response, rm, dcssf, mapping);
			}
			// The verb is not valid for the DDS web service
			else {
				dcssf.setErrorMsg("The verb argument '" + verb + "' is not valid");
				return (mapping.findForward("dcsservices.error"));
			}
		} catch (NullPointerException npe) {
			prtlnErr("DCSServicesAction caught exception. " + npe);
			npe.printStackTrace();
			dcssf.setErrorMsg("There was an internal error by the server: " + npe);
			return (mapping.findForward("dcsservices.error"));
		} catch (Throwable e) {
			prtlnErr("DCSServicesAction caught exception. " + e);
			e.printStackTrace();
			dcssf.setErrorMsg("There was an internal error by the server: " + e);
			return (mapping.findForward("dcsservices.error"));
		}
	}

	private SessionBean getSessionBean (HttpServletRequest request) throws Exception {
		SessionRegistry sessionRegistry = (SessionRegistry)servlet.getServletContext().getAttribute("sessionRegistry");
		if (sessionRegistry == null)
			throw new Exception ("sessionRegistry not found in servletContext");
		SessionBean sessionBean = sessionRegistry.getSessionBean(request);
		if (sessionBean == null)
			throw new Exception ("sessionBean not found in servletContext");
		return sessionBean;
	}
	
	/**
	 *  Handles a request to generate an id for a specified collection. <p>
	 *
	 *  Arguments: collection key.<p>
	 *
	 *  Error Exception Conditions: <br>
	 *  badArgument - The request includes illegal arguments.
	 *
	 * @param  request        The HTTP request
	 * @param  response       The HTTP response
	 * @param  rm             The RepositoryManager used
	 * @param  dcssf             The bean
	 * @param  mapping        ActionMapping used
	 * @return                An ActionForward to the JSP page that will handle the
	 *      response
	 * @exception  Exception  If error.
	 */
	protected ActionForward doGetId(
			HttpServletRequest request,
			HttpServletResponse response,
			RepositoryManager rm,
			DCSServicesForm dcssf,
			ActionMapping mapping)
			 throws Exception {

		String collectionParam = request.getParameter("collection");
		if (collectionParam == null || collectionParam.length() == 0) {
			dcssf.setErrorMsg("The collection argument is required but is missing or empty");
			return mapping.findForward("dcsservices.error");
		}
		
		CollectionConfig collectionConfig = collectionRegistry.getCollectionConfig(collectionParam, false);
		if (collectionConfig == null) {
			dcssf.setErrorMsg("collection '" + collectionParam + "' not recognized");
			return mapping.findForward("dcsservices.error");
		}
		
		String id;
		try {
			id = collectionConfig.nextID();
		} catch (Throwable t) {
			if (t.getMessage() != null)
				dcssf.setErrorMsg("Unable to generate ID: " + t.getMessage());
			else
				dcssf.setErrorMsg("Unable to generate ID");
			return mapping.findForward("dcsservices.error");
		}
		

		dcssf.setId(id);
		return mapping.findForward("dcsservices.GetId");
	}

	protected ActionForward doUrlCheck(
			HttpServletRequest request,
			HttpServletResponse response,
			RepositoryManager rm,
			DCSServicesForm dcssf,
			ActionMapping mapping)
			 throws Exception {

		String url = request.getParameter("url");
		if (url == null || url.trim().length() == 0) {
			dcssf.setErrorMsg("You must supply a url parameter for the UrlCheck request.");
			return mapping.findForward("dcsservices.error");
		}
		url = url.trim();
		dcssf.setUrl(url);
	
		String [] collections = request.getParameterValues("collection");
		dcssf.setCollections (collections);
	
		// search for the url in either the dcsurlenc or urlenc fields
 		String q = "(dcsurlenc:" + SimpleLuceneIndex.encodeToTerm(url, false);
		q += " OR urlenc:" + SimpleLuceneIndex.encodeToTerm(url, false) + ")";
		
		if (collections != null && collections.length > 0) {
			q += " AND (collection:0"+collections[0];
			for (int i=1;i<collections.length;i++) {
				q += " OR collection:0" + collections[i];
			}
			q += ")";
		}

		SimpleLuceneIndex index = rm.getIndex();
		ResultDocList resultDocs = index.searchDocs(q);
		
		prtln("doUrlCheck() search for: '" + q + "' had " + (resultDocs == null ? -1 : resultDocs.size()) + " resultDocs");

		if (resultDocs == null || resultDocs.size() == 0) {
			dcssf.setResults(null);
		}
		else {
			dcssf.setResults(resultDocs);
		}

		return (mapping.findForward("dcsservices.UrlCheck"));

	}
		

	
	/**
	 *  Handles a request to put a metadata record into the repository. Wraps
	 {@link edu.ucar.dls.repository.RepositoryManager}.putRecord and therefore requires
	 the same arguments.<p>
	 Currently allows existing records to be overwritten. <p>
	 *
	 *  Arguments: recordXml, xmlFormat, collection, and id.<p>
	 *
	 *  Error Exception Conditions: <br>
	 *  badArgument - The request includes illegal arguments.
	 *
	 * @param  request        The HTTP request
	 * @param  response       The HTTP response
	 * @param  rm             The RepositoryManager used
	 * @param  dcssf             The Form bean
	 * @param  mapping        ActionMapping used
	 * @return                An ActionForward to the JSP page that will handle the
	 *      response
	 * @exception  Exception  If error.
	 */
	protected ActionForward doPutRecord(
			HttpServletRequest request,
			HttpServletResponse response,
			RepositoryManager rm,
			DCSServicesForm dcssf,
			ActionMapping mapping)
			 throws Exception {

		prtln ("doPutRecord()");				 

		List errorList = new ArrayList ();
		
		// validate presence of required arguments
		String recordXmlParam = request.getParameter("recordXml");
		if (recordXmlParam == null || recordXmlParam.length() == 0) {
			errorList.add ("The \'recordXml\' argument is required but is missing or empty");
		}
		String xmlFormatParam = request.getParameter("xmlFormat");
		if (xmlFormatParam == null || xmlFormatParam.length() == 0) {
			errorList.add ("The \'xmlFormat\' argument is required but is missing or empty");
		}
		String collectionParam = request.getParameter("collection");
		if (collectionParam == null || collectionParam.length() == 0) {
			errorList.add ("The \'collection\' argument is required but is missing or empty");
		}		
		String idParam = request.getParameter("id");
		if (idParam == null || idParam.length() == 0) {
			errorList.add ("The \'id\' argument is required but is missing or empty");
		}

		if (errorList.size() > 0) {
			dcssf.setErrorList(errorList);
			return  mapping.findForward("dcsservices.error");
		}
		
		prtln ("params validated");
		
		PutRecordData prData = new PutRecordData();
		prData.init(recordXmlParam, xmlFormatParam, collectionParam, frameworkRegistry);
		
		// validate against the requirements of the index - NOT the metadata schema!
		errorList = validateRecordXml (prData);
		if (errorList.size() > 0) {
			dcssf.setErrorList(errorList);
			return  mapping.findForward("dcsservices.error");
		}
		
		// Status stuff
		String statusParam = getTrimmedValueOrNull (request.getParameter("dcsStatus"));
		String statusNoteParam = getTrimmedValueOrNull (request.getParameter("dcsStatusNote"));
		String dcsAgentParam = getTrimmedValueOrNull (request.getParameter("dcsAgent"));

		boolean recordExists = false;
		String fileName = prData.getId() + ".xml";
		try {
			XMLDocReader docReader = RepositoryUtils.getXMLDocReader(prData.getId(), rm);
			if (docReader != null) {
				recordExists = true;
				fileName = docReader.getFileName();
				prtln ("file exists for this record: " + fileName);
			}
		} catch (Throwable t) {}
		
		try {
		
			/* If the record does NOT yet exist, 
				if status and statusNotes not supplied, use defaults
				call repositoryServices.saveNewRecord
			*/
			if (!recordExists) {
				String dcsStatus = (statusParam != null ? 
									statusParam : StatusFlags.IMPORTED_STATUS);
				String dcsStatusNote = (statusNoteParam != null ? 
									statusNoteParam : "imported via DCS PutRecord Service");
				String dcsLastEditor = (dcsAgentParam != null ? dcsAgentParam :  Constants.ADMIN_USER);
												  
				prtln ("dcsLastEditor: " + dcsLastEditor);
												  
				repositoryService.saveNewRecord (prData.getId(), 
												  recordXmlParam, 		
												  collectionParam,
												  dcsLastEditor, // username
												  dcsStatus,
												  dcsStatusNote);
			}
			/*
			   if the record DOES exist 
				   we have to obtain a lock
					only update status and statusNote if they are explicitly supplied
					call repositoryServices.saveEditedRecord
					update status manually (now that an indexed record exists)
			*/
			else {
				String recId = prData.getId();
				SessionBean sessionBean = null;
				try {
					sessionBean = this.getSessionBean(request);
				} catch (Exception e) {
					dcssf.setErrorMsg("The server encountered a problem: " + e.getMessage());
					// dcssf.setErrorCode(DCSServicesForm.ERROR_CODE_INTERNALSERVERERROR);
					return mapping.findForward("dcsservices.error");
				}
				
				// Get lock for this record
				if (!sessionBean.getLock(idParam)) {
					
					// Handle lock not obtained error
					dcssf.setErrorMsg("Record " + idParam + " is busy and cannot be updated at this time");
					// dcssf.setErrorCode(DCSServicesForm.ERROR_CODE_BADARGUMENT);
					return mapping.findForward("dcsservices.error");
				}
				
				try {
					repositoryService.saveEditedRecord (recId,prData.getDocument(), null);
					
					// if only a note is supplied, then we don't want the status tag to change!
					if (statusParam == null) {
						// reuse the current status
						statusParam = repositoryService.getDcsDataRecord(idParam).getStatus();
					}
					String dcsLastEditor = (dcsAgentParam != null ? dcsAgentParam :  Constants.ADMIN_USER);
					StatusEntry statusEntry = new StatusEntry (statusParam, statusNoteParam, dcsLastEditor);
					repositoryService.updateRecordStatus(recId, statusEntry);
					
					sessionBean.releaseLock(idParam);
				} catch (Exception e) {
					sessionBean.releaseLock(idParam);
					throw new Exception (e);
				} 
			}
	
			prtln ("putRecord has succeeded and is returning");
			return mapping.findForward("dcsservices.PutRecord");
		} catch (RecordUpdateException e) {
			dcssf.setErrorMsg(e.getMessage());
			return mapping.findForward("dcsservices.error");
		}
	}

	/**
	 *  Handles a request to delete a metadata record into a DDS repository.<p>
	 *
	 *
	 * @param  request        The HTTP request
	 * @param  response       The HTTP response
	 * @param  rm             The RepositoryManager used
	 * @param  dcssf          The Form bean
	 * @param  mapping        ActionMapping used
	 * @return                An ActionForward to the JSP page that will handle the response
	 * @exception  Exception  If error.
	 */
	protected ActionForward doDeleteRecord(
	                                    HttpServletRequest request,
	                                    HttpServletResponse response,
	                                    RepositoryManager repositoryManager,
	                                    DCSServicesForm dcssf,
	                                    ActionMapping mapping)
		 throws Exception {

		prtln("doDeleteRecord()");

		String errorMsg = "";
		SessionBean sessionBean = null;
		try {
			sessionBean = this.getSessionBean(request);
		} catch (Exception e) {
			dcssf.setErrorMsg("The server encountered a problem: " + e.getMessage());
			// dcssf.setErrorCode(DCSServicesForm.ERROR_CODE_INTERNALSERVERERROR);
			return mapping.findForward("dcsservices.error");
		}
			

		// validate presence of required arguments
		String idParam = request.getParameter("id");
		if (idParam == null || idParam.length() == 0) {
			errorMsg += "The \'id\' argument is required but is missing or empty. ";
		}
		
		if (errorMsg.length() > 0) {
			dcssf.setErrorMsg(errorMsg);
			// dcssf.setErrorCode(DCSServicesForm.ERROR_CODE_BADARGUMENT);
			return mapping.findForward("dcsservices.error");
		}

		// Get lock for this record
		if (!sessionBean.getLock(idParam)) {
			// Handle lock not obtained error
			dcssf.setErrorMsg("Record " + idParam + " is busy and cannot be deleted at this time");
			// dcssf.setErrorCode(DCSServicesForm.ERROR_CODE_BADARGUMENT);
			return mapping.findForward("dcsservices.error");
		}

		prtln("params validated");
		String forwardName = null;
		try { 
			// boolean recordExisted = repositoryManager.deleteRecord(idParam);
			this.repositoryService.deleteRecord(idParam);
			dcssf.setResultCode(DCSServicesForm.RESULT_CODE_SUCCESS);
			dcssf.setId(idParam);

			prtln("doDeleteRecord has succeeded and is returning");
			forwardName = "dcsservices.DeleteRecord";
		} catch (Exception e) {
			dcssf.setResultCode(DCSServicesForm.RESULT_CODE_NO_SUCH_RECORD);
			if(e instanceof NullPointerException) {
				e.printStackTrace();
				dcssf.setErrorMsg("The server encountered a problem: Null pointer exception.");
				// dcssf.setErrorCode(DCSServicesForm.ERROR_CODE_INTERNALSERVERERROR);				
			}
			else {
				prtln (e.getMessage());
				dcssf.setErrorMsg("Record not found for id: " + idParam);
				// dcssf.setErrorCode(DCSServicesForm.ERROR_CODE_ILLEGAL_OPERATION);
			}
			// return mapping.findForward("dcsservices.error");
			forwardName = "dcsservices.error";
		} finally {
			sessionBean.releaseLock(idParam);
		}
		
		return mapping.findForward(forwardName);
	}

	
	/**
	 *  Handles a request to put a metadata record into the repository. Wraps
	 {@link edu.ucar.dls.repository.RepositoryManager}.putRecord and therefore requires
	 the same arguments.<p>
	 Currently allows existing records to be overwritten. <p>
	 *
	 *  Arguments: id, dcsStatus, dcsStatusNode (optional), and dcsStatusEditor(optional).<p>
	 *
	 *  Error Exception Conditions: <br>
	 *  badArgument - The request includes illegal or missing arguments.
	 *
	 * @param  request        The HTTP request
	 * @param  response       The HTTP response
	 * @param  rm             The RepositoryManager used
	 * @param  dcssf             The Form bean
	 * @param  mapping        ActionMapping used
	 * @return                An ActionForward to the JSP page that will handle the
	 *      response
	 * @exception  Exception  If error.
	 */
	protected ActionForward doUpdateStatus(
			HttpServletRequest request,
			HttpServletResponse response,
			RepositoryManager rm,
			DCSServicesForm dcssf,
			ActionMapping mapping)
			 throws Exception {

		prtln ("doUpdateStatus()");				 

		List errorList = new ArrayList ();
		
		// validate presence of required arguments
		
		String idParam = request.getParameter("id");
		if (idParam == null || idParam.length() == 0) {
			errorList.add ("The \'id\' argument is required but is missing or empty");
		}
		
		// check to see that the record exists
		try {
			repositoryService.getXMLDocReader(idParam);
		} catch (Exception e) {
			errorList.add ("Record not found for id=\'" + idParam + "\'");
		}

		String dcsStatus = null;
		String statusParam = request.getParameter("dcsStatus");
		if (statusParam != null && statusParam.trim().length() > 0) {
			dcsStatus = statusParam.trim();
		}
		else {
			errorList.add ("The \'dcsStatus\' argument is required but is missing or empty");
		}
		
		// statusNote is optional
		String dcsStatusNote = "";
		String statusNoteParam = request.getParameter("dcsStatusNote");
		if (statusNoteParam != null && statusNoteParam.length() > 0) {
			dcsStatusNote = statusNoteParam;
		}
	
		// statusEditor is optional
		String dcsStatusEditor = Constants.UNKNOWN_EDITOR;
		String statusEditorParam = request.getParameter("dcsStatusEditor");
		if (statusEditorParam != null && statusEditorParam.length() > 0) {
			dcsStatusEditor = statusEditorParam;
		}
		
		if (errorList.size() > 0) {
			dcssf.setErrorList(errorList);
			return  mapping.findForward("dcsservices.error");
		}
		
		prtln ("params validated");
		
		// make a status entry
		StatusEntry statusEntry = new StatusEntry (dcsStatus, dcsStatusNote, dcsStatusEditor);
		
		try {
			repositoryService.updateRecordStatus(idParam, statusEntry);
			dcssf.setStatusEntry(statusEntry.getElement().asXML());
			prtln ("doUpdateStatus has succeeded and is returning");
			return mapping.findForward("dcsservices.UpdateStatus");
		} catch (RecordUpdateException e) {
			dcssf.setErrorMsg(e.getMessage());
			return mapping.findForward("dcsservices.error");
		}
	}

	
	protected ActionForward doExportCollection(
			HttpServletRequest request,
			HttpServletResponse response,
			RepositoryManager rm,
			DCSServicesForm dcssf,
			ActionMapping mapping)
			 throws Exception {

		prtln ("doExportCollection()");				 

		CollectionRegistry collectionRegistry = 
			(CollectionRegistry) servlet.getServletContext().getAttribute("collectionRegistry");
		if (collectionRegistry == null) {
			dcssf.setErrorMsg("Server Error: \"collectionRegistry\" not found in servlet context");
			return mapping.findForward("dcsservices.error");
		}	
			
		ExportingService exportingService = (ExportingService) servlet.getServletContext().getAttribute ("exportingService");
		if (exportingService == null) {
			dcssf.setErrorMsg("Server Error: \"exportingService\" not found in servlet context");
			return mapping.findForward("dcsservices.error");
		}
		
		if (exportingService.getIsProcessing()) {
			String id = exportingService.getSessionId();
			DcsSetInfo set = exportingService.getExportingSetInfo();
			dcssf.setErrorMsg("Another session is currently performing an export. Please try again later.");
			return mapping.findForward("dcsservices.error");
		}
		
		
		List errorList = new ArrayList ();
		CollectionConfig collectionConfig = null;
		File destDir = null;
		
		// validate presence of required arguments
		String collectionParam = request.getParameter("collection");
		if (collectionParam == null || collectionParam.length() == 0) {
			errorList.add ("The \'collection\' argument is required but is missing or empty");
		}
		else {
			collectionConfig = collectionRegistry.getCollectionConfig(collectionParam, false);
			if (collectionConfig == null) {
				errorList.add ("No collection found for \"" + collectionParam + "\"");
			}
		}
		
		if (collectionConfig != null) {
			// exportDir is not required. it defaults to configured exportDir for this collection
			String relativeExportDest = collectionConfig.getExportDirectory();
			
			String exportDirParam = request.getParameter("exportDir");
			if (exportDirParam != null && exportDirParam.length() > 0) {
				prtln ("exportDirParam provided (" + exportDirParam + ") overriding default (" + relativeExportDest + ")");
				relativeExportDest = exportDirParam;
			}

			try {
				String exportBaseDir = exportingService.getExportBaseDir();
				destDir = ExportingService.validateExportDestination (exportBaseDir, relativeExportDest);
			} catch (Exception e) {
				errorList.add(e.getMessage());
			}
		}
		
		if (errorList.size() > 0) {
			dcssf.setErrorList(errorList);
			return  mapping.findForward("dcsservices.error");
		}
		
		prtln ("params validated");

		// status values are not required. The default is __final__
		String [] statusParams = request.getParameterValues("statuses");
		String [] statusValues = getStatusValues (statusParams, collectionConfig);
		
		DcsSetInfo setInfo = RepositoryUtils.getDcsSetInfo(collectionConfig.getId(), rm);

		prtln ("about to call exportingService.exportRecords()");
		prtln ("\t" + "destDir: " + destDir);
		prtln ("\t" + "statusValues");
		for (int i=0;i<statusValues.length;i++)
			prtln ("\t\t" + statusValues[i]);
		if (setInfo == null)
			prtln ("\t" + "setInfo is NULL!");
		
		dcssf.setExportDir (destDir.getAbsolutePath());
		dcssf.setStatusLabels(getStatusLabels (statusParams, collectionConfig));
		dcssf.setStatuses(statusParams);
		
		try {

			exportingService.exportRecords (destDir, setInfo, statusValues, null);
			
		} catch (Throwable t) {
			String errorMsg = "exportingService.exportRecords error: " + t.getMessage();
			prtlnErr (errorMsg);
			t.printStackTrace();
			throw new Exception (errorMsg);
		}

		return mapping.findForward("dcsservices.ExportCollection");
	}
	
	/**
	* EXPERIMENTAL and (and not complete or tested) action for SMILE use case: index and validate an collection after
	* physically populating collection metedata on disk - NOT CURRENTLY USED.
	*/
	protected ActionForward doIngestCollection(
			HttpServletRequest request,
			HttpServletResponse response,
			RepositoryManager rm,
			DCSServicesForm dcssf,
			ActionMapping mapping)
			 throws Exception {

		prtln ("doIngestCollection()");				 

		ServletContext servletContext = servlet.getServletContext();
		
		CollectionRegistry collectionRegistry = 
			(CollectionRegistry) servletContext.getAttribute("collectionRegistry");
		if (collectionRegistry == null) {
			dcssf.setErrorMsg("Server Error: \"collectionRegistry\" not found in servlet context");
			return mapping.findForward("dcsservices.error");
		}	
			
		ValidatingService validatingService = (ValidatingService) servletContext.getAttribute ("validatingService");
		if (validatingService == null) {
			dcssf.setErrorMsg("Server Error: \"validatingService\" not found in servlet context");
			return mapping.findForward("dcsservices.error");
		}
		
		// check for availability of validating service
		if (validatingService.getIsProcessing()) {
			String id = validatingService.getSessionId();
			DcsSetInfo set = validatingService.getValidatingSetInfo();
			dcssf.setErrorMsg("Another session is currently performing an export. Please try again later.");
			dcssf.setResultCode("busy");
			return mapping.findForward("dcsservices.error");
		}
		
		
		List errorList = new ArrayList ();
		CollectionConfig collectionConfig = null;
		File destDir = null;
		
		// validate presence of required arguments
		String collectionParam = request.getParameter("collection");
		if (collectionParam == null || collectionParam.length() == 0) {
			errorList.add ("The \'collection\' argument is required but is missing or empty");
		}
		else {
			collectionConfig = collectionRegistry.getCollectionConfig(collectionParam, false);
			if (collectionConfig == null) {
				errorList.add ("No collection found for \"" + collectionParam + "\"");
			}
		}

		// HERE WE COULD LOCK THE COLLECTION ...
		
		DcsSetInfo setInfo = null;
		String xmlFormat = null;
		if (collectionConfig != null) {
			setInfo = RepositoryUtils.getDcsSetInfo(collectionConfig.getId(), rm);
	
			xmlFormat = setInfo.getFormat();
			prtln ("XML_FORMAT: " + xmlFormat);
			if (this.frameworkRegistry.getFramework (xmlFormat) == null) {
				errorList.add ("Framework (\"" + xmlFormat + "\") not loaded for this collection");
			}
		}
		
		if (errorList.size() > 0) {
			dcssf.setErrorList(errorList);
			dcssf.setResultCode("error");
			return  mapping.findForward("dcsservices.error");
		}
		
		prtln ("params validated");
		
		// INDEX
		String collectionName = setInfo.getName();
		String collection = setInfo.getSetSpec();
		CollectionIndexingObserver observer = 
			new CollectionIndexingObserver(setInfo, rm, servletContext);
		
		try {
			if (!rm.indexCollection(collection, observer, true)) {
				throw new Exception ("Indexing failed for \"" + collection + "\"");
			}
		} catch (Throwable t) {
				errorList.add ("System Error while indexing: " + t.getMessage());
		}
		
		if (errorList.size() > 0) {
			dcssf.setErrorList(errorList);
			dcssf.setResultCode("error");
			return  mapping.findForward("dcsservices.error");
		}
			
		/* VALIDATE - do this in observer?!*/
		try {
			// all status, ignore cached validation
			// prtln ("VALIDATING");
			// validatingService.validateRecords (setInfo, null, null, true);
			
		} catch (Throwable t) {
			String errorMsg = "exportingService.exportRecords error: " + t.getMessage();
			prtlnErr (errorMsg);
			t.printStackTrace();
			throw new Exception (errorMsg);
		}

		dcssf.setResultCode(DCSServicesForm.RESULT_CODE_SUCCESS);
		return mapping.findForward("dcsservices.IngestCollection");
	}

		
	protected ActionForward doValidateCollection(
			HttpServletRequest request,
			HttpServletResponse response,
			RepositoryManager rm,
			DCSServicesForm dcssf,
			ActionMapping mapping)
			 throws Exception {

		prtln ("doValidateCollection()");				 

		CollectionRegistry collectionRegistry = 
			(CollectionRegistry) servlet.getServletContext().getAttribute("collectionRegistry");
		if (collectionRegistry == null) {
			dcssf.setErrorMsg("Server Error: \"collectionRegistry\" not found in servlet context");
			return mapping.findForward("dcsservices.error");
		}	
			
		ValidatingService validatingService = (ValidatingService) servlet.getServletContext().getAttribute ("validatingService");
		if (validatingService == null) {
			dcssf.setErrorMsg("Server Error: \"validatingService\" not found in servlet context");
			return mapping.findForward("dcsservices.error");
		}
		
		if (validatingService.getIsProcessing()) {
			String id = validatingService.getSessionId();
			DcsSetInfo set = validatingService.getValidatingSetInfo();
			dcssf.setErrorMsg("Another session is currently performing an export. Please try again later.");
			dcssf.setResultCode("busy");
			return mapping.findForward("dcsservices.error");
		}
		
		
		List errorList = new ArrayList ();
		CollectionConfig collectionConfig = null;
		File destDir = null;
		
		// validate presence of required arguments
		String collectionParam = request.getParameter("collection");
		if (collectionParam == null || collectionParam.length() == 0) {
			errorList.add ("The \'collection\' argument is required but is missing or empty");
		}
		else {
			collectionConfig = collectionRegistry.getCollectionConfig(collectionParam, false);
			if (collectionConfig == null) {
				errorList.add ("No collection found for \"" + collectionParam + "\"");
			}
		}
		
		
		if (errorList.size() > 0) {
			dcssf.setErrorList(errorList);
			dcssf.setResultCode("error");
			return  mapping.findForward("dcsservices.error");
		}
		
		prtln ("collection param validated");

		// status values are not required. The default is __final__
		/* String [] statusParams = request.getParameterValues("statuses"); */
		String [] statusParams = null;
		String [] statusValues = getStatusValues (statusParams, collectionConfig);
		
		DcsSetInfo setInfo = RepositoryUtils.getDcsSetInfo(collectionConfig.getId(), rm);

		prtln ("about to call validatingService.validateRecords()");
		prtln ("\t" + "destDir: " + destDir);
		prtln ("\t" + "statusValues");
		for (int i=0;i<statusValues.length;i++)
			prtln ("\t\t" + statusValues[i]);
		if (setInfo == null)
			prtln ("\t" + "setInfo is NULL!");
		
		dcssf.setStatusLabels(getStatusLabels (statusParams, collectionConfig));
		dcssf.setStatuses(statusParams);
		
		try {
			// all status, ignore cached validation
			validatingService.validateRecords (setInfo, null, null, true);
			
		} catch (Throwable t) {
			String errorMsg = "exportingService.exportRecords error: " + t.getMessage();
			prtlnErr (errorMsg);
			t.printStackTrace();
			throw new Exception (errorMsg);
		}

		dcssf.setResultCode(DCSServicesForm.RESULT_CODE_SUCCESS);
		return mapping.findForward("dcsservices.ValidateCollection");
	}

	
	
	private List getStatusLabels (String [] statusParams, CollectionConfig collectionConfig) {
		List labels = new ArrayList ();
		String finalStatusValue = collectionConfig.getFinalStatusValue();
		if (statusParams == null)
			return labels;
		for (int i=0;i<statusParams.length;i++) {
			String value = statusParams[i];
			if (value.equals(finalStatusValue))
				value = collectionConfig.getFinalStatusLabel();
			if (!labels.contains(value))
				labels.add (value);
		}
		return labels;
	}
			
	private String [] getStatusValues (String[] statusParams, CollectionConfig collectionConfig) {
		String finalStatusValue = collectionConfig.getFinalStatusValue();
		if (statusParams == null) {
			String [] ret = {finalStatusValue};
			return ret;
		}
		
		String [] values = new String [statusParams.length];
		String finalStatusLabel = collectionConfig.getFinalStatusLabel();
		if (statusParams == null)
			return values;
		for (int i=0;i<statusParams.length;i++) {
			String label = statusParams[i];
			String value = label;
			if (label.equals(finalStatusLabel))
				value = collectionConfig.getFinalStatusValue();
			values[i] = value;
		}
		return values;
	}
	
	/**
	* Ensure that the record will not choke the index. Currently only suported for "adn" format.<p>
	Notes:
	<li>This level of validation may not be neccesary since repository manager performs the same checks
	<li>If we do want to perform this level of validation, we should be using required paths as defined in 
	framework-config.
	*/
	private List validateRecordXml (PutRecordData prData) throws Exception {
		// prtln ("validateRecordXml");
		List errorList = new ArrayList ();
		String id = prData.getId();
		String format = prData.getFormat();
		String prefix = collectionRegistry.getIdPrefix(prData.getCollection());
		// prtln ("id: " + id + ", prefix: " + prefix);
		if (!id.startsWith(prefix))
			errorList.add ("record ID (" + id + ") does not have the correct prefix (\"" + prefix + "\" is required)");
		
		// validate against individual frameworks - not yet implemented
		if (format.equals("adn"))
			errorList.addAll (validateAdn (prData));
		/* 
		else if (format.equals("dlese_collect"))
			errorList.addAll (validateDleseCollect (prData));
		else if (format.equals("news_opps"))
			errorList.addAll (validateNewsOpps(prData));
		else if (format.equals("dlese_anno"))
			errorList.addAll (validateDleseAnno (prData));
		else
			return errorList; */
		return errorList;
	}
	
	private List validateAdn (PutRecordData prData) throws Exception {
		// prtln ("validateAdn()");
		List errorList = new ArrayList ();

		return errorList;
	}
		
	/** return a string with leading and trailing whitespace removed, or null if the 
	* supplied string was null or contained only whitespaces.
	*/
	private String getTrimmedValueOrNull (String val) {
		if (val != null) {
			val = val.trim();
			if (val.length() == 0) {
				val = null;
			}
		}
		return val;
	}
	
	/**
	 *  Checks for IP authorization
	 *
	 * @param  request       HTTP request
	 * @param  securityRole  Security role
	 * @param  rm            RepositoryManager
	 * @return               True if authorized, false otherwise.
	 */
	private boolean isAuthorized(HttpServletRequest request, String securityRole, RepositoryManager rm) {
		if (securityRole.equals("trustedIp")) {
			String[] trustedIps = rm.getTrustedWsIpsArray();
			if (trustedIps == null)
				return false;

			String IP = request.getRemoteAddr();
			for (int i = 0; i < trustedIps.length; i++) {
				if (IP.matches(trustedIps[i]))
					return true;
			}
			prtlnErr ("unauthorized IP: " + IP);
		}
		return false;
	}


			
	// --------------- Debug output ------------------

	/**
	 *  Return a string for the current time and date, sutiable for display in log
	 *  files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	protected final static String getDateStamp() {
		return
				new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	protected final void prtlnErr(String s) {
		System.err.println(getDateStamp() + " " + s);
	}



	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 * @param  s  The String that will be output.
	 */
	protected final void prtln(String s) {
		if (debug) {
			System.out.println("DCSServicesAction: " + s);
		}
	}


	/**
	 *  Sets the debug attribute of the object
	 *
	 * @param  db  The new debug value
	 */
	public static void setDebug(boolean db) {
		debug = db;
	}
}

