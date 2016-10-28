package edu.ucar.dls.harvest.processors.repository;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.repository.Repository;
import edu.ucar.dls.harvest.resources.HarvestedRecordsDB;
import edu.ucar.dls.harvest.workspaces.ResultsWrapper;

/**
 * Repository Processor populates the HarvestedRecords Database with the
 * current session in the workspace if the harvest isn't use the database
 * for the source of the harvest
 *
 */
public class PopulateHarvestedRecordsDB extends RepositoryProcessor{
	
	/**
	 * Run the processor, This will grab the current workspace and use it to
	 * populate the harvestedRecords database. This query of the workspace includes
	 * error records which also will make their way to the harvestedRecords db but
	 * with the repository_record as false set.
	 */
	public void run(Repository repository) throws HarvestException
	{
		// We do not want to run this for a harvest that is already harvesting
		// from this table
		if(this.workspace.harvestRequest.getProtocol().equals(
				   Config.Protocols.HARVESTED_RECORDS_DB))
				return;

		String setSpec = this.workspace.harvestRequest.getSetSpec();
		HarvestedRecordsDB harvestedRecordsDB = (HarvestedRecordsDB)this.workspace.getResource(
				HarvestedRecordsDB.class);
		String currentSessionId = repository.getCurrentSessionId();
		String dataFormat = this.workspace.harvestRequest.getNativeFormat();
		
		// Need to make sure we grab this before we start populating the database with the 
		// new session since we will delete it after the population finishes
		String previousHarvestedSession = harvestedRecordsDB.getLatestSession(setSpec);
		
		ResultsWrapper resultsWrapper = this.workspace.getHarvestedRecords();
		
		try{
			Iterator results = resultsWrapper.getResults();
			while (results.hasNext())
			{	
				Object[] recordData = (Object[])results.next();
				
				String partnerId = (String)recordData[0];
				
				String data = null;
				if(recordData[1]!=null)
				{
					try {
						data = new String((byte[])recordData[1], Config.ENCODING);
					} catch (UnsupportedEncodingException e) {
						throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
								"Cannot convert byte string to encoding utf-8", e);
					}
				}
				String url = (String)recordData[2];
				Date workingDate = (Date)recordData[3];
				int validRecord = ((Integer)recordData[4]).intValue();
				boolean repositoryRecord = false;
				
				// We assume that the repository only adds valid records, which that
				// is how it was originally coded.
				if(validRecord==1)
					repositoryRecord = true;
				
				harvestedRecordsDB.populateRecord(setSpec, partnerId, data, dataFormat, 
						currentSessionId, repositoryRecord, url, workingDate);	
			}
			
		}
		catch(HarvestException e)
		{
			System.out.println("PopulateHarvestedRecordsDB.run() Error: " + e);
			// We had an error someplace, remove what we just populated
			harvestedRecordsDB.removeSessionHarvestedRecords(setSpec, currentSessionId );
			throw e;
		}
		catch(Exception e)
		{
			System.out.println("PopulateHarvestedRecordsDB.run() Error: " + e);
			harvestedRecordsDB.removeSessionHarvestedRecords(setSpec, currentSessionId );
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
					"Un-caught error trying to populate harvestdRecords db.", e);
		}
		finally
		{
			// No longer need the workspace result set, clean it
			resultsWrapper.clean();
		}
		
		// finally if there is a previous session for the setSpec remove it
		if(previousHarvestedSession!=null)
			harvestedRecordsDB.removeSessionHarvestedRecords(setSpec, previousHarvestedSession );	
	}
}