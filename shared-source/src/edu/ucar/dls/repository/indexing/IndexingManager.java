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
package edu.ucar.dls.repository.indexing;

import edu.ucar.dls.repository.*;
import edu.ucar.dls.util.Utils;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import java.text.*;

/**
 *  Manages indexing processes from external sources to RepositoryManager that implement the Interface.
 *
 * @author    John Weatherley
 */
public class IndexingManager {
	private static boolean debug = true;

	/**  Message type used in #addIndexingMessage to post information. */
	public final static int MSG_TYPE_INFO = 0;
	/**  Message type used in #addIndexingMessage to indicate an error.  */
	public final static int MSG_TYPE_ERROR = 1;
	/**  Message type used in #addIndexingMessage to indicate a warning.  */
	public final static int MSG_TYPE_WARNING = 2;
	
	private final static int NUM_INDEXING_MESSAGES = 750;

	private CollectionIndexer collectionIndexer = null;
	private RepositoryManager repositoryManager = null;
	private Maintenance repositoryMaintenance = null;

	private List itemIndexers = new ArrayList();
	private ArrayList indexingMessages = new ArrayList(NUM_INDEXING_MESSAGES);

	private Timer _indexingTimer = null;
	private int[] _indexingDaysOfWeek = null;
	private Date _indexingStartTimeDate = null;

	private File itemIndexerPersistentDataDir = null;
	private Logger logger = null;
	private FileHandler fileHandler = null;

	private boolean _doPerformContinuousIndexing = false;


	/**
	 *  Constructor for the IndexingManager object -- refactor to be a factory...
	 *
	 * @param  repositoryManager  The RepositoryManager
	 */
	public IndexingManager(RepositoryManager repositoryManager, Maintenance repositoryMaintenance) {
        prtln("IndexingManager: New IndexingManager (constructor)");


		collectionIndexer = new CollectionIndexer(repositoryManager, this);
		this.repositoryManager = repositoryManager;
		this.repositoryMaintenance = repositoryMaintenance;
		try {
			this.itemIndexerPersistentDataDir = repositoryManager.getItemIndexerPersistentDataDir();
			File loggingDir = new File(itemIndexerPersistentDataDir, "logs");
			loggingDir.mkdirs();
			File myLoggingFile = new File(loggingDir, "indexing_log.txt");
			logger = Logger.getLogger(this.getClass().getName());
			fileHandler = new FileHandler(myLoggingFile.getAbsolutePath(), true);
			fileHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(fileHandler);
		} catch (Throwable t) {
			prtlnErr("Error IndexingManager(): " + t);
		}
		try {
			addIndexingMessage("Indexing Manager started (Repository data location: " + repositoryManager.getRepositoryDataDir() + ")",MSG_TYPE_INFO);
		} catch (Throwable t) {}
	}


	/**  Commit the index to the repository. If background indexing, the index will be swopped in, otherwise, nothing extra is done for inline indexing. */
	public void commitIndex() {
		try {
			repositoryMaintenance.commitBackgroundIndex();
			
		} catch (Exception e) {
			for(StackTraceElement s: e.getStackTrace())
				logger.info(s.toString());
			addIndexingMessage(e.getMessage(), 4);
			prtlnErr("commitIndex(): " + e);
		}
	}


	/**
	 *  Adds an event handler that performs the indexing actions, sets the config and persistance directories and
	 *  fires the configure and init event.
	 *
	 * @param  itemIndexerClassName  An event handler that implements the ItemIndexer Interface.
	 * @exception  Exception         If error
	 */
	public void addIndexingEventHandler(String itemIndexerClassName) throws Exception {
		Class ihClass = Class.forName(itemIndexerClassName);
		if (itemIndexers == null)
			return;
		synchronized (itemIndexers) {
			ItemIndexer itemIndexer = (ItemIndexer) ihClass.newInstance();
			itemIndexers.add(itemIndexer);
			itemIndexer.setConfigDirectory(repositoryManager.getItemIndexerConfigDir(), itemIndexerPersistentDataDir);
			fireConfigureAndInitializeEvent(itemIndexerClassName);
		}
	}


	/**
	 *  Fire this event to indicate to watchers that the indexer is ready to accept indexing calls.
	 *
	 * @param  itemIndexerClassName  An ItemIndexer to fire the event on, or null to fire the event on all
	 *      handlers
	 * @exception  Exception         If error
	 */
	public void fireIndexerReadyEvent(String itemIndexerClassName) throws Exception {
		IndexingEvent indexingEvent = new IndexingEvent(IndexingEvent.INDEXER_READY, null, collectionIndexer);
		fireEvent(indexingEvent, itemIndexerClassName);
	}


	/**
	 *  Fire this event to indicate to watchers that they should update their collections using
	 *  CollectionIndexer.putCollection() and CollectionIndexer.deleteCollection().
	 *
	 * @param  itemIndexerClassName  An ItemIndexer to fire the event on, or null to fire the event on all
	 *      handlers
	 * @exception  Exception         If error
	 */
	public void fireUpdateCollectionsEvent(String itemIndexerClassName) throws Exception {
		IndexingEvent indexingEvent = new IndexingEvent(IndexingEvent.UPDATE_COLLECTIONS, null, collectionIndexer);
		fireEvent(indexingEvent, itemIndexerClassName);
	}


	/**
	 *  Fire this event to indicate to watchers that they should perform indexing for the given collection if
	 *  they are managing it using {@link CollectionIndexer#putRecord}, {@link CollectionIndexer#deleteRecord},
	 *  {@link CollectionIndexer#deletePreviousSessionRecords}.
	 *
	 * @param  collectionKey         The collection key that should be indexed
	 * @param  itemIndexerClassName  An ItemIndexer to fire the event on, or null to fire the event on all
	 *      handlers
	 * @exception  Exception         If error
	 */
	public void fireIndexCollectionEvent(String collectionKey, String itemIndexerClassName) throws Exception {
		IndexingEvent indexingEvent = new IndexingEvent(IndexingEvent.BEGIN_INDEXING_COLLECTION, collectionKey, collectionIndexer);
		fireEvent(indexingEvent, itemIndexerClassName);
	}


	/**
	 *  Fire this event to indicate to watchers that they should perform indexing for ALL collections that they
	 *  know about using {@link CollectionIndexer#putRecord}, {@link CollectionIndexer#deleteRecord}, {@link
	 *  CollectionIndexer#deletePreviousSessionRecords}.
	 *
	 * @param  itemIndexerClassName  An ItemIndexer to fire the event on, or null to fire the event on all
	 *      handlers
	 * @exception  Exception         If error
	 */
	public void fireIndexAllCollectionsEvent(String itemIndexerClassName) throws Exception {
		IndexingEvent indexingEvent = new IndexingEvent(IndexingEvent.BEGIN_INDEXING_ALL_COLLECTIONS, null, collectionIndexer);
		fireEvent(indexingEvent, itemIndexerClassName);
	}


	/**
	 *  Fire this event to indicate to watchers that they should abort indexing at the earliest point possible.
	 *
	 * @param  itemIndexerClassName  An ItemIndexer to fire the event on, or null to fire the event on all
	 *      handlers
	 * @exception  Exception         If error
	 */
	public void fireAbortIndexingEvent(String itemIndexerClassName) throws Exception {
		IndexingEvent indexingEvent = new IndexingEvent(IndexingEvent.ABORT_INDEXING, null, collectionIndexer);
		fireEvent(indexingEvent, itemIndexerClassName);
	}


	/**
	 *  Fire this event to indicate to watchers that they update their configuration settings. This is called
	 *  automatically once at startup.
	 *
	 * @param  itemIndexerClassName  An ItemIndexer to fire the event on, or null to fire the event on all
	 *      handlers
	 * @exception  Exception         If error
	 */
	public void fireConfigureAndInitializeEvent(String itemIndexerClassName) throws Exception {
		IndexingEvent indexingEvent = new IndexingEvent(IndexingEvent.CONFIGURE_AND_INITIALIZE, null, collectionIndexer);
		fireEvent(indexingEvent, itemIndexerClassName);
	}


	/**
	 *  Fires the given event on all ItemIndexers.
	 *
	 * @param  indexingEvent         The event to fire
	 * @param  itemIndexerClassName  An ItemIndexer to fire the event on, or null to fire the event on all
	 *      handlers
	 * @exception  Exception         If error
	 */
	private void fireEvent(IndexingEvent indexingEvent, String itemIndexerClassName) throws Exception {
		if (itemIndexers == null)
			return;
		
		if(repositoryManager.getIsReadOnly()){
			String msg = "Repository is read-only. No action taken.";
			addIndexingMessage(msg,MSG_TYPE_INFO);
			return;
		}
		synchronized (itemIndexers) {
			if (itemIndexers.size() == 0)
				throw new Exception("There are no ItemIndexers configured. Check for initialization errors.");
		}
		IndexingEventThread indexingEventThread = new IndexingEventThread(indexingEvent, itemIndexerClassName);
		indexingEventThread.start();
	}


	/**
	 *  Issue the abort indexing event to watchers and shut down the IndexingManager
	 *
	 * @exception  Exception  If error
	 */
	public void destroy() throws Exception {
        prtln("IndexingManager: destroy()");
		fireAbortIndexingEvent(null);
        stopIndexingTimer();
		collectionIndexer = null;
		repositoryManager = null;
		repositoryMaintenance = null;
		itemIndexers = null;
		_indexingTimer = null;
		if (logger != null)
			logger.removeHandler(fileHandler);
	}


	/**
	 *  Gets the indexing status messages.
	 *
	 * @return    The indexingMessages.
	 */
	public ArrayList getIndexingMessages() {
		return indexingMessages;
	}


	/**
	 *  Adds a feature to the IndexingMessage attribute of the IndexingManager object
	 *
	 * @param  msg          The message
	 * @param  messageType  The message type
	 */
	protected void addIndexingMessage(String msg, int messageType) {
		if (logger != null) {
			if(messageType == MSG_TYPE_ERROR)
				logger.log(Level.SEVERE, msg);
			else if(messageType == MSG_TYPE_WARNING)
				logger.log(Level.WARNING, msg);			
			else
				logger.log(Level.INFO, msg);
		}
	
		String s = getSimpleDateStamp() + " " + msg + " [Index size: " + repositoryManager.getIndex().getNumDocs() + ", dups: " + repositoryManager.getDupItemsIndex().getNumDocs() + "]";
		indexingMessages.add(s);
		prtln(s);
		if (indexingMessages.size() > NUM_INDEXING_MESSAGES)
			indexingMessages.remove(0);
	}


	/**
	 *  Runs the event in the background.
	 *
	 * @author    John Weatherley
	 */
	private class IndexingEventThread extends Thread {
		IndexingEvent indexingEvent = null;
		String itemIndexerClassName = null;


		/**
		 *  Constructor for the IndexingEventThread object
		 *
		 * @param  indexingEvent         The event fired
		 * @param  itemIndexerClassName  The class name
		 */
		public IndexingEventThread(IndexingEvent indexingEvent, String itemIndexerClassName) {
			this.indexingEvent = indexingEvent;
			this.itemIndexerClassName = itemIndexerClassName;
		}


		/**  Main processing method for the Thread */
		public void run() {
			int currentSize = 0;
			if (itemIndexers == null)
				return;
			synchronized (itemIndexers) {
				currentSize = itemIndexers.size();
			}
			//prtln("Firing event: " + indexingEvent);

			// Loop through each of the ItemIndexers and fire the event:
			for (int i = 0; i < currentSize; i++) {
				ItemIndexer itemIndexer = null;
				synchronized (itemIndexers) {
					if (currentSize != itemIndexers.size())
						return;
					itemIndexer = (ItemIndexer) itemIndexers.get(i);
				}
				try {
					// If no ItemIndexer has been indicated, fire the event on all, otherwise only fire the event on the given ItemIndexer instance:
					if (itemIndexerClassName == null || itemIndexer.getClass().getName().equals(itemIndexerClassName)) {
						itemIndexer.indexingActionRequested(indexingEvent);
					}
				} catch (IndexingInProgressException e) {
					// -- The ItemIndexer has indicated that indexing is in progess, so can't continue...
					//addIndexingMessage("Error processing '" + indexingEvent + "'. Message: " + e.getMessage());
				} catch (Throwable t) {
					addIndexingMessage("Error processing '" + indexingEvent + "'. Message: " + t.getMessage(),MSG_TYPE_INFO);
				}
			}
		}
	}


	/* ---------------------- Timer task to run indexing on a cron: ------------------------ */
	/**
	 *  Starts or restarts the indexing timer thread to run continuously, triggering all collections indexing
	 *  every five minutes.
	 *
	 * @exception  Exception  If error reding the values
	 */
	public synchronized void startContinuousIndexingTimer() throws Exception {

		// Make sure the indexing timer is stopped before starting...
		stopIndexingTimer();

		_doPerformContinuousIndexing = true;

		// Make a new timer, with daemon set to true
		_indexingTimer = new Timer(true);

		long fiveMinutes = 1000 * 60 * 5;
		//long fiveMinutes = 1000 * 30; // 30 seconds for testing....

		_indexingTimer.scheduleAtFixedRate(new IndexingManagerTimerTask(), 5000, fiveMinutes);

		prtln("IndexingManager indexing timer started for continuous full indexing.");
	}


	/**
	 *  Starts or restarts the indexing timer thread to run every 24 hours, beginning at the specified time/date.
	 *  Use this method to schedule the timer to run as a nightly cron, beginning at the time you wish the
	 *  indexer to run.
	 *
	 * @param  indexingStartTime   The time of day at which start the indexing process in H:mm (24 hour time)
	 *      format, for example 0:35 or 23:35, or null to disable auto indexing.
	 * @param  indexingDaysOfWeek  The days of week to run the indexer as a comma separated list of integers, for
	 *      example 1,3,5 where 1=Sunday, 2=Monday, 7=Saturday etc. or null for all days.
	 * @exception  Exception       If error reding the values
	 */
	public synchronized void startIndexingTimer(String indexingStartTime, String indexingDaysOfWeek) throws Exception {
		_indexingDaysOfWeek = null;
		_indexingStartTimeDate = null;

		if (indexingStartTime == null) {
			stopIndexingTimer();
			return;
		}

		Date currentTime = new Date();
		try {
			int dayInYear = Integer.parseInt(Utils.convertDateToString(currentTime, "D"));
			int year = Integer.parseInt(Utils.convertDateToString(currentTime, "yyyy"));
			_indexingStartTimeDate = Utils.convertStringToDate(year + " " + dayInYear + " " + indexingStartTime, "yyyy D H:mm");

			Calendar startCal = new GregorianCalendar();
			//prtln("Current day-of-week is: " + Utils.getDayOfWeekString(startCal.get(Calendar.DAY_OF_WEEK)));

			// If this time has already passed today, increment to start tomorrow:
			if (_indexingStartTimeDate.before(currentTime)) {
				startCal = new GregorianCalendar();
				startCal.setTime(_indexingStartTimeDate);
				startCal.add(Calendar.DAY_OF_YEAR, 1);
				_indexingStartTimeDate = startCal.getTime();
			}
		} catch (Throwable t) {
			String msg = "Error parsing indexingStartTime value: " + t;
			_indexingStartTimeDate = null;
			throw new Exception(msg);
		}

		// Parse the days-of-week:
		try {
			_indexingDaysOfWeek = null;
			if (indexingDaysOfWeek != null && indexingDaysOfWeek.trim().length() > 0) {
				String[] indexingDaysOfWeekStrings = indexingDaysOfWeek.split(",");
				if (indexingDaysOfWeekStrings.length > 0)
					_indexingDaysOfWeek = new int[indexingDaysOfWeekStrings.length];
				for (int i = 0; i < indexingDaysOfWeekStrings.length; i++) {
					_indexingDaysOfWeek[i] = Integer.parseInt(indexingDaysOfWeekStrings[i].trim());
					if (_indexingDaysOfWeek[i] < 1 || _indexingDaysOfWeek[i] > 7)
						throw new Exception("Value must be an integer from 1 to 7 but found " + _indexingDaysOfWeek[i]);
				}
			}
		} catch (Throwable t) {
			String msg = "Error parsing indexingDaysOfWeek value: " + t.getMessage();
			_indexingStartTimeDate = null;
			_indexingDaysOfWeek = null;
			throw new Exception(msg);
		}

		String daysOfWeekMsg = "all days.";
		if (_indexingDaysOfWeek != null) {
			daysOfWeekMsg = "these days of the week: ";
			for (int i = 0; i < _indexingDaysOfWeek.length; i++)
				daysOfWeekMsg += Utils.getDayOfWeekString(_indexingDaysOfWeek[i]) + (i == _indexingDaysOfWeek.length - 1 ? "" : ", ");
		}

		// 24 hours, in milliseconds:
		long hours24 = 60 * 60 * 24 * 1000;

		// Make sure the (previous) indexing timer is stopped before starting...
		stopIndexingTimer();

		_doPerformContinuousIndexing = false;

		// Make a new timer, with daemon set to true
		_indexingTimer = new Timer(true);

		// Start the indexer at regular intervals beginning at the specified time
		try {
			prtln("Indexing timer is scheduled to start " +
				Utils.convertDateToString(_indexingStartTimeDate, "EEE, MMM d, yyyy h:mm a zzz") +
				", and run at that time on " + daysOfWeekMsg);
		} catch (ParseException e) {}

		_indexingTimer.scheduleAtFixedRate(new IndexingManagerTimerTask(), _indexingStartTimeDate, hours24);

		prtln("IndexingManager indexing timer started");
	}


	/**  Stops the indexing timer thread. */
	private void stopIndexingTimer() {
		if (_indexingTimer != null) {
			_indexingTimer.cancel();
            _indexingTimer.purge();
			prtln("IndexingManager indexing timer stopped");
		}
	}


	/**
	 *  Gets the indexingStartTime Date, representing the time of day the indexer will run, or null if no
	 *  indexing cron is being used.
	 *
	 * @return    The indexingStartTime or null
	 */
	public Date getIndexingStartTime() {
		return _indexingStartTimeDate;
	}


	/**
	 *  Gets the days of the week the indexer will run as an array of Calendar.DAY_OF_WEEK fields, or null to
	 *  indicate all days of the week.
	 *
	 * @return    The indexingDaysOfWeek or null for all days
	 */
	public int[] getIndexingDaysOfWeek() {
		return _indexingDaysOfWeek;
	}


	/**
	 *  Runs the indexer at regular intervals (every day for daily indexing or every five minutes for continuous
	 *  indexing).
	 *
	 * @author    John Weatherley
	 */
	private class IndexingManagerTimerTask extends TimerTask {
		/**  Main processing method for the IndexingManagerTimerTask object */
		public void run() {
			String msg = "";
			try {
				// Run every day if no DaysOfWeek indicated, or continuously, if indicated:
				if (_doPerformContinuousIndexing || _indexingDaysOfWeek == null) {
					msg = "IndexingManager: Triggering indexing of all collections (continuous indexing timer)...";
					addIndexingMessage(msg,MSG_TYPE_INFO);
					fireIndexAllCollectionsEvent(null);
					prtln(msg);
				}
				// Run today, if indicated:
				else {
					msg = "IndexingManager: Beginning automatic timed indexing of all collections...";
					Calendar now = new GregorianCalendar();
					int today = now.get(Calendar.DAY_OF_WEEK);
					for (int i = 0; i < _indexingDaysOfWeek.length; i++) {
						if (_indexingDaysOfWeek[i] == today) {
							addIndexingMessage(msg,MSG_TYPE_INFO);
							fireIndexAllCollectionsEvent(null);
							prtln(msg);
							break;
						}
					}
				}

			} catch (Throwable t) {
				String errMsg = "IndexingManager: Indexing timer failed to run: " + t.getMessage();
				addIndexingMessage(errMsg,MSG_TYPE_ERROR);
				prtlnErr(errMsg);
			}
		}
	}


	/* ---------------------- Debugging methods ------------------------ */
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
	 *  Return a string for the current time and date, sutiable for display in log files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	public static String getSimpleDateStamp() {
		try {
			return
				Utils.convertDateToString(new Date(), "EEE, MMM d h:mm:ss a");
		} catch (ParseException e) {
			return "";
		}
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final static void prtlnErr(String s) {
		System.err.println(getDateStamp() + " IndexingManager Error: " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final static void prtln(String s) {
		if (debug) {
			System.out.println(getDateStamp() + " IndexingManager: " + s);
		}
	}


	/**
	 *  Sets the debug attribute of the IndexingManager object
	 *
	 * @param  db  The new debug value
	 */
	public final void setDebug(boolean db) {
		debug = db;
	}
}

