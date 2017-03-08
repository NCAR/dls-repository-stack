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

 *
 *@author    Jonathan Ostwald
 */
public class StdElement {

	private static boolean debug = false;

	/**
	 *  NOT YET DOCUMENTED
	 */
	public Element element;
	private String id;
	private String parent;
	private String itemText;
	private List footnotes;


	/**
	 *  Constructor for the StdElement object
	 *
	 *@param  e  XML Element representing this node within a Standards Document in
	 *      XML form
	 */
	public StdElement(Element e) {
		element = e;
		id = getSubElementText("id");
		parent = getSubElementText("parent", null);
		itemText = getSubElementText("itemText");
		footnotes = getFootnotes();
	}

	public List getFootnotes () {
		if (this.footnotes == null) {
			this.footnotes = new ArrayList();
			List footnoteElements = this.element.selectNodes ("footnotes/footnote");
			for (Iterator i=footnoteElements.iterator();i.hasNext();) {
				Element footnoteEl = (Element)i.next();
				String id = footnoteEl.attributeValue("id");
				String footnoteText = footnoteEl.getTextTrim();
				this.footnotes.add (new Footnote (id, footnoteText));
			}
		}
		return this.footnotes;
	}
	
	public String getFootnoteIds () {
		List footnotes = this.getFootnotes();
		String ids = "";
		for (int i=0;i<footnotes.size();i++) {
			Footnote fn = (Footnote)footnotes.get(i);
			ids += fn.id;
			if (i < footnotes.size() -1)
				ids += ",";
		}
		return ids;
	}

	/**
	 *  Gets the description attribute of the StdElement object
	 *
	 *@return    The description value
	 */
	public String getDescription() {
		return getItemText();
	}

	/**
	 *  Gets the id attribute of the StdElement object
	 *
	 *@return    The id value
	 */
	public String getId() {
		return id;
	}


	/**
	 *  Gets the parentId attribute of the StdElement object
	 *
	 *@return    The parentId value
	 */
	public String getParentId() {
		return parent;
	}


	/**
	 *  Gets the text attribute of the StdElement object
	 *
	 *@return    The text value
	 */
	public String getItemText() {
		return itemText;
	}

	protected void setItemText (String text) {
		this.itemText = text;
	}

	/**
	 *  NOT YET DOCUMENTED
	 *
	 *@return    NOT YET DOCUMENTED
	 */
	public String toString() {
		// return Dom4jUtils.prettyPrint (element);
		String s = "\n" + getId();
		s += "\n\t" + "parentId: " + getParentId();
		s += "\n\t" + "itemText: " + getItemText();
		List footnotes = this.getFootnotes();
		if (footnotes.size() > 0) {
			s += "\n\t" + "footnotes";
			for (Iterator i=footnotes.iterator();i.hasNext();) {
				Footnote fn = (Footnote)i.next();
				s += "\n\t  " + fn.id + ": " + fn.text;
			}
		}
		return s;
	}


	public int getEndGradeLevel () {
		try {
			return Integer.parseInt(getSubElementText("endGradeLevel"));
		} catch (Exception e) {
			return -1;
		}
	}
	
	public int getStartGradeLevel() {
		try {
			return Integer.parseInt(getSubElementText("startGradeLevel"));
		} catch (Exception e) {
			return -1;
		}
	}
	
	public String getGradeRange () {
		return Integer.toString(this.getStartGradeLevel()) + "-" + Integer.toString(this.getEndGradeLevel());
	}	

	public String getSubElementText(String subElementName) {
		return getSubElementText(subElementName, "");
	}
	
	/**
	 *  Gets the subElementText attribute of the StdElement object
	 *
	 *@param  subElementName  NOT YET DOCUMENTED
	 *@return                 The subElementText value
	 */
	public String getSubElementText(String subElementName, String defaultVal) {
		return getSubElementText(this.element, subElementName, defaultVal);
	}
	
	public String getSubElementText(Element e, String subElementName) {
		return getSubElementText(e, subElementName, "");
	}
	
	/**
	 *  Gets the textual content of the named subelement of provided element.
	 *
	 *@param  e               element containing subelement
	 *@param  subElementName  name of subelement
	 *@return                 the textual content of named subelement
	 */
	public String getSubElementText(Element e, String subElementName, String defaultVal) {
		Element sub = e.element(subElementName);
		if (sub == null) {
			// prtln("getSubElementText could not find subElement for " + subElementName);
			return defaultVal;
		}
		return sub.getTextTrim();
	}

	public class Footnote {
		
		public String id;
		public String text;
		
		public Footnote (String id, String text) {
			this.id = id;
			this.text = text;
		}
		
		public String getText () {
			return this.text;
		}
		
		public String getId () {
			return this.id;
		}
	}

	/**
	 *  NOT YET DOCUMENTED
	 *
	 *@param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println("StdElement: " + s);
		}
	}
}

