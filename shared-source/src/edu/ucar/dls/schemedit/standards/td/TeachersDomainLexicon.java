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
package edu.ucar.dls.schemedit.standards.td;

import edu.ucar.dls.schemedit.standards.StandardsDocument;
import edu.ucar.dls.schemedit.standards.StandardsNode;
import edu.ucar.dls.schemedit.standards.asn.AsnDocKey;

import edu.ucar.dls.standards.commcore.StdDocument;
import edu.ucar.dls.standards.commcore.Standard;

import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.schemedit.*;

import java.io.*;
import java.util.*;

import java.net.*;

/**
 *  Reads a file representing the Teacher's Domain Lexicon XML as a Standards
 *  Document (and individual lexicon terms contained within) via the {@link
 *  StdDocument} and {@link Standard} classes. Provides lists of StandardsNodes
 *  for use in UI JSP.
 *
 * @author    ostwald
 */

public class TeachersDomainLexicon implements StandardsDocument {
	private static boolean debug = true;
	File source;

	Map standardsMap = new HashMap();
	StandardsNode rootNode = null;
	List nodeList = new ArrayList();
	int maxNodes = Integer.MAX_VALUE;

	String author = "WBGH-Teachers Domain";
	String topic = null;
	String created = "2010";
	String title = null;
	String id = null;
	String uid = null;


	/**
	 *  Constructor for the TeachersDomainLexicon object
	 *
	 * @param  source         StdDocument file
	 * @exception  Exception  if StdDocument file cannot be processed
	 */
	public TeachersDomainLexicon(File source)
		 throws Exception {
		this.source = source;
		StdDocument stdDoc = null;
		try {
			prtln("\nabout to read from " + this.source.getCanonicalPath());
			stdDoc = new StdDocument(this.source.getCanonicalPath());
		} catch (Exception e) {
			throw new Exception("StdDocument could not be initialized: " + e.getMessage());
		}
		init(stdDoc);
	}


	/**
	 *  Constructor for the TeachersDomainLexicon object
	 *
	 * @param  stdDoc         NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public TeachersDomainLexicon(StdDocument stdDoc)
		 throws Exception {
		this.source = new File(stdDoc.getPath());
		init(stdDoc);
	}


	/**
	 *  Initialize the TeachersDomainLexicon by populating the standardsMap and
	 *  tree
	 *
	 * @param  stdDoc         NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public void init(StdDocument stdDoc) throws Exception {

		rootNode = makeTeachersDomainLexicon(stdDoc);
		if (rootNode != null)
			nodeList = getNodeList(rootNode.getSubList());
		this.author = stdDoc.getAuthor();
		this.topic = stdDoc.getTopic();
		// this.created = stdDoc.getCreated();
		this.title = stdDoc.getTitle();
		this.id = stdDoc.getIdentifier();
		this.uid = this.id;
	}


	/**
	 *  Gets the id attribute of the TeachersDomainLexicon object
	 *
	 * @return    The id value
	 */
	public String getId() {
		return this.id;
	}


	/**
	 *  Gets the author attribute of the TeachersDomainLexicon object
	 *
	 * @return    The author value
	 */
	public String getAuthor() {
		return this.author;
	}
	
	public String getAuthorName() {
		return this.getAuthor();
	}


	/**
	 *  Gets the topic attribute of the TeachersDomainLexicon object
	 *
	 * @return    The topic value
	 */
	public String getTopic() {
		return this.topic;
	}


	/**
	 *  Gets the created attribute of the TeachersDomainLexicon object
	 *
	 * @return    The created value
	 */
	public String getCreated() {
		return this.created;
	}


	/**
	 *  Gets the title attribute of the TeachersDomainLexicon object
	 *
	 * @return    The title value
	 */
	public String getTitle() {
		return this.title;
	}


	/**
	 *  Gets the docKey attribute of the TeachersDomainLexicon object
	 *
	 * @return    The docKey value
	 */
	public String getDocKey() {
		// return StandardsRegistry.makeDocKey(this.author, this.topic, this.created);
		return new AsnDocKey(this.author, this.topic, this.created, this.uid).toString();
	}


	/**
	 *  Gets the rendererTag attribute of the TeachersDomainLexicon object
	 *
	 * @return    The rendererTag value
	 */
	public String getRendererTag() {
		return "standards_MultiBox";
	}


	/*
	* Limits the size of the standards to be processed, mostly useful for debugging
	*/
	/**
	 *  Sets the maxNodes attribute of the TeachersDomainLexicon object
	 *
	 * @param  max  The new maxNodes value
	 */
	public void setMaxNodes(int max) {
		maxNodes = max;
	}


	/**
	 *  Gets the maxNodes attribute of the TeachersDomainLexicon object
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
	public StandardsNode getStandard(String id) {
		if (standardsMap == null) {
			prtln("getStandard() for " + id + " standardsMap is not defined");
			return null;
		}

		return (StandardsNode) standardsMap.get(id);
	}


	/**
	 *  Gets the rootNode attribute of the TeachersDomainLexicon object
	 *
	 * @return    The rootNode value
	 */
	public StandardsNode getRootNode() {
		return rootNode;
	}


	/**
	 *  Create a hierarchical tree of StandardsNodes by splitting up the Standards
	 *  and populating the tree one standard at a time.
	 *
	 * @param  stdDoc  NOT YET DOCUMENTED
	 * @return         Description of the Return Value
	 */
	private StandardsNode makeTeachersDomainLexicon(StdDocument stdDoc) {
		Standard rootStd = stdDoc.getRootStandard();
		TeachersDomainStandardsNode rootNode = new TeachersDomainStandardsNode(rootStd, null);
		attachStandardsBranch(rootNode, stdDoc);
		return rootNode;
	}


	/**
	 * @param  parentNode  NOT YET DOCUMENTED
	 * @param  stdDoc      NOT YET DOCUMENTED
	 */
	private void attachStandardsBranch(TeachersDomainStandardsNode parentNode, StdDocument stdDoc) {

		// prtln ("attachStandardsBranch(): parentId: " + parentNode.getId());
		
		// start with the Standard instance corresponding to the parent node
		Standard parentStd = stdDoc.getStandard(parentNode.getId());

		// walk the children,
		for (Iterator i = parentStd.getChildren().iterator(); i.hasNext(); ) {
			Standard childStd = (Standard) i.next();
			TeachersDomainStandardsNode childNode = new TeachersDomainStandardsNode(childStd, parentNode);
			try {
				standardsMap.put(childNode.getId(), childNode);
			} catch (Throwable t) {
				// prtln ("childNode.getId() failed: " + t.getMessage());
				prtln("parentStd: " + parentStd.getId());
				if (childStd == null) {
					prtln("\t childStd is NULL");
				}
				diagnose(parentStd);
				System.exit(1);
			}
			parentNode.addSubNode(childNode);
			attachStandardsBranch(childNode, stdDoc);
		}
	}


	/**
	 *  Debugging - called to diagnose errors thrown during attachStandardsBranch
	 *
	 * @param  parentStd  NOT YET DOCUMENTED
	 */
	private void diagnose(Standard parentStd) {
		prtln("\nDIAGNOSE: " + parentStd.getId());

		List children = parentStd.getChildren();
		prtln("children (" + children.size() + ")");
		for (Iterator i = children.iterator(); i.hasNext(); ) {
			Standard childStd = (Standard) i.next();
			if (childStd == null)
				prtln("NULL");
			else
				prtln(childStd.getId());
		}
	}


	/**
	 *  Walk down a hierarchical tree of StandardsNodes and print an indented
	 *  display
	 */
	public void printStandardsDocument() {
		debug = true;
		prtln("STANDARD STREE ---------------------");
		printSubList(rootNode, "");
	}


	/**
	 *  Utility method to display the items of a subList.
	 *
	 * @param  node    Description of the Parameter
	 * @param  indent  Description of the Parameter
	 */
	void printSubList(StandardsNode node, String indent) {
		// prtln ("printSubList with " + node.getName());

		int truncateLen = 100; // the maximum length of text to show for this node
		String nodeText = node.getLabel().substring(0, java.lang.Math.min(truncateLen, node.getLabel().length()));

		prtln(indent + nodeText.trim());
		// prtln ("node has " + node.getSubList().size() + " children");
		if (node.getHasSubList()) {
			for (Iterator i = node.getSubList().iterator(); i.hasNext(); ) {
				printSubList((StandardsNode) i.next(), indent + "   ");
			}
		}
	}


	/**
	 *  Returns a flat list containing all StandardsNodes in the standardsTree.
	 *
	 * @return    The nodeList value
	 */
	public List getNodeList() {
		return nodeList;
	}


	/**
	 *  Flattens the hierarchy under the given TeachersDomainLexicon list, and
	 *  returns it as a flat arrayList.
	 *
	 * @param  nodes  NOT YET DOCUMENTED
	 * @return        The vocabList value
	 */
	private ArrayList getNodeList(List nodes) {
		ArrayList ret = new ArrayList();
		for (int i = 0; i < nodes.size(); i++) {
			StandardsNode addNode = (StandardsNode) nodes.get(i);
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
			TeachersDomainStandardsNode node = (TeachersDomainStandardsNode) nodeList.get(i);
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
			StandardsNode node = (StandardsNode) i.next();
			prtln("(" + node.getId() + ")  " + node.getFullText());
			if (count++ > max)
				break;
		}
	}


	/**
	 *  The main program for the TeachersDomainLexicon class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {

		String filename = "LEXICON_STANDARDS.xml";
		String dir = "H:/python-lib/wgbh/";
		File source = new File(dir, filename);
		TeachersDomainLexicon t = new TeachersDomainLexicon(source);

		prtln ("standardMap has " + t.standardsMap.size() + " entries");
		
		// find a specific node
		String id = "888";
		StandardsNode stdNode = t.getStandard(id);
		if (stdNode == null)
			prtln("StdNode not found for " + id);
		else {
			prtln("StandardsNode\n" + stdNode.toString());
			// prtln("label: " + stdNode.getLabel());
			// prtln("fullText: " + stdNode.getFullText());
		}

		// show anscestors of specific node
		List ancestors = stdNode.getAncestors();
		prtln(ancestors.size() + " ancestors found for \t" + id);
		for (Iterator i = ancestors.iterator(); i.hasNext(); ) {
			StandardsNode a = (StandardsNode) i.next();
			prtln("\n" + a.getClass().getName());
			prtln("\t" + a.getId() + " (" + a.getLevel() + ")");
			prtln(a.getFullText());
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
			SchemEditUtils.prtln(s, "tdLexicon");
			// SchemEditUtils.prtln(s, "");
			// System.out.println(s);
		}
	}
}

