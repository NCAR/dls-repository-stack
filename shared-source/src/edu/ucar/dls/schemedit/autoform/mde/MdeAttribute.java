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

import java.util.*;
import org.dom4j.Node;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

/**
 *  Renders Attributes in the Metadata Editor .
 *
 * @author     ostwald<p>
 *
 */
public class MdeAttribute  extends MdeNode {
	/**  Description of the Field */
	private static boolean debug = true;

	public MdeAttribute (RendererImpl renderer) {
		super (renderer);
	}

	public void render () {
		if (isEditMode()) {
			render_edit_mode();
		}
		else {
			render_display_mode();
		}
	}

	private void render_display_mode () {


		// we only display an attribute if it's parent exists!
		Element parentExists = this.rhelper.parentNodeExistsTest(xpath);
		parent.add (parentExists);

		attachElementId (parentExists);

		Element box = getDiv() // attribute
			.addAttribute("id", "${id}_box");
		parentExists.add (box);




		// first set typeDef to validating TypeDef - WHY IS THIS NECESSARY?
		typeDef = schemaNode.getValidatingType();
		Element fieldElement = getInputElement();

		// create label Element
		Label label = renderer.getSimpleTypeLabel(xpath);

		// join label and field elements as table
		Element renderedField = getRenderedField(label, fieldElement);
		box.add(renderedField);
	}

	private void render_edit_mode() {

		Element parentExists = rhelper.parentNodeExistsTest(xpath);
		parent.add(parentExists);

		attachElementId (parentExists);

		// create box
		// indent level 1 attributes for virtualPageLists, since they are displayed
		// as if the were level 2. 1/3/12
		Element box = null;
		if (this.getLevel() > 1) {
			box = getDiv();
		}
		else
			box = DocumentHelper.createElement("div");

		box.addAttribute("id", "${id}_box");
		// this.attachElementDebugInfo(box, "attribute box (" + this.getLevel() + ")", "pink");

		// attach box
		parentExists.add(box);

		// create fieldElement (containing input element)
		Element fieldElement = DocumentHelper.createElement("div");
		this.embedDebugInfo(fieldElement, "MdeAttribute - fieldElement");

		// attachMessages(fieldElement);
		// set typeDef to validating TypeDef - WHY IS THIS NECESSARY?
		typeDef = schemaNode.getValidatingType();
		fieldElement.add(getInputElement());

		// create label Element
		Label label = renderer.getSimpleTypeLabel(xpath);

		// join label and field elements as table
		Element renderedField = getRenderedField(label, fieldElement);
		box.add(renderedField);

	}

	public static void setDebug (boolean verbose) {
		debug = verbose;
	}

	protected void prtln (String s) {
		String prefix = "MdeAttribute";
		if (debug) {
			SchemEditUtils.prtln (s, prefix);
		}
	}

}
