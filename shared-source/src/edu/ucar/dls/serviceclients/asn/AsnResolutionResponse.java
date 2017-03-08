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
package edu.ucar.dls.serviceclients.asn;

import edu.ucar.dls.standards.asn.*;

import edu.ucar.dls.util.strings.FindAndReplace;
import edu.ucar.dls.schemedit.SchemEditUtils;
import org.dom4j.*;
import edu.ucar.dls.xml.Dom4jUtils;
import java.util.*;
import java.net.*;

/**
 *  Encapsulates response from the ASN Resolution Service (see http://www.thegateway.org/asn/services-description#uri_resolver).
 *
 * @author    Jonathan Ostwald
 */
public class AsnResolutionResponse extends NameSpaceXMLDocReader {

	private static boolean debug = false;
	final static String COLORADO_AUTHOR_PURL = "http://purl.org/ASN/scheme/ASNJurisdiction/CO";
	URL url;
	String id;
	Map stmntMap = null;
	Map parentMap = null;
	AsnStatement target = null;
	AsnDocStatement docStmnt = null;
	List ancestors = null;
	String topic;
	String author;


	/**
	 *  Constructor for the AsnResolutionResponse object
	 *
	 * @param  url            NOT YET DOCUMENTED
	 * @exception  Exception  if document cannot be parsed, or if both target and
	 *      document nodes cannot be found.
	 */
	public AsnResolutionResponse(URL url) throws Exception {
		super(url);
		this.url = url;
		this.id = idFromUrl (this.url);
		parse();
		
	}

	String idFromUrl (URL url) {
		String urlStr = url.toString();
		int x = urlStr.lastIndexOf(".");
		return urlStr.substring(0, x);
	}
	
	/**
	normalize all WMO purlIds
	**/
	public String preprocessXml (String xml) {
		prtln ("preprocessing ..");
		return FindAndReplace.replace (xml.toString(), AsnConstants.ASN_DESIRE2LEARN_BASE, AsnConstants.ASN_ID_BASE, false);
	}
	
	
	/**
	 *  Initialize this response.
	 *
	 * @exception  Exception  if either target or document nodes are not found.
	 */
	private void parse() throws Exception {
		// id = this.url.toString();
		List nodes = this.getNodes("/rdf:RDF/asn:Statement[@rdf:about]");

		// if the target node is a document, then there will be no other entries in the node map
		stmntMap = new HashMap();
		parentMap = new HashMap();

		// // prtln (nodes.size() + " nodes found");
		for (Iterator i = nodes.iterator(); i.hasNext(); ) {
			Element el = (Element) i.next();
			
			AsnStatement asnStmnt = new AsnStatement(el);
			stmntMap.put(asnStmnt.getUid(), asnStmnt);
			if (!asnStmnt.getChildrenIDs().isEmpty()) {
				Iterator childIdIter = asnStmnt.getChildrenIDs().iterator();
				while (childIdIter.hasNext()) {
					String childStmntId = (String)childIdIter.next();
					String childStmntUid = edu.ucar.dls.xml.XPathUtils.getLeaf(childStmntId);
					parentMap.put (childStmntUid, asnStmnt.getUid());
				}
			}
					
		}

		Element el = (Element) this.getNode("/rdf:RDF/asn:StandardDocument");
		if (el == null)
			throw new Exception("doc node not found for " + id);
		
		Element descEl = (Element) this.getNode("/rdf:RDF/rdf:Description");
		if (descEl == null)
			throw new Exception("desc node not found for " + id);
		
		docStmnt = new AsnDocStatement(el, descEl);
		
		String myid = docStmnt.getId();
		// // prtln ("\ndocID: " + myid);
		stmntMap.put(docStmnt.getUid(), docStmnt);

		this.target = this.getStd(id);
		
		if (target == null) {
			showStmntMap();
			throw new Exception("Target node not found for " + id);
		}
		
		// // prtln("target level: " + this.getTargetLevel());
		if (this.isColoradoBenchmark() && this.getTargetLevel() == 2) {
			String parentUid = (String)this.parentMap.get(target.getUid());
			AsnStatement parent = (AsnStatement) this.getStd(parentUid);
			this.target.setDescription(ColoradoStandardDisplay.getDisplayText(this.target, parent));
		}
	}

	void showStmntMap () {
		prtln ("stmntMap (" + this.stmntMap.size() + " nodes)");
		for (Iterator i=this.stmntMap.keySet().iterator();i.hasNext();) {
			String key = (String)i.next();
			AsnStatement st = (AsnStatement)this.stmntMap.get(key);
			prtln (" - " + key + ": " + st.getId());
		}
	}
	
	/**
	 *  Gets the docId attribute of the AsnResolutionResponse object
	 *
	 * @return    The docId value
	 */
	public String getDocId() {
		return this.docStmnt.getId();
	}


	/**
	 *  Gets the std attribute of the AsnResolutionResponse object
	 *
	 * @param  id  NOT YET DOCUMENTED
	 * @return     The std value
	 */
	public AsnStatement getStd(String id) {
		id = edu.ucar.dls.xml.XPathUtils.getLeaf(id);
		return (AsnStatement) this.stmntMap.get(id);
	}


	/**
	 *  Gets the coloradoBenchmark attribute of the AsnResolutionResponse object
	 *
	 * @return    The coloradoBenchmark value
	 */
	public boolean isColoradoBenchmark() {
		// // prtln("isColoradoBenchmark (" + this.getDocStmnt().getJurisdiction() + ")");
		return (COLORADO_AUTHOR_PURL.equals(this.getDocStmnt().getJurisdiction()));
	}


	/**
	 *  Gets the targetLevel attribute of the AsnResolutionResponse object
	 *
	 * @return    The targetLevel value
	 */
	private int getTargetLevel() {
		return this.getAncestors().size() + 1;
	}


	/**
	 *  Gets the target attribute of the AsnResolutionResponse object
	 *
	 * @return    The target value
	 */
	public AsnStatement getTarget() {
		return this.target;
	}

	/**
	 *  Gets the docStmnt attribute of the AsnResolutionResponse object
	 *
	 * @return    The docStmnt value
	 */
	public AsnDocStatement getDocStmnt() {
		return docStmnt;
	}


	/**
	 *  Gets the created attribute of the AsnResolutionResponse object
	 *
	 * @return    The created value
	 */
	public String getCreated() {
		return getDocStmnt().getSubElementText("created");
	}


	/**
	 *  Gets the author attribute of the AsnResolutionResponse object
	 *
	 * @return    The author value
	 */
	public String getAuthor() {
		String purl = getDocStmnt().getJurisdiction();
		return AsnHelper.getInstance().getAuthor(purl);
	}


	/**
	 *  Gets the topic attribute of the AsnResolutionResponse object
	 *
	 * @return    The topic value
	 */
	public String getTopic() {
		String purl = getDocStmnt().getSubject();
		return AsnHelper.getInstance().getTopic(purl);
	}


	/**
	 *  Gets the displayText attribute of the AsnResolutionResponse object
	 *
	 * @return    The displayText value
	 */
	public String getDisplayText() {

		String s = "";
		List aList = getAncestors();
		for (int i = 0; i < aList.size(); i++) {
			AsnStatement std = (AsnStatement) aList.get(i);
			s += std.getDescription();
			s += ": ";
		}
		s += this.getTarget().getDescription();

		s = FindAndReplace.replace(s, "<br>", "\n", true);
		// return removeEntityRefs (s);
		return s;
	}

	private String getParentUid (String id) {
		String uid = edu.ucar.dls.xml.XPathUtils.getLeaf (id);
		return (String)this.parentMap.get(uid);
	}
	
	private AsnStatement getParentStatement (String id) {
		String uid = getParentUid (id);
		if (uid != null)
			return (AsnStatement)this.stmntMap.get(uid);
		else
			return null;
	}
	
	private String getParentUid (AsnStatement child) {
		return getParentUid (child.getUid());
	}

	/**
	 *  Gets the ancestors attribute of the AsnResolutionResponse object
	 *
	 * @return    The ancestors value
	 */
	public List getAncestors() {
		if (this.ancestors == null) {
			this.ancestors = new ArrayList();
			String parentUid = getParentUid (target);
			while (parentUid != null) {
				AsnStatement parent = this.getStd(parentUid);
				if (parent == null)
					break;
				else {
					this.ancestors.add(parent);
					parentUid = getParentUid (target);
				}
			}
		}
		return this.ancestors;
	}


	/**
	 *  The main program for the AsnResolutionResponse class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {

		// String urlStr = "http://purl.org/ASN/resources/S103EC6D";  // Colorado
		// String urlStr = "http://purl.org/ASN/resources/S100D316"; // nses
		// String urlStr = "http://purl.org/ASN/resources/D100027B"; // Doc level

		// String urlStr = "http://purl.org/ASN/resources/S11419E5";

		String urlStr = "http://asn.jesandco.org/resources/S1009A52.xml";


		if (args.length > 0) {
			urlStr = args[0];
			if (!urlStr.startsWith("http"))
				urlStr = "http://purl.org/ASN/resources/" + urlStr;
			urlStr += ".xml";
		}

		prtln("ASN Resolver: " + urlStr);

		AsnResolutionResponse response = new AsnResolutionResponse(new URL(urlStr));

		prtln("author: " + response.getAuthor());
		prtln("topic: " + response.getTopic());

		prtln("\nTarget");
		prtln(response.getTarget().toString());

		prtln("\nAncestors (from target parent upwards)");
		for (Iterator i = response.getAncestors().iterator(); i.hasNext(); ) {
			AsnStatement node = (AsnStatement) i.next();
			prtln(node.toString());
		}

		prtln("\nDocNode:");
		prtln(response.docStmnt.toString());

		prtln("\nDisplayText: " + response.getDisplayText());

		// pp (response.getDocument());
	}


	/**  NOT YET DOCUMENTED */
	public void report() {
		prtln("\nCreated: " + this.getCreated());
		prtln("\nTarget: " + this.getTarget().toString());

		for (Iterator i = this.getAncestors().iterator(); i.hasNext(); ) {
			AsnStatement node = (AsnStatement) i.next();
			// prtln (node.toString());
		}

		prtln("\nDoc node: " + this.getDocStmnt().toString());

		prtln("\n" + this.getDisplayText());
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  node  NOT YET DOCUMENTED
	 */
	private static void pp(Node node) {
		prtln(Dom4jUtils.prettyPrint(node));
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("AsnDocument: " + s);
			System.out.println(s);
		}
	}
}

