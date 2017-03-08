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
package edu.ucar.dls.index.writer;

import java.io.*;
import java.util.*;
import java.text.*;
import java.net.URLEncoder;

import org.apache.lucene.document.*;
import org.apache.lucene.search.*;
import org.apache.lucene.index.*;
import org.apache.lucene.analysis.KeywordAnalyzer;

import edu.ucar.dls.dds.DDSServlet;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.index.*;
import edu.ucar.dls.index.reader.*;
import edu.ucar.dls.util.*;
import edu.ucar.dls.repository.*;
import edu.ucar.dls.vocab.*;
import edu.ucar.dls.index.document.DateFieldTools;
import edu.ucar.dls.index.writer.xml.*;
import edu.ucar.dls.repository.indexing.SessionIndexingPlugin;

/**
 *  Creates a Lucene {@link org.apache.lucene.document.Document} from any XML file by stripping the XML tags
 *  to extract and index the content. The reader for this type of Document is XMLDocReader.<p>
 *
 *  <b>The Lucene Document fields that are created by this class are (in addition the the ones listed for
 *  {@link edu.ucar.dls.index.writer.FileIndexingServiceWriter}):</b> <br>
 *  <br>
 *  <code><b>collection</b> </code> - The collection associated with this resource. <br>
 *
 *
 * @author    John Weatherley
 * @see       edu.ucar.dls.index.FileIndexingService
 * @see       edu.ucar.dls.index.reader.XMLDocReader
 */
public abstract class XMLFileIndexingWriter extends FileIndexingServiceWriter {

	private File sourceFile = null;
	private DleseCollectionDocReader myCollectionDocReader = null;
	private XMLIndexer _xmlIndexer = null;
	private String[] _collections = null;
	private ResultDocList _myAnnoResultDocs = null;
	private boolean processAsSingleRecord = false;
	private boolean processingMultiDoc = false;


	/**  Constructor for the XMLFileIndexingWriter. */
	public XMLFileIndexingWriter() { }


	/**
	 *  Gets the xmlFormat for this item
	 *
	 * @return                The xmlFormat value, for example 'nsdl_dc', 'oai_dc'
	 * @exception  Exception  Description of the Exception
	 */
	public String getXmlFormat() throws Exception {
		return getDocType();
	}


	/**
	 *  Returns the ids for the item being indexed. If more than one record catalogs the same item, the first
	 *  represents the primary ID.
	 *
	 * @return                The id String(s)
	 * @exception  Exception  If error
	 * @see                   #getIds
	 */
	public String[] getIds() throws Exception {
		return getXmlIndexer().getIds();
	}


	/**
	 *  Returns the unique primary record ID for the item being indexed. If more than one record catalogs the
	 *  same item, this represents the primary ID.
	 *
	 * @return                The id String
	 * @exception  Exception  If error
	 * @see                   #getIds
	 */
	public String getPrimaryId() throws Exception {
		if (getIds() == null || getIds().length == 0)
			return null;
		return getIds()[0];
	}


	/**
	 *  Gets the ids of related records.
	 *
	 * @return                            The related ids value, or null if none
	 * @exception  IllegalStateException  If called prior to calling method #indexFields
	 * @exception  Exception              If error
	 */
	public List getRelatedIds() throws IllegalStateException, Exception {
		return getXmlIndexer().getRelatedIds();
	}


	/**
	 *  Gets the urls of related records.
	 *
	 * @return                            The related urls value, or null if none
	 * @exception  IllegalStateException  If called prior to calling method #indexFields
	 * @exception  Exception              If error
	 */
	public List getRelatedUrls() throws IllegalStateException, Exception {
		return getXmlIndexer().getRelatedUrls();
	}


	/**
	 *  Gets the ids of related records. The Map key contains the relationship (isAnnotatedBy, etc.) and the Map
	 *  value contains a List of Strings that indicate the ids of the target records.
	 *
	 * @return                            The related ids value, or null if none
	 * @exception  IllegalStateException  If called prior to calling method #indexFields
	 * @exception  Exception              If error
	 */
	public Map getRelatedIdsMap() throws IllegalStateException, Exception {
		return getXmlIndexer().getRelatedIdsMap();
	}


	/**
	 *  Gets the urls of related records. The Map key contains the relationship (isAnnotatedBy, etc.) and the Map
	 *  value contains a List of Strings that indicate the urls of the target records.
	 *
	 * @return                            The related urls value, or null if none
	 * @exception  IllegalStateException  If called prior to calling method #indexFields
	 * @exception  Exception              If error
	 */
	public Map getRelatedUrlsMap() throws IllegalStateException, Exception {
		return getXmlIndexer().getRelatedUrlsMap();
	}


	/**
	 *  Returns unique collection keys for the item being indexed. For example "dcc" (single collection) or "dcc
	 *  dwel" (multiple collections). If more than one collection is provided, the first one must be the primary
	 *  collection. May be overridden by sub-classes as appropriate (overridden by ADNFileIndexingWriter).
	 *
	 * @return                The collection keys
	 * @exception  Exception  This method should throw and Exception with appropriate error message if an error
	 *      occurs.
	 */
	protected String[] getCollections() throws Exception {
		if (_collections == null) {
			String collString = (String) getConfigAttributes().get("collection");
			if (collString == null)
				return null;
			_collections = collString.split(" ");
		}
		return _collections;
	}


	/**
	 *  Gets the collection specifier, for example 'dcc', 'comet'.
	 *
	 * @return                The collection specifier
	 * @exception  Exception  If error occured
	 */
	public String getDocGroup() throws Exception {
		return getCollections()[0];
	}


	/**
	 *  Return the geospatial BoundingBox footprint that represnets the resource being indexed, or null if none
	 *  apply. Override if nessary.
	 *
	 * @return                BoundingBox, or null
	 * @exception  Exception  This method should throw and Exception with appropriate error message if an error
	 *      occurs.
	 */
	protected BoundingBox getBoundingBox() throws Exception {
		return null;
	}


	// --------------- Abstract methods for subclasses --------------------

	/**
	 *  This method is called prior to processing and may be used to for any necessary set-up. This method should
	 *  throw and exception with appropriate message if an error occurs.
	 *
	 * @param  source         The source file being indexed
	 * @param  existingDoc    An existing Document that currently resides in the index for the given resource, or
	 *      null if none was previously present
	 * @exception  Exception  If an error occured during set-up.
	 */
	public abstract void init(File source, Document existingDoc) throws Exception;


	/**
	 *  Return unique IDs for the item being indexed, one for each collection that catalogs the resource. For
	 *  example "DLESE-000-000-000-001" (single ID) or "DLESE-000-000-000-036 COMET-60" (multiple IDs). If more
	 *  than one ID is present, the first one is the primary.
	 *
	 * @return                The id(s)
	 * @exception  Exception  This method should throw and Exception with appropriate error message if an error
	 *      occurs.
	 */
	protected abstract String[] _getIds() throws Exception;


	/**
	 *  Return the URL(s) to the resource being indexed, or null if none apply. If more than one URL references
	 *  the resource, the first one is the primary. The URL Strings are tokenized and indexed under the field key
	 *  'uri' and is also indexed in the 'default' field. It is also stored in the index untokenized under the
	 *  field key 'url.'
	 *
	 * @return                The url String(s)
	 * @exception  Exception  This method should throw and Exception with appropriate error message if an error
	 *      occurs.
	 */
	public abstract String[] getUrls() throws Exception;


	/**
	 *  Return a title for the document being indexed, or null if none applies. The String is tokenized, stored
	 *  and indexed under the field key 'title' and is also indexed in the 'default' field.
	 *
	 * @return                The title String
	 * @exception  Exception  This method should throw and Exception with appropriate error message if an error
	 *      occurs.
	 */
	public abstract String getTitle() throws Exception;


	/**
	 *  Return a description for the document being indexed, or null if none applies. The String is tokenized,
	 *  stored and indexed under the field key 'description' and is also indexed in the 'default' field.
	 *
	 * @return                The description String
	 * @exception  Exception  This method should throw and Exception with appropriate error message if an error
	 *      occurs.
	 */
	public abstract String getDescription() throws Exception;


	/**
	 *  Return true to have the full XML content indexed in the 'default' and 'stems' fields, false if handled by
	 *  the sub-class. If true, the content is indexed using the #addToDefaultField method.
	 *
	 * @return    True to have the full XML content indexed in the 'default' and 'stems'
	 */
	public abstract boolean indexFullContentInDefaultAndStems();


	/**
	 *  Returns the date used to determine "What's new" in the library, or null if none is available.
	 *
	 * @return                The what's new date for the item or null if not available.
	 * @exception  Exception  This method should throw and Exception with appropriate error message if an error
	 *      occurs.
	 */
	protected abstract Date getWhatsNewDate() throws Exception;


	/**
	 *  Returns the type of category for "What's new" in the library, or null if none is available. Must be a
	 *  simple lower case String with no spaces, for example 'itemnew,' 'itemannocomplete,' 'itemannoinprogress,'
	 *  'annocomplete,' 'annoinprogress,' 'collection'.
	 *
	 * @return                The what's new type.
	 * @exception  Exception  This method should throw and Exception with appropriate error message if an error
	 *      occurs.
	 */
	protected abstract String getWhatsNewType() throws Exception;


	/**
	 *  Adds additional fields that are unique the document format being indexed. When implementing this method,
	 *  use the add method of the {@link org.apache.lucene.document.Document} class to add a {@link
	 *  org.apache.lucene.document.Field}.<p>
	 *
	 *  The following Lucene {@link org.apache.lucene.document.Field} types are available for indexing with the
	 *  {@link org.apache.lucene.document.Document}:<br>
	 *  Field.Text(string name, string value) -- tokenized, indexed, stored<br>
	 *  Field.UnStored(string name, string value) -- tokenized, indexed, not stored <br>
	 *  Field.Keyword(string name, string value) -- not tokenized, indexed, stored <br>
	 *  Field.UnIndexed(string name, string value) -- not tokenized, not indexed, stored<br>
	 *  Field(String name, String string, boolean store, boolean index, boolean tokenize) -- allows control to do
	 *  anything you want<p>
	 *
	 *  Example code:<br>
	 *  <code>protected void addCustomFields(Document newDoc, Document existingDoc) throws Exception {</code>
	 *  <br>
	 *  &nbsp;<code> String customContent = "Some content";</code><br>
	 *  &nbsp;<code> newDoc.add(Field.Text("mycustomefield", customContent));</code> <br>
	 *  <code>}</code>
	 *
	 * @param  newDoc         The new {@link org.apache.lucene.document.Document} that is being created for this
	 *      resource
	 * @param  existingDoc    An existing {@link org.apache.lucene.document.Document} that currently resides in
	 *      the index for the given resource, or null if none was previously present
	 * @param  sourceFile     The sourceFile that is being indexed
	 * @exception  Exception  This method should throw and Exception with appropriate error message if an error
	 *      occurs.
	 */
	protected abstract void addFields(Document newDoc, Document existingDoc, File sourceFile) throws Exception;


	// --------------- Concrete methods --------------------

	/**
	 *  Adds the full content of the XML to the default search field. Strips the XML tags to extract the content.
	 *  Will not work properly if the XML is not well-formed.<p>
	 *
	 *
	 *
	 * @param  documentWrapper         The new DocumentWrapper that is being created for this
	 *      resource
	 * @param  existingDoc    An existing {@link org.apache.lucene.document.Document} that currently resides in
	 *      the index for the given resource, or null if none was previously present
	 * @param  previousIndexExistingLuceneDoc  An existing Document that resides in the previous index for the
	 *      given resource, or null if none present. Used when background indexing is being used to preserve proper OAI mod time.	 
	 * @param  sourceFile     The feature to be added to the CustomFields attribute
	 * @exception  Exception  This method should throw and Exception with appropriate error message if an error
	 *      occurs.
	 */
	protected void addCustomFields(DocumentWrapper documentWrapper, Document existingDoc, Document previousIndexExistingLuceneDoc, File sourceFile) throws Exception {
		Document newDoc = documentWrapper.getDocument();
		RecordDataService recordDataService = getRecordDataService();
		MetadataVocab vocab = null;
		if (recordDataService != null)
			vocab = recordDataService.getVocab();

		// ------ Standard XML indexing handled by XMLIndexer ------------

		// Index all xPath and custom fields
		XMLIndexer xmlIndexer = getXmlIndexer();

		// Set up the default values to index for the standard fields:

		// Get the ids provided by sub-class:
		String[] providedIds = _getIds();

		// If no ID provided by sub-class, use the file name: simply remove the ".xml" from the end of the filename to get the ID.
		if (providedIds == null || providedIds.length == 0) {
			String fileName = getSourceFile().getName();
			providedIds = new String[]{Files.decode(fileName.substring(0, (fileName.length() - 4)))};
		}
		//prtln("setting providedIds: " + Arrays.toString(providedIds));
		xmlIndexer.setIds(providedIds);
		xmlIndexer.setUrls(getUrls());
		xmlIndexer.setTitle(getTitle());
		xmlIndexer.setDescription(getDescription());
		try {
			xmlIndexer.setBoundingBox(getBoundingBox());
		} catch (Throwable e) {
			prtlnErr("Unable to index Bounding Box coordinates: " + e.getMessage());
		}

		// If indicated by sub-class, index the 'default', 'admindefault', and 'stems' fields with the full XML content (Note: This performs the addToDefaultField and addToAdminDefaultField):
		xmlIndexer.setIndexDefaultAndStemsField(indexFullContentInDefaultAndStems());

		// Perform the XML indexing...:
		xmlIndexer.indexFields(documentWrapper);

		// Remove old docs for those we're adding:
		String[] idsEncoded = xmlIndexer.getIdsEncoded();
		if (idsEncoded != null) {
			for (int i = 0; i < idsEncoded.length; i++) {
				// Remove all IDs in the index that are the same as this one
				addDocToRemove("id", idsEncoded[i]);
			}
		}

		// --- Index things this item relates to (is an annotation for, etc), e.g. isRelatedTo:

		boolean itemAssignsRelationships = false;

		// Index the related IDs for this item:
		Map relatedIdsMap = xmlIndexer.getRelatedIdsMap();
		//prtln("xmlIndexer.getRelatedIds()");
		if (relatedIdsMap != null) {
			//prtln("xmlIndexer.getRelatedIds() has some!");
			Iterator it = relatedIdsMap.keySet().iterator();
			while (it.hasNext()) {
				String relationshipName = (String) it.next();
				List ids = (List) relatedIdsMap.get(relationshipName);
				//prtln("processing id relation: relationshipName: " + relationshipName + " ids: " + Arrays.toString(ids.toArray()));

				// Index the IDs so these docs can be retrieved later:
				for (int i = 0; i < ids.size(); i++) {
					itemAssignsRelationships = true;
					newDoc.add(new Field("assignsRelationshipById." + relationshipName, ids.get(i).toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
					newDoc.add(new Field("assignsRelationshipById", ids.get(i).toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				}
				newDoc.add(new Field("assignedRelationshipsById", relationshipName, Field.Store.YES, Field.Index.NOT_ANALYZED));
			}
		}
		else {
			//prtln("xmlIndexer.getRelatedIds() has no related IDs!");
		}

		// Index the related urls for this item:
		Map relatedUrlsMap = xmlIndexer.getRelatedUrlsMap();
		if (relatedUrlsMap != null) {
			Iterator it = relatedUrlsMap.keySet().iterator();
			while (it.hasNext()) {
				String relationshipName = (String) it.next();
				List urls = (List) relatedUrlsMap.get(relationshipName);
				//prtln("processing url relation: relationshipName: " + relationshipName + " urls: " + Arrays.toString(urls.toArray()));

				// Index the IDs so these docs can be retrieved later:
				for (int i = 0; i < urls.size(); i++) {
					itemAssignsRelationships = true;
					newDoc.add(new Field("assignsRelationshipByUrl." + relationshipName, urls.get(i).toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
					newDoc.add(new Field("assignsRelationshipByUrl", urls.get(i).toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				}
				newDoc.add(new Field("assignedRelationshipsByUrl", relationshipName, Field.Store.YES, Field.Index.NOT_ANALYZED));
			}
		}

		// Mark if this item assigns a relationship:
		newDoc.add(new Field("assignedRelationshipIsDefined", (itemAssignsRelationships ? "true" : "false"), Field.Store.YES, Field.Index.NOT_ANALYZED));

		// JW - Should we assitn the "isRelatedTo" relationship to this?
		if (itemAssignsRelationships) {
			//luceneDoc.add(new Field("indexedRelations", "isRelatedTo", Field.Store.YES, Field.Index.NOT_ANALYZED));
		}

		// ------ [end] Standard XML indexing handled by XMLIndexer ------------

		// ------ Resource De-Duplication (Generate one lucene document per resoruce URL, which may contain multiple records) ------

		boolean isMultiDoc = false;
		isMultiDoc = doResourceDeDuplication(newDoc, existingDoc, sourceFile);

		if (!processingMultiDoc) {
			if (isMultiDoc)
				newDoc.add(new Field("multirecord", "true", Field.Store.YES, Field.Index.NOT_ANALYZED));
			else
				newDoc.add(new Field("multirecord", "false", Field.Store.YES, Field.Index.NOT_ANALYZED));
		}

		// ------ [end ] Resource De-Duplication ------

		// ------ Index relations for this item (records that have a relation to this (isAnnotatedBy, etc.) ------------

		// The new way to handle relations....
		indexRelations(documentWrapper);

		// Index the annotations as a standard relation:
		//indexRelation(myAnnoResultDocs,"isAnnotatedBy",newDoc);

		// To do: Implement support for other configurable relations types...


		// ------ [end] Index relations for this item ------------

		// ----------- Crawled content: ------------------

		// The new way to handle relations....
		indexCrawledContent(newDoc);

		// ----------- Annotations for this item ------------------

		// Note: See some related index fields applied in ItemFileIndexingWriter

		ResultDocList myAnnoResultDocs = getMyAnnoResultDocs();

		// Add anno fields only available if the RecordDataService is avail:
		if (recordDataService != null) {

			String fieldContent = null;
			List fieldList = null;

			// Flag anno
			if (myAnnoResultDocs != null && myAnnoResultDocs.size() > 0)
				newDoc.add(new Field("itemhasanno", "true", Field.Store.YES, Field.Index.ANALYZED));
			else
				newDoc.add(new Field("itemhasanno", "false", Field.Store.YES, Field.Index.ANALYZED));

			// Anno types
			fieldContent = "";
			fieldList = recordDataService.getAnnoTypesFromResultDocs(myAnnoResultDocs);
			if (fieldList != null && fieldList.size() > 0) {
				for (int i = 0; i < fieldList.size(); i++)
					fieldContent += ((String) fieldList.get(i)).replaceAll(" ", "+") + " ";
				newDoc.add(new Field("itemannotypes", fieldContent, Field.Store.YES, Field.Index.ANALYZED));
			}

			// Anno pathways
			fieldContent = "";
			fieldList = recordDataService.getAnnoPathwaysFromResultDocs(myAnnoResultDocs);
			if (fieldList != null && fieldList.size() > 0) {
				for (int i = 0; i < fieldList.size(); i++)
					fieldContent += ((String) fieldList.get(i)).replaceAll(" ", "+") + " ";
				newDoc.add(new Field("itemannopathways", fieldContent, Field.Store.YES, Field.Index.ANALYZED));
			}

			// Anno collection keys, e.g. {06, 09}
			fieldContent = "";
			fieldList = recordDataService.getCollectionKeysFromResultDocs(myAnnoResultDocs);
			if (fieldList != null && fieldList.size() > 0) {
				for (int i = 0; i < fieldList.size(); i++) {
					fieldContent += (String) fieldList.get(i);
					if (i < (fieldList.size() - 1))
						fieldContent += "+";
				}
				//prtln("itemannocollectionkeys for " + this.getId() + " is: " + fieldContent);
				newDoc.add(new Field("itemannocollectionkeys", fieldContent, Field.Store.YES, Field.Index.ANALYZED));
			}

			// Anno collection keys e.g. {06, 09} for those with status completed only
			fieldContent = "";
			ArrayList completedAnnoCollectionKeys
				 = recordDataService.getCompletedAnnoCollectionKeysFromResultDocs(myAnnoResultDocs);
			if (completedAnnoCollectionKeys != null && completedAnnoCollectionKeys.size() > 0) {
				for (int i = 0; i < completedAnnoCollectionKeys.size(); i++) {
					fieldContent += (String) completedAnnoCollectionKeys.get(i);
					if (i < (completedAnnoCollectionKeys.size() - 1))
						fieldContent += "+";
				}
				//prtln("itemannocompletedcollectionkeys for " + this.getId() + " is: " + fieldContent);
				newDoc.add(new Field("itemannocompletedcollectionkeys", fieldContent, Field.Store.YES, Field.Index.ANALYZED));
			}

			// Anno status
			fieldContent = "";
			fieldList = recordDataService.getAnnoStatusFromResultDocs(myAnnoResultDocs);
			if (fieldList != null && fieldList.size() > 0) {
				for (int i = 0; i < fieldList.size(); i++)
					fieldContent += ((String) fieldList.get(i)).replaceAll(" ", "+") + " ";
				newDoc.add(new Field("itemannostatus", fieldContent, Field.Store.YES, Field.Index.ANALYZED));
			}

			// Anno formats
			fieldContent = "";
			fieldList = recordDataService.getAnnoFormatsFromResultDocs(myAnnoResultDocs);
			if (fieldList != null && fieldList.size() > 0) {
				for (int i = 0; i < fieldList.size(); i++)
					fieldContent += ((String) fieldList.get(i)) + " ";
				newDoc.add(new Field("itemannoformats", fieldContent, Field.Store.YES, Field.Index.ANALYZED));
			}

			// Anno rating information and statistics
			indexAnnoRatings(myAnnoResultDocs, newDoc);

		}
		// If no record data service, mark the item as having no annos
		else {
			newDoc.add(new Field("itemhasanno", "false", Field.Store.YES, Field.Index.ANALYZED));
		}

		// ----------- [end] Annotations for this item ------------------

		// ----------- Global fields for all XML records and sub-class handlers -------------

		//prtln("Adding index fields for ID: " + getPrimaryId());

		String[] collections = getCollections();
		// Add my collection and collectionKey
		if (collections != null && collections.length > 0) {
			String colStg = "";
			for (int i = 0; i < collections.length; i++) {
				// Index the collection key with a leading zero (for legacy compatibility) as well as verbatim:
				// Note: XMLDocReader expects both to be there in this order!
				newDoc.add(new Field("collection", "0" + collections[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
				newDoc.add(new Field("collection", collections[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
			}

			// Index the "ky" field:
			if (!getCollections()[0].equals("configuredcollections")) {
				newDoc.add(new Field(getFieldName("key", "dlese_collect"), getFieldContent(collections, "key", "dlese_collect"), Field.Store.YES, Field.Index.NOT_ANALYZED));
			}
		}

		// Store the ID for the collection I am a member of. (The first time the index is built, the DocReader for the 'collect' collection is not available):
		String key = getCollections()[0];
		String myCollectionRecordIdValue = null;
		DleseCollectionDocReader dleseCollectionDocReader = getMyCollectionDoc();
		if (dleseCollectionDocReader != null) {
			myCollectionRecordIdValue = dleseCollectionDocReader.getId();

			// Index all fields in the collection record for search on this item:
			// Index only  xPaths for my collection
			XMLIndexer collectionXmlIndexer = new XMLIndexer(dleseCollectionDocReader.getXml(), dleseCollectionDocReader.getDoctype(), getXmlIndexerFieldsConfig());
			collectionXmlIndexer.setXPathFieldsPrefix("/relation.memberOfCollection/");

			// Add the related records text to this one's default fields?
			collectionXmlIndexer.setIndexDefaultAndStemsField(true);

			// Index just the XPath fields for the collection:
			collectionXmlIndexer.indexXpathFields(documentWrapper);
		}
		else if (recordDataService != null && recordDataService.getCollectCollectionID() != null)
			myCollectionRecordIdValue = recordDataService.getCollectCollectionID();
		else if (key != null && key.equals("collect"))
			myCollectionRecordIdValue = "ID-FOR-COLLECT-NOT-YET-AVAILABLE";

		// If no collection info (such as jOAI).
		if (myCollectionRecordIdValue == null)
			myCollectionRecordIdValue = "COLLECTION-ID-NOT-AVAILABLE";

		newDoc.add(new Field("myCollectionRecordIdValue", myCollectionRecordIdValue, Field.Store.YES, Field.Index.NO));

		newDoc.add(new Field("metadatapfx", '0' + getXmlFormat(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		newDoc.add(new Field("xmlFormat", getXmlFormat(), Field.Store.YES, Field.Index.NOT_ANALYZED));

		String oaimodtime = getOaiModtime(existingDoc,previousIndexExistingLuceneDoc);
		if (oaimodtime != null)
			newDoc.add(new Field("oaimodtime", oaimodtime, Field.Store.YES, Field.Index.NOT_ANALYZED));

		// ---------- Handle all required metadata fields from sub-classes ------

		// What's new date and time
		Date wndate = getWhatsNewDate();
		if (wndate != null)
			newDoc.add(new Field("wndate", DateFieldTools.dateToString(wndate), Field.Store.YES, Field.Index.NOT_ANALYZED));
		String wntype = getWhatsNewType();
		if (wntype != null)
			newDoc.add(new Field("wntype", wntype, Field.Store.YES, Field.Index.ANALYZED));

		// ------ Check that this record does not already exist in the repository, throw ErrorDocException if needed ------
		if (!isMakingDeletedDoc()) {
			// Get the ids for this XML document (used below):
			String[] ids = getIds();

			ErrorDocException errorDocException = null;
			if (ids != null && ids.length > 0) {

				for (int i = 0; i < ids.length; i++) {

					// Verify that this record's ID (the first ID) does not already exist in the repository:
					if (i == 0) {

						File recordIdAlreadyInRepository = null;

						// Check for duplicate IDs in the repository (not de-duping, but actual dup IDs), if not temp file for checking:
						if (!getSourceFile().getName().equals(DDSServlet.DDS_UUID + "_temp_dds_repository_put_record_file.xml")) {

							// Check if another file with the same ID exists in the index:
							if (recordIdAlreadyInRepository == null) {
								BooleanQuery idQ = new BooleanQuery();
								idQ.add(new TermQuery(new Term("id", idsEncoded[i])), BooleanClause.Occur.MUST);
								idQ.add(new TermQuery(new Term("multirecord", "true")), BooleanClause.Occur.MUST_NOT);
								idQ.add(new TermQuery(new Term("docsource", getDocsource())), BooleanClause.Occur.MUST_NOT);
								idQ.add(new TermQuery(new Term("deleted", "false")), BooleanClause.Occur.MUST);

								ResultDocList results = getIndex().searchDocs(idQ);
								if (results != null && results.size() > 0) {
									//prtln("Found " + results.length + " potential dup ID(s)");
									DocReader reader = ((ResultDoc) results.get(0)).getDocReader();
									if (reader instanceof XMLDocReader) {
										XMLDocReader xmlDocReader = (XMLDocReader) reader;
										if (xmlDocReader.getId().equals(ids[i])) {
											recordIdAlreadyInRepository = xmlDocReader.getFile();
											//prtln("found dup ID in index: " + getIds()[i]);
											//prtln("Dup ID matched for ID '" + getIds()[i] + "'");
										}
										else {
											//prtln("no dup ID in index: " + getIds()[i]);
											//prtln("Dup ID not matched for ID '" + getIds()[i] + "' found instead '" + xmlDocReader.getId() + "'");
										}
									}
								}
							}

							// Check if another file with the same ID exists in the current indexing session:
							if (recordIdAlreadyInRepository == null) {
								HashMap sessionIDs = null;
								HashMap sessionAttributes = getSessionAttributes();
								if (sessionAttributes != null) {
									sessionIDs = (HashMap) sessionAttributes.get("sessionIDs");
									if (sessionIDs == null) {
										sessionIDs = new HashMap();
										sessionAttributes.put("sessionIDs", sessionIDs);
									}

									// If another file in the directory has the same ID, throw error...
									File fileInDirectory = (File) sessionIDs.get(idsEncoded[i]);
									if (fileInDirectory != null && getSourceFile().exists() && fileInDirectory.exists()) {
										//prtln("found dup ID in same dir: " + getIds()[i] + " dup path: " + fileInDirectory.getAbsolutePath() + " my path: " + this.getSourceFile().getAbsolutePath());
										recordIdAlreadyInRepository = fileInDirectory;
									}
									else {
										//prtln("no dup ID in same dir: " + getIds()[i]);
										//prtln("sessionIDs.put: " + getSourceFile());
										sessionIDs.put(idsEncoded[i], getSourceFile());
									}
								}
							}

							// If this ID is already in the repository, throw an Exception and store the dup info:
							if (recordIdAlreadyInRepository != null) {
								errorDocException = makeErrorDocException(getIds()[i], idsEncoded[i], recordIdAlreadyInRepository);
							}
							else {
								//prtln("Not making an error doc for: " + ids[i]);
							}
						}
					}
				}
			}

			/* 			// Remove any IDs in the index that are the same as this one
			if(encodedIdsString != null) {
				addDocToRemove("id", encodedIdsString.trim());
			} */
			// Throw if we've got an exception
			if (errorDocException != null)
				throw errorDocException;
		}

		// Grab fields from sub-classes.
		addFields(newDoc, existingDoc, sourceFile);
	}


	// ------ Resource De-Duplication (Generate one lucene document per resoruce URL, which may contain multiple records) ------
	/**
	 *  Performs resource de-duplication to generate one Lucene document per resoruce URL, which may contain
	 *  multiple records.
	 *
	 * @param  newDoc         The new Lucene document being indexed
	 * @param  existingDoc    The existing Lucene document for this entry
	 * @param  sourceFile     Description of the Parameter
	 * @return                True if this doc represents multiple records, false if only one
	 * @exception  Exception  If error
	 */
	private boolean doResourceDeDuplication(Document newDoc, Document existingDoc, File sourceFile) throws Exception {
		boolean isDuplicate = false;

		if (processingMultiDoc)
			return false;

		// If we are process a temprary ID, return false:
		if (getPrimaryId().equals(DDSServlet.DDS_UUID + "_temp_dds_repository_put_record_file"))
			return false;

		// If we are processing a single dup record or de-duping is not enabled, do nothing (doResourceDeDuplication is set in DDSServlet from servlet config):
		String doResourceDeDuplication = (String) getFileIndexingService().getAttribute("doResourceDeDuplication");
		if (processAsSingleRecord || doResourceDeDuplication == null || !doResourceDeDuplication.equalsIgnoreCase("true"))
			return false;

		// Only process record formats for resources (not annotations or other relations):
		String myXmlFormat = getXmlFormat();
		if (!(myXmlFormat.equals("nsdl_dc") || myXmlFormat.equals("oai_dc") || myXmlFormat.equals("adn")))
			return false;

		String[] myUrls = getXmlIndexer().getUrls();
		if (myUrls == null || myUrls.length == 0)
			return false;

		SimpleLuceneIndex dupItemsIndex =
			(SimpleLuceneIndex) getFileIndexingService().getAttribute("dupItemsIndex");

		// Fetch all records with same URL in the primary index:
		BooleanQuery urlQ = new BooleanQuery();
		for (int j = 0; j < myUrls.length; j++)
			urlQ.add(new TermQuery(new Term("url", myUrls[j])), BooleanClause.Occur.MUST);
		urlQ.add(new TermQuery(new Term("xmlFormat", myXmlFormat)), BooleanClause.Occur.MUST);
		urlQ.add(new TermQuery(new Term("multirecord", "true")), BooleanClause.Occur.MUST_NOT);
		urlQ.add(new TermQuery(new Term("idvalue", getPrimaryId())), BooleanClause.Occur.MUST_NOT);

		//prtln("urlQ:'" + urlQ + "'");

		ResultDocList dupUrlRecordsPrimaryIndex = getIndex().searchDocs(urlQ);
		ResultDocList dupUrlRecordsDupItemsIndex = dupItemsIndex.searchDocs(urlQ);

		//prtln("Num dups primaryIndex:" + dupUrlRecordsPrimaryIndex.size());
		//prtln("Num dups dupsIndex:" + dupUrlRecordsDupItemsIndex.size());

		// If there are no other records with the same URL then I'm not a dup:
		if (dupUrlRecordsPrimaryIndex.size() == 0 && dupUrlRecordsDupItemsIndex.size() == 0)
			return false;

		Map idsProcessed = new HashMap();

		// Count me as processed:
		idsProcessed.put(getPrimaryId(), new Object());

		// Generate a list of duplicate records found in the primary index:
		List dupRecordsDataPrimatyIndex = new ArrayList(dupUrlRecordsPrimaryIndex.size());
		for (int i = 0; i < dupUrlRecordsPrimaryIndex.size(); i++) {
			XMLDocReader xmlDocReader = (XMLDocReader) dupUrlRecordsPrimaryIndex.get(i).getDocReader();
			File dSrc = new File(xmlDocReader.getDocsource());
			String theId = xmlDocReader.getId();
			if (!idsProcessed.containsKey(theId)) {
				idsProcessed.put(theId, new Object());
				String[] sessionIds = xmlDocReader.getDocument().getValues("indexSessionId");
				dupRecordsDataPrimatyIndex.add(new RecordHolder(xmlDocReader.getId(), xmlDocReader.getCollection(), sessionIds, dSrc));
			}
		}

		// Generate a list of duplicate records found in the dup-items index:
		List dupRecordsDataDupIndex = new ArrayList(dupUrlRecordsDupItemsIndex.size());
		for (int i = 0; i < dupUrlRecordsDupItemsIndex.size(); i++) {
			XMLDocReader xmlDocReader = (XMLDocReader) dupUrlRecordsDupItemsIndex.get(i).getDocReader();
			File dSrc = new File(xmlDocReader.getDocsource());
			String theId = xmlDocReader.getId();
			if (!idsProcessed.containsKey(theId)) {
				idsProcessed.put(theId, new Object());
				String[] sessionIds = xmlDocReader.getDocument().getValues("indexSessionId");
				dupRecordsDataDupIndex.add(new RecordHolder(xmlDocReader.getId(), xmlDocReader.getCollection(), sessionIds, dSrc));
			}
		}

		// If we have dups, mark me as such:
		isDuplicate = true;

		// Make sure there are no dups of me before proceeding
		addDocToRemove("idvalue", getPrimaryId());

		// Index myself in the dupItemsIndex:
		//prtln("Indexing myslef in the dupItemsIndex: id: " + getPrimaryId());
		boolean fetchFromPrimaryIndex = false;
		boolean indexDupIndexDoc = true;
		boolean indexMultirecordData = false;
		FileIndexingPlugin plugin = getFileIndexingPlugin();
		String[] sessionIds = null;
		if (plugin != null && plugin instanceof SessionIndexingPlugin)
			sessionIds = ((SessionIndexingPlugin) plugin).getSessionIdsArray();
		indexIndividualDupDoc(getPrimaryId(), getCollections()[0], sessionIds, getSourceFile(), fetchFromPrimaryIndex, indexDupIndexDoc, indexMultirecordData);

		// TO DO! If I was previously a dup but am no longer, remove me from the multidoc while preserving the remaining records...

		// Index other records that are in the primary index but not already in the dupItemsIndex:
		for (int i = 0; i < dupRecordsDataPrimatyIndex.size(); i++) {
			RecordHolder recordHolder = (RecordHolder) dupRecordsDataPrimatyIndex.get(i);
			//prtln("Indexing dup from primaryIndex: " + recordHolder);
			fetchFromPrimaryIndex = true;
			indexDupIndexDoc = true;
			indexMultirecordData = true;
			indexIndividualDupDoc(recordHolder.getId(), recordHolder.getColKey(), recordHolder.getSessionIds(), recordHolder.getDocSource(), fetchFromPrimaryIndex, indexDupIndexDoc, indexMultirecordData);
		}

		// Index other records that are in the dupItemsIndex:
		for (int i = 0; i < dupRecordsDataDupIndex.size(); i++) {
			RecordHolder recordHolder = (RecordHolder) dupRecordsDataDupIndex.get(i);
			prtln("Indexing dup from dupsndex: " + recordHolder);
			fetchFromPrimaryIndex = false;
			indexDupIndexDoc = false;
			indexMultirecordData = true;
			indexIndividualDupDoc(recordHolder.getId(), recordHolder.getColKey(), recordHolder.getSessionIds(), recordHolder.getDocSource(), fetchFromPrimaryIndex, indexDupIndexDoc, indexMultirecordData);
		}

		return isDuplicate;
	}


	private void indexIndividualDupDoc(String id, String coll, String[] sessionIds, File sourceFile2, boolean fetchFromPrimaryIndex, boolean indexDupIndexDoc, boolean indexMultirecordData) throws Exception {
		//prtln("indexIndividualDupDoc() id:" + id + " coll:" + coll);

		SimpleLuceneIndex dupItemsIndex =
			(SimpleLuceneIndex) getFileIndexingService().getAttribute("dupItemsIndex");

		Query idQuery = new TermQuery(new Term("idvalue", id));
		ResultDocList thisDoc = null;
		if (fetchFromPrimaryIndex)
			thisDoc = getIndex().searchDocs(idQuery);
		else
			thisDoc = dupItemsIndex.searchDocs(idQuery);
		Document myExistingDoc = null;
		if (thisDoc != null && thisDoc.size() > 0)
			myExistingDoc = ((ResultDoc) thisDoc.get(0)).getDocument();

		XMLFileIndexingWriterFactory xmlFileIndexingWriterFactory = getXmlFileIndexingWriterFactory();
		XMLFileIndexingWriter xmlWriter = null;

		// ------ Index the item into the dup items index (only if it's not already there): -------

		if (indexDupIndexDoc) {
			xmlWriter = xmlFileIndexingWriterFactory.getIndexingWriter(coll, getXmlFormat());

			// Create a new config for the de-dup writer and set the dup colletcion to it...
			// Shallow copy so we don't change the original config...
			HashMap newConfig = (HashMap) getConfigAttributes().clone();
			newConfig.put("collection", coll);
			xmlWriter.setConfigAttributes(newConfig);
			xmlWriter.setProcessAsSingleRecord(true);
			xmlWriter.setFileIndexingService(getFileIndexingService());
			FileIndexingPlugin plugin = getFileIndexingPlugin();
			// Put forward sessionIds if indicated:
			if (sessionIds != null && sessionIds.length > 0) {
				SessionIndexingPlugin newPlugin = new SessionIndexingPlugin();
				newPlugin.addAllSessionIds(sessionIds);
				plugin = newPlugin;
			}
			if (plugin != null) {
				xmlWriter.setFileIndexingPlugin(plugin);
			}
			
			Document doc = null;
			FileIndexingServiceData newData = null;

			try {
				// Applies to background indexing only:
				Document previousIndexExistingLuceneDoc = null;

				newData = xmlWriter.create(sourceFile2, myExistingDoc, previousIndexExistingLuceneDoc, plugin, null);
				doc = newData.getDocWrapper().getDocument();
			} catch (Throwable e) {
				e.printStackTrace();
				throw new Exception("Error indexing dup index item " + id + ": " + e.getMessage());
			}

			if (doc != null) {
				//prtln("removing doc id: " + id);
				addDocToRemove("idvalue", id);
				dupItemsIndex.update("idvalue", id, newData.getDocWrapper(), true);
			}
		}

		// ------ Index the item into the current multirecord -------

		if (indexMultirecordData) {
			//prtln("indexing multirecord data: id:" + id + " coll:" + coll);
			xmlWriter = xmlFileIndexingWriterFactory.getIndexingWriter(coll, getXmlFormat());

			// Create a new config for the de-dup writer and set the dup colletcion to it...
			// Shallow copy so we don't change the original config...
			HashMap newConfig = (HashMap) getConfigAttributes().clone();
			newConfig.put("collection", coll);
			xmlWriter.setDocumentWrapper(getDocumentWrapper()); // Pass in my DocumentWrapper to add data to for multirecord
			xmlWriter.setProcessingMultiDoc(true);
			xmlWriter.setConfigAttributes(newConfig);
			xmlWriter.setProcessAsSingleRecord(true);
			xmlWriter.setFileIndexingService(getFileIndexingService());
			FileIndexingPlugin plugin = getFileIndexingPlugin();
			// Put forward sessionIds if indicated:
			if (sessionIds != null && sessionIds.length > 0) {
				SessionIndexingPlugin newPlugin = new SessionIndexingPlugin();
				newPlugin.addAllSessionIds(sessionIds);
				plugin = newPlugin;
			}
			if (plugin != null)
				xmlWriter.setFileIndexingPlugin(plugin);

			// Reset
			Document doc = null;
			FileIndexingServiceData newData = null;

			try {

				// Applies to background indexing only:
				Document previousIndexExistingLuceneDoc = null;
				newData = xmlWriter.create(sourceFile2, myExistingDoc, previousIndexExistingLuceneDoc, plugin, null);
				doc = newData.getDocWrapper().getDocument();
			} catch (Throwable e) {
				e.printStackTrace();
				throw new Exception("Error indexing multi-record item id:" + id + ": " + e.getMessage());
			}

			if (doc != null) {
				//prtln("removing doc id: " + id);
				addDocToRemove("idvalue", id);
				//prtln("indexed multirecord data successfully: id:" + id + " coll:" + coll);
			}
		}
	}


	private void setProcessAsSingleRecord(boolean processAsSingleRecord) {
		this.processAsSingleRecord = processAsSingleRecord;
	}

	// Set to true if currently processing a multidoc
	private void setProcessingMultiDoc(boolean processingMultiDoc) {
		this.processingMultiDoc = processingMultiDoc;
	}


	private XMLFileIndexingWriterFactory _xmlFileIndexingWriterFactory = null;


	private XMLFileIndexingWriterFactory getXmlFileIndexingWriterFactory() {
		if (_xmlFileIndexingWriterFactory == null)
			_xmlFileIndexingWriterFactory = new XMLFileIndexingWriterFactory(getRecordDataService(), getIndex(), getXmlIndexerFieldsConfig());
		return _xmlFileIndexingWriterFactory;
	}


	/**
	 *  Struct to cache data from index records used for re-indexing.
	 *
	 * @author    John Weatherley
	 */
	private class RecordHolder {
		private String _id;
		private String _colKey;
		private File _docSource;
		private String[] _sessionIds;


		/**
		 *  String
		 *
		 * @return    String
		 */
		public String toString() {
			return "id:" + _id + " collKey:" + _colKey + " docSource:" + _docSource + " sessionIds:" + Arrays.toString(_sessionIds);
		}


		/**
		 *  Returns the value of docSource.
		 *
		 * @return    The docSource value
		 */
		public File getDocSource() {
			return _docSource;
		}


		/**
		 *  Returns the value of _id.
		 *
		 * @return    The id value
		 */
		public String getId() {
			return _id;
		}


		/**
		 *  Returns the value of sessionIds.
		 *
		 * @return    The sessionIds value, or null
		 */
		public String[] getSessionIds() {
			return _sessionIds;
		}


		/**
		 *  Returns the value of _colKey.
		 *
		 * @return    The colKey value
		 */
		public String getColKey() {
			return _colKey;
		}


		/**
		 *  Create a record for indexing
		 *
		 * @param  id          Description of the Parameter
		 * @param  colKey      Description of the Parameter
		 * @param  sessionIds  Indexing session Ids
		 * @param  docSource   docSource File
		 */
		public RecordHolder(String id, String colKey, String[] sessionIds, File docSource) {
			_id = id;
			_colKey = colKey;
			_docSource = docSource;
			_sessionIds = sessionIds;
		}
	}


	// ------ [end] Resource De-Duplication (Generate one lucene document per resoruce URL, which may contain multiple records) ------

	/**
	 *  Indexes the relations for this record (records that annotate me, relate to me, etc).
	 *
	 * @exception  Exception  If error
	 */
	private void indexRelations(DocumentWrapper documentWrapper) throws Exception {
		Document luceneDoc = documentWrapper.getDocument();
		
		boolean hasIndexedRelation = false;

		// Get all the records that are related to me:
		String[] myIds = getIds();
		String[] myUrls = getXmlIndexer().getUrls();
		if (((myIds == null || myIds.length == 0) && (myUrls == null || myUrls.length == 0)) || getIndex() == null) {
			return;
		}
		try {

			BooleanQuery idQ = new BooleanQuery();
			if (myIds != null) {
				for (int i = 0; i < myIds.length; i++)
					idQ.add(new TermQuery(new Term("assignsRelationshipById", myIds[i])), BooleanClause.Occur.SHOULD);
			}

			if (myUrls != null) {
				for (int i = 0; i < myUrls.length; i++)
					idQ.add(new TermQuery(new Term("assignsRelationshipByUrl", myUrls[i])), BooleanClause.Occur.SHOULD);
			}

			ResultDocList relatedDocs =
				getIndex().searchDocs(idQ);
			if (relatedDocs == null || relatedDocs.size() == 0) {
				//prtln("indexRelations(): " + idQ + " num: 0");
				return;
			}
			else {
				prtln("indexRelations(): " + idQ + " num: " + relatedDocs.size());

				//Index my relations...
				//List relatedIds = new ArrayList();
				for (int i = 0; i < relatedDocs.size(); i++) {
					XMLDocReader relatedXmlDocReader = (XMLDocReader) relatedDocs.get(i).getDocReader();
					
					// Don't allow a relation to self:
					if(getPrimaryId().equals(relatedXmlDocReader.getId())) {
						continue;
					}
					
					// Get the list of relations assigned for this
					List myRelationTypes = (List) relatedXmlDocReader.getAssignedRelationshipsForItemsMap().get(getPrimaryId());

					//prtln("indexRelations() for id: " + getPrimaryId() + " myRelationTypes:" + (myRelationTypes == null ? " null" : Arrays.toString(myRelationTypes.toArray())));

					if (myRelationTypes != null) {
						for (int j = 0; j < myRelationTypes.size(); j++) {
							String relationType = (String) myRelationTypes.get(j);

							// Index all xPaths for this item
							XMLIndexer xmlIndexer = new XMLIndexer(relatedXmlDocReader.getXml(), relatedXmlDocReader.getDoctype(), getXmlIndexerFieldsConfig());
							String relationPrefix= "/relation." + relationType + "/";
							xmlIndexer.setXPathFieldsPrefix(relationPrefix);

							// Add the related records text to this one's default fields?
							xmlIndexer.setIndexDefaultAndStemsField(true);

							// Index just the XPath fields:
							xmlIndexer.indexXpathFields(documentWrapper);
							//relatedIds.add(xmlDocReader.getId());
							
							// Index custom field content from the related documents (the custom field xPath config will start with the relation prefix, e.g. '/relation.isAnnotatedBy/'):
							xmlIndexer.indexCustomFields(getXmlFormat(),documentWrapper,relationPrefix);							

							// Index the IDs so these docs can be retrieved later:
							luceneDoc.add(new Field("indexedRelationIds." + relationType, relatedXmlDocReader.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED));
							luceneDoc.add(new Field("indexedRelations", relationType, Field.Store.YES, Field.Index.NOT_ANALYZED));

							// Index the collection keys so that we can search by collections that annotate, realte to me, etc:
							String collectionKey = relatedXmlDocReader.getCollectionKey();
							luceneDoc.add(new Field("isRelatedToByCollectionKey." + relationType, collectionKey, Field.Store.YES, Field.Index.NOT_ANALYZED));
							luceneDoc.add(new Field("isRelatedToByCollectionKey", collectionKey, Field.Store.YES, Field.Index.NOT_ANALYZED));
							hasIndexedRelation = true;
						}
					}
				}
			}

		} catch (Throwable e) {
			prtlnErr("indexRelations(): " + e);
			e.printStackTrace();
			return;
		} finally {
			luceneDoc.add(new Field("hasIndexedRelation", (hasIndexedRelation ? "true" : "false"), Field.Store.YES, Field.Index.NOT_ANALYZED));
		}
	}


	/**
	 *  Indexes the annotation rating information and tabulated statistics.
	 *
	 * @param  annoResultDocs  An array of anno ResultDocs
	 * @param  newDoc          The Document to add the fields to
	 */
	private void indexAnnoRatings(ResultDocList annoResultDocs, Document newDoc) {

		float numRatings = 0;
		float totalRating = 0;
		String ratings = null;
		if (annoResultDocs != null && annoResultDocs.size() > 0) {
			ratings = "";
			for (int i = 0; i < annoResultDocs.size(); i++) {
				String rating = ((DleseAnnoDocReader) annoResultDocs.get(i).getDocReader()).getRating();
				if (rating != null && rating.length() > 0) {
					try {
						totalRating += Float.parseFloat(rating);
						numRatings++;
						ratings += rating + " ";
					} catch (Exception nfe) {}
				}
			}
		}

		// The total number of ratings assigned to this resource
		newDoc.add(new Field("itemannonumratings", new DecimalFormat("00000").format(numRatings), Field.Store.YES, Field.Index.NOT_ANALYZED));

		// A String of all the ratings assigned to this resource, as numbers (e.g. '1 1 2 4 2 3')
		if (ratings != null && ratings.length() > 0)
			newDoc.add(new Field("itemannoratingvalues", ratings, Field.Store.YES, Field.Index.ANALYZED));

		// The average rating for this resource
		if (numRatings > 0 || totalRating > 0) {
			float aveRating = (totalRating / numRatings);

			NumberFormat formatter = new DecimalFormat("0.000");
			//prtln("ave rating: " + aveRating + " string: " + formatter.format(aveRating));
			newDoc.add(new Field("itemannoaveragerating", formatter.format(aveRating), Field.Store.YES, Field.Index.NOT_ANALYZED));
		}
	}


	/**
	 *  Index crawled content, weeding status, and weeding reason from the ContentCache web service, if configured.
	 *
	 * @param  luceneDoc  Lucene doc to add the content to
	 */
	private void indexCrawledContent(Document luceneDoc) {
		String hasCrawledContent = "false";
		String weedingStatus = null;
		String weedingReasonCode = null;
		try {

			String[] myUrls = getXmlIndexer().getUrls();
			String contentCacheBaseUrl = (String) getFileIndexingService().getAttribute("contentCacheBaseUrl");

			if (contentCacheBaseUrl != null && !contentCacheBaseUrl.equalsIgnoreCase("disabled") && contentCacheBaseUrl.trim().length() > 0 && myUrls != null && myUrls.length > 0) {

				String requestUrl = contentCacheBaseUrl + "/getPage?url=" + URLEncoder.encode(myUrls[0].trim(), "UTF-8");
								
				org.dom4j.Document contentCacheResponse = Dom4jUtils.localizeXml(Dom4jUtils.getXmlDocument(requestUrl, 1500, "DDS Indexer"));
								
				// Grab weeding status and reason codes:
				weedingStatus = contentCacheResponse.valueOf("/NSDLContentCache/GetPage/weedingStatus").trim();			
				weedingReasonCode = contentCacheResponse.valueOf("/NSDLContentCache/GetPage/reasonCode").trim();
				
				// Handle content cache data:
				String parsedContent = contentCacheResponse.valueOf("/NSDLContentCache/GetPage/parsedText").trim();
				String title = contentCacheResponse.valueOf("/NSDLContentCache/GetPage/title").trim();
				if (parsedContent.length() == 0)
					parsedContent = title;
				if (parsedContent.length() > 0) {
					hasCrawledContent = "true";

					String contentType = contentCacheResponse.valueOf("/NSDLContentCache/GetPage/contentType").trim();
					String modifyDate = contentCacheResponse.valueOf("/NSDLContentCache/GetPage/modifyDate").trim();
					String latestFetchDate = contentCacheResponse.valueOf("/NSDLContentCache/GetPage/latestFetch[@status='FETCHED']/@date").trim();

					luceneDoc.add(new Field("crawledContent.parsedContent.text", parsedContent, Field.Store.YES, Field.Index.ANALYZED));
					luceneDoc.add(new Field("crawledContent.parsedContent.stems", parsedContent, Field.Store.NO, Field.Index.ANALYZED));

					// Add to default and stems fields:
					addToDefaultField(parsedContent);

					if (title.length() > 0)
						luceneDoc.add(new Field("crawledContent.title", title, Field.Store.YES, Field.Index.ANALYZED));
					if (contentType.length() > 0)
						luceneDoc.add(new Field("crawledContent.contentType", contentType, Field.Store.YES, Field.Index.NOT_ANALYZED));
					if (modifyDate.length() > 0)
						luceneDoc.add(new Field("crawledContent.modifyDate", modifyDate, Field.Store.NO, Field.Index.NOT_ANALYZED));
					if (latestFetchDate.length() > 0)
						luceneDoc.add(new Field("crawledContent.latestFetchDate", latestFetchDate, Field.Store.NO, Field.Index.NOT_ANALYZED));
				}
			}
			else {
				//System.out.println("ContentCache is disabled contentCacheBaseUrl: " + contentCacheBaseUrl);
			}
		} catch (Throwable t) {
			prtlnErr("Error fetching crawled content: " + t);
		}
				
		// Index weeding status and reason codes (possible values [IN,OUT,NEEDS_QA,NEEDS_REVIEW]:
		if(weedingStatus == null || weedingStatus.length() == 0)
			weedingStatus = "NONE";				
		if(weedingReasonCode == null || weedingReasonCode.length() == 0)
			weedingReasonCode = "NONE";
		luceneDoc.add(new Field("weeding.statusCode", weedingStatus, Field.Store.YES, Field.Index.NOT_ANALYZED));
		luceneDoc.add(new Field("weeding.reasonCode", weedingReasonCode, Field.Store.YES, Field.Index.NOT_ANALYZED));
		
		// TO DO: If weeding and/or content cache data are not available, check existing doc for the data to carry forward and/or throw exception...
		
		// Index crawled content yes/no:
		luceneDoc.add(new Field("crawledContent.hasCrawledContent", hasCrawledContent, Field.Store.NO, Field.Index.NOT_ANALYZED));
	}


	private ErrorDocException makeErrorDocException(String id,
	                                                String encodedId,
	                                                File recordIdAlreadyInRepository) {
		ErrorDocException errorDocException =
			new ErrorDocException("The ID '" + id + "' that was found in this file is the same as the ID already found in file '" + recordIdAlreadyInRepository + "'", "dupIdError");
		errorDocException.putKeywordField("duplicateIdValue", id);
		errorDocException.putTextField("duplicateIdEnc", encodedId);
		errorDocException.putKeywordField("duplicateIdDocsource", recordIdAlreadyInRepository.getAbsolutePath());
		//prtln("Throwing an ErrorDocException for dup ID: " + id + " path: " + recordIdAlreadyInRepository.getAbsolutePath());
		return errorDocException;
	}


	/**
	 *  Creates a Lucene Document for the XML that is equal to the exsiting Document.
	 *
	 * @param  existingDoc    An existing FileIndexingService Document that currently resides in the index for
	 *      the given file
	 * @return                A Lucene FileIndexingService Document
	 * @exception  Throwable  Thrown if error occurs
	 */
	public synchronized Document getDeletedDoc(Document existingDoc)
		 throws Throwable {
		existingDoc = super.getDeletedDoc(existingDoc);

		Document newDocument = null;
		try {
			if (existingDoc == null) {
				//prtln("existingDoc is null!");
			}
			ResultDocConfig resultDocConfig = new ResultDocConfig(getIndex());
			ResultDoc resultDoc = new ResultDoc(existingDoc, resultDocConfig);
			XMLDocReader recordInRepository = null;

			DocReader docReader = resultDoc.getDocReader();

			// If this is an ErrorDoc, return null so it is removed:
			if (docReader instanceof ErrorDocReader) {
				//prtln("getDeletedDoc() docReader is an ErrorDocReader");
				return null;
			}

			recordInRepository = (XMLDocReader) resultDoc.getDocReader();
			//prtln("docReader is " + recordInRepository.getReaderType());

			BooleanQuery idQ = new BooleanQuery();
			idQ.add(new TermQuery(new Term("id", recordInRepository.getIdEncoded())), BooleanClause.Occur.MUST);
			idQ.add(new TermQuery(new Term("deleted", "false")), BooleanClause.Occur.MUST);

			ResultDocList resultDocs = getIndex().searchDocs((idQ));

			// If another file has replaced this one, don't create a deleted doc:
			if (resultDocs != null && resultDocs.size() > 0) {
				XMLDocReader existingDocReader = (XMLDocReader) ((ResultDoc) resultDocs.get(0)).getDocReader();
				if (!existingDocReader.getDocsource().equals(recordInRepository.getDocsource())) {
					//prtln("getDeletedDoc() There was an existing doc in the index in another location, not deleting");
					return null;
				}
				else {
					//prtln("getDeletedDoc() The existing doc in the index is this one... deleting");
				}
			}

			Document previousIndexExistingLuceneDoc = null;

			// Make a new Document and return it
			XMLFileIndexingWriterFactory xmlFileIndexingWriterFactory = new XMLFileIndexingWriterFactory(getRecordDataService(), getIndex(), getXmlIndexerFieldsConfig());
			XMLFileIndexingWriter xmlFileIndexingWriter = xmlFileIndexingWriterFactory.getIndexingWriter(recordInRepository.getCollection(), recordInRepository.getNativeFormat());
			xmlFileIndexingWriter.setIsMakingDeletedDoc(true);
			FileIndexingServiceData fileIndexingServiceData = xmlFileIndexingWriter.create(null, existingDoc, previousIndexExistingLuceneDoc, getFileIndexingPlugin(), getSessionAttributes());
			newDocument = fileIndexingServiceData.getDocWrapper().getDocument();
			if (newDocument != null) {
				//prtln("Returning a deletedDoc for id: " + recordInRepository.getId() + " file: " + recordInRepository.getDocsource());
			}
		} catch (Throwable t) {
			//t.printStackTrace();
			throw t;
		}

		return newDocument;
	}


	/**
	 *  Gets the annotations for this record, null or zero length if none available.
	 *
	 * @return                The myAnnoResultDocs value
	 * @exception  Exception  If error
	 */
	protected ResultDocList getMyAnnoResultDocs() throws Exception {
		if (_myAnnoResultDocs == null) {
			RecordDataService recordDataService = getRecordDataService();
			if (recordDataService != null) {
				// Get annotation for this record only. If I am a multi-doc, these (should) include all annos for all records
				_myAnnoResultDocs = recordDataService.getDleseAnnoResultDocs(getIds());
			}
		}
		return _myAnnoResultDocs;
	}


	/**
	 *  Gets the XMLIndexerFieldsConfig to use for XML indexing, or null if none available.
	 *
	 * @return    The xmlIndexerFieldsConfig value
	 */
	protected XMLIndexerFieldsConfig getXmlIndexerFieldsConfig() {
		return (XMLIndexerFieldsConfig) getConfigAttributes().get("xmlIndexerFieldsConfig");
	}


	/**
	 *  Gets the vocab encoded keys for the given values, separated by the '+' symbol.
	 *
	 * @param  values           The valuse to encode.
	 * @param  useVocabMapping  The mapping to use, for example "contentStandards".
	 * @param  metadataFormat   The metadata format, for example 'adn'
	 * @return                  The encoded vocab keys.
	 * @exception  Exception    If error.
	 */
	protected String getFieldContent(String[] values, String useVocabMapping, String metadataFormat)
		 throws Exception {
		if (values == null || values.length == 0) {
			return "";
		}

		RecordDataService recordDataService = getRecordDataService();

		MetadataVocab vocab = null;
		if (recordDataService != null)
			vocab = recordDataService.getVocab();

		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			String str = values[i].trim();
			if (str.length() > 0) {
				// Use vocabMgr mapping if available, otherwise output unchanged
				if (useVocabMapping != null && vocab != null) {
					try {
						ret.append(vocab.getTranslatedValue(metadataFormat, useVocabMapping, str));
					} catch (Throwable t) {
						// prtlnErr("getFieldContent(): " + t);
						ret.append(str);
						//prtlnErr("Warning: Unable to get vocab mapping for '" + useVocabMapping + ":" + str + "'. Using unencoded value instead. Reason: " + t);
					}
				}
				else {
					ret.append(str);
				}

				// Separate each term with +
				if (i < (values.length - 1)) {
					ret.append("+");
				}
			}
		}
		//prtln("Field content: " + ret.toString());
		return ret.toString();
	}


	/**
	 *  Gets the encoded vocab key for the given content.
	 *
	 * @param  value            The value to encode
	 * @param  useVocabMapping  The vocab mapping to use, for example 'contentStandard'
	 * @param  metadataFormat   The metadata format, for example 'adn'
	 * @return                  The encoded value, or unchanged if unable to encode
	 * @exception  Exception    If error
	 */
	protected String getFieldContent(String value, String useVocabMapping, String metadataFormat)
		 throws Exception {
		if (value == null || value.trim().length() == 0) {
			return "";
		}

		RecordDataService recordDataService = getRecordDataService();

		MetadataVocab vocab = null;
		if (recordDataService != null)
			vocab = recordDataService.getVocab();

		// Use vocabMgr mapping if available, otherwise output unchanged
		if (useVocabMapping != null && vocab != null) {
			try {
				return vocab.getTranslatedValue(metadataFormat, useVocabMapping, value);
			} catch (Throwable t) {
				// prtlnErr("getFieldContent(): " + t);
				return value;
			}
		}
		else {
			return value;
		}
	}


	/**
	 *  Gets the field ID, for example 'gr', for a given vocab, for example 'gradeRange'. If unable to get the
	 *  field ID, the vocab field String is returned unchanged.
	 *
	 * @param  vocabFieldString  The field, for example 'gradeRange'
	 * @param  metadataFormat    The metadata format, for example 'adn'
	 * @return                   The field key, for example 'gr', or unchanged if unable to determine
	 * @exception  Exception     If error
	 */
	protected String getFieldName(String vocabFieldString, String metadataFormat)
		 throws Exception {
		if (vocabFieldString == null || vocabFieldString.trim().length() == 0) {
			return "";
		}

		RecordDataService recordDataService = getRecordDataService();

		MetadataVocab vocab = null;
		if (recordDataService != null)
			vocab = recordDataService.getVocab();

		String fieldName;
		if (vocab == null) {
			fieldName = vocabFieldString;
		}
		else {
			try {
				fieldName = vocab.getTranslatedField(metadataFormat, vocabFieldString);
			} catch (Throwable t) {
				// prtlnErr("getFieldName(): " + t);
				fieldName = vocabFieldString;
				//prtlnErr("Warning: Unable to get vocab mapping for '" + vocabFieldString + "'. Using unencoded value instead. Reason: " + t);
			}
		}

		//prtln("Field name " + vocabFieldString + " encoded as id: " + fieldName);
		return fieldName;
	}



	/**
	 *  Gets the appropriate terms from a string array of metadata fields. Uses all terms found after the last
	 *  colon ":" found in the string.
	 *
	 * @param  vals  Metadata fields that must be delemited by colons.
	 * @return       The individual terms used for indexing.
	 */
	protected String getTermStringFromStringArray(String[] vals) {
		if (vals == null) {
			return "";
		}
		String tmp = "";
		try {
			for (int i = 0; i < vals.length; i++) {
				tmp += " " + vals[i].substring(vals[i].lastIndexOf(":") + 1, vals[i].length());
			}
		} catch (Throwable e) {}
		return tmp.trim();
	}


	/**
	 *  Gets the XMLIndexer for use by sub-classes
	 *
	 * @return                The XMLIndexer
	 * @exception  Exception  If error
	 */
	protected XMLIndexer getXmlIndexer() throws Exception {
		if (_xmlIndexer == null)
			_xmlIndexer = new XMLIndexer(getFileContent(), getXmlFormat(), getXmlIndexerFieldsConfig());
		return _xmlIndexer;
	}


	/**
	 *  Gets the dom4j Document for use by sub-classes
	 *
	 * @return                The Document
	 * @exception  Exception  If error
	 */
	protected org.dom4j.Document getDom4jDoc() throws Exception {
		return getXmlIndexer().getXmlDocument();
	}


	/**
	 *  Gets the DLESECollectionDocReader for the collection in which this item is a part, or null if not
	 *  available.
	 *
	 * @return    The myCollectionDoc value
	 */
	protected DleseCollectionDocReader getMyCollectionDoc() {
		if (myCollectionDocReader == null) {
			RecordDataService recordDataService = getRecordDataService();

			// RecordDataService is not available in OAI app:
			if (recordDataService == null) {
				//prtlnErr("Error: recordDataService is null! Cannot get collection doc.");
				return null;
			}

			ResultDocList collections;
			try {
				String q = "key:" + getCollections()[0];
				collections = recordDataService.getIndex().searchDocs(q);
			} catch (Throwable e) {
				// When the index is first built, the 'collect' collection is not avaialble until it is written to the index
				prtlnErr("Unable to get collection doc: " + e);
				return null;
			}
			if (collections != null && collections.size() > 0)
				myCollectionDocReader = (DleseCollectionDocReader) ((ResultDoc) collections.get(0)).getDocReader();
		}
		return myCollectionDocReader;
	}


	/**
	 *  Gets the oaiModtime for the given record. If the XML has changed since the previous version as determined
	 *  by String.equals(), set to 3 minutes in the future to account for any delay in indexing updates,
	 *  otherwies oaiModtime stays the same.
	 *
	 * @param  existingDoc                     The existing Doc in the current working index
	 * @param  previousIndexExistingLuceneDoc  The existing Doc in the provious index, used for background indexing
	 * @return                                 The oaiModtime value
	 */
	public final String getOaiModtime(Document existingDoc, Document previousIndexExistingLuceneDoc) {
		try {
			//prtln("getOaiModtime() previousIndexExistingLuceneDoc is " + (previousIndexExistingLuceneDoc == null ? "null" : "not null"));
			
			Document thePreviousDocument = previousIndexExistingLuceneDoc;
			if (thePreviousDocument == null)
				thePreviousDocument = existingDoc;

			String newXMLVersion = getFileContent();
			String prevXMLVersion = null;
			if (thePreviousDocument != null)
				prevXMLVersion = thePreviousDocument.get("filecontent");

			// If the new record XML has not changed since the previous version, use existing oaimodtime:
			if (newXMLVersion != null && prevXMLVersion != null && prevXMLVersion.equals(newXMLVersion)) {
				String oaimodtime = thePreviousDocument.get("oaimodtime");
				if (oaimodtime != null) {
					return oaimodtime;
				}
			}
		} catch (Throwable t) {
			//prtln("Error fetching oaimodtime: " + t);
		}

		// If the XML records is new or has changed, generate a new oaimodtime:
		return DateFieldTools.timeToString(System.currentTimeMillis() + 180000);
	}


	/**
	 *  Gets the recordDataService used by this XML File Indexer
	 *
	 * @return    The recordDataService, or null if not available.
	 */
	protected RecordDataService getRecordDataService() {
		return (RecordDataService) getConfigAttributes().get("recordDataService");
	}


	/**
	 *  Gets the index used by this XML File Indexer
	 *
	 * @return    The index, or null if not available.
	 */
	protected SimpleLuceneIndex getIndex() {
		return (SimpleLuceneIndex) getConfigAttributes().get("index");
	}

}

