
/**
 *  Copyright 2002, 2003 DLESE Program Center/University Corporation for
 *  Atmospheric Research (UCAR), P.O. Box 3000, Boulder, CO 80307,
 *  support@dlese.org. This file is part of the DLESE OAI Project. The DLESE OAI
 *  Project is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU General Public License as published by the Free Software
 *  Foundation; either version 2 of the License, or (at your option) any later
 *  version. The DLESE OAI Project is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 *  Public License for more details. You should have received a copy of the GNU
 *  General Public License along with The DLESE System; if not, write to the
 *  Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307 USA
 */

package edu.ucar.dls.schemedit.repository;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.config.CollectionRegistry;
import edu.ucar.dls.schemedit.config.CollectionConfig;

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
public class CollectionIndexingObserver implements FileIndexingObserver {

	private static boolean debug = true;
	CollectionRegistry collectionRegistry;
	String collectionKey;
	RepositoryManager rm;


	/**
	 *  Constructor for the CollectionIndexingObserver object
	 *
	 * @param  collectionRegistry  the collectionRegistry (contains IDGenerators)
	 * @param  rm                  the repositoryManager
	 */
	public CollectionIndexingObserver(String collectionKey, CollectionRegistry collectionRegistry, RepositoryManager rm) {
		this.collectionKey = collectionKey;
		this.collectionRegistry = collectionRegistry;
		this.rm = rm;
		prtln ("starting to index collection");
	}


	/**
	 *  Called when indexing is complete.
	 *
	 * @param  status   status code returned by indexer
	 * @param  message  indexing message
	 */
	public void indexingCompleted(int status, String message) {
		// prtln("* Indexing Completed: " + message + " *");
		prtln("Indexing Completed for " + collectionKey);
		// prtln("Reinitializing IDGenerator for " + collectionKey);
		try {
			CollectionConfig collectionConfig = collectionRegistry.getCollectionConfig(collectionKey, false);
			if (collectionConfig == null)
				throw new Exception ("collection config not found for " + collectionKey);
			collectionRegistry.initializeIDGenerator (collectionConfig, rm.getIndex());
		} catch (Throwable e) {
			prtlnErr ("IDGenerator NOT re-initialized: " + e.getMessage());
		}

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
			System.out.println(getDateStamp() + " CollectionIndexingObserver: " + s);
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

