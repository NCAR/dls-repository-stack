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
package edu.ucar.dls.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.facet.taxonomy.CategoryPath;

import java.io.*;
import java.util.*;
import java.text.*;

/**
 *  Wrapper for the Lucene Document and facet CategoryPaths that are to be indexed.
 *
 * @author    John Weatherley
 */
public final class DocumentWrapper {
	private boolean debug = true;

	private Document _luceneDocument = null;
	private ArrayList<CategoryPath> _categoryPathList = null;


	/**
	 *  Constructor for the DocumentWrapper object
	 *
	 * @param  luceneDocument           The Lucene Document being populated for indexing
	 * @param  categoryDocumentBuilder  The CategoryDocumentBuilder being populated for faceted indexing
	 */
	/* public DocumentWrapper(Document luceneDocument, List<CategoryPath> _categoryPathList) {
		_luceneDocument = luceneDocument;
		_categoryDocumentBuilder = categoryDocumentBuilder;
	} */


	/**  Constructor for the DocumentWrapper object */
	public DocumentWrapper() { }


	/**
	 *  Returns the Lucene Document being populated for indexing for this item.
	 *
	 * @return    The document value
	 */
	public Document getDocument() {
		if(_luceneDocument == null)
			_luceneDocument = new Document();
		return _luceneDocument;
	}

	/**
	 *  Adds a facet CategoryPath to be indexed for this item.
	 *
	 * @return    The CategoryPath value
	 */	
	public void addCategoryPath(CategoryPath categoryPath) {
		if(_categoryPathList == null)
			_categoryPathList = new ArrayList<CategoryPath>();
		_categoryPathList.add(categoryPath);
	}
	
	/**
	 *  Returns the CategoryPath List being populated for faceted indexing.
	 *
	 * @return    The CategoryPath List or null if none
	 */
	protected List<CategoryPath> getCategoryPathList() {
		return _categoryPathList;
	}

	//------------------- Utility methods -------------------

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
	private final void prtlnErr(String s) {
		System.err.println(getDateStamp() + " DocumentWrapper Error: " + s);
	}



	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final void prtln(String s) {
		if (debug)
			System.out.println(getDateStamp() + " DocumentWrapper: " + s);
	}



	/**
	 *  Sets the debug attribute of the DocumentWrapper object
	 *
	 * @param  db  The new debug value
	 */
	public final void setDebug(boolean db) {
		debug = db;
	}

}

