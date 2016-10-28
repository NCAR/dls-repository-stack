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

package edu.ucar.dls.services.dcs.action.form;
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
 *  An ActionForm bean that holds data for DCS web services.
 *

 * @see        edu.ucar.dls.services.dcs.action.DCSServicesAction
 */
public class DCSServicesForm extends VocabForm implements Serializable {
	
	public static final String RESULT_CODE_SUCCESS = "success";
	public static final String RESULT_CODE_NO_SUCH_RECORD = "recordDoesNotExist";
	public static final String RESULT_CODE_NO_SUCH_COLLECTION = "collectionDoesNotExist";
	
	public static final String ERROR_CODE_BADARGUMENT = "badArgument";
	public static final String ERROR_CODE_BADVERB = "badVerb";
	public static final String ERROR_CODE_NOTAUTHORIZED = "notAuthorized";
	public static final String ERROR_CODE_INTERNALSERVERERROR = "internalServerError";
	public static final String ERROR_CODE_SERVICE_DISABLED = "serviceDisabled";
	public static final String ERROR_CODE_ILLEGAL_OPERATION = "illegalOperation";
	
	private static boolean debug = true;
	private String errorMsg = null;
	private ResultDocList results = null;
	private String authorizedFor = null;
	private String recordXml = null;
	private String recordFormat = null;
	private int s = 0, n = 10;
	private List xmlFormats = null;
	private String vocabField = null;
	private String requestElementLabel = null;
	private String id = null;
	private String collection = null;
	private String xmlFormat = null;
	private List errorList = null;
	private String[] statuses = null;
	private List statusLabels = null;
	private String exportDir = null;
	private String[] collections = null;
	private String url = null;
	private String statusEntry = null;
	private String responseDate = null;
	private String resultCode = null;
	// Bean properties:

	/**  Constructor for the RepositoryForm object */
	public DCSServicesForm() { }


	/**
	 *  Gets the id attribute of the DCSServicesForm object
	 *
	 * @return    The id value
	 */
	public String getId() {
		return id;
	}


	/**
	 *  Sets the id attribute of the DCSServicesForm object
	 *
	 * @param  id  The new id value
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getUrl () {
		return this.url;
	}
	
	public void setUrl (String url) {
		this.url = url;
	}
	
	public String [] getCollections () {
		return this.collections;
	}
	
	public void setCollections (String [] collections) {
		this.collections = collections;
	}

	/**
	 *  Gets the collection attribute of the DCSServicesForm object
	 *
	 * @return    The collection value
	 */
	public String getCollection() {
		return collection;
	}


	/**
	 *  Sets the collection attribute of the DCSServicesForm object
	 *
	 * @param  collection  The new collection value
	 */
	public void setCollection(String collection) {
		this.collection = collection;
	}


	/**
	 *  Gets the xmlFormat attribute of the DCSServicesForm object
	 *
	 * @return    The xmlFormat value
	 */
	public String getXmlFormat() {
		return xmlFormat;
	}


	/**
	 *  Sets the xmlFormat attribute of the DCSServicesForm object
	 *
	 * @param  xmlFormat  The new xmlFormat value
	 */
	public void setXmlFormat(String xmlFormat) {
		this.xmlFormat = xmlFormat;
	}

	/**
	 *  Gets the response date String.
	 *
	 * @return    The response date String
	 */
	public String getResponseDate() {
		if(responseDate == null)
			responseDate = OAIUtils.getDatestampFromDate(new Date());
		return responseDate;
	}
	
	/**
	 *  Gets the statuses attribute of the DCSServicesForm object
	 *
	 * @return    The statuses value
	 */
	public String[] getStatuses() {
		return statuses;
	}


	/**
	 *  Sets the statuses attribute of the DCSServicesForm object
	 *
	 * @param  statusValues  The new statuses value
	 */
	public void setStatuses(String[] statusValues) {
		statuses = statusValues;
	}


	/**
	 *  Gets the statusLabels attribute of the DCSServicesForm object
	 *
	 * @return    The statusLabels value
	 */
	public List getStatusLabels() {
		return statusLabels;
	}


	/**
	 *  Sets the statusLabels attribute of the DCSServicesForm object
	 *
	 * @param  labels  The new statusLabels value
	 */
	public void setStatusLabels(List labels) {
		statusLabels = labels;
	}

	public String getStatusEntry () {
		return this.statusEntry;
	}
	
	public void setStatusEntry (String entry) {
		this.statusEntry = entry;
	}

	/**
	 *  Gets the exportDir attribute of the DCSServicesForm object
	 *
	 * @return    The exportDir value
	 */
	public String getExportDir() {
		return exportDir;
	}


	/**
	 *  Sets the exportDir attribute of the DCSServicesForm object
	 *
	 * @param  dir  The new exportDir value
	 */
	public void setExportDir(String dir) {
		exportDir = dir;
	}


	/**
	 *  Sets the errorList attribute of the DCSServicesForm object
	 *
	 * @param  errorList  The new errorList value
	 */
	public void setErrorList(List errorList) {
		this.errorList = errorList;
	}


	/**
	 *  Gets the errorList attribute of the DCSServicesForm object
	 *
	 * @return    The errorList value
	 */
	public List getErrorList() {
		return errorList;
	}


	/**
	 *  Gets the xmlFormats attribute of the DCSServicesForm object
	 *
	 * @return    The xmlFormats value
	 */
	public List getXmlFormats() {
		return xmlFormats;
	}


	/**
	 *  Sets the xmlFormats attribute of the DCSServicesForm object
	 *
	 * @param  var  The new xmlFormats value
	 */
	public void setXmlFormats(List var) {
		xmlFormats = var;
		if (xmlFormats != null)
			Collections.sort(xmlFormats);
	}


	/**
	 *  Gets the localizedRecordXml attribute of the DCSServicesForm object
	 *
	 * @return    The localizedRecordXml value
	 */
	public String getLocalizedRecordXml() {
		String xml = recordXml.replaceAll("xmlns.*=\".*\"|xsi:schemaLocation.*=\".*\"", "");
		if (recordFormat == null)
			return xml;
		else if (recordFormat.equals("oai_dc"))
			return xml.replaceAll("oai_dc:dc", "oai_dc").replaceAll("<dc:", "<").replaceAll("</dc:", "</");
		else if (recordFormat.equals("nsdl_dc"))
			return xml.replaceAll("nsdl_dc:nsdl_dc", "ndsl_dc").replaceAll("<dc:", "<").replaceAll("<dct:", "<").replaceAll("</dc:", "</").replaceAll("</dct:", "</");
		return xml;
	}


	/**
	 *  Gets the recordXml attribute of the DCSServicesForm object
	 *
	 * @return    The recordXml value
	 */
	public String getRecordXml() {
		return recordXml;
	}


	/**
	 *  Sets the recordXml attribute of the DCSServicesForm object
	 *
	 * @param  val  The new recordXml value
	 */
	public void setRecordXml(String val) {
		recordXml = val;
	}


	/**
	 *  Sets the recordFormat attribute of the DCSServicesForm object
	 *
	 * @param  val  The new recordFormat value
	 */
	public void setRecordFormat(String val) {
		recordFormat = val;
	}


	/**
	 *  Gets the recordFormat attribute of the DCSServicesForm object
	 *
	 * @return    The recordFormat value
	 */
	public String getRecordFormat() {
		return recordFormat;
	}


	/**
	 *  Gets the s attribute of the DCSServicesForm object
	 *
	 * @return    The s value
	 */
	public int getS() {
		return s;
	}


	/**
	 *  Sets the s attribute of the DCSServicesForm object
	 *
	 * @param  val  The new s value
	 */
	public void setS(int val) {
		s = val;
	}


	/**
	 *  Gets the n attribute of the DCSServicesForm object
	 *
	 * @return    The n value
	 */
	public int getN() {
		return n;
	}


	/**
	 *  Sets the n attribute of the DCSServicesForm object
	 *
	 * @param  val  The new n value
	 */
	public void setN(int val) {
		n = val;
	}


	/**
	 *  Gets the role name for which this user is authorized
	 *
	 * @return    The authorizedFor value
	 */
	public String getAuthorizedFor() {
		return authorizedFor;
	}


	/**
	 *  Sets the role name for which this user is authorized
	 *
	 * @param  val  The new authorizedFor value
	 */
	public void setAuthorizedFor(String val) {
		authorizedFor = val;
	}


	/**
	 *  Sets the errorMsg attribute of the DCSServicesForm object
	 *
	 * @param  errorMsg  The new errorMsg value
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}


	/**
	 *  Gets the errorMsg attribute of the DCSServicesForm object
	 *
	 * @return    The errorMsg value
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

		/**
	 * Returns the value of resultCode.
	 */
	public String getResultCode()
	{
		return resultCode;
	}

	/**
	 * Sets the value of resultCode.
	 * @param resultCode The value to assign resultCode.
	 */
	public void setResultCode(String resultCode)
	{
		this.resultCode = resultCode;
	}	


	/**
	 *  Gets the results attribute of the DCSServicesForm object
	 *
	 * @return    The results value
	 */
	public ResultDocList getResults() {
		return results;
	}


	/**
	 *  Sets the results attribute of the DCSServicesForm object
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
	 *  A list of UTC dates in the past in the following order: one minute, one
	 *  hour, one day, one week, one month, one year.
	 *
	 * @return    A list of UTC dates in the past.
	 */
	public List getUtcDates() {
		long curTime = System.currentTimeMillis();
		long min = 1000 * 60;
		long hour = min * 60;
		long day = hour * 24;
		long week = day * 7;
		long month = day * 30;
		long year = day * 365;

		List dates = new ArrayList();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'z'");
		df.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
		Date date = new Date(curTime - min);
		dates.add(new DateLabelPair(df.format(date), "one minute ago"));
		date.setTime(curTime - hour);
		dates.add(new DateLabelPair(df.format(date), "one hour ago"));
		date.setTime(curTime - day);
		dates.add(new DateLabelPair(df.format(date), "one day ago"));
		date.setTime(curTime - week);
		dates.add(new DateLabelPair(df.format(date), "one week ago"));
		date.setTime(curTime - month);
		dates.add(new DateLabelPair(df.format(date), "one month ago"));
		date.setTime(curTime - year);
		dates.add(new DateLabelPair(df.format(date), "one year ago"));
		return dates;
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @author     Jonathan Ostwald
	 * @version    $Id: DCSServicesForm.java,v 1.7 2010/10/18 22:23:21 ostwald Exp $
	 */
	public class DateLabelPair {
		private String date, label;


		/**
		 *  Constructor for the DateLabelPair object
		 *
		 * @param  date   NOT YET DOCUMENTED
		 * @param  label  NOT YET DOCUMENTED
		 */
		public DateLabelPair(String date, String label) {
			this.date = date;
			this.label = label;
		}


		/**
		 *  Gets the date attribute of the DateLabelPair object
		 *
		 * @return    The date value
		 */
		public String getDate() {
			return date;
		}


		/**
		 *  Gets the label attribute of the DateLabelPair object
		 *
		 * @return    The label value
		 */
		public String getLabel() {
			return label;
		}
	}

	//================================================================

	/**
	 *  Return a string for the current time and date, sutiable for display in log
	 *  files and output to standout:
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
	 *  Output a line of text to standard out, with datestamp, if debug is set to
	 *  true.
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


