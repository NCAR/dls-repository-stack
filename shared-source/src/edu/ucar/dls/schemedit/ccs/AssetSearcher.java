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
package edu.ucar.dls.schemedit.ccs;

import java.text.SimpleDateFormat;

import edu.ucar.dls.services.dds.toolkit.*;

import java.io.*;
import java.util.*;
import java.text.*;
import java.net.URI;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.config.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.util.*;
import edu.ucar.dls.index.SimpleLuceneIndex;

import org.dom4j.*;
import org.json.*;

public class AssetSearcher {
	
	private static boolean debug = true;
	private static String USER_AGENT = "AssetSearcher";
	private String indexVersion = "-1";
	private Map<String, List<String>> assetUrlMap = null;
	
	DDSServicesToolkit ddsWSServiceForCurricula = null;

	public AssetSearcher (String ddswsBaseUrlForCurricula) {
		this.setDdswsBaseUrlForCurricula ( ddswsBaseUrlForCurricula);
		this.assetUrlMap = new HashMap<String, List<String>>();
	}

	/**
	 *  Sets the ddswsBaseUrlForCurricula attribute of the CurriculaDataBean object
	 *
	 * @param  ddswsBaseUrlForCurricula  The new ddswsBaseUrlForCurricula value
	 */
	public void setDdswsBaseUrlForCurricula(String ddswsBaseUrlForCurricula) {
		ddsWSServiceForCurricula = new DDSServicesToolkit(ddswsBaseUrlForCurricula, USER_AGENT, USER_AGENT);
		/* 
		ddsWSServiceForCurricula = new StatusAwareDDSServicesToolkit (ddswsBaseUrlForCurricula, 
																	  USER_AGENT, 
																	  USER_AGENT); */
	}
	
	/**
		search params:
			query
			xmlFormat
			startOffset
			numReturns
			sortByField
			sortOrder
			showRelation
			additionalRequestParams
			soAllRecord
			localXml
	*/
	public Document getAssetRecordsResponse (String filename) throws Exception {

		String query = "urlenc:h*" + SimpleLuceneIndex.encodeToTerm(filename, false, true);
		// String query = SimpleLuceneIndex.encodeToTerm(filename, false, true);

		Map additionalRequestParams = new HashMap();
		// additionalRequestParams.put("storedContent", new String[]{"url", "/key//assessment/question/outline/@url"});
		
		additionalRequestParams.put("storedContent", new String[]{"url"});
		DDSServicesResponse ddsServicesResponse = null;
		try {
			ddsServicesResponse = ddsWSServiceForCurricula.search(query, null, 0, 500, null, DDSServicesToolkit.SORT_ORDER_NO_SORT, null, additionalRequestParams, false, true);
		} catch (DDSServiceErrorResponseException ddse) {
			// No records match is OK...
			if (ddse.getServiceResponseCode().equals("noRecordsMatch")) {
				//System.out.println("Error getAssetRecordsResponse() DDS Response: No records match...");
				prtln ("No records match");
			}
			else {
				System.out.println("Error getAssetRecordsResponse() Unexpected DDS Service response: " + ddse);
				ddse.printStackTrace();
			}
		} catch (Throwable e) {
			System.out.println("Error getAssetRecordsResponse(): " + e);
			e.printStackTrace();
		}

		if (ddsServicesResponse == null)
			return null;
		return ddsServicesResponse.getResponseDocument();

	}
	
	String makeCacheKey (String[] collections) {
		if (collections == null)
			return null;
		String key = "";
		Arrays.sort(collections);
		for (int i=0;i<collections.length;i++) {
			if (i > 0)
				key += "_";
			key += collections[i];
		}
		return key;
	}
	
	/**
	Use a faceted search on the curriculum DDS to determine 
	all the assetUrls  in the specified collections, where
	assetUrls are those indexed in the facet category, CCSAssetUrl.

	The CCSAssetUrl field is configured in the xmlIndexer configs for
	xmlFormats that can catalog assetUrls (e.g., dlese_anno, ncs_item,
	adn, assessements)
	
	AssetUrls obtained from the webservice are cached.
	- we track the indexVersion
	- see http://localhost/api/dlese-tools/edu/ucar/dls/services/dds/toolkit/DDSServicesToolkit.html
	
	we cache results for each query (we make a key from the specified collections).
	test indexVersion to decide whether we return cached or refresh from webservice
	
	shouldn't this be SYNCHRONIZED?
	*/
	public List<String> getAssetUrls (String[] collections) throws Exception {
		
		/* if we have a cached version for all requested collections,
		   then build response from cache */
		   
		boolean useCachedUrls = true;
		String key = makeCacheKey(collections);
		// prtln ("key: " + key);
		List<String> cachedUrls = assetUrlMap.get(key);
		
		String currentVersion = null;
		try {
			currentVersion = ddsWSServiceForCurricula.getIndexVersion();
			// prtln ("currentVersion: " + currentVersion);
			// prtln ("indexVersion: " + indexVersion);
		} catch (Throwable t) {
			prtln ("Could not work versions: " + t.getMessage());
		}
		
		if (currentVersion == indexVersion && cachedUrls != null) {
			prtln ("Using cached urls");
			return cachedUrls;
		}
		
		if (currentVersion != null)
			indexVersion = currentVersion;
		
		String query = "allrecords:true AND (";
		
		for (int i=0;i<collections.length;i++) {
			if (i > 0)
				query += " OR ";
			query += collections[i];
		}
		query += ")";
		
		Map additionalRequestParams = new HashMap();
		// additionalRequestParams.put("storedContent", new String[]{"url", "/key//assessment/question/outline/@url"});
		
		additionalRequestParams.put("facet", "on");
		additionalRequestParams.put("facet.category", "CCSAssetUrl");
		additionalRequestParams.put("f.CCSAssetUrl.maxResults", "10000");
		additionalRequestParams.put("f.CCSAssetUrl.maxLabels", "10000");
		
		String xmlFormat = null;
		int startOffset = 0;
		int numReturns = 1;
		String sortByField = null;
		int sortOrder = DDSServicesToolkit.SORT_ORDER_NO_SORT;
		String showRelation = null;
		boolean soAllRecords = false;
		boolean localizeXml = true;
		
		DDSServicesResponse ddsServicesResponse = null;
		try {
			ddsServicesResponse = ddsWSServiceForCurricula.search(
					query, xmlFormat, startOffset, numReturns, sortByField, sortOrder, showRelation, 
					additionalRequestParams, soAllRecords, localizeXml);
		} catch (DDSServiceErrorResponseException ddse) {
			// No records match is OK...
			if (ddse.getServiceResponseCode().equals("noRecordsMatch")) {
				//System.out.println("Error getAssetUrls() DDS Response: No records match...");
				prtln ("No records match");
			}
			else {
				System.out.println("Error getAssetUrls() Unexpected DDS Service response: " + ddse);
				ddse.printStackTrace();
			}
		} catch (Throwable e) {
			System.out.println("Error getAssetUrls: " + e);
			e.printStackTrace();
		}

		if (ddsServicesResponse == null)
			return new ArrayList();
		
		// pp (ddsServicesResponse.getResponseDocument());
		
		List<String> terms = new ArrayList<String>();
		String selector = "/DDSWebService/Search/facetResults/facetResult[@category='CCSAssetUrl']";
		Node ccsAssetUrl_field = ddsServicesResponse.getResponseDocument().selectSingleNode(selector);
		
		if (ccsAssetUrl_field == null) {
			prtln ("ccsAssetUrl_field not found");
			return terms;
		}
				
		List<Node> result_nodes = ccsAssetUrl_field.selectNodes("result");
			
		// prtln (result_nodes.size() + " terms found");
		
		for (Node node : result_nodes) {
			terms.add(node.valueOf("@path"));
		}
		assetUrlMap.put(key, terms);
		// prtln ("- cached terms for: " + key);
		prtln ("- cached assetUrls");
		return terms;

	}
	
	// public List<String> getAssetUrls (String[] collections) throws Exception {
	public JSONArray getAssetUrlsJson (String[] collections) throws Exception {
		List<String> assetUrls = getAssetUrls(collections);
		JSONArray urlsJson = new JSONArray();
		for (String url : assetUrls) {
			urlsJson.put(url);
		}
		return urlsJson;
	}
	
	public void clearCacheEntries (String collection) {
		if (collection == null)
			return;
		prtln (" - clearing cache for " + collection);
		for (String key : assetUrlMap.keySet()) {
			// prtln (" -- KEY: " + key);
			if (key.indexOf(collection) != -1) {
				assetUrlMap.remove(key);
			}
			
		}
		
	}
	
/* 	public List<RecordInfo> getRecordInfos (String filename) throws Exception {
		
		List<RecordInfo> returnList = new ArrayList<RecordInfo> ();
		Document responseDoc = getAssetRecordsResponse(filename);
		if (responseDoc == null)
			return returnList;
		
		List<Node> nodes = responseDoc.selectNodes("/DDSWebService/Search/results/record");
		for (Node node : nodes) {
			returnList.add (new RecordInfo (node, filename));
		}
		return returnList;
	} */
	
	public JSONArray getAssetRecords (String filename) throws Exception {
		
		JSONArray assetRecords = new JSONArray();
		Document responseDoc = getAssetRecordsResponse(filename);
		// pp (responseDoc);
		if (responseDoc == null)
			return assetRecords;
		
		List<Node> nodes = responseDoc.selectNodes("/DDSWebService/Search/results/record");
		for (Node node : nodes) {
			// assetRecords.add (new RecordInfo (node));
			// JSONObject recordJson = new RecordInfo (node).toJson();
			assetRecords.put (new RecordInfo (node, filename).toJson());
		}
		return assetRecords;
	}
	
	public static void main (String[] args) throws Exception {
		prtln ("hello from AssetSearcher");
		String ddswsBaseUrlForCurricula = "http://localhost:8070/curricula/services/ddsws1-1";
		// String ddswsBaseUrlForCurricula = "http://localhost:8080/schemedit/services/ddsws1-1";
		AssetSearcher tester = new AssetSearcher(ddswsBaseUrlForCurricula);
		String filename = "fooberry.pdf";
		if (args.length > 0)
			filename = args[0];
		// prtln ("Filename: " + filename);
/* 		Document response = tester.getAssetRecordsResponse(filename);
		if (response != null) {
			pp (response);
		}
		else
			prtln ("no response received"); */
			
/* 		List<RecordInfo> resultInfos = tester.getRecordInfos(filename);
		prtln (resultInfos.size() + " results found");
		for (RecordInfo info : resultInfos)
			prtln (" - " + info.toString()); */
		
		// JSONArray info = tester.getAssetRecords(filename);
		// prtln (info.toString(2));
		
		String[] collections = {"comment_bscs", "assess_bscs"};
		List<String> urls = tester.getAssetUrls(collections);
		Collections.sort(urls);

		for (String url : urls) {
			// prtln ("- " + url);
		}

	}
	
	public class RecordInfo {
		private String recordId;
		private String collectionName;
		private String collection;
		private String xmlFormat;
		private String filename;
		private List<String> urls = null;
		
		public RecordInfo (Node result, String filename) {
			
			this.recordId = result.valueOf ("head/id");
			this.collectionName = result.valueOf ("head/collection");
			this.collection = result.valueOf ("head/collection/@key");
			this.xmlFormat = result.valueOf ("head/xmlFormat");
			this.filename = filename;
			
			String[] urlPaths = null;
			if (this.xmlFormat.equals("dlese_anno"))
				urlPaths = new String[]{"/annotationRecord/annotation/content/url"};
			if (this.xmlFormat.equals("assessments"))
				urlPaths = new String[]{
					"/assessment/question/outline/@url",
					"/assessment/answer/outline/@url"
				};
			
			if (urlPaths == null)
				urls = getUrlsFromStoredContent (result);
			else 
				urls = getUrlsUsingPaths (result, filename, urlPaths);
			
		}
		
		private List getUrlsUsingPaths (Node result, String filename, String[] urlPaths) {
			List<String> urls = new ArrayList<String>();
			for (int i=0;i<urlPaths.length;i++) {
				String xpath = "metadata" + urlPaths[i];
				List<Node> urlNodes = result.selectNodes(xpath);
				for (Node urlNode : urlNodes) {
					String url = urlNode.getText();
					if (url.endsWith(filename))
						urls.add(url);
				}
			}
			return urls;
		}
		
		/**
		Getting urls from stored content is BAD because it only returns the FIRST url
		*/
		private List<String> getUrlsFromStoredContent (Node result) {
			List<String> urls = new ArrayList<String>();
			List<Node> urlEls = result.selectNodes("storedContent/content[@fieldName='url']");
			this.urls = new ArrayList<String>();
			for (Node node : urlEls) {
				// WE could check for protected here ...
				urls.add (node.getText());
			}
			return urls;
		}
		
		public String getId() {
			return recordId;
		}
		
		public String getCollectionName () {
			return collectionName;
		}
		
		public List getUrls() {
			return urls;
		}
		
		public String getXmlFormat() {
			return xmlFormat;
		}
		
		public String getUrl () {
			if (urls.isEmpty())
				return null;
			return (String)urls.get(0);
		}
		
		public String getCollection () {
			return collection;
		}

		public JSONObject toJson () {
			JSONObject json = new JSONObject();
			try {
				json.put ("recordId", recordId);
				json.put ("collectionName", collectionName);
				json.put ("collection", collection);
				json.put ("urls", urls);
				json.put ("xmlFormat", xmlFormat);
				
			} catch (JSONException e) {
				prtln ("WARN: toJson Failed: " + e.getMessage());
			}
			return json;
		}
		
		public String toString () {
			return recordId + " - " + collection + " - " + collectionName;
		}
	}
		
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("AssetSearcher: " + s);
			System.out.println(s);
		}
	}
	
	private static void pp (Node node) {
		prtln (Dom4jUtils.prettyPrint(node));
	}
}

