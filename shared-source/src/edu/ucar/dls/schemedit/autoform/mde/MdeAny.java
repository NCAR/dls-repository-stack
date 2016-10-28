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

import java.util.*;
import org.dom4j.Node;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

/**
 *  Renders an ANY node.<P>
 *
 *  NOTE: this class is not used and should be considered as a stub only - it
 *  may not even represent the correct approach to handling "any" nodes.
 *
 * @author    ostwald
 *
 *
 */
public class MdeAny extends MdeSimpleType {
	/**  Description of the Field */
	private static boolean debug = false;


	/**
	 *  Constructor for the MdeAny object
	 *
	 * @param  renderer  NOT YET DOCUMENTED
	 */
	public MdeAny(RendererImpl renderer) {
		super(renderer);
	}

	protected void render_edit_mode() {
		super.render_edit_mode();
	}

	// <html:textarea property="elementAt(/annotationRecord/moreInfo/*)" style="width:98%" rows="8" styleId="${id}"/>
	protected Element getInputElement() {
		if (isEditMode()) {
			int rows = 8;
			Element input = DocumentHelper.createElement("html__textarea")
				.addAttribute("property", "anyTypeValueOf(" + xpath + ")")
				.addAttribute("style", "width__98%")
				.addAttribute("rows", String.valueOf(rows));
			return input;
		}
		else {
			Element valueDiv = DocumentHelper.createElement("div")
				.addAttribute("class", "static-value");
			// Must filter xml element!
			Element valueElement = valueDiv.addElement("bean__write")
				.addAttribute("name", formBeanName)
				.addAttribute("property", "anyTypeValueOf(" + xpath + ")")
				.addAttribute("filter", "true");
			return valueDiv;
		}
	}



	/**
	 *  Sets the debug attribute of the MdeAny class
	 *
	 * @param  verbose  The new debug value
	 */
	public static void setDebug(boolean verbose) {
		debug = verbose;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	protected void prtln(String s) {
		String prefix = "MdeAny";
		if (debug) {
			SchemEditUtils.prtln(s, prefix);
		}
	}

}
