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

import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Namespace;

import java.util.*;
import java.io.*;

/**
 *  Wrapper for global attribute definitions in an XML Schema, which are defined as an immediate child of the
 *  schema element.<p>
 *
 *  Global element definitions have an XPath of the form "/&lt;xsd:schema/&lt;xsd:attribute ...>", where the
 *  "xsd" prefix refers to a prefix that is mapped to the W3C Schema for Datatype Definitions (having a
 *  namespace uri of "http://www.w3.org/2001/XMLSchema").<p>
 *
 *  GlobalAttribute definitions are associated with a type in one of the following ways:
 *  <ul>
 *    <li> via a "type" attribute,
 *    <li> via a "ref" attribute
 *    <li> via an in-line simple type definition
 *  </ul>

 *
 * @author     Jonathan Ostwald
 * @version    $Id: GlobalAttribute.java,v 1.3 2009/03/20 23:34:01 jweather Exp $
 */
public class GlobalAttribute extends GlobalDeclaration {

/* 	private String name = null;
	private String type = null;
	private Element element = null;
	private String location = null;
	private Namespace namespace = null; */


	/**
	 *  Constructor for the GlobalAttribute object
	 *
	 * @param  element    NOT YET DOCUMENTED
	 * @param  location   NOT YET DOCUMENTED
	 * @param  namespace  NOT YET DOCUMENTED
	 */
	public GlobalAttribute(Element element, String location, Namespace namespace, SchemaReader schemaReader) {
		super (element, location, namespace, schemaReader);
	}

	/**
	 *  Gets the dataType attribute of the GlobalAttribute object
	 *
	 * @return    The dataType value
	 */
	public int getDataType() {
		return GLOBAL_ATTRIBUTE;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @return    NOT YET DOCUMENTED
	 */
	public String toString() {
		String s = "GlobalAttribute: " + name;
		s += super.toString();
/* 		String nl = "\n\t";
		s += nl + "type: " + type;
		s += nl + "location: " + getLocation();
		if (getNamespace() != null && getNamespace() != Namespace.NO_NAMESPACE)
			s += nl + "namespace: " + getNamespace().getPrefix() + ": " + getNamespace().getURI();
		else
			s += nl + "namespace: null"; */
		return s;
	}

}

