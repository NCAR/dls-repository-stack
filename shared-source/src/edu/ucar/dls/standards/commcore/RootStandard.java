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
package edu.ucar.dls.standards.commcore;

import org.dom4j.Element;

import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XPathUtils;
import edu.ucar.dls.util.strings.FindAndReplace;
import java.util.regex.*;

import java.util.*;

/**
 *  Root standard of the Sandards Document representing the top-most standard in
 *  the hierarchy for the Document. Almost identical to the Standard, but
 *  gets its text content from a different element.
 *
 *@author    Jonathan Ostwald
 */
public class RootStandard extends Standard {

	private static boolean debug = false;


	/**
	 *  Constructor for the RootStandard object
	 *
	 *@param  e       NOT YET DOCUMENTED
	 *@param  stdDoc  Description of the Parameter
	 */
	public RootStandard(Element e, StdDocument stdDoc) {
		super(e, stdDoc);
		setItemText(getSubElementText(e, "title"));
	}


	/**
	 *  Gets the level attribute of the RootStandard object
	 *
	 *@return    The level value
	 */
	public int getLevel() {
		return 0;
	}


	/**
	 *  The textual content of this RootStandard
	 *
	 *@return    The displayText value
	 */
	public String getDisplayText() {

		String s = FindAndReplace.replace(this.getItemText(), "<br>", "\n", true);
		return removeEntityRefs(s);
	}


	/**
	 *  Gets the ancestors (an empty list) of the AsnNode object
	 *
	 *@return    The ancestors value
	 */
	public List getAncestors() {
		return new ArrayList();
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 *@param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println("AsnNode: " + s);
		}
	}
}

