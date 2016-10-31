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

package edu.ucar.dls.schemedit.test;

import java.io.*;
import java.util.*;
import java.text.*;
import java.net.URI;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.config.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.util.*;
import edu.ucar.dls.webapps.tools.GeneralServletTools;
import edu.ucar.dls.repository.*;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Attribute;
import org.dom4j.Node;
import org.dom4j.QName;

/**
 *  Tester for {@link edu.ucar.dls.schemedit.config.FrameworkConfigReader} and
 {@link edu.ucar.dls.schemedit.MetaDataFramework}
 *
 *@author    ostwald
 <p>$Id: DefReportTester.java,v 1.4 2011/07/05 22:05:38 ostwald Exp $
 */
public class DefReportTester {
	MetaDataFramework framework = null;
	GlobalDefMap globalDefMap = null;

	/**
	 *  Constructor for the DefReportTester object
	 */
	public DefReportTester(String format) throws Exception {
		FrameworkTester ft = new FrameworkTester (format);
		framework = ft.getFramework();
		globalDefMap = framework.getSchemaHelper().getGlobalDefMap();

	}

	public static void main (String [] args) throws Exception {
		String format = "status_report_simple";
		if (args.length > 0)
			format = args[0];

		String reportFunction = "simpleType";
		if (args.length > 1)
			reportFunction = args[1];

		DefReportTester drt = new DefReportTester (format);
		GlobalDefMap globalDefMap = drt.globalDefMap;

		List defs = new ArrayList ();

		prtln ("\n\nDefReportTester:\n\t format: " + format + "\n\t reportFunction: " + reportFunction + "\n");

		if (reportFunction.equals ("simpleType")) {
			defs = getSimpleTypes (globalDefMap);
		}
		else if (reportFunction.equals ("complexType")) {
			defs = getComplexTypes (globalDefMap);
		}
		else if (reportFunction.equals ("derivedModel")) {
			defs = getDerivedModels (globalDefMap);
		}
		else if (reportFunction.equals ("globalElement")) {
			defs =  getGlobalElements (globalDefMap);
		}
		else if (reportFunction.equals ("globalAttribute")) {
			defs = getGlobalAttributes (globalDefMap);
		}
		else {
			prtln ("unrecognized reportFunction: " + reportFunction);
		}

		if (defs != null) {
			prtln ("reportFunction " + reportFunction + " (" + defs.size() + " found)");
			for (Iterator i=defs.iterator();i.hasNext();) {
				showGlobalDef ((GlobalDef)i.next());
			}
		}
	}

	static List getGlobalElements (GlobalDefMap globalDefMap) {
		List ret = new ArrayList ();
		Iterator i = globalDefMap.getValues().iterator();
		while (i.hasNext()) {
			GlobalDef def = (GlobalDef)i.next();
			if (def.isGlobalElement())
				ret.add (def);
		}
		return ret;
	}

	static List getGlobalAttributes (GlobalDefMap globalDefMap) {
		List ret = new ArrayList ();
		Iterator i = globalDefMap.getValues().iterator();
		while (i.hasNext()) {
			GlobalDef def = (GlobalDef)i.next();
			if (def.isGlobalAttribute())
				ret.add (def);
		}
		return ret;
	}

	static List getSimpleTypes (GlobalDefMap globalDefMap) {
		List ret = new ArrayList ();
		Iterator i = globalDefMap.getValues().iterator();
		while (i.hasNext()) {
			GlobalDef def = (GlobalDef)i.next();
			if (def.isSimpleType())
				ret.add (def);
		}
		return ret;
	}

	static List getComplexTypes (GlobalDefMap globalDefMap) {
		List ret = new ArrayList ();
		Iterator i = globalDefMap.getValues().iterator();
		while (i.hasNext()) {
			GlobalDef def = (GlobalDef)i.next();
			if (def.isComplexType())
				ret.add (def);
		}
		return ret;
	}

	static List getDerivedModels (GlobalDefMap globalDefMap) {
		List ret = new ArrayList ();
		Iterator i = globalDefMap.getValues().iterator();
		while (i.hasNext()) {
			GlobalDef def = (GlobalDef)i.next();
			if (def.isComplexType() && ((ComplexType)def).isDerivedType())
				ret.add (def);
		}
		return ret;
	}


/* 	static List getSimpleTypes (GlobalDefMap globalDefMap) {
		List ret = new ArrayList ();
		Iterator i = globalDefMap.getKeys().iterator();
		while (i.hasNext()) {
			String key = (String)i.next();
			GlobalDef def = (GlobalDef)globalDefMap.getValue (key);
			if (def.isSimpleType())
				ret.add (def);
		}
		return ret;
	}

	static List getComplexTypes (GlobalDefMap globalDefMap) {
		List ret = new ArrayList ();
		Iterator i = globalDefMap.getKeys().iterator();
		while (i.hasNext()) {
			String key = (String)i.next();
			GlobalDef def = (GlobalDef)globalDefMap.getValue (key);
			if (def.isComplexType())
				ret.add (def);
		}
		return ret;
	}

	static List getDerivedModels (GlobalDefMap globalDefMap) {
		List ret = new ArrayList ();
		Iterator i = globalDefMap.getKeys().iterator();
		while (i.hasNext()) {
			String key = (String)i.next();
			GlobalDef def = (GlobalDef)globalDefMap.getValue (key);
			if (def.isComplexType() && ((ComplexType)def).isDerivedType())
				ret.add (def);
		}
		return ret;
	}
 */
	static void showGlobalDef (GlobalDef def) {
		String s = "";
		String [] pathSplits = def.getClass().getName().split("\\.");
		String className = def.getClass().getName();
		if (pathSplits.length > 0)
			className = pathSplits[pathSplits.length -1];
		s += "\nname: " + def.getName() + "  (" + className + ")" ;
		s += "\n\tnamespace: " + def.getNamespace().getURI();
		s += "\n\tlocation: " + def.getLocation();
		prtln (s);
	}






	static void pp (Node node) {
		prtln (Dom4jUtils.prettyPrint(node));
	}

	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	public static void prtln(String s) {
		System.out.println(s);
	}
}
