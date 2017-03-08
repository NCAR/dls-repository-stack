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
package edu.ucar.dls.standards.asn;

import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.util.Files;
import org.dom4j.*;
import java.net.URL;
import java.util.*;
import java.io.*;

/**
 *  Abstract Class subclassed to read ASN authors and topics documents from ASN
 *  web service and provide lookup services by ASN purls. If ASN webservice is
 *  not available, used cached version.
 *
 * @author    Jonathan Ostwald
 */
public abstract class AsnHelperService {
	private static boolean debug = true;

	private NameSpaceXMLDocReader indexDoc = null;
	/**  NOT YET DOCUMENTED */
	public Map map = null;


	/**
	 *  Gets the instance attribute of the AsnHelperService class
	 *
	 * @return                The instance value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static AsnHelperService getInstance() throws Exception {
		throw new Exception("getInstance not defined in abtract class");
	}


	/**
	 *  Gets the cachedServiceIndex attribute of the AsnHelperService object
	 *
	 * @return    The cachedServiceIndex value
	 */
	public abstract String getCachedServiceIndex();


	/**
	 *  Returns the serviceIndexURI - the uri used to get data
	 *
	 * @return    The serviceIndexURI value
	 */
	public abstract String getServiceIndexURI();


	/**
	 *  Constructor for the AsnHelperService object
	 *
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	protected AsnHelperService() throws Exception {
		try {
			indexDoc = new NameSpaceXMLDocReader(new URL(getServiceIndexURI()));
		} catch (Throwable t) {
			prtln("WARNING: unable to process " + getServiceIndexURI() + ": " + t.getMessage());
			try {
				String cachedIndex = Files.readFileFromJarClasspath(getCachedServiceIndex()).toString();
				indexDoc = new NameSpaceXMLDocReader(cachedIndex);
			} catch (Exception e) {
				throw new Exception("Unable to use cached indexDocument: " + e.getMessage());
			}
		}

		try {
			getObjectMap();
		} catch (Throwable t) {
			throw new Exception("could not getObjectMap: " + t.getMessage());
		}
	}

	public void refresh() throws Exception {
		prtln ("refreshing ...");
		try {
			indexDoc = new NameSpaceXMLDocReader(new URL(getServiceIndexURI()));
		} catch (Throwable t) {
			throw new Exception ("Unable to process " + getServiceIndexURI() + ": " + t.getMessage());
		}
		map.clear();
		map = null;		
	}
	
	/**
	Injects a list of ServiceObjects into the objectMap, e.g., to add an author
	that ASN doesn't supply
	**/
	public List<ServiceObject> getCustomObjects () {
		return new ArrayList<ServiceObject>();
	}

	/**
	 *  Create ASN object (e.g., author/jurisdiction or subject/topic from provided element
	 *
	 * @param  element  NOT YET DOCUMENTED
	 * @return          NOT YET DOCUMENTED
	 */
	public abstract ServiceObject createObject(Element element);


	/**
	 *  Gets the objectXPath attribute of the AsnHelperService object
	 *
	 * @return    The objectXPath value
	 */
	public abstract String getObjectXPath();


	/**
	 *  Gets the indexDoc attribute of the AsnHelperService object
	 *
	 * @return    The indexDoc value
	 */
	public NameSpaceXMLDocReader getIndexDoc() {
		return this.indexDoc;
	}


	/**
	 *  Gets the objectPurls attribute of the AsnHelperService object
	 *
	 * @return    The objectPurls value
	 */
	public Set getObjectPurls() {
		try {
			return this.getObjectMap().keySet();
		} catch (Throwable t) {}
		return null;
	}


	/**
	*  Returns the object (e.g, a {@link Jurisdiction}) for provide purl
	 *
	 * @param  purl  NOT YET DOCUMENTED
	 * @return       The object value
	 */
	public ServiceObject getObject(String purl) {
		return (ServiceObject) getObjectMap().get(purl);
	}


	/**
	 *  Returns a mapping from author purl to ServiceObject
	 *  instance (e.g., Author) via createObject
	 *
	 * @return    The objectMap value
	 */
	protected Map getObjectMap() {
		if (map == null) {
			map = new HashMap();
			List objectNodes = getIndexDoc().getNodes(getObjectXPath());
			// prtln (Dom4jUtils.prettyPrint (getIndexDoc().getDocument()));
			// prtln(objectNodes.size() + " object nodes found");
			for (Iterator i = objectNodes.iterator(); i.hasNext(); ) {
				Element element = (Element) i.next();
				ServiceObject object = null;
				try {
					object = createObject(element);
				} catch (Throwable t) {
					prtln("ERROR: could not create object for " + element.asXML());
					continue;
				}
				map.put(object.purl, object);
			}
			// prtln("objectMap has " + map.size() + " entries");
			
			// Now add the WMO custom jurisdiction
			for (ServiceObject obj : getCustomObjects()) {
				map.put (obj.purl, obj);
			}
			
			
		}
		return map;
	}


	/**
	 *  Gets the objects attribute of the AsnHelperService object
	 *
	 * @return    The objects value
	 */
	public Collection getObjects() {
		return getObjectMap().values();
	}

	public int size() {
		return getObjectMap().size();
	}

	/**
	 *  Utility to update cached Index File
	 *
	 * @param  cacheFilePath  path to local src directory
	 * @exception  Exception  if file cannot be written
	 */
	public void cacheIndexDoc(String cacheFilePath) throws Exception {
		// String cacheFilePath = srcDir + CASHED_SERVICE_INDEX;
		File cacheFile = new File(cacheFilePath);
		// AsnHelperService service = getInstance();
		Dom4jUtils.writePrettyDocToFile(getIndexDoc().getDocument(), cacheFile);
		prtln("indexDoc cached at " + cacheFile);
	}


	/**
	 *  Class to encapsulate a 'Jurisdiction' element of the ASN 'jurisdictions'
	 *  response. See http://asn.jesandco.org/api/1/jurisdictions *
	 *
	 * @author    Jonathan Ostwald
	 */
	public abstract class ServiceObject {
		/**  e.g., http://purl.org/ASN/scheme/ASNJurisdiction/AAAS */
		public String purl;
		/**  NOT YET DOCUMENTED */
		public int docCount;
		/**  NOT YET DOCUMENTED */
		public Element element = null;


		/**
		 *  Constructor for the Jurisdiction object
		 *
		 * @param  element  NOT YET DOCUMENTED
		 */
		public ServiceObject(Element element) {
			this.element = element;
			this.purl = getPurl();
			this.docCount = Integer.parseInt(element.element("DocumentCount").getTextTrim());
		}
		
		public ServiceObject() {}


		/**
		 *  Gets the purl attribute of the ServiceObject object
		 *
		 * @return    The purl value
		 */
		public abstract String getPurl();


		/**
		 *  NOT YET DOCUMENTED
		 *
		 * @return    NOT YET DOCUMENTED
		 */
		public String toString() {
			return this.purl;
		}
	}


	/**  NOT YET DOCUMENTED */
	public void report() {
		// System.out.println("\nASN Authors Report");
		System.out.println("\n" + this.getClass().getName() + " Report");

		List<String> lines = new ArrayList<String>();
		
		Iterator objectIter = getObjectMap().values().iterator();
		while (objectIter.hasNext()) {
			ServiceObject object = (ServiceObject) objectIter.next();

			// System.out.println("- " + object.toString());
			lines.add (object.toString());
		}
		
		Collections.sort(lines);
		for (String line : lines)
			System.out.println("- " + line);
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println("AsnHelperService: " + s);
		}
	}

}

