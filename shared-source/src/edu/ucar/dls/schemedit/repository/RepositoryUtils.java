
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
package edu.ucar.dls.schemedit.repository;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.dcs.*;
import edu.ucar.dls.index.*;
import edu.ucar.dls.index.reader.*;
import edu.ucar.dls.repository.*;
import java.util.*;
import java.io.*;

/**
 *  Utility methods for Repsitory-level objects, such as SetInfo, and
 *  operations, such as obtaining docReaders and files from the index.
 *
 * @author    ostwald
 */
public class RepositoryUtils {

	static boolean debug = false;


	/**
	 *  Obtains a setInfo instance for specified collection from RepositoryMangager
	 *
	 * @param  collection  a collection key
	 * @param  rm          repositoryMangager
	 * @return             The dcsSetInfo value
	 */
	public static SetInfo getSetInfo(String collection, RepositoryManager rm) {
		SetInfo setInfo = rm.getSetInfo(collection);
		if (setInfo == null) {
			prtln("RepositoryUtils.getSetInfo(): setInfo not found for " + collection);
		}
		return setInfo;
	}


	/**
	 *  Obtains a dcsSetInfo instance for specified collection from RepositoryMangager.
	 *  The dcsSetInfo wraps the repositoryManager's setInfo with additional
	 *  information about this collection.
	 *
	 * @param  collection  a collection key
	 * @param  rm          repositoryMangager
	 * @return             The dcsSetInfo value
	 */
	public static DcsSetInfo getDcsSetInfo(String collection, RepositoryManager rm) {
		// prtln("DcsSetInfo trying to find collection: " + collection);
		DcsSetInfo dcsSetInfo = null;
		SetInfo setInfo = getSetInfo(collection, rm);
		if (setInfo != null) {
			dcsSetInfo = new DcsSetInfo(setInfo);
			dcsSetInfo.setSetInfoData(rm);
		}
		return dcsSetInfo;
	}



	/**
	 *  get DcsSetInfo for specified collection from a provided list of DcsSetInfo
	 *  instances
	 *
	 * @param  collection   Description of the Parameter
	 * @param  dcsSetInfos  all dcsSetInfos
	 * @return              The dcsSetInfo value
	 */
	public static DcsSetInfo getDcsSetInfo(String collection, List dcsSetInfos) {
		// prtln("DcsSetInfo trying to find collection: " + collection);
		for (Iterator i = dcsSetInfos.iterator(); i.hasNext(); ) {
			DcsSetInfo dcsSetInfo = (DcsSetInfo) i.next();
			if (dcsSetInfo.getSetSpec().equals(collection)) {
				return dcsSetInfo;
			}
		}
		prtln("RepositoryUtils.getDcsSetInfo(): dcsSetInfo not found for " + collection);
		return null;
	}


	/**
	 *  Returns the file associated with an id in an index.
	 *
	 * @param  id  record id
	 * @param  rm  repositoryManager
	 * @return     The source file for the record identified by id
	 */
	public static File getFileFromIndex(String id, RepositoryManager rm) {
		ResultDoc record = rm.getRecord(id);
		if (record == null) {
			prtln("indexed record not found for id: " + id);
			return null;
		}
		XMLDocReader docReader = (XMLDocReader) record.getDocReader();
		return docReader.getFile();
	}


	/**
	 *  Gets the XMLDocReader associated with an id
	 *
	 * @param  id             record ID
	 * @param  rm             repositoryManager
	 * @return                XMLDocReader or null if not found
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static XMLDocReader getXMLDocReader(String id, RepositoryManager rm) throws Exception {
		ResultDoc record = rm.getRecord(id);
		if (record == null) {
			throw new Exception("getXMLDocReader() indexed record not found for id: " + id);
			//return null;
		}
		try {
			return (XMLDocReader) record.getDocReader();
		} catch (Exception e) {
			throw new Exception("getXMLDocReader found unexpected docReader class " + e.getMessage());
			// return null;
		}
	}


	/**
	 *  Gets the recordFormat attribute of the RepositoryService class
	 *
	 * @param  id             record Id
	 * @param  rm             repositoryManager
	 * @return                The recordFormat value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static String getRecordFormat(String id, RepositoryManager rm) throws Exception {
		XMLDocReader reader = getXMLDocReader(id, rm);
		if (reader == null) {
			throw new Exception("Record (" + id + ") not found in the index");
		}
		return reader.getNativeFormat();
	}



	// ----------------- util util---------------------

	/**
	 *  Gets the dateString attribute of the RepositoryUtils class
	 *
	 * @return    The dateString value
	 */
	public static String getDateString() {
		return SchemEditUtils.fullDateString(new Date());
	}


	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "RepositoryUtils");
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	static void prtlnErr(String s) {
		SchemEditUtils.prtln(s, "RepositoryUtils");
	}
}

