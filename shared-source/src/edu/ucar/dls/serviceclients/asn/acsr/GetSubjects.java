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
package edu.ucar.dls.serviceclients.asn.acsr;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.Files;

import java.io.*;
import java.util.*;
import java.net.*;
import org.dom4j.*;

/**
 *  Use the ACSRService GetSubj command to (http://www.jesandco.net/asn/asnwebservice/acsrservice.asmx)
 *  to obtain ACSR subject values (and subject ids).
 *
 * @author    Jonathan Ostwald
 */
public class GetSubjects extends ACSRClient {
	private static boolean debug = true;

	private Map argMap = null;


	/**  Constructor for the GetSubjects object */
	public GetSubjects() {
		setDebug(debug);
		argMap = new HashMap();
	}


	/**
	 *  Gets the argMap attribute of the GetSubjects object
	 *
	 * @return    The argMap value
	 */
	public Map getArgMap() {
		return this.argMap;
	}


	/**
	 *  Gets the command attribute of the GetSubjects object
	 *
	 * @return    The command value
	 */
	public String getCommand() {
		return "GetSubj";
	}


	/**
	 *  Gets the results attribute of the GetSubjects object
	 *
	 * @return                The results value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public List getResults() throws Exception {
		String path = "//asn_ACSRSubjects/asn_ACSRSubjects";
		Document response = Dom4jUtils.localizeXml(this.getResponse());
		List docElements = response.selectNodes(path);
		prtln(docElements.size() + " nodes found");
		List results = new ArrayList();
		for (Iterator i = docElements.iterator(); i.hasNext(); ) {
			SubjBean result = new SubjBean((Element) i.next());
			results.add(result.subject);
		}
		return results;
	}


	/**
	 *  Gets the detailedResults attribute of the GetSubjects object
	 *
	 * @return                The detailedResults value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public List getDetailedResults() throws Exception {
		String path = "//asn_ACSRSubjects/asn_ACSRSubjects";
		Document response = Dom4jUtils.localizeXml(this.getResponse());
		List docElements = response.selectNodes(path);

		List results = new ArrayList();
		for (Iterator i = docElements.iterator(); i.hasNext(); ) {
			results.add(new SubjBean((Element) i.next()));
		}
		return results;
	}


	/**
	 *  The main program for the GetSubjects class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		GetSubjects client = new GetSubjects();

		// pp (client.getResponse());

		for (Iterator i = client.getResults().iterator(); i.hasNext(); )
			prtln((String) i.next());

		for (Iterator i = client.getDetailedResults().iterator(); i.hasNext(); ) {
			SubjBean subj = (SubjBean) i.next();
			prtln(subj.toString());
		}

	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @author    Jonathan Ostwald
	 */
	public class SubjBean {
		/**  NOT YET DOCUMENTED */
		public String subjectIdentifier;
		/**  NOT YET DOCUMENTED */
		public String subject;


		/**
		 *  Constructor for the SubjBean object
		 *
		 * @param  e  NOT YET DOCUMENTED
		 */
		public SubjBean(Element e) {
			this.subjectIdentifier = e.element("SubjectIdentifier").getTextTrim();
			this.subject = e.element("Subject").getTextTrim();
		}


		/**
		 *  Gets the subject attribute of the SubjBean object
		 *
		 * @return    The subject value
		 */
		public String getSubject() {
			return this.subject;
		}


		/**
		 *  NOT YET DOCUMENTED
		 *
		 * @return    NOT YET DOCUMENTED
		 */
		public String toString() {
			return this.subject + " (" + this.subjectIdentifier + ")";
		}

	}

}


