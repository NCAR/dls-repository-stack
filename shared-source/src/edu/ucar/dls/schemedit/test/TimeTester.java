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

package edu.ucar.dls.schemedit.test;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.xml.XPathUtils;
import edu.ucar.dls.util.*;
import edu.ucar.dls.util.strings.*;
import edu.ucar.dls.oai.OAIUtils;

import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.text.*;

/**
 *  Sandbox for working out date and calendar solutions
 *
 *@author    ostwald
 */
public class TimeTester {

	private static boolean debug = true;

	static String dateFormatString = SchemEditUtils.utcDateFormatString;

	// static String myDateFormat = "yyyy-MM-dd";
	static String myDateFormat = "yyyy-MM-dd HH:mm:ss";
	static SimpleDateFormat sdf = new SimpleDateFormat(myDateFormat);
	
	static String iVantageDateFormat = "MM/dd/yy";
	static SimpleDateFormat ivdf = new SimpleDateFormat(iVantageDateFormat);
	
	public TimeTester () {}
	
	static String getUtcTimeString () {
		Date date = SchemEditUtils.getUtcTime();
		return SchemEditUtils.utcDateString (date);
	}

	static String toUtcTime (String dateStr) throws Exception {
		Date date = sdf.parse(dateStr);
		return sdf.format (SchemEditUtils.getUtcTime(date));
	}
	
/* 	int getTimeZoneOffset () {
		return -(Calendar.get(Calendar.ZONE_OFFSET) + Calendar.get(Calendar.DST_OFFSET)) / (60 * 1000);
	} */
	
	static String getDateDisplayCal (Date activityTimeStamp) {
		Calendar calendar = Calendar.getInstance();
		prtln ("calendar intialized at " + calendar.getTime().toString());
		// calendar.setTime(activityTimeStamp);
		calendar.set(Calendar.HOUR, -1);
		
		
		Date oneHourAgo = calendar.getTime();
		if (activityTimeStamp.after(oneHourAgo))
			prtln ("within an hour");
		else
			prtln ("more than an hour ago");
		prtln ("activity time: " + activityTimeStamp.toString());
		prtln ("one hour ago: " + oneHourAgo.toString());
		return activityTimeStamp.toString();
	}
	
	static String getDateDisplay (Date activityTimeStamp) {
		
		long activityTime = activityTimeStamp.getTime();
		long now = new Date().getTime();
		
		long delta = now - activityTime;
		prtln ("delta: " + delta);
		
		// was activity less than a minute ago?
		int second_mills = 1000;
		int minute_mills = 60 * second_mills;
		if (delta < minute_mills) {
			int secs = (int)(delta / second_mills);
			return secs + " seconds ago";
		}
		prtln ("delta is more than a minute");
		int hour_mills = 60 * minute_mills;
		prtln ("hour: " + hour_mills);
		if (delta < hour_mills) {
			int minutes = (int)(delta / minute_mills);
			return minutes + " minutes ago";
		}
		prtln ("more than an hour");
		int day_mills = 24 * hour_mills;
		prtln ("day: " + day_mills);
		if (delta < day_mills) {
			int hours = (int)(delta / hour_mills);
			return hours + " hours ago";
		}
		prtln ("more than a day ...");
		int week_mills = 7 * day_mills;
		prtln ("week: " + week_mills);
		if (delta < week_mills) {
			int days = (int)(delta / day_mills);
			return days + " days ago";
		}
		prtln ("more than a week");
		return null;
	}
	
	
	/**
	 *  The main program for the TimeTester class
	 *
	 *@param  args           The command line arguments
	 *@exception  Exception  Description of the Exception
	 */
	public static void main(String[] args) throws Exception {
		// getDateDisplayTester();
		String dateStr = "2012-04-29T08:26:19Z";
		Date tmp = SchemEditUtils.parseUnionDateType(dateStr);
		Date parsed = SchemEditUtils.getFullDateFormat().parse(dateStr);
		prtln (parsed.toString());

	}
		
	public static void parseUnionDateTypeTester (String[] args) throws Exception {
		String dateStr = "2001";
		prtln (args.length + " args");
		if (args.length > 0)
			dateStr = args[0];
		prtln ("dateStr: " + dateStr);
		prtln ("parsed-1: " + MetadataUtils.parseUnionDateType(dateStr));
		prtln ("parsed-2: " + SchemEditUtils.parseUnionDateType(dateStr));
	}
	
	public static void getDateDisplayTester () {
		prtln ("getDateDisplayTester");
		prtln ("\nNOW: " + new Date().toString());
		// Date activityTime = MetadataUtils.parseDate("2012-05-27T17:19:00Z");
		Date activityTime = new Date(new Date().getTime() - (61 * 1000 * 60 * 24 * 7));
		prtln ("activityTime: " + activityTime.toString());
		String display = getDateDisplay (activityTime);
		if (display == null) {
			// Feb 25, 2010
			display = "could not compute display";
		}
		prtln ("display: " + display);
	}
	
	static void calendarPlay () throws Exception {
		
		Date hourAgoBase = MetadataUtils.parseDate("2012-05-27T00:00:00Z");
		prtln ("sanity check: " + hourAgoBase.toString());
		
		Calendar hourAgo = Calendar.getInstance();
		prtln ("");
		int timeZoneOffset = -(hourAgo.get(Calendar.ZONE_OFFSET) + hourAgo.get(Calendar.DST_OFFSET)) / (60 * 1000);
		prtln ("timeZoneOffset: " + timeZoneOffset);
		
		
		prtln ("calendar timezone: " + hourAgo.getTimeZone().toString());
		hourAgo.setTime (hourAgoBase);
		prtln ("hour ago BASE: " + hourAgo.getTime().toString());
		hourAgo.add(Calendar.HOUR, -1);
		prtln ("hour ago: " + hourAgo.getTime().toString());
	}
	
	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println(s);
		}
	}
}


