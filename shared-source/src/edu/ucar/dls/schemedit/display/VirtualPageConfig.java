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
package edu.ucar.dls.schemedit.display;

import edu.ucar.dls.schemedit.standards.StandardsManager;
import edu.ucar.dls.schemedit.standards.adn.DleseStandardsManager;
import edu.ucar.dls.schemedit.standards.asn.*;
import edu.ucar.dls.schemedit.standards.td.TeachersDomainStandardsManager;
import edu.ucar.dls.schemedit.config.AbstractConfigReader;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.xml.Dom4jUtils;

import java.io.*;
import java.util.*;

import java.net.*;

import org.dom4j.*;

/**
 *  Reads a SuggestionService configuration file and provides access to the
 *  individual configurations (each corresponding to a MetaDataFramework). The
 *  schema for the SuggestionService file is at<br/>
 *  http://www.dls.ucar.edu/people/ostwald/Metadata/standardsService/standardsServiceConfig.xsd
 *  <p>
 *
 *  createStandardsManager method is a factory that instantiates a
 *  StandardsManager instance for a particular framework.<p>
 *
 *  VirtualPageConfig is instantiated at startup by {@link edu.ucar.dls.schemedit.SetupServlet}
 *  and placed in the servletContext.
 *
 * @author    ostwald
 */
public class VirtualPageConfig extends AbstractConfigReader {

	protected static boolean debug = true;

	private Map formatMap = null;
	private static VirtualPageConfig instance = null;
	private static URI sourceURI = null;

	/**
	 *  Constructor for the VirtualPageConfig object, which reads a configuration
	 *  file and creates VirtualPageList instances for each configured framework.
	 *  The configs for the various frameworks are stored in a map, keyed by the
	 *  xmlFormat.
	 *
	 * @param  source         virtualConfig file
	 * @exception  Exception  if file cannot be processed
	 */
	private VirtualPageConfig(File source) throws Exception {
		super(source);
		init();
	}


	/**
	 *  Constructor for the VirtualPageConfig object taking a URL that points to
	 *  configuration file.
	 *
	 * @param  url            location of config file
	 * @exception  Exception  if url cannot be processed
	 */
	private VirtualPageConfig(URL url) throws Exception {
		super(url);
		init();
	}


	/**
	 *  parse sourceURI and populate the formatMap with VirtualPageList instances
	 */
	void init() {
		// prtln("reading standards virtualPage config file at: " + sourceURI);
		formatMap = new HashMap();
		for (Iterator i = getNodes("/virtualPageConfigs/virtualPageConfig").iterator(); i.hasNext(); ) {
			Element config = (Element) i.next();
			String format = config.attributeValue("xmlFormat");
			formatMap.put(format.trim(), new VirtualPageList(config));
		}
	}


	/**
	 *  Gets the instance attribute of the VirtualPageConfig class. Returns null if
	 the the virtualPageConfig cannot be instantiated.
	 *
	 * @return                The instance value or null if the instance cannot be created.
	 */
	public static VirtualPageConfig getInstance()  {
		if (instance == null) {
			try {
				if (sourceURI == null) {
					// throw new Exception ("source URI is not initialized");
					return null;
				}
	
				if (sourceURI.getScheme().equals("file")) {
					File source_file = new File(sourceURI.getPath());
					if (!source_file.exists())
						throw new Exception("source file does not exist at " + source_file);
					// File default_source = new File ("/Users/ostwald/devel/tmp/virtualPage_config.xml");
					instance = new VirtualPageConfig(source_file);
				}
				else if (sourceURI.getScheme().equals("http")) {
					instance = new VirtualPageConfig(sourceURI.toURL());
				}
			} catch (Exception e) {
				prtlnErr ("could not instantiate: " + e.getMessage());
			}
		}
		return instance;
	}


	/**
	 *  Sets the sourceURI attribute of the VirtualPageConfig class
	 *
	 * @param  uriStr         The new sourceURI value
	 * @exception  Exception  if supplied uriStr cannot be cast as URI
	 */
	public static void setSourceURI(String uriStr) throws Exception {
		try {
			sourceURI = new URI(uriStr);
		} catch (URISyntaxException e) {
			throw new Exception(e);
		}
		prtln("sourceURI set to " + sourceURI);
	}

	public static String getSourceURI() {
		if (sourceURI == null)
			return null;
		else
			return sourceURI.toString();
	}

	/**
	 *  Returns true if the specified framework is configured.
	 *
	 * @param  xmlFormat  e.g., ("lar")
	 * @return            true if xmlFormat has virtualPageConfig
	 */
	public boolean hasConfig(String xmlFormat) {
		return this.getKeys().contains(xmlFormat);
	}


	/**
	 *  Gets the xml_formats corresponding to the configured frameworks
	 *
	 * @return    The keys value
	 */
	public Set getKeys() {
		return this.formatMap.keySet();
	}


	/**
	 *  Gets the virtualPageLists attribute of the VirtualPageConfig object
	 *
	 * @return    The virtualPageLists value
	 */
	public List getVirtualPageLists() {
		List vpls = new ArrayList();
		List keys = Arrays.asList(this.getKeys().toArray());
		if (keys != null && !keys.isEmpty()) {
			Collections.sort(keys);
			for (Iterator i = keys.iterator(); i.hasNext(); )
				vpls.add((VirtualPageList) i.next());
		}
		return vpls;
	}


	/**
	 *  Gets the configuration for the specified xmlFormat (framework)
	 *
	 * @param  xmlFormat  e.g., ("lar")
	 * @return            The config value
	 */
	public VirtualPageList getConfig(String xmlFormat) {
		return (VirtualPageList) formatMap.get(xmlFormat);
	}


	/**
	 *  override super class flush which writes config file to disk.
	 *
	 * @exception  Exception  if config cannot be refreshed
	 */
	public void flush() throws Exception {
		this.refresh();
	}


	/**  force re-load of config */
	public void refresh() {
		super.refresh();
		VirtualPageConfig.instance = null;
		VirtualPageConfig instance = VirtualPageConfig.getInstance();
		if (instance != null)
			instance.init();
	}


	/**
	 *  The main program for the VirtualPageConfig class
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(String[] args) {
		// String path = "/Users/ostwald/devel/dcs-repos/dcs_release_test/dcs_conf/virtualPage_config.xml";
		String url = "http://ns.nsdl.org/ncs/lar/1.00/docs/virtualPageConfig.xml";
		String path = "file:///C:/Program%20Files/Apache%20Software%20Foundation/Tomcat%206.0/dcs_conf/virtualPageConfig.xml";
		VirtualPageConfig virtualPageConfig = null;
		try {
			VirtualPageConfig.setSourceURI(url);
			VirtualPageConfig.setSourceURI(path);
			virtualPageConfig = VirtualPageConfig.getInstance();
			prtln ("virtualPageConfig instantated (" + VirtualPageConfig.getSourceURI() + ")");
		} catch (Exception e) {
			prtlnErr("ERROR: " + e.getMessage());
			e.printStackTrace();
		}

	}


	/**  print debegging output */
	public void report() {
		prtln("\nsuggestion service configs");
		for (Iterator i = this.getKeys().iterator(); i.hasNext(); ) {
			String key = (String) i.next();
			VirtualPageList config = this.getConfig(key);
			// prtln("\t" + key + ": " + config.getPluginClass());
		}
	}


	/**
	 *  Print a line to standard out.
	 *
	 * @param  s  The String to print.
	 */
	protected static void prtln(String s) {
		if (debug) {
			// SchemEditUtils.prtln(s, "AbstractConfigReader");
			SchemEditUtils.prtln(s, "vpc");
		}
	}

	protected static void prtlnErr(String s) {
		System.out.println("VirtualPageConfig: " + s);
	}
	
}

