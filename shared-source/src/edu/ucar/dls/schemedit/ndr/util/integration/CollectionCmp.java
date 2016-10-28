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

import edu.ucar.dls.schemedit.ndr.util.*;
import edu.ucar.dls.ndr.apiproxy.*;
import edu.ucar.dls.ndr.NdrUtils;
import edu.ucar.dls.ndr.reader.*;
import edu.ucar.dls.ndr.request.*;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.util.strings.FindAndReplace;
import edu.ucar.dls.index.SimpleLuceneIndex;
import org.dom4j.*;
import java.util.Collection;
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
public class CollectionCmp {
	private static boolean debug = true;
	public static String dataFile = null;
	public static String outXmlFile = null;
	public List mismatches = null;
	Collection records = null;
	
	CollectionXSLReader collectionXSLReader;

	/**
	 *  Constructor for the CollectionCmp object
	 *
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public CollectionCmp() throws Exception {
		collectionXSLReader = new CollectionXSLReader (dataFile);
		records = collectionXSLReader.records.values();
		// prtln (records.size() + " records read");
		
	}
	
	void compareCollections () throws Exception {
		int max = 200;
		int counter = 0;
		mismatches = new ArrayList();
		for (Iterator i=records.iterator();i.hasNext();) {
			counter ++;
			CollectionXSLRecord rec = (CollectionXSLRecord)i.next();
			String ncsrecordid = rec.get("ncsrecordid");
			String aggregatorHandle = rec.get("aggregatorhandle");
			if (ncsrecordid == null || ncsrecordid.equals("??")) {
				prtln ("unmatched Aggregator: " + aggregatorHandle);
				continue;
			}
			
			if (aggregatorHandle == null) {
				prtln ("missing NCS Record for aggregator: " + aggregatorHandle);
				continue;
			}	
			
			
			try {
				compareCollection (ncsrecordid, aggregatorHandle);
			} catch (Exception e) {
				prtln ("could not compare for " + ncsrecordid + ": " + e.getMessage());
			}
			
			// prtln (counter + "/" + max);
			if (counter >= max ) {
				prtln ("STOPPING");
				break;
			}
		}
		// report();
		pp (this.reportAsXml());
		Dom4jUtils.writePrettyDocToFile(this.reportAsXml(), new File (outXmlFile));
		prtln ("wrote to " + outXmlFile);
	}
	
	void report () {
		System.out.println ("MISMATCHES");
		if (mismatches == null || mismatches.isEmpty()) {
			System.out.println ("no mismatches found");
			return;
		}
		
		for (Iterator i=mismatches.iterator();i.hasNext();) {
			MisMatch mismatch = (MisMatch)i.next();
			System.out.println (mismatch.toString());
		}
	}
	
	Document reportAsXml () {
		Element root = DocumentHelper.createElement ("mismatches");
		Document doc = DocumentHelper.createDocument(root);
		
		if (mismatches == null || mismatches.isEmpty()) {
			return doc;
		}
		
		for (Iterator i=mismatches.iterator();i.hasNext();) {
			MisMatch mismatch = (MisMatch)i.next();
			root.add (mismatch.asElement());
		}
		return doc;
	}
		
	
	void compareCollection (String ncsID, String aggHandle) throws Exception {
		// get NCSCollect Reader
		NCSCollectReader ncsRec = null;
		try {
			ncsRec = NSDLCollectionUtils.getNCSRecord(ncsID);
		} catch (Exception e) {
			throw new Exception ("could not get NCSRecord: " + e.getMessage());
		}
		Result ncsResult = getNCSResult (ncsRec);
		Result nsdlResult = getNSDLResult (aggHandle);
		
		if (!ncsResult.resourceHandle.equals (nsdlResult.resourceHandle) ||
			!ncsResult.resourceUrl.equals (nsdlResult.resourceUrl)) {
				
			MisMatch mm = new MisMatch (ncsID, ncsResult, nsdlResult);
			mismatches.add (mm);
		}
		else {
			// prtln ("match for: " + ncsID);
		}

	}
	
	Result getNCSResult (NCSCollectReader reader) {
		String resourceUrl = reader.getUrl();
		String resorceHandle = NdrUtils.findResource(resourceUrl);
		return new Result (resourceUrl, resorceHandle);
	}
	
	Result getNSDLResult (String aggHandle) {
		String resourceHandle = null;
		String resourceUrl = null;
		AggregatorReader agg = null;
		try {
			agg = new AggregatorReader(aggHandle);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		if (agg != null) {
			resourceHandle = agg.getCollectionResource();
			if (resourceHandle != null && resourceHandle.trim().length() > 0) {
				NdrObjectReader resource = null;
				try {
					resource = new NdrObjectReader (resourceHandle);
				} catch (Throwable t) {}
				if (resource != null)
					resourceUrl = resource.getProperty("hasResourceURL");
			}
		}
		return new Result (resourceUrl, resourceHandle);
	}

	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @return    NOT YET DOCUMENTED
	 */
	public String toString() {
		String s = ("CollectionCmp values:\n");

		return s;
	}

	public static void setup () {
		// taosSetup();
		String ndrApiBaseUrl = "";
		String ncsAgent = "";
		String keyFile = "";
		String userAgent = "";
		NdrUtils.setup(ndrApiBaseUrl, ncsAgent, keyFile, userAgent);
		

	}
	

	
	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  args           NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		File propFile = null; // propFile must be assigned!
		NdrUtils.setup (propFile);
		
		dataFile = "H:/Documents/NDR/NSDLCollections/CollectionsData-02282008.xml";
		outXmlFile = "H:/Documents/NDR/NSDLCollections/MisMatches-02282008.xml";
		
		prtln ("\nCollectionCmp ...\n");

		CollectionCmp comparer = new CollectionCmp();
		comparer.compareCollections();

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

	public static String getElementText(Element e, String tag) {
		String text = "";
		try {
			text = e.element(tag).getTextTrim();
		} catch (Throwable t) {}
		return text;
	}
	
}

