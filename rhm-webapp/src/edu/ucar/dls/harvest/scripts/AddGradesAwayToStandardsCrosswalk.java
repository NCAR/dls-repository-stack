package edu.ucar.dls.harvest.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dlese.dpc.util.TimedURLConnection;
import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.resources.ASNHelper;
import edu.ucar.dls.harvest.resources.SMSHelper;

/**

 *
 */
public class AddGradesAwayToStandardsCrosswalk extends Script{
	
	private static final String CROSSWALK_DOCUMENT = "http://ns.nsdl.org/ncs/ddsws/1-1/groupsNormal/AAAS_NGSS_Standards_identifier_crosswalks.xml";
	private ASNHelper asnHelper = null;
	private SMSHelper smsHelper = null;
	
	public AddGradesAwayToStandardsCrosswalk()
	{
		asnHelper = new ASNHelper();
		smsHelper = new SMSHelper();
	}
	
	
	public String run() throws Exception
	{
		List<String> allGrades = new ArrayList<String>();
		allGrades.add("Pre-Kindergarten");
		allGrades.add("Kindergarten");
		for(int i=1;i<=12;i++)
			allGrades.add("Grade " + i);
		
		String fileAsString = TimedURLConnection.importURL(
				CROSSWALK_DOCUMENT, Config.ENCODING, 5000);
		Document document = Dom4jUtils.getXmlDocument(fileAsString);

		List<Element> targetVocabs = document.getRootElement().selectNodes("*[local-name()='body']/*[local-name()='outline']");
		
		for(Element targetVocabElement: targetVocabs)
		{
			String targetVocab = targetVocabElement.attribute("vocab").getText();
			List<String> targetGrades = this.getGrades(targetVocab);
			if(targetGrades==null || targetGrades.size()==0)
			{
				targetVocabElement.addAttribute("grades_away", "N/A no grades assigned");	
				continue;
			}
			List<Element> sourceVocabElements = targetVocabElement.selectNodes("*[local-name()='outline']");
			
			int targetGradeStartIndex = allGrades.indexOf(targetGrades.get(0));
			int targetGradeEndIndex = allGrades.indexOf(targetGrades.get(targetGrades.size()-1));
			
			for(Element sourceVocabElement: sourceVocabElements)
			{
				String sourceVocab = sourceVocabElement.attribute("vocab").getText();
				
				List<String> sourceGrades = this.getGrades(sourceVocab);
				
				// we don't want any sources that are N/A or ones that don't have grades
				if(sourceGrades==null||sourceGrades.size()==0)
				{
					if(sourceGrades!=null && sourceGrades.size()==0)
						sourceVocabElement.addAttribute("grades_away", "N/A no grades assigned");
					continue;
				}
				if(Collections.disjoint(targetGrades, sourceGrades))
				{
					int sourceGradeStartIndex = allGrades.indexOf(sourceGrades.get(0));
					int sourceGradeEndIndex = allGrades.indexOf(sourceGrades.get(sourceGrades.size()-1));
					int direction1 = Math.abs(sourceGradeStartIndex-targetGradeEndIndex);
					int direction2 = Math.abs(sourceGradeEndIndex-targetGradeStartIndex);
					int gradesAway = 0;
					
					if(direction1<direction2)
						gradesAway = direction1;
					else if(direction2<direction1)
						gradesAway = direction2;
					sourceVocabElement.addAttribute("grades_away", String.valueOf(gradesAway));	
				}
				else
				{
					sourceVocabElement.addAttribute("grades_away", "Grades overlap");	
				}
			}
		}
		
		Dom4jUtils.writeDocToFile(document, new File(Config.BASE_FILE_PATH_STORAGE, "formatedCrosswalk.xml"));

		return null;
	}
	
	private List<String> getGrades(String alignment) throws Exception
	{
		if(alignment.startsWith("http://"))
			return this.asnHelper.getEducationalLevels(alignment);
		else if(alignment.startsWith("SMS"))
			return this.smsHelper.getEducationalLevels(alignment);
		else
			return null;
	}
	
	
	public static void main(String [] args) throws Exception
	{
		new AddGradesAwayToStandardsCrosswalk().run();
	}

}
