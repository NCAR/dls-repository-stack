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

package edu.ucar.dls.schemedit.ndr;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.config.CollectionConfig;

import edu.ucar.dls.ndr.apiproxy.InfoXML;
import edu.ucar.dls.ndr.apiproxy.NDRConstants.NDRObjectType;

import edu.ucar.dls.xml.Dom4jUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;


import org.dom4j.*;

public class SyncReport {
	private static boolean debug = false;
	
	Map entryMap = null;
	String collectionName;
	CollectionConfig collectionConfig;
	
	public SyncReport (CollectionConfig collectionConfig, String collectionName) {
		this.collectionName = collectionName;
		this.collectionConfig = collectionConfig;
		this.entryMap = new TreeMap ();
	}
	
/* 	public void addEntry (String id, String command, String resourceHandle, InfoXML response) {
		entryMap.put (id, new SyncReportEntry (id, command, resourceHandle, response));
	}
	
	public void addEntry (String id, String errorMsg) {
		entryMap.put (id, new SyncReportEntry (id, errorMsg));
	} */
	
	public void addEntry (ReportEntry entry) {
		entryMap.put (entry.getId(), entry);
	}
	
	public Map getEntries () {
		return this.entryMap;
	}
	
	public int size() {
		return this.entryMap.size();
	}
	
	public List getEntryList () {
		prtln ("getEntryList()");
		List entryList = new ArrayList();
		Set keys = getEntries().keySet();
		String key;
		
		key = NDRObjectType.AGGREGATOR.getNdrResponseType();
		prtln ("looking for " + key);
		if (keys.contains(key)) {
			prtln ("\t FOUND");
			entryList.add ((ReportEntry)getEntry (key));
			keys.remove (key);
		}
		else
			prtln ("\t not found");
		
		key = NDRObjectType.METADATAPROVIDER.getNdrResponseType();
		prtln ("looking for " + key);
		if (keys.contains(key)) {
			prtln ("\t FOUND");
			entryList.add ((ReportEntry)getEntry (key));
			keys.remove (key);
		}
		else
			prtln ("\t not found");
		
		String [] keyArray = (String [])keys.toArray (new String [0]);
		Arrays.sort (keyArray);
		for (int i=0;i<keyArray.length;i++) {
			entryList.add ((ReportEntry)getEntry (keyArray[i]));
		}
		
		return entryList;
	}
		
		
	
	public ReportEntry getEntry (String id) {
		return (ReportEntry)entryMap.get (id);
	}
	
	public String getCollectionName () {
		return collectionName;
	}
	
	public String collection () {
		return collectionConfig.getId();
	}
	
	public static void main (String [] args) throws Exception {
		
	}
	
	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug)
			SchemEditUtils.prtln(s, "SyncReport");
	}
}
