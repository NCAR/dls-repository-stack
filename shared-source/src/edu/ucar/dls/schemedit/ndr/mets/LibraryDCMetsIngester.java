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
import java.net.*;
import java.io.*;
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.ndr.NdrUtils;
import edu.ucar.dls.ndr.toolkit.NDRToolkit;
import edu.ucar.dls.ndr.toolkit.MimeTypes;
import org.nsdl.repository.model.types.*;
import org.nsdl.repository.model.types.Type;
import org.nsdl.repository.model.Datastream;
import org.nsdl.repository.model.Datastream.Name;
import org.nsdl.repository.model.NDRObject;
import org.nsdl.repository.model.NDRObjectInfo;
import org.nsdl.repository.access.Results;
import org.nsdl.repository.util.NDRObjectTemplate;

import edu.ucar.dls.schemedit.test.TesterUtils;
import edu.ucar.dls.standards.asn.NameSpaceXMLDocReader;

import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.ndr.toolkit.ContentUtils;
import edu.ucar.dls.ndr.apiproxy.NDRConstants;
import org.dom4j.*;

/**
 *  Unpack a METS envelop and place the contents (metadata + content) into the
 *  NDR.<p>
 *
 *  For now, assume the collection (mdp and agg) objects already exist in the
 *  NDR and are provided to this class.
 *
 * @author    ostwald
 */
public class LibraryDCMetsIngester extends MetsIngester {

	private static boolean debug = true;

	/**
	 *  Constructor for the LibraryDCMetsIngester object
	 *
	 * @param  doc            NOT YET DOCUMENTED
	 * @param  aggHandle      NOT YET DOCUMENTED
	 * @param  mdpHandle      NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */

	LibraryDCMetsIngester(Document doc, String aggHandle, String mdpHandle) throws Exception {
		super(doc, aggHandle, mdpHandle);
	}


	String getXmlFormat() {
		return "library_dc";
	}
	
	String getUrlXpath () {
		return "URL";
	}
	

	/**
	 *  The main program for the LibraryDCMetsIngester class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {

		String env = "sanluis";

		String propsPath = null;
		String metsPath = null;

		if (env.equals("sanluis")) {
			// propsPath = "C:/Documents and Settings/ostwald/devel/ndrServerProps/ndr.test.properties";
			propsPath = "C:/Documents and Settings/ostwald/devel/ndrServerProps/dls.ndr.properties";
			metsPath = "H:/Documents/Alliance/mets-work/mets-record.xml";
		}
		if (env.equals("taos")) {
			propsPath = "/Users/ostwald/projects/dcs.properties";
			metsPath = "/Users/ostwald/devel/tmp/mets-record.xml";
		}
		NdrUtils.setup(new File(propsPath));

		Document metsDoc = null;
		try {
			metsDoc = Dom4jUtils.getXmlDocument(new File(metsPath));
		} catch (Throwable e) {
			prtln(e.getMessage());
			return;
		}

		String ndrHost = "dls";
		String mdpHandle = null;
		String aggHandle = null;
		if (ndrHost.equals("dls")) {
			aggHandle = "ndr:16";
			mdpHandle = "ndr:17";
		}
		if (ndrHost.equals("ndrtest")) {
			aggHandle = "2200/test.20090821185036799T";
			mdpHandle = "2200/test.20090821185037493T";
		}

		LibraryDCMetsIngester ingester = new LibraryDCMetsIngester(metsDoc, aggHandle, mdpHandle);

	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  n  NOT YET DOCUMENTED
	 * @return    NOT YET DOCUMENTED
	 */
	private static String pp(Node n) {
		return Dom4jUtils.prettyPrint(n);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("LibraryDCMetsIngester: " + s);
			System.out.println(s);
		}
	}
	
}

