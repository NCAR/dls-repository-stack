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
package edu.ucar.dls.standards.asn;

import org.dom4j.Element;

import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XPathUtils;
import edu.ucar.dls.util.strings.FindAndReplace;
import edu.ucar.dls.schemedit.SchemEditUtils;
import java.util.regex.*;

import java.util.*;
import org.dom4j.*;
/**
 *  Encapsulates a single statement of an ASN Standards Document or ASN Resolver
 *  Response, which are represented as RDF elements, and exposes statement
 *  attributes as Strings and Lists. No resolution is performed on attributes
 *  that are expressed as ASN purl IDs, e.g., "juristiction" is exposed as
 *  "http://purl.org/ASN/scheme/ASNJurisdiction/CO" rather than as "Colorado".
 *
 * @author    Jonathan Ostwald
 */
public class AsnStatement {

	private static boolean debug = true;

	private Element element;
	private String statementId; // jesandco id, e.g., http://asn.jesandco.org/resources/S1009A52
	private String uid; // just the number part of the statementId and asnId, e.g., 'S1009A52'
	private String isPartOf;
	private String description;
	private String itemEducationLevel;
	private String statementNotation; // used (at least) in Common core standards
	private String comment; // used (at least) in Common core standards
	private String listID; // used (at least) in Colo
	private String subject;
	private int startGradeLevel = Integer.MAX_VALUE;
	private int endGradeLevel = Integer.MIN_VALUE;
	private List children = null;

	public String statementLabel;
	public String altStatementNotation;
	public List comments = null;
	public List concepts = null; //(s)
	public List comprisedOf = null;
	public List exactMatch = null;
	public List alignTo = null;


	/**
	 *  Constructor for the AsnStatement object
	 *
	 * @param  e  XML Element representing this node within a Standards Document in
	 *      XML form
	 */
	public AsnStatement(Element e) {
		this.element = e;
		statementId = e.valueOf("@rdf:about");
		uid = edu.ucar.dls.xml.XPathUtils.getLeaf(statementId);
		isPartOf = e.valueOf("dcterms:isPartOf");
		subject = e.valueOf("dcterms:subject/@rdf:resource");
		description = e.valueOf("dcterms:description");
		comments = getValues("asn:comment");
		concepts = getValues("asn:conceptTerm/@rdf:resource");
		comprisedOf = getValues("asn:comprisedOf/@rdf:resource");
		exactMatch = getValues("skos:exactMatch/@rdf:resource");
		alignTo = getValues("asn:alignTo/@rdf:resource");
		children = getChildrenIDs();
		setGradeLevels();
		statementNotation = e.valueOf("asn:statementNotation");
		this.statementLabel = e.valueOf("asn:statementLabel");
		this.altStatementNotation = e.valueOf("asn:altStatementNotation");
		this.listID = e.valueOf("asn:listID");
	}

	private List getValues (String name) {
		List<String> thelist = new ArrayList<String>();
		List<Node> nodes = this.element.selectNodes(name);
		for (Node nameEl:nodes)
			thelist.add(nameEl.getText());
		return thelist;
	}
	
	
	/**
	 *  Gets the element attribute of the AsnStatement object
	 *
	 * @return    The element value
	 */
	public Element getElement() {
		return this.element;
	}


	/**
	 *  Gets the childrenIDs (ids are StatementIds, e.g., http://asn.jesandco.org/resources/S10069B9)
	 *  of the AsnStatement object.
	 *
	 * @return    The childrenIDs value
	 */
	public List getChildrenIDs() {
		// prtln ("getChildrenIDs: " + Dom4jUtils.prettyPrint(this.element));
		if (this.children == null) {
			this.children = new ArrayList();
			List childNodes = this.element.selectNodes("gemq:hasChild");
			if (childNodes == null) {
				prtln("no childnotes found");
			}
			else {
				// prtln(childNodes.size() + " children found for " + this.getUid());
				
				for (Iterator i = childNodes.iterator(); i.hasNext(); ) {
					Element childElement = (Element) i.next();
					String childId = childElement.attributeValue("resource");
					// prtln("  " + childId);
					this.children.add(childId);
				}
			}
		}
		return this.children;
	}


	/**
	 *  Gets the description attribute of the AsnStatement object
	 *
	 * @return    The description value
	 */
	public String getDescription() {
		return this.description;
	}


	/**
	 *  Gets the comment attribute of the AsnStatement object
	 *
	 * @return    The comment value
	 */
	public String getComment() {
		return this.comment;
	}


	/**
	 *  Sets the description attribute of the AsnStatement object
	 *
	 * @param  s  The new description value
	 */
	public void setDescription(String s) {
		this.description = s;
	}


	/**
	 *  Gets the subject attribute of the AsnStatement object
	 *
	 * @return    The subject value
	 */
	public String getSubject() {
		return subject;
	}


	/**
	 *  Gets the statementNotation attribute of the AsnStatement object
	 *
	 * @return    The statementNotation value
	 */
	public String getStatementNotation() {
		return this.statementNotation;
	}

	/**
	 *  Gets the listID attribute of the AsnStatement object
	 *
	 * @return    The listID value
	 */
	public String getListID() {
		return this.listID;
	}

	/**
	 *  Sets the gradeLevels attribute of the AsnStatement object Grade levels are
	 *  represented by purls (e.g, http://purl.org/ASN/scheme/ASNEducationLevel/4)
	 *  from which we pluck the leaf (4 in this case).
	 */
	protected void setGradeLevels() {
		int min = startGradeLevel;
		int max = endGradeLevel;
		List levelElements = element.elements("educationLevel");
		for (Iterator i = levelElements.iterator(); i.hasNext(); ) {
			Element levelElement = (Element) i.next();
			String levelStr = XPathUtils.getLeaf(levelElement.attributeValue("resource"));
			int levelInt = -2;
			if (levelStr.toUpperCase().equals("PRE-K")) {
				levelInt = -1;
			}
			else if (levelStr.toUpperCase().equals("K")) {
				levelInt = 0;
			}
			else if (levelStr.equals("Undergraduate-LowerDivision")) {
				levelInt = 14;
			}
			else if (levelStr.equals("Undergraduate-UpperDivision")) {
				levelInt = 16;
			}
			else if (levelStr.equals("ProfessionalEducation-Development")) {
				levelInt = 20;
			}
			else {
				try {
					levelInt = Integer.parseInt(levelStr);
				} catch (Exception e) {
					// prtln("Warning: setGradeLevels could not parse (and therefore ignored): " + levelStr);
					continue;
				}
			}
			max = Math.max(levelInt, max);
			min = Math.min(levelInt, min);
		}
		startGradeLevel = min;
		endGradeLevel = max;
	}


	/**
	 *  Gets the startGradeLevel attribute of the AsnStatement object
	 *
	 * @return    The startGradeLevel value
	 */
	public int getStartGradeLevel() {
		return startGradeLevel;
	}


	/**
	 *  Gets the endGradeLevel attribute of the AsnStatement object
	 *
	 * @return    The endGradeLevel value
	 */
	public int getEndGradeLevel() {
		return endGradeLevel;
	}


	/**
	 *  Gets the ADN id for this statement (e.g., http://purl.org/ASN/resources/S100A71E)
	 *
	 * @return    The id value
	 */
	public String getId() {
		return statementId;
	}


	/**
	 *  Gets the Jes and co id for this statement
	 *
	 * @return    The id value
	 */
/* 	public String getStatementId() {
		return statementId;
	} */


	/**
	 *  Gets the uid attribute of the AsnStatement object
	 *
	 * @return    The uid value
	 */
	public String getUid() {
		return uid;
	}


	/**
	 *  Gets the gradeRange attribute of the AsnStatement object as a String of the
	 *  form, "&lt;startGradeLevel&gt;-&lt;endGradeLevel&gt;".
	 *
	 * @return    The gradeRange value
	 */
	public String getGradeRange() {
		String low;
		if (this.getStartGradeLevel() == 0) {
			low = "K";
		}
		else if (this.getStartGradeLevel() == -1) {
			low = "Pre-K";
		}
		else {
			low = Integer.toString(getStartGradeLevel());
		}

		return low + "-" + Integer.toString(this.getEndGradeLevel());
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @return    NOT YET DOCUMENTED
	 */
	public String toString() {
		// return Dom4jUtils.prettyPrint (element);
		String s = "\n" + getId();
		s += "\n\t" + "gradeLevel: " + getStartGradeLevel() + " - " + getEndGradeLevel();
//		s += "\n\t" + "isChildOf: " + getParentId();
		s += "\n\t" + "description: " + description;
		s += "\n\t" + "subject: " + this.getSubject();
		List children = getChildrenIDs();
		if (!children.isEmpty()) {
			s += "\n\t" + "children: ";
			for (Iterator i = children.iterator(); i.hasNext(); )
				s += "\n\t\t" + (String) i.next();
		}
		else {
			s += "\n\t" + "NO children";
		}
		return s;
	}


	/**
	 *  Gets the subElementText attribute of the AsnStatement object
	 *
	 * @param  subElementName  NOT YET DOCUMENTED
	 * @return                 The subElementText value
	 */
	public String getSubElementText(String subElementName) {
		return getSubElementText(this.element, subElementName);
	}


	/**
	 *  Gets the textual content of the named subelement of provided element.
	 *
	 * @param  e               element containing subelement
	 * @param  subElementName  name of subelement
	 * @return                 the textual content of named subelement
	 */
	public String getSubElementText(Element e, String subElementName) {
		Element sub = e.element(subElementName);
		if (sub == null) {
			// prtln("getSubElementText could not find subElement for " + subElementName);
			return "";
		}
		return sub.getText();
	}


	/**
	 *  Gets the subElementResource attribute of the AsnStatement object
	 *
	 * @param  subElementName  NOT YET DOCUMENTED
	 * @return                 The subElementResource value
	 */
	public String getSubElementResource(String subElementName) {
		return getSubElementResource(this.element, subElementName);
	}


	/**
	 *  Gets the value of the resource attribute for the named subelement.
	 *
	 * @param  e               element containing subelement
	 * @param  subElementName  name of subelement
	 * @return                 the value of the "resource" attribute of named
	 *      subelement
	 */
	public String getSubElementResource(Element e, String subElementName) {
		Element sub = e.element(subElementName);
		if (sub == null) {
			prtln("WARN: getSubElementResource could not find subElement for " + subElementName);
			// prtln (Dom4jUtils.prettyPrint (e));
			// System.exit(1);
			return "";
		}
		// return sub.attributeValue("resource", "");
		return sub.valueOf("@rdf:resource");
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println("AsnStatement: " + s);
		}
	}
}

