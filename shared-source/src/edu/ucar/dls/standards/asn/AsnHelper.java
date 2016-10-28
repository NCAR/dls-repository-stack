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

package edu.ucar.dls.standards.asn;

import edu.ucar.dls.xml.XPathUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.io.File;

/**
 *  Utility class to provide acess to resolution services for ASN topics and
 *  authors. Useful because values for authors and topics in ASN standards are
 *  represented as purls, and we often need to resolve them into a
 *  human-relevant form.
 *
 * @author    Jonathan Ostwald
 */
public class AsnHelper {
	private static Log log = LogFactory.getLog(AsnHelper.class);
	private static boolean debug = true;

	private AsnTopics topics = null;
	private AsnAuthors authors = null;
	private static AsnHelper instance = null;
	private boolean strict = true;


	/**
	 *  Constructor for the AsnHelper object
	 *
	 * @param  standardsPath  NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	private AsnHelper()  {
		try {
			topics = AsnTopics.getInstance();
		} catch (Exception e) {
			prtlnErr ("WARNING: AsnTopics helper could not be instantiated: " + e.getMessage());
		}
		try {
			authors = AsnAuthors.getInstance();
		} catch (Exception e) {
			prtlnErr ("WARNING: AsnAuthors helper could not be instantiated: " + e.getMessage());
		}
		prtln ("AsnHelper initialized with " + topics.size() + " topics and " + authors.size() + " authors");
	}

	public AsnAuthors getAuthors() {
		return authors;
	}
	
	public AsnTopics getTopics() {
		return topics;
	}
	
	public void refresh() {
		prtln ("refreshing ...");
		try {
			topics.refresh();
		} catch (Exception e) {
			prtlnErr ("WARNING: AsnTopics helper could not refresh: " + e.getMessage());
		}
		try {
			authors.refresh();
		} catch (Exception e) {
			prtlnErr ("WARNING: AsnAuthors helper could refresh: " + e.getMessage());
		}
		prtln ("AsnHelper refreshed with " + topics.size() + " topics and " + authors.size() + " authors");
	}
	
	public static AsnHelper getInstance () {
		if (instance == null) {
			try {
				instance = new AsnHelper();
			} catch (Throwable t) {
				prtln ("WARNING could not instantiate AsnHelper: " + t.getMessage());
			}
		}
		return instance;
	}

	/**
	 *  Resolves provided authorPurl into human-relevant form
	 *
	 * @param  purl  NOT YET DOCUMENTED
	 * @return       The author value
	 */
	public String getAuthor(String purl) {
		if (authors != null)
			return authors.getAuthor(purl);

		if (strict)
			return null;
		
		return XPathUtils.getLeaf(purl);
	}

	public String getAuthorName(String purl) {
		if (authors != null)
			return authors.getAuthorName(purl);

		else
			return getAuthor(purl);
		
	}
	

	/**
	 *  Resolves provided topicPurl into human-relevant form.<p>
	 *
	 * @param  purl  NOT YET DOCUMENTED
	 * @return       The topic value
	 */
	public String getTopic(String purl) {
		if (topics != null)
			return topics.getTopic(purl);
		
		if (strict)
			return null;
		
		else
			return XPathUtils.getLeaf(purl);
	}


	/**
	 *  The main program for the AsnHelper class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		AsnHelper mgr = new AsnHelper();
		prtln ("topic: " + mgr.getTopic(AsnConstants.ASN_TOPIC_BASE + "math"));
		prtln ("author: " + mgr.getAuthor(AsnConstants.ASN_JURISDICTION_BASE + "CO"));
		mgr.topics.report();
		// mgr.authors.report();
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println("AsnHelper: " + s);
		}
	}
	
	private static void prtlnErr(String s) {
		System.out.println("AsnHelper: " + s);
	}
	
}

