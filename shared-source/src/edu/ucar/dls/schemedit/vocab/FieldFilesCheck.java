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
package edu.ucar.dls.schemedit.vocab;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.schema.SchemaHelper;
import edu.ucar.dls.xml.schema.SchemaHelperException;
import edu.ucar.dls.xml.schema.SchemaNodeMap;

import java.io.*;
import java.util.*;
import java.text.*;
import java.net.*;
import java.lang.*;

import org.dom4j.*;


/**
 *  Command line routine that checks fields files for well-formedness, and
 *  ensures that the xpaths associated with the field files exist within the
 *  given metadata framework.
 *
 * @author    ostwald <p>
 *
 */
public class FieldFilesCheck {

	private HashMap map = null;
	private URI listingUri = null;
	private URI schemaUri = null;
	private SchemaHelper schemaHelper = null;

	int filesRead = 0;
	List badPaths = null;
	List readerErrors = null;
	Map fieldNames = null;
	SchemaPaths schemaPaths = null;

	public FieldFilesCheck(String listing, String schema, String rootElementName) throws Exception {
		this (new URI(listing), new URI (schema), rootElementName);
	}	
		
	/**
	 *  Constructor for the FieldFilesCheck object
	 *
	 * @param  listingUri     NOT YET DOCUMENTED
	 * @param  schemaUri      NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public FieldFilesCheck(URI listingUri, URI schemaUri, String rootElementName) throws Exception {
		this.schemaUri = schemaUri;
		this.listingUri = listingUri;

		String dateStamp = new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
		prtln("\nFieldFilesCheck - " + dateStamp + "\n");
		prtln("File Listing: " + listingUri.toString());
		prtln("Schema: " + schemaUri.toString());
		prtln("");

		String scheme = schemaUri.getScheme();
		// prtln ("SchemaUri scheme: " + scheme);
		try {
			if (scheme.equals("file"))
				schemaHelper = new SchemaHelper(new File(schemaUri.getPath()), rootElementName);
			else if (scheme.equals("http"))
				schemaHelper = new SchemaHelper(schemaUri.toURL(), rootElementName);
			else
				throw new Exception("ERROR: Unrecognized scheme (" + scheme + ")");

			if (schemaHelper == null)
				throw new Exception();
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().trim().length() > 0)
				throw new Exception("Unable to instantiate SchemaHelper at " + schemaUri + ": " + e.getMessage());
			else
				throw new Exception("Unable to instantiate SchemaHelper at " + schemaUri);
		}
	}
	

	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @author    Jonathan Ostwald
	 */
	public class FieldFileError {
		/**  NOT YET DOCUMENTED */
		public URI location;
		/**  NOT YET DOCUMENTED */
		public String data;


		/**
		 *  Constructor for the FieldFileError object
		 *
		 * @param  location  NOT YET DOCUMENTED
		 * @param  data      NOT YET DOCUMENTED
		 */
		FieldFileError(URI location, String data) {
			this.location = location;
			this.data = data;
		}
		
		public String getLocation () {
			return this.location.toString();
		}
		
		public String getData () {
			return this.data;
		}
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @author    Jonathan Ostwald
	 */
	public class ReaderError extends FieldFileError {
		/**
		 *  Constructor for the ReaderError object
		 *
		 * @param  location  NOT YET DOCUMENTED
		 * @param  data      NOT YET DOCUMENTED
		 */
		ReaderError(URI location, String data) {
			super(location, data);
			String[] splits = data.split("\\:");
			if (splits.length == 3) {
				this.data = splits[2].trim();
			}
		}
	}


	/**
	 *  Initialized with schemaNodeMap, contains a mapping of each schemaNode to an empty list. As fields files
	 are processed a SchemaPaths instance is used to verify that the path is legal, to keep track of which paths
	 have been seen. 
	 *
	 * @author    Jonathan Ostwald
	 */
	public class SchemaPaths {
		Map map = null;


		/**
		 *  Constructor for the SchemaPaths object
		 *
		 * @param  schemaNodeMap  NOT YET DOCUMENTED
		 */
		SchemaPaths(SchemaNodeMap schemaNodeMap) {
			map = new TreeMap();
			for (Iterator i = schemaHelper.getSchemaNodeMap().getKeys().iterator(); i.hasNext(); ) {
				String path = (String) i.next();
				map.put(path, new ArrayList());
			}
		}


		/**
		 *  Gets the legalPath attribute of the SchemaPaths object
		 *
		 * @param  xpath  NOT YET DOCUMENTED
		 * @return        The legalPath value
		 */
		boolean isLegalPath(String xpath) {
			if (xpath == null || xpath.trim().length() == 0)
				return false;
			// KLUDGE for LAR framework
			if (edu.ucar.dls.schemedit.MetaDataFramework.isVirtualPath (xpath, "lar"))
				return true;
			return (map.containsKey(xpath));
		}


		/**
		 *  NOT YET DOCUMENTED
		 *
		 * @param  xpath  NOT YET DOCUMENTED
		 * @param  uri    NOT YET DOCUMENTED
		 */
		void markAsSeen(String xpath, URI uri) {
			List locs = (List) map.get(xpath);
			if (locs != null) {
				locs.add(uri);
				map.put(xpath, locs);
			}
			else {
				// this should never happen since map contains all legal paths
				prtln("add error: no xpath found for " + xpath);
			}
		}


		/**
		 *  Gets the locs attribute of the SchemaPaths object
		 *
		 * @param  xpath  NOT YET DOCUMENTED
		 * @return        The locs value
		 */
		List getLocs(String xpath) {
			return (List) map.get(xpath);
		}


		/**
		 *  Gets the unseenPaths attribute of the SchemaPaths object
		 *
		 * @return    The unseenPaths value
		 */
		List getUnseenPaths() {
			List ret = new ArrayList();
			for (Iterator i = map.keySet().iterator(); i.hasNext(); ) {
				String key = (String) i.next();
				List locs = getLocs(key);
				if (locs == null || locs.size() == 0) {
					ret.add(key);
				}
			}
			return ret;
		}


		/**
		 *  Gets the multiples attribute of the SchemaPaths object
		 *
		 * @return    The multiples value
		 */
		Map getMultiples() {
			Map multiples = new TreeMap();
			for (Iterator i = map.keySet().iterator(); i.hasNext(); ) {
				String key = (String) i.next();
				List locs = getLocs(key);
				if (locs.size() > 1) {
					multiples.put(key, locs);
				}
			}
			return multiples;
		}
	}

		/**  NOT YET DOCUMENTED */
	void doReport() {
		prtln(filesRead + " fields files read\n");

		if (readerErrors.size() > 0) {
			prtln(readerErrors.size() + " files could not be read:");
			for (Iterator i = readerErrors.iterator(); i.hasNext(); ) {
				FieldFileError error = (FieldFileError) i.next();
				prtln(error.data);
				prtln("");
			}
		}
		else {
			prtln("All Listed Files were successfully read\n");
		}

		if (badPaths.size() > 0) {
			prtln(badPaths.size() + " bad Xpaths found:");
			for (Iterator i = badPaths.iterator(); i.hasNext(); ) {
				FieldFileError error = (FieldFileError) i.next();
				prtln(error.location.toString());
				prtln("\tbad path: " + error.data);
				prtln("");
			}
		}
		else {
			prtln("No bad Xpaths found\n");
		}

		List unSeenPaths = schemaPaths.getUnseenPaths();
		if (unSeenPaths == null || unSeenPaths.size() == 0)
			prtln("\nAll Xpaths defined in schema have corresponding field files\n");
		else {
			prtln("\n" + unSeenPaths.size() + " Xpaths have no corresponding field file:");
			for (Iterator i = unSeenPaths.iterator(); i.hasNext(); ) {
				prtln("\t" + (String) i.next());
			}
		}

		Map multiples = schemaPaths.getMultiples();
		if (multiples.size() > 0) {
			prtln("\n" + multiples.size() + " Xpaths are contained in mulitple fields files:");
			for (Iterator i = multiples.keySet().iterator(); i.hasNext(); ) {
				String key = (String) i.next();
				List locations = (List) multiples.get(key);
				prtln(key);
				for (Iterator j = locations.iterator(); j.hasNext(); ) {
					URI uri = (URI) j.next();
					prtln("\t" + uri.toString());
				}
				prtln("");
			}
		}
		
		Map dupFieldNames = this.getDupFieldNames();
		
		if (dupFieldNames.size() > 0) {
			prtln("\n" + dupFieldNames.size() + " Field Names occur in more than one file");
			for (Iterator i = dupFieldNames.keySet().iterator(); i.hasNext(); ) {
				String key = (String) i.next();
				List dupErrors = (List) dupFieldNames.get(key);
				prtln(key);
				for (Iterator j = dupErrors.iterator(); j.hasNext(); ) {
					FieldFileError dupError = (FieldFileError) j.next();
					prtln("\t" + dupError.getLocation() + "(" + dupError.getData() + ")");
				}
				prtln("");
			}
		}
	}



	/**
	 *  Read a listing of URIs Fields files from directoryUri and then loads each
	 *  of the listed files as FieldInfoReader objects, which are stored in a map.
	 *
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public void doCheck() throws Exception {
		Document fieldsFileListing = null;
		try {
			fieldsFileListing = SchemEditUtils.getLocalizedXmlDocument(listingUri);
		} catch (Exception e) {
			throw new Exception("ERROR: File listing document either does not exist or cannot be parsed as XML");
		}

		// prtln("Processing fields file listing at : " + listingUri.toString());

		Node filesNode = fieldsFileListing.selectSingleNode("/metadataFieldsInfo/files");
		if (filesNode == null) {
			throw new Exception("no filesNode found");
		}

		Element filesElement = (Element) filesNode;
		/* 		URI baseUri = null;
		try {
			baseUri = new URI (filesElement.attributeValue("file"));
		} catch (URISyntaxException e) {
			throw new Exception (e.getMessage());
		} */
		filesRead = 0;
		fieldNames = new HashMap();
		badPaths = new ArrayList();
		readerErrors = new ArrayList();
		schemaPaths = new SchemaPaths(schemaHelper.getSchemaNodeMap());
		// load each of the files in the fields file listing
		for (Iterator i = filesElement.elementIterator(); i.hasNext(); ) {
			Node fileNode = (Node) i.next();
			String fileName = fileNode.getText();
			FieldInfoReader reader = null;
			String xpath;
			try {
				URI myUri = FieldInfoMap.getFieldsFileUri(listingUri, fileName);
				try {
					reader = new FieldInfoReader(myUri);
				} catch (Exception e) {
					readerErrors.add(new ReaderError(myUri, e.getMessage()));
					continue;
				}

				filesRead++;

				try {
					xpath = reader.getPath();
				} catch (Throwable pathEx) {
					badPaths.add(new FieldFileError(myUri, "path not found"));
					continue;
				}

				if (!schemaPaths.isLegalPath(xpath)) {
					/* throw new Exception ("ERROR: Fields file at " + myUri.toString() + " contains an illegal path: " + xpath); */
					badPaths.add(new FieldFileError(myUri, xpath));
				}
				else {
					schemaPaths.markAsSeen(xpath, myUri);
				}
				
				String fieldName = null;
				try {
					fieldName = reader.getName();
				} catch (Throwable pathEx) {
					continue;
				}

				List dupList = (List)this.fieldNames.get(fieldName);
				if (dupList == null)
					dupList = new ArrayList();
				dupList.add (new FieldFileError(myUri, xpath));
				this.fieldNames.put(fieldName, dupList);

			} catch (Throwable t) {
				prtln(t.getMessage());
			}
		}
	}

	public Map getDupFieldNames () {
		Map dupFieldNames = new HashMap();
		for (Iterator i=this.fieldNames.keySet().iterator();i.hasNext();) {
			String fieldName = (String)i.next();
			List dups = (List)this.fieldNames.get(fieldName);
			if (dups.size() > 1) {
				dupFieldNames.put (fieldName, dups);
			}
		}
		return dupFieldNames;
	}
	
	/**
	 *  Gets the filesRead attribute of the FieldFilesCheck object
	 *
	 * @return    The filesRead value
	 */
	public int getFilesRead() {
		return this.filesRead;
	}


	/**
	 *  Gets the badPaths attribute of the FieldFilesCheck object
	 *
	 * @return    The badPaths value
	 */
	public List getBadPaths() {
		return this.badPaths;
	}


	/**
	 *  Gets the readerErrors attribute of the FieldFilesCheck object
	 *
	 * @return    The readerErrors value
	 */
	public List getReaderErrors() {
		return this.readerErrors;
	}


	/**
	 *  Gets the unseenPaths attribute of the FieldFilesCheck object
	 *
	 * @return    The unseenPaths value
	 */
	public List getUnseenPaths() {
		return schemaPaths.getUnseenPaths();
	}


	/**
	 *  Gets the multiples attribute of the FieldFilesCheck object
	 *
	 * @return    The multiples value
	 */
	public Map getMultiples() {
		return schemaPaths.getMultiples();
	}



	/**
	 *  Read a set of fields files
	 *
	 * @param  args           The command line arguments
	 */
	public static void main(String[] args) {
		boolean require_input = false;
		// usage assumes this script is being called from the shell script named "checkFieldFiles"
		String usage = "Usage: checkFieldFiles fieldsFileListingUri schemaUri rootElementName";

		if (require_input && args.length != 3) {
			prtln(usage);
			System.exit(1);
		}

		String fields_list = "http://ns.nsdl.org/ncs/lar/1.00/build/fields-list.xml";
		String schema_uri = "http://ns.nsdl.org/ncs/lar/1.00/schemas/lar.xsd";
		String root_element_name = "record";
		
		if (args.length > 0)
			fields_list = args[1];
		
		if (args.length > 1)
			schema_uri = args[2];
		
		if (args.length > 2)
			schema_uri = args[3];

		try {
			// FieldFilesCheck checker = new FieldFilesCheck(new URI (listing), new URI (schema));
			FieldFilesCheck checker = new FieldFilesCheck(
						new URI(fields_list), 
						new URI(schema_uri), 
						root_element_name );
			checker.doCheck();
			checker.doReport();
		} catch (Exception e) {
			prtln("ERROR: " + e.getMessage());
			e.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 * @param  s  The String that will be output.
	 */
	private static void prtln(String s) {
		System.out.println(s);
	}
}

