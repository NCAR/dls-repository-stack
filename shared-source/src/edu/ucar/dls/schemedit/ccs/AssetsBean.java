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
package edu.ucar.dls.schemedit.ccs;

import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.io.*;
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.util.Files;

import edu.ucar.dls.util.strings.FindAndReplace;
import org.json.*;

/**
 * Bean for selecting asset names from a protected directory
	
	list - select filenames for specified collections, return as json
	
	structure of list: for now make it a JSONArray
	
	FIRST:
	assume single collection. just return a JSONArray of filenames
	
	the controller can request the bean via getListAsJson 
	
	THEN:
	think about what data do send if we have mulitple collections
	- and how to ask for it (from UI)
	- and how to present it there
 
 * @author     ostwald<p>
 *
 */
public class AssetsBean {

	private static boolean debug = true;
	private File protectedDir = null;

	/**
	 *  Constructor for the AssetsBean object
	 *
	 * @param  path  NOT YET DOCUMENTED
	 */
	AssetsBean(String protectedDirPath) throws Exception {
		this.protectedDir = new File(protectedDirPath);
		if (!this.protectedDir.exists())
			throw new Exception ("protected dir does not exist at " + 
								  protectedDirPath);
	}

	public List getList (String[] collections) {
		List<String> selected = new ArrayList<String>();
		if (collections == null || collections.length == 0)
			selected.addAll(this.listAll());
		else {
			for (int i=0;i<collections.length;i++) {
				selected.addAll (this.listCollection(collections[i]));
			}
		}
		return selected;
	}
	
	private List<String> listCollection (String collection) {
		
		File collectionDir = new File (this.protectedDir, collection);
		File [] files = collectionDir.listFiles(new AssetFilter());
		
		prtln ("\nCOLLECTION: " + collection + " (" + files.length + ")");
		
		List<String> selected = new ArrayList<String>();
		for (int i=0; i<files.length; i++) {
			String file = files[i].getName();
			// prtln (" - " + file);
			selected.add(file);
		}
		return selected;
	}
	
	private List<String> listAll () {
		File [] collections = this.protectedDir.listFiles(new DirectoryFilter());
		List<String> selected = new ArrayList<String>();
		for (int i=0; i<collections.length; i++) {
			String collection = collections[i].getName();
			// prtln (" - " + collection);
			selected.addAll(this.listCollection(collection));
		}
		return selected;
	}
	
	
	class DirectoryFilter implements FileFilter {
		
		public boolean accept (File pathname) {
			return pathname.isDirectory();
		}
	}
	
	class AssetFilter implements FileFilter {
		
		public boolean accept (File pathname) {
			return pathname.isFile();
		}
	}
	
		
	/**
	 *  The main program for the AssetsBean class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		prtln ("hello from AssetsBean");
		String protectedDir = "/Users/ostwald/Documents/Work/CCS/BSCS_Integration/protected";
		AssetsBean bean = new AssetsBean(protectedDir);
		// List fake = bean.listAll();
		String[] collections = {"kh_assess"};
		List fake = bean.getList(null);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("AssetsBean: " + s);
			System.out.println(s);
		}
	}
	

}
