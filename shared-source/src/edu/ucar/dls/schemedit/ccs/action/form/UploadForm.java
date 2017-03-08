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
package edu.ucar.dls.schemedit.ccs.action.form;

// Portions of this code were copied verbatim from the Struts Upload example webapp module...

import javax.servlet.http.HttpServletRequest;
import java.text.*;
import java.util.*;

import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;

/**
 *  This class is a placeholder for form values. In a multipart request, files are represented by set and get
 *  methods that use the class org.apache.struts.upload.FormFile, an interface with basic methods to retrieve
 *  file information. The actual structure of the FormFile is dependant on the underlying impelementation of
 *  multipart request handling. The default implementation that struts uses is
 *  org.apache.struts.upload.CommonsMultipartRequestHandler.
 *
 * @author    John Weatherley
 */
public class UploadForm extends ActionForm {

	/**  The value of the text the user has sent as form data */
	protected String _recordId = null;
	
	protected String _action = null;	
	protected String _collection = null;
	protected String _existingFileName = null;
	protected List _userAssetCollections = null;
	protected String _existingAssetPath = null;


	/**  The file that the user has uploaded */
	protected FormFile _formFile = null;


	/**
	 *  Retrieve the value of the recordId
	 *
	 * @return    The recordId value
	 */
	public String getRecordId() {
		return _recordId;
	}


	/**
	 *  Set the value of the recordId
	 *
	 * @param  id  The new recordId value
	 */
	public void setRecordId(String recordId) {
		_recordId = recordId;
	}

	
	/**
	 * Returns the value of action.
	 */
	public String getAction()
	{
		// System.out.println(this.toString() + " getAction(): " + _action);
		return _action;
	}

	/**
	 * Sets the value of action.
	 * @param action The value to assign action.
	 */
	public void setAction(String action)
	{
		_action = action;
	}
	
	public String getCollection()
	{
		// System.out.println(this.toString() + " getCollection(): " + _collection);
		return _collection;
	}

	/**
	 * Sets the value of collection.
	 * @param collection The value to assign collection.
	 */
	public void setCollection(String collection)
	{
		_collection = collection;
	}

	public List getUserAssetCollections()
	{
		// System.out.println(this.toString() + " getUserAssetCollections(): " + _userAssetCollections);
		return _userAssetCollections;
	}

	/**
	 * Sets the value of userUserAssetCollections.
	 * @param collections The value to assign collections.
	 */
	public void setUserAssetCollections(List collections)
	{
		_userAssetCollections = collections;
	}	
	
	
	public String getExistingFileName()
	{
		// System.out.println(this.toString() + " getExistingFileName(): " + _existingFileName);
		return _existingFileName;
	}

	/**
	 * Sets the value of existingFileName.
	 * @param existingFileName The value to assign existingFileName.
	 */
	public void setExistingFileName(String existingFileName)
	{
		_existingFileName = existingFileName;
	}
	
	public String getExistingAssetPath()
	{
		// System.out.println(this.toString() + " getExistingAssetPath(): " + _existingAssetPath);
		return _existingAssetPath;
	}

	/**
	 * Sets the value of existingAssetPath.
	 * @param existingAssetPath The value to assign existingAssetPath.
	 */
	public void setExistingAssetPath(String existingAssetPath)
	{
		_existingAssetPath = existingAssetPath;
	}
	
	/**
	 *  Retrieve a representation of the file the user has uploaded
	 *
	 * @return    The formFile value
	 */
	public FormFile getTheFile() {
		return _formFile;
	}


	/**
	 *  Set a representation of the file the user has uploaded
	 *
	 * @param  formFile  The new theFile value
	 */
	public void setTheFile(FormFile formFile) {
		_formFile = formFile;
	}


	/**
	 *  Gets the fileSize as a String suitable for display to users.
	 *
	 * @return    The fileSizeDisplay value
	 */
	public String getFileSizeDisplay() {
		FormFile formFile = getTheFile();
		if (formFile != null)
			return getFileSizeDisplay(formFile.getFileSize());
		else
			return getFileSizeDisplay(-1);
	}


	/**
	 *  Gets the fileSize as a String suitable for display to users.
	 *
	 * @param  numBytes  The number of bytes or -1 if not available
	 * @return           The fileSizeDisplay value
	 */
	public static String getFileSizeDisplay(int numBytes) {
		if (numBytes < 0)
			return "n/a";
		String size = numBytes + " bytes";
		if (numBytes > 1048576) {
			NumberFormat nf = new DecimalFormat("###,###,###.#");
			size = nf.format((numBytes / 1048576f)) + "M";
		}
		else if (numBytes > 1024)
			size = (numBytes / 1024) + "KB";
		return size;
	}


	/**
	 *  Check to make sure the client hasn't exceeded the maximum allowed upload size inside of this validate
	 *  method.
	 *
	 * @param  mapping  ActionMapping
	 * @param  request  HttpServletRequest
	 * @return          ActionErrors
	 */
	public ActionErrors validate(
	                             ActionMapping mapping,
	                             HttpServletRequest request) {

		ActionErrors errors = null;
		//has the maximum length been exceeded?
		Boolean maxLengthExceeded =
			(Boolean) request.getAttribute(
			MultipartRequestHandler.ATTRIBUTE_MAX_LENGTH_EXCEEDED);
		
				
		if ((maxLengthExceeded != null) && (maxLengthExceeded.booleanValue())) {
			errors = new ActionErrors();
			errors.add(
				ActionMessages.GLOBAL_MESSAGE,
				new ActionMessage("maxLengthExceeded"));
		}
		
		return errors;
	}
}

