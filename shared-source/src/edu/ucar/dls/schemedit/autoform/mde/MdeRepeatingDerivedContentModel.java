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
 *  Render MetadataEditor inputs for all complexType elements at a given xpath, as well as a controller for
 adding a sibling.
 *
 * @author     ostwald<p>
 *
 */
public class MdeRepeatingDerivedContentModel  extends MdeRepeatingNode {
	/**  Description of the Field */
	private static boolean debug = false;
	String indexId;
	String siblingPath;
	String itemPath;
	ComplexType complexTypeDef;
	boolean indexRepeatingContentLabels = true;

	public MdeRepeatingDerivedContentModel (RendererImpl renderer) {
		super(renderer);
		// prtln ("initialized MdeRepeatingDerivedContentModel (" + xpath + ")");
		siblingPath = XPathUtils.getSiblingXPath(xpath);
		indexId = RendererHelper.encodeIndexId(siblingPath);
		itemPath = siblingPath + "_${" + indexId + "+1}_";
		complexTypeDef = (ComplexType) typeDef;
	}

	/**
	 *  Identical to SimpleJspRenderer.renderRepeatingElement, but adds a controller
	 *  for deleting items`
	 */
	public void render() {
		prtln("\nrender() with " + xpath);
		// prtln ("mode: " + getMode());

		Element repeatingItemsBox = DocumentHelper.createElement("div");
		this.embedDebugInfo (repeatingItemsBox, "MdeRepeatingDerivedContentModel - repeatingItemsBox");

		Element iteration = null;
		// attachElementDebugInfo(repeatingItemsBox, "repeating Derived Model Box (" + xpath + ")", "blue");

		if (isEditMode()) {

			// we don't want to show anything here if the parent doesn't exist!
			Element parentExists = rhelper.parentNodeExistsTest(xpath);
			parent.add (parentExists);

			parentExists.add (repeatingItemsBox);

			if (!sh.isRepeatingComplexSingleton(normalizedXPath)) {
				prtln ("\t calling emptyRepeatingElement()");
				repeatingItemsBox.add (emptyRepeatingElement());
			}

			Element hasMemberTest = rhelper.nodeHasMembersTest (xpath);
			repeatingItemsBox.add (hasMemberTest);
			iteration = getIteration(itemPath, siblingPath, indexId);
			insertRepeatingDisplaySetup(iteration);
			hasMemberTest.add (iteration);
			Element siblingController = newSiblingController();
			repeatingItemsBox.add (siblingController);
		}
		else {
			parent.add (repeatingItemsBox);
			iteration = getIteration(itemPath, siblingPath, indexId);
			repeatingItemsBox.add (iteration);
		}

		// attach repeatingContent as child of the iteration, so each item is displayed
		Element repeatingContent = getRepeatingContent();

		if (repeatingContent != null)
			iteration.add(repeatingContent);

	}

	/**
	 *  Renders the contents of this node, which are in turn rendered inside an iteration element.
	 *
	 * @return           The repeatingContent value
	 */
	protected Element getRepeatingContent() {
		// prtln ("getRepeatingContent() with itemPath: " + itemPath);
		Element repeatingContent = getRepeatingContentBox (itemPath);

		Label label;
		if (indexRepeatingContentLabels) {
			label = renderer.getComplexTypeLabel(xpath, itemPath, indexId);
		}
		else {
			label = renderer.getComplexTypeLabel(xpath);
		}

		String elementName = XPathUtils.getNodeName(this.siblingPath);
		Element controller = renderer.getDeleteController(itemPath, elementName);
		repeatingContent.add(getRenderedNoInputField(label, controller));

		Element subElementsBox = repeatingContent.addElement("div");
		subElementsBox.addAttribute("id", "${id}");
		if (getLevel() > 0 && isEditMode()) {
			subElementsBox.addAttribute("style", "display:${"+formBeanName+".collapseBean.displayState};");
			// this.attachElementDebugInfo(subElementsBox, "subElementsBox: id=${id}", "green");
		}

		Element complexContent = complexTypeDef.getComplexContent();

		Element extension = complexTypeDef.getExtensionElement();
		Element restriction = complexTypeDef.getRestrictionElement();

		Renderer modelRender = newRenderer (itemPath, subElementsBox);

		// this.renderer.parent = subElementsBox;
		// parent = subElementsBox;

		if (extension != null) {
			GlobalDef extnType = complexTypeDef.getExtensionType();
			if (extnType == null) {
				prtln("ERROR: renderDerivedContentModel() could not find extension base type");
				return null;
			}
			modelRender.renderSubElements(extnType);
			modelRender.renderSubElements(extension.elements());
		}
		else if (restriction != null) {
			modelRender.renderSubElements(restriction.elements());
		}

		return repeatingContent;
	}

 	public static void setDebug(boolean bool) {
		debug = bool;
	}

	protected void prtln (String s) {
		String prefix = "MdeRepeatingDerivedContentModel";
		if (debug) {
			SchemEditUtils.prtln (s, prefix);
		}
	}

}
