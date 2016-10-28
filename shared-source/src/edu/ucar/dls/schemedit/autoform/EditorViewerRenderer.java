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
package edu.ucar.dls.schemedit.autoform;

import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
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
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.FlyweightText;

/**
 *  Renders JSP for viewing (rather than editing) XML documents in the MetaData
 *  Editor. <p>
 *
 *  Unlike {@link edu.ucar.dls.schemedit.autoform.ViewerRenderer}, this class
 *  presents best Practice information (when available) for all elements.
 *
 *@author    ostwald
 */
public class EditorViewerRenderer extends ViewerRenderer {
	/**
	 *  Description of the Field
	 */
	private static boolean debug = false;


	/**
	 *  Gets the editMeTag attribute of the EditorViewerRenderer object
	 *
	 *@param  xpath        Description of the Parameter
	 *@param  siblingPath  Description of the Parameter
	 *@param  indexId      Description of the Parameter
	 *@return              The editMeTag value
	 */
	protected Element getEditMeTag(String xpath, String siblingPath, String indexId) {

		String tagPath = (siblingPath != null ? siblingPath : xpath);
		Element editMeTag = DocumentHelper.createElement("st__editorViewEditMeLabel")
				.addAttribute("xpath", tagPath);

		// the destination page in the editor depends on the baseRenderLevel attribute of this framework, which
		// specifies whether the editor is split up into several pages (e.g., adn) or rendered
		// as a single page (e.g., news_opps).
		String page = "";
		try {
			page = this.rhelper.getFramework().getPage(xpath);
		} catch (Throwable e) {
			prtln("getEditMeTag couldn't obtain page attribute");
		}
		prtln("  ... page: " + page);
		editMeTag.addAttribute("page", page);

		return editMeTag;
	}


	/**
	 *  Sets the debug attribute of the EditorViewerRenderer class
	 *
	 *@param  bool  The new debug value
	 */
	public static void setDebug(boolean bool) {
		debug = bool;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println("EditorViewerRenderer: " + s);
		}
	}

}
