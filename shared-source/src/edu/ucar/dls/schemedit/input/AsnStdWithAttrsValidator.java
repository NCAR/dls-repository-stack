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
package edu.ucar.dls.schemedit.input;

import edu.ucar.dls.schemedit.action.form.SchemEditForm;
import edu.ucar.dls.schemedit.MetaDataFramework;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.XPathUtils;
import edu.ucar.dls.xml.Dom4jUtils;

import java.util.*;

import org.dom4j.Node;
import org.dom4j.Element;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;

/**
 *  Extension of SchemEditValidator handles frameworks that use the ASN
 *  standards service and also attach attributes to asnStandard Element. In
 *  these cases, normal validation doesn't work because we have fooled the
 *  system into treating the asnStandard node as a repeating element (with no
 *  attributes) and therefore the attributes must be treated specially here to
 *  avoid knock-on repercussions.
 *
 * @author    ostwald
 */
public class AsnStdWithAttrsValidator extends SchemEditValidator {

	private static boolean debug = false;
	private Map pathMap = null;
	private static boolean verbose = false;
	private String asnXpath;


	/**
	 *  Constructor for the AsnStdWithAttrsValidator object
	 *
	 * @param  sef        the Form Bean
	 * @param  framework  the metadata framework of the record to be validated
	 * @param  request    the Request
	 * @param  mapping    the mapping
	 */
	public AsnStdWithAttrsValidator(SchemEditForm sef,
	                                MetaDataFramework framework,
	                                ActionMapping mapping,
	                                HttpServletRequest request) {
		super(sef, framework, mapping, request);
		try {
			asnXpath = framework.getStandardsManager().getXpath();
			prtln("asnXPath: " + asnXpath);
		} catch (Throwable t) {
			prtln("could not determine asnXpath: " + t.getMessage());
		}
	}


	/**
	 *  Gets the asnXpath attribute of the AsnStdWithAttrsValidator object, which
	 *  is defined in the asn Standards configuration for this framework.
	 *
	 * @return    The asnXpath value
	 */
	public String getAsnXpath() {
		return this.asnXpath;
	}


	/**
	 *  The docMap comes to us UN-updated (new elements for selected standards are
	 *  not present since they are not created by the ASN Standards Selector).
	 *  However, the updated elements are present in the InputManager (as
	 *  multivalue fields). This method creates an asn:element mapping to cache
	 *  EXISTING docMap elements for the ASNstandards field. Then all the docMap
	 *  elements for ASNstandards field are removed. Finally, all the existing
	 *  elements are replaced from the cache, and any NEW elements are added from
	 *  the InputManger.<p>
	 *
	 *  NOTE: we only want to do this if there are asnStandardsFields in the input
	 *  manager!
	 */
	private void updateASNStandardsFields() {
		prtln("updateASNStandardsFields");

		// if there are no multivalues then don't do anything!
		Iterator mvIter = this.im.getMultiValueFields().iterator();
		boolean foundAsnField = false;
		while (mvIter.hasNext()) {
			InputField field = (InputField) mvIter.next();
			if (field.getNormalizedXPath().equals(getAsnXpath())) {
				foundAsnField = true;
				break;
			}
		}
		if (!foundAsnField) {
			prtln("no asnFields found - not updating ASNStandards Fields");
			return;
		}

		if (verbose) {
			prtln("---- BEFORE ------");
			if (this.docMap != null) {
				prtln(Dom4jUtils.prettyPrint(this.docMap.getDocument()));
			}
			else {
				prtln("DocMap is NULL");
			}
			prtln("--------------");
		}

		String parentPath = XPathUtils.getParentXPath(this.getAsnXpath());
		Element parent = (Element) this.docMap.selectSingleNode(parentPath);

		/*  cache the current asnStd nodes in asnMap:
			select nodes for for the asnXpath and populate mapping from
			asnId -> asnElement
		*/
		Map asnMap = new HashMap();
		prtln("selecting nodes for " + this.getAsnXpath());
		List asnNodes = this.docMap.selectNodes(this.getAsnXpath());
		prtln("\n" + asnNodes.size() + " asn nodes selected from docMap");
		for (Iterator i = asnNodes.iterator(); i.hasNext(); ) {
			Element asnElement = (Element) i.next();
			prtln("  " + Dom4jUtils.prettyPrint(asnElement));
			asnMap.put(asnElement.getTextTrim(), asnElement.createCopy());
		}

		/* now remove the asnNodes from the docMap (and thus the instanceDoc) */
		try {
			docMap.removeSiblings(this.getAsnXpath());
		} catch (Exception e) {
			// this is not always an error ...
			// prtln("error removing siblings for multivalue: " + e.getMessage());
		}

		/* now process the asnStd fields (obtained from im.getMultiValueFields()).
			the InputManager will have a field for each asnStd element.
			- if a standard already existed in the docMap, the element (along with attrs)
			is cashed and can simply be inserted in the docMap
			- if the standard did NOT exist in the docMap (and therefore is not cached),
			we create a NEW element in the docMap (and we know it can't have children since
			it was just created).
		*/
		for (Iterator i = im.getMultiValueFields().iterator(); i.hasNext(); ) {
			InputField field = (InputField) i.next();

			if (!field.getNormalizedXPath().equals(getAsnXpath())) {
				prtln("skipping: " + field.getXPath());
				continue;
			}
			prtln("\nprocessing field for: " + field.getXPath());

			String siblingXPath = XPathUtils.getSiblingXPath(field.getXPath());

			// add the value of the current field.
			String value = field.getValue();
			prtln("FIELD value: " + value);
			if (value != null && value.trim().length() > 0) {
				Element existing = (Element) asnMap.get(value);
				try {
					if (existing != null) {
						prtln("inserting existing");
						this.docMap.insertElement(existing, parent, this.getAsnXpath());
					}
					else {
						prtln("inserting new");
						// new nodes have no children (attributes) yet, since they have just been selected
						Node newNode = docMap.createNewSiblingNode(this.getAsnXpath());
						newNode.setText(value);
					}
				} catch (Exception e) {
					prtln("failed to add element: " + e.getMessage());
				}
			}
		}
		if (verbose && this.docMap != null) {
			prtln("---- AFTER ------");
			prtln(Dom4jUtils.prettyPrint(this.docMap.getDocument()));
			prtln("--------------");
		}
	}


	/**
	 *  We've told the system that getAsnXpath() is a multiValueField so it can
	 *  work with the ASN Standards Selector, but in reality this field is NOT a
	 *  multivalue because it has attributes, so this version of updateMultiValueFields
	 *  has to avoid processing the getAsnXpath() field. Specifically, {@link
	 *  SchemeEditValidator#updateMultiValueFields()}, would destroy the contents
	 *  of getAsnXpath().
	 */
	public void updateMultiValueFields() {
		prtln("\n------------------\nupdateMultiValueFields()");
		// im.displayMultiValueFields();

		updateASNStandardsFields();

		String currentElementPath = "";
		List mulitValueGroups = new ArrayList();

		// traverse all multivalue fields in the InputManager
		for (Iterator i = im.getMultiValueFields().iterator(); i.hasNext(); ) {
			InputField field = (InputField) i.next();

			if (field.getNormalizedXPath().equals(getAsnXpath())) {
				continue;
			}

			/*
				we are only concerned with fields that are represented as multiboxes. fields
				that are not unbounded are represented as select objects and therefore will
				always have a value and do not need to be processed here
			*/
			boolean hasVocabLayout = false;
			try {
				hasVocabLayout = this.framework.getVocabLayouts().hasVocabLayout(field.getNormalizedXPath());
			} catch (Throwable t) {}
			if (hasVocabLayout) {
				// prtln ("\t .. hasVocabLayout: " + field.getNormalizedXPath());
			}

			if (!field.getSchemaNode().isUnbounded() && !hasVocabLayout) {
				// prtln("  .. not unbounded: continuing");
				continue;
			}
			String siblingXPath = XPathUtils.getSiblingXPath(field.getXPath());

			// is this a new group? groups are identified by the siblingXPath (which identifies all members)
			if (!siblingXPath.equals(currentElementPath)) {
				currentElementPath = siblingXPath;
				mulitValueGroups.add(field);
				// prtln("   added new group: " + field.getFieldName() + "(" + siblingXPath + ")");

				// delete all siblings at the currentElementPath
				try {
					docMap.removeSiblings(currentElementPath);
				} catch (Exception e) {
					// this is not always an error ...
					// prtln("error removing siblings for multivalue: " + e.getMessage());
				}
			}

			// add the value of the current field.
			String value = field.getValue();
			if ((value != null) && (value.trim().length() > 0)) {
				try {
					// prtln("  ... about to create a new node at " + field.getXPath());
					Node newNode = docMap.createNewSiblingNode(field.getXPath());
					newNode.setText(value);
				} catch (Throwable t) {
					prtln("updateMultiValueFields ERROR: " + t.getMessage());
					// t.printStackTrace();
				}
			}
		}
		multiValueFields = mulitValueGroups;
	}


	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "AsnStdWithAttrsValidator");
		}
	}

}

