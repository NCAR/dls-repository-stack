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
package edu.ucar.dls.index.reader;

import org.apache.lucene.document.*;
import org.apache.lucene.search.*;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.index.Term;
import edu.ucar.dls.index.writer.*;
import edu.ucar.dls.index.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.oai.OAIUtils;
import edu.ucar.dls.webapps.tools.*;
import edu.ucar.dls.util.*;
import edu.ucar.dls.vocab.*;
import edu.ucar.dls.repository.*;
import edu.ucar.dls.index.document.DateFieldTools;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.servlet.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *  A bean meant for subclassing that contains methods to read search results from indexed XML records.
 *  Subclasses of this bean provide specific accessor methods for XML data of their type. The data is read
 *  from a Lucene {@link org.apache.lucene.document.Document} that was returned from a search and from XML on
 *  file. <p>
 *
 *  In general, one XMLDocReader may be created for each document type that is defined in package {@link
 *  edu.ucar.dls.index.writer}.
 *
 * @author    John Weatherley
 * @see       edu.ucar.dls.index.writer.XMLFileIndexingWriter
 */
public class XMLDocReader extends FileIndexingServiceDocReader {
	private static boolean debug = false;

	private final String DEFAULT = "(null)";
	private static XMLConversionService xmlConversionService = null;
	/**  The vocab manager */
	protected MetadataVocab metadataVocab = null;
	private ArrayList availableFormats = null;
	private String oaiDublinCoreXml = null;
	private String nsdlDublinCoreXml = null;

	// The old vocab scheme:
	//private String vocabInterface = "dds.descr.en-us";

	// The new vocab scheme:
	private String vocabLang = "en-us";
	private String vocabAudience = "community";
	private boolean vocabAbbreviated = false;

	/**  The record data service */
	protected RecordDataService recordDataService = null;

	/**  The RepositoryManager */
	protected RepositoryManager repositoryManager = null;

	private org.dom4j.Document xmlDoc = null;
	private org.w3c.dom.Document w3cXmlDoc = null;
	private String requestedXmlFormat = null;

	private DleseCollectionDocReader[] _myCollectionDocs = null;
	private String myXml = null;
	private String xmlStripped = null;
	private String xmlLocalized = null;
	private String[] _relatedIds = null;
	private String[] _collectionKeys = null;
	private Map _relatedRecordsMap = null;
	private Map _assignedByIdRelatedRecordsMap = null;
	private Map _assignedByUrlRelatedRecordsMap = null;
	private Map _assignedRelationshipsForItems = null;
	private List _idsOfRecordsWithAssignedRelationships = null;

	private DocumentMap _documentMap = null;
	private LazyDocumentMap _lazyDocumentMap = null;
	private LazyDocumentMapValuesArray _lazyDocumentMapValuesArray = null;


	/**  Constructor for the XMLDocReader object */
	public XMLDocReader() { }


	/**  Initialized a new XMLDocReader at search time. */
	public void init() {
		repositoryManager = (RepositoryManager) getAttribute("repositoryManager");
		xmlConversionService = (XMLConversionService) getAttribute("xmlConversionService");
		recordDataService = (RecordDataService) getAttribute("recordDataService");
		if (recordDataService != null)
			metadataVocab = recordDataService.getVocab();
		if (repositoryManager != null) {
			vocabLang = repositoryManager.getMetadataVocabLanguageDefault();
			vocabAudience = repositoryManager.getMetadataVocabAudienceDefault();
		}

		initDeDuping();
	}


	/**
	 *  Constructor that may be used programatically to wrap a reader around a Lucene {@link
	 *  org.apache.lucene.document.Document} created by a {@link edu.ucar.dls.index.writer.FileIndexingServiceWriter}.
	 *
	 * @param  doc  A Lucene {@link org.apache.lucene.document.Document} created by a {@link
	 *      edu.ucar.dls.index.writer.ItemFileIndexingWriter}.
	 */
	public XMLDocReader(Document doc) {
		super(doc);
	}


	/**
	 *  Gets the String 'XmlDocReader,' which is the key that describes this reader type. This may be used in
	 *  (Struts) beans to determine which type of reader is available for a given search result and thus what
	 *  data is available for display in the UI. The reader type determines which getter methods are available.
	 *
	 * @return    The String 'XmlDocReader'.
	 */
	public String getReaderType() {
		return "XMLDocReader";
	}


	/**
	 *  Gets the full text of the content that was indexed.
	 *
	 * @return    The indexedContent value.
	 */
	public String getIndexedContent() {
		try {
			return XMLConversionService.getContentFromXML(getXml());
		} catch (Exception e) {
			//prtln("XmlDocReader: Unable to read file: " + e);
			return "";
		}
	}


	/**
	 *  Gets the title of the item.
	 *
	 * @return    The title or empty if none
	 */
	public String getTitle() {
		String t = doc.get("title");

		if (t == null)
			return "";
		else
			return t;
	}


	/**
	 *  Gets the description for the item.
	 *
	 * @return    The description or empty
	 */
	public String getDescription() {
		String t = doc.get("description");

		if (t == null)
			return "";
		else
			return t;
	}


	/**
	 *  Gets the url for the item.
	 *
	 * @return    The url or empty
	 */
	public String getUrl() {
		String t = doc.get("url");

		if (t == null)
			return "";
		else
			return t;
	}


	/**
	 *  Gets the id for this record, for example 'DLESE-000-000-000-001'.
	 *
	 * @return    The id value
	 */
	public String getId() {
		String val = "";
		String vals[] = doc.getValues("idvalue");
		for (int i = 0; i < vals.length; i++)
			val += vals[i] + (vals.length - 1 == i ? "" : " ");

		return val;
	}


	/**
	 *  Gets the id for this record as encoded for unique searching and inexing.
	 *
	 * @return    The id encoded
	 */
	public String getIdEncoded() {
		String t = doc.get("idvalue");

		if (t == null)
			return "";
		else
			return SimpleLuceneIndex.encodeToTerm(t);
	}


	/**
	 *  Gets the metadata previx (format) of the file associated with this reader, for example 'dlese_ims' or
	 *  'adn'.
	 *
	 * @return    The metadataPrefix value
	 */
	public String getMetadataPrefix() {
		String t = doc.get("metadatapfx");

		if (t == null)
			return "";
		// Remove leading '0'
		else
			return t.substring(1, t.length());
	}


	/**
	 *  Gets the nativeFormat of the file associated with this reader, for example 'dlese_ims' or 'adn'. Same as
	 *  {@link #getMetadataPrefix()}.
	 *
	 * @return    The nativeFormat.
	 */
	public String getNativeFormat() {
		return getMetadataPrefix();
	}


	/**
	 *  Gets a {@link edu.ucar.dls.index.reader.DocumentMap} of all field/values contained in the Lucene {@link
	 *  org.apache.lucene.document.Document}. The text values in each field is stored in the Map as Strings. For
	 *  example getDocMap.get("title") returns the text that was indexed and stored under the field name "title"
	 *  for this Document.<p>
	 *
	 *  This method provides access to data in the de-duped Document.<p>
	 *
	 *
	 *
	 * @return    A Map of all field/values contained in this Document, or null
	 */
	public DocumentMap getDocMap() {
		if (_documentMap == null)
			_documentMap = new DocumentMap(doc);

		return _documentMap;
	}


	/**
	 *  Gets a {@link edu.ucar.dls.index.reader.LazyDocumentMap} of all field/values contained in the Lucene
	 *  {@link org.apache.lucene.document.Document}. The text values in each field is stored in the Map as
	 *  Strings. For example getDocMap.get("title") returns the text that was indexed and stored under the field
	 *  name "title" for this Document.<p>
	 *
	 *  This method provides access to data in the de-duped Document.<p>
	 *
	 *
	 *
	 * @return    A Map of all field/values contained in this Document, or null
	 */
	public LazyDocumentMap getLazyDocMap() {
		if (_lazyDocumentMap == null)
			_lazyDocumentMap = new LazyDocumentMap(doc);
		return _lazyDocumentMap;
	}


	/**
	 *  Gets a {@link edu.ucar.dls.index.reader.LazyDocumentMapValuesArray} of all field/values contained in the
	 *  Lucene {@link org.apache.lucene.document.Document}. The text values in each field is stored in the Map as
	 *  Strings Arrays. For example getDocMap.get("title") returns and Array of values that were indexed and
	 *  stored under the field name "title" for this Document.<p>
	 *
	 *  This method provides access to data in the de-duped Document.<p>
	 *
	 *
	 *
	 * @return    A Map of all field/values contained in this Document, or null
	 */
	public LazyDocumentMapValuesArray getLazyDocMapValuesArray() {
		if (_lazyDocumentMapValuesArray == null)
			_lazyDocumentMapValuesArray = new LazyDocumentMapValuesArray(doc);
		return _lazyDocumentMapValuesArray;
	}


	/**
	 *  Gets a {@link edu.ucar.dls.index.reader.DocumentMap} of all field/values contained in the Lucene {@link
	 *  org.apache.lucene.document.Document}. The text values in each field is stored in the Map as Strings. For
	 *  example getDocMap.get("title") returns the text that was indexed and stored under the field name "title"
	 *  for this Document.<p>
	 *
	 *  This method provides access to data in the multidoc Document.<p>
	 *
	 *
	 *
	 * @return    A Map of all field/values contained in this Document, or null
	 */
	public DocumentMap getDocMapMultidoc() {
		return super.getDocMap();
	}


	/**
	 *  Gets a {@link edu.ucar.dls.index.reader.LazyDocumentMap} of all field/values contained in the Lucene
	 *  {@link org.apache.lucene.document.Document}. The text values in each field are concatinated and stored in
	 *  the Map as Strings. For example getDocMap.get("title") returns the all that was indexed and stored under
	 *  the field name "title" for this Document.<p>
	 *
	 *  This method provides access to data in the multidoc Document.
	 *
	 * @return    A Map of all field/values contained in this Document, or null
	 */
	public LazyDocumentMap getLazyDocMapMultiRecord() {
		return super.getLazyDocMap();
	}


	/**
	 *  Gets a {@link edu.ucar.dls.index.reader.LazyDocumentMapValuesArray} of all field/values contained in the
	 *  Lucene {@link org.apache.lucene.document.Document}. The text values in each field is stored in the Map as
	 *  Strings Arrays. For example getDocMap.get("title") returns and Array of values that were indexed and
	 *  stored under the field name "title" for this Document.<p>
	 *
	 *  This method provides access to data in the multidoc Document.
	 *
	 * @return    A Map of all field/values contained in this Document, or null
	 */
	public LazyDocumentMapValuesArray getLazyDocumentMapValuesArrayMultiRecord() {
		return super.getLazyDocumentMapValuesArray();
	}


	/**
	 *  Gets the Whats New type, which is one of 'itemnew,' 'itemannocomplete,' 'itemannoinprogress,'
	 *  'annocomplete,' 'annoinprogress,' 'drcannocomplete,' 'drcannoinprogress,' 'collection'.
	 *
	 * @return    The What's New type or empty String.
	 */
	public String getWhatsNewType() {
		String t = doc.get("wntype");

		if (t == null)
			return "";
		return t;
	}


	/**
	 *  Gets the Whats New date as a String. Note that for ADN records, the appropriate method to use when
	 *  displaying this value to an end user is {@link ItemDocReader#getMultiWhatsNewDate()}.
	 *
	 * @return    The What's New date or empty String.
	 */
	public String getWhatsNewDate() {
		String t = doc.get("wndate");

		if (t == null)
			return "";

		try {
			SimpleDateFormat df = new SimpleDateFormat("MMM' 'dd', 'yyyy");
			return df.format(DateFieldTools.stringToDate(t));
		} catch (Throwable e) {
			prtlnErr("Error getWhatsNewDate(): " + e);
			return "";
		}
	}


	/**
	 *  Gets the Whats New date as a Date. Note that for ADN records, the appropriate method to use when
	 *  displaying this value to an end user is {@link ItemDocReader#getMultiWhatsNewDateDate()}.
	 *
	 * @return    The What's New date or null.
	 */
	public Date getWhatsNewDateDate() {
		String t = doc.get("wndate");

		if (t == null)
			return null;

		try {
			return new Date(DateFieldTools.stringToTime(t));
		} catch (Throwable e) {
			prtlnErr("Error getWhatsNewDateDate(): " + e);
			return null;
		}
	}


	/**
	 *  Gets the OAI sets associated with this record as an ArrayList of Strings, for example 'dcc' or null.
	 *
	 * @return    A List of OAI set Strings, or null if none
	 */
	public List getOaiSets() {
		if (repositoryManager == null)
			return null;
		return repositoryManager.getOaiSetsForId(getId(), doc);
	}


	/**
	 *  Gets the sets (collections) associated with this record as a single String. The resoruce will have more
	 *  than one collection when #getIsMultiRecord returns true.
	 *
	 * @return    The sets/collections.
	 */
	public String getSetString() {
		String[] sets = getSets();
		String s = "";
		for (int i = 0; i < sets.length; i++)
			s = s + " " + sets[i];

		return s;
	}


	/**
	 *  Gets the sets (collections) associated with this record as an array of Strings, for example dcc, which is
	 *  not the same as the OAI sets. Assumes the set key has not been encoded using the vocab manager. The first
	 *  set in the array is the primary set. Additional sets, if present, represent secondary sets that also
	 *  catalog this record. The resoruce will have more than one collection when #getIsMultiRecord returns true.
	 *  This method is identical to #getCollections.
	 *
	 * @return    The set(s) associated with this record.
	 */
	public String[] getSets() {
		return getCollections();
	}


	/**
	 *  Gets the primary set (collection) associated with this item, for example dcc. This method is identical to
	 *  #getCollection. The first collection in the array is the primary collection. Additional collection, if
	 *  present, represent secondary collections that have been associated with this record via the ID mapper
	 *  service.
	 *
	 * @return    The primary collection associated with this record.
	 */
	public String getSet() {
		return getCollection();
	}


	/**
	 *  Gets the primary collection associated with this item, for example 'dcc'. This method is identical to
	 *  #getSet. The first collection in the array is the primary collection. Additional collection, if present,
	 *  represent secondary collections that have been associated with this record via the ID mapper service.
	 *
	 * @return    The primary collection associated with this record.
	 */
	public String getCollection() {
		try {
			return getCollections()[0];
		} catch (Throwable e) {
			return null;
		}
	}


	private String[] _collections = null;


	/**
	 *  Gets the collections associated with this record as an array of Strings, for example {dcc,comet}. The
	 *  first collection in the array is the primary collection. This method is identical to #getSets. Assumes
	 *  the collection key has not been encoded using the vocab manager. Additional collection, if present,
	 *  represent secondary collections that also catalog this resource. The resoruce will have more than one
	 *  collection when #getIsMultiRecord returns true.
	 *
	 * @return    The collection(s) associated with this record.
	 */
	public String[] getCollections() {
		if (_collections == null) {
			List myCols = new ArrayList();
			String[] primaryCol = doc.getValues("collection");
			if (primaryCol != null && primaryCol.length > 1)
				myCols.add(primaryCol[1]);

			String[] allCols = getMultiDoc().getValues("collection");
			for (int i = 0; i < allCols.length; i += 2)
				if (!myCols.contains(allCols[i + 1]))
					myCols.add(allCols[i + 1]);
			_collections = (String[]) myCols.toArray(new String[]{});
		}
		return _collections;
	}


	/**
	 *  Gets the collection key associated with this record, for example 01. Assumes the set key has been encoded
	 *  using the vocab manager and that there is only one collection associated with this item.
	 *
	 * @return    The collection for which this item belogs, or empty String if none available
	 */
	public String getCollectionKey() {
		if (getCollectionKeys().length == 0)
			return "";
		return getCollectionKeys()[0];
	}


	/**
	 *  Gets the collection keys for all collections that catalog this resource including this collection. The
	 *  resoruce will have more than one collection when #getIsMultiRecord returns true. Assumes the set key has
	 *  been encoded using the vocab manager, for example {01,02}, and that there are more than one collections
	 *  associated with this item.
	 *
	 * @return    The collections for which this item belogs, or empty Array if none
	 */
	public String[] getCollectionKeys() {
		if (_collectionKeys == null)
			_collectionKeys = getMultiDoc().getValues("ky");
		return _collectionKeys;
	}


	String[] _associatedCollectionKeys = null;


	/**
	 *  Gets the collection keys for all collections that are associated with this resource, not including this
	 *  collection. The resoruce will have more than one collection when #getIsMultiRecord returns true.
	 *
	 * @return    An array of associatedCollectionKeys Strings
	 */
	public String[] getAssociatedCollectionKeys() {
		if (_associatedCollectionKeys == null) {
			List assocKeys = new ArrayList();
			String[] keys = getCollectionKeys();
			for (int i = 0; i < keys.length; i++) {
				if (!keys[i].equals(getCollectionKey()) && !assocKeys.contains(keys[i]))
					assocKeys.add(keys[i]);
			}
			_associatedCollectionKeys = (String[]) assocKeys.toArray(new String[]{});
		}
		return _associatedCollectionKeys;
	}


	/**
	 *  Gets the collection UI label from the vocab manager for this record, for example 'DLESE Community
	 *  Collection (DCC)', or the short title from the collection record if the vocab manager is not available.
	 *  To specify the vocab interface to use, first call {@link #setVocabInterface(String)}, otherwise the
	 *  default will be used, which is 'dds.descr.en-us'.<p>
	 *
	 *  An example application using JSTL might look like:<br>
	 *  <code>
	 *  <pre>
	 *   &lt;%-- The following line is optional --%&gt;
	 *   &lt;c:set property=&quot;vocabInterface&quot; target=&quot;${docReader}&quot; value=&quot;dds.descr.en-us&quot;/&gt;
	 *   ${docReader.collectionLabel}
	 *  </pre> </code>
	 *
	 * @return    The collection label for this record
	 */
	public String getCollectionLabel() {
		String label = getUiLabelFromVocabId("ky", getCollectionKey(), "dlese_collect");
		if (label == null || label.trim().length() == 0 || label.startsWith("<!-- MUI") || label.equals(getCollectionKey()))
			label = getMyCollectionDoc().getShortTitle();
		return label;
	}


	//private DleseCollectionDocReader _myCollectionDoc = null;

	/**
	 *  Gets the collection doc for the primary collection in which this record is a part.
	 *
	 * @return    The myCollectionDoc value, or null if none available.
	 */
	public DleseCollectionDocReader getMyCollectionDoc() {
		try {
			return getMyCollectionDocs()[0];
		} catch (Throwable t) {
			prtlnErr("XMLDocReader.getMyCollectionDoc(): " + t);
			return null;
		}

	}


	/**
	 *  Gets the collection docs for all collections that catalog this resource, with the primary collection
	 *  first.
	 *
	 * @return    The myCollectionDoc value, or null if none available.
	 */
	public DleseCollectionDocReader[] getMyCollectionDocs() {
		if (_myCollectionDocs == null) {
			try {
				BooleanQuery bq = new BooleanQuery();
				BooleanQuery bq2 = new BooleanQuery();
				String[] cols = getCollections();
				// Search for all collections using boost to order primary collection first:
				for (int i = 0; i < cols.length; i++) {
					TermQuery tq = new TermQuery(new Term("key", cols[i]));
					if (i == 0)
						tq.setBoost(10000f);
					bq2.add(tq, BooleanClause.Occur.SHOULD);
				}
				bq.add(bq2, BooleanClause.Occur.MUST);
				bq.add(new TermQuery(new Term("readerclass", "edu.ucar.dls.index.reader.DleseCollectionDocReader")), BooleanClause.Occur.MUST);
				ResultDocList results = getPrimaryIndex().searchDocs(bq);

				if (results == null || results.size() == 0)
					return null;
				List myDocsList = new ArrayList(results.size());
				for (int i = 0; i < results.size(); i++)
					myDocsList.add(results.get(i).getDocReader());

				_myCollectionDocs = (DleseCollectionDocReader[]) myDocsList.toArray(new DleseCollectionDocReader[]{});
			} catch (Throwable t) {
				prtlnErr("XMLDocReader.getMyCollectionDocs(): " + t);
				return null;
			}
		}
		return _myCollectionDocs;
	}


	/**
	 *  Determines whether my collection is 'enabled'.
	 *
	 * @return    True if my collection is configured and is enabled, false if not
	 */
	public boolean getIsMyCollectionEnabled() {
		if (repositoryManager == null)
			return true;

		return repositoryManager.isSetEnabled(getCollectionKey());
	}


	/**
	 *  Determines whether my collection is 'disabled'.
	 *
	 * @return    True if my collection is configured and is disabled, false if not
	 */
	public boolean getIsMyCollectionDisabled() {
		if (repositoryManager == null)
			return true;

		return repositoryManager.isSetDisabled(getCollectionKey());
	}


	/**
	 *  Gets the ID of collection record in which this item belongs.
	 *
	 * @return    The ID of this item's collection record, or null if not available.
	 */
	public String getMyCollectionsRecordId() {
		return doc.get("myCollectionRecordIdValue");
	}


	/**
	 *  Gets the full XML for this record in it's native format. Characters in the String returned are encoded as
	 *  UTF-8.
	 *
	 * @return    The XML, or empty string if unable to process.
	 */
	public final String getXml() {
		if (myXml != null)
			return myXml;

		try {
			myXml = getFullContentEncodedAs("UTF-8");
		} catch (Exception ioe) {
			prtlnErr("Error reading XML: " + ioe);
		}
		if (myXml == null)
			return "";
		else
			return myXml;
	}


	/**
	 *  Gets a dom4j XML Document for this record. This method is optimized to create only one DOM when accessed
	 *  multiple times for the same XMLDocReader.
	 *
	 * @return    A dom4j XML Document, or null if unable to read
	 */
	public final org.dom4j.Document getXmlDoc() {
		if (xmlDoc != null)
			return xmlDoc;
		try {
			xmlDoc = Dom4jUtils.getXmlDocument(getXml());
			return xmlDoc;
		} catch (Throwable e) {
			prtlnErr("Error reading xml Doc: " + e);
			return null;
		}
	}


	/**
	 *  Gets a org.w3c.dom.Document for this record. This method is optimized to create only one DOM when
	 *  accessed multiple times for the same XMLDocReader.
	 *
	 * @return    A org.w3c.dom.Document, or null if unable to read.
	 */
	public org.w3c.dom.Document getW3CXmlDoc() {
		if (w3cXmlDoc != null)
			return w3cXmlDoc;

		DocumentBuilderFactory docfactory
				 = DocumentBuilderFactory.newInstance();
		docfactory.setCoalescing(true);
		docfactory.setExpandEntityReferences(true);
		docfactory.setIgnoringComments(true);

		docfactory.setNamespaceAware(true);

		// We must set validation false since jdk1.4 parser
		// doesn't know about schemas.
		docfactory.setValidating(false);

		// Ignore whitespace doesn't work unless setValidating(true),
		// according to javadocs.
		docfactory.setIgnoringElementContentWhitespace(false);
		try {
			DocumentBuilder docbuilder = docfactory.newDocumentBuilder();
			w3cXmlDoc = docbuilder.parse(getXml());
		} catch (Throwable e) {
			return null;
		}
		return w3cXmlDoc;
	}


	/**
	 *  Gets the full XML for this record in it's native format, with no XML or DTD declaration. Characters in
	 *  the returned String are encoded as UTF-8.
	 *
	 * @return    The XML, or empty string if unable to process.
	 */
	public final String getXmlStripped() {
		//prtln("getXmlStripped()");

		if (xmlStripped != null)
			return xmlStripped;

		try {
			xmlStripped = XMLConversionService.stripXmlDeclaration(
					new BufferedReader(new StringReader(getXml()))).toString();
		} catch (Exception ioe) {
			prtlnErr("Error reading XML: " + ioe);
		}

		if (xmlStripped == null)
			return "";
		else
			return xmlStripped;
	}


	/**
	 *  Gets the native format in a localized form if available, otherwise returns the native format stripped of
	 *  XML and DTD declarations. Localized XML contains no namespace declarations, making XPath sytax much
	 *  simpler to work with since there is no need to use the local-name() function. Characters in the String
	 *  retured are encoded at UTF-8.
	 *
	 * @return    The localized XML, or the native format stripped of XML and DTD declarations.
	 */
	public final String getXmlLocalized() {
		//prtln("getXmlLocalized()");

		if (xmlLocalized != null)
			return xmlLocalized;

		if (xmlConversionService == null) {
			xmlLocalized = getXmlStripped();
		}
		else {
			String nativeFormat = getDoctype();
			StringBuffer xml =
					xmlConversionService.getConvertedXml(nativeFormat, (nativeFormat + "-localized"), getFile(), this);

			if (xml == null)
				xmlLocalized = getXmlStripped();
			else
				xmlLocalized = xml.toString();
		}
		if (xmlLocalized == null)
			return "";
		else
			return xmlLocalized;
	}


	/**
	 *  Gets XML in the given format. The resulting String contains XML in the given format, or an empty String
	 *  if unable to dissiminate. Uses an {@link edu.ucar.dls.xml.XMLConversionService} to perform the
	 *  transformation from the native format to the requested format. If <code>filter</code> is set to true then
	 *  the output will have the XML declaration stripped out and the DTD declaration will be commented out, in
	 *  the case of DLESE IMS. Use <code>filter=true</code> to get XML suitable for insertion into an OAI
	 *  container. Use <code>filter=true</code> to get the full XML including XML and DTD declaration, if
	 *  present. Characters in the String returned are encoded as UTF-8.
	 *
	 * @param  format  The format desired.
	 * @param  filter  Indicates whether to filter out the XML and DTD declaration.
	 * @return         XML for the given format, or an empty String if unable to process.
	 */
	public String getXmlFormat(String format, boolean filter) {
		//prtln("getXmlFormat() format: " + format);
		if (format == null)
			return "";
		if (format.equals(getDoctype())) {
			if (filter)
				return getXmlStripped();
			else
				return getXml();
		}
		else if (xmlConversionService != null) {
			StringBuffer xml =
					xmlConversionService.getConvertedXml(getDoctype(), format, getFile(), this);

			if (xml == null)
				return "";
			else
				return xml.toString();
		}
		else
			return "";
	}


	/**
	 *  Gets XML content in the format that was previously specified using the {@link
	 *  #setRequestedXmlFormat(String)} method, or the localized native format if none was specified. The
	 *  resulting String contains XML in the requested format, or an empty String if unable to process. Uses an
	 *  {@link edu.ucar.dls.xml.XMLConversionService} to perform the transformation from the native format to
	 *  the requested format. XML and DTD declarations do not appear the output.
	 *
	 * @return    XML for the requested format, or an empty String if unable to process.
	 * @see       #setRequestedXmlFormat(String)
	 */
	public final String getRequestedXmlFormat() {
		if (requestedXmlFormat == null)
			return this.getXmlLocalized();

		else if (xmlConversionService != null) {
			StringBuffer xml =
					xmlConversionService.getConvertedXml(getDoctype(), requestedXmlFormat, getFile(), this);

			if (xml == null)
				return "";
			else
				return xml.toString();
		}
		else
			return "";
	}


	/**
	 *  Same as {@link #getRequestedXmlFormat} except returns the non-localized XML if no format was specified.
	 *  XML and DTD declarations do not appear the output.
	 *
	 * @return    XML for the requested format, or an empty String if unable to process.
	 * @see       #setRequestedXmlFormat(String)
	 * @see       #getRequestedXmlFormat
	 */
	public final String getRequestedXml() {
		if (requestedXmlFormat == null)
			return this.getXmlStripped();

		else if (xmlConversionService != null) {
			StringBuffer xml =
					xmlConversionService.getConvertedXml(getDoctype(), requestedXmlFormat, getFile(), this);

			if (xml == null)
				return "";
			else
				return xml.toString();
		}
		else
			return "";
	}


	/**
	 *  Sets the XML format type that will be returned by the {@link #getRequestedXmlFormat} method, for example
	 *  'nsdl_dc'.
	 *
	 * @param  format  The new requestedXmlFormat value
	 * @see            #getRequestedXmlFormat
	 */
	public void setRequestedXmlFormat(String format) {
		requestedXmlFormat = format;
	}


	/**
	 *  Gets the XML format type that will be returned by the {@link #getRequestedXmlFormat} method, for example
	 *  'nsdl_dc'.
	 *
	 * @return    The requestedXmlFormatType value
	 */
	public final String getRequestedXmlFormatType() {
		if (requestedXmlFormat == null)
			return getNativeFormat();
		else
			return requestedXmlFormat;
	}


	/**
	 *  Gets the XML formats that are available for this item including those that are available through {@link
	 *  edu.ucar.dls.xml.XMLConversionService}.
	 *
	 * @return    The availableFormats, for example adn, oai_dc
	 */
	public ArrayList getAvailableFormats() {
		if (availableFormats == null) {
			try {
				if (xmlConversionService == null) {
					availableFormats = new ArrayList(1);
					availableFormats.add(getDoctype());
				}
				else {
					availableFormats = xmlConversionService.getAvailableFormats(getDoctype());
				}
			} catch (Throwable t) {
				t.printStackTrace();
				availableFormats = new ArrayList();
			}
		}
		return availableFormats;
	}


	/**
	 *  Determine if XML for this item can be dissiminated in the requested format specified using the {@link
	 *  #setRequestedXmlFormat(String)}, as available through {@link edu.ucar.dls.xml.XMLConversionService}.
	 *
	 * @return    True if the requested XML format can be dissiminated
	 * @see       #setRequestedXmlFormat(String)
	 * @see       #getXmlFormat
	 * @see       #getRequestedXmlFormat
	 * @see       #getRequestedXml
	 */
	public boolean getCanDissiminateFormat() {
		List formats = getAvailableFormats();
		if (formats == null || requestedXmlFormat == null)
			return false;
		return formats.contains(requestedXmlFormat);
	}


	/**
	 *  Gets the content in OAI Dublin Core XML format, or empty String if not available. The output contains no
	 *  XML and DTD declaration.
	 *
	 * @return    The oaiDublinCoreXml or empty String
	 */
	public String getOaiDublinCoreXml() {
		if (oaiDublinCoreXml == null)
			oaiDublinCoreXml = getXmlFormat("oai_dc", true);
		return oaiDublinCoreXml;
	}


	/**
	 *  Gets the content in NSDL Dublin Core XML format, or empty String if not available. The output contains no
	 *  XML and DTD declaration.
	 *
	 * @return    The nsdlDublinCoreXml or empty String
	 */
	public String getNsdlDublinCoreXml() {
		if (nsdlDublinCoreXml == null)
			nsdlDublinCoreXml = getXmlFormat("nsdl_dc", true);
		return nsdlDublinCoreXml;
	}


	/**
	 *  Gets the OAI datestamp for the given record in ISO8601 UTC format yyyy-MM-ddTHH:mm:ssZ. The OAI datestamp
	 *  corresponds to the time the content last changed in the index plus 180 seconds.
	 *
	 * @return    The OAI datestamp in ISO8601 UTC format.
	 */
	public String getOaiDatestamp() {
		String t = doc.get("oaimodtime");

		if (t == null)
			return DEFAULT;

		long modTime = -1;
		try {
			modTime = DateFieldTools.stringToTime(t);
		} catch (java.text.ParseException pe) {
			prtlnErr("Error in getOaiDatestamp(): " + pe);
		}
		return OAIUtils.getDatestampFromDate(new Date(modTime));
	}


	/**
	 *  Gets a String representataion of the oai datestamp in human readable format.
	 *
	 * @return    The OAI datestamp in human readable format.
	 * @see       #getOaiDatestamp
	 */
	public String getOaiLastModifiedString() {
		String t = doc.get("oaimodtime");

		if (t == null)
			return DEFAULT;

		long modTime = -1;
		try {
			modTime = DateFieldTools.stringToTime(t);
		} catch (java.text.ParseException pe) {
			prtlnErr("Error in getOaiLastModifiedString(): " + pe);
		}
		return new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date(modTime));
	}


	/**
	 *  Gets the time the record content was last modified in the index. Specifically, the time the XML record
	 *  first appeared in the index or was changed since the previous version as determined by String.equals()
	 *  method.
	 *
	 * @return    The time the content was last modified.
	 */
	public long getContentLastModifiedTime() {
		String t = doc.get("oaimodtime");

		long modTime = -1;
		try {
			modTime = DateFieldTools.stringToTime(t) - 18000;
		} catch (java.text.ParseException pe) {
			prtlnErr("Error in getContentLastModifiedTime(): " + pe);
		}
		return modTime;
	}


	/**
	 *  Gets the Date the record content was last modified in the index. Specifically, the Date the XML record
	 *  first appeared in the index or was changed since the previous version as determined by String.equals()
	 *  method.
	 *
	 * @return    The Date the content was last modified.
	 */
	public Date getContentLastModifiedDate() {
		return new Date(getContentLastModifiedTime());
	}


	/**
	 *  Gets the datestamp the record content was last modified in the index in ISO8601 UTC format
	 *  yyyy-MM-ddTHH:mm:ssZ. Specifically, the datestamp the XML record first appeared in the index or was
	 *  changed since the previous version as determined by String.equals() method.
	 *
	 * @return    The ISO8601 UTC datestamp the content was last modified.
	 */
	public String getContentLastModifiedDatestamp() {
		return OAIUtils.getDatestampFromDate(getContentLastModifiedDate());
	}


	/**
	 *  Gets the validationReport for this document, or null if no validationReport was found.
	 *
	 * @return    The validationReport value.
	 * @see       #isValid()
	 */
	public String getValidationReport() {
		String t = doc.get("validationreport");

		if (t == null)
			return null;
		else
			return t;
	}


	/**
	 *  Gets the path to the source file of the document used to create this index record.
	 *
	 * @return    The document source file path
	 */
	public String getDocsource() {
		String collBaseDir =
				(String) getPrimaryIndex().getAttribute("collBaseDir");
		if (collBaseDir == null) {
			String t = doc.get("docsource");

			if (t == null)
				return DEFAULT;
			else
				return t;
		}
		else {

			return collBaseDir + "/" + getDoctype() + "/" + getSets()[0] + "/" + getFileName();
		}
	}


	/**
	 *  Determines whether the XML for this record is valid. To search for valididity, use field
	 *  valid:[true|false] (unstored). If the XML was not valid there will be a validation report available.
	 *
	 * @return    True if valid, else false.
	 * @see       #getValidationReport()
	 */
	public boolean isValid() {
		String t = doc.get("validationreport");

		if (t == null)
			return true;
		else
			return false;
	}


	/**
	 *  Gets the field ID from the field name, for example 'gradeRange' will return 'gr'.
	 *
	 * @param  fieldString     A vocab field, for example 'gradeRange'
	 * @param  metadataFormat  The metadata format, for example 'adn'
	 * @return                 The field ID, for example 'gr', or the original field name if a translation is not
	 *      available
	 */
	protected String getFieldId(String fieldString, String metadataFormat) {
		try {
			String fieldName;
			if (metadataVocab == null)
				fieldName = fieldString;
			else {
				//UPDATED-METADATA-UI-METHOD***
				//fieldName = metadataVocab.getFieldSystemId(fieldString);
				fieldName = metadataVocab.getTranslatedField(metadataFormat, fieldString);
			}

			//prtln("Field name " + fieldString + " encoded as id: " + fieldName);
			return fieldName;
		} catch (Throwable e) {
			prtlnErr("getFieldId(): " + e);
			return fieldString;
		}
	}


	/**
	 *  Gets the value ID from the field name and value name, for example 'key', 'dcc' will return '09'.
	 *
	 * @param  fieldName       The vocab field ID, for example 'key' or 'gradeRange'.
	 * @param  vocabName       The vocab value name, for example 'dcc' or 'DLESE:High school'.
	 * @param  metadataFormat  The metadata format, for example 'adn'
	 * @return                 The value ID, for example '09' or '02', or the original value name if a
	 *      translation is not available
	 */
	protected String getValueId(String fieldName, String vocabName, String metadataFormat) {
		try {
			String val;
			if (metadataVocab == null)
				val = vocabName;
			else {
				//UPDATED-METADATA-UI-METHOD***
				//val = metadataVocab.getFieldValueSystemId(fieldName, vocabName);
				val = metadataVocab.getTranslatedValue(metadataFormat, fieldName, vocabName);
			}

			return val;
		} catch (Throwable e) {
			prtlnErr("getValueId(): " + e);
			return vocabName;
		}
	}


	/**
	 *  Gets the metadataVocab manager, or null if one is not available
	 *
	 * @return    The metadataVocab manager
	 */
	public MetadataVocab getMetadataVocab() {
		return metadataVocab;
	}


	/**
	 *  Gets the metadataVocabLanguage attribute of the XMLDocReader object
	 *
	 * @return    The metadataVocabLanguage value
	 */
	public String getMetadataVocabLanguage() {
		return vocabLang;
	}


	/**
	 *  Sets the metadataVocabLanguage attribute of the XMLDocReader object
	 *
	 * @param  language  The new metadataVocabLanguage value
	 */
	public void setMetadataVocabLanguage(String language) {
		vocabLang = language;
	}


	/**
	 *  Gets the metadataVocabAudience attribute of the XMLDocReader object
	 *
	 * @return    The metadataVocabAudience value
	 */
	public String getMetadataVocabAudience() {
		return vocabAudience;
	}


	/**
	 *  Sets the metadataVocabAudience attribute of the XMLDocReader object
	 *
	 * @param  audience  The new metadataVocabAudience value
	 */
	public void setMetadataVocabAudience(String audience) {
		vocabAudience = audience;
	}


	/**
	 *  Gets the vocab interface currently being used to translate vocab field/value IDs into user interface
	 *  labels, for example 'dds.descr.en-us'.
	 *
	 * @param  fieldId         NOT YET DOCUMENTED
	 * @param  valueId         NOT YET DOCUMENTED
	 * @param  metadataFormat  NOT YET DOCUMENTED
	 * @return                 The vocabInterface
	 */
	/* public String getVocabInterface() {
		return vocabInterface;
	} */
	/**
	 *  Sets the vocabulary interface to use for translating vocab field/value IDs into user interface labels. If
	 *  not specified, defaults to 'dds.descr.en-us'. This effects subsequent calls to {@link
	 *  #getUiLabelsFromVocabIds(String fieldId, String[] valueIds)}.
	 *
	 * @param  fieldId         NOT YET DOCUMENTED
	 * @param  valueId         NOT YET DOCUMENTED
	 * @param  metadataFormat  NOT YET DOCUMENTED
	 * @return                 The uiLabelFromVocabId value
	 */
	/* public void setVocabInterface(String vi) {
		vocabInterface = vi;
	} */
	/**
	 *  Gets the UI label corresponding to the given vocab value IDs and field ID. To specify the vocab interface
	 *  to use, first call {@link #setVocabInterface(String)}, otherwise the default will be used, which is
	 *  'dds.descr.en-us'. Note that if the vocab manager can not translate the given IDs then a comment string
	 *  &lt;!-- MUI... will be returned.
	 *
	 * @param  fieldId         The vocab field ID, for example 'gr'.
	 * @param  valueId         The vocab value IDs, for example '07'.
	 * @param  metadataFormat  The metadata format, for example 'adn'
	 * @return                 The UI lables for the given IDs, for example 'Primary (K-2)'
	 */
	protected String getUiLabelFromVocabId(String fieldId, String valueId, String metadataFormat) {
		//UPDATED-METADATA-UI-METHOD***
		//return metadataVocab.getUiLabelOfSystemIds(vocabInterface, fieldId, valueId);
		return metadataVocab.getUiValueLabel(metadataFormat, vocabAudience, vocabLang, fieldId, valueId, vocabAbbreviated);
	}


	/**
	 *  Gets the UI label corresponding to the given vocab value name from the XML and field name. To specify the
	 *  vocab interface to use, first call {@link #setVocabInterface(String)}, otherwise the default will be
	 *  used, which is 'dds.descr.en-us'. Note that if the vocab manager can not translate the given name then a
	 *  comment string &lt;!-- MUI... will be returned.
	 *
	 * @param  fieldName       The vocab field ID, for example 'key' or 'gradeRange'.
	 * @param  vocabName       The vocab value name, for example 'dcc' or 'DLESE:High school'.
	 * @param  metadataFormat  The metadata format, for example 'adn'
	 * @return                 The UI lables for the given IDs, for example 'DLESE Community Collection (DCC)' or
	 *      'High (9-12)'.
	 */
	protected String getUiLabelFromVocabName(String fieldName, String vocabName, String metadataFormat) {
		//UPDATED-METADATA-UI-METHOD***
		//return metadataVocab.getUiLabelOf(vocabInterface, fieldName, vocabName);
		return metadataVocab.getUiValueLabel(metadataFormat, vocabAudience, vocabLang, fieldName, vocabName, vocabAbbreviated);
	}


	/**
	 *  Gets the UI labels corresponding to the given vocab value IDs and field ID. To specify the vocab
	 *  interface to use, first call {@link #setVocabInterface(String)}, otherwise the default will be used,
	 *  which is 'dds.descr.en-us'. Note that if the vocab manager can not translate the given IDs then the
	 *  valueIds will be returned unchanged.
	 *
	 * @param  fieldId         The vocab field ID, for example 'gr'.
	 * @param  valueIds        The vocab value IDs, for example '07', '04', '05'.
	 * @param  metadataFormat  The metadata format, for example 'adn'
	 * @return                 The UI lables for the given IDs, for example 'Primary (K-2)', 'Intermediate
	 *      (3-5)', 'Middle (6-8)'
	 */
	protected Collection getUiLabelsFromVocabIds(String fieldId, String[] valueIds, String metadataFormat) {
		return new VocabUiLabelsCollection(fieldId, valueIds, metadataFormat);
	}


	/**
	 *  A collection of user interface labels derived from vocabulary field/value IDs.
	 *
	 * @author    John Weatherley
	 */
	public final class VocabUiLabelsCollection extends AbstractCollection {
		int iter = 0;
		private String[] vids = null;
		private String fi = null;
		private String metadataFormat = null;


		/**
		 *  Constructor for the VocabUiLabelsCollection object
		 *
		 * @param  fieldId         A vocab field ID, for example 'gr'
		 * @param  valueIds        Vocab value IDs, for example 07, 04, 05
		 * @param  metadataFormat  The metadata format, for example 'adn'
		 */
		public VocabUiLabelsCollection(String fieldId, String[] valueIds, String metadataFormat) {
			vids = valueIds;
			fi = fieldId;
			this.metadataFormat = metadataFormat;
		}


		/**
		 *  The iterator over the UI labels
		 *
		 * @return    The iterator over the UI labels
		 */
		public Iterator iterator() {
			return new VocabUiLabelsIterator();
		}


		/**
		 *  The number of items in this collection
		 *
		 * @return    Number of items in this collection
		 */
		public int size() {
			if (vids == null)
				return 0;
			else
				return vids.length;
		}


		/**
		 *  The Iterator
		 *
		 * @author    John Weatherley
		 */
		public final class VocabUiLabelsIterator implements Iterator {

			/**
			 *  True if more items availble
			 *
			 * @return    True if more items availble
			 */
			public boolean hasNext() {
				if (vids == null)
					return false;
				else
					return (iter < vids.length);
			}


			/**
			 *  Gets the next UI label in the collection. If the UI label can not be determined using the vocab
			 *  manager, the vocab ID will be returned unchanged.
			 *
			 * @return                             The next UI lable in the collection
			 * @exception  NoSuchElementException  If no more items are available
			 */
			public Object next() throws NoSuchElementException {
				if (vids == null)
					throw new NoSuchElementException("The collection is null");
				if (iter >= vids.length)
					throw new NoSuchElementException("There are no more results in this collection");
				String vocabLabel = vids[iter];
				if (metadataVocab == null)
					return vocabLabel;
				try {
					//UPDATED-METADATA-UI-METHOD***
					//vocabLabel = metadataVocab.getUiLabelOfSystemIds(vi, fi, vids[iter]);
					vocabLabel = metadataVocab.getUiValueLabel(metadataFormat, vocabAudience, vocabLang, fi, vids[iter], vocabAbbreviated);
				} catch (Throwable t) {
					prtlnErr("Unable to translate metadata UI label for format: " + metadataFormat +
							" field: " + fi + " value: " + vids[iter] + ". Reason: " + t);
				}
				iter++;
				return vocabLabel;
			}


			/**
			 *  This method is not supported.
			 *
			 * @exception  UnsupportedOperationException  Thrown if this method is called.
			 */
			public void remove() throws UnsupportedOperationException {
				throw new UnsupportedOperationException("VocabUiLabelsCollection does not support this operation");
			}
		}
	}


	// ---------------- Assigned Relations by ID and URL from this item ---------------------------

	/**
	 *  Determines whether this item assigns one or more relationships by ID or URL.
	 *
	 * @return    True if asigned relations are present, false otherwise.
	 */
	public boolean getHasAssignedRelations() {
		String t = getMultiDoc().get("assignedRelationshipIsDefined");
		return (t != null && t.equals("true"));
	}


	/**
	 *  Gets the types of relationships that were asigned by this item by related ID, for example
	 *  'isAnnotatedBy'.
	 *
	 * @return    The types of assigned relationships, or empty array if none
	 */
	public String[] getAssignedRelationshipByIdTypes() {
		return getMultiDoc().getValues("assignedRelationshipsById");
	}


	/**
	 *  Gets the types of relationships that were asigned by this item by related URL, for example
	 *  'isAnnotatedBy'.
	 *
	 * @return    The types of assigned relationships, or empty array if none
	 */
	public String[] getAssignedRelationshipByUrlTypes() {
		return getMultiDoc().getValues("assignedRelationshipsByUrl");
	}


	/**
	 *  Gets the record IDs for the items that this record assignes the given relationship to.
	 *
	 * @param  relationType  The type of relationship assigned, for example 'isAnnotatedBy'
	 * @return               The IDs of the related records
	 */
	public String[] getAssignedRelatedIdsOfType(String relationType) {
		return getMultiDoc().getValues("assignsRelationshipById." + relationType);
	}


	/**
	 *  Gets the URLs for the items that this record assignes the given relationship to.
	 *
	 * @param  relationType  The type of relationship assigned, for example 'isAnnotatedBy'
	 * @return               The URLs of the related items
	 */
	public String[] getAssignedRelatedUrlsOfType(String relationType) {
		return getMultiDoc().getValues("assignsRelationshipByUrl." + relationType);
	}


	/**
	 *  Gets a Map of records that this item assignes a relationship to by ID, keyed by relationship type, for
	 *  example 'isAnnotatedBy'.
	 *
	 * @return    A Map of record ResultDoc arrays keyed by relationship type
	 */
	public synchronized Map getAssignedByIdRelatedRecordsMap() {
		if (_assignedByIdRelatedRecordsMap == null) {
			_assignedByIdRelatedRecordsMap = new TreeMap();
			String[] relationshipTypes = getAssignedRelationshipByIdTypes();

			prtln("getAssignedByIdRelatedRecordsMap() relationshipTypes: " + (relationshipTypes == null ? "null" : Arrays.toString(relationshipTypes)));
			for (int i = 0; i < relationshipTypes.length; i++) {
				String[] ids = getAssignedRelatedIdsOfType(relationshipTypes[i]);

				prtln("getAssignedByIdRelatedRecordsMap() relationshipType: " + relationshipTypes[i] + " ids: " + (ids == null ? "null" : Arrays.toString(ids)));

				BooleanQuery idQ = new BooleanQuery();
				for (int j = 0; j < ids.length; j++) {
					idQ.add(new TermQuery(new Term("idvalue", ids[j])), BooleanClause.Occur.SHOULD);
				}
				ResultDocList relatedRecords = getPrimaryIndex().searchDocs(idQ);

				prtln("getAssignedByIdRelatedRecordsMap() idQ: " + idQ + " num results: " + relatedRecords.size());
				_assignedByIdRelatedRecordsMap.put(relationshipTypes[i], relatedRecords);
			}
		}
		return _assignedByIdRelatedRecordsMap;
	}


	/**
	 *  Gets a Map of record ResultDoc arrays that this item assignes a relationship to by URL, keyed by
	 *  relationship type, for example 'isAnnotatedBy'.
	 *
	 * @return    A Map of record ResultDoc arrays keyed by relationship type
	 */
	public synchronized Map getAssignedByUrlRelatedRecordsMap() {
		if (_assignedByUrlRelatedRecordsMap == null) {
			_assignedByUrlRelatedRecordsMap = new TreeMap();
			String[] relationshipTypes = getAssignedRelationshipByUrlTypes();
			for (int i = 0; i < relationshipTypes.length; i++) {
				String[] ids = getAssignedRelatedUrlsOfType(relationshipTypes[i]);

				BooleanQuery idQ = new BooleanQuery();
				for (int j = 0; j < ids.length; j++) {
					idQ.add(new TermQuery(new Term("url", ids[j])), BooleanClause.Occur.SHOULD);
				}
				// Do not include relations to self...
				idQ.add(new TermQuery(new Term("idvalue", getId())), BooleanClause.Occur.MUST_NOT);
				ResultDocList relatedRecords = getPrimaryIndex().searchDocs(idQ);
				_assignedByUrlRelatedRecordsMap.put(relationshipTypes[i], relatedRecords);
			}
		}
		return _assignedByUrlRelatedRecordsMap;
	}


	/**
	 *  Gets a Map of record IDs and the corresponding relationships they are assigned. The key is the record ID,
	 *  the value is a list of relationships assigned for that ID (assigned by ID or URL).
	 *
	 * @return    A Map of type
	 */
	public synchronized Map getAssignedRelationshipsForItemsMap() {
		if (_assignedRelationshipsForItems == null) {
			_assignedRelationshipsForItems = new TreeMap();
			_assignedRelationshipsForItems = populateAssignedRelationshipsForItemsMap(_assignedRelationshipsForItems, getAssignedByIdRelatedRecordsMap());
			_assignedRelationshipsForItems = populateAssignedRelationshipsForItemsMap(_assignedRelationshipsForItems, getAssignedByUrlRelatedRecordsMap());
		}
		return _assignedRelationshipsForItems;
	}


	/**
	 *  Populates a Map of record IDs and the corresponding relationships they are assigned. The key is the
	 *  record ID, the value is a list of relationships assigned for that ID (assigned by ID or URL).
	 *
	 * @param  assignedRelationshipsForItems  Description of the Parameter
	 * @param  assignedRelatedRecordsMap      Description of the Parameter
	 * @return                                A Map
	 */
	private Map populateAssignedRelationshipsForItemsMap(Map assignedRelationshipsForItems, Map assignedRelatedRecordsMap) {
		Iterator it = assignedRelatedRecordsMap.keySet().iterator();
		while (it.hasNext()) {
			String relation = (String) it.next();
			ResultDocList relatedRecords = (ResultDocList) assignedRelatedRecordsMap.get(relation);
			for (int i = 0; i < relatedRecords.size(); i++) {
				XMLDocReader xmlDocReader = ((XMLDocReader) relatedRecords.get(i).getDocReader());

				String id = xmlDocReader.getId();

				List assignedRelationships = (List) assignedRelationshipsForItems.get(id);
				if (assignedRelationships == null)
					assignedRelationships = new ArrayList();

				if (!assignedRelationships.contains(relation))
					assignedRelationships.add(relation);
				assignedRelationshipsForItems.put(id, assignedRelationships);
			}
		}
		return assignedRelationshipsForItems;
	}


	/**
	 *  Gets a List of all IDs for records that this item assignes relationships for by ID or URL.
	 *
	 * @return    A List of ID Strings.
	 */
	public synchronized List getIdsOfRecordsWithAssignedRelationships() {
		if (_idsOfRecordsWithAssignedRelationships == null) {
			_idsOfRecordsWithAssignedRelationships = new ArrayList();
			Map idRlationsMap = getAssignedRelationshipsForItemsMap();
			Iterator it = idRlationsMap.keySet().iterator();
			while (it.hasNext()) {
				String id = (String) it.next();
				if (!_idsOfRecordsWithAssignedRelationships.contains(id))
					_idsOfRecordsWithAssignedRelationships.add(id);
			}
		}
		return _idsOfRecordsWithAssignedRelationships;
	}


	// ---------------- [end] Assigned Relations by ID and URL from this item ---------------------------


	// ---------------- Relations for this item ---------------------------

	/**
	 *  Determines whether this item has one or more relations.
	 *
	 * @return    True if relations are present, false otherwise.
	 */
	public boolean getHasRelations() {
		String t = getMultiDoc().get("itemhasrelations");
		return (t != null && t.equals("true"));
	}


	/**
	 *  Gets the types of relationships that were indexed for this item, for example 'isAnnotatedBy'.
	 *
	 * @return    The types of relationships, or empty array if none
	 */
	public String[] getRelationshipTypes() {
		return getMultiDoc().getValues("indexedRelations");
	}


	/**
	 *  Gets the record IDs for the documents with the given relationship to this item, for example
	 *  'isAnnotatedBy'.
	 *
	 * @param  relationType  The type of relationship, for example 'isAnnotatedBy'
	 * @return               The IDs of the related records
	 */
	public String[] getRelatedIdsOfType(String relationType) {
		return getMultiDoc().getValues("indexedRelationIds." + relationType);
	}


	/**
	 *  Gets the IDs of all records that have a relationship of any type to this record.
	 *
	 * @return    The IDs of all related records
	 */
	public String[] getRelatedIds() {
		if (_relatedIds == null) {
			List idList = new ArrayList();
			String[] relationshipTypes = getRelationshipTypes();
			for (int i = 0; i < relationshipTypes.length; i++) {
				String[] ids = getRelatedIdsOfType(relationshipTypes[i]);
				for (int j = 0; j < ids.length; j++)
					if (!idList.contains(ids[j]))
						idList.add(ids[j]);
			}
			Collections.sort(idList);
			_relatedIds = (String[]) idList.toArray(new String[]{});
		}
		return _relatedIds;
	}


	/**
	 *  Gets a Map of records that are related to this item, keyed by relationship type, for example
	 *  'isAnnotatedBy'.
	 *
	 * @return    A Map of record ResultDoc arrays keyed by relationship type
	 */
	public Map getRelatedRecordsMap() {
		if (_relatedRecordsMap == null) {
			_relatedRecordsMap = new TreeMap();
			String[] relationshipTypes = getRelationshipTypes();
			for (int i = 0; i < relationshipTypes.length; i++) {
				String[] ids = getRelatedIdsOfType(relationshipTypes[i]);

				BooleanQuery idQ = new BooleanQuery();
				for (int j = 0; j < ids.length; j++) {
					idQ.add(new TermQuery(new Term("idvalue", ids[j])), BooleanClause.Occur.SHOULD);
				}
				ResultDocList relatedRecords = getPrimaryIndex().searchDocs(idQ);
				_relatedRecordsMap.put(relationshipTypes[i], relatedRecords);
			}
		}
		return _relatedRecordsMap;
	}


	// ---------------- [end] Relations for this item ---------------------------


	// ---------------- Annotations display, etc. --------------------------

	// Dev note 5/8/2009: Below here was brought in from ItemDocReader - can refactor from there (note multidoc)

	//private final String DEFAULT = "(null)";
	//private ResultDocList associatedItemResultDocs = null;
	//private ResultDocList displayableAssociatedItemResultDocs = null;
	//private ResultDocList allItemResultDocs = null;
	private ResultDocList annotationResultDocs = null;
	private ResultDocList deDupedResultDocs = null;
	//private boolean associatedItemsInitialized = false;
	//private boolean displayableAssociatedItemsInitialized = false;
	//private boolean allItemsInitialized = false;
	private boolean annotationDocReadersInitialized = false;
	//private ArrayList missingAssociatedItemIds = null;
	private boolean isMultirecord = false;
	private Document multiDoc = null;
	//private RepositoryManager rm = null;
	private HashMap completedAnnosByType = null;
	private ArrayList completedAnnos = null;
	private HashMap inProgressAnnosByFormat = null;
	private int numCompletedAnnos = 0;
	private int numInProgressAnnos = 0;
	private String[] relatedResourceIds = null;
	private String[] relatedResourceUrls = null;
	private String[] _allIds = null;


	/**
	 *  Determines whether this item has annotations.
	 *
	 * @return    True if annotations are present, false otherwise.
	 */
	public boolean hasAnnotations() {
		String t = doc.get("itemannotypes");
		return (t != null && t.trim().length() != 0);
	}


	/**
	 *  Gets all the record IDs associated with this resource, including this record's ID.
	 *
	 * @return    All record IDs for this resource
	 */
	public String[] getAllIds() {
		if (_allIds == null)
			_allIds = getMultiDoc().getValues("idvalue");
		return _allIds;
	}


	private String[] _associatedIds = null;


	/**
	 *  Gets the IDs of records that refer to the same resource, not including this record's ID. Overriden by
	 *  ItemDocReader.
	 *
	 * @return    The associated IDs value.
	 */
	public String[] getAssociatedIds() {
		if (_associatedIds == null) {
			String[] allIds = getAllIds();
			List assocIds = new ArrayList(allIds.length);
			for (int i = 0; i < allIds.length; i++)
				if (!allIds[i].equals(getId()))
					assocIds.add(allIds[i]);
			_associatedIds = (String[]) assocIds.toArray(new String[]{});
		}
		return _associatedIds;
	}


	/**
	 *  Gets the IDs for associated records that are not in the index. NOT IMPLEMENTED, returns empty list.
	 *  Overriden by ItemDocReader.
	 *
	 * @return    A list of missingAssociatedItemIds Strings
	 */
	public ArrayList getMissingAssociatedItemIds() {
		return new ArrayList(0);
	}


	/**
	 *  Gets the ResultDocs for all annotations that refer to this resource.
	 *
	 * @return    The ResultDocs value
	 */
	public ResultDocList getAnnotationResultDocs() {
		//prtln("getAnnotationResultDocs()");
		try {
			if (annotationDocReadersInitialized) {
				return annotationResultDocs;
			}
			annotationDocReadersInitialized = true;

			if (recordDataService == null || !hasAnnotations()) {
				return null;
			}

			String[] ids = getAllIds();
			ArrayList allIds = new ArrayList();
			for (int i = 0; i < ids.length; i++) {
				allIds.add(ids[i]);
			}

			ids = getAssociatedIds();
			if (ids != null) {
				for (int i = 0; i < ids.length; i++) {
					allIds.add(ids[i]);
				}
			}

			//prtln("getAnnotationResultDocs() index query");
			annotationResultDocs = recordDataService.getDleseAnnoResultDocs((String[]) allIds.toArray(new String[]{}));
			return annotationResultDocs;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 *  Determines whether this item has at least one completed annotation.
	 *
	 * @return    True if this item has one or more completed annotation, false otherwise.
	 */
	public boolean hasCompletedAnno() {
		initAnnosByType();
		return !completedAnnos.isEmpty();
	}


	/**
	 *  Gets the hasCompletedAnno attribute of the ItemDocReader object
	 *
	 * @return    The hasCompletedAnno value
	 */
	public String getHasCompletedAnno() {
		if (hasCompletedAnno()) {
			return "true";
		}
		else {
			return "false";
		}
	}


	/**
	 *  Gets the numCompletedAnnos attribute of the ItemDocReader object
	 *
	 * @return    The numCompletedAnnos value
	 */
	public String getNumCompletedAnnos() {
		initAnnosByType();
		return Integer.toString(numCompletedAnnos);
	}


	/**
	 *  Gets the numInProgressAnnos attribute of the ItemDocReader object
	 *
	 * @return    The numInProgressAnnos value
	 */
	public String getNumInProgressAnnos() {
		initAnnosByType();
		return Integer.toString(numInProgressAnnos);
	}


	/**
	 *  Gets the numTextAnnosInProgress attribute of the ItemDocReader object
	 *
	 * @return    The numTextAnnosInProgress value
	 */
	public String getNumTextAnnosInProgress() {
		initAnnosByType();
		return Integer.toString(numTextInProgress);
	}


	/**
	 *  Gets the numAudioAnnosInProgress attribute of the ItemDocReader object
	 *
	 * @return    The numAudioAnnosInProgress value
	 */
	public String getNumAudioAnnosInProgress() {
		initAnnosByType();
		return Integer.toString(numAudioInProgress);
	}


	/**
	 *  Gets the numGraphicalAnnosInProgress attribute of the ItemDocReader object
	 *
	 * @return    The numGraphicalAnnosInProgress value
	 */
	public String getNumGraphicalAnnosInProgress() {
		initAnnosByType();
		return Integer.toString(numGraphicalInProgress);
	}


	/**
	 *  Gets the number of video format annotations in progress for this item.
	 *
	 * @return    The number of video format annotations
	 */
	public String getNumVideoAnnosInProgress() {
		initAnnosByType();
		return Integer.toString(numVideoInProgress);
	}


	/**
	 *  Determines whether this item has a completed annotataion of the given type, for example 'Review',
	 *  'Comment', 'Educational standard', etc.
	 *
	 * @param  type  The annotation type
	 * @return       True if this item has a completed annotataion of the given type.
	 */
	public boolean hasCompletedAnnoOfType(String type) {
		initAnnosByType();
		return completedAnnosByType.containsKey(type);
	}


	/**
	 *  Gets a list of all completed annotataions for this item of the given type.
	 *
	 * @param  type  The annotation type, for example 'Review', 'Teaching tip', etc.
	 * @return       A list of {@link DleseAnnoDocReader}s for all completed annotataions for this item of the
	 *      given type, or an empty list.
	 */
	public ArrayList getCompletedAnnosOfType(String type) {
		initAnnosByType();
		ArrayList list = (ArrayList) completedAnnosByType.get(type);
		if (list == null) {
			list = new ArrayList();
		}
		return list;
	}


	/**
	 *  Determines whether the item has an annotation in progress.
	 *
	 * @return    'true' if the item has an annotation in progress, otherwise 'false'.
	 */
	public String getHasInProgressAnno() {
		if (hasInProgressAnno()) {
			return "true";
		}
		else {
			return "false";
		}
	}


	/**
	 *  Determines whether the item has an annotation in progress.
	 *
	 * @return    True if the item has an annotation in progress, otherwise false.
	 */
	public boolean hasInProgressAnno() {
		initAnnosByType();
		return !inProgressAnnosByFormat.isEmpty();
	}


	/**
	 *  Determines whether the item has an annotation in progress of the given format, which is one of 'text',
	 *  'audio', 'graphical', or 'video'.
	 *
	 * @param  format  Annotation format
	 * @return         True if the item has an annotation in progress of the given format, otherwise false.
	 */
	public boolean hasInProgressAnnoOfFormat(String format) {
		initAnnosByType();
		return inProgressAnnosByFormat.containsKey(format);
	}


	/**
	 *  Gets all in-progress annotations for this item that have the given format, which is one of 'text',
	 *  'audio', 'graphical', or 'video'.
	 *
	 * @param  format  Annotation format
	 * @return         A list of {@link DleseAnnoDocReader}s
	 */
	public ArrayList getInProgressAnnosOfFormat(String format) {
		initAnnosByType();
		ArrayList list = (ArrayList) inProgressAnnosByFormat.get(format);
		if (list == null)
			list = new ArrayList();
		return list;
	}


	private int numAudioInProgress = 0;
	private int numGraphicalInProgress = 0;
	private int numTextInProgress = 0;
	private int numVideoInProgress = 0;


	private void initAnnosByType() {
		// If already initialized, return
		if (completedAnnosByType != null)
			return;

		completedAnnosByType = new HashMap();
		inProgressAnnosByFormat = new HashMap();
		completedAnnos = new ArrayList();
		ResultDocList annoResults = getAnnotationResultDocs();
		if (annoResults == null)
			return;
		ArrayList annoList = null;
		boolean isCompleted;
		boolean isInProgress;
		for (int i = 0; i < annoResults.size(); i++) {
			DleseAnnoDocReader anno = (DleseAnnoDocReader) annoResults.get(i).getDocReader();
			String annoType = anno.getType();
			String annoStatus = anno.getStatus();
			String annoFormat = anno.getFormat();
			isCompleted = anno.getIsCompleted();
			isInProgress = anno.getIsInProgress();

			if (isCompleted) {
				numCompletedAnnos++;
				annoList = (ArrayList) completedAnnosByType.get(annoType);
				completedAnnos.add(anno);
			}
			else if (isInProgress) {
				if (annoFormat.equals("text"))
					numTextInProgress++;
				else if (annoFormat.equals("audio"))
					numAudioInProgress++;
				else if (annoFormat.equals("graphical"))
					numGraphicalInProgress++;
				else if (annoFormat.equals("video"))
					numVideoInProgress++;
				numInProgressAnnos++;
				annoList = (ArrayList) inProgressAnnosByFormat.get(annoFormat);
			}

			if (annoList == null)
				annoList = new ArrayList();

			annoList.add(anno);
			if (isCompleted)
				completedAnnosByType.put(annoType, annoList);
			else if (isInProgress)
				inProgressAnnosByFormat.put(annoFormat, annoList);
		}
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the in-progress type 'text' annotations for
	 *  this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getTextAnnosInProgress() {
		return getInProgressAnnosOfFormat("text");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the in-progress type 'audio' annotations
	 *  for this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s
	 */
	public ArrayList getAudioAnnosInProgress() {
		return getInProgressAnnosOfFormat("audio");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the in-progress type 'graphical'
	 *  annotations for this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getGraphicalAnnosInProgress() {
		return getInProgressAnnosOfFormat("graphical");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the in-progress type 'video' annotations
	 *  for this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getVideoAnnosInProgress() {
		return getInProgressAnnosOfFormat("video");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the completed annotations, regardless of
	 *  type, for this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getCompletedAnnos() {
		initAnnosByType();
		return completedAnnos;
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the completed type 'review' annotations for
	 *  this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getCompletedReviews() {
		return getCompletedAnnosOfType("Review");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the completed type 'teaching tip'
	 *  annotations for this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getCompletedTeachingTips() {
		return getCompletedAnnosOfType("Teaching tip");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the completed type 'editors summary'
	 *  annotations for this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getCompletedEditorSummaries() {
		return getCompletedAnnosOfType("Editor's summary");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the completed type 'challenging situation'
	 *  annotations for this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getCompletedChallengingSituations() {
		return getCompletedAnnosOfType("Information on challenging teaching and learning situations");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the completed type 'average scores'
	 *  annotations for this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getCompletedAverageScores() {
		return getCompletedAnnosOfType("Average scores of aggregated indices");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the completed type 'advice' annotations for
	 *  this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getCompletedAdvice() {
		return getCompletedAnnosOfType("Advice");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the completed type 'annotation' annotations
	 *  for this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getCompletedAnnotation() {
		return getCompletedAnnosOfType("Annotation");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the completed type 'bias' annotations for
	 *  this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getCompletedBias() {
		return getCompletedAnnosOfType("Bias");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the completed type 'change' annotations for
	 *  this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getCompletedChange() {
		return getCompletedAnnosOfType("Change");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the completed type 'comment' annotations
	 *  for this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getCompletedComment() {
		return getCompletedAnnosOfType("Comment");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the completed type 'educational standard'
	 *  annotations for this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getCompletedEducationalStandard() {
		return getCompletedAnnosOfType("Educational standard");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the completed type 'example' annotations
	 *  for this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getCompletedExample() {
		return getCompletedAnnosOfType("Example");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the completed type 'explanation'
	 *  annotations for this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getCompletedExplanation() {
		return getCompletedAnnosOfType("Explanation");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the completed type 'question' annotations
	 *  for this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getCompletedQuestion() {
		return getCompletedAnnosOfType("Question");
	}


	/**
	 *  Gets a list of {@link DleseAnnoDocReader}s containing each of the completed type 'see also' annotations
	 *  for this resource.
	 *
	 * @return    A list of {@link DleseAnnoDocReader}s or empty list
	 */
	public ArrayList getCompletedSeeAlso() {
		return getCompletedAnnosOfType("See also");
	}


	/**
	 *  Gets the annotataion collection keys, for example {06, 08}, for all collections that annotate this item.
	 *
	 * @return    The annoCollectionKeys value
	 */
	public String[] getAnnoCollectionKeys() {
		String t = doc.get("itemannocollectionkeys");
		//System.out.println("getAnnoCollectionKeys() String: " + t );
		if (t == null || t.trim().length() == 0) {
			return null;
		}
		else {
			return t.split("\\+");
		}
	}


	/**
	 *  Gets the annotataion collection keys, for example {06, 08}, for all collections that annotate this item
	 *  with one or more status completed annotations.
	 *
	 * @return    The completedAnnoCollectionKeys value
	 */
	public String[] getCompletedAnnoCollectionKeys() {
		String t = doc.get("itemannocompletedcollectionkeys");
		//System.out.println("getAnnoCollectionKeys() String: " + t );
		if (t == null || t.trim().length() == 0) {
			return null;
		}
		else {
			return t.split("\\+");
		}
	}


	/**
	 *  Gets the anno types that are associated with this record.
	 *
	 * @return    The anno types value.
	 */
	public String[] getAnnoTypes() {
		String t = doc.get("itemannotypes");
		if (t == null || t.length() == 0) {
			return null;
		}
		else {
			String[] tmp = t.split("\\s+");
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = tmp[i].replaceAll("\\+", " ");
			}
			return tmp;
		}
	}


	/**
	 *  Gets the anno pathways that are associated with this record.
	 *
	 * @return    The list of anno pathway Strings
	 */
	public ArrayList getAnnoPathways() {
		String t = doc.get("itemannopathways");
		//System.out.println("getAnnoPathways() String: " + t );
		if (t == null || t.length() == 0) {
			return null;
		}
		else {
			String[] tmp = t.split("\\s+");
			ArrayList list = new ArrayList(tmp.length);
			for (int i = 0; i < tmp.length; i++)
				list.add(tmp[i].replaceAll("\\+", " "));
			return list;
		}
	}


	/**
	 *  Gets the annotation statuses that are associated with this item.
	 *
	 * @return    A list of anno status Strings
	 */
	public ArrayList getAnnoStatus() {
		String t = doc.get("itemannostatus");
		//System.out.println("getAnnoStatus() String: " + t );
		if (t == null || t.trim().length() == 0) {
			return null;
		}
		else {
			String[] tmp = t.split("\\s+");
			ArrayList list = new ArrayList(tmp.length);
			for (int i = 0; i < tmp.length; i++)
				list.add(tmp[i].replaceAll("\\+", " "));
			return list;
		}
	}


	/**
	 *  Gets the annotation formats that are associated with this item.
	 *
	 * @return    A list of anno formats Strings
	 */
	public ArrayList getAnnoFormats() {
		String t = doc.get("itemannoformats");
		if (t == null || t.trim().length() == 0) {
			return null;
		}
		else {
			String[] tmp = t.split("\\s+");
			ArrayList list = new ArrayList(tmp.length);
			for (int i = 0; i < tmp.length; i++)
				list.add(tmp[i].replaceAll("\\+", " "));
			return list;
		}
	}


	/**
	 *  Gets a String array of all annotation star ratings for this item in numerical form from 1 to 5, or null
	 *  if none. Each rating represents a single annotation's star rating for this item.
	 *
	 * @return    An String array of numbers 1, 2, 3, 4, 5, or null
	 */
	public String[] getAnnoRatings() {
		String t = doc.get("itemannoratingvalues");
		//System.out.println("getAnnoRatings() String: " + t );
		if (t == null || t.trim().length() == 0)
			return null;
		else
			return t.split("\\s+");
	}


	/**
	 *  Gets a the average of all star ratings for this item as a String, or null if none. The rating is shown to
	 *  three decimal points, for example '4.333' or '3.000'.
	 *
	 * @return    The average star rating to three decimal points as a String
	 */
	public String getAverageAnnoRating() {
		String t = doc.get("itemannoaveragerating");
		if (t == null || t.trim().length() == 0)
			return null;
		else
			return t;
	}


	/**
	 *  Gets a the average of all star ratings for this item as a float, or -1 if none.
	 *
	 * @return    The average star rating or -1 if none
	 */
	public float getAverageAnnoRatingFloat() {
		try {
			String t = getAverageAnnoRating();
			if (t != null)
				return Float.parseFloat(t);
		} catch (Throwable t) {
			prtlnErr("Error in getAverageAnnoRatingFloat(): " + t);
		}
		return -1;
	}


	/**
	 *  Gets a the total number of star ratings for this item as a String. The number is displayed to five
	 *  digits, for example '00002' or '00000'.
	 *
	 * @return    The number of star ratings as a String
	 */
	public String getNumAnnoRatings() {
		String t = doc.get("itemannonumratings");
		if (t == null || t.trim().length() == 0)
			return null;
		else
			return t;
	}


	/**
	 *  Gets a the total number of star ratings for this item as a int.
	 *
	 * @return    The number of star ratings for this item
	 */
	public int getNumAnnoRatingsInt() {
		try {
			String t = getNumAnnoRatings();
			if (t != null)
				return Integer.parseInt(t);
		} catch (Throwable t) {
			prtlnErr("Error in getNumAnnoRatingsInt(): " + t);
		}
		return 0;
	}


	// ---------------- [END] Annotations display, etc. --------------------------


	// ---------------- Multirecord deduping -----------------

	/**
	 *  Determine whether this item does or does not have multiple records associated with it.
	 *
	 * @return    True if there are multiple records, false if only one.
	 */
	public boolean getIsMultiRecord() {
		return isMultirecord;
	}


	/**
	 *  Gets the value 'true' or 'false' depending on whether this item does or does not have multiple records
	 *  associated with it.
	 *
	 * @return    'true' if there are multiple records, 'false' if only one.
	 */
	public String getMultiRecordStatus() {
		if (getIsMultiRecord())
			return "true";
		else
			return "false";
	}


	/**
	 *  Gets the de-duped ResultDocs for all records that refer to this resource including the primary from the
	 *  dup items index, or null if this is not a duped record. These are sorted if a sort was specified in the
	 *  ResultDoc attributes via key 'sortAscendingByField' or 'sortDescendingByField'. See also {@link
	 *  #getAllItemResultDocs}.
	 *
	 * @return    All de-duped ResultDocs for this resource.
	 * @see       #getAllItemResultDocs
	 */
	public ResultDocList getDeDupedResultDocs() {
		if (deDupedResultDocs == null)
			deDupedResultDocs = doGetDeDupedResultDocs(false, true);

		return deDupedResultDocs;
	}


	/**
	 *  Gets the de-duped ResultDocs for all records that refer to this resource, including this record, from the
	 *  dup items index, or null if this is not a duped record. See also {@link #getAllItemResultDocs}.
	 *
	 * @param  useFilter  True to use the filter, if one exists in the conf
	 * @param  useSort    True to use sorting, if indicated in the conf
	 * @return            All de-duped ResultDocs for this resource.
	 */
	private ResultDocList doGetDeDupedResultDocs(boolean useFilter, boolean useSort) {
		if (!getIsMultiRecord())
			return null;

		// Grab the IDs for this multi-record
		String[] ids = getAllIds();
		BooleanQuery idQ = new BooleanQuery();
		for (int j = 0; j < ids.length; j++)
			idQ.add(new TermQuery(new Term("idvalue", ids[j])), BooleanClause.Occur.SHOULD);

		org.apache.lucene.search.Filter filter = null;
		if (useFilter)
			filter = conf.filter;

		ResultDocList myDocs = null;
		Sort sort = null;

		String sortBy = "none";

		// If sorting has been requested, sort
		if (useSort && conf.attributes != null) {
			String sortAscendingByField = (String) conf.attributes.get("sortAscendingByField");
			String sortDescendingByField = (String) conf.attributes.get("sortDescendingByField");

			// Special case: since accessiondate is sorted ascending for it's multi-doc value, it must also be here regardless
			// E.g. the value in the multi-doc is the value sorted ascending
			if (sortDescendingByField != null && sortDescendingByField.equalsIgnoreCase("accessiondate")) {
				sortAscendingByField = sortDescendingByField;
				sortDescendingByField = null;
			}

			if (sortAscendingByField != null || sortDescendingByField != null) {
				if (sortAscendingByField != null) {
					sortBy = "sortAscendingByField:" + sortAscendingByField;
					//Collections.sort(myDocs, new LuceneFieldComparator(sortAscendingByField, LuceneFieldComparator.ASCENDING));
					sort = new Sort(new SortField(sortAscendingByField, SortField.STRING, false));
				}
				else {
					sortBy = "sortDescendingByField:" + sortDescendingByField;
					//Collections.sort(myDocs, new LuceneFieldComparator(sortDescendingByField, LuceneFieldComparator.DESCENDING));
					sort = new Sort(new SortField(sortDescendingByField, SortField.STRING, true));
				}
			}
		}

		myDocs = getDupItemsIndex().searchDocs(idQ, filter, sort, null, null);

		printQuery("\ndoGetDeDupedResultDocs() query: '" + idQ + "' filter: '" + filter + "' sortBy: '" + sortBy + "' had " + myDocs.size() + " results.\n");

		// If no filter was used, go ahead and set the deDupedDocs
		if (filter == null)
			deDupedResultDocs = myDocs;

		return myDocs;
	}


	/**  Initializes the de-duping items and sets the primary doc to the de-dupped doc if necessary */
	private void initDeDuping() {

		// Determine my multirecord status (true|false)
		String status = doc.get("multirecord");
		isMultirecord = (status != null && !status.equals("false"));

		// Set the multiDoc, which is either a normal doc or multiDoc as appropriate.
		multiDoc = doc;

		// If I'm a multirecord, replace my (multi)doc with the single doc that is a best match:
		if (isMultirecord) {
			SimpleLuceneIndex dupItemsIndex = getDupItemsIndex();

			// De-duping algorithm - choose the best record possible:
			if (dupItemsIndex != null) {
				ResultDocList deDupDocs = null;
				String q = null;

				// If sorting has been requested, use the first sorted record
				if (conf.attributes != null) {
					if (conf.attributes.get("sortAscendingByField") != null || conf.attributes.get("sortDescendingByField") != null) {
						deDupDocs = doGetDeDupedResultDocs(true, true);
						if (deDupDocs != null && deDupDocs.size() > 0) {
							//printQuery("De-dup is using sort query 0\n");

							setDoc(deDupDocs.get(0).getDocument());
							return;
						}
					}
				}

				// Grab the IDs for this multi-record
				String[] allIds = getAllIds();

				// Format idQuery using + syntax
				String idQuery = "+(id:" + SimpleLuceneIndex.encodeToTerm(allIds[0]);
				for (int i = 1; i < allIds.length; i++)
					idQuery += " id:" + SimpleLuceneIndex.encodeToTerm(allIds[i]);
				idQuery += ") ";

				// First query  ------------------------------------------------------

				// Since we're using the generated + syntax, we must use default OR query parser to search
				q = idQuery + removeMultiRecordQueryField(conf.query);
				QueryParser qp = dupItemsIndex.getQueryParser();
				qp.setDefaultOperator(QueryParser.Operator.OR);
				Query lq = null;

				try {
					lq = qp.parse(q);
				} catch (Exception pe) {
					prtlnErr("De-dup query 1 had a ParseException: " + pe);
				}

				deDupDocs = dupItemsIndex.searchDocs(lq, conf.filter);
				printQuery("\nDe-dup query 1: " + q + " had " + (deDupDocs == null ? 0 : deDupDocs.size()) + " results.\n");
				if (deDupDocs != null && deDupDocs.size() > 0) {
					//printQuery("De-dup is using query 1\n");
					setDoc(deDupDocs.get(0).getDocument());
					return;
				}

				// Grab the query fields that were entered
				String formattedUserQuery = null;
				String collectionsQuery = null;
				String discoverableItemsQuery = null;
				String reviewedBoostingQuery = null;
				if (conf.attributes != null) {
					formattedUserQuery = (String) conf.attributes.get("formattedUserQuery");
					formattedUserQuery = (formattedUserQuery != null) ? removeMultiRecordQueryField(formattedUserQuery) : formattedUserQuery;
					collectionsQuery = (String) conf.attributes.get("collectionsQuery");
					discoverableItemsQuery = (String) conf.attributes.get("discoverableItemsQuery");
					reviewedBoostingQuery = (String) conf.attributes.get("reviewedBoostingQuery");
				}

				// Second query ------------------------------------------------------

				// Re-format the idQuery to use AND syntax
				idQuery = "(id:" + SimpleLuceneIndex.encodeToTerm(allIds[0]);
				for (int i = 1; i < allIds.length; i++)
					idQuery += " OR id:" + SimpleLuceneIndex.encodeToTerm(allIds[i]);
				idQuery += ")";

				if (formattedUserQuery == null)
					formattedUserQuery = getNonFieldedQueryTerms(conf.query).trim();

				if (formattedUserQuery.length() > 0)
					q = "(" + formattedUserQuery + ") AND " + idQuery;
				else
					q = idQuery;

				q = (collectionsQuery != null) ? (q += " AND " + collectionsQuery) : q;
				q = (reviewedBoostingQuery != null) ? (q += " AND " + reviewedBoostingQuery) : q;
				q = (discoverableItemsQuery != null) ? (q += " AND " + discoverableItemsQuery) : q;

				deDupDocs = dupItemsIndex.searchDocs(q, conf.filter);
				//printQuery("\nDe-dup query 2: " + q + " had " + (deDupDocs == null ? 0 : deDupDocs.length) + " results.\n");
				if (deDupDocs != null && deDupDocs.size() > 0) {
					//printQuery("De-dup is using query 2\n");
					setDoc(deDupDocs.get(0).getDocument());
					return;
				}

				// Third query ------------------------------------------------------
				// search only by IDs, collection, review bossting and discoverable status...

				q = idQuery;
				q = (collectionsQuery != null) ? (q += " AND " + collectionsQuery) : q;
				q = (reviewedBoostingQuery != null) ? (q += " AND " + reviewedBoostingQuery) : q;
				q = (discoverableItemsQuery != null) ? (q += " AND " + discoverableItemsQuery) : q;

				deDupDocs = dupItemsIndex.searchDocs(q, conf.filter);

				//printQuery("\nDe-dup query 3: " + q + " had " + (deDupDocs == null ? 0 : deDupDocs.length) + " results.\n");
				if (deDupDocs != null && deDupDocs.size() > 0) {
					//printQuery("De-dup is using query 3\n");
					setDoc(deDupDocs.get(0).getDocument());
					return;
				}

				// Fourth query ------------------------------------------------------
				// Search only by IDs, with filter, return the first one

				deDupDocs = doGetDeDupedResultDocs(true, false);

				if (deDupDocs != null && deDupDocs.size() > 0) {
					//printQuery("De-dup is using query 4\n");
					setDoc(deDupDocs.get(0).getDocument());
					return;
				}

				// Fifth query ------------------------------------------------------
				// Search only by IDs, with no filter - return the first one - should always work

				deDupDocs = doGetDeDupedResultDocs(false, false);

				if (deDupDocs != null && deDupDocs.size() > 0) {
					//printQuery("De-dup is using query 5\n");
					setDoc(deDupDocs.get(0).getDocument());
					return;
				}
			}
		}
	}


	/**
	 *  Gets the multiDoc Lucene Document for this item, or the single doc, if none available. A multiDoc is a
	 *  Document that holds data from multiple XML records that catalog/reference the same resource. This method
	 *  is overridden by the sub classes that support it.
	 *
	 * @return    The multiDoc value
	 */
	protected Document getMultiDoc() {
		return multiDoc;
	}


	private final void printQuery(String s) {
		if (false)
			System.out.println(s);
	}


	private String removeMultiRecordQueryField(String query) {
		//printQuery("removeMultiRecordQueryField() query: '" + query + "'");
		String result = query.replaceAll("multirecord:true", "multirecord:false");
		//String result = query.replaceAll("\\(multirecord:true\\) AND |multirecord:true AND |\\(multirecord:true\\)", "");
		//printQuery("removeMultiRecordQueryField() removed query: '" + result + "'");
		return result;
	}


	private String getNonFieldedQueryTerms(String query) {
		query = query.replaceAll("\\(|\\)|\\\"", "");
		String[] terms = query.split("\\s+");
		if (terms == null) {
			return query;
		}
		StringBuffer newQuery = new StringBuffer();
		boolean prevIsTerm = false;
		for (int i = 0; i < terms.length; i++) {
			//prtln("term: " + terms[i]);

			if (terms[i].matches("[^:]*")) {
				//prtln("matching term: " + terms[i]);
				if (terms[i].matches("AND|OR")) {
					if (prevIsTerm) {
						newQuery.append(terms[i]);
						newQuery.append(" ");
						prevIsTerm = false;
					}
				}
				else {
					newQuery.append(terms[i]);
					newQuery.append(" ");
					//prtln("good term: " + terms[i]);
					prevIsTerm = true;
				}
			}
			else {
				prevIsTerm = false;
			}
		}

		String q = newQuery.toString();
		if (q.endsWith(" OR ")) {
			return q.substring(0, q.length() - 4);
		}
		if (q.endsWith(" AND ")) {
			return q.substring(0, q.length() - 5);
		}
		return q;
	}


	// ---------------- [end] Multirecord deduping -----------------


	// ------------ Debug / logging ---------------

	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	protected static void prtlnErr(String s) {
		if (debug)
			System.err.println(getDateStamp() + " XMLDocReader Error: " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	protected static void prtln(String s) {
		if (debug)
			System.out.println(getDateStamp() + " XMLDocReader: " + s);
	}

}


