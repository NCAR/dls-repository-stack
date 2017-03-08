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
package edu.ucar.dls.harvest.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentException;

import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.processors.record.ASNConversion;
import edu.ucar.dls.harvest.processors.record.TransformViaGroupFiles;
import edu.ucar.dls.harvest.resources.ASNHelper;

public class ASNTests extends HarvestTestCase
{	
	public ASNTests(String name) {
		super(name, "transform");
		// TODO Auto-generated constructor stub
	}
	
	public void testFetchASNIdByStmtNotation() throws Exception
	{
		ASNHelper asnHelper = new ASNHelper();
		String result = asnHelper.fetchASNIdByStmtNotation("CCSS.Math.Content.HSN-CN.C.7");
		TestCase.assertNotNull(result);
		TestCase.assertEquals(
				"http://asn.jesandco.org/resources/S114361B", 
				result);
		
		result = asnHelper.fetchASNIdByStmtNotation("CCSS.Math.Content.5.MD.C.5a");
		TestCase.assertNotNull(result);
		TestCase.assertEquals(
				"http://asn.jesandco.org/resources/S11436A2", 
				result);
		
		result = asnHelper.fetchASNIdByStmtNotation("this.is.a.bogus.find");
		TestCase.assertNull(result);
	}
	
	public void testFetchASNIdByDescription() throws Exception
	{
		ASNHelper asnHelper = new ASNHelper();
		String result = asnHelper.fetchASNIdByDescription("Find the volume of a right rectangular prism with whole-number side lengths by packing it with unit cubes, and show that the volume is the same as would be found by multiplying the edge lengths, equivalently by multiplying the height by the area of the base. Represent threefold whole-number products as volumes, e.g., to represent the associative property of multiplication.");
		TestCase.assertNotNull(result);
		TestCase.assertEquals(
				"http://asn.jesandco.org/resources/S11436A2", 
				result);
		
		// Test a use case that has quotes 
		result = asnHelper.fetchASNIdByDescription("During any transformation of energy, there is inevitably some dissipation of energy into the environment. In this practical sense, energy gets \"used up,\" even though it is still around somewhere.");
		TestCase.assertNotNull(result);
		TestCase.assertEquals(
				"http://asn.jesandco.org/resources/S2366374", 
				result);
		
		result = asnHelper.fetchASNIdByDescription("this is a bogus description");
		TestCase.assertNull(result);
	}
	
	public void testFetchASNIdByExternalURL() throws Exception
	{
		ASNHelper asnHelper = new ASNHelper();
		String result = asnHelper.fetchASNIdExternalURL("http://corestandards.org/Math/Content/HSS/CP/B/9");
		TestCase.assertNotNull(result);
		TestCase.assertEquals(
				"http://asn.jesandco.org/resources/S11435B6", 
				result);
		
		result = asnHelper.fetchASNIdExternalURL("this is a url");
		TestCase.assertNull(result);
	}
	
	public void testGetEducationLevels() throws Exception
	{
		ASNHelper asnHelper = new ASNHelper();
		List<String> result = asnHelper.getEducationalLevels("http://asn.jesandco.org/resources/S11435B6");
		TestCase.assertNotNull(result);
		
		
		List<String> expectedResult = new ArrayList<String>();
		expectedResult.add("Grade 9");
		expectedResult.add("Grade 10");
		expectedResult.add("Grade 11");
		expectedResult.add("Grade 12");
		
		TestCase.assertEquals(expectedResult, result);
		
		// testing bogus asn id
		result = asnHelper.getEducationalLevels("http://www.gmail.com");
		TestCase.assertNull(result);
		
		
		// testing kindergarten
		result = asnHelper.getEducationalLevels("http://asn.jesandco.org/resources/S1143417");
		expectedResult.clear();
		expectedResult.add("Kindergarten");
		TestCase.assertNotNull(result);
		TestCase.assertEquals(expectedResult,result);	
	}
	
	public void testGetSubject() throws Exception
	{
		ASNHelper asnHelper = new ASNHelper();
		String result = asnHelper.getSubject("http://asn.jesandco.org/resources/S11435B6");
		TestCase.assertNotNull(result);
		TestCase.assertEquals("Math", result);
		
		// testing bogus asn id
		result = asnHelper.getSubject("http://www.gmail.com");
		TestCase.assertNull(result);
		
	}
	
	
	public void testTransformComplex() throws DocumentException
	{
		try {

			File testXml = this.getTestFile("asn_lots.xml");
			String originalRecord =  FileUtils.readFileToString(testXml, "UTF-8");
			
			ASNConversion transform1 = new ASNConversion();
			transform1.setElementXpath("*[name()='dct:conformsTo']");
			transform1.initialize(null);
			
			
			String newRecord = transform1.run("a", originalRecord);
			
			TransformViaGroupFiles transform2 = new TransformViaGroupFiles();
			transform2.setGroupFileURIString("http://ns.nsdl.org/ncs/ddsws/1-1/groupsNormal/vocab_selections.xml");
			transform2.initialize(null);
			newRecord = transform2.run("TestDocId", newRecord);

		} catch (HarvestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
	
	}
	
}
