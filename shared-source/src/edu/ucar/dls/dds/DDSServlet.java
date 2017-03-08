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
package edu.ucar.dls.dds;

import edu.ucar.dls.dds.nr.NRIndexer3;
import edu.ucar.dls.repository.*;
import edu.ucar.dls.repository.indexing.*;
import edu.ucar.dls.repository.action.*;
import edu.ucar.dls.datamgr.*;
import edu.ucar.dls.repository.action.form.*;
import edu.ucar.dls.action.*;
import edu.ucar.dls.ndr.request.*;
import edu.ucar.dls.services.dds.action.*;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import edu.ucar.dls.index.*;
import edu.ucar.dls.index.writer.*;
import edu.ucar.dls.vocab.*;
import java.text.*;
import edu.ucar.dls.webapps.tools.*;
import edu.ucar.dls.dds.action.*;

import edu.ucar.dls.serviceclients.asn.CachingAsnResolutionServiceClient;
import edu.ucar.dls.standards.asn.*;

/****
 *  Provided as an administrative and intialization servlet for the Digital Discovery System (DDS).
 *
 * @author    John Weatherley, Dave Deniman, Ryan Deardorff
 */
public class DDSServlet extends HttpServlet {

	final static int VALID_REQUEST = 0;
	final static int REQUEST_EXCEPTION = -1;
	final static int UNRECOGNIZED_REQUEST = 1;
	final static int NO_REQUEST_PARAMS = 2;
	final static int INITIALIZING = 3;
	final static int NOT_INITIALIZED = 4;
	final static int INVALID_CONTEXT = 5;
	final static String DIRECTORY_DATA_DIR = "file_monitor_metadata";
	private String ddsContext = "unknown";

	// UUID should be set at starup to a unique string for each instance of DDS to avoid file clashes across instances:
	public static String DDS_UUID = "dds_uuid";

	
	private static boolean isInitialized = false;
	private boolean debug = true;
	private Timer _importRepositoriesTimer=null;
	private Timer _archiveIndexDataBeanTimer=null;
	
	// This frequency is the frequency in seconds on how often we should check for new repositories
	// in the import destination folders
	private long _importRepositoriesFrequency = 3600;
	
	// Frequency of how often the repository has a snap shot of its metadata for analytics.
	private long _archiveIndexDataBeanFrequency = 86400;
	
	/**  Constructor for the DDSServlet object */
	public DDSServlet() { }


	/**
	 *  The standard <code>HttpServlet</code> init method, called only when the servlet is first loaded.
	 *
	 * @param  servletConfig         The config
	 * @exception  ServletException
	 */
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		try {

			if (isInitialized) {
				prtlnErr("DDS has already been initialized. Call to DDSServlet.init() aborted...");
				return;
			}

			isInitialized = true;

			ServletContext servletContext = getServletContext();
			// Verbose debugging messages?
			if (((String) servletContext.getInitParameter("debug")).equalsIgnoreCase("true")) {
				debug = true;
				prtln("Outputting debug info");
			}
			else {
				debug = false;
				prtln("Debug info disabled");
			}

            // Set the UUID for the DDS instance:
            String ddsUuid = servletContext.getInitParameter("DDS_UUID");
            if(ddsUuid != null && ddsUuid.trim().length() > 0 )
                DDS_UUID = ddsUuid;

			// Put DDSServlet into app scope for use in RepositoryAdminAction:
			servletContext.setAttribute("ddsServlet", this);

			// The context in which DDS is running. Possible values: dds-webapps, dcs-webapp
			ddsContext = (String) servletConfig.getInitParameter("ddsContext");
			if (ddsContext == null || ddsContext.trim().length() == 0)
				ddsContext = "unknown";
					

			// Set the max number of service results allowed in DDSWS:
			String maxNumResultsDDSWS = (String) servletContext.getInitParameter("maxNumResultsDDSWS");
			int maxDDSWSResults = 1000;
			try {
				maxDDSWSResults = Integer.parseInt(maxNumResultsDDSWS);
			} catch (Throwable t) {}
			DDSServicesAction.setMaxSearchResults(maxDDSWSResults);
			servletContext.setAttribute("maxNumResultsDDSWS", Integer.toString(maxDDSWSResults));

			// Set all debugging:
			RepositoryManager.setDebug(debug);
			FileIndexingService.setDebug(debug);
			FileIndexingServiceWriter.setDebug(debug);
			SimpleLuceneIndex.setDebug(debug);
			SimpleQueryAction.setDebug(debug);
			RepositoryForm.setDebug(debug);
			SerializedDataManager.setDebug(debug);
			RepositoryAdminAction.setDebug(debug);
			RepositoryAction.setDebug(debug);
			RecordDataService.setDebug(debug);
			DDSAdminQueryAction.setDebug(debug);
			DDSQueryAction.setDebug(debug);
			SimpleNdrRequest.setDebug(debug);
			CollectionIndexer.setDebug(debug);
			NRIndexer3.setDebug(debug);

			
			// Remove any Lucene locks that may have persisted from a previous dirty shut-down:
			String tempDirPath = System.getProperty("java.io.tmpdir");
			if (tempDirPath != null) {
				File tempDir = new File(tempDirPath);

				FilenameFilter luceneLockFilter =
					new FilenameFilter() {
						public boolean accept(File dir, String name) {
							return (name.startsWith("lucene") && name.endsWith(".lock"));
						}
					};

				File[] tempFiles = tempDir.listFiles(luceneLockFilter);

				if (tempFiles != null) {
					for (int i = 0; i < tempFiles.length; i++) {
						prtlnErr("DDSServlet startup: Removing lucene lock file: " + tempFiles[i].getAbsolutePath());
						tempFiles[i].delete();
					}
				}
			}

			
			
			// Set up the ASN standards resolution services used in the indexer:
			File repositoryDataBaseDir = new File(getAbsolutePath((String) servletContext.getInitParameter("repositoryData")));
			File asnResolverClientCacheDir = new File(repositoryDataBaseDir,"asn_standards_cache");

			// AsnResolver for generating ASN standard facet hierarchies:
			String asnResolverBaseUrl = (String) getServletContext().getInitParameter("asnResolverBaseUrl");
			if (asnResolverBaseUrl != null && asnResolverBaseUrl.trim().startsWith("http")) {
				try {
					CachingAsnResolutionServiceClient asnResolutionServiceClient =
						new CachingAsnResolutionServiceClient(asnResolverBaseUrl.trim(), asnResolverClientCacheDir.toString());
					AsnStandardsIdFieldPreprocessor.setAsnResolutionServiceClient(asnResolutionServiceClient);
					AsnStandardsTextFieldPreprocessor.setAsnResolutionServiceClient(asnResolutionServiceClient);
					prtln("setAsnResolutionClient: " + asnResolverBaseUrl);
					if (AsnStandardsIdFieldPreprocessor.getAsnResolutionServiceClient() == null)
						throw new Exception("client has not been properly stowed in AsnCachingStandardsIdFieldPreprocessor");
				} catch (Throwable t) {
					prtlnErr("Unable to instantiate asnResolutionServiceClient: " + t.getMessage());
					t.printStackTrace();
				}
			}
			// Initialize and set up the Repository Managers:
			Maintenance repositoryMaintenance = new Maintenance(getServletContext(), ddsContext, debug);
			servletContext.setAttribute("repositoryMaintenance", repositoryMaintenance);
			repositoryMaintenance.startupRepositories();
			

			String recordDataSource = getRecordDataSource();
			servletContext.setAttribute("recordDataSource", recordDataSource);

			// Set the startup date and log the time:
			servletContext.setAttribute("ddsStartUpDate", new Date());
			
			System.out.println("\n\n" + getDateStamp() + " DDSServlet started for context " + ddsContext + ".\n\n");
			//IndexValidation.validateNewIndex(repositoryManager, 
			//		 repositoryManager,
			//		 getServletContext());
			startImportRepositoriesTimer();
			
			startArchiveIndexDataBeanTimer();
		} catch (Throwable t) {
			prtlnErr("DDSServlet.init() error: " + t);
			t.printStackTrace();
			throw new ServletException(t);
		}
	}
	
	
	
	private synchronized void startArchiveIndexDataBeanTimer() throws Exception {
		stopArchiveIndexDataBeanTimer();
		ServletContext servletContext = getServletContext();
		
		String repositoryAnalyticsData = servletContext.getInitParameter(
		"repositoryAnalyticsData");
		
		long ONE_MINUTE_IN_MILLIS=60000;//millisecs

		long t=new Date().getTime();
		Date afterAddingTenMins=new Date(t + (10 * ONE_MINUTE_IN_MILLIS));
		
		if(repositoryAnalyticsData!=null && !repositoryAnalyticsData.equals(""))
		{	_archiveIndexDataBeanTimer = new Timer(true);	
			_archiveIndexDataBeanTimer.scheduleAtFixedRate(new ArchiveIndexDataBeanTimer(servletContext), 
					afterAddingTenMins, _archiveIndexDataBeanFrequency*1000);
		}
	}
	private class ArchiveIndexDataBeanTimer extends TimerTask {
		private ServletContext servletContext;
		
		public ArchiveIndexDataBeanTimer(ServletContext servletContext)
		{
			this.servletContext = servletContext;
		}
		public void run() {
			try {
				
				Maintenance maintenace = (Maintenance)servletContext.getAttribute("repositoryMaintenance");
				RepositoryInfoBean repositoryInfoBean = maintenace.getRepositoryInfoBean();
				String reposPath = repositoryInfoBean.getCurrentRepository();
				maintenace.archiveIndexDataBean(reposPath);
				
				// Removing the cache for datatypes so it will be re-inited by repository analytics
				servletContext.setAttribute(RepositoryAnalyticsAction.DATA_TYPES_CACHE_KEY, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				prtlnErr(e.getMessage());
				e.printStackTrace();
			}
			
		}
	}
	/**  Stops the import repositories timer. */
	private synchronized void stopArchiveIndexDataBeanTimer() {
		if (_archiveIndexDataBeanTimer != null) {
			_archiveIndexDataBeanTimer.cancel();
			_archiveIndexDataBeanTimer = null;
			prtln("Automatic ArchiveIndexDataBeanTimer timer stopped");
		}
	}

	private synchronized void startImportRepositoriesTimer() throws Exception {
		stopImportRepositoriesTimer();
		ServletContext servletContext = getServletContext();

		if(((Boolean)servletContext.getAttribute("isImportIndex")).booleanValue())
		{	_importRepositoriesTimer = new Timer(true);	
			_importRepositoriesTimer.scheduleAtFixedRate(new ImportRepositoriesTimer(servletContext), 
					new Date(), _importRepositoriesFrequency*1000);
		}
	}
	private class ImportRepositoriesTimer extends TimerTask {
		private ServletContext servletContext;
		
		public ImportRepositoriesTimer(ServletContext servletContext)
		{
			this.servletContext = servletContext;
		}
		public void run() {
			try {
				((Maintenance)servletContext.getAttribute("repositoryMaintenance")).importRepositories();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				prtlnErr(e.getMessage());
				e.printStackTrace();
			}
			
		}
	}
	/**  Stops the import repositories timer. */
	private synchronized void stopImportRepositoriesTimer() {
		if (_importRepositoriesTimer != null) {
			_importRepositoriesTimer.cancel();
			_importRepositoriesTimer = null;
			prtln("Automatic import repositories timer stopped");
		}
	}
 
	private String getRecordDataSource() {
		return (String) getServletContext().getInitParameter("recordDataSource");
	}
	
	/**  Shut down sequence. */
	public void destroy() {
		stopImportRepositoriesTimer();
		IndexingManager indexingManager = (IndexingManager) getServletContext().getAttribute("indexingManager");

		try {
			if (indexingManager != null)
				indexingManager.destroy();
		} catch (Throwable t) {
			prtlnErr("Problem shutting down indexingManager: " + t);
		}

		RepositoryManager repositoryManager = (RepositoryManager) getServletContext().getAttribute("repositoryManager");
		if (repositoryManager != null)
			repositoryManager.destroy();

		RepositoryManager backgroundIndexingRepositoryManager = (RepositoryManager) getServletContext().getAttribute("backgroundIndexingRepositoryManager");
		if (backgroundIndexingRepositoryManager != null)
			backgroundIndexingRepositoryManager.destroy();

		System.out.println("\n\n" + getDateStamp() + " DDSServlet stopped." + "\n\n");
	}


	/**
	 *  Standard doPost method forwards to doGet
	 *
	 * @param  request
	 * @param  response
	 * @exception  ServletException
	 * @exception  IOException
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		 throws ServletException, IOException {
		doGet(request, response);
	}


	/**
	 *  The standard required servlet method, just parses the request header for known parameters. The <code>doPost</code>
	 *  method just calls this one. See {@link HttpServlet} for details.
	 *
	 * @param  request
	 * @param  response
	 * @exception  ServletException
	 * @exception  IOException
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		 throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		int result = handleRequest(request, response, out);
		switch (result) {

						case VALID_REQUEST:
							response.setContentType("text/html");
							// processed a request okay
							return;
						case UNRECOGNIZED_REQUEST:
							// no recognized parameters
							response.setContentType("text/html");
							out.println("Called with unrecognized parameter(s)...");
							return;
						case NO_REQUEST_PARAMS:
							// no paramters
							response.setContentType("text/html");
							out.println("Request did not contain a parameter...");
							return;
						case INITIALIZING:
							response.setContentType("text/html");
							out.println("System is initializing...");
							out.println(" ... initializtion may take less than a second or several minutes.");
							out.println(" ... please try request again.");
							return;
						case NOT_INITIALIZED:
							out.println("System is not initialized...");
							out.println(" ... the server may need to be restarted,");
							out.println(" ... or there is a problem with configuration.");
							out.println("");
							out.println("Please inform support@your.org.");
							out.println("");
							out.println("Thank You");
							return;
						case INVALID_CONTEXT:
							response.setContentType("text/html");
							out.println("A request was recieved, but the context can not be identified...");
							out.println(" ... either  unable to initialize the catalog context," +
								" or the servlet container is in an invalid state.");
							return;
						default:
							// an exception occurred
							response.setContentType("text/html");
							out.println("An unexpected exception occurred processing request...");
							return;
		}
	}


	/**
	 *  Used to provide explicit command parameter processing.
	 *
	 * @param  request
	 * @param  response
	 * @param  out       DESCRIPTION
	 * @return
	 */
	private int handleRequest(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {

		try {
			//if ( catalog != null && monitor != null ) {
			//if (catalog.ready()) {
			Enumeration paramNames = request.getParameterNames();
			if (paramNames.hasMoreElements()) {
				while (paramNames.hasMoreElements()) {
					String paramName = (String) paramNames.nextElement();
					String[] paramValues = request.getParameterValues(paramName);
					// this next section can use an interface and hashmap -
					// see pg 228 of JavaServerPages
					if (paramValues.length == 1) {
						if (paramName.equals("command")) {
							if (paramValues[0].equals("stop")) {
								//fileIndexingService.stopTester();
							}
							return VALID_REQUEST;
							//if(paramValues[0].equals("start"))
							//	fileIndexingService.startTester();

						}
//  								if (paramName.equals("query")) {
//  									//catalog.reinit();
//  									//response.setContentType("text/html");
//  									//PrintWriter out = response.getWriter();
//  									//out.println("CatalogAdmin called Catalog.reinit() ");
//  									//out.println("See Catalog Activity Log for messages.");
//  									return VALID_REQUEST;
//  								}
//  								if (paramName.equals("unlock")) {
//  									//releaseLock(paramValues[0], request, response);
//  									return VALID_REQUEST;
//  								}
					}
				}
				return UNRECOGNIZED_REQUEST;
			}
			return NO_REQUEST_PARAMS;
			//}
			//else if (catalog.initializing())
			//	return CATALOG_INITIALIZING;
			//else
			//	return CATALOG_NOT_INITIALIZED;
			//}
			//return INVALID_CONTEXT;
		} catch (Throwable t) {
			return REQUEST_EXCEPTION;
		}
	}


	/**
	 *  Sets the "noDisplay" property of collection vocab nodes according the results of the repository manager's
	 *  getEnabledSetsHashMap()
	 *
	 * @param  repositoryManager  The new collectionsVocabDisplay value
	 */
	public static void setCollectionsVocabDisplay(RepositoryManager repositoryManager) {
		MetadataVocab vocab = repositoryManager.getMetadataVocab();
		if (vocab == null)
			return;
		Set vi = vocab.getVocabSystemInterfaces();
		Iterator i = vi.iterator();
		while (i.hasNext()) {
			doSetCollectionsVocabDisplay(repositoryManager, (String) i.next());
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  systemInterface    Description of the Parameter
	 * @param  repositoryManager  RepositoryManager
	 */
	private static void doSetCollectionsVocabDisplay(RepositoryManager repositoryManager,
	                                                 String systemInterface) {
		HashMap sets = repositoryManager.getEnabledSetsHashMap();
		MetadataVocab vocab = repositoryManager.getMetadataVocab();
		ArrayList nodes = vocab.getVocabNodes(systemInterface + "/key", "");
		for (int i = 0; i < nodes.size(); i++) {
			if (sets.get(((VocabNode) nodes.get(i)).getId()) == null) {
				((VocabNode) nodes.get(i)).setNoDisplay(true);
			}
			// If the UI groups file said not to display (dwelanno for example), then don't override that:
			else if (!((VocabNode) nodes.get(i)).getNoDisplayOriginal()) {
				((VocabNode) nodes.get(i)).setNoDisplay(false);
			}
		}
	}


	/**
	 *  Gets the absolute path to a given file or directory. Assumes the path passed in is eithr already absolute
	 *  (has leading slash) or is relative to the context root (no leading slash). If the string passed in does
	 *  not begin with a slash ("/"), then the string is converted. For example, an init parameter to a config
	 *  file might be passed in as "WEB-INF/conf/serverParms.conf" and this method will return the corresponding
	 *  absolute path "/export/devel/tomcat/webapps/myApp/WEB-INF/conf/serverParms.conf." <p>
	 *
	 *  If the string that is passed in already begings with "/", nothing is done. <p>
	 *
	 *  Note: the super.init() method must be called prior to using this method, else a ServletException is
	 *  thrown.
	 *
	 * @param  fname                 An absolute or relative file name or path (relative the the context root).
	 * @return                       The absolute path to the given file or path.
	 * @exception  ServletException  An exception related to this servlet
	 */
	private String getAbsolutePath(String fname)
		 throws ServletException {
		return new File(GeneralServletTools.getAbsolutePath(fname, getServletContext())).getAbsolutePath();
	}



	// -------------------- Logging/debugging methods --------------------


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
		System.err.println(getDateStamp() + " DDSServlet Error: " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final void prtln(String s) {
		if (debug) {
			System.out.println(getDateStamp() + " DDSServlet: " + s);
		}
	}


	/**
	 *  Sets the debug attribute of the DDSServlet object
	 *
	 * @param  db  The new debug value
	 */
	public final void setDebug(boolean db) {
		debug = db;
	}
}

