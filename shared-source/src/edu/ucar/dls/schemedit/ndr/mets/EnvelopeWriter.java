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
package edu.ucar.dls.schemedit.ndr.mets;

import edu.ucar.dls.services.dds.toolkit.*;

import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.io.*;
import edu.ucar.dls.schemedit.*;

import edu.ucar.dls.standards.asn.NameSpaceXMLDocReader;
import edu.ucar.dls.schemedit.test.TesterUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.schema.SchemaHelper;
import edu.ucar.dls.xml.schema.DocMap;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.util.Utils;
import edu.ucar.dls.ndr.toolkit.ContentUtils;
import org.dom4j.*;

/**
 * @author    ostwald
 */
public abstract class EnvelopeWriter {

	private static boolean debug = true;
	NameSpaceXMLDocReader envelope = null;
	DocMap docMap = null;
	SchemaHelper sh = null;
	
	Element fileGrp = null;
	Element structMap = null;
	String collection = null;
	File destFile = null;

	/**
	 *  Constructor for the EnvelopeWriter object<p>
	 TODO: ensure envelope validates against mets schema!
	 *
	 * @param  path  NOT YET DOCUMENTED
	 */

	public EnvelopeWriter(String ddsServiceUrl, String collection, File destFile ) throws Exception {
		this.destFile = destFile;
		this.collection = collection;
		try {
			URL metsSchemaUrl = new URL ("http://www.loc.gov/standards/mets/mets.xsd");
			String rootElementName = "this:mets";
			sh = new SchemaHelper(metsSchemaUrl, rootElementName);
		} catch (Exception e) {
			prtln ("could not load schema helper for METS: " + e.getMessage());
			return;
		}
		try {
			metsInit();
		} catch (Throwable t) {
			prtln ("mets document could not be initialized: " + t.getMessage());
			return;
		}
		RepositoryUtils repoUtils = new RepositoryUtils (ddsServiceUrl);
		Map itemRecordMap = repoUtils.getItemRecordMap (collection);
		
		int max = 500;
		int count = 0;
		
		for (Iterator i=itemRecordMap.keySet().iterator();i.hasNext();) {
			String id = (String)i.next();
			prtln ("\nprocessing " + id);
			Document doc = (Document)itemRecordMap.get(id);
			doc = Dom4jUtils.localizeXml(doc);
			try {
/* 				Node urlElement = doc.selectSingleNode ("/record/general/url");
				if (urlElement == null)
					throw new Exception ("urlElement not found");
				String urlStr = urlElement.getText().trim();
				if (urlStr == null || urlStr.length() == 0)
					throw new Exception ("url not found for record " + id);
				URL url = new URL (urlStr); */
				
				URL url = getResourceUrl (doc);
				this.addRecord(id, url, doc, getMetadataFormat());
				
				count++;
				prtln ("\t " + count + " of " + Math.min (itemRecordMap.size(), max));
				if (count >= max)
					break;
				
			} catch (Exception e) {
				prtln ("could not process " + id + ": " + e.getMessage());
			}
			if (this.destFile != null && count % 50 == 0 )
				this.write ();
		}
		if (this.destFile != null)
			this.write ();
	}
	
	void write () throws Exception {
		Dom4jUtils.writeDocToFile(this.envelope.getDocument(), this.destFile);
		prtln ("wrote to " + this.destFile.getName());
	}
	
	public abstract URL getResourceUrl (Document doc) throws Exception;
	
	public abstract String getMetadataFormat ();
	
	void metsInit() throws Exception {
		try {
			Document stub = sh.getMinimalDocument();
			this.envelope = new NameSpaceXMLDocReader(stub);
			this.docMap = new DocMap (this.envelope.getDocument(), this.sh);
		} catch (Exception e) {
			prtln("could not process xml: " + e.getMessage());
			return;
		}
		Element fileSec = this.envelope.getRootElement().addElement (this.envelope.getQName("this:fileSec"));
		fileGrp = fileSec.addElement (this.envelope.getQName("this:fileGrp"));
		fileGrp.addAttribute("ID", "GRP01");
		fileGrp.addAttribute("USE", "ACCESS");
		
		// the struct map element is already present, but it has a div element that must be removed
		structMap = (Element) this.envelope.getNode("this:mets/this:structMap/this:div");
		// structMap.clearContent();
	}		

	/**
	 *  Gets the fileGrp attribute of the EnvelopeWriter object
	 *
	 * @return    The fileGrp value
	 */
	void addMetadata (Document metadata, String dmdID, String xmlFormat) throws Exception {
		Element dmdSec = this.envelope.getRootElement().addElement(this.envelope.getQName ("this:dmdSec"));
		dmdSec.addAttribute("ID", dmdID);
		
		Element mdWrap = dmdSec.addElement (this.envelope.getQName("this:mdWrap"));
		mdWrap.addAttribute("MIMETYPE", "text/xml");
		mdWrap.addAttribute("MDTYPE", "OTHER");
		mdWrap.addAttribute("OTHERMDTYPE", xmlFormat);
		
		Element xmlData = mdWrap.addElement (this.envelope.getQName("this:xmlData"));
		xmlData.add (metadata.getRootElement().createCopy());
	}

	void orderElements () {
		this.docMap.orderSequenceElements(this.envelope.getRootElement());
	}

	/**
	 *  Adds a feature to the File attribute of the EnvelopeWriter object
	 *
	 * @param  url            an URL pointing to a binary object
	 * @param  fileID         the id to be assigned to this file
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	void addFile(URL url, String fileID) throws Exception {
		DownLoadedFile dlf = new DownLoadedFile(url);
		if (!dlf.getIsBinary())
			throw new Exception("content at " + url + " is not binary!");
		Element fileEl = this.fileGrp.addElement (this.envelope.getQName("this:file"));
		fileEl.addAttribute ("ID", fileID);
		fileEl.addAttribute ("MIMETYPE", dlf.getContentType());
		
		Element fContent = fileEl.addElement(this.envelope.getQName("this:FContent"));
		/* ID is optional and using filename is problematic because file names are often
			not schema-valid as IDs.
			Instead, get file name (when needed) from image URL, which is in the metadata
		*/
		// fContent.addAttribute("ID", dlf.getFileName());
		
		
		fContent.addAttribute("USE", "ACCESS");
		
		Element binData = fContent.addElement (this.envelope.getQName("this:binData"));
		
		binData.setText(dlf.getContent());
		
 		// track memory usage
		java.lang.Runtime runtime = java.lang.Runtime.getRuntime();
		long totalMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		prtln ("total memory: " + totalMemory);
		prtln ("free memory: " + freeMemory);
		prtln ("total - free: " + (totalMemory - freeMemory));
		
/* 		// the steps below do not seem to matter 
		dlf = null;
		System.runFinalization();
		System.gc(); */
		
		
		// to use fake data ...
		// binData.setText("UjBsR09EbGhjZ0dTQUxNQUFBUUNBRU1tQ1p0dU1GUXhEUzhi"); // fake data
	}

	void addStructDiv(String dmdID, String fileID) throws Exception {

		Element div = this.structMap.addElement ("this:div");
		div.addAttribute("DMDID", dmdID);
		
		Element fptr = div.addElement ("this:fptr");
		fptr.addAttribute("FILEID", fileID);

	}

	public void addRecord (String id, URL contentUrl, Document metadata, String xmlFormat) throws Exception {
		String dmdID = "dmd-" + id;
		String fileID = "file-" + id;
		addFile(contentUrl, fileID);
		addMetadata(metadata, dmdID, xmlFormat);
		addStructDiv(dmdID, fileID);
		orderElements();
		prtln ("added " + id);
	}
	

	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  n  NOT YET DOCUMENTED
	 */
	private static void pp(Node n) {
		prtln(Dom4jUtils.prettyPrint(n));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("EnvelopeWriter: " + s);
			System.out.println(s);
		}
	}

}

