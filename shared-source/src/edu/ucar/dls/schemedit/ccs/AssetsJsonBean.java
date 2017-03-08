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
public class AssetsJsonBean  {

	private static boolean debug = true;
	private File protectedDir = null;

	/**
	 *  Constructor for the AssetsJsonBean object
	 *
	 * @param  path  NOT YET DOCUMENTED
	 */
	public AssetsJsonBean(String protectedDirPath) throws Exception {
		this.protectedDir = new File(protectedDirPath);
		if (!this.protectedDir.exists())
			throw new Exception ("protected dir does not exist at " + 
								  protectedDirPath);
		// prtln (" INSTANTATED - protectedDir is " + protectedDirPath);
	}

	public JSONObject getListJson (String[] collections) throws Exception {
		JSONObject selected = null;
		if (collections == null || collections.length == 0)
			// selected.addAll(this.listAll());
			selected = this.listAllJson(collections);
		else {
			selected = new JSONObject();
			for (int i=0;i<collections.length;i++) {
				selected.put (collections[i], this.listCollectionJson(collections[i]));
			}
		}
		return selected;
	}
	
	/**
	Returns a JSONArray containing filenames of assets for the collection
	
	this is the only one i'm using at the minute
	*/
	public JSONArray listCollectionJson (String collection) {
		
		JSONArray selected = new JSONArray();
		
		File collectionDir = new File (this.protectedDir, collection);
		if (!collectionDir.exists()) {
			prtln ("collectionDir does not exist");
			return selected;
		}	
		
		File [] files = collectionDir.listFiles(new AssetFilter());
		if (files == null) {
			prtln ("collectionDir not found or empty");
			return selected;
		}
		
		prtln ("\nCOLLECTION: " + collection + " (" + files.length + ")");
		
		
		for (int i=0; i<files.length; i++) {
			String file = files[i].getName();
			// prtln (" - " + file);
			selected.put (file);
		}
		return selected;
	}
	
	/**
	Returns a JSONObject mapping collection names to JSONArray containing 
	collection assets.
	
	*/
	public JSONObject listAllJson (String[] collections) throws Exception {
		
		JSONObject selected = new JSONObject();
		for (int i=0; i<collections.length; i++) {
			String collection = collections[i];
			// prtln (" - " + collection);
			selected.put(collection, this.listCollectionJson(collection));
		}
		return selected;
	}
	
	public String[] allCollectionNames () {
		File[] files = this.protectedDir.listFiles(new DirectoryFilter());
		String[] names = new String[files.length];
		for (int i=0;i<files.length;i++)
			names[i] = files[i].getName();
		return names;
	}
	
	/**
	Returns a JSONObject mapping collection names to JSONArray containing 
	collection assets.
	
	*/
	public Map<String,List<String>> listAll (String[] collections) throws Exception {
		// File [] collections = this.protectedDir.listFiles(new DirectoryFilter());
		Map<String,List<String>> selected = new HashMap<String,List<String>>();
		for (int i=0; i<collections.length; i++) {
			String collection = collections[i];
			// prtln (" - " + collection);
			selected.put(collection, this.listCollection(collection));
		}
		return selected;
	}
	
	private List<String> listCollection (String collection) {
		
		File collectionDir = new File (this.protectedDir, collection);
		File [] files = collectionDir.listFiles(new AssetFilter());
		
		// prtln ("COLLECTION: " + collection + " (" + files.length + ")");
		
		List<String> selected = new ArrayList<String>();
		
		// some collections may not have asset directories
		if (files != null) {
			for (int i=0; i<files.length; i++) {
				String file = files[i].getName();
				// prtln (" - " + file);
				selected.add(file);
			}
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
			if (!pathname.isFile())
				return false;
			if (pathname.getName().startsWith("."))
				return false;
			return true;
		}
	}
	
	/**
	Returns the FIRST collection found containing filename
	*/
	public String getCollectionForFilename (String filename) {
		prtln ("getCollectionForFilename() - " + filename);
		File [] collections = this.protectedDir.listFiles(new DirectoryFilter());
		for (int i=0; i<collections.length; i++) {
			String collection = collections[i].getName();
			// prtln (" - looking in " + collection);
			File assetFile = new File(collections[i], filename);
			if (assetFile.exists()) {
				// prtln ("   - asset found in " + collection);
				return collection;
			}
		}
		prtln ("asset not found");
		return null;
	}
	
	public List<FileInfo> getAssetFileInfoList (String filename) {
		List fileInfoList = new ArrayList<FileInfo>();
		File [] collections = this.protectedDir.listFiles(new DirectoryFilter());
		for (int i=0; i<collections.length; i++) {
			
			String collection = collections[i].getName();
			// prtln (" - looking in " + collection);
			File assetFile = new File(collections[i], filename);
			if (assetFile.exists()) {
				prtln ("   - asset found in " + collection);
				try {
					FileInfo fileInfo = new FileInfo(assetFile, collection);
					fileInfoList.add(fileInfo);
				} catch (Exception e) {
					prtln ("WARN: could not put fileInfo for" + collection);
				}
			}
		}
		prtln (fileInfoList.size() + " fileInfos found");
		return fileInfoList;
	}		
		
	public JSONArray getAssetFileInfoJson (String filename) {
		
		JSONArray fileInfoArray = new JSONArray();
		File [] collections = this.protectedDir.listFiles(new DirectoryFilter());
		for (int i=0; i<collections.length; i++) {
			
			String collection = collections[i].getName();
			// prtln (" - looking in " + collection);
			File assetFile = new File(collections[i], filename);
			if (assetFile.exists()) {
				prtln ("   - asset found in " + collection);
				try {
/* 					JSONObject fileInfo = new JSONObject();
					fileInfo.put("name", filename);
					fileInfo.put("collection", collection);
					fileInfo.put("lastModified", assetFile.lastModified());
					fileInfo.put("size", assetFile.length()); */
					FileInfo fileInfo = new FileInfo(assetFile, collection);
					fileInfoArray.put(fileInfo.toJson());
				} catch (Exception e) {
					prtln ("WARN: could not put fileInfo for" + collection);
				}
			}
		}
		prtln (fileInfoArray.length() + " fileInfos found");
		return fileInfoArray;
	}
	
	
	public JSONArray getCollectionsForAsset (String filename) {
		prtln ("getCollectionsListForAsset() - " + filename);
		JSONArray assetCollections = new JSONArray();
		File [] collections = this.protectedDir.listFiles(new DirectoryFilter());
		for (int i=0; i<collections.length; i++) {
			String collection = collections[i].getName();
			// prtln (" - looking in " + collection);
			File assetFile = new File(collections[i], filename);
			if (assetFile.exists()) {
				prtln ("   - asset found in " + collection);
				assetCollections.put (collection);
			}
		}
		prtln (assetCollections.length() + " collections found");
		return assetCollections;
	}
	
	public List<String> getCollectionsListForAsset (String filename) {
		prtln ("getCollectionsListForAsset() - " + filename);
		List assetCollections = new ArrayList<String>();
		File [] collections = this.protectedDir.listFiles(new DirectoryFilter());
		for (int i=0; i<collections.length; i++) {
			String collection = collections[i].getName();
			// prtln (" - looking in " + collection);
			File assetFile = new File(collections[i], filename);
			if (assetFile.exists()) {
				prtln ("   - asset found in " + collection);
				assetCollections.add (collection);
			}
		}
		prtln (assetCollections.size() + " collections found");
		return assetCollections;
	}
		
	/**
	 *  The main program for the AssetsJsonBean class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		prtln ("hello from AssetsJsonBean (" + args.length + ")");
		String protectedDir = "/Users/ostwald/Documents/Work/CCS/BSCS_Integration/protected";
		AssetsJsonBean bean = new AssetsJsonBean(protectedDir);
		String[] collections = {"kh_assess"};
		// JSONArray fake = bean.listCollectionJson("dps_tips");
		JSONObject fake = bean.listAllJson(collections);
		
		
		prtln (fake.toString(2));
		
		String filename = "ES1_engageStrategies.pdf";
		String collection = bean.getCollectionForFilename(filename);
		prtln ("collection for " + filename + " is " + collection);
		
		// List assetCollectionsList = bean.getCollectionsListForAsset(filename);
		
		// prtln(bean.getCollectionsForAsset(filename).toString(3));
		prtln(bean.getAssetFileInfoJson(filename).toString(3));
		
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("AssetsJsonBean: " + s);
			System.out.println("AssetsBean: " + s);
		}
	}
	
	public class FileInfo {
		private String name;
		private String collection;
		private long lastModified;
		private long size;
		
		public FileInfo (File assetFile, String collection) {
			
			this.name = assetFile.getName();
			this.collection = collection;
			this.lastModified = assetFile.lastModified();
			this.size = assetFile.length();
		}
		
		public String getName() {
			return name;
		}
		
		public String getCollection () {
			return collection;
		}
		
		public long getLastModified () {
			return lastModified;
		}
		
		public long getSize() {
			return size;
		}

		public JSONObject toJson () {
			JSONObject json = new JSONObject();
			try {
				json.put ("name", name);
				json.put ("collection", collection);
				json.put ("lastModified", lastModified);
				json.put ("size", size);
				
			} catch (JSONException e) {
				prtln ("WARN: toJson Failed: " + e.getMessage());
			}
			return json;
		}
		
		public String toString () {
			return name + " - " + collection ;
		}
	}

	

}
