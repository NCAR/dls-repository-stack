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
import org.dom4j.Namespace;
import java.io.*;

/**
 *  Interface for classes encapsulating XML Schema Elements, such as Elements, Attributes, and Type Definitions. There are two
 "families" of GlobalDefs: type definitions and declarations. Type definitions include GenericType, BuiltInType, SimpleType and ComplexType.
 Global Declarations include GlobalElement and GlobalAttributes.
 *
 *@author    ostwald<p>
 $Id $
 */
public interface GlobalDef {

	public final static int GENERIC_TYPE = 0;
	public final static int SIMPLE_TYPE = 1;
	public final static int COMPLEX_TYPE = 2;
	public final static int GLOBAL_DECLARATION = 3;
	public final static int GLOBAL_ELEMENT = 4;
	public final static int GLOBAL_ATTRIBUTE = 5;
	public final static int BUILT_IN_TYPE = 6;
	public final static int MODEL_GROUP = 7;
	public final static int ATTRIBUTE_GROUP = 8;


	/**
	 *  Returns an integer contant that specifies whether this GlobalDef is Generic, Simple, Complex, Global or Built-in datatype.
	 *
	 *@return    The dataType value
	 */
	public int getDataType();


	/**
	 *  Returns string representation of the Global Def's dataType (e.g., "simpleType", "complexType", etc).
	 *
	 *@return    The type value
	 */
	public String getType();


	public String getDocumentation();
	
	/**
	 *  Returns the dataType name for this GlobalDef (e.g., "union.dateType", "stringTextType", etc).
	 *
	 *@return    The name value
	 */
	public String getName();
	
	public Namespace getNamespace();

	public String getQualifiedName();

	/**
	* use prefix for namespace as defined at the instance level. 
		NOTE:  namespaces may not be defined at the instance level!
		*/
	public String getQualifiedInstanceName ();
	
	/**
	 *  Returns string representation of URI to the schema file in which this GlobalDef is defined.
	 *
	 *@return    The location value
	 */
	public String getLocation();


	/**
	 *  Gets the element attribute of the GlobalDef object
	 *
	 *@return    The element value
	 */
	public Element getElement();

	public String getElementAsXml();

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public String toString();

	public SchemaReader getSchemaReader();

		/**
	 *  Gets the builtIn attribute of the GlobalDef object
	 *
	 *@return    The builtIn value
	 */
	public boolean isTypeDef();
	
	/**
	 *  Gets the builtIn attribute of the GlobalDef object
	 *
	 *@return    The builtIn value
	 */
	public boolean isBuiltIn();
	
	public boolean isAnyType();
	
	/**
	 *  Gets the simpleType attribute of the GlobalDef object
	 *
	 *@return    The simpleType value
	 */
	public boolean isSimpleType();


	/**
	 *  Gets the complexType attribute of the GlobalDef object
	 *
	 *@return    The complexType value
	 */
	public boolean isComplexType();

	public boolean isModelGroup();
	
	public boolean isAttributeGroup();

	/**
	 *  Gets the globalElement attribute of the GlobalDef object
	 *
	 *@return    The globalElement value
	 */
	 
	public boolean isGlobalDeclaration();
	 
	public boolean isGlobalElement();

	public boolean isGlobalAttribute();


}

