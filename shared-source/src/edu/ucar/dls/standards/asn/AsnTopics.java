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
 *  Class to read the ASN topics document from ASN web service and provide topic
 *  lookup by topic purl.
 *
 * @author    Jonathan Ostwald
 */
public class AsnTopics extends AsnHelperService {
	private static boolean debug = true;

	private static String CASHED_SERVICE_INDEX = "/edu/ucar/dls/standards/asn/cachedxml/ASNSubjects.xml";
	private static String SERVICE_INDEX_URI = "http://asn.jesandco.org/api/1/subjects";
	
	private static AsnTopics instance = null;
	
	public String getCachedServiceIndex () {
		return CASHED_SERVICE_INDEX;
	}
	
	public String getServiceIndexURI (){
		return SERVICE_INDEX_URI;
	}
	
	
	/**
	 *  Gets the AsnTopic instance
	 *
	 * @return                The instance value
	 * @exception  Exception  if topicURL cannot be processed
	 */
	public static AsnTopics getInstance() throws Exception {
		if (instance == null) {
			instance = new AsnTopics();
		}
		return instance;
	}


	/**
	 *  Constructor for the AsnTopics object
	 *
	 * @exception  Exception  if neither ASN topics (SERVICE_INDEX_URI) or cached topics file can be processed
	 */
	private AsnTopics() throws Exception {
		super();
	}

		
	public ServiceObject createObject (Element element) {
		return new AsnSubject (element);
	}
	
	public String getObjectXPath () {
		return "/asnSubjects/Subject";
	}

	/**
	 *  The main program for the AsnTopics class - runs cacheIndexDoc utility.
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(String[] args) throws Exception {
		
		// run cacheIndexDoc
 		// String srcDir = "C:/Documents and Settings/ostwald/devel/projects/dlese-tools-project/src";
 		String srcDir = "/Users/ostwald/devel/projects/dlese-tools-project/src";
 		String cachePath = srcDir + "/" + CASHED_SERVICE_INDEX;
		try {
			AsnTopics topics = new AsnTopics();
			// topics.cacheIndexDoc(cachePath);
			topics.report();
		} catch (Throwable t) {
			prtln("cacheIndexDoc error: " + t.getMessage());
		}
		
		// test getInstance
/* 		AsnTopics asnTopics = getInstance();
		Map topicMap = asnTopics.getTopicMap();
		for (Iterator i=topicMap.keySet().iterator();i.hasNext();) {
			String key = (String)i.next();
			prtln ("- " + key + ": " + (String)topicMap.get(key));
		} */
		
	}


	/**
	 *  Returns a topic for a given topic purl (e.g., http://purl.org/ASN/scheme/ASNTopic/behavioralStudies)
	 *
	 * @param  purl  NOT YET DOCUMENTED
	 * @return       The topic value
	 */
	public String getTopic(String purl) {
		AsnSubject subject = getAsnSubject(purl);
		if (subject == null) {
			prtln("WARNING: AsnSubject not found for " + purl);
			return null;
		}
		return subject.name;
	}

	public AsnSubject getAsnSubject (String purl) {
		return (AsnSubject)getObject(purl);
	}
		
	/**
	 *  Class to encapsulate a 'AsnSubject' element of the ASN 'jurisdictions'
	 *  response. See http://asn.jesandco.org/api/1/jurisdictions *
	 *
	 * @author    Jonathan Ostwald
	 */
	class AsnSubject extends ServiceObject{
		public String id;
		public String name;
		public String identifier;
		/**  the number of standards docs for this jurisdiction */
		public int docCount;


		/**
		 *  Constructor for the AsnSubject object
		 *
		 * @param  element  NOT YET DOCUMENTED
		 */
		public AsnSubject(Element element) {
			super(element);
			this.id = element.valueOf("@xml:id");
			this.name = element.valueOf("Subject");
			this.identifier = this.purl;
			this.docCount = Integer.parseInt(element.valueOf("DocumentCount"));
		}
		
		public String getPurl() {
			return element.valueOf("SubjectIdentifier");
		}
		
		public String toString () {
			return this.name + " (" + this.docCount + ")";
		}
	}

	

	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println("AsnTopics: " + s);
		}
	}
}

