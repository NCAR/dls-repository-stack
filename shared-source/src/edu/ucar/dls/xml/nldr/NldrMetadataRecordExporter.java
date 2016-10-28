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
package edu.ucar.dls.xml.nldr;

import edu.ucar.dls.schemedit.threadedservices.NLDRProperties;
import edu.ucar.dls.util.Utils;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XMLUtils;
import edu.ucar.dls.xml.*;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Attribute;
import org.dom4j.Node;

import java.util.*;
import java.lang.*;
import java.io.*;
import java.text.*;
import java.net.*;
import java.util.Hashtable;
import java.util.regex.*;

/**
 *  Reads XML records and converts to an exported form (i.e., "containing
 *  citableUrls"). NLDR metadata records may involve mulitple-namespaces and may conttain "assets"
 *  (primary content in the NLDR).
 *
 *@author    Jonathan Ostwald
 */
public abstract class NldrMetadataRecordExporter extends MetadataRecord {
	private static boolean debug = false;
	private Node recordNode = null;
	protected static NLDRProperties nldrProps = null;

	/**
	 *  Constructor that loads the given record. No validation is performed.
	 *
	 *@param  xml                    The XML to start with
	 *@exception  DocumentException  If error parsing the XML
	 */
	public NldrMetadataRecordExporter(String xml, NLDRProperties nldrProps) throws DocumentException, Exception {
		super(xml);
		this.nldrProps = nldrProps;
	}

	public static NLDRProperties getNLDRProperties () {
		return nldrProps;
	}
	
	public static void setNLDRProperties (NLDRProperties props) {
		nldrProps = props;
	}
	
	/**
	 *  Gets the assetNodes attribute of the NldrMetadataRecord object
	 *
	 *@return    The assetNodes value
	 */
	public abstract List getAssetNodes();
	

	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 *@param  s  The String that will be output.
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println(getDateStamp() + " NldrMetadataRecord: " + s);
			System.out.println(" NldrMetadataRecordExporter: " + s);
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

