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
package edu.ucar.dls.schemedit;

import edu.ucar.dls.schemedit.repository.*;
import edu.ucar.dls.serviceclients.remotesearch.RemoteSearcher;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.XMLConversionService;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.schemedit.dcs.*;
import edu.ucar.dls.schemedit.input.InputManager;
import edu.ucar.dls.schemedit.config.*;
import edu.ucar.dls.schemedit.threadedservices.*;
import edu.ucar.dls.serviceclients.cat.CATServiceToolkit;

import edu.ucar.dls.webapps.tools.GeneralServletTools;
import edu.ucar.dls.vocab.*;
import edu.ucar.dls.repository.RepositoryManager;
import edu.ucar.dls.repository.SetInfo;
import edu.ucar.dls.repository.action.form.SetDefinitionsForm;

import java.io.File;
import java.util.*;
import java.text.SimpleDateFormat;
import java.net.URL;

// Enterprise imports
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 *  Servlet responsible for initializing widely-used classes and placing them
 *  into the servlet context as attributes.
 *
 * @author    Jonathan Ostwald
 */

public final class SchemEditServlet extends HttpServlet {

	private boolean debug = true;
	private FrameworkRegistry frameworkRegistry;
	private CollectionRegistry collectionRegistry;
	private DcsDataManager dcsDataManager;
	private RemoteSearcher rs;


	/**
	 *  Description of the Method
	 *
	 * @param  config                Description of the Parameter
	 * @exception  ServletException  Description of the Exception
	 */
	public void init(ServletConfig config)
		 throws ServletException {
		System.out.println("\n" + getDateStamp() + " SchemEditServlet starting");
		String initErrorMsg = "";
		try {
			super.init(config);
		} catch (Throwable exc) {
			initErrorMsg = "SchemEditServlet Initialization Error:\n  " + exc;
			prtlnErr(initErrorMsg);
		}

		try {
			ServletContext servletContext = getServletContext();

			String instanceName = (String) servletContext.getInitParameter("instanceName");
			servletContext.setAttribute("instanceName", instanceName);
			prtln("instanceName: " + instanceName);

			String readOnlyMode = (String) servletContext.getInitParameter("readOnlyMode");
			servletContext.setAttribute("readOnlyMode", "true".equals(readOnlyMode));
			prtln("readOnlyMode: " + readOnlyMode);

			String preserveWhiteSpace = (String) servletContext.getInitParameter("preserveWhiteSpace");
			InputManager.setPreserveWhiteSpace("true".equals(preserveWhiteSpace));
			prtln("preserveWhiteSpace: " + InputManager.getPreserveWhiteSpace());

			String catalogingInfo = (String) servletContext.getInitParameter("catalogingInfo");
			servletContext.setAttribute("catalogingInfo", catalogingInfo);
			// prtln("catalogingInfo: " + catalogingInfo);

			/*
			 *  String instanceHelp = (String) servletContext.getInitParameter("instanceHelp");
			 *  servletContext.setAttribute("instanceHelp", instanceHelp);
			 */
			MetadataVocab vocab = (MetadataVocab) servletContext.getAttribute("MetadataVocab");
			if (vocab == null) {
				throw new ServletException("ERROR: \'MetadataVocab\' not found in servleContext");
			}

			RoleManager roleManager = new RoleManager(servletContext);
			servletContext.setAttribute("roleManager", roleManager);

			frameworkRegistry = new FrameworkRegistry(servletContext);
			servletContext.setAttribute("frameworkRegistry", frameworkRegistry);
			prtln("\"frameworkRegistry\" initialized with " + frameworkRegistry.size() + " frameworks");

			MetadataVocabServlet metadataVocabServlet =
				(MetadataVocabServlet) servletContext.getAttribute("MetadataVocabServlet");
			if (metadataVocabServlet != null) {
				metadataVocabServlet.addListener(frameworkRegistry);
			}
			else {
				prtln("WARNING: metadataVocabServlet not found in servlet context");
			}

			String pageBannerPath = (String) servletContext.getInitParameter("pageBanner");
			if (pageBannerPath != null) {
				File pageBannerFile = new File(pageBannerPath.trim());
				String pageBanner = null;
				try {
					pageBanner = Files.readFile(pageBannerFile).toString();
				} catch (Throwable e) {
					prtlnErr("ERROR: could not read pageBanner file at " + pageBannerPath);
				}
				if (pageBanner != null) {
					servletContext.setAttribute("pageBanner", pageBanner);
					prtln("pageBanner (contents of " + pageBannerPath + ") set in servletContext");
				}
			}

			// set up CollectionRegistry
			try {
				String defaultCollConfigPath = getAbsolutePath("WEB-INF/data/default-collection-config.xml");

				File idFilesDir = (File) servletContext.getAttribute("idFilesDir");
				if (!idFilesDir.exists()) {
					prtlnErr("WARNING: id files directory does not exist");
				}
				File collectionConfigDir = (File) servletContext.getAttribute("collectionConfigDir");
				if (collectionConfigDir == null || !collectionConfigDir.exists()) {
					throw new Exception("init parameter \"collectionConfigDir\" not found in servlet context");
				}
				else {
					prtln("collectionConfigDir is " + collectionConfigDir);
				}
				collectionRegistry = new CollectionRegistry(collectionConfigDir,
					idFilesDir.getAbsolutePath(),
					defaultCollConfigPath);
				servletContext.setAttribute("collectionRegistry", collectionRegistry);

				prtln("\"collectionRegistry\" initialized with " + collectionRegistry.size() + " collections");
			} catch (Throwable t) {
				throw new ServletException("ERROR: could not instantiate collectionRegistry: " + t.getMessage());
			}

			RepositoryManager rm = (RepositoryManager) getServletContext().getAttribute("repositoryManager");
			if (rm == null) {
				throw new Exception("repository manager not found in servlet context");
			}

			// set up remote searcher
			String ddsWebServicesBaseUrl = (String) servletContext.getInitParameter("ddsWebServicesBaseUrl");
			rs = new RemoteSearcher(ddsWebServicesBaseUrl, vocab);
			if (rs == null) {
				prtln("WARNING: unable to instantiate \'RemoteSearcher\'");
			}
			servletContext.setAttribute("RemoteSearcher", rs);
			prtln("set \'RemoteSearcher\' in servlet context");

			// initialize dcsDataManager - requires the dcs_data MetaDataFramework
			MetaDataFramework dcsDataFramework = frameworkRegistry.getFramework("dcs_data");
			if (dcsDataFramework == null) {
				prtlnErr("WARNING: dcsDataFramework not found in frameworkRegistry");
			}
			dcsDataManager = new DcsDataManager(rm, dcsDataFramework, collectionRegistry);
			if (dcsDataManager == null) {
				throw new ServletException("failed to initialize dcsDataManager");
			}
			else {
				servletContext.setAttribute("dcsDataManager", dcsDataManager);
				/*
				 *  explicitly call loadCollectionRecords once the dcsDataManager is
				 *  available to DcsFileIndexingPlugIn. The collectionRecords may have already
				 *  been loaded, but without the dcsDataManager, DcsFileIndexingPlugin can't
				 *  access the data it needs to index.
				 */
				// rm.loadCollectionRecords(true);
				prtln("dcsDataManager initialized");
			}

			// WorkFlowServices
			WorkFlowServices workFlowServices = new WorkFlowServices(servletContext);
			dcsDataManager.addListener(workFlowServices);
			// servletContext.setAttribute("workFlowServices", workFlowServices);

			// ExportingService and ValidatingService
			String repositoryData = getAbsolutePath((String) servletContext.getInitParameter("repositoryData"));
			String exportBaseDir = getAbsolutePath((String) servletContext.getInitParameter("exportBaseDir"));
			String contributorOutXform = getAbsolutePath((String) servletContext.getInitParameter("contributorOutXform"));

			ExportingService exportingService = new ExportingService(servletContext, repositoryData + "/exporting_service_data", exportBaseDir, contributorOutXform);
			servletContext.setAttribute("exportingService", exportingService);

			ValidatingService validatingService = new ValidatingService(servletContext, repositoryData + "/validating_service_data");
			servletContext.setAttribute("validatingService", validatingService);

			if (servletContext.getAttribute("suggestionServiceManager") != null) {
				/* Threaded ASN Loader */
				prtln("initializing ASN asnFetchService");
				AsnFetchService asnFetchService = new AsnFetchService(servletContext, repositoryData + "/asn_fetch_service_data");
				servletContext.setAttribute("asnFetchService", asnFetchService);
				prtln("asnFetchService placed in servlet context");
			}

			try {
				collectionRegistry.initializeIDGenerators(rm.getIndex());
			} catch (Exception e) {
				throw new ServletException("WARNING: idGenerators could not be initialized: " + e.getMessage());
			}

			RepositoryService repositoryService = new RepositoryService(servletContext);
			servletContext.setAttribute("repositoryService", repositoryService);
			if (servletContext.getAttribute("repositoryService") != null) {
				prtln("initialized repositoryService");
			}
			else {
				throw new ServletException("repositoryService was not initialized");
			}

			this.setRepositoryWriterPlugins(repositoryService);

			this.setRepositoryEventListeners(repositoryService);

			/*
			 *  Ensure that the ndrServiceEnabled context attribute has a value.
			 *  This context attribute is set by NdrServlet, so if it is not set at this point
			 *  we set it to false and it will be overwritten by NdrServlet if that servlet is
			 *  configured to run.
			 */
			if (servletContext.getAttribute("ndrServiceEnabled") == null) {
				servletContext.setAttribute("ndrServiceEnabled", false);
			}

			// if OAI-PMH is enabled, set up OAI provider. This must be called AFTER repository
			// service is initilialized...
			if (rm.getIsOaiPmhEnabled()) {
				try {
					oaiProviderInit(rm);
				} catch (Throwable t) {
					throw new ServletException("oai init error: " + t.getMessage());
				}
			}

			catServiceInit();

			servletContext.setAttribute("schemEditServlet", this);
			System.out.println(getDateStamp() + " SchemEditServlet initialized.\n");

		} catch (Exception e) {
			prtln("\n ********* Servlet initialization error: " + e.getMessage());
			e.printStackTrace();
		} catch (Throwable t) {
			prtln("\n ********* Unknown servlet init error: " + t.getMessage());
			t.printStackTrace();
		}
	}


	/**
	 *  If application is configured with "catServiceProperties" parameter, set it
	 *  as properties file for CATServiceToolkit to use.
	 */
	private void catServiceInit() {
		ServletContext servletContext = getServletContext();

		String catServiceProperties = (String) servletContext.getInitParameter("catServiceProperties");
		if (catServiceProperties != null && catServiceProperties.trim().length() > 0) {
			// prtln ("catServiceProperties: " + catServiceProperties);
			try {
				CATServiceToolkit.propertiesFile = new File(getAbsolutePath(catServiceProperties));
				prtln("CAT Service connection configured from " + catServiceProperties);
				servletContext.setAttribute("catServiceIsActive", "true");
			} catch (Exception e) {
				prtlnErr("CAT Service config ERROR: " + e.getMessage());
			}
		}
		else {
			servletContext.setAttribute("catServiceIsActive", "false");
		}

		prtln("context attribute \"catServiceIsActive\" set to " +
			(String) servletContext.getAttribute("catServiceIsActive"));

	}


	/**
	 *  Add all frameworks known to DCS to those known to OAI
	 *
	 * @param  rm             repositoryManager
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	private void oaiProviderInit(RepositoryManager rm) throws Exception {

		// supply xmlformat schema and namespaces from registered frameworks to repository manager
		// these formats are IN ADDITION to those configured in web.xml (see xmlformatinfo params
		// for OAIProviderServlet).
		for (Iterator i = frameworkRegistry.getOaiFormats().iterator(); i.hasNext(); ) {
			String xmlFormat = (String) i.next();
			MetaDataFramework framework = frameworkRegistry.getFramework(xmlFormat);

			if (framework == null) {
				continue;
			}

			String schemaURL = framework.getSchemaURI();
			String schemaNamespace = framework.getSchemaHelper().getTargetNamespace();

			if (rm.getMetadataSchemaURL(xmlFormat) == null) {
				rm.setMetadataSchemaURL(xmlFormat, schemaURL);
			}

			if (rm.getMetadataNamespace(xmlFormat) == null) {
				rm.setMetadataNamespace(xmlFormat, schemaNamespace);
			}
		}

		try {
			rm.setOaiFilterQuery(this.getOaiFilterQuery());
		} catch (org.apache.lucene.queryParser.ParseException e) {
			throw new Exception("could not set OAI filter query: " + e.getMessage());
		}

		// show debugging info
		// conversionServiceReport (rm, frameworkRegistry);

		prtln("OAI Provider initialized for DCS");
	}


	/**
	 *  Sets the repositoryWriterPlugins attribute of the SchemEditServlet object
	 *
	 * @param  repositoryService  The new repositoryWriterPlugins value
	 */
	private void setRepositoryWriterPlugins(RepositoryService repositoryService) {
		RepositoryWriter repositoryWriter = repositoryService.getRepositoryWriter();
		try {
			// Add configured RepositoryWriterPlugins:
			Enumeration enumeration = getInitParameterNames();
			String param;
			while (enumeration.hasMoreElements()) {
				param = (String) enumeration.nextElement();

				if (param.toLowerCase().startsWith("repositorywriterplugin")) {
					String paramVal = getInitParameter(param);

					try {
						Class pluginClass = Class.forName(paramVal.trim());
						RepositoryWriterPlugin plugin = (RepositoryWriterPlugin) pluginClass.newInstance();

						// Make the ServletContext available to all ServletContextRepositoryWriterPlugins
						if (plugin instanceof ServletContextRepositoryWriterPlugin) {
							((ServletContextRepositoryWriterPlugin) plugin).setServletContext(getServletContext());
						}

						prtln("Adding plugin: " + plugin.getClass().getName());
						repositoryWriter.addPlugin(plugin);
					} catch (Throwable e) {
						prtlnErr("Error: setRepositoryWriterPlugins(): could not instantiate class '" + paramVal.trim() + "'. " + e);
						continue;
					}
				}
			}
		} catch (Throwable e) {
			String initErrorMsg = "Error: setFileIndexingPlugins(): " + e;
			prtlnErr(initErrorMsg);
		}
	}


	/**
	 *  Sets the repositoryEventListeners attribute of the SchemEditServlet object.
	 *  <p>
	 *
	 *  RepositoryEventListerns are configured as context-params that have a unique
	 *  param-name that begins with the string 'repositoryeventlistener'. The
	 *  param-value must be of the form [fully qualified Java class] to specify a
	 *  listener. The Java class must implement RepositoryEventListener (in package
	 *  edu.ucar.dls.schemedit.repository).
	 *
	 * @param  repositoryService  The repositoryService
	 */
	private void setRepositoryEventListeners(RepositoryService repositoryService) {
		try {
			// Add configured RepositoryWriterPlugins:
			// Enumeration enumeration = getInitParameterNames();
			Enumeration enumeration = this.getServletContext().getInitParameterNames();
			String param;
			while (enumeration.hasMoreElements()) {
				param = (String) enumeration.nextElement();
				// prtln ("** " + param);
				if (param.toLowerCase().startsWith("repositoryeventlistener")) {
					String paramVal = (String) this.getServletContext().getInitParameter(param);
					// prtln ("  paramVal: " + paramVal);
					if (paramVal == null || paramVal.trim().length() == 0) {
						continue;
					}

					try {
						Class listenerClass = Class.forName(paramVal.trim());
						RepositoryEventListener listener = (RepositoryEventListener) listenerClass.newInstance();

						// Make the ServletContext available to all ServletContextRepositoryWriterPlugins
						if (listener instanceof RepositoryEventListener) {
							((RepositoryEventListener) listener).setServletContext(getServletContext());
						}

						repositoryService.addListener(listener);
						prtln("Added RepositoryEvent Listener: " + listener.getClass().getName());
					} catch (Throwable e) {
						prtlnErr("Error: setRepositoryEventListeners(): could not instantiate class '" + paramVal.trim() + "'. " + e);
						continue;
					}
				}
			}
		} catch (Throwable e) {
			String initErrorMsg = "Error: setRepositoryEventListeners(): " + e;
			prtlnErr(initErrorMsg);
		}
	}


	/**
	 *  Prints information about the conversionService for OAI setup diagnostics
	 *
	 * @param  rm   RepositoryManager
	 * @param  reg  FrameworkRegistry
	 */
	void conversionServiceReport(RepositoryManager rm, FrameworkRegistry reg) {
		prtln("\nconvertionServiceDebug()");
		/*
		 *  / test the rm schema map
		 *  prtln ("\n repositoryManager schemaURLs");
		 *  Map schemaURLs = rm.getMetadataSchemaURLs();
		 *  for (Iterator i = schemaURLs.entrySet().iterator();i.hasNext();) {
		 *  Map.Entry entry = (Map.Entry)i.next();
		 *  prtln ("\t" + entry.getKey() + ": " + entry.getValue());
		 *  }
		 */
		prtln("\n xmlConversionService - Available Formats");
		XMLConversionService xmlConversionService = rm.getXMLConversionService();
		for (Iterator i = reg.getItemFormats().iterator(); i.hasNext(); ) {
			String xmlFormat = (String) i.next();
			prtln("\nFROM format: " + xmlFormat);
			List availableFormats = xmlConversionService.getAvailableFormats(xmlFormat);
			if (availableFormats == null) {
				continue;
			}
			for (Iterator a = availableFormats.iterator(); a.hasNext(); ) {
				prtln("\t" + (String) a.next());
			}
		}
	}


	/**
	 *  Build a query that finds all items with having the given statusValue as
	 *  their statusLabel
	 *
	 * @param  statusLabels  NOT YET DOCUMENTED
	 * @return               The statusQuery value
	 */
	public String getStatusQuery(String[] statusLabels) {
		// create list of status values by expanding each status label and concatenating
		List statusValues = new ArrayList();
		for (int li = 0; li < statusLabels.length; li++) {
			String label = statusLabels[li];

			if (label.trim().length() == 0) {
				continue;
			}

			statusValues.add(label);
			try {
				List flags = collectionRegistry.getFinalStatusFlags();
				// expand this label for all or any final statuses having it as a label
				for (Iterator i = flags.iterator(); i.hasNext(); ) {
					StatusFlag flag = (StatusFlag) i.next();
					if (flag.getLabel().equals(label)) {
						statusValues.add(flag.getValue());
					}
				}
			} catch (Throwable t) {
				// prtln ("getStatusQuery error: " + t.getMessage());
			}
		}

		String query = "";
		if (statusValues.size() > 0) {
			// construct query ORing all statusValues
			for (Iterator i = statusValues.iterator(); i.hasNext(); ) {
				String label = (String) i.next();
				query += "dcsstatus:" + SchemEditUtils.quoteWrap(label);
				if (i.hasNext()) {
					query += " OR ";
				}
			}
			query = "(" + query + ")";
		}
		// query = query + " AND (dcsisValid:" +  SchemEditUtils.quoteWrap ("true") + ")";
		return query;
	}


	/**
	 *  Records that match this query will NOT be served by the OAI data provider.
	 *  Used to ensure that the OAI provider provides only finalAndValid records
	 *
	 * @return    The oaiFilterQuery value
	 */
	private String getOaiFilterQuery() {
		String query = "dcsisFinalStatus:" + SchemEditUtils.quoteWrap("false");
		query += " OR dcsisValid:" + SchemEditUtils.quoteWrap("false");
		return query;
	}


	/**  Performs shutdown operations.  */
	public void destroy() {
		prtln("destroy() ...");
		try {
			rs.destroy();
			dcsDataManager.destroy();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		System.out.println(getDateStamp() + " SchemEditServlet stopped");
	}


	/**
	 *  Return a string for the current time and date, sutiable for display in log
	 *  files and output to standout:
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
		System.err.println(getDateStamp() + " SchemEditServlet: " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final void prtln(String s) {
		if (debug) {
			// System.out.println(getDateStamp() + " SchemEditServlet: " + s);
			SchemEditUtils.prtln(s, "SchemEditServlet");
		}
	}


	/**
	 *  Gets the absolute path to a given file or directory. Assumes the path
	 *  passed in is eithr already absolute (has leading slash) or is relative to
	 *  the context root (no leading slash). If the string passed in does not begin
	 *  with a slash ("/"), then the string is converted. For example, an init
	 *  parameter to a config file might be passed in as "WEB-INF/conf/serverParms.conf"
	 *  and this method will return the corresponding absolute path "/export/devel/tomcat/webapps/myApp/WEB-INF/conf/serverParms.conf."
	 *  <p>
	 *
	 *  If the string that is passed in already begings with "/", nothing is done.
	 *  <p>
	 *
	 *  Note: the super.init() method must be called prior to using this method,
	 *  else a ServletException is thrown.
	 *
	 * @param  fname                 An absolute or relative file name or path
	 *      (relative the the context root).
	 * @return                       The absolute path to the given file or path.
	 * @exception  ServletException  An exception related to this servlet
	 */
	private String getAbsolutePath(String fname)
		 throws ServletException {
		if (fname == null) {
			return null;
		}
		return GeneralServletTools.getAbsolutePath(fname, getServletContext());
	}


	/**  Debugging  */
	private void showContextParams() {

		prtln("\ninit parameters from CONFIG");
		Enumeration e1 = this.getServletConfig().getInitParameterNames();
		while (e1.hasMoreElements()) {
			String paramName = (String) e1.nextElement();
			String paramValue = (String) this.getServletConfig().getInitParameter(paramName);
			prtln("\t" + paramName + ": " + paramValue);
		}

		prtln("\ninit parameters from CONTEXT");
		Enumeration e2 = this.getServletContext().getInitParameterNames();
		while (e2.hasMoreElements()) {
			String paramName = (String) e2.nextElement();
			String paramValue = (String) this.getServletContext().getInitParameter(paramName);
			prtln("\t" + paramName + ": " + paramValue);
		}
	}

}

