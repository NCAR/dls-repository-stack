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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

public class AssetFileListener extends FileAlterationListenerAdaptor {

	private static boolean debug = true;
	
	private File folder = null;
	private String collection = null;
	private AssetFileManager fileManager = null;
	
	public AssetFileListener (String collection, AssetFileManager fileManager) {
		super();
		this.collection = collection;
		this.fileManager = fileManager;
	}
	
	@Override
	public void onFileCreate(File file) {
		try {
			// "file" is the reference to the newly created file
			prtln("File created: "
					+ file.getCanonicalPath());
			prtln (" - collection: " + collection);
			/* fileManager.getCollection(collection).updateFileInfo(file.getName()); */
			fileManager.updateFileInfo(file.getName(), collection);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		} catch (Throwable e) {
			prtln ("WARN: onFileCreate could not updateFileInfo: " + e.getMessage());
		}
	}

	// Is triggered when a file is deleted from the monitored folder
	@Override
	public void onFileDelete(File file) {
		try {
			// "file" is the reference to the removed file
			prtln("File removed: "
					+ file.getCanonicalPath());
			// "file" does not exists anymore in the location
			// prtln("File still exists in location: " + file.exists());
			
			fileManager.removeFileInfo(file.getName(), collection);
			
		} catch (IOException e) {
			e.printStackTrace(System.err);
		} catch (Throwable e) {
			prtln ("WARN: onFileDelete could not removeFileInfo: " + e.getMessage());
		} 
	}
	
	private static void prtln(String s) {
		if (debug) {
			System.out.println(s);
			// System.out.println("AssetFileMonitor: " + s);
		}
	}
}	
