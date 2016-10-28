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

package edu.ucar.dls.schemedit.vocab.integrity;

import edu.ucar.dls.schemedit.vocab.*;



import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.schema.SchemaHelper;
import edu.ucar.dls.xml.schema.DefinitionMiner;
import edu.ucar.dls.xml.schema.StructureWalker;
import edu.ucar.dls.xml.schema.SchemaReader;
import edu.ucar.dls.xml.schema.SchemaHelperException;
import edu.ucar.dls.xml.schema.SchemaNodeMap;
import edu.ucar.dls.xml.schema.SchemaNode;
import edu.ucar.dls.xml.schema.GlobalDef;
import edu.ucar.dls.xml.schema.GenericType;

import edu.ucar.dls.xml.XPathUtils;

import java.io.*;
import java.util.*;
import java.text.*;
import java.net.*;
import java.lang.*;

import org.dom4j.*;


/**
 *  Command line routine that checks fields files for well-formedness, and ensures that the xpaths associated
 *  with the field files exist within the given metadata framework.
 *
 * @author     ostwald <p>
 *
 *      $Id: Utils.java,v 1.4 2009/03/20 23:33:58 jweather Exp $
 * @version    $Id: Utils.java,v 1.4 2009/03/20 23:33:58 jweather Exp $
 */
public class Utils {
	
	private static boolean debug = true;
	/* private HashMap map = null; */
	
	
	public static SchemaHelper getSchemaHelper (URI schemaUri) throws Exception {
		SchemaHelper schemaHelper = null;
		String scheme = schemaUri.getScheme();
		try {
			if (scheme.equals("file"))
				schemaHelper = new SchemaHelper(new File(schemaUri.getPath()));
			else if (scheme.equals("http"))
				schemaHelper = new SchemaHelper(schemaUri.toURL());
			else
				throw new Exception("ERROR: Unrecognized scheme (" + scheme + ")");

			if (schemaHelper == null)
				throw new Exception();
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().trim().length() > 0)
				throw new Exception("Unable to instantiate SchemaHelper at " + schemaUri + ": " + e.getMessage());
			else
				throw new Exception("Unable to instantiate SchemaHelper at " + schemaUri);
		}
		return schemaHelper;
	}
	

	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	public static void prtln(String s) {
		if (debug)
			System.out.println(s);
	}
	
	public static String getTimeStamp () {
		return new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}
	
	public static int DEFAULT_LINE_WIDTH = 72;
	
	public static String DEFAULT_LINE_CHAR = "-";
	
	public static String NL = "\n";
	
	public static String TAB = "\t";
	
	public static String line () {
		return line (DEFAULT_LINE_WIDTH);
	}
	
	public static String line (String ch) {
		return line (DEFAULT_LINE_WIDTH, ch);
	}
	
	public static String line (int width) {
		return line (width, DEFAULT_LINE_CHAR);
	}
	
	public static String line (int width, String ch) {
		String s = "";
		for (int i=0;i < (width / ch.length()); i++)
			s += ch;
		return s;
	}
	
	public static String underline (String s) {
		String xx = expandTabs (s);
		return xx + "\n" + line(s.length());
	}
	
	public static String overline (String s) {
		return line(s.length()) + "\n" + s;
	}
	
	public static String expandTabs (String s) {
		String xx = "";
		String tab = "....";
		for (int i=0;i<s.length();i++) {
			char ch = s.charAt(i);
			if (ch == '\t')
				xx += tab;
			else 
				xx += String.valueOf(ch);
		}
		return xx;
	}
		
	
	private static int getMaxLen (String [] args) {
		int max = 0;
		for (int i=0;i<args.length;i++)
			max = java.lang.Math.max (max, expandTabs (args[i]).length());
		return max;
	}
	
	public static String pad (String s, int len) {
		String padded = s;
		for (int i=s.length();i<len;i++)
			padded += " ";
		return padded;
	}
	
	public static void main (String [] args) {
		prtln (box ("asdfsadf\n\tthis is much longer\nhava a good day sir!"));
	}
	
	public static String box (String s, String line_ch) {
		// String out = line (s.length() + 4, line_ch) + NL;
		String [] splits = s.split("\n");
		int width = getMaxLen (splits);
		String hr = line (width + 4, line_ch) + NL;
		
		String out = hr;
		for (int i=0;i<splits.length;i++) {
			if (splits[i].length() > 0) 
				out += "| " + pad (expandTabs (splits[i]), width) + " |" + NL;
		}
		/* out += "| " + s + " |" + NL; */
		return out + hr;
	}
	
	public static String box (String s) {
		return box (s, DEFAULT_LINE_CHAR);
	}
		
	public static String comment (String s) {
		return ". . . " + s + " . . .";
	}
}
	
