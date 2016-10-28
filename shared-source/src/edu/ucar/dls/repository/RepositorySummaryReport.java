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
 *  Copyright 2002-2012 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */

package edu.ucar.dls.repository;

import java.io.*;
import java.util.*;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.Element;
import org.dom4j.Attribute;
import edu.ucar.dls.xml.Dom4jUtils;

import edu.ucar.dls.oai.OAIUtils;

/**
 *  Generates an XML summary report for the repository.
 *
 * @author    John Weatherley
 */
public class RepositorySummaryReport {
	private RepositoryManager repositoryManager = null;


	/**
	 *  Constructor for the RepositorySummaryReport object
	 */
	protected RepositorySummaryReport(RepositoryManager rm) { 
		repositoryManager = rm;
	}


	protected String getRepositorySummaryReport() {
		Document repositorySummary = DocumentHelper.createDocument();
		Element root = repositorySummary.addElement("RepositorySummary");
		
		Element element = null;
		
		// Basic stats
		element = root.addElement( "numRecords" ).addText( Integer.toString(repositoryManager.getNumRecordsInIndex()) );
		element = root.addElement( "reportTimestamp" ).addText(OAIUtils.getDatestampFromDate(new Date()));
		element = root.addElement( "repositoryCreationDate" ).addText(OAIUtils.getDatestampFromDate(repositoryManager.getCreationDate()));
		element = root.addElement( "repositoryLastModifiedDate" ).addText(OAIUtils.getDatestampFromDate(repositoryManager.getLastModifiedDate()));
		
		/* // Collections summary
		Element collectionsSummary = root.addElement( "collectionsSummary" )
			.addElement( "numCollections" ).addText("4"); */
			
		// Format summary
		List xmlFormats = repositoryManager.getXmlFormats();
		Element xmlFormatSummary = root.addElement("xmlFormats")
			.addAttribute( "numFormats",Integer.toString(xmlFormats.size()));
			
		for(int i = 0; i < xmlFormats.size(); i++){
			String xmlFormat = (String)xmlFormats.get(i);
			xmlFormatSummary.addElement("xmlFormat")
				.addAttribute("numRecords",Integer.toString(repositoryManager.getNumRecordsForFormat(xmlFormat)))
				.addAttribute("numCollections","xxx")
				.addText(xmlFormat);
		}
			
		// Collections list
		Element collections = root.addElement( "collections" )
			.addAttribute( "numCollections","4");
		element = collections.addElement( "collection" )
			.addAttribute( "key","col-123" )
			.addAttribute( "xmlFormat","lar" )
			.addAttribute( "numRecords","10" )
			.addAttribute( "numMultiRecords","1" )
			.addText( "Test collection" );
			
		// Fields summary, relations summary, others?
		
		return Dom4jUtils.prettyPrint(root);	
	}


}

