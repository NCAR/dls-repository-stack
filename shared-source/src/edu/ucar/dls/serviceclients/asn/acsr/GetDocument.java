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
import java.util.*;
import java.net.*;
import org.dom4j.*;

/**
 *  Use the ACSRService GetACSRDocument command (see http://www.jesandco.net/asn/asnwebservice/acsrservice.asmx)
 *  to obtain a Standard Document
 *
 * @author    Jonathan Ostwald
 */
public class GetDocument extends ACSRClient {
	private static boolean debug = false;

	private Map argMap = null;


	/**
	 *  Constructor for the GetDocument object using acsrId (which is different from the ASN purl ID)
	 *
	 * @param  acsrId  id specifying document to get.
	 */
	public GetDocument(String acsrId) {
		setDebug(debug);
		argMap = new HashMap();
		argMap.put("DocumentID", acsrId);
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
	 *  Gets the command attribute of the GetDocument object, hardcoded as "GetACSRDocument"
	 *
	 * @return    "GetACSRDocument"
	 */
	public String getCommand() {
		return "GetACSRDocument";
	}


	/**
	 *  Returns a Document containing the RDF element of the SOAP response
	 *
	 * @return                The result value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public Document getResult() throws Exception {
		Document responseDoc = this.getResponse();
		String path = "//*[local-name()='RDF']";
		Element rdf = (Element) responseDoc.selectSingleNode(path);
		if (rdf == null)
			throw new Exception("RDF not found");
		return DocumentHelper.createDocument(rdf.createCopy());
	}


	/**
	 *  The main program for the GetDocument class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		String acsrId = "00a6d3fc-0cad-4048-bafa-bc0be9d2c2cd";

		if (args.length > 0)
			acsrId = args[0];

		GetDocument client = new GetDocument(acsrId);

		Document stdDoc = client.getResult();
		if (stdDoc == null)
			prtln("StdDoc not found");
		else {
			prtln("StdDoc FOUND!");
			pp(stdDoc);
		}
	}

}
