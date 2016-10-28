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
package edu.ucar.dls.schemedit.threadedservices;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.threadedservices.NLDRProperties;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.index.reader.*;

import edu.ucar.dls.util.*;
import edu.ucar.dls.util.strings.FindAndReplace;
import edu.ucar.dls.webapps.tools.GeneralServletTools;

import javax.xml.transform.Transformer;
import javax.servlet.ServletContext;

import org.dom4j.*;

import java.util.*;
import java.text.*;
import java.io.*;

/**
 *  Supports NLDR export process, in which exported osm_collect records are
 *  mirrored to the dlese_collect/collect directory, where they are indexed by
 *  DDS as collection records.<p>
 *
 *  Required init params:
 *  <ul>
 *    <li> nldrMasterCollectionKey - collect key of master osm_collect
 *    collection</li>
 *    <li> nldrMasterCollectionPrefix - prefix of master osm_collect collection
 *    records</li>
 *  </ul>
 *
 *
 * @author    ostwald
 */
public class NLDRExportHelper {

	static boolean debug = true;

	private ServletContext servletContext = null;
	private String exportBaseDir = null;
	private Transformer transformer = null;
	private File dleseCollectExportDir = null;
	private final String xslRelativePath = "WEB-INF/xsl_files/osm_collect-v1.0-to-dlese_collect-v1.0.00.xsl";
	/**  NOT YET DOCUMENTED */
	
	NLDRProperties nldrProps = null;
	
	private String OSM_COLLECT_KEY; // = "osmc"; // in production it will be "collections"
	private String OSM_COLLECT_PREFIX; // = "OSM-COL"; // not available in this.dcsSetInfo.getIdPrefix();


	/**
	 *  Constructor for the NLDRExportHelper object
	 *
	 * @param  servletContext  NOT YET DOCUMENTED
	 * @param  exportBaseDir   NOT YET DOCUMENTED
	 */
	public NLDRExportHelper(ServletContext servletContext, String exportBaseDir) throws Exception {

		// get params as init params
		String propsFile = (String) servletContext.getInitParameter("nldrProperties");
		if (propsFile == null || propsFile.trim().length() == 0)
			throw new Exception ("nldrProperties initParam not found"); 
		
		nldrProps = new NLDRProperties (propsFile);
		
		this.OSM_COLLECT_KEY = nldrProps.MASTER_COLLECTION_KEY;
		this.OSM_COLLECT_PREFIX = nldrProps.MASTER_COLLECTION_PREFIX;
		
/* 		this.OSM_COLLECT_KEY = (String) servletContext.getInitParameter("nldrMasterCollectionKey");
		this.OSM_COLLECT_PREFIX = (String) servletContext.getInitParameter("nldrMasterCollectionPrefix"); */

		if (this.OSM_COLLECT_PREFIX == null)
			prtlnErr("WARNING: nldrMasterCollectionPrefix init param was not found");

		this.servletContext = servletContext;
		this.exportBaseDir = exportBaseDir;
		this.dleseCollectExportDir = new File(this.exportBaseDir, "dlese_collect/collect");

		String xslPath = null;
		try {
			xslPath =
				GeneralServletTools.getAbsolutePath(this.xslRelativePath, this.servletContext);
			transformer = XSLTransformer.getTransformer(xslPath);
		} catch (Throwable e) {
			prtlnErr("transformer WARNING: " + e.getMessage());
			prtlnErr("xslPath: " + xslPath);
		}
	}

	public NLDRProperties getNLDRProperties () {
		return this.nldrProps;
	}
	
	/**
	 *  Return a dlese_collect id from profived osmCollectId
	 *
	 * @param  osmCollectId  NOT YET DOCUMENTED
	 * @return               NOT YET DOCUMENTED
	 */
	private String makeDleseCollectId(String osmCollectId) {
		return "DDS-" + osmCollectId;
	}


	/**
	 *  get the OSM Collect id from the osmCollect Filename, verifying that the
	 *  filename is indeed a valid osmCollect Filename
	 *
	 * @param  osmCollectFileName  NOT YET DOCUMENTED
	 * @return                     The idFromFileName value
	 * @exception  Exception       NOT YET DOCUMENTED
	 */
	private String getIdFromFileName(String osmCollectFileName) throws Exception {
		try {
			verifyOsmCollectFileName(osmCollectFileName);
		} catch (Exception e) {
			throw new Exception("\'" + osmCollectFileName + "\' is an invalid file name : " + e.getMessage());
		}
		return osmCollectFileName.substring(0, osmCollectFileName.length() - 4);
	}


	/**
	 *  get the export file for provided dlese_collect id
	 *
	 * @param  dleseColId  NOT YET DOCUMENTED
	 * @return             The dleseColDestFile value
	 */
	private File getDleseColDestFile(String dleseColId) {
		return new File(dleseCollectExportDir, dleseColId + ".xml");
	}


	/**
	 *  verify that the osmDestFile begins with proper prefix and ends in .xml
	 *
	 * @param  fileName       NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	private void verifyOsmCollectFileName(String fileName) throws Exception {
		//
		if (!fileName.startsWith(OSM_COLLECT_PREFIX))
			throw new Exception("filename does not start with OSM_COLLECT_PREFIX (" + OSM_COLLECT_PREFIX + ")");
		if (!fileName.endsWith(".xml"))
			throw new Exception("filename does not end in \'.xml\'");
	}


	/**
	 *  Mirror an osm_collect record to the dlese_collect/collect directory after
	 *  transforming it to dlese_collect format, AND inserting a copy of the original osm_collect as
	 * "additionalMetadata"
	 *
	 * @param  osmCollectXml   osm_collect record XML
	 * @param  osmColFileName  osm_collect filename
	 * @exception  Exception   if unable to mirror record
	 */
	public void mirrorToDleseCollect(String osmCollectXml, String osmColFileName) throws Exception {
		prtln ("mirrorToDleseCollect()");
		prtln (" - osmColFileName: " + osmColFileName);
		if (this.transformer == null)
			throw new Exception("Transformer not found");

		String osmColId = null;
		try {
			osmColId = getIdFromFileName(osmColFileName);
		} catch (Exception e) {
			throw new Exception("\'" + osmColFileName + "\' is an invalid file name : " + e.getMessage());
		}
		// prtln("osmColId: " + osmColId);

		// we want to mirror this record to /dlese_collect/collect after
		// transform record

		String dleseCollectXml = XSLTransformer.transformString(osmCollectXml, this.transformer);
		// prtln("\nTransformed: " + dleseCollectXml);
		prtln ("dlese_collect created");
		// insert the osmCollect record (localized) as "additionalMetadata" into dlese_collect record
		try {
			prtln ("inserting osm_collect ...");
			Document dleseCollect = DocumentHelper.parseText(dleseCollectXml);
			Document osmCollect = Dom4jUtils.localizeXml(DocumentHelper.parseText(osmCollectXml));
			
			Element additionalMetadata = dleseCollect.getRootElement().addElement("additionalMetadata");
			additionalMetadata.add (osmCollect.getRootElement().createCopy());
			dleseCollectXml = dleseCollect.asXML();
		} catch (Throwable t) {
			prtln ("WARNING: unable to add additionalMetadata: " + t);
		}
		
		
		// Write dlese_collect_record to disk
		if (!dleseCollectExportDir.exists()) {
			prtln("dlese_collect export dir does not exist, creating ...");
			if (!dleseCollectExportDir.mkdirs())
				throw new Exception("unable to make export dir at " + dleseCollectExportDir);
		}

		String dleseColId = makeDleseCollectId(osmColId);
		File destFile = getDleseColDestFile(dleseColId);
		Files.writeFile(dleseCollectXml, destFile);
		prtln (" - wrote dlese_collect to: " + destFile);
	}


	/**
	 *  delete dlese_collect file corresponding to provided osmCollect filename
	 *
	 * @param  osmFileName    name of osm_collect file
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public void deleteMirroredFile(String osmFileName) throws Exception {
		String osmColId = this.getIdFromFileName(osmFileName);
		String dleseColId = this.makeDleseCollectId(osmColId);
		File dleseCollectFile = this.getDleseColDestFile(dleseColId);
		prtln("about to delete dleseCollectFile at " + dleseCollectFile);
		if (!dleseCollectFile.exists())
			throw new Exception("did not exis");
		else {
			if (!dleseCollectFile.delete())
				throw new Exception("reason unknown");
		}
		prtln("dlese_collect record deleted (" + dleseCollectFile.getName() + ")");
	}



	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println("NLDRExportHelper: " + s);
		}
	}


	private static void prtlnErr(String s) {
		System.out.println("NLDRExportHelper: " + s);
	}

}

