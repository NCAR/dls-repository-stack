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

import java.text.SimpleDateFormat;

import edu.ucar.dls.services.dds.toolkit.*;

import java.io.*;
import java.util.*;
import java.text.*;
import java.net.URI;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.config.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.util.*;
import edu.ucar.dls.index.SimpleLuceneIndex;

import org.dom4j.*;
import org.json.*;

public class MockUploadAction {
	
	private static boolean debug = true;
	
	DDSServicesToolkit ddsWSServiceForCurricula = null;
	AssetSearcher assetSearcher = null;
	String protectedDir = null;
	// AssetsJsonBean assetBean = null;
	AssetFileManager assetBean = null;

	public MockUploadAction (String ddswsBaseUrlForCurricula, String protectedDir) {
		this.setDdswsBaseUrlForCurricula ( ddswsBaseUrlForCurricula);
		this.assetSearcher = new AssetSearcher(ddswsBaseUrlForCurricula);
		this.protectedDir = protectedDir;
		try {
			this.assetBean = new AssetFileManager(protectedDir);
		} catch (Exception e) {
			prtln ("AssetFileManager ERROR: " + e.getMessage());
		}
		 
	}

	/**
	
	simulate call used in UploadAction
	*/
	public AssetFileManager getAssetsBean() throws Exception {
		return this.assetBean;
	}
	
	/**
	
	simulate call used in UploadAction
	*/
	public AssetSearcher getAssetSearcher () throws Exception {
		return this.assetSearcher;
	}
	
	/**
	 *  Sets the ddswsBaseUrlForCurricula attribute of the CurriculaDataBean object
	 *
	 * @param  ddswsBaseUrlForCurricula  The new ddswsBaseUrlForCurricula value
	 */
	public void setDdswsBaseUrlForCurricula(String ddswsBaseUrlForCurricula) {
		ddsWSServiceForCurricula = new DDSServicesToolkit(ddswsBaseUrlForCurricula, "MyUserAgent", "MyUserAgent");
		/* 
		ddsWSServiceForCurricula = new StatusAwareDDSServicesToolkit (ddswsBaseUrlForCurricula, 
																	  "MyUserAgent", 
																	  "MyUserAgent"); */
	}

	public List<String> getOrphans (String[] collections) throws Exception {
		List<String> urls = this.getAssetSearcher().getAssetUrls(collections);
		// prtln (mockAction.getAssetsBean().listAllJson().toString(2));
		prtln (urls.size() + " asset urls found");
		
		Map<String,List<String>> allAssets = this.getAssetsBean().listAll(collections);
		
		/*
			we want to know: which of the assets are not cataloged: 
				I.e., which collection/filename does not match a url
		*/
		
		Set keys = allAssets.keySet();
		prtln (keys.size() + " collections searched");
		List<String> orphans = new ArrayList<String>();
		for (String collection : allAssets.keySet()) {
			// prtln (" - " + collection + " (" + ((List)allAssets.get(collection)).size() + ")");
			
			for (String filename : allAssets.get(collection)) {
				String assetUrl = "http://ccs.dls.ucar.edu/home/protected/" 
										+ collection + "/" + filename;
				if (!urls.contains(assetUrl))
					orphans.add (collection + "/" + filename);
			}
		}

		return orphans;
	}
	
	public JSONArray getOrphansJson (String[] collections) throws Exception {
		List<String> orphans = getOrphans(collections);
		JSONArray orphansJson = new JSONArray();
		for (String orphan : orphans) {
			orphansJson.put(orphan);
		}
		return orphansJson;
	}
	
	public static void main (String[] args) throws Exception {
		prtln ("hello from MockUploadAction");
		
		String ddswsBaseUrlForCurricula = "http://localhost:8070/curricula/services/ddsws1-1";
		String protectedDir = "/Users/ostwald/Documents/Work/CCS/BSCS_Integration/5-18/5-30-data/REORG/protected";
		// 
		// String ddswsBaseUrlForCurricula = "http://localhost:8080/schemedit/services/ddsws1-1";
		// String protectedDir = "C:/Users/ostwald/devel/protected";

		MockUploadAction mockAction = new MockUploadAction(ddswsBaseUrlForCurricula, protectedDir);
		
		String my_filename = "ES1_engageStrategies.pdf";
		if (args.length > 0)
			my_filename = args[0];

		String[] collections = mockAction.assetBean.allCollectionNames();
		// String[] collections = new String []{"comment_bscs", "assess_bscs"};
		// String[] collections = new String []{"comment_bscs"};
		// String[] collections = new String []{"assess_bscs"};
		// String[] collections = new String []{"dps_tips"};
		
/* 		List<String> orphans = mockAction.getOrphans(collections);
		prtln (orphans.size() + " orphans found");	
		for (String orphan : orphans) {
			prtln (" - " + orphan);
		} */
		
		JSONArray orphansJson = mockAction.getOrphansJson(collections);
		prtln ("ORPHANS - assets for which no metadata url was found");
		prtln (orphansJson.toString(2));
	}
		
	private static void prtln(String s) {
		if (debug) {
			// System.out.println("MockUploadAction: " + s);
			System.out.println(s);
		}
	}
	
	private static void pp (Node node) {
		prtln (Dom4jUtils.prettyPrint(node));
	}
}

