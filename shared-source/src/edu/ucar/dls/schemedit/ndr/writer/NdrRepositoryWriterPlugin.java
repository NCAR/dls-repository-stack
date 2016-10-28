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
package edu.ucar.dls.schemedit.ndr.writer;

import edu.ucar.dls.schemedit.repository.RepositoryService;
import edu.ucar.dls.schemedit.repository.ServletContextRepositoryWriterPlugin;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.dcs.DcsDataRecord;
import edu.ucar.dls.schemedit.config.CollectionConfig;
import edu.ucar.dls.schemedit.config.CollectionRegistry;
import edu.ucar.dls.schemedit.config.StatusFlags;

import edu.ucar.dls.schemedit.ndr.SyncReportEntry;
import edu.ucar.dls.schemedit.ndr.writer.MetadataWriter;
import edu.ucar.dls.schemedit.ndr.writer.NSDLCollectionWriter;
import edu.ucar.dls.schemedit.ndr.writer.MetadataProviderWriter;

import edu.ucar.dls.index.reader.XMLDocReader;

import edu.ucar.dls.ndr.apiproxy.InfoXML;
import edu.ucar.dls.ndr.NdrUtils;

import javax.servlet.ServletContext;

import java.util.*;
import java.net.*;

/**
 *  Methods to create, copy, and put Records to the Repository<p>
 *
 *  NOTE: currently, this class implements a repositoryWriter plugin for the
 *  NDR. When we understand what the plugin INTERFACE should be, then the
 *  interface will be in this package, and the NDR implementation will be
 *  elsewhere...
 *
 * @author     ostwald <p>
 *
 *
 */
public class NdrRepositoryWriterPlugin extends ServletContextRepositoryWriterPlugin {

	private static boolean debug = false;
	private RepositoryService repositoryService = null;


	/**
	 *  Constructor for the NdrRepositoryWriterPlugin object
	 *
	 */
	public NdrRepositoryWriterPlugin() { }


	/**
	 *  Gets the repositoryService attribute of the NdrRepositoryWriterPlugin
	 *  object
	 *
	 * @return    The repositoryService value
	 */
	private RepositoryService getRepositoryService() {
		if (repositoryService == null && getServletContext() != null) {
			this.repositoryService = (RepositoryService) getServletContext().getAttribute("repositoryService");
		}
		return this.repositoryService;
	}


	/**
	 *  Writes a metadata record to the NDR, with special handling for records of
	 *  ncs_collect format. For ncs_collect records (the collection management
	 *  format), we check to ensure the record is "FinalStatus" and valid, and if
	 *  so we update the entire collection definition in the NDR.<p>
	 *
	 *  Note: this method is called by RepositoryWriter AFTER the record has been
	 *  indexed, so the "recordXml" provided here should be equal to the xml stored
	 *  in the reader object for "recId"??
	 *
	 * @param  recId                                   metadata record Id
	 * @param  dcsDataRecord                           dcsData for the record to be
	 *      written
	 * @param  recordXml                               metadata as an xml String
	 * @param  xmlFormat                               format of metadata record
	 * @exception  NdrRepositoryWriterPluginException  NOT YET DOCUMENTED
	 */
	public void putRecord(String recId, String recordXml, String xmlFormat, DcsDataRecord dcsDataRecord)
		 throws NdrRepositoryWriterPluginException {

		if (!(this.isNdrServiceEnabled() &&
			this.isNdrEnabledCollection(recId)))
			return;

		if (!this.isNdrServiceActive()) {
			throw new NdrRepositoryWriterPluginException("NdrService is deactivated - cannot write");
		}

		/*
			Do not write to NDR if status is "Recommended" - this is a reserved status for records
			that enter the system via a Recommend Service. This keeps spam from being writen to NDR.
		*/
		prtln(" ... status: " + dcsDataRecord.getStatus());
		if (dcsDataRecord.getStatus().equals(StatusFlags.RECOMMENDED_STATUS))
			return;

		SyncReportEntry entry = null;
		MetadataWriter writer = null;
		try {
			if (xmlFormat.equals("ncs_collect")) {
				writer = new NSDLCollectionWriter(getServletContext());
			}
			else {
				writer = new MetadataWriter(getServletContext());
			}

			entry = writer.write(recId, recordXml, dcsDataRecord);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new NdrRepositoryWriterPluginException("putRecord", e);
		}
	}


	/**
	 *  NOT YET CALLED
	 *
	 * @param  id                                      NOT YET DOCUMENTED
	 * @param  collectionConfig                        NOT YET DOCUMENTED
	 * @param  dcsDataRecord                           NOT YET DOCUMENTED
	 * @exception  NdrRepositoryWriterPluginException  NOT YET DOCUMENTED
	 */
	public void putCollectionData(String id, CollectionConfig collectionConfig, DcsDataRecord dcsDataRecord)
		 throws NdrRepositoryWriterPluginException {
		prtln("putCollectionData()");

		if (!(this.isNdrServiceEnabled() &&
			this.isNdrEnabledCollection(id)))
			return;

		if (!this.isNdrServiceActive()) {
			throw new NdrRepositoryWriterPluginException("NdrService is deactivated - cannot write");
		}

		// Do not write to NDR if status is "Recommended"
		if (dcsDataRecord.getStatus() == StatusFlags.RECOMMENDED_STATUS)
			return;

		try {
			MetadataProviderWriter writer = new MetadataProviderWriter(getServletContext());
			writer.write(id, collectionConfig, dcsDataRecord);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new NdrRepositoryWriterPluginException("putCollectionData", e);
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  recId                                   NOT YET DOCUMENTED
	 * @param  dcsDataRecord                           NOT YET DOCUMENTED
	 * @exception  NdrRepositoryWriterPluginException  NOT YET DOCUMENTED
	 */
	public void deleteRecord(String recId, DcsDataRecord dcsDataRecord) throws NdrRepositoryWriterPluginException {
		prtln("deleteRecord");

		if (!(this.isNdrServiceEnabled() &&
			this.isNdrEnabledCollection(recId)))
			return;

		if (!this.isNdrServiceActive()) {
			throw new NdrRepositoryWriterPluginException("Cannot write to NDR", "NdrService is deactivated");
		}

		String handle = dcsDataRecord.getNdrHandle();

		if (handle != null && handle.trim().length() > 0) {
			dcsDataRecord.clearSyncErrors();
			InfoXML response = null;
			try {
				response = NdrUtils.deleteNDRObject(handle);
			} catch (Exception e) {
				throw new NdrRepositoryWriterPluginException("delete", e);
			}
			if (response.hasErrors()) {
				throw new NdrRepositoryWriterPluginException("delete", response.getError());
			}
		}
	}


	/**
	 *  Gets the ndrServiceEnabled attribute of the NdrRepositoryWriterPlugin
	 *  object
	 *
	 * @return    The ndrServiceEnabled value
	 */
	private boolean isNdrServiceEnabled() {
		return (Boolean) this.getServletContext().getAttribute("ndrServiceEnabled");
	}


	/**
	 *  Gets the ndrServiceActive attribute of the NdrRepositoryWriterPlugin object
	 *
	 * @return    The ndrServiceActive value
	 */
	private boolean isNdrServiceActive() {
		return (Boolean) this.getServletContext().getAttribute("ndrServiceActive");
	}


	/**
	 *  Gets the ndrEnabledCollection attribute of the NdrRepositoryWriterPlugin
	 *  object
	 *
	 * @param  recId  NOT YET DOCUMENTED
	 * @return        The ndrEnabledCollection value
	 */
	private boolean isNdrEnabledCollection(String recId) {

		CollectionConfig config = null;
		try {
			RepositoryService repositoryService = this.getRepositoryService();
			if (repositoryService == null)
				throw new Exception("Repository Service not found");
			XMLDocReader docReader = this.repositoryService.getXMLDocReader(recId);
			String collection = docReader.getCollection();
			CollectionRegistry reg =
				(CollectionRegistry) getServletContext().getAttribute("collectionRegistry");
			config = reg.getCollectionConfig(collection);
		} catch (Throwable t) {
			prtlnErr("isNdrEnabledCollection error (" + recId + "): " + t.getMessage());
			return false;
		}

		return (config != null && config.isNDRCollection());
	}


	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "NdrRepositoryWriterPlugin");
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtlnErr(String s) {
		SchemEditUtils.prtln(s, "NdrRepositoryWriterPlugin");
	}
}

