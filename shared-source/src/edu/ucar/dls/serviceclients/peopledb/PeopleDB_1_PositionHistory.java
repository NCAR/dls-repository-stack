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
package edu.ucar.dls.serviceclients.peopledb;

import java.sql.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;

import org.json.JSONObject;
import org.json.JSONArray;

import edu.ucar.dls.services.mmd.DbConn;
import edu.ucar.dls.services.mmd.MmdException;
import edu.ucar.dls.util.MetadataUtils;

/**
* Extracts Position History information from PeopleDB 1.0 via mysql
*
 * @author    Jonathan Ostwald
 */

public class PeopleDB_1_PositionHistory {

	String dbUrl = null;
	String printableUrl = null;
	DbConn dbconn = null;
	int bugs = 0;
	List positions = null;
	JSONObject json = null;


	/**
	 *  Constructor for the PeopleDB_1_PositionHistory object with provided upid
	 *
	 * @param  upid  UCAR People ID (e.g., 2457)
	 */
	public PeopleDB_1_PositionHistory(String upid) {
		try {
			openDB();

			List all_positions = getRawPeopleDB_1_PositionHistory(upid);
			positions = prunePeopleDB_1_PositionHistory(all_positions);

			json = this.positionHistoryToJson(positions);
			// prtln(json.toString(2));

			closeDB();
		} catch (Exception e) {
			prtln("ERROR: " + e);
		}
	}


	/**
	 *  Gets the history as a json string
	 *
	 * @return                The historyJson value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public String getHistoryJson() throws Exception {
		return this.json.toString(2);
	}


	/**
	 *  Obtains information about an organization (org_id) from the "organization" table.
	 Org: org_id, acronym, full_name
	 *
	 * @param  org_id            organization ID
	 * @return                   The organization for org_id
	 * @exception  MmdException  NOT YET DOCUMENTED
	 */
	Organization getOrganization(String org_id) throws MmdException {
		// query the "position_history" table for upid
		Object[][] dbmat;

		dbmat = dbconn.getDbTable(
			"SELECT org_id, acronym, full_name FROM organization"
			 + " WHERE org_id = " + org_id,
			new String[]{"string", "string", "string"}, false);
		if (dbmat.length > 0)
			return new Organization(dbmat[0]);
		else
			return null;
	}


	/**
	 *  returns a list of Position instances for provided upid
	 *
	 * @param  upid              identifies a person (e.g., 3458)
	 * @return                   The positionHistory value
	 * @exception  MmdException  trouble communicating with database
	 */
	List getRawPeopleDB_1_PositionHistory(String upid) throws MmdException {
		// query the "position_history" table for upid
		Object[][] dbmat;

		dbmat = dbconn.getDbTable(
			"SELECT org_id, start_date, end_date, position_type_id   FROM position_history"
			 + " WHERE upid = " + upid,
			new String[]{"string", "string", "string", "string"}, true);

		List results = new LinkedList();
		for (int i = 0; i < dbmat.length; i++) {
			Position pos = new Position(dbmat[i]);
			if (pos.org_id != null || pos.isVisitor())
				results.add(pos);
		}
		return results;
	}


	/**
	 *  Converts a list of Position instances into a Json Object
	 *
	 * @param  positionList  list of Positions to convert
	 * @return               trouble contructing json
	 */
	JSONObject positionHistoryToJson(List positionList) {
		JSONObject json = new JSONObject();
		JSONArray positions = new JSONArray();
		try {
			json.put("positions", positions);
		} catch (Exception e) {
			prtln("couldnt put array: " + e.getMessage());
		}
		for (Iterator i = positionList.iterator(); i.hasNext(); ) {
			Position pos = (Position) i.next();
			try {
				// json.append ("positions", new JSONObject(pos.asMap()));
				json.append("positions", pos.asJson());
			} catch (Exception e) {
				prtln("Whoops: " + e.getMessage());
			}
		}
		return json;
	}


	/**
	 *  collapse list of Position by merging chronologically adjacent positions
	 *
	 * @param  positions  list of position Instances
	 * @return            NOT YET DOCUMENTED
	 */
	List prunePeopleDB_1_PositionHistory(List positions) {
		List pruned = new LinkedList();
		if (positions == null || positions.isEmpty())
			return pruned;
		Collections.sort(positions, new PositionComparator());
		String start = null;
		String org_id = null;

		Position prev = null;
		for (Iterator i = positions.iterator(); i.hasNext(); ) {
			Position pos = (Position) i.next();

			if (prev == null) {
				prev = pos;
				continue;
			}
			if (pos.isAdjacentTo(prev)) {
				prev = new Position(prev.org_id, prev.start_date, pos.end_date, prev.position_type_id);
			}
			else {
				pruned.add(prev);
				prev = pos;
			}
		}
		if (prev != null) {
			pruned.add(prev);
		}
		return pruned;
	}


	/**
	 *  Encapsulates information about an Organization
	 *
	 * @author    Jonathan Ostwald
	 */
	public class Organization {
		/**  NOT YET DOCUMENTED */
		public String org_id;
		/**  NOT YET DOCUMENTED */
		public String acronym;
		/**  NOT YET DOCUMENTED */
		public String full_name;


		/**
		 *  Constructor for the Organization object
		 *
		 * @param  data  NOT YET DOCUMENTED
		 */
		public Organization(Object[] data) {
			this.org_id = (String) data[0];
			this.acronym = (String) data[1];
			this.full_name = (String) data[2];
		}


		/**
		 *  NOT YET DOCUMENTED
		 *
		 * @return    NOT YET DOCUMENTED
		 */
		public String toString() {
			return acronym + " (" + full_name + ")";
		}
	}


	/**
	 *  Stored information about a position
	 *
	 * @author    Jonathan Ostwald
	 */
	public class Position {
		/**  NOT YET DOCUMENTED */
		public String start_date;
		/**  NOT YET DOCUMENTED */
		public String end_date;
		/**  NOT YET DOCUMENTED */
		public String org_id;
		/**  NOT YET DOCUMENTED */
		public String position_type_id; // 1 is visitor, 2 is employee
		/**  NOT YET DOCUMENTED */
		public Organization organization = null;
		String NL = "\n\t";


		/**
		 *  Constructor for the Position object
		 *
		 * @param  org_id            NOT YET DOCUMENTED
		 * @param  start_date        NOT YET DOCUMENTED
		 * @param  end_date          NOT YET DOCUMENTED
		 * @param  position_type_id  NOT YET DOCUMENTED
		 */
		public Position(String org_id, String start_date, String end_date, String position_type_id) {
			this(new String[]{org_id, start_date, end_date, position_type_id});
		}


		/**
		 *  Constructor for the Position object
		 *
		 * @param  data  NOT YET DOCUMENTED
		 */
		public Position(Object[] data) {
			this.org_id = (String) data[0];
			this.start_date = (String) data[1];
			this.end_date = (String) data[2];
			this.position_type_id = (String) data[3];
			if (this.org_id != null)
				try {
					this.organization = getOrganization(org_id);
				} catch (Exception e) {}
		}


		/**
		 *  for use in json, emulate (part of) peopleDB 1.0 API (see https://wiki.ucar.edu/display/weg/Get+Internal+Staff+Detail)
		 *
		 * @return    NOT YET DOCUMENTED
		 */
		public Map asMap() {
			Map map = new HashMap();
			// map.put ("org_id", org_id);
			map.put("startDate", start_date);
			map.put("endDate", end_date);
			if (this.organization != null)
				map.put("organization", this.organization.acronym);
			map.put("type", (this.isVisitor() ? "Visitor" : "Employee"));
			// return new JSONObject (map);
			return map;
		}


		/**
		 *  Produces a Json representation of this Position
		 *
		 * @return    NOT YET DOCUMENTED
		 */
		public JSONObject asJson() {
			return new JSONObject(this.asMap());
		}


		/**
		 *  Gets the visitor attribute of the Position object
		 *
		 * @return    The visitor value
		 */
		public boolean isVisitor() {
			return "1".equals(position_type_id);
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

			String my_key = (this.org_id != null ? this.org_id : "") + "_" + this.position_type_id;
			String other_key = (other.org_id != null ? other.org_id : "") + "_" + other.position_type_id;

			if (!my_key.equals(other_key)) {
				// prtln (my_key + " != " + other_key);
				return false;
			}

			Date other_end_date = null;
			Date my_start_date = null;
			try {
				other_end_date = MetadataUtils.parseDate(other.end_date);
				my_start_date = MetadataUtils.parseDate(this.start_date);
			} catch (Exception e) {
				prtln("could not compare dates for adjacency: " + e.getMessage());
				return false;
			}

			// prtln ("\n other_end: " + other_end_date + "  my_start: " + my_start_date);

			Calendar threshold = Calendar.getInstance();
			threshold.setTime(other_end_date);
			threshold.add(Calendar.HOUR, 25);

			Calendar myCal = Calendar.getInstance();
			myCal.setTime(my_start_date);

			// prtln ("  threshold: " + threshold.getTime().toString());

			return threshold.after(myCal);
		}


		/**
		 *  NOT YET DOCUMENTED
		 *
		 * @return    NOT YET DOCUMENTED
		 */
		public String toString() {
			String s = "";
			if (this.organization != null)
				s += this.organization.toString();
			else
				s += "org_id: " + org_id;
			if (this.isVisitor())
				s += "  Visitor";
			s += NL + "start_date: " + start_date;
			s += NL + "end_date: " + end_date;
			return s;
		}
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
	 *  Simply wraps Exception
	 *
	 * @author    Jonathan Ostwald
	 */
	public class PeopleDBTesterException extends Exception {

		/**
		 *  Constructor for the PeopleDBTesterException object
		 *
		 * @param  msg  NOT YET DOCUMENTED
		 */
		public PeopleDBTesterException(String msg) {
			super(msg);
		}
	}


	/**
	 *  Enable time-based sorting of Position instances
	 *
	 * @author    Jonathan Ostwald
	 */
	public class PositionComparator implements Comparator {

		/**
		 *  Provide comparison for sorting Positions by "start_date"
		 *  property
		 *
		 * @param  o1  Position 1
		 * @param  o2  Position 2
		 * @return     comparison
		 */
		public int compare(Object o1, Object o2) {
			String d1;
			String d2;
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


	/**
	 *  Opens the SQL database.
	 *
	 * @exception  PeopleDBTesterException
	 */
	private void openDB()
		 throws PeopleDBTesterException {

		String dbClass = "com.mysql.jdbc.Driver";
		String dbHost = "merapisql.ucar.edu";
		int dbPort = 3306;

		String dbName = "people";

		String dbUser = "ostwald";
		String dbPassword = "Jbjbjatw2009";

		// Get a DB connection
		try {
			Class.forName(dbClass).newInstance();
		} catch (ClassNotFoundException cnf) {
			mkerror("db driver not found.  Insure \""
				 + dbClass + "\" is in the CLASSPATH.  exc: " + cnf);
		} catch (InstantiationException iex) {
			mkerror("db driver not found.  Insure \""
				 + dbClass + "\" is in the CLASSPATH.  exc: " + iex);
		} catch (IllegalAccessException iex) {
			mkerror("db driver not found.  Insure \""
				 + dbClass + "\" is in the CLASSPATH.  exc: " + iex);
		}
		dbUrl = "jdbc:mysql://" + dbHost + ":" + dbPort
			 + "/" + dbName
			 + "?autoReconnect=true&user=" + dbUser
		// + "?user=" + dbUser
			 + "&password=";
		// all except password, for err msgs
		printableUrl = dbUrl + "(omitted)";
		dbUrl += dbPassword;

		dbconn = null;
		try {
			dbconn = new DbConn(bugs, dbUrl);
		} catch (MmdException mde) {
			mkerror("could not open db connection to URL \""
				 + printableUrl + "\"  exc: " + mde);
		}
		if (bugs >= 1) {
			prtln("openDB: opened: " + printableUrl);
		}
		// prtln("openDB: opened:\n\t" + printableUrl);
	}


	/**
	 *  Closes the database connection. After close, this Query object is useless.
	 *
	 * @exception  PeopleDBTesterException  NOT YET DOCUMENTED
	 */
	public void closeDB()
		 throws PeopleDBTesterException {
		if (dbconn != null) {
			try {
				dbconn.closeDb();
			} catch (Exception e) {
				throw new PeopleDBTesterException(e.getMessage());
			}
			dbconn = null;
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  dbmat  NOT YET DOCUMENTED
	 */
	void printDbMat(Object[][] dbmat) {
		prtln("dbmat.length: " + dbmat.length);

		// print out the rows (all values are assumed to be strings);
		for (int i = 0; i < dbmat.length; i++) {
			Object[] row = dbmat[i];
			String rowStr = "";
			for (int j = 0; j < row.length; j++) {
				rowStr += row[j];
				if (j < row.length - 1)
					rowStr += '\t';
			}
			prtln(rowStr);
		}
	}


	/**
	 *  Gets the dbTableTester attribute of the PeopleDBTester object
	 *
	 * @param  upid              NOT YET DOCUMENTED
	 * @return                   The dbTableTester value
	 * @exception  MmdException  NOT YET DOCUMENTED
	 */
	Object[][] getDbTableTester(String upid) throws MmdException {
		// query the "position_history" table for upid
		Object[][] dbmat;

		dbmat = dbconn.getDbTable(
			"SELECT position_history_id FROM position_history"
			 + " WHERE upid = " + upid,
			new String[]{"string"}, false);

		return dbmat;
	}


	/**
	 *  Gets the dbStringTester attribute of the PeopleDBTester object
	 *
	 * @param  upid              NOT YET DOCUMENTED
	 * @return                   The dbStringTester value
	 * @exception  MmdException  NOT YET DOCUMENTED
	 */
	String getDbStringTester(String upid) throws MmdException {
		// query the "position_history" table for upid
		String res = dbconn.getDbString(
			"SELECT position_history_id FROM position_history"
			 + " WHERE upid = " + upid);
		return res;
	}


	/**
	 *  Simply throws an PeopleDBTesterException.
	 *
	 * @param  msg                          DESCRIPTION
	 * @exception  PeopleDBTesterException  DESCRIPTION
	 */

	void mkerror(String msg)
		 throws PeopleDBTesterException {
		throw new PeopleDBTesterException(msg);
	}

}


