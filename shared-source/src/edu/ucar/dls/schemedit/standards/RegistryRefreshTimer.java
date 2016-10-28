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
package edu.ucar.dls.schemedit.standards;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.util.*;
import edu.ucar.dls.util.strings.FindAndReplace;
import edu.ucar.dls.schemedit.standards.StandardsRegistry;

import java.util.*;
import java.text.*;
import java.io.*;
import java.net.*;

import javax.servlet.ServletContext;

import org.dom4j.Document;


/**
*  Timer for automatically refreshing the {@link StandardsRegistry}.
 *
 * @author    ostwald <p>
 *
 *
 */
public class RegistryRefreshTimer {

	static boolean debug = true;
	ServletContext servletContext;

	private int autoRefreshFrequency = 0;
	private Timer autoRefreshTimer;
	private String autoRefreshStartTime = null;
	private Date autoRefreshStartTimeDate = null;

	public static void main (String[] args) throws Exception {
		File libDir = new File ("/Users/ostwald/tmp/standards_documents/");
		StandardsRegistry.setLibraryDir(libDir);
		StandardsRegistry.getInstance().load(libDir);
		RegistryRefreshTimer timer = new RegistryRefreshTimer(null);
		timer.startRegistryRefreshTimer(30, null);
		prtln ("main is sleeping");
		Thread.sleep(30000);
		prtln ("main has awaken");
	}
	

	/**
	 *  Constructor for the RegistryRefreshTimer object, requiring ServletContext.
	 *
	 * @param  servletContext
	 * @exception  Exception   if repositoryWriter or autoRefresh service cannot be
	 *      initialized.
	 */
	public RegistryRefreshTimer(ServletContext servletContext) throws Exception {
		this.servletContext = servletContext;
		init();
	}


	/**
	 *  Initialize the autoRefresh service with init params from servletContext. 
	 */
	private void init() {
		
		// Gets startTime from servletContext
 		String autoRefreshStartTime = null;
 		if (servletContext != null) {
 			autoRefreshStartTime = (String) servletContext.getInitParameter("autoRefreshStartTime");
			if (autoRefreshStartTime == null || autoRefreshStartTime.equalsIgnoreCase("disabled")) {
					autoRefreshStartTime = null;
					prtln("AutoExport task is not scheduled");
					return;
				}
		}

		long hours24 = 60 * 60 * 24;

		// Start the indexer timer
		if (autoRefreshStartTime != null) {
			Date startTime = null;
			try {
				Date currentTime = new Date(System.currentTimeMillis());
				//Date oneHourFromNow = new Date(System.currentTimeMillis() + (1000 * 60 * 60));
				Date oneHourFromNow = new Date(System.currentTimeMillis() + (1000 * 60));
				// One minute from now instead...
				int dayInYear = Integer.parseInt(Utils.convertDateToString(currentTime, "D"));
				int year = Integer.parseInt(Utils.convertDateToString(currentTime, "yyyy"));

				startTime = Utils.convertStringToDate(year + " " + dayInYear + " " + autoRefreshStartTime, "yyyy D H:mm");

				// Make sure the timer starts one hour from now or later
				if (startTime.compareTo(oneHourFromNow) < 0) {
					if (dayInYear == 365) {
						year++;
						dayInYear = 1;
					}
					else
						dayInYear++;
					startTime = Utils.convertStringToDate(year + " " + dayInYear + " " + autoRefreshStartTime, "yyyy D H:mm");
				}
				startRegistryRefreshTimer(hours24, startTime); // every 24 hours by default
				// startRegistryRefreshTimer(30, startTime); // refresh every 30 secs
			} catch (ParseException e) {
				prtlnErr("Syntax error in context parameter 'autoRefreshStartTime.' AutoExport timer not started: " + e.getMessage());
			}
		}
		else
			startRegistryRefreshTimer(autoRefreshFrequency, null);
	}


	/**
	 *  Start or restarts the timer thread with the given update frequency. Same
	 *  as {@link #changeautoRefreshFrequency(long autoRefreshFrequency)}.
	 *
	 * @param  autoRefreshFrequency  The number of seconds between updates.
	 */
	private void startRegistryRefreshTimer(long autoRefreshFrequency) {
		startRegistryRefreshTimer(autoRefreshFrequency, null);
	}


	/**
	 *  Start or restarts the timer thread with the given update frequency,
	 *  beginning at the specified time/date. Use this method to schedule the timer
	 *  to run as a nightly cron, beginning at the time you wish the indexer to
	 *  run.
	 *
	 * @param  autoRefreshFrequency  The number of seconds between index updates.
	 * @param  startTime            The time at which start the indexing process.
	 */
	private void startRegistryRefreshTimer(long autoRefreshFrequency, Date startTime) {
		// Make sure the indexing timer is stopped before starting...
		stopRegistryRefreshTimer();

/* 		prtln("startRegistryRefreshTimer()");
		prtln (" - autoRefreshFrequency: " + autoRefreshFrequency);
		prtln (" - startTime: " + startTime); */

		this.autoRefreshStartTimeDate = startTime;

		if (autoRefreshFrequency > 0) {
			if (autoRefreshTimer != null)
				autoRefreshTimer.cancel();

			// Set daemon to true
			autoRefreshTimer = new Timer(true);

			// Convert seconds to milliseeconds
			long freq = ((autoRefreshFrequency > 0) ? autoRefreshFrequency * 1000 : 60000);

			// Start the indexer at regular intervals beginning at the specified Date/Time
			if (startTime != null) {
				try {
					prtln("autoRefresh timer is scheduled to start " +
						Utils.convertDateToString(startTime, "EEE, MMM d, yyyy h:mm a zzz") +
						", and run every " + Utils.convertMillisecondsToTime(freq) + ".");
				} catch (ParseException e) {}

				autoRefreshTimer.scheduleAtFixedRate(new RefreshTask(), startTime, freq);
			}
			// Start the indexer at regular intervals beginning after 6 seconds
			else {
				prtln("autoRefresh timer is scheduled to run every " +
						Utils.convertMillisecondsToTime(freq) + ".");
				autoRefreshTimer.schedule(new RefreshTask(), 6000, freq);
			}
		}
	}

	public class RefreshTask extends TimerTask {
		// Main processing method for this thread.
		
		public RefreshTask () {
			super();
		}
			
		
		public void run() {
			prtln ("\n ===============================");
			prtln("run()");
			try {
				StandardsRegistry.getInstance().refresh();
			} catch (Throwable t) {
				prtln ("ERROR: " + t.getMessage());
			}
			prtln ("RefreshTask completed\n");
	
		}
	}


	/**  Stops the indexing timer thread. */
	private void stopRegistryRefreshTimer() {
		if (autoRefreshTimer != null) {
			autoRefreshTimer.cancel();
			prtln("AutoRefresh timer stopped");
		}
	}

	// ----------------- util ---------------------

	/**
	 *  Gets the dateString attribute of the RegistryRefreshTimer class
	 *
	 * @return    The dateString value
	 */
	public static String getDateString() {
		return SchemEditUtils.fullDateString(new Date());
	}


	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "RegistryRefreshTimer");
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	static void prtlnErr(String s) {
		SchemEditUtils.prtln(s, "RegistryRefreshTimer");
	}
}

