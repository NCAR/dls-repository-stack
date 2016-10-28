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

package edu.ucar.dls.schemedit.ndr.util.integration;

import edu.ucar.dls.ndr.apiproxy.*;
import edu.ucar.dls.ndr.NdrUtils;
import edu.ucar.dls.ndr.reader.*;
import edu.ucar.dls.ndr.request.*;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.util.strings.FindAndReplace;
import edu.ucar.dls.index.SimpleLuceneIndex;
import org.dom4j.*;
import java.util.*;
import java.io.File;
import java.net.*;

/**
 *  Wraps a row from CollectionXSLReader, reprsenting information about a particular collection
 with the purpose of determining overlaps and gaps
 between the collection management info in NDR and NCS models.
 *
 * @author    Jonathan Ostwald
 */
public class CollectionXSLRecord {
	private static boolean debug = true;
	public Element element = null;
	private Map map = null;

	/**
	 *  Constructor for the CollectionXSLRecord object
	 *
	 * @param  xslPath  NOT YET DOCUMENTED
	 */
	public CollectionXSLRecord(Element rec) {
		this.element = rec;
		this.map = new HashMap ();
		for (Iterator i=rec.elementIterator();i.hasNext();) {
			Element e = (Element)i.next();
			map.put (e.getName(), e.getTextTrim());
		}
	}

	
	public String get (String field) {
		String val = (String)this.map.get (field);
		return (val != null ? val : "");
	}
	
	public String toString () {
		String s = "";
		for (Iterator i=map.keySet().iterator();i.hasNext();) {
			String name = (String)i.next();
			String val = (String)map.get(name);
			s += name + ": " + val;
			if (i.hasNext())
				s += "\n";			
		}
		return s;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  args  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) {
		String xml = "H:/Documents/NDR/NSDLCollections/NDRCollectionsNCSIDs.xml";
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
		String prefix = null;
		if (debug) {
			NdrUtils.prtln(s, prefix);
		}
	}
	

	
}

