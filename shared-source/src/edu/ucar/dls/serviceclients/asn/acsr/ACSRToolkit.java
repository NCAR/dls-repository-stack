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
package edu.ucar.dls.serviceclients.asn.acsr;

import edu.ucar.dls.standards.asn.AsnDocument;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.Files;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.dom4j.*;

/**
 *  Provide convenience methods to exercise the ACSRService calls, see
 * (http://www.jesandco.net/asn/asnwebservice/acsrservice.asmx)
 *  to obtain a Standard Document
 *
 * @author    Jonathan Ostwald
 */
public class ACSRToolkit {
	private static boolean debug = true;


	/**  Constructor for the ACSRToolkit object */
	public ACSRToolkit() { }


	/**
	 *  Gets the jurisdiction attribute of the ACSRToolkit class
	 *
	 * @param  author  NOT YET DOCUMENTED
	 * @return         The jurisdiction value
	 */
	public static String getJurisdiction(String author) {
		return JurisMapper.getInstance().getJuris(author);
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  jurisdiction   NOT YET DOCUMENTED
	 * @param  subject        NOT YET DOCUMENTED
	 * @return                NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static List findDocuments(String jurisdiction, String subject) throws Exception {
		return findDocuments(jurisdiction, subject, null);
	}


	/**
	 *  Returns list of AcsrDocumentBean instances matching search criteria
	 *
	 * @param  jurisdiction   i.e., author (e.g., CO)
	 * @param  subject        i.e., topic (e.g., Science)
	 * @param  year           e.g., 2009
	 * @return                NOT YET DOCUMENTED
	 * @exception  Exception  if GetDocuments cannot execute
	 */
	public static List findDocuments(String jurisdiction, String subject, String year) throws Exception {
		GetDocuments client = new GetDocuments(jurisdiction, subject);
		List results = client.getResults();

		List filtered = new ArrayList();
		for (Iterator i = results.iterator(); i.hasNext(); ) {
			AcsrDocumentBean acsrDoc = (AcsrDocumentBean) i.next();
			if (year == null) {
				filtered.add(acsrDoc);
			}
			else if (acsrDoc.getLocalAdoptionDate().equals(year)) {
				filtered.add(acsrDoc);
				// prtln(acsrDoc.toString());
			}
		}
		return filtered;
	}


	/**
	 *  Gets the asnDocument attribute of the ACSRToolkit class
	 *
	 * @param  acsrId         NOT YET DOCUMENTED
	 * @return                The asnDocument value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static AsnDocument getAsnDocument(String acsrId) throws Exception {
		Document doc = null;
		try {
			GetDocument client = new GetDocument(acsrId);
			doc = client.getResult();
		} catch (Exception e) {
			throw new Exception("Get Document ERROR: " + e.getMessage());
		}

		AsnDocument asnDoc = null;
		try {
			asnDoc = new AsnDocument(doc, acsrId);
		} catch (Exception e) {
			throw new Exception("Could not create AsnDocument: " + e.getMessage());
		}
		return asnDoc;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  jurisdiction   i.e., author (e.g., CO)
	 * @param  subject        i.e., topic (e.g., Science)
	 * @param  year           e.g., 2009
	 * @return                NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static AcsrDocumentBean findSingleDocument(String jurisdiction, String subject, String year) throws Exception {
		List candidates = findDocuments(jurisdiction, subject, year);
		if (candidates.size() == 0)
			throw new Exception("No candidates found");

		if (candidates.size() > 1)
			throw new Exception("Ambigous results - " + candidates.size() + " found");

		return (AcsrDocumentBean) candidates.get(0);
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  acsrDoc        NOT YET DOCUMENTED
	 * @param  destDirpath    NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void downloadDoc(AcsrDocumentBean acsrDoc, String destDirpath) throws Exception {
		File destDir = new File(destDirpath);
		if (!destDir.exists()) {
			if (!destDir.mkdirs())
				throw new Exception("Dest directory does not exist at " + destDir);
		}

		String fileSaveName = acsrDoc.getFullFileName();
		Document stdDoc = null;
		try {
			stdDoc = getDocument(acsrDoc.getAcsrId());
		} catch (Exception e) {
			prtln("getAsnDocument error: " + e.getMessage());
		}

		File dest = new File(destDir, fileSaveName);
		try {
			Dom4jUtils.writeDocToFile(stdDoc, dest);
			prtln("doc written to " + dest);
		} catch (Exception e) {
			throw new Exception("could not write to disk: " + e.getMessage());
		}
	}


	/**
	 *  Download a single document matching provided criteria.
	 *
	 * @param  jurisdiction   NOT YET DOCUMENTED
	 * @param  subject        NOT YET DOCUMENTED
	 * @param  year           NOT YET DOCUMENTED
	 * @param  destDir        NOT YET DOCUMENTED
	 * @exception  Exception  if other than an single document matches criteria
	 */
	public static void fetchSingleDocument(String jurisdiction, String subject, String year, String destDir) throws Exception {
		AcsrDocumentBean acsrDoc = null;
		try {
			acsrDoc = findSingleDocument(jurisdiction, subject, year);
		} catch (Exception e) {
			throw new Exception ("fetchSingleDocument error: " + e.getMessage());
		}
		downloadDoc(acsrDoc, destDir);
	}


	/**
	 *  Download standard documents for provided jurisdiction into destDir
	 *
	 * @param  jurisdiction   NOT YET DOCUMENTED
	 * @param  destDir        NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void fetchDocuments(String jurisdiction, String destDir) throws Exception {
		fetchDocuments(jurisdiction, null, null, destDir);
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  jurisdiction   NOT YET DOCUMENTED
	 * @param  subject        NOT YET DOCUMENTED
	 * @param  destDir        NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void fetchDocuments(String jurisdiction, String subject, String destDir) throws Exception {
		fetchDocuments(jurisdiction, subject, null, destDir);
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  jurisdiction   NOT YET DOCUMENTED
	 * @param  subject        NOT YET DOCUMENTED
	 * @param  year           NOT YET DOCUMENTED
	 * @param  destDir        NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void fetchDocuments(String jurisdiction, String subject, String year, String destDir) throws Exception {
		List acsrDocs = null;
		try {
			acsrDocs = findDocuments(jurisdiction, subject, year);
		} catch (Exception e) {
			prtln("find Documents error: " + e.getMessage());
		}
		for (Iterator i = acsrDocs.iterator(); i.hasNext(); ) {
			AcsrDocumentBean acsrDoc = (AcsrDocumentBean) i.next();
			try {
				downloadDoc(acsrDoc, destDir);
			} catch (Exception e) {
				prtln("could not download " + acsrDoc.getFullFileName());
			}
		}
	}


	/**
	 *  Get a standards document for provided acsrId as XML Document
	 *
	 * @param  acsrId         e.g., a5bc5f13-3c33-465f-aa18-e8258055ca83
	 * @return                NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static Document getDocument(String acsrId) throws Exception {
		GetDocument client = new GetDocument(acsrId);
		return client.getResult();
	}


	/**
	 *  Gets the jurisdictions attribute of the ACSRToolkit class
	 *
	 * @return                The jurisdictions value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static List getJurisdictions() throws Exception {
		GetJurisdictions client = new GetJurisdictions();
		return client.getResults();
	}


	/**
	 *  Gets the subjects attribute of the ACSRToolkit class
	 *
	 * @return                The subjects value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static List getSubjects() throws Exception {
		GetSubjects client = new GetSubjects();
		return client.getResults();
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  jurisdiction   NOT YET DOCUMENTED
	 * @param  subject        NOT YET DOCUMENTED
	 * @param  year           NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void findSingleDocumentTester(String jurisdiction, String subject, String year) throws Exception {

		AcsrDocumentBean doc = findSingleDocument(jurisdiction, subject, year);
		if (doc != null)
			prtln(doc.getFullFileName());
	}


	/**
	 *  The main program for the ACSRToolkit class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		String subject = "Science";
		String jurisdiction = "CO";
		String year = "1995";
		String destDir = "C:/tmp";

		// findSingleDocumentTester (jurisdiction, subject, year);
		// fetchSingleDocument(jurisdiction, subject, year, destDir);
		// fetchDocuments ("AAAS", "Science", destDir);
		// fetchSubjectDocuments("Geography", "C:/tmp/ASN-2010-0903");
		findDocuments (jurisdiction, subject);
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  subject        NOT YET DOCUMENTED
	 * @param  destDir        NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void fetchSubjectDocuments(String subject, String destDir) throws Exception {
		Iterator jurisIter = getJurisdictions().iterator();
		while (jurisIter.hasNext()) {
			String jurisdiction = (String) jurisIter.next();
			fetchDocuments(jurisdiction, subject, destDir + "/" + subject);
		}
	}


	private static void prtln(String s) {
		if (debug) {
			// SchemEditUtils.prtln(s, "AsnDocument");
			SchemEditUtils.prtln(s, "");
		}
	}

}


