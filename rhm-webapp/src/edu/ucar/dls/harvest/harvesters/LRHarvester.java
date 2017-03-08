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
package edu.ucar.dls.harvest.harvesters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.dlese.dpc.util.TimedURLConnection;
import org.dlese.dpc.util.URLConnectionTimedOutException;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.exceptions.InvalidSignatureException;
import edu.ucar.dls.harvest.harvesters.Harvester;
import edu.ucar.dls.harvest.processors.harvest.LRHarvestStartedInfo;
import edu.ucar.dls.harvest.resources.LRVerify;
import edu.ucar.dls.harvest.tools.JsonUtil;
import edu.ucar.dls.harvest.workspaces.Workspace;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *  Harvester sub class for protocol lr. This class implements the required methods
 *  from Harvester class. it downloads the files to harvest directory and then
 *  populates the records into the workspace.
 *  Authored 4/2/2013
 */

@XmlRootElement(name="LRHarvester")
public class LRHarvester extends Harvester{
	private LRVerify lrVerify = null;
	private Map<String, List<List<String>>> fileToSchemaPayloadFiltersMap = null;
	
	/**
	 * initialize method to make sure that public key and key owner were
	 * set correctly since they are required by the verify tool . Initialize
	 * is also ceated here to make sure it has its dependencies
	 */
	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);
		try {
			this.lrVerify = new LRVerify(workspace.harvestRequest.getPublicKeys());
		} catch (Exception e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					"Error trying to creating LRVerifyier. Public key error possibly.", e);
		}
		this.fileToSchemaPayloadFiltersMap = new HashMap<String, List<List<String>>>();
	}
	
	/**
	 * Overwritten method that adds in a special processor just for this harvester.
	 * In this case it adds a processor that logs info that the harvest started
	 */
	protected void setPreProcessors() throws HarvestException
	{
		super.setPreProcessors();
		this.preProcessors.add(new LRHarvestStartedInfo());
	}
	
	/** 
	 *  Download files to the specified directory at getHarvestDirectory(),
	 *  During the process there can be a resumption token in the JSON which
	 *  defines the next URL that should be called to get the next set of 
	 *  harvested files. This method also gives servers multiple chance to come
	 *  back online if they are not currently serving correctly before the harvest
	 *  process is exited
	 */
	
	protected void downloadFiles() throws HarvestException
	{
		File harvestDirectory = this.getHarvestDirectory();

		String filePrefix = null;

		Iterator<Map.Entry<String, List<List<String>>>> sliceRequestIterator = 
				this.workspace.harvestRequest.getSliceRequests().entrySet().iterator();
		
		String sliceUrl = this.workspace.harvestRequest.getBaseUrl() + "/slice";
		// Loop through all collections that were specified
		while (sliceRequestIterator.hasNext()) {
			Map.Entry<String, List<List<String>>> sliceRequestPairs = sliceRequestIterator.next();
			String requestParams = sliceRequestPairs.getKey();
			List<List<String>> payloadSchemaFilters = sliceRequestPairs.getValue();
			
			String serviceRequestUrl = sliceUrl+
					LRHarvester.createParamString(requestParams);
			
			// For the name of the file either use the set id or all_sets if this
			// call is for all sets
			if (requestParams!=null)
				filePrefix = requestParams+'_';
			else
				filePrefix = "no_params_";
			
			// We also are going to index the files since there can be more then one
			int fileIndex = 1;
			while (serviceRequestUrl!= null)
			{
				try {
					String fileAsString=null;
					int serverTryIndex = 0;
					Map<String, Object> documentMap = null;
					File uniqueFile = null;
					// Try to ping the server. If it fails try SERVER_MAX_TRIES more waiting a specified
					// amount of millisecond in between
					
					while(true)
					{
						serverTryIndex++;
						try
						{	

							fileAsString = TimedURLConnection.importURL(
									serviceRequestUrl, Config.ENCODING, 10000);
							
							uniqueFile = new File(harvestDirectory, filePrefix+fileIndex+".txt");

							
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
						    boolean viewUpToDate = (Boolean)documentMap.get("viewUpToDate");
						    
						    if(!viewUpToDate)
						    {
						    	throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
										"Response is marked as not up todate, either this harvest is "+
										"being updated currently or the node is having issues. url: " 
										 + serviceRequestUrl, "LRHarvester.downloadFiles()");
						    }
						    break;
						}
						catch (Exception e) 
						{
							if(serverTryIndex>=SERVER_MAX_TRIES)
								throw e;
							// Note the sleep here is not the default 10 minutes. Because
							// alot of the LR's issue comes from be sporatic with its responses
							// not thats its down
							Thread.sleep(10000);
							HarvestRequest.checkForInterruption(
								"Harvester.downloadFiles()");
						}
						

					}

				    
				    // save the filters that are used for this file, this is to keep consistent
				    // where we download all files then go through them
				    this.fileToSchemaPayloadFiltersMap.put(uniqueFile.getName(), payloadSchemaFilters);
				    
					// Get the resumption token if there is one and use that as the 
				    // next service url
					String resumptionToken = (String)documentMap.get("resumption_token");
					if (resumptionToken!=null)
					{
						serviceRequestUrl = String.format("%s?resumption_token=%s", 
								sliceUrl, resumptionToken);
					}
					else
						serviceRequestUrl = null;
					fileIndex++;
					Thread.sleep(2000);
				
				} catch (URLConnectionTimedOutException e) {
					throw new HarvestException(Config.Exceptions.OAI_SERVER_ERROR_CODE,
							"Timeout has been reached trying to connect to the OAI url: "+
							"%s" + serviceRequestUrl, e);
				} catch (IOException e) {
					throw new HarvestException(Config.Exceptions.OAI_SERVER_ERROR_CODE,
							"Having trouble with getting a valid response back from url: "
							 + serviceRequestUrl, e);
				} 
			    catch (Exception e) {
					throw new HarvestException(Config.Exceptions.OAI_RESPONSE_ERROR_CODE,
							"Error writing XML response to file system url: "
							 + serviceRequestUrl, e);
				}
				
			}
		}
		
	}
	
	/**
	 *  Populate the records in the database by looping through the files that
	 *  were downloaded earlier and pulling out the resource data and resource
	 *  locator
	 */
	
	public void populateRecords() throws HarvestException{
		
		DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String resource_data_type = null;
		try {
			for (File file: this.getHarvestDirectory().listFiles())
			{
				InputStream is = new FileInputStream(file);
			    String jsonTxt = IOUtils.toString( is );
			    
			    Map<String, Object> documentMap = JsonUtil.convertToMap(jsonTxt);
			    
			    List<Map> jsonDocuments = (List)documentMap.get("documents");
				for (Map lrDocument:jsonDocuments)
				{				
					String documentId = (String)lrDocument.get("doc_ID");
					
					Map resourceDataDescriptionJson = (Map)lrDocument.get(
					"resource_data_description");
					
					try
					{
						// verify that the document in question is from the person
						// we expect since we have their public key
						boolean isValidDocument  = this.lrVerify.verify(
								resourceDataDescriptionJson);
						if (!isValidDocument)
						{
							// if its no, then ignore the record
							continue;
						}
					}
					catch(InvalidSignatureException e)
					{
						// If it gets here this means that the signature is good but doesn't
						// match whats actually in the envelope. Therefore either someone is
						// forging their signature on something they want published or the signer
						// algorithm changed on our side or there side.
						// Possibly do something here, if this is thrown the signature is wrong,
						// Probably should add a note saying that either someone is forging the
						// signature or LRSignature's algorithm has changed
						continue;
					} 
					catch (Exception e) {
						throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
								"Error trying verify the signature of the document.", e);
					}
					
					
					
					/** a way to test typical age range
					Map properties = (Map)((List<Map>)resourceDataMap.get("items")).get(0).get("properties");
					String [] typicalAgeRange = {"18+", "13","12", "8-11"};
					properties.put("typicalAgeRange", typicalAgeRange);
					**/
					
					String resourceLocator = (String)resourceDataDescriptionJson.get("resource_locator");	
					String updateDateSring = (String)resourceDataDescriptionJson.get("update_timestamp");	
					
					// Payload placement none means that that the item was deleted
					String payloadPlacement = (String)resourceDataDescriptionJson.get("payload_placement");
					if(payloadPlacement.equals("none"))
						continue;
					
					Date updateDate = null;

					try {
						updateDate = utcFormat.parse(updateDateSring.replaceFirst("\\.\\d*", ""));
					} catch (ParseException e) {
						// timestamp is required to figure out which came first, something is wrong with 
						// the node or the spec changed for the format, throw harvest exception
						throw new HarvestException(Config.Exceptions.OAI_RESPONSE_ERROR_CODE,
								"update_timestamp is not formatted correctly, something is wrong with node " +
								"or spec.", e);
					}
					
					Object payload = resourceDataDescriptionJson.get("resource_data");
					List<String> payloadSchema = (List<String>)resourceDataDescriptionJson.get(
										"payload_schema");
					
					// make sure that what type of payload we are expecting is defined
					// in the payload schema
					if(!this.validatePayloadSchema(payloadSchema, file.getName()))
						continue;
					
					// Finally make sure that the dataformat is what we are expecting for the
					// payload
					String payloadString = null;
					if(payload instanceof Map &&
							this.workspace.getHarvestDataFormat().equals(
									Config.DataFormats.JSON))
					{
						payloadString = JsonUtil.convertToJson((Map)payload);
					}
					else if(payload instanceof String && this.workspace.getHarvestDataFormat().equals(
							Config.DataFormats.XML))
					{
						payloadString = (String)payload;
					}
					else
					{
						continue;
					}
					
					resource_data_type = (String)resourceDataDescriptionJson.get("resource_data_type");
					
					
					// populate the record
					
					this.workspace.populateRecord(documentId, payloadString, resourceLocator, updateDate);
					HarvestRequest.checkForInterruption(
						"Harvester.populateRecords()");
				}
				HarvestRequest.checkForInterruption(
					"Harvester.populateRecords()");
			}
			this.workspace.cleanupAfterIteration();
		} catch (IOException e) {
			throw new HarvestException(Config.Exceptions.OAI_RESPONSE_ERROR_CODE,
					"Error trying to re-access harvest files to populate workspace", e);
		}
	}
	
	/**
	 * Private method that validates what kind of payload is attached matches with 
	 * that of the Harvest request filters
	 * @param payloadSchemaList
	 * @return
	 */
	private boolean validatePayloadSchema(List<String> payloadSchemaList, String fileName) {
		List<List<String>> payloadFiltersContainers = this.fileToSchemaPayloadFiltersMap.get(fileName);
		
		// If none are specified just return true
		if(payloadFiltersContainers==null || payloadFiltersContainers.size()==0)
			return true;
		
		for(List<String> payloadFilterContainer: payloadFiltersContainers)
		{
			boolean matches = true;
			for (String payloadFilter: payloadFilterContainer)
			{
				boolean innerMatches = false;
				for(String payloadSchemaItem:payloadSchemaList )
				{
					if( payloadSchemaItem.contains(payloadFilter))
					{
						innerMatches = true;
						break;
					}
				}
				if(!innerMatches)
				{
					matches = false;
					break;
				}
			}
			if(matches)
				return true;
		}
		
		return false;
		
	}

	/**
	 *  Create a param string based on metadataprefix and collection set id. 
	 *  This is public because other processors might use this for logging. 
	 */
	public static String createParamString(String tag)
	{
		return  '?' + tag;
	}	
}
