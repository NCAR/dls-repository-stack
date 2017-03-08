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
package edu.ucar.dls.harvestmanager;

import org.dlese.dpc.datamgr.SimpleDataStore;
import org.dlese.dpc.webapps.tools.GeneralServletTools;


import edu.ucar.dls.harvest.Config;

// JDK imports
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Properties;


// Enterprise imports
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 *  Initializes and starts the Harvest Repository Manager.
 *
 * @author    John Weatherley
 */


public final class HarvestManagerServlet extends HttpServlet {

	// Caution: ALL class and instance variables are shared.
	// Multiple requests may use this object concurrently.

	private boolean initialized = false;
	private boolean debug = true;

	HarvestManager harvestManager = null;
	
	/**
	 *  Init method called by the web application server upon startup
	 *
	 * @param  conf                  Servlet configuration
	 * @exception  ServletException  If error
	 */
	public void init(ServletConfig conf) throws ServletException {

		
		System.out.println(getDateStamp() + " HarvestManagerServlet starting");
		String initErrorMsg = "";
		try {
			// super.init must be called before getServletContext().getRealPath("/");
			super.init(conf);
		} catch (Throwable exc) {
			initErrorMsg = "HarvestManagerServlet Initialization Error:\n  " + exc;
			prtlnErr(initErrorMsg);
		}

		ServletContext context = getServletContext();		
		
		// Grab the document root for this servlet.
		// Note: this cannot be grabbed before super.init():
		//docRoot = getServletContext().getRealPath("/");

		// Check to see if init has been called previously. If yes, do nothing
		// (This happens when the app is running in a Tomcat environment configured
		// with multiple JVM's).
		if (initialized) {
			prtlnErr("HarvestManagerServlet already initialized. Will not initialize twice.");
			return;		
		}
		initialized = true;
				
		String bugs = context.getInitParameter("debug");
		if (bugs != null && bugs.equalsIgnoreCase("true")) {
			debug = true;
			prtln("Outputting debug info");
		}else{
			debug = false;		
			prtln("Debug info disabled");
		}

		String ncsWebServiceURL = context.getInitParameter("ncsWebServiceURL");
		if (ncsWebServiceURL == null || ncsWebServiceURL.length() == 0) {
			initErrorMsg = "HarvestManagerServlet init parameter 'ncsWebServiceURL' is missing";
			prtlnErr(initErrorMsg);
			throw new ServletException(initErrorMsg);
		}

		String ucarCollectionsQuery = context.getInitParameter("ucarCollectionsQuery");
		if (ucarCollectionsQuery == null || ucarCollectionsQuery.length() == 0) {
			initErrorMsg = "HarvestManagerServlet init parameter 'ucarCollectionsQuery' is missing";
			prtlnErr(initErrorMsg);
			throw new ServletException(initErrorMsg);
		}		
		
		String harvestTriggerFileDir = context.getInitParameter("harvestTriggerFileDir");
		if (harvestTriggerFileDir == null || harvestTriggerFileDir.length() == 0) {
			initErrorMsg = "HarvestManagerServlet init parameter 'harvestTriggerFileDir' is missing";
			prtlnErr(initErrorMsg);
			throw new ServletException(initErrorMsg);
		}
		harvestTriggerFileDir = GeneralServletTools.getAbsolutePath(harvestTriggerFileDir,context);
		
		String harvestManagerPersistentDataDir = context.getInitParameter("harvestManagerPersistentDataDir");
		if (harvestManagerPersistentDataDir == null || harvestManagerPersistentDataDir.length() == 0) {
			initErrorMsg = "HarvestManagerServlet init parameter 'harvestManagerPersistentDataDir' is missing";
			prtlnErr(initErrorMsg);
			throw new ServletException(initErrorMsg);
		}
		harvestManagerPersistentDataDir = GeneralServletTools.getAbsolutePath(harvestManagerPersistentDataDir,context);

		// 24 hours, in seconds (86400)
		long _harvestCheckInterval = 60 * 60 * 24; 
		String harvestCheckInterval = context.getInitParameter("harvestCheckInterval");
		try {
			_harvestCheckInterval = Long.parseLong(harvestCheckInterval);
		} catch (Exception e) {
			prtlnErr("Unable to parse init parameter 'harvestCheckInterval'. Using default value '" + _harvestCheckInterval + "'. Reason: " + e.getMessage());		
		}
		
		if(_harvestCheckInterval != 0 && _harvestCheckInterval != 86400) {
			prtlnErr("Init param 'harvestCheckInterval' must be either 0 or 86400, not " + _harvestCheckInterval + ". Using default value 86400 instead.");
			_harvestCheckInterval = 86400;
		}

		boolean doReprocessCollectionsMonthly = false;
		String reprocessCollectionsMonthly = context.getInitParameter("reprocessCollectionsMonthly");
		if(reprocessCollectionsMonthly != null && reprocessCollectionsMonthly.trim().equalsIgnoreCase("true"))
			doReprocessCollectionsMonthly = true;

		// E-mailer config:
		String mailServer = context.getInitParameter("mailServer");
		String mailType = context.getInitParameter("mailType");
		String toEmails = context.getInitParameter("toEmails");
		String fromEmail = context.getInitParameter("fromEmail");
		String harvestManagerUrl = context.getInitParameter("harvestManagerUrl");
		String ncsViewEditRecordUrl = context.getInitParameter("ncsViewEditRecordUrl");
		
		String _harvestCheckTime = "1:30";
		String harvestCheckTime = context.getInitParameter("harvestCheckTime");
		if (harvestCheckTime == null) {
			prtlnErr("Unable to read init parameter 'harvestCheckTime'. Using default value '" + _harvestCheckTime + "'.");		
		}
		else
			_harvestCheckTime = harvestCheckTime;
		
		// Params used for testing:
		String runMode = context.getInitParameter("runMode"); // Pass in 'test' to display test links in UI, etc.
		int numDaysToSimulate = 0;
		try {
			numDaysToSimulate = Integer.parseInt(context.getInitParameter("runHarvestSimulation"));
		} catch (Throwable t) {}
		
		
		// Params used for the ingestor process
		int maxIngestorThreads = 50;
		try
		{
			maxIngestorThreads = Integer.parseInt(context.getInitParameter("maxIngestorThreads"));
		}
		catch(NumberFormatException e){}
		try {
			harvestManager = new HarvestManager(ncsWebServiceURL,ucarCollectionsQuery,harvestTriggerFileDir,harvestManagerPersistentDataDir, maxIngestorThreads, doReprocessCollectionsMonthly);
			//harvestManager.setRunMode(runMode);
			harvestManager.setEmailer(mailServer,mailType,toEmails.split(","),fromEmail,harvestManagerUrl,ncsViewEditRecordUrl);
			context.setAttribute( "harvestManager", harvestManager );
			harvestManager.startHarvestTimer(_harvestCheckInterval,_harvestCheckTime);
			setupIngestorConfig(context, harvestManager);
			if(numDaysToSimulate > 0)
				System.out.println(harvestManager.runSimulatedHarvests(numDaysToSimulate));	
		} catch (Exception e) {
			String msg = "HarvestManager error on start up: " + e.getMessage();
			prtlnErr(msg);
			e.printStackTrace();
			throw new ServletException(msg);
		}
		
		
		context.setAttribute( "startUpDate", new Date() );
		System.out.println(getDateStamp() + " HarvestManagerServlet started...");
	}

	private void setupIngestorConfig(ServletContext context, HarvestManager hm) throws ServletException
	{
		String harvestDbUrl = context.getInitParameter("harvestDbUrl");
		String harvestDbUser = context.getInitParameter("harvestDbUser");
		String harvestDbPwd = context.getInitParameter("harvestDbPwd");
		String handleServiceURL = context.getInitParameter("handleServiceURL");
		String ingestorBaseFilePathStorage = GeneralServletTools.getAbsolutePath(context.getInitParameter("ingestorBaseFilePathStorage"), context);
		String ingestorConfigsURI = context.getInitParameter("ingestorConfigsURI");
		String asnStandardResolverUrlFormat = context.getInitParameter("asnStandardResolverUrlFormat");
		String smsInfoJsonUrlFormat = context.getInitParameter("smsInfoJsonUrlFormat");
		
		
		
		boolean zipHarvestFiles = Boolean.parseBoolean(context.getInitParameter("zipHarvestFiles"));
		
		if(ingestorConfigsURI.contains("file:")||ingestorConfigsURI.contains("http:")||ingestorConfigsURI.contains("https:"))
		{
			//do nothing keep as is
		}
		else
		{
			ingestorConfigsURI = new File(GeneralServletTools.getAbsolutePath(ingestorConfigsURI, context)).toURI().toString();
		}

		float maxErrorThreshold = Float.parseFloat(context.getInitParameter("maxErrorThreshold"));
		int maxProcessorIssueReportingExamples = Integer.parseInt(context.getInitParameter("maxProcessorIssueReportingExamples"));
		float minRecordCountDiscrepencyThreshold = Float.parseFloat(context.getInitParameter("minRecordCountDiscrepencyThreshold"));
	
		Config.setDB(harvestDbUrl, harvestDbUser, harvestDbPwd);
		Config.setBASE_FILE_PATH_STORAGE(ingestorBaseFilePathStorage);
		Config.setINGESTOR_CONFIGS_URI(ingestorConfigsURI);
		Config.setMAX_ERROR_THRESHOLD(maxErrorThreshold);
		Config.setMIN_RECORD_COUNT_DISCREPANCY_THRESHOLD(minRecordCountDiscrepencyThreshold);
		Config.Reporting.setMaxNumberOfExamples(maxProcessorIssueReportingExamples);
		Config.setHANDLE_SERVICE_URL(handleServiceURL);
		Config.Reporting.setHarvestManager(hm);
		Config.setZIP_HARVEST_FILES(zipHarvestFiles);
		Config.ASN.setAsnStandardResolverUrlFormat(asnStandardResolverUrlFormat);
		Config.setSMS_INFO_JSON_URL_FORMAT(smsInfoJsonUrlFormat);
	}


	/**  Performs shutdown operations.  */
	public void destroy() {
		if(harvestManager != null)
			harvestManager.destroy();
		System.out.println(getDateStamp() + " HarvestManagerServlet stopped");
	}


	//================================================================

	/**
	 *  Handle POST requests by forwarding to GET.
	 *
	 * @param  req                   DESCRIPTION
	 * @param  resp                  DESCRIPTION
	 * @exception  ServletException  DESCRIPTION
	 * @exception  IOException       DESCRIPTION
	 */
	public void doPost(
	                   HttpServletRequest req,
	                   HttpServletResponse resp)
		 throws ServletException, IOException {
		doGet(req, resp);
	}


	//================================================================

	/**
	 *  Handle PUT requests.
	 *
	 * @param  req                   Input request.
	 * @param  resp                  Resulting response.
	 * @exception  IOException       I/O error
	 * @exception  ServletException  servlet error
	 */
	public void doPut(
	                  HttpServletRequest req,
	                  HttpServletResponse resp)
		 throws ServletException, IOException {
		//badreq( req, resp);
	}


	//================================================================

	/**
	 *  Handle DELETE requests.
	 *
	 * @param  req                   Input request.
	 * @param  resp                  Resulting response.
	 * @exception  IOException       I/O error
	 * @exception  ServletException  servlet error
	 */
	public void doDelete(
	                     HttpServletRequest req,
	                     HttpServletResponse resp)
		 throws ServletException, IOException {
		badreq(req, resp);
	}


	//================================================================

	/**
	 *  Handle GET requests.
	 *
	 * @param  req                   Input request.
	 * @param  resp                  Resulting response.
	 * @exception  IOException       I/O error
	 * @exception  ServletException  servlet error
	 */
	public void doGet(
	                  HttpServletRequest req,
	                  HttpServletResponse resp)
		 throws IOException, ServletException {

		// Set up output writer.
		PrintWriter respwtr = resp.getWriter();
		respwtr.print(" doGet() ");
		respwtr.close();

	}


	// end doGet

	/**
	 *  Override the standard servlet logging to use our logger
	 *
	 * @param  msg  DESCRIPTION
	 */
	public final void log(String msg) {
		prtln(msg);
	}



	/**
	 *  Handle illegal requests.
	 *
	 * @param  req                   Input request.
	 * @param  resp                  Resulting response.
	 * @exception  IOException       I/O error
	 * @exception  ServletException  servlet error
	 */
	private void badreq(
	                    HttpServletRequest req,
	                    HttpServletResponse resp)
		 throws ServletException, IOException {

	}



	//================================================================


	/**
	 *  Return a string for the current time and date, sutiable for display in log files and
	 *  output to standout:
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
		System.err.println(getDateStamp() + " HarvestManagerServlet Error: " + s);
	}



	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final void prtln(String s) {
		if (debug)
			System.out.println(getDateStamp() + " HarvestManagerServlet: " + s);
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

