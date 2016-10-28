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
import edu.ucar.dls.xml.schema.compositor.*;

import edu.ucar.dls.util.*;

import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.Namespace;

import java.util.*;
import java.io.*;

/**
 *  Wrapper for ModelGroup definitions in XML Schemas.
 *
 * @author     ostwald
 * @version    $Id: ModelGroup.java,v 1.3 2009/03/20 23:34:01 jweather Exp $
 */
public class ModelGroup extends ComplexType {

	private static boolean debug = true;

	// modelGroup can be all, choice, simpleContent, sequence
	// public String modelGroup = null;

	private Compositor compositor = null;

	// contentModel holds the "name" of the first element in the ModelGroup definition
	// it can be a compositor, a derivation (simpleContent, complexContent),
	private String contentModel = "unknown";


	/**
	 *  Constructor for the ModelGroup object
	 *
	 * @param  element    Description of the Parameter
	 * @param  location   Description of the Parameter
	 * @param  namespace  NOT YET DOCUMENTED
	 */
	public ModelGroup(Element element, String location, Namespace namespace, SchemaReader schemaReader) {

		super(element, location, namespace, schemaReader);

	}


	/**
	 *  Gets the dataType attribute of the ModelGroup object
	 *
	 * @return    COMPLEX_TYPE - The dataType value as an int
	 */
	public int getDataType() {
		return MODEL_GROUP;
	}


	/**
	 *  String representation of the ModelGroup object
	 *
	 * @return    a String representation of the ModelGroup object
	 */
	public String toString() {
		String s = "ModelGroup: " + getName();
		String nl = "\n\t";
		s += nl + "type: " + getType();
		s += nl + "location: " + getLocation();
		s += nl + "contentmodel: " + getContentModel();
		if (getNamespace() != Namespace.NO_NAMESPACE)
			s += nl + "namespace: " + getNamespace().getPrefix() + ": " + getNamespace().getURI();
		else
			s += nl + "namespace: null";
		//		s += nl + "modelGroup: " + modelGroup;
		//		s += nl + "there are " + getChildren().size() + " content elements";
		/*
			 *  if (modelGroup == null)
			 *  s += "\n" + element.asXML();
			 */
		// s += Dom4jUtils.prettyPrint (getElement());
		return s;
	}


	/**
	 *  print a string to std out
	 *
	 * @param  s  Description of the Parameter
	 */
/* 	protected static void prtln(String s) {
		if (debug)
			System.out.println(s);
	} */
}

