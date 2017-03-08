/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package edu.ucar.dls.schemedit.ndr.util;

import edu.ucar.dls.ndr.apiproxy.NDRConstants;
import edu.ucar.dls.ndr.NdrUtils;
import edu.ucar.dls.ndr.request.*;
import edu.ucar.dls.ndr.reader.*;
import edu.ucar.dls.ndr.apiproxy.InfoXML;
import edu.ucar.dls.ndr.apiproxy.NDRConstants;
import edu.ucar.dls.ndr.apiproxy.NDRConstants.NDRObjectType;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XPathUtils;
import org.dom4j.*;
import java.util.*;
import java.io.File;

/**
 *  Utility to Download the nsdl_dc stream for all metadata items, for a
 *  specified metadataProvider, as files.
 *
 * @author    Jonathan Ostwald
 */
public class CollectionDownloader {

	private static boolean debug = true;

	String mdp;
	MetadataProviderReader mdpReader = null;
	File destDir;
	String prefix;


	/**
	 *  Constructor for the CollectionDownloader object given a dom4j.Document in
	 *  ncs_collect format.
	 *
	 * @param  mdpHandle      NOT YET DOCUMENTED
	 * @param  destPath       NOT YET DOCUMENTED
	 * @param  prefix         NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public CollectionDownloader(String mdpHandle, String destPath, String prefix) throws Exception {
		this.destDir = new File(destPath);
		if (!this.destDir.exists())
			throw new Exception("dest directory does not exist at " + destPath);
		this.prefix = prefix;
		try {
			this.mdpReader = new MetadataProviderReader(mdpHandle);
			this.download();
		} catch (Exception e) {
			prtln("ERROR: " + e.getMessage());
			e.printStackTrace();
		}

	}


	/**
	 *  Calculates an ID for the metadata record. mdReader, and recNum are provided
	 *  to allow flexibility in determining ids.
	 *
	 * @param  mdReader  NOT YET DOCUMENTED
	 * @param  recNum    NOT YET DOCUMENTED
	 * @return           The id value
	 */
	String getRecordId(MetadataReader mdReader, int recNum) {
		if ("bppb".equals(this.prefix))
			return this.prefix + "-" + recNum;

		else {
			String uniqueId = mdReader.getUniqueID();
			prtln("uniqueId: " + uniqueId);
			String pat = "CWIS-";
			int i = uniqueId.indexOf(pat);
			return this.prefix + "-" + uniqueId.substring(i + pat.length());
		}
	}


	/**
	 *  download metadata, assign recordId, and write to disk (in destdir)
	 *
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	void download() throws Exception {
		List itemHandles = this.mdpReader.getMemberHandles();
		prtln(itemHandles.size() + " handles found");
		int recCounter = 0;
		for (Iterator i = itemHandles.iterator(); i.hasNext(); ) {
			String mdHandle = (String) i.next();
			MetadataReader mdReader = new MetadataReader(mdHandle);
			Element nsdl_dc = mdReader.getDataStream("nsdl_dc");
			String recordId = getRecordId(mdReader, ++recCounter);
			prtln("recordId: " + recordId);

			Document doc = DocumentHelper.createDocument(nsdl_dc);
			// name file from recID
			Dom4jUtils.writeDocToFile(doc, new File(this.destDir, recordId + ".xml"));
			break;
		}
	}


	/**
	 *  The main program for the CollectionDownloader class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		NDRConstants.setNdrApiBaseUrl("http://ndr.nsdl.org/api");

		// directory holding subdirectories that will contain downloaded records
		String baseDest = "C:/tmp/downloaded-collections";

		String dest;
		String prefix;
		String mdpHandle;

		String batch = "eduplc";

		if (batch.equals("bppd")) {
			dest = baseDest + "/bppb";
			prefix = "bppb";
			mdpHandle = "2200/20091114113854725T";
		}
		else if (batch.equals("explore")) {
			dest = baseDest + "/explore";
			prefix = "explore";
			mdpHandle = "2200/20080701144650617T";
		}
		else if (batch.equals("eduplc")) {
			dest = baseDest + "/eduplc";
			prefix = "eduplc";
			mdpHandle = "2200/20080317203657821T";
		}
		else
			throw new Exception("unrecognized batch: " + batch);

		CollectionDownloader cd = new CollectionDownloader(mdpHandle, dest, prefix);
	}


	/**
	 *  pretty print provided node
	 *
	 * @param  n  node to pretty print
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
			System.out.println(s);
		}
	}
}

