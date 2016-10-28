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

import java.util.Properties;
import java.io.FileInputStream;
import java.io.File;

/**
 *  NOT YET DOCUMENTED
 *
 * @author    Jonathan Ostwald
 */
public class NLDRProperties {
	private static boolean debug = true;

	private Properties props = null;
	private File file = null;
	/**  NOT YET DOCUMENTED */
	public String BASE_PHYSICAL_ASSET_URL;
	/**  NOT YET DOCUMENTED */
	public String BASE_ASSET_DIR;
	/**  NOT YET DOCUMENTED */
	public String MASTER_COLLECTION_KEY;
	/**  NOT YET DOCUMENTED */
	public String MASTER_COLLECTION_PREFIX;
	/**  NOT YET DOCUMENTED */
	public String BASE_REPOSITORY_URL;
	public String BASE_DDS_SERVICE_URL;


	/**
	 *  Constructor for the NLDRProperties object
	 *
	 * @param  pfile          NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public NLDRProperties(String pfile) throws Exception {
		this.file = new File(pfile);
		try {
			FileInputStream fis = new FileInputStream(pfile);
			props = new Properties();
			props.load(fis);
			fis.close();
		} catch (Exception exc) {
			throw new Exception(
				"NLDRProperties could not load properties file \""
				 + pfile + "\"", exc);
		}

		BASE_PHYSICAL_ASSET_URL = getProperty("base.physical.asset.url", props, pfile);
		BASE_ASSET_DIR = getProperty("base.asset.dir", props, pfile);
		MASTER_COLLECTION_KEY = getProperty("master.collection.key", props, pfile);
		MASTER_COLLECTION_PREFIX = getProperty("master.collection.prefix", props, pfile);
		BASE_REPOSITORY_URL = getProperty("base.repository.url", props, pfile);
		BASE_DDS_SERVICE_URL = getProperty("base.dds.service.url", props, pfile);
	}


	/**
	 *  Gets the property attribute of the NLDRProperties object
	 *
	 * @param  propName           NOT YET DOCUMENTED
	 * @param  props              NOT YET DOCUMENTED
	 * @param  pfile              NOT YET DOCUMENTED
	 * @return                    The property value
	 * @exception  Exception      NOT YET DOCUMENTED
	 */
	protected String getProperty(
	                             String propName,
	                             Properties props,
	                             String pfile)
		 throws Exception {
		//prtln("prop name: " + propName);

		String res = props.getProperty(propName);
		if (res == null || res.length() == 0)
			throw new Exception(
				"NLDRProperties: invalid " + propName + " in properties file \""
				 + pfile + "\"", null);
		return res;
	}


	/**
	 *  The main program for the NLDRProperties class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		String path = "c:/tmp/nldr.properties";
		NLDRProperties nldrProps = new NLDRProperties(path);
		prtln("BASE_PHYSICAL_ASSET_URL: " + nldrProps.BASE_PHYSICAL_ASSET_URL);
	}

	public void report () {
		prtln ("\nNLDRProperties");
		prtln("  BASE_PHYSICAL_ASSET_URL: " + BASE_PHYSICAL_ASSET_URL);
		prtln("  BASE_ASSET_DIR: " + BASE_ASSET_DIR);
		prtln("  MASTER_COLLECTION_KEY: " + MASTER_COLLECTION_KEY);
		prtln("  MASTER_COLLECTION_PREFIX: " + MASTER_COLLECTION_PREFIX);
		prtln("  BASE_REPOSITORY_URL: " + BASE_REPOSITORY_URL);
	}
		
		

	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("AsnDocument: " + s);
			System.out.println(s);
		}
	}
}

