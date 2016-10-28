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
 *  Renders JSP for a Derived Content Model, which uses the "ComplexContent"
 *  schema element to "extend" or "restrict" an existing ComplexType.
 *
 * @author    ostwald<p>
 *
 *
 */
public class MdeDerivedContentModel extends MdeComplexType {
	/**  Description of the Field */
	private static boolean debug = false;


	/**
	 *  Constructor for the MdeDerivedContentModel object
	 *
	 * @param  renderer  NOT YET DOCUMENTED
	 */
	public MdeDerivedContentModel(RendererImpl renderer) {
		super(renderer);
	}


	/**
	 *  Render a derived content model (complexType) by calling either
	 *  renderExtension or renderRestriction as appropriate.
	 */
	public void render() {

		prtln("renderDerivedContentModel()");
		prtln("\t xpath: " + xpath);
		prtln("\t TypeDef: " + this.typeDef.getQualifiedInstanceName());
		prtln("\t location: " + this.typeDef.getLocation() + "\n");
		prtln(Dom4jUtils.prettyPrint(this.typeDef.getElement()));

		Element complexContent = complexTypeDef.getComplexContent();

		Element extension = complexTypeDef.getExtensionElement();
		Element restriction = complexTypeDef.getRestrictionElement();
		if (extension != null) {
			renderExtension(extension);
			return;
		}
		if (restriction != null) {
			renderRestriction(restriction);
			return;
		}
	}


	/**
	 *  Render a restriction-based derived content model
	 *
	 * @param  restriction  the restriction element of the complexType definition
	 */
	private void renderRestriction(Element restriction) {

		prtln("\tRENDER RESTRICTION");
		prtln("path: " + this.xpath);
		prtln(Dom4jUtils.prettyPrint(restriction));

		Element box = DocumentHelper.createElement("div");
		this.embedDebugInfo(box, "DerivedContentModel box - Restriction");
		parent.add(box);
		parent = box;
		renderSubElements(restriction.elements());
	}


	/**
	 *  Render a extension-based derived content model
	 *
	 * @param  extension  the extension element of the complexType definition
	 */
	private void renderExtension(Element extension) {
		Element box = DocumentHelper.createElement("div");
		this.embedDebugInfo(box, "DerivedContentModel box - Extension");

		parent.add(box);
		parent = box;

		GlobalDef extnType = complexTypeDef.getExtensionType();
		if (extnType == null) {
			prtln("ERROR: renderDerivedContentModel() could not find extension base type");
			return;
		}

		prtln("\t extn Base TypeDef: " + extnType.getQualifiedInstanceName());
		prtln("\t extn Base location: " + extnType.getLocation() + "\n");
		prtln(Dom4jUtils.prettyPrint(extnType.getElement()));

		prtln("inline compositor: " + this.complexTypeDef.isInline());

		/* we don't want to render node label, just children */
		// prtln("rendering subelements of extention TYPE");
		// newRenderer(xpath, parent, extnType).renderNode();
		
		renderSubElements(extnType.getElement().elements());

		prtln("rendering subelements of EXTENTION");
		prtln(Dom4jUtils.prettyPrint(extension));
		renderSubElements(extension.elements());
	}


	/**
	 *  Sets the debug attribute of the MdeDerivedContentModel class
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
		String prefix = "MdeDerivedContentModel";
		if (debug) {
			SchemEditUtils.prtln(s, prefix);
		}
	}

}
