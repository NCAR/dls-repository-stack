package edu.ucar.dls.harvestmanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Node;

import edu.ucar.dls.harvest.Config;

/**
 * Threadable class that re-harvests all collections via harvested records db. This
 * class also keeps tracks of the harvested threads so it can create a summary detailing
 * what is currently running 
 * @author dfinke
 *
 */
public class Reharvester implements Runnable {
	private HarvestManager harvestManager;
	private Date lastHarvestStart = null;
	private Date lastHarvestEnd = null;
	private List<String> collectionsHarvestStarted = new ArrayList<String>();
	private static int MAX_REHARVEST_THREADS = 5;
	private static int WAIT_TIME_BETWEEN_THREADS_CHECK = 300000;
	
	/**
	 * Constructor for the re-harvester that takes the harvest manager so it can 
	 * have access to the harvest threads and trigger harvests
	 * @param harvestManager
	 */
	public Reharvester(HarvestManager harvestManager)
	{
		this.harvestManager = harvestManager;
	}
	
	/**
	 * Run method to run the re-harvester. Note nothing is stopping something from calling
	 * this more then once, Since you could without any issues. But since
	 */
	public void run() {
		// Clear the started list so we can start from scratch on the summary
		this.collectionsHarvestStarted.clear();
		this.lastHarvestEnd = null;
		this.lastHarvestStart = new Date();
		List recordNodes = (List) this.harvestManager.getCollectionsDocument().selectNodes("/DDSWebService/Search/results/record");
		boolean interrupt = false;
		if (recordNodes == null || recordNodes.size() == 0)
			return;

		/// Loop through all collection that are in the DCS
		for (int j = 0; j < recordNodes.size(); j++) {
			String collectionId = ((Node) recordNodes.get(j)).valueOf("head/id");

			ConcurrentHashMap<String, Thread> ingestorThreads= null;
			try
			{
				ingestorThreads = harvestManager.getIngestorThreads();
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				System.err.println(
				"Ingestor threads have been corrupted aborting reharvesting");
				this.lastHarvestEnd = new Date();
				return;
			}
			
			if(ingestorThreads.containsKey(collectionId))
			{
				try {
					ingestorThreads.get(collectionId).join();
				} catch (InterruptedException e1) {
					interrupt = true;
				}
			}
			
			// We don't want to have more then 15 being harvested at the same time, since
			// these triggers will be processor and memory heavy. 15 is just an estimate which 
			// seems to work alright
			while(!interrupt && ingestorThreads.size()>MAX_REHARVEST_THREADS)
			{
				try {
					Thread.sleep(WAIT_TIME_BETWEEN_THREADS_CHECK);
					ingestorThreads = harvestManager.getIngestorThreads();
				} catch (InterruptedException e) {
					interrupt = true;
				}catch (Exception e2) {
					System.err.println(
					"Ingestor threads have been corrupted aborting reharvesting");
					this.lastHarvestEnd = new Date();
					return;
				}
			}

			// Check for interruptions before we trigger the harvest
			if(interrupt || Thread.currentThread().isInterrupted())
			{
				System.err.println(
						"Thread has been interrupted by system, aborting re-harvesting");
				this.lastHarvestEnd = new Date();
				return;
			}
			try {
				// Trigger the harvest using the protocol of HARVESTED_RECORDS_DB
				this.harvestManager.triggerHarvest(collectionId, "full_reharvest", false, 
						Config.Protocols.HARVESTED_RECORDS_DB);
				// Add it to the started collections so we can report on them in real time
				this.collectionsHarvestStarted.add(collectionId);
				// Wait 5 seconds to make sure that the ingestor thread count is right
				try {
					Thread.sleep(10000);
				}
				catch (InterruptedException e) {
					interrupt = true;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.lastHarvestEnd = new Date();
	}
	
	/**
	 * Get the real time status the reharvester. It does this by comparing what is currently running in 
	 * the thread list to whats been started
	 * @return HashMap<String, Object>
	 * @throws Exception
	 */
	public HashMap<String, Object> getSummaryMap() throws Exception
	{
		Map ingestorThreads = harvestManager.getIngestorThreads();
		HashMap<String, Object> summary = new HashMap<String, Object>();
		
		String reharvestStatus = "";
		List recordNodes = (List) this.harvestManager.getCollectionsDocument().selectNodes(
					"/DDSWebService/Search/results/record");
		List<HashMap<String, String>> collectionSummaryList = new ArrayList<HashMap<String, String>>();
		
		// Loop through all collections in the DCS
		for (int j = 0; j < recordNodes.size(); j++) {
			boolean started = false;
			boolean harvestRunning = false;
			String status = "";
			String collectionId = ((Node) recordNodes.get(j)).valueOf("head/id");
			String collectionName = ((Node) recordNodes.get(j)).valueOf("metadata/record/general/title");
			if(collectionsHarvestStarted.contains(collectionId))
				started = true;
			
			if(ingestorThreads.containsKey(collectionId))
				harvestRunning = true;
			
			// Calculate the status. on_hold ones is the interesting status. Since this means that
			// something else started the harvest for a collection before the reharvester got to it.
			// Might have been a manual start or the automatic one. Either way its on hold until it
			// finishes. The run method actually freezes until its done.
			if(started && !harvestRunning)
				status = "completed";
			else if(!started && harvestRunning)
				status = "on_hold";
			else if(started)
				status = "started";
			HashMap<String, String> collectionSummary = new HashMap<String, String>();
			
			if( this.lastHarvestStart!=null && this.lastHarvestEnd==null)
				reharvestStatus = "running";
			else if(this.lastHarvestStart!=null && this.lastHarvestEnd!=null)
				reharvestStatus = "finished";
			else
				reharvestStatus = "not_ran";
			collectionSummary.put("status", status);
			collectionSummary.put("collectionId", collectionId);
			collectionSummary.put("collectionName", collectionName);
			collectionSummaryList.add(collectionSummary);
		}
		
		// Add everything to the HashMap
		summary.put("collectionSummaryList", collectionSummaryList);
		summary.put("lastHarvestStart", lastHarvestStart);
		summary.put("lastHarvestEnd", lastHarvestEnd);
		summary.put("reharvestStatus", reharvestStatus);
		return summary;
		
	}
	
}
