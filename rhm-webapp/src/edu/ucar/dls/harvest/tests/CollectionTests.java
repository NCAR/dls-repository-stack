package edu.ucar.dls.harvest.tests;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;



import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.repository.DBRepository;
import edu.ucar.dls.harvest.resources.DBConnector;
import edu.ucar.dls.harvest.tools.AbstractTestCase;
import edu.ucar.dls.harvest.workspaces.DBWorkspace;

public class CollectionTests extends AbstractTestCase
{	
	boolean TEST_HANDLES = false;
	boolean testAllCollections = false;
	public CollectionTests(String name)
	{
		super(name);
	}
	
	public void testIndividualCollection() throws SQLException, DocumentException, HarvestException
	{
		if(testAllCollections)
			return;
		//String setSpec = "ncs-NSDL-COLLECTION-000-003-112-098";
		String setSpec = "AVC";
		this.runCollections( this.createCollectionInfo(setSpec));
	}
	public void testCollections() throws SQLException, DocumentException, HarvestException
	{
		if(!testAllCollections)
			return;

		this.runCollections( this.createCollectionInfo(null));
	}
	
	private void runCollections(List<HashMap<String, Object>> collectionInfoList) throws SQLException, DocumentException, HarvestException
	{
		Collections.shuffle(collectionInfoList);
		Iterator collectionIterator = collectionInfoList.iterator();
		while(collectionIterator.hasNext())
		{
			
			
			HashMap collectionInfo = (HashMap)collectionIterator.next();
			
			String setSpec = (String)collectionInfo.get("setSpec");
			String name = (String)collectionInfo.get("name");
			String uuid = setSpec;
			System.out.println("");
			System.out.println(String.format("Running collection %s-%s." , setSpec, name));
			if (this.hasBeenRan(uuid))
			{
				System.out.println("Already ran, remove the temp table as setSepc for a re-run.");
				continue;
			}
			HarvestRequest harvestRequest = new HarvestRequest();
			harvestRequest.setBaseUrl((String)collectionInfo.get("baseURL"));
			
			String prefix = (String)collectionInfo.get("metadataPrefix");
			harvestRequest.setMetadataPrefix(prefix);
			harvestRequest.setSetSpec(setSpec);
			harvestRequest.setUuid(uuid);
			if(TEST_HANDLES)
				harvestRequest.setRunType(HarvestRequest.FULL_HARVEST_RUN_TYPE);
			else
				harvestRequest.setRunType(HarvestRequest.FULL_HARVEST_TEST_RUN_TYPE);
			harvestRequest.setCollection_sets((String[])collectionInfo.get("collection_sets"));
			
			// fixes ones that metadataprefix equals nsdl_dcV1.2 etc
			if(prefix.contains("nsdl_dc"))
				prefix = "nsdl_dc";
			harvestRequest.setNativeFormat(prefix);
			harvestRequest.setProtocol("oai");
			harvestRequest.setMdpHandle(setSpec);
			this.cleanTestRepos(harvestRequest);
			
			
			Thread aThread = new Thread(harvestRequest);
			aThread.start();
			try {
				aThread.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("going for it");
			
			this.isDataComparable(harvestRequest);
			
			
			System.out.println("Finished ingesting. Moving on, hit stop in the next 5 seconds before the next one starts."+
					"Do not stop half way in between.");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static List<HashMap<String, Object>> createCollectionInfo(String setSpec)
	{
		List<HashMap<String, Object>> collectionInfoList = new ArrayList<HashMap<String, Object>>();
		String collectionsURL = "http://ncs.nsdl.org/mgr/services/ddsws1-1?verb=Search&xmlFormat=ncs_collect&dcsStatus=In+Progress&dcsStatus=Done&q=ky:1201216476279&n=300&s=0";
		
		
		try {
			
			Document collectionsDocumentDoc = Dom4jUtils.localizeXml(
					Dom4jUtils.getXmlDocument(new URL(collectionsURL)));
			List nodes = collectionsDocumentDoc.selectNodes(
				"/DDSWebService/Search/results/record/metadata/record/"+
					"collection/ingest/oai");
			Iterator nodesIterator = nodes.iterator();
			while (nodesIterator.hasNext())
			{
				HashMap collectionInfoMap = new HashMap();
				Element oaiInfoNode = (Element)nodesIterator.next();
				
				Element collectionRecord = (Element)oaiInfoNode.getParent().getParent();
				if(setSpec!=null && !collectionRecord.valueOf("setSpec").equals(setSpec))
					continue;

				collectionInfoMap.put("setSpec", collectionRecord.valueOf("setSpec"));
				collectionInfoMap.put("name", collectionRecord.valueOf("altText"));
				collectionInfoMap.put("metadataPrefix", oaiInfoNode.attributeValue("metadataPrefix"));
				collectionInfoMap.put("baseURL", oaiInfoNode.attributeValue("baseURL"));
				collectionInfoMap.put("nativeFormat", oaiInfoNode.attributeValue("libraryFormat"));
				
				List<String> sets = new ArrayList<String>();
				
				List setElements = oaiInfoNode.selectNodes("set");
				Iterator<Element> setElementsIterator = setElements.iterator();
				while(setElementsIterator.hasNext())
				{
					sets.add(setElementsIterator.next().attributeValue("setSpec"));
				}
				collectionInfoMap.put("collection_sets", (String[]) sets.toArray(new String[sets.size()]));
				collectionInfoList.add(collectionInfoMap);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return collectionInfoList;
			
	}
	
	private boolean hasBeenRan(String uuid) throws HarvestException
	{
		String tableName = String.format(DBWorkspace.TEMP_INGEST_FORMAT_STRING, 
				uuid).replace('-', '_').replace('.', '_');
		boolean tableExists = false;
		ResultSet tables = null;
		Connection connection = null;
		
		try {
			connection=DBConnector.createWorkspaceConnection();
			DatabaseMetaData dbm = connection.getMetaData();
			System.out.println(tableName);
			tables = dbm.getTables(null, null, tableName, null);
			if (tables.next()) {
			  tableExists = true;
			}
			else
			{
				 tableExists = false;
			}
			tables.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try {
				tables.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return tableExists;
	}
	
	private void cleanTestRepos(HarvestRequest harvestRequest) throws HarvestException
	{
		DBWorkspace workspace = new DBWorkspace();
		workspace.initialize(harvestRequest);
		DBRepository testRepository = new DBRepository();
		testRepository.initialize(workspace,  false);
		
		Connection dbConnection;
		try {
			testRepository.removeMetadataBySessionId( 
				harvestRequest.getUuid());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			workspace.clean();
			testRepository.clean();
		}
		testRepository.clean();
		
		
		
	}
	
	
	private static final String GET_DATA_SCHEMA_STMT = 
		"SELECT metadatahandle, partnerid, agent, %s, native_xml " +
		"FROM metadata " +
		"WHERE setSpec=? " +
		"ORDER BY partnerid";
	
	private static final String TEST_RESOURCE_GET_DATA_SCHEMA_STMT = 
			"SELECT m.metadatahandle, m.partnerid, m.agent, m.%s , m.native_xml," +
						" r.resourcehandle, r.URL " +
			"FROM metadata as m, resource as r " +
			"WHERE m.setSpec=? and m.metadatahandle=r.metadatahandle and " +
					"m.sessionid=r.sessionid and r.setSpec=m.setSpec " +
			"ORDER BY m.partnerid";

	private static final String TEST_TARGET_XML_COL = "target_xml";
	private static final String COMPARE_TARGET_XML_COL = "target_xml";
	
	
	private void isDataComparable(HarvestRequest harvestRequest) throws SQLException, DocumentException, HarvestException
	{
		boolean testResource = true;
		if (harvestRequest.getNativeFormat().equals("comm_anno") || harvestRequest.getNativeFormat().equals("comm_para"))
			testResource = false;
		else if(!TEST_HANDLES)
			testResource = false;
		
		Connection dbConnectionTest = DBConnector.createRepositoryConnection();
		Connection dbConnectionCompare = DBConnector.getConnection(
				Config.DBConnectionForTestCompares.DB_URL, 
				Config.DBConnectionForTestCompares.DB_USER,
				Config.DBConnectionForTestCompares.DB_PASS);
		
		String STMT_TO_USE=null;
		if(testResource)
			STMT_TO_USE = TEST_RESOURCE_GET_DATA_SCHEMA_STMT;
		else
			STMT_TO_USE = GET_DATA_SCHEMA_STMT;
		
		PreparedStatement testDataSTMT = dbConnectionTest.prepareStatement(
				String.format(STMT_TO_USE, TEST_TARGET_XML_COL) );
		PreparedStatement compareDataSTMT = dbConnectionCompare.prepareStatement(
				String.format(STMT_TO_USE, COMPARE_TARGET_XML_COL));
		
		
		
		testDataSTMT.setString(1, harvestRequest.getSetSpec());
		compareDataSTMT.setString(1, harvestRequest.getSetSpec());
		
		ResultSet rs_test = testDataSTMT.executeQuery();
		ResultSet rs_compare = compareDataSTMT.executeQuery();
		
		boolean matches = true;
		int test_count = 0;
		int compare_count = 0;
		
		// Test is probably the newest so this will be our base
		
		if(!rs_test.next())
		{
			System.out.println("Not comparing anything, test is empty. Something is wrong");
			return;
		}
		if(!rs_compare.next())
		{
			System.out.println("Not comparing anything, compare is empty. Something is wrong");
			return;
		}
		test_count++;
		compare_count++;
		
		while(true)
		{
			String testPartnerid = rs_test.getString(2);
			String comparePartnerid = rs_compare.getString(2);
			int compareResult = testPartnerid.compareTo(comparePartnerid);
			if(compareResult<0)
			{
				System.out.println(String.format("No compare result for %s, must be new record", testPartnerid));
				if(!rs_test.next())
					break;
				test_count++;
			}
			else if(compareResult>0)
			{
				// record must have been deleted or we have an error, because we removed a record that shouldn't have bene
				System.out.println(String.format("!!!!ERROR!!!!!!!!!!!! Possible error no test record  for %s, make sure it was deleted", 
						comparePartnerid));
				if(!rs_compare.next())
					break;
				compare_count++;
			}
			else if(compareResult==0)
			{
				String testMetadataHandle = rs_test.getString(1);
				String compareMetadataHandle = rs_compare.getString(1);
				String testAgent = rs_test.getString(3);
				String compareAgent = rs_compare.getString(3);
				Blob blobTestTargetXML = rs_test.getBlob(4);
				Blob blobCompareTargetXML = rs_compare.getBlob(4);
				Blob blobTestNativeXML = rs_test.getBlob(5);
				Blob blobCompareNativeXML = rs_compare.getBlob(5);
				
				byte[] blobTestTargetXMLBytes = blobTestTargetXML.getBytes(1, (int)blobTestTargetXML.length());
				byte[] blobCompareTargetXMLBytes = blobCompareTargetXML.getBytes(1, (int)blobCompareTargetXML.length());
				
				String testTargetXML=null;
				try {
					testTargetXML = new String(blobTestTargetXMLBytes, Config.ENCODING);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String compareTargetXML=null;
				try {
					compareTargetXML = new String(blobCompareTargetXMLBytes, Config.ENCODING);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				byte[] blobTestNativeXMLBytes = blobTestNativeXML.getBytes(1, (int)blobTestNativeXML.length());
				byte[] blobCompareNativeXMLBytes = blobCompareNativeXML.getBytes(1, (int)blobCompareNativeXML.length());
				
				String testNativeXML=null;
				try {
					testNativeXML = new String(blobTestNativeXMLBytes, Config.ENCODING);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String compareNativeXML=null;
				try {
					compareNativeXML = new String(blobCompareNativeXMLBytes, Config.ENCODING);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// we have a match, compare them
				if(TEST_HANDLES && !testMetadataHandle.equals(compareMetadataHandle))
				{
					System.out.println(String.format("\n\nError: metadata handles do not match for partnerid %s, %s!=%s\n\n",
							testPartnerid, testMetadataHandle, compareMetadataHandle));
					matches = false;
					//return false;
				}
				if(testResource)
				{
					String testResouceHandle = rs_test.getString(6);
					String compareResouceHandle = rs_compare.getString(6);
					
					String testURL = rs_test.getString(7);
					String compareURL = rs_compare.getString(7);
					
					if(!testResouceHandle.equals(compareResouceHandle))
					{
						System.out.println(String.format("\n\nError: ResourceHandle for partnerid %s and url %s do not match. %s!=%s\n\n",
								testPartnerid, testURL, testResouceHandle, compareResouceHandle)	);
						matches = false;
						//return false;
					}
					if(!testURL.equals(compareURL))
					{
						System.out.println(String.format("\n\nError: URL for partnerid %s do not match. %s!=%s\n\n",
								testPartnerid, testURL, compareURL)	);
						matches = false;
						//return false;
					}
				}
				if(!testAgent.equals(compareAgent))
				{
					System.out.println(String.format("\n\nError: Agent for metadatahandle %s do not match. %s!=%s\n\n",
							testAgent, compareAgent)	);
					matches = false;
					//return false;
				}

				if(harvestRequest.getNativeFormat().equals("lar"))
				{
					String msg = this.xmlDocumentsEqual(testNativeXML, compareNativeXML, false);
					if(msg!=null)
					{
						System.out.println(String.format("\n\nError: native xml for partnerid %s do not match"+
								"Reason: %s\n\n", testPartnerid, msg));
						System.out.println("Info: test xml was\n "+testNativeXML);
						System.out.println("Info: compare xml was\n "+compareNativeXML);
						matches = false;
					}
				}
				
				String msg = null;
				String info1 = null;
				String info2 = null;
				if(harvestRequest.getNativeFormat().equals("comm_para")||harvestRequest.getNativeFormat().equals("comm_anno"))
				{
					msg = this.xmlDocumentsEqual(testTargetXML, compareNativeXML, false);
					info1 = testTargetXML;
					info2 = compareNativeXML;
				}
				else
				{
					msg = this.xmlDocumentsEqual(testTargetXML, compareTargetXML, true);
					info1 = testTargetXML;
					info2 = compareTargetXML;
				}
				if(msg!=null)
				{
					System.out.println(String.format("\n\nError: target xml for partnerid %s do not match"+
							"Reason: %s\n\n", testPartnerid,msg));
					System.out.println("Info: test xml was \n"+info1);
					System.out.println("Info: compare xml was\n "+info2);
					//return false;
				}
				
				if(!rs_test.next())
					break;
				test_count++;
				
				if(!rs_compare.next())
					break;
				compare_count++;
				
				
			}
			
		}
		
		// Chances of this happening are slim since the records are sorted. There threfore we would have
		// to be missing some records that are alphapbetically partnerid last for this to happen, 
		if(rs_compare.next())
		{
			System.out.println("Possible Error: Test results are missing records.\n" +rs_compare.getString(1) );
			while(rs_compare.next())
				System.out.println(rs_compare.getString(1));
			
		}
		if(test_count>compare_count)
			System.out.println(String.format("There are more test results(%d) then compare results(%d). ",
					test_count, compare_count ));
		
		rs_test.close();
		rs_compare.close();
		testDataSTMT.close();
		compareDataSTMT.close();
		dbConnectionTest.close();
		dbConnectionCompare.close();
		return;
		
	}
	
	
	private String xmlDocumentsEqual(String testXmlString, String compareXMLString, boolean fixCompare) throws DocumentException
	{
		
		Document testDocument = Dom4jUtils.getXmlDocument(testXmlString);
		Document compareDocument = Dom4jUtils.getXmlDocument(compareXMLString);

		if(fixCompare)
			this.fixCompareDocument(compareDocument);

		try {
			
			assertDocumentsEqual(compareDocument, testDocument);
		} 
		catch (Error e) {
			return e.getMessage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return e.getMessage();
		}
		
		
		return null;
		
	}
	
	private void fixCompareDocument(Document document)
	{
		Element rootElement = document.getRootElement();
		if(rootElement.getNamespaceForPrefix("ieee")==null)
			rootElement.addNamespace("ieee", "http://www.ieee.org/xsd/LOMv1p0");

	}
	 
}