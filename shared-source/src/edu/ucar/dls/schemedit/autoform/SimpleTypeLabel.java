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
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.AbstractElement;
import org.dom4j.io.SAXReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.FlyweightText;

/**
 *  Class to render an element label for metadata editors and viewers. Label components, such as controls and
links, are added to SimpleTypeLabel instance, and then these components are assembled into jsp by the getElement method.
 *
 *@author    ostwald<p>
 */
public class SimpleTypeLabel extends Label {
	/**
	 *  Description of the Field
	 */
	protected static boolean debug = false;
	public Element control = null;
	public Element editMeTag = null;
	private boolean requiredField = false;;
	private boolean repeatingField = false;

	/**
	* build up the label Element
	*/
	public Element getElement () {

		Element labelElement = DocumentHelper.createElement ("div");

		if (fieldType != null)
			labelElement.addAttribute ("class", fieldType);

		if (this.isRequired())
			labelClass = labelClass + " required";

		Element textHolder = DocumentHelper.createElement("div");
		if (editMeTag != null) {
			textHolder.add (editMeTag);
			textHolder.addAttribute("class", labelClass);
			labelElement.add (textHolder);

			editMeTag.addAttribute ("label", labelText);
		}
		else {
			textHolder.setText (labelText);
			labelElement.addAttribute("class", labelClass);
			labelElement.add (textHolder);

			if (control != null) {
				labelElement.add (control.createCopy());
			}
		}

		if (bestPractices != null) {
			Element wrapper = DocumentHelper.createElement ("div");

			wrapper.add (labelElement);
			wrapper.add(bestPractices.createCopy());
			labelElement = wrapper;
		}

		addDebugInfo (labelElement);

		return labelElement;
	}

	public void setRepeating () {
		this.repeatingField = true;
	}

	public boolean isRepeating () {
		return repeatingField;
	}

	public void setRequired () {
		this.requiredField = true;
	}

	public void setOptional () {
		this.requiredField = false;
	}

	public boolean isRequired () {
		return requiredField;
	}

	public void report () {
		prtln ("\t labelText: " + labelText);
		prtln ("\t fieldType: " + fieldType);
		prtln ("\t labelClass: " + labelClass);
		prtln ("\t isRequired: " + isRequired());
	}

	public static void main(String[] args) {
		Label stl = new SimpleTypeLabel();
		stl.setText("hello world");

		Element bp = DocumentHelper.createElement("span");
		bp.setText("best practices");
		stl.bestPractices = bp;

		pp (stl.getElement());
		stl.setLabelClass("label-class");
		pp (stl.getElement());
	}

	private static void prtln(String s) {
		if (debug) {
			System.out.println(s);
		}
	}

}
