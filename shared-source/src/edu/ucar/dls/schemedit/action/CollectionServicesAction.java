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
import edu.ucar.dls.schemedit.action.form.CollectionServicesForm;
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
public final class CollectionServicesAction extends DCSAction {

	private static boolean debug = true;

	private ExportingService exportingService;
	private ValidatingService validatingService;


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
		ActionErrors errors = initializeFromContext(mapping, request);
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			return (mapping.findForward("error.page"));
		}
		CollectionServicesForm csForm = (CollectionServicesForm) form;
		String errorMsg = "";

		ServletContext servletContext = servlet.getServletContext();
		csForm.setRequest(request);

		SimpleLuceneIndex index = repositoryManager.getIndex();
		csForm.setNumIndexingErrors(repositoryManager.getNumIndexingErrors());
		if (index != null) {
			ResultDocList indexingErrors = index.searchDocs("error:true");
			if (indexingErrors == null) {
				csForm.setNumIndexingErrors(0);
			}
			else {
				csForm.setNumIndexingErrors(indexingErrors.size());
			}
			indexingErrors = null;
		}

		MetadataVocab vocab = repositoryManager.getMetadataVocab();

		exportingService = (ExportingService) servletContext.getAttribute("exportingService");
		csForm.setIsExporting(exportingService.getIsProcessing());
		csForm.setExportingSession(exportingService.getSessionId());
		csForm.setExportingSet(exportingService.getExportingSetInfo());
		csForm.setExportBaseDir(exportingService.getExportBaseDir());

		validatingService = (ValidatingService) servletContext.getAttribute("validatingService");
		csForm.setIsValidating(validatingService.getIsProcessing());
		csForm.setValidatingSession(validatingService.getSessionId());
		csForm.setProgress(null);

		User sessionUser = this.getSessionUser(request);
		csForm.setSets(repositoryService.getAuthorizedSets(sessionUser, this.requiredRole));

		MetaDataFramework collectionFramework = getCollectionFramework();
		if (collectionFramework == null) {
			errorMsg = "attribute \"collectionFramework\" not found in servlet context";
			throw new ServletException(errorMsg);
		}

		SchemEditUtils.showRequestParameters(request);

		try {

			String command = request.getParameter("command");
			if (command == null || command.trim().length() == 0) {

				if (request.getParameter("disableSet") != null ||
					request.getParameter("enableSet") != null) {

					prtln("Handling currentSet updates...");
					this.handleWebserviceEnable(request, vocab);
					// must recalculate to get new webservice enable values
					csForm.setSets(repositoryService.getAuthorizedSets(sessionUser, this.requiredRole));

				}
				return mapping.findForward("manage.collections");
			}

			else {

				if (command.equalsIgnoreCase("new")) {
					csForm.clear();

					csForm.setFormats(this.getFormatList());
					if (collectionRegistry.getIDGenerator("collect") == null) {
						// at startup the idmanager is initialized before the indexer has a chance to complete.
						// in this case, we have to reinitialize the idManager here to create the IDGenerator we need
						// to create a new collection
						collectionRegistry.initializeIDGenerators(index);
						if (collectionRegistry.getIDGenerator("collect") == null) {
							errorMsg = "ID Generators not initialized. Please Reindex all via Collection Settings";
							errors.add("error", new ActionError("generic.error", errorMsg));
							saveErrors(request, errors);
							return mapping.findForward("error.page");
						}
					}
					return mapping.findForward("create.collection.input");
				}

				if (command.equalsIgnoreCase("cancel")) {
					csForm.clear();
					return mapping.findForward("manage.collections");
				}

				if (command.equalsIgnoreCase("submit")) {
					errors = validateNewCollectionInput(request, csForm);
					// prtln ("validation found " + errors.size() + " errors");
					if (errors.size() > 0) {
						errors.add("error",
							new ActionError("edit.errors.found"));
						saveErrors(request, errors);
						return mapping.findForward("create.collection.input");
					}
					else {
						return createCollection(csForm, collectionFramework, mapping, request);
					}
				}
				if (command.equalsIgnoreCase("export")) {
					return handleExportCommand(csForm, mapping, request);
				}
				if (command.equalsIgnoreCase("validate")) {
					return handleValidateCommand(csForm, mapping, request);
				}
				// Delete and rebuild the index:
				if (command.equals("Reindex All")) {
					repositoryManager.deleteIndex();
					repositoryManager.loadCollectionRecords(true);
					repositoryManager.indexFiles(new RepositoryIndexingObserver(collectionRegistry, repositoryManager), true);
					errors.add("message", new ActionError("generic.message", "Index has been deleted and is now rebuilding."));
					errors.add("showIndexMessagingLink", new ActionError("generic.message", ""));
					saveErrors(request, errors);
					return mapping.findForward("manage.collections");
				}
				if (command.equals("reindexCollection")) {
					String collection = request.getParameter("collection");
					SetInfo setInfo = RepositoryUtils.getSetInfo(collection, this.repositoryManager);
					String collectionName = collection;
					if (setInfo != null)
						collectionName = setInfo.getName();
					// if (repositoryManager.indexCollection(collection, new SimpleFileIndexingObserver("Collection indexer for '" + collection + "'", "Starting indexing"), true)) {
					CollectionIndexingObserver observer = new CollectionIndexingObserver(collection, collectionRegistry, repositoryManager);
					if (repositoryManager.indexCollection(collection, observer, true)) {
						errors.add("message", new ActionError("generic.message", "Files for collection '" + collectionName + "' are being indexed."));
						errors.add("message", new ActionError("generic.message", "Changes may take several several minutes to appear"));
						errors.add("showIndexMessagingLink", new ActionError("generic.message", ""));
					}
					else {
						errors.add("error", new ActionError("generic.error", "Collection '" + collectionName + "' is not configured in the repository. Unable to index files."));
					}
					saveErrors(request, errors);
					return mapping.findForward("manage.collections");
				}
				// Show indexing status messages:
				if (command.equals("showIndexingMessages")) {
					List indexingMessages = repositoryManager.getIndexingMessages();	
					for (int i = indexingMessages.size() - 1; i >= 0; i--) {
						errors.add("message", new ActionError("generic.message", (String) indexingMessages.get(i)));
					}
					
					errors.add("showIndexMessagingLink", new ActionError("generic.message", ""));
					saveErrors(request, errors);
					return mapping.findForward("manage.collections");
				}

				if (command.equalsIgnoreCase("deleteCollection")) {
					return handleDeleteCollection(mapping, form, request, response);
				}
				
				if (command.equals("Reload vocabulary")) {
					if (vocab != null) {

						MetadataVocabServlet mvs =
							(MetadataVocabServlet) servlet.getServletContext().getAttribute("MetadataVocabServlet");
						try {
							if (mvs == null)
								throw new Exception("MetadataVocabServlet not found in servlet context");
							mvs.loadVocabs();
						} catch (Exception e) {
							errorMsg = "Vocab reload error: " + e.getMessage();
							errors.add("error",
								new ActionError("generic.error", "Vocabs not found in servlet context"));
						}
						errors.add("message",
							new ActionError("generic.message", "Vocabs reloaded"));
					}
					else {
						errors.add("error", new ActionError("generic.error", "Vocabs not found in servlet context"));
					}
					saveErrors(request, errors);
					return mapping.findForward("manage.collections");
				}
			}
			csForm.clear();
		} catch (Exception e) {
			prtlnErr("CollectionServicesAction caught exception: " + e);
			errors.add("error",
				new ActionError("generic.error", e.getMessage()));
		} catch (Throwable e) {
			prtlnErr("CollectionServicesAction caught exception.");
			e.printStackTrace();
			errors.add("error",
				new ActionError("generic.error", "CollectionServicesAction caught exception"));

		}
		saveErrors(request, errors);
		return mapping.findForward("manage.collections");
	}


	/**
	 *  Enable / Disable webservice search for specified set
	 *
	 * @param  req            NOT YET DOCUMENTED
	 * @param  vocab          NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	private void handleWebserviceEnable(HttpServletRequest req, MetadataVocab vocab)
		 throws Exception {

		if (req.getParameter("enableSet") != null) {
			prtln("enabling " + req.getParameter("setUid"));
			repositoryManager.enableSet(req.getParameter("setUid"));
		}
		else {
			prtln("disabling " + req.getParameter("setUid"));
			repositoryManager.disableSet(req.getParameter("setUid"));
		}

		if (vocab != null &&
			servlet.getServletContext().getAttribute("suppressSetCollectionsVocabDisplay") == null) {
			DDSServlet.setCollectionsVocabDisplay(repositoryManager);
		}
	}


	/**
	 *  Delete a collection from the repository
	 *
	 * @param  mapping        the mapping object
	 * @param  form           the form object
	 * @param  response       the response
	 * @param  request        the request
	 * @return                forward to manage.collections page
	 * @exception  Exception  if collection could not be deleted
	 */
	private ActionForward handleDeleteCollection(
	                                             ActionMapping mapping,
	                                             ActionForm form,
	                                             HttpServletRequest request,
	                                             HttpServletResponse response)
		 throws Exception {

		ActionErrors errors = new ActionErrors();
		CollectionServicesForm csForm = (CollectionServicesForm) form;
		SessionBean sessionBean = this.getSessionBean(request);
		User sessionUser = this.getSessionUser(request);
		String collection = request.getParameter("collection");

		SetInfo setInfo = RepositoryUtils.getSetInfo(collection, repositoryManager);
		if (setInfo == null) {
			errors.add("message", new ActionError("collection.not.found", collection));
			saveErrors(request, errors);
			return mapping.findForward("manage.collections");
		}
		
		String id = setInfo.getId();

		if (!sessionBean.getLock(id)) {
			errors.add("recordLocked", new ActionError("lock.not.obtained.error", id));
			saveErrors(request, errors);
			return mapping.findForward("manage.collections");
		}

		RecordList records = repositoryService.getCollectionItemRecords(collection);
		if (!sessionBean.getBatchLocks(records)) {
			errors.add("error", new ActionError("batch.lock.not.obtained", "Delete Collection"));
			saveErrors(request, errors);
			return mapping.findForward("manage.collections");
		}

		/* 	don't allow deletion of a collection containing dlese_collect records, since the reap
			process will attempt to delete collections if their key is the same as an item collection
			(this is the case when a dlese_collect collection is used to export collection records, as
			we used to do with DLESE DCS and Library DCS).
			In this case, instruct user to first delete items first (using batch delete on collection) and THEN
			delete the collection. NOTE: if there is a "master" collection record (with key="collect") the key
			for this record must also be changed before it can be deleted (because the system thinks this
			is the DCS master collection record and won't allow its deletion)
		*/
		if (setInfo.getFormat().equals ("dlese_collect") && !records.isEmpty()) {
			String msg = "Because the items of this collection (" + setInfo.getName() + ")" +
					" are \'dlese_collect\' format, " +
					"this collection cannot be deleted until it is empty. To empty the collection, " +
					"perform a \'Batch Delete\' from the search page.";
			errors.add("error", new ActionError("generic.error", msg));
			saveErrors(request, errors);
			return mapping.findForward("manage.collections");
		}
		
		// reaper removes item records and unhooks from vocab, collectionRegistry and idManager.
		try {
			/*
				do NOT delete the collection from the NDR without getting express written
				consent from the user. There should be a dialog to clarify user's intentions
				and also offer an alternative (later) way of deleting from the ndr ...
			*/
			/* 			CollectionConfig config = this.collectionRegistry.getCollectionConfig(collection);
			if (config.isNDRCollection()) {
				// we used to delete automatically from NDR, but no more ...
			} */
			repositoryService.deleteCollection(collection);
			csForm.setSets(repositoryService.getAuthorizedSets(sessionUser, this.requiredRole));
		} catch (Exception e) {
			errors.add("error", new ActionError("generic.error", e.getMessage()));
		}

		if (errors.size() > 1) {
			saveErrors(request, errors);
			return mapping.findForward("manage.collections");
		}
		errors.add("message", new ActionError("delete.collection.confirm", setInfo.getName(), id));
		saveErrors(request, errors);
		return mapping.findForward("manage.collections");
	}


	/**
	 *  Handle the export operation. called when command="export"
	 *
	 * @param  csForm   the form
	 * @param  mapping  the mapping
	 * @param  request  the request
	 * @return          NOT YET DOCUMENTED
	 */
	private ActionForward handleExportCommand(CollectionServicesForm csForm,
	                                          ActionMapping mapping, HttpServletRequest request) {
		String errorMsg = "";
		ActionErrors errors = new ActionErrors();

		// EXIT
		if (request.getParameter("exit") != null) {
			csForm.setDestPath(null);
			saveErrors(request, errors);
			return mapping.findForward("manage.collections");
		}

		csForm.setArchivedReports(exportingService.getArchivedReports());
		csForm.setExportReport(exportingService.getExportReport());
		try {
			//showExportMessages
			if (request.getParameter("showExportMessages") != null) {
				ArrayList exportingMessages = exportingService.getStatusMessages();
				int maxMessagesToShow = Integer.MAX_VALUE;
				int totalMessages = exportingMessages.size();
				int lastMessage = (totalMessages >= maxMessagesToShow ?
					totalMessages - maxMessagesToShow : 0);

				for (int i = totalMessages - 1; i >= lastMessage; i--) {
					errors.add("message", new ActionError("generic.message", (String) exportingMessages.get(i)));
				}
				errors.add("showExportMessagingLink", new ActionError("generic.message", ""));
			}
			// Stop indexing:
			else if (request.getParameter("stopExporting") != null) {
				if (exportingService.getIsProcessing()) {
					exportingService.stopProcessing();
					csForm.setIsExporting(false);
					errors.add("message", new ActionError("generic.message", "Exporting has stopped."));
					errors.add("showExportMessagingLink", new ActionError("generic.message", ""));
				}
				else {
					errors.add("message", new ActionError("generic.message", "Export completed before stop command was received"));
					errors.add("showExportMessagingLink", new ActionError("generic.message", ""));
				}
			}
			// exportCollection
			else if (request.getParameter("exportCollection") != null) {
				return doExportCollection(csForm, mapping, request);
			}

			else if (request.getParameter("setup") != null) {
				return doExportSetup(csForm, mapping, request);
			}

			else if (request.getParameter("report") != null) {
				String collection = request.getParameter("collection");
				if (collection != null && collection.trim().length() > 0) {
					ExportReport report = (ExportReport) exportingService.getArchivedReport(collection);
					csForm.setExportReport(report);
				}
				return mapping.findForward("export.reports");
			}
			else
				throw new Exception("Export did not receive known operation");
		} catch (Throwable e) {
			errorMsg = e.getMessage();
			prtlnErr(errorMsg);
			errors.add("error", new ActionError("generic.message", errorMsg));
			saveErrors(request, errors);
			return mapping.findForward("export.collection");
		}
		saveErrors(request, errors);
		return mapping.findForward("export.collection");
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  csForm   the form
	 * @param  mapping  the mapping
	 * @param  request  the request
	 * @return                NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	private ActionForward doExportSetup(CollectionServicesForm csForm,
	                                    ActionMapping mapping,
	                                    HttpServletRequest request) throws Exception {
		ActionErrors errors = new ActionErrors();
		String errorMsg = "";

		String collection = csForm.getCollection();
		if (collection == null || collection.trim().length() == 0) {
			csForm.setCollection(null);
			csForm.setDcsSetInfo(null);
			csForm.setDestPath(null);
			csForm.setExportReport(null);
			return mapping.findForward("export.collection");
		}

		CollectionConfig collectionConfig = collectionRegistry.getCollectionConfig(collection);
		if (collectionConfig == null) {
			// throw new Exception ("could not update collectionConfig: collectionConfig not found for " + collection);
			errorMsg = "collectionConfig not found for " + collection;
			errors.add("error", new ActionError("generic.error", errorMsg));
			saveErrors(request, errors);
			return mapping.findForward("error.page");
		}

		String destPath = request.getParameter("destPath");
		if (destPath == null || destPath.trim().length() == 0)
			csForm.setDestPath(collectionConfig.getExportDirectory());
		else
			csForm.setDestPath(destPath);

		// build the list of statuses from which the user will choose to export
		csForm.setStatusOptions(collectionConfig.getSelectableStatusFlags());

		DcsSetInfo dcsSetInfo = RepositoryUtils.getDcsSetInfo(collection, repositoryManager);
		csForm.setDcsSetInfo(dcsSetInfo);

		csForm.setExportReport(null);
		return mapping.findForward("export.collection");
	}


	/**
	 *  Description of the Method
	 *
	 * @param  csForm   the form
	 * @param  mapping  the mapping
	 * @param  request  the request
	 * @return          Description of the Return Value
	 */
	private ActionForward doExportCollection(CollectionServicesForm csForm,
	                                         ActionMapping mapping,
	                                         HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String errorMsg = "";

		if (exportingService.getIsProcessing()) {
			csForm.setExportingSession(exportingService.getSessionId());
			csForm.setExportingSet(exportingService.getExportingSetInfo());
			errors.add("message",
				new ActionError("generic.error", "Exporting is in progress!"));
			saveErrors(request, errors);
			return mapping.findForward("export.collection");
		}

		String collection = request.getParameter("collection");
		if (collection == null || collection.trim().length() == 0) {
			errors.add("message",
				new ActionError("generic.error", "no collection was specified"));
			saveErrors(request, errors);
			return mapping.findForward("export.collection");
		}

		if (!collectionRegistry.isRegistered(collection)) {
			errorMsg = "collectionConfig not found for " + collection;
			errors.add("error", new ActionError("generic.error", errorMsg));
			saveErrors(request, errors);
			return mapping.findForward("error.page");
		}

		// test for absolute paths and give error message.
		try {
			String relativeDestPath = request.getParameter("destPath");
			String exportBaseDir = exportingService.getExportBaseDir();
			File destDir = ExportingService.validateExportDestination(exportBaseDir, relativeDestPath);
			// File destDir = validateExportDestination (csForm, request);

			String[] selectedStatuses = request.getParameterValues("sss");
			DcsSetInfo dcsSetInfo = RepositoryUtils.getDcsSetInfo(collection, repositoryManager);
			SessionBean sessionBean = this.getSessionBean(request);
			csForm.setDcsSetInfo(dcsSetInfo);
			csForm.setExportReport(null);
			csForm.setIsExporting(true);
			csForm.setExportingSession(sessionBean.getId());
			csForm.setExportingSet(dcsSetInfo);
			try {
				exportingService.exportRecords(destDir, dcsSetInfo, selectedStatuses, sessionBean);
			} catch (Throwable t) {
				errorMsg = "exportingService.exportRecords error: " + t.getMessage();
				prtlnErr(errorMsg);
				t.printStackTrace();
				throw new Exception(errorMsg);
			}

			errors.add("message", new ActionError("generic.message", "Specified files are being exported to " + destDir.getAbsolutePath()));
			errors.add("message", new ActionError("generic.message", "This operation may take several several minutes to complete"));
			errors.add("showExportMessagingLink", new ActionError("generic.message", ""));
			saveErrors(request, errors);
			return mapping.findForward("export.collection");
		} catch (Exception e) {
			errors.add("error",
				new ActionError("generic.error", e.getMessage()));
			saveErrors(request, errors);
			return mapping.findForward("export.collection");
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  csForm   the form
	 * @param  mapping  the mapping
	 * @param  request  the request
	 * @return          NOT YET DOCUMENTED
	 */
	private ActionForward handleValidateCommand(CollectionServicesForm csForm,
	                                            ActionMapping mapping, HttpServletRequest request) {
		String errorMsg = "";
		ActionErrors errors = new ActionErrors();

		// prtln("handleValidateCommand()");

		// EXIT - handle this before authorizing
		if (request.getParameter("exit") != null) {
			saveErrors(request, errors);
			return mapping.findForward("manage.collections");
		}

		csForm.setValidationReport(validatingService.getValidationReport());
		csForm.setArchivedReports(validatingService.getArchivedReports());
		try {
			//showValidateMessages
			if (request.getParameter("showValidationMessages") != null) {
				ArrayList validatingMessages = validatingService.getStatusMessages();
				for (int i = validatingMessages.size() - 1; i >= 0; i--) {
					errors.add("message", new ActionError("generic.message", (String) validatingMessages.get(i)));
				}
				errors.add("showValidationMessagingLink", new ActionError("generic.message", ""));
			}
			// Stop indexing:
			else if (request.getParameter("stopValidating") != null) {
				if (validatingService.getIsProcessing()) {
					validatingService.stopProcessing();
					csForm.setIsValidating(false);
					errors.add("message", new ActionError("generic.message", "Validation has stopped."));
					errors.add("showValidationMessagingLink", new ActionError("generic.message", ""));
				}
				else {
					errors.add("message", new ActionError("generic.message", "Validation completed before stop command was received"));
					errors.add("showValidationMessagingLink", new ActionError("generic.message", ""));
				}
			}
			// validateCollection
			else if (request.getParameter("validateCollection") != null) {
				return (doValidateCollection(csForm, mapping, request));
			}
			// set up - set collection in form bean
			else if (request.getParameter("setup") != null) {
				String collection = request.getParameter("collection");
				validatingService.clearServiceReport();
				csForm.setValidationReport(null);
				if (collection != null && collection.trim().length() > 0) {
					DcsSetInfo dcsSetInfo = RepositoryUtils.getDcsSetInfo(collection, repositoryManager);
					csForm.setDcsSetInfo(dcsSetInfo);
					CollectionConfig collectionConfig = collectionRegistry.getCollectionConfig(collection);
					csForm.setStatusOptions(collectionConfig.getSelectableStatusFlags());
				}
				else {
					csForm.setCollection(null);
					csForm.setDcsSetInfo(null);
					csForm.setValidationReport(null);
					return mapping.findForward("validate.collection");
				}
			}
			else if (request.getParameter("report") != null) {
				String collection = request.getParameter("collection");
				if (collection != null && collection.trim().length() > 0) {
					ValidationReport report = (ValidationReport) validatingService.getArchivedReport(collection);
					csForm.setValidationReport(report);
				}
				return mapping.findForward("validation.reports");
			}

			else if (request.getParameter("progress") != null) {
				TaskProgress progress = validatingService.getTaskProgress();
				String rpt = progress.getProgressReport();
				csForm.setProgress(rpt);
				return mapping.findForward("progress");
			}

			else
				throw new Exception("Validate did not receive known operation");
		} catch (Throwable e) {
			errorMsg = e.getMessage();
			prtlnErr(errorMsg);
			errors.add("error", new ActionError("generic.message", errorMsg));
			saveErrors(request, errors);
			return mapping.findForward("validate.collection");
		}
		saveErrors(request, errors);
		return mapping.findForward("validate.collection");
	}


	/**
	 *  Description of the Method
	 *
	 * @param  csForm   the form
	 * @param  mapping  the mapping
	 * @param  request  the request
	 * @return          Description of the Return Value
	 */
	private ActionForward doValidateCollection(CollectionServicesForm csForm,
	                                           ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String errorMsg = "";

		if (validatingService.getIsProcessing()) {
			csForm.setValidatingSession(validatingService.getSessionId());
			errors.add("message",
				new ActionError("generic.error", "Validating is in progress!"));
			saveErrors(request, errors);
			return mapping.findForward("validate.collection");
		}

		String collection = request.getParameter("collection");
		if (collection == null || collection.trim().length() == 0) {
			// csForm.setDestPath ("");
			errors.add("message",
				new ActionError("generic.error", "no collection was specified"));
			saveErrors(request, errors);
			return mapping.findForward("validate.collection");
		}

		DcsSetInfo dcsSetInfo = RepositoryUtils.getDcsSetInfo(collection, repositoryManager);
		SessionBean sessionBean = this.getSessionBean(request);
		csForm.setDcsSetInfo(dcsSetInfo);
		csForm.setValidationReport(null);
		csForm.setIsValidating(true);
		csForm.setValidatingSession(sessionBean.getId());
		String[] selectedStatuses = request.getParameterValues("sss");
		try {
			boolean ignoreCachedValidation = "true".equals(request.getParameter("ignoreCachedValidation"));
			try {
				validatingService.validateRecords(dcsSetInfo, selectedStatuses, sessionBean, ignoreCachedValidation);
			} catch (Throwable t) {
				errorMsg = "validatingService error: " + t.getMessage();
				prtlnErr(errorMsg);
				t.printStackTrace();
				throw new Exception(errorMsg);
			}

			errors.add("message", new ActionError("generic.message", "Collection is being validated"));
			errors.add("message", new ActionError("generic.message", "This operation may take several several minutes to complete"));
			errors.add("showValidationMessagingLink", new ActionError("generic.message", ""));
			saveErrors(request, errors);
			return mapping.findForward("validate.collection");
		} catch (Exception e) {
			prtlnErr(e.getMessage());
			errors.add("error",
				new ActionError("generic.error", e.getMessage()));
			saveErrors(request, errors);
			return mapping.findForward("validate.collection");
		}
	}

	/**
	 *  Create a collection using information contained in the actionForm
	 *
	 * @param  csForm   the actionForm
	 * @param  collectionFramework  Description of the Parameter
	 * @param  mapping  the mapping
	 * @param  request  the request
	 * @return                      Description of the Return Value
	 */
	private ActionForward createCollection(CollectionServicesForm csForm,
	                                       MetaDataFramework collectionFramework,
	                                       ActionMapping mapping,
	                                       HttpServletRequest request) {

		// Document collectionDoc = null;
		ActionErrors errors = new ActionErrors();
		String newCollectionID;
		try {

			// validation preformed by repositoryManager.putCollection
			String collectionKey = csForm.getCollectionKey();
			String xmlFormat = csForm.getFormatOfRecords();
			String title = csForm.getShortTitle();
			String description = csForm.getDescription();
			String additionalMetadata = null;
			try {
				newCollectionID = this.repositoryManager.putCollection(collectionKey, xmlFormat, title, description, additionalMetadata);
				prtln ("new collection record created with id: " + newCollectionID);
			} catch (PutCollectionException e) {
				prtlnErr ("PutCollectionException: " + e.getMessage());
				errors.add("error",
					new ActionError("generic.error", e.getMessage()));
				saveErrors(request, errors);
				return mapping.findForward("create.collection.input");
			}
			
			csForm.setRecId(newCollectionID);
			
			CollectionConfig collectionConfig = null;
			// update CollectionConfig with collected data if there are collectionConfig paths defined for this format
			try {
				collectionConfig = updateCollectionConfig(csForm);
			} catch (Throwable t) {
				prtlnErr("updateCollectionConfig error: " + t.getMessage());
				t.printStackTrace();
			}

			// create dcsDataRecord
			DcsDataRecord dcsDataRecord = dcsDataManager.getDcsDataRecord("collect", "dlese_collect", newCollectionID + ".xml", newCollectionID);

			dcsDataRecord.updateStatus(StatusFlags.DCS_SPECIAL, "Record Created", StatusFlags.DCS_SPECIAL);
			dcsDataRecord.flushToDisk();

			User sessionUser = this.getSessionUser(request);
			if (sessionUser != null)
				sessionUser.setRole(collectionKey, Roles.MANAGER_ROLE);
			//update queryselectors so the collection will show up ..
			Iterator i = sessionRegistry.getUserSessionBeans(sessionUser).iterator();
			while (i.hasNext()) {
				SessionBean sb = (SessionBean) i.next();
				sb.setQuerySelectorsInitialized(false);
			}
			
			updateCollectionRecord (csForm, collectionFramework);

		} catch (Exception e) {
			e.printStackTrace();
			errors.add("error",
				new ActionError("generic.error", e.getMessage()));
			saveErrors(request, errors);
			return mapping.findForward("create.collection.input");
		}
		// prtln (Dom4jUtils.prettyPrint(collectionDoc));
		errors.add("message",
			new ActionError("collection.creation.confirmation"));
		saveErrors(request, errors);

		// build link to edit the newly created record
		String contextUrl = GeneralServletTools.getContextUrl(request);
		String editRecordLink = contextUrl + "/editor/edit.do?command=edit&xmlFormat=" +
			"dlese_collect&recId=" + newCollectionID;
		csForm.setEditRecordLink(editRecordLink);
		return mapping.findForward("confirm.page");
	}

	/**
	 *  Update a newly created collectionRecord with some values.<p>
	 NOTE: not sure this method is even necessary now that we are using repositoryManager.putCollection,
	 which creates the record. Why do we care about policy url and type?
	 *
	 * @param  csForm               Description of the Parameter
	 * @param  collectionFramework  Description of the Parameter
	 * @return                      Description of the Return Value
	 * @exception  Exception        Description of the Exception
	 */
	private void updateCollectionRecord(CollectionServicesForm csForm, MetaDataFramework collectionFramework)
		 throws Exception {

		// create a miminal document and then insert values from csForm
		// Document doc = schemaHelper.getMinimalDocument();
		String id = csForm.getRecId();

		// write collection Record to disk in appropriate subdir of collectionRecordsLocation
		File collectionDir = new File(repositoryManager.getCollectionRecordsLocation());
		File collectionFile = new File(collectionDir, id + ".xml");
		Document doc = collectionFramework.getEditableDocument(collectionFile);
		
		Map pathValueMap = new HashMap();

		//policyUrl
		pathValueMap.put("/collectionRecord/general/policies/policy/@url", csForm.getPolicyUrl());

		// policyType
		pathValueMap.put("/collectionRecord/general/policies/policy/@type", csForm.getPolicyType());

		// use docMap as wraper for Document
		DocMap docMap = new DocMap(doc, collectionFramework.getSchemaHelper());
		// load the dlese-collect metadata document with values from the form
		// pattern for smartPut: docMap.smartPut(xpath, value)
		for (Iterator i = pathValueMap.keySet().iterator(); i.hasNext(); ) {
			try {
				String xpath = (String) i.next();
				String val = (String) pathValueMap.get(xpath);
				docMap.smartPut(xpath, val);
			} catch (Exception e) {
				prtlnErr(e.getMessage());
			}
		}

		try {
			// now prepare document to write to file by inserting namespace information
			doc = collectionFramework.getWritableRecord (doc);
			repositoryManager.putRecord(doc.asXML(), "dlese_collect", "collect", id, true);
		} catch (Exception e) {
			throw new Exception("could not update collection record: " + e);
		}

		// return MetaDataHelper.makeCollectionDoc(id, pathValueMap, collectionFramework);
	}


	/**
	 *  Validate the input from user for creating new collections. Put changed or
	 *  default values into csForm. After this method returns, the csForm (rather
	 *  than request) is used to process user input.
	 *
	 * @param  request  Description of the Parameter
	 * @param  csForm   Description of the Parameter
	 * @return          Description of the Return Value
	 */
	private ActionErrors validateNewCollectionInput(HttpServletRequest request, CollectionServicesForm csForm) {
		ActionErrors errors = new ActionErrors();

		String formatOfRecords = request.getParameter("formatOfRecords");
		if ((formatOfRecords != null) && (formatOfRecords.trim().equals(""))) {
			errors.add("formatOfRecords", new ActionError("field.required", "Item Record Format"));
		}
		else {
			csForm.setFormatOfRecords(formatOfRecords.trim());
		}

		String shortTitle = request.getParameter("shortTitle").trim();
		if ((shortTitle == null) || (shortTitle.equals(""))) {
			errors.add("shortTitle", new ActionError("field.required", "Short title"));
		}
		else {
			shortTitle = shortTitle.trim();
			try {
				shortTitle = edu.ucar.dls.xml.XMLUtils.escapeXml(shortTitle);
			} catch (Throwable t) {
				prtlnErr(t.getMessage());
				t.printStackTrace();
			}
			csForm.setShortTitle(shortTitle);
		}

		String collectionKey = request.getParameter("collectionKey");

		// collection key is uniqe string of integers
		prtln("validateNewCollectionInput() collectionKey param: " + collectionKey);
		if ((collectionKey == null) || (collectionKey.trim().equals(""))) {
			csForm.setCollectionKey(SchemEditUtils.getUniqueId());
			csForm.setUserProvidedKey(false);
		}
		else {
			collectionKey = collectionKey.trim();
			if (collectionRegistry.getIds().contains(collectionKey)) {
				errors.add("collectionKey", new ActionError("generic.error", "\"" + collectionKey + "\" is already in use. please choose another"));
			}
			if (!isLegalKey(collectionKey)) {
				// errors.add("collectionKey", new ActionError("generic.error", "\"" + collectionKey + "\" contains illegal characters"));
				errors.add("collectionKey", new ActionError("collection.badchar"));
			}
			csForm.setCollectionKey(collectionKey);
			csForm.setUserProvidedKey(true);
		}

		String idPrefix = request.getParameter("idPrefix");
		prtln("validateNewCollectionInput() idPrefix param: " + idPrefix);
		if ((idPrefix == null) || (idPrefix.trim().equals(""))) {
			errors.add("idPrefix", new ActionError("field.required", "ID prefix"));
		}
		else {
			idPrefix = idPrefix.trim();
			if (collectionRegistry.isDuplicateIdPrefix(idPrefix)) {
				errors.add("idPrefix", new ActionError("generic.error", "\"" + idPrefix + "\" is already in use. please choose another"));
			}
			if (!isLegalKey(idPrefix)) {
				errors.add("idPrefix", new ActionError("collection.badchar"));
			}
			csForm.setIdPrefix(idPrefix);
		}

		return errors;
	}

	/**
	 *  Check the given string for occurrance of characters outside the standard
	 *  "word characters" ([a-zA-Z_0-9]), excluding space, hyphen and underscores.
	 *  <p>
	 *
	 *  Used to validate Collection Keys and Id Prefixes
	 *
	 * @param  s  the key to be validated
	 * @return    false if illegal chars are found in key
	 */
	public static boolean isLegalKey(String s) {
		Pattern p = Pattern.compile("[^\\w_-]+");
		Matcher m = p.matcher(s);
		return !m.find();
	}


	/**
	 *  Gets the formatList attribute of the CollectionServicesAction object
	 *
	 * @return    The formatList value
	 */
	private List getFormatList() {
		List formats = new ArrayList();
		formats.add(new org.apache.struts.util.LabelValueBean("-- Select format --", ""));
		for (Iterator i = frameworkRegistry.getItemFormats().iterator(); i.hasNext(); ) {
			String format = (String) i.next();
			formats.add(new org.apache.struts.util.LabelValueBean(format, format));
		}
		return formats;
	}


	/**
	 *  Update CollectionConfig with collected data from create collection form if
	 *  there are collectionConfig paths defined for this format (e.g., "adn" and
	 *  "dlese_anno" allow creation of tuples).
	 *
	 * @param  csForm         the Form
	 * @return                updated collection configuration
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	private CollectionConfig updateCollectionConfig(CollectionServicesForm csForm) throws Exception {

		CollectionConfig config = collectionRegistry.getCollectionConfig(csForm.getCollectionKey());
		config.setXmlFormat(csForm.getFormatOfRecords());
		config.setIdPrefix(csForm.getIdPrefix());
		prtln("updateCollectionConfig() idPrefix set to " + config.getIdPrefix());

		if (csForm.getFormatOfRecords().equals("adn")) {
			config.setTupleValue("termsOfUse", csForm.getTermsOfUse());
			config.setTupleValue("termsOfUseURI", csForm.getTermsOfUseURI());
			config.setTupleValue("copyright", csForm.getCopyright());
		}
		if (csForm.getFormatOfRecords().equals("dlese_anno")) {
			config.setTupleValue("serviceName", csForm.getServiceName());
		}
		config.flush();
		return config;
	}



	// -------------- Debug ------------------

	/**
	 *  Sets the debug attribute of the CollectionServicesAction class
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
			SchemEditUtils.prtln(s, "CollectionServicesAction");
		}
	}


	private void prtlnErr(String s) {
		SchemEditUtils.prtln(s, "CollectionServicesAction");
	}
}

