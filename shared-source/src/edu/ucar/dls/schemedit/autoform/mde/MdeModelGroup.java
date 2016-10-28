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


/**
 *  Renders JSP for metadata editing but is "Simple" in the sense that it provides no control for adding new
 *  elements or deleting optional elements. Used as a superClass for other JSP-based renderers, such as {@link
 *  edu.ucar.dls.schemedit.autoform.DleseEditorRenderer}.
 *
 * @author     ostwald<p>
 *
 */
public class MdeModelGroup  extends MdeComplexType {
	/**  Description of the Field */
	private static boolean debug = false;
	
	public MdeModelGroup (RendererImpl renderer) {
		super(renderer);
	}	
	
	/**
	 *  Render children elements of a ModelGroup compositor.
	 *
	 * @param  group  Description of the Parameter
	 */
	public void render (Element group) {
		
		prtln ("\nrenderModelGroup " + xpath);
		prtln ("\t globalDef element:\n" + typeDef.getElement().asXML());
		prtln ("\t group element:\n" + group.asXML());
		
		// we need to find the typeDef for the referenced group
		String ref = group.attributeValue("ref");
		GlobalDef globalDef = typeDef.getSchemaReader().getGlobalDef (ref);
		
		if (globalDef == null || !globalDef.isModelGroup()) {
			prtln ("\t *** groupDef not found!");
		}
		else {
			ModelGroup groupDef = (ModelGroup) globalDef;
			renderSubElements(groupDef.getChildren());
		}
	}
	
 	public static void setDebug(boolean bool) {
		debug = bool;
	}
	
	protected void prtln (String s) {
		String prefix = "MdeModelGroup";
		if (debug) {
			SchemEditUtils.prtln (s, prefix);
		}
	}

}

