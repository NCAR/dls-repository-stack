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
package edu.ucar.dls.harvest.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dlese.dpc.util.TimedURLConnection;
import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import edu.ucar.dls.harvest.Config;


/**
 * Helper class that deals with getting ASN information directly from jesco or through resolver
 * services. This class also takes advantage of caching information for multiple calls.
 */

public class ASNHelper {
	public static final int SERVER_REQUEST_TIMEOUT = 60000;
	private static final String GRADE_RANGE = "grade_range";
	private static final String SUBJECT = "subject";
	
	// Object collections that cache results
	private List<Document> rdfDocuments = null;
	private HashMap<String, Object> asnInfoMap = new HashMap <String, Object>();
	
	
	/**
	 * Fetch an ASN id by its skos external url. For example common core standards external url 
	 * would be http://corestandards.org/Math/Content/HSA/APR/C/5
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String fetchASNIdExternalURL(String url) throws Exception
	{
		String xpath = "asn:Statement/*[name()='skos:exactMatch'][@rdf:resource="+generateConcatForXPath(url)+"]";
		return this.fetchASNId(xpath);
	}
	
	/**
	 * Fetch an ASN Id by its description. Thankfully jesco uses the same description as the standard 
	 * @param description
	 * @return
	 * @throws Exception
	 */
	public String fetchASNIdByDescription(String description) throws Exception
	{
		String xpath = "asn:Statement/dcterms:description[contains(.,"+generateConcatForXPath(description)+")]";
		return this.fetchASNId(xpath);
	}
	
	
	/**
	 * Fetch an ASN by its Dot statement notation
	 * @param stmtNotation
	 * @return
	 * @throws Exception
	 */
	public String fetchASNIdByStmtNotation(String stmtNotation) throws Exception
	{
		String xpath = "asn:Statement/asn:statementNotation[contains(.,"+generateConcatForXPath(stmtNotation)+")]";
		return this.fetchASNId(xpath);
	}
	
	/**
	 * Method that is called by the public facing methods to get an ASN id by
	 * a particular xpath within the rdf document
	 * @param xpath
	 * @return
	 * @throws Exception
	 */
	private String fetchASNId(String xpath) throws Exception
	{
		// Calling get rdf documents get the cached version if possible
		List<Document> rdfDocuments = this.getRDFDcouments();
		
		for(Document rdfDocument: rdfDocuments )
		{
			List<Element> selectedElements = rdfDocument.getRootElement().selectNodes(xpath);
			
			if(selectedElements!=null && selectedElements.size()>0)
			{
				Element selectedElement =  selectedElements.get(0);
				return selectedElement.getParent().attributeValue("about");
			}
		}
		return null;
	}

	/**
	 * Get the RDF documents in dom4j form. This keeps the documents
	 * in memory so it called be caled more then once
	 * @return
	 * @throws Exception
	 */
	private List<Document> getRDFDcouments() throws Exception{

		if(this.rdfDocuments==null)
		{
			this.rdfDocuments = new ArrayList<Document>();
			for(String rdfURL: Config.ASN.RDF_CONVERSION_STMT_URLS)
			{
				String fileAsString = TimedURLConnection.importURL(
						rdfURL, Config.ENCODING, SERVER_REQUEST_TIMEOUT);
				this.rdfDocuments.add(Dom4jUtils.getXmlDocument(fileAsString));
			}
		}
		return this.rdfDocuments;
	}

	/**
	 * Retrieve educational levels based on the asn id through the use
	 * of the resolver
	 * @param asnId
	 * @return
	 * @throws Exception
	 */
	public List<String> getEducationalLevels(String asnId) throws Exception
	{
		List<String> educationLevels = null;
		
		Map<String, Object> asnInfo = this.getASNInfo(asnId);
		
		if(asnInfo==null || !asnInfo.containsKey(GRADE_RANGE))
			return null;
		
		Integer[] gradeLevelRange = (Integer[])asnInfo.get(GRADE_RANGE);
		
		// grades in the ASN are given as a range -1 through 12. We need
		// to convert this to something useable in nsdl_dc. By putting
		// Grade in front of the number for all grades except kindergarten
		// and pre kindergarten
		if(gradeLevelRange!=null)
		{
			int startGrade = gradeLevelRange[0].intValue();
			int endGrade = gradeLevelRange[1].intValue();
			educationLevels = new ArrayList<String>();
			
			for(int gradeLevel=startGrade; gradeLevel<=endGrade; gradeLevel++)
			{
				String educationLevel = null;
				if(gradeLevel==-1)
					educationLevel = "Pre-Kindergarten";
				else if(gradeLevel==0)
					educationLevel = "Kindergarten";
				else
					educationLevel = String.format("Grade %s", gradeLevel);
				educationLevels.add(educationLevel);
			}	
		}
		return educationLevels;
	}
	
	/**
	 * Get the subject for a specified asn id
	 * @param asnId
	 * @return
	 * @throws Exception
	 */
	public String getSubject(String asnId) throws Exception
	{
		Map<String, Object> asnInfo = this.getASNInfo(asnId);
		
		if(asnInfo==null || !asnInfo.containsKey(SUBJECT) || asnInfo.get(SUBJECT)==null)
			return null;
		return (String)asnInfo.get(SUBJECT);
	}
	
	/**
	 * Retrieve the asn info for a particular ASN via the resolver. And
	 * cache it in memory. You can view the struture of these response by just 
	 * looking at example one 
	 * domain/asn/service.do?verb=GetStandard&id=http://asn.jesandco.org/resources/S1012C79
	 * @param asnId
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> getASNInfo(String asnId) throws Exception
	{
		
		if(this.asnInfoMap.containsKey(asnId))
		{
			return (Map<String, Object>)this.asnInfoMap.get(asnId);
			
		}
		
		// it must start with the base url
		if(!asnId.startsWith(Config.ASN.ASN_BASE_URL))
			return null;

		String url = String.format(Config.ASN.ASN_GET_STANDARD_RESOLVER_URL_FORMAT, asnId);
		String fileAsString = TimedURLConnection.importURL(
				url, Config.ENCODING, SERVER_REQUEST_TIMEOUT);
		Document document = Dom4jUtils.localizeXml(Dom4jUtils.getXmlDocument(fileAsString));
		
		HashMap asnInfo = new HashMap<String, Object>();
		
		List<Element> standardElements = document.getRootElement().selectNodes(
				"GetStandard/result/Standard");
		
		// Load the ranges into a Integer[] for keeping
		if(standardElements!=null && standardElements.size()>0)
		{
			Element standardElement = standardElements.get(0);
			String startGradeLevel = standardElement.valueOf("StartGradeLevel");
			String endGradeLevel = standardElement.valueOf("EndGradeLevel");
			Integer[] gradeLevelRange = {new Integer(startGradeLevel), new Integer(endGradeLevel)};
			asnInfo.put(GRADE_RANGE, gradeLevelRange);
		}
		
		List<Element> standardDocuments = document.getRootElement().selectNodes("GetStandard/result/StandardDocument");
		if(standardDocuments!=null && standardDocuments.size()>0)
		{
			Element standardDocElement = standardDocuments.get(0);
			String subject = standardDocElement.valueOf("Subject");
			asnInfo.put(SUBJECT, subject);
		}
		
		// save for next time
		this.asnInfoMap.put(asnId, asnInfo);
		return asnInfo;
		
	}
	
	/**
	 * Method taken from the web to make it so we can search for string
	 * that have quotes and apostrophies in them. It does this by using a concat
	 * function with ticks in it.
	 * @param a_xPathQueryString
	 * @return
	 */
	private static String generateConcatForXPath(String a_xPathQueryString)
	{
		String returnString = "";
		String searchString = a_xPathQueryString;
	    char[] quoteChars = new char[] { '\'', '"' };
	 
	    int quotePos = searchString.indexOf(quoteChars[0]);
	    if(quotePos==-1)
	    	quotePos = searchString.indexOf(quoteChars[1]);
	    if (quotePos == -1)
	    {
	        returnString = "'" + searchString + "'";
	    }
	    else
	    {

	        returnString = "concat(";
	        while (quotePos != -1)
	        {
	            String subString = searchString.substring(0, quotePos);
	            returnString += "'" + subString + "', ";

	            if (searchString.substring(quotePos, quotePos+1).equals( "'"))
	            {
	                returnString += "\"'\", ";
	            }
	            else
	            {
	                //must be a double quote
	                returnString += "'\"', ";
	            }
	            searchString = searchString.substring(quotePos + 1);
	            quotePos = searchString.indexOf(quoteChars[0]);
	    	    if(quotePos==-1)
	    	    	quotePos = searchString.indexOf(quoteChars[1]);
	            
	        }
	        
	        returnString += "'" + searchString + "')";
	    }
	    return returnString;
	}
	
}
