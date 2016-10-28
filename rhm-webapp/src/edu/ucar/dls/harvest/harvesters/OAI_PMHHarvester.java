package edu.ucar.dls.harvest.harvesters;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dlese.dpc.util.TimedURLConnection;
import org.dlese.dpc.util.URLConnectionTimedOutException;
import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.harvesters.Harvester;
import edu.ucar.dls.harvest.processors.harvest.OAIHarvestStartedInfo;
import edu.ucar.dls.harvest.processors.harvest_files.OAISchemaValidator;
import edu.ucar.dls.harvest.workspaces.FileHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  Harvester sub class for protocol oai. This class implements the required methods
 *  from Harvester class. it downloads the files to harvest directory and then
 *  populates the records into the workspace.
 *  Authored 4/2/2013
 */

public class OAI_PMHHarvester extends Harvester{
	protected void setHarvestFileProcessors()
	{
		this.harvestFileProcessors.add(new OAISchemaValidator());
	}
	protected void setPreProcessors() throws HarvestException
	{
		super.setPreProcessors();
		this.preProcessors.add(new OAIHarvestStartedInfo());
	}
	
	/**
	 *  Download files to the specified directory at getHarvestDirectory(),
	 *  During the process there can be a resumption token in the xml which
	 *  defines the next URL that should be called to get the next set of 
	 *  harvested files. This method also gives servers multiple chance to come
	 *  back online if they are not currently serving correctly before the harvest
	 *  process is exited
	 */
	
	protected void downloadFiles() throws HarvestException
	{
		File harvestDirectory = this.getHarvestDirectory();
		
		String[] collectionSets = {null};
		
		// If there are no sets specified then collectionSets will be an array of one one null
		String [] specifiedCollectionSets = this.workspace.harvestRequest.getCollection_sets();
		if (specifiedCollectionSets!=null &&specifiedCollectionSets.length!=0)
			collectionSets = specifiedCollectionSets;
		
		String filePrefix = null;
		
		// Loop through all collections that were specified
		for (String collectionSet:collectionSets)
		{	
			String serviceRequestUrl = this.workspace.harvestRequest.getBaseUrl() +
			OAI_PMHHarvester.createParamString(collectionSet, 
					this.workspace.harvestRequest.getMetadataPrefix());
			
			// For the name of the file either use the set id or all_sets if this
			// call is for all sets
			if (collectionSet!=null)
				filePrefix = collectionSet+'_';
			else
				filePrefix = "all_sets_";
			
			// We also are going to index the files since there can be more then one
			int fileIndex = 1;
			while (serviceRequestUrl!= null)
			{
				try {
					
					String fileAsString=null;
					
					int serverTryIndex = 0;
					
					// Try to ping the server. If it fails try SERVER_MAX_TRIES more waiting a specified
					// amount of millisecond in between
					while(true)
					{
						serverTryIndex++;
						Exception exception = null;
						try
						{	

							fileAsString = TimedURLConnection.importURL(
									serviceRequestUrl, Config.ENCODING, SERVER_REQUEST_TIMEOUT);
							// Once we have a valid document get out of this never ending loop
							break;
						}
						catch (URLConnectionTimedOutException e) {
						{
							exception = e;
							e.printStackTrace();
							if(serverTryIndex>=SERVER_MAX_TRIES)
								throw e;
						}
						} catch (IOException e) {
							exception = e;
							if(serverTryIndex>=SERVER_MAX_TRIES||e.getMessage().contains("404"))
								throw e;
						}

						prtln("Server request '" + serviceRequestUrl + "' resulted in error '" +
								(exception == null ? "no info available" : exception.getMessage()) +
								".' Waiting " + SERVER_TRY_TIMEOUT + " milliseconds to try harvesting again.");
						Thread.sleep(SERVER_TRY_TIMEOUT);
						HarvestRequest.checkForInterruption(
							"Harvester.downloadFiles()");
					}
					
					File uniqueFile = new File(harvestDirectory, filePrefix+fileIndex+".xml");
					fileIndex++;
					
					// This is very important statement, the set id might not be a valid
					// name, this will remove chars that are not valid as well as make sure
					// the file is unique, which there should never be a case where its not
					// since we index the name
					String validUniquePath = FileHelper.findAvailableFileName(
							uniqueFile.getAbsolutePath());

					File file = new File(validUniquePath);
					FileUtils.writeStringToFile(new File(validUniquePath), fileAsString, Config.ENCODING);
					
					Document document = Dom4jUtils.getXmlDocument(file, Config.ENCODING);

                    // If this is a static OAI feed, simply report as such:
                    String schemaLocation = document.valueOf("/*[local-name()='Repository']/@xsi:schemaLocation");
                    boolean isStaticRepository = (schemaLocation != null && schemaLocation.indexOf("www.openarchives.org/OAI/2.0/static-repository.xsd") >= 0);
                    if(isStaticRepository) {
                        serviceRequestUrl = null;
                        String setsMsg = "";
                        if (specifiedCollectionSets!=null && specifiedCollectionSets.length!=0)
                            setsMsg = " Warning: Sets are not supported in static OAI repositories. Specified set(s) '" + Arrays.toString(specifiedCollectionSets)+"' will be ignored.";
                        prtln("Fetched static OAI feed repository." + setsMsg);
                    }
                    else {
                        // Get the resumptoken if there is one and use that as the next service url
                        String resumptionToken = document.valueOf(
                                "/*[local-name()='OAI-PMH']/*[local-name()='ListRecords']/*[local-name()='resumptionToken'");

                        if (resumptionToken != null && resumptionToken.trim() != "") {
                            serviceRequestUrl = String.format("%s?verb=ListRecords&resumptionToken=%s",
                                    this.workspace.harvestRequest.getBaseUrl(), resumptionToken);
                        } else
                            serviceRequestUrl = null;
                    }
				
				} catch (URLConnectionTimedOutException e) {
					throw new HarvestException(Config.Exceptions.OAI_SERVER_ERROR_CODE,
							"Timeout has been reached trying to connect to the OAI url: "+
							"%s" + serviceRequestUrl, e);
				} catch (IOException e) {
					throw new HarvestException(Config.Exceptions.OAI_SERVER_ERROR_CODE,
							"Having trouble with getting a valid response back from url: "
							 + serviceRequestUrl, e);
				} 
				catch (DocumentException e) {
					throw new HarvestException(Config.Exceptions.OAI_RESPONSE_ERROR_CODE,
							"Response coming back not valid XML. Cannot create XML document. url: " 
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
	 *  were downloaded earlier and pulling out the partner id from the header,
	 *  whether or not it was deleted and the actual record string. This method
	 *  is seperated from the download files method due to processors that
	 *  need to be ran on the files before we populate. Ie schemavalidate the xml
	 */
	
	public void populateRecords() throws HarvestException{
		try {
			for (File file: this.getHarvestDirectory().listFiles())
			{
				Document document = Dom4jUtils.getXmlDocument(file, Config.ENCODING);

                List <Element>recordNodes;

                String schemaLocation = document.valueOf("/*[local-name()='Repository']/@xsi:schemaLocation");
                boolean isStaticRepository = (schemaLocation != null && schemaLocation.indexOf("www.openarchives.org/OAI/2.0/static-repository.xsd") >= 0);
                if(isStaticRepository) {
                    String metadataPrefix = this.workspace.harvestRequest.getMetadataPrefix();
                    prtln("Processing static OAI feed for format '" + metadataPrefix + "'");

                    recordNodes = document.selectNodes("/*[local-name()='Repository']/*[local-name()='ListRecords'][@metadataPrefix='"+metadataPrefix+"']/*[local-name()='record']");
                }
                // Process standard OAI response:
                else {
                    recordNodes = document.selectNodes("/*[local-name()='OAI-PMH']/*[local-name()='ListRecords']/*[local-name()='record']");
                }

                for (Element record: recordNodes)
				{
					Element header = (Element)record.selectSingleNode("*[local-name()='header']");
					
					Attribute statusAttribute = header.attribute("status");

					if (statusAttribute!=null && statusAttribute.getValue().equals("deleted"))
					{
						//Not a record we want to deal with ignore it
						continue;
					}
					String partnerId = record.valueOf(
							"*[local-name()='header']/*[local-name()='identifier']");
					
					List<Element> documentList = ((Element)record.selectSingleNode(
						"*[local-name()='metadata']")).elements();
					
					// this happens once and awhile, there is no record in the metadata tag but
					// the header also doesn't specify that its a delete. just skip it
					if(documentList.size()==0)
						continue;
					
					Element recordDocumentElement = (Element)documentList.get(0);
					
					String recordDocument = recordDocumentElement.asXML();
					
					this.workspace.populateRecord(partnerId, recordDocument, null, null);
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
		} catch (DocumentException e) {
			throw new HarvestException(Config.Exceptions.OAI_RESPONSE_ERROR_CODE,
				"Error trying to parse through harvested files to populate workspace", e);
		}
	}
	
	/**
	 *  Create a param string based on metadataprefix and collection set id. 
	 *  This is public because other processors might use this for logging. 
	 */
	public static String createParamString(String collection_set, String metadataPrefix)
	{
		ArrayList<String> params_list = new ArrayList<String>();
		params_list.add("verb=ListRecords");
		params_list.add(String.format("metadataPrefix=%s", 
				metadataPrefix));
		if(collection_set != null)
			params_list.add(String.format("set=%s", collection_set));
		return  '?' + StringUtils.join(params_list, "&");
	}
	
}
