package edu.ucar.dls.harvestmanager;

import org.dlese.dpc.datamgr.SimpleDataStore;
import org.dlese.dpc.util.Utils;
import org.dlese.dpc.xml.XMLUtils;
import org.dlese.dpc.util.TimedURLConnection;
import org.dlese.dpc.xml.Dom4jUtils;
import org.dlese.dpc.oai.OAIUtils;
import org.dlese.dpc.util.Files;
import org.dlese.dpc.email.SendEmail;

import org.dom4j.*;
import org.dom4j.tree.*;

import java.util.TimerTask;
import java.util.Timer;
import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.io.File;

/**
 *  Utility methods used in the Harvest Repository Manager application.
 *
 * @author    John Weatherley
 */
public final class HarvestManagerUtils {

	private boolean debug = true;


	/**
	 *  Gets a Date Object from the timestamp sent in the harvest ingest notification. Specifically, converts an
	 *  ISO8601 UTC datastamp String of the form yyyy-MM-ddTHH-mm-ssZ or the short form yyyy-MM-dd to a Java
	 *  Date. See <a href="http://www.w3.org/TR/NOTE-datetime">ISO8601 </a> and <a
	 *  href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Dates"> OAI date info</a> for more
	 *  info. If the short form yyyy-MM-dd is given, this method adds the String t01-00-00z to it to produce an
	 *  ISO8601 compliant date time.
	 *
	 * @param  datestamp           A datestamp in UTC format.
	 * @return                     The dateFromDatestamp value
	 * @exception  ParseException  If unable to interpret the datestamp.
	 */
	public final static Date getDateFromTimestamp(String datestamp)
		 throws ParseException {

		// Since we're using a Date parser, time begins at January 1, 1970, 00:00:00 GMT,
		// We want to return no matches rather than badArgument, so convert the year to 1970.
		int year = 0;
		try {
			year = Integer.parseInt(datestamp.substring(0, 4));
		} catch (Throwable e) {
			throw new ParseException("Year is malformed", 3);
		}
		if (year < 1970)
			datestamp = "1970" + datestamp.substring(4, datestamp.length());

		if (datestamp.length() == 10) {
			datestamp += "t01-00-00z";
		}
		else {
			datestamp = datestamp.toLowerCase();
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd't'HH-mm-ss'z'");
		df.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
		Date date = df.parse(datestamp);
		//prtln("getDateFromTimestamp() returning: " + date.toString());
		return date;
	}


	/**
	 *  Gets a UTC timstamp String that is exactly one month before the time of calling this method.
	 *
	 * @return    The oneMonthAgoTimestamp value
	 */
	public final static String getOneMonthAgoTimestamp() {
		Calendar rightNow = Calendar.getInstance();
		rightNow.add(Calendar.MONTH, -1);
		return OAIUtils.getDatestampFromDate(rightNow.getTime());
	}


	/**
	 *  Returns true if the given harvest trigger Date is more than three days old.
	 *
	 * @param  harvestTriggerDate  The Date the harvest was triggered
	 * @return                     True if more than three days old
	 */
	public final static boolean isHarvestStale(Date harvestTriggerDate) {
		Calendar triggerDate = Calendar.getInstance();
		triggerDate.setTime(harvestTriggerDate);
		triggerDate.add(Calendar.DAY_OF_YEAR, 3);
		Calendar rightNow = Calendar.getInstance();
		return (rightNow.compareTo(triggerDate) > 0);
	}

	//=========== Debugging/logging ===============================================

	/**
	 *  Return a string for the current time and date, sutiable for display in log files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	public static String getDateStamp() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final void prtlnErr(String s) {
		System.err.println(getDateStamp() + " HarvestManagerUtils Error: " + s);
	}



	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final void prtln(String s) {
		if (debug)
			System.out.println(getDateStamp() + " HarvestManagerUtils: " + s);
	}


	/**
	 *  Sets the debug attribute.
	 *
	 * @param  db  The new debug value
	 */
	public final void setDebug(boolean db) {
		debug = db;
	}
}

