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

import edu.ucar.dls.oai.OAIUtils;
import edu.ucar.dls.util.SizeLimitedLinkedHashMap;
import edu.ucar.dls.xml.Dom4jUtils;

import java.io.IOException;

import org.dom4j.*;

import java.net.*;
import java.util.*;
import java.text.*;

/**
 * Toolkit for working with DDS repository Search and Update Web Services (DDSWS and DDSUpdateWS) for read
 * and write operations. All methods use only local variables and are thread safe but are not synchronized.
 * See <a href="http://www.dlese.org/dds/services/ddsws1-1/service_specification.jsp"> DDSWS documentation
 * </a> and <a href="http://www.dlese.org/dds/services/ddsupdatews1-1/ddsupdatews_api_documentation.jsp">
 * DDSUpdateWS documentation</a> . <p>
 * <p/>
 * Response Documents for search and getRecord responses can be stored in in-memory cache. The cache is
 * automatically cleared if the remote repository version changes, which is checked every 60 seconds.
 * By default, caching is turned off. Use the static caching methods before using accessor methods to enable caching
 * for the responses. Caching is global for all Toolkits
 * <p/>
 * Sets http User-Agent to 'DDS Service APIs Toolkit' unless otherwise specified.
 *
 * @author John Weatherley
 */
public class DDSServicesToolkit {
    private static boolean debug = false;

    // Max response Document cache size for my BaseURL:
    private int MAX_CACHE_SIZE = 50;

    // Number of milliseconds between checking for a change in the index to update the cache:
    private static final int INTERVAL_TO_CHECK_FOR_UPDATING_CACHE_IN_MILISECONDS = 60 * 1000;

    protected String _userAgent = "DDS Service APIs Toolkit";

    protected String ddswsBaseUrl = null;
    private String ddsupdatewsBaseUrl = null;
    protected String clientName = null;
    private boolean _doCacheDocuments = false;

    /**
     * Indicates the optional sort order for search results should be ascending
     */
    public static int SORT_ORDER_ASCENDING = 0;

    /**
     * Indicates the optional sort order for search results should be descending
     */
    public static int SORT_ORDER_DESCENDING = 1;

    /**
     * Indicates no sorting should be applied to search results
     */
    public static int SORT_ORDER_NO_SORT = -1;


    /**
     * Constructor for the DDSServicesToolkit that normalizes the baseUrl for use with both the Search API and
     * Update API. Accepts baseUrl of the form http://www.example.org/dds/services/,
     * http://www.example.org/dds/services/ddsws1-1, http://www.example.org/dds/services/ddsupdatews1-1, or
     * URLs that end with dds-search or search-api for example
     * http://www.example.org/dds-search or http://www.example.org/search-api or http://www.example.org/repo1-search-api
     *
     * @param ddsServicesUrl BaseUrl for DDS services, for example http://www.example.org/dds/services/ (or
     *                       other derivative)
     * @param clientName     The client name, or null to send none
     * @param userAgent      The http User-Agent header sent with the requests, or null to use default 'DDS
     *                       Services Toolkit'
     */
    public DDSServicesToolkit(String ddsServicesUrl, String clientName, String userAgent) {
        // Special case for DDS Search API configured at baseUrl ending with dds-search or search-api (read-only):
        if (ddsServicesUrl.endsWith("dds-search") || ddsServicesUrl.endsWith("search-api")) {
            ddswsBaseUrl = ddsServicesUrl;
        }
        // Normalize the baseUrls for read-write operations:
        else {
            ddsServicesUrl = ddsServicesUrl.replace("ddsws1-1", "").replace("ddsupdatews1-1", "");
            if (!ddsServicesUrl.endsWith("/"))
                ddsServicesUrl += "/";

            ddswsBaseUrl = ddsServicesUrl + "ddsws1-1";
            ddsupdatewsBaseUrl = ddsServicesUrl + "ddsupdatews1-1";
        }
        this.clientName = clientName;
        if (userAgent != null)
            _userAgent = userAgent;

        //System.out.println("DDSServicesToolkit(): ddsServicesUrl:'" + ddsServicesUrl + "' clientName:'" + clientName + "' userAgent:'" + _userAgent + "'");
    }


    // ------------ Start caching framework methods ------------

    /*
     * Caching framework: Save the Search and GetRecord Documents in a static application-scope
     * in-memory cache.
     *
     * If requested, use caching for all instances that share this BaseURL,
     * otherwise fetch a new Document each time requested.
     */

    // A Map of each BaseURL that has a cache:
    private static Map<String, Map> _cachedBaseURLsMap = new SizeLimitedLinkedHashMap(20);

    // A Map to track when each remote repo has changed and the cache needs updating
    private static Map<String, Object> _indexVersionsMap = new HashMap<String, Object>();

    /**
     * Set to true to fetch search and getRecord Documents from an in-memory cache shared by all instances with this
     * same BaseURL. The cache is automatically updated at a regular interval of 60 seconds if the remote index version changes.
     *
     * @param doCacheDocuments True or false to enable/disable
     */
    public void doUseDocumentCache(boolean doCacheDocuments) {

        // For now, turn off caching altogether (disable caching features):
        //if(true)
        //  return;

        this._doCacheDocuments = doCacheDocuments;
        if (doCacheDocuments && !_cachedBaseURLsMap.containsKey(ddswsBaseUrl)) {
            _cachedBaseURLsMap.put(ddswsBaseUrl, new SizeLimitedLinkedHashMap(MAX_CACHE_SIZE));
            prtln("Enabled caching for baseUrl " + ddswsBaseUrl + " MAX_CACHE_SIZE:" + getMaxCacheSize());
        }
    }

    /**
     * If caching is enabled, clear the cache for all instances with my baseURL and start a new cache.
     * If caching is not enabled for this client, nothing is done.
     */
    public void clearDocumentCasheForMyBaseUrl() {
        if (this._doCacheDocuments && _cachedBaseURLsMap.containsKey(ddswsBaseUrl)) {
            prtln("Clearing Document cache for baseURL " + ddswsBaseUrl);
            _cachedBaseURLsMap.put(ddswsBaseUrl, new SizeLimitedLinkedHashMap(MAX_CACHE_SIZE));
        } else {
            // Do NOT clear the cache because other clients may be caching for this baseURL
            //_cachedBaseURLsMap.remove(ddswsBaseUrl);
        }
    }

    /**
     * Gets the current setting for max number of response Documents to cache for each baseURL across all DDSServicesToolkit
     * instances in the JVM.
     *
     * @return The max cache size per baseURL
     */
    public int getMaxCacheSize() {
        return MAX_CACHE_SIZE;
    }

    /**
     * Sets the max number of response Documents to cache for all instances with my baseURL (default size is 50).
     * If the number is changed from the previous value then the cache will be cleared and a new one started with the
     * new max size.
     *
     * @param maxCacheSize Sets the max number of response Documents to cache for all instances with my baseURL. Must be a value greater than 0.
     */
    public void setMaxCacheSize(int maxCacheSize) {
        if (maxCacheSize > 0 && maxCacheSize != MAX_CACHE_SIZE) {
            MAX_CACHE_SIZE = maxCacheSize;
            clearDocumentCasheForMyBaseUrl();
        }
    }

    /**
     * Fetch the cached Document if caching is enable for this instance, used for Search and GetRecord methods only.
     *
     * @param uniqueKey A unique key for each Document (unique request URI).
     * @return Cached document or null
     */
    protected Document fetchDocumentFromCache(String uniqueKey) {
        if (!_doCacheDocuments) {
            printCacheSize();
            return null;
        }

        Map<String, Object> cachedDocumentsMapForMyBaseURL = _cachedBaseURLsMap.get(ddswsBaseUrl);
        if (cachedDocumentsMapForMyBaseURL != null) {

            // Update/flush the cache if remote repo has changed:
            checkForCacheUpdate();

            // Fetch the Document from cache:
            Document fetchedDocument = (Document) cachedDocumentsMapForMyBaseURL.get(uniqueKey);
            if (fetchedDocument != null) {
                prtln("Fetched Document from cache: " + ddswsBaseUrl + "?" + uniqueKey);
                printCacheSize();
                return (Document) fetchedDocument.clone();
                //return fetchedDocument;
            } else {
                prtln("Fetched Document NOT found in cache: " + ddswsBaseUrl + "?" + uniqueKey);
                printCacheSize();
                return null;
            }
        } else {
            prtln("Fetch baseURL not in cache: " + ddswsBaseUrl);
            printCacheSize();
            return null;
        }
    }

    private void checkForCacheUpdate() {

        if (!_doCacheDocuments)
            return;

        // Check if the cache needs to be updated:
        Long currentTime = System.currentTimeMillis();
        Long indexLastCheckedTime = (Long) _indexVersionsMap.get("indexLastCheckedTime" + ddswsBaseUrl);
        if (indexLastCheckedTime == null ||
                ((currentTime - indexLastCheckedTime) > INTERVAL_TO_CHECK_FOR_UPDATING_CACHE_IN_MILISECONDS)) {

            prtln("Interval is up for baseUrl " + ddswsBaseUrl + " (currentTime:'" + currentTime + "' indexLastCheckedTime:'" + indexLastCheckedTime + "' + interval:'" + INTERVAL_TO_CHECK_FOR_UPDATING_CACHE_IN_MILISECONDS + "') Checking for change in remote index...");

            String indexLastCheckedVersion = (String) _indexVersionsMap.get("indexLastCheckedVersion" + ddswsBaseUrl);
            if (indexLastCheckedVersion == null)
                indexLastCheckedVersion = "n/a";

            String indexCurrentVersion = "";
            try {
                indexCurrentVersion = getIndexVersion();
            } catch (Throwable t) {
            }

            if (!indexCurrentVersion.equals(indexLastCheckedVersion)) {
                prtln("Index version has changed for " + ddswsBaseUrl + " indexCurrentVersion:" + indexCurrentVersion + " indexLastCheckedVersion:" + indexLastCheckedVersion);
                clearDocumentCasheForMyBaseUrl();
            } else
                prtln("Index version has not changed for " + ddswsBaseUrl);
            _indexVersionsMap.put("indexLastCheckedVersion" + ddswsBaseUrl, indexCurrentVersion);
        }
        _indexVersionsMap.put("indexLastCheckedTime" + ddswsBaseUrl, currentTime);
    }

    private void printCacheSize() {
        Map<String, Object> cachedDocumentsMapForMyBaseURL = _cachedBaseURLsMap.get(ddswsBaseUrl);
        if (cachedDocumentsMapForMyBaseURL != null) {
            prtln("Cache size for baseURL " + ddswsBaseUrl + ": " + cachedDocumentsMapForMyBaseURL.size());
        } else
            prtln("Cache size for baseURL " + ddswsBaseUrl + ": " + ": CACHE NOT ENABLED");
    }

    /**
     * Save a given Document into the cache for my baseUrl.
     *
     * @param uniqueKey A unique key for each Document (unique request URI).
     * @param document  The Document to save
     */
    protected void saveDocumentToCache(String uniqueKey, Document document) {
        if (!_doCacheDocuments)
            return;

        Map<String, Document> cachedDocumentsMap = _cachedBaseURLsMap.get(ddswsBaseUrl);
        if (cachedDocumentsMap != null && document != null) {
            cachedDocumentsMap.put(uniqueKey, document);
            prtln("Saved Document to cache: " + ddswsBaseUrl + "?" + uniqueKey);
            printCacheSize();
        }
    }

    // ------------ END caching framework methods ------------


    // -------------- DDS Web Service Requests (DDSWS) -------------

    /**
     * Performs a GetRecord request from a DDSWS repository.
     *
     * @param id                      Record ID
     * @param showRelation            The relation data to include in the response for example
     *                                isAnnotatedBy, or null for none
     * @param xmlFormat               The XML format to return, or null for native format
     * @param additionalRequestParams A Map that contains param/value pairs. Values must be of
     *                                type String to indicate a single value, or a String [] to indicate multiple values. Map may be null
     *                                for none.
     * @param soAllRecords            True to search all records including non-discoverable ones
     *                                (client must be authorized by IP)
     * @param localizeXml             True to localize the response
     * @return The service response
     * @throws DDSServiceErrorResponseException Standard service error if one was returned
     * @throws Exception                        If other error occurs
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

        // Add any additional request parameters:
        serviceRequest += getQueryStringFromMap(additionalRequestParams);

        String uniqueKey = serviceRequest + " localized:" + localizeXml;

        Document ddsResponseDocument = fetchDocumentFromCache(uniqueKey);

        if (ddsResponseDocument == null) {
            prtln("GetRecord serviceRequest: " + serviceRequest);

            // Perfrom the GetRecord request:

            int num_tries = 3;
            IOException ioEx = null;
            // DDS GetRecord request can return IOException message 'Invalid HTTP response code: 500' if, for example, the DDS index is stale from a recent update.
            // Often another try will be successful:
            for (int i = 0; i < num_tries; i++) {
                ioEx = null;
                try {
                    ddsResponseDocument = Dom4jUtils.getXmlDocument(new URL(serviceRequest), 60000, _userAgent);
                } catch (IOException ioe) {
                    ioEx = ioe;
                }
                if (ioEx == null)
                    break;
            }
            if (ioEx != null)
                throw ioEx;

            if (localizeXml)
                ddsResponseDocument = Dom4jUtils.localizeXml(ddsResponseDocument);
            //prtln("GetRecord response:\n" + ddsResponseDocument.asXML());

            // Check for service error code response:
            String[] errorResponse = checkForErrorResponseDDSWS(ddsResponseDocument);
            if (errorResponse != null)
                throw new DDSServiceErrorResponseException(errorResponse[0], errorResponse[1], "GetRecord");

            saveDocumentToCache(uniqueKey, ddsResponseDocument);
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
                } else
                    throw new Exception("Value for additionalRequestParams Map must be of type String or String []");
            }
        }
        return queryString;
    }


    /**
     * Determines whether the given record is in this DDS repository. If a format is specified, then also
     * determines if the record can be dissiminated in the given format.
     *
     * @param id        The record id
     * @param xmlFormat The XML format to check availability for, or null for
     *                  any/all
     * @return True if the given record is in the repository
     * @throws DDSServiceErrorResponseException If service error
     * @throws Exception                        If other error
     */
    public boolean hasRecord(String id, String xmlFormat) throws DDSServiceErrorResponseException, Exception {
        DDSServicesResponse ddsServicesResponse = null;
        try {
            ddsServicesResponse = getRecord(id, null, xmlFormat, null, false, true);
        } catch (DDSServiceErrorResponseException de) {
            if (de.getServiceResponseCode().equals("idDoesNotExist") || de.getServiceResponseCode().equals("cannotDisseminateFormat")) {
                return false;
            }
            throw de;
        }
        return true;
    }


    /**
     * Performs a Search request from a DDSWS repository. Uses the POST method to send the arguments to
     * accommodate very long queries/requests.
     *
     * @param query                   The search query, or null
     * @param xmlFormat               The xmlFormat for which results must be dissiminated, or
     *                                null for any/all
     * @param startOffset             Starting offset in the returns
     * @param numReturns              Number of records to return
     * @param showRelation            The relation data to include in the response for example
     *                                isAnnotatedBy, or null for none
     * @param additionalRequestParams A Map that contains param/value pairs. Values must be of
     *                                type String to indicate a single value, or a String [] to indicate multiple values. Map may be null
     *                                for none.
     * @param soAllRecords            True to search all records including non-discoverable ones
     *                                (client must be authorized by IP)
     * @param localizeXml             True to localize the response
     * @param sortByField             Indicates the search field to sort the results by, or null
     *                                for none
     * @param sortOrder               The sort order to apply (ascending, descending). Ignored if
     *                                no sortByField has been indicated
     * @return The service response
     * @throws DDSServiceErrorResponseException Standard service error if one was returned
     * @throws Exception                        If other error occurs
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
        return doSearch(
                "Search",
                query,
                xmlFormat,
                startOffset,
                numReturns,
                sortByField,
                sortOrder,
                showRelation,
                additionalRequestParams,
                soAllRecords,
                localizeXml);
    }

    /**
     * Performs a UserSearch request from a DDSWS repository (UserSearch is deprecated, but still works for old ADN/DLESE
     * DLESE repositories. Uses the POST method to send the arguments to
     * accommodate very long queries/requests.
     *
     * @param query                   The search query, or null
     * @param xmlFormat               The xmlFormat for which results must be dissiminated, or
     *                                null for any/all
     * @param startOffset             Starting offset in the returns
     * @param numReturns              Number of records to return
     * @param showRelation            The relation data to include in the response for example
     *                                isAnnotatedBy, or null for none
     * @param additionalRequestParams A Map that contains param/value pairs. Values must be of
     *                                type String to indicate a single value, or a String [] to indicate multiple values. Map may be null
     *                                for none.
     * @param soAllRecords            True to search all records including non-discoverable ones
     *                                (client must be authorized by IP)
     * @param localizeXml             True to localize the response
     * @param sortByField             Indicates the search field to sort the results by, or null
     *                                for none
     * @param sortOrder               The sort order to apply (ascending, descending). Ignored if
     *                                no sortByField has been indicated
     * @return The service response
     * @throws DDSServiceErrorResponseException Standard service error if one was returned
     * @throws Exception                        If other error occurs
     */
    public DDSServicesResponse userSearch(String query,
                                          String xmlFormat,
                                          int startOffset,
                                          int numReturns,
                                          String sortByField,
                                          int sortOrder,
                                          String showRelation,
                                          Map additionalRequestParams,
                                          boolean soAllRecords,
                                          boolean localizeXml) throws DDSServiceErrorResponseException, Exception {
        return doSearch(
                "UserSearch",
                query,
                xmlFormat,
                startOffset,
                numReturns,
                sortByField,
                sortOrder,
                showRelation,
                additionalRequestParams,
                soAllRecords,
                localizeXml);
    }

    /**
     * Performs a Search or UserSearch request from a DDSWS repository. Uses the POST method to send the arguments to
     * accommodate very long queries/requests.
     *
     * @param query                   The search query, or null
     * @param xmlFormat               The xmlFormat for which results must be dissiminated, or
     *                                null for any/all
     * @param startOffset             Starting offset in the returns
     * @param numReturns              Number of records to return
     * @param showRelation            The relation data to include in the response for example
     *                                isAnnotatedBy, or null for none
     * @param additionalRequestParams A Map that contains param/value pairs. Values must be of
     *                                type String to indicate a single value, or a String [] to indicate multiple values. Map may be null
     *                                for none.
     * @param soAllRecords            True to search all records including non-discoverable ones
     *                                (client must be authorized by IP)
     * @param localizeXml             True to localize the response
     * @param sortByField             Indicates the search field to sort the results by, or null
     *                                for none
     * @param sortOrder               The sort order to apply (ascending, descending). Ignored if
     *                                no sortByField has been indicated
     * @return The service response
     * @throws DDSServiceErrorResponseException Standard service error if one was returned
     * @throws Exception                        If other error occurs
     */
    private DDSServicesResponse doSearch(
            String verb,
            String query,
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
                "verb=" + verb +
                        "&n=" + numReturns +
                        "&s=" + startOffset;

        if (query != null)
            serviceRequestArgs += "&q=" + URLEncoder.encode(query, "UTF-8");
        if (xmlFormat != null)
            serviceRequestArgs += "&xmlFormat=" + URLEncoder.encode(xmlFormat, "UTF-8");

        // Apply a sort order, if requested:
        if (sortByField != null && sortByField.trim().length() > 0 && sortOrder != SORT_ORDER_NO_SORT) {
            String sortArg = null;
            if (sortOrder == SORT_ORDER_ASCENDING)
                sortArg = "sortAscendingBy";
            else if (sortOrder == SORT_ORDER_DESCENDING)
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

        // Add any additional request parameters:
        serviceRequestArgs += getQueryStringFromMap(additionalRequestParams);

        //prtln("Search serviceRequestArgs: " + serviceRequestArgs);

        String cachedObjectKey = serviceRequestArgs + " localized:" + localizeXml;
        Document ddsResponseDocument = fetchDocumentFromCache(cachedObjectKey);
        if (ddsResponseDocument == null) {

            prtln("Performing remote search to baseURL " + ddswsBaseUrl);

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
            if (errorResponse != null)
                throw new DDSServiceErrorResponseException(errorResponse[0], errorResponse[1], "Search");

            saveDocumentToCache(cachedObjectKey, ddsResponseDocument);
        }

        return new DDSServicesResponse(ddswsBaseUrl + "?" + serviceRequestArgs, ddsResponseDocument);
    }


    /**
     * Performs a ListCollections request from a DDSWS repository.
     *
     * @param localizeXml True to localize the response XML
     * @return The service response
     * @throws DDSServiceErrorResponseException Standard service error if one was returned
     * @throws Exception                        If other error occurs
     */
    public DDSServicesResponse listCollections(boolean localizeXml) throws DDSServiceErrorResponseException, Exception {
        String serviceRequest = ddswsBaseUrl + "?verb=ListCollections";

        if (clientName != null && clientName.length() > 0)
            serviceRequest += "&client=" + URLEncoder.encode(clientName, "UTF-8");

        prtln("listCollections serviceRequest: " + serviceRequest);

        Document ddsResponse = Dom4jUtils.getXmlDocument(new URL(serviceRequest), 60000, _userAgent);
        if (localizeXml)
            ddsResponse = Dom4jUtils.localizeXml(ddsResponse);
        //prtln("Search response:\n" + ddsResponse.asXML());

        // Check for service error code response:
        String[] errorResponse = checkForErrorResponseDDSWS(ddsResponse);
        if (errorResponse != null)
            throw new DDSServiceErrorResponseException(errorResponse[0], errorResponse[1], "ListCollections");

        return new DDSServicesResponse(serviceRequest, ddsResponse);
    }


    /**
     * Performs a ServiceInfo request from a DDSWS repository.
     *
     * @param localizeXml True to localize the response XML
     * @return The service response
     * @throws DDSServiceErrorResponseException Standard service error if one was returned
     * @throws Exception                        If other error occurs
     */
    public DDSServicesResponse serviceInfo(boolean localizeXml) throws DDSServiceErrorResponseException, Exception {
        String serviceRequest = ddswsBaseUrl + "?verb=ServiceInfo";

        if (clientName != null && clientName.length() > 0)
            serviceRequest += "&client=" + URLEncoder.encode(clientName, "UTF-8");

        //prtln("ServiceInfo serviceRequest: " + serviceRequest);

        Document ddsResponse = Dom4jUtils.getXmlDocument(new URL(serviceRequest), 60000, _userAgent);
        if (localizeXml)
            ddsResponse = Dom4jUtils.localizeXml(ddsResponse);
        //prtln("Search response:\n" + ddsResponse.asXML());

        // Check for service error code response:
        String[] errorResponse = checkForErrorResponseDDSWS(ddsResponse);
        if (errorResponse != null)
            throw new DDSServiceErrorResponseException(errorResponse[0], errorResponse[1], "ServiceInfo");

        return new DDSServicesResponse(serviceRequest, ddsResponse);
    }


    /**
     * Get the index version, which can be useful to determine if a service response cache needs to be updated.
     *
     * @return The indexVersion value
     * @throws DDSServiceErrorResponseException If DDSServiceErrorResponseException error
     * @throws Exception                        If exception
     */
    public String getIndexVersion() throws DDSServiceErrorResponseException, Exception {

        DDSServicesResponse ddsServicesResponse = null;
        try {
            ddsServicesResponse = serviceInfo(false);
            Document serviceInfoDoc = ddsServicesResponse.getResponseDocument();
            String indexVersion = serviceInfoDoc.valueOf("/*[local-name()='DDSWebService']/*[local-name()='ServiceInfo']/*[local-name()='indexVersion']");
            prtln("getIndexVersion() for baseURL " + ddswsBaseUrl + ":" + indexVersion);
            return indexVersion;
        } catch (DDSServiceErrorResponseException de) {
            prtlnErr("getIndexVersion(): " + de);
        }
        return "";
    }

    /**
     * Get the Date of the last modification to the index.
     *
     * @return The last modified date value, or null if unable to attain
     * @throws DDSServiceErrorResponseException If DDSServiceErrorResponseException error
     * @throws Exception                        If exception
     */
    public Date getIndexLastModifiedDate() throws DDSServiceErrorResponseException, Exception {
        DDSServicesResponse ddsServicesResponse = null;
        try {
            ddsServicesResponse = serviceInfo(false);
            Document serviceInfoDoc = ddsServicesResponse.getResponseDocument();
            String indexLastModifiedDateStr = serviceInfoDoc.valueOf("/*[local-name()='DDSWebService']/*[local-name()='ServiceInfo']/*[local-name()='indexLastModifiedDate']");
            Date indexLastModifiedDate = OAIUtils.getDateFromDatestamp(indexLastModifiedDateStr);
            prtln("getIndexLastModifiedDate() for baseURL " + ddswsBaseUrl + ":" + indexLastModifiedDate);
            return indexLastModifiedDate;
        } catch (DDSServiceErrorResponseException de) {
            prtlnErr("getIndexLastModifiedDate(): " + de);
        }
        return null;
    }

    /**
     * Performs a ListFields request from a DDSWS repository.
     *
     * @param localizeXml True to localize the response XML
     * @return The service response
     * @throws DDSServiceErrorResponseException Standard service error if one was returned
     * @throws Exception                        If other error occurs
     */
    public DDSServicesResponse listFields(boolean localizeXml) throws DDSServiceErrorResponseException, Exception {
        String serviceRequest = ddswsBaseUrl + "?verb=ListFields";

        if (clientName != null && clientName.length() > 0)
            serviceRequest += "&client=" + URLEncoder.encode(clientName, "UTF-8");

        prtln("listFields serviceRequest: " + serviceRequest);

        Document ddsResponse = Dom4jUtils.getXmlDocument(new URL(serviceRequest), 60000, _userAgent);
        if (localizeXml)
            ddsResponse = Dom4jUtils.localizeXml(ddsResponse);
        //prtln("Search response:\n" + ddsResponse.asXML());

        // Check for service error code response:
        String[] errorResponse = checkForErrorResponseDDSWS(ddsResponse);
        if (errorResponse != null)
            throw new DDSServiceErrorResponseException(errorResponse[0], errorResponse[1], "ListFields");

        return new DDSServicesResponse(serviceRequest, ddsResponse);
    }


    /**
     * Performs a ListTerms request from a DDSWS repository. The request accepts one or more fields to list the
     * terms data for. Note that this request is much less efficient when more than one field is requested.
     *
     * @param fields      One or more fields to list terms for
     * @param localizeXml True to localize the response XML
     * @return The service response
     * @throws DDSServiceErrorResponseException Standard service error if one was returned
     * @throws Exception                        If other error occurs
     */
    public DDSServicesResponse listTerms(String[] fields, boolean localizeXml) throws DDSServiceErrorResponseException, Exception {
        String serviceRequest = ddswsBaseUrl + "?verb=ListTerms";

        if (fields != null)
            for (int i = 0; i < fields.length; i++)
                serviceRequest += "&field=" + URLEncoder.encode(fields[i], "UTF-8");

        if (clientName != null && clientName.length() > 0)
            serviceRequest += "&client=" + URLEncoder.encode(clientName, "UTF-8");

        prtln("listTerms serviceRequest: " + serviceRequest);

        Document ddsResponse = Dom4jUtils.getXmlDocument(new URL(serviceRequest), 60000, _userAgent);
        if (localizeXml)
            ddsResponse = Dom4jUtils.localizeXml(ddsResponse);
        //prtln("Search response:\n" + ddsResponse.asXML());

        // Check for service error code response:
        String[] errorResponse = checkForErrorResponseDDSWS(ddsResponse);
        if (errorResponse != null)
            throw new DDSServiceErrorResponseException(errorResponse[0], errorResponse[1], "ListTerms");

        return new DDSServicesResponse(serviceRequest, ddsResponse);
    }


    /**
     * Determines whether the given collection is in this DDS repository matching the given collection key and
     * xml format.
     *
     * @param collectionKey The collection key, for example 'dcc'
     * @param xmlFormat     The xml format for this collection, for example 'adn'
     * @return True if the given collection is in the repository with the
     * given
     * @throws DDSServiceErrorResponseException If service error
     * @throws Exception                        If other error
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
     * Gets the number of records that reside in the given collection. Returns 0 or more if the collection
     * exists, -1 if the collection was not found in the repository.
     *
     * @param collectionKey The collection key, for example 'dcc'
     * @return 0 or more if the collection exists, -1 if the collection was
     * not found
     * @throws DDSServiceErrorResponseException If service error
     * @throws Exception                        If other error
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


    /**
     * Checks for the existance of an error response from a DDS service request (DDSWS). If no error was
     * recieved, this method returns null, otherwise returns the error code and message.
     *
     * @param ddswsResponse The service response
     * @return The error code and message [code,message], or null if none
     */
    protected String[] checkForErrorResponseDDSWS(Document ddswsResponse) {
        if (ddswsResponse != null && ddswsResponse.selectSingleNode("/*[local-name()='DDSWebService']/*[local-name()='error']") != null) {
            String code = ddswsResponse.valueOf("/*[local-name()='DDSWebService']/*[local-name()='error']/@code");
            String msg = ddswsResponse.valueOf("/*[local-name()='DDSWebService']/*[local-name()='error']");
            if (code == null)
                code = "";
            if (msg == null)
                msg = "";
            return new String[]{code, msg};
        }
        return null;
    }


    // ---------------------- DDSUpdateWS methods -----------------

    /**
     * Puts a record into a DDS repository.
     *
     * @param recordId      recordId
     * @param recordXml     recordXml
     * @param collectionKey collectionKey
     * @param xmlFormat     xmlFormat
     * @return The service response
     * @throws Exception                        If error
     * @throws DDSServiceErrorResponseException Standard service error if one was returned
     */
    public Document putRecord(
            String recordId,
            String recordXml,
            String collectionKey,
            String xmlFormat) throws DDSServiceErrorResponseException, Exception {

        // Save the user's anno record:
        //prtln("Saving record:\n" + recordXml);

        String postData =
                "verb=PutRecord" +
                        "&id=" + URLEncoder.encode(recordId, "UTF-8") +
                        "&collectionKey=" + URLEncoder.encode(collectionKey, "UTF-8") +
                        "&xmlFormat=" + URLEncoder.encode(xmlFormat, "UTF-8") +
                        "&recordXml=" + URLEncoder.encode(recordXml, "UTF-8");

        if (clientName != null && clientName.length() > 0)
            postData += "&client=" + URLEncoder.encode(clientName, "UTF-8");

        //prtln("PutRecord serviceRequest: " + ddsupdatewsBaseUrl);
        Document ddsupdateResponse = Dom4jUtils.localizeXml(Dom4jUtils.getXmlDocumentPostData(new URL(ddsupdatewsBaseUrl), postData, 90000, _userAgent));
        //prtln("PutRecord response:\n" + ddsupdateResponse.asXML());

        // Check for service error code response:
        String[] errorResponse = checkForErrorResponseDDSUpdateWS(ddsupdateResponse);
        if (errorResponse != null)
            throw new DDSServiceErrorResponseException(errorResponse[0], errorResponse[1], "PutRecord");

        return ddsupdateResponse;
    }


    /**
     * Deletes a record from a DDS repository.
     *
     * @param recordId recordId
     * @return The service response
     * @throws DDSServiceErrorResponseException Standard service error if one was returned
     * @throws Exception                        If other error occurs
     */
    public DDSServicesResponse deleteRecord(String recordId) throws DDSServiceErrorResponseException, Exception {

        String serviceRequest = ddsupdatewsBaseUrl +
                "?verb=DeleteRecord" +
                "&id=" + URLEncoder.encode(recordId, "UTF-8");

        if (clientName != null && clientName.length() > 0)
            serviceRequest += "&client=" + URLEncoder.encode(clientName, "UTF-8");

        //prtln("DeleteRecord serviceRequest: " + serviceRequest);
        Document ddsupdateResponse = Dom4jUtils.localizeXml(Dom4jUtils.getXmlDocument(new URL(serviceRequest), 60000, _userAgent));
        //prtln("DeleteRecord response:\n" + ddsupdateResponse.asXML());

        // Check for error code response from DeleteRecord:
        String[] errorResponse = checkForErrorResponseDDSUpdateWS(ddsupdateResponse);
        if (errorResponse != null)
            throw new DDSServiceErrorResponseException(errorResponse[0], errorResponse[1], "DeleteRecord");

        return new DDSServicesResponse(serviceRequest, ddsupdateResponse);
    }


    /**
     * Puts a collection into a DDS repository.
     *
     * @param collectionKey         The collection key, for example 'dcc'
     * @param xmlFormat             The xml format, for example 'adn'
     * @param collectionName        The name of the collection
     * @param collectionDescription Description of the collection
     * @return The service response
     * @throws DDSServiceErrorResponseException Standard service error if one was returned
     * @throws Exception                        If other error occurs
     */
    public DDSServicesResponse putCollection(String collectionKey,
                                             String xmlFormat,
                                             String collectionName,
                                             String collectionDescription) throws DDSServiceErrorResponseException, Exception {
        String serviceRequest = ddsupdatewsBaseUrl +
                "?verb=PutCollection" +
                "&collectionKey=" + URLEncoder.encode(collectionKey, "UTF-8") +
                "&xmlFormat=" + URLEncoder.encode(xmlFormat, "UTF-8") +
                "&name=" + URLEncoder.encode(collectionName, "UTF-8") +
                "&description=" + URLEncoder.encode(collectionDescription, "UTF-8");

        if (clientName != null && clientName.length() > 0)
            serviceRequest += "&client=" + URLEncoder.encode(clientName, "UTF-8");

        //prtln("PutCollection serviceRequest: " + serviceRequest);
        Document ddsupdateResponse = Dom4jUtils.localizeXml(Dom4jUtils.getXmlDocument(new URL(serviceRequest), 60000, _userAgent));
        //prtln("PutCollection response:\n" + ddsupdateResponse.asXML());

        // Check for error code response:
        String[] errorResponse = checkForErrorResponseDDSUpdateWS(ddsupdateResponse);
        if (errorResponse != null)
            throw new DDSServiceErrorResponseException(errorResponse[0], errorResponse[1], "PutCollection");

        return new DDSServicesResponse(serviceRequest, ddsupdateResponse);
    }


    /**
     * Deletes a collection from the repository including all of the records it contains.
     *
     * @param collectionKey The collectionKey
     * @return The service response
     * @throws DDSServiceErrorResponseException Standard service error if one was returned
     * @throws Exception                        If other error occurs
     */
    public DDSServicesResponse deleteCollection(String collectionKey) throws DDSServiceErrorResponseException, Exception {

        String serviceRequest = ddsupdatewsBaseUrl +
                "?verb=DeleteCollection" +
                "&collectionKey=" + URLEncoder.encode(collectionKey, "UTF-8");

        if (clientName != null && clientName.length() > 0)
            serviceRequest += "&client=" + URLEncoder.encode(clientName, "UTF-8");

        //prtln("DeleteCollection serviceRequest: " + serviceRequest);
        Document ddsupdateResponse = Dom4jUtils.localizeXml(Dom4jUtils.getXmlDocument(new URL(serviceRequest), 90000, _userAgent));
        //prtln("DeleteCollection response:\n" + ddsupdateResponse.asXML());

        // Check for error code response from DeleteRecord:
        String[] errorResponse = checkForErrorResponseDDSUpdateWS(ddsupdateResponse);
        if (errorResponse != null)
            throw new DDSServiceErrorResponseException(errorResponse[0], errorResponse[1], "DeleteCollection");

        return new DDSServicesResponse(serviceRequest, ddsupdateResponse);
    }


    /**
     * Checks for the existance of an error response from a DDS update service request (DDSUpdateWS). If no
     * error was recieved, this method returns null, otherwise returns the error code and message.
     *
     * @param ddsupdateResponse The DDS response
     * @return The error code and message [code,message], or null if none
     */
    protected String[] checkForErrorResponseDDSUpdateWS(Document ddsupdateResponse) {
        if (ddsupdateResponse != null && ddsupdateResponse.selectSingleNode("/DDSRepositoryUpdateService/error") != null) {
            String code = ddsupdateResponse.valueOf("/DDSRepositoryUpdateService/error/@code");
            String msg = ddsupdateResponse.valueOf("/DDSRepositoryUpdateService/error");
            if (code == null)
                code = "";
            if (msg == null)
                msg = "";
            return new String[]{code, msg};
        }
        return null;
    }


    // ---------------------- Debug info --------------------

    /**
     * Return a string for the current time and date, sutiable for display in log files and output to standout:
     *
     * @return The dateStamp value
     */
    protected final static String getDateStamp() {
        return
                new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
    }


    /**
     * Output a line of text to error out, with datestamp.
     *
     * @param s The text that will be output to error out.
     */
    private final static void prtlnErr(String s) {
        System.err.println(getDateStamp() + " DDSServicesToolkit Error: " + s);
    }


    /**
     * Output a line of text to standard out, with datestamp, if debug is set to true.
     *
     * @param s The String that will be output.
     */
    private final static void prtln(String s) {
        if (debug) {
            System.out.println(getDateStamp() + " DDSServicesToolkit: " + s);
        }
    }


    /**
     * Sets the debug attribute of the object
     *
     * @param db The new debug value
     */
    public static void setDebug(boolean db) {
        debug = db;
    }
}

