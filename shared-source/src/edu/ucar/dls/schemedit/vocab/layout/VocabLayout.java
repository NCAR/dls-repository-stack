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
package edu.ucar.dls.schemedit.vocab.layout;

import java.io.*;
import java.util.*;
import java.text.*;
import java.net.*;

import org.dom4j.*;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;

/**
 *  Class to read a "groups file" for a single schemaNode and create the
 *  LayoutNodes representing it's controlled vocab items.
 *
 * @author    Jonathan Ostwald
 */

public class VocabLayout {
	private static boolean debug = false;

	private URI uri = null;
	private Document doc = null;
	private Element body = null;

	private String title = null;
	private String xmlFormat = null;
	private String version = null;
	private String text = null;
	private String audience = null;
	private String path = null;
	private String deftn = null;
	private String vocab = null;
	private List layoutNodes = null;
	private String collapseExpand = null;
	private String smartCheckBox = null;


	/**
	 *  Constructor for the VocabLayout object
	 *
	 * @param  path           schemaPath for the element rendered using vocabLayout
	 * @exception  Exception  if vocabLayout file cannot be processed
	 */
	public VocabLayout(String path) throws Exception {
		// prtln ("\npath: " + path);
		Object ref = SchemEditUtils.getUriRef(path);
		if (ref instanceof File) {
			doc = Dom4jUtils.getXmlDocument((File) ref);
			uri = ((File) ref).toURI();
		}
		else if (ref instanceof URL) {
			doc = SchemEditUtils.getRemoteDoc(ref.toString(), false);
			uri = ((URL) ref).toURI();
		}
		else
			throw new Exception("Could not resolve path (" + path + ")");
		init();
	}


	/**
	 *  Constructor for the VocabLayout object using specified file.
	 *
	 * @param  file           vocabLayout file
	 * @exception  Exception  if vocabLayout file cannot be processed
	 */
	public VocabLayout(File file) throws Exception {
		this(file.toURI().toString());
	}


	/**
	 *  Parse the Document obtained from the vocabLayout source.
	 *
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	protected void init() throws Exception {

		Element rootElement = doc.getRootElement();

		// process HEAD element
		Element head = rootElement.element("head");
		if (head == null)
			throw new Exception("head not found");
		Element titleElement = head.element("title");
		title = titleElement.getText();

		// grab attributes from concept element
		Element conceptElement = head.element("concept");
		text = conceptElement.attributeValue("text");
		xmlFormat = conceptElement.attributeValue("metaFormat");
		version = conceptElement.attributeValue("metaVersion");
		audience = conceptElement.attributeValue("audience");
		path = conceptElement.attributeValue("path");
		collapseExpand = conceptElement.attributeValue("collapseExpand");
		smartCheckBox = conceptElement.attributeValue("smartCheckBox");
		body = rootElement.element("body");
		if (body == null)
			throw new Exception("body not found");
	}

	public String getJson () {
		String jsonStr = null;
		try {
			org.json.JSONObject json = org.json.XML.toJSONObject(this.doc.asXML());
			// prtln(json.toString(2));
			// prtln ("getJason for " + this.getPath());
			jsonStr = json.toString();
		} catch (Throwable t) {
			jsonStr = "error: " + t.getMessage();
		}
		return jsonStr;
	}

	/**
	 *  Gets the path of the element to be rendered using VocabLayout
	 *
	 * @return    The path value
	 */
	public String getPath() {
		return this.path;
	}


	/**
	 *  Gets the source attribute of the VocabLayout object
	 *
	 * @return    The source value
	 */
	public String getSource() {
		return this.uri.toString();
	}

	public String getCollapseExpand () {
		return this.collapseExpand;
	}

	public String getSmartCheckBox () {
		return this.smartCheckBox;
	}
	
	/**
	 *  Gets the top-level layoutNodes  VocabLayout tree
	 *
	 * @return    The layoutNodes value
	 */
	public List getLayoutNodes() {
		if (layoutNodes == null) {
			layoutNodes = new ArrayList();
			for (Iterator i = this.body.elementIterator(); i.hasNext(); ) {
				layoutNodes.add(new LayoutNode((Element) i.next(), this));
			}
		}
		return layoutNodes;
	}


	/**
	 *  Returns all layout nodes calculated by flattening the hierarchy of layoutNodes into a list.
	 *
	 * @return    The nodeList value
	 */
	public List getNodeList() {
		List ret = new ArrayList();
		return getNodeList(this.getLayoutNodes());
	}


	/**
	 *  Recursively expands the provided list of Nodes into a flat list.
	 *
	 * @param  nodeList  list of LayoutNodes to be processed
	 * @return           The nodeList value
	 */
	private List getNodeList(List nodeList) {
		List ret = new ArrayList();
		for (int i = 0; i < nodeList.size(); i++) {
			LayoutNode addNode = (LayoutNode) nodeList.get(i);
			List sublist = addNode.getSubList();
			ret.add(addNode);
			if (sublist.size() > 0) {
				ret.addAll(getNodeList(sublist));
			}
		}
		return ret;
	}


	/**  NOT YET DOCUMENTED */
	public void report() {
		prtln("title: " + this.title);
		prtln("xmlFormat: " + this.xmlFormat);
		prtln(getLayoutNodes().size() + " vocab nodes");
		for (Iterator i = layoutNodes.iterator(); i.hasNext(); ) {
			LayoutNode node = (LayoutNode) i.next();
			node.report();
		}
	}


	/**
	 *  The main program for the VocabLayout class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		prtln("\n ----------------");
		prtln("VocabLayout");
		File taosMuiDir = new File("/Users/ostwald/devel/projects/metadata-ui-project");
		File muiDir = new File("D:/Documents and Settings/ostwald/devel/projects/metadata-ui-project");
		File adnGroups = new File(muiDir, "frameworks/adn-item/0.6.50/groups");
		String subject = "subject-adn-groups-cataloger-en-us.xml";
		String resourceType = "resourceType-adn-groups-cataloger-en-us.xml";
		File file = new File(adnGroups, resourceType);
		VocabLayout docReader = new VocabLayout(file);

		// VocabLayout docReader = new VocabLayout ("http://localhost/vocabLayoutTest/groups/resourceType-adn-groups-cataloger-en-us.xml");

		// pp (docReader.getDocument());
		docReader.report();
	}

	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  node  NOT YET DOCUMENTED
	 */
	private static void pp(Node node) {
		prtln(Dom4jUtils.prettyPrint(node));
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "VocabLayout");
		}
	}

}

