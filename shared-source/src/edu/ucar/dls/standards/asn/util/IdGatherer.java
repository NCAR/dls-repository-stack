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
package edu.ucar.dls.standards.asn.util;

import edu.ucar.dls.standards.asn.*;
import edu.ucar.dls.schemedit.standards.*;
import edu.ucar.dls.schemedit.standards.asn.*;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.Files;
import org.dom4j.*;
import java.net.URL;
import java.util.*;
import java.io.*;

/**
 *  Populates a directory on the file system with an xml file for each Topic
 *  (e.g., "Science") in the ASN Standards Service containing a summary of all
 *  documents for that topic. {@link AsnCatalog} uses the cache created by
 *  this class. <p>
 *
 *  Approach: for each Jurisdiction explosed by the JesAndCo ASN Service (see
 *  {@link AsnAuthors}), obtain DocInfo instances for each standards document
 *  published by that Juris. In the process, create a mapping from Topic to list
 *  of all documents for that topic. <p>
 *
 *  In event ASN webservice is not available, use cached version.
 *
 *@author    Jonathan Ostwald
 */
public class IdGatherer {
	private static boolean debug = true;

	StandardsRegistry reg = null;
	List gatheredIds = null;


	/**
	 *  Constructor for the IdGatherer object
	 *
	 *@param  baseCacheDirPath  Description of the Parameter
	 *@exception  Exception     NOT YET DOCUMENTED
	 */
	private IdGatherer(String cachePath, String standards_library) throws Exception {
		AsnCatalog.setCacheDirectory(cachePath);
		
		StandardsRegistry.setLibraryDir (new File (standards_library));
		// StandardsRegistry.setLibraryDir (new File ("/Users/ostwald/devel/dcs-repos/dcs_release_test/app_info/standards_library"));

		StandardsRegistry reg = StandardsRegistry.getInstance();
		if (reg == null)
			prtln ("Registry is NULL?");
		else {
			gatheredIds = gatherStandardsIds(reg);
			prtln ("Gather IDs is complete");
			Element root = DocumentHelper.createElement("GatheredIds");
			Document doc = DocumentHelper.createDocument(root);
			Iterator idIter = this.gatheredIds.iterator();
			while (idIter.hasNext()) {
				GatheredId gid = (GatheredId)idIter.next();
				root.add(gid.asElement());
			}
			prtln (Dom4jUtils.prettyPrint(doc));
			Dom4jUtils.writePrettyDocToFile(doc, new File("C:/users/Ostwald/tmp/gathered-ids.xml"));
		}
	}
	
	public List gatherStandardsIds(StandardsRegistry reg) throws Exception {
		prtln("\ngetStandardsIds");

		AsnCatalog catalog = reg.getAsnCatalog();

		this.gatheredIds = new ArrayList();
		int num_subjects = 1000;
		int num_docs = 100;
		int subject_cnt = 0;
		int doc_cnt = 0;
		// Collections.sort (keys);
		Iterator subjectIter = catalog.getSubjects().iterator();
		while (subjectIter.hasNext()) {
			String subject = (String) subjectIter.next();
			prtln ("\nSubject: " + subject);
			Iterator docIter = catalog.getSubjectItems(subject).iterator();
			while (docIter.hasNext()) {
				AsnCatItemNew catInfo = (AsnCatItemNew)docIter.next();
				String docId = catInfo.getId();
				AsnDocInfo docInfo = null;
				try {
					docInfo = reg.registerId (docId);
				} catch (Exception e) {
					prtln ("could not get docInfo for " + docId + ": " + e.getMessage());
					continue;
				}
				AsnStandardsDocument asnDoc = reg.getStandardsDocumentForDocId(docId);
				if (asnDoc == null)
					prtln (docId + " NOT FOUND");
				else {
					prtln (docId + ": " + asnDoc.size());
					int num_to_get = Math.min (asnDoc.size(), 3);
					List nodeList = asnDoc.getNodeList();
					for (int i=0;i<num_to_get-1;i++) {
						AsnStandardsNode node = (AsnStandardsNode)nodeList.get(i);
						String stdId = node.getId();
						// prtln (stdId);
						this.gatheredIds.add (new GatheredId (stdId, docId));
					}
					if (++doc_cnt >+ num_docs)
						break;
				}
						
			}

			// prtln("\t key: " + key + "\n\tasnId: " + docId + "\n\t" + );
			if (++subject_cnt >+ num_subjects)
				break;
		}
		
		return this.gatheredIds;
		
	}


	/**
	 *  The main program for the IdGatherer class - runs cacheAuthorsDoc
	 *  utility.
	 *
	 *@param  args           The command line arguments
	 *@exception  Exception  Description of the Exception
	 */
	public static void main(String[] args) throws Exception {
		String cachePath = "C:/Users/ostwald/devel/projects/dcs-project/web/WEB-INF/data/ASN-Catalog-Cache";
		
		String tomcat = "C:/Program Files/Apache Software Foundation/Tomcat 6.0.35";
		String standardsLibrary = tomcat + "/var/standards_library";
		IdGatherer gatherer = null;
		try {
			gatherer = new IdGatherer(cachePath, standardsLibrary);
		} catch (Throwable t) {
			prtln("IdGatherer error: " + t.getMessage());
			t.printStackTrace();
		}
/* 		Iterator idIter = gatherer.gatheredIds.iterator();
		while (idIter.hasNext()) {
			GatheredId gid = (GatheredId)idIter.next();
			prtln (gid.toString());
		} */

	}

	public class GatheredId {
		public String docId;
		public String stdId;
		
		public GatheredId (String stdId, String docId) {
			this.stdId = stdId;
			this.docId = docId;
		}
		
		public Element asElement() {
			Element el = DocumentHelper.createElement("id");
			el.setAttributeValue("stdId", this.stdId);
			el.setAttributeValue("docId", this.docId);
			return el;
		}
		
		public String toString() {
			return "- " + this.stdId + "  (" + this.docId + ")";
		}
	}

	/**
	 *  NOT YET DOCUMENTED
	 *
	 *@param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println("IdGatherer: " + s);
		}
	}
}

