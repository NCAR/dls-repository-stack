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
package edu.ucar.dls.standards.asn;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.standards.*;
import org.dom4j.*;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.schema.SchemaHelper;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.util.strings.FindAndReplace;
import java.util.*;
import java.io.File;
import java.net.URL;

/**
 *  Encapsulates an ASN Standards Document by reading the source XML file and
 *  creating a hierarchy of AsnStandard instances. Also provides lookup for
 *  AsnStandards by their id.
 *
 * @author    ostwald
 */
public class AsnDocument {
	private static boolean debug = false;

	private AsnHelper asnHelper = null;
	private Document rawDoc = null;
	private Map asnIdMap = null;
	private Map statementIdMap = null;
	private Map parentIdMap = null; // map from nodeId to parentId
	private Map otherReferencesMap = null;
	private String path;
	private String identifier = null;
	private String uid = null;
	private String title = null;
	private String fileCreated = null;
	private String created = null;
	private String author = null;
	private String authorName = null;
	private String topic = null;
	private String description = null;
	private String version = null;
	private String authorPurl = null;
	private String topicPurl = null;


	/**
	 *  Constructor for the AsnDocument object given the path to an ASN XML file.
	 *
	 * @param  file           NOT YET DOCUMENTED
	 * @exception  Exception  Description of the Exception
	 */
	public AsnDocument(File file) throws Exception {

		this.asnHelper = AsnHelper.getInstance();
		this.path = file.getCanonicalPath();
		try {
			StringBuffer xml = Files.readFileToEncoding(new File(path), "UTF-8");
			
			
			// String normalizedXml = FindAndReplace.replace (xml.toString(), AsnConstants.ASN_DESIRE2LEARN_BASE, AsnConstants.ASN_ID_BASE, false); 
			// rawDoc = Dom4jUtils.getXmlDocument(normalizedXml);
			
			rawDoc = Dom4jUtils.getXmlDocument(xml.toString());
			init();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new Exception("init error: " + e.getMessage());
		}
		// prtln ("AsnDocument instantiated");
	}


	/**
	 *  Constructor taking an xml docuement and "Path" (a unique id that can be
	 *  used to identify the document)
	 *
	 * @param  xmlDoc         NOT YET DOCUMENTED
	 * @param  path           NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public AsnDocument(Document xmlDoc, String path) throws Exception {

		this.asnHelper = AsnHelper.getInstance();
		this.path = path;
		this.rawDoc = xmlDoc;

		try {
			init();
		} catch (Throwable e) {
			// e.printStackTrace();
			throw new Exception("init error: " + e.getMessage());
		}
		// prtln ("AsnDocument instantiated");
	}

	public Document getDocument() {
		return this.rawDoc;
	}

	/**
	 *  Gets the path attribute of the AsnDocument object
	 *
	 * @return    The path value
	 */
	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 *  Gets the fileCreated attribute of the AsnDocument object
	 *
	 * @return    The fileCreated value
	 */
	public String getFileCreated() {
		return this.fileCreated;
	}


	/**
	 *  Gets the created attribute of the AsnDocument object
	 *
	 * @return    The created value
	 */
	public String getCreated() {
		return this.created;
	}


	/**
	 *  Gets the identifier attribute of the AsnDocument object (the full ASN Purl
	 *  id)
	 *
	 * @return    The identifier value (e.g., "http://purl.org/ASN/resources/S1015D9B")
	 */
	public String getIdentifier() {
		return this.identifier;
	}


	/**
	 *  Gets the unique part of the ASN purl id
	 *
	 * @return    The uid value (e.g., "S1015D9B")
	 */
	public String getUid() {
		if (this.uid == null) {
			try {
				this.uid = new File(this.identifier).getName();
			} catch (Throwable t) {
				prtln("get uid error (" + t.getMessage() + ") - will use full purl id for uid");
				this.uid = identifier;
			}
		}
		return this.uid;
	}


	/**
	 *  Gets the title attribute of the AsnDocument object
	 *
	 * @return    The title value
	 */
	public String getTitle() {
		return this.title;
	}


	/**
	 *  Gets the description attribute of the AsnDocument object
	 *
	 * @return    The description value
	 */
	public String getDescription() {
		return this.description;
	}


	/**
	 *  Gets the version attribute of the AsnDocument object
	 *
	 * @return    The version value
	 */
	public String getVersion() {
		return this.version;
	}


	/**
	 *  Gets the authorPurl attribute of the AsnDocument object
	 *
	 * @return    The authorPurl value
	 */
	public String getAuthorPurl() {
		return this.authorPurl;
	}


	/**
	 *  Gets the author attribute of the AsnDocument object
	 *
	 * @return    The author value
	 */
	public String getAuthor() {
		return this.author;
	}


	/**
	 *  Gets the author attribute of the AsnDocument object
	 *
	 * @param  authors  Description of the Parameter
	 * @return          The author value
	 */
	public String getAuthor(AsnAuthors authors) {
		if (this.author == null && authors != null) {
			this.author = authors.getAuthor(this.authorPurl);
		}
		return this.author;
	}

	public String getAuthorName () {
		return this.authorName;
	}

	/**
	 *  Gets the topicPurl attribute of the AsnDocument object
	 *
	 * @return    The topicPurl value
	 */
	public String getTopicPurl() {
		return this.topicPurl;
	}


	/**
	 *  Gets the topic attribute of the AsnDocument object
	 *
	 * @return    The topic value
	 */
	public String getTopic() {
		return this.topic;
	}


	/**
	 *  Gets the topic attribute of the AsnDocument object
	 *
	 * @param  topics  Description of the Parameter
	 * @return         The topic value
	 */
	public String getTopic(AsnTopics topics) {
		if (this.topic == null && topics != null) {
			this.topic = topics.getTopic(this.topicPurl);
		}
		return this.topic;
	}


	/**
	 *  Gets the rootStandard attribute of the AsnDocument object
	 *
	 * @return    The rootStandard value
	 */
	public AsnStandard getRootStandard() {
		// why aren't we returning a RootStdNode?
		return (AsnStandard) asnIdMap.get(this.getIdentifier());
	}


	/**
	 *  Gets the AsnStandard having provicded id
	 *
	 * @param  id  Description of the Parameter
	 * @return     The standard value
	 */
	public AsnStandard getStandard(String id) {
		return (AsnStandard) asnIdMap.get(id);
	}
	
	public AsnStandard getStandardForStatmentId (String statementId) {
		return (AsnStandard) statementIdMap.get(statementId);
	}


	/**
	 *  Gets all standards contained in this AsnDocument
	 *
	 * @return    The standards value
	 */
	public Collection getStandards() {
		return asnIdMap.values();
	}


	/**
	 *  Gets the standards at the specified level of the standards hierarchy of the
	 *  AsnDocument object
	 *
	 * @param  level  Description of the Parameter
	 * @return        The standardsAtLevel value
	 */
	public List getStandardsAtLevel(int level) {
		List ret = new ArrayList();
		if (getStandards() != null) {
			for (Iterator i = getStandards().iterator(); i.hasNext(); ) {
				AsnStandard std = (AsnStandard) i.next();
				if (std.getLevel() == level) {
					ret.add(std);
				}
			}
		}
		return ret;
	}


	/* purls for identifying standards by specific "authors" */
	static String COLORADO_PURL_AUTHOR = AsnConstants.ASN_JURISDICTION_BASE + "CO";
	static String AAAS_PURL_AUTHOR = AsnConstants.ASN_JURISDICTION_BASE + "AAAS";
	static String COMMON_CORE_PURL_AUTHOR = AsnConstants.ASN_JURISDICTION_BASE + "CCSS";


	/**
	 *  Returns true if the author of this AsnDocument is Colorado
	 *
	 * @return    The coloradoBenchmark value
	 */
	public boolean authorIsColorado() {
		return (COLORADO_PURL_AUTHOR.equals(this.getAuthorPurl()));
	}

	public boolean authorIsCommonCore() {
		return (COMMON_CORE_PURL_AUTHOR.equals(this.getAuthorPurl()));
	}

	/**
	 *  Returns true if
	 *
	 * @return    The aAASBenchmark value
	 */
	public boolean authorIsAAAS() {
		return (AAAS_PURL_AUTHOR.equals(this.getAuthorPurl()));
	}


	/**
	 *  Factory to creates an AsnStandard instance based on the AsnDocuments
	 *  authorPurl attribute
	 *
	 * @param  e  statement element from the XML document
	 * @return    AsnStandard instance created from statement element
	 */
	private AsnStandard makeAsnStandard(Element e) {
		AsnStatement stmnt = new AsnStatement(e);
		AsnStandard std = null;
		if (authorIsColorado()) {
			std = new ColoradoBenchmark(stmnt, this);
		}
		else if (authorIsAAAS()) {
			std = new AAASBenchmark(stmnt, this);
		}
 		else if (authorIsCommonCore()) {
			std = new CommonCoreStandard(stmnt, this);
		}
		else {
			std = new AsnStandard(stmnt, this);
		}
		return std;
	}


	/**
	 *  Gets the list of asn IDs defined by the AsnDocument object
	 *
	 * @return    The identifiers value
	 */
	public Set getIdentifiers() {
		return asnIdMap.keySet();
	}

	public String getParentId (String stdId) {
		return (String)this.parentIdMap.get(stdId);
	}

	/**
	 *  Read XML document and initialize attributes and data structures for the AsnDocument object
	 *
	 * @param  rawDoc         NOT YET DOCUMENTED
	 * @exception  Exception  Description of the Exception
	 */
	private void init() throws Exception {
		asnIdMap = new HashMap();
		statementIdMap = new HashMap();
		parentIdMap = new HashMap();
		otherReferencesMap = new HashMap();
		
		NameSpaceXMLDocReader doc = null;
		try {
			doc = new NameSpaceXMLDocReader(this.rawDoc);
		} catch (Exception e) {
			throw new Exception("Couldn't read standards doc: " + e.getMessage());
		}

		Element stdDocElement = (Element) doc.getNode("/rdf:RDF/asn:StandardDocument");
		if (stdDocElement == null) {
			throw new Exception("StandardDocument element not found");
		}
		
		Element stdDescElement = (Element) doc.getNode("/rdf:RDF/rdf:Description");
		if (stdDescElement == null) {
			throw new Exception("StandardDescription element not found");
		}

		AsnDocStatement stdDocStmnt = new AsnDocStatement(stdDocElement, stdDescElement);
		
		this.identifier = stdDocStmnt.getId();
		this.fileCreated = stdDocStmnt.getFileCreated();
		this.created = stdDocStmnt.getCreated();
		this.title = stdDocStmnt.getTitle();
		this.description = stdDocStmnt.getDescription();
		this.authorPurl = stdDocStmnt.getJurisdiction();
		this.author = asnHelper.getAuthor(this.authorPurl);
		this.authorName = asnHelper.getAuthorName(this.authorPurl);
		this.topicPurl = stdDocStmnt.getSubject();
		prtln ("topicPurl: " + this.topicPurl);
		if (this.topicPurl == null || this.topicPurl.trim().length() == 0)
			prtln (Dom4jUtils.prettyPrint (stdDocStmnt.getElement()));	
		this.topic = asnHelper.getTopic(this.topicPurl);

		String versionPurl = stdDocStmnt.getExportVersion();
		try {
			this.version = versionPurl.substring("http://purl.org/ASN/export/".length());
		} catch (Throwable t) {
			prtln("WARNING: could not extract version number (" + t.getMessage() + ") - using purl instead");
			this.version = versionPurl;
		}

		RootAsnStandard root = new RootAsnStandard(stdDocStmnt, this);
		asnIdMap.put(root.getId(), root);
		if (root == null)
			throw new Exception ("RootAsnStandard not found");
		statementIdMap.put(root.getId(), root);

		// Create AsnStandard objects for each standard Statement in this document
		// comprisedOfMap - stores linkages from the comprisedOf stds back to the std of which it is comprised
		Map comprisedOfMap = new HashMap();
		otherReferencesMap.put("ComprisedOf", comprisedOfMap);
		
		List asnStmntNodes = doc.getNodes("/rdf:RDF/asn:Statement");
		prtln (asnStmntNodes.size() + " items found");
		for (Iterator i = asnStmntNodes.iterator(); i.hasNext(); ) {
			Element e = (Element) i.next();
			AsnStandard std = this.makeAsnStandard(e);
			asnIdMap.put(std.getId(), std);
			// prtln (" - " + std.getId());
			statementIdMap.put(std.getId(), std);
			
			// populate a the otherReferencesMap -> "ComprizedOf"
			
			
			Iterator cIter = std.getComprisedOf().iterator();
			while (cIter.hasNext()) {
				String comprisedOf = (String)cIter.next();
				List values = (List)comprisedOfMap.get (comprisedOf);
				if (values == null) {
					values = new ArrayList();
				}
				values.add (std.getId());
				comprisedOfMap.put (comprisedOf, values);
			}
			
		}

		// Now go through the nodes and assign parentIds
		
		for (Iterator i = asnIdMap.values().iterator();i.hasNext();) {
			AsnStandard parent = (AsnStandard)i.next();
			String parentId = parent.getId();
			List children = parent.getChildren();
			if (children != null) {
			
				Iterator childIter = children.iterator();
				while (childIter.hasNext()) {
					// String childId = (String)childIter.next();
					AsnStandard child = (AsnStandard)childIter.next();
					String childId = child.getId();
					// prtln ("child: " + childId + "  parent: " + parentId);
					parentIdMap.put(childId, parentId);
				}
			}
		}
		
		// clean up
		doc.destroy();
		System.runFinalization();
		System.gc();
	}

	/**
	* the otherReferencesMap stores inverse relationships between standards. For example,
	* in the NGSS standards can specify a list of "comprisedOf" resources. the otherReferencesMap
	* would store relationsips from the comprisedOf resource to the std of which it was comprised.
	* This information is stored as a map named "ComprisedOf" in the otherReferencesMap
	*/
	public Map getOtherReferencesMap () {
		return this.otherReferencesMap;
	}

	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public String toString() {
		String s = "\ntitle: " + this.getTitle();
		s += "\n\t" + "identifier: " + this.getIdentifier();
		s += "\n\t" + "fileCreated: " + this.getFileCreated();
		s += "\n\t" + "version: " + this.getVersion();
		s += "\n\t" + "description: " + this.getDescription();
		s += "\n\t" + "author: " + this.getAuthor();
		s += "\n\t" + "topic: " + this.getTopic();
		return s;
	}


	/**
	 *  The main program for the AsnDocument class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  Description of the Exception
	 */
	public static void main(String[] args) throws Exception {

		//get doc from FILE
 		String path = "/Users/ostwald/tmp/D10003FB.xml";
		AsnDocument asnDoc = new AsnDocument(new File(path));
		
/*  		// String url = "http://asn.jesandco.org/resources/D1000152_full.xml";  // AAAS
		String url = "http://asn.jesandco.org/resources/D2454348_full.xml";
		Document doc = Dom4jUtils.getXmlDocument(new URL(url));
		AsnDocument asnDoc = new AsnDocument(doc, null); */

		prtln("author: " + asnDoc.getAuthor());
		prtln("topic: " + asnDoc.getTopic());
		prtln("created: " + asnDoc.getCreated());
		prtln("version: " + asnDoc.getVersion());
		prtln("identity: " + asnDoc.getIdentifier());
		
		prtln ("------");
		

		/* 		prtln ("ASN DocStatement");
		prtln (asnDoc.getRootStandard().getAsnStatement().toString()); */

		// test a particular std
		String asnId = "http://asn.jesandco.org/resources/S11434CE"; 
		prtln ("std: " + asnId);
		AsnStandard std = asnDoc.getStandard(asnId);
		if (std == null)
			prtln("no standard found for " + asnId);
		else {
			prtln(std.toString());
			// prtln ("parent: " + std.getParentId());
		}
		
		Map comprisedRelationMap = (Map)asnDoc.getOtherReferencesMap().get("ComprisedOf");
		prtln ("comprisedRelationMap has " + comprisedRelationMap.size() + " entries");
		List comprisedOfRelations = (List)comprisedRelationMap.get(asnId);
		
		String s = "";
		if (comprisedOfRelations != null && !comprisedOfRelations.isEmpty()) {
			s += "\n\tcomprisedOfRelations";
			s += listToString(comprisedOfRelations, "\t - ");
		}
		else
			s += "\n\tno comprisedOfRelationsMap found for " + asnId;
		prtln (s);
		
		// show the otherReferencesMap 
/* 		prtln ("otherReferencesMap");
 		for (Iterator i=asnDoc.otherReferencesMap.keySet().iterator();i.hasNext();) {
 			String key = (String)i.next();
			prtln (" - " + key);
			Map subMap = (Map)asnDoc.otherReferencesMap.get(key);
			prtln (key + " has " + subMap.size() + " entries");
		} */
		
		// asnDoc.showPerformanceLabels();
	}

	private static String listToString (List<String> list, String prefix) {
		prtln ("listToString (" + list.size() + ")");
		String s = "";
		for (String val:list)
			s += "\n" + prefix + val;
		return s;
	}
	
	void showPerformanceLabels () {
		List labels=new ArrayList();
		for (Iterator i=getStandards().iterator();i.hasNext();) {
			AsnStandard std = (AsnStandard)i.next();
			// prtln (std.getId());
			String label = std.getStatementLabel();
			if (!labels.contains(label)) {
				prtln (" - " + label);
				labels.add (label);
			}
				
		}
	}

	/**
	 *  Gets the standardTest attribute of the AsnDocument class
	 *
	 * @param  asnDoc  Description of the Parameter
	 * @param  asnId   Description of the Parameter
	 */
	private static void getStandardTest(AsnDocument asnDoc, String asnId) {
		AsnStandard std = asnDoc.getStandard(asnId);
		List children = std.getChildren();
		prtln("children (" + children.size() + ")");
		for (Iterator i = children.iterator(); i.hasNext(); ) {
			AsnStandard childStd = (AsnStandard) i.next();
			if (childStd == null) {
				prtln("NULL");
			}
			else {
				prtln(childStd.getId());
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			// SchemEditUtils.prtln(s, "AsnDocument");
			SchemEditUtils.prtln(s, "");
		}
	}
}

