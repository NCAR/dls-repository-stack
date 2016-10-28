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

import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.util.strings.FindAndReplace;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import java.net.URL;
import org.dom4j.*;
import org.dom4j.DocumentHelper;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.AbstractElement;
import org.dom4j.io.SAXReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.FlyweightText;

/**
 *  Class to render an element label for metadata editors and viewers. Label
 *  components, such as collapse widgets, controls and best practice links, are
 *  added to ComplexTypeLabel instance, and then these components are assembled
 *  into jsp by the getElement method.
 *
 *@author    ostwald<p>
 *
 *      $Id $
 */
public class ComplexTypeLabel extends SimpleTypeLabel {
	/**
	 *  Description of the Field
	 */
	protected static boolean debug = false;

	public Element isExpandableTest = null;
	public Element notExpandableTest = null;
	public Element collapseWidget = null;
	int level = -1;
	private boolean isRequiredBranch = false;


	/**
	 *  Gets the expandable attribute of the ComplexTypeLabel object
	 *
	 *@return    The expandable value
	 */
	private boolean isExpandable() {
		return (isExpandableTest != null && notExpandableTest != null && collapseWidget != null);
	}

	public void setRequired () {
		this.isRequiredBranch = true;
	}

	public boolean isRequired () {
		return isRequiredBranch;
	}

	/**
	 *  build up the label Element
	 *
	 *@return    The element value
	 */
	public Element getElement() {
		prtln ("ComplexTypeLabel.getElement()");

		Element labelElement = DocumentHelper.createElement("div");

		if (fieldType != null)
			labelElement.addAttribute ("class", fieldType);

		Element base = labelElement.addElement("div");

		if (isRequired()) {
			base.addAttribute("class", labelClass + " required ");
		}
		else {
			base.addAttribute("class", labelClass);
		}

		if (isExpandable()) {
			base.add(isExpandableTest);
			Element link = isExpandableTest.element("a");
			if (link == null) {
				prtln("WARNING: link element not found in isExpandableTest");
			}
			else {
				link.add(collapseWidget);
				link.setText(labelText);
			}

			base.add(notExpandableTest);
			notExpandableTest.setText(labelText);
		}
		else if (editMeTag != null) {
			base.add(editMeTag);
			editMeTag.addAttribute("label", labelText);
		}

		else {
			Element textHolder = base.addElement("div");
			textHolder.setText(labelText);
		}

		if (bestPractices != null) {
			labelElement.add(bestPractices);
		}

		addDebugInfo (labelElement);

		return labelElement;
	}

	/**
	 *  The main program for the ComplexTypeLabel class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args) {
		ComplexTypeLabel ctl = new ComplexTypeLabel();
		ctl.setText("hello world");
		ctl.setLabelClass("indexed-element-label");
		ctl.setText("i am new text");

		Element bp = DocumentHelper.createElement("span");
		bp.setText("best practices");
		ctl.bestPractices = bp;

	}

	/**
	 *  Description of the Method
	 */
	public void pp() {
		System.out.println(Dom4jUtils.prettyPrint(getElement()));
	}


	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println(s);
		}
	}

}
