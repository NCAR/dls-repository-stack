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

package edu.ucar.dls.schemedit.action.form;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.xml.schema.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.serviceclients.remotesearch.RemoteResultDoc;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.LabelValueBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.io.*;
import java.text.*;
import java.net.*;
import java.util.regex.*;

import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;

/**
 *  ActionForm bean for handling requests to support Schemaedit. Most methods
 *  acesss the {@link DocMap} attribute, which wraps the XML Document that is
 *  being edited.
 *
 *@author    ostwald
 */
public class FrameworkAdminForm extends ActionForm {

	private boolean debug = true;

	private Map frameworks = null;
	private List unloadedFrameworks = null;
	private List uninitializedFrameworks = null;

	/**
	 *  Description of the Field
	 */
	// protected FormFile sampleFile;

	public Map getFrameworks () {
		return frameworks;
	}
	
	public void setFrameworks (Map map) {
		frameworks = map;
	}
	
	public void setUnloadedFrameworks (List formats) {
		this.unloadedFrameworks = formats;
	}
	
	public List getUnloadedFrameworks () {
		return this.unloadedFrameworks;
	}
	
	public void setUninitializedFrameworks (List formats) {
		this.uninitializedFrameworks = formats;
	}
	
	public List getUninitializedFrameworks () {
		return this.uninitializedFrameworks;
	}	

	/**
	 *  Description of the Method
	 *
	 *@param  s  Description of the Parameter
	 */
	private void prtln(String s) {
		if (debug) {
			System.out.println("SchemaEditAdminForm: " + s);
		}
	}
}

