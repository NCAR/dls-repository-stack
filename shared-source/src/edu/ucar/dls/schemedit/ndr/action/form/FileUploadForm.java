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

package edu.ucar.dls.schemedit.ndr.action.form;

import edu.ucar.dls.schemedit.SchemEditUtils;

import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.CommonsMultipartRequestHandler;
import org.apache.struts.action.ActionForm;

public class FileUploadForm extends ActionForm {

	private static boolean debug = true;
	
	public FileUploadForm () {
		setMultipartRequestHandler(new CommonsMultipartRequestHandler());
	}
	
	private FormFile myFile;
	public void setMyFile(FormFile myFile) {
		  this.myFile = myFile;
	}

	public FormFile getMyFile() {
		  return myFile;
	}
	
	private String contentURL;
	
	public void setContentURL (String handle) {
		this.contentURL = handle;
	}
	
	public String getContentURL () {
		return this.contentURL;
	}
	
	private String forwardPath;
	
	public void setForwardPath (String handle) {
		this.forwardPath = handle;
	}
	
	public String getForwardPath () {
		return this.forwardPath;
	}
	
	private String error;
	
	public void setError (String error) {
		this.error = error;
	}
	
	public String getError () {
		return this.error;
	}
	
	public boolean getHasError () {
		return this.error == null || this.error.trim().length() == 0;
	}
	
	private void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "");
		}
	}

}
	  

