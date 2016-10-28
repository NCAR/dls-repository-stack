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
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.url.UrlHelper;
import edu.ucar.dls.schemedit.dcs.*;
import edu.ucar.dls.schemedit.config.*;
import edu.ucar.dls.schemedit.security.user.User;
import edu.ucar.dls.schemedit.action.form.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.util.strings.*;

import org.dom4j.Document;

import java.util.*;
import java.net.*;
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

/**
 *  A Struts Action controlling interaction during creation of ADN records in
 *  the DCS.<p>
 *
 *  Ensures that new records have unique ID's across their collection. Works in
 *  conjunction with {@link edu.ucar.dls.schemedit.action.form.CreateADNRecordForm}.
 *
 * @author    Jonathan Ostwald
 */
public class CreateADNRecordAction extends CreateRecordAction {

	private static boolean debug = false;


	/**
	 *  Gets the xmlFormat attribute of the CreateADNRecordAction object
	 *
	 * @return    The xmlFormat value
	 */
	protected String getXmlFormat() {
		return "adn";
	}


	/**
	 *  Gets the createForward attribute of the CreateADNRecordAction object
	 *
	 * @param  mapping  NOT YET DOCUMENTED
	 * @return          The createForward value
	 */
	protected ActionForward getCreateForward(ActionMapping mapping) {
		return mapping.findForward("adn.create");
	}


	/**
	 *  Gets the confirmForward attribute of the CreateADNRecordAction object
	 *
	 * @param  mapping   NOT YET DOCUMENTED
	 * @param  carForm   NOT YET DOCUMENTED
	 * @param  request   NOT YET DOCUMENTED
	 * @param  response  NOT YET DOCUMENTED
	 * @return           The confirmForward value
	 */
	protected ActionForward getConfirmForward(ActionMapping mapping,
	                                          CreateADNRecordForm carForm,
	                                          HttpServletRequest request,
	                                          HttpServletResponse response) {
		return mapping.findForward("adn.confirm");
	}

	// --------------------------------------------------------- Public Methods

	/**
	 *  Create an empty collection metadata document and populate from ActionForm
	 *  (carForm). ActionErrors available to pass messages back up call chain (but
	 *  not currently used).
	 *
	 * @param  carForm        the ActionForm
	 * @param  adnFramework   NOT YET DOCUMENTED
	 * @return                Description of the Return Value
	 * @exception  Exception  Description of the Exception
	 */
	protected Document makeRecordDoc(CreateADNRecordForm carForm,
	                                 MetaDataFramework framework,
									 ActionMapping mapping,
									 HttpServletRequest request,
									 HttpServletResponse response)
		 throws Exception {

		// create a miminal document and then insert values from carForm
		String id = carForm.getRecId();
		String collection = carForm.getCollection();
		CollectionConfig collectionConfig = null;
		if (collectionRegistry != null)
			collectionConfig = collectionRegistry.getCollectionConfig(collection, false);
		
		if (collectionConfig == null)
			throw new Exception ("Unable to find collection configuration for \"" + collection + "\"");
		
		Document doc = framework.makeMinimalRecord(id, collectionConfig);

		// use docMap as wraper for Document
		DocMap docMap = new DocMap(doc, framework.getSchemaHelper());

		try {
			/*
			load the dlese-collect metadata document with values from the form
			pattern for smartPut: docMap.smartPut(xpath, value)
			recId is already inserted by MetaDataFramework.makeMinimalDocument
			*/
			//title
			docMap.smartPut("/itemRecord/general/title", carForm.getTitle());

			// description
			docMap.smartPut("/itemRecord/general/description", carForm.getDescription());

			// primaryURL
			docMap.smartPut("/itemRecord/technical/online/primaryURL", carForm.getPrimaryUrl());

			// creationDate
			String creationDate = SchemEditUtils.simpleDateString(new Date());
			docMap.smartPut("/itemRecord/metaMetadata/dateInfo/@created", creationDate);

			try {
				User sessionUser = this.getSessionUser(request);
				if (sessionUser != null)
					insertUserInfo(docMap, sessionUser);
			} catch (Throwable t) {
				prtln("insertUserInfo error: " + t.getMessage());
				t.printStackTrace();
			}

			// now prepare document to write to file by inserting namespace information
			doc = framework.getWritableRecord(doc);

		} catch (Exception e) {
			throw new Exception("makeRecord error: " + e);
		}
		return doc;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  docMap         NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	private void insertUserInfo(DocMap docMap, User user) throws Exception {
		String creationDate = SchemEditUtils.simpleDateString(new Date());
		docMap.smartPut("/itemRecord/metaMetadata/contributors/contributor/@date", creationDate);
		docMap.smartPut("/itemRecord/metaMetadata/contributors/contributor/@role", "Creator");
		docMap.smartPut("/itemRecord/metaMetadata/contributors/contributor/person/nameFirst", user.getFirstName());
		docMap.smartPut("/itemRecord/metaMetadata/contributors/contributor/person/nameLast", user.getLastName());
		docMap.smartPut("/itemRecord/metaMetadata/contributors/contributor/person/instName", user.getInstitution());
		docMap.smartPut("/itemRecord/metaMetadata/contributors/contributor/person/instDept", user.getDepartment());
		docMap.smartPut("/itemRecord/metaMetadata/contributors/contributor/person/emailPrimary", user.getEmail());
	}


	/**
	 *  Validate the input from user. Put changed or default values into carForm.
	 *  After this method returns carForm (rather than request) is used to process
	 *  user input
	 *
	 * @param  request       Description of the Parameter
	 * @param  carForm       Description of the Parameter
	 * @param  adnFramework  NOT YET DOCUMENTED
	 * @return               Description of the Return Value
	 */
	protected ActionErrors validateForm(HttpServletRequest request,
	                                    CreateADNRecordForm carForm,
	                                    MetaDataFramework adnFramework) {
		ActionErrors errors = new ActionErrors();

		String title = request.getParameter("title");
		if ((title != null) && (title.trim().equals(""))) {
			errors.add("title", new ActionError("field.required", "title"));
		}
		else {
			carForm.setTitle(title.trim());
		}

		String primaryUrl = request.getParameter("primaryUrl");
		// does url value exist?
		if ((primaryUrl != null) && (primaryUrl.trim().equals(""))) {
			errors.add("primaryUrl", new ActionError("field.required", "Url"));
			return errors;
		}

		try {
			primaryUrl = UrlHelper.normalize(primaryUrl);
			UrlHelper.validateUrl(primaryUrl);
		} catch (MalformedURLException e) {
			if (e.getMessage() == null)
				errors.add("primaryUrl", new ActionError("generic.error", "malformed url"));
			else
				errors.add("primaryUrl", new ActionError("generic.error", "malformed url: " + e.getMessage()));
			return errors;
		}

		primaryUrl = primaryUrl.trim();
		carForm.setPrimaryUrl(primaryUrl);

		//  check dups if this framework has specified primaryUrl as a "uniqueUrl" valueType
		if (adnFramework.getUniqueUrlPath() != null) {
			List dups = repositoryService.getDups(primaryUrl, carForm.getCollection());
			carForm.setDups(dups);
			if (dups.size() > 0) {
				errors.add("primaryUrl", new ActionError("invalid.url", "URL already cataloged in this collection"));
			}
		}

		return errors;
	}


	/**
	 *  Sets the debug attribute of the CreateADNRecordAction class
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
			System.out.println("CreateADNRecordAction: " + s);
		}
	}
}

