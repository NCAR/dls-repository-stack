package edu.ucar.dls.harvest.tests;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.DocumentException;

import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.processors.record.ASNConversion;
import edu.ucar.dls.harvest.processors.record.AddNSDLElementsFromASN;
import edu.ucar.dls.harvest.processors.record.EncodedCharsTransform;
import edu.ucar.dls.harvest.processors.record.RecordProcessor;
import edu.ucar.dls.harvest.processors.record.RemoveDuplicateElementsTransform;
import edu.ucar.dls.harvest.processors.record.RemoveEmptyElementsTransform;
import edu.ucar.dls.harvest.processors.record.SubscriptTransform;
import edu.ucar.dls.harvest.processors.record.SuperscriptTransform;
import edu.ucar.dls.harvest.processors.record.TransformViaGroupFiles;
import edu.ucar.dls.harvest.processors.record.XSLTransform;


public class TransformTests extends HarvestTestCase
{
	public TransformTests(String name) {
		super(name, "transform");
		// TODO Auto-generated constructor stub
	}
	
	/*public void testEncodedCharsValidator() throws IOException, HarvestException
	{
		RecordProcessor validator = new EncodedCharsTransform();
		
		String stringFormat = "<xml><start1>%s</start1><start2>%s</start2>%slipt;</xml>";
		
		String withChanges = String.format(stringFormat, "&amp;gt;", "&amp;lt;", "&amp;amp;");
		String returnedString = validator.run("change_test", withChanges, null);
		assertNotNull("String should have been modified to replace encoded chars", returnedString);
		
		
		String whatItShouldBe = String.format(stringFormat, "&gt;", "&lt;", "&amp;");
		assertEquals("String should have been modified with &amp; " +
				"replaced when preceding an encoded char", 
				returnedString, whatItShouldBe);
		
		
		// test no changes
		String noChanges = String.format(stringFormat, "&gt;", "&", "&typ;");
		returnedString = validator.run("change_test", noChanges, null);
		
		assertNull("String should not have been modified to replace encoded chars", returnedString);
		
		
	}
	
	public void testXSLTransform()
	{
		try {

			//System.out.println(new File(Config.xlsPath, "normalized_nsdl_dc.xsl").toURI()))
			XSLTransform transform = new XSLTransform();
			
			String rootPath = System.getProperty("user.dir");
			
			transform.setXslURIString(new File(
					String.format("%s/web/WEB-INF/xsl/", 
							rootPath), "normalized_nsdl_dc.xsl").toURI().toString());
			transform.initialize();
			//DocumentAction transform = new XSLTransform(new URI("http://ns.nsdl.org/transforms/xsl/crsd105ToDbi107.xsl"));
			File testXml = this.getTestFile("valid_nsdl_dc_record.xml");
			System.out.println(
	transform.run("TestDocId", FileUtils.readFileToString(testXml, "UTF-8"), null));
		} catch (HarvestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
		
	} 
	
	public void testRemoveEmptyElementsTransform()
	{
		try {

			//System.out.println(new File(Config.xlsPath, "normalized_nsdl_dc.xsl").toURI()))
			RecordProcessor transform = new RemoveEmptyElementsTransform();
			//DocumentAction transform = new XSLTransform(new URI("http://ns.nsdl.org/transforms/xsl/crsd105ToDbi107.xsl"));
			File testXml = this.getTestFile("valid_nsdl_dc_record_w_empty_element.xml");
			
			String originalRecord =  FileUtils.readFileToString(testXml, "UTF-8");
			String newRecord = transform.run("TestDocId",originalRecord, null);
			
			String resultExpected = originalRecord.replaceAll("<dc:creator></dc:creator>", "");
			resultExpected = resultExpected.replaceAll("<dc:creator/>", "");
			
			assertEquals(resultExpected, newRecord);
		} catch (HarvestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
	
	}*/
	
	/*public void testRemoveDuplicateElementsTransform() throws DocumentException
	{
		try {

			//System.out.println(new File(Config.xlsPath, "normalized_nsdl_dc.xsl").toURI()))
			RemoveDuplicateElementsTransform transform = new RemoveDuplicateElementsTransform();
			transform.setIgnoreAttributes(false);
			transform.initialize(null);
			//DocumentAction transform = new XSLTransform(new URI("http://ns.nsdl.org/transforms/xsl/crsd105ToDbi107.xsl"));
			File testXml = this.getTestFile("valid_nsdl_dc_record_w_duplicate_element.xml");
			File resultAfterTransform = this.getTestFile("transform_valid_record_w_duplicate_element_w_attrs.xml");

			String originalRecord =  FileUtils.readFileToString(testXml, "UTF-8");
			String newRecord = transform.run("TestDocId", originalRecord);
			
			String expectedRecord = Dom4jUtils.getXmlDocument(
					resultAfterTransform, "UTF-8").getRootElement().asXML();
			
			assertEquals(fixXML(expectedRecord), fixXML(newRecord));
			
			// Now test what happens when attributes are ignored
			
			resultAfterTransform = this.getTestFile("transform_valid_record_w_duplicate_element_w-o_attrs.xml");
			expectedRecord = Dom4jUtils.getXmlDocument(
					resultAfterTransform, "UTF-8").getRootElement().asXML();
			
			transform = new RemoveDuplicateElementsTransform();
			transform.setIgnoreAttributes(true);
			newRecord = transform.run("TestDocId", originalRecord);
			assertEquals(fixXML(expectedRecord), fixXML(newRecord));
			
		} catch (HarvestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
	
	}*/
	
	public void testTransformViaGroupFile() throws DocumentException
	{
		try {

			//System.out.println(new File(Config.xlsPath, "normalized_nsdl_dc.xsl").toURI()))
			TransformViaGroupFiles transform = new TransformViaGroupFiles();
			transform.setGroupFileURIString("http://ns.nsdl.org/ncs/ddsws/1-1/groupsNormal/vocab_selections.xml");
			transform.initialize(null);
			//DocumentAction transform = new XSLTransform(new URI("http://ns.nsdl.org/transforms/xsl/crsd105ToDbi107.xsl"));
			File testXml = this.getTestFile("transformViaGroupFiles.xml");
			String originalRecord =  FileUtils.readFileToString(testXml, "UTF-8");
			
			System.out.println(new Date().toString());
			String newRecord = transform.run("TestDocId", originalRecord);
			System.out.println(new Date().toString());
			
			
		} catch (HarvestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
	
	}
	
	
	/*public void testSubscriptTransform() throws IOException, HarvestException
	{
		SubscriptTransform transform = new SubscriptTransform();
		
		String [] regExs = {"&lt;sub&gt;(.+?)&lt;/sub&gt;"};
		transform.setSubscriptRegExpressions(regExs);
		transform.initialize();
		
		String stringFormat = "<xml><start1>this is the chemical equivelenve of %s. That cannot be " +
						"rigtht should be %s. Thats more like it</start1></xml>";
		
		
		String withChanges = String.format(stringFormat, "&lt;sub&gt;210&lt;/sub&gt;", "&lt;sub&gt;2&lt;/sub&gt;");
		String returnedString = transform.run("change_test", withChanges, null);
		assertNotNull("String should have been modified to replace encoded chars", returnedString);
		
		
		String whatItShouldBe = String.format(stringFormat, "&#8322;&#8321;&#8320;", "&#8322;");
		assertEquals("String should have been modified ",
				returnedString, whatItShouldBe);
		
		
		// test no changes
		String noChanges = String.format(stringFormat, "this is something <sub>no</sub>", "this is something <sub>no</sub>");
		returnedString = transform.run("change_test", noChanges, null);
		
		assertNull("String should not have been modified to replace encoded chars", returnedString);
		String completeList = "&lt;sub&gt; 0123456789+-=()aeoiruvxz&lt;/sub&gt;";
		String transformation = " &#8320;&#8321;&#8322;&#8323;&#8324;&#8325;&#8326;&#8327;&#8328;&#8329;"+
					"&#8330;&#8331;&#8332;&#8333;&#8334;&#8336;&#8337;&#8338;&#7522;&#7523;&#7524;&#7525;&#8339;z";
		returnedString = transform.run("change_test", completeList, null);
		assertEquals("String should not have been modified to replace encoded chars", transformation,returnedString );
	}
	
	public void testSuperTransform() throws IOException, HarvestException
	{
		SuperscriptTransform transform = new SuperscriptTransform();
		
		String [] regExs = {"&lt;sup&gt;(.+?)&lt;/sup&gt;"};
		transform.setSuperscriptRegExpressions(regExs);
		transform.initialize();
		
		String stringFormat = "<xml><start1>this is the chemical equivelenve of %s. That cannot be " +
						"rigtht should be %s. Thats more like it</start1></xml>";
		
				
		
		// test no changes
		String noChanges = String.format(stringFormat, "this is something <sup>no</sup>", "this is something <sup>no</sup>");
		String returnedString = transform.run("change_test", noChanges, null);
		
		assertNull("String should not have been modified to replace encoded chars", returnedString);
		
		
		String completeList = "&lt;sup&gt; 0123456789+-=()inz&lt;/sup&gt;";
		String transformation = " &#8304;&#185;&#178;&#179;&#8308;&#8309;&#8310;&#8311;&#8312;&#8313;&#8314;&#8315;&#8316;&#8317;&#8318;&#8305;&#8319;z";
		returnedString = transform.run("change_test", completeList, null);
		assertEquals("String should not have been modified to replace encoded chars", transformation,returnedString );
	}
	
	public void testASNConversion() throws IOException, HarvestException, DocumentException
	{
		ASNConversion transform = new ASNConversion();
		transform.setElementXpath("*[name()='dct:conformsTo']");
		transform.initialize(null);
		File testXml = this.getTestFile("non_asn_conforms_to_element.xml");
		
		String beforeTransformFile =  FileUtils.readFileToString(testXml, "UTF-8");
		String newRecord = transform.run("TestDocId",beforeTransformFile);
		assertEquals(transform.getErrorCount(), 0);
		File correctResultFile = this.getTestFile("transformed_non_asn_conforms_to_element.xml");
		assertEquals(Dom4jUtils.getXmlDocument(correctResultFile).getRootElement().asXML(), newRecord);	
	}
	
	
	public void testAddNSDLElementsFromASN() throws IOException, HarvestException, DocumentException
	{
		AddNSDLElementsFromASN transform = new AddNSDLElementsFromASN();
		transform.setAsnElementXpath("*[name()='dct:conformsTo']");
		transform.initialize(null);
		File testXml = this.getTestFile("asn_conforms_to_before_additions.xml");
		
		String beforeTransformFile =  FileUtils.readFileToString(testXml, "UTF-8");
		String newRecord = transform.run("TestDocId",beforeTransformFile);

		assertEquals(transform.getErrorCount(), 0);
		File correctResultFile = this.getTestFile("transformed_asn_conforms_to_before_additions.xml");
		assertEquals(Dom4jUtils.getXmlDocument(correctResultFile).getRootElement().asXML(), newRecord);	
	}
	
	*/
	
	public static String fixXML(String xmlRecord)
	{
		return xmlRecord.replace("\n", "").replace("\r", "");
	}
	
}