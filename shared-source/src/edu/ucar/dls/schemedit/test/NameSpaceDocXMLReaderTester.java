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
package edu.ucar.dls.schemedit.test;

import edu.ucar.dls.standards.asn.NameSpaceXMLDocReader;

import org.dom4j.*;
import edu.ucar.dls.xml.Dom4jUtils;
import java.util.*;
import java.io.File;
import java.net.*;

/**
* Tester/Demo for the NameSpaceDocXMLReader class
*/
public class NameSpaceDocXMLReaderTester {
	
	private static boolean debug = true;
	
	
	public NameSpaceDocXMLReaderTester ()  {}

	
	public static void main (String[] args) throws Exception {

		// Use an ASN Standards docs
		String urlStr = "http://asn.jesandco.org/resources/S1024B7C.xml";
		NameSpaceXMLDocReader reader = new NameSpaceXMLDocReader (new URL(urlStr));
		
		// print document to standard out
		// pp (reader.getDocument());
		
		// get the title
		String titlePath = "/rdf:RDF/asn:StandardDocument/dc:title";
		String title = reader.getValueAtPath(null, titlePath);
		prtln ("title: " + title);
		
		// get the subject (represented as attribute)
		String subjectPath = "/rdf:RDF/asn:StandardDocument/dcterms:subject/@rdf:resource";
		String subject = reader.getValueAtPath(null, subjectPath);
		prtln ("Subject: " + subject);
	
		// get all the education Level nodes
		List<Node> edLevelNodes = reader.getNodes("/rdf:RDF/asn:StandardDocument/dcterms:educationLevel");
		prtln (edLevelNodes.size() + " edLevel nodes found");
		
	}
	
	private static void pp (Node node) {
		prtln (Dom4jUtils.prettyPrint(node));
	}
	
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("AsnDocument: " + s);
			System.out.println(s);
		}
	}
}
