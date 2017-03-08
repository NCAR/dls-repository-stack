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
package edu.ucar.dls.xml.nldr;

import edu.ucar.dls.util.Utils;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.util.strings.FindAndReplace;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XMLUtils;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.util.MetadataUtils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.dom4j.Element;
import org.dom4j.Branch;
import org.dom4j.Attribute;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.Namespace;

import java.util.*;
import java.lang.*;
import java.io.*;
import java.text.*;
import java.net.*;
import java.util.Hashtable;

/**
 *  Specializes NldrMetadataRecordExporter to handle export for the osm
 *  framework. Populates the "urlOfRecord" field with a citableURL, as well as
 *  converting the Asset urls. Also enforces embargoes (expresed in
 *  coverage/dataRange fields).
 *
 *@author    Jonathan Ostwald
 */
public class OsmRecord extends MetadataRecord {
	private static boolean debug = true;
	
	private String path;
	private Document doc;
	
	/**
	 *  Constructor for the AsnDocument object given the path to an ASN XML file.
	 *
	 * @param  file           NOT YET DOCUMENTED
	 * @exception  Exception  Description of the Exception
	 */
	public OsmRecord(String xml) throws Exception {
		super (xml);
		// prtln ("OsmRecord instantiated");
	}
	
	protected String id_path = "/record/general/recordID";

	/**
	 *  Gets the record ID
	 *
	 *@return    The id value
	 */
	public String getId() {
		return this.getTextAtPath(id_path);
	}

	private String url_path = "/record/general/urlOfRecord";

	/**
	 *  Gets the citable URL for this record
	 *
	 *@return    The url value
	 */
	public String getUrl() {
		return this.getTextAtPath(url_path);
	}

	public List getPersonAuthors () {
		return this.selectNodes ("/record/contributors/person[@role='Author']");
	}
	
	public List getOrganizationAuthors () {
		return this.selectNodes ("/record/contributors/organization[@role='Author']");
	}

	public String getTitle () {
		return this.getTextAtPath("/record/general/title");
	}
	
	public String getIssue () {
		return this.getTextAtPath("/record/general/issue");
	}
	
	public String getSize () {
		return this.getTextAtPath("/record/resources/primaryAsset/size");
	}
	
	public List getSizes () {
		return getValuesAtPath("/record/resources/primaryAsset/size");
	}
	
	public List getMimeTypes () {
		return getValuesAtPath("/record/resources/primaryAsset/mimeType");
	}
	
	
	public String getAbstract () {
		return this.getTextAtPath("/record/general/abstract");
	}
		
	public String getDescription () {
		return this.getTextAtPath("/record/general/description");
	}
	
	public String getPublicationYear () {
		try {
			String date = selectSingleNode ("/record/coverage/date[@type='Published']").getText();
			return date.substring(0,4);
		} catch (Throwable t) {
			prtln ("WARN: getPublicationYear did not find a date[published] node");
			return "";
		}
	}
	
	/**
	 *  Sets the urlOfRecord (a citableUrl) for this record
	 *
	 *@param  newValue  The new url value
	 */
	public void setUrl(String newValue) {
		Element general = (Element) selectSingleNode("/record/general");
		if (general.element("urlOfRecord") == null) {
			QName qname = DocumentHelper.createQName("urlOfRecord", general.getNamespace());
			general.elements().add(2, DocumentHelper.createElement(qname));
		}
		this.setTextAtPath(url_path, newValue);
	}
	
	/**
	will have to be more careful than this. should we initialize first and middles?
	*/
	public List getAuthorNames () {
		List authorNames = new ArrayList();
		List authorEls = this.getPersonAuthors();
		prtln (authorEls.size() + " person authors found");
		for (Iterator i=authorEls.iterator();i.hasNext();) {
			Element authorEl = (Element)i.next();
			String firstName = authorEl.element("firstName").getTextTrim();
			String lastName = authorEl.element("lastName").getTextTrim();
			authorNames.add (lastName + ", " + firstName);
		}
		
		List orgEls = this.getOrganizationAuthors();
		prtln (orgEls.size() + " org authors found");
		for (Iterator i=orgEls.iterator();i.hasNext();) {
			Element orgEl = (Element)i.next();
			String instName = null;
			try {
				// instName = orgEl.selectSingleNode("affiliation/instName").getText();
				instName = this.getTextAtPath("affiliation/instName", orgEl);
				prtln ("InstName: " + instName);
			} catch (Throwable t) {
				prtln ("WARNING: could not get instName for " + orgEl.asXML());
			}
			if (instName != null && instName.trim().length()>0 && !authorNames.contains(instName))
				authorNames.add (instName);
		}
		
		return authorNames;
	}
	
	private String doi_path = "/record/classify/idNumber[@type='DOI']";
	
	public String getDOI () {
		Node doiNode = getDOINode();
		if (doiNode != null)
			return doiNode.getText().trim();
		else
			return null;
	}
		
	/**
	* necessary because select methods don't handle attribute value selection:(
	*/
	private Element getDOINode () {
		List candidates = this.selectNodes("/record/classify/idNumber");
		for (Iterator i=candidates.iterator();i.hasNext();) {
			Element candidate = (Element)i.next();
			String type = candidate.attributeValue("type");
			if ("DOI".equals(type))
				return candidate;
		}
		return null;
	}
	
	/**
	*Insert the DOI (without leading "doi:") into this osmRecord
	*/
	public void setDOI (String doi) {

		if (doi == null)
			return;
		if (doi.startsWith("doi:"))
			doi = doi.substring(4,doi.length());
		Element doi_node = getDOINode();
		if (doi_node == null) {
			Element classifyEl = (Element)this.selectSingleNode("/record/classify");
			QName qname = DocumentHelper.createQName("idNumber", classifyEl.getNamespace());
			doi_node = DocumentHelper.createElement(qname);
			
			qname = DocumentHelper.createQName("type", classifyEl.getNamespace());
			doi_node.addAttribute(qname, "DOI");
			
			List classifyEls = classifyEl.elements();
			
			if (classifyEls.isEmpty()) {
				classifyEls.add(doi_node);
			}
			else {
				for (int i=0;i<classifyEls.size();i++) {
					Element child = (Element)classifyEls.get(i);
					String childName = child.getName();
					if (i == (classifyEls.size()-1) ||
						(!childName.equals("language") &&
						!childName.equals("classification"))) 
					{
						classifyEls.add(i, doi_node);
						break;
					}
				}
			}
		}
		doi_node.setText(doi);
	}

	private String valueListToString(List values) {
		String s = "";
		String NL = "\n\t";
		for (Iterator i=values.iterator();i.hasNext();) {
			s += NL + "- " +  (String)i.next();
		}
		return s;
	}
	
	public String toString () {
		String s = "";
		String NL = "\n\t";
		s += this.getTitle();
		s += NL + "identifyer: " + this.getId();
		s += NL + "identifyer: " + this.getId();
		s += NL + "authors: ";
		s += NL + valueListToString(getAuthorNames());	
		s += NL + "publicationYear: " + this.getPublicationYear();
		s += NL + "sizes: " + valueListToString(getSizes());
		s += NL + "mimeTypes: " + valueListToString(getMimeTypes());
		s += NL + "description: " + getDescription();
		s += NL + "abstract: " + getAbstract();
		
		return s;
	}
	
	/**
	 *  The main program for the OsmRecord class
	 *
	 *@param  args           The command line arguments
	 *@exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		// /Users/ostwald/devel/dcs-records/ccs-records/osm/osm/OSM-000-000-000-001.xml
		OsmRecord.setDebug(true);

		// String xmlPath = "C:/Users/ostwald/tmp/TECH-NOTE-000-000-000-853-dds.xml";
		String xmlPath = "C:/Users/ostwald/tmp/TECH-NOTE-000-000-000-534.xml";

		String xml = Files.readFile(xmlPath).toString();
		OsmRecord rec = new OsmRecord(xml);
		
		// pp(rec.getXmlNode());
		
		// prtln (rec.toString());
		rec.setDOI("doi:foo");
		rec.setDOI("fooberry");
		// prtln (rec.getDOI());
		prtln (Dom4jUtils.prettyPrint(rec.getDocument()));

	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 *@param  s  The String that will be output.
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("OsmRecord: " + s);
			System.out.println(s);
		}
	}


	/**
	 *  Sets the debug attribute of the object
	 *
	 *@param  db  The new debug value
	 */
	public static void setDebug(boolean db) {
		debug = db;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 *@author    Jonathan Ostwald
	 */
	class DateRange {
		Element element = null;
		String type;
		String start;
		String end;
		Date never;


		/**
		 *  Constructor for the DateRange object
		 *
		 *@param  element  Description of the Parameter
		 */
		DateRange(Element element) {
			// pp (element);
			this.element = element;
			this.type = element.attributeValue("type", "");
			this.start = element.attributeValue("start", null);
			this.end = element.attributeValue("end", null);
			try {
				this.never = MetadataUtils.parseUnionDateType("5000");
			} catch (Exception e) {
				prtln("never error: " + e.getMessage());
			}
		}


		/**
		 *  Gets the startDate attribute of the DateRange object
		 *
		 *@return    The startDate value
		 */
		Date getStartDate() {
			try {
				return MetadataUtils.parseUnionDateType(this.start);
			} catch (Exception e) {
				prtln("couldn't parse start date: \"" + this.start + "\"");
			}
			return new Date(0);
		}


		/**
		 *  Gets the endDate attribute of the DateRange object
		 *
		 *@return    The endDate value
		 */
		Date getEndDate() {
			try {
				return MetadataUtils.parseUnionDateType(this.end);
			} catch (Exception e) {
				// end date is not required!
				// prtln ("couldn't parse end date: \"" + this.end + "\"");
			}
			return this.never;
		}

	}

}

