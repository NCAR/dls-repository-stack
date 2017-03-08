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

import edu.ucar.dls.schemedit.test.TesterUtils;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.standards.asn.AsnDocument;
import edu.ucar.dls.serviceclients.asn.acsr.AcsrDocumentBean;
import edu.ucar.dls.serviceclients.asn.acsr.ACSRToolkit;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XMLFileFilter;
import edu.ucar.dls.util.Files;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.dom4j.*;

/**
 *  Use the ACSRService to build mappings between ACSR_IDs and ASN Ids.
 *
 * @author    Jonathan Ostwald
 */
public class AsnIdMappingsUpdater {
	private static boolean debug = true;

	private String mappingsDirectory;
	private Map subjectMappings = null;
	private Map acsrIdMap = null;
	private Map asnIdMap = null;
	private List updatedSubjects = null;


	/**
	 *  Constructor for the AsnIdMappingsUpdater object
	 *
	 * @param  mappingsDirectory  directory containing ASN ID mappings file
	 * @exception  Exception      NOT YET DOCUMENTED
	 */
	public AsnIdMappingsUpdater(String mappingsDirectory) throws Exception {
		this.mappingsDirectory = mappingsDirectory;
		this.subjectMappings = new HashMap();
		this.acsrIdMap = new HashMap();
		this.asnIdMap = new HashMap();
		this.readMappingFiles();
	}


	/**
	 *  Gets the asnId attribute of the AsnIdMappings object
	 *
	 * @param  acsrId  NOT YET DOCUMENTED
	 * @return         The asnId value
	 */
	public String getAsnId(String acsrId) {
		return (String) acsrIdMap.get(acsrId);
	}


	/**
	 *  Gets the acsrId attribute of the AsnIdMappings object
	 *
	 * @param  asnId  NOT YET DOCUMENTED
	 * @return        The acsrId value
	 */
	public String getAcsrId(String asnId) {
		return (String) asnIdMap.get(asnId);
	}


	/**
	 *  Gets the subjectMappings attribute of the AsnIdMappingsUpdater object
	 *
	 * @param  subject  NOT YET DOCUMENTED
	 * @return          The subjectMappings value
	 */
	public Map getSubjectMappings(String subject) {
		if (!this.subjectMappings.containsKey(subject))
			this.subjectMappings.put(subject, new HashMap());
		return (Map) this.subjectMappings.get(subject);
	}

	// ------------- initialize -----------------

	/**
	 *  Read all mapping files in the mappings directory. If this directory does
	 *  not exist no mappings will be read.
	 *
	 * @exception  Exception  if an existing mappings file cannot be processed
	 */
	public void readMappingFiles() throws Exception {
		File mappingsDir = new File(mappingsDirectory);
		if (!mappingsDir.exists()) {
			// throw new Exception("mappings directory does not exist at " + mappingsDirectory);
			return;
		}

		File[] subjectFiles = mappingsDir.listFiles(new XMLFileFilter());

		for (int i = 0; i < subjectFiles.length; i++) {
			try {
				readMappingFile(subjectFiles[i]);
			} catch (Exception e) {
				throw new Exception("could not process " + subjectFiles[i].getName() + ": " + e.getMessage());
			}
		}
	}


	/**
	 *  Read a mapping file for a particular subject and add the mappings to the
	 *  AsnIdMappingsUpdater
	 *
	 * @param  source         an XML file containing mappings for a single subject
	 * @exception  Exception  if this file cannot be processed
	 */
	public void readMappingFile(File source) throws Exception {
		Document doc = Dom4jUtils.getXmlDocument(source);
		List mappings = doc.selectNodes("//mapping");
		String fileName = source.getName();
		// prtln ("\nreading " + fileName);
		String subject = source.getName().substring(0, fileName.length() - 4);
		// prtln (mappings.size() + " mappings found");
		for (Iterator i = mappings.iterator(); i.hasNext(); ) {
			IdMapping idMapping = new IdMapping((Element) i.next(), subject);
			// prtln (" mapping: " + idMapping.toString());
			try {
				this.addMapping(idMapping);
			} catch (Exception e) {
				prtln("WARNING invalid mapping: " + e.getMessage());
			}
		}
	}


	/**
	 *  Get mapping associated with provided id and subject
	 *
	 * @param  acsrId   NOT YET DOCUMENTED
	 * @param  subject  NOT YET DOCUMENTED
	 * @return          The mapping value
	 */
	public IdMapping getMapping(String acsrId, String subject) {
		Map subjectMappings = this.getSubjectMappings(subject);
		return (IdMapping) subjectMappings.get(acsrId);
	}


	/**
	 *  Update the mappings in the AsnIdMappingsUpdater using information from the ACSR web
	 *  services.
	 *
	 * @exception  Exception  if a writable mappingsDirectory cannot be found or
	 *      created
	 */
	public void updateMappings() throws Exception {
		this.updatedSubjects = new ArrayList();
		File mappingsDir = new File(mappingsDirectory);
		if (!mappingsDir.exists()) {
			prtln("creating mappings dir at " + mappingsDirectory);
			if (!mappingsDir.mkdirs())
				throw new Exception("failed to create mappings directory at " + mappingsDirectory);
		}

		List subjects = ACSRToolkit.getSubjects();

		for (Iterator i = subjects.iterator(); i.hasNext(); ) {
			String subject = (String) i.next();
			/* 			if (!subject.equals("Reading"))
				continue; */
			updateSubjectMappings(subject);
		}
	}


	/**
	 *  If the ACSR service contains documents that are not yet known to the
	 *  AsnIdMappingsUpdater, add new mappings to AsnIdMappingsUpdater.
	 *
	 * @param  subject        the subject
	 * @exception  Exception  if there are problems with ACSR Service
	 */
	public void updateSubjectMappings(String subject) throws Exception {
		prtln("Getting \"" + subject + "\" mappings from ACSR ...");
		Iterator jurisIter = ACSRToolkit.getJurisdictions().iterator();
		while (jurisIter.hasNext()) {
			String jurisdiction = (String) jurisIter.next();
			List acsrDocs = ACSRToolkit.findDocuments(jurisdiction, subject, null);

			for (Iterator i = acsrDocs.iterator(); i.hasNext(); ) {
				AcsrDocumentBean acsrDoc = (AcsrDocumentBean) i.next();
				String acsrId = acsrDoc.getAcsrId();

				/* NOTE: currently we don't worry about the status of an ACSR Doc.
				   If we wanted to map only "Active" docs, we would check the status here
				   and act accordingly (not adding non-Active's that aren't currently mapped, and
				   removing not-Active's that ARE currently mapped)
				*/


				if (this.getMapping(acsrId, subject) == null) {

					// if (!this.getSubjectMappings(subject).containsKey(acsrId)) {
					IdMapping mapping = null;
					try {
						mapping = createMapping(acsrDoc, subject);
					} catch (Exception e) {
						prtln("could not create mapping for " + acsrDoc.getFullFileName());
						continue;
					}

					try {
						this.addMapping(mapping);
						prtln("added mapping: " + mapping.toString());
						if (!this.updatedSubjects.contains(subject))
							this.updatedSubjects.add(subject);
					} catch (Exception e) {
						prtln("WARNING: could not add mapping: " + e.getMessage());
					}

				}
			}
		}

	}


	/**
	 *  Write all updated mappings to disk
	 *
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public void writeUpdatedMappings() throws Exception {
		for (Iterator i = this.updatedSubjects.iterator(); i.hasNext(); )
			writeSubjectMappings((String) i.next());
	}


	/**
	 *  Write updated mappings for specified subject to disk
	 *
	 * @param  subject        if a mappings file cannot be updated
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public void writeSubjectMappings(String subject) throws Exception {
		prtln("\nWriting mappings for " + subject + " ...");
		Element root = DocumentHelper.createElement("mappings");
		root.addAttribute("fileCreated", SchemEditUtils.fullDateString(new Date()));
		root.addAttribute("subject", subject);

		List mappings = new ArrayList();
		mappings.addAll(this.getSubjectMappings(subject).values());

		prtln(mappings.size() + " mappings for " + subject);
		Collections.sort(mappings, new MappingComparator());
		for (Iterator m = mappings.iterator(); m.hasNext(); ) {
			IdMapping mapping = (IdMapping) m.next();
			root.add(mapping.asElement());
		}
		Document doc = DocumentHelper.createDocument(root);
		// prtln(Dom4jUtils.prettyPrint(doc));

		File dst = new File(mappingsDirectory, subject + ".xml");
		try {
			Dom4jUtils.writePrettyDocToFile(doc, dst);
			prtln("wrote to " + dst);
		} catch (Exception e) {
			throw new Exception("could not write xml to " + dst);
		}
	}


	/**
	 *  Creates a mapping between providee acsrDoc and associated AsnDoc.
	 *
	 * @param  acsrDoc        NOT YET DOCUMENTED
	 * @param  subject        NOT YET DOCUMENTED
	 * @return                The mapping value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public IdMapping createMapping(AcsrDocumentBean acsrDoc, String subject) throws Exception {

		AcsrDocInfo acsrDocInfo = new AcsrDocInfo(acsrDoc);

		AsnDocument asnDoc = null;
		try {
			asnDoc = ACSRToolkit.getAsnDocument(acsrDoc.getAcsrId());
		} catch (Exception e) {
			throw new Exception("Could not create AsnDocument: " + e.getMessage());
		}

		AsnDocInfo asnDocInfo = new AsnDocInfo(asnDoc);
		return new IdMapping(acsrDocInfo, asnDocInfo, subject);
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  acsrId   NOT YET DOCUMENTED
	 * @param  subject  NOT YET DOCUMENTED
	 */
	public void removeMapping(String acsrId, String subject) {
		Map subjectMappings = this.getSubjectMappings(subject);
		subjectMappings.remove(acsrId);
		this.acsrIdMap.remove(acsrId);
		List asnIdsToRemove = new ArrayList();
		for (Iterator i = this.asnIdMap.keySet().iterator(); i.hasNext(); ) {
			String asnId = (String) i.next();
			String myAcsrId = (String) this.asnIdMap.get(asnId);
			if (myAcsrId.equals(acsrId))
				asnIdsToRemove.add(asnId);
		}
		for (Iterator i = asnIdsToRemove.iterator(); i.hasNext(); ) {
			this.asnIdMap.remove(i.next());
		}
	}


	/**
	 *  Adds a Mapping to the AsnIdMappingsUpdater object
	 *
	 * @param  mapping        The feature to be added to the Mapping attribute
	 * @exception  Exception  if mapping is not valid
	 */
	public void addMapping(IdMapping mapping) throws Exception {
		try {
			mapping.validate();
		} catch (Exception e) {
			prtln("\nrejecting invalid mapping: " + e.getMessage());
			return;
		}
		Map subjectMappings = this.getSubjectMappings(mapping.subject);
		String asnId = mapping.asnInfo.id;
		String acsrId = mapping.acsrInfo.id;
		subjectMappings.put(acsrId, mapping);
		this.asnIdMap.put(asnId, acsrId);
		this.acsrIdMap.put(acsrId, asnId);
	}


	/**  produce debugging report for this AsnIdMappingsUpdater */
	public void report() {
		prtln("\nAsnIdMappingsUpdater report");
		prtln(" - " + this.acsrIdMap.size() + " acsrIDs");
		prtln(" - " + this.asnIdMap.size() + " asnIDs");
		prtln("\nSubjectMaps");
		Iterator subjIter = this.subjectMappings.keySet().iterator();
		while (subjIter.hasNext()) {
			String subject = (String) subjIter.next();
			Map mappings = this.getSubjectMappings(subject);
			prtln("- " + subject + " (" + mappings.size() + ")");
		}
	}


	/**
	 *  The main program for the AsnIdMappingsUpdater class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		String mappingsDirectory = "C:/tmp/ACSR-to-ASN-Mappings";
		AsnIdMappingsUpdater mapper = new AsnIdMappingsUpdater(mappingsDirectory);
		mapper.report();

		// test the id maps
		/* 		prtln("");
		String asnId = "http://purl.org/ASN/resources/D1000034";
		String acsrId = mapper.getAcsrId(asnId);
		prtln("acsrId: " + acsrId);
		prtln("asnId: " + mapper.getAsnId(acsrId) + " (" + asnId + ")"); */
		prtln("\nUpdating mappings");
		mapper.updateMappings();

		prtln("\nUpdated Subjects");
		for (Iterator i = mapper.updatedSubjects.iterator(); i.hasNext(); )
			prtln("- " + (String) i.next());

		prtln("\nwriting Updated Mappings");
		mapper.writeUpdatedMappings();

		if (!mapper.updatedSubjects.isEmpty())
			mapper.report();
	}


	/**
	 *  Class to hold mapping from acsrDocument to asnDocument
	 *
	 * @author    Jonathan Ostwald
	 */
	public class IdMapping {
		AcsrDocInfo acsrInfo;
		AsnDocInfo asnInfo;
		String subject;


		/**
		 *  Constructor for the IdMapping object
		 *
		 * @param  e        NOT YET DOCUMENTED
		 * @param  subject  NOT YET DOCUMENTED
		 */
		public IdMapping(Element e, String subject) {
			this.subject = subject;
			this.acsrInfo = new AcsrDocInfo(e.element("acsrInfo"));
			this.asnInfo = new AsnDocInfo(e.element("asnInfo"));
		}


		/**
		 *  Sets the status attribute of the IdMapping object
		 *
		 * @param  status  The new status value
		 */
		public void setStatus(String status) {
			this.acsrInfo.status = status;
		}


		/**
		 *  Gets the status attribute of the IdMapping object
		 *
		 * @return    The status value
		 */
		public String getStatus() {
			return this.acsrInfo.status;
		}


		/**
		 *  Constructor for the IdMapping object
		 *
		 * @param  acsrInfo  NOT YET DOCUMENTED
		 * @param  asnInfo   NOT YET DOCUMENTED
		 * @param  subject   NOT YET DOCUMENTED
		 */
		public IdMapping(AcsrDocInfo acsrInfo, AsnDocInfo asnInfo, String subject) {
			this.acsrInfo = acsrInfo;
			this.asnInfo = asnInfo;
			this.subject = subject;
		}


		/**
		 *  NOT YET DOCUMENTED
		 *
		 * @return    NOT YET DOCUMENTED
		 */
		public Element asElement() {
			Element mapping = DocumentHelper.createElement("mapping");
			mapping.add(acsrInfo.asElement());
			mapping.add(asnInfo.asElement());
			return mapping;
		}


		/**
		 *  NOT YET DOCUMENTED
		 *
		 * @exception  Exception  NOT YET DOCUMENTED
		 */
		public void validate() throws Exception {
			if (this.acsrInfo == null)
				throw new Exception("acsrInfo not found");
			if (this.asnInfo == null)
				throw new Exception("asnInfo not found");
			if (this.acsrInfo.id == null)
				throw new Exception("acsrId not found");
			if (this.asnInfo.id == null)
				throw new Exception("asnId not found");
			// prtln (acsrInfo.status);

			// topics never match for "US History" (ascr) vs "U.S. History" (asn)
			/*if (!this.asnInfo.topic.equals(this.acsrInfo.subject))
				throw new Exception ("topic mismatch: (" + this.asnInfo.topic + " vs " + this.acsrInfo.subject + ")");*/
		}


		/**
		 *  NOT YET DOCUMENTED
		 *
		 * @return    NOT YET DOCUMENTED
		 */
		public String toString() {
			String s = "Mapping for " + asnInfo.author + " - " + asnInfo.created + " - " + asnInfo.title;
			s += "\n\t" + this.acsrInfo.id + " -> " + this.asnInfo.id;
			return s;
		}
	}


	/**
	 *  Bean to hold info about an AcsrDocument
	 *
	 * @author    Jonathan Ostwald
	 */
	public class AcsrDocInfo {
		String id;
		String title;
		String localAdoptionDate;
		String subject;
		String jurisdiction;
		String fileName;
		String status;


		/**
		 *  Constructor for the AcsrDocInfo object
		 *
		 * @param  acsrDoc  NOT YET DOCUMENTED
		 */
		public AcsrDocInfo(AcsrDocumentBean acsrDoc) {
			this.id = acsrDoc.getAcsrId();
			this.title = acsrDoc.getTitle();
			this.localAdoptionDate = acsrDoc.getLocalAdoptionDate();
			this.subject = acsrDoc.getSubject();
			this.jurisdiction = acsrDoc.getJurisdiction();
			this.fileName = acsrDoc.getFileName();
			this.status = acsrDoc.getStatus();
		}


		/**
		 *  Constructor for the AcsrDocInfo object
		 *
		 * @param  e  NOT YET DOCUMENTED
		 */
		public AcsrDocInfo(Element e) {
			this.id = e.attributeValue("id");
			this.title = e.element("title").getTextTrim();
			this.localAdoptionDate = e.element("localAdoptionDate").getTextTrim();
			this.subject = e.element("subject").getTextTrim();
			this.jurisdiction = e.element("jurisdiction").getTextTrim();
			this.fileName = e.element("fileName").getTextTrim();
			this.status = e.element("status").getTextTrim();
		}


		/**
		 *  NOT YET DOCUMENTED
		 *
		 * @return    NOT YET DOCUMENTED
		 */
		public Element asElement() {
			Element e = DocumentHelper.createElement("acsrInfo");
			e.addAttribute("id", this.id);
			e.addElement("title").setText(this.title);
			e.addElement("localAdoptionDate").setText(this.localAdoptionDate);
			e.addElement("subject").setText(this.subject);
			e.addElement("jurisdiction").setText(this.jurisdiction);
			e.addElement("fileName").setText(this.fileName);
			e.addElement("status").setText(this.status);
			return e;
		}
	}


	/**
	 *  * Bean to hold info about an AsnDocument
	 *
	 * @author    Jonathan Ostwald
	 */
	public class AsnDocInfo {
		String id;
		String title;
		String created;
		// String fileCreated;
		String topic;
		String author;


		/**
		 *  Constructor for the AsnDocInfo object
		 *
		 * @param  asnDoc  NOT YET DOCUMENTED
		 */
		public AsnDocInfo(AsnDocument asnDoc) {
			this.id = asnDoc.getIdentifier();
			this.title = asnDoc.getTitle();
			this.created = asnDoc.getCreated();
			// this.fileCreated = asnDoc.getFileCreated();
			this.topic = asnDoc.getTopic();
			this.author = asnDoc.getAuthor();
		}


		/**
		 *  Constructor for the AsnDocInfo object
		 *
		 * @param  e  NOT YET DOCUMENTED
		 */
		public AsnDocInfo(Element e) {
			this.id = e.attributeValue("id");
			this.title = e.element("title").getTextTrim();
			this.created = e.element("created").getTextTrim();
			// this.fileCreated = e.element("fileCreated").getTextTrim();
			this.topic = e.element("topic").getTextTrim();
			this.author = e.element("author").getTextTrim();
		}


		/**
		 *  Gets the title attribute of the AsnDocInfo object
		 *
		 * @return    The title value
		 */
		public String getTitle() {
			return safeVal(this.title);
		}


		/**
		 *  Gets the created attribute of the AsnDocInfo object
		 *
		 * @return    The created value
		 */
		public String getCreated() {
			return safeVal(this.created);
		}


		/**
		 *  Gets the topic attribute of the AsnDocInfo object
		 *
		 * @return    The topic value
		 */
		public String getTopic() {
			return safeVal(this.topic);
		}


		/**
		 *  Gets the author attribute of the AsnDocInfo object
		 *
		 * @return    The author value
		 */
		public String getAuthor() {
			return safeVal(this.author);
		}


		/**
		 *  NOT YET DOCUMENTED
		 *
		 * @param  val  NOT YET DOCUMENTED
		 * @return      NOT YET DOCUMENTED
		 */
		public String safeVal(String val) {
			return (val != null ? val : "");
		}


		/**
		 *  NOT YET DOCUMENTED
		 *
		 * @return    NOT YET DOCUMENTED
		 */
		public Element asElement() {
			Element e = DocumentHelper.createElement("asnInfo");
			e.addAttribute("id", this.id);
			e.addElement("title").setText(this.getTitle());
			e.addElement("created").setText(this.getCreated());
			// e.addElement("fileCreated").setText (this.fileCreated);
			e.addElement("topic").setText(this.getTopic());
			e.addElement("author").setText(this.getAuthor());
			return e;
		}
	}


	/**
	 *  Comparator to sort mappings by subject, jurisdiction, year
	 *
	 * @author    Jonathan Ostwald
	 */
	public class MappingComparator implements Comparator {

		/**
		 *  Compare mappings using the asnInfo component. First compare topic, then
		 *  author and finally year
		 *
		 * @param  o1  InputField 1
		 * @param  o2  InputField 2
		 * @return     comparison of asnInfos
		 */
		public int compare(Object o1, Object o2) {

			AcsrDocInfo acsrInfo1 = ((IdMapping) o1).acsrInfo;
			AcsrDocInfo acsrInfo2 = ((IdMapping) o2).acsrInfo;

			int subjectCmp = acsrInfo1.subject.compareTo(acsrInfo2.subject);
			if (subjectCmp != 0)
				return subjectCmp;

			int jurisdictionCmp = acsrInfo1.jurisdiction.compareTo(acsrInfo2.jurisdiction);
			if (jurisdictionCmp != 0)
				return jurisdictionCmp;

			int localAdoptionDateCmp = acsrInfo1.localAdoptionDate.compareTo(acsrInfo2.localAdoptionDate);
			if (localAdoptionDateCmp != 0)
				return localAdoptionDateCmp;

			return acsrInfo1.title.compareTo(acsrInfo2.title);
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @author    Jonathan Ostwald
	 */
	public class MappingComparatorASN implements Comparator {

		/**
		 *  Compare mappings using the asnInfo component. First compare topic, then
		 *  author and finally year
		 *
		 * @param  o1  InputField 1
		 * @param  o2  InputField 2
		 * @return     comparison of asnInfos
		 */
		public int compare(Object o1, Object o2) {

			AsnDocInfo asnInfo1 = ((IdMapping) o1).asnInfo;
			AsnDocInfo asnInfo2 = ((IdMapping) o2).asnInfo;

			int topicCmp = asnInfo1.topic.compareTo(asnInfo2.topic);
			if (topicCmp != 0)
				return topicCmp;

			int authorCmp = asnInfo1.author.compareTo(asnInfo2.author);
			if (authorCmp != 0)
				return authorCmp;

			int createdCmp = asnInfo1.created.compareTo(asnInfo2.created);
			if (createdCmp != 0)
				return createdCmp;

			return asnInfo1.title.compareTo(asnInfo2.title);
		}
	}


	private static void prtln(String s) {
		if (debug) {
			// SchemEditUtils.prtln(s, "AsnDocument");
			SchemEditUtils.prtln(s, "");
		}
	}

}


