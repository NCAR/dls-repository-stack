/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package edu.ucar.dls.xml.schema;

import edu.ucar.dls.xml.Dom4jUtils;

import java.io.*;
import java.util.*;
import java.net.*;
import org.dom4j.Node;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.Namespace;

/**
 *  SchemaReader traverses an XML schema, which may be represented using
 *  multiple files, and extracts the key schema elements (including Data Type
 *  definitions and global elements). These key schema elements are represented
 *  as GlobalDefs and stored in a {@link GlobalDefMap} which is keyed by element
 *  name. <p>
 *
 *  SchemaReader instances are instantated by DefinitionMiner.processSchemaFile().
 *
 * @author    ostwald
 */
public class SchemaReader {

	private static boolean debug = false;

	private DefinitionMiner definitionMiner;
	private GlobalDefMap globalDefMap;

	private Document doc;
	private Element root;
	private URI source;

	private Namespace targetNamespace;
	private String xsdPrefix;
	private int inlineDefCounter = 0;
	private Namespace ns;
	private NamespaceRegistry namespaces = null;


	/**
	 *  Constructor for the SchemaReader object.
	 *
	 * @param  doc                        NOT YET DOCUMENTED
	 * @param  source                     NOT YET DOCUMENTED
	 * @param  definitionMiner            NOT YET DOCUMENTED
	 * @param  defaultTargetNamespaceURI  NOT YET DOCUMENTED
	 * @exception  SchemaHelperException  Description of the Exception
	 */
	public SchemaReader(Document doc,
	                    URI source,
	                    DefinitionMiner definitionMiner,
	                    String defaultTargetNamespaceURI) throws SchemaHelperException {
		this.doc = doc;
		namespaces = new NamespaceRegistry();
		try {
			if (this.doc == null)
				throw new Exception("SchemaReader got null Document");
			namespaces.registerNamespaces(doc);
		} catch (Exception e) {
			throw new SchemaHelperException("Could not register namespaces for (" + source + ": " + e);
		}

		if (namespaces.getDefaultNamespace() == namespaces.getSchemaNamespace()) {
			prtln("CONVERTING ... (" + source.toString());
			Namespace namedDefaultNS = namespaces.getNamedDefaultNamespace();
			namespaces.register(namedDefaultNS);
			prtln("registered " + NamespaceRegistry.nsToString(namedDefaultNS));
			prtln(namespaces.toString());
			SchemaNamespaceConverter converter = new SchemaNamespaceConverter();
			doc = converter.convert(doc, namedDefaultNS.getPrefix());
		}

		this.root = doc.getRootElement();
		this.source = source;
		this.definitionMiner = definitionMiner;

		// prtln (namespaces.toString());
		globalDefMap = definitionMiner.getGlobalDefMap();
		xsdPrefix = root.getNamespacePrefix();

		setTargetNamespace(defaultTargetNamespaceURI);

	}


	/**
	 *  Gets the xsdPrefix attribute of the SchemaReader object
	 *
	 * @return    The xsdPrefix value
	 */
	public String getXsdPrefix() {
		return this.xsdPrefix;
	}


	/**
	 *  Determine the targetNamespace for this SchemaReader and update the
	 *  NamespaceRegistry if necessary.
	 *
	 * @param  defaultTargetNamespaceURI  The new targetNamespace value
	 */
	private void setTargetNamespace(String defaultTargetNamespaceURI) {

		// if the root element contains a targetNamespace attribute, then use it
		Attribute tnsAttribute = root.attribute("targetNamespace");
		String targetNamespaceURI = (tnsAttribute != null) ? tnsAttribute.getValue() : defaultTargetNamespaceURI;

		targetNamespace = namespaces.getNSforUri(targetNamespaceURI);

		// if the targetNamespace has not been registered, then create it (as the default) and update
		// the targetNamespace as well as the NamespaceRegistry
		if (targetNamespace == Namespace.NO_NAMESPACE) {
			targetNamespace = new Namespace("", targetNamespaceURI);
			namespaces.register(targetNamespace);
			namespaces.setTargetNamespaceUri(targetNamespaceURI);
		}
	}


	/**
	 *  Gets the builtIn attribute of the SchemaReader object
	 *
	 * @param  qualifiedTypeName  the a qualifiedTypeName to test
	 * @return                    true if the provided typeName is qualified by "xml" or local equivalent
	 */
	public boolean isBuiltIn(String qualifiedTypeName) {
		if (NamespaceRegistry.getNamespacePrefix(qualifiedTypeName).equals("xml"))
			return true;
		return (NamespaceRegistry.isQualified(qualifiedTypeName) &&
			NamespaceRegistry.getNamespacePrefix(qualifiedTypeName).equals(namespaces.getSchemaNamespace().getPrefix()));
	}


	/**
	 *  Gets the namespaces attribute of the SchemaReader object
	 *
	 * @return    The namespaces value
	 */
	public NamespaceRegistry getNamespaces() {
		return namespaces;
	}


	/**
	 *  Gets the instanceNamespaces attribute of the SchemaReader object
	 *
	 * @return    The instanceNamespaces value
	 */
	public NamespaceRegistry getInstanceNamespaces() {
		return definitionMiner.getNamespaces();
	}


	/**
	 *  Resolve provided prefix to the prefix in the instance document for the namespace
	 *
	 * @param  prefix  the prefix to resolve
	 * @return         resolved prefix from instance document namespace
	 */
	public String resolveToInstancePrefix(String prefix) {
		String nsUri = namespaces.getNSforPrefix(prefix).getURI();
		return getInstanceNamespaces().getPrefixforUri(nsUri);
	}


	/**
	 *  Gets the location attribute of the SchemaReader object
	 *
	 * @return    The location value
	 */
	public URI getLocation() {
		return source;
	}


	/**
	 *  Determines the targetNamespace of the schema document. If a "targetNamespace"
	 *  is explicitly declared, then this is returned. Otherwise, the
	 *  "defaultTargetNamespaceURI" is used.
	 *
	 * @param  defaultTargetNamespaceURI  NOT YET DOCUMENTED
	 * @return                            The targetNamespace value
	 */
	private String getTargetNamespaceUri(String defaultTargetNamespaceURI) {
		Attribute tnsAttribute = root.attribute("targetNamespace");
		if (tnsAttribute != null)
			return tnsAttribute.getText();
		else
			return defaultTargetNamespaceURI;
	}


	/**
	 *  Process the document and extract type definitions 
	 *
	 * @exception  SchemaHelperException  if the doc can't be processed
	 */
	public void read() throws SchemaHelperException {

		ArrayList includePaths = getIncludePaths();
		ArrayList importPaths = getImportPaths();
		includePaths.addAll(importPaths);

		for (Iterator i = includePaths.iterator(); i.hasNext(); ) {
			IncludePath includePath = (IncludePath) i.next();
			String path = includePath.path;
			String namespace = includePath.namespace;
			// paths are relative to parent of file
			URI includeUri = null;
			URI rootRelativeIncludeUri = null;
			try {
				includeUri = source.resolve(path);
				rootRelativeIncludeUri = definitionMiner.getSchemaURI().resolve(path);
				// prtln ("includeUri: " + includeUri);
			} catch (Exception e) {
				String msg = "couldn't make a url for " + path;
				prtln(msg);
				throw new SchemaHelperException(msg);
			}

			boolean processed = false;
			String processErrorMsg = null;
			try {
				definitionMiner.processSchemaFile(includeUri, namespace);
				processed = true;
			} catch (Throwable t) {}

			// couldn't process using parent-relative uri, now try rootRelative
			if (!processed) {
				try {
					definitionMiner.processSchemaFile(rootRelativeIncludeUri, namespace);
					processed = true;
				} catch (Throwable t) {
					processErrorMsg = t.getMessage();
				}
			}

			if (!processed) {
				throw new SchemaHelperException("could not process " + path + ": " + processErrorMsg);
			}
		}

		try {
			getTypeDefs("simpleType");
			getTypeDefs("complexType");
			getGroupDefinitions("group");
			getGlobalDeclarations("element");
			getGlobalDeclarations("attribute");
			getGlobalDeclarations("attributeGroup");
		} catch (Exception e) {
			prtln("SchemaReader ERROR reading from " + this.source + ": " + e.getMessage());
			e.printStackTrace();
		}
	}


	/**
	 *  Returns a list of the paths that are declared as "include"s for the given
	 *  {@link org.dom4j.Document}
	 *
	 * @return    an ArrayList containing the paths of all include schema files
	 */
	public ArrayList getIncludePaths() {
		ArrayList paths = new ArrayList();
		List list = doc.selectNodes("//" + NamespaceRegistry.makeQualifiedName(xsdPrefix, "include"));
		for (Iterator iter = list.iterator(); iter.hasNext(); ) {
			Element element = (Element) iter.next();
			String schemaLocation = element.attributeValue("schemaLocation");
			paths.add(new IncludePath(schemaLocation, targetNamespace.getURI()));
		}
		return paths;
	}


	/**
	 *  Gets the importPaths attribute of the SchemaReader object
	 *
	 * @return    The importPaths value
	 */
	public ArrayList getImportPaths() {
		ArrayList paths = new ArrayList();
		List list = doc.selectNodes("//" + NamespaceRegistry.makeQualifiedName(xsdPrefix, "import"));
		for (Iterator iter = list.iterator(); iter.hasNext(); ) {
			Element element = (Element) iter.next();
			String schemaLocation = element.attributeValue("schemaLocation");
			String namespace = element.attributeValue("namespace");
			paths.add(new IncludePath(schemaLocation, namespace));
		}
		return paths;
	}


	/**
	 *  Return globalDef from the globalDefMap after resolving the given typeName
	 *  into a baseName and namespace. Returns null if namespace cannot be
	 *  determined. NOTE: this can be refactored to use getInstanceQualifiedName
	 *  (returned prefix must be resolved into a namespace before the call to
	 *  globalDefMap.getValue (baseName, namespace)
	 *
	 * @param  typeName  typeName identifying the globalDef to find
	 * @return           The globalDef value
	 */
	public GlobalDef getGlobalDef(String typeName) {
		// prtln ("getGlobalDef with " + typeName);
		Namespace namespace = targetNamespace;
		String prefix = null;
		String baseName = typeName;
		if (NamespaceRegistry.isQualified(typeName)) {
			baseName = NamespaceRegistry.stripNamespacePrefix(typeName);
			prefix = NamespaceRegistry.getNamespacePrefix(typeName);
			namespace = getNamespaces().getNSforPrefix(prefix);
			if (namespace == Namespace.NO_NAMESPACE) {
				prtln("\nWARNING: getGlobalDef can't find namespace for \"" + prefix + "\"");
				prtln(this.namespaces.toString());
				return null;
			}
		}
		return globalDefMap.getValue(baseName, namespace.getURI());
	}


	/**
	 *  Gets the instanceQualifiedName attribute of the SchemaReader object
	 *
	 * @param  name  a type name from the local document
	 * @return       The instanceQualifiedName value
	 */
	public String getInstanceQualifiedName(String name) {
		// prtln ("getInstanceQualifiedName() with " + name);
		Namespace namespace = targetNamespace;
		String prefix = null;
		String baseName = name;
		if (NamespaceRegistry.isQualified(name)) {
			baseName = NamespaceRegistry.stripNamespacePrefix(name);
			prefix = NamespaceRegistry.getNamespacePrefix(name);
			namespace = getNamespaces().getNSforPrefix(prefix);
			if (namespace == Namespace.NO_NAMESPACE) {
				prtln("\nWARNING: getInstanceQualifiedName can't find namespace for \"" + prefix + "\"");
				prtln("name: " + name);
				prtln("schemaReader.source: " + this.source);
				prtln(this.namespaces.toString());
				return null;
			}
		}

		NamespaceRegistry instanceNamespaces = this.getInstanceNamespaces();
		Namespace instanceNS = instanceNamespaces.getNSforUri(namespace.getURI());

		if (instanceNS.getPrefix().equals("")) {
			// if there is a namedDefaultNamespace at the instanceLevel, use it instead ...
			if (instanceNamespaces.getNamedDefaultNamespace() != Namespace.NO_NAMESPACE &&
				instanceNamespaces.isMultiNamespace()) {
				instanceNS = this.getInstanceNamespaces().getNamedDefaultNamespace();
			}
		}

		return NamespaceRegistry.makeQualifiedName(instanceNS, baseName);
	}


	/**
	 *  Gets the unionDataType attribute of the SchemaReader object
	 *
	 * @param  e  element to check for UnionDataTye
	 * @return    The unionDataType value
	 */
	private boolean isUnionDataType(Element e) {
		return (e.getQualifiedName().equals(NamespaceRegistry.makeQualifiedName(xsdPrefix, "union")));
	}


	/**
	 *  Adds a feature to the MemberType attribute of the SchemaReader object
	 *
	 * @param  union     The feature to be added to the MemberType attribute
	 * @param  typeName  The feature to be added to the MemberType attribute
	 */
	private void addMemberType(Element union, String typeName) {
		String memberTypes = union.attributeValue("memberTypes", null);
		if (memberTypes == null)
			memberTypes = typeName;
		else
			memberTypes = memberTypes + " " + typeName;
		union.setAttributeValue("memberTypes", memberTypes);
	}


	/**
	 *  Gets the typeDefs attribute of the SchemaReader object
	 *
	 * @param  typeSpec       NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	private void getTypeDefs(String typeSpec) throws Exception {
		// prtln ("getTypeDefs: " + typeSpec + " (" + this.source.toString() + ")");
		if (doc == null) {
			return;
		}
		List list = doc.selectNodes("//" + NamespaceRegistry.makeQualifiedName(xsdPrefix, typeSpec));

		for (Iterator iter = list.iterator(); iter.hasNext(); ) {
			Element element = (Element) iter.next();
			String typeName = element.attributeValue("name");
			boolean isInlineTypeDef = false;

			// type definitions without a name attribute are inline definitions
			if (typeName == null) {
				Element parent = element.getParent();
				if (parent == null) {
					prtlnErr("WARNING: parent not found for in-line typeDef\n" + element.asXML());
					continue;
				}
				String parentName = parent.attributeValue("name");
				typeName = definitionMiner.getInlineTypeName(parentName);

				// handle union inlines differently than others
				if (isUnionDataType(parent))
					addMemberType(parent, typeName);
				else
					parent.addAttribute("type", typeName);

				// parent.addAttribute("type", typeName);
				element.addAttribute("name", typeName);
				isInlineTypeDef = true;
			}

			GenericType typeDef = null;
			try {
				if (typeSpec == "complexType") {
					typeDef = new ComplexType(element, source.toString(), targetNamespace, this);
					typeDef.setInline(isInlineTypeDef);
				}
				else if (typeSpec == "simpleType") {
					typeDef = new SimpleType(element, source.toString(), targetNamespace, this);
					typeDef.setInline(isInlineTypeDef);
				}
				else
					throw new Exception("unknown typeSpec: " + typeSpec);

				if (typeDef != null) {
					try {
						definitionMiner.addGlobalDef(typeName, typeDef, targetNamespace);
					} catch (Exception e) {
						prtlnErr(e.getMessage());
					}
				}
			} catch (Throwable t) {
				prtlnErr("WARNING: not able to create typeDef for this element:" + Dom4jUtils.prettyPrint(element));
				/* 				prtlnErr ("\tReason: " + t.getMessage());
				t.printStackTrace(); */
			}
		}
	}


	/**
	 *  Gets the globalDeclarations attribute of the SchemaReader object
	 *
	 * @param  decType        the declarationType to find (e.g., "element", "attribute", etc)
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	private void getGlobalDeclarations(String decType) throws Exception {
		if (doc == null) {
			return;
		}
		String selectionPath = "/" + NamespaceRegistry.makeQualifiedName(xsdPrefix, "schema") +
			"/" + NamespaceRegistry.makeQualifiedName(xsdPrefix, decType);
		List list = doc.selectNodes(selectionPath);
		for (Iterator iter = list.iterator(); iter.hasNext(); ) {
			Element element = (Element) iter.next();
			String name = element.attributeValue("name");
			GlobalDeclaration globalDec = null;

			if (decType == "element") {
				globalDec = new GlobalElement(element, source.toString(), targetNamespace, this);
			}
			else if (decType == "attribute") {
				globalDec = new GlobalAttribute(element, source.toString(), targetNamespace, this);
			}
			else if (decType == "attributeGroup") {
				globalDec = new AttributeGroup(element, source.toString(), targetNamespace, this);
			}
			else
				throw new Exception("unknown declarationType: " + decType);

			try {
				definitionMiner.addGlobalDef(name, globalDec, targetNamespace);
			} catch (Exception e) {
				prtlnErr(e.getMessage());
			}
		}
	}


	/**
	 *  Gets the groupDefinitions attribute of the SchemaReader object
	 *
	 * @param  decType        NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	private void getGroupDefinitions(String decType) throws Exception {
		if (doc == null) {
			return;
		}
		String selectionPath = "/" + NamespaceRegistry.makeQualifiedName(xsdPrefix, "schema") +
			"/" + NamespaceRegistry.makeQualifiedName(xsdPrefix, decType);
		List list = doc.selectNodes(selectionPath);
		for (Iterator iter = list.iterator(); iter.hasNext(); ) {
			Element element = (Element) iter.next();
			String name = element.attributeValue("name");
			GenericType groupDef = null;

			if (decType == "group") {
				groupDef = new ModelGroup(element, source.toString(), targetNamespace, this);
			}
			else
				throw new Exception("unknown groupDefinition type: " + decType);

			try {
				definitionMiner.addGlobalDef(name, groupDef, targetNamespace);
			} catch (Exception e) {
				prtlnErr(e.getMessage());
			}
		}
	}


	/**
	 *  Class reresenting a schema path and it's namespace
	 *
	 * @author    Jonathan Ostwald
	 */
	class IncludePath {
		/**  NOT YET DOCUMENTED */
		public String path;
		/**  NOT YET DOCUMENTED */
		public String namespace;


		/**
		 *  Constructor for the IncludePath object
		 *
		 * @param  path       NOT YET DOCUMENTED
		 * @param  namespace  NOT YET DOCUMENTED
		 */
		IncludePath(String path, String namespace) {
			this.path = path;
			this.namespace = namespace;
		}
	}


	/**
	 *  Sets the debug attribute of the SchemaReader class
	 *
	 * @param  bool  The new debug value
	 */
	public static void setDebug(boolean bool) {
		debug = bool;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {

			while (s.length() > 0 && s.charAt(0) == '\n') {
				System.out.println("");
				s = s.substring(1);
			}

			System.out.println("Schema Reader: " + s);
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtlnErr(String s) {
		while (s.length() > 0 && s.charAt(0) == '\n') {
			System.out.println("");
			s = s.substring(1);
		}

		System.out.println("Schema Reader: " + s);
	}
}

