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

import edu.ucar.dls.xml.Dom4jUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;

import org.dom4j.*;

/**
 *  Class to capture the results of a NDRSync operation in which a metadata record is written to the NDR.
 *
 * @author     Jonathan Ostwald
 * @version    $Id: SyncReportEntry.java,v 1.3 2009/03/20 23:33:56 jweather Exp $
 */
public class SyncReportEntry extends ReportEntry {
	private static boolean debug = true;

	String resourceHandle = null;

	/**
	 *  Constructor for the SyncReportEntry object
	 *
	 * @param  collectionConfig  NOT YET DOCUMENTED
	 * @param  collectionName    NOT YET DOCUMENTED
	 */
	public SyncReportEntry(String id, String errorMsg) {
		super (id, errorMsg);
		this.command = "package";
	}

	public SyncReportEntry(String id, String command, String resourceHandle, InfoXML ndrResponse) {
		this (id, null);
		this.command = command;
		this.resourceHandle = resourceHandle;
		this.ndrResponse = ndrResponse;
	}
	
	public String getResourceHandle () {
		return resourceHandle;
	}
		
	public String getMetadataHandle () {
		return ndrResponse.getHandle();
	}
		
	/**
	 *  The main program for the SyncReportEntry class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {

	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		SchemEditUtils.prtln(s, "SyncReportEntry");
	}
}

