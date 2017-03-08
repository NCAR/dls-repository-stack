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

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.Files;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.dom4j.*;

/**
 *  Bean to encapsulate XML representation of standards Documents as returned by
 *  ACSR Web Services.
 *
 * @author    Jonathan Ostwald
 */
public class AcsrDocumentBean {
	private static boolean debug = true;
	Element element = null;
	String acsrId;
	String title;
	String description;
	String subject;
	String jurisdiction;
	String localAdoptionDate;
	String documentStatus;
	String publicationStatus;
	String fileName;

	final static Pattern CDATA_PAT = Pattern.compile("<!\\[CDATA\\[(.*)\\]\\]>");


	/**
	 *  Constructor for the AcsrDocumentBean object
	 *
	 * @param  e  xml representatin of standards document
	 */
	public AcsrDocumentBean(Element e) {
		this.element = e;
		acsrId = getSubElementText("DocumentID");
		title = getSubElementText("DocumentTitle");
		description = getSubElementText("DocumentDescription");
		subject = getSubElementText("DocumentSubject");
		jurisdiction = getSubElementText("DocumentJurisdiction");
		localAdoptionDate = getSubElementText("LocalAdoptionDate");
		documentStatus = getSubElementText("DocumentStatus");
		publicationStatus = getSubElementText("PublicationStatus");
		fileName = getSubElementText("DocumentFileName");
	}


	/**
	 *  Gets the acsrId attribute of the AcsrDocumentBean object
	 *
	 * @return    The acsrId value
	 */
	public String getAcsrId() {
		return this.acsrId;
	}


	/**
	 *  Gets the title attribute of the AcsrDocumentBean object
	 *
	 * @return    The title value
	 */
	public String getTitle() {
		return this.title;
	}


	/**
	 *  Gets the description attribute of the AcsrDocumentBean object
	 *
	 * @return    The description value
	 */
	public String getDescription() {
		return this.description;
	}


	/**
	 *  Gets the subject attribute of the AcsrDocumentBean object
	 *
	 * @return    The subject value
	 */
	public String getSubject() {
		return this.subject;
	}


	/**
	 *  Gets the jurisdiction attribute of the AcsrDocumentBean object
	 *
	 * @return    The jurisdiction value
	 */
	public String getJurisdiction() {
		return this.jurisdiction;
	}


	/**
	 *  Gets the localAdoptionDate attribute of the AcsrDocumentBean object
	 *
	 * @return    The localAdoptionDate value
	 */
	public String getLocalAdoptionDate() {
		return this.localAdoptionDate;
	}


	/**
	 *  Gets the fileName attribute of the AcsrDocumentBean object
	 *
	 * @return    The fileName value
	 */
	public String getFileName() {
		return this.fileName;
	}


	/**
	 *  Gets the status attribute of the AcsrDocumentBean object
	 *
	 * @return    The status value
	 */
	public String getStatus() {
		return this.documentStatus;
	}


	/**
	 *  Creates a normalized version of the fileName attribute of ACSR Documents by
	 *  replacing characters that cause problems for file systems (i.e., ':', '/',
	 *  ',')
	 *
	 * @return    The normalizedFileName value
	 */
	public String getNormalizedFileName() {
		String ret = this.fileName.replaceAll(":", "")
			.replaceAll("/", "-")
			.replaceAll(",", "_");
		return ret;
	}


	/**
	 *  Creates a fileName containing subject, date, jurisdiction and ACSR Filename
	 *
	 * @return    The fullFileName value
	 */
	public String getFullFileName() {
		return this.subject + "-" +
			this.localAdoptionDate + "-" +
			this.jurisdiction + "-" +
			this.getNormalizedFileName() + ".xml";
	}


	/**
	 *  Return a string representation of this ACSR Doc
	 *
	 * @return    string representation
	 */
	public String toString() {
		String NT = "\n\t";
		String s = this.getTitle();
		s += NT + "localAdoptionDate: " + this.getLocalAdoptionDate();
		s += NT + "subject: " + this.getSubject();
		s += NT + "jurisdiction: " + this.getJurisdiction();
		s += NT + "fileName: " + this.getFileName();
		s += NT + "acsrId: " + this.getAcsrId();
		return s;
	}


	/**
	 *  Gets the subElementText attribute of the AsnStatement object
	 *
	 * @param  subElementName  NOT YET DOCUMENTED
	 * @return                 The subElementText value
	 */
	public String getSubElementText(String subElementName) {
		return getSubElementText(this.element, subElementName);
	}


	/**
	 *  Gets the textual content of the named subelement of provided element.
	 *
	 * @param  e               element containing subelement
	 * @param  subElementName  name of subelement
	 * @return                 the textual content of named subelement
	 */
	public String getSubElementText(Element e, String subElementName) {
		Element sub = e.element(subElementName);
		if (sub == null) {
			// prtln("getSubElementText could not find subElement for " + subElementName);
			return "";
		}
		return handleCdata(sub.getTextTrim());
	}


	/**
	 *  if string is of form: <![CDATA[Colorado Academic Standards for Science]]>
	 *  extract and return contents, otherwise, return string as is
	 *
	 * @param  s  input string
	 * @return    string without CDATA wrapper
	 */
	public String handleCdata(String s) {
		Matcher m = CDATA_PAT.matcher(s);
		if (m.matches())
			return m.group(1);
		else
			return s;
	}



	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  n              NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	private static void pp(Node n) throws Exception {
		prtln(Dom4jUtils.prettyPrint(n));
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			// SchemEditUtils.prtln(s, "AsnDocument");
			SchemEditUtils.prtln(s, "");
		}
	}

}


