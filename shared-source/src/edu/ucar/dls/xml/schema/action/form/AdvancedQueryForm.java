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
package edu.ucar.dls.xml.schema.action.form;

import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.schema.action.GlobalDefReporter;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.serviceclients.remotesearch.RemoteResultDoc;

import edu.ucar.dls.schemedit.MetaDataFramework;
import edu.ucar.dls.index.ResultDocList;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Namespace;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.HTMLWriter;
import org.dom4j.io.OutputFormat;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.LabelValueBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.io.*;
import java.text.*;
import java.net.*;
import java.util.regex.*;

/**
 *  Controller for the SchemaViewer app.
 *
 *@author    ostwald
 */
public class AdvancedQueryForm extends ActionForm {

	private boolean debug = false;
	private SchemaHelper schemaHelper = null;
	private MetaDataFramework framework = null;
	
	private SchemaNode schemaNode = null;
	private GlobalDef globalDef = null;

	private String xmlFormat = null;
	private List frameworks = null;
	private String path = null;
	private List choices = null;
	private String [] selectedFrameworks; // selected frameworks

	private ResultDocList results = null;
	
	/**
	 *  Constructor
	 */
	public AdvancedQueryForm() {

	}
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		prtln ("reset()");
		// selectedFrameworks = new String[]{};
		selectedFrameworks = null;
	}
	
	public ResultDocList getResults() {
		return this.results;
	}
	
	public void setResults (ResultDocList resultDocList) {
		this.results = resultDocList;
	}
	
	public int getNumResults() {
		if (this.results != null)
			return this.results.size();
		else
			return 0;
	}
	
	public List getChoices () {
		return this.choices;
	}
	
	public void setChoices (List choicesList) {
		this.choices = choicesList;
	}
	
	/**
	 *  Gets the frameworks attribute of the AdvancedQueryForm object
	 *
	 *@return    The frameworks value
	 */
	public List getFrameworks() {
		if (frameworks == null)
			frameworks = new ArrayList();
		return frameworks;
	}
	/**
	 *  Sets the frameworks attribute of the AdvancedQueryForm object
	 *
	 *@param  list  The new frameworks value
	 */
	public void setFrameworks(List list) {
		frameworks = list;
	}
	
	public String [] getSelectedFrameworks () {
/*  		if (selectedFrameworks == null)
			selectedFrameworks = new String []{}; */
		return selectedFrameworks;
	}
	
	public void setSelectedFrameworks (String [] sf) {
		selectedFrameworks = sf;
	}

	/**
	 *  Sets the schemaNode attribute of the AdvancedQueryForm object
	 *
	 *@param  schemaNode  The new schemaNode valueead
	 */
	public void setSchemaNode(SchemaNode schemaNode) {
		this.schemaNode = schemaNode;
	}

	public boolean getSchemaNodeIsRequired () {
		if (schemaNode != null)
			return schemaNode.isRequired();
		else
			return false;
	}
	
	public boolean getSchemaNodeIsAttribute () {
		if (schemaNode != null)
			return schemaNode.isAttribute();
		else
			return false;
	}
	
	public boolean getSchemaNodeIsElement () {
		if (schemaNode != null)
			return schemaNode.isElement();
		else
			return false;
	}

	public List getSubstitutionGroupMembers () {
		List members = new ArrayList ();
		String baseUrl = "schema.do?command=doPath&path=";
		String parentPath = XPathUtils.getParentXPath(path);
		if (schemaNode.isHeadElement()) {
			Iterator elements = schemaNode.getSubstitutionGroup().iterator();
			while (elements.hasNext()) {
				GlobalElement element = (GlobalElement) elements.next();
				String iqn = element.getQualifiedInstanceName();
				// members.add (iqn);
				String href = baseUrl + parentPath + "/" + iqn;
				members.add( "<a href=\'" + href + "\'>" + iqn + "</a>");
			}
		}
		return members;
	}
	
	public String getHeadElement () {
		if (schemaNode != null && 
			globalDef != null && 
			globalDef.getSchemaReader() != null &&
			schemaNode.isSubstitutionGroupMember()) {
				
			String headElementName = schemaNode.getHeadElementName();
			String iqn = globalDef.getSchemaReader().getInstanceQualifiedName(headElementName);
			String baseUrl = "schema.do?command=doPath&path=";
			String parentPath = XPathUtils.getParentXPath(path);
			String href = baseUrl + parentPath + "/" + iqn;
			return "<a href=\'" + href + "\'>" + iqn + "</a>";
		}
		return "";
	}

	/**
	 *  Sets the globalDef attribute of the AdvancedQueryForm object
	 *
	 *@param  def  The new globalDef value
	 */
	public void setGlobalDef(GlobalDef def) {
		globalDef = def;
	}
	
	/**
	 *  Gets the globalDef attribute of the AdvancedQueryForm object
	 *
	 *@return    The globalDef value
	 */
	public GlobalDef getGlobalDef() {
		return globalDef;
	}


	public boolean getIsSimpleType () {
		if (globalDef != null)
			return (globalDef instanceof SimpleType);
		else
			return false;
	}
	
	public boolean getIsComplexType () {
		if (globalDef != null)
			return (globalDef instanceof ComplexType);
		else
			return false;
	}
	
	
	public boolean getDefIsEnumeration () {
		if (getIsSimpleType())
			return ((SimpleType)globalDef).isEnumeration();
		else
			return false;
	}

	public boolean getDefIsUnion () {
		if (getIsSimpleType())
			return ((SimpleType)globalDef).isUnion();
		else
			return false;
	}
	
	public boolean getDefIsBuiltin () {
		return globalDef.isBuiltIn();
	}
	
	/**
	 *  Gets the globalDef attribute of the AdvancedQueryForm object
	 *
	 *@return    The globalDef value
	 */
	public MetaDataFramework getFramework() {
		return framework;
	}


	/**
	 *  Sets the framework attribute of the AdvancedQueryForm object
	 *
	 *@param  mdf  The new framework value
	 */
	public void setFramework(MetaDataFramework mdf) {
		framework = mdf;
	}

	/**
	 *  Sets the schemaHelper attribute of the AdvancedQueryForm object
	 *
	 *@param  schemaHelper  The new schemaHelper value
	 */
	public void setSchemaHelper(SchemaHelper schemaHelper) {
		this.schemaHelper = schemaHelper;
	}


	/**
	 *  Gets the schemaHelper attribute of the AdvancedQueryForm object
	 *
	 *@return    The schemaHelper value
	 */
	public SchemaHelper getSchemaHelper() {
		return schemaHelper;
	}

	public String [] getUnionMembers () {
		if (getDefIsUnion()) {
			SimpleType def = (SimpleType)globalDef;
			return def.getUnionMemberTypeNames();
		}
		return null;
	}

	public List getEnumerationOptions () {
		LabelValueBean [] lvb = getEnumerationOptions (path);
		List options = new ArrayList ();
		for (int i=0;i<lvb.length;i++) {
			options.add (lvb[i].getValue());
		}
		return options;
	}
	
	/**
	 *  Gets the enumerationOptions attribute of the AdvancedQueryForm object
	 *
	 *@param  xpath  Description of the Parameter
	 *@return        The enumerationOptions value
	 */
	public LabelValueBean[] getEnumerationOptions(String xpath) {
		// prtln ("getEnumerationOptions() with xpath = " + xpath);
		LabelValueBean[] emptyArray = new LabelValueBean[]{};
		GlobalDef globalDef = schemaHelper.getGlobalDefFromXPath(xpath);

		if (globalDef == null) {
			prtln("globalDef not found for " + xpath);
			return emptyArray;
		}

		// prtln ("globalDef: " + globalDef.toString());

		if (globalDef.getDataType() != GlobalDef.SIMPLE_TYPE) {
			prtln("a SIMPLE_TYPE is required!");
			return emptyArray;
		}

		SimpleType simpleType = (SimpleType) globalDef;
		if ((!simpleType.isEnumeration()) && (!simpleType.isUnion())) {
			prtln("simpleType (" + simpleType.getQualifiedInstanceName() + " is not an enumeration");
			return emptyArray;
		}
		List values = schemaHelper.getEnumerationValues(simpleType.getQualifiedInstanceName(), false);
		
		if (values == null) {
			prtln ("WARNING: Unable to obtain enumeration values from " + simpleType.getQualifiedInstanceName());
			return emptyArray;
		}
		
		LabelValueBean[] options = new LabelValueBean[values.size()];
		for (int i = 0; i < values.size(); i++) {
			String value = (String) values.get(i);
			options[i] = new LabelValueBean(value, value);
		}
		prtln("getEnumerationOptions() returning " + options.length + " LabelValueBeans");
		return options;
	}


	/**
	 *  Gets the selectOptions attribute of the AdvancedQueryForm object
	 *
	 *@param  xpath  Description of the Parameter
	 *@return        The selectOptions value
	 */
	public LabelValueBean[] getSelectOptions(String xpath) {
		LabelValueBean[] enumerationOptions = getEnumerationOptions(xpath);
		LabelValueBean[] selectOptions = new LabelValueBean[enumerationOptions.length + 1];
		selectOptions[0] = new LabelValueBean("", "");
		for (int i = 0; i < enumerationOptions.length; i++) {
			selectOptions[i + 1] = enumerationOptions[i];
		}
		return selectOptions;
	}



	/**
	 *  Description of the Method
	 *
	 *@param  node           Description of the Parameter
	 *@return                Description of the Return Value
	 *@exception  Exception  Description of the Exception
	 */
	private String prettyPrint(Node node)
		throws Exception {
		StringWriter sw = new StringWriter();
		OutputFormat format = OutputFormat.createPrettyPrint();
		//These are the default formats from createPrettyPrint, so you needn't set them:
		//  format.setNewlines(true);
		//  format.setTrimText(true);
		format.setXHTML(true);
		//Default is false, this produces XHTML
		HTMLWriter ppWriter = new HTMLWriter(sw, format);
		ppWriter.write(node);
		ppWriter.flush();
		return sw.toString();
	}

	private String getGloballyQualifiedName (String name, SchemaReader schemaReader) {
		String iqn = schemaReader.getInstanceQualifiedName(name);
		if (schemaHelper.getNamespaceEnabled() && !NamespaceRegistry.isQualified(iqn)) {
			NamespaceRegistry globalNamespaces = schemaHelper.getDefinitionMiner().getNamespaces();
			String prefix = globalNamespaces.getNamedDefaultNamespace().getPrefix();
			iqn = NamespaceRegistry.makeQualifiedName(prefix, iqn);
		}
		return iqn;
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 *@param  s  The String that will be output.
	 */
	protected final void prtln(String s) {
		if (debug) {
			System.out.println("AdvancedQueryForm: " + s);
		}
	}

}

