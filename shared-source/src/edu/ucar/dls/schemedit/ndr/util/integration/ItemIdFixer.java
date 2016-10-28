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

import edu.ucar.dls.schemedit.ndr.util.NCSCollectReader;
import edu.ucar.dls.schemedit.ndr.util.NCSWebServiceClient;
import edu.ucar.dls.schemedit.dcs.DcsDataRecord;
import edu.ucar.dls.schemedit.MetaDataFramework;
import edu.ucar.dls.schemedit.config.CollectionConfigReader;
import edu.ucar.dls.ndr.apiproxy.*;
import edu.ucar.dls.ndr.NdrUtils;
import edu.ucar.dls.ndr.reader.*;
import edu.ucar.dls.ndr.request.*;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XMLFileFilter;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.util.strings.FindAndReplace;
import edu.ucar.dls.index.SimpleLuceneIndex;
import org.dom4j.*;
import java.util.*;
import java.io.File;
import java.net.*;

/**
 *  Utilities to find the metdata objects in the NDR that have an "old-style" prefix ("oai:dls.org") for the 
 nsdl:itemId property, and convert to the "new-style" prefix ("oai:nsdl.org:ncs").
 *
 * @author    ostwald
 */
public class ItemIdFixer {
	private static boolean debug = true;

	static final String OLD_PREFIX = "oai:dls.org";
	static final String NEW_PREFIX = "oai:nsdl.org:ncs";
	
	MappingsManager mm = null;
	List mdps = null;
	
	/**
	 *  Constructor for the ItemIdFixer object
	 *
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	 ItemIdFixer() throws Exception {
		 String mmdata = CIGlobals.COLLECTION_MAPPINGS_DIR + "FinalMappingsManagerData.xml";
		 mm = new MappingsManager(mmdata);
	 }
	 
	 String getFixedItemId (String oldId) throws Exception {
		 if (!oldId.startsWith(OLD_PREFIX))
			 throw new Exception ("id (" + oldId +") does not start with " + OLD_PREFIX);
		 return FindAndReplace.replace(oldId, OLD_PREFIX, NEW_PREFIX, false);
	 }
	 
	List getMdps() throws Exception {
		if (mdps == null) {
			mdps = new ArrayList();
			File configsDir = new File (CIGlobals.COLLECTION_CONFIG_DIR);
			File [] configFiles = configsDir.listFiles(new XMLFileFilter());
			for (int i=0;i<configFiles.length;i++) {
				CollectionConfigReader configReader = new CollectionConfigReader (configFiles[i]);
				if (configReader == null)
					throw new Exception("configReader could not be created for " + configFiles[i]);
				String mdpHandle = configReader.getMetadataProviderHandle();
				if (mdpHandle.trim().length() == 0)
					continue;
				// prtln("\t mdpHandle: " + mdpHandle);
				mdps.add (mdpHandle);
			}
		}
		return mdps;
	}
	
	List processCollection (String mdpHandle) throws Exception {
		List errors = new ArrayList();
		MetadataProviderReader mdpReader = new MetadataProviderReader (mdpHandle);
		List mdHandles = mdpReader.getItemHandles();
		prtln ("\n processing " + mdpReader.getSetName() + " (" + mdpHandle + ")");
		int counter = 0;
		for (Iterator i=mdHandles.iterator();i.hasNext();) {
			prtln (++counter + "/" + mdHandles.size());
			String mdHandle = (String)i.next();
			try {
				processMetadata (mdHandle);
			} catch (Exception e) {
				errors.add ("error processing " + mdHandle + ": " + e.getMessage());
				continue;
			}
			// prtln ("processed " + mdHandle + " (" + ++counter + "/" + mdHandles.size());
		}
		if (!errors.isEmpty()) {
			prtln ("\nerrors processing " + mdpReader.getSetName() + " (" + mdpHandle + ")");
			for (Iterator i=errors.iterator();i.hasNext();)
				prtln ("\t" + (String)i.next());
		}
		return errors;
	}
		
	
	void processMetadata (String mdHandle) throws Exception {
		MetadataReader mdReader = null;
		try {
			mdReader = new MetadataReader (mdHandle);
		} catch (Exception e) {
			throw new Exception ("reader error: " + e.getMessage());
		}
		String itemId = mdReader.getProperty("itemId");
		String fixedItemId = null;
		try {
			fixedItemId = this.getFixedItemId (itemId);
		} catch (Exception e) {
			return; // nothing to be done
		}
		ModifyMetadataRequest request = new ModifyMetadataRequest(mdHandle);
		request.addCommand ("property", "itemId", fixedItemId);
		request.submit ();
		prtln ("  .. updated " + mdHandle);
	}
	 
	void processMdps () throws Exception {
		prtln (getMdps().size() + " collection ids found");
		for (Iterator i=getMdps().iterator();i.hasNext();) {
			String mdpHandle = (String)i.next();
			if (mdpHandle.equals("2200/20080317203737583T"))
				continue;
			processCollection (mdpHandle);
		}
	}
		
	void processHandles (String [] mdHandles) {
		for (int i=0;i<mdHandles.length;i++) {
			String mdHandle = mdHandles[i];
			try {
				processMetadata (mdHandle);
			} catch (Throwable t) {
				prtln("error with " + mdHandle + " :" + t.getMessage());
			}
		}
	}
	
	/**
	 *  The main program for the ItemIdFixer class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {

		CIGlobals.setup();

		boolean verbosity = false;
		NdrRequest.setDebug(verbosity);
		NdrRequest.setVerbose(verbosity);

		String [] stillToProcess = {
			"2200/20080125175222797T",
			"2200/20080125175222268T",
			"2200/20080220110441105T",
			"2200/20080228151642493T",
			"2200/20080125102313822T"
			};
		
		try {
			ItemIdFixer fixer = new ItemIdFixer();

			fixer.processHandles(stillToProcess);
/* 			String mdHandle = "2200/20080124181837730T";
			fixer.processMetadata (mdHandle); */
			
			
		} catch (Exception e) {
			prtln("Fixer ERROR: " + e.getMessage());
			e.printStackTrace();
		}
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
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		String prefix = null;
		if (debug) {
			NdrUtils.prtln(s, prefix);
		}
	}
}

