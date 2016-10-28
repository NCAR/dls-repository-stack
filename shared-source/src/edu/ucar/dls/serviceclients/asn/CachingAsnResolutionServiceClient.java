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
package edu.ucar.dls.serviceclients.asn;

import edu.ucar.dls.serviceclients.webclient.WebServiceClient;
import edu.ucar.dls.serviceclients.webclient.WebServiceClientException;

import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XPathUtils;
import edu.ucar.dls.util.Files;
import org.dom4j.*;
import java.util.*;
import java.util.regex.*;
import java.io.File;
import java.net.*;

/**
 *  Given an asnId (e.g., http://asn.jesandco.org/resources/S10154EF) build a
 *  string of the form asnId, parent, ..., docId
 *
 * @author    Jonathan Ostwald
 */
public class CachingAsnResolutionServiceClient extends AsnResolutionServiceClient {

	private static boolean debug = false;
	private File id_cache = null;
	private File text_cache = null;
	private long cache_life = (long)(1000 * 60 * 60 * 24 * 7); // one week
	//private long cache_life = (long)(1000 * 60); // one minute


	public CachingAsnResolutionServiceClient(String baseWebServiceUrl) {
		this(baseWebServiceUrl, null);
	}
	
	/**
	 *  Constructor for the CachingAsnResolutionServiceClient object
	 *
	 * @param  baseWebServiceUrl  NOT YET DOCUMENTED
	 */
	public CachingAsnResolutionServiceClient(String baseWebServiceUrl, String cachePath) {
		super(baseWebServiceUrl);
		try {
			this.init(cachePath);
		} catch (Exception e) {
			prtln ("CachingAsnResolutionServiceClient init ERROR: " + e);
		}

	}
	
	private boolean fileIsStale (File file) {
		long mod = file.lastModified();
		long now = new Date().getTime();
		
		return (now - mod > cache_life);
	}
	
	private void init(String cachePath) throws Exception {
		if (cachePath == null)
			throw new Exception ("cachePath is required");
			
		File cacheDir = new File(cachePath);
		if (!cacheDir.exists()) {
			prtln ("cacheDir does not exist at " + cachePath);
			prtln ("creating ...");
			if (!cacheDir.mkdir()) {
				throw new Exception ("ERROR: could not create cacheDir at " + cacheDir);
			}
		}	
		id_cache = new File (cacheDir, "id_cache");
		if (!id_cache.exists() && !id_cache.mkdir())
			throw new Exception ("ERROR: could not create id cache at " + id_cache);

		text_cache = new File (cacheDir, "text_cache");
		if (!text_cache.exists() && !text_cache.mkdir())
			throw new Exception ("ERROR: could not create text cache at " + text_cache);
	}

	/**
	 *  Gets the asnFieldIndexFieldValue attribute of the CachingAsnResolutionServiceClient
	 *  object
	 *
	 * @param  stdId          NOT YET DOCUMENTED
	 * @return                The asnFieldIndexFieldValue value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public String getAsnIdIndexFieldValue(String stdId) throws Exception {
		File cachedResponse = new File (id_cache, getUniqueAsnIdPart(stdId));
		String fieldValue = null;
		if (cachedResponse.exists() && !this.fileIsStale(cachedResponse)) {
			prtln ("... using cached value");
			fieldValue = Files.readFile(cachedResponse).toString();
		}
		else {
			prtln ("... fetching value");
			try {
				fieldValue = super.getAsnIdIndexFieldValue (stdId);
			} catch (Exception e) {
				prtlnErr (e.getMessage());
				// cache an empty result if no standard found for this asnId
				if (e.getMessage().indexOf ("No standard found for asnId") != -1)
					fieldValue = "";
				else
					throw e;
			}
			Files.writeFile(fieldValue, cachedResponse);
		}
		return fieldValue;
	}

	/**
	 *  Gets the asnFieldIndexFieldValue attribute of the CachingAsnResolutionServiceClient
	 *  object
	 *
	 * @param  stdId          NOT YET DOCUMENTED
	 * @return                The asnFieldIndexFieldValue value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public String getAsnTextIndexFieldValue(String stdId) throws Exception {
		File cachedResponse = new File (text_cache, getUniqueAsnIdPart(stdId));
		String fieldValue = null;
		if (cachedResponse.exists() && !this.fileIsStale(cachedResponse)) {
			prtln ("... using cached value");
			fieldValue = Files.readFile(cachedResponse).toString();
		}
		else {
			prtln ("... fetching value");
			fieldValue = super.getAsnTextIndexFieldValue(stdId);
			Files.writeFile(fieldValue, cachedResponse);
		}
		return fieldValue;
	}

	

	/**
	 *  The main program for the CachingAsnResolutionServiceClient class
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(String[] args) {
		prtln("CachingAsnResolutionServiceClient");
		String cache = "C:/Program Files/Apache Software Foundation/Tomcat 6.0.35/var/asnClientCache";
		CachingAsnResolutionServiceClient client = null;
		String serviceUrl = "http://nsdldev.org/asn/service.do";
		String asnId = "http://asn.jesandco.org/resources/S10154EF";
		List results = null;
		Document response = null;
		try {
			client = new CachingAsnResolutionServiceClient(serviceUrl, cache);
			String indexFieldValue = client.getAsnTextIndexFieldValue(asnId);
			prtln("indexFieldValue: " + indexFieldValue);
		} catch (Throwable t) {
			prtln(t.getMessage());
			return;
		}

		// pp (response);
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
			System.out.println(s);
		}
	}
	
	private static void prtlnErr(String s) {
		System.out.println("AsnResoloutionServiceClient: " + s);
	}
	
}

