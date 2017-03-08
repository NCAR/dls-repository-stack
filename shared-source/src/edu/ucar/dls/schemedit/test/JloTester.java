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
package edu.ucar.dls.schemedit.test;

import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.io.*;
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.input.InputManager;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XPathUtils;
import edu.ucar.dls.xml.XMLUtils;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.util.Files;
import org.dom4j.*;
import org.dom4j.tree.*;
import org.dom4j.io.*;
import org.jaxen.SimpleNamespaceContext;

import edu.ucar.dls.util.strings.FindAndReplace;


/**
 *  Class for testing dom manipulation with help from {@link edu.ucar.dls.xml.schema.SchemaHelper}
 * I am different
 * @author     ostwald<p>
 *
 */
public class JloTester {

	private static boolean debug = true;

	Document doc = null;
	DocMap docMap = null;
	DocumentFactory df = null;
	NamespaceRegistry namespaces = null;
	String T = "\t";
	String NL = "\n";

	/**
	 *  Constructor for the JloTester object
	 *
	 * @param  path  NOT YET DOCUMENTED
	 */
	JloTester(String path) throws Exception {
		try {
			doc = Dom4jUtils.getXmlDocument (new File (path));
			docMap = new DocMap (doc);
		} catch (Exception e) {
			throw new Exception ("init error: " + e.getMessage());
		} catch (Throwable t) {
			throw new Exception ("unknown error!");
		}
	}

	JloTester(URL url) throws Exception {
		try {
			doc = Dom4jUtils.getXmlDocument (url);
			docMap = new DocMap (doc);
		} catch (Exception e) {
			throw new Exception ("init error: " + e.getMessage());
		} catch (Throwable t) {
			throw new Exception ("unknown error!");
		}
	}


	static void DrNums (String [] args) {
		Map map = getAccessionNumberMap();
		prtln (map.size() + " mappings read");
		String drNum = "DR000454";
		if (args.length > 0)
			drNum = args[0];
		prtln (resolveDrNum (drNum));
	}

	static void dateStuff (String[] args) throws Exception {
		Date now = new Date();
		prtln ("utc: " + SchemEditUtils.utcDateString(now));
		prtln ("full: " + SchemEditUtils.fullDateString(now));

		Date myDate = SchemEditUtils.getSimpleDateFormat().parse ("2010-03-02");
		prtln ("myDate: " + SchemEditUtils.fullDateString(myDate));

		prtln ("now?: " + SchemEditUtils.fullDateString(new Date()));
	}

	static void fileSizeStuff (String[] args) throws Exception  {
		String path = "C:/tmp/sync-error.jpg";
		File file = new File(path);
		if (!file.exists())
			throw new Exception ("file does not exist at " + path);
/* 		prtln ("file length in bytes: " + Long.toString(file.length()));

		Long bytes = file.length();
		int intsize = bytes.intValue();

		prtln ("file length in Kbytes: " + (intsize / 1000)); */

		int kbytes = ((Long)file.length()).intValue();
		String filesize = new Integer(kbytes / 1000).toString() + " KB";

		prtln ("file length in Kbytes: " + filesize);
	}

	/**
	 *  The main program for the JloTester class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {

		// fileSizeStuff(args);
		// dateStuff(args);

		String filename = "default_filename.xml";
		if (args.length > 0)
			filename = args[0];

		int dot = filename.lastIndexOf(".xml");
		if (dot == -1)
			throw new Exception ("dot not found in " + filename);
		prtln ("dot is at " + dot);

		String newName = filename.substring(0,dot) + "_backup.xml";
		prtln ("new filename: " + newName);
	}

	public static int getSelectedCATStartGrade(String[] gradeConstraints) {

		return Math.min (Integer.parseInt(gradeConstraints[0]), Integer.parseInt(gradeConstraints[1]));
	}

	static String resolveDrNum (String dr) {
		Pattern drPattern = Pattern.compile ("DR000[0-9]{3}");
		Matcher drMatcher = drPattern.matcher (dr);
		if (drMatcher.matches()) {
			prtln ("MATCHES");
			return "foo";
		}
		else {
			prtln ("NOPE");
			return null;
		}
	}


	static Map getAccessionNumberMap () {
		Map map = new HashMap();
		String path = "C:/Documents and Settings/ostwald/devel/projects/nldr-project/web/WEB-INF/data/accessionNumberMappings.xml";
		Document doc = null;
		try {
			doc = Dom4jUtils.getXmlDocument(new File (path));
		} catch (Exception e) {
			prtln (e.getMessage());
			return null;
		}
		for (Iterator i=doc.selectNodes("/accessionNumberMappings/mapping").iterator();i.hasNext();) {
			Element e = (Element)i.next();
			map.put (e.attributeValue("drNumber"), e.attributeValue("path"));
		}
		return map;
	}

	static String replaceDirectives (String s) {
		// pattern to detect page directives (e.g., includes) that were inserted
		// into Document as text and therefore have the tags escaped.
		Pattern p = Pattern.compile("&lt;(%@.*?)%&gt;");
		Matcher m = null;
		while (true) {
			m = p.matcher(s);
			if (m.find()) {
				prtln ("MATCH: \"" + m.group(1) + "\"");
				String repl = "<" + m.group(1) + ">";
				s = m.replaceFirst (repl);
			}
			else
				break;
		}
		return s;
	}

	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  n  NOT YET DOCUMENTED
	 */
	private static void pp(Node n) {
		prtln(Dom4jUtils.prettyPrint(n));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("JloTester: " + s);
			System.out.println(s);
		}
	}

}
