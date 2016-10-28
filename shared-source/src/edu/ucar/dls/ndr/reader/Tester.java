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

package edu.ucar.dls.ndr.reader;

import edu.ucar.dls.ndr.apiproxy.NDRConstants;
import edu.ucar.dls.ndr.request.SimpleNdrRequest;
import edu.ucar.dls.ndr.NdrUtils;
import edu.ucar.dls.xml.Dom4jUtils;

import edu.ucar.dls.schemedit.SchemEditUtils;
import org.dom4j.*;
import java.io.File;
import java.net.URL;
import java.util.*;

/**
 *  Base Class for reading NDR responses to GET Requests, used primarily to
 *  support NDR Import operations. Extended to read specific types of NDR
 *  Objects, such as Metadata and MetadataProvider.<p>
 *
 *  NOTE: this implementation localizes the response from the NDR and thereby
 *  throws out all namespace information. This may be too simplistic, since the
 *  NDR responses make heavy use of namespaces to avoid element naming
 *  colisions.
 *
 * @author     ostwald<p>
 *
 *      $Id $
 * @version    $Id: Tester.java,v 1.4 2011/02/15 16:13:35 ostwald Exp $
 * @created    July 13, 2007
 */
public class Tester {

	private static boolean debug = true;

	static void ndrObjectReaderTester () throws Exception  {
		String handle = "2200/test.20080207195827480T";
		prtln ("calling reader");
		NdrObjectReader reader = new NdrObjectReader (handle);
		prtln ("back from reader");
		prtln ("objectType: " + reader.getObjectType());
		prtln ("handle: " + reader.getHandle());
		// pp (reader.getDataStream("dcs_data"));
		getTestNode (reader);
		// pp (reader.getDataStream("dcs_data"));
	}
	
	static void metadataReaderTester () throws Exception {
		String nativeDataStreamFormat = "library_dc";
		String handle = "ndr:21";
		MetadataReader reader = new MetadataReader (handle, nativeDataStreamFormat);
 		prtln ("\nHandle: " + reader.getHandle());
		prtln ("uniqueID property: " + reader.getProperty("nsdl:uniqueID"));
		prtln ("status property: " + reader.getStatus());
		prtln ("isFinal: " + reader.getIsFinal());

		showDataStreams (reader);
		
		pp (reader.getItemRecord());
		prtln ("metadataProvidedBy relationship: " + reader.getRelationship("metadataProvidedBy"));
		prtln ("collectionMetadataFor relationship: " + reader.getRelationship("collectionMetadataFor"));
		pp (reader.getDataStream(null));
	}
	
	static void showDataStreams (NdrObjectReader reader) {
		prtln("\nFormats:");
		for (Iterator i = reader.getFormats().iterator(); i.hasNext(); ) {
			String format = (String) i.next();
			prtln("\n" + format);
			pp(reader.getDataStream(format));
		}
	}
	
	static void parentObjectTester (GroupingObjectReader reader) throws Exception {
		boolean d = SimpleNdrRequest.getDebug();
		SimpleNdrRequest.setDebug (false);
		boolean v = SimpleNdrRequest.getVerbose();
		SimpleNdrRequest.setVerbose (false);
		prtln("\n ServiceDescription:\n\t" + reader.getServiceDescription());
		
		prtln ("\n There are " + reader.getMemberCount() + " ACTIVE members");
/* 		for (Iterator i=reader.getMemberHandles().iterator();i.hasNext();) {
			prtln ("\t" + (String)i.next());
		} */
	
		prtln ("\n There are " + reader.getInactiveMemberCount() + " INACTIVE members");
/* 		for (Iterator i=reader.getInactiveMemberHandles().iterator();i.hasNext();) {
			prtln ("\t" + (String)i.next());
		} */
		SimpleNdrRequest.setDebug (d);
		SimpleNdrRequest.setVerbose (v);
	}
		
	static void metadataProviderReaderTester () throws Exception {
		String mdpHandle = "2200/test.20071205133545566T";
		MetadataProviderReader reader = new MetadataProviderReader(mdpHandle);
		prtln("\n Handle: " + reader.getHandle());
		prtln("\n collectionId: " + reader.getCollectionId());
		prtln("\n collectionName: " + reader.getCollectionName());
		prtln("\n nativeFormat: " + reader.getNativeFormat());
			
		// showDataStreams (reader);
		
		prtln ("\n AggregatedBy: " + reader.getAggregatedBy());
		prtln ("\n metadataProviderFor: " + reader.getMetadataProviderFor());
		prtln ("\n isAuthorizedToChange: " + reader.isAuthorizedToChange());
		
		prtln ("\n Created: " + NdrUtils.formattedDate(reader.getCreated()));
		prtln ("\n LastModified: " + NdrUtils.formattedDate(reader.getLastModified()));
		prtln ("\n setSpec: " + reader.getSetSpec());
		prtln ("\n setName: " + reader.getSetName());
		
		parentObjectTester (reader);
		
		// test the date stuff
		Date threshold = NdrUtils.parseSimpleDateString("2007-12-10");
		if (reader.getLastModified().before(threshold))
			prtln ("this record is DUST");
		else
			prtln ("keep it");
	}
	
	public static void aggregatorReaderTester () throws Exception {
		String aggHandle = "2200/test.20071205133522873T";
		AggregatorReader reader = new AggregatorReader(aggHandle);
		prtln("\nHandle: " + reader.getHandle());
		
		prtln ("\t AggregatorFor: " + reader.getAggregatorFor());
		prtln ("\t mdpHandle: " + reader.getMdpHandle());
		
		prtln ("\t Created: " + NdrUtils.formattedDate(reader.getCreated()));
		prtln ("\t LastModified: " + NdrUtils.formattedDate(reader.getLastModified()));
		prtln ("\t state: " + reader.getState());
		
		parentObjectTester (reader);

	}
	
	public static void agentReader () throws Exception {
		String handle = "2200/NCS";
		AgentReader reader = new AgentReader (handle);

		// pp (reader.doc);
		prtln ("identifier: " + reader.getIdentifier());
		prtln ("type: " + reader.getIdentifierType());

	}
	
	
	/**  Gets the testNode attribute of the NdrObjectReader object */
	static void getTestNode(NdrObjectReader reader) {
		String path = "/ndr:NSDLDataRepository/ndr:NDRObject/ndr:data/ndr:format[@ID='format_dcs_data']";
		// String path = "ndr:NSDLDataRepository/ndr:NDRObject/ndr:properties/fedora-view:createdDate";
		// String path = "//ndr:properties/fedora-view:createdDate";

		XPath xpath = DocumentHelper.createXPath(path);
		xpath.setNamespaceContext(reader.getNsContext());
		Element element = (Element) xpath.selectSingleNode(reader.getDocument());
		if (element == null)
			prtln("element not found for " + path);
		else
			prtln("FOUND: " + element.getText());
	}
	
	
	public static void main (String [] args) throws Exception {
		File propFile = new File ("C:/Documents and Settings/ostwald/devel/ndrServerProps/dls.ndr.properties"); // propFile must be assigned!
		NdrUtils.setup (propFile);
		try {
			// ndrObjectReaderTester();
			metadataReaderTester();
			// metadataProviderReaderTester();
			// aggregatorReaderTester();
			// agentReader();
		} catch (Throwable t) {
			prtln ("TEST ERROR: " + t.getMessage());
		}

		
		System.exit (1);
	}


	/**
	 *  Sets the debug attribute of the NdrObjectReader class
	 *
	 * @param  bool  The new debug value
	 */
	public static void setDebug(boolean bool) {
		debug = bool;
	}


	/**
	 *  Prints a dom4j.Node as formatted string.
	 *
	 * @param  node  NOT YET DOCUMENTED
	 */
	protected static void pp(Node node) {
		prtln(Dom4jUtils.prettyPrint(node));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "");
		}
	}

}

