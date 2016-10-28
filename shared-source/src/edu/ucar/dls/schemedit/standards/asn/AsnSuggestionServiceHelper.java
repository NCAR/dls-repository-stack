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
package edu.ucar.dls.schemedit.standards.asn;

import edu.ucar.dls.schemedit.standards.adn.AsnToAdnMapper;
import edu.ucar.dls.schemedit.standards.CATServiceHelper;
import edu.ucar.dls.schemedit.standards.CATHelperPlugin;
import edu.ucar.dls.schemedit.standards.StandardsRegistry;
import edu.ucar.dls.schemedit.standards.StandardsManager;
import edu.ucar.dls.standards.asn.AsnDocument;
import edu.ucar.dls.schemedit.standards.asn.AsnStandardsDocument;
import edu.ucar.dls.serviceclients.asn.AsnClientToolkit;
import edu.ucar.dls.schemedit.MetaDataFramework;
import edu.ucar.dls.schemedit.action.form.SchemEditForm;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.schemedit.*;

import edu.ucar.dls.serviceclients.cat.CATStandard;

import org.dom4j.*;

import java.io.*;
import java.util.*;
import org.apache.struts.util.LabelValueBean;

import java.net.*;

/**
 *  SuggestionsServiceHelper for the CAT REST standards suggestion service,
 *  operating over ASN Standards.
 *
 * @author    ostwald
 */
public class AsnSuggestionServiceHelper extends CATServiceHelper {
	private static boolean debug = false;

	SelectedStandardsBean selectedStandardsBean = null;
	AsnStandardsDocument standardsDocument = null;
	List extraAvailableDocs = null;
	List noCatDocs = null;


	/**
	 *  Constructor for the AsnSuggestionServiceHelper object
	 *
	 * @param  sef              NOT YET DOCUMENTED
	 * @param  frameworkPlugin  NOT YET DOCUMENTED
	 */
	public AsnSuggestionServiceHelper(SchemEditForm sef, CATHelperPlugin frameworkPlugin) {
		super(sef, frameworkPlugin);
		try {
			this.setStandardsDocument(this.getDefaultDoc());
		} catch (Throwable e) {
			prtlnErr("ERROR: could not set document: " + e.getMessage());
			e.printStackTrace();
		}
	}


	/**
	 *  Gets the current standardsDocument if one has been assigned, or fetches the
	 *  "defaultDoc" from the StandardsRegistry.
	 *
	 * @return    The standardsDocument value
	 */
	public AsnStandardsDocument getStandardsDocument() {
		if (this.standardsDocument == null) {
			prtln("getStandardsDocument: this.standardsDocument is null, obtaining from StandardsRegistry");
			try {
				this.standardsDocument =
					StandardsRegistry.getInstance().getStandardsDocument(this.getDefaultDoc());
			} catch (Throwable t) {
				prtlnErr("could not instantiate AsnStandardsDocument: " + t.getMessage());
			}
		}
		return this.standardsDocument;
	}


	/**
	 *  Sets the current document as specified by provided key
	 *
	 * @param  key            key specifying a standards document
	 * @exception  Exception  Description of the Exception
	 */
	public void setStandardsDocument(String key) throws Exception {
		prtln("setStandardsDocument()  = " + key);
		if (key == null) {
			prtln("WARNING setStandardsDocument got a null key");
			return;
		}
		try {
			AsnStandardsDocument doc = StandardsRegistry.getInstance().getStandardsDocument(key);
			if (doc == null)
				throw new Exception("Standards document not found for " + key);
			this.standardsDocument = doc;
			if (this.getSelectedStandardsBean() == null)
				throw new Exception ("could not obtain SelectedStandardsBean");
			this.getSelectedStandardsBean().setCurrentDocKey(key);
			this.setSuggestedStandards(null);
		} catch (Throwable t) {
			prtlnErr("WARNING: could not setStandardsDocument to \'" + key + "\': " + t.getMessage());
		}
	}



	/**
	 *  Gets the standardsFormat attribute of the AsnSuggestionServiceHelper object
	 *  (hardcoded to "asn").
	 *
	 * @return    The standardsFormat value
	 */
	public String getStandardsFormat() {
		return "asn";
	}


	/**
	 *  Gets the standardsManager attribute of the AsnSuggestionServiceHelper
	 *  object
	 *
	 * @return    The standardsManager value
	 */
	public AsnStandardsManager getStandardsManager() {
		return (AsnStandardsManager) super.getStandardsManager();
	}


	//----------------- from AsnStandardsManager --------------------

	/**
	 *  Gets the otherSelectedStandards attribute of the AsnSuggestionServiceHelper
	 *  object
	 *
	 * @return    The otherSelectedStandards value
	 */
	public Map getOtherSelectedStandards() {
		if (this.selectedStandardsBean != null) {
			return this.selectedStandardsBean.getOtherSelectedStandards();
		}
		return null;
	}


	/**
	 *  Write the list of selected standards, as well as the currentStandards Doc
	 *  to the SelectedStandardsBean
	 */
	public void updateSelectedStandardsBean() {
		if (this.selectedStandardsBean == null)
			this.selectedStandardsBean =
				new SelectedStandardsBean(this.getSelectedStandards(), this.getCurrentDoc());
		else
			this.selectedStandardsBean.update(this.getSelectedStandards(), this.getCurrentDoc());
	}


	/**
	 *  Gets the selectedStandardsBean attribute of the AsnSuggestionServiceHelper
	 *  object
	 *
	 * @return    The selectedStandardsBean value
	 */
	public SelectedStandardsBean getSelectedStandardsBean() {
		return this.selectedStandardsBean;
	}


	/**
	 *  Returns all standards documents available to be cataloged, which consists
	 *  of the availableDocs from the standardsManager plus those documents
	 *  containing cataloged standards that were not originally loaded in the
	 *  standardsManager.
	 *
	 * @return    The availableDocs value
	 * @see       #syncRegistryWithSelectedStandards
	 */
	public List getAvailableDocs() {
		List availableDocs = new ArrayList();
		availableDocs.addAll(getStandardsManager().getAvailableDocs());
		
		if (this.extraAvailableDocs != null) {
			availableDocs.addAll(this.extraAvailableDocs);
		}
		return availableDocs;
	}


	/**
	 *  returns list of all docs that have been loaded in the context of this
	 *  framework, but are not in the configured docSet (stamdardsManager.availableDocs)
	 *
	 * @return    The extraAvailableDocs value
	 */
	public List getExtraAvailableDocs() {
		return this.extraAvailableDocs;
	}


	/**
	 *  Gets a list of all availableDocs (those configured and loaded for a
	 *  particular framework) that are NOT available from the CAT service. The CAT
	 *  UI will not offer suggestions for these standards docs.
	 *
	 * @return    availableDocs for wich CAT cannot offer suggestions.
	 */
	public List getNoCatDocs() {
		if (noCatDocs == null) {
			noCatDocs = new ArrayList();
			if (this.getServiceIsActive()) {
				List availableDocs = this.getAvailableDocs();
				Collection allCatDocs = null;
				Map allCatDocsMap = null;
				try {
					allCatDocsMap = StandardsRegistry.getInstance().getAllCatDocs();
					// allCatDocs = allCatDocMap.values();
				} catch (Exception e) {
					prtln("WARNING: could not calculate noCatDocs: " + e.getMessage());
					return null;
				}
				if (availableDocs == null) {
					prtln("no available docs found?");
					return null;
				}
				for (Iterator i = availableDocs.iterator(); i.hasNext(); ) {
					AsnDocInfo docInfo = (AsnDocInfo) i.next();
					String docId = docInfo.getDocId();
					if (!allCatDocsMap.keySet().contains(docId))
						noCatDocs.add(docInfo);
				}
			}
		}
		return noCatDocs;
	}


	/**
	 *  Gets the key of the current standards doc (i.e., the doc currently selected
	 *  in the CAT UI).
	 *
	 * @return    The currentDoc value
	 */
	public String getCurrentDoc() {
		if (getStandardsDocument() == null) {
			// prtln("getCurrentDoc() - standardsDocument is null, returning empty string!");
			return "";
		}
		return getStandardsDocument().getDocKey();
	}


	/**
	 *  Gets the defaultDoc attribute of the AsnSuggestionServiceHelper object
	 *
	 * @return    The defaultDoc value
	 */
	public String getDefaultDoc() {
		return getStandardsManager().getDefaultDocKey();
	}



	/**
	 *  Gets the idFromCATStandard attribute of the AsnSuggestionServiceHelper
	 *  object
	 *
	 * @param  std  NOT YET DOCUMENTED
	 * @return      The idFromCATStandard value
	 */
	protected String getIdFromCATStandard(CATStandard std) {
		return std.getIdentifier();
	}


	/**
	 *  Ensure that the registry holds docs for all currently selected standards<p>
	 *
	 *  NOTE: the standards manager's availableDocs attribute must be updated for
	 *  the user to be able to select all docs in UI ...
	 */
	public void syncRegistryWithSelectedStandards() {
		StandardsRegistry registry = StandardsRegistry.getInstance();
		List selectedStandards = this.getSelectedStandards();

		/*
			- selected standards may not be known to standards registry,
				in which case they are LOADED into registry
			- they may not be on standardmanager's available doc list,
			    in which case they are added to extraAvailableDocs
		*/
		List unknownStandards = new ArrayList();
		for (Iterator i = selectedStandards.iterator(); i.hasNext(); ) {
			String stdId = (String) i.next();
			if (registry.getStandard(stdId) == null) {
				unknownStandards.add(stdId);
			}
		}

		// load and register the docs for each unknown standardd
		List loadedDocs = new ArrayList(); // id -> key
		for (Iterator i = unknownStandards.iterator(); i.hasNext(); ) {
			String stdId = (String) i.next();
			String docId = null;
			try {
				docId = AsnClientToolkit.getDocIdForStdId(stdId);
			} catch (Exception e) {
				// standards docs that are not "active" will not be resolvable via ASN
				prtlnErr("WARNING: could not get docId for " + stdId + ": " + e.getMessage());
				continue;
			}

			if (!loadedDocs.contains(docId)) {
				AsnDocInfo docInfo = null;
				// prtln ("doc not the standards registry (" + docId + ") - registering...");
				try {
					docInfo = registry.registerId(docId);
				} catch (Exception e) {
					prtlnErr("WARNING: could not obtain doc for " + docId + ": " + e.getMessage());
					continue;
				}
				loadedDocs.add(docId);
			}
		}

		/*
			now we have docs loaded for all selected standards.
			put docs that are not on standardsManager's availableDocs
			onto "extraAvailableDocs" so they will be available to UI
			
			"availableDocIds" allows us to see if a doc is already available via standardsManager
			so we avoid adding it more than once
		*/
		List availableDocIds = new ArrayList();
		for (Iterator i = this.getStandardsManager().getAvailableDocs().iterator(); i.hasNext(); ) {
			AsnDocInfo docInfo = (AsnDocInfo) i.next();
			if (docInfo == null) {
				continue;
			}
			availableDocIds.add(docInfo.getDocId());
		}
		this.extraAvailableDocs = new ArrayList();

		for (Iterator i = selectedStandards.iterator(); i.hasNext(); ) {
			String stdId = (String) i.next();
			AsnStandardsNode stdNode = registry.getStandardsNode(stdId);
			if (stdNode == null) {
				prtln("WARNING: stdNode not found for " + stdId);
				continue;
			}
			String docId = stdNode.getDocId();
			if (!availableDocIds.contains(docId)) {
				AsnDocInfo infoToAdd = registry.getDocInfo(registry.getKey(docId));
				this.extraAvailableDocs.add(infoToAdd);
				this.getStandardsManager().addExtraDoc(infoToAdd);
				availableDocIds.add (docId); // so we only add extra docs once
			}
		}

	}



	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "AsnSuggestionServiceHelper");
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtlnErr(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "AsnSuggestionServiceHelper");
		}
	}


	/**
	 *  Debugging
	 *
	 * @param  standardsList  A list of StandardsWrapper instances to display.
	 */
	public void displaySuggestions(List standardsList) {
		prtln(standardsList.size() + " items returned by suggestion service ...");
		for (int i = 0; i < standardsList.size(); i++) {
			CATStandard std = (CATStandard) standardsList.get(i);
			String id = std.getIdentifier();
			String text = std.getText();
			prtln("\n" + i + "\n" + id + "\n" + text + "\n");
		}
	}

}

