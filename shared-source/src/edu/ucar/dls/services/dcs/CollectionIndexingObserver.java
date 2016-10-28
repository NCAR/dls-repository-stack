
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

package edu.ucar.dls.services.dcs;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.dcs.DcsSetInfo;
import edu.ucar.dls.schemedit.config.CollectionRegistry;
import edu.ucar.dls.schemedit.config.CollectionConfig;

import edu.ucar.dls.schemedit.threadedservices.ValidatingService;

import edu.ucar.dls.repository.*;
import edu.ucar.dls.index.*;

import java.util.*;
import java.io.*;
import java.text.*;

import javax.servlet.ServletContext;

/**
 *  CollectionIndexingObserver listens for completion of collection indexing,
 *  and then validates the collection.
 *
 * @author    ostwald<p>
 *
 *
 */
public class CollectionIndexingObserver implements FileIndexingObserver {

	private static boolean debug = true;
	private String collectionKey;
	private DcsSetInfo setInfo = null;
	private RepositoryManager rm;
	private ServletContext servletContext = null;


	/**
	 *  Constructor for the CollectionIndexingObserver object
	 *
	 * @param  rm                  the repositoryManager
	 * @param  setinfo             NOT YET DOCUMENTED
	 * @param  servletContext      NOT YET DOCUMENTED
	 */
	public CollectionIndexingObserver(DcsSetInfo setinfo,
	                                  RepositoryManager rm,
	                                  ServletContext servletContext) {

		this.setInfo = setinfo;
		this.servletContext = servletContext;
		this.collectionKey = this.setInfo.getSetSpec();
		this.rm = rm;
		prtln("CollectionIndexingObserver instantiated");
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
		try {

			ValidatingService validatingService = (ValidatingService) this.servletContext.getAttribute("validatingService");
			if (validatingService == null) {
				throw new Exception("Server Error: \"validatingService\" not found in servlet context");
			}

			if (validatingService.getIsProcessing()) {
				String id = validatingService.getSessionId();
				DcsSetInfo set = validatingService.getValidatingSetInfo();
				throw new Exception("Server Error: \"validatingService\" is busy");
			}
			prtln("VALIDATING");
			CollectionValidationObserver observer = new CollectionValidationObserver(setInfo, rm, servletContext);
			validatingService.validateRecords(setInfo, null, null, true, observer);

		} catch (Throwable e) {
			prtlnErr("indexingCompleted task ERROR: " + e.getMessage());
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

