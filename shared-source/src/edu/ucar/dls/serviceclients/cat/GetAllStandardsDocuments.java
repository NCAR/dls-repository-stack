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

package edu.ucar.dls.serviceclients.cat;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.TimedURLConnection;
import edu.ucar.dls.util.URLConnectionTimedOutException;
import org.dom4j.*;
import java.util.*;
import java.net.*;
import java.io.File;

/**
 *  See
 *
 * @author    Jonathan Ostwald
 */
public class GetAllStandardsDocuments extends CATWebService {

	private static boolean debug = true;
	private final static String METHOD = "getAllStandardDocuments";

	/**  Constructor for the GetAllStandardsDocuments object */
 	public GetAllStandardsDocuments(File propsFile) {
		super (propsFile);
	}

	/**
	 *  Constructor for the GetAllStandardsDocuments object
	 *
	 * @param  username  NOT YET DOCUMENTED
	 // * @param  password  NOT YET DOCUMENTED
	 */
 	public GetAllStandardsDocuments(String username, String password, String baseUrl) {
		super(username, password, baseUrl);
	}

	protected String getMethod () {
		return METHOD;
	}
	
	/**
	 *  Gets the suggestions attribute of the GetAllStandardsDocuments object
	 *
	 * @param  resourceUrl    NOT YET DOCUMENTED
	 * @param  constraints    NOT YET DOCUMENTED
	 * @return                The suggestions value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public List getAllDocs() throws Exception {
		
		Map args = new HashMap();
		args.put("username", this.username);
		args.put("password", this.password);
		args.put("method", this.getMethod ());
		
		Document response = null;
		try {
			response = this.getResponse(args);
		} catch (Throwable t) {
			throw new Exception("webservice error: " + t);
		}

		// DEBUGGING - show response
		/* pp (response.selectSingleNode("//RequestInfo")); */
		List results = new ArrayList();
		List resultNodes = response.selectNodes("/CATWebService/StandardDocuments/StandardDocument");
		for (Iterator i = resultNodes.iterator(); i.hasNext(); ) {
			Element e = (Element) i.next();
			results.add(new CATStandardDocument(e));
		}
		return results;
	}

	/**
	 *  Gets the suggestions attribute of the GetAllStandardsDocuments object
	 *
	 * @param  resourceUrl    NOT YET DOCUMENTED
	 * @param  constraints    NOT YET DOCUMENTED
	 * @return                The suggestions value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public Map getAllDocsMap() throws Exception {
		
		Map args = new HashMap();
		args.put("username", this.username);
		args.put("password", this.password);
		args.put("method", this.getMethod ());
		
		Document response = null;
		try {
			response = this.getResponse(args);
		} catch (Throwable t) {
			throw new Exception("webservice error: " + t);
		}

		// DEBUGGING - show response
		/* pp (response.selectSingleNode("//RequestInfo")); */
		Map results = new HashMap();
		List resultNodes = response.selectNodes("/CATWebService/StandardDocuments/StandardDocument");
		for (Iterator i = resultNodes.iterator(); i.hasNext(); ) {
			Element e = (Element) i.next();
			CATStandardDocument stdDoc = new CATStandardDocument(e);
			results.put(stdDoc.getId(), stdDoc);
		}
		return results;
	}
	
	/**
	 *  Gets the resultIds attribute of the CATWebService class
	 *
	 * @param  results  NOT YET DOCUMENTED
	 * @return          The resultIds value
	 */
	static List getResultIds(List results) {
		prtln(results.size() + " suggestions returned");
		List ids = new ArrayList();
		for (Iterator i = results.iterator(); i.hasNext(); ) {
			CATStandardDocument stdDoc = (CATStandardDocument) i.next();
			String id = stdDoc.getId();
			ids.add(id);
			prtln("\t" + id);
		}
		return ids;
	}


	/**
	 *  Debugging Utility
	 *
	 * @param  results  NOT YET DOCUMENTED
	 */
	static void showTopics(List results) {
		List topics = new ArrayList();
		for (Iterator i = results.iterator(); i.hasNext(); ) {
			CATStandardDocument result = (CATStandardDocument) i.next();
			String topic = result.getTopic();
			if (!topics.contains(topic))
				topics.add(topic);
		}
		prtln("Topics");
		for (Iterator i = topics.iterator(); i.hasNext(); )
			prtln("\t" + (String) i.next());
	}

	static void showResults(List results) {
		prtln ("\nAll Standards Documents (" + results.size() + ")");
		for (Iterator i = results.iterator(); i.hasNext(); ) {
			CATStandardDocument result = (CATStandardDocument) i.next();
			prtln ("\n" + result.toString());
		}
	}
	
	static void showResults(List results, String author) {
		prtln ("\nAll Standards Documents (" + results.size() + ")");
		for (Iterator i = results.iterator(); i.hasNext(); ) {
			CATStandardDocument result = (CATStandardDocument) i.next();
			if (result.getAuthor().equals(author))
				prtln ("\n" + result.toString());
		}
	}
	
	String join (List list, String delimiter) {
		if (list == null)
			return "";
		// String delimiter = "|";
		String joined = "";
		for (Iterator i=list.iterator();i.hasNext();) {
			joined += (String)i.next();
			if (i.hasNext())
				joined += delimiter;
		}
		return joined;
	}
	
	String toTabDelimited (List results) {
		List lines = new ArrayList();
		List header = new ArrayList();
		header.add ("topic");
		header.add ("author");
		header.add ("year");
		header.add ("title");
		header.add ("id");
		lines.add (join (header, "\t"));
		
		for (Iterator i = results.iterator(); i.hasNext(); ) {
			List data = new ArrayList();
			CATStandardDocument result = (CATStandardDocument) i.next();
			data.add (result.getTopic());
			data.add (result.getAuthor());
			data.add (result.getCreated());
			data.add (result.getTitle());
			data.add (result.getId());
			lines.add (join (data, "\t"));
		}		
		
		return join (lines, "\n");
	}
	
		
	
	
	/**
	 *  The main program for the GetAllStandardsDocuments class
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(String[] args) {

 		// GetAllStandardsDocuments client = new GetAllStandardsDocuments("dlese", "p");
		File propsFile = new File ("C:/mykeys/cat_service.properties");
 		GetAllStandardsDocuments client = new GetAllStandardsDocuments(propsFile);

		List results = null;
		try {
			results = client.getAllDocs();
		} catch (Throwable t) {
			prtln("ERROR: " + t.getMessage());
			System.exit(1);
		}
		prtln(results.size() + " results found");
		// showResults(results, "American Association for the Advancement of Science");
		showResults(results, "National Science Education Standards");
		showResults(results, "Colorado");
		// showResults(results);
		// prtln (client.toTabDelimited(results));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		String prefix = null;
		if (debug) {
			SchemEditUtils.prtln(s, prefix);
		}
	}

}

