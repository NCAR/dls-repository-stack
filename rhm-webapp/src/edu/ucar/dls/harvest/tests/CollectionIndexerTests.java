package edu.ucar.dls.harvest.tests;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;



import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.harvesters.OAI_PMHHarvester;
import edu.ucar.dls.harvest.tools.AbstractTestCase;
import edu.ucar.dls.harvest.workspaces.DBWorkspace;
import edu.ucar.dls.harvest.workspaces.ResultsWrapper;

public class CollectionIndexerTests extends AbstractTestCase
{	
	boolean testAllCollections = false;
	boolean testLarVersion = false;
	boolean testConformsTo = false;
	
	
	String [] pathsToRemove = {"*[name()='dc:language']", "*[name()='dc:subject']", "*[name()='dc:relation']", "*[name()='recordID']"};
	
	public CollectionIndexerTests(String name)
	{
		super(name);
	}
	
	
	public void testIndividualCollection() throws Exception
	{
		if(testAllCollections)
			return;
		//String setSpec = "AVC";
		String setSpec = "ncs-NSDL-COLLECTION-000-003-112-025";
		//setSpec = "439869";
		this.testCollections( CollectionTests.createCollectionInfo(setSpec));
	}
	public void testCollections() throws Exception
	{
		if(!testAllCollections)
			return;

		this.testCollections( CollectionTests.createCollectionInfo(null));
	}
	
	
	public HarvestRequest createHarvestRequest(String collectionId, String setSpec,  String baseUrl, String nativeFormat, 
			String metadataPrefix, String mdpHandle, String uuid, String runType, String[] sets)
	{
		HarvestRequest harvestRequest = new HarvestRequest();
		
		harvestRequest.setBaseUrl(baseUrl);
		harvestRequest.setNativeFormat(nativeFormat);
		harvestRequest.setMetadataPrefix(metadataPrefix);
		harvestRequest.setSetSpec(setSpec);
		harvestRequest.setMdpHandle(mdpHandle);
		harvestRequest.setUuid(uuid);
		harvestRequest.setRunType(runType);
		harvestRequest.setProtocol("oai");
		harvestRequest.setCollectionId(collectionId);
		harvestRequest.setCollection_sets(sets);
		return harvestRequest;
	}
	
	public void testCollections(List<HashMap<String, Object>> collectionInfoList) throws Exception
	{
		Collections.shuffle(collectionInfoList);
		Iterator collectionIterator = collectionInfoList.iterator();
		while(collectionIterator.hasNext())
		{
			HashMap collectionInfo = (HashMap)collectionIterator.next();
		
		    
			String nativeFormat = (String)collectionInfo.get("nativeFormat");
			String metadataPrefix = (String)collectionInfo.get("metadataPrefix");
			String devBaseUrl = null;
			String prodBaseUrl = null;
			String partnerIdPrefix = "";
			String partnerIdSuffix = "";
			String setSuffix = "";
			if(metadataPrefix.equals("lar") && !testLarVersion)
			{
				metadataPrefix = "nsdl_dc";
				partnerIdPrefix = "oai:nsdl.org:";
				// This is done because prior to 8/26/2013 the oai_dds transformed the lar 
				// on the fly using xsl. Now its been switched to where the oai_index has both
				// with normalization.
				devBaseUrl = "http://nsdldev.org/oai_dds/services/oai2-0";
				prodBaseUrl = "http://nsdl.org/nsdl_ddsi/services/oai2-0";
			}
			else
			{
				if(metadataPrefix.contains("nsdl_dc"))
					metadataPrefix = "nsdl_dc";
				else if(metadataPrefix.equals("lar"))
				{
					setSuffix = "-lar";
					partnerIdSuffix = "-LAR";
				}
				devBaseUrl = "http://nsdldev.org/oai_dds/services/oai2-0";
				prodBaseUrl = "http://nsdl.org/oai_dds/services/oai2-0";
			}
			String setSpec = (String)collectionInfo.get("setSpec");
			String mdpHandle = setSpec;
			String runType = "manual";
			String name = (String)collectionInfo.get("name");
			String devSetSpec = setSpec+setSuffix;
			String[] prodSets = {setSpec};
			String[] devSets = {devSetSpec};
			String collectionId = name;
			
			System.out.println("Running comparison for "+name);
			
			
			System.out.println(devSetSpec);
			HarvestRequest devHarvestRequest = createHarvestRequest(collectionId, devSetSpec,  devBaseUrl, nativeFormat, 
					metadataPrefix, mdpHandle, "dev", runType, devSets);
			HarvestRequest prodHarvestRequest = createHarvestRequest(collectionId, setSpec,  prodBaseUrl, nativeFormat, 
					metadataPrefix, mdpHandle, "prod", runType, prodSets);
			
	
			DBWorkspace devdbworkspace = new DBWorkspace();
			DBWorkspace proddbworkspace = new DBWorkspace();
			
			devdbworkspace.initialize(devHarvestRequest);
			proddbworkspace.initialize(prodHarvestRequest);
	
			
			OAI_PMHHarvester devharvester = new OAI_PMHHarvester();
			devharvester.initialize(devdbworkspace);
			devharvester.harvest();
			
			
			OAI_PMHHarvester prodharvester = new OAI_PMHHarvester();
			prodharvester.initialize(proddbworkspace);
			prodharvester.harvest();
			
			ResultsWrapper devResultsWrapper = devdbworkspace.getHarvestedRecords();
			Iterator devResults = devResultsWrapper.getResults();
			String devRecord=null;
	
			if(devResults==null)
				return;
			int devCount = 0;
			while (devResults.hasNext())
			{
				devCount++;
				Object[] devRecordData = (Object[])devResults.next();
				String partnerId = (String)devRecordData[0];
				try {
					devRecord = new String((byte[])devRecordData[1], Config.ENCODING);
				} catch (UnsupportedEncodingException e) {
					throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
							"Cannot convert byte string to encoding utf-8", e);
				}
				
				// Order doesn't matter here
				partnerId = partnerId.replace(partnerIdPrefix, "").replace(partnerIdSuffix, "");
				ResultsWrapper prodResultsWrapper1 = proddbworkspace.getRecordIdsByPartnerId(partnerId, Config.DatabaseOptions.ASCENDING);
				Iterator prodResults1 = prodResultsWrapper1.getResults();
				Object[] prodRecordData1 = (Object[])prodResults1.next();
				if(prodResults1==null || prodRecordData1==null)
				{
					System.out.println(String.format("Unable to find a match in prods instance of partnerId %s", partnerId));
					continue;
				}
				

				String recordId = ((Integer)prodRecordData1[0]).toString();
				
				ResultsWrapper prodResultsWrapper2 =proddbworkspace.getRecordData(recordId);
				
				Iterator prodResults2 = prodResultsWrapper2.getResults();
				Object[] prodRecordData2 = (Object[])prodResults2.next();
				
				String prodRecord = null;
				try {
					prodRecord = new String((byte[])prodRecordData2[1], Config.ENCODING);
				} catch (UnsupportedEncodingException e) {
					throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
							"Cannot convert byte string to encoding utf-8", e);
				}
				//System.out.println(prodRecord);
				prodResultsWrapper2.clean();
				prodResultsWrapper1.clean();
				
				this.xmlDocumentsEqual(devRecord, prodRecord, partnerId);
				
				
					
			}
			devResultsWrapper.clean();
			
			int prodCount = proddbworkspace.getRecordCount();
			System.out.println(prodCount);
			if(prodCount!=devCount)
			{
				System.out.println(String.format("\n\nThere is a discrepency in the prod(%s) vs dev(%s) counts." +
						"Make sure these descripencies make sense", prodCount, devCount));
			}
			
			devdbworkspace.clean();
			proddbworkspace.clean();
		}
	}
	

	private String xmlDocumentsEqual(String testXmlString, String compareXMLString, String partnerId) throws DocumentException
	{
		Document testDocument = Dom4jUtils.getXmlDocument(testXmlString);
		Document compareDocument = Dom4jUtils.getXmlDocument(compareXMLString);
		
		Attribute attr = testDocument.getRootElement().attribute("schemaVersion");
		if(attr!=null)
			testDocument.getRootElement().remove(attr);
		
		attr = compareDocument.getRootElement().attribute("schemaVersion");
		if(attr!=null)
			compareDocument.getRootElement().remove(attr);
		
		String conformsToXpathToRemove = null;
		if(testConformsTo)
			conformsToXpathToRemove = "*[name()!='dct:conformsTo']";
		else
			conformsToXpathToRemove = "*[name()='dct:conformsTo']";
		
		List<Node> devNodes = testDocument.getRootElement().selectNodes(conformsToXpathToRemove);
		for(Node node: devNodes)
			node.detach();
		
		List<Node> compareNodes = compareDocument.getRootElement().selectNodes(conformsToXpathToRemove);
		for(Node element: compareNodes)
			element.detach();
		
		for(String xpathToRemove: pathsToRemove)
		{
			devNodes = testDocument.getRootElement().selectNodes(xpathToRemove);
			compareNodes = compareDocument.getRootElement().selectNodes(xpathToRemove);
			for(Node node: devNodes)
			{
				node.detach();
			}
			for(Node node: compareNodes)
				node.detach();
		}
		
		try {
			assertDocumentsEqual(compareDocument, testDocument);
		} 
		catch (Error e) {
			System.out.println(String.format("\n\nError: dev xml for partnerid %s do not match"+
					"Reason: %s\n\n", partnerId, e.getMessage()));
			System.out.println("Info: test xml was\n "+Dom4jUtils.prettyPrint(testDocument.getRootElement()));
			System.out.println("Info: compare xml was\n "+Dom4jUtils.prettyPrint(compareDocument.getRootElement()));
		} catch (Exception e) {
			System.out.println(String.format("\n\nError: dev xml for partnerid %s do not match"+
					"Reason: %s\n\n", partnerId, e.getMessage()));
			System.out.println("Info: test xml was\n "+Dom4jUtils.prettyPrint(testDocument.getRootElement()));
			System.out.println("Info: compare xml was\n "+Dom4jUtils.prettyPrint(compareDocument.getRootElement()));
		}
		
		
		return null;
		
	}
	

}