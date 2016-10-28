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
package edu.ucar.dls.serviceclients.peopledb;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.MetadataUtils;
import java.util.*;
import java.io.File;

import org.json.JSONObject;
import org.json.JSONArray;

import org.dom4j.*;

/**
 *  Reads an XML document containing people history, and provides ...<p>
 *
 *  The data was created from an iVantage data dump, and correlated with UCAR
 *  upids, which are the keys in the current PeopleDB.
 *
 * @author    Jonathan Ostwald
 */

/**
 *  Stored information about a position
 *
 * @author    Jonathan Ostwald
 */
public class Position {

	/**  NOT YET DOCUMENTED */
	public Date start_date;
	/**  NOT YET DOCUMENTED */
	public Date end_date;
	/**  NOT YET DOCUMENTED */
	public String start;
	/**  NOT YET DOCUMENTED */
	public String end;
	/**  NOT YET DOCUMENTED */
	public String entity;
	/**  NOT YET DOCUMENTED */
	public String lab;
	/**  NOT YET DOCUMENTED */
	public String org;
	/**  NOT YET DOCUMENTED */
	public String divCode;
	/**  NOT YET DOCUMENTED */
	public String divProg;

	String NL = "\n\t";

	boolean initialized = false;


	/**
	 *  Constructor for the Position object
	 *
	 * @param  start   Description of the Parameter
	 * @param  end     Description of the Parameter
	 * @param  entity  Description of the Parameter
	 * @param  lab     Description of the Parameter
	 * @param  org     Description of the Parameter
	 */
	public Position(String start, String end, String entity, String lab, String org) {
		this.start = start;
		this.end = end;
		this.entity = entity;
		this.lab = lab;
		this.org = org;

		this.initDates();
	}


	/**
	 *  Constructor for the Position object
	 *
	 * @param  element  Description of the Parameter
	 */
	public Position(Element element) {
		this.start = element.attributeValue("start");
		this.end = element.attributeValue("end");
		this.entity = element.attributeValue("entity");
		this.lab = element.attributeValue("lab");
		this.org = element.attributeValue("org");
		this.divCode = element.attributeValue("divCode");
		this.divProg = element.attributeValue("divProg");
		this.initDates();
	}


	/**
	 *  Gets the positionJson attribute of the Position object
	 *
	 * @return                The positionJson value
	 * @exception  Exception  Description of the Exception
	 */
	public String getPositionJson() throws Exception {
		throw new Exception("PositionHistory.getPositionJson not implemented");
	}


	/**  Description of the Method  */
	private void initDates() {
		try {
			if (this.end.equals("_")) {
				this.start_date = new Date();
			}
			else {
				this.start_date = PositionHistory.parseDate(this.start);
			}
			this.end_date = PositionHistory.parseDate(this.end);
			this.initialized = true;
		} catch (Exception e) {
			prtln("COULD NOT PROCESS DATE: " + e);
			e.printStackTrace();
		}
	}


	/**
	 *  Gets the key attribute of the Position object
	 *
	 * @return    The key value
	 */
	public String getKey() {
		return this.entity + "_" + this.lab + "_" + this.org;
	}


	/**
	 *  Is other's end date the previous day to this Position's start_date?<p>
	 *
	 *  org_ids AND position_type_id must match for adjacency (and neither should
	 *  be null - these should have been weeded out).
	 *
	 * @param  other  another Position instance
	 * @return        The adjacentTo value
	 */
	public boolean isAdjacentTo(Position other) {

		if (!this.getKey().equals(other.getKey())) {
			// prtln (my_key + " != " + other_key);
			return false;
		}

		Date other_end_date = other.end_date;
		Date my_start_date = this.start_date;

		// prtln ("\n other_end: " + other_end_date + "  my_start: " + my_start_date);

		Calendar threshold = Calendar.getInstance();
		threshold.setTime(other.end_date);
		threshold.add(Calendar.HOUR, 25);

		Calendar myCal = Calendar.getInstance();
		myCal.setTime(this.start_date);

		// prtln ("  threshold: " + threshold.getTime().toString());

		return threshold.after(myCal);
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public String toString() {
		// return "start: " + this.start + ", end: " + this.end + ", key: " + this.getKey();
		return "- " + PositionHistory.iVantageDate(this.start_date) + " - " +
					  PositionHistory.iVantageDate(this.end_date) + " (" + this.getKey() + ")";
	}


	/**
	 *  Prints a String with a trailing newline.
	 *
	 * @param  msg  DESCRIPTION
	 */

	static void prtln(String msg) {
		System.out.println(msg);
	}


	/**
	 *  Gets the dateStamp attribute of the PeopleDBTester class
	 *
	 * @return    The dateStamp value
	 */
	static String getDateStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(new Date());
	}


	/**
	 *  Enable time-based sorting of Position instances
	 *
	 * @author    Jonathan Ostwald
	 */
	public class PositionComparator implements Comparator {

		/**
		 *  Provide comparison for sorting Positions by "start_date" property
		 *
		 * @param  o1  Position 1
		 * @param  o2  Position 2
		 * @return     comparison
		 */
		public int compare(Object o1, Object o2) {
			Date d1;
			Date d2;
			try {
				d1 = ((Position) o1).start_date;
				d2 = ((Position) o2).start_date;
			} catch (Exception e) {
				prtln("Error: unable to compare start_dates: " + e.getMessage());
				return 0;
			}
			return d1.compareTo(d2);
		}
	}

}


