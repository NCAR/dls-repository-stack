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
package edu.ucar.dls.oai.harvester;

import edu.ucar.dls.oai.*;
import edu.ucar.dls.util.*;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.StringWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.text.*;

import java.util.zip.GZIPInputStream;
import lattelib.util.zip.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import java.net.URLEncoder;

/**
 *  Harvests metadata from an <a href="http://www.openarchives.org/">OAI</a> data provider, saving the results
 *  to file or returning the raw XML as an array of Strings. Supports data providers that use resumption
 *  tokens for <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#FlowControl"> flow
 *  control</a> , selective harvesting by <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#SelectiveHarvestingandDatestamps">
 *  date</a> or <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#SelectiveHarvestingandSets">
 *  set</a> , gzip <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ResponseCompression">
 *  response compression</a> and other protocol features. Supports OAI protocol versions 1.1 and <a
 *  href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm">2.0</a> . <p>
 *
 *  To perform a harvest, use one of the following methods:
 *  <ul>
 *    <li> The static <b>harvest</b> method (for general use): <code>{@link #harvest}}</code><br/>
 *    <br/>
 *    </li>
 *    <li> The static <b>main</b> method (for command-line use): <code>{@link #main}</code><br/>
 *    <br/>
 *    </li>
 *    <li> The non-static <b>doHarvest</b> method (provides a few additional options): {@link #doHarvest}.
 *    <br/>
 *    <br/>
 *    </li>
 *  </ul>
 *  Use of this API assumes familiarity with the <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm">
 *  OAI protocol</a> .<p>
 *
 *
 *
 * @author    Steve Sullivan, John Weatherley
 * @see       HarvestMessageHandler
 */
public class Harvester implements ErrorHandler {
	private static boolean debug = true;

	private final static String defaultUserAgent = "jOAI Harvester";
	private String _userAgent = null;

	private final static int GRAN_DAY = 1;
	private final static int GRAN_SECOND = 2;
	private int granularity = -1;

	private final static int DELETED_RECORD_NO = 0;
	private final static int DELETED_RECORD_TRANSIENT = 1;
	private final static int DELETED_RECORD_PERSISTENT = 2;
	private int deleted_record = -1;

	private static long nextIdIter = 0;
	private final static int MAX_FILES = 50;

	private int bugs = 0;
	private String xmlerrors;
	private String xmlwarnings;

	private int recordCount = 0;
	private int resumpCount = 0;
	private long startTime = 0;
	private long endTime = 0;
	private boolean isRunning = false;
	private boolean hasDoneHarvest = false;
	private boolean killed = false;
	private String outputDir = "";
	private HarvestMessageHandler msgHandler = null;
	private int messagingNum = 100;
	private long harvestId;
	private int timeOutMilliseconds = 180000;


	/**
	 *  Output an error message and exit.
	 *
	 * @param  msg  The error message
	 */
	private static void badparms(String msg) {
		System.out.println("");
		System.out.println("Could not perform harvest. " + msg);
		System.out.println("");
		System.out.println("Arguments must be:");
		System.out.println("    outdir");
		System.out.println("    baseURL");
		System.out.println("    metadataPrefix");
		System.out.println("    [ -set:setSpec ]");
		System.out.println("    [ -from:fromDate ]");
		System.out.println("    [ -until:untilDate ]");
		System.out.println("    [ -splitBySet:true|False ]");
		System.out.println("    [ -writeHeaders:true|False ]");
		System.out.println("    [ -userAgent:userAgentString ]");
		System.out.println("");
		System.exit(1);
	}


	/**
	 *  Command line interface for the harvester. Harvest status messages are output to standard out.<p>
	 *
	 *  Arguments (required arguments must be in this order, optional arguments may be in any order):
	 *  <ul>
	 *    <li> outdir (required) - Path to the directory to write the harvested record files, for example "." or
	 *    "/home/user/harvested_files"
	 *    <li> baseURL (required) - Base URL to harvest from, for example "http://www.dlese.org/oai/provider"
	 *
	 *    <li> metadataPrefix (required) - The metadata prefix, for example "oai_dc"
	 *    <li> [ -set:setSpec ] (optional) - The set to harvest, for example -set:myset
	 *    <li> [ -from:fromDate ] (optional) - The harvest from date, for example, -from:2003-12-31T23:59:59Z
	 *
	 *    <li> [ -until:untilDate ] (optional) - The harvest until date, for example, -until:2004-12-31T23:59:59Z
	 *
	 *    <li> [ -splitBySet:true|False ] (optional) - True to save each record in separate directories split by
	 *    set inside outdir, false to save all records to the root of outdir (default is false)
	 *    <li> [ -writeHeaders:true|False ] (optional) - True to have OAI headers written to the output, false
	 *    not to (default is false)
	 *    <li> [ -userAgent:userAgentString ] (optional) - The http User-Agent header to send with the request
	 *    (otherwise uses system default)
	 *  </ul>
	 *
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(String[] args) {
		int ii;

		if (args.length < 3 || args.length > 7) {
			badparms("Wrong number of required arguments");
		}
		int iarg = 0;

		String outdir = args[iarg++];
		if (outdir.length() == 0 || outdir.equalsIgnoreCase("null")) {
			outdir = null;
		}

		String baseURL = args[iarg++];

		String metadataPrefix = args[iarg++];
		if (metadataPrefix.length() == 0) {
			metadataPrefix = null;
		}

		// Set up the default values:
		boolean harvestAll = false;
		boolean harvestAllIfNoDeletedRecord = false;
		boolean writeHeaders = false;
		boolean splitBySet = false;
		String set = null;
		Date from = null;
		Date until = null;
		String userAgent = defaultUserAgent;

		// Parse the optional arguments:
		try {
			for (int i = iarg; i < args.length; i++) {
				if (args[i].startsWith("-")) {

					if (args[i].toLowerCase().startsWith("-set:")) {
						set = args[i].substring(5);
					}
					else if (args[i].toLowerCase().startsWith("-from:")) {
						try {
							from = parseDate(args[i].substring(6));
						} catch (Throwable pe) {
							badparms("Error parsing the from date: " + pe.getMessage());
						}
					}
					else if (args[i].toLowerCase().startsWith("-until:")) {
						try {
							until = parseDate(args[i].substring(7));
						} catch (Throwable pe) {
							badparms("Error parsing the until date: " + pe.getMessage());
						}
					}
					else if (args[i].toLowerCase().startsWith("-splitbyset:")) {
						if (args[i].toLowerCase().equals("-splitbyset:true"))
							splitBySet = true;
						else if (args[i].toLowerCase().equals("-splitbyset:false"))
							splitBySet = false;
						else
							badparms("splitBySet argument must be either 'true' or 'false'");
					}
					else if (args[i].toLowerCase().startsWith("-writeheaders:")) {
						if (args[i].toLowerCase().equals("-writeheaders:true"))
							writeHeaders = true;
						else if (args[i].toLowerCase().equals("-writeheaders:false"))
							writeHeaders = false;
						else
							badparms("writeHeaders argument must be either 'true' or 'false'");
					}
					else if (args[i].toLowerCase().startsWith("-useragent:")) {
						userAgent = args[i].substring(11);
					}
					else {
						badparms("Unrecognized argument '" + args[i] + "'");
					}
				}
				else {
					badparms("Optional arguments must start with '-'");
				}
			}

		} catch (Throwable t) {
			prtln("Error found in one or more arguments: " + t.getMessage());
			System.exit(1);
		}

		SimpleHarvestMessageHandler hmh = new SimpleHarvestMessageHandler();
		String[][] resmat = null;
		try {
			resmat = harvest(baseURL, metadataPrefix,
				set, from, until, outdir, splitBySet, hmh, writeHeaders, harvestAll, harvestAllIfNoDeletedRecord, 240000, userAgent);
		} catch (Hexception hex) {
			prtlnErr("Error: " + hex.getMessage());
			//hex.printStackTrace();
			System.exit(1);
		} catch (OAIErrorException oex) {
			// This message is being output by the HarvestMessageHandler
			// prtln(oex.getMessage());
			System.exit(0);
		}

		if (resmat != null) {
			for (ii = 0; ii < resmat.length; ii++) {
				prtln("resmat " + ii + ": \"" + resmat[ii][0] + "\"");
				prtln(resmat[ii][1]);
				prtln("\n");
			}
		}
	}


	/**
	 *  Harvest the given provider, saving the resulting metadata to file or returning the results as an array of
	 *  Strings. A HarvestMessageHandler may be specified to capture harvest progress messages. Use a {@link
	 *  SimpleHarvestMessageHandler} to have harvest messages sent to standard out.
	 *
	 * @param  baseURL                      The baseURL of the data provider, for example
	 *      "http://www.dlese.org/oai/provider"
	 * @param  metadataPrefix               The metadataPrefix, for example "oai_dc"
	 * @param  set                          The set to harvest, for example "testset", or null to harvest all
	 *      sets
	 * @param  from                         The from date, for example "2003-12-31T23:59:59Z", or null for none
	 * @param  until                        The until date, for example "2003-12-31T23:59:59Z", or null for none
	 * @param  outdir                       The path of output dir. If null or "", we return the String[][]
	 *      array; if specified we return null
	 * @param  msgHandler                   A handler for status messages that occur during the harvest, or null
	 *      to ingnore messages
	 * @param  writeHeaders                 True to have OAI headers written to the output, false not to
	 * @param  harvestAll                   True to delete previous harvested record files and harvest all
	 *      records again from scratch; false to preserve previous record files and replace or delete only those
	 *      that have changed
	 * @param  harvestAllIfNoDeletedRecord  True to harvest all record files from scratch if deleted records are
	 *      not supported
	 * @param  splitBySet                   True to save each record in separate directories split by set inside
	 *      outdir, false to save all records to the root of outdir
	 * @param  timeOutMilliseconds          Number of milliseconds the harvester will wait for a response from
	 *      the data provider before timing out
	 * @param  userAgent                    The http User-Agent header sent with the request, or null to use
	 *      default
	 * @return                              If outdir is specified returns null; if outdir is null or "", returns
	 *      one row for each record harvested. Each row has two elements:
	 *      <ul>
	 *        <li> identifier, encoded
	 *        <li> content xml record, or the String deleted if status=deleted.
	 *      </ul>
	 *      <p>
	 *
	 *
	 * @exception  Hexception               If serious error
	 * @exception  OAIErrorException        If OAI error
	 */
	public static String[][] harvest(
	                                 String baseURL,
	                                 String metadataPrefix,
	                                 String set,
	                                 Date from,
	                                 Date until,
	                                 String outdir,
	                                 boolean splitBySet,
	                                 HarvestMessageHandler msgHandler,
	                                 boolean writeHeaders,
	                                 boolean harvestAll,
	                                 boolean harvestAllIfNoDeletedRecord,
	                                 int timeOutMilliseconds,
	                                 String userAgent)
		 throws Hexception, OAIErrorException {
		Harvester hvst;

		hvst = new Harvester(msgHandler, timeOutMilliseconds, userAgent);

		String[][] res = hvst.doHarvest(baseURL,
			metadataPrefix, set, from, until, outdir, splitBySet, " ", " ", writeHeaders, harvestAll, harvestAllIfNoDeletedRecord);
		return res;
	}



	/**  Creates a Harvester that uses no HarvestMessageHandler. */
	public Harvester() {
		this.msgHandler = null;
		this.harvestId = System.currentTimeMillis() + nextIdIter++;
	}


	/**
	 *  Creates a Harvester that uses the given HarvestMessageHandler.
	 *
	 * @param  msgHandler           The HarvestMessageHandler that will receive messages as the harvest
	 *      progresses, or null if none.
	 * @param  timeOutMilliseconds  Number of milliseconds the harvester will wait for a response from the data
	 *      provider before timing out
	 * @param  userAgent            The http User-Agent header sent with the OAI requests, or null to use default
	 *      'jOAI Harvester'
	 */
	public Harvester(HarvestMessageHandler msgHandler, int timeOutMilliseconds, String userAgent) {
		this.msgHandler = msgHandler;
		this.timeOutMilliseconds = timeOutMilliseconds;
		if (msgHandler != null) {
			messagingNum = msgHandler.getNumRecordsForStatusNotification();
		}
		this.harvestId = System.currentTimeMillis() + nextIdIter++;
		if (userAgent == null)
			_userAgent = defaultUserAgent;
		else
			_userAgent = userAgent;
	}


	/**  Gracefully kills the harvest after the current record is finished being harvested. */
	public void kill() {
		prtln("Harvester kill() ");
		killed = true;
	}


	/**
	 *  Sets the number of records harvested before statusMessage notifications to the HarvestMessageHandler are
	 *  made.
	 *
	 * @param  numRecords  The new numRecordsForNotification value
	 */
	public void setNumRecordsForNotification(int numRecords) {
		this.messagingNum = numRecords;
	}


	/**
	 *  Gets the startTime when the harvest began, or 0 if it has not begun yet.
	 *
	 * @return    The startTime, or 0 if not started yet.
	 */
	public long getStartTime() {
		return startTime;
	}


	/**
	 *  Gets the harvestedRecordsDir attribute of the Harvester object
	 *
	 * @return    The harvestedRecordsDir value
	 */
	public String getHarvestedRecordsDir() {
		return outputDir;
	}


	/**
	 *  Returns a unique ID for this harvest.
	 *
	 * @return    The harvestId value
	 */
	public long getHarvestUid() {
		return harvestId;
	}


	/**
	 *  Gets the endTime when the havest completed either because of an error or at the end of a successful
	 *  harvest. Returns 0 if the harvest is still in progress.
	 *
	 * @return    The endTime, or 0 if the harvest is still in progress.
	 */
	public long getEndTime() {
		return endTime;
	}


	/**
	 *  Gets the current number of records that have been harvested by this harvester. This number increases as
	 *  the harvest progresses.
	 *
	 * @return    The numRecordsHarvested value
	 */
	public int getNumRecordsHarvested() {
		return recordCount;
	}


	/**
	 *  Gets the number of resumption tokens that have currently been issued by the data provider. This number
	 *  increases as the harvest progresses. This number gives a rough indication of the progression and duration
	 *  of the harvest.
	 *
	 * @return    The numResumptionTokensIssued value.
	 */
	public int getNumResumptionTokensIssued() {
		return resumpCount;
	}


	/**
	 *  Determines whether this Harvester is currently running or not.
	 *
	 * @return    True if the harvest is in progress, false otherwise.
	 */
	public boolean isRunning() {
		return isRunning;
	}


	// Deletes all files and subdirectories under dir.
	// Returns true if all deletions were successful.
	// If a deletion fails, the method stops attempting to delete and returns false.

	/**
	 *  Recursively delete the contents of a directory.
	 *
	 * @param  dir            The dir to delete
	 * @return                True if successful
	 * @exception  Throwable  If error
	 */
	private static boolean deleteDir(File dir) throws Throwable {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}


	/**
	 *  Performs the harvest. Note that his method is not safe for multiple harvests - a separate Harvester
	 *  instance should be created for each havest performed.<P>
	 *
	 *
	 *
	 * @param  metadataPrefix               metadataPrefix. e.g., "oai_dc", or null to harvest all formats
	 * @param  set                          set. e.g., "testset" or null for none.
	 * @param  from                         from date. May be null.
	 * @param  until                        until date. May be null.
	 * @param  outdir                       path of output dir. If null or "", we return the String[][] array; if
	 *      specified we return null.
	 * @param  writeHeaders                 True to have oai headers written to file, false not to. <br>
	 *      The directory structure under outdir is: <br>
	 *      outdir/set/subset/subset/metadataPrefix/oaiId_hdr.xml OAI header <br>
	 *      outdir/set/subset/subset/metadataPrefix/oaiId_data.xml OAI contents <br>
	 *
	 * @param  baseURL                      The baseURL of the data provider.
	 * @param  splitBySet                   To split set
	 * @param  zipName                      Name of the zip file to save to
	 * @param  zDir                         Directory of the zipfile
	 * @param  harvestAll                   True to delete previous harvested records and harvest all records
	 *      again from scratch
	 * @param  harvestAllIfNoDeletedRecord  True to harvest all records from scratch if deleted records are not
	 *      supported
	 * @return                              If outdir is specified returns null; if outdir is null or "", returns
	 *      one row for each record harvested. Each row has two elements:
	 *      <ul>
	 *        <li> identifier, encoded
	 *        <li> content xml record.
	 *      </ul>
	 *      <p>
	 *
	 *
	 * @exception  Hexception               If serious error.
	 * @exception  OAIErrorException        If OAI error was returned by the data provider.
	 */
	public String[][] doHarvest(
	                            String baseURL,
	                            String metadataPrefix,
	                            String set,
	                            Date from,
	                            Date until,
	                            String outdir,
	                            boolean splitBySet,
	                            String zipName,
	                            String zDir,
	                            boolean writeHeaders,
	                            boolean harvestAll,
	                            boolean harvestAllIfNoDeletedRecord)
		 throws Hexception, OAIErrorException {

		//this.outputDir = new File(OAIUtils.getHarvestedDirBaseURLPath(outdir, baseURL)).getAbsolutePath();

		if (from == null)
			prtln("doHarvest() from: null");
		else
			prtln("doHarvest() from: " + from);

		startTime = System.currentTimeMillis();

		this.outputDir = new File(outdir).getAbsolutePath();
		File oD = new File(outdir);

		prtln("harvestAll 1 is " + harvestAll);
		prtln("outputDir is " + this.outputDir);

		/* int z;
		if (oD.exists())
			z = Count(oD);
		else
			z = 0;
		prtln("Count at beginning is " + z);
		if ((splitBySet != true) && (recordCount != z)) {
			recordCount = z;
		} */
		synchronized (this) {
			if (hasDoneHarvest) {
				throw new Hexception("This harvester has already performed a harvest. Please use a new Harvester instance.");
			}
			hasDoneHarvest = true;
			isRunning = true;
		}

		String errorMsg = null;

		try {

			if (!(baseURL.startsWith("http://") || baseURL.startsWith("https://"))) {
				errorMsg = "baseURL does not start with http:// or https://";
				endTime = System.currentTimeMillis();
				if (msgHandler != null)
					msgHandler.errorMessage(errorMsg);
				throw new Hexception(errorMsg);
			}

			killed = false;

			int ipref;

			String verb = "ListRecords";
			String resumption = null;
			LinkedList reslist = null;
			if (outdir == null) {
				reslist = new LinkedList();
			}

			// Get the granularity and deleted record supported by the data provider
			getIdentifyInfo(baseURL);

			if (harvestAllIfNoDeletedRecord && deleted_record == DELETED_RECORD_NO)
				harvestAll = true;

			//prtln("harvestAll if no deleted record is " + harvestAll);

			if (harvestAll) {
				// Make sure from and until dates are not used for a fresh harvest:
				from = null;
				until = null;
				boolean delete_dir_success = true;
				if (oD.exists()) {
					try {
						delete_dir_success = deleteDir(new File(oD.getAbsolutePath()));
						if (delete_dir_success) {
							prtln("deleted harvest dir: " + oD);
							recordCount = 0;
						}
					} catch (Throwable t) {
						errorMsg = "Unable to delete harvest directory '" + oD.getAbsolutePath() + "': " + t.getMessage();
					}
				}

				if (!delete_dir_success) {
					errorMsg = "Unable to delete harvest directory '" + oD.getAbsolutePath() + "'";
					if (!oD.canWrite())
						errorMsg += ". No write permissions.";
					else if (!oD.canRead())
						errorMsg += ". No read permissions.";
				}
			}

			// Set the from and until dates for reporting
			if (msgHandler != null)
				msgHandler.setHarvestAttributes(from, until);

			// Make the harvest directory if not already created:
			if (!(oD.exists())) {
				boolean create_dir_success = false;
				try {
					create_dir_success = oD.mkdirs();
				} catch (Throwable t) {
					// prtlnErr("Unable to create harvest directory: " + t);
					errorMsg = "Unable to create harvest directory'" + oD.getAbsolutePath() + "'. " + t.getMessage();
				}
				if (!create_dir_success) {
					if (!oD.canWrite())
						errorMsg = "Unable to create harvest directory '" + oD.getAbsolutePath() + "'. No write permissions.";
					else if (!oD.canRead())
						errorMsg = "Unable to create harvest directory '" + oD.getAbsolutePath() + "'. No read permissions.";
					else
						errorMsg = "Unable to create harvest directory '" + oD.getAbsolutePath() + "'";
				}
			}

			// Generate an error:
			if (errorMsg != null) {
				endTime = System.currentTimeMillis();
				if (msgHandler != null)
					msgHandler.errorMessage(errorMsg);
				throw new Hexception(errorMsg);
			}

			// If metadataPrefix is not specified, harvest all supported formats
			String[] prefices;
			if (metadataPrefix == null) {
				prefices = getPrefices(baseURL);
			}
			else {
				prefices = new String[]{metadataPrefix};
			}

			for (ipref = 0; ipref < prefices.length; ipref++) {
				String prefix = prefices[ipref];
				while (true) {
					if (killed) {
						throw new Hexception("Harvest received kill signal");
					}
					String request = baseURL;
					String reqMessage;
					if (resumption == null) {
						request += "?verb=" + verb;
						request += "&metadataPrefix=" + prefix;
						if (set != null && set.length() > 0) {
							request += "&set=" + set;
						}
						if (from != null) {
							request += "&from="
								 + formatDate(granularity, from);
						}
						if (until != null) {
							request += "&until="
								 + formatDate(granularity, until);
						}
						reqMessage = "A request for ListRecords has been made. Establishing connection with the data provider...";
					}
					else {
						reqMessage = "A request for ListRecords with resumptionToken " + resumption + " has been made. Establishing connection with the data provider...";

						prtln("\n\nResumption unencoded is: " + resumption + "\n\n");
						try {
							resumption = encode(resumption);
						} catch (Exception e) {}
						request += "?verb=" + verb
							 + "&resumptionToken=" + resumption;
						//prtln("\n\nResumption encoded is: " + resumption + "\n\n");
					}

					if (msgHandler != null) {
						msgHandler.statusMessage(reqMessage);
					}

					// Perform the harvest...
					prtln("sending request '" + request + "'");
					Document doc = getDoc(request);
					if (bugs >= 10) {
						try {
							prtln("\n========== begin doc");
							XMLSerializer ser = new XMLSerializer(System.out, null);
							ser.serialize(doc);
							prtln("\n========== end doc\n");
						} catch (IOException ioe) {
							throw new Hexception("cannot serialize: " + ioe);
						}
					}
					resumption = extractRecords(prefix, doc, reslist, outdir, baseURL, splitBySet, writeHeaders);
					if (resumption == null) {
						break;
					}
				}
			}

			if (set == null) {
				set = "";
			}

			String[][] resmat = null;
			if (outdir == null) {
				resmat = (String[][]) reslist.toArray(new String[0][0]);
			}

			/* z = Count(oD);
			prtln("Count at end is " + z);
			if ((splitBySet != true) && (recordCount != z)) {
				recordCount = z;
			}
			prtln("recordCount is " + recordCount); */
			//Create a zip file of this harvest so that it can be directly downloaded from the UI
			File harvestedToDir = new File(outdir);
			File[] harvestedFiles = harvestedToDir.listFiles();
			String fullZipFileName = null;
			if (harvestedFiles == null || harvestedFiles.length == 0) {
				// Do nothing...
			}
			else if (zipName == null || zipName.trim().length() == 0) {
				// Do nothing...
			}
			// Make the zip:
			else {
				errorMsg = null;

				fullZipFileName = new File(zipName + ".zip").getAbsolutePath();
				if (msgHandler != null)
					msgHandler.statusMessage("Zipping harvested files to '" + fullZipFileName + "'");

				File zD = new File(zDir);
				prtln("Zip dir is: " + zD);
				if (!(zD.exists())) {

					boolean create_dir_success = false;
					try {
						create_dir_success = zD.mkdirs();
					} catch (Throwable t) {
						errorMsg = "Unable to create zip file directory'" + zD.getAbsolutePath() + "'. " + t.getMessage();
					}
					if (!create_dir_success) {
						if (!zD.canWrite())
							errorMsg = "Unable to create zip file directory '" + zD.getAbsolutePath() + "'. No write permissions.";
						else if (!zD.canRead())
							errorMsg = "Unable to create zip file directory '" + zD.getAbsolutePath() + "'. No read permissions.";
						else
							errorMsg = "Unable to create zip file directory '" + zD.getAbsolutePath() + "'";
					}
				}

				// Create the zip:
				if (errorMsg == null) {
					try {
						ZipFile zipFile = new ZipFile(fullZipFileName);
						zipFile.compress(harvestedToDir.getAbsolutePath());
					} catch (Throwable t) {
						errorMsg = "Unable to create zip archive: " + t.getMessage();
					}
				}
				// If problem, generate an error:
				else {
					errorMsg += " The harvest was completed but the zip archive of files could not be generated.";
					endTime = System.currentTimeMillis();
					if (msgHandler != null)
						msgHandler.errorMessage(errorMsg);
					throw new Hexception(errorMsg);
				}
			}

			endTime = System.currentTimeMillis();
			//int c = Count(oD);
			int c = recordCount;
			if (msgHandler != null) {
				msgHandler.completedHarvestMessage(
					c,
					resumpCount,
					baseURL,
					set,
					startTime,
					endTime,
					fullZipFileName,
					getGranularityString(),
					getDeletedRecordString());
			}
			isRunning = false;

			return resmat;
		} catch (Hexception he) {
			endTime = System.currentTimeMillis();
			if (msgHandler != null) {
				msgHandler.errorMessage(he.getMessage());
			}
			throw new Hexception(he.getMessage());
		} catch (OAIErrorException oaie) {
			endTime = System.currentTimeMillis();
			if (msgHandler != null) {
				msgHandler.oaiErrorMessage(oaie.getOAIErrorCode(), oaie.getOAIErrorMessage(), getGranularityString(), getDeletedRecordString());
			}
			throw new OAIErrorException(oaie.getOAIErrorCode(), oaie.getOAIErrorMessage());
		} catch (Throwable e) {
			endTime = System.currentTimeMillis();
			if (msgHandler != null) {
				msgHandler.errorMessage("Internal harvester error: " + e);
			}
			throw new Hexception("Internal harvester error: " + e);
		} finally {
			isRunning = false;
		}

	}


	private String getGranularityString() {
		if (granularity == GRAN_SECOND)
			return "seconds";
		else if (granularity == GRAN_DAY)
			return "days";
		else
			return null;
	}


	String getDeletedRecordString() {
		if (deleted_record == DELETED_RECORD_TRANSIENT)
			return "transient";
		else if (deleted_record == DELETED_RECORD_NO)
			return "no";
		else if (deleted_record == DELETED_RECORD_PERSISTENT)
			return "persistent";
		else
			return null;
	}


	/**
	 *  Counts the number of files in a directory
	 *
	 * @param  dir             the directory in question
	 * @return                 the total number of files in dir
	 * @exception  Hexception  If error
	 */
	private int Count(File dir) throws Hexception {
		//return 0;

		if (!dir.isDirectory()) {
			throw new Hexception("File " + dir.getName() + " is not a directory.");
		}

		int total = 0;
		String[] files = dir.list();
		String thisDir = dir.getPath();

		// count non-directory files in this directory and recurse for each
		// that IS a directory
		for (int k = 0; k < files.length; k++) {
			File f = new File(thisDir + File.separator + files[k]);
			if (!f.isDirectory()) {
				total++;
			}
			else {
				total += Count(f);
			}
		}
		return total;
	}


	/**
	 *  Extracts records from an XML document. If outdir is null, appends them to reslist. If outdir is not null,
	 *  writes them to the appropriate subdir of outdir.
	 *
	 * @param  prefix                 The metadata prefix.
	 * @param  doc                    The XML document in OAI format.
	 * @param  reslist                The result list, used only when outdir is null.
	 * @param  outdir                 The output directory.
	 * @param  baseURL                The base URL
	 * @param  writeHeaders           True to have header files written.
	 * @param  splitBySet             Description of the Parameter
	 * @return                        Returns a resumption token.
	 * @exception  Hexception         If serious error.
	 * @exception  OAIErrorException  If OAI error.
	 */
	private String extractRecords(
	                              String prefix,
	                              Document doc,
	                              LinkedList reslist,
	                              String outdir,
	                              String baseURL, boolean splitBySet,
	                              boolean writeHeaders)
		 throws Hexception, OAIErrorException {
		String resumption = null;
		Element root = doc.getDocumentElement();
		Element errele = findChild(root, "error");
		if (errele != null) {
			String oaiErrCode = errele.getAttribute("code");
			String errMsg = getContent(errele);
			if (errMsg == null) {
				errMsg = "";
			}
			throw new OAIErrorException(oaiErrCode, getContent(errele));
		}

		boolean isV1 = false;
		Element verbele = null;
		try {
			verbele = mustFindChild(root, "ListRecords");
		} catch (Hexception e) {
			// Try protocol version 1.1 format:
			verbele = root;
		}

		Element recele = null;
		try {
			recele = mustFindChild(verbele, "record");
		} catch (Hexception e) {
			Element requestURL = findChild(verbele, "requestURL");
			if (requestURL != null) {
				throw new Hexception("No matching records were returned by the data provider (protocol version 1.x)");
			}

			throw new Hexception("The data provider returned an invalid response to the ListRecords request: " +
				e.getMessage());
		}

		while (recele != null) {
			if (killed) {
				throw new Hexception("Harvest received kill signal");
			}

			Element hdr = mustFindChild(recele, "header");
			//String status = hdr.getAttribute("status");
			//if ( status == null || ! status.equalsIgnoreCase("deleted"))
			extractContent(prefix, hdr, reslist, outdir, doc, baseURL, splitBySet, writeHeaders);

			recele = findSibling(recele, "record", "resumptionToken");
			if (recele != null && recele.getNodeName().equals("resumptionToken")) {
				resumption = getContent(recele);
				if (resumption.length() == 0) {
					resumption = null;
				}
				else {
					resumpCount++;
				}
				break;
			}
		}
		return resumption;
	}



	/**
	 *  Extracts the content portion of a single record within an OAI XML document. If outdir is null, appends
	 *  them to reslist. If outdir is not null, writes them to the appropriate subdir of outdir.
	 *
	 * @param  prefix          The metadata prefix.
	 * @param  hdr             The &lt;header&gt; element for the record within the OAI XML document.
	 * @param  reslist         The result list, used only when outdir is null.
	 * @param  outdir          The output directory.
	 * @param  doc             DESCRIPTION
	 * @param  baseURL         DESCRIPTION
	 * @param  writeHeaders    DESCRIPTION
	 * @param  splitBySet      Description of the Parameter
	 * @exception  Hexception  DESCRIPTION
	 */

	private void extractContent(
	                            String prefix,
	                            Element hdr,
	                            LinkedList reslist,
	                            String outdir,
	                            Document doc,
	                            String baseURL, boolean splitBySet,
	                            boolean writeHeaders)
		 throws Hexception {

		boolean deleted = false;
		String status = hdr.getAttribute("status");
		Element identifier = mustFindChild(hdr, "identifier");
		String identstg = getContent(identifier);

		if (status != null && status.equalsIgnoreCase("deleted")) {
			deleted = true;
		}

		Thread.yield();

		Element metadata = null;

		Element contele = null;

		if (!deleted) {
			metadata = findSibling(hdr, "metadata");

			if (metadata == null) {
				Node textNode = identifier.getFirstChild();
				if (textNode != null) {
					prtlnErr("Warning: no metadata element present for identifier " + textNode.getNodeValue());
				}
				else {
					prtlnErr("Warning: no metadata element present for record");
				}
				return;
			}

			// First Element under metadata is the true content
			Node contnode = metadata.getFirstChild();
			while (contnode != null) {
				if (contnode.getNodeType() == Node.ELEMENT_NODE) {
					break;
				}
				contnode = contnode.getNextSibling();
			}
			if (contnode == null) {
				Node textNode = identifier.getFirstChild();
				if (textNode != null) {
					prtlnErr("Warning: no metadata element present for identifier " + textNode.getNodeValue());
				}
				else {
					prtlnErr("Warning: no metadata element present for record");
				}
				return;
			}
			contele = (Element) contnode;
		}

		Element datestamp = mustFindSibling(identifier, "datestamp");

		// The setSpec may be omitted ...
		LinkedList setlist = new LinkedList();
		Element setSpec = findSibling(datestamp, "setSpec");
		while (setSpec != null) {
			setlist.add(getContent(setSpec));
			setSpec = findSibling(setSpec, "setSpec");
		}
		if (setlist.size() == 0) {
			setlist.add("");
		}
		// if omitted, use default

		// Place the harvested records in an array if no output dir is specified
		if (outdir == null) {
			if (deleted) {
				reslist.add(new String[]{
					encode(identstg), "deleted"});
			}
			else {
				Writer wtr = new StringWriter();
				try {
					XMLSerializer ser = new XMLSerializer(wtr, null);
					ser.serialize(contele);
					wtr.close();
					recordCount++;
				} catch (IOException ioe) {
					throw new Hexception("cannot serialize.  reason: " + ioe);
				}
				reslist.add(new String[]{
					encode(identstg), wtr.toString()});
				if (recordCount % messagingNum == 0) {
					if (msgHandler != null) {
						msgHandler.statusMessage(recordCount, resumpCount);
					}
				}
			}
		}

		// Save the harvested records to files if output dir is specified
		else {
			Iterator iter = setlist.iterator();
			boolean savedAtLeastOne = false;
			while (iter.hasNext()) {
				String setname = (String) iter.next();
				String path;

				if (splitBySet == true) {
					//path = OAIUtils.getHarvestedDirPath(outdir, setname, prefix, baseURL);
					path = new File(outdir).getAbsolutePath() + "/" + setname;
				}
				else {
					path = new File(outdir).getAbsolutePath();
				}

				String fnamebase = path + "/" + encode(identstg);

				// Remove files if status deleted.
				if (deleted) {
					File f = new File(fnamebase + ".xml");
					f.delete();
					f = new File(fnamebase + "_hdr.xml");
					f.delete();
				}
				else {
					File new_dir = new File(path);

					if (!(new_dir.exists())) {
						String errorMsg = null;
						boolean create_dir_success = false;
						try {
							create_dir_success = new_dir.mkdirs();
						} catch (Throwable t) {
							errorMsg = "Unable to create file directory'" + new_dir.getAbsolutePath() + "'. " + t.getMessage();
						}
						if (!create_dir_success) {
							if (!new_dir.canWrite())
								errorMsg = "Unable to create file directory '" + new_dir.getAbsolutePath() + "'. No write permissions.";
							else if (!new_dir.canRead())
								errorMsg = "Unable to create file directory '" + new_dir.getAbsolutePath() + "'. No read permissions.";
							else
								errorMsg = "Unable to create file directory '" + new_dir.getAbsolutePath() + "'";
						}

						// Generate an error:
						if (errorMsg != null) {
							endTime = System.currentTimeMillis();
							if (msgHandler != null)
								msgHandler.errorMessage(errorMsg);
							throw new Hexception(errorMsg);
						}
					}

					if (bugs >= 1) {
						prtln("fnamebase: \"" + fnamebase + "\"");
					}
					if (writeHeaders) {
						writedoc(fnamebase + "_hdr.xml", hdr, doc);
					}
					writedoc(fnamebase + ".xml", contele, doc);
					savedAtLeastOne = true;
				}
			}

			if (savedAtLeastOne)
				recordCount++;

			if (recordCount % messagingNum == 0) {
				if (msgHandler != null) {
					msgHandler.statusMessage(recordCount, resumpCount);
				}
			}
		}
	}


	/**
	 *  Retrieves an OAI XML document via http and parses the XML.
	 *
	 * @param  request         The http request, e.g., "http://www.x.com/..."
	 * @return                 The doc value
	 * @exception  Hexception  DESCRIPTION
	 */

	private Document getDoc(
	                        String request)
		 throws Hexception {

		if (bugs >= 1) {
			prtln("getDoc: request: \"" + request + "\"");
		}
		Document doc = null;
		try {
			InputStream istm = TimedURLConnection.getInputStream(request, timeOutMilliseconds, _userAgent);

			DocumentBuilderFactory docfactory
				 = DocumentBuilderFactory.newInstance();
			docfactory.setCoalescing(true);
			docfactory.setExpandEntityReferences(true);
			docfactory.setIgnoringComments(true);

			docfactory.setNamespaceAware(true);

			// We must set validation false since jdk1.4 parser
			// doesn't know about schemas.
			docfactory.setValidating(false);

			// Ignore whitespace doesn't work unless setValidating(true),
			// according to javadocs.
			docfactory.setIgnoringElementContentWhitespace(false);

			DocumentBuilder docbuilder = docfactory.newDocumentBuilder();

			xmlerrors = "";
			xmlwarnings = "";
			docbuilder.setErrorHandler(this);
			doc = docbuilder.parse(istm);
			istm.close();
			if (xmlerrors.length() > 0 || xmlwarnings.length() > 0) {
				String msg = "XML validation failed.\n";
				if (xmlerrors.length() > 0) {
					msg += "Errors:\n" + xmlerrors;
				}
				if (xmlwarnings.length() > 0) {
					msg += "Warnings:\n" + xmlwarnings;
				}
				throw new Hexception(msg);
			}
		} catch (URLConnectionTimedOutException uctoe) {
			throw new Hexception(uctoe.getMessage());
		} catch (Exception exc) {
			String msg = "";
			if (exc.getMessage().matches(".*respcode.*")) {
				msg =
					"The request for data resulted in an invalid response from the provider." +
					" The baseURL indicated may be incorrect or the service may be unavailable." +
					" HTTP response: " + exc.getMessage();
			}
			else {
				msg =
					"The request for data resulted in an invalid response from the provider. Error: " +
					exc.getMessage();
			}
			throw new Hexception(msg);
		}
		return doc;
	}



	/**
	 *  Writes the given element's subtree to the specified file.
	 *
	 * @param  fname           The output file.
	 * @param  ele             The xml element subtree.
	 * @param  doc             DESCRIPTION
	 * @exception  Hexception  DESCRIPTION
	 */

	private void writedoc(
	                      String fname,
	                      Element ele,
	                      Document doc)
		 throws Hexception {
		try {
			FileOutputStream fos = new FileOutputStream(fname);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			Writer wtr = new BufferedWriter(osw);
			OutputFormat format = new OutputFormat(doc, "UTF-8", true);
			// Indenting true
			format.setMethod("xml");
			// May not ne necessary to call this
			format.setLineWidth(0);
			// No line wrapping
			XMLSerializer ser = new XMLSerializer(wtr, format);
			ser.serialize(ele);
			fos.close();
			osw.close();
			wtr.close();
		} catch (IOException ioe) {
			throw new Hexception("cannot write file \"" + fname
				 + "\"  reason: " + ioe);
		}
	}



	/**
	 *  Finds the first immediate child of the specified Element having the specified tag; throws Hexception if
	 *  none found.
	 *
	 * @param  ele             DESCRIPTION
	 * @param  tag             DESCRIPTION
	 * @return                 DESCRIPTION
	 * @exception  Hexception  DESCRIPTION
	 */

	private Element mustFindChild(Element ele, String tag)
		 throws Hexception {
		Element res = findChild(ele, tag);
		if (res == null) {
			throw new Hexception("Element not found: \"" + tag + "\"");
		}
		return res;
	}



	/**
	 *  Finds the first immediate child of the specified Element having the specified tag; returns null if none
	 *  found.
	 *
	 * @param  ele  DESCRIPTION
	 * @param  tag  DESCRIPTION
	 * @return      DESCRIPTION
	 */

	private Element findChild(Element ele, String tag) {
		Element res = null;
		Node nd = ele.getFirstChild();
		while (nd != null) {
			if (nd.getNodeType() == Node.ELEMENT_NODE
				 && nd.getNodeName().equals(tag)) {
				res = (Element) nd;
				break;
			}
			nd = nd.getNextSibling();
		}
		return res;
	}



	/**
	 *  Finds the first following sibling of the specified Element having the specified tag; throws Hexception if
	 *  none found.
	 *
	 * @param  ele             DESCRIPTION
	 * @param  tag             DESCRIPTION
	 * @return                 DESCRIPTION
	 * @exception  Hexception  DESCRIPTION
	 */

	private Element mustFindSibling(Element ele, String tag)
		 throws Hexception {
		Element res = findSibling(ele, tag);
		if (res == null) {
			throw new Hexception("Element not found: \"" + tag + "\"");
		}
		return res;
	}



	/**
	 *  Finds the first following sibling of the specified Element having the specified tag; returns null if none
	 *  found.
	 *
	 * @param  ele  DESCRIPTION
	 * @param  tag  DESCRIPTION
	 * @return      DESCRIPTION
	 */

	private Element findSibling(Element ele, String tag) {
		Element res = null;
		Node nd = ele.getNextSibling();
		while (nd != null) {
			if (nd.getNodeType() == Node.ELEMENT_NODE
				 && nd.getNodeName().equals(tag)) {
				res = (Element) nd;
				break;
			}
			nd = nd.getNextSibling();
		}
		return res;
	}



	/**
	 *  Finds the first following sibling of the specified Element having either of the specified tags; returns
	 *  null if none found.
	 *
	 * @param  ele   DESCRIPTION
	 * @param  taga  DESCRIPTION
	 * @param  tagb  DESCRIPTION
	 * @return       DESCRIPTION
	 */

	private Element findSibling(Element ele, String taga, String tagb) {
		Element res = null;
		Node nd = ele.getNextSibling();
		while (nd != null) {
			if (nd.getNodeType() == Node.ELEMENT_NODE
				 && (nd.getNodeName().equals(taga)
				 || nd.getNodeName().equals(tagb))) {
				res = (Element) nd;
				break;
			}
			nd = nd.getNextSibling();
		}
		return res;
	}



	/**
	 *  Returns the concatenation of all text node content under the specified node.
	 *
	 * @param  nd  DESCRIPTION
	 * @return     The content value
	 */

	private String getContent(Node nd) {
		StringBuffer resbuf = new StringBuffer();
		getContentSub(nd, resbuf);
		return resbuf.toString();
	}


	/**
	 *  Appends to resbuf the concatenation of all text node content under the specified node.
	 *
	 * @param  nd      DESCRIPTION
	 * @param  resbuf  DESCRIPTION
	 */
	private void getContentSub(Node nd, StringBuffer resbuf) {
		switch (nd.getNodeType()) {
						case Node.TEXT_NODE:
						case Node.CDATA_SECTION_NODE:
							resbuf.append(nd.getNodeValue().trim());
							break;
						case Node.ELEMENT_NODE:
							// recurse on children
							Node subnd = nd.getFirstChild();
							while (subnd != null) {
								getContentSub(subnd, resbuf);
								subnd = subnd.getNextSibling();
							}
							break;
						default:
						// ignore all else
		}
	}



	/**
	 *  Sets the granularity and deleted record support of the data provider being harvested. Granularity is used
	 *  for the "from" and "until" arguments: either GRAN_DAY or GRAN_SECOND. Deleted record support is used to
	 *  determine whether incremental updates are supported.
	 *
	 * @param  baseURL                The baseURL
	 * @exception  Hexception         If serious error
	 * @exception  OAIErrorException  If OAI error
	 */

	private void getIdentifyInfo(String baseURL)
		 throws Hexception, OAIErrorException {
		int ii;

		String request = baseURL + "?verb=Identify";
		if (msgHandler != null) {
			msgHandler.statusMessage("A request for Identify has been made. Establishing connection with the data provider...");
		}
		Document doc = getDoc(request);

		Element root = doc.getDocumentElement();
		Element errele = findChild(root, "error");
		if (errele != null) {
			String oaiErrCode = errele.getAttribute("code");
			String errMsg = getContent(errele);
			if (errMsg == null) {
				errMsg = "";
			}
			throw new OAIErrorException(oaiErrCode, getContent(errele));
		}
		Element granele = null;
		try {
			Element verbele = mustFindChild(root, "Identify");
			granele = mustFindChild(verbele, "granularity");
		} catch (Hexception e) {
			// Check for protocol version v1.x:
			try {
				Element protocolVersion = mustFindChild(root, "protocolVersion");
			} catch (Throwable te) {
				throw new Hexception("The data provider returned an invalid response to the Identify request: " +
					e.getMessage());
			}
			granularity = GRAN_DAY;
		}

		// Set the supported date granularity
		String gran = getContent(granele);

		if (gran.equals("YYYY-MM-DD")) {
			granularity = GRAN_DAY;
			if (bugs >= 1) {
				prtln("granularity: day");
			}
		}
		else if (gran.toLowerCase().equals("yyyy-mm-ddthh:mm:ssz")) {
			granularity = GRAN_SECOND;
			if (bugs >= 1) {
				prtln("granularity: second");
			}
		}
		else {
			throw new Hexception("provider supports an invalid granularity according to the OAI protocol. Invalid response: " + gran);
		}

		Element deletedredele = null;
		try {
			Element verbele = mustFindChild(root, "Identify");
			deletedredele = mustFindChild(verbele, "deletedRecord");
		} catch (Hexception e) {
			throw new Hexception("The data provider returned an invalid response to the Identify request: " +
				e.getMessage());
		}

		// Set the level of deleted record support
		String deletedRecord = getContent(deletedredele);

		if (deletedRecord.equalsIgnoreCase("no")) {
			deleted_record = DELETED_RECORD_NO;
			if (bugs >= 1) {
				prtln("deleted record: no");
			}
		}
		else if (deletedRecord.equalsIgnoreCase("transient")) {
			deleted_record = DELETED_RECORD_TRANSIENT;
			if (bugs >= 1) {
				prtln("deleted record: transient");
			}
		}
		else if (deletedRecord.equalsIgnoreCase("persistent")) {
			deleted_record = DELETED_RECORD_PERSISTENT;
			if (bugs >= 1) {
				prtln("deleted record: persistent");
			}
		}
		else {
			throw new Hexception("provider shows an invalid deleted record support according to the OAI protocol. Invalid response: " + deletedRecord);
		}

	}



	/**
	 *  Returns an array of the legal metadataFormats for the specified host.
	 *
	 * @param  baseURL                DESCRIPTION
	 * @return                        The prefices value
	 * @exception  Hexception         DESCRIPTION
	 * @exception  OAIErrorException  DESCRIPTION
	 */

	private String[] getPrefices(String baseURL)
		 throws Hexception, OAIErrorException {
		int ii;

		String request = baseURL + "?verb=ListMetadataFormats";
		if (msgHandler != null) {
			msgHandler.statusMessage("A request for ListMetadataFormats has been made. Establishing connection with the data provider...");
		}
		Document doc = getDoc(request);

		Element root = doc.getDocumentElement();
		Element errele = findChild(root, "error");
		if (errele != null) {
			String oaiErrCode = errele.getAttribute("code");
			String errMsg = getContent(errele);
			if (errMsg == null) {
				errMsg = "";
			}
			throw new OAIErrorException(oaiErrCode, getContent(errele));
		}
		Element verbele = mustFindChild(root, "ListMetadataFormats");

		LinkedList reslist = new LinkedList();
		Element pfxele = mustFindChild(verbele, "metadataFormat");
		while (pfxele != null) {
			Element pfxspec = mustFindChild(pfxele, "metadataPrefix");
			reslist.add(getContent(pfxspec));
			pfxele = findSibling(pfxele, "metadataFormat");
		}
		String[] prefices = new String[reslist.size()];
		for (ii = 0; ii < prefices.length; ii++) {
			String prefix = (String) reslist.get(ii);
			prefices[ii] = prefix.replaceAll(":", "/");
			if (bugs >= 1) {
				prtln("prefix: \"" + prefices[ii] + "\"");
			}
		}
		return prefices;
	}


	/**
	 *  Encode an identifier or resumption token. Can also be used to encode a file name.<p>
	 *
	 *  See section 3.1.1.3 in http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm
	 *
	 * @param  msg             A String to encode.
	 * @return                 Encoded String
	 * @exception  Hexception  If error.
	 */
	private String encode(String msg)
		 throws Hexception {
		try {
			return OAIUtils.encode(msg);
		} catch (Exception e) {
			throw new Hexception(e.getMessage());
		}
	}


	/**
	 *  Formats a date as specified in section 3.3 of http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm.
	 *  <p>
	 *
	 *  If granularity is GRAN_DAY, the format is "yyyy-MM-dd". If granularity is GRAN_SECOND, the format is
	 *  "yyyy-MM-ddTHH:mm:ssZ".
	 *
	 * @param  granularity  GRAN_DAY or GRAN_SECOND
	 * @param  dt           The Date
	 * @return              An OAI datestamp
	 */
	private static String formatDate(int granularity, Date dt) {
		SimpleDateFormat sdf = null;
		if (granularity == GRAN_SECOND) {
			sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
		}
		else {
			sdf = new SimpleDateFormat("yyyy-MM-dd");
		}

		String res = sdf.format(dt);
		return res;
	}


	/**
	 *  Parses a date string as specified in section 3.3 of http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm.
	 *  <p>
	 *
	 *  If the date contains ":", the format is "yyyy-MM-ddTHH:mm:ssZ". Otherwise the format is "yyyy-MM-dd".
	 *
	 * @param  stg             DESCRIPTION
	 * @return                 DESCRIPTION
	 * @exception  Hexception  DESCRIPTION
	 */
	private static Date parseDate(String stg)
		 throws Hexception {
		Date res = null;
		if (stg != null && stg.length() > 0) {
			SimpleDateFormat sdf = null;
			if (stg.indexOf(":") >= 0) {
				sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
			}
			else {
				sdf = new SimpleDateFormat("yyyy-MM-dd");
			}

			try {
				res = sdf.parse(stg.toUpperCase());
			} catch (ParseException pex) {
				throw new Hexception("invalid date: \"" + stg + "\"");
			}
		}
		prtln("parseDate() returning: " + res.toString());
		return res;
	}


	/**
	 *  Return a string for the current time and date, sutiable for display in log files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	private final static String getDateStamp() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final static void prtlnErr(String s) {
		System.err.println(getDateStamp() + " Harvester: ERROR: " + s);
	}



	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final static void prtln(String s) {
		if (debug) {
			System.out.println(getDateStamp() + " Harvester: " + s);
		}
	}


	/**
	 *  Sets the debug attribute object
	 *
	 * @param  db  The new debug value
	 */
	public static void setDebug(boolean db) {
		debug = db;
	}


	/**
	 *  Handles fatal errors. Part of ErrorHandler interface.
	 *
	 * @param  exc  The Exception thrown
	 */
	public void fatalError(SAXParseException exc) {
		xmlerrors += exc;
	}


	/**
	 *  Handles errors. Part of ErrorHandler interface.
	 *
	 * @param  exc  The Exception thrown
	 */
	public void error(SAXParseException exc) {
		xmlerrors += exc;
	}


	/**
	 *  Handles warnings. Part of ErrorHandler interface.
	 *
	 * @param  exc  The Exception thrown
	 */
	public void warning(SAXParseException exc) {
		xmlwarnings += exc;
	}

}

