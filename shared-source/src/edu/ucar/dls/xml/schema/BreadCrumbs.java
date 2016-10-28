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

package edu.ucar.dls.xml.schema;

import java.util.*;

public class BreadCrumbs {

	static boolean debug = true;
	List list = new ArrayList();
	
	String url = "schema.do?command=doPath";
	
	public BreadCrumbs (String xpath) {
		String [] segments = xpath.split("/");
		String currentPath = "";
		for (int i=0;i<segments.length;i++) {
			String segment = segments[i];
			if (segment.trim().length() == 0) continue;
			currentPath += "/" + segment;
			Crumb crumb = new Crumb (segment, currentPath);
			list.add (crumb);
		}
	}

	public String toString () {
		StringBuffer buffer = new StringBuffer ();
		for (Iterator i=list.iterator();i.hasNext();) {
			Crumb crumb = (Crumb)i.next();
			// buffer.append ("\n" + makeLink(crumb));
			if (i.hasNext())
				buffer.append("/" + makeLink(crumb));
			else
				// buffer.append ("/" + crumb.label);
				buffer.append("/" + makeLink(crumb));
		}
		return buffer.toString();
	}
	
	public String makeLink (Crumb c) {
		String href = url + "&path=" + c.path;
		return "<a href=\"" + href + "\">" + c.label + "</a>";
	}
	
	public static void main (String [] args) {
		String xpath = "/itemRecord/lifecycle/foo";
		BreadCrumbs bc = new BreadCrumbs (xpath);
		prtln (bc.toString());
	}
	
	private static void prtln(String s) {
		if (debug) {
			System.out.println(s);
		}
	}
	
	public class Crumb  {
		String label;
		String path;
		
		public Crumb (String label, String path) {
			this.label = label;
			this.path = path;
		}
		
		public String toString () {
			return label + " (" + path + ")";
		}
	}
}
