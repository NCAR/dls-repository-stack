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
package edu.ucar.dls.schemedit.vocab;

import java.io.*;
import java.util.*;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Attribute;

import edu.ucar.dls.xml.*;
import edu.ucar.dls.util.Utils;

/**
 *  Encapsulates the semantics of the TermAndDeftn element of FieldInfo files,
 *  which defines a term/definition relationship for controlled vocabulary items
 *  in a Fields file.
 *
 *@author    ostwald
 *@see       edu.ucar.dls.schemedit.vocab.FieldInfoReader
 */
public class TermAndDeftn {
	private static boolean debug = true;
	private Element element = null;
	private String term = null;
	private String attribution = null;
	private String definition = null;
	private String id = null;


	/**
	 *  Constructor for the TermAndDeftn object
	 *
	 *@param  e  Description of the Parameter
	 */
	public TermAndDeftn(Element e) {
		element = e;
	}


	/**
	 *  Gets the term attribute of the TermAndDeftn object
	 *
	 *@return                The term value
	 *@exception  Exception  Description of the Exception
	 */
	public String getTerm()
			 throws Exception {
		return element.attributeValue("vocab");
	}


	/**
	 *  Gets the attribution attribute of the TermAndDeftn object
	 *
	 *@return                The attribution value
	 *@exception  Exception  Description of the Exception
	 */
	public String getAttribution()
			 throws Exception {
		return element.attributeValue("attribution");
	}


	/**
	 *  Gets the id attribute of the TermAndDeftn object
	 *
	 *@return                The id value
	 *@exception  Exception  Description of the Exception
	 */
	public String getId()
			 throws Exception {
		return element.attributeValue("id");
	}


	/**
	 *  Gets the definition attribute of the TermAndDeftn object
	 *
	 *@return                The definition value
	 *@exception  Exception  Description of the Exception
	 */
	public String getDefinition()
			 throws Exception {
		return element.getText();
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public String toString() {
		ArrayList buf = new ArrayList();
		buf.add("TermAndDeftn:");
		try {
			buf.add("term: " + getTerm());
			buf.add("definition: " + getDefinition());
			buf.add("attribution: " + getAttribution());
		} catch (Exception e) {
			return ("accessor failed: " + e.getMessage());
		}

		String ret = "";
		for (Iterator i = buf.iterator(); i.hasNext(); ) {
			ret += (String) i.next();
			if (i.hasNext()) {
				ret += "\n\t\t";
			}
		}
		return ret;
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 *@param  s  The String that will be output.
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println(s);
		}
	}

}

