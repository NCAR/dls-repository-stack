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
package edu.ucar.dls.schemedit.autoform;

import edu.ucar.dls.serviceclients.webclient.WebServiceClient;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.schemedit.test.TesterUtils;
import edu.ucar.dls.schemedit.MetaDataFramework;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.util.strings.FindAndReplace;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import java.net.URL;
import org.dom4j.Node;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentException;

/**
 *  Supports creation of jsp to view (as opposed to edit) an entire MetaDataRecord within the metadata editor.
 *  Jsp is constructed by the {@link EditorViewerRenderer}.
 *
 * @author     ostwald
 */
public class EditorViewRecord extends DcsViewRecord {
	private static boolean debug = true;

	/**
	 *  Constructor for the EditorViewRecord object
	 *
	 * @param  framework  Description of the Parameter
	 */
	public EditorViewRecord(MetaDataFramework framework) {
		super(framework);
		formBeanName = "sef";
	}


	/**
	 *  Constructor for the Stand-alone DcsViewRecord object, meaning it is created from command line rather than
	 *  via schemedit.
	 *
	 * @param  xmlFormat                  Description of the Parameter
	 * @exception  SchemaHelperException  Description of the Exception
	 * @exception  Exception              NOT YET DOCUMENTED
	 */
	public EditorViewRecord(String xmlFormat)
		 throws Exception, SchemaHelperException {
		super(xmlFormat);
		formBeanName = "sef";
	}


	/**
	 *  The main program for the EditorViewRecord class, to be invoked from the command line for debugging
	 *  purposes.
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(String[] args) throws Exception {
		prtln("\n==========================================================\nDCS View Record:");
		String xmlFormat = "mast_demo";
		String command = "batchRender";
		String xpath = "/record/general";

		try {
			xmlFormat = args[0];
			command = args[1];
			xpath = args[2];
		} catch (Exception e) {}

		EditorViewRecord viewRecord = null;
		try {
			viewRecord = new EditorViewRecord(xmlFormat);
		} catch (Exception e) {
			prtln(e.getMessage());
			return;
		}
		viewRecord.setLogging(false);
		AutoForm.setVerbose(true);

		if (command.equals("batchRender")) {
			prtln("batchRenderAndWrite");
			viewRecord.batchRenderAndWrite();
			return;
		}

		if (command.equals("renderAndWrite")) {
			prtln("renderAndWrite");
			viewRecord.renderAndWrite(xpath);
		}
	}


	/**
	 *  Gets the rendererClassName attribute of the EditorViewRecord object
	 *
	 * @return    The rendererClassName value
	 */
	protected String getRendererClassName() {
		return "EditorViewerRenderer";
	}

	/**
	 *  Contruct a path for the single jsp page, in the case of single-page jsp, or the page that integrates the component jsp pages,
	 * in the case of batch-rendered frameworks.
	 *
	 * @param  unused  NOT YET DOCUMENTED
	 * @return         The jspDest value
	 */
	protected File getJspDest(String unused) {
		String pageName = "viewRecord";
		File autoFormDir = new File(framework.getAutoFormDir());
		return new File(autoFormDir, pageName + ".jsp");
	}

	private String componentDir = "view_mode_pages";

	/**
	 *  Contruct a path for the component jsp pages
	 *
	 * @param  pageName       NOT YET DOCUMENTED
	 * @return                The batchJspDest value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	protected File getBatchJspDest(String pageName) throws Exception {
		String fileName = pageName + ".jsp";
		File dir = new File(framework.getAutoFormDir(), "/" + componentDir);
		return new File(dir, Files.encode(fileName));
	}

	protected String getMasterJspHeader () {
		String header = "<%@ include file=\"/lib/includes.jspf\" %>\n\n";
/* 		header += "<%@ page import=\"edu.ucar.dls.schemedit.display.CollapseBean\" %>\n\n";
		header +=
				"<bean:define id=\"collapseBean\" name=\"" + formBeanName + "\" property=\"collapseBean\" type=\"CollapseBean\" />\n\n"; */
		header += "<%@ include file=\"/lib/recordSummary.jspf\" %>\n\n";
		return header;
	}

	protected String getComponentJspHeader () {
		return "<%@ include file=\"/lib/includes.jspf\" %>\n\n";
	}

	/*
	** Path from Master jsp file to component jsp pages
	*/
	protected String getMasterComponentPath (String pageName) {
		return componentDir + "/" + pageName + ".jsp";
	}



	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln (s, "EditorViewRecord");
		}
	}
}
