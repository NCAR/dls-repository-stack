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
package edu.ucar.dls.schemedit;

import java.util.*;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.repository.RepositoryUtils;
import edu.ucar.dls.schemedit.repository.RepositoryService;
import edu.ucar.dls.schemedit.repository.collection.*;
import edu.ucar.dls.schemedit.dcs.DcsDataRecord;
import edu.ucar.dls.schemedit.dcs.StatusListener;
import edu.ucar.dls.schemedit.dcs.StatusEvent;
import edu.ucar.dls.schemedit.config.*;
import edu.ucar.dls.schemedit.standards.adn.AsnToAdnMapper;

import edu.ucar.dls.repository.RepositoryManager;
import edu.ucar.dls.index.reader.XMLDocReader;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.serviceclients.handle.*;

import javax.servlet.ServletContext;

import org.dom4j.Document;

/**
 *  Listens for StatusChanged events generated by DcsDataManager via
 *  DcsDataRecord.updateStatus.
 *
 * @author     Jonathan Ostwald
 * @created    December 10, 2008
 */
public class WorkFlowServices implements StatusListener {

	private static boolean debug = false;
	private ServletContext servletContext = null;


	/**
	 *  Constructor for the WorkFlowServices object
	 *
	 * @param  servletContext  the ServletContext
	 */
	public WorkFlowServices(ServletContext servletContext) {
		this.servletContext = servletContext;
	}


	/**
	 *  Gets the repositoryManager attribute of the WorkFlowServices object
	 *
	 * @return    The repositoryManager value
	 */
	private RepositoryManager getRepositoryManager() {
		RepositoryManager rm = (RepositoryManager) servletContext.getAttribute("repositoryManager");
		if (rm == null) {
			prtln("WARNING: \'repositoryManager\' not found in servlet context");
		}
		return rm;
	}


	/**
	 *  Gets the repositoryService attribute of the WorkFlowServices object
	 *
	 * @return    The repositoryService value
	 */
	private RepositoryService getRepositoryService() {
		RepositoryService repositoryService = (RepositoryService) servletContext.getAttribute("repositoryService");
		if (repositoryService == null) {
			prtln("WARNING: \'repositoryService\' not found in servlet context");
		}
		return repositoryService;
	}


	/**
	 *  Gets the metaDataFramework attribute of the WorkFlowServices object
	 *
	 * @param  xmlFormat  the format for which to obtain a MetaDataFramework
	 * @return            The metaDataFramework value
	 */
	protected MetaDataFramework getMetaDataFramework(String xmlFormat) {
		FrameworkRegistry reg = (FrameworkRegistry) servletContext.getAttribute("frameworkRegistry");
		if (reg != null) {
			return reg.getFramework(xmlFormat);
		}
		else {
			prtln("WARNING: \'frameworkRegistry\' not found in servlet context");
			return null;
		}
	}


	/**
	 *  StatusChanged event handler, called when the status for a metadata record
	 *  has been changed
	 *
	 * @param  statusEvent  the statusEvent to be handled
	 */
	public void statusChanged(StatusEvent statusEvent) {
		RepositoryManager rm = getRepositoryManager();
		DcsDataRecord dcsDataRecord = (DcsDataRecord) statusEvent.getSource();

		String currentStatus;
		String priorStatus;
		String statusNote;
		currentStatus = priorStatus = statusNote = "unknown";

		String collection;
		String xmlFormat;
		collection = xmlFormat = "unknown";

		String id = null;
		XMLDocReader docReader = null;

		prtln("\nreceived a statusEvent!");

		if (dcsDataRecord != null) {
			currentStatus = dcsDataRecord.getStatus();
			priorStatus = dcsDataRecord.getPriorStatus();
			statusNote = dcsDataRecord.getStatusNote();
			id = dcsDataRecord.getId();
		}

		if (id != null) {
			try {
				docReader = RepositoryUtils.getXMLDocReader(id, rm);
				collection = docReader.getCollection();
				xmlFormat = docReader.getNativeFormat();
			} catch (Exception e) {
				prtln("Unable to obtain docReader for changed record: " + e.getMessage());
			}
		}

		// show the info available when statusChanged is called
		prtln("id: " + id);
		prtln("collection: " + collection);
		prtln("xmlFormat: " + xmlFormat);
		prtln("statusNote: " + statusNote);
		prtln("current status: " + currentStatus);
		prtln("prior status: " + priorStatus);
		prtln("\n");

		// Do operations when status changes to FINAL or "In Progress"
		if (currentStatus != null && 
			(currentStatus.equals("In Progress")) || 
			 StatusFlags.isFinalStatusValue(currentStatus)) {

			prtln("Status is FINAL or In Progress");
			RepositoryService repositoryService = this.getRepositoryService();

			// Operate on the record that has just gone to Done
			
		}
	}


	/**
	 *  Obtain a metadataHandle from the handleService for provided partnerId and
	 *  setSpec, and write it to the provided DcsDataRecord.
	 *
	 * @param  partnerId             the partnerId of this collectionOfCollections
	 *      Record
	 * @param  setSpec               the setSpec of this collectionOfCollections
	 *      Record
	 * @param  dcsDataRecord         the dcsDataRecord for this collectionOfCollections
	 *      Record
	 * @param  handleServiceBaseUrl  baseUrl to the handle service
	 * @exception  Exception         if metadataHandle cannot be obtained and
	 *      assigned
	 */
	 private void assignMetadataHandle(XMLDocReader docReader, DcsDataRecord dcsDataRecord, String handleServiceBaseUrl) throws Exception {
		String partnerId = dcsDataRecord.getId();
		Document record = docReader.getXmlDoc();
		String xmlFormat = docReader.getNativeFormat();
		CollectionRecord collectionRecord = null;
		if ("ncs_collect".equals(xmlFormat))
			collectionRecord = new NcsCollectCollectionRecord(record, handleServiceBaseUrl);
		else if ("osm_collect".equals(xmlFormat))
			collectionRecord = new OsmCollectCollectionRecord(record, handleServiceBaseUrl);
		else {
			throw new Exception ("unrecognized format for collection record: " + xmlFormat);
		}
		
		// NO - we're looking for the 
		String collectionSetSpec = docReader.getCollection();
		if (collectionSetSpec == null || collectionSetSpec.trim().length() == 0) {
			throw new Exception ("could not find setSpec in collection record " + partnerId);
		}
		
		String mdHandle = collectionRecord.getMetadataHandle(collectionSetSpec);
		prtln("mdHandle: " + mdHandle);

		String setSpec = collectionRecord.getSetSpec();
		
		// updfate dcsDataRecord
		dcsDataRecord.setSetSpec(setSpec);
		dcsDataRecord.setCollectionMetadataHandle(mdHandle);
		
		dcsDataRecord.flushToDisk();
	}

	/**
	 *  Save assigned standards to CAT service.<p>
	 *
	 *  NOTE: This is where we USED to save assignments to casaaServer. Disabled
	 *  when casaa was retired in favor of CAT REST service. There should be an
	 *  equivalent call if we want to restore this feature.
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	/* 	private void saveContentStandardAssignments(XMLDocReader docReader) {
		try {
			if (docReader == null) {
				throw new Exception("docReader is null");
			}
			String xmlFormat = docReader.getNativeFormat();
			MetaDataFramework framework = getMetaDataFramework(xmlFormat);
			if (framework == null) {
				throw new Exception("metadataFramework not found for \"" + xmlFormat + "\"");
			}
			if (framework.getStandardsManager() == null) {
				prtln("framework does not have nses4thLevelStandards - returning");
				return;
			}
			if (!docReader.getFile().exists()) {
				prtln("docReader could not obtain file");
			}
			Document doc = framework.getEditableDocument(docReader.getFile());
			StandardsSuggestionService casaaService =
					(StandardsSuggestionService) servletContext.getAttribute("casaaService");
			if (casaaService == null) {
				throw new Exception("\"casaaService\" not found in servletContext");
			}
			AsnToAdnMapper standardsMapper = (AsnToAdnMapper) servletContext.getAttribute("standardsMapper");
			if (standardsMapper == null) {
				throw new Exception("\"standardsMapper\" not found in servletContext");
			}
			CasaaServiceHelper.saveAssignments(doc, framework, casaaService, standardsMapper);
		} catch (Exception e) {
			prtln("Unable to save content standards: " + e.getMessage());
		} catch (Throwable t) {
			prtln("Unknown error trying to save content standards: " + t.getMessage());
			t.printStackTrace();
		}
	}
 */
	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "WorkFlowServices");
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	static void prtlnErr(String s) {
		SchemEditUtils.prtln(s, "WorkFlowServices ERROR:");
	}
}

