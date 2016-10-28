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
package edu.ucar.dls.xml.schema.action;

import java.util.*;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.config.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.schema.compositor.*;

/**
 *  GlobalDefReporter supports reports over selected schema definition types
 *  across the different metadata frameworks. It exposes functions that return
 *  lists of {@link GlobalDef} instances.
 *
 * @author    Jonathan Ostwald
 */
public class GlobalDefReporter {

	/**  NOT YET DOCUMENTED */
	public final static String[] REPORT_FUNCTIONS =
		{
		"globalElements",
		"globalAttributes",
		"simpleTypes",
		"comboUnionTypes",
		"complexTypes",
// 		"derivedModels",
		"textOnlyModels",
		"derivedContentModels",
		"choiceTypes"
		};


	/**
	 *  Gets the globalDefs attribute of the GlobalDefReporter class
	 *
	 * @param  reportFunction  NOT YET DOCUMENTED
	 * @param  globalDefMap    NOT YET DOCUMENTED
	 * @return                 The globalDefs value
	 * @exception  Exception   NOT YET DOCUMENTED
	 */
	public static List getGlobalDefs(String reportFunction, GlobalDefMap globalDefMap) throws Exception {

		if (reportFunction.equals("simpleTypes")) {
			return getSimpleTypes(globalDefMap);
		}
		if (reportFunction.equals("comboUnionTypes")) {
			return getComboUnionTypes(globalDefMap);
		}
		else if (reportFunction.equals("complexTypes")) {
			return getComplexTypes(globalDefMap);
		}
		else if (reportFunction.equals("derivedModels")) {
			return getDerivedModels(globalDefMap);
		}
		else if (reportFunction.equals("globalElements")) {
			return getGlobalElements(globalDefMap);
		}
		else if (reportFunction.equals("globalAttributes")) {
			return getGlobalAttributes(globalDefMap);
		}
		else if (reportFunction.equals("derivedContentModels")) {
			return getDerivedContentModels(globalDefMap);
		}
		else if (reportFunction.equals("textOnlyModels")) {
			return getDerivedTextOnlyModels(globalDefMap);
		}
		else if (reportFunction.equals("choiceTypes")) {
			return getChoiceTypes(globalDefMap);
		}
		else {
			throw new Exception("unrecognized reportFunction: " + reportFunction);
		}
	}


	/**
	 *  Gets the globalElements attribute of the GlobalDefReporter class
	 *
	 * @param  globalDefMap  NOT YET DOCUMENTED
	 * @return               The globalElements value
	 */
	static List getGlobalElements(GlobalDefMap globalDefMap) {
		List ret = new ArrayList();
		Iterator i = globalDefMap.getValues().iterator();
		while (i.hasNext()) {
			GlobalDef def = (GlobalDef) i.next();
			if (def.isGlobalElement())
				ret.add(def);
		}
		return ret;
	}


	/**
	 *  Gets the globalAttributes attribute of the GlobalDefReporter class
	 *
	 * @param  globalDefMap  NOT YET DOCUMENTED
	 * @return               The globalAttributes value
	 */
	static List getGlobalAttributes(GlobalDefMap globalDefMap) {
		List ret = new ArrayList();
		Iterator i = globalDefMap.getValues().iterator();
		while (i.hasNext()) {
			GlobalDef def = (GlobalDef) i.next();
			if (def.isGlobalAttribute())
				ret.add(def);
		}
		return ret;
	}


	/**
	 *  Gets the simpleTypes attribute of the GlobalDefReporter class
	 *
	 * @param  globalDefMap  NOT YET DOCUMENTED
	 * @return               The simpleTypes value
	 */
	public static List getSimpleTypes(GlobalDefMap globalDefMap) {
		List ret = new ArrayList();
		Iterator i = globalDefMap.getValues().iterator();
		while (i.hasNext()) {
			GlobalDef def = (GlobalDef) i.next();
			if (def.isSimpleType())
				ret.add(def);
		}
		return ret;
	}

		/**
	 *  Gets the simpleTypes attribute of the GlobalDefReporter class
	 *
	 * @param  globalDefMap  NOT YET DOCUMENTED
	 * @return               The simpleTypes value
	 */
	public static List getComboUnionTypes(GlobalDefMap globalDefMap) {
		List ret = new ArrayList();
		Iterator i = getSimpleTypes (globalDefMap).iterator();
		while (i.hasNext()) {
			SimpleType def = (SimpleType) i.next();
			if (def.isComboUnionType())
				ret.add(def);
		}
		return ret;
	}

	/**
	 *  Gets the complexTypes attribute of the GlobalDefReporter class
	 *
	 * @param  globalDefMap  NOT YET DOCUMENTED
	 * @return               The complexTypes value
	 */
	public static List getComplexTypes(GlobalDefMap globalDefMap) {
		List ret = new ArrayList();
		Iterator i = globalDefMap.getValues().iterator();
		while (i.hasNext()) {
			GlobalDef def = (GlobalDef) i.next();
			if (def.isComplexType())
				ret.add(def);
		}
		return ret;
	}


	/**
	 *  Gets the choiceTypes attribute of the GlobalDefReporter class
	 *
	 * @param  globalDefMap  NOT YET DOCUMENTED
	 * @return               The choiceTypes value
	 */
	public static List getChoiceTypes(GlobalDefMap globalDefMap) {
		List ret = new ArrayList();
		Iterator i = globalDefMap.getValues().iterator();
		while (i.hasNext()) {
			GlobalDef def = (GlobalDef) i.next();
			if (def.isComplexType()) {
				ComplexType complexType = (ComplexType) def;
				if (complexType.getChoices().size() > 0)
					ret.add(def);
			}
		}
		return ret;
	}


	/**
	 *  Gets the derivedModels attribute of the GlobalDefReporter class
	 *
	 * @param  globalDefMap  NOT YET DOCUMENTED
	 * @return               The derivedModels value
	 */
	public static List getDerivedModels(GlobalDefMap globalDefMap) {
		List ret = new ArrayList();
		Iterator i = globalDefMap.getValues().iterator();
		while (i.hasNext()) {
			GlobalDef def = (GlobalDef) i.next();
			if (def.isComplexType() && ((ComplexType) def).isDerivedType())
				ret.add(def);
		}
		return ret;
	}


	/**
	 *  Gets the derivedContentModels attribute of the GlobalDefReporter class
	 *
	 * @param  globalDefMap  NOT YET DOCUMENTED
	 * @return               The derivedContentModels value
	 */
	public static List getDerivedContentModels(GlobalDefMap globalDefMap) {
		List ret = new ArrayList();
		Iterator i = globalDefMap.getValues().iterator();
		while (i.hasNext()) {
			GlobalDef def = (GlobalDef) i.next();
			if (def.isComplexType() && ((ComplexType) def).isDerivedContentModel())
				ret.add(def);
		}
		return ret;
	}


	/**
	 *  Gets the derivedTextOnlyModels attribute of the GlobalDefReporter class
	 *
	 * @param  globalDefMap  NOT YET DOCUMENTED
	 * @return               The derivedTextOnlyModels value
	 */
	public static List getDerivedTextOnlyModels(GlobalDefMap globalDefMap) {
		List ret = new ArrayList();
		Iterator i = globalDefMap.getValues().iterator();
		while (i.hasNext()) {
			GlobalDef def = (GlobalDef) i.next();
			if (def.isComplexType() && ((ComplexType) def).isDerivedTextOnlyModel())
				ret.add(def);
		}
		return ret;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  def  NOT YET DOCUMENTED
	 */
	static void showGlobalDef(GlobalDef def) {
		String s = "";
		String[] pathSplits = def.getClass().getName().split("\\.");
		String className = def.getClass().getName();
		if (pathSplits.length > 0)
			className = pathSplits[pathSplits.length - 1];
		s += "\nname: " + def.getName() + "  (" + className + ")";
		s += "\n\tnamespace: " + def.getNamespace().getURI();
		s += "\n\tlocation: " + def.getLocation();
		prtln(s);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	public static void prtln(String s) {
		System.out.println(s);
	}
}

