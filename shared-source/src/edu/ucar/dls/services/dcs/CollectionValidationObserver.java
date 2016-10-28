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
package edu.ucar.dls.services.dcs;

import edu.ucar.dls.schemedit.threadedservices.ThreadedServiceObserver;
import edu.ucar.dls.schemedit.SearchHelper;
import edu.ucar.dls.schemedit.dcs.DcsSetInfo;

import edu.ucar.dls.repository.RepositoryManager;
import edu.ucar.dls.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.queryParser.QueryParser;

import java.text.*;
import java.util.*;
import javax.servlet.ServletContext;

/**
 *  Observer that is fired with the Validating service completes, and sets
 *  status for all valid records in collection to "Done".
 *
 * @author    Jonathan Ostwald
 */
public class CollectionValidationObserver implements ThreadedServiceObserver {

	private static boolean debug = true;

	private String collectionKey = null;
	private DcsSetInfo setInfo = null;
	private SimpleLuceneIndex index = null;
	private RepositoryManager rm;
	private ServletContext servletContext = null;


	/**
	 *  Constructor for the CollectionValidationObserver object
	 *
	 * @param  setinfo         NOT YET DOCUMENTED
	 * @param  rm              NOT YET DOCUMENTED
	 * @param  servletContext  NOT YET DOCUMENTED
	 */
	public CollectionValidationObserver(DcsSetInfo setinfo,
	                                    RepositoryManager rm,
	                                    ServletContext servletContext) {

		this.setInfo = setinfo;
		this.servletContext = servletContext;
		this.collectionKey = this.setInfo.getSetSpec();
		this.rm = rm;
		prtln("CollectionValidationObserver instantiated");
	}


	/**
	 *  This method is called when the service is complete. This method may then do
	 *  additional processing that is required after indexing and will execute
	 *  within the same indexing thread, thus blocking all other indexing
	 *  operations until this method is returned.
	 *
	 * @param  status   The status code upon completion
	 * @param  message  A message describing how the indexer completed
	 */
	public void serviceCompleted(int status, String message) {
		prtln("validation completed!!");
		prtln(message);

		SimpleLuceneIndex index = rm.getIndex();

		//Now we assign status of DONE to all records

		// String query = sessionBean.getCollectionsQueryClause();
		String query = "(allrecords:true AND collection:0" + collectionKey + ")";
		query += " AND dcsisValid:true";

		prtln("query: " + query);

		BooleanQuery booleanQuery = new BooleanQuery();
		QueryParser qp = index.getQueryParser();
		try {
			booleanQuery.add(qp.parse(query), BooleanClause.Occur.MUST);
		} catch (Throwable e) {
			prtln("could not parse query (" + query + "): " + e);
			return;
		}

		SearchHelper searchHelper = new SearchHelper(index);
		ResultDocList results = searchHelper.search(booleanQuery);
		prtln(results.size() + " results found");

		Iterator resultsIter = results.iterator();
		while (resultsIter.hasNext()) {
			ResultDoc resultDoc = (ResultDoc) resultsIter.next();
		}

	}


	/**
	 *  Return a string for the current time and date, sutiable for display in log
	 *  files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	protected final static String getDateStamp() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final void prtln(String s) {
		if (debug) {
			System.out.println(getDateStamp() + " CollectionValidationObserver: " + s);
		}
	}


	/**
	 *  Sets the debug attribute of the object
	 *
	 * @param  db  The new debug value
	 */
	public static void setDebug(boolean db) {
		debug = db;
	}

}

