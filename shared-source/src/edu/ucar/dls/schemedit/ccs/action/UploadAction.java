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
package edu.ucar.dls.schemedit.ccs.action;

import edu.ucar.dls.schemedit.action.DCSAction;
import edu.ucar.dls.schemedit.ccs.action.form.UploadForm;
// import edu.ucar.dls.schemedit.ccs.AssetsJsonBean;
import edu.ucar.dls.schemedit.ccs.AssetFileManager;
import edu.ucar.dls.schemedit.ccs.AssetSearcher;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.security.user.User;
import edu.ucar.dls.schemedit.security.access.Roles;
import edu.ucar.dls.schemedit.repository.RepositoryService;

import edu.ucar.dls.util.Utils;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.oai.*;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.xml.XMLUtils;
import edu.ucar.dls.util.TimedURLConnection;
import edu.ucar.dls.repository.SetInfo;

import javax.servlet.*;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.dom4j.Element;
import org.dom4j.Branch;
import org.dom4j.Attribute;
import org.dom4j.Node;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.*;
import java.lang.*;
import java.io.*;
import java.text.*;
import java.net.*;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.util.Hashtable;
import java.util.Locale;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import edu.ucar.dls.webapps.tools.GeneralServletTools;

/**
 *  Action that manages uploading and selecting assets from a CCS protected
 *  directory.
 *
 * @author    Jonathan Ostwald
 */
public final class UploadAction extends DCSAction {
	private static boolean debug = true;


	/**  Constructor for the UploadAction object */
	public UploadAction() { }

	// --------------------------------------------------------- Public Methods

	/**
	 *  Process the specified HTTP request, and create the corresponding HTTP
	 *  response (or forward to another web component that will create it). Return
	 *  an <code>ActionForward</code> instance describing where and how control
	 *  should be forwarded, or <code>null</code> if the response has already been
	 *  completed.
	 *
	 * @param  mapping        The ActionMapping used to select this instance
	 * @param  response       The HTTP response we are creating
	 * @param  form           The ActionForm for the given page
	 * @param  req            The HTTP request.
	 * @return                The ActionForward instance describing where and how
	 *      control should be forwarded
	 * @exception  Exception  If error.
	 */
	public ActionForward execute(
	                             ActionMapping mapping,
	                             ActionForm form,
	                             HttpServletRequest req,
	                             HttpServletResponse response)
		 throws Exception {

		ServletContext servletContext = servlet.getServletContext();
		ActionErrors errors = initializeFromContext(mapping, req);
		
		String action = req.getParameter("action");
		try {
			System.out.println ("\n--------------------------------\nUploadAction");
			printParams(req);
			// SchemEditUtils.showRequestParameters(req);

			HttpSession session = req.getSession(true);

			// If we're in read-only mode, don't continue:
/* 			if (CCSServlet.getIsReadOnly()) {
				prtln("CCS is in read-only mode. Modifications by users blocked...");
				req.setAttribute("errorPublicDisplay", "The service is in read-only mode. Saves are currently disabled. Please try again shortly.");
				return mapping.findForward("save.resources");
			}
*/
			UploadForm uploadForm = (UploadForm) form;
			uploadForm.setUserAssetCollections (this.getUserAssetCollections(req)); 	
			// prtln (uploadForm.getUserAssetCollections().size() + " assetCollections");
 
			String path = req.getRequestURI();
			prtln("Path: " + path);

			// Prepare the page/form for upload a user's binary resource:
			if (action.equals("uploadDisplayExisting") || action.equals("uploadNew") || action.equals("uploadReplace")) {
				return doPrepareUploadFile(action, (UploadForm) form, mapping, req, response);
			}
			
			if (action.equals("helper")) {
				return doPresentUploadHelper (action, (UploadForm) form, mapping, req, response);
			}
			
			if (action.equals("list")) {
				return doListAssets ((UploadForm) form, mapping, req, response);
			}
			
			else if (action.equals("assetInfo")) {
				return doGetAssetInfo((UploadForm) form, mapping, req, response);
			}

			else if (action.equals("orphanInfo")) {
				return doGetOrphanInfo((UploadForm) form, mapping, req, response);
			}
			
			else if (action.equals("rename")) {
				return doRenameAsset((UploadForm) form, mapping, req, response);
			}
			
			else if (action.equals("delete")) {
				return doDeleteAssets((UploadForm) form, mapping, req, response);
			}

			else if (action.equals("undo")) {
				return doUndoCommand((UploadForm) form, mapping, req, response);
			}
			
			// Upload a user's binary resource:
			else if (action.equals("upload")) {
				return doUploadFile((UploadForm) form, mapping, req, response);
			}

			prtlnErr ("ERROR: Invalid action requested: '" + action + "'");
			req.setAttribute("error", "Invalid action requested: '" + action + "'");
			return mapping.findForward("upload.error");
		} catch (Throwable e) {
			prtln("UploadAction caught exception: " + e);
			e.printStackTrace();
			ByteArrayOutputStream stackTraceStream = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(stackTraceStream));
			String errMsg = " " + stackTraceStream.toString();
			req.setAttribute("error", "Error: " + errMsg);

			return mapping.findForward("upload.error");

		}
	}
	
		/**
	
	expects collection, oldFilename, and newFilename
	*/
	private ActionForward doRenameAsset (
	                                          UploadForm uploadForm,
	                                          ActionMapping mapping,
	                                          HttpServletRequest request,
	                                          HttpServletResponse response) throws Exception {
		prtln ("doRenameAsset()");

		String collection = request.getParameter("collection");
		prtln("- collection: " + collection);
		
		String oldFilename = request.getParameter("oldFilename");
		prtln("- oldFilename: " + oldFilename);
		
		String newFilename = request.getParameter("newFilename");
		prtln("- newFilename: " + newFilename);
		
		JSONObject responseJson = new JSONObject();
		Map requestMap = new HashMap();
		requestMap.put("verb", "rename");
		requestMap.put("collection", collection);
		requestMap.put("oldFilename", oldFilename);
		requestMap.put("newFilename", newFilename);
		requestMap.put("verb", "rename");
		responseJson.put ("request", requestMap);
		
		try {
			if (collection == null || collection.trim().length() == 0)
				throw new Exception ("collection parameter is required");
			collection = collection.trim();
			
			if (oldFilename == null || oldFilename.trim().length() == 0)
				throw new Exception ("oldFilename parameter is required");
			oldFilename = oldFilename.trim();

			if (newFilename == null || newFilename.trim().length() == 0)
				throw new Exception ("newFilename parameter is required");
			newFilename = newFilename.trim();			
			
			// check to see that this user is able to access this collection
			
			AssetFileManager assetFileManager = getAssetsBean();
			if (!assetFileManager.renameAssetFile (oldFilename, newFilename, collection))
				throw new Exception ("did NOT rename asset file");
			
			try {
				this.getAssetSearcher().clearCacheEntries(collection);
			} catch (Exception e) {
				prtlnErr ("ERROR: could not clearCacheEntries: " + e.getMessage());
			}	
			
			
			Map fileInfo = new HashMap();
			fileInfo.put("collection", collection);
			fileInfo.put("filename", newFilename);
			
			responseJson.put ("success", fileInfo);
			prtln ("added success element to responseJson");
			prtln (responseJson.toString(2));
			
/* 		} catch (Exception e) {
			prtlnErr ("doRename ERROR: " + e.getMessage());
			responseJson.put ("error", e.getMessage()); */
		} catch (Throwable e) {
			prtlnErr ("doRename ERROR: " + e.getMessage());
			String msg = e.getMessage();
			if (msg == null) {
				msg = "Unknown error";
			}
			e.printStackTrace();
			responseJson.put ("error", msg);
		}
		
		
		request.setAttribute ("searchResponse", responseJson.toString(2));
		return mapping.findForward ("upload.search.response");
  }

	/**
	
	expects collection, oldFilename, and newFilename
	*/
	private ActionForward doUndoCommand (
	                                          UploadForm uploadForm,
	                                          ActionMapping mapping,
	                                          HttpServletRequest request,
	                                          HttpServletResponse response) throws Exception {
		prtln ("doUndoCommand()");

		String command = request.getParameter("command");
		prtln("- command: " + command);
		
		String collection = request.getParameter("collection");
		prtln("- collection: " + collection);
		
		String oldFilename = request.getParameter("oldFilename");
		prtln("- oldFilename: " + oldFilename);
		
		String newFilename = request.getParameter("newFilename");
		prtln("- newFilename: " + newFilename);
		
		String[] ids = request.getParameterValues("id");
		
		JSONObject responseJson = new JSONObject();
		Map requestMap = new HashMap();
		requestMap.put("verb", "undo");
		requestMap.put("command", command);
		if (ids != null)
			requestMap.put("id", Arrays.asList(ids));
		requestMap.put("collection", collection);
		requestMap.put("oldFilename", oldFilename);
		requestMap.put("newFilename", newFilename);
		responseJson.put ("request", requestMap);
		
		Map results = new HashMap<String,String>();
		AssetFileManager assetFileManager = this.getAssetsBean();
		
		try {
			if (command == null || command.trim().length() == 0)
				throw new Exception ("command parameter is required");
			command = command.trim();			
			
			if (command.equals("rename")) {
				
				requestMap.put("command", command);
				
				if (collection == null || collection.trim().length() == 0)
					throw new Exception ("collection parameter is required");
				collection = collection.trim();				
				
				if (oldFilename == null || oldFilename.trim().length() == 0)
					throw new Exception ("oldFilename parameter is required");
				oldFilename = oldFilename.trim();				
				
				if (newFilename == null || newFilename.trim().length() == 0)
					throw new Exception ("newFilename parameter is required");
				newFilename = newFilename.trim();	
				prtln("- newFilename: " + newFilename);
				
				// CHECK TO SEE THAT THIS USER IS ABLE TO ACCESS THIS COLLECTION
				
				// COIULD GTHE FOLLOWING BE DONE IN ASSET_FILE_MANAGER??
				// make the asset path and locate the file to be renamed
				String primaryContentPath = servlet.getServletContext().getInitParameter("primaryContentPathOnServer");
				
				
				if (!assetFileManager.renameAssetFile (newFilename, oldFilename, collection))
					throw new Exception ("did NOT rename asset!");
				
				Map fileInfo = new HashMap();
				fileInfo.put("collection", collection);
				fileInfo.put("filename", oldFilename);
			
				responseJson.put ("success", fileInfo);
			}
			else if (command.equals("delete")) {
				
				if (ids == null || ids.length == 0)
					throw new Exception ("ids parameter is required");
				
				JSONArray undeleted = new JSONArray();
				
				// for all ids:
				// - parse into collection, filename
				// - mv from trash to collection
				for (int i=0;i<ids.length;i++) {
					try {
						prtln (" - " + i + ": " + ids[i]);
						// here we would undelete
						
						String[] splits = ids[i].split ("\\|");
						prtln (splits.length + " splits found");
						String myCollection = splits[0];
						String myFilename = splits[1];
						assetFileManager.undeleteAssetFile (myFilename, myCollection);
						this.getAssetSearcher().clearCacheEntries(myCollection);
						
						undeleted.put (ids[i]);
					} catch (Exception e) {
						prtln ("WARN: could not undelete " + ids[i]);
					}
				}
					
				prtln ("UNDO DELETE NOT YET IMPLEMENTED");
				JSONObject success = new JSONObject();
				success.put("undeleted", undeleted);
				responseJson.put ("success", success);
			}
		
		} catch (Exception e) {
			prtlnErr ("doRename ERROR: " + e.getMessage());
			String msg = e.getMessage();
			if (msg == null) {
				msg = "Unknown error";
				e.printStackTrace();
			}
				
			responseJson.put ("error", msg);
		}
		
		request.setAttribute ("searchResponse", responseJson.toString(2));
		return mapping.findForward ("upload.search.response");
  }
  
  /**
	
	expects collection, oldFilename, and newFilename
	*/
	private ActionForward doDeleteAssets (
	                                          UploadForm uploadForm,
	                                          ActionMapping mapping,
	                                          HttpServletRequest request,
	                                          HttpServletResponse response) throws Exception {
		prtln ("doDeleteAssets()");
		String[] ids = request.getParameterValues("id");
		
		JSONObject responseJson = new JSONObject();
		Map requestMap = new HashMap();
		requestMap.put("verb", "delete");
		requestMap.put("id", Arrays.asList(ids));
		responseJson.put ("request", requestMap);
		
		Map results = new HashMap<String,String>();
		JSONArray deleted = new JSONArray();
		JSONArray not_deleted = new JSONArray();
		
		try {
			
			prtln("- ids: ");
			if (ids != null && ids.length > 0) {
				for (int i=0; i<ids.length;i++)
					prtln ("- " + ids[i]);
			}
			else
				throw new Exception ("id parameter is required");
			
			
			for (int i=0; i<ids.length;i++) {
				String[] splits = ids[i].split ("\\|");
				prtln (splits.length + " splits found");
				String collection = splits[0];
				String filename = splits[1];
			
				try {
					
					if (!repositoryService.isAuthorizedSet(collection,
												   this.getSessionUser (request),
												   this.requiredRole)) 
					{
						throw new Exception ("User not permitted to access this collection: " + collection);
					}
					
					AssetFileManager assetFileManager = this.getAssetsBean();
					if (!assetFileManager.deleteAssetFile(filename, collection))
						throw new Exception ("not deleted");
					// results.put (ids[i], "deleted");
					deleted.put (ids[i]);
				} catch (Throwable t) {
					// throw new Exception ("rename ERROR: " + t.getMessage());
					not_deleted.put (ids[i]);
				}
			}
			results.put ("deleted", deleted);
			results.put ("not_deleted", not_deleted);
			responseJson.put ("success", results);
			
		} catch (Exception e) {
			prtlnErr ("doDeleteAssets ERROR: " + e.getMessage());
			responseJson.put ("error", e.getMessage());
		}
		
		
		request.setAttribute ("searchResponse", responseJson.toString(2));
		return mapping.findForward ("upload.search.response");
  }


	
	/**
	* filter the collections specified in the request with the collections
	* the user is permitted to see
	*/
	private String[] getRequestedCollections (HttpServletRequest req) {
		List filtered = new ArrayList<String>();
		String [] collections = req.getParameterValues ("collection");
		List<String> userAssetCollectionKeys = new ArrayList<String>();
		for (SetInfo setInfo : getUserAssetCollections(req)) {
			userAssetCollectionKeys.add (setInfo.getSetSpec());
		}
		
/* 		prtln ("userAssetCollectionKeys");
		for (String id : userAssetCollectionKeys)
			prtln (" - " + id); */
		
		if (collections == null || collections.length == 0)
			filtered = userAssetCollectionKeys;
		else {
			for (int i=0;i<collections.length;i++) {
				if (userAssetCollectionKeys.contains (collections[i]))
					filtered.add (collections[i]);
			}
		}
		return (String[])filtered.toArray(new String[]{});
	}
	
	/**
	Return the list of asset collections this user has permission to see
	*/
	private List<SetInfo> getUserAssetCollections (HttpServletRequest req) {
		// assetCollections are collections user has permission for
		// that also may contain assets
		List<SetInfo> assetCollections = new ArrayList<SetInfo>();
		List<String> assetFormats = new ArrayList<String>();
		assetFormats.add ("adn");
		assetFormats.add ("ncs_item");
		assetFormats.add ("assessments");
		assetFormats.add ("dlese_anno");
		assetFormats.add ("comm_anno");
		/* Which ollections this user can see ....
		*/
		
		
		List<SetInfo> setInfos = repositoryService.getAuthorizedSets (this.getSessionUser (req), Roles.CATALOGER_ROLE);
		// List<SetInfo> setInfos = repositoryService.getAuthorizedSets (this.getSessionUser (req), this.requiredRole);
		prtln (setInfos.size() + " authorized collections - requiredRole: " + Roles.CATALOGER_ROLE);
		for (SetInfo setInfo : setInfos) {
			String xmlFormat = setInfo.getFormat();
			if (assetFormats.contains(xmlFormat))
				assetCollections.add (setInfo);
			// String key = setInfo.getSetSpec();
			// prtln (" - " + setInfo.getName() + " - " + key + " - " + xmlFormat);
			// assetCollections.add (key);
			
		}
		return assetCollections;
	}
		
	private AssetFileManager getAssetsBean () throws Exception {
		ServletContext servletContext = servlet.getServletContext();
		AssetFileManager assetsBean = (AssetFileManager)servletContext.getAttribute("assetsBean");
		if (assetsBean == null) {
			String primaryContentPath = servletContext.getInitParameter("primaryContentPathOnServer");
			// prtln ("primaryContentPath: " + primaryContentPath);
			try {
				assetsBean = new AssetFileManager (primaryContentPath);
			} catch (Exception e) {
				prtlnErr ("WARN: AssetFileManager: " + e.getMessage());
			}
			servletContext.setAttribute("assetsBean", assetsBean);
		}
		return assetsBean;
	}
	
	private AssetSearcher getAssetSearcher () throws Exception {
		ServletContext servletContext = servlet.getServletContext();
		AssetSearcher assetSearcher = (AssetSearcher)servletContext.getAttribute("assetSearcher");
		if (assetSearcher == null) {
			String ddswsBaseUrlForCurricula = servletContext.getInitParameter("ddswsBaseUrlForCurricula");
			// prtln ("ddswsBaseUrlForCurricula: " + ddswsBaseUrlForCurricula);
			try {
				if (ddswsBaseUrlForCurricula == null || ddswsBaseUrlForCurricula.trim().length() == 0)
					throw new Exception ("initParam not found for ddswsBaseUrlForCurricula");
				assetSearcher = new AssetSearcher (ddswsBaseUrlForCurricula);
			} catch (Exception e) {
				prtlnErr ("WARN: AssetSearcher: " + e.getMessage());
			}
			servletContext.setAttribute("assetSearcher", assetSearcher);
		}
		return assetSearcher;
	}		
	
	/**
	 *  Prepare for uploading a file. The file may already exisit, in which case a
	 *  new upload will replace it, or it does not exist, in which case it needs to
	 *  be added new.
	 *
	 * @param  action         The requested action, one of: uploadDisplayExisting,
	 *      uploadNew, uploadReplace
	 * @param  mapping        Mapping
	 * @param  uploadForm     The form that contains the upload data
	 * @param  request        Request
	 * @param  response       Response
	 * @param  currentUser    NOT YET DOCUMENTED
	 * @return                ActionForward
	 * @exception  Exception  If error
	 */
	private ActionForward doPrepareUploadFile(
	                                          String action,
	                                          UploadForm uploadForm,
	                                          ActionMapping mapping,
	                                          HttpServletRequest request,
	                                          HttpServletResponse response) throws Exception {
		prtln ("\ndoPrepareUploadFile()");
		//retrieve the file representation
		FormFile formFile = uploadForm.getTheFile();

		//retrieve the file name
/* 		String fileName = formFile.getFileName();

		if (fileName == null || fileName.trim().length() == 0)
			throw new Exception("File name is empty"); */

		String recordId = request.getParameter("recordId");
		prtln("- requested recordId for doPrepareUploadFile: " + recordId);
		
		String existingFileName = request.getParameter("existingFileName");
		prtln("- existingFileName: " + existingFileName);
		prtln ("-> action: " + action);

		uploadForm.setRecordId(recordId);
		uploadForm.setAction(action);

		boolean isNewUpload = false;
		// Generate a new ID and record, if new:
		if (action.equals("uploadNew")) {
			isNewUpload = true;
/* 			recordId = Long.toString(Utils.getUniqueID());
			uploadForm.setRecordId(recordId);

			prtln("set new recordId to: " + recordId); */

			//Display an 'upload' dialog, if this is new:
			prtln ("- forwarding to upload.input");
			return mapping.findForward("upload.input");
		}
		// Replace an existing uploaded file:
		else if (action.equals("uploadReplace")) {
			// Grab the file name and place it in the bean for display...
			prtln ("- forwarding to upload.input");
			return mapping.findForward("upload.input");
		}
		// Display an existing uploaded file:
		else if (action.equals("uploadDisplayExisting")) {

			//prtln("set recordId to: " + recordId);

			//if (fileName == null || fileName.trim().length() == 0)
			//throw new Exception("File name is empty");

			uploadForm.setRecordId(recordId);
			request.setAttribute("fileName", request.getParameter("existingFileName"));
			request.setAttribute("size", request.getParameter("sizeDisplay"));

			//Display the existing file info, if this is existing:
			prtln ("- forwarding to upload.display");
			return mapping.findForward("upload.display");
		}
		// This should never be reached...
		return mapping.findForward("upload.display");
	}

	/**
	 *  Prepare for uploading a file. The file may already exisit, in which case a
	 *  new upload will replace it, or it does not exist, in which case it needs to
	 *  be added new.
	 *
	 * @param  action         The requested action, one of: uploadDisplayExisting,
	 *      uploadNew, uploadReplace
	 * @param  mapping        Mapping
	 * @param  uploadForm     The form that contains the upload data
	 * @param  request        Request
	 * @param  response       Response
	 * @param  currentUser    NOT YET DOCUMENTED
	 * @return                ActionForward
	 * @exception  Exception  If error
	 */
	private ActionForward doPresentUploadHelper (
	                                          String action,
	                                          UploadForm uploadForm,
	                                          ActionMapping mapping,
	                                          HttpServletRequest request,
	                                          HttpServletResponse response) throws Exception {
		prtln ("\ndoPresentUploadHelper()");
		//retrieve the file representation
		FormFile formFile = uploadForm.getTheFile();

		//retrieve the file name
/* 		String fileName = formFile.getFileName();

		if (fileName == null || fileName.trim().length() == 0)
			throw new Exception("File name is empty"); */

		String recordId = request.getParameter("recordId");
		prtln("- requested recordId for doPrepareUploadFile: " + recordId);
		
		String existingFileName = request.getParameter("existingFileName");
		prtln("- existingFileName: " + existingFileName);
		prtln ("-> action: " + action);

		uploadForm.setRecordId(recordId);
		uploadForm.setAction(action);

		
		/*
		NOTE - THE FOLLOWING ARE NEVER EXECUTED
		*/
		
		
		boolean isNewUpload = false;
		// Generate a new ID and record, if new:
		if (action.equals("uploadNew")) {
			isNewUpload = true;

			//Display an 'upload' dialog, if this is new:
			prtln ("- forwarding to upload.input");
			return mapping.findForward("upload.input");
		}
		// Replace an existing uploaded file:
		else if (action.equals("uploadReplace")) {
			// Grab the file name and place it in the bean for display...
			prtln ("- forwarding to upload.input");
			return mapping.findForward("upload.input");
		}
		// Display an existing uploaded file:
		else if (action.equals("uploadDisplayExisting")) {

			//prtln("set recordId to: " + recordId);

			//if (fileName == null || fileName.trim().length() == 0)
			//throw new Exception("File name is empty");

			uploadForm.setRecordId(recordId);
			request.setAttribute("fileName", request.getParameter("existingFileName"));
			request.setAttribute("size", request.getParameter("sizeDisplay"));

			//Display the existing file info, if this is existing:
			prtln ("- forwarding to upload.display");
			return mapping.findForward("upload.display");
		}
		
		prtln (" ... forwarding to upload.helper");
		return mapping.findForward("upload.helper");
	}
	
	/**
	 *  Save or edit a user's uploaded file and related metadata.
	 *
	 * @param  mapping        Mapping
	 * @param  uploadForm     The form that contains the upload data
	 * @param  request        Request
	 * @param  response       Response
	 * @param  currentUser    NOT YET DOCUMENTED
	 * @return                ActionForward
	 * @exception  Exception  If error
	 */
	private ActionForward doListAssets(
	                                   UploadForm uploadForm,
	                                   ActionMapping mapping,
	                                   HttpServletRequest request,
	                                   HttpServletResponse response) throws Exception {

		// Note: Portions of this code were copied verbatim from the Struts Upload webapp module example (UploadAction.java).
		prtln ("doListAssets()");
		
		SchemEditUtils.showRequestParameters(request);
		
		AssetFileManager assetsBean = getAssetsBean();
		// String [] collections = request.getParameterValues ("collection");
		String [] collections = getRequestedCollections(request);
		
		// NEED to ensure that provided collections are accessible
		
		
		JSONObject responseJson = new JSONObject();
		Map requestMap = new HashMap();
		requestMap.put("verb", "list");
		responseJson.put ("request", requestMap);
		
		
		if (collections == null) {
			prtln (" - listing NO collections!");
			responseJson.put ("results", new JSONObject());
		}
		
		else {
			prtln (" - listing selected collections (" + collections.length + ")");
			responseJson.put ("results", assetsBean.getListJson(collections));
		}
		
		request.setAttribute ("searchResponse", responseJson.toString(2));
		return mapping.findForward ("upload.search.response");
   }
	
   private ActionForward doGetAssetInfo(
	                                   UploadForm uploadForm,
	                                   ActionMapping mapping,
	                                   HttpServletRequest request,
	                                   HttpServletResponse response) throws Exception {
		String filename = request.getParameter ("filename");
		
		prtln ("doGetAssetInfo (" + filename + ")");
		
		JSONObject responseJson = new JSONObject();
		Map requestMap = new HashMap();
		requestMap.put("verb", "assetInfo");
		requestMap.put("filename", filename);
		responseJson.put ("request", requestMap);
		
		// assetSearcher and assetsBean only return results from authorized collections
		responseJson.put ("assetRecords", getAssetSearcher().getAssetRecords(filename));
		responseJson.put ("assetFiles", getAssetsBean().getAssetFilesJson(filename));
			
		request.setAttribute ("searchResponse", responseJson.toString(2));
		return mapping.findForward ("upload.search.response");
   }
   
	/**
		creates json orphansJson and packages into "searchResponse"
		before forwarding to upload.search.response
	*/
   private ActionForward doGetOrphanInfo(
	                                   UploadForm uploadForm,
	                                   ActionMapping mapping,
	                                   HttpServletRequest request,
	                                   HttpServletResponse response) throws Exception {
		
		// prtln ("doGetOrphanInfo()");
		
		JSONObject responseJson = new JSONObject();
		Map requestMap = new HashMap();
		requestMap.put("verb", "orphanInfo");
		responseJson.put ("request", requestMap);
		
		String [] collections = request.getParameterValues ("collection");
		JSONArray orphansJson = new JSONArray();
		for (String orphan : getOrphans(collections)) {
			orphansJson.put(orphan);
		}
		
		responseJson.put ("orphans", orphansJson);

		request.setAttribute ("searchResponse", responseJson.toString(2));
		return mapping.findForward ("upload.search.response");
   }

   

   	/**
	Returns a List of orphans, repsesnted as <collection>/<filename>
	*/
	public List<String> getOrphans (String[] collections) throws Exception {
		// prtln ("getOrphans()");
		List<String> urls = this.getAssetSearcher().getAssetUrls(collections);
		// prtln (" - " + urls.size() + " asset urls found by AssetSearcher");
		
		this.getAssetsBean().updateAssetData();
		Map<String,List<String>> allAssets = this.getAssetsBean().listAll(collections);
		
		/*
			we want to know: which of the assets are not cataloged: 
				I.e., which collection/filename does not match a url
		*/
		
		Set keys = allAssets.keySet();
		// prtln (keys.size() + " collections");
		List<String> orphans = new ArrayList<String>();
		for (String collection : allAssets.keySet()) {
			// prtln (" - " + collection + " (" + ((List)allAssets.get(collection)).size() + ")");
			
			for (String filename : allAssets.get(collection)) {
				String assetUrl = "http://ccs.dls.ucar.edu/home/protected/" 
										+ collection + "/" + filename;
				if (!urls.contains(assetUrl)) {
					orphans.add (collection + "/" + filename);
					// prtln (" --- " + collection + "/" + filename);
				}
			}
		}
		prtln (" - returning " + orphans.size() + " orphans");
		return orphans;
	}
	
	/**
	Returns a JSONArray of orphans, repsesnted as <collection>/<filename>
	*/
	public JSONArray getOrphansJson (String[] collections) throws Exception {
		List<String> orphans = getOrphans(collections);
		JSONArray orphansJson = new JSONArray();
		for (String orphan : orphans) {
			orphansJson.put(orphan);
		}
		return orphansJson;
	}
	
   
   
   
	/**
	 *  Upload file, store it in protectedDir, and return json response:
	 
	 * jsonResponse: {
	 	"success": {
	 		"fileName": "ES1_engageStrategies.pdf",
	 		"contentType": "application/x-download",
	 		"size": "30KB"
		  }
	  }
	 *
	 * @param  mapping        Mapping
	 * @param  uploadForm     The form that contains the upload data
	 * @param  request        Request
	 * @param  response       Response
	 * @param  currentUser    NOT YET DOCUMENTED
	 * @return                ActionForward
	 * @exception  Exception  If error
	 */
	private ActionForward doUploadFile(
	                                   UploadForm uploadForm,
	                                   ActionMapping mapping,
	                                   HttpServletRequest request,
	                                   HttpServletResponse response) throws Exception {

		// Note: Portions of this code were copied verbatim from the Struts Upload webapp module example (UploadAction.java).
		prtln ("doUploadFile()");
		ServletContext servletContext = servlet.getServletContext();

		//this line is here for when the input page is contentType header and meta tag is set to UTF-8,
		//it sets the correct character encoding for the response
		String encoding = request.getCharacterEncoding();
		if ((encoding != null) && (encoding.equalsIgnoreCase("utf-8"))) {
			response.setContentType("text/html; charset=utf-8");
		}

		//retrieve the file representation
		FormFile formFile = uploadForm.getTheFile();

		//retrieve the file name
		String fileName = formFile.getFileName();

		if (fileName == null || fileName.trim().length() == 0)
			throw new Exception("File name is empty");

/* 		String recordId = uploadForm.getRecordId();
		prtln("initial recordId: " + recordId);

		if (recordId.equals("new"))
			throw new Exception("Improper recordId: 'new'"); */

		//retrieve the content type
		String contentType = formFile.getContentType();

		//retrieve the file size
		String sizeDisplay = uploadForm.getFileSizeDisplay();

		int sizeBytes = formFile.getFileSize();
		String collection = request.getParameter("collection");

		// Check that user is authorized for this collection!!
		
		try {
			if (collection == null || collection.trim().length() == 0)
				throw new Exception ("collection is required");
			// Save the binary content:
			saveBinaryContent(formFile.getInputStream(), fileName, collection);

		} catch (Throwable t) {
			t.printStackTrace();
			throw new Exception("Unable to upload file: " + t);
		} finally {
			if (formFile != null)
				formFile.destroy();
		}
		try {
			this.getAssetsBean().updateFileInfo(fileName, collection);
		} catch (Exception e) {
			prtlnErr ("ERROR: could not updateFileInfo: " + e.getMessage());
		}
		try {
			this.getAssetSearcher().clearCacheEntries(collection);
		} catch (Exception e) {
			prtlnErr ("ERROR: could not clearCacheEntries: " + e.getMessage());
		}		
		//place the data into the request for retrieval from display.jsp
		// request.setAttribute("recordId", recordId);
		request.setAttribute("fileName", fileName);
		request.setAttribute("contentType", contentType);
		request.setAttribute("size", sizeDisplay);

		//destroy the temporary file created
		formFile.destroy();

		/* we don't need to send fileName, and size back anymore?
		   now we create a json response and forward to "upload.search.response"
		   which should be RENAMED
		*/

		// Create JSON response		
		JSONObject responseJson = new JSONObject();
		Map respMap = new HashMap();
		respMap.put("fileName", fileName);
		respMap.put("contentType", contentType);
		respMap.put("size", sizeDisplay);
		responseJson.put ("success", respMap);

		
		// OLD - return a forward to display.jsp
		// return mapping.findForward("upload.display");
		request.setAttribute ("searchResponse", responseJson.toString(2));
		return mapping.findForward("upload.search.response");
	}

	private File getSavedFileDir (String collection) throws Exception {
		//return new File("/Users/ostwald/tmp/UPLOADED");
		File uploadDir = null;
		String primaryContentPath = servlet.getServletContext().getInitParameter("primaryContentPathOnServer");
		if (primaryContentPath == null) 
			throw new Exception ("primaryContentPathOnServer initParameter not configured");
		else {
			File primaryContentDir = new File (primaryContentPath);
			if (!primaryContentDir.exists())
				throw new Exception ("primaryContentDir doe not exist");
			// uploadDir = new File (primaryContentDir, "UPLOADED");
			uploadDir = new File (primaryContentDir, collection);
			if (!uploadDir.exists() && !uploadDir.mkdir())
				throw new Exception ("Could not create upload directory at " + uploadDir);
		}
		return uploadDir;

			
	}

	/* Save the binary content to persistent store */
	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  stream                 NOT YET DOCUMENTED
	 * @param  userADNMetadataRecord  NOT YET DOCUMENTED
	 * @exception  Exception          NOT YET DOCUMENTED
	 */
	private void saveBinaryContent(InputStream stream, String fileName, String collection) throws Exception {
		prtln ("saveBinaryContent() filename: " + fileName);
		ServletContext servletContext = servlet.getServletContext();

		// File savedFileDir = new File(GeneralServletTools.getAbsolutePath(servletContext.getInitParameter("binaryContentFileStore"), servletContext));

		File savedFileDir = getSavedFileDir(collection);
		if (savedFileDir == null || !savedFileDir.exists())
			throw new Exception ("savedFileDir not found at " + savedFileDir);
		// savedFileDir.mkdirs();

		File fileToWrite = new File(savedFileDir, fileName);

		//retrieve the file data
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		//write the file to the file specified
		OutputStream bos = new FileOutputStream(fileToWrite);
		int bytesRead = 0;
		byte[] buffer = new byte[8192];
		while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
			bos.write(buffer, 0, bytesRead);
		}
		bos.close();

		//close the stream
		stream.close();
		
		prtln ("wrote binary content to " + fileToWrite);
	}


	/* Delete the binary content from persistent store */
	/**
	 *  NOT YET DOCUMENTED
	 *
	 * getSavedFileDir
	 * @param  userADNMetadataRecord  NOT YET DOCUMENTED
	 * @exception  Exception          NOT YET DOCUMENTED
	 */
	private void deleteBinaryContent(String fileName, String collection) throws Exception {
		prtln ("deleteBinaryContent(): " + fileName);
		ServletContext servletContext = servlet.getServletContext();
		// File savedFileDir = new File(GeneralServletTools.getAbsolutePath(servletContext.getInitParameter("binaryContentFileStore"), servletContext));
		File savedFileDir = getSavedFileDir(collection);
		File fileToWrite = new File(savedFileDir, fileName);
		fileToWrite.delete();
	}

	// Output for debugging...
	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  req  NOT YET DOCUMENTED
	 */
	private void printParams(HttpServletRequest req) {
		Enumeration params = req.getParameterNames();

		// Set the appropriate forwarding:
		//String adminEmail = req.getParameter("adminEmail");
		String paramName = null;
		String[] paramValues = null;
		String formData = "";
		while (params.hasMoreElements()) {
			paramName = (String) params.nextElement();
			paramValues = req.getParameterValues(paramName);

			for (int i = 0; i < paramValues.length; i++)
				formData += paramName + ":" + paramValues[i] + " ";

			if (paramName.startsWith("edit") || paramName.startsWith("add")) {
				if (paramValues[0].startsWith("Edit") || paramValues[0].startsWith("Add")) {
					//prtln("case 1");
					//return mapping.findForward("edit.repository.settings");
				}
			}
		}

		prtln("params were: " + formData);
	}

	// ---------------------- Debug info --------------------

	/**
	 *  Return a string for the current time and date, sutiable for display in log
	 *  files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	protected final static String getDateStamp() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final void prtlnErr(String s) {
		System.err.println(getDateStamp() + " UploadAction Error: " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final void prtln(String s) {
		if (debug) {
			// System.out.println(getDateStamp() + " UploadAction: " + s);
			// System.out.println("UploadAction: " + s);
			System.out.println(s);
		}
	}


	/**
	 *  Sets the debug attribute of the object
	 *
	 * @param  db  The new debug value
	 */
	public static void setDebug(boolean db) {
		debug = db;
	}
}

