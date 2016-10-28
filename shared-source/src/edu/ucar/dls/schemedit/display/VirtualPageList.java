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
package edu.ucar.dls.schemedit.display;

import edu.ucar.dls.schemedit.MetaDataFramework;

import java.util.*;
import java.io.Serializable;

import org.dom4j.Element;
import org.dom4j.Document;

/**
 *  The VirtualPageList implements a virtual structure, in which a flat
 *  heirarchy is broken up into virtual pages.<p>
 *
 *  For example, given a schema that defines the following top level fields:
 *  "field-A, field-B, field-C, ... field-D. A VirtualPageList could then define
 *  the following virtual structure:
 *  <ul>
 *    <li> page-1
 *    <ul>
 *      <li> field-A</li>
 *      <li> field-C</li>
 *    </ul>
 *
 *    <li> page-2
 *    <ul>
 *      <li> field-B</li>
 *      <lio> ... </li>
 *    </ul>
 *
 *  </ul>
 *  Manages information about the editor's UI pages which present a structure to
 *  the user that The PageList is used to build the menu of pages within the
 *  metadata editor, as well as to aid in navigation within the SchemEdit
 *  controllers.<p>
 *
 *
 *
 * @author    ostwald
 */
public class VirtualPageList extends PageList {

	private static boolean debug = false;
	private Element element = null;
	private Map fieldMap = new HashMap();
	private List allFields = null;
	private List missingFields = null;
	private List extraFields = null;


	/**  Constructor for the PageList object */
	public VirtualPageList() {
		super();
	}


	/**
	 *  Constructor for the VirtualPageList object
	 *
	 * @param  pageData  Description of the Parameter
	 */
	public VirtualPageList(String[][] pageData) {
		super(pageData);
	}


	/**
	 *  Constructor for the VirtualPageList object
	 *
	 * @param  element  XML element defining pages for this VirtualPageList
	 */
	public VirtualPageList(Element element) {
		this();
		prtln("\nVirtualPageList()");
		this.element = element;
		List virtualPageElements = element.elements("virtualPage");
		prtln(virtualPageElements.size() + " virtualPageElements");
		Iterator virtualPageElementIter = element.elements("virtualPage").iterator();

		while (virtualPageElementIter.hasNext()) {
			Element virtualPageElement = (Element) virtualPageElementIter.next();

			List fieldElements = virtualPageElement.selectNodes("fields/field");
			String name = virtualPageElement.attributeValue("name");

			prtln(name + " has " + fieldElements.size() + " fieldElements");
			if (this.getHomePage() == null) {
				this.setHomePage(name);
				this.setFirstPage(name);
			}

			List fields = new ArrayList();
			for (Iterator i = fieldElements.iterator(); i.hasNext(); ) {
				Element fieldElement = (Element) i.next();
				prtln("  adding page for " + name);
				fields.add(fieldElement.getTextTrim());
			}
			this.addPage(name, name, fields);
		}
	}
	
	/**
	* fields that are defined by framework but not present in allFields of this virtualPageList
	*/
	public List getMissingFields () {
		return this.missingFields;
	}
	
	/**
	* Initialize missingFields and extraFields attributes for this VirtualPageList.
	*/
	public void verifyFields(MetaDataFramework framework) {

		/* test that all top-level elements in schema/instance doc are accounted for in VPL */
		Document instanceDoc = framework.getSchemaHelper().getInstanceDocument();
		Iterator instanceFieldIter = instanceDoc.getRootElement().elementIterator();
		List allFields = this.getAllFields();
		this.missingFields = new ArrayList();
		while (instanceFieldIter.hasNext()) {
			Element instanceElement = (Element)instanceFieldIter.next();
			String instanceField = instanceElement.getPath();
			prtln (instanceField);
			if (!allFields.contains(instanceField)) {
				prtln (" NOT FOUND!");
				missingFields.add (instanceField);
			}
		}

		this.extraFields = new ArrayList();
		Iterator allFieldsIter = allFields.iterator();
		while (allFieldsIter.hasNext()) {
			String field = (String)allFieldsIter.next();
			if (instanceDoc.selectSingleNode (field) == null)
				this.extraFields.add (field);
		}
		
	}
	
	/**
	* Fields that are in allFields of this VirtualPageList but not defined in framework.
	*/
	public List getExtraFields () {
		return this.extraFields;
	}

	
	/**
	* Get list of all fields across all Virtual Pages of this VirtualPageList.
	*/
	public List getAllFields () {
		if (this.allFields == null) {
			this.allFields = new ArrayList();
			Iterator pageIter = this.getPages().iterator();
			while(pageIter.hasNext()) {
				VirtualPage page = (VirtualPage)pageIter.next();
				Iterator fieldIter = page.getFields().iterator();
				while (fieldIter.hasNext()) {
					this.allFields.add ((String)fieldIter.next());
				}
			}
		}
		return this.allFields;
	}

	/**
	 *  Adds a feature to the Page attribute of the VirtualPageList object
	 *
	 * @param  vals  The feature to be added to the Page attribute
	 */
	public void addPage(String[] vals) {
		/* 		VirtualPage page = new VirtualPage (vals[0], vals[1]);
		if (!pages.contains(page)) {
			pages.add(page);
		} */
		prtln("WARNING: this version of addPage method not implemented in VirtualPageList");
	}


	/**
	 *  Adds a feature to the VirtualPage attribute of the VirtualPageList object
	 *
	 * @param  mapping  The feature to be added to the Page attribute
	 * @param  name     The feature to be added to the Page attribute
	 * @param  fields   The feature to be added to the Page attribute
	 */
	public void addPage(String mapping, String name, List fields) {
		VirtualPage page = new VirtualPage(mapping, name, fields);
		if (!pages.contains(page)) {
			pages.add(page);
			for (Iterator i = fields.iterator(); i.hasNext(); ) {
				this.fieldMap.put((String) i.next(), page.getName());
			}
		}
	}


	/**
	 *  Gets the pages attribute of the VirtualPageList object
	 *
	 * @param  fieldXPath  NOT YET DOCUMENTED
	 * @return             The pages value
	 */
	/* 	public ArrayList getPages() {
		return pages;
	} */
	public String getPageName(String fieldXPath) {
		return (String) this.fieldMap.get(fieldXPath);
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (Iterator i = pages.iterator(); i.hasNext(); ) {
			VirtualPage page = (VirtualPage) i.next();
			buf.append("\n" + page.toString());
		}
		buf.append("\n\nhomePage: " + getHomePage());
		buf.append("\nfirstPage: " + getFirstPage());
		return buf.toString();
	}


	/**
	 *  Information about the pages/form in an editor. This information supports
	 *  navigation (in the navBar and in the ActionForm
	 *
	 * @author    ostwald
	 */
	public class VirtualPage {

		private String name = "";
		private String mapping = "";
		private List fields;


		/**
		 *  Constructor for the Page object
		 *
		 * @param  mapping  the key into the action mapping to display this page
		 * @param  name     pretty name used for display
		 * @param  fields   NOT YET DOCUMENTED
		 */
		public VirtualPage(String mapping, String name, List fields) {
			this.name = name;
			this.mapping = mapping;
			this.fields = fields;
		}


		/**
		 *  Gets the name attribute of the Page object, defaulting to mapping
		 *  attribute if name is not defined.
		 *
		 * @return    The name value
		 */
		public String getName() {
			if (name == null || name.trim().length() == 0)
				return this.mapping;
			return name;
		}


		/**
		 *  Gets the mapping attribute of the Page object
		 *
		 * @return    The mapping value
		 */
		public String getMapping() {
			return mapping;
		}


		/**
		 *  Gets the fields attribute of the VirtualPage object
		 *
		 * @return    The fields value
		 */
		public List getFields() {
			return fields;
		}


		/**
		 *  Description of the Method
		 *
		 * @return    Description of the Return Value
		 */
		public String toString() {
			String s = "virtual page - name: " + name + ", mapping: " + mapping + ", and " + fields.size() + " fields";
			for (Iterator i = this.fields.iterator(); i.hasNext(); ) {
				s += "\n- " + (String) i.next();
			}
			return s;
		}

	}


	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	private void prtln(String s) {
		if (debug) {
			System.out.println("VirtualPageList: " + s);
		}
	}
}

