package edu.ucar.dls.standards.asn.util;

import edu.ucar.dls.schemedit.standards.asn.AsnDocKey;

import edu.ucar.dls.schemedit.test.TesterUtils;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XMLFileFilter;
import edu.ucar.dls.util.Files;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.dom4j.*;

/**
 *  Reads cached files on disk that contain all ASN Standards Documents
 *  associatged with a particular subject/topic (e.g., "Math", "Science"),
 *  available at the time they were compiled<p>
 *
 *  NOTE: getInstance() requires that "setCacheDirectory" is called prior to
 *  "getInstance". The mapping directory points to the cached XML files, each of
 *  which is associated with a particular subject/topic (e.g., "Math",
 *  "Science"). <p>
 *
 *  NOTE: Use {@link CatalogCacher.java}) periodically update the cached files.
 *
 *@author    Jonathan Ostwald
 */
public class AsnCatalog implements AsnCatalogInterface {
	private static boolean debug = true;

	private File cacheDir;
	private Map idMap = null;
	private Map subjectIdMap = null;
	private Map subjectItemsMap = null;
	private List subjects = null;

	private static String cachePath;
	private static AsnCatalog instance = null;


	/**
	 *  Gets the instance attribute of the AsnCatalog class - requires that
	 *  cachePath has already been initialized via setCacheDirectory.
	 *
	 *@return                The instance value
	 *@exception  Exception  if cachePath is not initialized or if files could not
	 *      be processed.
	 */
	public static AsnCatalog getInstance() throws Exception {
		if (cachePath == null) {
			throw new Exception("Cant instantiate: cachePath not initialized");
		}

		if (instance == null) {
			instance = new AsnCatalog();
		}
		return instance;
	}


	/**
	 *  Sets the cachePath attribute, which is the directory read to initialize the AsnCatalog.
	 *
	 *@param  path  The new cachePath value
	 */
	public static void setCacheDirectory(String path) {
		cachePath = path;
	}
	
	public static String getCacheDirectory() {
		return cachePath;
	}

	public void refresh () throws Exception {
		CatalogCacher.refreshCache (this.cacheDir.getAbsolutePath());
	}

	/**
	 *  Constructor for the AsnCatalog object given path to a cache directory
	 *
	 *@exception  Exception  NOT YET DOCUMENTED
	 */
	private AsnCatalog() throws Exception {
		if (cachePath == null) {
			throw new Exception("cache directory not supplied");
		}
		cacheDir = new File(cachePath);
		if (!cacheDir.exists()) {
			throw new Exception("cache directory does not exist at " + cachePath);
		}

		prtln("\nASN CATALOG initializing ...");
		this.idMap = new HashMap();
		this.subjectIdMap = new HashMap();
		this.subjectItemsMap = new HashMap();
		this.subjects = getSubjects();

		this.readCache();
		prtln("  AsnCatalog instantiated with " + this.subjects.size() + " subjects");
		prtln("    cachePath: " + cachePath);
		prtln("");
	}


	/**
	 *  Gets the item attribute of the AsnCatalog object
	 *
	 *@param  asnId  NOT YET DOCUMENTED
	 *@return        The item value
	 */
	public AsnCatItemNew getItem(String asnId) {
		return (AsnCatItemNew) idMap.get(asnId);
	}


	/**
	 *  Gets the status attribute of the AsnCatalog object
	 *
	 *@param  asnId  NOT YET DOCUMENTED
	 *@return        The status value
	 */
	public String getStatus(String asnId) {
		// prtln ("getStatus (" + asnId + ")");
		AsnCatItemNew asnCatItem = this.getItem(asnId);
		if (asnCatItem == null) {
			prtln("getStatus() WARNING: standard not found for " + asnId);
			return "";
		}

		return asnCatItem.getStatus();
	}


	/**
	 *  Gets the subjects attribute of the AsnCatalog object
	 *
	 *@return    The subjects value
	 */
	public List getSubjects() {
		if (this.subjects == null) {
			this.subjects = new ArrayList();

			File[] subjectFiles = cacheDir.listFiles(new XMLFileFilter());
			for (int i = 0; i < subjectFiles.length; i++) {
				String filename = subjectFiles[i].getName();
				String subject = filename.substring(0, filename.length() - 4);
				if (subject.length() > 0) {
					this.subjects.add(subject);
				}
			}
		}
		return this.subjects;
	}


	/**
	 *  Gets the subjectItems attribute of the AsnCatalog object
	 *
	 *@return    The subjectItems value
	 */
	public Map getSubjectItemsMap() {
		return this.subjectItemsMap;
	}


	/**
	 *  Read all mapping files in the cache directory. If this directory does not
	 *  exist no cache will be read.
	 *
	 *@exception  Exception  if an existing cache file cannot be processed
	 */
	private void readCache() throws Exception {

		for (Iterator i = this.subjects.iterator(); i.hasNext(); ) {
			String subject = (String) i.next();

			try {
				readCacheFile(subject);
			} catch (Exception e) {
				throw new Exception("could not process file for" + subject + ": " + e.getMessage());
			}
		}
	}


	/**
	 *  Reads a cached file containing all documents cached for the specified
	 *  subject and populates the data structures of this AsnCatalog.
	 *
	 *@param  subject        An ASN subject (e.g., "Science")
	 *@exception  Exception  if file could not be read
	 */
	private void readCacheFile(String subject) throws Exception {
		// prtln("readCacheFile() for subject: " + subject + " (cacheDir: " + cacheDir + ")");
		File subjectFile = new File(cacheDir, subject + ".xml");
		if (!subjectFile.exists()) {
			throw new Exception("subject file does not exist at " + subjectFile);
		}

		try {
			readCacheFile(subjectFile);
		} catch (Exception e) {
			throw new Exception("could not process " + subjectFile.getName() + ": " + e.getMessage());
		}
	}


	/**
	 *  Read a mapping file for a particular subject and AsnCatItemNew instances
	 *  (representing documents).
	 *
	 *@param  source         an XML file containing cache for a single subject
	 *@exception  Exception  if this file cannot be processed
	 */
	private void readCacheFile(File source) throws Exception {
		Document doc = Dom4jUtils.getXmlDocument(source);
		List asnDocs = doc.selectNodes("//asnDocument");
		String fileName = source.getName();
		// prtln("\nreading " + fileName);
		String subject = source.getName().substring(0, fileName.length() - 4);

		/*
		 *  // prtln (" subject: " + subject);
		 *  / Don't bother if we don't even know the subject (why would a subject-less file exist, anyway?)
		 *  if (subject == null || subject.trim().length()==0) {
		 *  / throw new Exception ("found non-subject in " + source + "??");
		 *  return;
		 *  }
		 */
		// prtln(asnDocs.size() + " docs found for subject=" + subject);
		for (Iterator i = asnDocs.iterator(); i.hasNext(); ) {
			Element asnDocElement = (Element) i.next();
			AsnCatItemNew catItem = new AsnCatItemNew(asnDocElement);
			// skip docs that have status of "In Process"
			// prtln ("status: " + catItem.getStatus());
			if ("In Process".equals(catItem.getStatus())) {
				// prtln ("Skipping " + catItem.getTitle());
				continue;
			}

			this.idMap.put(catItem.getId(), catItem);

			List subjectItemIds = (List) this.subjectIdMap.get(subject);
			if (subjectItemIds == null) {
				subjectItemIds = new ArrayList();
			}
			subjectItemIds.add(catItem.getId());
			this.subjectIdMap.put(subject, subjectItemIds);

			// List subjectItems = (List) this.subjectItemsMap.get(subject);
			List subjectItems = this.getSubjectItems(subject);
			if (subjectItems == null) {
				subjectItems = new ArrayList();
			}
			subjectItems.add(catItem);
			this.subjectItemsMap.put(subject, subjectItems);
		}
	}


	/**
	 *  Returns a list of all standards documents for a specified subject.
	 *
	 *@param  subject  ASN Subject (e.g., "Science")
	 *@return          a List of
	 */
	public List getSubjectItems(String subject) {
		return (List) this.subjectItemsMap.get(subject);
	}


	/**
	 *  produce debugging report for this AsnCatalog
	 */
	public void report() {
		prtln("\nAsnCatalogNew report");
		prtln(this.idMap.size() + " asn docIDs read for the following subjects");
		for (Iterator i = this.subjects.iterator(); i.hasNext(); ) {
			String subject = (String) i.next();
			int itemCount = this.getSubjectItems(subject).size();
			prtln(" - " + subject + " (" + itemCount + ")");
		}
	}


	/**
	 *  The main program for the AsnCatalog class
	 *
	 *@param  args           The command line arguments
	 *@exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		// String cachePath = "/Users/ostwald/devel/projects/dcs-project/web/WEB-INF/data/ACSR-to-ASN-Mappings";
		String cachePath = "C:/Users/ostwald/devel/projects/dcs-project/web/WEB-INF/data/ASN-Catalog-Cache";
		// String cachePath = "/Users/ostwald/tmp/catalogCache";
		AsnCatalog.setCacheDirectory(cachePath);
		AsnCatalog asnCat = AsnCatalog.getInstance();

		// test the id maps

/* 		asnCat.readCacheFile("Math");

		asnCat.report();

		prtln("");
		String asnId = "http://asn.jesandco.org/resources/D1000223";
		AsnCatItemNew item = asnCat.getItem(asnId);
		prtln(item.toString()); */

		asnCat.refresh();
		
	}


	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "AsnCatalog");
		}
	}

}

