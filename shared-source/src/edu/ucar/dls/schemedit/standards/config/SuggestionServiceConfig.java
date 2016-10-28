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
package edu.ucar.dls.schemedit.standards.config;

import edu.ucar.dls.schemedit.standards.StandardsRegistry;
import edu.ucar.dls.schemedit.standards.asn.AsnDocKey;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.schemedit.*;

import java.io.*;
import java.util.*;

import java.net.*;

import org.dom4j.*;

/**
 *  Reads am XML configuration file for a ASN vocab display and suggestion
 *  service. All frameworks are configured in the same file, and include
 *  per-framework properties such as the framework, the ASN docs configured for
 *  that framework, the xpath of the vocab field, the plugin for each framework,
 *  etc.
 *
 * @author    ostwald
 */
public class SuggestionServiceConfig {

	private static boolean debug = false;
	private Element element = null;
	/**  NOT YET DOCUMENTED */
	public static String DATA_TYPES = "dataTypes";
	/**  NOT YET DOCUMENTED */
	public static String STANDARDS_FILE = "standardsFile";
	/**  NOT YET DOCUMENTED */
	public static String STANDARDS_DIRECTORY = "standardsDirectory";
	/**  NOT YET DOCUMENTED */
	public static String ASN = "asn";
	private List asnDocKeys = null;
	private String defaultDocKey = null;


	/**
	 *  Constructor for the SuggestionServiceConfig object with provided
	 *  configuration Element.
	 *
	 * @param  config  A element form a standards config file
	 */
	public SuggestionServiceConfig(Element config) {
		this.element = config;

		if (this.getStandardSourceType().equals(ASN)) {
			this.asnDocKeys = new ArrayList();
			List nodes = getNodes("stdSource/asn/asnDocKey");
			// prtln (nodes.size() + " asnDocKey nodes found");
			for (Iterator i = nodes.iterator(); i.hasNext(); ) {
				Element el = (Element) i.next();
				this.asnDocKeys.add(el.getTextTrim());
			}
		}
	}


	/**
	 *  Gets the element attribute of the SuggestionServiceConfig object
	 *
	 * @return    The element value
	 */
	public Element getElement() {
		return this.element;
	}

	public String getDefaultDocKey () {
		return this.getAsnNode().attributeValue("defaultDocKey", "");
	}
	
	public void setDefaultDocKey(String key) {
		Element asnNode = this.getAsnNode();
		asnNode.setAttributeValue("defaultDocKey", key);
	}
	
	private Element getAsnNode() {
		Element asnNode = (Element) getNode("stdSource/asn");
		if (asnNode == null) {
			prtln("  asnNode is null");
			Element stdSource = (Element) getNode("stdSource");
			if (stdSource == null) {
				stdSource = this.element.addElement("stdSource");
				prtln("added stdSource");
			}
			asnNode = stdSource.addElement("asn");
			prtln("added asnNode");
		}
		return asnNode;
	}

	/**
	 *  Sets the asnDockeys attribute of the SuggestionServiceConfig object
	 *
	 * @param  keys  The new asnDockeys value
	 */
	public void setAsnDockeys(List keys) {
		// replace the children of stdSource/asn with elements constructed from the provided values
		/*
			<config>
			  <stdSource>
				<asn default.key>
				  <asnDocKey>Colorado.1992.Science.DHUEJ02</asnDocKey>
		*/
		prtln("setAsnDockeys: " + keys.size() + " keys provided");
		for (Iterator i = keys.iterator(); i.hasNext(); ) {
			prtln(" - " + (String) i.next());
		}

		Element asnNode = this.getAsnNode();

		if (asnNode.attribute("defaultAuthor") == null)
			asnNode.addAttribute("defaultAuthor", "");
		if (asnNode.attribute("defaultTopic") == null)
			asnNode.addAttribute("defaultTopic", "");
		if (asnNode.attribute("defaultYear") == null)
			asnNode.addAttribute("defaultYear", "");

		prtln("asnNode had " + asnNode.elements().size());
		/* 		for (Iterator i=asnNode.elementIterator();i.hasNext();) {
			Element asnDocKeyEl = (Element)i.next();
			if (!asnNode.remove(asnDocKeyEl))
				prtln ("WARNING: failed to remove asnDocKey");
		} */
		asnNode.clearContent(); // doesn't touch attributes
		prtln("asnNode has " + asnNode.elements().size() + " after clearing content");
		;

		if (keys == null) {
			prtln("setAsnDocs received a null");
			keys = new ArrayList();
		}

		prtln("about to add from keys to config");
		for (Iterator i = keys.iterator(); i.hasNext(); ) {
			String key = (String) i.next();
			asnNode.addElement("asnDocKey").setText(key);
			prtln("   - added node for " + key);
		}

		this.asnDocKeys = keys;
		prtln("my element after update: " + Dom4jUtils.prettyPrint(this.element));
	}


	/**
	 *  Gets the asnDocKeys attribute of the SuggestionServiceConfig object
	 *
	 * @return    The asnDocKeys value
	 */
	public List getAsnDocKeys() {
		return this.asnDocKeys;
	}


	/**
	 *  Gets the version attribute of the SuggestionServiceConfig object
	 *
	 * @return    The version value
	 */
	public String getVersion() {
		return getNodeText("xmlFormat/@version");
	}


	/**
	 *  Gets the xpath attribute of the SuggestionServiceConfig object
	 *
	 * @return    The xpath value
	 */
	public String getXpath() {
		return getNodeText("xpath");
	}


	/* Issue: whether tp throw an exception */
	/**
	 *  Sets the xpath attribute of the SuggestionServiceConfig object
	 *
	 * @param  value  The new xpath value
	 */
	public void setXpath(String value) {
		setTextAtPath(value, "xpath");
	}


	/**
	 *  Gets the xmlFormat attribute of the SuggestionServiceConfig object
	 *
	 * @return    The xmlFormat value
	 */
	public String getXmlFormat() {
		return getNodeText("xmlFormat");
	}


	/**
	 *  Sets the xmlFormat attribute of the SuggestionServiceConfig object
	 *
	 * @param  value  The new xmlFormat value
	 */
	public void setXmlFormat(String value) {
		setTextAtPath(value, "xmlFormat");
	}


	/**
	 *  Gets the standardSourceType attribute of the SuggestionServiceConfig object
	 *
	 * @return    The standardSourceType value
	 */
	public String getStandardSourceType() {
		Element stdSource = (Element) getNode("stdSource");
		if (stdSource == null)
			return ASN;
		return ((Element) stdSource.elements().get(0)).getName();
	}


	/**
	 *  Gets the helperClass attribute of the SuggestionServiceConfig object
	 *
	 * @return    The helperClass value
	 */
	public String getHelperClass() {
		return getNodeText("helperClass");
	}


	/**
	 *  Gets the pluginClass that supplies framework-specific informations, like
	 *  xpaths, to the suggestionService.
	 *
	 * @return    The pluginClass value
	 */
	public String getPluginClassOff() {
		return getNodeText("plugin");
	}


	/**
	 *  Gets the dataTypes attribute of the SuggestionServiceConfig object
	 *
	 * @return                The dataTypes value
	 * @exception  Exception  Description of the Exception
	 */
	public List getDataTypes() throws Exception {
		String sourceType = getStandardSourceType();
		if (!sourceType.equals(DATA_TYPES)) {
			throw new Exception("cannot get dataTypes for " + sourceType);
		}
		List dataTypes = new ArrayList();
		List nodes = getNodes("stdSource/dataTypes/dataType");
		for (Iterator i = nodes.iterator(); i.hasNext(); ) {
			dataTypes.add(((Node) i.next()).getText());
		}
		return dataTypes;
	}


	/**
	 *  Gets the standardsDirectory attribute of the SuggestionServiceConfig
	 *  object, which designates a directory containing standards files.
	 *
	 * @return                The standardsDirectory value
	 * @exception  Exception  Description of the Exception
	 */
	public String getStandardsDirectory() throws Exception {
		String sourceType = getStandardSourceType();
		if (!sourceType.equals(STANDARDS_DIRECTORY)) {
			throw new Exception("cannot get standardsDirectory for " + sourceType);
		}
		return getNodeText("stdSource/standardsDirectory");
	}


	/**
	 *  Gets the defaultDocKey attribute of the SuggestionServiceConfig object
	 *
	 * @return                The defaultDocKey value
	 * @exception  Exception  Description of the Exception
	 */
	public String getDefaultDocKeyOld() throws Exception {
		if (this.defaultDocKey == null) {

			String sourceType = getStandardSourceType();
			if (!(sourceType.equals(STANDARDS_DIRECTORY) || sourceType.equals(ASN))) {
				throw new Exception("cannot get getDefaultDocKey for " + sourceType);
			}
			Element sdElement = null;
			if (sourceType.equals(ASN)) {
				sdElement = (Element) getNode("stdSource/asn");
			}
			else {
				prtln("OTHER");
				sdElement = (Element) getNode("stdSource/standardsDirectory");
			}

			if (sdElement == null) {
				// throw new Exception ("element holding default doc attributes not found");
				prtln("WARNING: element holding default doc attributes not found");
				return null;
			}

			String author = sdElement.attributeValue("defaultAuthor", "");
			String topic = sdElement.attributeValue("defaultTopic", "");
			String year = sdElement.attributeValue("defaultYear", "");
			// return StandardsRegistry.makeDocKey (author, topic, year);
			this.defaultDocKey = new AsnDocKey(author, topic, year).toString();
		}
		return this.defaultDocKey;
	}


	/**
	 *  Gets the defaultDoc attribute of the SuggestionServiceConfig object
	 *
	 * @return                The defaultDoc value
	 * @exception  Exception  Description of the Exception
	 */
	public String getDefaultDoc() throws Exception {
		String standardsDirectory = getStandardsDirectory();
		return getNodeText("stdSource/standardsDirectory/@defaultDoc");
	}


	/**
	 *  Gets the standardsFile attribute of the SuggestionServiceConfig object
	 *
	 * @return                The standardsFile value
	 * @exception  Exception  Description of the Exception
	 */
	public String getStandardsFile() throws Exception {
		String sourceType = getStandardSourceType();
		if (!sourceType.equals(STANDARDS_FILE)) {
			throw new Exception("cannot get dataTypes for " + sourceType);
		}
		return getNodeText("stdSource/standardsFile");
	}

	// ----------- DOM utilities -------------------
	/**
	 *  Get all Nodes satisfying the given xpath.
	 *
	 * @param  xpath  an XPath
	 * @return        a List of all modes satisfying given XPath, or null
	 */
	private List getNodes(String xpath) {
		try {
			return element.selectNodes(xpath);
		} catch (Throwable e) {
			prtln("getNodes() failed with " + xpath + ": " + e);
		}
		return null;
	}


	/**
	 *  Gets a single Node satisfying give XPath. If more than one Node is found,
	 *  the first is returned (and a msg is printed).
	 *
	 * @param  xpath  an XPath
	 * @return        a dom4j Node
	 */

	private Node getNode(String xpath) {
		List list = getNodes(xpath);
		if ((list == null) || (list.size() == 0)) {
			// prtln ("getNode() did not find node for " + xpath);
			return null;
		}
		if (list.size() > 1) {
			prtln("getNode() found multiple modes for " + xpath + " (returning first)");
		}
		return (Node) list.get(0);
	}


	/**
	 *  return the Text of a Node satisfying the given XPath.
	 *
	 * @param  xpath  an XPath\
	 * @return        Text of Node or empty String if no Node is found
	 */
	private String getNodeText(String xpath) {
		Node node = getNode(xpath);
		try {
			return node.getText().trim();
		} catch (Throwable t) {

			// prtln ("getNodeText() failed with " + xpath + "\n" + t.getMessage());
			// Dom4jUtils.prettyPrint (docMap.getDocument());
		}
		return "";
	}


	/**
	 *  Sets the textAtPath attribute of the SuggestionServiceConfig object
	 *
	 * @param  text   The new textAtPath value
	 * @param  xpath  The new textAtPath value
	 */
	void setTextAtPath(String text, String xpath) {
		Node node = getNode(xpath);
		if (node == null) {
			System.out.println("setTextAtPath did not find a node at \'" + xpath + "\'");
			return;
		}
		node.setText(text);
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	void report() throws Exception {
		prtln("\nxmlFormat: " + this.getXmlFormat());
		prtln("version: " + this.getVersion());
		prtln("xpath: " + this.getXpath());
		String sourceType = this.getStandardSourceType();
		prtln("sourceType: " + sourceType);
		if (sourceType.equals(DATA_TYPES)) {
			prtln("\tdataTypes:");
			for (Iterator dt = this.getDataTypes().iterator(); dt.hasNext(); ) {
				prtln("\t\t" + (String) dt.next());
			}
		}
		if (sourceType.equals(STANDARDS_FILE)) {
			prtln("\tstandards file:");
			prtln("\t\t" + this.getStandardsFile());
		}
		if (sourceType.equals(STANDARDS_DIRECTORY)) {
			prtln("\tstandards directory: " + this.getStandardsDirectory());
			// prtln("\tdefault: " + this.getDefaultDoc());
			prtln("\tdefaultKEY: " + this.getDefaultDocKey());
		}
		if (sourceType.equals(ASN)) {
			prtln("\tdefaultKEY: " + this.getDefaultDocKey());
			prtln("asnDocs:");
			for (Iterator dt = this.getAsnDocKeys().iterator(); dt.hasNext(); ) {
				prtln("\t\t" + (String) dt.next());
			}
		}
	}


	/**
	 *  The main program for the SuggestionServiceConfig class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		String path = "C:/Program Files/Apache Software Foundation/Tomcat 5.5/var/dcs_conf/suggestionServiceConfig_ASN_3.xml";
		//String path = "/Users/ostwald/devel/dcs-repos/tiny/dcs_conf/suggestionServiceConfig.xml";
		SuggestionServiceManager manager = null;

		try {
			manager = new SuggestionServiceManager(new File(path));
		} catch (Throwable t) {
			t.printStackTrace();
			throw new Exception("couldn't instantiate SuggestionServiceManager");
		}

		String xmlFormat = "ncs_item";
		SuggestionServiceConfig config = manager.getConfig(xmlFormat);
		if (config == null) {
			throw new Exception("config not found for: " + xmlFormat);
		}

		String[] docKeys = new String[]{
			"AAAS.Science.1993.D1000152",
			"Colorado.Science.1995.D100027B",
			"Rhode Island.Science.1995.D10001B9"
			};

		config.setAsnDockeys(Arrays.asList(docKeys));
		manager.flush();
		config.report();
	}


	/**
	 *  Description of the Method
	 *
	 * @param  node  Description of the Parameter
	 */
	private static void pp(Node node) {
		prtln(Dom4jUtils.prettyPrint(node));
	}


	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "");
		}
	}

}

