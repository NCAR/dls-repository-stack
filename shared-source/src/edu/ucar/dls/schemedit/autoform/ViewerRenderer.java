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

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.autoform.mde.*;
import edu.ucar.dls.schemedit.display.VirtualPageList;

import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.schema.compositor.InlineCompositor;
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
 *  Renders JSP for viewing metadata records within the DCS (as opposed to
 *  within a metadata editor). <p>
 *
 *  Shows only elements that are either required or that have values. Element
 *  labels have "editMe" links that open a metadata editor to the selected
 *  element for editing.
 *
 *
 * @author    ostwald
 *
 *
 */
public class ViewerRenderer extends RendererImpl {
	private static boolean debug = true;

	public ViewerRenderer () {
		setRenderMode (DISPLAY_MODE);
	}

	/**  Render an attribute */
	public void renderAttribute() {
		Element tester = rhelper.viewNodeTest(xpath);

		this.parent.add(tester);
		this.parent = tester;

		MdeAttribute a = new MdeAttribute(this);
		a.render();

	}


	/**  Concrete render method for SimpleType elements */
	public void renderSimpleTypeConcrete() {
		if (getLevel(xpath) > 0) {
			Element tester = rhelper.viewNodeTest(xpath);
			this.parent.add(tester);
			this.parent = tester;
		}

		if (this.typeDef.isAnyType()) {
			MdeAny anyType = new MdeAny(this);
			anyType.render();
		}
		else {
			MdeSimpleType n = new MdeSimpleType(this);
			n.render();
		}
	}


	/**
	 *  Render a choice Compositor
	 *
	 * @param  choiceElement  NOT YET DOCUMENTED
	 */
	public void renderChoice(Element choiceElement) {

		MdeChoice c = null;
		if (this.sh.isMultiChoiceElement(this.schemaNode))
			c = new MdeMultiChoice(this);
		else
			c = new MdeChoice(this);
		c.render(choiceElement);
	}


	/**
	 *  A complexType is displayed iff: it is required or it satisfies the viewNode
	 *  predicate.<p>
	 *
	 *  approach:
	 *  <li> a tester element (viewNode) implements the jsp to insure it should be
	 *  shown
	 *  <li> the tester node is attached to this nodes parent in the render tree.
	 *  <p>
	 *
	 *
	 */
	public void renderComplexTypeConcrete() {
		// prtln("\renderComplexTypeConcrete()");
		if (getLevel(xpath) < 0) {
			renderSubElements();
			return;
		}

		if (getLevel(xpath) > -1) {
			Element tester = rhelper.viewNodeTest(xpath);
			this.parent.add(tester);
			this.parent = tester;
		}
		MdeComplexType mct = new MdeComplexType(this);
		mct.render();
	}


	/**
	 *  Render a Sequence Compositor
	 *
	 * @param  sequence  The Sequence element
	 */
	public void renderSequence(Element sequence) {
		// prtln("\nrenderSequence()");
		InlineCompositor comp = new InlineCompositor((ComplexType) typeDef, sequence);
		prtln(comp.toString());

		MdeSequence s = new MdeSequence(this);
		s.render(sequence);
	}


	/**  Render a repeating element */
	public void renderRepeatingElement() {
		// prtln("\nrenderRepeatingElement()");
		if (typeDef.isAnyType()) {
			MdeRepeatingAnyType s = new MdeRepeatingAnyType(this);
			s.render();
		}
		else if (typeDef.isSimpleType() || typeDef.isBuiltIn()) {
			MdeRepeatingSimpleType s = new MdeRepeatingSimpleType(this);
			s.render();
		}
		else {
			MdeRepeatingComplexType c = new MdeRepeatingComplexType(this);
			c.render();
		}
	}


	/**
	 *  Render the provided list of subelements
	 *
	 * @param  subElements  list of subElements to render
	 */
	public void renderSubElements(List subElements) {

		MdeComplexType ct = new MdeComplexType(this);
		ct.renderSubElements(subElements);

	}


	/**  Render the subElements of the current typeDefinition */
	public void renderSubElements() {
		if (typeDef != null && typeDef.isAnyType()) {
			MdeAny any = new MdeAny(this);
			any.render();
		}
		else if (typeDef == null || !typeDef.isComplexType()) {
			MdeSimpleType st = new MdeSimpleType(this);
			st.render();
		}
		else {
			MdeComplexType ct = new MdeComplexType(this);
			ct.renderSubElements();
		}

	}

	// ---------- input elements

	/**
	 *  Gets the inputElement attribute of the ViewerRenderer object
	 *
	 * @param  xpath       xpath of inputElement
	 * @param  schemaNode  schemaNode of element
	 * @param  typeDef     typeDef of element
	 * @return             The inputElement value
	 */
	public Element getInputElement(String xpath, SchemaNode schemaNode, GlobalDef typeDef) {
		prtln("getInputElement() with " + xpath);

		Element inputElement = null;

		// built-ins have no typeDef, and are assumed to use TextInput
		if (typeDef == null) {
			// prtln("\t ... no typeDef found for " + normalizedXPath + " assuming built-in and punting with Text Input");
			prtln("\t typeDef is NULL");
			return getStaticSimpleValue(xpath);
		}

		/*
		    sanity check - we must have either a SIMPLE_TYPE,
		    or a COMPLEX_TYPE with simpleContent to render an input Element
		  */
		if (!typeDef.isSimpleType()) {
			if (typeDef.isComplexType()) {
				ComplexType ct = (ComplexType) typeDef;
				if (!ct.hasSimpleContent() && !ct.hasComplexContent()) {
					prtln("getInputElement can only handle SimpleData Types and Enumerated Simple/ComplexContent types at this time!");
					return null;
				}
			}
		}

		if (sh.isEnumerationType(typeDef)) {
			prtln("\t isEnumerationType");
			// prtln ("getInputElement(): typeName: " + typeName + ", xpath: " + xpath + " is an enumeration");
			if (sh.isMultiSelect(schemaNode)) {
				prtln("\t isMultiSelect");
				// only display MultiBoxInput if the parentNodeExists
				return getMultiSelectInput(xpath);
			}
			else {
				return getStaticSimpleValue(xpath);
			}
		}
		else {
			return getStaticSimpleValue(xpath);
		}
	}
	// Input Elements

	/**
	 *  Displays the value of the element at specified xpath
	 *
	 * @param  xpath  xpath of element for which we render the value
	 * @return        The textInput value
	 */
	protected Element getStaticSimpleValue(String xpath) {
		prtln("\t getStaticSimpleValue()");
		Element valueDiv = DocumentHelper.createElement("div")
			.addAttribute("class", "static-value");
		Element valueElement = valueDiv.addElement("bean__write")
			.addAttribute("name", formBeanName)
			.addAttribute("property", "valueOf(" + xpath + ")")
			.addAttribute("filter", "false");
		return valueDiv;
	}


	/**
	 *  Renders a multiSelect input (a set of of checkboxes) as an Element. The
	 *  multiSelect is represented as a HTML table
	 *
	 * @param  xpath  xpath of element for which we render an input
	 * @return        The multiSelectInput as an element
	 */
	public Element getMultiSelectInput(String xpath) {
		prtln("\t getMultiSelectInput()");
		Element valueDiv = DocumentHelper.createElement("div")
			.addAttribute("class", "static-enumerated");

		Element enums = valueDiv.addElement("bean__define")
			.addAttribute("id", "enums")
			.addAttribute("name", formBeanName)
			.addAttribute("property", "enumerationValuesOf(" + xpath + ")");

		Element iterator = valueDiv.addElement("c__forEach")
			.addAttribute("var", "item")
			.addAttribute("items", "${enums}")
			.addAttribute("varStatus", "status");

		Element item = iterator.addElement("div");
		item.setText("${item}");

		return valueDiv;
	}

	// controls
	/**
	 *  View pages do not have conrols!
	 *
	 * @param  xpath  NOT YET DOCUMENTED
	 * @return        null
	 */
	public Element getOptionalItemControl(String xpath) {
		// prtln ("getOptionalItemControl()");
		return DocumentHelper.createElement("div");
	}


	/**
	 *  Gets the deleteController attribute of the ViewerRenderer object
	 *
	 * @param  itemPath     NOT YET DOCUMENTED
	 * @param  elementName  NOT YET DOCUMENTED
	 * @return              The deleteController value
	 */
	public Element getDeleteController(String itemPath, String elementName) {
		return DocumentHelper.createElement("div");
	}

	// --------- labels ----------

	/**
	 *  Gets the simpleTypeLabel attribute of the ViewerRenderer object
	 *
	 * @param  xpath        NOT YET DOCUMENTED
	 * @param  siblingPath  NOT YET DOCUMENTED
	 * @param  indexId      NOT YET DOCUMENTED
	 * @return              The simpleTypeLabel value
	 */
	public SimpleTypeLabel getSimpleTypeLabel(String xpath, String siblingPath, String indexId) {
		SimpleTypeLabel labelObj = super.getSimpleTypeLabel(xpath, siblingPath, indexId);
		labelObj.editMeTag = getEditMeTag(xpath, siblingPath, indexId);
		return labelObj;
	}


	/**
	 *  Gets the complexTypeLabel attribute of the ViewerRenderer object
	 *
	 * @param  xpath        normalized xpath to the field for this label
	 * @param  siblingPath  xpath to reach all siblings
	 * @param  indexId      jsp variable used to generate indicies for repeating
	 *      fields
	 * @return              The complexTypeLabel value
	 */
	public ComplexTypeLabel getComplexTypeLabel(String xpath, String siblingPath, String indexId) {
		ComplexTypeLabel labelObj = super.getComplexTypeLabel(xpath, siblingPath, indexId);
		labelObj.editMeTag = getEditMeTag(xpath, siblingPath, indexId);
		return labelObj;
	}

	/**
	 *  Gets the editMeTag attribute of the ViewerRenderer object
	 *
	 * @param  xpath  NOT YET DOCUMENTED
	 * @return        The editMeTag value
	 */
	protected Element getEditMeTag(String xpath) {
		return getEditMeTag(xpath, null, null);
	}


	/**
	 *  creates a jsp tag (st:dcsViewEditMeLabel) that renders a label as a link
	 *  that will open the metadata editor to this field. <p>
	 *
	 *  If provided, siblingPath parameter includes indexing (e.g., /record/general_${index+1}_)
	 *  to make the proper link to an indexed field.
	 *
	 * @param  xpath        NOT YET DOCUMENTED
	 * @param  siblingPath  NOT YET DOCUMENTED
	 * @param  indexId      NOT YET DOCUMENTED
	 * @return              The editMeTag value
	 */
	protected Element getEditMeTag(String xpath, String siblingPath, String indexId) {
		prtln ("getEditMeTag - " + xpath);
		MetaDataFramework framework = rhelper.getFramework();
		if (framework.getXmlFormat().equals("collection_config") ||
			framework.getXmlFormat().equals("framework_config")) {
			return null;
		}

		String tagPath = (siblingPath != null ? siblingPath : xpath);
		Element editMeTag = DocumentHelper.createElement("st__dcsViewEditMeLabel")
			.addAttribute("reader", "${" + formBeanName + ".docReader}")
			.addAttribute("xpath", tagPath);

		String page = this.rhelper.getFramework().getPage(xpath);
		if (page == null || page.trim().length() == 0)
			prtln ("WARNING: page not found for " + xpath);
		
		editMeTag.addAttribute("page", page);
		return editMeTag;
	}


	/**
	 *  Gets the labelledInputField attribute of the ViewerRenderer object
	 *
	 * @param  xpath       NOT YET DOCUMENTED
	 * @param  label       NOT YET DOCUMENTED
	 * @param  inputField  NOT YET DOCUMENTED
	 * @return             The labelledInputField value
	 */
	public Element getRenderedField(String xpath, Label label, Element inputField) {
		prtln("getRenderedField() - " + xpath);

		// have to define the label so it is accessible within
		return getRenderedField_1(xpath, label, inputField);
	}


	/**
	 *  Gets the labelledInputField_2 attribute of the ViewerRenderer object
	 *
	 * @param  xpath       NOT YET DOCUMENTED
	 * @param  label       NOT YET DOCUMENTED
	 * @param  inputField  NOT YET DOCUMENTED
	 * @return             The labelledInputField_2 value
	 */
	public Element getRenderedField_2(String xpath, Label label, Element inputField) {
		prtln("getRenderedField_2()");

		String page = "";
		try {
			page = xpath.split("/")[rhelper.getFramework().getBaseRenderLevel() - 1];
		} catch (Throwable e) {
			prtln("getEditMeTag couldn't obtain page attribute");
		}

		Element displayFieldTag = DocumentHelper.createElement("mde__displayField")
			.addAttribute("xpath", xpath)
			.addAttribute("page", page)
			.addAttribute("label", label.getText())
			.addAttribute("viewForm", "${viewForm}");
		return displayFieldTag;
	}


	/**
	 *  Formats the label and value for the current node.<p>
	 *
	 *  NOTE: this probably shouldn't be here, since it is misnamed (it does not
	 *  have anything to do with "input"). To fix this problem we have to change
	 *  super classes to differentiate between renders that create edit inputs and
	 *  those that simply display values.
	 *
	 * @param  xpath       NOT YET DOCUMENTED
	 * @param  label       NOT YET DOCUMENTED
	 * @param  inputField  NOT YET DOCUMENTED
	 * @return             The labelledInputField_1 value
	 */
	public Element getRenderedField_1(String xpath, Label label, Element inputField) {
		prtln("getRenderedField()");
		// prtln ("inputField: " + Dom4jUtils.prettyPrint(inputField));


		Element table = DocumentHelper.createElement("table")
			.addAttribute("class", "input-field-table");

		Element row = table.addElement("tr")
			.addAttribute("class", "form-row");

		// LABEL cell
		Element labelCell = row.addElement("td")
			.addAttribute("class", "label-cell");

		rhelper.attachToolHelp(labelCell, xpath);
		labelCell.add(label.getElement());

		// INPUT cell
		Element emptyTest = missingValueTest(xpath);
		Element debugging = emptyTest.addElement("span")
			.addAttribute("class", "element-error-msg");
		debugging.setText("missing value");

		Element notEmptyTest = hasValueTest(xpath);
		notEmptyTest.add(inputField);

		Element inputFieldCell = row.addElement("td")
			.addAttribute("class", "input-cell");
		inputFieldCell.add(emptyTest);
		inputFieldCell.add(notEmptyTest);

		return table;
	}


	/**
	 *  test for whether a node has a value
	 *
	 * @param  xpath  NOT YET DOCUMENTED
	 * @return        NOT YET DOCUMENTED
	 */
	public Element missingValueTest(String xpath) {
		// test to see if child node (the multiSelect) is empty
		Element test = DocumentHelper.createElement("logic__equal")
			.addAttribute("name", formBeanName)
			.addAttribute("property", "nodeIsMissingValue(" + xpath + ")")
			.addAttribute("value", "true");

		return test;
	}


	/**
	 *  test for whether a node has a value
	 *
	 * @param  xpath  NOT YET DOCUMENTED
	 * @return        NOT YET DOCUMENTED
	 */
	public Element hasValueTest(String xpath) {
		// test to see if child node (the multiSelect) is empty
		Element test = DocumentHelper.createElement("logic__notEqual")
			.addAttribute("name", formBeanName)
			.addAttribute("property", "nodeIsMissingValue(" + xpath + ")")
			.addAttribute("value", "true");

		return test;
	}


	/**
	 *  Sets the debug attribute of the ViewerRenderer class
	 *
	 * @param  verbose  The new debug value
	 */
	public static void setDebug(boolean verbose) {
		debug = verbose;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "ViewerRenderer");
		}
	}

}
