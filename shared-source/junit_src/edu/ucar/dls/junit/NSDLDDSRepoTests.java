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
 *  Copyright 2002-2012 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */

package edu.ucar.dls.junit;

import edu.ucar.dls.junit.TestTools;

import junit.framework.*;
import java.util.Date;
import java.util.Random;
import java.util.Enumeration;
import java.util.*;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Attribute;
import org.dom4j.Node;

import edu.ucar.dls.services.dds.toolkit.DDSServicesToolkit;
import edu.ucar.dls.services.dds.toolkit.DDSServicesResponse;
import edu.ucar.dls.services.dds.toolkit.DDSServiceErrorResponseException;



public class NSDLDDSRepoTests extends TestCase
{

	String baseUrl = "http://nsdl.org/dds-search";
	String userAgent = "NSDL repository unit tester";
	
	DDSServicesToolkit ddsWSServiceForNSDL = new DDSServicesToolkit(baseUrl, userAgent, userAgent);
	
	public static Test suite() {
		// Use java reflection to run all test methods in this class:
		prtln("Running NSDL repository tests...");
		return new TestSuite(NSDLDDSRepoTests.class);
	} 
	
	static int recNum = 0;
	
	/* setUp() gets called prior to running EACH test method in this class */
	protected void setUp()
	throws Exception
	{
		prtln(".");// introduce some space between the tests
	}
	
	
	public void testFacetsExist() throws Exception {
		DDSServicesResponse ddsServicesResponse = null;
		DDSServiceErrorResponseException serviceException = null;
		try {
			String query = "xmlFormat:nsdl_dc";
			String xmlFormat = null;
			int startOffset = 0;
			int numReturns = 0;
			String sortByField = null;
			int sortOrder = DDSServicesToolkit.SORT_ORDER_NO_SORT;
			String showRelation = null;
			Map additionalRequestParams = new HashMap();
			boolean soAllRecords = false;
			boolean localizeXml = true;	
			
			// Set up the additional arguments in the service request:
			additionalRequestParams.put("facet","on");
			String [] facetCagetories = {"Type","Subject","Audience","EducationLevel","ASNStandardID"};
			additionalRequestParams.put("facet.category",facetCagetories);
			
			ddsServicesResponse = ddsWSServiceForNSDL.search(query, xmlFormat, startOffset, numReturns, sortByField, sortOrder, showRelation, additionalRequestParams, soAllRecords, localizeXml);	
		} catch (DDSServiceErrorResponseException e) {
			serviceException = e;
			System.out.println("Service exception: " + e.getMessage());
		}
		
		if(ddsServicesResponse != null) {
			System.out.println("Request sent was: '" + ddsServicesResponse.getRequestString() + "'");
			System.out.println("Response was:" + ddsServicesResponse.getResponseAsXMLPretty());	
		}
		
		assertNull("Service returned an error!",serviceException);
		assertNotNull("DDS response Object was null!",ddsServicesResponse);
		
		Document ddsServiceDoc = ddsServicesResponse.getResponseDocument();
		
		List facetsFound = ddsServiceDoc.selectNodes("/DDSWebService/Search/facetResults//facetResult/@category");
		assertEquals("Wrong number of facet categories were returned.",5,facetsFound.size());
		
		HashMap facetCagetories = new HashMap();
		for(int i = 0; i < facetsFound.size(); i++) {
			Attribute facetAtt = (Attribute)facetsFound.get(i);
			facetCagetories.put(facetAtt.getText(),new Object());
		}
		assertTrue("Facet category 'Type' not found", facetCagetories.containsKey("Type") ); 
		assertTrue("Facet category 'Subject' not found", facetCagetories.containsKey("Subject") );
		assertTrue("Facet category 'Audience' not found", facetCagetories.containsKey("Audience") );
		assertTrue("Facet category 'EducationLevel' not found", facetCagetories.containsKey("EducationLevel") );
		assertTrue("Facet category 'ASNStandardID' not found", facetCagetories.containsKey("ASNStandardID") );		
	}

	
	public void testNoRecordsMatch() throws Exception {
		DDSServicesResponse ddsServicesResponse = null;
		DDSServiceErrorResponseException serviceException = null;
		String query = "noMatchesQuery123456";
		try {
			Map additionalRequestParams = new HashMap();
			
			//additionalRequestParams.put("storedContent","idmaperrors");
			ddsServicesResponse = ddsWSServiceForNSDL.search(query, null, 0, 500, null, DDSServicesToolkit.SORT_ORDER_NO_SORT, null, additionalRequestParams, false, true);
		} catch (DDSServiceErrorResponseException e) {
			serviceException = e;
		}
		
		assertNotNull("Service did NOT return an error code as expected!",serviceException);
		assertEquals("Service did not return noRecordsMatch as expected for query '"+query+"'",serviceException.getServiceResponseCode(),"noRecordsMatch");		
	}
	
	
	public void testCollectionsNotEmpty() throws Exception {
		DDSServicesResponse ddsServicesResponse = null;
		DDSServiceErrorResponseException serviceException = null;
		try {
			int numRecords = ddsWSServiceForNSDL.getNumRecordsInCollection("ncs-NSDL-COLLECTION-000-003-112-021");
			assertTrue("AMSER collection has less than 10,000 records! (key='ncs-NSDL-COLLECTION-000-003-112-021')", (numRecords > 10000) );     

			numRecords = ddsWSServiceForNSDL.getNumRecordsInCollection("439869");
			assertTrue("ComPADRE collection has less than 6,000 records! (key='439869')", (numRecords > 6000) );    
			
			numRecords = ddsWSServiceForNSDL.getNumRecordsInCollection("ncs-NSDL-COLLECTION-000-003-112-056");
			assertTrue("SMILE collection has less than 2,000 records! (key='ncs-NSDL-COLLECTION-000-003-112-056')", (numRecords > 2000) );     

			numRecords = ddsWSServiceForNSDL.getNumRecordsInCollection("ncs-NSDL-COLLECTION-000-003-112-016");
			assertTrue("NSDL Math Common Core collection has less than 300 records! (key='ncs-NSDL-COLLECTION-000-003-112-016')", (numRecords > 300) );      			
		} catch (DDSServiceErrorResponseException e) {
			serviceException = e;
		}
		
		assertNull(serviceException);
	}	


	
	/* tearDown() gets called after running EACH test method in this class */
	protected void tearDown()
	{
		//prtln("tearDown()");		
	}
		
	
	private static void prtln(String s)
	{
		System.out.println(s);
	}
	
	/* Main method for running this single suite from the command line*/
	public static void main(String [] args)
	{
		junit.textui.TestRunner.run(suite());
	}
	
	
}
