/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package edu.ucar.dls.schemedit.autoform;

import edu.ucar.dls.schemedit.autoform.mde.*;
import edu.ucar.dls.schemedit.standards.StandardsManager;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;

import java.util.*;
import java.util.regex.*;

import org.dom4j.Node;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;

/**
 *  Includes math_path-specific kludges, most notably the /record/general/subjects
 *  field, which requires an editing approach OTHER than than implied by the
 *  schema ...<p>
 *
 *  Elements created by the MathPathEditorRenderer starting with "msp2__" (e.g.,
 *  "msp2__subjects") are rendered tag calls (e.g., "msg:subjects") which are
 *  handled by tag files (e.g., "subjects.tag") in the tags/msp2 directory.
 *
 * @author    ostwald
 */
public class MathPathEditorRenderer extends Msp2EditorRenderer {
	/**  Description of the Field  */
	private static boolean debug = false;

	String subjectsPath = "/record/general/subjects";
	String languagePath = "/record/general/language";


	/**
	 *  Intercept renderNode calls for certain paths and use jsp tags instead of
	 *  autoform
	 */
	public void renderNode() {
		if (subjectsPath.equals(xpath)) {
			prtln("MathPathEditorRenderer got SUBJECTS node");
			renderSubjectsNode();
		}
		else if (languagePath.equals(xpath)) {
			renderLanguageNode();
		}
		else {
			super.renderNode();
		}
	}


	/**  Custom renderer for the Subjects node using a tag file.  */
	protected void renderSubjectsNode() {
		prtln("renderSubjectsNode()");
		Element subjectTag = DocumentHelper.createElement("math_path__subjects");
		parent.add(subjectTag);
	}


	/**
	 *  Custom renderer for the Subjects node - calls a tag file to render this
	 *  field.
	 */
	private void renderLanguageNode() {
		Element lingoTag = DocumentHelper.createElement("msp2__language");
		parent.add(lingoTag);
	}


	/**
	 *  Sets the debug attribute of the MathPathEditorRenderer class
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
			System.out.println("MathPathEditorRenderer: " + s);
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private final void prtlnErr(String s) {
		System.err.println("MathPathEditorRenderer: " + s);
	}

}
