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

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.Constants;
import edu.ucar.dls.schemedit.dcs.*;
import edu.ucar.dls.schemedit.vocab.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.*;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.*;
import java.io.*;
import java.text.*;
import java.util.regex.*;

import javax.servlet.http.HttpServletRequest;

/**
 *  Maintains the state of collapsible nodes in the metadata Editor. States are
 *  OPENED and CLOSED, and the default is controlled by setDefaultState.
 *
 * @author    ostwald
 */
public class DcsTableBean implements Serializable {
	private boolean debug = false;

	private Map stateMap = null;
	String sortBy = null;
	String sortOrder = null;
	List sets = null;
	Map userRolesMap = null;
	List columns = null;
	List collectionInfos = null;


	/**  Constructor for the DcsTableBean object */
	public DcsTableBean(List sets, Map userRolesMap) {
		this.sets = sets;
		this.userRolesMap = userRolesMap;
		this.columns = this.getColumns();
		this.collectionInfos = new ArrayList();
		for (Iterator i=this.sets.iterator();i.hasNext();) {
			DcsSetInfo setInfo = (DcsSetInfo)i.next();
			this.collectionInfos.add (new CollectionInfo (setInfo, userRolesMap));
		}
	}

	/**
	*  DcsTableColumn params: (String headerText, String cellText, String sortBy, String sortOrder, String onclick) 
	*/
	public List getColumns () {
		if (this.columns == null) {
			this.columns = new ArrayList();
			this.columns.add (new DcsTableColumn ("Collection", "name", "name", Constants.ASCENDING, "listAllRecords"));
			this.columns.add (new DcsTableColumn ("Create record", "create", null, -1, "createRecord"));
			this.columns.add (new DcsTableColumn ("Metadata format", "format", "format", Constants.ASCENDING, null));
			this.columns.add (new DcsTableColumn ("Num Records", "numFiles", "numFiles", Constants.DESCENDING, "listAllRecords"));
			this.columns.add (new DcsTableColumn ("Not Valid", "numNotValid", "numNotValid", Constants.DESCENDING, "listNotValidRecords"));
		}
		return this.columns;
	}

	public List getHeaderCells() {
		List headerCells = new ArrayList ();
		for (Iterator i=this.getColumns().iterator();i.hasNext();) {
			DcsTableColumn column = (DcsTableColumn)i.next();
			headerCells.add (new DcsTableHeaderCell (column.getHeaderText(), column.getSortBy(), column.getSortOrder()));
		}
		return headerCells;
	}
	
	public List getDcsTableRows () {
		prtln ("getDcsTableRows()");
		List rows = new ArrayList();
		Iterator colInfoIter = this.collectionInfos.iterator();
		while (colInfoIter.hasNext()) {
			CollectionInfo colInfo = (CollectionInfo)colInfoIter.next();
			rows.add (getTableRow (colInfo));
		}
		prtln ("getDcsTableRows() returning " + rows.size() + " rows");
		return rows;
	}
	
	public DcsTableRow getTableRow(CollectionInfo collectionInfo) {
		// a row is a list of DcsTableCells
		List cells = new ArrayList();
		Iterator colIter = this.columns.iterator();
		
		while (colIter.hasNext()) {
			DcsTableColumn column = (DcsTableColumn)colIter.next();
			String contentSpec = column.getCellText();
			DcsTableCell cell = new DcsTableCell();
			if (collectionInfo.hasProp (contentSpec))
				cell.setLabel (collectionInfo.getProp(contentSpec));
			else
				cell.setLabel (contentSpec);
			cell.setOnclick(column.getOnclick());
			cells.add (cell);
		}
		return new DcsTableRow(collectionInfo.props, cells);
	}
	
	public class DcsTableHeaderCell {
		
		String label;
		String sortBy;
		int sortOrder;
		
		public DcsTableHeaderCell (String label, String sortBy, int sortOrder) {
			this.label = label;
			this.sortBy = sortBy;
			this.sortOrder = sortOrder;
		}
		
		public String getLabel () {
			return this.label;
		}
		
		public String getSortBy () {
			return this.sortBy;
		}	
		
		public int getSortOrder() {
			return this.sortOrder;
		}
		
	}
	
	public class DcsTableRow {
		
		CollectionInfo colInfo = null;
		List cells = null;
		Map props = null;
		
		public DcsTableRow (Map props, List cells) {
			this.colInfo = colInfo;
			this.cells = cells;
			this.props = props;
		}
		
		public List getCells() {
			return this.cells;
		}
		
		public Map getProps() {
			return this.props;
		}
		
		public String getProp(String prop) {
			return (String)this.props.get(prop);
		}
/* 		
		public String getSetSpec () {
			return this.colInfo.getProp("setSpec");
		}
		
		public String getFormat () {
			return this.colInfo.getProp("format");
		} */
		
	}
	
	public class DcsTableCell {
		String label;
		String onclick;
		
		public DcsTableCell () {
			this (null, null);
		}			
			
		public DcsTableCell (String label, String onclick) {
			this.label = label;
			this.onclick = onclick;
		}
		
		public String getLabel () {
			return this.label;
		}
		
		public void setLabel (String label) {
			this.label = label;
		}
		
		public String getOnclick () {
			return this.onclick;
		}
		
		public void setOnclick (String onclick) {
			this.onclick = onclick;
		}
	}
	
	public class CollectionInfo {
/* 		String setSpec;
		String name;
		String xmlFormat;
		int numFiles;
		int numNotValid;
		String userRole; */
		DcsSetInfo setInfo;
		Map props;
		
		public CollectionInfo (DcsSetInfo setInfo, Map userRolesMap) {
			this.setInfo = setInfo;
			// this.userRole = userRole;
			this.props = new HashMap();
			this.props.put ("setSpec", this.setInfo.getSetSpec());
			this.props.put ("name", this.setInfo.getName());
			this.props.put ("format", this.setInfo.getFormat());
			this.props.put ("numFiles", String.valueOf(this.setInfo.getNumFiles()));
			this.props.put ("numNotValid", String.valueOf(this.setInfo.getNumNotValid()));
			this.props.put ("authority", this.setInfo.getAuthority());
			this.props.put ("userRole", (String)userRolesMap.get(this.setInfo.getSetSpec()));
		}
		
		public boolean hasProp (String prop) {
			return this.props.containsKey (prop);
		}
		
		public String getProp (String prop) {
			if (this.hasProp (prop))
				return (String)this.props.get(prop);
			else
				return null;
		}
	}
	
	public class DcsTableColumn {
		String headerText;
		String cellText;
		String sortBy;
		int sortOrder = -1;
		String onclick;
		
		public DcsTableColumn (String headerText, String cellText) {
			this(headerText, cellText, null, -1, null);
		}
		
		public DcsTableColumn (String headerText, String cellText, String sortBy, int sortOrder, String onclick) {
			this.headerText = headerText;
			this.cellText = cellText;
			this.sortBy = sortBy;
			this.sortOrder = sortOrder;
			this.onclick = onclick;
		}
		
		public String getHeaderText() {
			return this.headerText;
		}
		
		public String getCellText() {
			return this.cellText;
		}
		
		public String getOnclick() {
			return this.onclick;
		}
		
		public String getSortBy () {
			return this.sortBy;
		}
		
		public int getSortOrder() {
			return this.sortOrder;
		}
	}

	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	protected final void prtln(String s) {
		if (debug) {
			// SchemEditUtils.prtln(s, "DcsTableBean");
			SchemEditUtils.prtln(s, "cb");
		}
	}
}

