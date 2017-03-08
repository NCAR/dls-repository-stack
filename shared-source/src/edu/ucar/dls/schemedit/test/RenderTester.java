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

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.autoform.*;
import edu.ucar.dls.schemedit.standards.*;
import edu.ucar.dls.standards.asn.util.AsnCatalog;
import edu.ucar.dls.schemedit.standards.config.*;
import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.io.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.schema.compositor.Compositor;
import edu.ucar.dls.util.Files;
import org.dom4j.*;


/**
 *  Class for testing metadata editor rendering on a specific node of a specific framework
 *
 *@author     ostwald
 */
public class RenderTester {

	private static boolean debug = true;
	MetaDataFramework framework = null;
	SchemaHelper sh = null;
	String xmlFormat;
	
	RenderTester (String xmlFormat) throws Exception {
		this (xmlFormat, null);
	}
	
	RenderTester (String xmlFormat, String stdSuggestConfigPath) throws Exception {
		System.setProperty( "javax.xml.transform.TransformerFactory", 
			"com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl" );
		this.xmlFormat = xmlFormat;
		String configFileDir = TesterUtils.getFrameworkConfigDir();
		String docRoot = TesterUtils.getDocRoot();

		// make sure the prop file really exists
		File configFile = new File(configFileDir, xmlFormat + ".xml");
		String configFilePath = configFile.toString();
		if (!configFile.exists()) {
			prtln("configFile doesn't exist at " + configFilePath);
			return;
		}
		else {
			prtln ("frameworkConfig: " + configFilePath);
			framework = new MetaDataFramework(configFilePath, docRoot);
		}

		try {
			framework.loadSchemaHelper();
		} catch (Exception e) {
			prtln("failed to instantiate SchemaHelper: " + e);
			return;
		}
		sh = framework.getSchemaHelper();
		
		if (stdSuggestConfigPath != null) {
			try {
				SuggestionServiceManager ssMgr = 
					new SuggestionServiceManager (new File (stdSuggestConfigPath));
				StandardsManager stdMgr = 
					ssMgr.createStandardsManager (framework);
				framework.setStandardsManager (stdMgr);
			} catch (Exception e) {
				String errMsg = "Could not instantiate suggestionServiceManager: " + e.getMessage();
				throw new Exception (errMsg);
			}
		}
		
		prtln ("RenderTester instantiated\n");
		prtln ("+++++++++++++++++++++++++++++++++++++++++++++++++++\n");
	}

	
	public static void main (String [] args) throws Exception {
		// RendererHelper.setLogging(true);
		edu.ucar.dls.schemedit.sif.SIFRefIdManager.setPath (
			"C:/Documents and Settings/ostwald/devel/SIF/dcs_config/sifTypePaths");
		String xmlFormat = null;
		String renderPath = null;

		
		xmlFormat = "adn";
		if (args.length > 0)
			xmlFormat = args[0];
		if (args.length > 1)
			renderPath = args[1];
		
		renderPath = "/itemRecord/temporalCoverages/timeAndPeriod";

		if (args.length > 1)
			renderPath = args[1];
			
		boolean showJsp = false;
		boolean dumpJsp = true;
		if (args.length > 2)
			dumpJsp = true;
		
		RenderTester rt = null;
		boolean useStandards = false;
		if (useStandards) {
			// to test with suggestor ...
			String stdConfig = "/Users/ostwald/devel/dcs-repos/dcs_release_test/ncs_suggestionServiceConfig.xml";
			String stdsLib = "/Users/ostwald/tmp/standards-library";
			StandardsRegistry.setLibraryDir (new File (stdsLib));
			String asnCache = "/Users/ostwald/devel/projects/dcs-project/web/WEB-INF/data/ASN-Catalog-Cache";
			AsnCatalog.setCacheDirectory (asnCache);
			rt = new RenderTester(xmlFormat, stdConfig); 
		}
		else {
			rt = new RenderTester(xmlFormat);
		}
		
		AutoForm autoForm = null;
		try {
			autoForm = new AutoForm(rt.framework);
		} catch (Exception e) {
			e.printStackTrace();
			prtln(e.getMessage());
			return;
		}
		
		// SchemaUtils.showSchemaNodeMap(rt.sh);
		// SchemaUtils.showGlobalDefs(rt.sh);
		
		if (renderPath == null)
			renderPath = "/" + rt.framework.getRootElementName();
		prtln ("renderPath: " + renderPath);
		Element jsp = autoForm.render (renderPath);
		if (jsp == null) {
			prtln ("autoForm.render return NULL!");
			return;
		}
		
		String jspStr = AutoForm.elementToJsp (jsp);  // never throws exception
		if ("Pretty Print Failed".equals(jspStr))
			jspStr = jsp.asXML();
		
		if (showJsp) 
			prtln (jspStr);
		if (dumpJsp) {
			
			File dir = new File (TesterUtils.getTmpDir());
			if (!dir.exists()) {
				throw new Exception ("direcory does not exist at " + dir);
			}
			File out = new File (dir, "renderTester.jsp");

			Files.writeFile(jspStr, out);
			prtln ("\njsp dumped to " + out);
		}
	}

	private static void  pp (Node node) {
		prtln (Dom4jUtils.prettyPrint(node));
	}
	
	
	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("RenderTester: " + s);
			System.out.println(s);
		}
	}

	
}

