/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package edu.ucar.dls.schemedit.action;

import edu.ucar.dls.repository.*;
import edu.ucar.dls.dds.DDSServlet;
import edu.ucar.dls.webapps.tools.GeneralServletTools;
import edu.ucar.dls.index.*;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.repository.CollectionReaper;
import edu.ucar.dls.schemedit.repository.RepositoryIndexingObserver;
import edu.ucar.dls.schemedit.repository.CollectionIndexingObserver;
import edu.ucar.dls.schemedit.repository.RepositoryUtils;
import edu.ucar.dls.schemedit.dcs.*;
import edu.ucar.dls.schemedit.config.*;
import edu.ucar.dls.schemedit.threadedservices.*;
import edu.ucar.dls.schemedit.action.form.NLDRToolsForm;
import edu.ucar.dls.schemedit.security.access.Roles;
import edu.ucar.dls.schemedit.security.user.User;
import edu.ucar.dls.vocab.MetadataVocabServlet;
import edu.ucar.dls.vocab.MetadataVocab;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.ndr.*;

import org.dom4j.Document;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.text.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;

/* import edu.ucar.dls.repository.RepositoryManager;
import edu.ucar.dls.index.*;
import edu.ucar.dls.index.reader.*;
import  edu.ucar.dls.xml.nldr.VocabRecord;
import edu.ucar.dls.services.dds.toolkit.*; */

/**
 *  A Struts Action controlling several collection-level operations, including
 *  creation, export, validation, and deletion.
 *
 * @author    Jonathan Ostwald
 */
public final class NLDRToolsAction extends DCSAction {

	private static boolean debug = true;
	VocabRecordsAffectedService vocabRecordsAffectedService = null;


	/**
	 *  Gets the metaDataFramework attribute of the AbstractSchemEditAction object
	 *
	 * @return    The metaDataFramework value
	 */
	protected MetaDataFramework getCollectionFramework() {
		return this.getMetaDataFramework("dlese_collect");
	}

	// --------------------------------------------------------- Public Methods

	/**
	 *  Processes the specified HTTP request and creates the corresponding HTTP
	 *  response by forwarding to a JSP that will create it. A {@link
	 *  edu.ucar.dls.index.SimpleLuceneIndex} must be available to this class via
	 *  a ServletContext attribute under the key "index." Returns an {@link
	 *  org.apache.struts.action.ActionForward} instance that maps to the Struts
	 *  forwarding name "??," which must be configured in struts-config.xml to
	 *  forward to the JSP page that will handle the request.
	 *
	 * @param  mapping               The ActionMapping used to select this instance
	 * @param  request               The HTTP request we are processing
	 * @param  response              The HTTP response we are creating
	 * @param  form                  The ActionForm for the given page
	 * @return                       The ActionForward instance describing where
	 *      and how control should be forwarded
	 * @exception  IOException       if an input/output error occurs
	 * @exception  ServletException  if a servlet exception occurs
	 */
	public ActionForward execute(
	                             ActionMapping mapping,
	                             ActionForm form,
	                             HttpServletRequest request,
	                             HttpServletResponse response)
		 throws IOException, ServletException {
		/*
		 *  Design note:
		 *  Only one instance of this class gets created for the app and shared by
		 *  all threads. To be thread-safe, use only local variables, not instance
		 *  variables (the JVM will handle these properly using the stack). Pass
		 *  all variables via method signatures rather than instance vars.
		 */
		 
		 prtln ("NLDR Tools Action");
		ActionErrors errors = initializeFromContext(mapping, request);
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			return (mapping.findForward("error.page"));
		}
		NLDRToolsForm nldrToolsForm = (NLDRToolsForm) form;
		String errorMsg = "";

		ServletContext servletContext = servlet.getServletContext();
		// nldrToolsForm.setRequest(request);

		SimpleLuceneIndex index = repositoryManager.getIndex();

		User sessionUser = this.getSessionUser(request);
		SchemEditUtils.showRequestParameters(request);
		
		vocabRecordsAffectedService = 
			(VocabRecordsAffectedService) servlet.getServletContext().getAttribute("vocabRecordsAffectedService");
		if (vocabRecordsAffectedService == null)
			prtlnErr ("ERROR: vocabRecordsAffectedService not found in servlet context");
		nldrToolsForm.setIsUpdating(vocabRecordsAffectedService.getIsProcessing());
		nldrToolsForm.setUpdatingSession(vocabRecordsAffectedService.getSessionId());
		nldrToolsForm.setProgress(null);
		
		try {

			String command = request.getParameter("command");
			prtln ("command: " + command);
			
			if (command == null || command.trim().length() == 0) {

				return mapping.findForward("nldr.tools");
			}

			else {

				if (command.equalsIgnoreCase("UpdateVocabAffectedRecords")) {
					return doUpdateVocabAffectedRecords(mapping, form, request, response);
				}
				
				if (command.equalsIgnoreCase("progress")) {
					TaskProgress progress = vocabRecordsAffectedService.getTaskProgress();
					String rpt = progress.getProgressReport();
					nldrToolsForm.setProgress(rpt);
					return mapping.findForward("progress");
				}
				
				if (command.equalsIgnoreCase("stopUpdate")) {
					prtln ("STOP UPDATE!");
					if (vocabRecordsAffectedService.getIsProcessing()) {
						vocabRecordsAffectedService.stopProcessing();
						nldrToolsForm.setIsUpdating(false);
						errors.add("message", new ActionError("generic.message", "Update has stopped."));
						errors.add("showValidationMessagingLink", new ActionError("generic.message", ""));
					}
					else {
						errors.add("message", new ActionError("generic.message", "Update completed before stop command was received"));
						errors.add("showValidationMessagingLink", new ActionError("generic.message", ""));
					}
				}
				
				if (command.equalsIgnoreCase ("finalizeUpdate")) {
/* 					String collection = request.getParameter("collection");
					request.setAttribute ("pageMessage", "Done updating \"" + collection + "\""); */
					request.setAttribute ("pageMessage", "Done updating");
					return mapping.findForward("nldr.tools"); 
				}
				
				else {
					errors.add("error",
						new ActionError("generic.error", "Unrecognized command: " + command));
				}
				
			}
			// nldrToolsForm.clear();
		} catch (Exception e) {
			prtln("NLDRToolsAction caught exception: " + e);
			errors.add("error",
				new ActionError("generic.error", e.getMessage()));
		} catch (Throwable e) {
			prtln("NLDRToolsAction caught exception.");
			e.printStackTrace();
			errors.add("error",
				new ActionError("generic.error", "NLDRToolsAction caught exception"));

		}
		saveErrors(request, errors);
		return mapping.findForward("nldr.tools");
	}

	protected ActionForward doUpdateVocabAffectedRecords(
												 ActionMapping mapping,
	                                             ActionForm form,
	                                             HttpServletRequest request,
	                                             HttpServletResponse response) {
		prtln("doUpdateVocabAffectedRecords");
		NLDRToolsForm nldrToolsForm = (NLDRToolsForm) form;
		SessionBean sessionBean = sessionRegistry.getSessionBean(request);

		ActionErrors errors = new ActionErrors();
		String errorMsg;

		if (vocabRecordsAffectedService.getIsProcessing()) {
			nldrToolsForm.setUpdatingSession(vocabRecordsAffectedService.getSessionId());
			errors.add("message",
				new ActionError("generic.error", "Validating is in progress!"));
			saveErrors(request, errors);
			return mapping.findForward("validate.collection");
		}
		
		String collection = request.getParameter("collection");
		if (collection == null || collection.trim().length() == 0) {
			errors.add("error", new ActionError("generic.error", "A collection was not specified"));
			saveErrors(request, errors);
			return mapping.findForward("nldr.tools");
		}
		collection = collection.trim();
		String query = "ky:"+collection;
		prtln ("query: " + query);
		SimpleLuceneIndex index = repositoryManager.getIndex();
		RecordList recordBatch = new RecordList(query,  index);
		prtln (recordBatch.size() + " items for collection " + collection);
		try {
			if (!sessionBean.getBatchLocks(recordBatch)) {
				errors.add("error", new ActionError("batch.lock.not.obtained", "Batch Move"));
				saveErrors(request, errors);
				return mapping.findForward("nldr.tools");
			}
			
			// initialize the form
			nldrToolsForm.setUpdateReport(null);
			nldrToolsForm.setIsUpdating(true);
			nldrToolsForm.setUpdatingSession(sessionBean.getId());
			
			try {
				prtln ("CALLING updateVocabRecordsAffected");
				vocabRecordsAffectedService.updateVocabRecordsAffected (recordBatch.getItems(), collection);
			} catch (Throwable t) {
				errorMsg = "updatingService error: " + t.getMessage();
				prtlnErr(errorMsg);
				t.printStackTrace();
				throw new Exception(errorMsg);	
				
			}
			
			errors.add("message", new ActionError("generic.message", "Vocabs are being updated"));
			errors.add("message", new ActionError("generic.message", "This operation may take several several minutes to complete"));
			errors.add("showUpdateMessagingLink", new ActionError("generic.message", ""));
			saveErrors(request, errors);

	/* 		// NOW HANDLED BY SERVICE
			Iterator recIter = recordBatch.iterator();
			while (recIter.hasNext()) {
				String recId = (String)recIter.next();
				VocabRecord vocabRecord = null;
				// ResultDoc resultDoc = recordBatch.getResultDoc(recId);
				
				try {
					XMLDocReader docReader = RepositoryUtils.getXMLDocReader (recId, repositoryManager);
					String recordXml = docReader.getXml();
					vocabRecord = new VocabRecord (recordXml);
					// prtln ("vocabRecord: " + vocabRecord.getId() + " " + vocabRecord.getTerm());
					int recordsAffected = this.getAffectedVocabRecords (collection, vocabRecord.getTerm());
					vocabRecord.setRecordsAffected(recordsAffected);
				} catch (Exception e) {
					prtlnErr ("could not setRecordsAffected for " + recId + ": " + e.getMessage());
					continue;
				}
				
				// Write vocab record to repository
				try {
					repositoryManager.putRecord(vocabRecord.getXml(), "vocabs", collection, recId, true);
				} catch (Exception e) {
					prtlnErr ("could not putRecord for " + recId + ": " + e.getMessage());
					continue;
				}
			} */
			request.setAttribute ("pageMessage", "updating \"" + collection + "\" ...");

		} catch (Exception e) {
			errorMsg = "Could not update vocab records: " + e.getMessage();
			errors.add("error", new ActionError("generic.error", errorMsg));
		} finally {
			sessionBean.releaseAllLocks();
		}
		saveErrors(request, errors);
		return mapping.findForward("nldr.tools");


	}


/* 	
	private int getLocalAffectedVocabRecords (String collection, String term) {
		if (collection == null || collection.trim().length() == 0)
			return 0;
		ServletContext servletContext = servlet.getServletContext();
		RepositoryManager rm = (RepositoryManager)servletContext.getAttribute("repositoryManager");
		SimpleLuceneIndex index = rm.getIndex();
		
		String query = "";
		if (collection.equals("pubname")) {
			query = "/key//record/contributors/organization/affiliation/instName:\"" + term + "\"";
			query += " OR ";
			query += "/key//record/contributors/person/affiliation/instName:\"" + term + "\"";
		}
		query = "(" + query + ") AND xml_format:osm";
		
		prtln ("query: " + query);
		ResultDocList results = index.searchDocs (query);
		prtln (results.size() + " results");
		
		return results.size();
	} */


	// -------------- Debug ------------------

	/**
	 *  Sets the debug attribute of the NLDRToolsAction class
	 *
	 * @param  isDebugOutput  The new debug value
	 */
	public static void setDebug(boolean isDebugOutput) {
		debug = isDebugOutput;
	}


	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	private void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "NLDRToolsAction");
		}
	}


	private void prtlnErr(String s) {
		SchemEditUtils.prtln(s, "NLDRToolsAction");
	}
}

