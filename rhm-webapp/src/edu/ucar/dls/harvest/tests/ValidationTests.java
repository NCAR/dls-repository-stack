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

import org.apache.commons.io.FileUtils;
import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.DocumentException;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.ingestors.Ingestor;
import edu.ucar.dls.harvest.processors.Processor;
import edu.ucar.dls.harvest.processors.ProcessorIssue;
import edu.ucar.dls.harvest.processors.harvest.DedupRecords;
import edu.ucar.dls.harvest.processors.harvest_files.HarvestFileProcessor;
import edu.ucar.dls.harvest.processors.harvest_files.OAISchemaValidator;
import edu.ucar.dls.harvest.processors.record.EncodedCharsTransform;
import edu.ucar.dls.harvest.processors.record.RecordProcessor;
import edu.ucar.dls.harvest.processors.record.RecordSchemaValidator;
import edu.ucar.dls.harvest.processors.record.RestrictElementCount;
import edu.ucar.dls.harvest.processors.record.URINormalizer;
import edu.ucar.dls.harvest.workspaces.DBWorkspace;
import edu.ucar.dls.harvest.workspaces.Workspace;


public class ValidationTests extends HarvestTestCase
{
	private File testFileDirectory = null;
	public ValidationTests(String name)
	{
		super(name, "validation");
	
	}
	
	/*public void testRecordSchemaValidator() throws IOException, HarvestException
	{
		File testXml = this.getTestFile("invalid_record_schema.xml");
		RecordProcessor validator = new RecordSchemaValidator();
		validator.initialize();
		boolean exceptionThrown = false;
		try {
			validator.run("invalid_test", FileUtils.readFileToString(testXml, "UTF-8"));
		} catch (HarvestException e) {
			// TODO Auto-generated catch block
			exceptionThrown = true;
		}
		assertEquals("Invalid record schema should have been caught by validator.", 
				1, validator.getErrorCount());
		assertEquals("Invalid record schema should not throw an exception.", false, exceptionThrown);
	
		validator.clear();
		testXml = this.getTestFile("valid_record_schema.xml");
		exceptionThrown = false;
		try {
			validator.run("valid_test", FileUtils.readFileToString(testXml, "UTF-8"));
		} catch (HarvestException e) {
			// TODO Auto-generated catch block
			exceptionThrown = true;
		}
		assertEquals("Valid record schema should have no errors attached.", 
				0, validator.getErrorCount());
		assertEquals("Valid record schema never throw an exception.", false, exceptionThrown);
	}
	
	public void testOAISchemaValidator() throws IOException, HarvestException
	{
		File testXml = this.getTestFile("valid_oai_schema.xml");
		HarvestFileProcessor validator = new OAISchemaValidator();
		validator.initialize();
		validator.run(testXml);
		assertEquals("Valid record schema should have passed test.", 0, validator.getErrorCount());
	
		validator.clear();
		//This file is bad because ListRecords is changed to ListRecord
		boolean exceptionThrown = false;
		testXml = this.getTestFile("oai_outer_schema_invalid.xml");
		try
		{
			validator.run(testXml);
		}
		catch (HarvestException e)
		{
			exceptionThrown=true;
		}
		//assertEquals("Invalid oai schema should error out.", 1, validator.getErrorCount());
		assertEquals("Invalid oai schema should error out by throwing an exception.", true, exceptionThrown);
		
		validator.clear();
		//This file is inavlid because the first metadata is missing schema_location
		testXml = this.getTestFile("oai_schema_invalid_metadata.xml");
		validator.run(testXml);
		assertEquals("Invalid metadata record schema should not error out."+
					"Those validations are done later.", 0, validator.getErrorCount());
		
	}
	*/
	public void testURLValidator() throws IOException, HarvestException
	{
		String [] xpaths = {"*[name()='usageDataResourceURL'][contains(.,'http:')]"};
		File testXml = this.getTestFile("valid_comm_para_record.xml");
		
		URINormalizer uv = new URINormalizer();
		uv.setUriXPaths(xpaths);
		uv.initialize(null);
		uv.setURIRequired(true);
		uv.run("valid_comm_para_record.xml", FileUtils.readFileToString(testXml, "UTF-8"));
		assertEquals("Valid record schema for Comm para should have passed test.", 0, uv.getErrorCount());		
		
		xpaths = new String[]{"dc:identifier[@xsi:type='dct:URI']"};
		testXml = this.getTestFile("valid_nsdl_dc_record.xml");
		uv.setUriXPaths(xpaths);
		uv.run("valid_comm_para_record.xml", FileUtils.readFileToString(testXml, "UTF-8"));
		assertEquals("Valid record schema for normalized nsdl_dc should have passed test.", 0, uv.getErrorCount());		

		
		uv.clear();
		xpaths = new String[]{"*[name()='dc:identifier'][contains(.,'http:')]']"};
		testXml = this.getTestFile("valid_nsdl_dc_record_w_url_weirdness.xml");
		uv.setUriXPaths(xpaths);
		uv.run("valid_nsdl_dc_record_w_url_weirdness", FileUtils.readFileToString(testXml, "UTF-8"));
		assertEquals("Valid record schema for normalized nsdl_dc should have passed test.", 0, uv.getErrorCount());		
	}
	
	public void testURLOrValidator() throws IOException, HarvestException
	{
		String [] xpaths = {"*[name()='usageDataResourceURL'][contains(.,'http:') or contains(.,'https:') or contains(.,'ftp:')]"};
		File testXml = this.getTestFile("valid_record_with_http_ident.xml");
		
		URINormalizer uv = new URINormalizer();
		uv.setUriXPaths(xpaths);
		uv.initialize(null);
		uv.setURIRequired(true);
		uv.run("valid_record_with_http_ident.xml", FileUtils.readFileToString(testXml, "UTF-8"));
		assertEquals("Valid record schema for http.", 0, uv.getErrorCount());
		
		uv.clear();
		testXml = this.getTestFile("valid_record_with_https_ident.xml");
		uv.run("valid_record_with_http_ident.xml", FileUtils.readFileToString(testXml, "UTF-8"));
		assertEquals("Valid record schema for https.", 0, uv.getErrorCount());
		
		uv.clear();
		testXml = this.getTestFile("valid_record_with_ftp_ident.xml");
		uv.run("valid_record_with_ftp_ident.xml", FileUtils.readFileToString(testXml, "UTF-8"));
		assertEquals("Valid record schema for https.", 0, uv.getErrorCount());
		
		uv.clear();
		testXml = this.getTestFile("valid_record_with_invalid_ident.xml");
		uv.run("valid_record_with_invalid_ident.xml", FileUtils.readFileToString(testXml, "UTF-8"));
		assertEquals("Valid record schema for invalid.", 1, uv.getErrorCount());

	}
	/*
	
	
	public void testDedupProcessor() throws IOException, HarvestException
	{
		HarvestRequest hr = new HarvestRequest();
		hr.setUuid("TestForDedup");
		Workspace workspace = new DBWorkspace();
		workspace.initialize(hr);
		workspace.populateRecord("partner1", "does not matter");
		workspace.populateRecord("partner1", "does not matter");
		workspace.populateRecord("partner1", "does not matter");
		workspace.populateRecord("partner2", "does not matter");
		workspace.populateRecord("partner2", "does not matter");
		workspace.populateRecord("partner2", "does not matter");
		workspace.populateRecord("partner3", "does not matter");
		
		Processor processor = new DedupRecords();
		processor.run(workspace);
	
		assertEquals("The correct amount of records should have been marked as invalid", 
				4, workspace.getInvalidRecordCount());
		assertEquals("The correct amount of warnings should have been created", 
				2, processor.getWarningCount());
	
		processor.clear();
		hr = new HarvestRequest();
		hr.setUuid("TestForDedup2");
		workspace = new DBWorkspace();
		workspace.initialize(hr);
		workspace.populateRecord("partner1", "does not matter");
		workspace.populateRecord("partner2", "does not matter");
		workspace.populateRecord("partner3", "does not matter");
		processor.run(workspace);
		assertEquals("The correct amount of warnings should have been created", 
				0, processor.getWarningCount());
	}
	
	
	public void testHandlesValidator() throws IOException, HarvestException
	{
		RecordProcessor processor = new HandlesValidator();
		processor.initialize();
		
		HarvestRequest hr = new HarvestRequest();
		hr.setSetSpec("2802835");
		hr.setUuid("TestForHandlesValidator");
		Workspace workspace = new DBWorkspace();
		workspace.initialize(hr);
		workspace.populateRecord("oai:dlese.org:NASA-Edmall-975", "does not matter");
		workspace.populateRecord("oai:dlese.org:NASA-Edmall-923", "does not matter");
		
		workspace.insertURI("1", "http://serc.carleton.edu/sp/library/direct_measurement_video/examples/example11.html");
		workspace.insertURI("2", "http://www.windows.ucar.edu/cgi-bin/tour.cgi?link=/people/ren_epoch/copernicus.html");

		
		processor.run("1", "a", workspace);
		processor.run("2", "a", workspace);
		
		hr.setSetSpec("ncs-NSDL-COLLECTION-000-003-112-025");
		workspace.populateRecord("serc-pia-70996", "does not matter");
		
				workspace.insertURI("3", "http://www.windows.ucar.edu/cgi-bin/tour.cgi?link=/people/ren_epoch/copernicus.html");

		processor.run("3", "a", workspace);
		assertEquals("The correct amount of errors should have been created", 
				2, processor.getErrorCount());
	
	}
	*/
	
	public void testRestrictElementCount() throws HarvestException, IOException, DocumentException
	{
		String [] xpaths = {"*[name()='dc:description']", "*[name()='description']"};
		File testXml = this.getTestFile("restrictElementCountSource.xml");
		
		RestrictElementCount rp = new RestrictElementCount();
		rp.setXpaths(xpaths);
		rp.setPriority("first");
		rp.setElementCount(2);
		rp.setIncludeWarning(true);
		rp.initialize(null);

		String result = rp.run("restrictElementCountSource.xml", FileUtils.readFileToString(testXml, "UTF-8"));
		assertEquals("warning was included.", 1, rp.getWarningCount());
		
		File resultXml = this.getTestFile("restrictElementCountResult.xml");
		assertEquals("Record is transformed correctly.", Dom4jUtils.prettyPrint(
				Dom4jUtils.getXmlDocument(resultXml, Config.ENCODING)), 
				Dom4jUtils.prettyPrint(
						Dom4jUtils.getXmlDocument(result)));
		
		rp.clear();
		rp.setThrowError(true);
		rp.run("restrictElementCountSource.xml", FileUtils.readFileToString(testXml, "UTF-8"));
		assertEquals("warning was included.", 1, rp.getErrorCount());
		
		
	}
	
	public void testProblemsWithXerces() 
	{
		Ingestor ingestor;
		try {
			HarvestRequest hr = new HarvestRequest();
			hr.setUuid("TestForDedup");
			Workspace workspace = new DBWorkspace();
			System.out.println("testing");
			File testXml = this.getTestFile("record_with_long_description_and_url2.xml");
			RecordSchemaValidator validator = new RecordSchemaValidator();
			
			validator.initialize(workspace);
			String x = FileUtils.readFileToString(testXml, "UTF-8");
			validator.run("55", x);
			validator.run("55", x);
			validator.run("55", x);
			validator.run("55", x);
			//assertEquals("Valid record schema should have passed test.", 0, validator.getErrorCount());	
			


			
			Thread aThread = new Thread(new ThreadTest(testXml));
			aThread.start();
		} catch (HarvestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
