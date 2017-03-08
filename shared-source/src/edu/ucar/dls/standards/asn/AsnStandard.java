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
import java.util.regex.*;

import java.util.*;

/**
 *  Wraps {@link AsnStatement} to expose object based hierarchy information,
 *  such as "children" and "ancestors" as AsnStandards rather than ASN IDs, and
 *  to expose resolved ASN purl-based Attributes as human-readable strings, such
 *  as "Science" rather than "http://purl.org/ASN/scheme/ASNTopic/science".
 *
 * @author    Jonathan Ostwald
 */
public class AsnStandard {

	private static boolean debug = true;

	private AsnStatement asnStmnt;
	protected AsnDocument asnDoc;
	protected List children = null;
	protected List ancestors = null;


	/**
	 *  Constructor for the AsnStandard object given an XML Element and the
	 *  containing AsnDocument instance;
	 *
	 * @param  asnDoc    the Document containing this standard
	 * @param  asnStmnt  NOT YET DOCUMENTED
	 */
	public AsnStandard(AsnStatement asnStmnt, AsnDocument asnDoc) {
		this.asnStmnt = asnStmnt;
		this.asnDoc = asnDoc;
	}


	/**
	 *  Gets the stdDocument attribute of the AsnStandard object
	 *
	 * @return    The stdDocument value
	 */
	public AsnDocument getStdDocument() {
		return this.asnDoc;
	}


	/**
	 *  Gets the asnStatement attribute of the AsnStandard object
	 *
	 * @return    The asnStatement value
	 */
	public AsnStatement getAsnStatement() {
		return this.asnStmnt;
	}

	public String getUid() {
		return this.asnStmnt.getUid();
	}

	/**
	 *  Gets the description attribute of the AsnStandard object
	 *
	 * @return    The description value
	 */
	public String getDescription() {
		return this.asnStmnt.getDescription();
	}

	public String getStatementNotation() {
		return this.asnStmnt.getStatementNotation();
	}
	
	public String getAltStatementNotation() {
 		return this.asnStmnt.altStatementNotation;
 	}
	
	public String getStatementLabel() {
 		return asnStmnt.statementLabel;
 	}
	
	/**
	 *  Gets the listID attribute of the AsnStatement object
	 *
	 * @return    The listID value
	 */
	public String getListID() {
		return this.asnStmnt.getListID();
	}
	
	public String getComment() {
		return this.asnStmnt.getComment();
	}
	
	/**
	 *  Gets the id attribute of the AsnStandard object
	 *
	 * @return    The id value
	 */
	public String getId() {
		return this.asnStmnt.getId();
	}


	/**
	 *  Gets the statementId attribute of the AsnStandard object
	 *
	 * @return    The statementId value
	 */
/* 	public String getStatementId() {
		return this.asnStmnt.getStatementId();
	} */


	/**
	 *  Gets the gradeRange attribute of the AsnStandard object
	 *
	 * @return    The gradeRange value
	 */
	public String getGradeRange() {
		return this.asnStmnt.getGradeRange();
	}


	/**
	 *  Gets the documentIdentifier attribute of the AsnStandard object
	 *
	 * @return    The documentIdentifier value
	 */
	public String getDocumentIdentifier() {
		return this.asnDoc.getIdentifier();
	}


	/**
	 *  Gets the std attribute of the AsnStandard object
	 *
	 * @param  id  Description of the Parameter
	 * @return     The std value
	 */
	protected AsnStandard getStd(String id) {
		return asnDoc.getStandard(id);
	}


	/**
	 *  Gets the standardForStatmentId attribute of the AsnStandard object
	 *
	 * @param  statementId  NOT YET DOCUMENTED
	 * @return              The standardForStatmentId value
	 */
	protected AsnStandard getStandardForStatmentId(String statementId) {
		return asnDoc.getStandardForStatmentId(statementId);
	}


	/**
	 *  Gets the author attribute of the AsnStandard object
	 *
	 * @return    The author value
	 */
	public String getAuthor() {
		return this.asnDoc.getAuthor();
	}


	/**
	 *  Gets the topic attribute of the AsnStandard object
	 *
	 * @return    The topic value
	 */
	public String getTopic() {
		return this.asnDoc.getTopic();
	}


	/**
	 *  Returns true if the AsnStandard object is a leaf
	 *
	 * @return    The leaf value
	 */
	public boolean isLeaf() {
		return getChildren().isEmpty();
	}


	/**
	 *  Returns children as AsnStandard instances in same order as the XML Element
	 *  defining this Standard<p>
	 *
	 *  NOTE: in the XML, children are expressed as jesandco urls, rather than as
	 *  asnID purls!
	 *
	 * @return    The children value (never null)
	 */
	public List getChildren() {
		if (this.children == null) {
			this.children = new ArrayList();
			try {
				List childIds = this.asnStmnt.getChildrenIDs();
				if (childIds.isEmpty()) {
					// prtln ("Leaf node: " + this.getId());
					return this.children;
				}
				for (Iterator i = childIds.iterator(); i.hasNext(); ) {
					String childId = (String) i.next();
					// AsnStandard std = this.getStd(childId);

					AsnStandard std = this.getStandardForStatmentId(childId);
					if (std == null) {
						prtln("getChildren (" + this.getId() + ") WARNING: ASN Document error: std not found for " + childId);
						continue;
					}
					this.addChild(std);
				}
			} catch (Throwable t) {
				prtln("trouble getting children for " + this.getId() + ": " + t.getMessage());
				t.printStackTrace();
				System.exit(1);
			}
		}
		return children;
	}


	/**
	 *  Adds a Child to the AsnStandard object
	 *
	 * @param  std  The feature to be added to the Child attribute
	 */
	protected void addChild(AsnStandard std) {
		if (std == null) {
			return;
		}
		if (!children.contains(std)) {
			children.add(std);
		}
	}


	/**
	 *  Gets the startGradeLevel attribute of the AsnStandard object
	 *
	 * @return    The startGradeLevel value
	 */
	public int getStartGradeLevel() {
		return this.asnStmnt.getStartGradeLevel();
	}


	/**
	 *  Gets the endGradeLevel attribute of the AsnStandard object
	 *
	 * @return    The endGradeLevel value
	 */
	public int getEndGradeLevel() {
		return this.asnStmnt.getEndGradeLevel();
	}

	public List getComments () {
		return this.asnStmnt.comments;
	}
	
	public List getConcepts () {
		return this.asnStmnt.concepts;
	}
	
	public List getComprisedOf () {
		return this.asnStmnt.comprisedOf;
	}

	public List getExactMatch () {
		return this.asnStmnt.exactMatch;
	}
	
	public List getAlignTo () {
		return this.asnStmnt.alignTo;
	}
		

	/**
	 *  Gets the parentId attribute of the AsnStandard object
	 *
	 * @return    The parentId value
	 */
	public String getParentId() {
		return this.asnDoc.getParentId(this.getId());
	}


	/**
	 *  Gets the parentStandard attribute of the AsnStandard object
	 *
	 * @return    The parentStandard value
	 */
	public AsnStandard getParentStandard() {
		try {
			return this.asnDoc.getStandard(getParentId());
		} catch (Exception e) {
			prtln("parent standard: " + e.getMessage());
		}
		return null;
	}


	/**
	 *  Removes entityRefs from the provided string
	 *
	 * @param  in  input string
	 * @return     string with entity refs removed
	 */
	public static String removeEntityRefs(String in) {
		Pattern p = Pattern.compile("&[0-9a-zA-Z#]+?;");
		StringBuffer ret = new StringBuffer();
		int ind1 = 0;
		Matcher m = p.matcher(in);

		while (m.find()) {
			int ind2 = m.start();

			ret.append(in.substring(ind1, ind2));

			in = ";" + in.substring(ind2 + m.group().length());
			ind1 = 0;
			m = p.matcher(in);
		}
		ret.append(in.substring(ind1));
		return ret.toString();
	}


	/**
	 *  Walk the ancestor list, adding text from each node
	 *
	 * @return    The displayText value
	 */
	public String getDisplayText() {

		String s = "";
		List aList = getAncestors();
		for (int i = 0; i < aList.size(); i++) {
			AsnStandard std = (AsnStandard) aList.get(i);
			s += std.getDescription();
			s += ": ";
		}
		s += this.getDescription();

		s = FindAndReplace.replace(s, "<br>", "\n", true);
		return removeEntityRefs(s);
	}


	/**
	 *  Gets the ancestors attribute of the AsnStandard object
	 *
	 * @return    The ancestors value
	 */
	public List getAncestors() {
		if (ancestors == null) {
			ancestors = new ArrayList();
			AsnStandard marker = getStd(getParentId());
			while (marker != null) {
				ancestors.add(marker);
				marker = getStd(marker.getParentId());
			}

			Collections.reverse(ancestors);
		}
		return ancestors;
	}


	/**
	 *  Gets the level attribute of the AsnStandard object
	 *
	 * @return    The level value
	 */
	public int getLevel() {
		// return getAncestors().size() + 1;
		return getAncestors().size();
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @return    NOT YET DOCUMENTED
	 */
	public String toString() {
		// return Dom4jUtils.prettyPrint (element);
		String s = "\n" + getId();
		s += "\n\t" + "level: " + getLevel();
		s += "\n\t" + "gradeRange (string): " + this.getGradeRange();
		s += "\n\t" + "gradeLevel: " + getStartGradeLevel() + " - " + getEndGradeLevel();
		if (getStatementNotation() != null)
			s += "\n\t" + "statementNotation: " + getStatementNotation();
		s += "\n\t" + "statementLabel: " + getStatementLabel();
		s += "\n\t" + "isChildOf: " + getParentId();
		s += "\n\t" + "description: " + getDescription();
		s += "\n\n\t" + "displayText: " + getDisplayText();
		if (!this.getAncestors().isEmpty()) {
			s += "\n\t" + "Ancestors";
			for (Iterator i = this.getAncestors().iterator(); i.hasNext(); ) {
				AsnStandard anc = (AsnStandard) i.next();
				s += "\n\t\t" + anc.getId();
			}
		}
		if (!this.getChildren().isEmpty()) {
			s += "\n\t" + "Children";
			for (Iterator i = this.getChildren().iterator(); i.hasNext(); ) {
				AsnStandard anc = (AsnStandard) i.next();
				s += "\n\t\t" + anc.getId();
			}
		}
/* 		
		if (!asnStmnt.comments.isEmpty()) {
			s += "\n\tCOMMENTS";
			s += listToString(asnStmnt.comments, "\t - ");
		}
		else
			s += "\n\tno comments found";
		
		if (!asnStmnt.concepts.isEmpty()) {
			s += "\n\tCONCEPTS";
			s += listToString(asnStmnt.comments, "\t - ");
		}
		else
			s += "\n\tno concepts found";
*/		
		if (!asnStmnt.comprisedOf.isEmpty()) {
			s += "\n\tCOMPRISED_OF";
			s += listToString(asnStmnt.comprisedOf, "\t - ");
		}
		else
			s += "\n\tno comprisedOf found";	
		
		if (!asnStmnt.exactMatch.isEmpty()) {
			s += "\n\tEXACT_MATCH";
			s += listToString(asnStmnt.exactMatch, "\t - ");
		}
		else
			s += "\n\tno exactMatch found";
		
/* 		if (!asnStmnt.alignTo.isEmpty()) {
			s += "\n\tALIGNTO";
			s += listToString(asnStmnt.alignTo, "\t - ");
		}
		else
			s += "\n\tno alignTo found";  */
		
		return s;
	}

	
	private String listToString (List<String> list, String prefix) {
		prtln ("listToString (" + list.size() + ")");
		String s = "";
		for (String val:list)
			s += "\n" + prefix + val;
		prtln (" .. returning \"" + s + "\"");
		return s;
	}
	

	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println("AsnStandard: " + s);
		}
	}
}

