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
 *  Render MetadataEditor inputs for repeating AnyType nodes. <p>
 All existing nodes are rendered, and then an empty input is rendered for a new one.
 *
 * @author     ostwald
 *
 */
public class MdeRepeatingAnyType extends MdeRepeatingNode {
	/**  Description of the Field */
	private static boolean debug = true;
	private String siblingPath = null;
	private String indexId = null;

	/**
	 *  Constructor for the MdeRepeatingAnyType object
	 *
	 * @param  renderer  NOT YET DOCUMENTED
	 */
	public MdeRepeatingAnyType(RendererImpl renderer) {
		super(renderer);

		siblingPath = XPathUtils.getSiblingXPath(xpath);
		indexId = RendererHelper.encodeIndexId(siblingPath);
	}


	/**
	 *  Identical to SimpleJspRenderer.renderRepeatingElement, but adds a
	 *  controller for deleting items`
	 */
	public void render() {
		// prtln("\n renderRepeatingAny() with " + xpath);

		// we don't want to show anything here if the parent doesn't exist!
		Element repeatingItemsBox = DocumentHelper.createElement("div");
		embedDebugInfo(repeatingItemsBox, "MdeRepeatingAnyType - repeatingItemsBox");


		String itemPath = siblingPath + "_${" + indexId + "+1}_";
		Element iteration = getIteration(itemPath, siblingPath, indexId);
		// attach repeatingContent as child of the iteration, so each item is displayed

		Element repeatingContent = getRepeatingContent(itemPath);
		// prtln (Dom4jUtils.prettyPrint(repeatingContent));
		iteration.add(repeatingContent);

		if (isEditMode()) {
			Element parentExists = rhelper.parentNodeExistsTest(xpath);
			parent.add(parentExists);
			parentExists.add(repeatingItemsBox);

			Element hasMemberTest = rhelper.nodeHasMembersTest(xpath);
			repeatingItemsBox.add(hasMemberTest);
			hasMemberTest.add(iteration);
			parentExists.add (newItemInput());
		}
		else {
			Element viewNodeTest = rhelper.viewNodeTest(xpath);
			parent.add(viewNodeTest);
			viewNodeTest.add(repeatingItemsBox);
			repeatingItemsBox.add(iteration);
		}
	}

	Element newItemInput () {
		Element acceptsNewSibling = rhelper.acceptsNewSiblingTest(xpath);
		String itemPath = siblingPath + "_${" + indexId + "+1}_";
		Element id = DocumentHelper.createElement ("bean__define")
			.addAttribute ("id", indexId)
			.addAttribute ("name", this.formBeanName)
			.addAttribute ("property", "memberCountOf(" + xpath + ")");

				// create fieldElement (containing input element)
		Element fieldElement = DocumentHelper.createElement("div");

		// new anyType input element
		int rows = 8;
		Element input = DocumentHelper.createElement("html__textarea")
			.addAttribute("property", "anyTypeValueOf(" + itemPath + ")")
			.addAttribute("style", "width__98%")
			.addAttribute("rows", String.valueOf(rows));

		SimpleTypeLabel label = renderer.getSimpleTypeLabel(itemPath);

		acceptsNewSibling.add (id);
		acceptsNewSibling.add(this.renderer.getRenderedField(itemPath, label, input));
		return acceptsNewSibling;
	}

	/**
	 *  Renders the contents of this node, which are in turn rendered inside an iteration element.
	 *
	 * @param  itemPath  the xpath for this element, with indexing to support iteration
	 * @return           The repeatingContent value
	 */
	protected Element getRepeatingContent(String itemPath) {
		Element repeatingContent = getRepeatingContentBox(itemPath);
		attachElementDebugInfo (repeatingContent, "repeatingAny Field", "red");
		newRenderer(itemPath, repeatingContent).renderSubElements();
		return repeatingContent;
	}



	/**
	 *  Sets the debug attribute of the MdeRepeatingAnyType class
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
		String prefix = "MdeRepeatingAnyType";
		if (debug) {
			SchemEditUtils.prtln(s, prefix);
		}
	}

}
