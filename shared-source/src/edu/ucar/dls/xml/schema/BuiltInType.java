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
 * wrapper for BuiltInType definitions ({@link org.dom4j.Element}).
 * filters out annotation elements for now
 */
public class BuiltInType extends GenericType {
/* 	protected String name;
	protected String location;
	protected Element element;
	protected String path;
	protected String type;
	protected Namespace namespace;
	protected String xsdPrefix; */

	
	/**
	 * Constructor - 
	 */
	 public BuiltInType (String name) {
		 this (name, null);
	 }
	 
	public BuiltInType (String name, Namespace namespace) {
		super (name, namespace);
		location = "http://www.w3.org/2001/XMLSchema.xsd";
	}
	
	public SchemaReader getSchemaReader () {
		return null;
	}
	
	public boolean isSimpleType () {
		return (getDataType() == SIMPLE_TYPE);
	}
	public boolean isComplexType () {
		return (getDataType() == COMPLEX_TYPE);
	}
	
	public String getXsdPrefix () {
		return xsdPrefix;
	}
	
	public boolean isGlobalElement () {
		return false;
	}

	public boolean isGlobalDeclaration () {
        return false;
    }
	
	public boolean isGlobalAttribute () {
		return false;
	}
	
	public boolean isBuiltIn () {
		return (getDataType() == BUILT_IN_TYPE);
	}
	
	public int getDataType () {
		return BUILT_IN_TYPE;
	}
	
	public String getType () {
		return type;
	}
	
	public boolean isAnyType () {
		String qName = NamespaceRegistry.makeQualifiedName(getNamespace().getPrefix(), "any");
		return this.getName().equals(qName);
	}
	
	public String getName() {
		return name;
	}
	
	public String getQualifiedName () {
		// return NamespaceRegistry.makePrefix(getNamespace().getPrefix()) + getName();
		return name;
	}
	
	/**
	* schemaReader is unavailable, so we return a bogus prefix
	*/
	public String getQualifiedInstanceName () {
		return name;
	}
	
	public Namespace getNamespace () {
		return namespace;
	}
	
	public String getLocation() {
		return location;
	}
	
	public Element getElement() {
		return element;
	}
	
	public String getElementAsXml() {
		return "";
	}
	
	public String toString () {
		return "";
	}

}
