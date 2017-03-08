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
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;



import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dlese.dpc.util.TimedURLConnection;
import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.repository.DBRepository;
import edu.ucar.dls.harvest.resources.DBConnector;
import edu.ucar.dls.harvest.tools.JsonUtil;

import java.util.concurrent.ArrayBlockingQueue;
public class LRHarvest extends HarvestTestCase
{
	public static final String ITERATION_PREFIX = "lr_ingest_11_22_2013";
	public static final String ALIGNMENT_TYPE = "alignment";
	public static final String baseFilePath = "C:\\file_storage\\"+ITERATION_PREFIX;
	public static final String filePathFormat = "C:\\file_storage\\"+ITERATION_PREFIX+"\\file_%d.txt";
	private static final String CREATE_TABLE_STMT = "CREATE TABLE "+ITERATION_PREFIX+"(doc_id varchar(255), " +
			  "doc_type varchar(255), "+
			  "resource_locator text, "+
			/*  "resource_data longblob, "+*/
			  "tagged_keys varchar(500), "+
			  "resource_data_type varchar(255), "+
			  "payload_placement varchar(255), "+
			  "payload_schema varchar(255),  "+
			  "key_location varchar(255), "+
			  "key_owner varchar(255), "+
			  "tos varchar(255), "+
			  "attribution varchar(255), "+
			  "create_timestamp timestamp NULL DEFAULT NULL, "+
			  "update_timestamp timestamp NULL DEFAULT NULL, "+
			  "submitter varchar(255), "+
			  "signer varchar(255), "+
			  "curator varchar(255), "+
			  "replaces varchar(255), " +
			  "id int(11) NOT NULL AUTO_INCREMENT, " +
			  "valid_url int(1), " +
			  "publishing_node varchar(255), " +
			  "PRIMARY KEY (id), " +
			  "KEY `doc_id` (`doc_id`) " +
			") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ";

	private static final String INSERT_STMT = "INSERT INTO "+ITERATION_PREFIX+" (doc_id, " +
	"doc_type, tagged_keys, resource_data_type, payload_placement, " +
	"payload_schema, key_location, key_owner, tos, attribution, create_timestamp, update_timestamp, submitter, " +
	"signer, curator, resource_locator, valid_url, publishing_node)" + 
		    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	
	private static final String UPDATE_VALID_URL = "UPDATE "+ITERATION_PREFIX+" set valid_url=1 where doc_id=?";
	private static final String GET_URL = "select resource_locator from "+ITERATION_PREFIX+" where doc_id=?";
	
	
	private static final String CREATE_PAYLOAD_PROPERTY_TABLE_STMT = 
		"CREATE TABLE "+ITERATION_PREFIX+"_payload (" +
			"doc_id VARCHAR(50) NOT NULL , "+
			"type VARCHAR(45) NOT NULL , "+
			"sub_type VARCHAR(700) NULL , " +
			"value VARCHAR(700) NULL , " +
			"id int(11) NOT NULL AUTO_INCREMENT, "+
			"PRIMARY KEY (id) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci; ";

	private static final String INSERT_PROPERTY_STMT = "INSERT INTO "+ITERATION_PREFIX+"_payload (doc_id, " +
	"type, sub_type, value)" + 
		    "VALUES (?, ?, ?, ?)";
	
	
	public LRHarvest(String name) {
		super(name, "transform");
		// TODO Auto-generated constructor stub
	}


	public void testHarvest() throws Exception
	{
		
		//LRHarvest.downloadFiles();
		class URLChecker extends Thread {

			private ArrayBlockingQueue<String> queue = null; 
			PreparedStatement updatePstmt = null;
			PreparedStatement getPstmt = null;
			public URLChecker(ArrayBlockingQueue<String> queue, Connection connection) throws SQLException
			{
				this.queue = queue;
				this.updatePstmt = connection.prepareStatement(UPDATE_VALID_URL);
				this.getPstmt = connection.prepareStatement(GET_URL);
			}
			public void run() {
				Object recordIdObj = null;
				while(!this.isInterrupted() || recordIdObj!=null)
				{
					try
					{
						recordIdObj = queue.poll();
						if(recordIdObj==null)
						{
							System.out.println("none to find");
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							continue;
						}
						String docId = (String)recordIdObj;
						this.getPstmt.setString(1, docId);
						ResultSet rs = this.getPstmt.executeQuery();
						rs.next();
						String url = rs.getString(1);
						boolean valid_url = false;
						try
						{
							HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
							urlConnection.setConnectTimeout(8000);
							urlConnection.setReadTimeout(8000);
							urlConnection.setRequestMethod("HEAD");
							int responseCode = urlConnection.getResponseCode();
							if (responseCode == 200) {
								valid_url=true;
							}
							urlConnection.disconnect();

						}
						catch(Exception e)
						{
				
						}
						if(valid_url)
						{
							this.updatePstmt.setString(1, docId);
							this.updatePstmt.execute();
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				DbUtils.closeQuietly(this.updatePstmt);
				DbUtils.closeQuietly(this.getPstmt);
				
		    }
		}
		
		Connection connection = DBConnector.createRepositoryConnection();
		ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(600000);
		ArrayList<Thread> threads = new ArrayList<Thread>();
		/*for(int i=0; i<200;i++)
		{
			Thread aThread = new URLChecker(queue, connection);
			aThread.start();
			
			threads.add(aThread);

		}*/
		LRHarvest.harvestFiles(queue);
		
		/*Iterator<Thread> threadIterator = threads.iterator();
		
		while(threadIterator.hasNext())
		{
			Thread thread = threadIterator.next();
			thread.interrupt();
			thread.join();
		}*/
		
	}
	
	
	public static void downloadFiles() throws Exception
	{
		
		/*try {
			FileUtils.deleteDirectory(new File(baseFilePath));
		} catch (IOException e) {

		}*/
		
		String baseServiceRequestUrl = "http://node01.public.learningregistry.net/harvest/listrecords";
		
		//baseServiceRequestUrl = "http://node01.public.learningregistry.net/slice";
		//String serviceRequestUrl = baseServiceRequestUrl+"?any_tags=nsdl_dc";
		String serviceRequestUrl = baseServiceRequestUrl;
		int index = 1;
		while (serviceRequestUrl!= null)
			{
				String fileAsString=null;
				int serverTryIndex = 0;
				Map<String, Object> documentMap=null;
				// Try to ping the server. If it fails try SERVER_MAX_TRIES more waiting a specified
				// amount of millisecond in between
				while(true)
				{
					serverTryIndex++;
					try
					{	
						fileAsString = TimedURLConnection.importURL(
								serviceRequestUrl, Config.ENCODING, 10000);
						
						File uniqueFile = new File(String.format(filePathFormat, index));

						
						FileUtils.writeStringToFile(uniqueFile, fileAsString, 
								Config.ENCODING);
						
						InputStream is = new FileInputStream(uniqueFile);
						
						// Down to here this method is exactly as it was in OAI harvesting. From
						// here on its LR specific
					    String jsonText = IOUtils.toString( is );
						
					    // convert the json Text into a map
					    documentMap = JsonUtil.convertToMap(jsonText);
					    
					    // If up to date is false that means that the node currently switching
					    // out data that may have affected the response. Throw harvest exception
					   /* boolean viewUpToDate = (Boolean)documentMap.get("viewUpToDate");
					    
					    if(!viewUpToDate)
					    {
					    	throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
									"Response is marked as not up todate, either this harvest is "+
									"being updated currently or the node is having issues. url: " 
									 + serviceRequestUrl, "LRHarvester.downloadFiles()");
					    }*/
					    break;
					}
					catch (Exception e) 
					{
						if(serverTryIndex>=3)
							throw e;
					}
					

				}

				// Get the resumption token if there is one and use that as the 
			    // next service url
				String resumptionToken = (String)documentMap.get("resumption_token");
				if (resumptionToken!=null)
				{
					serviceRequestUrl = String.format("%s?resumption_token=%s", 
							baseServiceRequestUrl, resumptionToken);
					index++;
				}
				else
					serviceRequestUrl = null;	
				
				Thread.yield();
			}
	}
	
	public static void harvestFiles(ArrayBlockingQueue<String> queue) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = DBConnector.createRepositoryConnection();
			LRHarvest.setupTable(connection);
			int index = 1;
			File file = new File(String.format(filePathFormat, index));
			
			while(file.exists())
			{
				LRHarvest.populateDatabase(file, connection, queue);
				index++;
				file = new File(String.format(filePathFormat, index));
				if(index%100==0)
					System.out.println(index);
			}
		}
		finally
		{
			DbUtils.closeQuietly(connection);
		}
	}
	
	
	private static void populateDatabase(File file, Connection connection, 
			ArrayBlockingQueue<String> queue) throws Exception
	{
		PreparedStatement insertPstmt = connection.prepareStatement(INSERT_STMT);
		PreparedStatement insertPropertyPstmt = connection.prepareStatement(INSERT_PROPERTY_STMT);
		
		DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		
		InputStream is = new FileInputStream(file);
	    String jsonTxt = IOUtils.toString( is );

	    Map<String, Object> documentMap = JsonUtil.convertToMap(jsonTxt);
	    ArrayList<String> docIds = new ArrayList<String>();
	    List<Map> jsonDocuments = (List)documentMap.get("listrecords");
		for (Map lrDocument:jsonDocuments)
		{			
			
			Map recordJson = (Map)lrDocument.get(
			"record");
			Map resourceDataDescriptionJson = (Map)recordJson.get(
			"resource_data");
			
/*	    List<Map> jsonDocuments = (List)documentMap.get("documents");
		for (Map lrDocument:jsonDocuments)
		{			

			Map resourceDataDescriptionJson = (Map)lrDocument.get(
			"resource_data_description");*/
			
			try
			{
				String documentId = (String)resourceDataDescriptionJson.get("doc_ID");
				//String documentId = (String)lrDocument.get("doc_ID");
				
				String resourceLocator = "";
				if(resourceDataDescriptionJson.containsKey("resource_locator"))
					resourceLocator = (String)resourceDataDescriptionJson.get("resource_locator");
				
				String payloadPlacement = null;
				if(resourceDataDescriptionJson.containsKey("payload_placement"))
					payloadPlacement = (String)resourceDataDescriptionJson.get("payload_placement");
				
				
				// deleted records we want to ignore
				if(payloadPlacement.equals("none"))
					continue;
				
				
				List payloadSchemas = null;
				String payloadSchemaString = null;
				if(resourceDataDescriptionJson.containsKey("payload_schema"))
				{
					payloadSchemas = (List)resourceDataDescriptionJson.get("payload_schema");
					payloadSchemaString = StringUtils.join(payloadSchemas, ", ");
				}
				String keys = null;
				if(resourceDataDescriptionJson.containsKey("keys"))
				{
					keys = StringUtils.join((List)resourceDataDescriptionJson.get("keys"), ", ");
					if(keys.length()>499)
						keys = keys.substring(0, 498);
				}
				String replaces = null;
				if(resourceDataDescriptionJson.containsKey("replaces"))
					replaces = StringUtils.join((List)resourceDataDescriptionJson.get("replaces"), ", ");
				
				String updateDateSring = (String)resourceDataDescriptionJson.get("update_timestamp");	
				String createDateSring = (String)resourceDataDescriptionJson.get("create_timestamp");
				String docType = (String)resourceDataDescriptionJson.get("doc_type");
				String publishingNode = (String)resourceDataDescriptionJson.get("publishing_node");
				
				String resourceDataType = (String)resourceDataDescriptionJson.get("resource_data_type");
				
	
				Object digitalSignatureObj = resourceDataDescriptionJson.get("digital_signature");
				
				String keyLocation = null;
				String keyOwner = null;
				if(digitalSignatureObj!=null)
				{
					Map digitalSignature = (Map)digitalSignatureObj;
					if(digitalSignature.containsKey("key_location"))
						keyLocation = StringUtils.join((List)digitalSignature.get("key_location"), ", ");
					if(digitalSignature.containsKey("key_owner"))
						keyOwner = (String)digitalSignature.get("key_owner");
					
				}
		
				Date updateDate = null;
				Date createDate = null;
	
				try {
					updateDate = utcFormat.parse(updateDateSring.replaceFirst("\\.\\d*", ""));
					createDate = utcFormat.parse(createDateSring.replaceFirst("\\.\\d*", ""));
				} catch (ParseException e) {
					// timestamp is required to figure out which came first, something is wrong with 
					// the node or the spec changed for the format, throw harvest exception
					throw new Exception(
							"update_timestamp is not formatted correctly, something is wrong with node " +
							"or spec.");
				}
				
				Map identity = (Map)resourceDataDescriptionJson.get("identity");
				
				String submitter = (String)identity.get("submitter");
				String curator = null;
				String owner = null;
				String signer = null;
				
				if (identity.containsKey("curator"))
					curator = (String)identity.get("curator");
				
				if (identity.containsKey("owner"))
					owner = (String)identity.get("owner");
				
				
				if (identity.containsKey("signer"))
					signer = (String)identity.get("signer");
				
				Object payload = resourceDataDescriptionJson.get("resource_data");
				
				
				String submissionTos = null;
				String attribution = null;
				if(resourceDataDescriptionJson.containsKey("TOS"))
				{
					Map tos = (Map)resourceDataDescriptionJson.get("TOS");
					submissionTos = (String)tos.get("submission_TOS");
				
					attribution = null;
					if(tos.containsKey("submission_attribution"))
					{
						attribution = (String)tos.get("submission_attribution");
					}
				}
				// Finally make sure that the dataformat is what we are expecting for the
				// payload
				String payloadString = null;
				if(payload!=null && payload instanceof Map )
				{
					payloadString = JsonUtil.convertToJson((Map)payload);
					
					if(payloadSchemas!=null && payloadSchemas.size()>0)
						populatePayloadProperties(documentId, (Map)payload, payloadSchemas, insertPropertyPstmt);
					
				}
				else if(payload!=null && payload instanceof String)
				{
					payloadString = (String)payload;
					populatePayloadProperties(documentId, payloadString, payloadSchemas, insertPropertyPstmt);
				}
				else
				{
					payloadString = "";
				}
				
				
				docIds.add(documentId);
				
				insertPstmt.clearParameters();
				
				insertPstmt.setString(1, documentId);
				insertPstmt.setString(2, docType);
				//insertPstmt.setBlob(3, new SerialBlob(resourceLocator.getBytes(Config.ENCODING)) );
				//insertPstmt.setBlob(4, new SerialBlob(payloadString.getBytes(Config.ENCODING)));

				insertPstmt.setString(3, keys);
				insertPstmt.setString(4, resourceDataType);
				insertPstmt.setString(5, payloadPlacement);
				insertPstmt.setString(6, payloadSchemaString);
				insertPstmt.setString(7, keyLocation);
				insertPstmt.setString(8, keyOwner);
				insertPstmt.setString(9, submissionTos);
				insertPstmt.setString(10, attribution);
				
				
				new Timestamp(createDate.getTime());
				Timestamp createTimestamp = null;
				if(createDate!=null)
					createTimestamp = new Timestamp(createDate.getTime());
				insertPstmt.setTimestamp(11, createTimestamp);
				
				new Timestamp(updateDate.getTime());
				Timestamp updateTimestamp = null;
				if(updateDate!=null)
					updateTimestamp = new Timestamp(updateDate.getTime());
				insertPstmt.setTimestamp(12, updateTimestamp);
				
				insertPstmt.setString(13, submitter);
				insertPstmt.setString(14, signer);
				insertPstmt.setString(15, curator);
				insertPstmt.setString(16, resourceLocator);
				insertPstmt.setInt(17, 0);
				insertPstmt.setString(18, publishingNode);
				insertPstmt.addBatch();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	  }
		insertPstmt.executeBatch();
		insertPropertyPstmt.executeBatch();
		DbUtils.closeQuietly(insertPstmt);
		DbUtils.closeQuietly(insertPropertyPstmt);
		//queue.addAll(docIds);
		
		
		
	}
	
	
	private static void populatePayloadProperties(String documentId,
			String payload, List payloadSchemas, PreparedStatement insertPropertyPstmt) {
		try {
			Document doc = Dom4jUtils.getXmlDocument(payload);
			Element rootElement = doc.getRootElement();
			List<Element> conformsTos = rootElement.selectNodes("*[name()='dct:conformsTo']");
			for(Element conformsTo:conformsTos)
			{
				String conformsToText = conformsTo.getTextTrim();
				insertPropertyPstmt.clearParameters();
				insertPropertyPstmt.setString(1, documentId);
				insertPropertyPstmt.setString(2, ALIGNMENT_TYPE);
				
				String subType = "conformsTo";
				if (conformsToText.contains("ASN"))
					subType+=" - ASN";
				else if(conformsToText.contains("SMS"))
					subType+=" - SMS";
				else if(conformsToText.contains("NSES"))
					subType+=" - NSES";
				else
					subType+=" - Other";
				insertPropertyPstmt.setString(3, subType);
				
				if (conformsToText.length()>699)
					conformsToText = conformsToText.substring(0, 699);
				insertPropertyPstmt.setString(4, conformsToText);
				insertPropertyPstmt.addBatch();
			}
			
		} catch (DocumentException e) {
			// XML issues when submission ignore
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void populatePayloadProperties(String documentId,
			Map payload, List payloadSchemas, PreparedStatement insertPropertyPstmt) {
		
		try
		{
			List<Map> educationalAlignments = new ArrayList<Map>();
			
			
			if (payload.containsKey("educationalAlignment"))
			{
				Object educationalAlignmentsObject = payload.get("educationalAlignment");
				if(educationalAlignmentsObject instanceof Map)
				{
					educationalAlignments.add((Map)educationalAlignmentsObject);
				}
				else if(educationalAlignmentsObject instanceof List)
					educationalAlignments = (List)educationalAlignmentsObject;
			}
			else if(payload.containsKey("items"))
			{
				Map item = (Map)((List)payload.get("items")).get(0);
				Map properties = (Map)item.get("properties");
				if(properties.containsKey("educationalAlignment"))
				{
					Object educationalAlignmentObject = properties.get("educationalAlignment");
					if(educationalAlignmentObject instanceof  Map)
						educationalAlignments.add((Map)((Map)educationalAlignmentObject).get("properties")); 
					else
					{
						for(Object educationalAlignment: (List)educationalAlignmentObject)
						{
							educationalAlignments.add((Map)((Map)educationalAlignment).get("properties"));
						}
					}
				}
					
			}
	
			if(educationalAlignments.size()==0)
				return;
			
			
			for(Map educationalAlignment:educationalAlignments)
			{
				String value = null;
				String subType = null;
				if(educationalAlignment.containsKey("educationalFramework"))
				{
					Object educationalFrameworkObject = educationalAlignment.get("educationalFramework");
					subType = getSingleStringValue(educationalFrameworkObject);
				}
				if(educationalAlignment.containsKey("targetName"))
				{
					Object educationalFrameworkNameObject = educationalAlignment.get("targetName");
					value = getSingleStringValue(educationalFrameworkNameObject);
					
					if(subType==null||subType.equals(""))
					{
						// If someone didn't submit a framework try to figure it out
						if(value.contains("CCSS.Math"))
							subType = "Common Core State Standards for Mathematics";
						else if(value.contains("CCSS.ELA"))
							subType = "Common Core State Standards for English Language Arts & Literacy in History/Social Studies, Science, and Technical Subjects";
					}
					
				}
				if(value==null && educationalAlignment.containsKey("targetUrl"))
				{
					Object educationalFrameworkUrlObject = educationalAlignment.get("targetUrl");
					value = getSingleStringValue(educationalFrameworkUrlObject);
				}
				
				if(value==null)
					continue;
				if(subType==null)
				{
					subType = "Other";
					System.out.println(value);
					continue;
				}
				insertPropertyPstmt.clearParameters();
				insertPropertyPstmt.setString(1, documentId);
				insertPropertyPstmt.setString(2, ALIGNMENT_TYPE);
				if (subType.length()>699)
					subType = subType.substring(0, 699);
				
				insertPropertyPstmt.setString(3, subType);
				if (value.length()>699)
					value = value.substring(0, 699);
				insertPropertyPstmt.setString(4, value);
				insertPropertyPstmt.addBatch();
			}
		}
		catch(Exception e)
		{
			System.out.println(documentId);
			e.printStackTrace();
		}
	}


	private static String getSingleStringValue(Object propertyObject)
	{
		String stringValue = null;
		if(propertyObject instanceof List)
		{
			List listObjectt = (List)propertyObject;
			Object firstObject = listObjectt.get(0);
			
			// sometimes people error and put lists in lists
			stringValue = getSingleStringValue(firstObject);
		}
		else if(propertyObject instanceof String)
		{
			stringValue = (String)propertyObject;
		}
		return stringValue;
	}
	
	private static void setupTable(Connection connection) throws SQLException
	{
		String sqlDropTable = "Drop table " + ITERATION_PREFIX;
		String sqlDropPropertyTable = "Drop table " + ITERATION_PREFIX+"_payload";
		Statement stmt = null;
		try
		{
			stmt = connection.createStatement();
			stmt.execute(sqlDropTable);
			stmt.execute(sqlDropPropertyTable);
		}
		catch (SQLException e) {
			// table doesn't exist, doesn't matter keep on chugging
		}
		finally
		{
			DbUtils.closeQuietly(stmt);
		}
		
		try
		{
			stmt = connection.createStatement();
			stmt.execute(CREATE_TABLE_STMT);
			stmt.execute(CREATE_PAYLOAD_PROPERTY_TABLE_STMT);
		}
		catch (SQLException e) {
			throw e;
		}
		finally
		{
			DbUtils.closeQuietly(stmt);
		}
		
		
	}
	
}
