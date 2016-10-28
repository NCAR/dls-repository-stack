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
package edu.ucar.dls.repository.action;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import edu.ucar.dls.repository.RepositoryManager;
import edu.ucar.dls.repository.action.form.RepositoryAnalyticsForm;
import edu.ucar.dls.repository.validation.IndexDataBean;
import edu.ucar.dls.webapps.tools.GeneralServletTools;


/**
  Action class for rendering the analytics chart and options page
 */
public final class RepositoryAnalyticsAction extends Action {
	/**
	Action methods for extracting graph data from the archived summary data files
	 */
	public static String COUNT = "count";
	public static String DATE = "date";
	public static String DATA_TYPES_CACHE_KEY = "histogramDataTypes";
	@Override
	public ActionForward execute(
	                             ActionMapping mapping,
	                             ActionForm form,
	                             HttpServletRequest req,
	                             HttpServletResponse response)
		 throws Exception {
		ServletContext servletContext = getServlet().getServletContext();
		RepositoryAnalyticsForm rsf = (RepositoryAnalyticsForm) form;
		
		// localize all the graphing options variables
		String dataType = rsf.getDataType();
		boolean cumulative = rsf.isCumulative();
		String dataTypeSelectionOption = rsf.getDataTypeSelectionOption();
		String[] selectedDataTypeOptions = rsf.getSelectedDataTypeOptions();
		String collectionSelectionOption = rsf.getCollectionSelectionOption();
		String[] selectedCollections = rsf.getSelectedCollections();
		String fromDate = rsf.getFromDate();
		String toDate = rsf.getToDate();
		
		
		
		// Set up the from and to date if nothing is specified. These dates default to the time
		// When the data starts and ends
		if(fromDate==null||toDate==null||fromDate==""||toDate=="" )
		{
			String[] dateRanges = getDateRange(servletContext);
			if(fromDate==null||fromDate=="")
				rsf.setFromDate(dateRanges[0]);
			if(toDate==null||toDate=="")
				rsf.setToDate(dateRanges[1]);
			
			fromDate = rsf.getFromDate();
			toDate = rsf.getToDate();
			
		}
		
		Date fromDateObj = getDateFromString(rsf.getFromDate());
		Date toDateObj = getDateFromString(rsf.getToDate());
		// If from date is greater then to  date. To date should be from date
		if(fromDateObj.compareTo(toDateObj)==1)
		{
			rsf.setToDate(rsf.getFromDate());
			toDate = rsf.getFromDate();
		}
		Map<String, Map<String,String>> dataTypes = getOrCreateDataTypes(servletContext);
		if(dataType!=null)
		{
			// extract the graph data
			HashMap<String, List<HashMap<String, String>>> graphData = createGraphData(servletContext, dataType, cumulative, 
					dataTypeSelectionOption, selectedDataTypeOptions,collectionSelectionOption,
					selectedCollections, fromDate, toDate);
			
			// set it in the form so it can be displayed on the page
			rsf.setGraphData(graphData);
			
			List<String> includedFacetResultTypes = null;
			if(dataTypeSelectionOption.equals(RepositoryAnalyticsForm.ALL))
				includedFacetResultTypes = new ArrayList(dataTypes.get(dataType).keySet());
			else
				includedFacetResultTypes = Arrays.asList(rsf.getSelectedDataTypeOptions());
			rsf.setIncludedFacetResultTypes(includedFacetResultTypes);
		}
		
		rsf.setDataTypes(dataTypes);
		
		// Collections were already set in datatypes for record count, no reason to fetch
		// them again. Just grab them from there. The map format is setSpec -> Name
		rsf.setCollections(dataTypes.get(RepositoryAnalyticsForm.COLLECTION));
		return (mapping.findForward("display.repository.analytics"));
	}
	
	/*
	 * Method that extracts the data and puts them into java objects that can be then used in javascript
	 */
	private HashMap<String, List<HashMap<String, String>>> createGraphData(
			ServletContext servletContext, String dataType, boolean cumulative,
			String dataTypeSelectionOption, String[] selectedDataTypeOptions,
			String collectionSelectionOption, String[] selectedCollections,
			String fromDateString, String toDateString) throws Exception {
		
		// We need the dates instead of the strings to use the dates
		Date fromDate = getDateFromString(fromDateString);
		Date toDate = getDateFromString(toDateString);
		
		// We want to create the base graph data, to make sure we represent 0's. the base graph data
		// Will be all dates, with all selected dataType options as 0. Therefore if we start extracting
		// data from the files and a datatype doesn't appear in the files, that datatype will be shown as
		// 0. Instead of not being shown at all
		HashMap<String, List<HashMap<String, String>>> graphData = createBaseGraphData(servletContext, 
				fromDate, toDate, dataType, 
				dataTypeSelectionOption, selectedDataTypeOptions, cumulative);
		
		File archiveIndexDataBeanDir = getAnalyticsDir(servletContext);
		Pattern filenamePattern = Pattern.compile("(\\d{4}-\\d{1,2})-.*");

		for (File summaryDataDir:archiveIndexDataBeanDir.listFiles())
		{
			Matcher m = filenamePattern.matcher(summaryDataDir.getName());
			m.matches();
			String dateString = m.group(1);
			Date dateObj = getDateFromString(dateString);
			
			// If the file doesn't fit within the date range just keep going through the rest of the files
			if(fromDate!=null && dateObj.compareTo(fromDate)==-1)
				continue;
			if(toDate!=null && dateObj.compareTo(toDate)==1)
				continue;
			
			// Loop through all the files that we need underneath this data summary data. The method
			// getFilesToProcess is used because we use different files depending on what is selected.
			// the indexSummary is used by default unless individual collections are selected
			for(File indexSummaryFile:getFilesToProcess(summaryDataDir, dataType, 
					collectionSelectionOption, selectedCollections ))
			{
				IndexDataBean indexSummary = RepositoryManager.createIndexDataBean(indexSummaryFile);
				if(indexSummary==null)
					continue;
				Map<String, Map> facetCategories = indexSummary.getFacetDetails();
				
				ArrayList<Map> categories = null;
				// categories can be considered in this case the data type. audience, type, subject etc...
				if (facetCategories!=null)
					categories = new ArrayList(facetCategories.values());
				else
					categories = new ArrayList();
				// Collections are not considered facets but in the case for graphing thats an option
				// so add that if applicable
				if(indexSummary.getCollectionsDetails()!=null)
				{
					HashMap<String, Object> recordCounts = new HashMap<String, Object>();
					
					recordCounts.put(IndexDataBean.LABEL, RepositoryAnalyticsForm.COLLECTION);
		
					recordCounts.put(IndexDataBean.RESULTS, 
							indexSummary.getCollectionsDetails().get(IndexDataBean.COLLECTIONS));
					categories.add(recordCounts);
				}
				
				// Loop through each category and put in the graph data
				for(Map facetCategory:categories)
				{
					String facetLabel = (String)facetCategory.get(IndexDataBean.LABEL);
					Map<String, Map>facetResults = (Map)facetCategory.get(IndexDataBean.RESULTS);
					
					// We only want to do the facet that was selected and factes that have data
					if(!facetLabel.equals(dataType) || facetResults==null)
						continue;
					
					List<HashMap<String, String>> graphElement = null;
					for(Map facetResult:facetResults.values())
					{
						
						String facet = null;
						
						if(facetLabel.equals(RepositoryAnalyticsForm.COLLECTION))
							facet = (String)facetResult.get(IndexDataBean.SET_SPEC);
						else
							facet = (String)facetResult.get(IndexDataBean.LABEL);
						
						if(facet==null)
							facet="Null";
						// Only add the data if the client selected all options or if it was an option that was selected
						if(dataTypeSelectionOption.equals(RepositoryAnalyticsForm.ALL)||Arrays.asList(selectedDataTypeOptions).contains(facet))
						{
							String labelToUse = null;
							// If Cumulative was selected all options are considered the same, as text all
							if(cumulative)
								labelToUse = RepositoryAnalyticsForm.ALL;
							else
								labelToUse = facet;
							
							graphElement = graphData.get(labelToUse);
							HashMap<String,String> graphElementData = null;
							
							// Find the date element within the it. selecting it
							for(HashMap<String, String>graphElementDataArg :graphElement)
							{
								if(graphElementDataArg.get(DATE).equals(dateString))
								{
									graphElementData = graphElementDataArg;
									break;
								}
							}
							// Add the counts together.
							int count = Integer.parseInt((graphElementData.get(COUNT)))+Integer.parseInt((String)facetResult.get(IndexDataBean.RECORDS));
							graphElementData.put(COUNT, String.valueOf(count));
						}
					}	
				}
			}
		
		}

		return graphData;
	}
	
	/*
	 * Take a dateMonthString and convert it to a real date for comparison
	 */
	private Date getDateFromString(String dateMonthString) throws ParseException
	{
		if(dateMonthString==null)
			return null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		// Day is arbitrary, from the point of view of the data and graph. We only care about the month and year
		// But day is needed to create dates. So as long as all dates are compared on the first. Everything should
		// work out correctly
		return formatter.parse(dateMonthString+"-01");
	}
	
	/*
	 * Create a Date string given a date
	 */
	private String getStringFromDate(Date date) throws ParseException
	{
		if(date==null)
			return null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		return  formatter.format(date);
	}
	
	/*
	 * Create the base data for the graph. This graph data, is a hashmap for all selected datetype options,
	 * with "all being extended" to actually be all applicable data type options. Throughout all selected dates.
	 * the base data sets everything to 0. This way if something is selected, even if its not found in the data
	 * it will be shown on the graph as 0.
	 */
	private HashMap<String, List<HashMap<String, String>>> createBaseGraphData(ServletContext servletContext,
			Date fromDate, Date toDate, String dataType,
			String dataTypeSelectionOption, String[] selectedDataTypeOptions,
			boolean cumulative) throws Exception {
		HashMap<String, List<HashMap<String, String>>> baseGraphData = new HashMap<String, List<HashMap<String, String>>>();
		
		// We use calendars to loop through all months/years creating the data
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		
		start.setTime(fromDate);
		end.setTime(toDate);
		
		// Expand on all datatypes if all is selected as an option
		List<String> dataTypeOptionsToUse = new ArrayList<String>();
		if(cumulative)
			dataTypeOptionsToUse.add(RepositoryAnalyticsForm.ALL);
		else if(dataTypeSelectionOption.equals(RepositoryAnalyticsForm.ALL))
			dataTypeOptionsToUse.addAll(getOrCreateDataTypes(servletContext).get(dataType).keySet());
		else
			dataTypeOptionsToUse.addAll(Arrays.asList(selectedDataTypeOptions));
		
		for(String dataTypeOption: dataTypeOptionsToUse)
		{
			List<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
			start.setTime(fromDate);
			for (Date date = start.getTime(); !start.after(end); start.add(Calendar.MONTH, 1),date = start.getTime()) {
				HashMap<String, String> dateResultMap = new HashMap<String, String>();
				dateResultMap.put(DATE, getStringFromDate(date));
				dateResultMap.put(COUNT, "0");
				resultList.add(dateResultMap);
			}
			baseGraphData.put(dataTypeOption, resultList);
		}
		return baseGraphData;
	}

	/*
	 * Get the files to process given a summary data directory and what was selected by the clients. 
	 * This is used to make sure that when individual collections are selected to grab the data from. That
	 * we are using the collection specific numbers instead of the summary data
	 */
	private List<File> getFilesToProcess(File summaryDataDir, String dataType,
			String collectionSelectionOption, String[] selectedCollections) {
		ArrayList<File> filesToProcess = new ArrayList<File>();
		
		if(dataType.equals(RepositoryAnalyticsForm.COLLECTION)|| collectionSelectionOption.equals(RepositoryAnalyticsForm.ALL))
			filesToProcess.add(new File(summaryDataDir, RepositoryManager.SUMMARY_REPORT_FILE_NAME));
		else
		{
			File collectionFacetsDataDir = new File(summaryDataDir, RepositoryManager.COLLECTION_FACETS_DIR_NAME);
			for(String setSpec: selectedCollections)
			{
				File possibleFile = new File(collectionFacetsDataDir, setSpec+".xml");
				if(possibleFile.exists())
					filesToProcess.add(possibleFile);
			}
		}
		return filesToProcess;
	}

	/*
	 * Get or create the datatypes depending on if they are in memory already
	 */
	private Map<String, Map<String,String>> getOrCreateDataTypes(
			ServletContext servletContext) throws ServletException {
		
		Object histogramDataTypesObject = servletContext.getAttribute(DATA_TYPES_CACHE_KEY);
		if(histogramDataTypesObject==null)
		{
			histogramDataTypesObject = createDataTypes(servletContext);
			servletContext.setAttribute(DATA_TYPES_CACHE_KEY, histogramDataTypesObject);
		}
		return (Map<String, Map<String,String>>)histogramDataTypesObject;
	}

	/*
	 * Create the list of datatypes by looping through all files getting all the possibilites
	 */
	private Map<String, HashMap<String,String>> createDataTypes(ServletContext servletContext) throws ServletException {
		
		File archiveIndexDataBeanDir = getAnalyticsDir(servletContext);
		Map<String, HashMap<String,String>> dataTypes = new HashMap<String, HashMap<String,String>>();
		
		for (File summaryDataDirectory:archiveIndexDataBeanDir.listFiles())
		{
			File summaryFile = new File(summaryDataDirectory, RepositoryManager.SUMMARY_REPORT_FILE_NAME);
			IndexDataBean indexSummary = RepositoryManager.createIndexDataBean(summaryFile);
			if(indexSummary==null)
				continue;
			Map<String, Map> facetCategories = indexSummary.getFacetDetails();
			
			ArrayList<Map> categories = null;
			if(facetCategories!=null)
				categories = new ArrayList(facetCategories.values());
			else
				categories = new ArrayList();
			HashMap<String, Object> recordCounts = new HashMap<String, Object>();
			recordCounts.put(IndexDataBean.LABEL, RepositoryAnalyticsForm.COLLECTION);

			recordCounts.put(IndexDataBean.RESULTS, 
					indexSummary.getCollectionsDetails().get(IndexDataBean.COLLECTIONS));
			categories.add(recordCounts);
			for(Map facetCategory:categories)
			{
				
				String facetLabel = (String)facetCategory.get(IndexDataBean.LABEL);
				Map<String, Map>facetResults = (Map)facetCategory.get(IndexDataBean.RESULTS);
				
				HashMap<String,String> dataTypeResults = null;
				if(dataTypes.containsKey(facetLabel))
					dataTypeResults = dataTypes.get(facetLabel);
				else
				{
					dataTypeResults = new HashMap<String,String>();
					dataTypes.put(facetLabel, dataTypeResults);
				}
				if(facetResults==null)
					continue;
				for(Map facetResult:facetResults.values())
				{
					String value = null;
					String text = null;
					// Collections are special, since we have set_specs and names
					if(facetLabel.equals(RepositoryAnalyticsForm.COLLECTION))
					{
						value = (String)facetResult.get(IndexDataBean.SET_SPEC);
						text = (String)facetResult.get(IndexDataBean.NAME);
					}
					else
					{
						value = (String)facetResult.get(IndexDataBean.LABEL);
						text = value;
						if(facetResult.containsKey(IndexDataBean.NAME))
							text = (String)facetResult.get(IndexDataBean.NAME);
					}
					if(value==null)
						value = "Null";
					if(text==null)
						text = "Null";
					if(!dataTypeResults.containsKey(value))
					{
						dataTypeResults.put(value, text);
						
					}
				}
				
			}
		}

		// todo order all the data types
		return dataTypes;
	}

	/*
	 * Get the possible date ranges for the data
	 */
	private String[] getDateRange(ServletContext servletContext) throws ParseException, ServletException {
		
		
		File archiveIndexDataBeanDir = getAnalyticsDir(servletContext);
		Date startDate = null;
		Date endDate = null;
		
		Pattern filenamePattern = Pattern.compile("(\\d{4}-\\d{1,2})-.*");
		
		for (File summaryDataDir:archiveIndexDataBeanDir.listFiles())
		{
			Matcher m = filenamePattern.matcher(summaryDataDir.getName());
			m.matches();
			String dateString = m.group(1);
			Date dateObj = getDateFromString(dateString);
			if(startDate==null || dateObj.compareTo(startDate)==-1)
				startDate=dateObj;
			if(endDate==null || dateObj.compareTo(endDate)==1)
				endDate=dateObj;
		}
		String [] results = new String[2];
		results[0] = getStringFromDate(startDate);
		results[1] = getStringFromDate(endDate);
		return results;
	}
	
	private File getAnalyticsDir(ServletContext servletContext)
	 throws ServletException {
		return new File(GeneralServletTools.getAbsolutePath(servletContext.getInitParameter(
			"repositoryAnalyticsData"), servletContext));
}
	
}

