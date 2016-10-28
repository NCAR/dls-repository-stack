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

package edu.ucar.dls.schemedit.threadedservices;

import java.util.*;
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.dcs.*;
import edu.ucar.dls.util.Utils;
import org.dom4j.*;

public class ExportReport extends Report {
	
	protected static boolean debug = true;
	// private HashMap props;
	
	public ExportReport (DcsSetInfo dcsSetInfo, String [] statuses) {
		super (dcsSetInfo, statuses);
	}

	public ExportReport (String name, String collection) {
	    super (name, collection);
	}

	public String getNumToExport () {
		return getProp ("numToExport");
	}
	
	public String getNumExported () {
		return getProp ("numExported");
	}
	
	public String getNumNotExported () {
		return getProp ("numNotExported");
	}
	
	public String getNumDeleted () {
		return getProp ("numDeleted");
	}
	
	public String getSummary () {
		String s = "\nSummary";
		s += "\n\t" + recordsProcessed + " records processed";
		if (processingTime > 0)
			s += " in " + Utils.convertMillisecondsToTime (processingTime);
		s += "\n\t" + getInvalidRecCount() + " were invalid";
		return s;
	}
	
	public String details () {
		String s =  "\n\nDetailed Report";
		for (Iterator i = reportEntries.iterator();i.hasNext();) {
			ReportEntry entry = (ReportEntry)i.next();
			// s += "\n" + entry.id;
		}
		return s;
	}
	
	public String getReport () {
		String s = "Report: " + name;
		s += "\n\tCollection key: " + collection;
		s += getSummary();
		s += details();
		return s;
	}

}
	
