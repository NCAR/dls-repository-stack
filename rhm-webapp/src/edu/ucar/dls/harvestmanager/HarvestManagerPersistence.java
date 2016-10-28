package edu.ucar.dls.harvestmanager;

import org.dlese.dpc.xml.Dom4jUtils;
import org.dlese.dpc.oai.OAIUtils;
import org.dom4j.*;
import org.dom4j.tree.*;

import edu.ucar.dls.harvest.Config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;


/**
 *  Persists HarvestManager related data.
 *
 * @author    John Weatherley
 */
public final class HarvestManagerPersistence {

	private boolean debug = true;
	
	private int MAX_TRIGGERS_LOGGED_PER_COLLECTION = 400;
	private File _persistentDataDir = null;
	private File _triggerHistoriesDir = null;
	private File _lastTriggerdDataFile = null;
	private File _localCollectionsXmlFile = null;
	
	/**  Constructor for the HarvestManagerPersistence object */
	public HarvestManagerPersistence(String persistentDataDir) throws Exception {
		
		_persistentDataDir = new File( persistentDataDir );
		if(!_persistentDataDir.exists() && !_persistentDataDir.mkdirs())
			throw new Exception("HarvestManagerPersistence unable to create the location for persistent data files '" + _persistentDataDir + "'"); 

		if(!_persistentDataDir.canWrite())
			throw new Exception("HarvestManagerPersistence unable to write to the location for persistent data files '" + _persistentDataDir + "'"); 				
			
		_localCollectionsXmlFile = new File(_persistentDataDir,"collection_records.xml");
		_lastTriggerdDataFile = new File(_persistentDataDir,"last_triggered_data.xml");
		_triggerHistoriesDir = new File(_persistentDataDir,"trigger_histories");
		_triggerHistoriesDir.mkdirs();
	}

	
	protected Document getCollectionsDocument() throws Exception {
		synchronized(_localCollectionsXmlFile){
			if(!_localCollectionsXmlFile.exists())
				return null;
			return Dom4jUtils.getXmlDocument(_localCollectionsXmlFile);
		}
	}
	
	protected void setCollectionsDocument(Document collections) throws Exception {
		synchronized(_localCollectionsXmlFile){
			Dom4jUtils.writePrettyDocToFile(collections,_localCollectionsXmlFile);
		}
	}
	
	public LastTriggeredDAO getLastTriggeredDAO(String id) {
		Date lastTriggerDate = null;
		try{
			Document lastTriggerdDataDocument = getLastTriggerdDataDocument();
			if(lastTriggerdDataDocument != null) {
				
				Element root = lastTriggerdDataDocument.getRootElement();
				Element triggerElm = (Element) root.selectSingleNode("HarvestTriggered[@id=\"" + id + "\"]");

				// If we have a trigger event for this uuid:
				if(triggerElm != null) {
					Date reprocessTimestamp = null;
					String reprocessTimestampString = triggerElm.valueOf("@reprocessTimestamp");
					if(reprocessTimestampString!=null && !reprocessTimestampString.trim().equals(""))
						reprocessTimestamp = OAIUtils.getDateFromDatestamp(reprocessTimestampString);

					return new LastTriggeredDAO(
						OAIUtils.getDateFromDatestamp(triggerElm.valueOf(".")),
						triggerElm.valueOf("@statusCode"),
						triggerElm.valueOf("@statusTimeStamp"),
						triggerElm.valueOf("@uuid"),
						triggerElm.valueOf("@runType"),
						triggerElm.valueOf("@harvestType"),
						reprocessTimestamp);
				}
			}
		} catch (Throwable t) { t.printStackTrace();}
		//prtln("getLastTriggerDate() for id: " + id + " lastTriggerDate: " + lastTriggerDate);
		return null;
	}
		
	protected void logTriggerEvent(String collectionTitle, String id, String uuid, 
			String triggerDate, String harvestType, String runType, String protocol, 
				Document triggerFileDoc) throws Exception {
			synchronized(_lastTriggerdDataFile) {
			
			// --- Write the last trigger date to file --- :
						
			// If this is not a test harvest, write last trigger date to file:
			if(!runType.equalsIgnoreCase("test")) {
				synchronized(_lastTriggerdDataFile) {
					Document lastTriggerdDataDoc = getLastTriggerdDataDocument();
					if(lastTriggerdDataDoc == null) {
						lastTriggerdDataDoc = DocumentHelper.createDocument();
						lastTriggerdDataDoc.addElement("LastTriggeredDates");
					}
					
					Element root = lastTriggerdDataDoc.getRootElement();
					Element triggerElm = (Element) root.selectSingleNode("HarvestTriggered[@id=\"" + id + "\"]");
					
					boolean newOne = false;
					// Create a new trigger element if does not exists:
					if(triggerElm == null) {
						triggerElm = new DefaultElement("HarvestTriggered");
						triggerElm.addAttribute("id",id);
						newOne = true;
					}
					// Move it to the end of the list if previously existed:
					else
						triggerElm.detach(); 
						
					// If the initial harvest is from source(really never should be) we
					// really need to trigger file to have all the fields therefore it 
					// will not be considered a reprocess but a harvest
					if(!newOne && protocol.equals(Config.Protocols.HARVESTED_RECORDS_DB))
					{
						// Otherwise we just add the reprocess timestamp onto the element
						// and don't change a thing on the record
						triggerElm.addAttribute("reprocessTimestamp",triggerDate);
					}
					else
					{
						triggerElm.setText(triggerDate);
						
						Attribute statusCode = triggerElm.attribute("statusCode");
						
						if(statusCode != null)
							triggerElm.remove(statusCode);
						
						triggerElm.addAttribute("uuid",uuid);
						triggerElm.addAttribute("runType",runType);
						triggerElm.addAttribute("harvestType",harvestType);

						Attribute statusTimeStamp = triggerElm.attribute("statusTimeStamp");
						if(statusTimeStamp != null)
							triggerElm.remove(statusTimeStamp);

					}
					root.add(triggerElm);
				
					Dom4jUtils.writePrettyDocToFile(lastTriggerdDataDoc,_lastTriggerdDataFile);
				}
			}
			
			// --- Write the trigger event to file for this collection --- :			
			synchronized(_triggerHistoriesDir) {
				// Add this trigger event to the history for this collection:
				Document harvestHistoryDocument = getHarvestTriggerDocument(id);
				
				// If no Document exists, create it:
				if(harvestHistoryDocument == null) {
					harvestHistoryDocument = new DefaultDocument(new DefaultElement("TriggerHistory"));
					Element collInfo = new DefaultElement("collectionInfo");
					collInfo.addElement("title").setText(collectionTitle);
					collInfo.addElement("id").setText(id);
					harvestHistoryDocument.getRootElement().add(collInfo);
					harvestHistoryDocument.getRootElement().add(new DefaultElement("triggerEvents"));
				}
				
				// Create the triggerEvent element:
				Element triggerEvent = ((Element)harvestHistoryDocument.selectSingleNode("/TriggerHistory/triggerEvents")).addElement("triggerEvent");
				triggerEvent.addAttribute("date",triggerDate);
				triggerEvent.addAttribute("harvestType",harvestType);
				triggerEvent.addAttribute("uuid",uuid);
				triggerEvent.addElement("triggerFile").add(triggerFileDoc.getRootElement());
				
				// Remove oldest log entry if reached max size:
				int numTriggerEvents = harvestHistoryDocument.selectNodes("/TriggerHistory/triggerEvents/triggerEvent").size();
				if(numTriggerEvents> MAX_TRIGGERS_LOGGED_PER_COLLECTION)
					harvestHistoryDocument.selectSingleNode("/TriggerHistory/triggerEvents/triggerEvent").detach();
			
				// Write to file:
				Dom4jUtils.writePrettyDocToFile(harvestHistoryDocument,new File(_triggerHistoriesDir,id+"-trigger-history.xml"));
			}
		}
	}
	
	protected void logHarvestStatus(String statusCode, String description, String uuid, String timeStamp) throws Exception {
		
		// Write the latest status for this collection/uuid:
		synchronized(_lastTriggerdDataFile) {
			Document lastTriggerdDataDoc = getLastTriggerdDataDocument();
			if(lastTriggerdDataDoc != null) {
				Element root = lastTriggerdDataDoc.getRootElement();
				Element triggerElm = (Element) root.selectSingleNode("HarvestTriggered[@uuid=\"" + uuid + "\"]");
				
				// If we have a trigger event for this uuid:, for harvests by harvested DB this will be null since
				// we don't store the uuid in last harvested files. This is what we want since we don't want to touch
				// when it was actually harvested from a real source
				if(triggerElm != null) {
					// Move it to the end of the list:
					triggerElm.detach(); 
				
					// Set the status and timeStamp:
					triggerElm.addAttribute("statusCode",statusCode);
					triggerElm.addAttribute("statusCodeDescription",description);
					triggerElm.addAttribute("statusTimeStamp",timeStamp);
					root.add(triggerElm);
				
					Dom4jUtils.writePrettyDocToFile(lastTriggerdDataDoc,_lastTriggerdDataFile);
				}
			}
		}		
		
		// Write the harvest history for this collection:
		synchronized(_triggerHistoriesDir) {
			String id = HarvestManager.getIdFromUuid(uuid);
				
			Document harvestHistoryDocument = getHarvestTriggerDocument(id);
			
			// Check to make sure there is a trigger for this UUID:
			if(harvestHistoryDocument == null)
				throw new Exception("UUID " + uuid + " does not exists (1)");
			
			// If no Document exists, error:
			Element triggerEvent = (Element)harvestHistoryDocument.selectSingleNode("/TriggerHistory/triggerEvents/triggerEvent[@uuid='"+uuid+"']");
			if(triggerEvent == null)                       
				throw new Exception("UUID " + uuid + " does not exists (2)");
			
			Element harvestStatus = (Element)triggerEvent.selectSingleNode("harvestStatus");
			if(harvestStatus != null) {
				harvestStatus.detach();
				//throw new Exception("UUID " + uuid + " has already logged a status.");
			}
			
			harvestStatus = new DefaultElement("harvestStatus");
			harvestStatus.addElement("statusCode").setText(statusCode);
			harvestStatus.addElement("statusCodeDescription").setText(description);
			harvestStatus.addElement("timeStamp").setText(timeStamp);
			harvestStatus.addElement("statusRecievedAtTime").setText(OAIUtils.getDatestampFromDate(new Date()));
			triggerEvent.add(harvestStatus);
			
			// Write to file:
			Dom4jUtils.writePrettyDocToFile(harvestHistoryDocument,new File(_triggerHistoriesDir,id+"-trigger-history.xml"));		
		}
	} 

	public Document getLastTriggerdDataDocument() throws Exception {
		synchronized(_lastTriggerdDataFile) {
			if(!_lastTriggerdDataFile.exists())
				return null;
			return Dom4jUtils.getXmlDocument(_lastTriggerdDataFile);
		}
	}
	
	protected Document getHarvestTriggerDocument(String id) throws Exception {
		synchronized(_triggerHistoriesDir){
			File f = new File(_triggerHistoriesDir,id+"-trigger-history.xml");
			if(!f.exists())
				return null;
			return Dom4jUtils.getXmlDocument(f);
		}
	}
	
	protected boolean getHasHarvestTriggerHistory(String id) {
		synchronized(_triggerHistoriesDir) {
			File f = new File(_triggerHistoriesDir,id+"-trigger-history.xml");
			return f.exists();
		}
	}	
	

	//================================================================


	/**
	 *  Return a string for the current time and date, sutiable for display in log files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	public static String getDateStamp() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final void prtlnErr(String s) {
		System.err.println(getDateStamp() + " HarvestManagerPersistence Error: " + s);
	}



	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final void prtln(String s) {
		if (debug)
			System.out.println(getDateStamp() + " HarvestManagerPersistence: " + s);
	}


	/**
	 *  Sets the debug attribute.
	 *
	 * @param  db  The new debug value
	 */
	public final void setDebug(boolean db) {
		debug = db;
	}
}

