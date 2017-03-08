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
package edu.ucar.dls.schemedit.repository;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.config.CollectionRegistry;

import edu.ucar.dls.repository.*;
import edu.ucar.dls.index.*;

import java.util.*;
import java.io.*;
import java.text.*;

/**
 *  RepositoryIndexingObsever initializes IDGenerators for each collection after
 *  reindexing the entire repository (all collections). This is necessary
 *  because if metadata records have been added by hand (outside the DCS), then
 *  the existing "nextID" may no longer be valid.
 *
 * @author    ostwald<p>
 *
 *
 */
public class RepositoryIndexingObserver implements FileIndexingObserver {

	private static boolean debug = true;
	CollectionRegistry collectionRegistry;
	RepositoryManager rm;


	/**
	 *  Constructor for the RepositoryIndexingObserver object
	 *
	 * @param  collectionRegistry  the collectionRegistry (contains IDGenerators)
	 * @param  rm                  the repositoryManager
	 */
	public RepositoryIndexingObserver(CollectionRegistry collectionRegistry, RepositoryManager rm) {
		this.collectionRegistry = collectionRegistry;
		this.rm = rm;
		prtln ("Commencing ...");
	}


	/**
	 *  Called when indexing is complete.
	 *
	 * @param  status   status code returned by indexer
	 * @param  message  indexing message
	 */
	public void indexingCompleted(int status, String message) {
		prtln("Indexing Completed\n\t" + message);
		// prtln("Reinitializing IDGenerators");
		collectionRegistry.initializeIDGenerators(rm.getIndex());

	}
	// ---------------------- Debug info --------------------

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
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final void prtlnErr(String s) {
		System.err.println(getDateStamp() + " " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final void prtln(String s) {
		if (debug) {
			System.out.println(getDateStamp() + " RepositoryIndexingObserver: " + s);
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

