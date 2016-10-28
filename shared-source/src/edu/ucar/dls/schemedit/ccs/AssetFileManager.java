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

package edu.ucar.dls.schemedit.ccs;

import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.io.*;
import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.util.Files;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import edu.ucar.dls.util.strings.FindAndReplace;
import org.json.*;

/**
* TODO: AssetFileManager is a singleTon
 
 * @author     ostwald<p>
 *
 */
public class AssetFileManager  {

	private static boolean debug = true;
	private File protectedDir = null;
	private File trashDir = null;
	final long pollingInterval = 10 * 1000;
	private Map<String,AssetCollection> assetCollections = null;

	/**
	 *  Constructor for the AssetFileManager object
	 *
	 * @param  path  NOT YET DOCUMENTED
	 */
	public AssetFileManager(String protectedDirPath) throws Exception {
		prtln ("AssetFileManager INSTANTIATING ...");
		this.protectedDir = new File(protectedDirPath);
		if (!this.protectedDir.exists())
			throw new Exception ("protected dir does not exist at " + 
								  protectedDirPath);
			
		// injest assetData
		try {
/* 			this.assetCollections = new HashMap<String,AssetCollection>();
			File [] collections = this.protectedDir.listFiles(new DirectoryFilter());
			for (int i=0; i<collections.length; i++) {
				AssetCollection assetCollection = new AssetCollection(collections[i]);
				this.assetCollections.put(assetCollection.getName(), assetCollection);
			} */
			this.updateAssetData();
		} catch (Exception e) {
			prtln ("initialize assetCollections ERROR: " + e.getMessage());
		}
		
		// if a trash directory doesn't exist, create it
		this.trashDir = new File(this.protectedDir, ".trash");
		if (!this.trashDir.exists() && !this.trashDir.mkdir())
			throw new Exception ("Could not create trash directory");
		
		prtln ("assetCollections ingested (" + assetCollections.size() + ")");
			
		// initialize the monitors
		try {
			// initializeMonitors();
		} catch (Exception e) {
			prtln ("initializeMonitors ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		
		prtln ("collection monitors started ...");
		
		
		// prtln (" INSTANTATED - protectedDir is " + protectedDirPath);
	}
	
	public void updateAssetData () {
		// prtln ("updateAssetData()");
		Map<String,AssetCollection> assetCollections =  new HashMap<String,AssetCollection>();
		try {
			File [] collections = this.protectedDir.listFiles(new DirectoryFilter());
			for (int i=0; i<collections.length; i++) {
				AssetCollection assetCollection = new AssetCollection(collections[i]);
				assetCollections.put(assetCollection.getName(), assetCollection);
			}
		} catch (Exception e) {
			prtln ("initialize assetCollections ERROR: " + e.getMessage());
		}
		this.assetCollections = assetCollections;
	}
	
	public String toString() {
		String s = "AssetFileManager";
		for (String filename : this.assetCollections.keySet()) {
			s += "\n - " + this.assetCollections.get(filename);
		}
		return s;
	}

	/**
	
	@method getCollections
	*/
	public Collection<AssetCollection> getCollections() {
		return this.assetCollections.values();
	}
	
	/**
	
	@method getCollection
	*/
	public AssetCollection getCollection(String collection) {
		return this.assetCollections.get(collection);
	}
	
	
	public void updateFileInfo (String filename, String collection) throws Exception {
		try {
			getCollection(collection).updateFileInfo(filename);
		} catch (Throwable t) {
			String msg = "could not update: " + t.getMessage();
			prtln (msg);
			if (t instanceof NullPointerException)
				t.printStackTrace();
			throw new Exception (msg);
		}
	}
	
	public void removeFileInfo (String filename, String collection) throws Exception {
		try {
			getCollection(collection).removeFileInfo(filename);
		} catch (Throwable t) {
			String msg = "could not remove: " + t.getMessage();
			prtln (msg);
			if (t instanceof NullPointerException)
				t.printStackTrace();
			throw new Exception (msg);
		}
	}
	
	private void initializeMonitors () throws Exception {
		for (AssetCollection assetCollection : getCollections()) {
			this.monitorCollection (assetCollection.getName());
		}
	}
	
	/**
	
	@method monitorCollection
	*/
    private FileAlterationMonitor monitorCollection (String collection) throws Exception {
    	// prtln ("monitorCollection() - " + collection);
    	
    	AssetCollection assetCollection = this.getCollection(collection);
    	
        if (assetCollection == null) {
            // Test to see if monitored folder exists
            throw new RuntimeException("AssetCollection not found: " + collection);
        }
        
        File folder = assetCollection.getDirectory();
        if (!folder.exists())
        	throw new RuntimeException("Folder not found at: " + folder);

        FileAlterationObserver observer = new FileAlterationObserver(folder);
        FileAlterationMonitor monitor =
                new FileAlterationMonitor(pollingInterval);
                
		FileAlterationListener listener = new AssetFileListener(collection, this);

        observer.addListener(listener);
        monitor.addObserver(observer);
        monitor.start();
        // prtln (" .. monitoring " + collection);
        return monitor;
    }
	
	/**
	
	Called from UploadAction.doListAssets()
	
	@method getListJson
	*/
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
		prtln (" - listCollectionJson - " + collection);
		JSONArray selected = new JSONArray();
		
		AssetCollection assetCollection = this.getCollection(collection);
		if (assetCollection == null) {
			prtln ("collection not found for: " + collection);
			return selected;
		}
		
		Collection<FileInfo> fileInfos = assetCollection.getFiles();
		prtln (fileInfos.size() + " files found in " + collection);
		for (FileInfo fileInfo : assetCollection.getFiles()) {
			selected.put(fileInfo.name);
		}

		return selected;
	}
	
	/**
	Returns a JSONObject mapping collection names to JSONArray containing 
	collection assets.
	
	*/
	public JSONObject listAllJson (String[] collections) throws Exception {
		
		JSONObject selected = new JSONObject();
		
		for (AssetCollection assetCollection : getCollections()) {
			String collection = assetCollection.getName();
			selected.put(collection, this.listCollectionJson(collection));
		}
		
		return selected;
	}
	
	/**
	
	@method allCollectionNames
	*/
	public String[] allCollectionNames () {
		
		Collection<AssetCollection> collections = this.getCollections();
		List<String> names = new ArrayList<String>();
		for (AssetCollection assetCollection : collections)
			names.add(assetCollection.getName());
		return (String[])names.toArray (new String[]{});
	}
	
	/**
	Returns a  mapping of collection names to Lists of file/asset names for that collection.
	
	*/
	public Map<String,List<String>> listAll (String[] collections) throws Exception {
		Map<String,List<String>> selected = new HashMap<String,List<String>>();
		
		for (int i=0; i<collections.length; i++) {
			String collection = collections[i];
			// prtln (" - " + collection);
			selected.put(collection, this.listCollection(collection));
		}
		return selected;
	}
	
	/**
	
	@method listCollection
	*/
	private List<String> listCollection (String collection) {
		
		List<String> selected = new ArrayList<String>();
		AssetCollection assetCollection = getCollection(collection);
		// some collections may not have asset directories
		if (assetCollection != null) {
			for (FileInfo fileInfo : assetCollection.getFiles()) {
				String filename = fileInfo.getName();
				// prtln (" - " + filename);
				selected.add(filename);
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
/* 	public String getCollectionForFilename (String filename) {
		prtln ("getCollectionForFilename() - " + filename);
		
		for (AssetCollection assetCollection : this.getCollections()) {
			FileInfo fileInfo = assetCollection.get(filename);
			if (fileInfo != null)
				return assetCollection.getName();
		}

		prtln ("asset not found for filename");
		return null;
	} */
	
	
	/**
	Returns a list of asset FileInfos for the given asset filename.
	@method getAssetFileInfoList
	*/
/* 	public List<FileInfo> getAssetFileInfoList (String filename) {
		List fileInfoList = new ArrayList<FileInfo>();
		for (AssetCollection coll : this.getCollections()) {
			FileInfo fileInfo = coll.get(filename);
			if (fileInfo != null)
				fileInfoList.add(fileInfo);
		}
		prtln (fileInfoList.size() + " fileInfos found");
		return fileInfoList;
	} */		
		
	/**
	Supplies JSON to UploadAction.doGetAssetInfo
	@method getAssetFilesJson
	*/	
	public JSONArray getAssetFilesJson (String filename) {
		prtln ("getAssetFilesJson - " + filename);
		JSONArray fileInfoArray = new JSONArray();

		for (AssetCollection coll : this.getCollections()) {
			FileInfo fileInfo = coll.get (filename);
			if (fileInfo != null)
				fileInfoArray.put(fileInfo.toJson());		
		}
		
		prtln (fileInfoArray.length() + " fileInfos found");
		return fileInfoArray;
	}
	
	public boolean renameAssetFile (String oldFileName, String newFileName, String collection) throws Exception {
		prtln ("AFM: renameAssetFile()");
		FileInfo oldFileInfo = null;
		try {
			oldFileInfo = this.getCollection(collection).get(oldFileName);
			if (oldFileInfo == null)
				throw new Exception ("oldFileInfo could not be obtained for " +
						collection + "/" + oldFileName);
		} catch (Throwable t) {
			prtln ("could not get oldFileInfo: " + t.getMessage());
			return false;
		}
		File srcAssetFile = oldFileInfo.getFile(); // where we are renamein from 		
		
/* 		if (!srcAssetFile.exists())
			throw new Exception ("Asset could not be found at " + srcAssetFile);
		prtln ("done with srcAssetFile"); */
		
		File newAssetFile = new File (this.protectedDir, collection+'/'+newFileName);
		// prtln ("does newAssetFile exist?? " + newAssetFile.exists());
		
	/* 	
		
		if (!newAssetFile.getParentFile().canWrite())
			throw new Exception ("Application does not have write permission for Asset file at " + newAssetFile);	
		
		try {
			srcAssetFile.renameTo(newAssetFile);
			updateFileInfo(newAssetFile.getName(), collection);
			removeFileInfo(oldFileInfo.getName(), collection);
			prtln ("renameAssetFile renamed asset to" + newAssetFile.getName());
		} catch (Throwable t) {
			throw new Exception ("rename ERROR: " + t.getMessage());
		}
		 */
		try {
			if (!this.moveAsset(srcAssetFile, newAssetFile))
				throw new Exception ("move did not succeed for unknown reason");
			prtln ("renameAssetFile renamed asset to" + newAssetFile.getName());
		} catch (Throwable t) {
			throw new Exception ("rename ERROR: " + t.getMessage());
		}	
		
		prtln ("AFTA: does newAssetFile exist?? " + newAssetFile.exists());
		return true;
		
	}
	
	/**
	We don't really delete it, but rather move to trash folder
	@method deleteAssetFile
	*/
	public boolean deleteAssetFile (String filename, String collection) {
		prtln ("deleteAssetFile() " + collection + "/" + filename);
		FileInfo fileInfo = null;
		try {
			fileInfo = this.getCollection(collection).get(filename);
			if (fileInfo == null)
				throw new Exception ("not found");
		} catch (Throwable e) {
			prtln ("WARN: could not find asset file for " + collection + "/" + filename);
			return false;
		}
		
		File assetFile = fileInfo.getFile();
		try {
			File trashFile = new File (this.trashDir, collection+"/"+filename);
			// prtln ("trashFile: " + trashFile);
			// prtln ("is Trash file a trashFile?? " + isTrashFile(trashFile));
			
			if (this.moveAsset(assetFile, trashFile))
				prtln (" - trashed: " + filename);
			else
				prtln (" - NOT trashed");
			
		} catch (Throwable e) {
			prtln ("ERROR: deleteAssetFile: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	/**
	We don't really delete it, but rather move to trash folder
	@method deleteAssetFile
	*/
	public boolean undeleteAssetFile (String filename, String collection) {
		prtln ("undeleteAssetFile() " + collection + "/" + filename);
		
		File restoredFile = new File (this.protectedDir, collection + "/" + filename);
		File trashFile = new File (this.trashDir, collection+"/"+filename);
		
		try {
			
			// prtln ("trashFile: " + trashFile);
			// prtln ("is Trash file a trashFile?? " + isTrashFile(trashFile));
			
			if (this.moveAsset(trashFile, restoredFile))
				prtln (" - restored: " + filename);
			else
				prtln (" - NOT restored");
			
		} catch (Throwable e) {
			prtln ("ERROR: undeleteAssetFile: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	private boolean isTrashFile (File file) {
		return (file.getParentFile().getParentFile().compareTo(this.trashDir) == 0);
	}
	
	private boolean moveAsset (File src, File dst) throws Exception {
		try {
			if (!src.exists())
				throw new Exception ("Asset could not be found at " + src);
			
			if (!src.canWrite())
				throw new Exception ("Application does not have write permission for Asset file at " + src);
			
			if (!dst.getParentFile().exists() && !dst.getParentFile().mkdir()) {
				throw new Exception ("Could not create Dest directory at " + dst.getParentFile());
			}
			
			if (!dst.getParentFile().canWrite())
				throw new Exception ("Application does not have write permission for Dest directory at " + dst.getParentFile());
			
			if (!src.renameTo(dst))
				throw new Exception ("The renameTo operation was not successful - reason unknown");
			
			// remove fileInfo for src -  - unless it is a trash item
			if (!isTrashFile(src)) {
				this.removeFileInfo(src.getName(), src.getParentFile().getName()); 
				// prtln ("removed fileInfo for: " + src);
			} else {
				// prtln ("DIDNT remove FileInfo for trash file: " + src);
			}
			
			// update fileInfo for dst - unless it is a trash item
			
			if (!isTrashFile(dst)) {
				this.updateFileInfo(dst.getName(), dst.getParentFile().getName());
				// prtln ("updated fileInfo for " + dst);
			} else {
				// prtln ("DIDNT update file Info for trash file: " + dst);
			}
			
		} catch (Exception e) {
			prtln ("ERROR moveAsset: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	
	@method getFileInfo
	*/
	public FileInfo getFileInfo (String filename, String collection) throws Exception {
		try {
			return this.assetCollections.get(collection).get(filename);
		} catch (Throwable t) {
			prtln ("WARN: couldnt get fileInfo for: " + collection + "/" + filename);
		}
		return null;
	}
	
	public static boolean isAssetUrl (String url) {
		// prtln ("isAssetUrl() : " + url); 
		try {
			new URL (url);
		} catch (Exception e) {
			prtln ("not a URL!");
			return false;
		}

		if (!url.startsWith("http://ccs.dls.ucar.edu/home/protected/")) {
			prtln (" -- non starter");
			return false;
		}
		String[] splits = url.split("/");
		// prtln (splits.length + " splits found");
		// for (int i=0;i<splits.length;i++)
			// prtln ("- " + i + ": " + splits[i]);
		if (splits.length != 7) {
			prtln ("wrong number of splits");
			return false;
		}
		return true;
	}
	
	public static String getAssetUrlFilename (String url) {
		try {
			if (!isAssetUrl(url)) {
				throw new Exception ("provided url is not an assetUrl: " + url);
			}
			return url.split("/")[6];
		} catch (Throwable t) {
			prtln ("WARN: getAssetUrlFilename - provited url was not processed: " + url + "\n" + t.getMessage());
		}
		return null;
	}
	
	public static String getAssetUrlCollection (String url) {
		try {
			if (!isAssetUrl(url)) {
				throw new Exception ("provided url is not an assetUrl: " + url);
			}
		
			return url.split("/")[5];
		} catch (Throwable t) {
			prtln ("WARN: getAssetUrlCollection - provited url was not processed: " + url);
		}
		return null;		
	}
			
	
	/**
	
	@method getFileInfoJson
	*/
	public JSONObject getFileInfoJson (String filename, String collection) throws Exception {
		return getFileInfo(filename, collection).toJson();
	}
		
	/**
	Collects 
	@method getCollectionsListForAsset
	*/
/* 	public List<String> getCollectionsListForAsset (String filename) {
		prtln ("getCollectionsListForAsset() - " + filename);
		List assetCollections = new ArrayList<String>();
		
		// look in each collection and return list of collections where found
		for (AssetCollection coll : this.getCollections()) {
			// prtln (" - " + coll.getName());
			FileInfo fileInfo = coll.get (filename);
			if (fileInfo != null)
				assetCollections.add(coll.getName());
		}

		prtln (assetCollections.size() + " collections found");
		return assetCollections;
	} */
		
	/**
	 *  The main program for the AssetFileManager class
	 *
	 * @param  args           The command line arguments
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public static void main(String[] args) throws Exception {
		prtln ("\n--------------------\nhello from AssetFileManager (" + args.length + ")");
		String protectedDir = "/Users/ostwald/Documents/Work/CCS/BSCS_Integration/protected";
		AssetFileManager bean = new AssetFileManager(protectedDir);
		String[] collections = {"kh_assess"};
		// JSONArray fake = bean.listCollectionJson("dps_tips");
		// prtln (fake.toString(2));
 
		String filename = "FIZZ-WODDLE-POO copy.pdf";
		String collection = "assess_bscs";
		
		bean.undeleteAssetFile(filename, collection);
		
/* 		String oldFilename = "FIZZ-WODDLE -floopy.pdf";
		String newFilename = "FIZZ-WODDLE-opy.pdf";
		String collection = "assess_bscs";
		if (!bean.renameAssetFile(oldFilename, newFilename, collection)) {
			prtln ("WHOOPS! not renamed");
		}
		else
			prtln ("renamed"); */

	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println(s);
			// System.out.println("AssetFileManager: " + s);
		}
	}
	
	public class AssetCollection {
		private File directory = null;
		private String name;
		private Map<String, FileInfo> assetFiles = null;
		
		public AssetCollection (File directory) {
			this.directory = directory;
			this.name = directory.getName();
			try {
				this.assetFiles = this.readDirectory();
			} catch (Exception e) {
				prtln ("WARN readDirectory: " + e.getMessage());
			}
		}
		
		public int size() {
			return this.assetFiles.size();
		}
		
		public Collection<FileInfo> getFiles() {
			return this.assetFiles.values();
		}
		
		public FileInfo get(String filename) {
			try {
				return this.assetFiles.get(filename);
			} catch (Exception e) {
				prtln ("WARN: AssetCollection.get could not find " + filename);
			}
			return null;
		}
		
		public Map<String,FileInfo> readDirectory() throws Exception {
			if (!this.directory.exists())
				throw new Exception ("directory does not exist at " + this.directory);
			HashMap<String,FileInfo> assetFileMap = new HashMap<String,FileInfo>();
			File [] files = directory.listFiles(new AssetFilter());
			for (int i=0; i<files.length; i++) {
				File assetFile = files[i];
				try {
					FileInfo fileInfo = new FileInfo(assetFile, this.name);
					assetFileMap.put(fileInfo.name, fileInfo);
				} catch (Exception e) {
					prtln ("WARN: could not put fileInfo for" + this.name);
				}	
			}
			return assetFileMap;
		}
		
		public String getName () {
			return this.name;
		}
		
		public File getDirectory() {
			return this.directory;
		}
		
		public synchronized void updateFileInfo (String filename) throws Exception {
			File assetFile = new File (this.directory, filename);
			if (!assetFile.exists())
				throw new Exception ("updateFileInfo: file not found at: " + assetFile);
			FileInfo fileInfo = new FileInfo (assetFile, this.name);
			this.assetFiles.put(fileInfo.name, fileInfo);
			prtln (" - updated FileInfo for " + this.name + "/" + filename);
		}
		
		public synchronized void removeFileInfo (String filename) throws Exception {
			File assetFile = new File (this.directory, filename);
			if (assetFile.exists()) {
				if (!assetFile.delete())
					throw new Exception ("Could not delete assetFile at: " + assetFile);
			}
			this.assetFiles.remove(filename);
			prtln (" - removed FileInfo for " +  this.name + "/" + filename);
		}
		
		public String toString() {
			String s = "AssetCollection for " + this.name;
			s += " - " + this.assetFiles.size() + " files";
			return s;
		}
		
	}

			
	
	public class FileInfo {
		private String name;
		private String collection;
		private long lastModified;
		private long size;
		private File file;
		
		public FileInfo (File assetFile, String collection) {
			this.file = assetFile;
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
		public File getFile () {
			return this.file;
		}
		
		public String toString () {
			return name + " - " + collection ;
		}
	}

	

}
