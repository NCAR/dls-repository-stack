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

package edu.ucar.dls.standards.asn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.schema.NamespaceRegistry;
import edu.ucar.dls.util.TimedURLConnection;
import edu.ucar.dls.util.URLConnectionTimedOutException;

import org.dom4j.*;
import org.jaxen.SimpleNamespaceContext;

import java.util.*;
import java.net.URL;
import java.io.File;

/**
 *  Class to read and write XML documents having namespaces
 *
 * @author    Jonathan Ostwald
 */
public class NameSpaceXMLDocReader {
	private static Log log = LogFactory.getLog(NameSpaceXMLDocReader.class);
	private static boolean debug = true;
	private static boolean verbose = true;

	private Document doc = null;
	private SimpleNamespaceContext nsContext = null;
	private NamespaceRegistry namespaces = null;

    /**
     * NameSpaceXMLDocReader Constructor for xml string
     * @param an xml document as string
     * @throws Exception
     */
	public NameSpaceXMLDocReader (String xml) throws Exception {
		xml = this.preprocessXml (xml);
		this.doc = DocumentHelper.parseText(xml);
		init();
	}

    /**
     * NameSpaceXMLDocReader Constructor for xml Document
     * @param doc
     * @throws Exception
     */
	public NameSpaceXMLDocReader (Document doc) throws Exception {
		this.doc = doc;
		init();
	}

	/**
	 *  Constructor for the NameSpaceXMLDocReader from a URL
	 *
	 * @param  url            NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public NameSpaceXMLDocReader(URL url) throws Exception {
		this.doc = readDocument(url);
		init();
	}

	/**
	 *  Constructor for the NameSpaceXMLDocReader object from a file
	 *
	 * @param  file            file containing XML Doc
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public NameSpaceXMLDocReader(File file) throws Exception {
		this.doc = Dom4jUtils.getXmlDocument(file);
		init();
	}

    /**
     * Initialize namespace-aware components
     * @throws Exception
     */
	protected void init() throws Exception {
		this.namespaces = new NamespaceRegistry();
		this.namespaces.registerNamespaces(this.doc);		
		this.nsContext = namespaces.getNamespaceContext();
	}

    /**
     * Returns this object as a dom4j Document
     * @return
     */
	public Document getDocument () {
		return this.doc;
	}

    /**
     * Returns the root element as a dom4j Element
     * @return
     */
	public Element getRootElement () {
		return this.doc.getRootElement();
	}

    /**
     * Returns the text for the first node found at the provided xpath
     */
	public String getNodeText (String xpath) {
		Node node = getNode (xpath);
		if (node == null)
			return null;
		else
			return node.getText();
	}

    /**
     * Returns the text for the first node found at the provided baseElement and path
     * @param baseElement - if null, the document is used
     * @param relativePath - xpath relative to baseElement

     * @throws Exception
     */
	public String getValueAtPath(Element baseElement, String relativePath) throws Exception {
		Node node = getNode (baseElement, relativePath);
		if (node == null) {
			throw new Exception("node not found at relative path: " + relativePath);
		}
		String ret = node.getText();
		if (ret == null) {
			prtln("WARNING: text not found for node at relative path: " + relativePath);
			return ret;
		}
		return ret.trim();
	}
	
	/**
	 *  Gets the node at the specified path relative to the document
	 *
	 * @param  xpath  NOT YET DOCUMENTED
	 * @return        The node value
	 */
	public Node getNode(String xpath) {
		return getNode (null, xpath);
	}

    /**
     * Gets the node at the specified path relative to the baseNode (or Document if null)
     * @param baseNode
     * @param xpath
     * @return
     */
	public Node getNode (Node baseNode, String xpath) {
		List list = getNodes(baseNode, xpath);
		if ((list == null) || (list.size() == 0)) {
			// prtln ("getNode() did not find node for " + xpath);
			return null;
		}
		if (list.size() > 1) {
			log.warn ("getNode() found mulitple modes for " + xpath + " (returning first)");
		}
		return (Node) list.get(0);
	}


	/**
	 *  Gets the nodes found at provided xpath
	 *
	 * @param  path  NOT YET DOCUMENTED
	 * @return       The nodes value
	 */
	public List getNodes(String path) {
		return getNodes (null, path);
	}

	public List getNodes(Node baseNode, String path) {
		if (baseNode == null)
			baseNode = this.doc;
		try {
			XPath xpath = getXPath(path);
			return xpath.selectNodes(baseNode);
		} catch (Throwable e) {
			// prtln("getNodes() failed with " + path + ": " + e);
		}
		return null;
	}


	
	public String getChildElementText(Element e, String childElementName) {
		Element child = e.element(getQName (childElementName));
		if (child == null) {
			log.warn("getChildElementText could not find subElement for " + childElementName);
			// prtln(Dom4jUtils.prettyPrint(e));
			return "";
		}
		return child.getTextTrim();
	}
	
	public String getChildElementAttribute(Element e, String childElementName, String attributeName) {
		Element child = e.element(getQName (childElementName));
		if (child == null) {
			log.warn("getChildElementText could not find childElement for " + childElementName);
			return "";
		}
		return child.attributeValue(getQName (attributeName), null);
	}
	

	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  url            NOT YET DOCUMENTED
	 * @return                NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	private Document readDocument(URL url) throws Exception {

		// timed connection
		int millis = 10000;
		String content = null;
		Document doc = null;
		try {
			content = TimedURLConnection.importURL(url.toString(), "utf-8", millis);
			content = this.preprocessXml(content);
			doc = Dom4jUtils.getXmlDocument(content);
		} catch (URLConnectionTimedOutException e) {
			prtln("connection timed out: " + e);
			throw new Exception("Connection to " + url + " timed out after " + (millis / 1000) + " seconds");
		} catch (DocumentException e) {
			throw new Exception("Could not process Document: " + e.getMessage());
		}
		if (doc == null)
			throw new Exception("Document was not processed");
		return doc;
	}
	
	
    /**
     * Perform some operation on the xml string representation before parsing.
     * To be implmented by subclasses
     * @param xml
     * @return
     */
	public String preprocessXml (String xml) {
		return xml;
	}	
	
	
	// Namespace-aware stuff
	
	/**
	 *  Gets an namespace aware XPath object for the provided xpath string
	 *
	 * @param  path  NOT YET DOCUMENTED
	 * @return       The xPath value
	 */
	private XPath getXPath(String path) {
		XPath xpath = DocumentHelper.createXPath(path);
		xpath.setNamespaceContext(this.nsContext);
		// prtln ("xpath: " + xpath.toString());
		return xpath;
	}
	
    /**
     * Returns a QName object corresponding to provided qualified name
     * @param qualifiedName
     * @return
     */
	public QName getQName (String qualifiedName) {
		try {
			return namespaces.getQName(qualifiedName);
		} catch (Exception e) {
			log.warn ("could not obtain QName for \"" + qualifiedName + ": " + e.getMessage());
		}
		return null;
	}

    /**
     * Returns Namespace object from profided prefix string
     */
	private Namespace getNamespace (String prefix) {
		return this.namespaces.getNSforPrefix(prefix);
	}


	public SimpleNamespaceContext getNamespaceContext () {
		return this.nsContext;
	}

	// end namespace-aware stuff

	
	public void destroy () {
		this.doc = null;
		this.nsContext = null;
		this.namespaces = null;
	}

	private static void prtln(String s) {
		if (debug) {
			System.out.println("NameSpaceXMLDocReader: " + s);
		}
	}
	
	private static void pp(Node node) {
		prtln(Dom4jUtils.prettyPrint(node));
	}
	

}

