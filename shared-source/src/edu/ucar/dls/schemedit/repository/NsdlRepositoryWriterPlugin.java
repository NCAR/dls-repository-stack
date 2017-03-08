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

import edu.ucar.dls.schemedit.repository.collection.*;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.dcs.DcsDataRecord;
import edu.ucar.dls.schemedit.config.CollectionConfig;
import edu.ucar.dls.schemedit.config.CollectionRegistry;
import edu.ucar.dls.schemedit.config.StatusFlags;

import edu.ucar.dls.index.reader.XMLDocReader;
import edu.ucar.dls.xml.Dom4jUtils;

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
 * @author    ostwald <p>
 *
 *
 */
public class NsdlRepositoryWriterPlugin extends ServletContextRepositoryWriterPlugin {

	private static boolean debug = true;
	private RepositoryService repositoryService = null;


	/**  Constructor for the NsdlRepositoryWriterPlugin object */
	public NsdlRepositoryWriterPlugin() { }


	/**
	 *  Does nothing - no action required for NsdlRepositoryWriterPlugin
	 *
	 * @param  recId                                NOT YET DOCUMENTED
	 * @param  dcsDataRecord                        NOT YET DOCUMENTED
	 * @exception  RepositoryWriterPluginException  NOT YET DOCUMENTED
	 */
	public void deleteRecord(String recId, DcsDataRecord dcsDataRecord)
		 throws RepositoryWriterPluginException {
		// do nothing
	}


	/**
	 *  Does nothing - no action required for NsdlRepositoryWriterPlugin
	 *
	 * @param  id                                   NOT YET DOCUMENTED
	 * @param  collectionConfig                     NOT YET DOCUMENTED
	 * @param  dcsDataRecord                        NOT YET DOCUMENTED
	 * @exception  RepositoryWriterPluginException  NOT YET DOCUMENTED
	 */
	public void putCollectionData(String id, CollectionConfig collectionConfig, DcsDataRecord dcsDataRecord)
		 throws RepositoryWriterPluginException {

		// do nothing
	}


	/**
	 *  Gets the repositoryService attribute of the NsdlRepositoryWriterPlugin
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
	 * @param  recId                                metadata record Id
	 * @param  dcsDataRecord                        dcsData for the record to be
	 *      written
	 * @param  recordXml                            metadata as an xml String
	 * @param  xmlFormat                            format of metadata record
	 * @exception  RepositoryWriterPluginException  NOT YET DOCUMENTED
	 */
	public void putRecord(String recId, String recordXml, String xmlFormat, DcsDataRecord dcsDataRecord)
		 throws RepositoryWriterPluginException {

		/*
			update the dcsDataRecord for records in Collections Of CollectionRecords
		*/
		if (xmlFormat.equals("ncs_collect")) {
			try {

				RepositoryService repositoryService = getRepositoryService();
				XMLDocReader docReader = repositoryService.getXMLDocReader(recId);
				if (docReader == null)
					throw new Exception("docReader not found for " + recId);
				String collection = docReader.getCollection();

				if (repositoryService.isCollectionOfCollectionRecords(collection)) {
					String handleServiceBaseUrl = repositoryService.getHandleServiceBaseUrl(collection);
					assignMetadataHandle(recordXml, collection, dcsDataRecord, handleServiceBaseUrl);
				}
			} catch (Exception e) {
				// prtlnErr ("putRecord ERROR: " + e.getMessage());
				// e.printStackTrace();
				throw makeErr("putRecord", e);
			}
		}
	}


	/**
	 *  Update the provided DcsDataRecord with collection metadata handle, and
	 *  setspec defined in collection record.<p>
	 *
	 *  The metadataHandle is determined by the recordId, and the collectionSetSpec
	 *
	 * @param  dcsDataRecord         Collection record dcsDataRecord
	 * @param  handleServiceBaseUrl  url of service from which to obtain handle
	 * @param  xmlRecord             the collectionRecord as an XML string
	 * @param  collectionSetSpec     setSpec (key) for CollectionOfCollectionRecords
	 * @exception  Exception         if xmlRecord cannot be parsed or handle cannot be obtained
	 */
	private void assignMetadataHandle(String xmlRecord, String collectionSetSpec, DcsDataRecord dcsDataRecord, String handleServiceBaseUrl) throws Exception {

		org.dom4j.Document record = Dom4jUtils.getXmlDocument(xmlRecord);
		CollectionRecord collectionRecord = new NcsCollectCollectionRecord(record, handleServiceBaseUrl);

		String partnerId = collectionRecord.getId();
		String mdHandle = collectionRecord.getMetadataHandle(collectionSetSpec);
		String setSpec = collectionRecord.getSetSpec();

		// updfate dcsDataRecord
		dcsDataRecord.setSetSpec(setSpec);
		dcsDataRecord.setCollectionMetadataHandle(mdHandle);
	}


	/**
	 *  Make a RepositoryWriterPluginException
	 *
	 * @param  op   NOT YET DOCUMENTED
	 * @param  msg  NOT YET DOCUMENTED
	 * @return      NOT YET DOCUMENTED
	 */
	private RepositoryWriterPluginException makeErr(String op, String msg) {
		return new RepositoryWriterPluginException("NSLD repo writer plugin", op, msg);
	}


	/**
	 *  Make a RepositoryWriterPluginException
	 *
	 * @param  msg  NOT YET DOCUMENTED
	 * @return      NOT YET DOCUMENTED
	 */
	private RepositoryWriterPluginException makeErr(String msg) {
		return new RepositoryWriterPluginException("NSLD repo writer plugin", msg);
	}


	/**
	 *  Make a RepositoryWriterPluginException
	 *
	 * @param  operation  NOT YET DOCUMENTED
	 * @param  cause      NOT YET DOCUMENTED
	 * @return            NOT YET DOCUMENTED
	 */
	private RepositoryWriterPluginException makeErr(String operation, Throwable cause) {
		return new RepositoryWriterPluginException("NSLD repo writer plugin", operation, cause);
	}


	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "NsdlRepositoryWriterPlugin");
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtlnErr(String s) {
		SchemEditUtils.prtln(s, "NsdlRepositoryWriterPlugin");
	}
}

