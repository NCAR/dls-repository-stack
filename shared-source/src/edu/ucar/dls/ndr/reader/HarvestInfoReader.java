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
package edu.ucar.dls.ndr.reader;

import edu.ucar.dls.ndr.apiproxy.NDRConstants;
import edu.ucar.dls.ndr.NdrUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.XPath;
import java.net.URL;
import java.util.*;

/**
 *  Reads HarvestInfoReaders as a dom4j.Document and provides access to
 *  components.
 *
 * @author    ostwald
 */
public class HarvestInfoReader {

	private static boolean debug = true;

	private Document doc = null;
	Map namespaces = null;


	/**
	 *  Constructor for the HarvestInfoReader object
	 *
	 * @param  xml            NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public HarvestInfoReader(String xml) throws Exception {
		this.doc = DocumentHelper.parseText(xml);
	}


	/**
	 *  Constructor for the HarvestInfoReader object
	 *
	 * @param  url            NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public HarvestInfoReader(URL url) throws Exception {
		this.doc = NdrUtils.getNDRObjectDoc(url);
	}


	/**
	 *  Gets the baseURL attribute of the HarvestInfoReader object
	 *
	 * @return    The baseURL value
	 */
	public String getBaseURL() {
		return getValue("/ingest:harvestInfo/ingest:harvestRequest/ingest:baseURL");
	}


	/**
	 *  Gets the sets attribute of the HarvestInfoReader object
	 *
	 * @return    The sets value
	 */
	public List getSets() {
		return getValues("/ingest:harvestInfo/ingest:harvestRequest/ingest:sets/ingest:set");
	}


	/**
	 *  Gets the nSMap attribute of the HarvestInfoReader object
	 *
	 * @return    The nSMap value
	 */
	public Map getNSMap() {
		if (namespaces == null) {
			namespaces = new HashMap();
			namespaces.put("ingest", "http://ns.nsdl.org/MRingest/harvest_v1.00/");
		}
		return namespaces;
	}


	/* 	private XPath getXPath (String path) {
		XPath xpath = DocumentHelper.createXPath(path);
		xpath.setNamespaceURIs(getNSMap());
		return xpath;
	} */
	private String getValue(String xpath) {
		XPath xpathObj = DocumentHelper.createXPath(xpath);
		xpathObj.setNamespaceURIs(getNSMap());
		Node node = xpathObj.selectSingleNode(doc);
		if (node != null)
			return node.getText();
		else
			return null;
	}


	private List getValues(String xpath) {
		XPath xpathObj = DocumentHelper.createXPath(xpath);
		xpathObj.setNamespaceURIs(getNSMap());
		List nodes = xpathObj.selectNodes(doc);
		List values = new ArrayList();
		for (Iterator i = nodes.iterator(); i.hasNext(); ) {
			values.add(((Node) i.next()).getText());
		}
		return values;
	}



	/**
	 *  The main program for the HarvestInfoReader class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		URL url = new URL("http://ndr.nsdl.org/api/get/2200/20061002124900610T/harvestInfo");
		HarvestInfoReader reader = new HarvestInfoReader(url);

		pp(reader.doc);

		prtln("baseURL: " + reader.getBaseURL());
		prtln("sets: ");
		for (Iterator i = reader.getSets().iterator(); i.hasNext(); )
			prtln("\t" + (String) i.next());
	}


	/**
	 *  Prints a dom4j.Node as formatted string.
	 *
	 * @param  node  NOT YET DOCUMENTED
	 */
	protected static void pp(Node node) {
		prtln(Dom4jUtils.prettyPrint(node));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println("HarvestInfoReader: " + s);
		}
	}

}

