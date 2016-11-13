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

import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.Files;
import org.dom4j.*;
import java.net.URL;
import java.util.*;
import java.io.*;

/**
 *  Class to read the ASN authors document from ASN web service and provide
 *  author lookup by author purl. In event ASN webservice is not available, used
 *  cached version.<p>
 NOTE: ASN has changed Author to Juristiction. IN this class, Jurisdiction is used to
 communicate with and parse ASN service, but "Author" is used in API for externals.
 *
 * @author    Jonathan Ostwald
 */
public class AsnAuthors extends AsnHelperService {
	private static boolean debug = true;

	private static String CASHED_SERVICE_INDEX = "/edu/ucar/dls/standards/asn/cachedxml/ASNJurisdiction.xml";
	private static String  SERVICE_INDEX_URI = "http://asn.jesandco.org/api/1/jurisdictions";
	// private static String BASE_OBJECT_URI = "http://purl.org/ASN/scheme/ASNJurisdiction/";
	private static String BASE_OBJECT_URI = AsnConstants.ASN_JURISDICTION_BASE;
	
	private static AsnAuthors instance = null;


	/**
	 *  Gets the AsnAuthor instance
	 *
	 * @return                The instance value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static AsnAuthors getInstance() throws Exception {
		if (instance == null) {
			instance = new AsnAuthors();
		}
		return instance;
	}

	public String getCachedServiceIndex () {
		return CASHED_SERVICE_INDEX;
	}
	
	public String getServiceIndexURI (){
		return SERVICE_INDEX_URI;
	}	

	/**
	 *  Constructor for the AsnAuthors object<p>
	 Try to read index from Base Index URI
	 *
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	protected AsnAuthors() throws Exception {
		super();
	}
		
	public ServiceObject createObject (Element element) {
		return new Jurisdiction (element);
	}
	
	public String getObjectXPath () {
		return "/asnJurisdictions/Jurisdiction";
	}

	/**
	 *  Gets the authorPurl attribute of the AsnAuthors class
	 *
	 * @param  abbrev  author abbreviation (e.g., 'AAAS')
	 * @return         The authorPurl value (e.g., http://purl.org/ASN/scheme/ASNJurisdiction/AAAS)
	 */
	public static String getAuthorPurl(String abbrev) {
		prtln ("getAuthorPurl: " + abbrev);
		Iterator objectIter = instance.getObjects().iterator();
		while (objectIter.hasNext()) {
			Jurisdiction juri = (Jurisdiction)objectIter.next();
			if (juri.abbrev.equals(abbrev))
				return juri.purl;
		}
		return null;
	}
	
	public static String getObjectPurl (String key) {
		return BASE_OBJECT_URI + key;
	}


	/**
	 *  Returns a string representation (abbreviation, e.g., 'CO') of the author
	 *  for a given author purl
	 *
	 * @param  purl  author purl (e.g., http://purl.org/ASN/scheme/ASNJurisdiction/CO)
	 * @return       The author value (e.g., CO)
	 */
	public String getAuthor(String purl) {
		Jurisdiction juris = (Jurisdiction) getObject(purl);
		if (juris == null) {
			prtln("WARNING: jurisdiction not found for " + purl);
			return null;
		}
		return juris.abbrev;
	}

	/**
	 *  Gets the authorName for the given author purl, using abbreviations for
	 *  organizations (e.g., AAAS) and full names for others.
	 *
	 * @param  purl  author purl (e.g., http://purl.org/ASN/scheme/ASNJurisdiction/MN)
	 * @return       The authorName value
	 */
	public String getAuthorName(String purl) {
		Jurisdiction juris = (Jurisdiction) getObject(purl);
		if (juris == null) {
			prtln("WARNING: jurisdiction not found for " + purl);
			return null;
		}
		if (juris != null) {
			if (juris.orgClass.equals("Organization"))
				return juris.abbrev;
			else
				return juris.name;
		}
		else
			return null;
	}
	
	private Jurisdiction getJurisdiction(String purl) {
		return (Jurisdiction)getObject(purl);
	}


	/**
	 *  The main program for the AsnAuthors class - runs cacheIndexDoc utility.
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(String[] args) {
		// String srcDir = "C:/Documents and Settings/ostwald/devel/projects/dlese-tools-project/src";
		String srcDir = "/Users/ostwald/devel/projects/dlese-tools-project/src";
		String cachePath = srcDir + "/" + CASHED_SERVICE_INDEX;
		try {
			
			AsnAuthors authors = AsnAuthors.getInstance();
			if (authors == null)
				throw new Exception ("AsnAuthors is NULL");
			// authors.cacheIndexDoc(cachePath);
			// authors.report();
			
			// getAuthorPurl tester
 			// Iterator authorIter = authors.getObjects().iterator(); 
			// while(authorIter.hasNext()) {
				// Jurisdiction author = (Jurisdiction)authorIter.next();
				// prtln ("- " + author.purl + ": " + getAuthorPurl(author.abbrev));
			// }
			
			// String abbrev = "CO";
			String abbrev = "WMO";
			String purl = getAuthorPurl(abbrev);
			prtln ("purl for " + abbrev + ": " + purl);
			Jurisdiction author = authors.getJurisdiction(purl);
			if (author == null)
				throw new Exception ("Jurisdiction NOT FOUND");
			
			prtln ("found Jurisdiction: " + author.toString());
			prtln (Dom4jUtils.prettyPrint(author.element));
		} catch (Throwable t) {
			prtln("Error: " + t.getMessage());
			t.printStackTrace();
		}
	}

	/**
	 *  Gets the authorPurls attribute of the AsnAuthors object
	 *
	 * @return    The authorPurls value
	 */
 	public Set getAuthorPurls() {
		return getObjectMap().keySet();
	}

	
	/**
	 *  Class to encapsulate a 'Jurisdiction' element of the ASN 'jurisdictions'
	 *  response. See http://asn.jesandco.org/api/1/jurisdictions *
	 *
	 * @author    Jonathan Ostwald
	 */
	class Jurisdiction extends ServiceObject {

		/**  e.g., American Association for the Advancement of Science */
		public String name;
		/**  e.g., AAAS */
		public String abbrev;
		/**  e.g., Organization */
		public String orgClass;

		public Jurisdiction() {}

		/**
		 *  Constructor for the Jurisdiction object
		 *
		 * @param  element  NOT YET DOCUMENTED
		 */
		public Jurisdiction(Element element) {
			super(element);
			
			this.name = element.valueOf("organizationName");
			this.abbrev = element.valueOf("organizationJurisdiction");
			this.orgClass = element.valueOf("organizationClass");
			this.docCount = Integer.parseInt(element.valueOf("DocumentCount"));
		}
		
		public String getPurl () {
			return this.element.valueOf("organizationAlias");
		}
		
		public String toString () {
			return this.abbrev + " (" + this.name + ")";
		}
	}

	/**
	 *  Constructor for the CustomJurisdiction object for provided values, used
	 *  to create a Jurisdiction that isn't supplied by ADN.<p>
	 *  NOTE: used to add WMO
	 *
	 * @param  element  NOT YET DOCUMENTED
	 */
	class CustomJurisdiction extends Jurisdiction {

		public CustomJurisdiction (String name, String alias, String jurisdiction, String orgClass, int docCount) {
			this.name = name;
			this.abbrev = jurisdiction;
			this.orgClass = orgClass;
			this.purl = alias;
			this.docCount = docCount;
		}
		
		public String getPurl () {
			return this.purl;
		}
	}
	
	/**
	Convenience fuction to provide CustomJurisdiction for WMO
	**/
	CustomJurisdiction getWMO () {
		String abbrev = "WMO";
		String purl = AsnConstants.ASN_JURISDICTION_BASE + abbrev;
		
		String orgClass = "??";
		String name = "World Meteorological Organization";
		int docCount = 2;
		return new CustomJurisdiction (name, purl, abbrev, orgClass, docCount);
	}
	
	/**
	Injects a list of ServiceObjects into the objectMap, e.g., to add an author
	that ASN doesn't supply
	**/
	public List<ServiceObject> getCustomObjects () {
		List<ServiceObject> list = new ArrayList<ServiceObject>();
		list.add (getWMO());
		return list;
														 
	}
	
	
	/**
	 *  Write to std out
	 *
	 * @param  s  content to write
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println("AsnAuthors: " + s);
		}
	}
}

