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
package edu.ucar.dls.repository.indexing;

import edu.ucar.dls.repository.*;

import java.io.*;
import java.util.*;

import edu.ucar.dls.index.*;
import edu.ucar.dls.index.writer.*;
import java.text.*;
import org.dom4j.*;
import edu.ucar.dls.xml.Dom4jUtils;

/**
 *  Manage collections of records in a DDS repository.
 *
 * @author    John Weatherley
 */
public class CollectionIndexer {
	private static boolean debug = false;

	private RepositoryManager repositoryManager;
	private IndexingManager indexingManager;

	/**  Message type used in #addIndexingMessage to post information. */
	public final static int MSG_TYPE_INFO = IndexingManager.MSG_TYPE_INFO;
	/**  Message type used in #addIndexingMessage to indicate an error.  */
	public final static int MSG_TYPE_ERROR = IndexingManager.MSG_TYPE_ERROR;	
	/**  Message type used in #addIndexingMessage to indicate a warning.  */
	public final static int MSG_TYPE_WARNING = IndexingManager.MSG_TYPE_WARNING;	
	
	/**
	 *  Constructor for the CollectionIndexer object
	 *
	 * @param  repositoryManager  The RepositoryManager
	 * @param  indexingManager    The IndexingManager
	 */
	public CollectionIndexer(RepositoryManager repositoryManager, IndexingManager indexingManager) {
		this.repositoryManager = repositoryManager;
		this.indexingManager = indexingManager;
	}
	
	public void commitIndex() {
		indexingManager.commitIndex();	
	}

	/**
	 *  Put a collection in the repository. If the collection key does not exist, the collection will be created
	 *  otherwise it will be updated with this new information.
	 *
	 * @param  collectionKey       The collection key, for example 'dcc'
	 * @param  format              The XML format for the records that will reside in the collection
	 * @param  name                A display name for the collection
	 * @param  description         A long description for the collection
	 * @param  additionalMetadata  A text or XML string to be inserted into the additionalMetadata element of the
	 *      collection record, or null for none
	 * @exception  Exception       If error
	 */
	public void putCollection(String collectionKey, String format, String name, String description, String additionalMetadata)
		 throws Exception {
		printPrivateStatusMessage("Adding collection '" + name + "', key '" + collectionKey + "'",MSG_TYPE_INFO);
		repositoryManager.putCollection(collectionKey, format, name, description, additionalMetadata);
	}


	/**
	 *  Gets the collection record of an existing colleciton in the repository as a String, or null if not
	 *  available. The collection record contains the collectionKey, format, name, description, and
	 *  additionalMetadata.
	 *
	 * @param  collectionKey  The collection key
	 * @return                The collectionRecord value
	 * @exception  Exception  If error retrieving the collection record
	 */
	public String getCollectionRecord(String collectionKey) throws Exception {
		return repositoryManager.getCollectionRecord(collectionKey);
	}


	/**
	 *  Delete a collection and all its records from a DDS repository.
	 *
	 * @param  collectionKey  The key for the collection to delete
	 * @return                The collection record XML String if the collection existed and was deleted, null if
	 *      no such collection exists *
	 * @exception  Exception  If error
	 */
	public String deleteCollection(String collectionKey) throws Exception {
		if (collectionKey.equalsIgnoreCase("collect")) {
			String msg = "Permission denied to delete collection 'collect': it is managed internally";
			printPrivateStatusMessage(msg,MSG_TYPE_INFO);
			throw new Exception(msg);
		}
		String collectionRecord = repositoryManager.deleteCollection(collectionKey);
		String collName = collectionKey;
		if (collectionRecord != null) {
			Document deletedCollectionDoc = Dom4jUtils.getXmlDocument(collectionRecord);
			collName = deletedCollectionDoc.valueOf("/*[local-name()='collectionRecord']/*[local-name()='general']/*[local-name()='fullTitle']");
		}
		printPrivateStatusMessage("Deleting collection '" + collName + "'",MSG_TYPE_INFO);

		return collectionRecord;
	}


	/**
	 *  Get a list of the collections currently configured.
	 *
	 * @return    A List of collection key Strings, for example 'dcc'
	 */
	public List getConfiguredCollections() {
		List collections = repositoryManager.getConfiguredSets();

		// Do not advertise the "collect" collection:
		if (collections != null)
			collections.remove("collect");
		return collections;
	}


	/**
	 *  Gets the collectionConfigured attribute of the CollectionIndexer object
	 *
	 * @param  collectionKey  The key for the collection
	 * @return                The collectionConfigured value
	 */
	public boolean isCollectionConfigured(String collectionKey) {
		//prtln("isCollectionConfigured() '" + collectionKey + "'");
		return repositoryManager.isSetConfigured(collectionKey);
	}


	/**
	 *  Put a record in a collection, adding or replacing the given record in the repository.
	 *
	 * @param  recordXml      The XML for this record
	 * @param  collectionKey  The collection this record should be put
	 * @param  id             The ID of the record - ignored if the ID can be derived from the record XML
	 * @param  session        An indexing session ID - used for deleting records later after a new indexing
	 *      session occurs
	 * @exception  Exception  If error
	 */
	public void putRecord(
	                      String recordXml,
	                      String collectionKey,
	                      String id,
	                      CollectionIndexingSession session) throws Exception {
		SetInfo setInfo = repositoryManager.getSetInfo(collectionKey);
		if (setInfo == null)
			throw new Exception("Collection '" + collectionKey + "' is not configured");

		//printPrivateStatusMessage("Adding record ID '" + id + "', collection '" + collectionKey + "'");
		SessionIndexingPlugin plugIn = new SessionIndexingPlugin(session.getSessionId());
		repositoryManager.putRecord(recordXml, setInfo.getFormat(), collectionKey, id, plugIn, false);
	}

	public void putRecords(
            List<HashMap<String, String>> records,
            String collection,
            CollectionIndexingSession session) throws Exception {

		//printPrivateStatusMessage("Adding record ID '" + id + "', collection '" + collectionKey + "'");
		SessionIndexingPlugin plugIn = new SessionIndexingPlugin(session.getSessionId());
		repositoryManager.putRecords(records, collection, plugIn, false);
		}

	/**
	 *  Deletes a record in the repository.
	 *
	 * @param  id             Id of the record.
	 * @exception  Exception  If error
	 */
	public void deleteRecord(String id) throws Exception {
		//printPrivateStatusMessage("Deleting record ID '" + id + "'");
		repositoryManager.deleteRecord(id);
	}


	/**
	 *  Gets the newCollectionIndexingSession attribute of the CollectionIndexer object
	 *
	 * @param  collectionKey  The collection
	 * @return                A new indexing session ID
	 */
	public CollectionIndexingSession getNewCollectionIndexingSession(String collectionKey) {
		return new CollectionIndexingSession(collectionKey);
	}


	/**
	 *  Post a status message to the indexing process to let admins know the current indexing status.
	 *
	 * @param  msg  The message to post
	 */
	public void printStatusMessage(String msg, int messageType) {
		indexingManager.addIndexingMessage(msg,messageType);
	}


	private void printPrivateStatusMessage(String msg, int messageType) {
		printStatusMessage("Indexer: " + msg,messageType);
	}


	/**
	 *  Gets the existingCollectionIndexingSession attribute of the CollectionIndexer object
	 *
	 * @param  sessionId  The session ID
	 * @return            The existingCollectionIndexingSession value
	 */
	protected CollectionIndexingSession getExistingCollectionIndexingSession(String sessionId) {
		CollectionIndexingSession sess = new CollectionIndexingSession();
		sess.setSessionId(sessionId);
		return sess;
	}


	/**
	 *  Delete all records that DO NOT have the given session ID.
	 *
	 * @param  currentSession  The session ID
	 */
	public void deletePreviousSessionRecords(CollectionIndexingSession currentSession) {
		doDeletePreviousSessionRecords(currentSession, false);
		doDeletePreviousSessionRecords(currentSession, true);
	}


	/**
	 *  Delete all records that DO NOT have the given session ID.
	 *
	 * @param  currentSession   The session ID
	 * @param  doDupItemsIndex  True to delete from dups items index, false from primary index
	 */
	private void doDeletePreviousSessionRecords(CollectionIndexingSession currentSession, boolean doDupItemsIndex) {
		boolean foundSessions = false;

		SimpleLuceneIndex theIndex = null;
		if (doDupItemsIndex)
			theIndex = repositoryManager.getDupItemsIndex();
		else
			theIndex = repositoryManager.getIndex();

		List indexedSessions = theIndex.getTerms("indexSessionId");

		if (indexedSessions != null) {
			for (int i = 0; i < indexedSessions.size(); i++) {
				CollectionIndexingSession indexedSession = getExistingCollectionIndexingSession((String) indexedSessions.get(i));
				//prtln("Session ID found: " + indexedSession + " collection: " + indexedSession.getCollection());

				if (currentSession.getCollection().equals(indexedSession.getCollection()) && !currentSession.equals(indexedSession)) {
					int numBefore = theIndex.getNumDocs();
					//String msg = "Deleting records for session: " + indexedSession + ". Num docs: " + theIndex.getNumDocs();
					//prtln(msg);
					//printPrivateStatusMessage(msg);
					try {
						theIndex.removeDocs("indexSessionId", indexedSession.getSessionId());
						String msg = "Deleted records for session: " + indexedSession + ". Num deleted: " + (numBefore - theIndex.getNumDocs() + (doDupItemsIndex ? " from dups index" : " from primary index"));
						printPrivateStatusMessage(msg,MSG_TYPE_INFO);
						prtln(msg);
						foundSessions = true;
					} catch (WriteNotPermittedException we) {
						String msg = "Unable to deleted records for session: " + indexedSession + ": " + we;
						prtlnErr(msg);
						printPrivateStatusMessage(msg,MSG_TYPE_ERROR);
					}					
				}
			}
		}

		if (!foundSessions)
			printPrivateStatusMessage("No previous indexing sessions found in " + (doDupItemsIndex ? "dups index" : "primary index"),MSG_TYPE_INFO);
	}



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
		System.err.println(getDateStamp() + " CollectionIndexer Error: " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final static void prtln(String s) {
		if (debug) {
			System.out.println(getDateStamp() + " CollectionIndexer: " + s);
		}
	}


	/**
	 *  Sets the debug attribute of the CollectionIndexer object
	 *
	 * @param  db  The new debug value
	 */
	public final static void setDebug(boolean db) {
		debug = db;
	}

}

