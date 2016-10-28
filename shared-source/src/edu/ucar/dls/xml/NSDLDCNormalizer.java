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
 *  Copyright 2002-2012 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */
package edu.ucar.dls.xml;

import java.io.*;
import java.util.*;
import java.net.URL;
import java.net.MalformedURLException;

import java.text.*;
import java.io.*;

import org.dom4j.*;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.URLConnectionTimedOutException;
import java.net.URLEncoder;

/** ***********DEPRECATED *********
 *  This is not longer used with the new indexer. All functionality listed here
 *  was moved to the harvest manager. So all normalization would be done in the same place
 *  This is only here for historical purposes.
 *  DO NOT USE THIS FILE for normalization since if it tries to normalize an already normalized documents
 *  errors will possibly make there way into the documents
 *  
 *  Normalizes NSDL DC records to conform to standard vocabularies in specific data elements like audience,
 *  education level, and type, and to insert standard data elements. Uses groups files to define the data
 *  elements and vocabularies. Lazy-loads the groups files and caches them once loaded. Create a new
 *  NSDLDCNormalizer to release the cache and refresh the vocabularies.<p>
 *
 *  See example groups files and documentation at dds-project/web/WEB-INF/conf/nsdlDcNormalizationConfigs <p>
 *
 *  Groups files have been saved to http://ns.nsdl.org/ncs/ddsws/1-1/groupsNormal/<p>
 *
 *
 *
 * @author    John Weatherley
 */
public class NSDLDCNormalizer {
	private static boolean debug = true;
	
	// NSDL TOU gets inserted into nsdl_dc and lar records:
	public static String NSDL_TOU = "The National Science Digital Library (NSDL), located at the University Corporation for Atmospheric Research (UCAR), provides these metadata terms: These data and metadata may not be reproduced, duplicated, copied, sold, or otherwise exploited for any commercial purpose that is not expressly permitted by NSDL.";
	
	// Comment gets inserted into nsdl_dc and lar records:
	public static String NSDL_COMMENT = "The National Science Digital Library (NSDL) has normalized this metadata record for use across its systems and services.";

	
	private String nsdlDcVocabSelectionsUrlStr = null;
	private boolean writeVerboseComments = false;
	private Document _vocabSelectionsDom = null;

	private final static String userAgent = "NSDL DC Normalization Processor";


	/**
	 *  Constructs a NSDLDCNormalizer object. Create a new NSDLDCNormalizer to refresh the vocabularies.
	 *
	 * @param  nsdlDcVocabSelectionsUrl  nsdlDcVocabSelectionsUrl
	 * @param  writeVerboseComments      True to write verbose comments into the normalized record about what
	 *      terms were changed
	 */
	public NSDLDCNormalizer(String nsdlDcVocabSelectionsUrl, boolean writeVerboseComments) {
		this.nsdlDcVocabSelectionsUrlStr = nsdlDcVocabSelectionsUrl;
		this.writeVerboseComments = writeVerboseComments;
	}


	private List _xpathsToModify = null;
	private Map _xpathsToModifyKeepExisting = null;
	private Map _groupsFileDoms = null;
	private Map _fromXpathsLists = null;


	/**
	 *  Normalizes the given NSDL DC record.
	 *
	 * @param  nsdlDcRecordDoc                     An NSDL DC record Document
	 * @param  metadataHandle                      The NSDL metadata handle for this record
	 * @param  resourceHandles                     A List of resource handle Strings corresponding to the NSDL
	 *      handles for this resource, or null if none available
	 * @param  nsdlPartnerId                       Partner ID to identifier, or null if none
	 * @param  larStatus                           The LAR status that should be indicated in this record
	 * @param  requiredXpaths                      XPaths that are required to be non-empty for the records to be
	 *      valid
	 * @return                                     Normalized NSDL DC record Document
	 * @exception  NSDLDCNormalizerXpathException  Indicates a required XPath was missing or empty in the NSDL DC
	 *      record
	 */
	public Document normalizeNsdlDcRecord(Document nsdlDcRecordDoc, String metadataHandle, List resourceHandles, String nsdlPartnerId, String larStatus, List requiredXpaths) throws NSDLDCNormalizerXpathException {
		Document newDoc = null;
		try {

			// Verify that all required XPaths are non-empty:
			if (requiredXpaths != null) {
				for (int i = 0; i < requiredXpaths.size(); i++) {
					String requiredXpath = (String) requiredXpaths.get(i);
					Node elm = nsdlDcRecordDoc.selectSingleNode(requiredXpath);
					if (elm == null || elm.getText() == null || elm.getText().trim().length() == 0) {
						throw new NSDLDCNormalizerXpathException(requiredXpath);
					}
				}
			}

			initAllData();
			if (_xpathsToModify == null || _xpathsToModify.size() == 0 || nsdlDcRecordDoc == null)
				return nsdlDcRecordDoc;

			newDoc = (Document) nsdlDcRecordDoc.clone();

			// Insert the NSDL resource handles:
			if (resourceHandles != null) {
				for (int i = 0; i < resourceHandles.size(); i++) {
					String handle = ((String) resourceHandles.get(i)).trim();
					if (handle.startsWith("2200")) {
						Element newElm = newDoc.getRootElement().addElement("dc:identifier");
						newElm.setText("hdl:" + handle);
						newElm.addAttribute("xsi:type", "nsdl_dc:ResourceHandle");
					}
				}
			}

			// Insert the NSDL metadata handle (production handles start with 2200/, dev handles 2200.1/):
			if (metadataHandle != null && metadataHandle.startsWith("2200")) {
				Element newElm = newDoc.getRootElement().addElement("dc:identifier");
				newElm.setText("hdl:" + metadataHandle);
				newElm.addAttribute("xsi:type", "nsdl_dc:MetadataHandle");
			}

			// Insert the partner ID:
			if (nsdlPartnerId != null && nsdlPartnerId.trim().length() > 0) {
				Element newElm = newDoc.getRootElement().addElement("dc:identifier");
				newElm.setText(nsdlPartnerId);
				newElm.addAttribute("xsi:type", "nsdl_dc:NSDLPartnerID");
			}

			for (int i = 0; i < _xpathsToModify.size(); i++) {
				Map foundValues = new HashMap();
				// Track values that have been created to avoid duplicating
				String xPathToModify = (String) _xpathsToModify.get(i);
				boolean keepExistingValues = _xpathsToModifyKeepExisting.containsKey(xPathToModify);

				// First remove all the existing elements from the new Document corresponding to this XPath, if indicated:
				if (!keepExistingValues) {
					List existingElms = newDoc.selectNodes(xPathToModify);
					for (int ii = 0; ii < existingElms.size(); ii++)
						((Node) existingElms.get(ii)).detach();
				}

				Document groupsDom = (Document) _groupsFileDoms.get(xPathToModify);
				List fromXpathsList = (List) _fromXpathsLists.get(xPathToModify);

				// Only process if we've got something to process...
				if (groupsDom != null && fromXpathsList != null || fromXpathsList.size() > 0) {

					// For each xPath to pull from, grab it's value and translate to the new value:
					for (int j = 0; j < fromXpathsList.size(); j++) {
						String fromXpath = (String) fromXpathsList.get(j);
						//prtln("fromXpath: " + fromXpath);
						List existingDCElms = nsdlDcRecordDoc.selectNodes(fromXpath);
						for (int k = 0; k < existingDCElms.size(); k++) {
							String existingValue = ((Node) existingDCElms.get(k)).getText();
							//prtln("existingValue: " + existingValue);
							//prtln("groupsDom:\n" + groupsDom.asXML());

							// Fetch the outline nodes that contain the existing value from the record:
							List outlineNodes = groupsDom.selectNodes("/opml/body//outline[@vocab='" + existingValue + "']");
							//prtln("numOutlineNodes for existingValue: " + outlineNodes.size());
							for (int m = 0; m < outlineNodes.size(); m++) {

								Node outlineNode = (Node) outlineNodes.get(m);
								//prtln("outlineNode vocab: " + outlineNode.valueOf("@vocab").trim());

								// Fetch the nodes that define the new value for the term:
								List newValueOutlineNodes = outlineNode.selectNodes(".[@type='group']|..[@type='group']|../..[@type='group']");
								// Go one and two levels up, for nested vocabs like EdLevel
								//prtln("numNewValueOutlineNodes: " + newValueOutlineNodes.size());
								for (int n = 0; n < newValueOutlineNodes.size(); n++) {
									Node newValueOutlineNode = (Node) newValueOutlineNodes.get(n);
									String newValue = newValueOutlineNode.valueOf("@vocab").trim();
									if (!foundValues.containsKey(newValue)) {
										foundValues.put(newValue, new Object());
										//prtln("xPathToModify:" + xPathToModify + " newElmName:" + newElmName + " existingValue:'" + existingValue + "' newValue:'" + newValue + "' xsiType:'" + xsiType + "'");

										// Remove existing terms if necessary, and add comments:
										if (existingValue.equals(newValue)) {
											if (writeVerboseComments)
												newDoc.getRootElement().addComment("'" + existingValue + "' is a standard vocabulary term");
											// If keeping existing values, remove all the existing elements for this term to avoid duplicate terms:
											if (keepExistingValues) {
												List existingElms = newDoc.selectNodes(xPathToModify + "[. = '" + existingValue + "']");
												for (int ii = 0; ii < existingElms.size(); ii++)
													((Node) existingElms.get(ii)).detach();
											}
										}
										else {
											if (writeVerboseComments)
												newDoc.getRootElement().addComment("'" + existingValue + "' was normalized to standard vocabulary term '" + newValue + "'");
										}

										// Add the new vocab term:
										String xsiType = newValueOutlineNode.valueOf("@attribution").trim();
										String newElmName = XPathUtils.getNodeName(xPathToModify);
										Element newElm = newDoc.getRootElement().addElement(newElmName);
										newElm.setText(newValue);
										if (xsiType.length() > 0)
											newElm.addAttribute("xsi:type", xsiType);
									}
									// Comment on other terms that were converted:
									else {
										//newDoc.getRootElement().addComment("'" + existingValue + "' in " + xPathToModify + " was also normalized to standard vocabulary term '" + newValue + "'");
									}
								}
							}
						}
					}
				}
			}

			// Ensure we have the LAR namespace defined:
			newDoc.getRootElement().addNamespace("lar", "http://ns.nsdl.org/schemas/dc/lar");
			// Remove any existing LAR status that may be indicated:
			List existingLarElms = newDoc.selectNodes("/nsdl_dc:nsdl_dc/lar:readiness");
			for (int ii = 0; ii < existingLarElms.size(); ii++)
				((Node) existingLarElms.get(ii)).detach();
			// Insert the LAR status (of the form <lar:readiness xsi:type="lar:Ready">Fully ready</lar:readiness>):
			if (larStatus == null || larStatus.trim().length() == 0)
				larStatus = "Not ready";
			Element newLarElm = newDoc.getRootElement().addElement("lar:readiness");
			newLarElm.setText(larStatus);
			newLarElm.addAttribute("xsi:type", "lar:Ready");			
			
			// Remove existing TOU to make sure we do not duplicate:
			List existingTouElms = newDoc.selectNodes("/nsdl_dc:nsdl_dc/lar:metadataTerms");
			for (int ii = 0; ii < existingTouElms.size(); ii++)
				((Node) existingTouElms.get(ii)).detach();

			// Insert NSDL TOU:
			String tou = NSDL_TOU + " More information is available at: http://nsdl.org/help/terms-of-use."; 
			newDoc.getRootElement().addElement("lar:metadataTerms").setText(tou);

			// Insert boilerplate comment:
			//comment += "\nTerms in the following fields have been normalized to conform to standard vocabularies: " + Arrays.toString(_xpathsToModify.toArray(new String[]{}));
			//comment += "\nNon-conforming terms have been removed from each of these fields except for: " + Arrays.toString(_xpathsToModifyKeepExisting.keySet().toArray(new String[]{}));
			newDoc.getRootElement().addComment(NSDL_COMMENT);

			//prtln("exisingNsdlDC:\n" + nsdlDcRecordDoc.asXML());
			//prtln("newNsdlDC:\n" + Dom4jUtils.prettyPrint(newDoc));
		} catch (NSDLDCNormalizerXpathException ne) {
			throw ne;
		} catch (Throwable t) {
			prtlnErr("Error normalizing NSDL DC record: " + t);
			t.printStackTrace();
			return nsdlDcRecordDoc;
		}

		//prtln("Record normalized...");
		return newDoc;
	}


	private void initAllData() {
		// Init only if not done so previously:
		if (_vocabSelectionsDom == null) {
			try {
				_vocabSelectionsDom = Dom4jUtils.getXmlDocument(nsdlDcVocabSelectionsUrlStr, 5000, userAgent);
			} catch (Throwable t) {
				prtlnErr("Error fetching vocab selections file from URL '" + nsdlDcVocabSelectionsUrlStr + "': " + t);
				return;
			}
			if (_vocabSelectionsDom == null)
				return;

			List groupsFileNodes = _vocabSelectionsDom.selectNodes("/groupsFiles/groupsFile");
			if (groupsFileNodes.size() == 0)
				return;

			_xpathsToModify = new ArrayList();
			_xpathsToModifyKeepExisting = new TreeMap();
			_groupsFileDoms = new TreeMap();
			_fromXpathsLists = new TreeMap();
			for (int i = 0; i < groupsFileNodes.size(); i++) {
				Node groupsFileNode = (Node) groupsFileNodes.get(i);
				String toXpath = groupsFileNode.valueOf("toXpath").trim();
				String keepExistingValues = groupsFileNode.valueOf("keepExistingValues").trim();
				if (keepExistingValues != null && keepExistingValues.equalsIgnoreCase("true"))
					_xpathsToModifyKeepExisting.put(toXpath, new Object());
				String url = groupsFileNode.valueOf("url").trim();
				List fromXpaths = groupsFileNode.selectNodes("fromXpaths/fromXpath");
				if (toXpath.length() > 0 && url.length() > 0 && fromXpaths.size() > 0) {
					Document groupsDom = null;
					try {
						groupsDom = Dom4jUtils.localizeXml(Dom4jUtils.getXmlDocument(url, 5000, userAgent));
					} catch (Throwable t) {
						prtlnErr("Error fetching groups file  rom URL '" + url + "': " + t);
						continue;
					}
					List fromXpathsStrings = new ArrayList();
					for (int j = 0; j < fromXpaths.size(); j++)
						fromXpathsStrings.add(((Node) fromXpaths.get(j)).getText().trim());
					_xpathsToModify.add(toXpath);
					_groupsFileDoms.put(toXpath, groupsDom);
					_fromXpathsLists.put(toXpath, fromXpathsStrings);
				}
			}
		}
	}


	/* ---------------------- Debug methods ----------------------- */
	/**
	 *  Return a string for the current time and date, sutiable for display in log files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	public static String getDateStamp() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final static void prtlnErr(String s) {
		System.err.println(getDateStamp() + " NSDLDCNormalizer Error: " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final static void prtln(String s) {
		if (debug) {
			System.out.println(getDateStamp() + " NSDLDCNormalizer: " + s);
		}
	}


	/**
	 *  Sets the debug attribute of the NSDLDCNormalizer object
	 *
	 * @param  db  The new debug value
	 */
	public final void setDebug(boolean db) {
		debug = db;
	}
}

