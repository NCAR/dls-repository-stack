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

import edu.ucar.dls.schemedit.autoform.mde.*;

import edu.ucar.dls.xml.XPathUtils;
import edu.ucar.dls.xml.schema.SchemaNode;

import java.util.*;

import org.dom4j.Node;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;


/**
 *  Renders JSP for metadata editing with controls for adding new elements or
 *  deleting optional elements as well as for collapsible elements.
 *
 * @author     ostwald<p>
 *
 */
public class EditorRenderer extends RendererImpl {
	/**  Description of the Field */
	private static boolean debug = true;

	// --------- labels -----------

	/**
	 *  Gets the simpleTypeLabel attribute of the EditorRenderer object
	 *
	 * @param  xpath        NOT YET DOCUMENTED
	 * @param  siblingPath  NOT YET DOCUMENTED
	 * @param  indexId      NOT YET DOCUMENTED
	 * @return              The simpleTypeLabel value
	 */
	public SimpleTypeLabel getSimpleTypeLabel(String xpath, String siblingPath, String indexId) {

		SimpleTypeLabel labelObj = super.getSimpleTypeLabel(xpath, siblingPath, indexId);

		String normalizedXPath = RendererHelper.normalizeXPath(xpath);
		SchemaNode schemaNode = this.sh.getSchemaNode(normalizedXPath);

		// optional, non-repeating derivedTextOnlyModels get a optionalItemContol
		if (schemaNode.isDerivedModel() &&
			!this.sh.isRequiredBranch(schemaNode) &&
			!this.sh.isRepeatingElement(schemaNode)) {
			labelObj.control = getOptionalItemControl(xpath);
		}
		else if (this.sh.isChoiceElement(schemaNode)) {
			labelObj.control = getOptionalItemControl(xpath);
		}

		return labelObj;
	}


	/**
	 *  Create label for a mulitBoxLabel element that will collapse the mulitBox
	 *  input.<P>
	 *
	 *  Based on getComplexTypeLabel, but will always display collapse widget,
	 *  rather than first testing for nodeIsExpandable as getComplexTypeLabel does.
	 *  <P>
	 *
	 *  Depends on the multibox input having an id and display style initialized to
	 *  value of collapseBean.displayState.
	 *
	 * @param  xpath  NOT YET DOCUMENTED
	 * @return        The multiBoxLabel value
	 */
	public Label getMultiBoxLabel(String xpath) {
		ComplexTypeLabel labelObj = getComplexTypeLabel(xpath);
		Element alwaysExpandable = DocumentHelper.createElement("c__if")
			.addAttribute("test", "${true}");
		Element labelLink = alwaysExpandable.addElement("a")
			.addAttribute("href", "javascript__toggleDisplayState(" + RendererHelper.jspQuotedString("${id}") + ");");

		Element notExpandableTest = DocumentHelper.createElement("c__if")
			.addAttribute("test", "${false}");

		labelObj.isExpandableTest = alwaysExpandable.createCopy();
		labelObj.notExpandableTest = notExpandableTest.createCopy();
		labelObj.collapseWidget = getCollapseWidget().createCopy();
		return labelObj;
	}


	/**
	 *  Create a label with collapse widget (if this nodeIsExpandable is true for
	 *  this node.
	 *
	 * @param  xpath        NOT YET DOCUMENTED
	 * @param  siblingPath  NOT YET DOCUMENTED
	 * @param  indexId      NOT YET DOCUMENTED
	 * @return              The complexTypeLabel value
	 */
	public ComplexTypeLabel getComplexTypeLabel(String xpath, String siblingPath, String indexId) {

		ComplexTypeLabel labelObj = super.getComplexTypeLabel(xpath, siblingPath, indexId);

		String itemPath = (indexId == null ? xpath : siblingPath);

		// make expand widget
		if (getLevel(xpath) > 0) {
			// node is expandable clause
			Element nodeIsExpandable = DocumentHelper.createElement("logic__equal")
				.addAttribute("name", formBeanName)
				.addAttribute("property", "nodeIsExpandable(" + itemPath + ")")
				.addAttribute("value", "true");

			// the clickable version
			Element labelLink = nodeIsExpandable.addElement("a")
				.addAttribute("href", "javascript__toggleDisplayState(" + RendererHelper.jspQuotedString("${id}") + ");");

			Element nodeNotExpandable = DocumentHelper.createElement("logic__notEqual")
				.addAttribute("name", formBeanName)
				.addAttribute("property", "nodeIsExpandable(" + itemPath + ")")
				.addAttribute("value", "true");

			// load labelObj with the components it needs to render collapsible node
			labelObj.isExpandableTest = nodeIsExpandable.createCopy();
			labelObj.notExpandableTest = nodeNotExpandable.createCopy();
			labelObj.collapseWidget = getCollapseWidget().createCopy();
		}

		return labelObj;
	}

	// ----------------------------------
	/**
	 *  Render open/close widget for this element.
	 *
	 * @return    The collapseWidget value
	 */
	protected Element getCollapseWidget() {
		Element img_template = DocumentHelper.createElement("img")
			.addAttribute("id", "${id}_img")
			.addAttribute("border", "0")
			.addAttribute("hspace", "3")
			.addAttribute("height", "12")
			.addAttribute("width", "12");

		Element choose = DocumentHelper.createElement("c__choose");

		Element isOpen = choose.addElement("c__when")
			.addAttribute("test", "${" + formBeanName + ".collapseBean.isOpen}");

		Element openedImg = img_template.createCopy();
		isOpen.add(openedImg);
		openedImg.addAttribute("src", "../images/opened.gif");

		Element otherwise = choose.addElement("c__otherwise");
		Element closedImg = (Element) img_template.createCopy();
		otherwise.add(closedImg);
		closedImg.addAttribute("src", "../images/closed.gif");

		return choose;
	}

	/**
	 *  Sets the debug attribute of the EditorRenderer class
	 *
	 * @param  bool  The new debug value
	 */
	public static void setDebug(boolean bool) {
		debug = bool;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			while (s.length() > 0 && s.charAt(0) == '\n') {
				System.out.println("");
				s = s.substring(1);
			}
			System.out.println("EditorRenderer: " + s);
		}
	}



	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private final void prtlnErr(String s) {
		System.err.println("EditorRenderer: " + s);
	}

}
