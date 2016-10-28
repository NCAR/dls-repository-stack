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

import edu.ucar.dls.schemedit.autoform.RendererImpl;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.schema.compositor.InlineCompositor;

import org.dom4j.Node;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;


/**
 *  Renders JSP for Sequence compositors in the metadata editor.
 *
 * @author     ostwald<p>
 *
 */
public class MdeSequence  extends MdeComplexType {
	/**  Description of the Field */
	private static boolean debug = true;

	public MdeSequence (RendererImpl renderer) {
		super(renderer);
	}

	public void render(Element sequenceElement) {

		InlineCompositor comp = new InlineCompositor (complexTypeDef, sequenceElement);

		// Is this solely for debugging?
		if (comp.getMaxOccurs() > 1) {
			Element box = DocumentHelper.createElement ("div");
			this.embedDebugInfo(box, "MdeSequence - box");
			parent.add (box);
			parent = box;
		}

		renderSubElements(sequenceElement.elements());
	}

 	public static void setDebug (boolean verbose) {
		debug = verbose;
	}

	protected void prtln (String s) {
		String prefix = "MdeSequence";
		if (debug) {
			SchemEditUtils.prtln (s, prefix);
		}
	}

}
