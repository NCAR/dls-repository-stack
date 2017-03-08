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
package edu.ucar.dls.serviceclients.asn.acsr;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.Files;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.dom4j.*;

/**
 *  Use the ACSRService "GetDocuments" command to obtain a list of Standards
 *  Documents by jurisdiction and subject. See http://www.jesandco.net/asn/asnwebservice/acsrservice.asmx.
 *
 * @author    Jonathan Ostwald
 */
public class GetDocuments extends ACSRClient {
	private static boolean debug = false;

	private Map argMap = null;
	private String jurisdiction = null;
	private String subject = null;


	/**
	 *  Constructor for the GetDocuments object for specified jurisdiction
	 *
	 * @param  jurisdiction  the jurisdiction (e.g., "TN")
	 */
	public GetDocuments(String jurisdiction) {
		this(jurisdiction, null);
	}


	/**
	 *  Constructor for the GetDocuments object for specified jurisdiction and subject
	 *
	 * @param  jurisdiction  the jurisdiction (e.g., "TN")
	 * @param  subject       the subject (e.g., "Science")
	 */
	public GetDocuments(String jurisdiction, String subject) {
		setDebug(debug);
		this.jurisdiction = (jurisdiction != null ? jurisdiction : "");
		this.subject = (subject != null ? subject : "");

		this.argMap = new HashMap();
		this.argMap.put("Jurisdiction", this.jurisdiction);
		this.argMap.put("Subject", this.subject);
	}


	/**
	 *  Hardcoded to return "GetDocuments"
	 *
	 * @return    "GetDocuments"
	 */
	public String getCommand() {
		return "GetDocuments";
	}


	/**
*  Gets the argMap attribute of the GetDocument object, which stores arguments for SOAP request.
	 *
	 * @return    The argMap value
	 */
	public Map getArgMap() {
		return this.argMap;
	}


	/**
	 *  Extracts the document elements from the SOAP response
	 *
	 * @return                List of document elements
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public List getDocElements() throws Exception {
		// for some reason, the document elements have the tag "Jurisdiction"
		String path = "//*[local-name()='Jurisdiction'";
		return this.getResponse().selectNodes(path);
	}


	/**
	 *  Gets the documents as ACSRDocumentBean instances
	 *
	 * @return                list of ACSRDocumentBean instances
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public List getResults() throws Exception {
		List docElements = this.getDocElements();

		List results = new ArrayList();
		for (Iterator i = docElements.iterator(); i.hasNext(); ) {
			AcsrDocumentBean doc = new AcsrDocumentBean((Element) i.next());
			results.add(doc);
		}
		return results;
	}


	/**
	 *  The main program for the GetDocuments class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		debug = true;
		String jurisdiction = "TN";
		String subject = "Science";

		if (args.length > 0)
			jurisdiction = args[0];

		if (args.length > 1)
			subject = args[1];

		GetDocuments client = new GetDocuments(jurisdiction, subject);

		pp(client.getResponse());

		List results = client.getResults();
		prtln(results.size() + " docs found");
		for (Iterator i = results.iterator(); i.hasNext(); ) {
			prtln("");
			AcsrDocumentBean result = (AcsrDocumentBean) i.next();
			prtln(result.toString());
		}
	}

}


