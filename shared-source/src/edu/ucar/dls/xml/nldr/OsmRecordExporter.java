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
package edu.ucar.dls.xml.nldr;

import edu.ucar.dls.util.Utils;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.util.strings.FindAndReplace;
import edu.ucar.dls.schemedit.threadedservices.NLDRProperties;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XMLUtils;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.util.MetadataUtils;
import edu.ucar.dls.ndr.toolkit.MimeTypes;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.dom4j.Element;
import org.dom4j.Branch;
import org.dom4j.Attribute;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.Namespace;

import java.util.*;
import java.lang.*;
import java.io.*;
import java.text.*;
import java.net.*;
import java.util.Hashtable;

/**
 *  Specializes NldrMetadataRecordExporter to handle export for the osm
 *  framework. Populates the "urlOfRecord" field with a citableURL, as well as
 *  converting the Asset urls. Also enforces embargoes (expresed in
 *  coverage/dataRange fields).
 *
 *@author    Jonathan Ostwald
 */
public class OsmRecordExporter extends NldrMetadataRecordExporter {
	private static boolean debug = false;

	static String BASE_PHYSICAL_ASSET_URL;// = "http://nldr.library.ucar.edu/collections";
	static String BASE_ASSET_DIR;//  = "/web/nldr/collections";
	static String BASE_REPOSITORY_URL;
	static String MASTER_COLLECTION_KEY;
	static String MASTER_COLLECTION_PREFIX;
	static boolean ignoreEmbargo = false;

	// The vocab values that can cause an embargo
	static List EMBARGO_TYPES = Arrays.asList(
			new String[]{"Author embargo", "Donor embargo", "Publisher embargo"});



	/**
	 *  Constructor that loads the given ADN record for editing. No validation is
	 *  performed.
	 *
	 *@param  xml                    NOT YET DOCUMENTED
	 *@param  nldrProps              Description of the Parameter
	 *@exception  DocumentException  If error parsing the XML
	 *@exception  Exception          Description of the Exception
	 */
	public OsmRecordExporter(String xml, NLDRProperties nldrProps) throws DocumentException, Exception {
		this(xml, nldrProps, false);
	}

	public OsmRecordExporter(String xml, NLDRProperties nldrProps, boolean ignoreEmbargo) throws DocumentException, Exception {
		super(xml, nldrProps);
		this.ignoreEmbargo = ignoreEmbargo;
		setNLDRProperties (nldrProps);
	}
	
	public static void setNLDRProperties (NLDRProperties props) {
		nldrProps = props;
		BASE_PHYSICAL_ASSET_URL = nldrProps.BASE_PHYSICAL_ASSET_URL;
		BASE_ASSET_DIR = nldrProps.BASE_ASSET_DIR;
		BASE_REPOSITORY_URL = nldrProps.BASE_REPOSITORY_URL;
		MASTER_COLLECTION_KEY = nldrProps.MASTER_COLLECTION_KEY;
		MASTER_COLLECTION_PREFIX = nldrProps.MASTER_COLLECTION_PREFIX;
	}

	public static String getExportXml(String xml, NLDRProperties nldrProps) throws Exception {
		return getExportXml(xml, nldrProps, false);
	}
	
	/**
	 *  Takes a record and changes URLs for urlOfRecord and all Assets to
	 *  CitableUrl form
	 *
	 *@param  xml            An XML record as a String
	 *@param  nldrProps      Description of the Parameter
	 *@return                The exportXml value
	 *@exception  Exception  NOT YET DOCUMENTED
	 */
	public static String getExportXml(String xml, NLDRProperties nldrProps, boolean ignoreEmbargo) throws Exception {
		OsmRecordExporter exporter = new OsmRecordExporter(xml, nldrProps, ignoreEmbargo);

		String id = exporter.getId();
		// String urlOfRecord = parentUrl + "/" + id;
		String urlOfRecord = BASE_REPOSITORY_URL + "/" + MASTER_COLLECTION_KEY + "/" + id;
		exporter.setUrl(urlOfRecord);
		List allAssetNodes = exporter.getAssetNodes();
		// prtln ("parentUrl: " + parentUrl);
		/*
		 *  the url for assetNodes has the form:
		 *  http://nldr.library.ucar.edu/collections/soars/2009_Goode_Sharome.pdf
		 */
		for (int i = 0; i < allAssetNodes.size(); i++) {
			Element assetElement = (Element) allAssetNodes.get(i);
			try {
				String physicalUrl = assetElement.attributeValue("url");

				// if a record is embargoed, we simply do not provide the url
				String citableUrl = (exporter.isEmbargoed() ? "" :
						makeLogicalAssetUrl(physicalUrl));

				assetElement.addAttribute("url", citableUrl);
				updateAssetNode(assetElement, physicalUrl);
			} catch (Throwable t) {
				prtlnErr("could not process assetElement: " + t.getMessage());
				t.printStackTrace();
				prtlnErr("assetElement: " + Dom4jUtils.prettyPrint(assetElement));
				assetElement.addAttribute("url", "");
			}
		}

		List thumbnailNodes = exporter.getThumbnailNodes();
		for (int i = 0; i < thumbnailNodes.size(); i++) {
			Element thumbNailElement = (Element) thumbnailNodes.get(i);
			String physicalUrl = thumbNailElement.getTextTrim();
			if (physicalUrl.startsWith(BASE_PHYSICAL_ASSET_URL)) {
				String citableUrl = makeLogicalAssetUrl(physicalUrl);
				thumbNailElement.setText(citableUrl);

			}
		}

		return Dom4jUtils.prettyPrint(exporter.getXmlNode());
	}


	/**
	 *  Set the size and mimeType for this record.
	 *
	 *@param  assetNode    the element that will receive size and mimeType
	 *@param  physicalUrl  Description of the Parameter
	 */
	static void updateAssetNode(Element assetNode, String physicalUrl) {
		// update filesize and mime-type
		File assetFile = null;
		try {
			assetFile = getAssetFile(physicalUrl);
		} catch (Exception e) {
			prtln("could not updateAssetNode: " + e.getMessage());
			return;
		}
		int kbytes = ((Long) assetFile.length()).intValue();
		String filesize = new Integer(kbytes / 1000).toString() + " KB";

		// we have to place size and mimeType elements in order, following 'title' if there is one.
		// careful about when there is nothing after title, because then you can't use an index
		// and must then append
		int insert_index = (assetNode.element("title") == null ? 0 : 1);

		prtln("insert_index: " + insert_index);
		prtln("num asset children: " + assetNode.elements().size());

		Element sizeEl = assetNode.element("size");
		if (sizeEl == null) {
			QName qname = DocumentHelper.createQName("size", assetNode.getNamespace());
			sizeEl = DocumentHelper.createElement(qname);
			if (insert_index == assetNode.elements().size()) {
				prtln ("about to append SIZE to assetNode");
				assetNode.add(sizeEl);
			} else {
				prtln("about to insert size INFO at index: " + insert_index);
				assetNode.elements().add(insert_index, sizeEl);
			}
			insert_index++;
		}
		
		prtln("num asset children: " + assetNode.elements().size());
		sizeEl.setText(filesize);

		int sizeX = assetNode.elements().indexOf(sizeEl);
		prtln ("\nsize index: " + sizeX);
		
		MimeTypes mt = MimeTypes.getInstance();
		String mimeType = mt.getMimeType(assetFile);
		Element mimeTypeEl = assetNode.element("mimeType");
		if (mimeTypeEl == null) {
			QName qname = DocumentHelper.createQName("mimeType", assetNode.getNamespace());
			mimeTypeEl = DocumentHelper.createElement(qname);
			if (sizeX + 1 < assetNode.elements().size()) {
				prtln("about to insert size INFO at index: " + insert_index);
				assetNode.elements().add(sizeX + 1, mimeTypeEl);
			} else {
				prtln ("about to append MIME_TYPE to assetNode");
				assetNode.add(mimeTypeEl);
			}
		}
		mimeTypeEl.setText(mimeType);
	}


	/**
	 *  Convert a physical URL to an asset into a logical (citableURL) form
	 *
	 *@param  physicalAssetUrl  physical URL for asset
	 *@return                   citableURL form of url
	 *@exception  Exception     if physical path is not within the pysical
	 *      repository
	 */
	public static String makeLogicalAssetUrl(String physicalAssetUrl) throws Exception {
		prtln("makeLogicalAssetUrl with " + physicalAssetUrl);
		if (!physicalAssetUrl.startsWith(BASE_PHYSICAL_ASSET_URL)) {
			throw new Exception("AssetUrl (" + physicalAssetUrl + ") does not begin with " + BASE_PHYSICAL_ASSET_URL);
		}
		String baseLogicalAssetUrl = BASE_REPOSITORY_URL + "/assets";
		return FindAndReplace.replace(physicalAssetUrl, BASE_PHYSICAL_ASSET_URL, baseLogicalAssetUrl, true);
	}


	/**
	 *  Gets the primary and other assetNodes in the document
	 *
	 *@return    The assetNodes value
	 */
	public List getAssetNodes() {
		List primaryAssetNodes = this.selectNodes("/record/resources/primaryAsset");
		prtln(primaryAssetNodes.size() + " primaryAssetNodes found");
		List otherAssetNodes = this.selectNodes("/record/resources/otherAsset");
		prtln(otherAssetNodes.size() + " otherAssetNodes found");

		List assetNodes = new ArrayList();
		assetNodes.addAll(primaryAssetNodes);
		assetNodes.addAll(otherAssetNodes);
		return assetNodes;
	}


	/**
	 *  Gets the thumbnailNodes defined by this record
	 *
	 *@return    The thumbnailNodes value
	 */
	public List getThumbnailNodes() {
		return this.selectNodes("/record/general/thumbnail");
	}


	protected String id_path = "/record/general/recordID";


	/**
	 *  Gets the record ID
	 *
	 *@return    The id value
	 */
	public String getId() {
		return this.getTextAtPath(id_path);
	}


	private String url_path = "/record/general/urlOfRecord";


	/**
	 *  Gets the citable URL for this record
	 *
	 *@return    The url value
	 */
	public String getUrl() {
		return this.getTextAtPath(url_path);
	}


	/**
	 *  Sets the urlOfRecord (a citableUrl) for this record
	 *
	 *@param  newValue  The new url value
	 */
	public void setUrl(String newValue) {
		Element general = (Element) selectSingleNode("/record/general");
		if (general.element("urlOfRecord") == null) {
			QName qname = DocumentHelper.createQName("urlOfRecord", general.getNamespace());
			general.elements().add(2, DocumentHelper.createElement(qname));
		}
		this.setTextAtPath(url_path, newValue);
	}



	/**
	 *  Returns true if this record is under embargo, as determined by the date
	 *  range fields with a type in EMBARGO_TYPES.
	 *
	 *@return    The embargoed value
	 */
	boolean isEmbargoed() {
		if (this.ignoreEmbargo)
			return false;
		List drEls = selectNodes("/record/coverage/dateRange");
		// prtln (drEls.size() + " date ranges found");
		Date now = new Date();
		for (Iterator i = drEls.iterator(); i.hasNext(); ) {
			DateRange dateRange = new DateRange((Element) i.next());
			if (this.EMBARGO_TYPES.contains(dateRange.type) &&
					dateRange.getStartDate().before(now) &&
					now.before(dateRange.getEndDate())) {

				// prtln ("embargoed by " + dateRange.type);
				return true;
			}
		}
		return false;
	}


	/**
	 *  Gets the assetFile for provided physical url, which must begin with the
	 *  configured BASE_PHYSICAL_URL.
	 *
	 *@param  physicalUrl    url to a physical location in opensky, but closed off
	 *      access
	 *@return                the asset's file
	 *@exception  Exception  if the physical url is malformed or if file does not
	 *      exist
	 */
	static File getAssetFile(String physicalUrl) throws Exception {
		if (!physicalUrl.startsWith(BASE_PHYSICAL_ASSET_URL)) {
			throw new Exception("physicalUrl (" + physicalUrl + ") does not start with " + BASE_PHYSICAL_ASSET_URL);
		}
		String path = FindAndReplace.replace(physicalUrl, BASE_PHYSICAL_ASSET_URL, BASE_ASSET_DIR, false);
		prtln("asset physical path: " + path);
		File assetFile = new File(path);
		if (!assetFile.exists()) {
			throw new Exception("asset file does not exist at " + path);
		}
		return assetFile;
	}


	/*
	 *  --------------------------------
	 *  how to determine file path for assets?
	 *  - and, is that path even visible from DCS??
	 *  physical URL: http://nldr.library.ucar.edu/collections/osgc/OSGC-000-000-002-164.pdf
	 *  file path: /web/nldr/collections
	 *  so all we have to do is replace:
	 *  - http://nldr.library.ucar.edu/collections, with
	 *  - /web/nldr/collections
	 *  to get the physical path
	 *  to get mime-type:
	 *  1 - MimeTypes mt = MimeTypes.getInstance();
	 *  2 - String mimeType = mt.getMimeType("foo.tif");
	 */
	/**
	 *  The main program for the OsmRecordExporter class
	 *
	 *@param  args           The command line arguments
	 *@exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		// /Users/ostwald/devel/dcs-records/ccs-records/osm/osm/OSM-000-000-000-001.xml
		OsmRecordExporter.setDebug(true);

		String xmlPath = "C:/Users/ostwald/devel/ezid/dcs-metadata/osm/technotes/TECH-NOTE-000-000-000-240.xml";

		String xml = Files.readFile(xmlPath).toString();
		String propsPath = "C:/Users/ostwald/tmp/osm.to.doi.properties";
		NLDRProperties nldrProps = new NLDRProperties(propsPath);
		nldrProps.report();
		OsmRecordExporter rec = new OsmRecordExporter(xml, nldrProps);
		// pp(rec.getXmlNode());

		prtln("------");
		if (rec.isEmbargoed()) {
			prtln("EMBARGOED");
		} else {
			prtln("NOT embargoed");
		}

		String exported = OsmRecordExporter.getExportXml(xml, nldrProps);
		prtln("EXPORTED RECORD" + exported);
	}


	/**
	 *  Debugging
	 *
	 *@param  rec  Description of the Parameter
	 */
	static void showAssets(OsmRecordExporter rec) {
		prtln("\nAssets");
		showAssets(rec.getAssetNodes());
	}


	/**
	 *  Debugging
	 *
	 *@param  assets  Description of the Parameter
	 */
	static void showAssets(List assets) {
		showAssets(assets.iterator());
	}


	/**
	 *  Debugging
	 *
	 *@param  i  Description of the Parameter
	 */
	static void showAssets(Iterator i) {
		int counter = 0;
		while (i.hasNext()) {
			Element assetEl = (Element) i.next();
			String url = assetEl.attributeValue("url", "N/A");
			String title = assetEl.attributeValue("title", "Untitled");
			String order = assetEl.attributeValue("order", "N/A");
			prtln(String.valueOf(++counter) + " - " + title + " (" + order + ")");
		}
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 *@param  s  The String that will be output.
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("OsmRecordExporter: " + s);
			System.out.println(s);
		}
	}


	/**
	 *  Sets the debug attribute of the object
	 *
	 *@param  db  The new debug value
	 */
	public static void setDebug(boolean db) {
		debug = db;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 *@author    Jonathan Ostwald
	 */
	class DateRange {
		Element element = null;
		String type;
		String start;
		String end;
		Date never;


		/**
		 *  Constructor for the DateRange object
		 *
		 *@param  element  Description of the Parameter
		 */
		DateRange(Element element) {
			// pp (element);
			this.element = element;
			this.type = element.attributeValue("type", "");
			this.start = element.attributeValue("start", null);
			this.end = element.attributeValue("end", null);
			try {
				this.never = MetadataUtils.parseUnionDateType("5000");
			} catch (Exception e) {
				prtln("never error: " + e.getMessage());
			}
		}


		/**
		 *  Gets the startDate attribute of the DateRange object
		 *
		 *@return    The startDate value
		 */
		Date getStartDate() {
			try {
				return MetadataUtils.parseUnionDateType(this.start);
			} catch (Exception e) {
				prtln("couldn't parse start date: \"" + this.start + "\"");
			}
			return new Date(0);
		}


		/**
		 *  Gets the endDate attribute of the DateRange object
		 *
		 *@return    The endDate value
		 */
		Date getEndDate() {
			try {
				return MetadataUtils.parseUnionDateType(this.end);
			} catch (Exception e) {
				// end date is not required!
				// prtln ("couldn't parse end date: \"" + this.end + "\"");
			}
			return this.never;
		}

	}

}

