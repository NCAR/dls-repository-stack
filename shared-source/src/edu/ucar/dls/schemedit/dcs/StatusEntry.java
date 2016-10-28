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
package edu.ucar.dls.schemedit.dcs;
import edu.ucar.dls.util.MetadataUtils;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.Constants;
import edu.ucar.dls.schemedit.config.StatusFlags;
import edu.ucar.dls.xml.Dom4jUtils;

import org.dom4j.*;
import java.util.*;
import java.text.*;

/**
 *  Wrapper for a StatusEntry element of a {@link edu.ucar.dls.schemedit.dcs.DcsDataRecord}.
 *  <p>
 *
 *  Contains the following elements:
 *  <ul>
 *    <li> editor - the person (if known) that created the statusEntry</li>
 *    <li> status - the status flag (e.g., "Done"). </li>
 *    <li> statusNote - an explanation for the assigned status</li>
 *    <li> changeDate - a timestamp for creation of the StatusEntry</li>
 *  </ul>
 *
 *
 * @author    ostwald
 */
public class StatusEntry {
	// private String status, statusNote, changeDate;
	protected Element entryElement;


	/**  Constructor for the StatusEntry object */
	public StatusEntry() {
		this(null);
	}


	/**
	 *  Constructor for the StatusEntry object given parameters as Strings.
	 *
	 * @param  status      status label
	 * @param  statusNote  note describing status change
	 * @param  editor      who made the change
	 */
	public StatusEntry(String status, String statusNote, String editor) {
		this(status, statusNote, editor, SchemEditUtils.fullDateString(new Date()));
	}


	/**
	 *  Constructor for the StatusEntry object given parameters as Strings.
	 *
	 * @param  status      status label
	 * @param  statusNote  note describing status change
	 * @param  editor      who made the change
	 * @param  changeDate  textual representation of date
	 */
	public StatusEntry(String status, String statusNote, String editor, String changeDate) {
		this(null);
		if (status == null || status.trim().length() == 0)
			status = StatusFlags.UNKNOWN_STATUS;
		try {
			setStatus(status);
			setStatusNote(statusNote);
			setEditor(editor);
			setChangeDate(changeDate);
		} catch (Throwable t) {
			prtln("error setting values for new HistoryElement: " + t.getMessage());
		}
	}


	/**
	 *  Constructor for the StatusEntry object given a {@link org.dom4j.Element}
	 *  containing the values for statusEntry attributes.
	 *
	 * @param  entryElement  Description of the Parameter
	 */
	public StatusEntry(Element entryElement) {
		if (entryElement == null) {
			entryElement = DocumentHelper.createElement("statusEntry");
			Element status = entryElement.addElement("status");
			status.setText("Not yet processed");
			Element statusNote = entryElement.addElement("statusNote");
			statusNote.setText("");
			Element editor = entryElement.addElement("editor");
			editor.setText("");
			Element changeDate = entryElement.addElement("changeDate");
			changeDate.setText("");
		}
		this.entryElement = entryElement;
	}


	/**
	 *  Returns clone of the entryElement attribute of the StatusEntry object.
	 *
	 * @return    The element value
	 */
	public Element getElement() {
		return (Element) entryElement.clone();
	}


	/**
	 *  Gets the status attribute of the StatusEntry object
	 *
	 * @return                The status value
	 * @exception  Exception  if the entryElement does not have an "status" element
	 */
	public String getStatus()
		 throws Exception {
		return entryElement.element("status").getText();
	}


	/**
	 *  Sets the status attribute of the StatusEntry object
	 *
	 * @param  s              The new status value
	 * @exception  Exception  Description of the Exception
	 */
	public void setStatus(String s)
		 throws Exception {
		if (s == null)
			s = "";
		entryElement.element("status").setText(s);
	}


	/**
	 *  Gets the statusNote attribute of the StatusEntry object
	 *
	 * @return                The statusNote value
	 * @exception  Exception  if the entryElement does not have an "statusNote"
	 *      element
	 */
	public String getStatusNote()
		 throws Exception {
		return entryElement.element("statusNote").getText();
	}


	/**
	 *  Sets the statusNote attribute of the StatusEntry object
	 *
	 * @param  s              The new statusNote value
	 * @exception  Exception  Description of the Exception
	 */
	public void setStatusNote(String s)
		 throws Exception {
		if (s == null) s = "";
		entryElement.element("statusNote").setText(s);
	}


	/**
	 *  Gets the editor attribute of the StatusEntry object
	 *
	 * @return                The editor value
	 * @exception  Exception  if the entryElement does not have an "editor" element
	 */
	public String getEditor()
		 throws Exception {
		return entryElement.element("editor").getText();
	}


	/**
	 *  Sets the editor attribute of the StatusEntry object
	 *
	 * @param  s              The new editor value
	 * @exception  Exception  Description of the Exception
	 */
	public void setEditor(String s)
		 throws Exception {
		if (s == null) s = Constants.UNKNOWN_EDITOR;
		entryElement.element("editor").setText(s);
	}


	/**
	 *  Gets the changeDate attribute of the StatusEntry object
	 *
	 * @return                The changeDate value
	 * @exception  Exception  Description of the Exception
	 */
	public String getChangeDate()
		 throws Exception {
		return entryElement.element("changeDate").getText();
	}


	/**
	 *  Sets the changeDate attribute of the StatusEntry object
	 *
	 * @param  s              The new changeDate value
	 * @exception  Exception  Description of the Exception
	 */
	public void setChangeDate(String s)
		 throws Exception {
		entryElement.element("changeDate").setText(s);
	}


	/**
	 *  Return the changeDate attribute as a Date. If the changeDate attribute has
	 *  not been set, then set it as "now" and return the corresponding date
	 *  object.
	 *
	 * @return    The date value
	 */
	public Date getDate() {
		String changeDate = "";
		// make sure the changeDate is of the right form by first checking that it can be parsed
		// by parseUnionDateType, and then convert the changeDate to a fullDateFormatted date object
		try {
			changeDate = getChangeDate();
			Date tmp = SchemEditUtils.parseUnionDateType(changeDate);
			return SchemEditUtils.getFullDateFormat().parse(changeDate);
		} catch (Exception e) {
			prtln("StatusEntry.getDate (" + changeDate + ") error:" + e.getMessage());
			prtln(Dom4jUtils.prettyPrint(this.entryElement));
			e.printStackTrace();
			Date defaultDate = new Date(0);
			try {
				this.setChangeDate(SchemEditUtils.fullDateString(defaultDate));
			} catch (Exception e2) {
				prtln("StatusEntry.getDate Unable to set change date to \"defaultDate\": " + e2.getMessage());
			}
			return defaultDate;
		}
	}


	/**  Description of the Method */
	public void printEntry() {
		try {
			prtln("    status: " + getStatus());
			prtln("    statusNote: " + getStatusNote());
			prtln("    editor: " + getEditor());
			prtln("    changeDate: " + getChangeDate());
		} catch (Throwable t) {
			prtln("printEntry error: " + t.getMessage());
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		SchemEditUtils.prtln(s, "StatusEntry");
	}
}

