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

package edu.ucar.dls.schemedit.autoform.mde;

import edu.ucar.dls.schemedit.autoform.*;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.standards.StandardsManager;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.schema.compositor.InlineCompositor;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.util.strings.FindAndReplace;

import java.util.*;
import org.dom4j.Node;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;


/**
 *  Renders JSP for nodes that are controlled by Standards Manager (in
 *  conjuction with suggestion service).
 *
 * @author    ostwald<p>
 *
 *
 */
public class MdeStdsNode extends MdeSimpleType {
	/**  Description of the Field */
	private static boolean debug = true;
	private StandardsManager standardsManager = null;


	/**
	 *  Constructor for the MdeStdsNode object
	 *
	 * @param  renderer          NOT YET DOCUMENTED
	 * @param  standardsManager  NOT YET DOCUMENTED
	 */
	public MdeStdsNode(RendererImpl renderer, StandardsManager standardsManager) {
		super(renderer);
		this.standardsManager = standardsManager;
	}


	/**  Renders a managed standards field, unless this collection does not allow the suggestion
	service, in which case the field is rendered as a repeating simple type */
	protected void render_edit_mode() {
		Element parentExists = rhelper.parentNodeExistsTest(xpath);
		parent.add(parentExists);
		insertDisplaySetup(parentExists);

		// LABEL - use a complex label so we can make it
		// collapsible. Otherwise, use simpleTypeLabel
		Label label = renderer.getMultiBoxLabel(xpath);

		// add a controller to delete this element
		Element deleteController = renderer.getDeleteController(xpath, "field");
		((SimpleTypeLabel) label).control = deleteController;

		// create box
		Element box = DocumentHelper.createElement("div")
			.addAttribute("id", "${id}_box");
		embedDebugInfo(box, "simpleType");

		// attach box
		parentExists.add(box);

		// create fieldElement (containing input element)
		Element fieldElement = DocumentHelper.createElement("div");
		fieldElement.addAttribute("id", "${id}");

		Element inputElement = null;
		try {
			prtln("rendering as managedStandards_MultiBox (" + normalizedXPath + ")");

			Element helperChoice = DocumentHelper.createElement("c__choose");
			Element helperPresent = DocumentHelper.createElement ("c__when")
				.addAttribute("test", "${not empty "+this.formBeanName+".suggestionServiceHelper}");
			Element otherwise = DocumentHelper.createElement ("c__otherwise");

			helperChoice.add (helperPresent);
			helperChoice.add (otherwise);

			prtln("rendering as managedStandards_MultiBox (" + normalizedXPath + ")");
			Element standardsTag = DocumentHelper.createElement("std__" + standardsManager.getRendererTag())
				.addAttribute("elementPath", "enumerationValuesOf(" + XPathUtils.getSiblingXPath(xpath) + ")")
				.addAttribute("pathArg", XPathUtils.getSiblingXPath(xpath));
			helperPresent.add (standardsTag);

			// render this xpath as a repeatingSimpleType, attached to the "otherwise" clause
			RendererImpl newRenderer = (RendererImpl)newRenderer(xpath, otherwise);
			MdeRepeatingSimpleType s = new MdeRepeatingSimpleType(newRenderer);
			s.render();

			fieldElement.add(helperChoice);
		} catch (Throwable t) {
			prtln("WARNING: getMultiBoxInput() standards error: " + t.getMessage());
			t.printStackTrace();
		}

		box.add(getRenderedField(label, fieldElement));
	}


	/**
	 *  Sets the debug attribute of the MdeStdsNode class
	 *
	 * @param  bool  The new debug value
	 */
	public static void setDebug(boolean bool) {
		debug = bool;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	protected void prtln(String s) {
		String prefix = "MdeStdsNode";
		if (debug) {
			SchemEditUtils.prtln(s, prefix);
		}
	}

}
