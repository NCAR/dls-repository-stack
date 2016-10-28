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
 *  A ActionForm bean that holds data for DDS web services and has access to vocab info.
 *
 * @author    John Weatherley
 * @see       edu.ucar.dls.services.dds.action.DDSServicesAction
 */
public class DDSServicesForm extends VocabForm implements Serializable {

	/**  DESCRIPTION */
	public final static String ERROR_CODE_NORECORDSMATCH = "noRecordsMatch";
	/**  DESCRIPTION */
	public final static String ERROR_CODE_CANNOTDISSEMINATEFORMAT = "cannotDisseminateFormat";
	/**  DESCRIPTION */
	public final static String ERROR_CODE_IDDOESNOTEXIST = "idDoesNotExist";
	/**  DESCRIPTION */
	public final static String ERROR_CODE_BADARGUMENT = "badArgument";
	/**  DESCRIPTION */
	public final static String ERROR_CODE_BADVERB = "badVerb";
	/**  DESCRIPTION */
	public final static String ERROR_CODE_BADQUERY = "badQuery";
	/**  DESCRIPTION */
	public final static String ERROR_CODE_NOTAUTHORIZED = "notAuthorized";
	/**  DESCRIPTION */
	public final static String ERROR_CODE_INTERNALSERVERERROR = "internalServerError";

	private static boolean debug = true;
	private String errorMsg = null;
	private String errorCode = null;
	private ResultDocList resultDocList = null;
	private String authorizedFor = null;
	private String recordXml = null;
	private String recordFormat = null;
	private int s = 0, n = 10;
	private List xmlFormats = null;
	private String vocabField = null;
	private String requestElementLabel = null;
	private Map facetMap = null;
	private Map facetCategoryDelimiterMap = null;
	private int globalMaxFacetResults = 10;
	private int globalMaxFacetDepth = 10;
	private int globalMaxFacetLabels = 1000;



	// Bean properties:

	/**  Constructor for the RepositoryForm object */
	public DDSServicesForm() { }


	/**
	 *  Used for facet taxonomies and hierarchies, gets a List of FacetResult Objects, or null if none.
	 *
	 * @return    The facetResults value
	 */
	public List getFacetResults() {
		if (resultDocList == null)
			return null;
		return resultDocList.getFacetResults();
	}


	/**
	 *  Used for standard field faceting, keys are index field names, values are "termMaps", which map the terms
	 *  for that field to the number of records containing that term (in that field).
	 *
	 * @return    The facetMap value
	 */
	public Map getFacetMap() {
		return this.facetMap;
	}


	/**
	 *  Sets the facetMap attribute of the DDSServicesForm object
	 *
	 * @param  map  The new facetMap value
	 */
	public void setFacetMap(Map map) {
		this.facetMap = map;
	}


	/**
	 *  Returns the value of facetCategoryDelimiterMap.
	 *
	 * @return    The facetCategoryDelimiterMap value
	 */
	public Map getFacetCategoryDelimiterMap() {
		return facetCategoryDelimiterMap;
	}


	/**
	 *  Sets the value of facetCategoryDelimiterMap.
	 *
	 * @param  facetCategoryDelimiterMap  The value to assign facetCategoryDelimiterMap.
	 */
	public void setFacetCategoryDelimiterMap(Map facetCategoryDelimiterMap) {
		this.facetCategoryDelimiterMap = facetCategoryDelimiterMap;
	}


	/**
	 *  Gets the facetCategoryDelimiter attribute of the DDSServicesForm object
	 *
	 * @param  category  The category
	 * @return           The facetCategoryDelimiter value
	 */
	public String getFacetCategoryDelimiter(String category) {
		return (String) facetCategoryDelimiterMap.get(category);
	}


	/**
	 *  Returns the value of globalMaxFacetResults.
	 *
	 * @return    The globalMaxFacetResults value
	 */
	public int getGlobalMaxFacetResults() {
		return globalMaxFacetResults;
	}


	/**
	 *  Sets the value of globalMaxFacetResults.
	 *
	 * @param  globalMaxFacetResults  The value to assign globalMaxFacetResults.
	 */
	public void setGlobalMaxFacetResults(int globalMaxFacetResults) {
		this.globalMaxFacetResults = globalMaxFacetResults;
	}


	/**
	 *  Returns the value of globalMaxFacetDepth.
	 *
	 * @return    The globalMaxFacetDepth value
	 */
	public int getGlobalMaxFacetDepth() {
		return globalMaxFacetDepth;
	}


	/**
	 *  Sets the value of globalMaxFacetDepth.
	 *
	 * @param  globalMaxFacetDepth  The value to assign globalMaxFacetDepth.
	 */
	public void setGlobalMaxFacetDepth(int globalMaxFacetDepth) {
		this.globalMaxFacetDepth = globalMaxFacetDepth;
	}


	/**
	 *  Returns the value of globalMaxFacetLabels.
	 *
	 * @return    The globalMaxFacetLabels value
	 */
	public int getGlobalMaxFacetLabels() {
		return globalMaxFacetLabels;
	}


	/**
	 *  Sets the value of globalMaxFacetLabels.
	 *
	 * @param  globalMaxFacetLabels  The value to assign globalMaxFacetLabels.
	 */
	public void setGlobalMaxFacetLabels(int globalMaxFacetLabels) {
		this.globalMaxFacetLabels = globalMaxFacetLabels;
	}


	/**
	 *  Gets the xmlFormats attribute of the DDSServicesForm object
	 *
	 * @return    The xmlFormats value
	 */
	public List getXmlFormats() {
		return xmlFormats;
	}


	/**
	 *  Sets the xmlFormats attribute of the DDSServicesForm object
	 *
	 * @param  var  The new xmlFormats value
	 */
	public void setXmlFormats(List var) {
		xmlFormats = var;
		if (xmlFormats != null)
			Collections.sort(xmlFormats);
	}


	/**
	 *  Gets the localizedRecordXml attribute of the DDSServicesForm object
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
	 *  Gets the recordXml attribute of the DDSServicesForm object
	 *
	 * @return    The recordXml value
	 */
	public String getRecordXml() {
		return recordXml;
	}


	/**
	 *  Sets the recordXml attribute of the DDSServicesForm object
	 *
	 * @param  val  The new recordXml value
	 */
	public void setRecordXml(String val) {
		recordXml = val;
	}


	/**
	 *  Sets the recordFormat attribute of the DDSServicesForm object
	 *
	 * @param  val  The new recordFormat value
	 */
	public void setRecordFormat(String val) {
		recordFormat = val;
	}


	/**
	 *  Gets the recordFormat attribute of the DDSServicesForm object
	 *
	 * @return    The recordFormat value
	 */
	public String getRecordFormat() {
		return recordFormat;
	}


	/**
	 *  Gets the s attribute of the DDSServicesForm object
	 *
	 * @return    The s value
	 */
	public int getS() {
		return s;
	}


	/**
	 *  Sets the s attribute of the DDSServicesForm object
	 *
	 * @param  val  The new s value
	 */
	public void setS(int val) {
		s = val;
	}


	/**
	 *  Gets the n attribute of the DDSServicesForm object
	 *
	 * @return    The n value
	 */
	public int getN() {
		return n;
	}


	/**
	 *  Sets the n attribute of the DDSServicesForm object
	 *
	 * @param  val  The new n value
	 */
	public void setN(int val) {
		n = val;
	}


	/**
	 *  Gets the role name for which this user is authorized for
	 *
	 * @return    The authorizedFor value
	 */
	public String getAuthorizedFor() {
		return authorizedFor;
	}



	/**
	 *  Sets the role name for which this user is authorized for
	 *
	 * @param  val  The new authorizedFor value
	 */
	public void setAuthorizedFor(String val) {
		authorizedFor = val;
	}


	/**
	 *  Sets the errorCode attribute of the DDSServicesForm object
	 *
	 * @param  errorCode  The new errorCode value
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}


	/**
	 *  Gets the errorCode attribute of the DDSServicesForm object
	 *
	 * @return    The errorCode value
	 */
	public String getErrorCode() {
		return errorCode;
	}


	/**
	 *  Sets the errorMsg attribute of the DDSServicesForm object
	 *
	 * @param  errorMsg  The new errorMsg value
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}


	/**
	 *  Gets the errorMsg attribute of the DDSServicesForm object
	 *
	 * @return    The errorMsg value
	 */
	public String getErrorMsg() {
		return errorMsg;
	}


	/**
	 *  Gets the results attribute of the DDSServicesForm object
	 *
	 * @return    The results value
	 */
	public ResultDocList getResults() {
		return resultDocList;
	}


	/**
	 *  Sets the results attribute of the DDSServicesForm object
	 *
	 * @param  results  The new results value
	 */
	public void setResults(ResultDocList results) {
		resultDocList = results;
	}


	/**
	 *  Gets the number of matching results.
	 *
	 * @return    The numResults value
	 */
	public int getNumResults() {
		if (resultDocList == null)
			return 0;
		return resultDocList.size();
	}


	/**
	 *  A list of UTC dates in the past in the following order: one minute, one hour, one day, one week, one
	 *  month, one year.
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
	 * @author    John Weatherley
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
	 *  Return a string for the current time and date, sutiable for display in log files and output to standout:
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


