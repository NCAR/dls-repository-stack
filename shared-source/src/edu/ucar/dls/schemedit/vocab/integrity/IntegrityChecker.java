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

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.schema.SchemaHelper;
import edu.ucar.dls.xml.schema.DefinitionMiner;
import edu.ucar.dls.xml.schema.StructureWalker;
import edu.ucar.dls.xml.schema.SchemaReader;
import edu.ucar.dls.xml.schema.SchemaHelperException;
import edu.ucar.dls.xml.schema.GlobalDefMap;
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
 */
public class IntegrityChecker {

	static boolean debug = true;
	static boolean SCHEMA_DEBUG = false;
	
	Element frameworks = null;
	List checkers;
	
	File muiDir;
	
	public IntegrityChecker (URL frameworksUrl, File muiDir) throws Exception {
		
		prtln ("\nIntegrityChecker");
		prtln ("\t frameworksUrl: " + frameworksUrl);
		prtln ("\t muiDir: " + muiDir);
		
		try {
			Document doc = Dom4jUtils.getXmlDocument (frameworksUrl);
			frameworks = doc.getRootElement();
		} catch (Exception e) {
			throw new Exception ("Unable to parse XML at " + frameworksUrl.toString());
		}
		
		
		this.muiDir = muiDir;
		if (!muiDir.exists()) {
			throw new Exception ("muiDir (" + muiDir.toString() + ") does not exist");
		}
		
		this.checkers = new ArrayList ();
	}

	FieldFilesChecker getFieldFilesChecker (File versionDir, URI schemaUri) throws Exception {
		FieldFilesChecker newChecker = new FieldFilesChecker (versionDir, schemaUri);
		checkers.add (newChecker);
		return newChecker;
	}
	
	void processFrameworks () {
		// prtln ("\nProcessing Frameworks ...");
		// process each of the framework directories
		Iterator frameworksElements = frameworks.elementIterator();
		while (frameworksElements.hasNext()) {
			Element el = (Element) frameworksElements.next();
			String name = el.attributeValue("name", null);
			String dir = el.attributeValue("dir", null);
			String uri = el.attributeValue("uri", null);

			try {
				processFramework(name, dir, uri);
			} catch (Exception e) {
				prtlnErr ("unable to process framework at " + dir + "\n" + e.getMessage() + "\n ... skipping");
			}
		}
	}
	
	void processFramework (String name, String dir, String uri) throws Exception {
		URI schemaUri;
		try {
			schemaUri = new URI (uri);
		} catch (Exception e) {
			throw new Exception ("unable to create schemaURI from " + uri);
		}
		
		File frameworkDir = new File (muiDir, "frameworks/" + dir);
		if (!frameworkDir.exists())
			throw new Exception ("framework dir does not exist at " + dir.toString());
		
		// prtln (Utils.line (20, "+"));
		// prtln ("Processing Framework (" + name + " - " + dir + ") ...");
		prtln ("Processing Framework (" + dir + ") ...");
		
		FieldFilesChecker checker = null;
		try {
			checker = this.getFieldFilesChecker(frameworkDir, schemaUri);
			checker.doCheck();
		} catch (Exception e) {
			
			prtln("ERROR: " + e.getMessage() + "\n");
			
			if (checkers != null) {
				checkers.remove(checker);
			}
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	void report () {
		
		prtln ("\n\n" + Utils.line ("="));
		prtln(Utils.underline("FieldFiles Integrety Checker Report - " + Utils.getTimeStamp()));
		prtln ("Metadata-ui-project: " + this.muiDir.toString());
		prtln ("Reporting on " + checkers.size() + " Fields File Listings");
		prtln ("");
		for (Iterator i=checkers.iterator();i.hasNext();) {
			FieldFilesChecker checker = (FieldFilesChecker)i.next();
			checker.doReport();
		}
	}
	

	/**
	 *  Read a set of fields files
	 *
	 * @param  args           The command line arguments
	 */
 	public static void main(String[] args) {

		prtln ("hello world");
		SchemaHelper.setDebug (SCHEMA_DEBUG);
		DefinitionMiner.setDebug(SCHEMA_DEBUG);
		StructureWalker.setDebug(SCHEMA_DEBUG);
		SchemaReader.setDebug(SCHEMA_DEBUG);
		GlobalDefMap.setDebug(SCHEMA_DEBUG);
	
		boolean verbose = false;
		
		String frameworksPath = "http://www.dlese.org/Metadata/documents/xml/frameworks.xml";
		// String frameworksPath = "http://www.dpc.ucar.edu/people/ostwald/Metadata/frameworks.xml";
		String muiPath = "/devel/ostwald/projects/cvs/metadata-ui-project";
		
		if (args.length > 0)
			muiPath = args[0];
		
		if (args.length == 2)
			verbose = args[1].equals("true");

		URL frameworksUrl = null;
		File muiDir = null;

		FieldFilesChecker.setVerbose (verbose);
		ErrorManager.setVerbose (verbose);
		
		try {
			frameworksUrl = new URL (frameworksPath);
		} catch (Exception e) {
			prtln ("ERROR: could not create URL for " + frameworksPath + "(" + e.getMessage() + ")");
			return;
		}
		
		muiDir = new File (muiPath);
		if (!muiDir.exists()) {
			prtln ("ERROR: mui directory does not exist at " + muiPath);
			return;
		}
		
		IntegrityChecker checker = null;
		
		try {
			checker = new IntegrityChecker (frameworksUrl, muiDir);
			checker.processFrameworks();
			checker.report();
		} catch (Exception e) {
			prtln (e.getMessage());
			return;
		} catch (Throwable t) {
			prtln ("Unknown Error!");
			t.printStackTrace();
		}
	}	

	/**
	 *  Output a line of text to standard out, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private static void prtln(String s) {
		System.out.println(s);
	}
	
	private static void prtlnErr(String s) {
		System.out.println("ERROR: " + s);
	}
	
	class CVSDirectoryFilter implements FileFilter {
		/**
		 *  A FileFilter to accept on directories that aren't named 'CVS'.
		 *
		 * @param  file  The file in question.
		 * @return       True if the file is a Directory and is not named 'CVS'
		 */
		public boolean accept(File file) {
			return (file.isDirectory() && !file.getName().equals("CVS"));
		}
	}
		
	
}



