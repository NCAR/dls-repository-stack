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
import edu.ucar.dls.xml.XPathUtils;
import edu.ucar.dls.xml.schema.*;

import java.util.Iterator;

import org.dom4j.Node;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;


/**
 *  Renders JSP for Repeating Substitution Group elements for the metadata editor.
 *
 * @author     ostwald<p>
 *
 */
public class MdeRepeatingSubstitutionGroup extends MdeRepeatingNode {
	/**  Description of the Field */
	private static boolean debug = true;

	public MdeRepeatingSubstitutionGroup (RendererImpl renderer) {
		super(renderer);
	}

	/**
	* Does not attempt to preserve order of elements in the instance document, but rather
	groups them by their element name.
	*/
	public void render () {
		prtln ("RENDER - " + xpath);
		String parentPath = XPathUtils.getParentXPath(xpath);
		Iterator subGroup = schemaNode.getSubstitutionGroup().iterator();

		if (isEditMode()) {
		
			while (subGroup.hasNext()) {
				GlobalElement memberDef = (GlobalElement)subGroup.next();
				prtln ("\nSubGroup: " + memberDef.getName());
				String siblingPath = parentPath + "/" + memberDef.getQualifiedInstanceName();
				String indexId = RendererHelper.encodeIndexId(siblingPath);
				String itemPath = siblingPath + "_${" + indexId + "+1}_";
	
				// loop through like-named members, auto-attached to parent
				Element iteration = getIteration(itemPath, siblingPath, indexId);
				insertRepeatingDisplaySetup(iteration);
				parent.add (iteration);
	
				// itemBox encloses a individual item
				Element repeatingContent = getRepeatingContent(itemPath, siblingPath, indexId);
				// the following works because "itemPath" has been defined in the jsp
				// (see getRepeatIteration) prior to the repeating itemBox
				repeatingContent.addAttribute("id", "${sf__pathToId(itemPath)}_box");
				// this.rhelper.attachElementDebugInfo(repeatingContent, "repeating content", "orange");
				
				// attach itemBox as child of the iteration, so each item is displayed
				iteration.add(repeatingContent);
			}
			parent.add (getSubstitutionGroupNewItemControl());
		}
		
		else {
/* 			Element fooberry = DocumentHelper.createElement("h2");
			fooberry.setText("hello world");
			parent.add (fooberry); */
			
			while (subGroup.hasNext()) {
				GlobalElement memberDef = (GlobalElement)subGroup.next();
				prtln ("\nSubGroup: " + memberDef.getName());
				String siblingPath = parentPath + "/" + memberDef.getQualifiedInstanceName();
				String indexId = RendererHelper.encodeIndexId(siblingPath);
				String itemPath = siblingPath + "_${" + indexId + "+1}_";
	
				// loop through like-named members, auto-attached to parent
				Element iteration = getIteration(itemPath, siblingPath, indexId);
				Element repeatingContent = getRepeatingContent(itemPath, siblingPath, indexId);
				iteration.add (repeatingContent);
				parent.add (iteration);
			}
			
/* 			parent.add (repeatingItemsBox);
			iteration = getIteration(itemPath, siblingPath, indexId);
			repeatingItemsBox.add (iteration); */
		}
	}

	/**
	 *  Renders the contents of this node, which are in turn rendered inside an iteration element.
	 *
	 * @param  itemPath  the xpath for this element, with indexing to support iteration
	 * @return           The repeatingContent value
	 */
	protected Element getRepeatingContent(String itemPath, String siblingPath, String indexId) {
		prtln ("getRepeatingContent - " + itemPath);

		Element repeatingContent = getRepeatingContentBox (itemPath);

		String elementName = XPathUtils.getNodeName(siblingPath);
		Element deleteController = renderer.getDeleteController(itemPath, elementName);

		if (typeDef.isBuiltIn() || typeDef.isSimpleType()) {
			prtln ("- getRepeatingContent with simple typedef (" + this.typeDef.getName() + ")");
			SimpleTypeLabel label = renderer.getSimpleTypeLabel(xpath, siblingPath, indexId);
			label.control = deleteController;

			Element fieldElement = DocumentHelper.createElement("div");

			String xpathTmp = xpath;
			xpath = itemPath;
			fieldElement.add(getInputElement());
			repeatingContent.add(getRenderedField(label, fieldElement));
			xpath = xpathTmp;
		}

		else if (typeDef.isComplexType()) {
			prtln ("- getRepeatingContent with complex typedef (" + this.typeDef.getName() + ")");
			ComplexTypeLabel label = renderer.getComplexTypeLabel(xpath, siblingPath, indexId);
			repeatingContent.add(getRenderedNoInputField(label, deleteController));

			// addSubElementsBox so repeating items can be hidden;
			Element subElementsBox = repeatingContent.addElement("div")
				.addAttribute("id", "${id}");
				
			if (getLevel() > 0 && isEditMode()) {
				subElementsBox.addAttribute("style", "display:${"+formBeanName+".collapseBean.displayState};");
			}
			/* this.attachElementDebugInfo(subElementsBox, "subElementsBox: id=${id}", "green"); */
			newRenderer(itemPath, subElementsBox).renderSubElements();
		}
		return repeatingContent;
	}

	protected Element getSubstitutionGroupNewItemControl() {

		Element newItemAction = DocumentHelper.createElement("div")
				.addAttribute("class", "action-button");
		
		//make a drop-down menu with choices, and present as controller
		/*
			do not present control if there are no children
			- this case is handled elsewhere (search for "required choice")
			-- but should it be handled here?
			
			experimental - do NOT present control if child element is
			a choice ...
			
		*/
		Element notEmptyTest = newItemAction.addElement("logic__notEmpty")
			.addAttribute("name", formBeanName)
			.addAttribute("property", "membersOf(" + xpath + "/*)");

		notEmptyTest = newItemAction;

		// tests whether the instance document can accept a new child given
		// schema constraints
		Element canAcceptTest = notEmptyTest.addElement("logic__equal")
			.addAttribute("name", formBeanName)
			.addAttribute("property", "acceptsNewSubstitionGroupMember(" + xpath + ")")
			.addAttribute("value", "true");

		String elementId = "${id}_controller";
		String Name = XPathUtils.getNodeName(normalizedXPath);

		Element newItemPrompt = canAcceptTest.addElement("div");
		newItemPrompt.setText("add " + XPathUtils.getNodeName (xpath));
		rhelper.attachToolHelp(newItemPrompt, "Choose a field to add from the pulldown menu");

		// wrap prompt and select in div
		Element selectWrapper = canAcceptTest.addElement ("div");
		this.embedDebugInfo(selectWrapper, "MdeRepeatingSubstitutionGroup - selectWrapper");


		// tmpArg is initialized to "" so the "-- choice --" option is selected,
		// rather than one of the choices
		Element tmpArgInit = selectWrapper.addElement("jsp__setProperty")
				.addAttribute("name", formBeanName)
				.addAttribute("property", "tmpArg")
				.addAttribute("value", "");

		// the select element for making a choice
		Element newItemSelect = selectWrapper.addElement("html__select")
				.addAttribute("name", formBeanName)
				.addAttribute("property", "tmpArg")
				.addAttribute("onchange", "doNewChoice (this.value)");

		// the permissible choices for this element
		Element newChoiceOptions = newItemSelect.addElement("html__optionsCollection")
				.addAttribute("property", "substitutionGroupOptions(" + xpath + ")");

		rhelper.attachToolHelp(newItemSelect, "Choose an element to create");

		// wrap the newItemAction in a table. the left cell is the controller, and the right is blank
		Element table = DocumentHelper.createElement("table")
			.addAttribute ("class", "input-field-table");

		Element row = table.addElement ("tr")
			.addAttribute ("class", "form-row");

		Element controllerCell = row.addElement ("td")
			.addAttribute ("class", "action-box")
			.addAttribute ("nowrap", "1");

		controllerCell.add (newItemAction);

		Element emptyCell = row.addElement ("td")
			.addAttribute ("width", "95%");
		emptyCell.setText (" ");

		return table;

	}

 	public static void setDebug(boolean bool) {
		debug = bool;
	}

	protected void prtln (String s) {
		String prefix = "MdeRepeatingSubstitutionGroup";
		if (debug) {
			SchemEditUtils.prtln (s, prefix);
		}
	}

}
