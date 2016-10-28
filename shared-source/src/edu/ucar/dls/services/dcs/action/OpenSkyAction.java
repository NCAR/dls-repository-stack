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
import edu.ucar.dls.services.dcs.action.form.RecommenderForm;
import edu.ucar.dls.dds.*;
import edu.ucar.dls.dds.action.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.DocMap;
import edu.ucar.dls.repository.*;
import edu.ucar.dls.index.reader.*;
import edu.ucar.dls.index.*;
import edu.ucar.dls.util.*;
import edu.ucar.dls.index.writer.*;
import edu.ucar.dls.webapps.servlets.filters.GzipFilter;
import edu.ucar.dls.vocab.MetadataVocab;
import org.apache.lucene.search.*;
import org.apache.lucene.queryParser.QueryParser;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.action.DCSAction;
import edu.ucar.dls.schemedit.repository.RepositoryService;
import edu.ucar.dls.schemedit.config.IDGenerator;
import edu.ucar.dls.schemedit.threadedservices.ExportingService;
import edu.ucar.dls.schemedit.dcs.*;
import edu.ucar.dls.schemedit.config.*;

import java.util.*;
import java.text.*;
import java.util.regex.*;
import java.io.*;
import java.net.URL;
import java.util.Hashtable;
import java.util.Locale;
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
 *  An <strong>Action</strong> that handles interaction with the OpenSky
 *  Submission UI.<p>
 *
 *  OpenSkyAction only writes new records to the DCS, so we don't have to worry
 *  about locking issues.<p>
 *
 *  This class must save revisions of records, which is done by assigning a
 *  revision id (e.g., 'DLESE-000-000-099-a'). There are some fields the
 *  Submission UI doesn't handle. When writing the revision record, these fields
 *  are filled from the Reference version.
 *
 *@author    Jonathan Ostwald
 *@see       edu.ucar.dls.services.dcs.action.form.RecommenderForm
 */
public final class OpenSkyAction extends DCSAction {
	private static boolean debug = true;

	public final static String PUT_RECORD = "PutRecord";
	public final static String SUBMITTED_STATUS = "Submitted";


	// --------------------------------------------------------- Public Methods

	/**
	 *  Processes the DDS web service request by forwarding to the appropriate
	 *  corresponding JSP page for rendering.
	 *
	 *@param  mapping        The ActionMapping used to select this instance
	 *@param  request        The HTTP request we are processing
	 *@param  response       The HTTP response we are creating
	 *@param  form           The ActionForm for the given page
	 *@return                The ActionForward instance describing where and how
	 *      control should be forwarded
	 *@exception  Exception  If error.
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
		RecommenderForm recForm = null;
		try {

			recForm = (RecommenderForm) form;

			// how to notify of configuration errrors??
			ActionErrors errors = initializeFromContext(mapping, request);
			if (!errors.isEmpty()) {
				List errorList = new ArrayList();
				for (Iterator i = errors.get(); i.hasNext(); ) {
					ActionError err = (ActionError) i.next();
					prtln("\t" + err.toString());
					errorList.add(err.toString());
				}
				recForm.setErrorList(errorList);
				prtln("initializeFromContext errors (" + errors.size() + ")");
				logErrors(errorList);
				return mapping.findForward("dcsservices.error");
			}

			// Grab the DDS service request verb:
			String verb = request.getParameter("verb");
			if (verb == null) {
				recForm.setErrorMsg("The verb argument is required. Please indicate the request verb");
				return (mapping.findForward("dcsservices.error"));
			} // Handle put record request:
			else if (verb.equals(PUT_RECORD)) {
				return doPutRecord(request, response, recForm, mapping);
			} // The verb is not valid for the DDS web service
			else {
				recForm.setErrorMsg("The verb argument '" + verb + "' is not valid");
				return (mapping.findForward("dcsservices.error"));
			}
		} catch (NullPointerException npe) {
			prtln("OpenSkyAction caught exception. " + npe);
			npe.printStackTrace();
			recForm.setErrorMsg("There was an internal error by the server: " + npe);
			return (mapping.findForward("dcsservices.error"));
		} catch (Throwable e) {
			prtln("OpenSkyAction caught exception. " + e);
			e.printStackTrace();
			recForm.setErrorMsg("There was an internal error by the server: " + e);
			return (mapping.findForward("dcsservices.error"));
		}
	}



	/**
	 *  Handles a request to put a metadata record into the repository. Wraps
	 *  {@link edu.ucar.dls.repository.RepositoryManager}.putRecord and therefore
	 *  requires the same arguments.<p>
	 *
	 *  Currently allows existing records to be overwritten. <p>
	 *
	 *  Arguments: recordXml, xmlFormat, collection, and id.<p>
	 *
	 *  Error Exception Conditions: <br>
	 *  badArgument - The request includes illegal arguments.
	 *
	 *@param  request        The HTTP request
	 *@param  response       The HTTP response
	 *@param  recForm        The Form bean
	 *@param  mapping        ActionMapping used
	 *@return                An ActionForward to the JSP page that will handle the
	 *      response
	 *@exception  Exception  If error.
	 */
	protected ActionForward doPutRecord(
			HttpServletRequest request,
			HttpServletResponse response,
			RecommenderForm recForm,
			ActionMapping mapping)
			 throws Exception {

		prtln("doPutRecord()");
		boolean doAuthorize = false;

		// only trusted ips may perform put record
		prtln("authorizing");
		if (doAuthorize) {
			if (isAuthorized(request, "trustedIp")) {
				recForm.setAuthorizedFor("trustedUser");
			} else {
				recForm.setErrorMsg("You are not authorized to use the DCS Recommender service");
				return mapping.findForward("dcsservices.error");
			}
		}

		List errorList = new ArrayList();
		prtln("validating required arguments");

		// validate presence of required arguments
		String recordXmlParam = request.getParameter("recordXml");
		if (recordXmlParam == null || recordXmlParam.length() == 0) {
			errorList.add("The \'recordXml\' argument is required but is missing or empty");
		}

		CollectionConfig collectionConfig = null;
		String collectionParam = request.getParameter("collection");
		if (collectionParam == null || collectionParam.length() == 0) {
			errorList.add("The \'collection\' argument is required but is missing or empty");
		} else {
		    collectionConfig = this.collectionRegistry.getCollectionConfig(collectionParam, false);
			if (collectionConfig == null) {
				errorList.add("The specified collection, \"" + collectionParam + "\" does not exist");
			}
		}

		if (errorList.size() > 0) {
			recForm.setErrorList(errorList);
			logErrors(errorList);
			return mapping.findForward("dcsservices.error");
		}

		prtln("required request params validated");

		/*
			GENERATE NEW ID - if this is a REVISION we have to generate a revision ID
		*/
		Document recordDoc = this.getRecordDoc(recordXmlParam, collectionConfig);
		String recId = this.getRecordId(recordDoc, collectionConfig);
		prtln ("RECORD ID: " + recId);
		if (recId != null && recId.trim().length() > 0) {
			try {
				recId = this.getRevisionId (recId);
			} catch (Exception e) {
				errorList.add (e.getMessage());
				recForm.setErrorList(errorList);
				logErrors(errorList);
				return mapping.findForward("dcsservices.error");
			}				
		} else {
			recId = collectionConfig.nextID();
		}
		
		String itemRecordXml = null;
		String xmlFormat = collectionConfig.getXmlFormat();
		try {
			itemRecordXml = makeItemRecordXml(recordDoc, recId, collectionConfig);
		} catch (Exception e) {
			errorList.add(e.getMessage());
			recForm.setErrorList(errorList);
			logErrors(errorList);
			return mapping.findForward("dcsservices.error");
		}

		try {
			repositoryManager.putRecord(itemRecordXml, xmlFormat, collectionParam, recId, true);
		} catch (RecordUpdateException e) {
			recForm.setErrorMsg(e.getMessage());
			return mapping.findForward("dcsservices.error");
		}

		recForm.setId(recId);

		/*
		 *  Here is where we would set Status and StatusNote ..
		 */
		try {
			// create a status entry
			DcsDataRecord dcsDataRecord = dcsDataManager.getDcsDataRecord(recId, repositoryManager);

			String changeDate = dcsDataRecord.getChangeDate();
			dcsDataRecord.deleteStatusEntry(changeDate);
			// prtln (Dom4jUtils.prettyPrint (dcsDataRecord.getDocument()));

			// String dcsStatus = StatusFlags.RECOMMENDED_STATUS;
			String dcsStatus = SUBMITTED_STATUS;
			String dcsStatusNote = "submitted via OpenSkySubmission service";
			this.repositoryService.validateRecord(itemRecordXml, dcsDataRecord, xmlFormat);

			dcsDataRecord.updateStatus(dcsStatus, dcsStatusNote, Constants.UNKNOWN_EDITOR);

			// prtln ("status record for recommended record");
			// prtln (Dom4jUtils.prettyPrint (dcsDataRecord.getDocument()));
			repositoryService.updateRecord(recId);
			dcsDataRecord.flushToDisk();
		} catch (Throwable e) {
			prtln("WARNING: error updating DcsDataRecord: " + e.getMessage());
			return mapping.findForward("dcsservices.error");
		}
		return mapping.findForward("dcsservices.PutRecord");
	}

	private String getRevisionId (String recId)  throws Exception {
		// do a search for all recIds starting with recId
		
		SimpleLuceneIndex index = this.repositoryManager.getIndex();
		if (index == null) {
			prtlnErr ("Index not found");
		}
		
		String q = "id:" + SimpleLuceneIndex.encodeToTerm(recId + "*", false);
		QueryParser qp = index.getQueryParser();
		
		BooleanQuery simpleQuery = new BooleanQuery();
		try {
			simpleQuery.add(qp.parse(q), BooleanClause.Occur.MUST);
		} catch (Exception e) {
			prtlnErr ("QueryParse error: " + e.toString());
		}
		
		ResultDocList results = index.searchDocs (simpleQuery);
		List ids = new ArrayList();
		if (results != null)
		prtln (results.size() + " results");
		
		if (results.size() == 0)
			throw new Exception ("base record not found for provided base revisionID (" + recId + ")"); 
		
		for (Iterator i=results.iterator();i.hasNext();) {
			ResultDoc resultDoc = (ResultDoc)i.next();
			String id = (String)resultDoc.getDocMap().get("idvalue");
			ids.add (id);
		}
		
		Collections.sort (ids);
		String last = (String)ids.get(ids.size()-1);
		String rl = SchemEditUtils.getRevisionLetter(last);
		char rl_next = 'a';
		if (rl != null) {
			rl_next = rl.charAt(0);
			rl_next++;
		}
		return recId + "-" + rl_next;
	}
	
/* 	static String getRevisionLetter (String id) {
		// Pattern p = Pattern.compile("[\\d]");
		Pattern p = Pattern.compile(".*?[\\d]{3}-[\\d]{3}-[\\d]{3}-([^0-9]+)");
		Matcher m = p.matcher(id);
		if (m.matches()) {
			return m.group(1);
		}
		return null;
	} */

	/**
	 *  Description of the Method
	 *
	 *@param  errorList  Description of the Parameter
	 */
	private void logErrors(List errorList) {
		if (errorList.isEmpty()) {
			return;
		}
		prtln("Recommender service request errors: ");
		for (Iterator i = errorList.iterator(); i.hasNext(); ) {
			prtln("\t" + (String) i.next());
		}
	}

	private String getRecordId (Document record, CollectionConfig collectionConfig) {
		// use xpath.valueOf()
		String idPath = null;
		try {
			idPath = this.getRecordIdPath(collectionConfig);
		} catch (Exception e) {
			prtlnErr ("could not getRecordIdPath: " + e.getMessage());
			return idPath;
		}
		prtln ("xpath: " + idPath);
		XPath xpath = DocumentHelper.createXPath(idPath);
		Element idNode = (Element)xpath.selectSingleNode(record);
		if (idNode != null) {
			prtln ("idNode found: " + idNode.asXML());
			return idNode.getTextTrim();
		}
		else
			prtln ("idNode not found at " + xpath.toString());
		return null;
	}
	
	/** returns a localized document */
	private Document getRecordDoc (String recordXml, CollectionConfig collectionConfig) throws Exception {
		Document itemRecord = null;
		try {
			itemRecord = DocumentHelper.parseText(recordXml);
		} catch (Exception e) {
			throw new Exception("Provided recordXML is not well formed");
		}
		String xmlFormat = collectionConfig.getXmlFormat();
		prtln ("xmlFormat: " + xmlFormat);
		if (xmlFormat == null || xmlFormat.trim().length() == 0)
			prtln (collectionConfig.toString());
		
		MetaDataFramework framework = null;
		try {
			framework = this.frameworkRegistry.getFramework(xmlFormat);
			if (framework == null) {
				throw new Exception("MetadataFramework not loaded for \"" + xmlFormat + "\"");
			}

			if (!framework.getSchemaHelper().getNamespaceEnabled()) {
				itemRecord = Dom4jUtils.localizeXml(itemRecord);
			}

		} catch (Exception e) {
			throw new Exception("Server Configuration error: " + e.getMessage());
		}
		return itemRecord;
	}
	
	
	private String getRecordIdPath (CollectionConfig collectionConfig) throws Exception {
		String xmlFormat = collectionConfig.getXmlFormat();
		String idPath = null;
		MetaDataFramework framework = null;
		try {
			framework = this.frameworkRegistry.getFramework(xmlFormat);

			idPath = framework.getIdPath();
			if (idPath == null || idPath.trim().length() == 0) {
				throw new Exception("id path not configured for \"" + xmlFormat + "\" framework");
			}
		} catch (Exception e) {
			throw new Exception("Server Configuration error: " + e.getMessage());
		}
		return idPath;
	}
		

	/**
	 *  Inserts the provided recId in the provided recordXml and returns the record ready to be written to disk
	 *
	 *@param  recordXml         the XML record as a string
	 *@param  recId             the record ID to insert
	 *@param  collectionConfig  collection Config
	 *@return                   Description of the Return Value
	 *@exception  Exception     Description of the Exception
	 */
	private String makeItemRecordXml(Document itemRecord,
			String recId,
			CollectionConfig collectionConfig) throws Exception {

		String xmlFormat = collectionConfig.getXmlFormat();
		
		try {
			DocMap docMap = new DocMap(itemRecord);
			String idPath = this.getRecordIdPath(collectionConfig);
			docMap.smartPut(idPath, recId);
			
			// HERE IS WHERE WE DO THE HOCUS-POCUS WITIH FIELDS NOT MANAGED IN SUBMISSION UI

		} catch (Exception e) {
			throw new Exception("Server error: unable to assign id to record: " + e);
		}
		
		MetaDataFramework framework = this.frameworkRegistry.getFramework(xmlFormat);
		if (framework == null)
			throw new Exception ("metadata framework not found for " + xmlFormat);
		return framework.getWritableRecordXml(itemRecord);
			
	}


	/**
	 *  Checks for IP authorization
	 *
	 *@param  request       HTTP request
	 *@param  securityRole  Security role
	 *@return               True if authorized, false otherwise.
	 */
	private boolean isAuthorized(HttpServletRequest request, String securityRole) {
		prtln("ip of requester: " + request.getRemoteAddr());
		if (securityRole.equals("trustedIp")) {
			String[] trustedIps = repositoryManager.getTrustedWsIpsArray();
			if (trustedIps == null) {
				return false;
			}

			String IP = request.getRemoteAddr();
			for (int i = 0; i < trustedIps.length; i++) {
				if (IP.matches(trustedIps[i])) {
					return true;
				}
			}
		}
		return false;
	}



	// --------------- Debug output ------------------

	/**
	 *  Return a string for the current time and date, sutiable for display in log
	 *  files and output to standout:
	 *
	 *@return    The dateStamp value
	 */
	protected final static String getDateStamp() {
		return
				new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 *@param  s  The text that will be output to error out.
	 */
	protected final void prtlnErr(String s) {
		System.err.println(getDateStamp() + " " + s);
	}



	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 *@param  s  The String that will be output.
	 */
	protected final void prtln(String s) {
		if (debug) {
			System.out.println("OpenSkyAction: " + s);
		}
	}


	/**
	 *  Sets the debug attribute of the object
	 *
	 *@param  db  The new debug value
	 */
	public static void setDebug(boolean db) {
		debug = db;
	}
}

