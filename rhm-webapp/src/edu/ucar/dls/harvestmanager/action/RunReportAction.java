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
package edu.ucar.dls.harvestmanager.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.dlese.dpc.util.TimedURLConnection;
import org.dlese.dpc.util.URLConnectionTimedOutException;
import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.processors.record.EducationLevelsVsAlignmentsReport;
import edu.ucar.dls.harvestmanager.action.reports.IndexerXpathsToTSVReport;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

/**
 *  Action controller for running reports, This uses a dispatch action so the report name is sent in.
 */
public final class RunReportAction extends DispatchAction  {
	
	
	private int getDayPadding(HttpServletRequest request)
	{
		int dayPadding=0;
		String dayPaddingString = request.getParameter("day_padding");
		if(dayPaddingString!=null)
		{
			try
			{
				dayPadding = Integer.parseInt(dayPaddingString);
			}
			catch(Exception e)
			{}
				
		}
		return dayPadding;
	}
	/**
	 * Run the zip version educationLevelsVsAlignmentsReport. This report just gathers the latest run for all
	 * collections and puts all their alignment reports together if they had one into a zip
	 * file and returns it.
	 */
	public ActionForward educationLevelsVsAlignmentsReportZip(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,HttpServletResponse response) 
        throws Exception {

		int dayPadding=getDayPadding(request);
		
		// Use a temp file, so it doesn't stick around on the file system
		File tempZipFile = File.createTempFile("educationLevelVsAlignmentsReport",".zip");
		ZipOutputStream zos =
		    new ZipOutputStream(new FileOutputStream(tempZipFile));
		byte[] buf = new byte[1024];
		
		response.setContentType("application/zip");
	    response.setHeader("Content-Disposition",String.format(
	    		"attachment;filename=educationLevelVsAlignmentsReport_%d.zip",dayPadding) );

	    File fileStorageDirectory =  new File(
				Config.BASE_FILE_PATH_STORAGE);
	    
	    // Loop through all the collections.
	    for(File collectionDirectory: fileStorageDirectory.listFiles())
	    {
	    	File[] sessionDirectories = collectionDirectory.listFiles();
	    	if(sessionDirectories!=null && sessionDirectories.length==0)
				continue;
	    	
	    	// We only want the latest session
	    	Arrays.sort(sessionDirectories, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
	    	File lastSessionDirectory = sessionDirectories[0];
	    	
	    	File collectionEdLevelsReport = new File(lastSessionDirectory, 
	    			String.format(EducationLevelsVsAlignmentsReport.REPORT_FILE_NAME_FORMAT, dayPadding));
	    	// If it doesn't have a report don't do anything
	    	if(!collectionEdLevelsReport.exists())
	    		continue;

	    	String title = collectionDirectory.getName();
	    	
	    	// The status file contains the setSpec so we can use that as the file name instead
	    	// of the mdpHandle which is the collectionDirectoy name if it exists
	    	File statusFile = new File(lastSessionDirectory, 
	    			"status.xml");
	    	
	    	if(statusFile.exists())
	    	{
	    		Document document = Dom4jUtils.getXmlDocument(statusFile);
	    		Element setSpecNode = (Element)document.getRootElement().selectSingleNode("setSpec");
	    		if(setSpecNode!=null)
	    			title = setSpecNode.getText();
	    	}
	    	// Write the xml file into the zip file
			FileInputStream fis = new FileInputStream(collectionEdLevelsReport);
			zos.putNextEntry(new ZipEntry(title+".xml"));
			int len;
			while ((len = fis.read(buf)) > 0)
			    zos.write(buf, 0, len);
			fis.close();
			zos.closeEntry();
	    	
	    }
	    zos.close();
	    ServletOutputStream out = response.getOutputStream();
	    
	    FileInputStream in = 
      		new FileInputStream(tempZipFile);
	    
        byte[] outputByte = new byte[4096];
        //copy binary content to output stream
        while(in.read(outputByte, 0, 4096) != -1){
        	out.write(outputByte, 0, 4096);
        }
        
        // Close everything
        in.close();
        out.flush();
        out.close();
        tempZipFile.delete();
        return null;
	}
	
	/**
	 * Runs the concat version educationLevelsVsAlignmentsReport. This report just gathers the latest run for all
	 * collections and puts all their alignment reports together in an xml.
	 */
	public ActionForward educationLevelsVsAlignmentsReportConcat(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,HttpServletResponse response) 
        throws Exception {

		int dayPadding=getDayPadding(request);
		
		response.setContentType("text/xml");
		response.setHeader("Content-Disposition",String.format(
	    		"attachment;filename=educationLevelVsAlignmentsReport_%d.xml",dayPadding) );


	    ServletOutputStream out = response.getOutputStream();
	    out.print( getEducationLevelsVsAlignmentsConcat(dayPadding));
	    return null;
	}
	
	// Helper method that creates the xml concat for all current reports
	private String getEducationLevelsVsAlignmentsConcat(int dayPadding) throws MalformedURLException, DocumentException
	{
		File fileStorageDirectory =  new File(
				Config.BASE_FILE_PATH_STORAGE);
	    
	    
	    ArrayList<String> xmlStrings = new ArrayList<String>();
	    
	    String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	    xmlStrings.add(header);
	    xmlStrings.add("<collections>");
	    
	    int collectionCount = 0;
	    int recordCount = 0;
	    // Loop through all the collections.
	    for(File collectionDirectory: fileStorageDirectory.listFiles())
	    {
	    	File[] sessionDirectories = collectionDirectory.listFiles();
	    	if(sessionDirectories!=null && sessionDirectories.length==0)
				continue;
	    	
	    	// We only want the latest session
	    	Arrays.sort(sessionDirectories, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
	    	File lastSessionDirectory = sessionDirectories[0];
	    	
	    	File collectionEdLevelsReport = new File(lastSessionDirectory, 
	    			String.format(EducationLevelsVsAlignmentsReport.REPORT_FILE_NAME_FORMAT, dayPadding));

	    	// If it doesn't have a report don't do anything
	    	if(!collectionEdLevelsReport.exists())
	    		continue;
	    	
	    	Document collectionDocument = Dom4jUtils.getXmlDocument(
	    			collectionEdLevelsReport);
	    	
	    	// If the record count was not included in the collection report just use 0.
	    	// This will be the case for reports written before it was added.
	    	int collectionRecordCount = 0;
	    	try
	    	{
	    		collectionRecordCount = Integer.parseInt(collectionDocument.getRootElement().attributeValue("record_count"));
	    	}
	    	catch(Exception e)
	    	{
	    		collectionRecordCount= 0;
	    	}
	    	collectionCount++;
	    	recordCount += collectionRecordCount;
	    	
	    	String xmlString = collectionDocument.asXML().replace(header, "");
	    	xmlStrings.add(xmlString);
	
	    }
	    xmlStrings.add("</collections>");
	    Document completeXmlDocument = Dom4jUtils.getXmlDocument(
	    		StringUtils.join(xmlStrings, "\n"));
	    Element collectionsElement = completeXmlDocument.getRootElement();
	    
	    // Finally add the collection_count and record_count to the collections element
	    collectionsElement.addAttribute("collection_count", Integer.toString(collectionCount));
	    collectionsElement.addAttribute("record_count", Integer.toString(recordCount));

	    return Dom4jUtils.prettyPrint(completeXmlDocument).replace(header, "");
	}
	
	
	/**
	 * Runs the ASN report that is based on the concat report except that it just lists the
	 * asnId along with their accounts instead of seperating it out by record id
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward educationLevelsVsAlignmentsAsnReport(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,HttpServletResponse response) 
        throws Exception {
		
		int dayPadding=getDayPadding(request);
		Document document = Dom4jUtils.getXmlDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+getEducationLevelsVsAlignmentsConcat(dayPadding));
		
		HashMap <String, AtomicInteger> asnCounts = new HashMap<String, AtomicInteger>();
		List <Element>asns = document.selectNodes(
		"//collections/collection/record/error_asns/asnId");
		System.out.println(asns.size());
		for(Element asn:asns)
		{
			if(asn.attribute("created_by_normalization").getText().equals("true"))
			{
				String asnText = asn.getText();
				if(!asnCounts.containsKey(asnText))
					asnCounts.put(asnText, new AtomicInteger(0));
				asnCounts.get(asnText).incrementAndGet();
			}
			
				
		}
		
		ArrayList<String>outputList = new ArrayList<String>();
		outputList.add("<asns>");
		for(Entry<String, AtomicInteger> entrySet: asnCounts.entrySet())
		{
			String asnId = entrySet.getKey();
			int count = entrySet.getValue().get();
			outputList.add(String.format("<asn count='%d'>%s</asn>", count, asnId));
		}
		outputList.add("</asns>");

		Document resultDocument = Dom4jUtils.getXmlDocument(StringUtils.join(outputList, "\n"));
		
		
		response.reset();
		response.setContentType("text/xml");
		response.setHeader("Content-Disposition",String.format(
    		"attachment;filename=educationLevelVsAlignmentsAsnReport_%d.xml",dayPadding) );

		
		ServletOutputStream out = response.getOutputStream();
	    out.print( Dom4jUtils.prettyPrint(resultDocument));
	    return null;
		
		
	}
	
	/**
	 * Runs the IndexerXpathsToTSVReport. This uses the helper class IndexerXpathsToTSVReport to create the report
	 * see that class for specifics
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward IndexerXpathsToTSVReport(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,HttpServletResponse response) 
        throws Exception {
		
		String baseUrl = request.getParameter("base_url");
		String parametersString = request.getParameter("parameters");
		String[] parameters =  parametersString.split("\n");

		response.reset();
		response.setContentType("text/tsv");
		response.setHeader("Content-Disposition",
    		"attachment;filename=IndexerXpathsToTSV.Tsv" );
		PrintWriter out = response.getWriter(); 
		IndexerXpathsToTSVReport.writeReport(out, baseUrl, parameters);

		return null;
		
	}
	
	
	
}
