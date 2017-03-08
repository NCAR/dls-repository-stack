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
package edu.ucar.dls.harvest.processors.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
  Custom processor that transforms a type of html encoded sub script number and makes
  it an entity reference.
 */

@XmlRootElement(name="SubscriptTransform")
public class SubscriptTransform extends RecordProcessor{
	private String[]  subscriptRegExpressions = null;
	private List<Pattern> subscriptPatterns = null;
	
	private static final String ENTITY_REF_DIGIT_REF_FORMAT = "&#832";
	private static final String[] ENTITY_REF_REPLACE_MAPPINGS = {
			"+:&#8330;", 
			"-:&#8331;",
			"=:&#8332;",
			"(:&#8333;",
			"):&#8334;",
			"a:&#8336;",
			"e:&#8337;",
			"o:&#8338;",
			"i:&#7522;",
			"r:&#7523;",
			"u:&#7524;",
			"v:&#7525;",
			"x:&#8339;"};
			
	private Map<Character, String> entityRefReplaceMap = new HashMap<Character, String>();
	
	/**
	 * initialize method that is being used to make sure that the the proecessor
	 * was setup correctly. 
	 */
	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);
		
		// make sure that at least one regular expression is set
		if(this.subscriptRegExpressions.length==0)
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					"Subscript transform requires at least one subscript Regular Expression.",
					"SubscriptTransform.initialize()");
		
		this.subscriptPatterns = new ArrayList<Pattern>();
		for(String subscriptRegEx: this.subscriptRegExpressions)
		{
			this.subscriptPatterns.add( Pattern.compile(subscriptRegEx));
		}
		
		for(String mappingString:ENTITY_REF_REPLACE_MAPPINGS) 
		{
			String[] mapping = mappingString.split(":"); 
			char key = mapping[0].charAt(0);
			this.entityRefReplaceMap.put(Character.valueOf(key), mapping[1]);
		}
	}
	
	/**
	 * Method called by the ingest processor that uses the regular expression supplied
	 * to find them and replace each digit of the containing number with an entity reference
	 * so <expr>125</expr> will be &#8321;&#8322;&#8325; since in entities the base &#832 goes
	 * in order of the subscript
	 */
	public String run(String documentId, String record) 
		throws HarvestException {

		 String formatedRecord = new String(record);
		 
		 // loop through all the patterns
		 for(Pattern pattern: this.subscriptPatterns )
		 {
			 Matcher m = pattern.matcher(record);
			 while (m.find()) {
				 String replacement = "";
				 String subChars = m.group(1);

				 // Replace each didgit with its entity reference equivalent
				 for(int i=0; i<subChars.length();i++)
				 {
					 char aChar = subChars.charAt(i);

					 if(aChar >= '0' && aChar <= '9')
					 {
						 replacement = String.format("%s%s%c;", replacement, 
								 ENTITY_REF_DIGIT_REF_FORMAT, 
							 subChars.charAt(i));
					 }
					 else if(this.entityRefReplaceMap.containsKey(Character.valueOf(aChar)))
					 {
						 String replacementValue = this.entityRefReplaceMap.get(
								 Character.valueOf(aChar));
						 replacement = String.format("%s%s", replacement, 
								 replacementValue);
					 }
					 else
					 {
						 replacement = String.format("%s%c", replacement, aChar);
					 }
					 
				 }
				 
				 formatedRecord = formatedRecord.replaceAll(java.util.regex.Pattern.quote(m.group(0)), 
						 			replacement);
			 }
		 }
		
		// Only if it changed return it otherwise return null, so we don't waste
		// a update to the record
		if (!formatedRecord.equals(record))
		{	
			return formatedRecord;
		}
		
		return null;
	}
	
	
	@XmlElement(name = "regular-expression")
	@XmlElementWrapper(name="regular-expressions")
	public String[] getSubscriptRegExpressions() {
		return subscriptRegExpressions;
	}

	public void setSubscriptRegExpressions(String[] subscriptRegExpressions) {
		this.subscriptRegExpressions = subscriptRegExpressions;
	}

}
