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
  Custom processor that transforms a type of html encoded sub super number and makes
  it an entity reference.
 */

@XmlRootElement(name="SuperscriptTransform")
public class SuperscriptTransform extends RecordProcessor{
	private String[]  superscriptRegExpressions = null;
	private List<Pattern> superscriptPatterns = null;
	
	private static final String[] ENTITY_REF_REPLACE_MAPPINGS = {
			"0:&#8304;",
			"1:&#185;",
			"2:&#178;",
			"3:&#179;",
			"4:&#8308;",
			"5:&#8309;",
			"6:&#8310;",
			"7:&#8311;",
			"8:&#8312;",
			"9:&#8313;",
			"+:&#8314;",
			"-:&#8315;",
			"=:&#8316;",
			"(:&#8317;",
			"):&#8318;",
			"i:&#8305;",
			"n:&#8319;"
	};
			
	private Map<Character, String> entityRefReplaceMap = new HashMap<Character, String>();
	
	/**
	 * initialize method that is being used to make sure that the the proecessor
	 * was setup correctly. 
	 */
	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);
		
		// make sure that at least one regular expression is set
		if(this.superscriptRegExpressions.length==0)
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					"Superscript transform requires at least one superscript Regular Expression.",
					"SuperscriptTransform.initialize()");
		
		this.superscriptPatterns = new ArrayList<Pattern>();
		for(String superscriptRegEx: this.superscriptRegExpressions)
		{
			this.superscriptPatterns.add( Pattern.compile(superscriptRegEx));
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
	 * in order of the superscript
	 */
	public String run(String documentId, String record) 
		throws HarvestException {

		 String formatedRecord = new String(record);
		 
		 // loop through all the patterns
		 for(Pattern pattern: this.superscriptPatterns )
		 {
			 Matcher m = pattern.matcher(record);
			 while (m.find()) {
				 String replacement = "";
				 String subChars = m.group(1);

				 // Replace each didgit with its entity reference equivalent
				 for(int i=0; i<subChars.length();i++)
				 {
					 char aChar = subChars.charAt(i);

					 if(this.entityRefReplaceMap.containsKey(Character.valueOf(aChar)))
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
	public String[] getSuperscriptRegExpressions() {
		return superscriptRegExpressions;
	}

	public void setSuperscriptRegExpressions(String[] superscriptRegExpressions) {
		this.superscriptRegExpressions = superscriptRegExpressions;
	}

}
