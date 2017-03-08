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
package edu.ucar.dls.schemedit.test.tmp;

import edu.ucar.dls.schemedit.test.SchemaRegistry;
// import edu.ucar.dls.schemedit.test.SchemaRegistry.Schema;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.schema.compositor.Compositor;
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.util.strings.*;

import java.io.File;
import java.util.*;
import java.net.URL;
import org.dom4j.*;

public class CdTester {
	private static boolean debug = true;
	
	SchemaHelper schemaHelper = null;
	Document doc = null;
	DocMap docMap = null;
	
	public CdTester (String instanceDocPath) {
		SchemaHelper.setDebug (false);
		StructureWalker.setDebug (false);
		try {
			doc = Dom4jUtils.getXmlDocument(new File (instanceDocPath));
		} catch (Exception e) {
			prtln ("Couldn't parse document at " + instanceDocPath);
			System.exit(1);
		}
		
		SchemaRegistry reg = new SchemaRegistry();
		SchemaRegistry.Schema cdSchema = reg.getSchema("play");
		try {
			URL schemaUrl = new URL (cdSchema.path);
			schemaHelper = new SchemaHelper(schemaUrl, cdSchema.rootElementName);
		} catch (Throwable t) {
			prtln ("could not process schema at " + cdSchema.path);
			System.exit(1);
		}
		
		// doc = schemaHelper.getMinimalDocument();
		
		docMap = new DocMap (doc, schemaHelper);
	}
	
	void putValue (String xpath, String value) {
		try {
			docMap.smartPut(xpath, value);
		} catch (Exception e) {
			prtln ("WARNING: putValue failed: " + e);
		}
	}
	
	public static void main (String [] args) throws Exception {
		String instanceDocPath = "L:/ostwald/tmp/validate-sandbox/CD-000-000-000-075.xml";
		CdTester tester = new CdTester (instanceDocPath);
		
		prtln ("\nMINIMAL DOCUMENT");
		pp (tester.doc);
		String xpath = "cd:cd/cd:artist";
		List nodes = tester.docMap.selectNodes(xpath);
		prtln (nodes.size() + " nodes found at " + xpath);
	}
	
	private static void pp(Node node) {
		prtln(Dom4jUtils.prettyPrint(node));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		// System.out.println("SimpleSchemaHelperTester: " + s);
		System.out.println(s);
	}
}

