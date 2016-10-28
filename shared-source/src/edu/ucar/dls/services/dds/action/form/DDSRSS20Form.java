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

package edu.ucar.dls.services.dds.action.form;

import edu.ucar.dls.propertiesmgr.*;
import edu.ucar.dls.webapps.tools.*;
import edu.ucar.dls.index.*;
import edu.ucar.dls.index.reader.*;
import edu.ucar.dls.oai.*;
import edu.ucar.dls.repository.*;
import edu.ucar.dls.dds.action.form.VocabForm;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import java.util.*;
import java.io.*;
import java.text.*;
import java.net.URLEncoder;

/**
 *  A ActionForm bean that holds data for DDS RSS requests
 *
 * @see edu.ucar.dls.services.dds.action.DDSRSS20Action
 */
public class DDSRSS20Form extends ActionForm implements Serializable {

	private static boolean debug = true;
	private ResultDocList results = null;
	private String errMsg = null;

	// Bean properties:

	/**  Constructor for the DDSRSS20Form object */
	public DDSRSS20Form() { }



	/**
	 *  Gets the results attribute of the DDSRSS20Form object
	 *
	 * @return    The results value
	 */
	public ResultDocList getResults() {
		return results;
	}


	/**
	 *  Sets the results attribute of the DDSRSS20Form object
	 *
	 * @param  results  The new results value
	 */
	public void setResults(ResultDocList results) {
		this.results = results;
	}


	/**
	 *  Gets the number of matching results.
	 *
	 * @return    The numResults value
	 */
	public int getNumResults() {
		if (results == null)
			return 0;
		return results.size();
	}


	/**
	 *  Gets the error message or null if there was no error
	 *
	 * @return    The errorMsg value
	 */
	public String getErrorMsg() {
		return errMsg;
	}


	/**
	 *  Sets the error message
	 *
	 * @param  message  The new errorMsg value
	 */
	public void setErrorMsg(String message) {
		errMsg = message;
	}


	//================================================================


	/**
	 *  Return a string for the current time and date, sutiable for display in log files and
	 *  output to standout:
	 *
	 * @return    The dateStamp value
	 */
	protected final static String getDs() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	protected final void prtlnErr(String s) {
		System.err.println(getDs() + " " + s);
	}



	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	protected final void prtln(String s) {
		if (debug)
			System.out.println(getDs() + " " + s);
	}


	/**
	 *  Sets the debug attribute
	 *
	 * @param  isDebugOuput  The new debug value
	 */
	public static void setDebug(boolean isDebugOuput) {
		debug = isDebugOuput;
	}
}


