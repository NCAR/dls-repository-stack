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

import edu.ucar.dls.schemedit.SchemEditUtils;
import javax.servlet.ServletContext;

import edu.ucar.dls.schemedit.FrameworkRegistry;
import edu.ucar.dls.schemedit.MetaDataFramework;
import edu.ucar.dls.schemedit.dcs.*;
// import edu.ucar.dls.schemedit.config.*;

import edu.ucar.dls.repository.RepositoryManager;
import edu.ucar.dls.index.reader.XMLDocReader;
import edu.ucar.dls.util.TimedURLConnection;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.schema.DocMap;

import org.dom4j.Document;

import java.util.*;
import java.text.SimpleDateFormat;
import java.net.URL;

/**
 *  RepositoryEventListener that provides Smile specific handlers for some
 *  repository events (.<p>
 *
 *  See RepositoryEventListener for how to configure. SmileEventListener
 *  requires an additional "smileMoveEventUrl" context paramter.
 *
 * @author    jonathan
 */
public class SmileEventListener extends RepositoryEventListener {

	private static boolean debug = true;
	URL smileMoveEventUrl = null;


	/**  No parameter Constructor for the SmileEventListener object */
	public SmileEventListener() { }


	/**
	 *  Constructor for the SmileEventListener object providing servletContext
	 *
	 * @param  servletContext  the servletContext
	 */
	public SmileEventListener(ServletContext servletContext) {
		super(servletContext);
		try {
			smileMoveEventUrl = new URL((String) servletContext.getInitParameter("smileMoveEventUrl"));
		} catch (Throwable t) {
			prtlnErr("SmileEventLister could not be instantiated: " + t.getMessage());
		}
		prtln("instantiated with url: " + smileMoveEventUrl);
	}


	/**
	 *  Respond to delete-, copy-, move-, copyMoveRecord RepositoryEvents by
	 *  submitting a URL containing event data to an external server.
	 *
	 * @param  event  the repositoryEvent
	 */
	public void handleEvent(RepositoryEvent event) {
		System.out.println("SmileEventListener received a repositoryEvent: " + event.getName());
		System.out.println(event.toString());

		if ("deleteRecord".equals(event.getName()) ||
			"copyRecord".equals(event.getName()) ||
			"moveRecord".equals(event.getName()) ||
			"copyMoveRecord".equals(event.getName())) {

			Map data = event.getEventData();
			String url = this.smileMoveEventUrl + "?func=" + event.getName();
			for (Iterator i = data.keySet().iterator(); i.hasNext(); ) {
				String param = (String) i.next();
				String val = (String) data.get(param);
				url += "&" + param + "=" + val;
			}
			prtln("URL: " + url);
			// ping the url
			try {
				String response = TimedURLConnection.importURL(url, 2000);
				// prtln("\nSmile Response: " + response);
			} catch (Exception e) {
				prtlnErr("could not get response from smile server (" + url + ")");
			}
		}
		else {
			prtln("\"" + event.getName() + " event ignored");
		}

		// set record creation date on moves
		if ("moveRecord".equals(event.getName())) {

			// just to see what we've got - print eventData to console
			Map data = event.getEventData();
			for (Iterator i = data.keySet().iterator(); i.hasNext(); ) {
				String param = (String) i.next();
				String val = (String) data.get(param);
				prtln("- " + param + ": " + val);
			}

			// Grab id of moved record from eventData
			String id = (String) data.get("dstId");
			ServletContext servletContext = this.getServletContext();
			Document doc = null;
			try {
				// Takes a lot of "machinery" (stored in servlet context) to manipulate and
				// store records in repository!
				RepositoryManager rm = (RepositoryManager) getRequiredContextAttributeValue("repositoryManager");
				
				// docReader holds bunch of information about this record
				XMLDocReader docReader = RepositoryUtils.getXMLDocReader(id, rm);
				if (docReader == null)
					throw new Exception("docReader not found for id=" + id);

				String recordXml = docReader.getXml();
				String xmlFormat = docReader.getNativeFormat();
				
				// repositoryService eventually does the actual write (via repositoryWriter)
				RepositoryService repositoryService =
					(RepositoryService) getRequiredContextAttributeValue("repositoryService");

				// get the MetadataFramework for this record's xmlFormat
				FrameworkRegistry fr =
					(FrameworkRegistry) getRequiredContextAttributeValue("frameworkRegistry");
				MetaDataFramework framework = fr.getFramework(xmlFormat);
				if (framework == null)
					throw new Exception("Metadata Framework not found for " + xmlFormat);

				// get the dcsDataRecord for this metadata record
				DcsDataManager dcsDataManager =
					(DcsDataManager) getRequiredContextAttributeValue("dcsDataManager");
				DcsDataRecord dcsDataRecord = dcsDataManager.getDcsDataRecord(id, rm);

				// make the change to metadata record (localize first if necessary)
				if (!framework.getSchemaHelper().getNamespaceEnabled())
					recordXml = Dom4jUtils.localizeXml(recordXml, framework.getRootElementName());

				doc = Dom4jUtils.getXmlDocument(recordXml);
				if (doc == null) {
					throw new Exception("failed to create localized copy of moved record");
				}

				DocMap docMap = new DocMap(doc, framework.getSchemaHelper());

				String recordCreatedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				String recordCreatedPath = "/record/general/recordDate"; // Yours will be different!

				// insert new value (docMap is backed by document)
				try {
					docMap.smartPut(recordCreatedPath, recordCreatedDate);
				} catch (Exception e) {
					throw new Exception("smartPut failed with " + recordCreatedPath + ": " + e.getMessage());
				}

				// now prepare document to write to repository by inserting namespace information
				recordXml = framework.getWritableRecordXml(doc);
				
				// do the write and we're done
				repositoryService.getRepositoryWriter().writeRecord(id, recordXml, docReader, dcsDataRecord);

			} catch (Exception e) {
				// throw new Exception("Unable to copy Record: " + e);
				prtlnErr("Unable to update creationDate for moved record: " + e);
			}

		}
	}


	/**
	 *  Gets the named attribute from the servlet context.
	 *
	 * @param  attrName       attribute to get
	 * @return                the servletContext attribute (as Object)
	 * @exception  Exception  if attribute not found
	 */
	private Object getRequiredContextAttributeValue(String attrName) throws Exception {
		Object attrValue = getServletContext().getAttribute(attrName);
		if (attrValue == null)
			throw new Exception(attrName + " not found in servletContext");
		return attrValue;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "SmileEventListener");
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	static void prtlnErr(String s) {
		SchemEditUtils.prtln(s, "SmileEventListener");
	}
}

