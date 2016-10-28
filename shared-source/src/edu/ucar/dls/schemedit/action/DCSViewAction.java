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
import edu.ucar.dls.index.*;
import edu.ucar.dls.schemedit.action.form.*;
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.dcs.*;
import edu.ucar.dls.schemedit.config.*;
import edu.ucar.dls.schemedit.repository.RepositoryUtils;
import edu.ucar.dls.index.reader.*;
import edu.ucar.dls.xml.*;
import org.apache.lucene.search.*;
import edu.ucar.dls.oai.*;
import edu.ucar.dls.webapps.tools.GeneralServletTools;

import java.util.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Locale;
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
import java.net.URLEncoder;
import org.dom4j.Document;
import org.dom4j.DocumentException;

/**
 *  A Struts Action for handling query requests that access a {@link
 *  edu.ucar.dls.index.SimpleLuceneIndex}. This class works in conjunction with the
 *  {@link edu.ucar.dls.schemedit.action.form.DCSViewForm} Struts form bean class.
 *
 *
 *
 * @author    Jonathan Ostwald
 */
public final class DCSViewAction extends DCSAction {

	private static boolean debug = false;
	DcsDataManager dcsDataManager = null;
	RepositoryManager rm = null;
	List dcsStatusOptions = null;
	long indexLastModified = -1;

	// --------------------------------------------------------- Public Methods

	/**
	 *  Processes the specified HTTP request and creates the corresponding HTTP response by
	 *  forwarding to a JSP that will create it. A {@link
	 *  edu.ucar.dls.index.SimpleLuceneIndex} must be available to this class via a
	 *  ServletContext attribute under the key "index." Returns an {@link
	 *  org.apache.struts.action.ActionForward} instance that maps to the Struts forwarding
	 *  name "browse.query," which must be configured in struts-config.xml to forward to the
	 *  JSP page that will handle the request.
	 *
	 * @param  mapping               The ActionMapping used to select this instance
	 * @param  request               The HTTP request we are processing
	 * @param  response              The HTTP response we are creating
	 * @param  form                  The ActionForm for the given page
	 * @return                       The ActionForward instance describing where and how
	 *      control should be forwarded
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
		 
		DCSViewForm viewForm = (DCSViewForm) form;
		 
		ActionErrors errors = initializeFromContext (mapping, request);
		if (!errors.isEmpty()) {
			saveErrors (request, errors);
			return (mapping.findForward("error.page"));
		}	
		
		// Set up params for paging:
		if (request.getParameter("s") != null) {
			try {
				viewForm.setStart(Integer.parseInt(request.getParameter("s").trim()));
			} catch (Throwable e) {
				viewForm.setStart(0);
			}
		}
		else
			viewForm.setStart(0);
			
		viewForm.setRequest(request);
		
		SchemEditUtils.showRequestParameters(request);
		
		rm = (RepositoryManager) repositoryManager;
		if (rm != null) {
			viewForm.setContextURL(GeneralServletTools.getContextUrl(request));
		}

		dcsDataManager = (DcsDataManager)servlet.getServletContext().getAttribute("dcsDataManager");		
		
		try {
			String id = request.getParameter ("id");
			String collection = request.getParameter ("collection");
			String xmlFormat = request.getParameter("format");
			
			if (id != null && id.length() > 0) {
				return viewRecordById (mapping, form, request, response);
			}
			
			if (collection != null && collection.length() > 0) {
				return viewCollectionConfig (mapping, form, request, response);
			}
			
			if (xmlFormat != null && xmlFormat.length() > 0) {
				return viewFrameworkConfig (mapping, form, request, response);
			}
			else  {
				viewForm.setResult (null);
					errors.add("message", new ActionError("generic.message",
						"No ID indicated. Please supply an ID number."));
					saveErrors(request, errors);
					return mapping.findForward("view.record");
				}

		} catch (NullPointerException e) {
			prtln("DCSViewAction caught exception.");
			e.printStackTrace();
			return mapping.findForward("error.page");
		} catch (Throwable e) {
			prtln("DCSViewAction caught exception: " + e);
			return mapping.findForward("error.page");
		}
	}
	
	private ActionForward viewCollectionConfig ( ActionMapping mapping,
	                             ActionForm form,
	                             HttpServletRequest request,
	                             HttpServletResponse response ) throws Exception {
		prtln ("viewCollectionConfig()");
		DCSViewForm viewForm = (DCSViewForm) form;
		String errorMsg = "";
		ActionErrors errors = new ActionErrors ();
		String collection = request.getParameter ("collection");
 		CollectionConfig info = collectionRegistry.getCollectionConfig(collection);
		if (info == null) {
			errorMsg = "collection not found for " + collection;
			prtln (errorMsg);
			throw new Exception (errorMsg);
		}
		// write config file to disk, in case collectionConfig has just been created
		info.getConfigReader().flush();

		String xmlFormat = "collection_config";

		File file = info.getConfigReader().getSource();
		
		if (!file.exists()) {
			prtln ("config file not found for " + collection);
			errors.add("message", new ActionError("generic.message",
				"No collection config record was found for \"" + collection + "\""));
			saveErrors(request, errors);
			return mapping.findForward("error.page");
		}
		
		MetaDataFramework framework = this.getMetaDataFramework(xmlFormat);
		if (framework.getSchemaHelper() == null)
			prtln ("SchemaHelper is NULL!");
		
		viewForm.setFramework(framework);
		viewForm.setResult(null);
		viewForm.setDcsSetInfo(RepositoryUtils.getDcsSetInfo(collection, rm));
		boolean collectionFrameworkLoaded = false;
		prtln ("\ncollectionFrameworkLoaded?");
		try {
			String collectionXmlFormat = info.getXmlFormat();
			prtln ("\t collectionXmlFormat: " + collectionXmlFormat);
			collectionFrameworkLoaded = (this.getMetaDataFramework(collectionXmlFormat) != null);
		} catch (Throwable t) {
			prtln ("collectionFramework loaded problem: " + t.getMessage());
		}
		prtln ("\t collectionFrameworkLoaded: " + collectionFrameworkLoaded);
		viewForm.setCollectionFrameworkLoaded (collectionFrameworkLoaded);
		
		Document doc = SchemEditUtils.getLocalizedXmlDocument(file.toURI());
		
		viewForm.setDocMap(doc);
		viewForm.clearMultiValuesCache();
		viewForm.setHash("");
		
		return mapping.findForward ("collection.config.display");
	}
	
	private ActionForward viewFrameworkConfig ( ActionMapping mapping,
	                             ActionForm form,
	                             HttpServletRequest request,
	                             HttpServletResponse response ) throws Exception {
		prtln ("viewFrameworkConfig()");
		DCSViewForm viewForm = (DCSViewForm) form;
		String errorMsg = "";
		ActionErrors errors = new ActionErrors ();
		
		
 		MetaDataFramework framework = this.getMetaDataFramework ("framework_config");
		if (framework == null) {
			errorMsg = "framework not found for " + "framework_config";
			prtln (errorMsg);
			throw new Exception (errorMsg);
		}
		

		String configXmlFormat = request.getParameter ("format");
		prtln ("configXmlFormat: " + configXmlFormat);
		MetaDataFramework configFramework = this.frameworkRegistry.getFramework(configXmlFormat);
		if (configFramework == null)
			throw new Exception ("format framework not found for " + configXmlFormat);
		
		FrameworkConfigReader reader = configFramework.getConfigReader();
		// Why do we write the configs? what can change, and if something was changed, 
		// should the 'changer' be responsible?		
		// reader.flush();

		// configXmlFormat is the format of the record to view. This param is used
		// to pluck the file to edit, NOT the metadataFramework to edit with!!

		File file = reader.getSource();
		
		if (!file.exists()) {
			prtln ("config file not found for " + configXmlFormat);
			errors.add("message", new ActionError("generic.message",
				"No framework config record was found for \"" + configXmlFormat + "\""));
			saveErrors(request, errors);
			return mapping.findForward("error.page");
		}
		
		viewForm.setFrameworkConfigFormat(configXmlFormat);
		viewForm.setFramework(framework);
		prtln ("framework set to " + framework.getXmlFormat());
		viewForm.setResult(null);
		
		Document doc = SchemEditUtils.getLocalizedXmlDocument(file.toURI());
		prtln (Dom4jUtils.prettyPrint(doc));
		
		viewForm.setDocMap(doc);
		viewForm.clearMultiValuesCache();
		viewForm.setHash("");
		
		return mapping.findForward ("framework.config.display");
	}

	
	private ActionForward viewRecordById ( ActionMapping mapping,
	                             ActionForm form,
	                             HttpServletRequest request,
	                             HttpServletResponse response ) throws ServletException {
		prtln ("viewRecordById");
		DCSViewForm viewForm = (DCSViewForm) form;
		SessionBean sessionBean = this.getSessionBean(request);
		ActionErrors errors = new ActionErrors ();

		String id = request.getParameter ("id");
		
		SearchHelper searchHelper = sessionBean.getSearchHelper();
		ResultDoc result = searchHelper.getResultDoc (id);
		if (searchHelper.isEmpty()) {
			searchHelper.setResults (result);
		}
		
		if (result == null) {
			viewForm.setResult (null);
			errors.add("message", new ActionError("generic.message",
				"No record was found in the index for ID \"" + id + "\""));
			saveErrors(request, errors);
			return mapping.findForward("view.record");
		}
		
		searchHelper.setCurrentRecId(id);
		sessionBean.setRecId(id);   // so record will be highlighted when returning to search
		
		// initialize the viewForm.docMap
		XMLDocReader resultDocReader = (XMLDocReader) result.getDocReader();
		String xmlFormat = resultDocReader.getNativeFormat();
		String collection = resultDocReader.getCollection();
 		MetaDataFramework framework = this.getMetaDataFramework(xmlFormat);

 		if (framework == null) {
			viewForm.setResult(null);
			errors.add("error", new ActionError("generic.message",
				"Framework is not loaded for format \"" + xmlFormat + "\""));
		}
		
		Document doc = null;
		if (framework != null) {
			try {
				doc = framework.getEditableDocument(resultDocReader.getDocsource());
			} catch (DocumentException e) {
				throw new ServletException ("Failed to obtain XML document from " + resultDocReader.getDocsource());
			}
		}
		
		// Set up form to view the current record (document)
		viewForm.setFramework(framework);
		viewForm.setResult(result);
		viewForm.setDcsSetInfo(RepositoryUtils.getDcsSetInfo(collection, rm));
		try {
			viewForm.setDcsDataRecord(dcsDataManager.getDcsDataRecord(id, rm));
		} catch (Exception e) {
			prtlnErr ("WARNING: could not obtain dcsDataRecord for " + id + ": " + e.getMessage());
		}		
		viewForm.setDocMap(doc);
		viewForm.clearMultiValuesCache();
		viewForm.setHash("");
		
		// Set up form to support navigation within search results
		String prevId = null;
		String nextId = null;
		try {
			int resultIndex = searchHelper.getCurrentRecIndex();
			viewForm.setResultIndex(resultIndex);
			
			// Compute next and prevIds (for nav arrows) only if 
			// there are searchResults AND searchResults contain currentRec
			if (resultIndex != -1 && !searchHelper.isEmpty()) {
				if (resultIndex > 0)
					prevId = searchHelper.getRecId (resultIndex-1);
				if (resultIndex < searchHelper.getNumHits() - 1)
					nextId = searchHelper.getRecId (resultIndex+1);
			}
		}
		catch (Throwable t) {
			prtln (t.getMessage());
			t.printStackTrace();
		}
		
		viewForm.setResults(searchHelper);
		viewForm.setPrevId (prevId);
		viewForm.setNextId (nextId);
			
		saveErrors(request, errors);
		return mapping.findForward("view.record");
	 }

	// -------------- Debug ------------------


	public static void setDebug(boolean isDebugOutput) {
		debug = isDebugOutput;
	}



	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	private void prtln(String s) {
		if (debug)
			System.out.println("DCSViewAction: " + s);
	}
	
	private void prtlnErr(String s) {
			System.out.println("DCSViewAction: " + s);
	}
}

