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
package edu.ucar.dls.standards.commcore;

import edu.ucar.dls.xml.XMLFileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.io.File;

/**
 *  Class to read all comm-core standards documents in specified directory, and
 *  provide access to their contents, e.g., getStdDocument, getStandard.
 *
 * @author    Jonathan Ostwald
 */
public class CommCoreServiceHelper {
	private static Log log = LogFactory.getLog(CommCoreServiceHelper.class);
	private static boolean debug = true;
	private Map docMap = null;
	private List docList = null;


	/**
	 *  Constructor for the CommCoreServiceHelper object
	 *
	 * @param  standardsPath  NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public CommCoreServiceHelper(String standardsPath) throws Exception {
		load(standardsPath);
	}


	private void load(String standardsPath) throws Exception {
		this.docMap = new HashMap();
		File standardsDir = new File(standardsPath);
		prtln ("\nloading from " + standardsPath);
		if (!standardsDir.exists())
			throw new Exception("Standards Directory does not exist at " + standardsPath);
		File[] files = standardsDir.listFiles(new XMLFileFilter());
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().startsWith("."))
				continue;
			String stdDocPath = files[i].getAbsolutePath();
			try {
				StdDocument stdDoc = new StdDocument(stdDocPath);
				docMap.put(stdDoc.getIdentifier(), stdDoc);
				prtln((i + 1) + "/" + files.length + ": processed " + files[i].getName() + "\n\t " + stdDoc.getIdentifiers().size() + " items read");
			} catch (Exception e) {
				prtln("WARNING: could not process standards document at " + stdDocPath);
			}
		}
	}


	/**
	 *  Gets the stdDocument attribute of the CommCoreServiceHelper object
	 *
	 * @param  docId  NOT YET DOCUMENTED
	 * @return        The stdDocument value
	 */
	public StdDocument getStdDocument(String docId) {
		return (StdDocument) docMap.get(docId);
	}


	/**
	 *  Gets the standard attribute of the CommCoreServiceHelper object
	 *
	 * @param  docId  NOT YET DOCUMENTED
	 * @param  stdId  NOT YET DOCUMENTED
	 * @return        The standard value
	 */
	public Standard getStandard(String docId, String stdId) {
		Standard std = null;
		StdDocument stdDoc = this.getStdDocument(docId);
		if (stdDoc != null)
			std = stdDoc.getStandard(stdId);
		return std;
	}


	/**
	 *  Gets the standard attribute of the CommCoreServiceHelper object
	 *
	 * @param  stdId  NOT YET DOCUMENTED
	 * @return        The standard value
	 */
	public Standard getStandard(String stdId) {
		Standard std = null;
		for (Iterator i = getStdDocuments().iterator(); i.hasNext(); ) {
			StdDocument stdDoc = (StdDocument) i.next();
			std = stdDoc.getStandard(stdId);
			if (std != null)
				break;
		}
		return std;
	}


	/**
	 *  Gets the stdDocuments attribute of the CommCoreServiceHelper object
	 *
	 * @return    The stdDocuments value
	 */
	public List getStdDocuments() {
		if (this.docList == null) {
			this.docList = new ArrayList();
			for (Iterator i = this.docMap.values().iterator(); i.hasNext(); ) {
				this.docList.add((StdDocument) i.next());
			}
		}
		return this.docList;
	}


	/**
	 *  The main program for the CommCoreServiceHelper class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		prtln("\nCommCoreServiceHelper");
		String stdId = "HS.F-IF.7.d";
		if (args.length > 0)
			stdId = args[0];
		String dir = "H:/python-lib/common_core/converted";
		CommCoreServiceHelper helper = new CommCoreServiceHelper(dir);
		Standard std = helper.getStandard(stdId);
		if (std == null)
			throw new Exception ("standard not found for " + stdId);
		prtln (std.toString());
		// prtln("author: " + std.getAuthor());
		// prtln("topic: " + std.getTopic());

	}


	private static void prtln(String s) {
		if (debug) {
			System.out.println("CommCoreServiceHelper: " + s);
		}
	}
}

