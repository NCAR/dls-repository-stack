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
package edu.ucar.dls.harvestmanager.action.reports;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dlese.dpc.util.TimedURLConnection;
import org.dlese.dpc.util.URLConnectionTimedOutException;
import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import edu.ucar.dls.harvest.Config;

/**
 * Report helper class the writes out the TSV to a PrintWriter. The report consists of calling the baseUrl that 
 * is sent in, which must be part of the index services search. And uses xpath fields which are send in to query the data
 * for each reocrd and then writes each value to the TSV file for consumption.
 */
public final class IndexerXpathsToTSVReport{
	
	public static int MAX_NUMBER_PER_PAGE = 100;
	
	public static void writeReport(PrintWriter out, String baseUrl, 
			String[] fields) throws IOException, URLConnectionTimedOutException, DocumentException
	
			{
		
		// Add the paging fields, which in increment as we pull everything
		baseUrl = baseUrl+"&s=%d&n=%d";
		Map<String, Integer> maxFieldValueCountMap = new HashMap<String, Integer>();
		for(String fieldXpath:fields)
			maxFieldValueCountMap.put(fieldXpath, new Integer(0));
		
		
		int currentOffset = 0;
		List<Map<String, List<String>>> records = new ArrayList<Map<String, List<String>>>();
		
		while(true)
		{
			String url = String.format(baseUrl, currentOffset, MAX_NUMBER_PER_PAGE );
			
			String fileAsString = TimedURLConnection.importURL(
					url, Config.ENCODING, 10000);
			
			// Localize the xml so the parameters do not need to contain namespaces
			Document document = Dom4jUtils.localizeXml(Dom4jUtils.getXmlDocument(fileAsString));
			
			Element rootElement = document.getRootElement();
			Element totalNumRecordsElement = (Element)rootElement.selectSingleNode(
			"Search/resultInfo/totalNumResults");
			int totalNumRecords = Integer.parseInt(totalNumRecordsElement.getTextTrim());
			
			List<Element> recordElements = rootElement.selectNodes(
					"Search/results/record");
			for(Element recordElement:recordElements)
			{
				Map<String, List<String>> record = new HashMap<String, List<String>>();
				
				// For each record we are going to go through all the fields that were requested.
				for(String field: fields)
				{
					record.put(field, new ArrayList<String>());
					
					// The xpath portion of the field is the second element fetch that from the field and 
					// use that as the xpath to select the nodes
					List<Element> fieldValues = recordElement.selectNodes(getFieldXpath(field));
					
					// Now add each field value to the list as long as unique. 
					int uniqueCount = 0;
					for(Element fieldElement:fieldValues)
					{
						String fieldValue = fieldElement.getTextTrim();
						if(!record.get(field).contains(fieldValue))
						{
							record.get(field).add(fieldValue);
							uniqueCount++;
						}
					}
					Integer maxFieldValueCount = maxFieldValueCountMap.get(field);
					
					// If the unique count is so far the highest keep track of it. Because in the end we need to account
					// for that many columns so all records have their fields aligned correctly. Ie one record might have
					// 2 subject where another one might have 20. Therefore the case of the two we blank out 18 columns
					if(uniqueCount>maxFieldValueCount.intValue())
						maxFieldValueCountMap.put(field, Integer.valueOf(uniqueCount));
				}
				records.add(record);
			}
			
			// If that was the final offset break out
			if(currentOffset>totalNumRecords)
				break;
			currentOffset+=MAX_NUMBER_PER_PAGE;

		}
		
		// Add the headers to the file
		List<String> fieldHeaders = new ArrayList<String>();
		for(String field: fields)
		{
			// The fieldHeader is the first part of the field param get it
			String fieldHeader = getFieldHeaderName(field);
			
			int maxFieldCount = maxFieldValueCountMap.get(field).intValue();
			
			// if the max field count is only one, no reason to index the column header. Leave be. 
			if(maxFieldCount==1)
				fieldHeaders.add(fieldHeader); 
			else
			{
				// Otherwise write all headers with an index number. Notice the plus one. Since Title 0 doesn't really make sense
				for(int i=0; i<maxFieldCount;i++)
				{
					fieldHeaders.add(String.format("%s %d", fieldHeader, i+1)); 
				}
			}
		}
		
		// Tab delimit all the columns
		out.println(StringUtils.join(fieldHeaders, "\t"));
		
		
		// Now to write the actual records
		for(Map<String, List<String>> record:records)
		{
			List<String> columns = new ArrayList<String>();
			
			// We want to display all the fields
			for(String field:fields)
			{
				int count = 0;
				// Add the field values that we have.
				for(String fieldValue:record.get(field))
				{
					columns.add(fieldValue);
					count++;
				}
				
				// Finally blank out all the rest of the columns that we can't fill out because all records will not have the
				// same number of subjects or other fields. But we still want them aligned right.
				for(int fieldCount=count; fieldCount<maxFieldValueCountMap.get(field).intValue();
					fieldCount++)
				{
					columns.add("");
				}
			}
			out.println(StringUtils.join(columns, "\t"));
		}

	}

	/*
	 * Get the xpath from the field
	 */
	private static String getFieldXpath(String field) {
		return field.split(":")[1];
	}

	/*
	 * Get the field name from the field paramater
	 */
	private static String getFieldHeaderName(String field) {
		return field.split(":")[0];
	}
}
