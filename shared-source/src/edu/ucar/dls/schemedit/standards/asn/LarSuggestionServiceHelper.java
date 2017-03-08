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
package edu.ucar.dls.schemedit.standards.asn;

import edu.ucar.dls.standards.asn.AsnConstants;
import edu.ucar.dls.schemedit.standards.CATServiceHelper;
import edu.ucar.dls.schemedit.standards.CATHelperPlugin;

import edu.ucar.dls.schemedit.standards.StandardsDocument;
import edu.ucar.dls.schemedit.standards.StandardsNode;
import edu.ucar.dls.schemedit.standards.StandardsRegistry;
import edu.ucar.dls.schemedit.action.form.SchemEditForm;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XPathUtils;
import edu.ucar.dls.xml.schema.DocMap;
import edu.ucar.dls.schemedit.*;

import edu.ucar.dls.schemedit.display.CollapseBean;
import edu.ucar.dls.schemedit.display.CollapseUtils;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.*;

import java.io.*;
import java.util.*;
import org.apache.struts.util.LabelValueBean;

import java.net.*;

/**
 *  SuggestionsServiceHelper for the CAT REST standards suggestion service,
 *  operating over lar framework, which presents special considerations ...
 *
 * @author    ostwald
 */
public class LarSuggestionServiceHelper extends AsnSuggestionServiceHelper {
	private static boolean debug = false;

	private List newAsnNodes = new ArrayList();
	
	/**
	 *  Constructor for the  object
	 *
	 * @param  sef              the SchemEditForm instance
	 * @param  frameworkPlugin  the framework plugin for the lar framework
	 */
	public LarSuggestionServiceHelper (SchemEditForm sef, CATHelperPlugin frameworkPlugin) {
		super(sef, frameworkPlugin);
		prtln("instantiating ");
		// display selected standards by default
		this.setDisplayContent(this.SELECTED_CONTENT);
		this.setDisplayMode(this.LIST_MODE);
		prtln("instantiated ");
	}

	/**
	 *  Gets the ASN standards (as ASN IDs) that have been selected in the Standards UI. These
	 have a parameterName of "enumerationValuesOf(..)" and are different from the
	 instanceDoc params. The LAR UI creates parameters for ALL instance doc fields
	 at /record/standards/id. This method must filter out the non-asn ids.
	 *
	 * @param  request  NOT YET DOCUMENTED
	 * @return          The selectedCATStandards value
	 */
	private List getSelectedASNStandards(HttpServletRequest request) {
		List selectedASNStds = new ArrayList();

		String paramName = "enumerationValuesOf(" + this.getXpath() + ")";
		String[] selectedStds = request.getParameterValues(paramName);
		if (selectedStds == null || selectedStds.length == 0) {
			// prtln("\tno selected standards found in request");
			return selectedASNStds;
		}
		// prtln("\tselected standards in CAT interface (" + selectedStds.length + ")");
		// prtln ("paramName: " + paramName);
		for (int i = 0; i < selectedStds.length; i++) {
			String id = selectedStds[i].trim();
			if (id.length() > 0) {
				prtln("\t" + id);
				if (AsnConstants.isNormalizedAsnId(id)) {
					// id.startsWith(AsnConstants.ASN_ID_BASE))
					selectedASNStds.add(id);
				}
			}
		}
		// prtln ("returning " + selectedASNStds.size() + " ASN standards");
		return selectedASNStds;
	}

	/**
	* Return the selected ASN standards (in the LAR framework there can be other kinds
	* of standards selected, all at the same path.
	*/
	protected List getSelectedStandards(Document doc) {
		List asnStandards = new ArrayList();
		Iterator stdsIter = super.getSelectedStandards(doc).iterator();
		while(stdsIter.hasNext()) {
			String id = (String)stdsIter.next();
			// if (id.startsWith(AsnConstants.ASN_ID_BASE) || id.startsWith(AsnConstants.ASN_DESIRE2LEARN_BASE)
				// asnStandards.add(id);
			prtln (" - id: " + id);
			if (AsnConstants.isNormalizedAsnId(id))
				asnStandards.add(id);
			else
				prtln ("... not added");
		}
		// prtln ("getSelectedStandards returning " + asnStandards);
		return asnStandards;
	}
	
	/**
	 *  Update instanceDocument to make the ASN elements in the instance doc
	 *  correspond to the selected ASN standards in the Standards UI.<p>
	 *
	 *  Called by SchemEditForm.validate() to pre-process the instanceDoc.
	 *
	 * @param  request        the Request
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public Document rectifyInstanceDoc(HttpServletRequest request) throws Exception {
		// prtln("\n------------------------\nrectifyInstanceDoc");
		this.newAsnNodes = new ArrayList();
		SchemEditForm sef = this.getActionForm();
		DocMap docMap = sef.getDocMap();
		/* 
			selectedASNStds were selected in the Standards(browse) UI. the rectified
			instance doc will have standards elements for these ids.
		*/
		List selectedASNStds = this.getSelectedASNStandards(request);

		if (selectedASNStds.isEmpty()) {
			// prtln("no selectedASNStds found - bailing");
			return docMap.getDocument();
		}
		
		// just for fun ...
		// prtln ("this.xpath: " + this.getXpath());
		// build mapping of assigned asnStandards
		Map assignedASNIds = new HashMap(); // mapping of asnID to standards element

		List asnStandardsElements = docMap.selectNodes("/record/standard[alignment/id/@type='ASN']"); // benchmark elements existing in instanceDoc
		if (asnStandardsElements.isEmpty()) {
			prtln("no asn Elements found");
		}
		else {
			prtln(asnStandardsElements.size() + " asnStandardsElements found");

			for (Iterator i = asnStandardsElements.iterator(); i.hasNext(); ) {
				Element stdsEl = (Element) i.next();
				// prtln (Dom4jUtils.prettyPrint(stdEl));
				Element idEl = (Element)stdsEl.selectSingleNode("alignment/id");
				String stdId = idEl.getTextTrim();
				if (stdId.length() > 0 && selectedASNStds.contains(stdId)) {
					assignedASNIds.put(stdId, stdsEl.createCopy());
				}
			}
		}

		String baseStandardPath = "/record/standard";
		
		/* add a new asnStandard element for each newly selected standard */
		// prtln ("processing " + selectedASNStds.size() + " selectedASNStds ...");
		for (Iterator i = selectedASNStds.iterator(); i.hasNext(); ) {
			String selectedStd = (String) i.next();
			if (!assignedASNIds.containsKey(selectedStd)) {

				int index = docMap.selectNodes(baseStandardPath).size();
				String stdPath = baseStandardPath;
				if (index > 0)
					stdPath += "[" + (index + 1) + "]";

				// docMap.createNewNode returns element with schema-required element alignment as child
				Element newAsnStandards = (Element) docMap.createNewNode(stdPath);
				Element alignment = newAsnStandards.element("alignment");
				Element idElement = alignment.addElement("id");
				idElement.setText(selectedStd);
				idElement.setAttributeValue("type", "ASN");
				prtln ("newAsnStandards element: " + Dom4jUtils.prettyPrint(newAsnStandards));
				assignedASNIds.put(selectedStd, newAsnStandards.createCopy());
				
				// encode the stdPath so it can be compared with paths as they come from JSP
				newAsnNodes.add (XPathUtils.encodeXPath(stdPath));
			}
		}
		
/* 		// DEBUGGING
		prtln ("-----------------");
		prtln ("assignedASNIds");
		for (Iterator i=assignedASNIds.keySet().iterator();i.hasNext();) {
			String id = (String)i.next();
			Element element = (Element)assignedASNIds.get(id);
			prtln ("\n -- " + id + " --");
			prtln (Dom4jUtils.prettyPrint(element));
		}
		prtln ("-----------------");

		prtln ("\n----------\nAFTER");
		prtln (Dom4jUtils.prettyPrint(docMap.getDocument())); 
*/
		return docMap.getDocument();

	}


	/**
	 *  Initialize the collapse bean to show selected and suggested standards nodes
	 *  in the display specified by "displayContent".
	 *
	 * @param  displayContent  specifies what type of standards display is to be
	 *      updated.
	 * @exception  Exception   NOT YET DOCUMENTED
	 */
	public void updateStandardsDisplay(String displayContent) throws Exception {
		prtln("\n +++++++++++++++++++++");
		prtln("updateStandardsDisplay()");
		prtln("  displayContent: " + displayContent);
		
		prtln (this.newAsnNodes.size() + " new ASN nodes\n");
		for (Iterator i=this.newAsnNodes.iterator();i.hasNext();) {
			prtln ("- " + (String)i.next());
		}
		
		SchemEditForm sef = this.getActionForm();
		StandardsRegistry standardsRegistry = StandardsRegistry.getInstance();

		CollapseBean cb = sef.getCollapseBean();
		StandardsDocument standardsTree = this.getStandardsDocument();

		if (standardsTree == null)
			throw new Exception("standardsTree not found");

		// close all before opening desired (this may not be what we want to do ...)
		for (Iterator i = standardsTree.getNodeList().iterator(); i.hasNext(); ) {
			StandardsNode node = (StandardsNode) i.next();
			if (node.getHasSubList()) {
				String key = CollapseUtils.pairToId(this.getXpath(), node.getId());
				cb.closeElement(key);
			}
		}

		// expose selected nodes
		if (displayContent.equalsIgnoreCase("selected") || displayContent.equalsIgnoreCase("both")) {
			// prtln ("\nexposing selected nodes");
			String[] selected = sef.getEnumerationValuesOf(this.getXpath());
			for (int i = 0; i < selected.length; i++) {
				String id = selected[i];
				StandardsNode node = standardsRegistry.getStandardsNode(id);
				
				// prtln ("id: " + id);
				if (node == null) {
					// don't even warn - this will happen all the time since there are non-asn standards
					// at same path
					prtln("WARNING: selected node not found for \"" + id +
						"\" in standardsTree for " + this.getCurrentDoc());
					continue;
				}
				List ancestors = node.getAncestors();
				for (Iterator a = ancestors.iterator(); a.hasNext(); ) {
					StandardsNode ancestor = (StandardsNode) a.next();
					String key = CollapseUtils.pairToId(this.getXpath(), ancestor.getId());
					cb.openElement(key);
					// prtln ("opened: " + key);
				}
			}
			if (sef.getDocMap().nodeExists(XPathUtils.decodeXPath(this.getXpath())))
				sef.exposeNode(XPathUtils.decodeXPath(this.getXpath()));
		}

		// expose suggested nodes
		if (displayContent.equalsIgnoreCase("suggested") || displayContent.equalsIgnoreCase("both")) {
			// prtln ("\nexposing suggested nodes");
			List suggested = getSuggestedStandards();
			for (Iterator s = suggested.iterator(); s.hasNext(); ) {
				String id = (String) s.next();
				// prtln ("id: " + id);
				StandardsNode node = standardsRegistry.getStandardsNode(id);
				
				if (node == null) {
					// prtln("WARNING: node not found for \"" + id + "\"");
					continue;
				}
				List ancestors = node.getAncestors();
				for (Iterator a = ancestors.iterator(); a.hasNext(); ) {
					StandardsNode ancestor = (StandardsNode) a.next();
					String key = CollapseUtils.pairToId(this.getXpath(), ancestor.getId());
					cb.openElement(key);
				}
			}
			
			String decodedPath = XPathUtils.decodeXPath(this.getXpath());
			prtln ("decodedPath: " + decodedPath);
			if (sef.getDocMap().nodeExists(decodedPath)) {
				prtln ("exposing: " + decodedPath);
				sef.exposeNode(decodedPath);
			}
			else
				prtln ("decoded path does not exist");
		}
		
		/* expose the children (alignment, alignmentDegree) of ASN standard nodes */
 		String [] children = { "alignment", "alignmentDegree" };
		List stdNodes = sef.getDocMap().selectNodes ("/record/standard");
		int index = 0;
		prtln ("setting collapse for " + stdNodes.size() + " standard nodes");

		for (Iterator i=stdNodes.iterator();i.hasNext();) {
			index++;
			Element std = (Element)i.next();
			String stdPath = std.getPath() + "_" + String.valueOf(index) + "_";
			
			// Skip non-ASN standards
			if (std.selectSingleNode ("alignment/id[@type='ASN']") == null) {
				prtln ("skipping " + stdPath + " (non-ASN)");
				continue;
			}

			String key = CollapseUtils.pathToId(stdPath);
			prtln ("- path: " + stdPath + ", key: " + key);
			
			// if there are new nodes, open them and close others
			if (!this.newAsnNodes.isEmpty()) {
				if (this.newAsnNodes.contains(stdPath)) {
					prtln ("  opened " + stdPath);
					cb.openElement(key);
				}
				else {
					prtln ("  closed " + stdPath);
					cb.closeElement(key);
				}
			}
			// open the children of the new nodes
			for (int j=0;j<children.length;j++) {
				String childPath = stdPath + "/" + children[j];
				key = CollapseUtils.pathToId(childPath);
				prtln ("- path: " + childPath + ", key: " + key);
				cb.openElement(key);
			}
		}

		// set hash to first of new nodes (so it will be focused upon)
		if (!this.newAsnNodes.isEmpty()) {
			String firstNewNodeKey = CollapseUtils.pathToId((String)this.newAsnNodes.get(0));
			this.getActionForm().setHash(firstNewNodeKey);
			prtln ("set hash to " + firstNewNodeKey);
		}
		
		prtln("   ... done with updateStandardsDisplay()");
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "LARHelper");
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtlnErr(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "");
		}
	}

}

