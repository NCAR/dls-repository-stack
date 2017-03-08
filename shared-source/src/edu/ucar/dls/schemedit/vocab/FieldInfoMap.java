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

import java.io.*;
import java.util.*;
import java.text.*;
import java.net.*;

import org.dom4j.*;

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;

/**
 *  Populates the a fieldInfoMap by reading from a file specified in Framework
 *  config file (with name ending in "fields-list.xml"). The fields-list file
 *  contains a list of relative URIs that can be used to calculate the absolute
 *  URI to the individual fields files.<p>
 *
 *  E.g.,
 *  <ul>
 *    <li> the fields-list for dlese_anno format is http://www.dlese.org/Metadata/annotation/1.0.00/build/fields-list.xml.
 *
 *    <li> within the fields-list file a relative URI is: annotation/1.0.00/fields/annotation-anno-fields-en-us.xml
 *
 *    <li> the absolute uri to this field file is: http://www.dlese.org/Metadata/annotation/1.0.00/fields/annotation-anno-fields-en-us.xml.
 *    The resolution of absolute Uris is done in {@link #getFieldsFileUri(URI,
 *    String)}.
 *  </ul>
 *
 *
 * @author    ostwald
 */
public class FieldInfoMap {
	private static boolean debug = true;
	protected HashMap map = null;
	private String directoryUri;


	/**  Constructor for the FieldInfoMap object */
	public FieldInfoMap() {
		map = new HashMap();
	}


	/**
	 *  Constructor for the FieldInfoMap object
	 *
	 * @param  uri  NOT YET DOCUMENTED
	 */
	public FieldInfoMap(String uri) {
		this();
		directoryUri = uri;
	}


	/**
	 *  Read a listing of URIs Fields files from directoryUri and then loads each
	 *  of the listed files as FieldInfoReader objects, which are stored in a map.
	 *
	 * @exception  Exception  Description of the Exception
	 */
	public void init()
		 throws Exception {
		map.clear();

		URI fileListUri = new URI(directoryUri);

		Document fieldsFileListing = SchemEditUtils.getRemoteDoc(directoryUri, true);

		// print xml file for debugging
		// prtln("FieldsDirectory at : " + fileListUri.toString());
		// prtln (Dom4jUtils.prettyPrint(fieldsFileListing));

		Node filesNode = fieldsFileListing.selectSingleNode("metadataFieldsInfo/files");
		if (filesNode == null) {
			prtln("no filesNode found");
			return;
		}

		Element filesElement = (Element) filesNode;

		// load each of the files in the fields file listing
		for (Iterator i = filesElement.elementIterator(); i.hasNext(); ) {
			Node fileNode = (Node) i.next();
			String filePath = fileNode.getText().trim();
			URI fieldFileUri = null;
			try {
				fieldFileUri = new URI(filePath);
				if (!fieldFileUri.isAbsolute())
					fieldFileUri = getFieldsFileUri(fileListUri, filePath);
			} catch (Exception e) {
				prtln("could not compute uri to fields file: " + e.getMessage());
				continue;
			}

			try {
				FieldInfoReader reader = new FieldInfoReader(fieldFileUri);
				putFieldInfo(reader.getPath(), reader);

				// Sleep so we don't get shut out by www.dlese.org
				Thread.sleep(100);
			} catch (Exception e) {
				prtln("field info reader ERROR: " + e.getMessage());
				e.printStackTrace();
			}
		}
		prtln("FieldInfoMap initialized - " + map.size() + " fields files read");
	}


	/**
	 *  Gets the uri of the directory file (e.g., http://ns.nsdl.org/ncs/lar/1.00/build/fields-list.xml)
	 *  that determines which fields files are loaded for a particular framework.
	 *
	 * @return    The directoryUri value
	 */
	public String getDirectoryUri() {
		return this.directoryUri;
	}


	/**
	 *  Add a FieldInfoReader to the map.
	 *
	 * @param  xpath   Description of the Parameter
	 * @param  reader  Description of the Parameter
	 */
	public void putFieldInfo(String xpath, FieldInfoReader reader) {
		map.put(xpath, reader);
	}


	/**
	 *  Gets the fieldInfo for given xpath.
	 *
	 * @param  xpath  Description of the Parameter
	 * @return        The fieldInfo value or null if not found.
	 */
	public FieldInfoReader getFieldInfo(String xpath) {
		return (FieldInfoReader) map.get(xpath);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  xpath  Description of the Parameter
	 */
	public void removeFieldInfo(String xpath) {
		map.remove(xpath);
	}


	/**
	 *  Gets the keySet attribute of the FieldInfoMap object
	 *
	 * @return    The keySet value
	 */
	public Set getKeySet() {
		if (map == null) {
			map = new HashMap();
		}
		return map.keySet();
	}


	/**
	 *  Reload all the FieldInfoReaders in the map
	 *
	 * @exception  Exception  Description of the Exception
	 */
	public void reload()
		 throws Exception {
		init();
	}


	/**
	 *  Gets all the FieldInfoReaders as a list.
	 *
	 * @return    a List of FieldInfoReaders
	 */
	public List getAllFieldInfo() {
		ArrayList list = new ArrayList();
		list.addAll(map.values());
		return list;
	}


	/**
	 *  Gets all the fields (xpaths) having FieldInfoReaders
	 *
	 * @return    a list of xpaths to Fields
	 */
	public List getFields() {
		ArrayList keys = new ArrayList();
		for (Iterator i = getKeySet().iterator(); i.hasNext(); ) {
			keys.add((String) i.next());
		}
		return keys;
	}


	/**
	 *  Returns true if there is a FieldInfoReader present for the specified xpath.
	 *
	 * @param  xpath  Description of the Parameter
	 * @return        Description of the Return Value
	 */
	public boolean hasFieldInfo(String xpath) {
		return getFields().contains(xpath);
	}



	/**
	 *  Gets an absolute URI be resolving a baseUri (pointing to the fields-list
	 *  file) and a relativePath.<p>
	 *
	 *  The first two parts of the relative path form a format, version key. E.g.
	 *  for relativePath "annotation/1.0.00/fields/annotation-anno-fields-en-us.xml"
	 *  the key is "annotation", "1.0.00". To form an absolute fieldsFileUri, the
	 *  part of the baseUri above the key are joined with the relativePath.<p>
	 *
	 *  Exceptions are thrown if the relativePath does not contain a key, or if the
	 *  baseUri does not contain the key specified by the relativePath.
	 *
	 * @param  baseUri        baseUri pointing to a fields-list file
	 * @param  relativePath   a relative path containing a framework, version key
	 *      and the name of the fields file.
	 * @return                an absolute fieldsFileUri
	 * @exception  Exception  if absoluteFieldsFileUri cannot be computed
	 */
	public static URI getFieldsFileUri(URI baseUri, String relativePath) throws Exception {
		/*
		prtln ("baseUri: " + baseUri.toString());
		prtln ("authority: " + baseUri.getAuthority());
		prtln ("schemeSpecificPart: " + baseUri.getSchemeSpecificPart());
		prtln ("userInfo: " + baseUri.getUserInfo());
		prtln ("scheme: " + baseUri.getScheme());
		prtln ("host: " + baseUri.getHost());
		prtln ("port: " + baseUri.getPort());
		prtln ("path: " + baseUri.getPath());
		*/
		// relativePath must not begin with '/'
		if (relativePath.charAt(0) == '/')
			throw new Exception("relativePath (\"" + relativePath + "\") is not relative");

		// the first two levels of the relativePath are the key - they specify frameworkName and version
		String[] relativeSplits = relativePath.split("/");
		if (relativeSplits.length < 3)
			throw new Exception("illegal relative path (\"" + relativePath + "\") - at least 3 segements are required: ");
		String frameworkName = relativeSplits[0];
		String version = relativeSplits[1];
		String key = frameworkName + "/" + version;

		// now get rid of the tail segments of the baseUri from the key down
		int x = baseUri.getPath().indexOf(key);
		if (x == -1)
			throw new Exception("framework/version key (\"" + key + "\") not found in baseUri (\"" + baseUri.toString() + "\")");

		String newPath = baseUri.getPath().substring(0, x) + relativePath;

		// this contructor works with fileURIs as well as with urlURIs
		return new URI(baseUri.getScheme(), baseUri.getAuthority(), newPath, null, null);
	}


	/**
	 *  Utility to download fields files to local disk.
	 *
	 * @exception  Exception  Description of the Exception
	 */
	void downLoadFieldsFiles()
		 throws Exception {
		prtln("hello world");
		String format = "dlese_anno";
		File dir = new File("/devel/ostwald/metadata-frameworks/metadata-ui/" + format + "/fields-files/");
		if (!dir.exists() && !dir.mkdirs()) {
			throw new Exception("could not make directory at " + dir.getAbsolutePath());
		}
		List fieldInfoReaders = getAllFieldInfo();
		prtln("there are " + fieldInfoReaders.size() + " readers");
		for (Iterator i = getAllFieldInfo().iterator(); i.hasNext(); ) {
			FieldInfoReader fir = (FieldInfoReader) i.next();
			Document doc = SchemEditUtils.getLocalizedXmlDocument(fir.uri);
			String path = fir.uri.getPath();
			String[] splits = path.split("/");
			String fileName = null;
			if (splits != null && splits.length > 0) {
				fileName = splits[splits.length - 1];
			}
			if (fileName == null) {
				throw new Exception("could not get filename from " + path);
			}
			File dest = new File(dir, fileName);
			Dom4jUtils.writePrettyDocToFile(doc, dest);
			prtln("wrote " + dest.toString());
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public String toString() {
		String ret = "";
		for (Iterator i = map.keySet().iterator(); i.hasNext(); ) {
			String key = (String) i.next();
			ret += key + "\n";
		}
		return ret;
	}


	/**
	 *  Sets the debug attribute of the FieldInfoMap class
	 *
	 * @param  d  The new debug value
	 */
	public static void setDebug(boolean d) {
		debug = d;
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 * @param  s  The String that will be output.
	 */
	protected static void prtln(String s) {
		if (debug) {
			System.out.println("FieldInfoMap: " + s);
		}
	}
	
}

