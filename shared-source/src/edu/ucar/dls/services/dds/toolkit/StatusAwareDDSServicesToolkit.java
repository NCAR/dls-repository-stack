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
package edu.ucar.dls.services.dds.toolkit;


import edu.ucar.dls.xml.Dom4jUtils;
import java.io.IOException;
import javax.servlet.*;

import org.dom4j.*;

import java.net.*;
import java.util.*;
import java.text.*;

/**
Extends DDSServicesToolkit to interact with a DCS repository, which supplies
status-oriented metadata (aka dcs_data). The additional fields allow us to 
search for schema-valid records having specified status values (e.g., "Done",
"In Progress"). The available statuses are those configured in the DCS for
a particular collection. "Done" is always available and is the default for
this toolkit.

This Toolkit is currently for read-only operations. Write webservices are available
for the DCS.

This tookKit will by default only retrieve "valid" records
 *
 *  Sets http User-Agent to 'DDS Service APIs Toolkit' unless otherwise specified.
 *
 * @author    Jonathan Ostwald
 */
public class StatusAwareDDSServicesToolkit extends DDSServicesToolkit {
	private static boolean debug = false;

    // private String[] statuses = null;
	private static ServletContext servletContext = null;


	/**
	 *  Constructor for the StatusAwareDDSServicesToolkit that normalizes the baseUrl for use with both the Search API and
	 *  Update API. Accepts baseUrl of the form http://www.example.org/dds/services/,
	 *  http://www.example.org/dds/services/ddsws1-1, http://www.example.org/dds/services/ddsupdatews1-1, or
	 *  http://www.example.org/dds-search.
	 *
	 * @param  ddsServicesUrl  BaseUrl for DDS services, for example http://www.example.org/dds/services/ (or
	 *      other derivative)
	 * @param  clientName      The client name, or null to send none
	 * @param  userAgent       The http User-Agent header sent with the requests, or null to use default 'DDS
	 *      Services Toolkit'
	 */
	public StatusAwareDDSServicesToolkit(String ddsServicesUrl, String clientName, String userAgent) {
		super(ddsServicesUrl, clientName, userAgent);
		
		//System.out.println("StatusAwareDDSServicesToolkit(): ddsServicesUrl:'" + ddsServicesUrl + "' clientName:'" + clientName + "' userAgent:'" + _userAgent + "'");
		// prtln("StatusAwareDDSServicesToolkit(): statuses: " + Arrays.toString(getStatuses()));
	}
	
	/**
	 *  Sets the ServletContext to make it available to this plugin during the indexing
	 *  process.
	 *
	 * @param  context  The ServletContext
	 */
	public static void setServletContext(ServletContext context) {
		servletContext = context;
	}
	
	private static String[] getStatuses () {
		List<String> statusList = new ArrayList<String>();
		if (servletContext != null) {
			String statusStr = (String)servletContext.getAttribute("requiredCurriculaRecordStatus");
			if (statusStr != null) {
				String[] splits = statusStr.split("\\,");
				for (int i=0;i<splits.length;i++) {
					String status = splits[i].trim();
					if (status.length() > 0)
						statusList.add(status);
				}
			}
		}
		
		return statusList.toArray(new String[0]);
	}
	
	private static String[] getStatusesOLD (String statusStr) {
		prtln ("hello world");
		List<String> statusList = new ArrayList<String>();
		if (statusStr != null) {
			String[] splits = statusStr.split("\\,");
			for (int i=0;i<splits.length;i++) {
				String status = splits[i].trim();
				if (status.length() > 0)
					statusList.add(status);
			}
		}
		
		return statusList.toArray(new String[0]);
	}
	
	public static void main (String[] args) throws Exception {
		prtln ("main");
/* 		List<String> statuses = getStatusList ("fooberry, Done, wakadoo");
		for (String status: statuses)
			prtln (" - " + status); */
			
		String[] statuses = getStatusesOLD ("fooberry, Done, wakadoo ");
		// String[] statuses = getStatuses (null);
		prtln ("there are " + statuses.length + " statuses");
		for (int i=0;i<statuses.length;i++) {
			prtln (" - \"" + statuses[i] + "\"");
		}
		prtln ("statuses: " + statuses.toString());
		prtln ("statuses: " + Arrays.toString(statuses));
	}


	// -------------- DDS Web Service Requests (DDSWS) -------------

	/**
	 *  Performs a GetRecord request from a DDSWS repository.
	 *
	 * @param  id                                    Record ID
	 * @param  showRelation                          The relation data to include in the response for example
	 *      isAnnotatedBy, or null for none
	 * @param  xmlFormat                             The XML format to return, or null for native format
	 * @param  additionalRequestParams               A Map that contains param/value pairs. Values must be of
	 *      type String to indicate a single value, or a String [] to indicate multiple values. Map may be null
	 *      for none.
	 * @param  soAllRecords                          True to search all records including non-discoverable ones
	 *      (client must be authorized by IP)
	 * @param  localizeXml                           True to localize the response
	 * @return                                       The service response
	 * @exception  DDSServiceErrorResponseException  Standard service error if one was returned
	 * @exception  Exception                         If other error occurs
	 */
	public DDSServicesResponse getRecord(String id, String showRelation, String xmlFormat, Map additionalRequestParams, boolean soAllRecords, boolean localizeXml) throws DDSServiceErrorResponseException, Exception {

		String serviceRequest = ddswsBaseUrl +
			"?verb=GetRecord" +
			"&id=" + URLEncoder.encode(id, "UTF-8");

		if (xmlFormat != null)
			serviceRequest += "&xmlFormat=" + URLEncoder.encode(xmlFormat, "UTF-8");

		if (showRelation != null)
			serviceRequest += "&relation=" + URLEncoder.encode(showRelation, "UTF-8");

		if (soAllRecords)
			serviceRequest += "&so=allRecords";

		if (clientName != null && clientName.length() > 0)
			serviceRequest += "&client=" + URLEncoder.encode(clientName, "UTF-8");

		String[] statuses = getStatuses();
		if (statuses.length > 0) {
			for (int i=0;i<statuses.length;i++)
				serviceRequest += "&dcsStatus=" + URLEncoder.encode(statuses[i], "UTF-8");
			serviceRequest += "&storedContent=" + URLEncoder.encode("dcsstatusLabel", "UTF-8");
			serviceRequest += "&storedContent=" + URLEncoder.encode("dcsisValid", "UTF-8");
		}
		
		// Add any additional request parameters:
		serviceRequest += getQueryStringFromMap(additionalRequestParams);

		prtln("GetRecord serviceRequest: " + serviceRequest);


		String uniqueKey = serviceRequest + " localized:"+localizeXml;

		Document ddsResponseDocument = fetchDocumentFromCache(uniqueKey);

		if(ddsResponseDocument == null){
            // Perfrom the GetRecord request:
            int num_tries = 3;
            IOException ioEx = null;
            // DDS GetRecord request can return IOException message 'Invalid HTTP response code: 500' if, for example, the DDS index is stale from a recent update.
            // Often another try will be successful:
            for(int i = 0; i < num_tries; i++) {
                ioEx = null;
                try {
                    ddsResponseDocument = Dom4jUtils.getXmlDocument(new URL(serviceRequest), 60000, _userAgent);
                } catch (IOException ioe) {
                    ioEx = ioe;
                }
                if(ioEx == null)
                    break;
            }
            if(ioEx != null) {
                prtln ("THROWING");
                throw ioEx;
            }

            if (localizeXml)
                ddsResponseDocument = Dom4jUtils.localizeXml(ddsResponseDocument);
            //prtln("GetRecord response:\n" + ddsResponseDocument.asXML());

            // Check for service error code response:
            String[] errorResponse = checkForErrorResponseDDSWS(ddsResponseDocument);
            if (errorResponse != null)
                throw new DDSServiceErrorResponseException(errorResponse[0], errorResponse[1], "GetRecord");

            saveDocumentToCache(uniqueKey,ddsResponseDocument);
		}
		return new DDSServicesResponse(serviceRequest, ddsResponseDocument);
	}


	private String getQueryStringFromMap(Map additionalRequestParams) throws Exception {
		String queryString = "";
		if (additionalRequestParams != null) {
			Iterator keyIterator = additionalRequestParams.keySet().iterator();
			while (keyIterator.hasNext()) {
				Object key = keyIterator.next();
				if (!(key instanceof String))
					throw new Exception("Key for additionalRequestParams Map must be of type String");
				Object val = additionalRequestParams.get(key);
				if (val instanceof String)
					queryString += "&" + URLEncoder.encode((String) key, "UTF-8") + "=" + URLEncoder.encode((String) val, "UTF-8");
				else if (val instanceof String[]) {
					String[] values = (String[]) val;
					for (int i = 0; i < values.length; i++)
						queryString += "&" + URLEncoder.encode((String) key, "UTF-8") + "=" + URLEncoder.encode(values[i], "UTF-8");
				}
				else
					throw new Exception("Value for additionalRequestParams Map must be of type String or String []");
			}
		}
		return queryString;
	}


	/**
	 *  Performs a Search request from a DDSWS repository. Uses the POST method to send the arguments to
	 *  accommodate very long queries/requests.
	 *
	 * @param  query                                 The search query, or null
	 * @param  xmlFormat                             The xmlFormat for which results must be dissiminated, or
	 *      null for any/all
	 * @param  startOffset                           Starting offset in the returns
	 * @param  numReturns                            Number of records to return
	 * @param  showRelation                          The relation data to include in the response for example
	 *      isAnnotatedBy, or null for none
	 * @param  additionalRequestParams               A Map that contains param/value pairs. Values must be of
	 *      type String to indicate a single value, or a String [] to indicate multiple values. Map may be null
	 *      for none.
	 * @param  soAllRecords                          True to search all records including non-discoverable ones
	 *      (client must be authorized by IP)
	 * @param  localizeXml                           True to localize the response
	 * @param  sortByField                           Indicates the search field to sort the results by, or null
	 *      for none
	 * @param  sortOrder                             The sort order to apply (ascending, descending). Ignored if
	 *      no sortByField has been indicated
	 * @return                                       The service response
	 * @exception  DDSServiceErrorResponseException  Standard service error if one was returned
	 * @exception  Exception                         If other error occurs
	 */
	public DDSServicesResponse search(String query,
	                                  String xmlFormat,
	                                  int startOffset,
	                                  int numReturns,
	                                  String sortByField,
	                                  int sortOrder,
	                                  String showRelation,
	                                  Map additionalRequestParams,
	                                  boolean soAllRecords,
	                                  boolean localizeXml) throws DDSServiceErrorResponseException, Exception {

		String serviceRequestArgs =
			"verb=Search" +
			"&n=" + numReturns +
			"&s=" + startOffset;

		String queryStr = null;
		/* 
			QUESTION: should we ALWAYS require that dcsvalid is true? this means this bean
			will only work with DCS repos. 
		*/
		String[] statuses = getStatuses();
		if (statuses.length > 0) {
			queryStr = "dcsisValid:true";
			if (query != null)
				queryStr += " AND " + URLEncoder.encode(query, "UTF-8");
		}
		else if (query != null)
				queryStr = URLEncoder.encode(query, "UTF-8");
		
		if (queryStr != null)
			serviceRequestArgs += "&q=" + queryStr;

		if (xmlFormat != null)
			serviceRequestArgs += "&xmlFormat=" + URLEncoder.encode(xmlFormat, "UTF-8");

		// Apply a sort order, if requested:
		if (sortByField != null && sortByField.trim().length() > 0 && sortOrder != this.SORT_ORDER_NO_SORT) {
			String sortArg = null;
			if (sortOrder == this.SORT_ORDER_ASCENDING)
				sortArg = "sortAscendingBy";
			else if (sortOrder == this.SORT_ORDER_DESCENDING)
				sortArg = "sortDescendingBy";
			else
				throw new IllegalArgumentException("Illegal value for sortOrder: " + sortOrder);

			serviceRequestArgs += "&" + sortArg + "=" + URLEncoder.encode(sortByField, "UTF-8");
		}

		if (showRelation != null)
			serviceRequestArgs += "&relation=" + URLEncoder.encode(showRelation, "UTF-8");

		if (soAllRecords)
			serviceRequestArgs += "&so=allRecords";

		if (clientName != null && clientName.length() > 0)
			serviceRequestArgs += "&client=" + URLEncoder.encode(clientName, "UTF-8");
		
		if (statuses.length > 0) {
			for (int i=0;i<statuses.length;i++)
				serviceRequestArgs += "&dcsStatus=" + URLEncoder.encode(statuses[i], "UTF-8");
			serviceRequestArgs += "&storedContent=" + URLEncoder.encode("dcsstatusLabel", "UTF-8");
			serviceRequestArgs += "&storedContent=" + URLEncoder.encode("dcsisValid", "UTF-8");
		}
		
		// Add any additional request parameters:
		serviceRequestArgs += getQueryStringFromMap(additionalRequestParams);


        String cachedObjectKey = serviceRequestArgs + " localized:"+localizeXml;
        Document ddsResponseDocument = fetchDocumentFromCache(cachedObjectKey);
        if(ddsResponseDocument == null) {


            // prtln("Search serviceRequestArgs: " + serviceRequestArgs);

            // Send the search request using POST method (pass arg string in separately):
            int num_tries = 3;

            IOException ioEx = null;
            // DDS Search request can return IOException message 'Invalid HTTP response code: 500' if, for example, the DDS index is stale from a recent update.
            // Often another try with the same search will be successful:
            for (int i = 0; i < num_tries; i++) {
                ioEx = null;
                try {
                    ddsResponseDocument = Dom4jUtils.getXmlDocument(ddswsBaseUrl, serviceRequestArgs, "UTF-8", 60000, localizeXml, _userAgent);
                } catch (IOException ioe) {
                    ioEx = ioe;
                }
                if (ioEx == null)
                    break;
            }
            if (ioEx != null)
                throw ioEx;

            //Document ddsResponseDocument = Dom4jUtils.getXmlDocument(new URL(serviceRequest), 60000, _userAgent);
            //if (localizeXml)
            //ddsResponseDocument = Dom4jUtils.localizeXml(ddsResponseDocument);
            // prtln("Search response:\n" + ddsResponseDocument.asXML());

            // Check for service error code response:
            String[] errorResponse = checkForErrorResponseDDSWS(ddsResponseDocument);
            if (errorResponse != null) {
                // prtln (Dom4jUtils.prettyPrint (ddsResponseDocument));
                throw new DDSServiceErrorResponseException(errorResponse[0], errorResponse[1], "Search");
            }

            saveDocumentToCache(cachedObjectKey,ddsResponseDocument);
        }
		return new DDSServicesResponse(ddswsBaseUrl + "?" + serviceRequestArgs, ddsResponseDocument);
	}

	/**
	 *  Determines whether the given collection is in this DDS repository matching the given collection key and
	 *  xml format.
	 *
	 * @param  collectionKey                         The collection key, for example 'dcc'
	 * @param  xmlFormat                             The xml format for this collection, for example 'adn'
	 * @return                                       True if the given collection is in the repository with the
	 *      given
	 * @exception  DDSServiceErrorResponseException  If service error
	 * @exception  Exception                         If other error
	 */
	public boolean hasCollection(String collectionKey, String xmlFormat) throws DDSServiceErrorResponseException, Exception {
		DDSServicesResponse ddsServicesResponse = null;
		try {
			String searchQuery = "(key:\"" + collectionKey + "\" AND /key//collectionRecord/access/key/@libraryFormat:\"" + xmlFormat + "\" AND xmlFormat:\"dlese_collect\")";

			ddsServicesResponse = search(
				searchQuery, null, 0, 1, null, DDSServicesToolkit.SORT_ORDER_NO_SORT, null, null, false, true);
		} catch (DDSServiceErrorResponseException de) {
			if (de.getServiceResponseCode().equals("noRecordsMatch")) {
				return false;
			}
			throw de;
		}
		Document response = ddsServicesResponse.getResponseDocument();
		if (response == null)
			return false;

		String keyFound = response.valueOf("/DDSWebService/Search/results/record/metadata/collectionRecord/access/key");
		String formatFound = response.valueOf("/DDSWebService/Search/results/record/metadata/collectionRecord/access/key/@libraryFormat");

		if (keyFound == null || formatFound == null)
			return false;
		return (keyFound.equals(collectionKey) && formatFound.equals(xmlFormat));
	}


	/**
	 *  Gets the number of records that reside in the given collection. Returns 0 or more if the collection
	 *  exists, -1 if the collection was not found in the repository.
	 *
	 * @param  collectionKey                         The collection key, for example 'dcc'
	 * @return                                       0 or more if the collection exists, -1 if the collection was
	 *      not found
	 * @exception  DDSServiceErrorResponseException  If service error
	 * @exception  Exception                         If other error
	 */
	public int getNumRecordsInCollection(String collectionKey) throws DDSServiceErrorResponseException, Exception {
		DDSServicesResponse ddsServicesResponse = null;
		try {
			String searchQuery = "(key:\"" + collectionKey + "\" AND xmlFormat:\"dlese_collect\")";

			ddsServicesResponse = search(
				searchQuery, null, 0, 1, null, DDSServicesToolkit.SORT_ORDER_NO_SORT, null, null, false, true);
		} catch (DDSServiceErrorResponseException de) {
			if (de.getServiceResponseCode().equals("noRecordsMatch")) {
				return -1;
			}
			throw de;
		}
		Document response = ddsServicesResponse.getResponseDocument();
		if (response == null)
			return -1;

		String keyFound = response.valueOf("/DDSWebService/Search/results/record/metadata/collectionRecord/access/key");
		if (keyFound == null)
			return -1;

		String numRecordsInCollection = response.valueOf("/DDSWebService/Search/results/record/head/additionalMetadata/dlese_collect/numRecordsIndexed");
		return Integer.parseInt(numRecordsInCollection);
	}


	// ---------------------- Debug info --------------------


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final static void prtlnErr(String s) {
		System.err.println(getDateStamp() + " StatusAwareDDSServicesToolkit Error: " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final static void prtln(String s) {
		if (debug) {
			// System.out.println(getDateStamp() + " StatusAwareDDSServicesToolkit: " + s);
			System.out.println(" StatusKit: " + s);
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

