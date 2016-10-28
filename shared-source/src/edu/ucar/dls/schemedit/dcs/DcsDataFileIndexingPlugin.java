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
package edu.ucar.dls.schemedit.dcs;

import java.io.*;
import java.util.*;
import java.text.*;

import edu.ucar.dls.schemedit.Constants;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.MetaDataFramework;
import edu.ucar.dls.schemedit.FrameworkRegistry;
import edu.ucar.dls.schemedit.repository.RepositoryService;
import edu.ucar.dls.schemedit.security.user.User;
import edu.ucar.dls.schemedit.security.user.UserManager;
import edu.ucar.dls.schemedit.standards.asn.AsnStandardsManager;
import edu.ucar.dls.schemedit.standards.StandardsRegistry;
import edu.ucar.dls.schemedit.standards.asn.AsnStandardsNode;

import edu.ucar.dls.index.writer.*;
import edu.ucar.dls.index.reader.DocumentMap;
import edu.ucar.dls.util.*;
import org.apache.lucene.document.*;
import edu.ucar.dls.repository.*;

/**
 *  Helper for indexing DCS-specific workflow status information along with the
 *  normally-indexed fields for a metadata record.<p>
 *
 *  Status information is obtained from a {@link
 *  edu.ucar.dls.schemedit.dcs.DcsDataRecord} via the {@link
 *  edu.ucar.dls.schemedit.dcs.DcsDataManager}.<p>
 *
 *  Invoked by the {@link edu.ucar.dls.repository.RepositoryManager} <b>
 *  putRecord</b> method during all indexing operations.
 *
 *@author    ostwald
 */
public class DcsDataFileIndexingPlugin extends ServletContextFileIndexingPlugin {

	/**
	 *  Description of the Field
	 */
	protected static boolean debug = true;

	/**
	 *  Name space prefix for dcs indexing fields
	 */
	public final static String FIELD_NS = "dcs";


	/**
	 *  Adds workflow status fields to the index for a particular record.
	 *
	 *@param  newDoc         The new Document that is being created for this
	 *      resource
	 *@param  existingDoc    An existing Document that currently resides in the
	 *      index for the given resource, or null if none was previously present
	 *@param  file           The sourceFile that is being indexed.
	 *@param  docType        The feature to be added to the Fields attribute
	 *@param  docGroup       The feature to be added to the Fields attribute
	 *@exception  Exception  If an error occurs
	 */
	public final void addFields(File file,
	                            org.apache.lucene.document.Document newDoc,
	                            org.apache.lucene.document.Document existingDoc,
	                            String docType, String docGroup)
		 throws Exception {
			 
		
			 
		// try to obtain id from the source record to be indexed if at all possible.
		String id = null;
		try {
			id = newDoc.getField("idvalue").stringValue();
		} catch (Throwable t) {
			prtln("failed to obtain id from newDoc, trying fileName ...");
			if (file.exists()) {
				id = file.getName().split(".xml")[0];
			}
		}

		// prtln ("id: " + id);

		DcsDataManager dcsDataManager = (DcsDataManager) getServletContext().getAttribute("dcsDataManager");
		if (dcsDataManager == null) {
			// throw new Exception ("DcsDataManager not found in servlet context");
			// prtln ("dcsDataManager not yet available - will not add indexing fields");
			return;
		}

		DcsDataRecord dcsDataRecord = dcsDataManager.getDcsDataRecord(docGroup, docType, file.getName(), id);
		if (dcsDataRecord == null) {
			throw new Exception("unable to obtain dcsDataRecord");
		}

		/*
		 *  System.out.println("dcsDataFile: " + dcsDataRecord.getSource().getAbsolutePath()
		 *  + " myDocType: " + docType + " myCollection: " + docGroup);
		 */
		String lastEditor = dcsDataRecord.getLastEditor();
		if (lastEditor.trim().length() == 0) {
			lastEditor = Constants.UNKNOWN_EDITOR;
		}
		newDoc.add(new Field(FIELD_NS + "lastEditor", lastEditor,
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		// lastEditorName is used to sort search results, so it must be "NOT_ANALYZED"
		newDoc.add(new Field(FIELD_NS + "lastEditorName", getFullName(lastEditor),
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		newDoc.add(new Field(FIELD_NS + "isValid", dcsDataRecord.getIsValid(),
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		newDoc.add(new Field(FIELD_NS + "status", dcsDataRecord.getStatus(),
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		newDoc.add(new Field(FIELD_NS + "statusLabel", dcsDataRecord.getStatusLabel(),
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		String isFinalStatus = (dcsDataRecord.isFinalStatus() ? "true" : "false");
		newDoc.add(new Field(FIELD_NS + "isFinalStatus", isFinalStatus,
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		newDoc.add(new Field(FIELD_NS + "statusNote", dcsDataRecord.getStatusNote(),
				Field.Store.YES, Field.Index.ANALYZED));

		newDoc.add(new Field(FIELD_NS + "hasSyncError", dcsDataRecord.getHasSyncError(),
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		newDoc.add(new Field(FIELD_NS + "ndrHandle", dcsDataRecord.getNdrHandle(),
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		Date lastTouchDate = getLastTouchDate(dcsDataRecord, file);
		newDoc.add(
				new Field(FIELD_NS + "lastTouchDate", DateField.dateToString(lastTouchDate),
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		/*
		 *  index all status entries (except most recent) this is indexed above
		 */
		for (Iterator i = dcsDataRecord.getEntryList().iterator(); i.hasNext(); ) {
			StatusEntry statusEntry = (StatusEntry) i.next();

			newDoc.add(new Field(FIELD_NS + "statusEntryLabel",
					dcsDataRecord.getStatusLabel(statusEntry.getStatus()),
					Field.Store.NO, Field.Index.NOT_ANALYZED));

			newDoc.add(new Field(FIELD_NS + "statusEntryNote",
					statusEntry.getStatusNote(),
					Field.Store.YES, Field.Index.ANALYZED));

			newDoc.add(new Field(FIELD_NS + "statusEntryChangeDate",
					DateField.dateToString(statusEntry.getDate()),
					Field.Store.NO, Field.Index.NOT_ANALYZED));

			newDoc.add(new Field(FIELD_NS + "statusEntryEditor",
					statusEntry.getEditor(),
					Field.Store.NO, Field.Index.NOT_ANALYZED));

			newDoc.add(new Field(FIELD_NS + "statusEntryEditorName",
					getFullName(statusEntry.getEditor()),
					Field.Store.NO, Field.Index.ANALYZED));

			// entries processed in reverse chron order
			if (!i.hasNext()) {
				//we're looking at the first status entry
				newDoc.add(new Field(FIELD_NS + "recordCreationDate",
						DateField.dateToString(statusEntry.getDate()),
						Field.Store.YES, Field.Index.NOT_ANALYZED));

				String recordCreator = statusEntry.getEditor().trim();
				if (recordCreator.length() == 0) {
					recordCreator = Constants.UNKNOWN_EDITOR;
				}
				newDoc.add(new Field(FIELD_NS + "recordCreator",
						recordCreator,
						Field.Store.YES, Field.Index.NOT_ANALYZED));
			}
		}

		try {
			addFrameworkSpecificFields(file, id, newDoc, existingDoc, docType, docGroup);
		} catch (Throwable e) {
			prtln("addFrameworkSpecificFields() error: " + e.getMessage());
		}
		
		try {
			// indexStandardsFields(file, id, newDoc, existingDoc, docType, docGroup);
		} catch (Throwable e) {
			prtln("addFrameworkSpecificFields() error: " + e.getMessage());
		}

	}


	/**
	 *  Adds a indexing fields for records of specific formats.<p>
	 For example, computes fiscalYear for osm records.
	 *
	 *@param  newDoc         The new Document that is being created for this
	 *      resource
	 *@param  existingDoc    An existing Document that currently resides in the
	 *      index for the given resource, or null if none was previously present
	 *@param  file           The sourceFile that is being indexed.
	 *@param  docType        The feature to be added to the Fields attribute
	 *@param  docGroup       The feature to be added to the Fields attribute
	 *@exception  Exception  If an error occurs
	 */
	private void addFrameworkSpecificFields(File file, String recId,
			org.apache.lucene.document.Document newDoc,
			org.apache.lucene.document.Document existingDoc,
			String docType, String docGroup)
			 throws Exception {

		// try to obtain id from the source record to be indexed if possible.
		String xmlFormat = null;
		try {
			xmlFormat = newDoc.getField("xmlFormat").stringValue();
		} catch (Throwable t) {
			prtln("failed to obtain xmlformat from newDoc");
			return;
		}

		if ("osm".equals(xmlFormat)) {
			OsmIndexingBean osmFields = new OsmIndexingBean(file);
			int fiscalYear = osmFields.getFiscalYear();
			if (fiscalYear != -1) {
				newDoc.add(new Field(FIELD_NS + "osmFiscalYear",
						Integer.toString(fiscalYear),
						Field.Store.YES, Field.Index.NOT_ANALYZED));
			}
		
			String flattenedTitle = osmFields.getFlattenedTitle();
			if (flattenedTitle != null) {
				newDoc.add(new Field(FIELD_NS + "osmFlattenedTitle",
						flattenedTitle,
						Field.Store.YES, Field.Index.NOT_ANALYZED));
			}
			
			String submission = null;
			try {
				submission = newDoc.getField("/key//record/general/title/@submission").stringValue();
			} catch (Throwable t) {}
			
			String revisionLetter = SchemEditUtils.getRevisionLetter(recId);
			if (submission != null && submission.trim().length() > 0 && revisionLetter != null) {
				newDoc.add(new Field(FIELD_NS + "osmIsRevision",
						"true",
						Field.Store.NO, Field.Index.NOT_ANALYZED));
			}				
		}
		
	}

	/* write the values of all cataloged AsnStandardIds to "asnStandard" field, and
		the standard Doc Ids for each asnStd to the "asnDocument" field
	*/
	private void indexStandardsFields (File file, String recId,
			org.apache.lucene.document.Document newDoc,
			org.apache.lucene.document.Document existingDoc,
			String docType, String docGroup)
			 throws Exception {
		
		// try to obtain id from the source record to be indexed if possible.
		DocumentMap docMap = new DocumentMap(newDoc);
				 
		prtln ("\nindexStandardsFields()");
		String xmlFormat = newDoc.getField("xmlFormat").stringValue();
		prtln ("xmlFormat: " + xmlFormat);		 
		
		try {
			FrameworkRegistry frameworkRegistry = 
				(FrameworkRegistry) getServletContext().getAttribute("frameworkRegistry");
			MetaDataFramework framework = frameworkRegistry.getFramework(xmlFormat);
			AsnStandardsManager standardsManager = (AsnStandardsManager)framework.getStandardsManager();
			if (standardsManager != null) {
				StandardsRegistry standardsRegistry = standardsManager.getStandardsRegistry();
				String xpath = standardsManager.getXpath();
				// find the standards from the existing index field for the standards path 
				String key = "/key/" + xpath;
				String fieldValue = (String)docMap.get(key);
				prtln ("asnIds: " + fieldValue);
				
				if (fieldValue != null) {
					String[] asnIds = fieldValue.split(" ");
					String asnDocValue = "";
					String asnStdValue = "";
					for (int i=0;i<asnIds.length;i++) {
						String asnId = asnIds[i];
						AsnStandardsNode stdNode = standardsRegistry.getStandardsNode(asnId);
						if (i > 0) {
							asnDocValue += " ";
							asnStdValue += " ";
						}
						asnDocValue += stdNode.getDocId();
						asnStdValue += asnId;
					}
					newDoc.add(new Field("asnDocument",
						asnDocValue,
						Field.Store.YES, Field.Index.ANALYZED)); // NOT_ANALYZED
					
					newDoc.add(new Field("asnStandard",
						asnStdValue,
						Field.Store.YES, Field.Index.ANALYZED));
				}
			}
		
		} catch (Throwable t) {
			prtln ("indexStandardsFields error: " + t.getMessage());
		}

	}

	

	/**
	 *  Gets the fullName attribute of the DcsDataFileIndexingPlugin object
	 *
	 *@param  userName  Description of the Parameter
	 *@return           The fullName value
	 */
	private String getFullName(String userName) {
		try {
			UserManager userManager =
					(UserManager) getServletContext().getAttribute("userManager");
			User user = userManager.getUser(userName);
			return user.getFullName();
		} catch (Throwable t) {}

		if (userName != null && userName.trim().length() > 0) {
			return userName;
		} else {
			return Constants.UNKNOWN_USER;
		}
	}


	/**
	 *  calculate a lastTouchDate for the DcsDataRecord<p>
	 *
	 *  New records will not have a lastTouchDate, so we give it a default date for
	 *  now so it may be indexed. The default date uses the lastModified time of
	 *  the indexed file, and if this is not found, then the "epoch" date is used.
	 *
	 *@param  file           NOT YET DOCUMENTED
	 *@param  dcsDataRecord  Description of the Parameter
	 *@return                The lastTouchDate value
	 *@exception  Exception  NOT YET DOCUMENTED
	 */
	private Date getLastTouchDate(DcsDataRecord dcsDataRecord, File file) throws Exception {

		String lastTouchString = dcsDataRecord.getLastTouchDate();

		// if there is no lastTouchDate stored in the dcsDataRecord, attempt to get the
		// file's lastModified date
		if (lastTouchString == null || lastTouchString.length() == 0) {
			prtln("  ... lastTouchDate not found in DcsDataRecord");
			if (file != null && file.exists()) {
				lastTouchString = SchemEditUtils.getFullDateFormat().format(new Date(file.lastModified()));
				prtln("lastTouchString based on file lastMod: " + lastTouchString);
			}
			prtln("setting lastTouchSTring to " + lastTouchString);
			dcsDataRecord.setLastTouchDate(lastTouchString);
		}

		return dcsDataRecord.getLastTouchDateDate();
	}


	/**
	 *  Print a line to standard out.
	 *
	 *@param  s  The String to print.
	 */
	protected void prtln(String s) {
		if (debug) {
			// System.out.println("DcsDataFileIndexingPlugin: " + s);
			// SchemEditUtils.prtln(s, "DcsDataFileIndexingPlugin");
			SchemEditUtils.prtln(s, "plug");
		}
	}
}

