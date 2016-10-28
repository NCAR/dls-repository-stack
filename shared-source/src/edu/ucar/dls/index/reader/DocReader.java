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
import edu.ucar.dls.index.*;
import edu.ucar.dls.repository.*;
import java.util.*;

/**
 *  An abstract bean for accessing the data stored in the Lucene {@link org.apache.lucene.document.Document}s
 *  that are returned from a search. All concrete DocReader classes must implement a default no-argument
 *  constructor so that they may be created automatically by the factory {@link
 *  edu.ucar.dls.index.ResultDoc#getDocReader()}. In addition, the index writer corresponding to the concrete
 *  reader must implement the {@link edu.ucar.dls.index.writer.DocWriter#getReaderClass()} method.
 *
 * @author    John Weatherley
 * @see       edu.ucar.dls.index.SimpleLuceneIndex
 * @see       edu.ucar.dls.index.ResultDoc
 */
public abstract class DocReader {
	/**  The Lucene Document that is being read. */
	protected Document doc = null;
	/**  The Lucene score assigned to the Document, or -1 if none available. */
	protected float score = -1;
	/**  The index that was searched over. */
	protected ResultDocConfig conf = null;
	/**  NOT YET DOCUMENTED */
	protected ResultDoc resultDoc = null;
	private RepositoryManager rm = null;


	// -------------- Abstract methods -------------------
	/**
	 *  Gets a String describing the reader type. This may be used in (Struts) beans to determine which type of
	 *  reader is available for a given search result and thus what data is available for display in the UI. The
	 *  reader type implies which getter methods are available.
	 *
	 * @return    The readerType value.
	 */
	public abstract String getReaderType();


	/**
	 *  An init method that may be used by concrete classes to initialize variables and resources as needed.
	 */
	public abstract void init();

	// -------------- Concrete methods -------------------

	/**
	 *  Called by {@link edu.ucar.dls.index.ResultDoc} at search time to initialize a new instance of a
	 *  DocReader.
	 *
	 * @param  myDoc      The Lucene Document
	 * @param  myScore    The Lucene score asigned to this hit
	 * @param  myConf     The config available to the doc reader
	 * @param  resultDoc  NOT YET DOCUMENTED
	 */
	public final void doInit(Document myDoc, ResultDoc resultDoc, float myScore, ResultDocConfig myConf) {
		conf = myConf;
		doc = myDoc;
		score = myScore;

		this.resultDoc = resultDoc;

		init();
	}


	/**
	 *  Gets a {@link edu.ucar.dls.index.reader.DocumentMap} of all field/values contained in the Lucene {@link
	 *  org.apache.lucene.document.Document}. The text values in each field is stored in the Map as Strings. For
	 *  example getDocMap.get("title") returns the text that was indexed and stored under the field name "title"
	 *  for this Document.<p>
	 *
	 *  Example that uses JSTL inside a JSP page (assumes result is an instance of ResultDoc):<p>
	 *
	 *  <code>
	 *  The title is: ${result.docMap["title"]}
	 *  </code>
	 *
	 * @return    A Map of all field/values contained in this Document, or null
	 * @see       edu.ucar.dls.index.reader.DocumentMap
	 */
	public DocumentMap getDocMap() {
		return resultDoc.getDocMap();
	}


	/**
	 *  Gets a {@link edu.ucar.dls.index.reader.LazyDocumentMap} of all field/values contained in the Lucene
	 *  {@link org.apache.lucene.document.Document}. The text values in each field are concatinated and stored in the Map as
	 *  Strings. For example getDocMap.get("title") returns the all that was indexed and stored under the field
	 *  name "title" for this Document.<p>
	 *
	 *  Example that uses JSTL inside a JSP page (assumes result is an instance of ResultDoc):<p>
	 *
	 *  <code>
	 *  The title is: ${result.docMap["title"]}
	 *  </code>
	 *
	 * @return    A Map of all field/values contained in this Document, or null
	 * @see       edu.ucar.dls.index.reader.DocumentMap
	 */
	public LazyDocumentMap getLazyDocMap() {
		return resultDoc.getLazyDocMap();
	}

	
	/**
	 *  Gets a {@link edu.ucar.dls.index.reader.LazyDocumentMapValuesArray} of all field/values contained in the
	 *  Lucene {@link org.apache.lucene.document.Document}. The text values in each field is stored in the Map as
	 *  Strings Arrays. For example getDocMap.get("title") returns and Array of values that were indexed and
	 *  stored under the field name "title" for this Document.<p>
	 *
	 *
	 * @return    A Map of all field/values String Arrays in this Document, or null
	 * @see       edu.ucar.dls.index.reader.DocumentMap
	 */
	public LazyDocumentMapValuesArray getLazyDocumentMapValuesArray() {
		return resultDoc.getLazyDocumentMapValuesArray();
	}	
	

	/**
	 *  Constructor that may be used programatically to wrap a reader around a Lucene {@link
	 *  org.apache.lucene.document.Document} that was created by a {@link edu.ucar.dls.index.writer.DocWriter}.
	 *
	 * @param  myDoc  Gets the Lucene Document that is being read by this DocReader.
	 * @see           edu.ucar.dls.index.writer.DocWriter
	 */
	protected DocReader(Document myDoc) {
		doc = myDoc;
	}


	/**
	 *  Sets the Lucene Document that is being read by this DocReader.
	 *
	 * @param  myDoc  The new doc value
	 */
	protected void setDoc(Document myDoc) {
		this.doc = myDoc;
	}


	/**  Constructor that initializes an empty DocReader. */
	protected DocReader() { }


	/**
	 *  Gets the Lucene score for the {@link org.apache.lucene.document.Document}, or -1 if none available.
	 *
	 * @return    The score or -1 if not available.
	 */
	public String getScore() {
		return Float.toString(score);
	}


	/**
	 *  Gets the Lucene {@link org.apache.lucene.document.Document} associated with this DocReader.
	 *
	 * @return    A Lucene {@link org.apache.lucene.document.Document}.
	 */
	public Document getDocument() {
		return doc;
	}


	/**
	 *  Gets the index that was searched over to produce the resulting hit, which may be the primary index or the
	 *  duplicate items index.
	 *
	 * @return    The index used for this search.
	 */
	public SimpleLuceneIndex getIndex() {
		return conf.index;
	}


	/**
	 *  Gets the primary index.
	 *
	 * @return    The primary index.
	 */
	public SimpleLuceneIndex getPrimaryIndex() {
		return getRepositoryManager().getIndex();
	}


	/**
	 *  Gets the duplicate items index. May be null depending on the webapp.
	 *
	 * @return    dupItemsIndex The dupItemsIndex value, or null;
	 */
	public SimpleLuceneIndex getDupItemsIndex() {
		return getRepositoryManager().getDupItemsIndex();
	}


	/**
	 *  Gets an attribute that has been previously set using {@link edu.ucar.dls.index.SimpleLuceneIndex#setAttribute(String,Object)}.
	 *
	 * @param  key  The key for the attribute
	 * @return      The attruibute, or null if none exists under the given key
	 * @see         edu.ucar.dls.index.SimpleLuceneIndex#setAttribute(String,Object)
	 */
	public Object getAttribute(String key) {
		try {
			return conf.index.getAttribute(key);
		} catch (Throwable e) {
			return null;
		}
	}


	/**
	 *  Gets the RepositoryManager associated with this DocReader or null if not available. Assumes the
	 *  RepositoryManager has been set as attribute 'repositoryManager' in the SimpleLuceneIndex that was
	 *  searched.
	 *
	 * @return    The repositoryManager value
	 */
	public RepositoryManager getRepositoryManager() {
		if (rm == null)
			rm = (RepositoryManager) conf.index.getAttribute("repositoryManager");
		return rm;
	}


	/**
	 *  Gets the query that was used in the search.
	 *
	 * @return    The query value
	 */
	public String getQuery() {
		return conf.query;
	}
}


