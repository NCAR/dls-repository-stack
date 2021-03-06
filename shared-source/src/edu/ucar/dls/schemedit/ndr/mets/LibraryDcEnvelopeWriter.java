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
package edu.ucar.dls.schemedit.ndr.mets;

import edu.ucar.dls.services.dds.toolkit.*;

import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.io.*;
import edu.ucar.dls.schemedit.*;

import edu.ucar.dls.standards.asn.NameSpaceXMLDocReader;
import edu.ucar.dls.schemedit.test.TesterUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.schema.SchemaHelper;
import edu.ucar.dls.xml.schema.DocMap;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.util.Utils;
import edu.ucar.dls.ndr.toolkit.ContentUtils;
import org.dom4j.*;

/**
 * @author    ostwald
 */
public class LibraryDcEnvelopeWriter extends EnvelopeWriter {

	private static boolean debug = true;

	/**
	 *  Constructor for the EnvelopeWriter object<p>
	 TODO: ensure envelope validates against mets schema!
	 *
	 * @param  path  NOT YET DOCUMENTED
	 */

	LibraryDcEnvelopeWriter(String ddsServiceUrl, String collection, File dest ) throws Exception {
		super (ddsServiceUrl, collection, dest);
		this.collection = collection;
	}
	
	public URL getResourceUrl (Document doc) throws Exception {
		Node urlElement = doc.selectSingleNode ("/record/URL");
		if (urlElement == null)
			throw new Exception ("urlElement not found");
		String urlStr = urlElement.getText().trim();
		if (urlStr == null || urlStr.length() == 0)
			throw new Exception ("url not found");
		return new URL (urlStr);
	}
	
	public String getMetadataFormat () {
		return "library_dc";
	}
	
	/**
	 *  The main program for the LibraryDcEnvelopeWriter class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		prtln("Mets Doc");
/* 		String baseUrl = "http://localhost/schemedit/services/ddsws1-1";
		String collection = "1255556992209"; */
		
		String baseUrl = "http://nldr.library.ucar.edu/schemedit/services/ddsws1-1";
		String collection = "technotes";
		String dest = "C:/tmp/technotes-mets/technotes-envelope.xml";
		
		LibraryDcEnvelopeWriter md = new LibraryDcEnvelopeWriter(baseUrl, collection, new File(dest));
		prtln  ("\nfinished!");
	}
	

	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  n  NOT YET DOCUMENTED
	 */
	private static void pp(Node n) {
		prtln(Dom4jUtils.prettyPrint(n));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("LibraryDcEnvelopeWriter: " + s);
			System.out.println(s);
		}
	}

}

