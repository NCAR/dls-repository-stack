package edu.ucar.dls.harvest.processors.record;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.resources.ASNHelper;
import edu.ucar.dls.harvest.workspaces.ResultsWrapper;
import edu.ucar.dls.harvest.workspaces.Workspace;
/**
 * Processor that creates a report comparing the education levels declared by the record vs the education
 * levels of the ASN's in the record if there are some. For example if the record said its only for 
 * grades 3-6 but its aligned to asn that's grades are 9-12. Something is wrong.
 */

@XmlRootElement(name="EducationLevelsVsAlignmentsReport")
public class EducationLevelsVsAlignmentsReport extends RecordProcessor{
	private String asnElementXpath = null;
	private ASNHelper asnHelper = null;
	public static String REPORT_FILE_NAME_FORMAT = "educationLevelvsAlignmentReport_%d.xml";
	public static int[] GRADE_LEVEL_PADDING_DEF = {0,1,2,3,4,5};
		
	/**
	 * initialize method that is being used to make sure that the the proecessor
	 * was setup correctly. We want to sure that elementXML was set. 
	 */
	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);
		if(this.asnElementXpath==null)
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					"asn-element-xpath is a required attribute for EducationLevelsVsAlignmentReport processor",
					"EducationLevelsVsAlignmentsReport()");
		// Set it once in initialize so that for each record after the first it doesn't have
		// to re-get the conversion docs
		this.asnHelper = new ASNHelper();
		
	}
	
	/*
	 * gets the grade from the grade level string. This converts
	 * it into a int grade so it can be used for going up and down levels
	 */
	private static int getGrade(String gradeLevel)
	{
		String prefix = "Grade ";
		int grade = 0;
		if(gradeLevel.equals("Kindergarten"))
 		 	grade = 0;
 		else if(gradeLevel.equals("Pre-Kindergarten"))
 			grade = -1;
 		else
 		{
 			try
 			{
 				grade = Integer.parseInt(gradeLevel.replace(prefix, ""));
 			}
 			catch(Exception e)
 			{
 				grade = -99;
 			}
 		}
		return grade;
	}
	static class GradeLevelComparator implements Comparator<String>
    {    
		String prefix = "Grade ";
         public int compare(String edLevel1, String edLevel2)
         {
        	 return new Integer(getGrade(edLevel1)).compareTo(new Integer( getGrade(edLevel2))); 
         }
     }

	/**
	 * Run method that writes the report for a record
	 */
	public String run(String documentId, String record) 
		throws HarvestException {
		Document document;
		String asnId = null;
		Element rootElement = null;
		try {
			document = Dom4jUtils.getXmlDocument(record);
			rootElement =  document.getRootElement();
			
			List<Element>definedEducationLevels = rootElement.selectNodes("*[name()='dct:educationLevel']");
			
			
			// If there is no education levels no reason to do anything
			if(definedEducationLevels.size()==0)
				return null;
			List<String>definedGradeLevels = this.getGrades(definedEducationLevels);
			
			
			// This might be zero even though there might have been definedEducationLevels
			// since we ignore some education levels
			if(definedGradeLevels.size()==0)
				return null;

			List<Element>asnElements = rootElement.selectNodes(this.asnElementXpath);
			
			for(int gradeLevelPadding: GRADE_LEVEL_PADDING_DEF)
			{
				List<String>targetGradeLevels = this.getTargetGradeLevels(
						definedGradeLevels, gradeLevelPadding);
				
				
				List<String> errorAsns = new ArrayList<String>();
				for(Element element : asnElements)
				{
					asnId = element.getTextTrim();
					if(!asnId.contains(Config.ASN.ASN_BASE_URL))
						continue;
	
					// Adding educational levels
					List<String> asnEducationalLevels = this.asnHelper.getEducationalLevels(asnId);
					
					if(asnEducationalLevels==null || asnEducationalLevels.size()==0)
						continue;
	
					if(Collections.disjoint(asnEducationalLevels, targetGradeLevels))
						errorAsns.add(asnId);   
				}
				if(errorAsns.size()>0)
					this.createErroredAsnReport(documentId, record, errorAsns, definedGradeLevels, 
							targetGradeLevels, gradeLevelPadding);
			}
			return null;
			
		} 
		catch (Exception e) {
			e.printStackTrace();
			System.out.println(rootElement.asXML());
			this.addWarning(documentId, String.format(
				"Error trying to convert asn to education levels. Error %s at EducationLevelsVsAlignmentReport()", 
					  e.getMessage()));
		}
		return null;
	}
	
	

	private List<String> getTargetGradeLevels(
			List<String> definedGradeLevels, int gradeLevelPadding) {
		
		List<String> targetGradeLevels = new ArrayList<String>();
		targetGradeLevels.addAll(definedGradeLevels);
		Collections.sort(definedGradeLevels, new GradeLevelComparator());
		String prefix = "Grade ";
		String startGradeLevel = definedGradeLevels.get(0);
		String endGradeLevel = definedGradeLevels.get(definedGradeLevels.size()-1);
		
		int startGrade = getGrade(startGradeLevel);;
		
		for(int i=startGrade-1; i>=startGrade-gradeLevelPadding;i--)
		{
			if(i==0)
				targetGradeLevels.add("Kindergarten");
			else if(i==-1)
			{
				targetGradeLevels.add("Pre-Kindergarten");
				break;
			}
			else if(i<-1)
			{
				break;
			}
			else
				targetGradeLevels.add(prefix+i);
		}
		
		
		
		int endGrade = getGrade(endGradeLevel);
		
			
		for(int i=endGrade+1; i<=endGrade+gradeLevelPadding;i++)
		{
			if(i==0)
				targetGradeLevels.add("Kindergarten");
			else if(i>12)
			{
				break;
			}
			else
				targetGradeLevels.add(prefix+i);
		}
		
		
		return targetGradeLevels;
	}

	/**
	 * Add to the collection error report the list of erroredASN's that should be looked at
	 * @param documentId
	 * @param record
	 * @param errorAsns
	 * @param gradeLevels
	 * @throws Exception
	 */
	private void createErroredAsnReport(String documentId, String record, List<String> errorAsns,
			List<String> recordDefinedGradeLevels, 
			List<String> targetGradeLevels, int gradeLevelPadding) throws Exception {
		File report = new File(this.workspace.getWorkingFileDirectory(), 
				String.format(REPORT_FILE_NAME_FORMAT, gradeLevelPadding));
		
		Collections.sort(recordDefinedGradeLevels, new GradeLevelComparator());
		Collections.sort(targetGradeLevels, new GradeLevelComparator());
		
		// gets the validated record(before the transforms and normalizations)
		ResultsWrapper resultsWrapper = this.workspace.getRecordData(documentId);
		Iterator results = resultsWrapper.getResults();
    	Object[] recordData = (Object[])results.next();
    	String originalRecord = new String((byte[])recordData[1], Config.ENCODING);
    	resultsWrapper.clean();
    	
    	// Gets the metadatahandle for a document
		resultsWrapper = this.workspace.getRecordData(documentId);
    	results = resultsWrapper.getResults();
    	recordData = (Object[])results.next();
    	resultsWrapper.clean();
    	String metadatahandle = (String)recordData[5];
    	
		Document document = null;
		// If this is the record that had the error, we create teh file and place the
		// root element in there
		if(report.exists())
			document = Dom4jUtils.getXmlDocument(report);
		else
			document = Dom4jUtils.getXmlDocument(String.format("<collection name='%s' record_count='0'></collection>", 
					this.workspace.harvestRequest.getCollectionName().replace("'", "")));
		
		Element rootElement = document.getRootElement();
		
		Attribute recordCountAttr = rootElement.attribute("record_count");
		int newCount = Integer.parseInt(recordCountAttr.getText())+1;
		recordCountAttr.setText(Integer.toString(newCount));
    	
    	// Create and place the element in the xml
		Element recordElement = rootElement.addElement("record");
		recordElement.addAttribute("id", metadatahandle);
		Element educationLevelsElement = recordElement.addElement("education_levels");
		educationLevelsElement.addText(StringUtils.join(recordDefinedGradeLevels, ", "));
		
		Element targetEducationLevelsElement = recordElement.addElement("target_education_levels");
		targetEducationLevelsElement.addText(StringUtils.join(targetGradeLevels, ", "));
		
		Element errorAsnsElement  = recordElement.addElement("error_asns");
		
		for(String errorAsn: errorAsns )
		{
			Element asnElement = errorAsnsElement.addElement("asnId");
			String[] asnParts = errorAsn.split("/");
			asnElement.addText(errorAsn);
			
			String asnId = asnParts[asnParts.length-1];
			if(originalRecord.contains(asnId))
				asnElement.addAttribute("created_by_normalization", "false");
			else
				asnElement.addAttribute("created_by_normalization", "true");
		}
		
		// Write it back out
		Dom4jUtils.writePrettyDocToFile(document, report);
		
	}

	/**
	 * Gets the grades from a record, along with transforming a description 
	 * into actual grades. So it can be compared to the ASN's
	 * @param educationLevels
	 * @return
	 */
	private List<String> getGrades(List<Element> educationLevels) {
		HashSet<String> gradeLevels = new HashSet<String>();
		String prefix = "Grade ";
		for(Element educationLevelElement: educationLevels)
		{
			String educationLevel = educationLevelElement.getText();
			if(educationLevel.startsWith(prefix))
				gradeLevels.add(educationLevel);
			else if(educationLevel.equals("Pre-Kindergarten"))
				gradeLevels.add("Pre-Kindergarten");
			else if(educationLevel.equals("Kindergarten"))
				gradeLevels.add("Kindergarten");
			else if(educationLevel.equals("Elementary School"))
			{
				gradeLevels.add("Kindergarten");
				gradeLevels.add(prefix+"1");
				gradeLevels.add(prefix+"2");
				gradeLevels.add(prefix+"3");
				gradeLevels.add(prefix+"4");
				gradeLevels.add(prefix+"5");
				
			}
			else if(educationLevel.equals("Early Elementary"))
			{
				gradeLevels.add("Kindergarten");
				gradeLevels.add(prefix+"1");
				gradeLevels.add(prefix+"2");
				
			}
			else if(educationLevel.equals("Upper Elementary"))
			{
				gradeLevels.add(prefix+"3");
				gradeLevels.add(prefix+"4");
				gradeLevels.add(prefix+"5");	
			}
			else if(educationLevel.equals("Middle School"))
			{
				gradeLevels.add(prefix+"6");
				gradeLevels.add(prefix+"7");
				gradeLevels.add(prefix+"8");
				
			}
			else if(educationLevel.equals("High School"))
			{
				gradeLevels.add(prefix+"9");
				gradeLevels.add(prefix+"10");
				gradeLevels.add(prefix+"11");
				gradeLevels.add(prefix+"12");
			}
		}

		return new ArrayList<String>(gradeLevels);
	}
	
	@XmlElement(name = "asn-element-xpath")
	public String getAsnElementXpath() {
		return asnElementXpath;
	}

	public void setAsnElementXpath(String asnElementXpath) {
		this.asnElementXpath = asnElementXpath;
	}
	
}