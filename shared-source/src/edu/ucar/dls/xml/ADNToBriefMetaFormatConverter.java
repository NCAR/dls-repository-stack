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

package edu.ucar.dls.xml;

import edu.ucar.dls.util.*;
import javax.servlet.ServletContext;

/**
 *  Converts from ADN format to the BriefMeta format. See the briefMeta <a
 *  href="http://www.dlese.org/Metadata/briefmeta/0.1.01/record.xml">sample
 *  record</a> and <a href="http://www.dlese.org/Metadata/briefmeta/0.1.01/brief-record.xsd">
 *  schema</a> .
 *
 * @author    John Weatherley
 * @see       XMLConversionService
 */
public class ADNToBriefMetaFormatConverter implements XMLFormatConverter {

	/**
	 *  Converts from the ADN format.
	 *
	 * @return    The String "adn".
	 */
	public String getFromFormat() {
		return "adn";
	}


	/**
	 *  Converts to the BrifeMeta format.
	 *
	 * @return    The String "briefmeta".
	 */
	public String getToFormat() {
		return "briefmeta";
	}

	/**
	 *  Gets the time this converter code was last modified. If unknown, this method should
	 *  return -1.
	 *
	 * @return    The time this converter code was last modified.
	 */
	public long lastModified(ServletContext context)
	{
		return -1;				
	}
	
	/**
	 *  Performs XML conversion from ADN to BriefMeta format.
	 *
	 * @param  xml  XML input in the 'adn' format.
	 * @return      XML in the converted 'briefmeta' format.
	 */
	public String convertXML(String xml,ServletContext context) {
			
		StringBuffer output = new StringBuffer();
		try{
			XMLDoc adnXmlDoc = new XMLDoc();
			adnXmlDoc.useXmlString(xml,false,true,false);
		
			output.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
			output.append(
				"<briefRecord xmlns=\"http://adn.dlese.org\" " +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
				"xsi:schemaLocation=\"http://adn.dlese.org " +
				"http://www.dlese.org/Metadata/briefmeta/0.1.01/brief-record.xsd\">\n");	
			
			output.append("<title>");		
			output.append(HTMLTools.encodeCharacterEntityReferences(adnXmlDoc.getXmlString("general/title"),false));
			output.append("</title>\n");

			
			output.append("<description>");		
			output.append(HTMLTools.encodeCharacterEntityReferences(adnXmlDoc.getXmlString("general/description"),false));
			output.append("</description>\n");

			output.append("<url>");		
			output.append(HTMLTools.encodeCharacterEntityReferences(adnXmlDoc.getXmlString("technical/online/primaryURL"),false));
			output.append("</url>\n");	
			
			String [] gradeRanges = adnXmlDoc.getXmlFields("educational/audiences/audience/gradeRange");
			if(gradeRanges != null && gradeRanges.length > 0){
				output.append("<gradeRanges>\n");	
				for(int i = 0; i < gradeRanges.length; i++)
					output.append("  <gradeRange>" + HTMLTools.encodeCharacterEntityReferences(gradeRanges[i],false) + "</gradeRange>\n");				
				output.append("</gradeRanges>\n");
			}
			
			String [] subjects = adnXmlDoc.getXmlFields("general/subjects/subject");
			if(subjects != null && subjects.length > 0){
				output.append("<subjects>\n");	
				for(int i = 0; i < subjects.length; i++)
					output.append("  <subject>" + HTMLTools.encodeCharacterEntityReferences(subjects[i],false) + "</subject>\n");				
				output.append("</subjects>\n");
			}	
			
			String [] resourceTypes = adnXmlDoc.getXmlFields("educational/resourceTypes/resourceType");
			if(resourceTypes != null && resourceTypes.length > 0){
				output.append("<resourceTypes>\n");	
				for(int i = 0; i < resourceTypes.length; i++)
					output.append("  <resourceType>" + HTMLTools.encodeCharacterEntityReferences(resourceTypes[i],false) + "</resourceType>\n");				
				output.append("</resourceTypes>\n");
			}
			
			output.append("</briefRecord>");
						
		}catch (XMLException xe){
			System.out.println("XMLException while creating briefmeta from ADN: " + xe);
			return null;
		}
		
		//System.out.println("Outputting: " + output.toString());
		return output.toString();
	}


	/**
	 * Custom destroy method that may be implemented at a later time to clean up
	 * resources
	 */
	public void destroy() {
		
	}

}

