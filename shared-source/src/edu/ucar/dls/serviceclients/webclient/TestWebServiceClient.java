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

package edu.ucar.dls.serviceclients.webclient;

import edu.ucar.dls.schemedit.test.TesterUtils;
import edu.ucar.dls.xml.Dom4jUtils;

import org.dom4j.Node;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import java.io.File;
import java.util.*;

public class TestWebServiceClient {
	
	private static boolean debug = true;
	
	

	private WebServiceClient wsClient = null;
	
	public TestWebServiceClient(String baseWebServiceUrl) {
		// this.baseWebServiceUrl = baseWebServiceUrl;
		wsClient = new WebServiceClient (baseWebServiceUrl);
	}
	
	static void putRecordTest () {
		String baseWebServiceUrl = "http://ttambora.ucar.edu:10160/schemedit/services/dcsws1-0";
		TestWebServiceClient tester = new TestWebServiceClient(baseWebServiceUrl);
		
		Document doc = null;
		String collection = "testosm";
		String xmlFormat = "osm";
		String status = "Fooberry";
		String statusNote = "This is but a test ..";
		String id = null;
		String path = "C:/Users/ostwald/tmp/bag-ass-OSM.xml";
		try {
			doc = Dom4jUtils.getXmlDocument(new File (path));
			id = tester.wsClient.doPutRecord (doc.asXML(), xmlFormat, collection, status, statusNote);
		} catch (Exception e) {
			prtln ("putRecordTest error: " + e.getMessage());
		} catch (Throwable t) {
			prtln ("putRecordTest error: " + t.getMessage());
			t.printStackTrace();
		}
		prtln ("id: " + id);
	}
	
	static void getRecordTest () {
		String baseWebServiceUrl = "http://www.dlese.org/dds/services/ddsws1-1";
		TestWebServiceClient tester = new TestWebServiceClient(baseWebServiceUrl);
		String id = "DLESE-000-000-004-409";
		Document doc = null;
		Map props = new HashMap();
		try {
			GetRecordResponse response = tester.wsClient.getRecord(id);
			// doc = response.getDocument();
			doc = response.getItemRecord();
			prtln (pp(doc));
		} catch (Exception e) {
			prtln (e.getMessage());
			return;
		}
		
		String title_path = "/itemRecord/general/title";
		try {
			props.put ("title", ((Element)doc.selectSingleNode (title_path)).getText());
		} catch (Throwable t){}
		
		String id_path = "/itemRecord/metaMetadata/catalogEntries/catalog/@entry";
		try {
			props.put ("id", ((Node)doc.selectSingleNode (id_path)).getText());
		} catch (Throwable t){}
		
		prtln ("title: " + (String)props.get("title"));
		prtln ("id: " + (String)props.get("id"));
	}
			
			
		
	public static void main (String [] args) {
		
		prtln ("\n\n--------------------------------------------");
		prtln ("TestWebServiceClient \n");
		
		// String baseWebServiceUrl = "http://dcs.dlese.org/dds/services/ddsws1-0";
		// private String baseWebServiceUrl = "http://dcs.dlese.org/roles/services/dcsws1-0";
		// TestWebServiceClient tester = new TestWebServiceClient(baseWebServiceUrl);
		putRecordTest();
		// getRecordTest ();
	}
		
	private static String pp (Node node) {
		return Dom4jUtils.prettyPrint(node);
	}
	
	private static void prtln(String s) {
		if (debug) {
			edu.ucar.dls.schemedit.SchemEditUtils.prtln(s, "");
		}
	}
	
}
