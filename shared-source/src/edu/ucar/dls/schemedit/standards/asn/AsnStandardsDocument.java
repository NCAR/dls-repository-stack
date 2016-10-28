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
package edu.ucar.dls.schemedit.standards.asn;

import edu.ucar.dls.standards.asn.AsnHelper;
import edu.ucar.dls.schemedit.standards.StandardsDocument;
import edu.ucar.dls.schemedit.standards.adn.util.MappingUtils;

import edu.ucar.dls.standards.asn.AsnDocument;
import edu.ucar.dls.standards.asn.AsnStandard;

import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.schemedit.*;

import java.io.*;
import java.util.*;

import java.net.*;

/**
 *  Provides acess to a single ASN Standards Document (and individual standards
 *  contained within) via the {@link AsnDocument} and {@link AsnStandard}
 *  classes. Provides lists of AsnStandardsNodes for use in UI JSP.
 *
 * @author    ostwald
 */

public class AsnStandardsDocument implements StandardsDocument {
	private static boolean debug = true;
	File source;

	Map standardsMap = new HashMap();
	List dataTypes = new ArrayList();
	AsnStandardsNode rootNode = null;
	List nodeList = new ArrayList();
	int maxNodes = Integer.MAX_VALUE;

	String author = null;
	String authorName = null;
	String topic = null;
	String created = null;
	String title = null;
	String id = null;
	String uid = null;


	/**
	 *  Constructor for the AsnStandardsDocument object
	 *
	 * @param  source         AsnDocument file
	 * @exception  Exception  if AsnDocument file cannot be processed
	 */
	public AsnStandardsDocument(File source)
		 throws Exception {
		this.source = source;
		AsnHelper asnHelper = AsnHelper.getInstance();
		AsnDocument asnDoc = null;
		try {
			prtln("\nabout to read from " + this.source.getCanonicalPath());
			asnDoc = new AsnDocument(this.source);
		} catch (Exception e) {
			throw new Exception("AsnDocument could not be initialized: " + e.getMessage());
		}
		init(asnDoc);
	}


	/**
	 *  Constructor for the AsnStandardsDocument object
	 *
	 * @param  asnDoc         NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public AsnStandardsDocument(AsnDocument asnDoc)
		 throws Exception {
		if (asnDoc.getPath() != null)
			this.source = new File(asnDoc.getPath());
		AsnHelper asnHelper = AsnHelper.getInstance();
		init(asnDoc);
	}


	/**
	 *  Initialize the AsnStandardsDocument by populating the standardsMap and tree
	 *
	 * @param  asnDoc         NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public void init(AsnDocument asnDoc) throws Exception {

		rootNode = makeStandardsDocument(asnDoc);
		if (rootNode != null) {
			nodeList = getNodeList(rootNode.getSubList());
			nodeList.add (rootNode); // getNodeList never adds rootNode
		}
		this.author = asnDoc.getAuthor();
		this.authorName = asnDoc.getAuthorName();
		this.topic = asnDoc.getTopic();
		this.created = asnDoc.getCreated();
		this.title = asnDoc.getTitle();
		this.id = asnDoc.getIdentifier();
		this.uid = asnDoc.getUid();
	}


	/**
	 *  Gets the id attribute of the AsnStandardsDocument object
	 *
	 * @return    The id value
	 */
	public String getId() {
		return this.id;
	}


	/**
	 *  Gets the author attribute of the AsnStandardsDocument object
	 *
	 * @return    The author value
	 */
	public String getAuthor() {
		return this.author;
	}
	
	public String getAuthorName() {
		return this.authorName;
	}


	/**
	 *  Gets the topic attribute of the AsnStandardsDocument object
	 *
	 * @return    The topic value
	 */
	public String getTopic() {
		return this.topic;
	}


	/**
	 *  Gets the created attribute of the AsnStandardsDocument object
	 *
	 * @return    The created value
	 */
	public String getCreated() {
		return this.created;
	}


	/**
	 *  Gets the title attribute of the AsnStandardsDocument object
	 *
	 * @return    The title value
	 */
	public String getTitle() {
		return this.title;
	}


	/**
	 *  Gets the docKey attribute of the AsnStandardsDocument object
	 *
	 * @return    The docKey value
	 */
	public String getDocKey() {
		return new AsnDocKey(this.author, this.topic, this.created, this.uid).toString();
	}


	/**
	 *  Gets the rendererTag attribute of the AsnStandardsDocument object
	 *
	 * @return    The rendererTag value
	 */
	public String getRendererTag() {
		return "asnStandards_MultiBox";
	}


	/*
	* Limits the size of the standards to be processed, mostly useful for debugging
	*/
	/**
	 *  Sets the maxNodes attribute of the AsnStandardsDocument object
	 *
	 * @param  max  The new maxNodes value
	 */
	public void setMaxNodes(int max) {
		maxNodes = max;
	}


	/**
	 *  Gets the maxNodes attribute of the AsnStandardsDocument object
	 *
	 * @return    The maxNodes value
	 */
	public int getMaxNodes() {
		return maxNodes;
	}


	/**
	 *  Get a StandardNode by id
	 *
	 * @param  id  NOT YET DOCUMENTED
	 * @return     The standard value
	 */
	public AsnStandardsNode getStandard(String id) {
		if (standardsMap == null) {
			prtln("getStandard() for " + id + " standardsMap is not defined");
			return null;
		}
		
		return (AsnStandardsNode) standardsMap.get(id);
	}


	/**
	 *  Gets the rootNode attribute of the AsnStandardsDocument object
	 *
	 * @return    The rootNode value
	 */
	public AsnStandardsNode getRootNode() {
		return rootNode;
	}


	/**
	 *  Create a hierarchical tree of AsnStandardsNodes by splitting up the
	 *  Standards and populating the tree one standard at a time.
	 *
	 * @param  asnDoc  NOT YET DOCUMENTED
	 * @return         Description of the Return Value
	 */
	private AsnStandardsNode makeStandardsDocument(AsnDocument asnDoc) {
		AsnStandard rootStd = asnDoc.getRootStandard();
		AsnStandardsNode rootNode = new AsnStandardsNode(rootStd, null);
		attachStandardsBranch(rootNode, asnDoc);
		standardsMap.put(rootNode.getId(), rootNode);
		return rootNode;
	}


	/**
	 *  
	 *
	 * @param  parentNode  NOT YET DOCUMENTED
	 * @param  asnDoc      NOT YET DOCUMENTED
	 */
	private void attachStandardsBranch(AsnStandardsNode parentNode, AsnDocument asnDoc) {
		// AsnStandard parentStd = parentNode.getAsnStandard();
		
		// start with the AsnStandard instance corresponding to the parent node
		String parentId = parentNode.getId();
		AsnStandard parentStd = asnDoc.getStandard(parentNode.getId());
		// walk the children, 
		for (Iterator i = parentStd.getChildren().iterator(); i.hasNext(); ) {
			AsnStandard childStd = (AsnStandard) i.next();
			AsnStandardsNode childNode = new AsnStandardsNode(childStd, parentNode);
			String childId = childNode.getId();
			try {
				standardsMap.put(childNode.getId(), childNode);
			} catch (Throwable t) {
				// prtln ("childNode.getId() failed: " + t.getMessage());
				if (childStd == null) {
					prtln("\t childStd is NULL");
				}
				diagnose(parentStd);
				System.exit(1);
			}
			parentNode.addSubNode(childNode);
			attachStandardsBranch(childNode, asnDoc);
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  parentStd  NOT YET DOCUMENTED
	 */
	private void diagnose(AsnStandard parentStd) {
		prtln("\nDIAGNOSE: " + parentStd.getId());

		List children = parentStd.getChildren();
		prtln("children (" + children.size() + ")");
		for (Iterator i = children.iterator(); i.hasNext(); ) {
			AsnStandard childStd = (AsnStandard) i.next();
			if (childStd == null)
				prtln("NULL");
			else
				prtln(childStd.getId());
		}
	}


	/**
	 *  Walk down a hierarchical tree of AsnStandardsNodes and print an indented
	 *  display
	 */
	public void printStandardsDocument() {
		debug=true;
		prtln("STANDARD STREE ---------------------");
		printSubList(rootNode, "");
	}


	/**
	 *  Utility method to display the items of a subList.
	 *
	 * @param  node    Description of the Parameter
	 * @param  indent  Description of the Parameter
	 */
	void printSubList(AsnStandardsNode node, String indent) {
		// prtln ("printSubList with " + node.getName());
		
		int truncateLen = 100;  // the maximum length of text to show for this node
		String nodeText = node.getLabel().substring(0, java.lang.Math.min (truncateLen, node.getLabel().length()));
		
		prtln(indent + nodeText.trim());
		// prtln ("node has " + node.getSubList().size() + " children");
		if (node.getHasSubList()) {
			for (Iterator i = node.getSubList().iterator(); i.hasNext(); ) {
				printSubList((AsnStandardsNode) i.next(), indent + "   ");
			}
		}
	}


	/**
	 *  Returns a flat list containing all AsnStandardsNodes in the standardsTree.
	 *
	 * @return    The nodeList value
	 */
	public List getNodeList() {
		return nodeList;
	}


	/**
	 *  Flattens the hierarchy under the given AsnStandardsDocument list, and returns
	 *  it as a flat arrayList.
	 *
	 * @param  nodes  NOT YET DOCUMENTED
	 * @return        The vocabList value
	 */
	private ArrayList getNodeList(List nodes) {
		ArrayList ret = new ArrayList();
		for (int i = 0; i < nodes.size(); i++) {
			AsnStandardsNode addNode = (AsnStandardsNode) nodes.get(i);
			List sublist = addNode.getSubList();
			ret.add(addNode);
			if (sublist.size() > 0) {
				ret.addAll(getNodeList(sublist));
			}
		}
		return ret;
	}


	/**  Description of the Method */
	public void printNodeList() {
		prtln("NODE LIST ---------------------");
		for (int i = 0; i < nodeList.size() && i < 1000; i++) {
			AsnStandardsNode node = (AsnStandardsNode) nodeList.get(i);
			String lastIndicator = "";
			if (node.getIsLastInSubList()) {
				lastIndicator = "   _|";
			}
			String leafIndicator = "";
			if (!node.getHasSubList()) {
				leafIndicator = " * ";
			}
			prtln("(" + node.getLevel() + ") " + leafIndicator + node.getLabel() + lastIndicator);

		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @return    NOT YET DOCUMENTED
	 */
	public int size() {
		return this.standardsMap.size();
	}


	/**  Description of the Method */
	void showStandards() {
		Collection standards = this.standardsMap.values();
		int max = 50;
		int count = 0;
		for (Iterator i = standards.iterator(); i.hasNext(); ) {
			AsnStandardsNode node = (AsnStandardsNode) i.next();
			prtln("(" + node.getId() + ")  " + node.getFullText());
			if (count++ > max)
				break;
		}
	}


	/**
	 *  The main program for the AsnStandardsDocument class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {

		String path = "/Users/ostwald/tmp/D100003B_full.xml";
		File source = new File(path);
		AsnStandardsDocument t = new AsnStandardsDocument(source);

 		// find a specific node
		String id = "http://purl.org/ASN/resources/S1009A52";
		AsnStandardsNode stdNode = t.getStandard(id);
		if (stdNode == null)
			prtln("StdNode not found for " + id);
		else {
			prtln("AsnStandardsNode\n" + stdNode.toString());
			// prtln("label: " + stdNode.getLabel());
			// prtln("fullText: " + stdNode.getFullText());
		}

		// show anscestors of specific node
		List ancestors = stdNode.getAncestors();
		prtln (ancestors.size() + " ancestors found for \t" + id);
		for (Iterator i=ancestors.iterator();i.hasNext();) {
			AsnStandardsNode a = (AsnStandardsNode)i.next();
			prtln ("\n" + a.getClass().getName());
			prtln ("\t" + a.getId() + " (" + a.getLevel() + ")");
			prtln (a.getFullText());
		}
		
		// t.printStandardsDocument(); 
	}


	/**  NOT YET DOCUMENTED */
	public void destroy() {
		// this.standards = null;
		this.standardsMap.clear();
		this.standardsMap = null;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "AsnStandardsDocument");
			// SchemEditUtils.prtln(s, "");
			// System.out.println(s);
		}
	}
}

