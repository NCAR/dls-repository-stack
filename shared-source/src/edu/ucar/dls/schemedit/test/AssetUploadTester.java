package edu.ucar.dls.schemedit.test;

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

public class AssetUploadTester {
	
	private static boolean debug = true;
	
	DDSServicesToolkit ddsWSServiceForCurricula = null;

	public AssetUploadTester (String ddswsBaseUrlForCurricula) {
		this.setDdswsBaseUrlForCurricula ( ddswsBaseUrlForCurricula);
	}

	/**
	 *  Sets the ddswsBaseUrlForCurricula attribute of the CurriculaDataBean object
	 *
	 * @param  ddswsBaseUrlForCurricula  The new ddswsBaseUrlForCurricula value
	 */
	public void setDdswsBaseUrlForCurricula(String ddswsBaseUrlForCurricula) {
		ddsWSServiceForCurricula = new DDSServicesToolkit(ddswsBaseUrlForCurricula, "MyUserAgent", "MyUserAgent");
		/* 
		ddsWSServiceForCurricula = new StatusAwareDDSServicesToolkit (ddswsBaseUrlForCurricula, 
																	  "MyUserAgent", 
																	  "MyUserAgent"); */
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
	public Document getResponseDoc (String filename) throws Exception {
		// String query = "urlenc:" + SimpleLuceneIndex.encodeToTerm("http://ccs.dls.ucar.edu/*");
		
		// String query = "urlenc:" + SimpleLuceneIndex.encodeToTerm("h*"+filename);
		// String query = "urlenc:" + "h*"+filename;
		
		// String query = "ocean";
		String query = "urlenc:h*" + SimpleLuceneIndex.encodeToTerm(filename, false, true);
		// String query = SimpleLuceneIndex.encodeToTerm(filename, false, true);
		Map additionalRequestParams = null;
		prtln ("query: " + query);
		DDSServicesResponse ddsServicesResponse = null;
		try {
			ddsServicesResponse = ddsWSServiceForCurricula.search(query, null, 0, 500, null, DDSServicesToolkit.SORT_ORDER_NO_SORT, null, additionalRequestParams, false, true);
		} catch (DDSServiceErrorResponseException ddse) {
			// No records match is OK...
			if (ddse.getServiceResponseCode().equals("noRecordsMatch")) {
				//System.out.println("Error getSelectedConceptCCSRecordsDom() DDS Response: No records match...");
				prtln ("No records match");
			}
			else {
				System.out.println("Error getSelectedConceptCCSRecordsDom() Unexpected DDS Service response: " + ddse);
				ddse.printStackTrace();
			}
		} catch (Throwable e) {
			System.out.println("Error getSelectedConceptCCSRecordsDom(): " + e);
			e.printStackTrace();
		}

		if (ddsServicesResponse == null)
			return null;
		return ddsServicesResponse.getResponseDocument();

	}
	
	public List<RecordInfo> getRecordInfos (String filename) throws Exception {
		
		List<RecordInfo> returnList = new ArrayList<RecordInfo> ();
		Document responseDoc = getResponseDoc(filename);
		if (responseDoc == null)
			return returnList;
		
		List<Node> nodes = responseDoc.selectNodes("/DDSWebService/Search/results/record");
		for (Node node : nodes) {
			returnList.add (new RecordInfo (node));
		}
		return returnList;
	}
	
	public static void main (String[] args) throws Exception {
		prtln ("hello from AUT");
		String ddswsBaseUrlForCurricula = "http://localhost:8070/curricula/services/ddsws1-1";
		AssetUploadTester tester = new AssetUploadTester(ddswsBaseUrlForCurricula);
		String filename = "fooberry.pdf";
		if (args.length > 0)
			filename = args[0];
		prtln ("Filename: " + filename);
/* 		Document response = tester.getResponseDoc(filename);
		if (response != null) {
			pp (response);
		}
		else
			prtln ("no response received"); */
			
		List<RecordInfo> resultInfos = tester.getRecordInfos(filename);
		prtln (resultInfos.size() + " results found");
		for (RecordInfo info : resultInfos)
			prtln (" - " + info.toString());
	}
	
	public class RecordInfo {
		private String recordId;
		private String collectionName;
		private String collection;
		
		public RecordInfo (Node result) {
			this.recordId = result.valueOf ("head/id");
			this.collectionName = result.valueOf ("head/collection");
			this.collection = result.valueOf ("head/collection/@key");
		}
		
		public String getId() {
			return recordId;
		}
		
		public String getCollectionName () {
			return collectionName;
		}
		
		public String getCollection () {
			return collection;
		}
		
		public String toString () {
			return recordId + " - " + collection + " - " + collectionName;
		}
	}
		
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("JloTester: " + s);
			System.out.println(s);
		}
	}
	
	private static void pp (Node node) {
		prtln (Dom4jUtils.prettyPrint(node));
	}
}

