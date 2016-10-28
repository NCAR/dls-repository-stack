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

package edu.ucar.dls.schemedit.ndr.util.integration;

import edu.ucar.dls.schemedit.ndr.util.*;
import edu.ucar.dls.ndr.apiproxy.*;
import edu.ucar.dls.ndr.NdrUtils;
import edu.ucar.dls.ndr.reader.*;
import edu.ucar.dls.ndr.request.*;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.util.strings.FindAndReplace;
import edu.ucar.dls.index.SimpleLuceneIndex;
import org.dom4j.*;
import java.util.*;
import java.io.File;
import java.net.*;

/**
 *  Reads spreadsheet data (xml file created from spreadsheet) with data
 *  supplied by NSDL but augmented from NCS Collect records, with the purpose of
 *  determining overlaps and gaps between the collection management info in both
 *  models.
 *
 * @author    Jonathan Ostwald
 */
public class IntegrationUtils {
	private static boolean debug = true;
	
	public Collection collection = null;

	
	/**
	 *  Constructor for the Collection object
	 *
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public IntegrationUtils(String ncsRecId, String aggHandle) throws Exception {
		this (ncsRecId, aggHandle, null);
	}
		
	public IntegrationUtils(String ncsRecId, String aggHandle, String webServiceBaseUrl) throws Exception {
		try {
			collection = new Collection (ncsRecId, aggHandle, webServiceBaseUrl);
		} catch (Exception e) {
			throw new Exception ("could not get collection: " + e.getMessage());
		}	
	}

	ServiceDescription getTestServiceDescription () {
		NCSCollectReader reader = integrateServiceDescription ();
		return ServiceDescription.makeServiceDescription(reader, NDRConstants.NDRObjectType.AGGREGATOR);
	}
	
	NCSCollectReader integrateServiceDescription () {
		ServiceDescription sd = this.collection.getServiceDescription ();
		Element root = this.collection.ncsRec.doc.getRootElement().createCopy();
		NCSCollectReader newReader = new NCSCollectReader (DocumentHelper.createDocument(root));

		newReader.setBrandURL(sd.getImage().getBrandURL());

		newReader.setImageHeight(sd.getImage().getHeight());
		newReader.setImageWidth(sd.getImage().getWidth());
		
		newReader.setContacts (sd.getContacts());
		
		return newReader;
	}
	
	static Collection getCollection () throws Exception {
		File propFile = null; // propFile must be assigned!
		NdrUtils.setup (propFile);
		
		prtln ("\nCollection ...\n");

		String aggHandle = "2200/test.20080227142005210T";
		String ncsRecId = "NCS7-COLL-000-000-000-002";
		String webServiceBaseUrl = "http://localhost/schemedit/services/";
		IntegrationUtils iUtils = new IntegrationUtils (ncsRecId, aggHandle, webServiceBaseUrl);
		return iUtils.collection;
	}
	
	/**
	* we want to put an arbitray info stream into the Metadata Object for this collection for testing purposes.
	*/
	static void setInfoStream () throws Exception {

		Collection collection = getCollection();
		
		String path = "C:/tmp/info_stream.xml";
		Document doc = Dom4jUtils.getXmlDocument(new File (path));
		InfoStream myInfoStream = new InfoStream (doc.getRootElement().createCopy());
		myInfoStream.setLink ("yaba");
		
		MetadataReader mdReader = collection.nsdlColl.metadata;
		ModifyMetadataRequest request = new ModifyMetadataRequest (mdReader.getHandle());
		
		Element nsdl_dc = mdReader.getDataStream("nsdl_dc");
		pp (nsdl_dc);
		request.setDataStream("nsdl_dc", nsdl_dc);
		request.setDataInfoStream("nsdl_dc", myInfoStream.asElement());
		request.submit();
		
		pp (myInfoStream.asElement());
	}
	
	static void getInfoStream () throws Exception {

		Collection collection = getCollection();
		MetadataReader mdReader = collection.nsdlColl.metadata;
		Element info_stream_element = mdReader.getDataStream("nsdl_dc_info");
		pp (info_stream_element);
		InfoStream infoStream = new InfoStream (info_stream_element);
		pp (infoStream.asElement());
	}
	
	/**
	* we want to put an arbitray info stream into the Metadata Object for this collection
	*/
	static void setServiceDescriptions () throws Exception {

		Collection collection = getCollection();
		
		String path = "C:/tmp/serviceDescription.xml";
		Document doc = Dom4jUtils.getXmlDocument(new File (path));
		ServiceDescription mySD = mySD = new ServiceDescription (doc.getRootElement().createCopy());
		mySD.setImage("http://foo.brand.com/image.jpg", "my bogus title", "100", "25", "my bogus alttext");
		
		String aggHandle = collection.nsdlColl.aggregator.getHandle();
		ModifyAggregatorRequest aggRequest = new ModifyAggregatorRequest (aggHandle);
		mySD.setType (NDRConstants.NDRObjectType.AGGREGATOR.toString());
		aggRequest.addServiceDescriptionCmd(mySD.asElement());
		aggRequest.submit();
		
		String mdpHandle = collection.nsdlColl.mdp.getHandle();
		ModifyMetadataProviderRequest mdpRequest = new ModifyMetadataProviderRequest (mdpHandle);
		mySD.setType (NDRConstants.NDRObjectType.METADATAPROVIDER.toString());
		mdpRequest.addServiceDescriptionCmd(mySD.asElement());
		mdpRequest.submit();
		
/* 		MetadataReader mdReader = collection.nsdlColl.metadata;
		ModifyMetadataRequest request = new ModifyMetadataRequest (mdReader.getHandle());
		
		Element nsdl_dc = mdReader.getDataStream("nsdl_dc");
		pp (nsdl_dc);
		request.setDataStream("nsdl_dc", nsdl_dc);
		request.setDataInfoStream("nsdl_dc", myInfoStream.asElement());
		request.submit(); */
		
		pp (mySD.asElement());
	}	
			
	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  args           NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {

		// setInfoStream();
		setServiceDescriptions();
		// getInfoStream();
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  node  NOT YET DOCUMENTED
	 */
	private static void pp(Node node) {
		prtln(Dom4jUtils.prettyPrint(node));
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		String prefix = null;
		if (debug) {
			NdrUtils.prtln(s, prefix);
		}
	}
	
		
}

