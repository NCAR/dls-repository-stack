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
package edu.ucar.dls.xml.nldr;

import edu.ucar.dls.schemedit.threadedservices.NLDRProperties;
import edu.ucar.dls.util.Utils;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XMLUtils;
import edu.ucar.dls.xml.*;

import org.dom4j.*;

import java.util.*;
import java.lang.*;
import java.io.*;
import java.text.*;
import java.net.*;
import java.util.Hashtable;
import java.util.regex.*;

/**
 *  Reads XML records and converts to an exported form (i.e., "containing
 *  citableUrls"). MetadataRecords may involve mulitple-namespaces.
 *
 *@author    Jonathan Ostwald
 */
public abstract class MetadataRecord {
	private static boolean debug = false;
	private Document docNode = null;
	private static String schemaNamespacePrefix = "xsi";

	/**
	 *  Constructor that loads the given record. No validation is performed.
	 *
	 *@param  xml                    The XML to start with
	 *@exception  DocumentException  If error parsing the XML
	 */
	public MetadataRecord(String xml) throws DocumentException, Exception {
		docNode = Dom4jUtils.getXmlDocument(xml);
		Element root = docNode.getRootElement();
		if (root.getNamespaceForURI("http://www.w3.org/2001/XMLSchema-instance") == null) {
			root.addNamespace(schemaNamespacePrefix, "http://www.w3.org/2001/XMLSchema-instance");
		}
		// prtln (Dom4jUtils.prettyPrint(docNode));
	}
	
	public void setNoNamespaceSchemaLocation (String uriStr) {
		String qualifiedAttr = schemaNamespacePrefix + ":noNamespaceSchemaLocation";
		docNode.getRootElement().addAttribute(qualifiedAttr, uriStr);
	}
	
	public void setSchemaNamespacePrefix (String prefix) {
		schemaNamespacePrefix = prefix;
	}
	
	/**
	 *  Gets the id attribute of the MetadataRecord object
	 *
	 *@return    The id value
	 */
	public abstract String getId();

    /**
     * Get the document node of this MetadataRecord
     * @return
     */
	public Document getDocument() {
		return this.docNode;
	}
	
	/**
	 *  Gets the textAtPath attribute of the MetadataRecord object
	 *
	 *@param  path  NOT YET DOCUMENTED
	 *@return       The textAtPath value
	 */
	public String getTextAtPath(String path) {
		return docNode.valueOf(makeXPath(path));
	}

	public String getTextAtPath(String path, Element context) {
		return context.valueOf(makeXPath(path));
	}
	
	public List getValuesAtPath (String path) {
		List values = new ArrayList();
		List nodes = selectNodes (path);
		for (Iterator i = nodes.iterator();i.hasNext();) {
			values.add (((Node)i.next()).getText());
		}
		return values;
	}

	/**
	 *  Sets the textAtPath attribute of the MetadataRecord object
	 *
	 *@param  xpath  xpath of element
	 *@param  value  text to set
	 */
	public void setTextAtPath(String xpath, String value) {
		docNode.selectSingleNode(makeXPath(xpath)).setText(value);
	}


	/**
	 *  Select Nodes for provided xpath
	 *
	 *@param  xpath  xpath
	 *@return        list of selected nodes
	 */
	public List selectNodes(String xpath) {
		return docNode.selectNodes(makeXPath(xpath));
	}

    /**
     * Creates an element at the provided path relative to the docNode
     * @param path
     * @return
     */
	public Element makeElement(String path) {
		return DocumentHelper.makeElement(this.docNode, path);
	}

    /**
     * Create an element at specified path relative to specified source node
     * @param source
     * @param path
     * @return
     */
	public Element makeElement(Branch source, String path) {
		return DocumentHelper.makeElement(source, path);
	}

	/**
	 *  Select Single node for provided xpath
	 *
	 *@param  xpath  xpath
	 *@return        NOT YET DOCUMENTED
	 */
	public Node selectSingleNode(String xpath) {
		return docNode.selectSingleNode(makeXPath(xpath));
	}


	/**
	 *  Expands xpath into namespace-aware version, hanlding attributes and
	 *  attribute/value specifiers.<p>
	 *
	 *
	 *  <dl>
	 *    <dt> /record/relation
	 *    <dd> /*[local-name()='record']/*[local-name()='relation'
	 *    <dt> //relation/@type
	 *    <dd> //*[local-name()='relation']/@type
	 *    <dt> /record/relation[@type='Has part']
	 *    <dd> /*[local-name()='record']/*[local-name()='relation'][@type='Has
	 *    part']
	 *  </dl>
	 *
	 *
	 *@param  s  xpath as a qualifed string
	 *@return    namespace aware xpath
	 */
	public static String makeXPath(String s) {
		// prtln ("\n" + s);
		String[] splits = s.split("/");
		
		prtln ("splits (" + splits.length + ")");
		for (int i=0;i<splits.length; i++)
			prtln ("- " + splits[i]);
	
		
		String xpath = "";

		// relative paths do not start with /
		int start = s.startsWith("/") ? 1 : 0;

		for (int i = start; i < splits.length; i++) {
			String segment = splits[i];
			if (i == 0)  // this is first segment of relative path
				xpath += "";
			else
				xpath += "/";

			if (segment.startsWith("@")) {
				xpath += segment;
			} else {
				String elementName = segment;
				String attrValueSpecifier = "";
				int avsStart = segment.indexOf("[@");
				if (avsStart != -1) {
					attrValueSpecifier = segment.substring(avsStart);
					elementName = segment.substring(0, avsStart);
				}
				/*
				 *  prtln ("elementName: " + elementName);
				 *  prtln ("attrValueSpecifier: " + attrValueSpecifier);
				 */
				if (elementName.length() > 0) {
					xpath += "*[local-name()=\'" + elementName + "\']";
				}
				if (attrValueSpecifier != null) {
					xpath += attrValueSpecifier;
				}
			}
		}
		return xpath;
	}


	/**
	 *  Gets the xmlNode attribute of the MetadataRecord object
	 *
	 *@return    The xmlNode value
	 */
	public Node getXmlNode() {
		return docNode;
	}


	/**
	 *  Gets the string representation of the MetadataRecord object
	 *
	 *@return    The xml value
	 */
	public String getXml() {
		if (docNode == null) {
			return "";
		} else {
			return docNode.asXML();
		}
	}


	/**
	 *  Gets the xml stripped of the XML declaration and DTD declaration.
	 *
	 *@return    The xml value
	 */
	public String getXmlStripped() {
		if (docNode == null) {
			return "";
		} else {
			try {
				StringBuffer xml = XMLConversionService.stripXmlDeclaration(
						new BufferedReader(new StringReader(docNode.asXML())));
				return xml.toString();
			} catch (Throwable t) {
				return "";
			}
		}
	}


	/**
	 *  Get a String representation of this XML.
	 *
	 *@return    The XML string
	 */
	public String toString() {
		return getXml();
	}

	// ---------------------- Debug info --------------------

	/**
	 *  Return a string for the current time and date, sutiable for display in log
	 *  files and output to standout:
	 *
	 *@return    The dateStamp value
	 */
	protected final static String getDateStamp() {
		return
				new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Description of the Method
	 *
	 *@param  node  Description of the Parameter
	 */
	protected final static void pp(Node node) {
		System.out.println(Dom4jUtils.prettyPrint(node));
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 *@param  s  The text that will be output to error out.
	 */
	protected final static void prtlnErr(String s) {
		System.err.println(getDateStamp() + " MetadataRecord Error: " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 *@param  s  The String that will be output.
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println(getDateStamp() + " MetadataRecord: " + s);
			System.out.println(" MetadataRecord: " + s);
		}
	}


	/**
	 *  Sets the debug attribute of the object
	 *
	 *@param  db  The new debug value
	 */
	public static void setDebug(boolean db) {
		debug = db;
	}
}

