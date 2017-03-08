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
package edu.ucar.dls.standards.asn.util;

import edu.ucar.dls.standards.asn.AsnHelper;
import edu.ucar.dls.standards.asn.AsnAuthors;
import edu.ucar.dls.standards.asn.NameSpaceXMLDocReader;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.Files;
import org.dom4j.*;
import java.net.URL;
import java.util.*;
import java.io.*;

/**
 *  Populates a directory on the file system with an xml file for each Topic
 *  (e.g., "Science") in the ASN Standards Service containing a summary of all
 *  documents for that topic. {@link AsnCatalog} uses the cache created by this
 *  class. <p>
 *
 *  Approach: for each Jurisdiction explosed by the JesAndCo ASN Service (see
 *  {@link AsnAuthors}), obtain DocInfo instances for each standards document
 *  published by that Juris. In the process, create a mapping from Topic to list
 *  of all documents for that topic. <p>
 *
 *  In event ASN webservice is not available, use cached version.
 *
 * @author    Jonathan Ostwald
 */
public class CatalogCacher {
	private static boolean debug = true;

	private final static String BASE_DOCS_PURL = "http://asn.jesandco.org/api/1/documents";
	private NameSpaceXMLDocReader doc = null;
	private Map subjectMap = null;
	private AsnAuthors authorHelper = null;
	File baseCacheDir;


	/**
	 *  Constructor for the CatalogCacher object
	 *
	 * @param  baseCacheDirPath  Description of the Parameter
	 * @exception  Exception     NOT YET DOCUMENTED
	 */
	private CatalogCacher(String baseCacheDirPath) throws Exception {
		this.baseCacheDir = new File(baseCacheDirPath);
		if (!this.baseCacheDir.exists() && !this.baseCacheDir.mkdir()) {
			throw new Exception("could not create baseCacheDir at " + baseCacheDirPath);
		}

		AsnHelper asnHelper = AsnHelper.getInstance();
		asnHelper.refresh();

		authorHelper = asnHelper.getAuthors();

		subjectMap = new HashMap();

	}


	/**  Description of the Method */
	void processJurisdictions() {
		Iterator authorPurlIter = authorHelper.getAuthorPurls().iterator();
		while (authorPurlIter.hasNext()) {
			String purl = (String) authorPurlIter.next();
			try {
				processJurisdiction(purl);
			} catch (Exception e) {
				prtln("ERROR: trouble processing " + purl + ": " + e.getMessage());
			}
		}
	}


	/**
	 *  Given an authorPurl, collect Doc info for each document owned by
	 *  author/jurisdiction, and put into subject map (which is keyed by topic).
	 *
	 * @param  authorPurl     Description of the Parameter
	 * @exception  Exception  Description of the Exception
	 */
	void processJurisdiction(String authorPurl) throws Exception {
		String author = authorHelper.getAuthor(authorPurl);
		String authorName = authorHelper.getAuthorName(authorPurl);
		prtln("processJurisdiction() - " + author);
		String url = BASE_DOCS_PURL + "?jurisdiction=" + author;
		NameSpaceXMLDocReader doc = null;
		try {
			doc = new NameSpaceXMLDocReader(new URL(url));
		} catch (Throwable t) {
			throw new Exception("unable to process " + url + ": " + t.getMessage());
		}
		List documentNodes = doc.getNodes("/asnDocuments/Document");
		// prtln ("  " + documentNodes.size() + " document nodes found");
		Iterator docIter = documentNodes.iterator();
		while (docIter.hasNext()) {
			Element documentElement = (Element) docIter.next();
			DocInfo docInfo = new DocInfo(documentElement, doc, author, authorName);
			// prtln (docInfo.id + " - " + docInfo.topic);
			if (docInfo.topic != null) {
				String topic = docInfo.topic.trim();
				List docs = null;
				if (subjectMap.containsKey(topic)) {
					docs = (List) subjectMap.get(topic);
				}
				else {
					docs = new ArrayList();
				}
				docs.add(docInfo);
				subjectMap.put(topic, docs);
			}
		}
	}


	/**  Description of the Method */
	void report() {
		prtln("\n------ REPORT --------");
		Iterator subjectIter = subjectMap.keySet().iterator();
		while (subjectIter.hasNext()) {
			String subject = (String) subjectIter.next();
			List asnDocs = (List) subjectMap.get(subject);
			prtln(subject + " (" + asnDocs.size() + ")");
		}
	}


	/**
	 *  For each subject in the subjectMap, collect docInfos into one XML doc named
	 *  for subject, and write to file in baseCacheDir named for subject.<p>
	 *
	 *  I.e., the documents in the catalog are sorted by subject and each subject
	 *  gets its own file.
	 *
	 * @exception  Exception  Description of the Exception
	 */
	void writeSubjectFiles() throws Exception {
		prtln("writeSubjectFiles()");
		File cacheDir = new File(this.baseCacheDir, "topics");
		if (!cacheDir.exists() && !cacheDir.mkdir()) {
			throw new Exception("could not create cacheDir at " + cacheDir);
		}

		Iterator subjectIter = subjectMap.keySet().iterator();
		while (subjectIter.hasNext()) {
			String subject = (String) subjectIter.next();
			if (subject.trim().length() == 0) {
				continue;
			}
			List asnDocs = (List) subjectMap.get(subject);
			// prtln ("writing " + subject + " (" + asnDocs.size() + ")");
			Element root = DocumentHelper.createElement("AsnDocuments");
			root.setAttributeValue("topic", subject);
			for (Iterator i = asnDocs.iterator(); i.hasNext(); ) {
				DocInfo docInfo = (DocInfo) i.next();
				root.add(docInfo.asCatItemElement());
			}
			Document doc = DocumentHelper.createDocument(root);

			File out = new File(cacheDir, subject + ".xml");
			Dom4jUtils.writePrettyDocToFile(doc, out);
			// Dom4jUtils.writeDocToFile(doc, out);
			prtln("wrote to " + out);
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  baseCacheDir   NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void refreshCache(String baseCacheDir) throws Exception {
		prtln("Refreshing catalog cache...");
		CatalogCacher cacher = null;
		try {
			cacher = new CatalogCacher(baseCacheDir);
		} catch (Throwable t) {
			prtln("CatalogCacher error: " + t.getMessage());
		}
		// cacher.processJurisdiction (AsnAuthors.getAuthorPurl("CO"));
		cacher.processJurisdictions();
		cacher.writeSubjectFiles();
		prtln("updated cache at " + baseCacheDir);
	}


	/**
	 *  The main program for the CatalogCacher class - runs cacheAuthorsDoc
	 *  utility.
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  Description of the Exception
	 */
	public static void main(String[] args) throws Exception {
		// String baseCacheDir = "Z:/Documents/ASN/ASN_v3.1.0-cache/topics-new";
		// String baseCacheDir = "C:/Users/ostwald/devel/projects/dcs-project/web/WEB-INF/data/ASN-Catalog-Cache/topics";
		// String baseCacheDir = "/Users/ostwald/devel/projects/dcs-project/web/WEB-INF/data/ASN-Catalog-Cache";
		String baseCacheDir = "/Users/ostwald/tmp/ASN_Catalog_cache/";
		refreshCache(baseCacheDir);
	}


	/**
	 *  Encapsulutates a single "asnDocument" element of the ASN response including
	 *  all docs for given author (jurisdiction).
	 *
	 * @author    ostwald
	 */
	class DocInfo {
		/**  NOT YET DOCUMENTED */
		public Element element = null;
		/**  NOT YET DOCUMENTED */
		public String id;
		/**  NOT YET DOCUMENTED */
		public String title;
		/**  NOT YET DOCUMENTED */
		public String created;
		/**  NOT YET DOCUMENTED */
		public String topic;
		/**  NOT YET DOCUMENTED */
		public String author;
		/**  NOT YET DOCUMENTED */
		public String authorName;
		/**  NOT YET DOCUMENTED */
		public String status;

		/**  NOT YET DOCUMENTED */
		public boolean isInitialized = false;


		/**
		 *  Constructor for the DocInfo object
		 *
		 * @param  element     Description of the Parameter
		 * @param  doc         Description of the Parameter
		 * @param  author      Description of the Parameter
		 * @param  authorName  Description of the Parameter
		 */
		DocInfo(Element element, NameSpaceXMLDocReader doc, String author, String authorName) {
			this.element = element;
			this.author = author;
			this.authorName = authorName;
			this.id = safeGet("DocumentID[@type='asnUri']", doc);
			this.title = safeGet("DocumentTitle", doc);
			this.created = safeGet("LocalAdoptionDate", doc);
			this.topic = safeGet("DocumentSubject", doc);
			this.status = safeGet("PublicationStatus", doc);
			this.isInitialized = true;
		}


		/**
		 *  Description of the Method
		 *
		 * @param  xpath  Description of the Parameter
		 * @param  doc    Description of the Parameter
		 * @return        Description of the Return Value
		 */
		String safeGet(String xpath, NameSpaceXMLDocReader doc) {
			try {
				return doc.getValueAtPath(element, xpath);
			} catch (Throwable t) {
				// prtln ("safeGet had trouble with " + xpath + ": " + t.getMessage());
				// prtln(Dom4jUtils.prettyPrint(element));
			}
			return null;
		}


		/**
		 *  Description of the Method
		 *
		 * @return    Description of the Return Value
		 */
		Element asCatItemElement() {
			Element base = DocumentHelper.createElement("asnDocument");
			base.setAttributeValue("id", this.id);
			if (this.title != null) {
				base.addElement("title").setText(this.title);
			}
			if (this.topic != null) {
				base.addElement("topic").setText(this.topic);
			}
			if (this.author != null) {
				base.addElement("author").setText(this.author);
			}
			if (this.authorName != null) {
				base.addElement("authorName").setText(this.authorName);
			}
			if (this.created != null) {
				base.addElement("created").setText(this.created);
			}
			if (this.status != null) {
				base.addElement("status").setText(this.status);
			}
			return base;
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println("Cacher: " + s);
		}
	}
}

