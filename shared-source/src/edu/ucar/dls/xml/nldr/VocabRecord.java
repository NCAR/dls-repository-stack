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
public class VocabRecord extends MetadataRecord {
	private static boolean debug = true;
	
	private String path;
	private Document doc;
	
	/**
	 *  Constructor for the AsnDocument object given the path to an ASN XML file.
	 *
	 * @param  file           NOT YET DOCUMENTED
	 * @exception  Exception  Description of the Exception
	 */
	public VocabRecord(String xml) throws Exception {
		super (xml);
		// prtln ("VocabRecord instantiated");
	}
	
	protected String id_path = "/vocabTerm/recordID";

	/**
	 *  Gets the record ID
	 *
	 *@return    The id value
	 */
	public String getId() {
		return this.getTextAtPath(id_path);
	}

	public void setId (String newValue) {
		this.setTextAtPath(id_path, newValue);
	}
		
	private String recordsAffected_path = "/vocabTerm/recordsAffected";
	
	public String getRecordsAffected() {
		return this.getTextAtPath(id_path);
	}

	public void setRecordsAffected (int newValue) {
		String valueStr = "0";
		try {
			valueStr = Integer.toString(newValue);
		} catch (Exception e) {}
		
		this.setTextAtPath(recordsAffected_path, valueStr);
	}
	
	private String fullName_path = "/vocabTerm/fullName";
	
	public String getFullName() {
		return this.getTextAtPath(fullName_path);
	}

	public void setFullName (String newValue) {
		this.setTextAtPath(fullName_path, newValue);
	}
	
	public String getTerm() {
		return this.getFullName();
	}
	
	private String valueListToString(List values) {
		String s = "";
		String NL = "\n\t";
		for (Iterator i=values.iterator();i.hasNext();) {
			s += NL + "- " +  (String)i.next();
		}
		return s;
	}
	
	
	/**
	 *  The main program for the VocabRecord class
	 *
	 *@param  args           The command line arguments
	 *@exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		// /Users/ostwald/devel/dcs-records/ccs-records/osm/osm/OSM-000-000-000-001.xml
		VocabRecord.setDebug(true);

		// String xmlPath = "C:/Users/ostwald/tmp/TECH-NOTE-000-000-000-853-dds.xml";
		String xmlPath = "C:/Users/ostwald/tmp/TECH-NOTE-000-000-000-534.xml";

		String xml = Files.readFile(xmlPath).toString();
		VocabRecord rec = new VocabRecord(xml);
		
		// pp(rec.getXmlNode());
		
		prtln (rec.toString());

	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 *@param  s  The String that will be output.
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("VocabRecord: " + s);
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


}

