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

package edu.ucar.dls.schemedit.test;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.autoform.*;
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
 *
 *@author     ostwald
 */
public class EditorViewRenderTester extends RenderTester {

	private static boolean debug = true;
	String xmlFormat;
	
	EditorViewRenderTester (String xmlFormat) throws Exception {
		super(xmlFormat);
	}
	
	
	public static void main (String [] args) throws Exception {

		String xmlFormat = "mast_demo";
		if (args.length > 0)
			xmlFormat = args[0];
		
		String renderPath = null;
		renderPath = "/record/general";
		// renderPath = "/annotationRecord/moreInfo";
		if (args.length > 1)
			renderPath = args[1];
			
		boolean showJsp = false;
		boolean dumpJsp = true;
		if (args.length > 2)
			dumpJsp = true;
		
		EditorViewRenderTester rt = new EditorViewRenderTester(xmlFormat);
		if (rt.framework == null) {
			prtln ("FRAMEWORK IS NULL");
			return;
		}
		EditorViewRecord editorViewRecord = null;
		try {
			editorViewRecord = new EditorViewRecord(rt.framework);
		} catch (Exception e) {
			e.printStackTrace();
			prtln(e.getMessage());
			return;
		}
		
		// rt.sh.showSchemaNodeMap();
		// rt.sh.showGlobalDefs();
		
		if (renderPath == null)
			renderPath = "/" + rt.framework.getRootElementName();
		prtln ("renderPath: " + renderPath);
		Element jsp = editorViewRecord.render (renderPath);
		if (showJsp)
			prtln (AutoForm.elementToJsp (jsp));
		if (dumpJsp) {
			String jspStr = AutoForm.elementToJsp (jsp);
			File out = new File ("C:/tmp/EditorViewRenderTester.jsp");
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
			// System.out.println("EditorViewRenderTester: " + s);
			System.out.println(s);
		}
	}

	
}

