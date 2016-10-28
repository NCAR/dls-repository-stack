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
 *  Wrapper for AttributeGroup definitions in an XML Schema, which are defined as an immediate child of the
 *  schema element.<p>
 *
 *  AttributeGroup definitions are associated with a type in one of the following ways:
 *  <ul>
 *    <li> via a "type" attribute,
 *    <li> via a "ref" attribute
 *    <li> via an in-line simple type definition
 *  </ul>

 *
 * @author     Jonathan Ostwald
 */
public class AttributeGroup extends GlobalDeclaration {


	/**
	 *  Constructor for the AttributeGroup object
	 *
	 * @param  element    NOT YET DOCUMENTED
	 * @param  location   NOT YET DOCUMENTED
	 * @param  namespace  NOT YET DOCUMENTED
	 */
	public AttributeGroup(Element element, String location, Namespace namespace, SchemaReader schemaReader) {
		super (element, location, namespace, schemaReader);
	}

	public List getAttributes () {
		return this.element.elements("attribute");
	}
	
	/**
	 *  Gets the dataType attribute of the AttributeGroup object
	 *
	 * @return    The dataType value
	 */
	public int getDataType() {
		return ATTRIBUTE_GROUP;
	}

	public void extractDocumentation () {
		Element docElement = (Element) this.element.selectSingleNode (this.xsdPrefix + ":attribute/"  +
																	  this.xsdPrefix + ":annotation/"  +
									                                  this.xsdPrefix + ":documentation");
		if (docElement != null) {
			this.documentation = docElement.getTextTrim();
		}
	}	

	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @return    NOT YET DOCUMENTED
	 */
	public String toString() {
		String s = "AttributeGroup: " + name;
		s += super.toString();
 		String nl = "\n\t";
		for (Iterator i=getAttributes().iterator();i.hasNext();) {
			Element e = (Element)i.next();
			s += nl + e.asXML();
		}
		return s;
	}

}

