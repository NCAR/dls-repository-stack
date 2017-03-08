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
import edu.ucar.dls.standards.asn.AsnConstants;

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
 *  operating over comm_anno framework, which presents special considerations ...
 *
 * @author    ostwald
 */
public class AsnWithAttributesServiceHelper extends AsnSuggestionServiceHelper {
	private static boolean debug = false;
	private List newAsnIds = new ArrayList();
	private List asnStdNodeAttributeNames = null;

	/**
	 *  Constructor for the  object
	 *
	 * @param  sef              the SchemEditForm instance
	 * @param  frameworkPlugin  the framework plugin for the comm_anno framework
	 */
	public AsnWithAttributesServiceHelper (SchemEditForm sef, CATHelperPlugin frameworkPlugin) {
		super(sef, frameworkPlugin);
		prtln("instantiating ");
		// display selected standards by default
		this.setDisplayContent(this.SELECTED_CONTENT);
		this.setDisplayMode(this.LIST_MODE);
		prtln("instantiated ");
		this.asnStdNodeAttributeNames = sef.getSchemaHelper().getAttributeNames(this.getXpath());
	}

	private List getNewAsnIds() {

		return newAsnIds;
	}
		
	/**
	* Set newAsnIds to the ids that are selected in the ASN Standards selector but
	* have not net been added to the Doc. The method is called from SchemEditForm.validate
	* so it sees the Instance Doc before it is updated with request params.
	*/
	public void setNewAsnIds (HttpServletRequest request) {
		prtln ("\nsetNewAsnIds()");
		List allAsnIds = this.getSelectedASNIds(request);
		prtln ("\nallAsnIds");
		for (Iterator i=allAsnIds.iterator();i.hasNext();) {
			prtln ("- " + (String)i.next());
		}
		
		
		List asnIdsInDoc = this.getSelectedStandards();
		prtln ("\nasnIdsInDoc");
		for (Iterator i=asnIdsInDoc.iterator();i.hasNext();) {
			prtln ("- " + (String)i.next());
		}

		newAsnIds = new ArrayList();
		for (Iterator i=allAsnIds.iterator();i.hasNext();) {
			String id = (String)i.next();
			if (!asnIdsInDoc.contains(id))
				newAsnIds.add (id);
		}
		
		prtln ("\n ... Set newAsnIds (" + newAsnIds.size() + ")");
		for (Iterator i=newAsnIds.iterator();i.hasNext();) {
			prtln ("- " + (String)i.next());
		}
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
	private List getSelectedASNIds(HttpServletRequest request) {
		// prtln ("getSelectedASNIds()");
		List selectedASNIds = new ArrayList();

		String paramName = "enumerationValuesOf(" + this.getXpath() + ")";
		String[] selectedStds = request.getParameterValues(paramName);
		if (selectedStds == null || selectedStds.length == 0) {
			// prtln("\tno selected standards found in request");
			return selectedASNIds;
		}
		// prtln("\tselected standards in CAT interface (" + selectedStds.length + ")");
		for (int i = 0; i < selectedStds.length; i++) {
			String id = selectedStds[i].trim();
			if (id.length() > 0) {
				// prtln("\t" + id);
				if (id.startsWith(AsnConstants.ASN_ID_BASE))
					selectedASNIds.add(id);
			}
		}
		// prtln ("returning " + selectedASNIds.size() + " ASN standards");
		return selectedASNIds;
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
		
		if (displayContent.equals("list"))
			prtln ("DISPLAY_CONTENT IS LIST!");
			
		super.updateStandardsDisplay(displayContent);
		
		if (displayContent.equals("selected")) {
			prtln ("DISPLAY_CONTENT IS SELECTED!");
		
			List newAsnIds = this.getNewAsnIds();
			
			prtln (newAsnIds.size() + " new ASN nodes\n");
			for (Iterator i=newAsnIds.iterator();i.hasNext();) {
				prtln ("- " + (String)i.next());
			}
			
			SchemEditForm sef = this.getActionForm();
			CollapseBean cb = sef.getCollapseBean();
			StandardsDocument standardsTree = this.getStandardsDocument();
	
			/* expose the attributes (alignment, alignmentDegree) of ASN standard nodes */
			// String [] children = { "alignment", "alignmentComment" };

			List stdNodes = sef.getDocMap().selectNodes (this.getXpath());
			int index = 0;
			prtln ("setting collapse for " + stdNodes.size() + " standard nodes");
			String firstNewStdPath = null;
			
			for (Iterator i=stdNodes.iterator();i.hasNext();) {
				index++;
				Element std = (Element)i.next();
				String stdPath = std.getPath() + "_" + String.valueOf(index) + "_";
				String stdId = std.getTextTrim();
				
				String key = CollapseUtils.pathToId(stdPath);
				prtln ("- path: " + stdPath + ", key: " + key);
				prtln ("  stdId: " + std.getTextTrim());
				
				// if there are new nodes, open them and close others
				if (!newAsnIds.isEmpty()) {
					if (newAsnIds.contains(stdId)) {
						prtln ("  opened " + stdPath);
						cb.openElement(key);
						if (firstNewStdPath == null)
							firstNewStdPath = stdPath;
					}
					else {
						prtln ("  closed " + stdId);
						cb.closeElement(key);
					}
				}
				// open the attributes of the new nodes
				// for (int j=0;j<children.length;j++) {
				Iterator attrIter = this.asnStdNodeAttributeNames.iterator();
				while (attrIter.hasNext()) {
					// String childPath = stdPath + "/" + children[j];
					String childPath = stdPath + "/@" + (String)attrIter.next();
					key = CollapseUtils.pathToId(childPath);
					prtln ("- path: " + childPath + ", key: " + key);
					cb.openElement(key);
				}
			}
	
			// set hash to first of new nodes (so it will be focused upon)
			if (firstNewStdPath != null) {
				String firstNewNodeKey = CollapseUtils.pathToId(firstNewStdPath);
				this.getActionForm().setHash(firstNewNodeKey);
				prtln ("set hash to " + firstNewNodeKey);
			}
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
			SchemEditUtils.prtln(s, "AsnAttrHelper");
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

