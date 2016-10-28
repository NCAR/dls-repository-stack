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
package edu.ucar.dls.index;

import org.apache.lucene.document.*;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.apache.lucene.search.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.index.CorruptIndexException;

import org.apache.lucene.facet.search.results.FacetResult;

import java.util.*;
import java.text.SimpleDateFormat;

import edu.ucar.dls.index.reader.*;

import java.io.*;

/**
 *  A List of ResultDocs returned by a SimpleLucenIndex search.
 *
 * @author    John Weatherley
 * @see       SimpleLuceneIndex
 * @see       ResultDoc
 */
public class ResultDocList extends AbstractList {
	private static boolean debug = true;

	private TopDocs _topDocs = null;
	private List<FacetResult> _facetResults = null;
	private ResultDocConfig _resultDocConfig = null;

	private ResultDoc[] _resultDocs = null;


	/**  Creates an empty ResultDocList that can not be expanded. */
	public ResultDocList() {
		_resultDocs = new ResultDoc[]{};
	}


	/**
	 *  Creates a ResultDocList backed by the given resultDocs that can not be expanded.
	 *
	 * @param  resultDocs  The result docs that back this ResultDocList.
	 */
	public ResultDocList(ResultDoc[] resultDocs) {
		_resultDocs = resultDocs;
	}


	/**
	 *  Creates a ResultDocList backed by the given search results that can not be expanded. Constructed by
	 *  SimpleLuceneIndex when a search is made.
	 *
	 * @param  topDocs          The TopDocs
	 * @param  facetResults     The List of FacetResult objects, or null if none
	 * @param  resultDocConfig  The config
	 */
	public ResultDocList(TopDocs topDocs, List<FacetResult> facetResults, ResultDocConfig resultDocConfig) {
		_topDocs = topDocs;
		_facetResults = facetResults;
		_resultDocConfig = resultDocConfig;
	}


	private int numGets = 0;


	/**
	 *  Get the ResultDoc at the given location.
	 *
	 * @param  i  Index
	 * @return    The ResultDoc
	 */
	public ResultDoc get(int i) {
		//prtln("get() i: " + i + " num gets(): " + (++numGets));

		if (i < 0 || i >= size())
			throw new IndexOutOfBoundsException("Index " + i + " is out of bounds. Must be greater than or equal to 0 and less than " + size());

		// First check to see if this List is backed by an array and return from there:
		if (_resultDocs != null) {
			return _resultDocs[i];
		}

		// If not backed by an array, fetch from the index:
		ScoreDoc scoreDoc = _topDocs.scoreDocs[i];
		return new ResultDoc(_resultDocConfig, scoreDoc.doc, scoreDoc.score);
	}


	/**
	 *  The number of search results.
	 *
	 * @return    The number of search results.
	 */
	public int size() {
		if (_resultDocs != null)
			return _resultDocs.length;
		if (_topDocs == null)
			return 0;
		return _topDocs.totalHits;
	}


	/**
	 *  Gets the facet results, or null if none were requested
	 *
	 * @return    The facetResults value
	 */
	public List<FacetResult> getFacetResults() {
		return _facetResults;
	}

	/**
	 *  Gets the ResultDocs as an array. Note that this is significantly less efficient than using the List
	 *  methods for access.
	 *
	 * @return    The ResultDocs
	 */
	public ResultDoc[] toArray() {
		if (_resultDocs == null)
			_resultDocs = (ResultDoc[]) super.toArray(new ResultDoc[]{});
		return _resultDocs;
	}

// ---------------------- Debug methods -------------------------------
	/**
	 *  Gets a datestamp of the current time formatted for display with logs and output.
	 *
	 * @return    A datestamp for display purposes.
	 */
	public final static String getDateStamp() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final void prtlnErr(String s) {
		System.err.println(getDateStamp() + " ResultDocList ERROR: " + s);
	}



	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final static void prtln(String s) {
		if (debug)
			System.out.println(getDateStamp() + " ResultDocList: " + s);
	}


	/**
	 *  Sets the debug attribute of the SimpleLuceneIndex object
	 *
	 * @param  db  The new debug value
	 */
	public static void setDebug(boolean db) {
		debug = db;
	}
}


