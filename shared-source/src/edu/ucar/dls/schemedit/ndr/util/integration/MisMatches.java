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
 *  Reads spreadsheet data (xml file created from spreadsheet) with data
 *  supplied by NSDL but augmented from NCS Collect records, with the purpose of
 *  determining overlaps and gaps between the collection management info in both
 *  models.
 *
 * @author    Jonathan Ostwald
 */
public class MisMatches {
	private static boolean debug = true;
	public static String dataFile = null;

	public List mismatches = null;

	/**
	 *  Constructor for the MisMatches object
	 *
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public MisMatches() throws Exception {
		Document doc = Dom4jUtils.getXmlDocument(new File (dataFile));
		mismatches = doc.selectNodes ("/mismatches/mismatch");
	}
	
	List getMisMatchingUrls () {
		List list = new ArrayList();
		prtln ("Mismatching URLS");
		for (Iterator i=mismatches.iterator();i.hasNext();) {
			MisMatch mm = new MisMatch ((Element)i.next());
			String ncsUrl = mm.ncsResult.resourceUrl;
			String nsdlUrl = mm.nsdlResult.resourceUrl;
			if (!ncsUrl.equals(nsdlUrl)) {
				list.add (mm);
				prtln ("\n" + mm.id);
				prtln ("\t ncs: " + ncsUrl);
				prtln ("\t nsdl: " + nsdlUrl);
			}
		}
		return null;
	}
	
	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @return    NOT YET DOCUMENTED
	 */
	public String toString() {
		String s = ("MisMatches values:\n");

		return s;
	}

	public static void setup () {
		CollectionIntegrator.setup();
		// dataFile = "/Users/ostwald/Desktop/Working/CollectionsData-02282008.xml";
		dataFile = "H:/Documents/NDR/NSDLCollections/MisMatches-02282008-2.xml";
	}
	
	public static void taosSetup () {
		String ndrApiBaseUrl = "http://ndr.nsdl.org/api";
		String ncsAgent = null;
		String keyFile = null;
		String userAgent = null;
		NdrUtils.setup(ndrApiBaseUrl, ncsAgent, keyFile, userAgent);
		NdrRequest.setVerbose(false);
		NdrRequest.setDebug(false);
	}

	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  args           NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		
		setup();
		prtln ("\nMisMatches ...\n");

		MisMatches mm = new MisMatches();
		prtln (mm.mismatches.size() + " mismatches found");
		mm.getMisMatchingUrls ();
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

