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
package edu.ucar.dls.schemedit.standards.asn;

import edu.ucar.dls.schemedit.standards.StandardsManager;
import edu.ucar.dls.schemedit.standards.StandardsRegistry;
import edu.ucar.dls.schemedit.standards.config.SuggestionServiceConfig;
import edu.ucar.dls.schemedit.standards.config.SuggestionServiceManager;

import edu.ucar.dls.schemedit.standards.adn.util.MappingUtils;

import edu.ucar.dls.standards.asn.AsnDocument;
import edu.ucar.dls.standards.asn.util.AsnCatalog;

import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.schemedit.*;

import java.io.*;
import java.util.*;

import java.net.*;

/**
 *  Provides acess to a single ASN Standards Document (and individual standards
 *  contained within) via the AsnDocument and AsnNode classes. Provides lists of
 *  AsnStandardsNodes for use in UI JSP.
 *
 * @author    ostwald
 */

public class AsnStandardsManager implements StandardsManager {
	
	private static boolean debug = false;
	private AsnDocMap asnDocMap = null;
	private SuggestionServiceConfig config = null;
	private StandardsRegistry standardsRegistry = null;
	private String defaultDocKey = null;


	public AsnStandardsManager (String xmlFormat, String xpath, File source) throws Exception {
		// this is just a stub for classes like ResQualStandardsManager that need this interface to compile
	}

	/**
	 *  Constructor for the AsnStandardsManager object
	 *
	 * @param  xmlFormat      format of framework for this standardsManager
	 * @param  xpath          field for which standards are managed
	 * @param  source         AsnDocument file
	 * @exception  Exception  if AsnDocument file cannot be processed
	 */
	public AsnStandardsManager(SuggestionServiceConfig config, String defaultDocKey) throws Exception {
		prtln ("AsnStandardsManager");
		this.asnDocMap = new AsnDocMap();
		this.config = config;
		this.standardsRegistry = StandardsRegistry.getInstance();
		if (this.getStandardsRegistry() == null)
			throw new Exception ("ERROR: couldn't instantiate standardsRegistry");

 		try {
			init();
		} catch (Exception e) {
			throw new Exception("AsnDocument could not be initialized: " + e.getMessage());
		}

		prtln("\nAsnStandardsManager() defaultDocKey: " + defaultDocKey);
		/* defaultDocKey is not necessarily a complete key (i.e., "author.topic." vs "author.topic.year")
			- try to match defaultDocKey and if there is no match simply pick the first available standards
			doc configured for this helper.
		*/

		try {
			String docKey = this.getStandardsRegistry().matchKey(defaultDocKey);
			prtln("matchKey retured: " + docKey);
			if (docKey == null) {
				// couldn't get default, so grab first available
				prtln("couldnt get default, grabbing first available");
				List availableDocs = this.getAvailableDocs();
				if (!availableDocs.isEmpty()) {
					AsnDocInfo docInfo = (AsnDocInfo) availableDocs.get(0);
					docKey = docInfo.getKey();
				}
			}
			this.setDefaultDocKey(docKey);
			prtln("  ... default set to: " + this.getDefaultDocKey());
		} catch (Throwable e) {
			e.printStackTrace();
			throw new Exception("standards manager could not establish defaultDocKey for " + this.getXmlFormat());
		}

	}

	private void init () throws Exception {

		for (Iterator i=this.config.getAsnDocKeys().iterator();i.hasNext();) {
			String key = (String)i.next();
			try {
				AsnDocInfo docInfo = this.standardsRegistry.register(key);
				if (docInfo == null)
					throw new Exception ("not able to obtain standards doc for " + key);
				// String docId = docInfo.getDocId();
				this.addAvailableDoc(docInfo);
			} catch (Throwable e) {
				prtln ("WARNING: unable to load doc for " + key + ": " + e.getMessage());
			}
		}
	}

	/**
	 *  Gets the standardsRegistry attribute of the AsnStandardsManager object
	 *
	 * @return    The standardsRegistry value
	 */
	public StandardsRegistry getStandardsRegistry() {
		return this.standardsRegistry;
	}


	/**
	 *  Gets the defaultDocKey attribute of the AsnStandardsManager object
	 *
	 * @return    The defaultDocKey value
	 */
	public String getDefaultDocKey() {
		if (this.defaultDocKey == null) {
				
			List availableDocs = this.getAvailableDocs();
			if (!availableDocs.isEmpty()) {
				AsnDocInfo docInfo = (AsnDocInfo) availableDocs.get(0);
				this.defaultDocKey = docInfo.getKey();
			}
		}
		return this.defaultDocKey;
	}


	/**
	 *  Sets the defaultDocKey attribute of the AsnStandardsManager object
	 *
	 * @param  docKey  The new defaultDocKey value
	 */
	public void setDefaultDocKey(String docKey) {
		this.defaultDocKey = docKey;
		// persist changes
		if (this.config != null) {
			// System.out.println ("config: " + config);
			this.config.setDefaultDocKey(docKey);
		}
	}


	/**
	 *  Gets the availableDocs (avaliable ASN Standards Documents) attribute of the AsnStandardsManager object.

	 *
	 * @return    The availableDocs value
	 */
	public List getAvailableDocs() {
		return this.asnDocMap.getAvailableDocs();
	}

	/**
	* SIDE EFFECT?? - update configuration??
	*/
	public void addAvailableDoc (AsnDocInfo docInfo) {
		if (docInfo == null) {
			prtln ("docInfo is null!");
			return;
		}
		this.asnDocMap.addAvailableDoc(docInfo);
	}

	/**
	 *  Sets the availableDocs attribute of the AsnStandardsManager object and
	 updates the configuration, making the updates persistent.<p>
	 NOTE: what should policy be on automatically saving config?
	 *
	 * @param  docs  The new availableDocs value
	 */
	public void setAvailableDocs(List newKeys) {
		prtln ("\nsetAvailableDocs with " + newKeys.size() + " new keys");
		// this.availableDocs = new ArrayList();
		// this.availableDocIds = null;  // causes re-compute on next acess
		
		this.asnDocMap.removeAvailableDocs ();
		
		List keys = new ArrayList();
		for (Iterator i=newKeys.iterator();i.hasNext();) {
			String key = (String)i.next();
			prtln ("key: " + key);
			AsnDocInfo docInfo = null;
			try {
				// docInfo = this.getStandardsRegistry().register(key);
				
				// EXPERIMENTAL - GET docInfo from catalog and use it, instead (without registering) ...
				String docId = AsnDocKey.makeAsnDocKey(key).getAsnId();
				if (docId == null)
					throw new Exception ("could not get asnDocId for provided key: " + key);
				docInfo = new AsnDocInfo (AsnCatalog.getInstance().getItem(docId));
				if (docInfo == null)
					throw new Exception ("could not get docInfo from AsnCatalog for " + docId);
				prtln ("  - got docInfo for " + key + "\n" + docInfo.toString());
			} catch (Exception e) {
				prtln ("ERROR: could not register " + key + ": " + e.getMessage());
				continue;
			}

			if (docInfo == null) {
				prtln ("WARNING: no docInfo for " + key);
				continue;
			}
			prtln ("docInfo for " + key + ": " + docInfo.toString());
			this.addAvailableDoc(docInfo);
			prtln ("added to available docs");
			keys.add (key);
		}
		// persist changes
		if (this.config != null) {
			this.config.setAsnDockeys(keys);
		}

	}

	public List getAvailableDocIds () {
		return this.asnDocMap.getAvailableDocIds();
	}

	public List getExtraDocIds () {
		return this.asnDocMap.getExtraDocIds();
	}

	public List getExtraDocs () {
		return this.asnDocMap.getExtraDocs();
	}

	public void addExtraDoc (AsnDocInfo docInfo) {

		if (docInfo == null) {
			prtln ("docInfo is null!");
			return;
		}

		this.asnDocMap.addExtraDoc(docInfo);
	}

	public boolean getDocIsAvailable (String docId) {
		return (this.getAvailableDocIds().contains(docId));
	}

	/**
	 *  Gets the xpath attribute of the AsnStandardsManager object
	 *
	 * @return    The xpath value
	 */
	public String getXmlFormat() {
		return this.config.getXmlFormat();
	}


	/**
	 *  Gets the xpath attribute of the AsnStandardsManager object
	 *
	 * @return    The xpath value
	 */
	public String getXpath() {
		return this.config.getXpath();
	}


	protected void setXpath(String xpath) {
		this.config.setXpath(xpath);
	}


	/**
	 *  Gets the rendererTag attribute of the AsnStandardsManager object
	 *
	 * @return    The rendererTag value
	 */
	public String getRendererTag() {
		return "standards_MultiBox";
	}


	/**  prints debugging information about this AsnStandardsManager */
	public void report() {
		prtln("\n----------------------");
		prtln("xmlFormat: " + this.getXmlFormat());
		prtln("xpath: " + this.getXpath());
		prtln("rendererTag: " + this.getRendererTag());
	}

	public static void main (String[] args) throws Exception {
		// String path = "C:/Program Files/Apache Software Foundation/Tomcat 5.5/var/dcs_conf/suggestionServiceConfig.xml";

		StandardsRegistry.setLibraryDir (new File ("/Documents/Work/DLS/ASN/standards-documents/v3.1"));
		// edu.ucar.dls.standards.asn.util.AsnCatalogOld.setMappingsDirectory("/Users/ostwald/devel/projects/dcs-project/web/WEB-INF/data/ACSR-to-ASN-Mappings");

		String cachePath = "Z:/Documents/ASN/ASN_v3.1.0-cache/topics";
		edu.ucar.dls.standards.asn.util.AsnCatalog.setCacheDirectory(cachePath);
		
		String path = "/Users/ostwald/devel/dcs-repos/tiny/dcs_conf/suggestionServiceConfig.xml";
		SuggestionServiceManager manager = null;


		try {
			manager = new SuggestionServiceManager(new File(path));
		} catch (Throwable t) {
			throw new Exception ("couldn't instantiate SuggestionServiceManager");
		}

		String xmlFormat = "ncs_item";
		SuggestionServiceConfig config = manager.getConfig(xmlFormat);
		if (config == null) {
			throw new Exception ("config not found for: "+ xmlFormat);
		}

		AsnStandardsManager standardsManager = new AsnStandardsManager (config, "");
	}

	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug)
			SchemEditUtils.prtln(s, "AsnStandardsManager");
		// System.out.println(s);
	}
	
	public class AsnDocMap extends HashMap {
		
		public void addAvailableDoc (AsnDocInfo docInfo) {
			this.put (docInfo.getDocId(), new DocInfoStatus (docInfo, true));
		}
		
		public void addExtraDoc (AsnDocInfo docInfo) {
			this.put (docInfo.getDocId(), new DocInfoStatus (docInfo, false));
		}
	
		public List getAvailableDocIds () {
			List ids = new ArrayList();
			Iterator infoIter = this.values().iterator();
			while (infoIter.hasNext()) {
				DocInfoStatus info = (DocInfoStatus)infoIter.next();
				if (info.registered)
					ids.add (info.docId);
			}
			return ids;
		}
		
		public List getAvailableDocs () {
			List ids = new ArrayList();
			Iterator infoIter = this.values().iterator();
			while (infoIter.hasNext()) {
				DocInfoStatus info = (DocInfoStatus)infoIter.next();
				if (info.registered)
					ids.add (info.docInfo);
			}
			return ids;
		}
		
		/**
		* Remove all entries that are currently registered
		*/
		public void removeAvailableDocs () {
/* 			Iterator entryIter = this.entrySet().iterator();
			while (entryIter.hasNext()) {
				Map.Entry entry = (Map.Entry) entryIter.next();
				DocInfoStatus info = (DocInfoStatus)entry.getValue();
				if (info.registered)
					this.remove((String)entry.getKey());
			} */
			for (Iterator i=this.getAvailableDocIds().iterator();i.hasNext();)
				this.remove ((String)i.next());
		}
			
		
		public List getExtraDocIds () {
			List ids = new ArrayList();
			Iterator infoIter = this.values().iterator();
			while (infoIter.hasNext()) {
				DocInfoStatus info = (DocInfoStatus)infoIter.next();
				if (!info.registered)
					ids.add (info.docId);
			}
			return ids;
		}
		
		public List getExtraDocs () {
			List ids = new ArrayList();
			Iterator infoIter = this.values().iterator();
			while (infoIter.hasNext()) {
				DocInfoStatus info = (DocInfoStatus)infoIter.next();
				if (!info.registered)
					ids.add (info.docInfo);
			}
			return ids;
		}
	}
				
	public class DocInfoStatus {
		String docId;
		AsnDocInfo docInfo = null;
		boolean registered = false;
		
		public DocInfoStatus (AsnDocInfo docInfo, boolean registered) {
			this.docInfo = docInfo;
			this.docId = this.docInfo.getDocId();
			this.registered = registered;
		}
		
		public String toString () {
			return this.docId + ": " + this.registered;
		}
	}
}
