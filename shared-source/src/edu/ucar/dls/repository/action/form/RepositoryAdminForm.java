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
package edu.ucar.dls.repository.action.form;

import edu.ucar.dls.propertiesmgr.*;
import edu.ucar.dls.repository.*;
import edu.ucar.dls.webapps.tools.*;
import edu.ucar.dls.util.*;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.io.*;
import java.text.*;

import java.net.URL;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.dom4j.*;

/**
 *  This class uses the getter methods of the ProviderBean and then adds setter methods for editable fields.
 *
 * @author     John Weatherley
 * @version    $Id: RepositoryAdminForm.java,v 1.28 2010/12/18 00:04:14 jweather Exp $
 */
public final class RepositoryAdminForm extends RepositoryForm implements Serializable {
	private String message = null;
	private String xmlError = null;
	private String currentAdminEmail = null;
	private String currentDescription = null;
	private String repositoryName = null;
	private String repositoryIdentifier = null;
	private String currentSetSpec = null;
	private String currentSetName = null;
	private String currentSetDirectory = null;
	private String currentSetDescription = null;
	private String currentSetFormat = null;
	private String trustedWsIps = null;
	private String add = "";
	private String numIdentifiersResults = null;
	private String numRecordsResults = null;
	private ArrayList validCollectionKeys = null;
	private ArrayList validMetadataFormats = null;
	private String updateFrequency = "-1";
	private String numIndexingErrors = "-1";
	private String collectionRecordsLocation = null;
	private File metadataRecordsLocation = null;
	private Date indexingStartTimeDate = null;
	private int[] indexingDaysOfWeek = null;
	private String drcBoostFactor = "", titleBoostFactor = "", stemmingBoostFactor = "", multiDocBoostFactor = "";
	private String stemmingEnabled = "false";

	private String command = null;


	/**
	 *  Gets the command attribute of the RepositoryAdminForm object
	 *
	 * @return    The command value
	 */
	public String getCommand() {
		return command;
	}


	/**
	 *  Sets the command attribute of the RepositoryAdminForm object
	 *
	 * @param  command  The new command value
	 */
	public void setCommand(String command) {
		this.command = command;
	}



	/**
	 *  Gets the trustedWsIps attribute of the RepositoryAdminForm object
	 *
	 * @return    The trustedWsIps value
	 */
	public String getTrustedWsIps() {
		return trustedWsIps;
	}


	/**
	 *  Sets the trustedWsIps attribute of the RepositoryAdminForm object
	 *
	 * @param  val  The new trustedWsIps value
	 */
	public void setTrustedWsIps(String val) {
		trustedWsIps = val;
	}


	/**
	 *  Gets the numIdentifiersResults attribute of the RepositoryAdminForm object
	 *
	 * @return    The numIdentifiersResults value
	 */
	public String getNumIdentifiersResults() {
		if (numIdentifiersResults == null)
			return "value not initialized";
		else
			return numIdentifiersResults;
	}


	/**
	 *  Sets the numIdentifiersResults attribute of the RepositoryAdminForm object
	 *
	 * @param  numResults  The new numIdentifiersResults value
	 */
	public void setNumIdentifiersResults(String numResults) {
		numIdentifiersResults = numResults;
	}



	/**
	 *  Gets the numRecordsResults attribute of the RepositoryAdminForm object
	 *
	 * @return    The numRecordsResults value
	 */
	public String getNumRecordsResults() {
		if (numRecordsResults == null)
			return "value not initialized";
		else
			return numRecordsResults;
	}


	/**
	 *  Sets the numRecordsResults attribute of the RepositoryAdminForm object
	 *
	 * @param  numResults  The new numRecordsResults value
	 */
	public void setNumRecordsResults(String numResults) {
		numRecordsResults = numResults;
	}



	/**
	 *  Gets the frequency by which the index is updated to reflect changes that occur in the metadata files. A
	 *  return of 0 indicates no automatic updating occurs.
	 *
	 * @return    The updateFrequency, in minutes.
	 */
	public String getUpdateFrequency() {
		return updateFrequency;
	}


	/**
	 *  Sets the frequency by which the index is updated to reflect changes that occur in the metadata files. A
	 *  return of 0 indicates no automatic updating occurs.
	 *
	 * @param  frequency  The new updateFrequency, in minutes.
	 */
	public void setUpdateFrequency(int frequency) {
		updateFrequency = Integer.toString(frequency);
	}


	/**
	 *  Gets the date and time the indexer is/was scheduled to start, for example 'Dec 2, 2003 1:35 AM MST'.
	 *
	 * @return    The date and time the indexer is scheduled to start, or empty String if not available.
	 */
	public String getIndexingStartDate() {
		if (indexingStartTimeDate == null)
			return "";
		try {
			return Utils.convertDateToString(indexingStartTimeDate, "EEE, MMM d, yyyy");
		} catch (ParseException e) {
			return "";
		}
	}


	/**
	 *  Gets the time of day the indexer isscheduled to start, for example '1:35 AM MST'.
	 *
	 * @return    the time of day the indexer isscheduled to start, or empty String if not available.
	 */
	public String getIndexingTimeOfDay() {
		if (indexingStartTimeDate == null)
			return "";
		try {
			return Utils.convertDateToString(indexingStartTimeDate, "h:mm a zzz");
		} catch (ParseException e) {
			return "";
		}
	}



	/**
	 *  Sets the date and time the indexer is/was scheduled to start.
	 *
	 * @param  indexingStartTime  The new indexingStartTime value
	 */
	public void setIndexingStartTime(Date indexingStartTime) {
		this.indexingStartTimeDate = indexingStartTime;
	}


	/**
	 *  Gets the days of the week the indexer will run, as a String.
	 *
	 * @return    The indexingDaysOfWeek value
	 */
	public String getIndexingDaysOfWeek() {
		if (indexingDaysOfWeek == null)
			return "all days";
		else {
			String daysOfWeekMsg = "";
			for (int i = 0; i < indexingDaysOfWeek.length; i++)
				daysOfWeekMsg += Utils.getDayOfWeekString(indexingDaysOfWeek[i]) + (i == indexingDaysOfWeek.length - 1 ? "s" : "s, ");
			return daysOfWeekMsg;
		}
	}


	/**
	 *  Sets the idexingDaysOfWeek as an array of Calendar.DAY_OF_WEEK fields, or null if none.
	 *
	 * @param  daysOfWeek  The new idexingDaysOfWeek value
	 */
	public void setIdexingDaysOfWeek(int[] daysOfWeek) {
		this.indexingDaysOfWeek = daysOfWeek;
	}


	/**
	 *  Sets the number of indexing errors that are present.
	 *
	 * @param  numErrors  The number of indexing errors that are present.
	 */
	public void setNumIndexingErrors(int numErrors) {
		numIndexingErrors = Integer.toString(numErrors);
	}


	/**
	 *  Gets the number of indexing errors that are present. A value of -1 means no data is available.
	 *
	 * @return    numErrors The number of indexing errors that are present.
	 */
	public String getNumIndexingErrors() {
		return numIndexingErrors;
	}


	/**
	 *  Gets the removeInvalidRecords attribute of the RepositoryAdminForm object
	 *
	 * @return    The removeInvalidRecords value
	 */
	public String getRemoveInvalidRecords() {
		RepositoryManager rm =
			(RepositoryManager) getServlet().getServletContext().getAttribute("repositoryManager");
		if (rm == null)
			return "";

		return rm.getRemoveInvalidRecords();
	}


	/**
	 *  Gets the validateRecords attribute of the RepositoryAdminForm object
	 *
	 * @return    The validateRecords value
	 */
	public String getValidateRecords() {
		RepositoryManager rm =
			(RepositoryManager) getServlet().getServletContext().getAttribute("repositoryManager");
		if (rm == null)
			return "";

		return rm.getValidateRecords();
	}


	/**
	 *  Sets the repositoryName attribute of the RepositoryAdminForm object
	 *
	 * @param  name  The new repositoryName value
	 */
	public void setRepositoryName(String name) {
		repositoryName = name.trim();
	}


	/**
	 *  Sets the repositoryIdentifier attribute of the RepositoryAdminForm object
	 *
	 * @param  value  The new repositoryIdentifier value
	 */
	public void setRepositoryIdentifier(String value) {
		if (value != null)
			repositoryIdentifier = value.trim();
	}


	/**
	 *  Gets the repositoryIdentifier attribute of the RepositoryAdminForm object
	 *
	 * @return    The repositoryIdentifier value
	 */
	public String getRepositoryIdentifier() {
		return repositoryIdentifier;
	}


	/**
	 *  Sets the currentAdminEmail attribute of the RepositoryAdminForm object
	 *
	 * @param  value  The new currentAdminEmail value
	 */
	public void setCurrentAdminEmail(String value) {
		prtln("setCurrentAdminEmail( " + value + " )");
		currentAdminEmail = value;
	}


	/**
	 *  Gets the currentAdminEmail attribute of the RepositoryAdminForm object
	 *
	 * @return    The currentAdminEmail value
	 */
	public String getCurrentAdminEmail() {
		prtln("getCurrentAdminEmail( ) returning: " + currentAdminEmail);
		return currentAdminEmail;
	}


	/**
	 *  Gets the exampleId attribute of the RepositoryAdminForm object
	 *
	 * @return    The exampleId value
	 */
	public String getExampleId() {
		RepositoryManager rm =
			(RepositoryManager) getServlet().getServletContext().getAttribute("repositoryManager");
		if (rm == null)
			return "";

		return rm.getExampleID();
	}


	/**
	 *  Sets the currentDescription attribute of the RepositoryAdminForm object
	 *
	 * @param  value  The new currentDescription value
	 */
	public void setCurrentDescription(String value) {
		prtln("setDescription( " + value + " )");
		currentDescription = value;
	}


	/**
	 *  Gets the currentDescription attribute of the RepositoryAdminForm object
	 *
	 * @return    The currentDescription value
	 */
	public String getCurrentDescription() {
		prtln("getDescription( ) returning: " + currentDescription);
		return currentDescription;
	}



	/**
	 *  Sets the add attribute of the RepositoryAdminForm object
	 *
	 * @param  value  The new add value
	 */
	public void setAdd(String value) {
		this.add = value;
	}


	/**
	 *  Gets the add attribute of the RepositoryAdminForm object
	 *
	 * @return    The add value
	 */
	public String getAdd() {
		return add;
	}


	/**
	 *  Sets the absolute path to the metadataRecordsLocation.
	 *
	 * @param  metadataRecordsLocation  The absolute path to a directory containing item-level metadata. All
	 *      metadata files must reside in sub-directores by format and collection, for example:
	 *      metadataRecordsLocation + /adn/dcc/DLESE-000-000-000-001.xml.
	 */
	public void setMetadataRecordsLocation(String metadataRecordsLocation) {
		try {
			this.metadataRecordsLocation = new File(metadataRecordsLocation);
		} catch (Throwable t) {}
	}


	/**
	 *  Sets the absolute path to the collectionRecordsLocation.
	 *
	 * @param  collectionRecordsLocation  The absolute path to a directory of DLESE collection-level XML records.
	 */
	public void setCollectionRecordsLocation(String collectionRecordsLocation) {
		this.collectionRecordsLocation = collectionRecordsLocation;
	}


	/**
	 *  Gets the path for the directory of collect-level records the RepositoryManager is using, or empty string
	 *  if none is configured.
	 *
	 * @return    The collectionRecordsLocation value
	 */
	public String getCollectionRecordsLocation() {
		if (collectionRecordsLocation != null)
			return collectionRecordsLocation;
		else
			return "";
	}


	/**
	 *  Gets the path for the directory of metadata records the RepositoryManager is using, or empty string if
	 *  none is configured.
	 *
	 * @return    The metadataRecordsLocation value
	 */
	public String getMetadataRecordsLocation() {
		if (metadataRecordsLocation != null)
			return metadataRecordsLocation.getAbsolutePath();
		else
			return "";
	}


	/**
	 *  Gets the path to the RepositoryManager config directory.
	 *
	 * @return    The RepositoryManager config dir.
	 */
	public String getConfigDirLocation() {
		try {
			RepositoryManager rm = (RepositoryManager) getServlet().getServletContext().getAttribute("repositoryManager");
			if (rm != null)
				return rm.getConfigDir().getAbsolutePath();
		} catch (Throwable e) {
			prtlnErr("getConfigDirLocation(): " + e);
		}
		return "";
	}


	/**
	 *  Get the directory where the repository persistent data and certain configs resides, including the
	 *  collections configs, specified by the init param repositoryData.
	 *
	 * @return    The directory where the repository data and certain configs resides.
	 */
	public String getRepositoryDataDir() {
		try {
			RepositoryManager rm = (RepositoryManager) getServlet().getServletContext().getAttribute("repositoryManager");
			if (rm != null)
				return rm.getRepositoryDataDir().getAbsolutePath();
		} catch (Throwable e) {
			prtlnErr("getRepositoryDataDir(): " + e);
		}
		return "";
	}


	/**
	 *  Sets the currentSetDescription attribute of the RepositoryAdminForm object
	 *
	 * @param  value  The new currentSetDescription value
	 */
	public void setCurrentSetDescription(String value) {
		prtln("setCurrentSetDescription( " + value + " )");
		currentSetDescription = value;
	}


	/**
	 *  Gets the currentSetDescription attribute of the RepositoryAdminForm object
	 *
	 * @return    The currentSetDescription value
	 */
	public String getCurrentSetDescription() {
		prtln("getCurrentSetDescription( ) returning: " + currentSetDescription);
		return currentSetDescription;
	}



	/**
	 *  Sets the currentSetName attribute of the RepositoryAdminForm object
	 *
	 * @param  value  The new currentSetName value
	 */
	public void setCurrentSetName(String value) {
		prtln("setCurrentSetName( " + value + " )");
		currentSetName = value;
	}


	/**
	 *  Gets the currentSetName attribute of the RepositoryAdminForm object
	 *
	 * @return    The currentSetName value
	 */
	public String getCurrentSetName() {
		prtln("getCurrentSetName( ) returning: " + currentSetName);
		return currentSetName;
	}


	/**
	 *  Sets the currentSetSpec attribute of the RepositoryAdminForm object
	 *
	 * @param  value  The new currentSetSpec value
	 */
	public void setCurrentSetSpec(String value) {
		prtln("setCurrentSetSpec( " + value + " )");
		currentSetSpec = value;
	}


	/**
	 *  Gets the currentSetSpec attribute of the RepositoryAdminForm object
	 *
	 * @return    The currentSetSpec value
	 */
	public String getCurrentSetSpec() {
		prtln("getCurrentSetSpec( ) returning: " + currentSetSpec);
		return currentSetSpec;
	}


	/**
	 *  Sets the currentSetDirectory attribute of the RepositoryAdminForm object
	 *
	 * @param  value  The new currentSetDirectory value
	 */
	public void setCurrentSetDirectory(String value) {
		currentSetDirectory = value;
	}


	/**
	 *  Gets the currentSetDirectory attribute of the RepositoryAdminForm object
	 *
	 * @return    The currentSetDirectory value
	 */
	public String getCurrentSetDirectory() {
		return currentSetDirectory;
	}


	/**
	 *  Gets the sortSetsBy attribute of the RepositoryAdminForm object
	 *
	 * @return    The sortSetsBy value
	 */
	public String getSortSetsBy() {
		return sortSetsBy;
	}


	private String sortSetsBy = "collection";


	/**
	 *  Sets the sortSetsBy attribute of the RepositoryAdminForm object
	 *
	 * @param  sortSetsBy  The new sortSetsBy value
	 */
	public void setSortSetsBy(String sortSetsBy) {
		this.sortSetsBy = sortSetsBy;
	}


	/**
	 *  Gets the sets configured in the RepositoryManager. Overloaded method from RepositoryForm.
	 *
	 * @return    The sets value
	 */
	public ArrayList getSets() {
		if (sets == null)
			return new ArrayList();
		return sets;
	}


	private ArrayList sets = null;


	/**
	 *  Sets the sets attribute of the RepositoryAdminForm object
	 *
	 * @param  sets  The new sets value
	 */
	public void setSets(ArrayList sets) {
		RepositoryManager rm =
			(RepositoryManager) getServlet().getServletContext().getAttribute("repositoryManager");
		if (rm == null) {
			sets = new ArrayList();
			return;
		}

		try {
			if (sets != null) {
				this.sets = sets;
				for (int i = 0; i < sets.size(); i++)
					((SetInfo) sets.get(i)).setSetInfoData(rm);

				String sortBy = getSortSetsBy();
				if (sortBy.equals("collection"))
					Collections.sort(sets);
				else
					Collections.sort(sets, SetInfo.getComparator(sortBy));
			}
		} catch (Throwable e) {
			prtlnErr("getSets() error: " + e);
			sets = new ArrayList();
		}
	}


	/**
	 *  Sets the currentSetFormat attribute of the RepositoryAdminForm object
	 *
	 * @param  value  The new currentSetFormat value
	 */
	public void setCurrentSetFormat(String value) {
		prtln("setCurrentSetFormat( " + value + " )");
		currentSetFormat = value;
	}


	/**
	 *  Gets the currentSetFormat attribute of the RepositoryAdminForm object
	 *
	 * @return    The currentSetFormat value
	 */
	public String getCurrentSetFormat() {
		prtln("getCurrentSetFormat( ) returning: " + currentSetFormat);
		return currentSetFormat;
	}


	/**
	 *  Sets the boosting factor used to rank items in the DRC. Value must be zero or greater.
	 *
	 * @param  boostFactor  The new boosting factor used to rank items in the DRC.
	 */
	public void setDrcBoostFactor(String boostFactor) {
		drcBoostFactor = boostFactor;

	}


	/**
	 *  Sets the boosting factor used to rank resources that have multiple records.
	 *
	 * @param  boostFactor  The boosting factor used to rank resources that have multiple records.
	 */
	public void setMultiDocBoostFactor(String boostFactor) {
		multiDocBoostFactor = boostFactor;

	}


	/**
	 *  Sets the boosting factor used to rank items with matching terms in the title field. Value must be zero or
	 *  greater.
	 *
	 * @param  boostFactor  The boosting factor used to rank items with matching terms in the title field.
	 */
	public void setTitleBoostFactor(String boostFactor) {
		titleBoostFactor = boostFactor;

	}


	/**
	 *  Sets whether stemming support is enabled.
	 *
	 * @param  stemmingEnabled  The new stemmingEnabled value
	 */
	public void setStemmingEnabled(String stemmingEnabled) {
		this.stemmingEnabled = stemmingEnabled;
	}



	/**
	 *  Sets the boosting factor used to rank items with matching stemmed terms. Value must be zero or greater.
	 *
	 * @param  boostFactor  The boosting factor used to rank items with matching stemmed terms.
	 */
	public void setStemmingBoostFactor(String boostFactor) {
		stemmingBoostFactor = boostFactor;

	}


	/**
	 *  Gets the boosting factor used to rank items with matching stemmed terms.
	 *
	 * @return    The boosting factor used to rank items with matching stemmed terms.
	 */
	public String getStemmingBoostFactor() {
		return stemmingBoostFactor;
	}



	/**
	 *  Gets the boosting factor used to rank items in the DRC.
	 *
	 * @return    The boosting factor used to rank items in the DRC.
	 */
	public String getDrcBoostFactor() {
		return drcBoostFactor;
	}


	/**
	 *  Gets the boosting factor used to rank resources that have multiple records.
	 *
	 * @return    The boosting factor used to rank resources that have multiple records.
	 */
	public String getMultiDocBoostFactor() {
		return multiDocBoostFactor;
	}


	/**
	 *  Gets the titleBoostFactor attribute of the RepositoryAdminForm object
	 *
	 * @return    The titleBoostFactor value
	 */
	public String getTitleBoostFactor() {
		return titleBoostFactor;
	}


	/**
	 *  Indicates whether stemming support is enabled.
	 *
	 * @return    true if stemming is enabled, false otherwise.
	 */
	public String getStemmingEnabled() {
		return stemmingEnabled;
	}



	/**  Constructor for the RepositoryAdminForm Bean object */
	public RepositoryAdminForm() { }


	/**
	 *  Sets the message attribute of the RepositoryAdminForm object
	 *
	 * @param  message  The new message value
	 */
	public void setMessage(String message) {
		this.message = message;
	}


	/**
	 *  Gets the message attribute of the RepositoryAdminForm object
	 *
	 * @return    The message value
	 */
	public String getMessage() {
		return message;
	}


	/**
	 *  Sets the xmlError attribute of the RepositoryAdminForm object
	 *
	 * @param  xmlError  The new xmlError value
	 */
	public void setXmlError(String xmlError) {
		//prtln("setXmlError( "+xmlError+" )");
		this.xmlError = xmlError;
	}


	/**
	 *  Gets the xmlError attribute of the RepositoryAdminForm object
	 *
	 * @return    The xmlError value
	 */
	public String getXmlError() {
		return xmlError;
	}


	/**
	 *  Grabs the base directory where collections metadata files are located, or null if not configured.
	 *
	 * @return    Base directory where the collections reside.
	 */
	public String getCollectionsBaseDir() {
		String collectionsBaseDir =
			getServlet().getServletContext().getInitParameter("collBaseDir");

		if (collectionsBaseDir == null || collectionsBaseDir.equalsIgnoreCase("null"))
			collectionsBaseDir = null;
		return collectionsBaseDir;
	}


	/**
	 *  Grabs the collection keys from the DPC keys schema.
	 *
	 * @return    A list of valid colleciton keys.
	 */
	public ArrayList getValidCollectionKeys() {
		String collectionKeySchemaUrl =
			getServlet().getServletContext().getInitParameter("collectionKeySchemaUrl");
		if (collectionKeySchemaUrl == null)
			return null;

		if (validCollectionKeys == null) {
			try {
				validCollectionKeys = new ArrayList();
				SAXReader reader = new SAXReader();
				Document document = reader.read(new URL(collectionKeySchemaUrl));
				validCollectionKeys.add("-- SELECT COLLECTION KEY --");
				List nodes = document.selectNodes("//xsd:simpleType[@name='keyType']/xsd:restriction/xsd:enumeration");
				for (Iterator iter = nodes.iterator(); iter.hasNext(); ) {
					Node node = (Node) iter.next();
					validCollectionKeys.add(node.valueOf("@value"));
				}
			} catch (Throwable e) {
				prtlnErr("Error getCollectionKeys(): " + e);
				validCollectionKeys = null;
			}
		}
		return validCollectionKeys;
	}


	/**
	 *  Grabs the valid metadata formats from the DPC schema.
	 *
	 * @return    A list of valid metadata formats.
	 */
	public ArrayList getValidMetadataFormats() {
		String metadataFormatSchemaUrl =
			getServlet().getServletContext().getInitParameter("metadataFormatSchemaUrl");
		if (metadataFormatSchemaUrl == null)
			return null;

		if (validMetadataFormats == null) {
			try {
				validMetadataFormats = new ArrayList();
				SAXReader reader = new SAXReader();
				Document document = reader.read(new URL(metadataFormatSchemaUrl));
				validMetadataFormats.add("-- SELECT FORMAT --");
				List nodes = document.selectNodes("//xsd:simpleType[@name='itemFormatType']/xsd:restriction/xsd:enumeration");
				for (Iterator iter = nodes.iterator(); iter.hasNext(); ) {
					Node node = (Node) iter.next();
					validMetadataFormats.add(node.valueOf("@value"));
				}
			} catch (Throwable e) {
				prtlnErr("Error getValidMetadataFormats(): " + e);
				validMetadataFormats = null;
			}
		}
		return validMetadataFormats;
	}


	/**
	 *  Reset bean properties to their default state, as needed.
	 *
	 * @param  mapping  The ActionMapping being used
	 * @param  request  The HttpServletRequest
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		//prtln("--reset()");
		command = null;
	}

	// ************************ Validation methods ***************************

	/**
	 *  Validate the input. This method is called AFTER the setter method is called.
	 *
	 * @param  mapping  The ActionMapping used.
	 * @param  request  The HttpServletRequest for this request.
	 * @return          An ActionError containin any errors that had occured.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);
		if (errors == null)
			errors = new ActionErrors();
		//prtln("validate()");
		try {
			// Validate imput for the commands
			if (command != null) {
				// Nothing here yet...
			}

			if (currentSetSpec != null && currentSetSpec.matches(".*--.*"))
				errors.add("currentSetSpec", new ActionError("generic.message", "You must select a collection key."));

			if (currentSetFormat != null && currentSetFormat.matches(".*--.*"))
				errors.add("currentSetFormat", new ActionError("generic.message", "You must select a metadata format."));

			/* if (currentAdminEmail != null && !emailIsValid(currentAdminEmail))
				errors.add("currentAdminEmail", new ActionError("errors.adminEmail"));
			if (currentSetDirectory != null && currentSetDirectory.length() != 0 &&
				request.getParameter("remove") == null &&
				!(new File(currentSetDirectory.trim()).isDirectory()))
				errors.add("setDirectory", new ActionError("errors.setDirectory"));
			if (repositoryIdentifier != null &&
				repositoryIdentifier.length() != 0 &&
				!repositoryIdentifier.matches("[a-zA-Z][a-zA-Z0-9\\-]*(\\.[a-zA-Z][a-zA-Z0-9\\-]+)+"))
				errors.add("namespaceIdentifier", new ActionError("errors.namespaceIdentifier")); */
			if (numRecordsResults != null && !numRecordsResults.matches("[1-9]+[0-9]*"))
				errors.add("numResults", new ActionError("errors.numResults"));
			if (numIdentifiersResults != null && !numIdentifiersResults.matches("[1-9]+[0-9]*"))
				errors.add("numResults", new ActionError("errors.numResults"));
		} catch (NullPointerException e) {
			prtln("validate() error: " + e);
			e.printStackTrace();
		} catch (Throwable e) {
			prtln("validate() error: " + e);
		}

		if (!errors.isEmpty())
			prtln("RepositoryAdminForm.validate() returning errors... ");

		return errors;
		/*
		 *  if (currentSetSpec != null || add.equals("t") && !setSpecIsValid(currentSetSpec))
		 *  errors.add("currentSetSpec", new ActionError("errors.setSpec"));
		 *  if (currentSetName != null || add.equals("t") && currentSetName.length() == 0 )
		 *  errors.add("currentSetName", new ActionError("errors.setName"));
		 */
	}


	/**
	 *  Validates the format of an e-mail address.
	 *
	 * @param  email  The e-mail address to validate.
	 * @return        True iff this e-mail has a valid format.
	 */
	private final boolean emailIsValid(String email) {
		if (email == null || email.trim().length() == 0)
			return true;
		return FormValidationTools.isValidEmail(email);
	}
}


