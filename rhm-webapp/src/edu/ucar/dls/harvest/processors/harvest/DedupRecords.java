package edu.ucar.dls.harvest.processors.harvest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.processors.Processor;
import edu.ucar.dls.harvest.workspaces.ResultsWrapper;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 * Workspace processor that removes duplicate records from the workspace along
 * with adding a warning specifying what was removed. A duplicate record is one
 * that has a partner id that is already exists in the workspace. Using the logic
 * here we end up with the first record is the one we keep
 *
 */
@XmlRootElement(name="DedupRecords")
public class DedupRecords extends Processor {
	
	public static final String PRIORITY_FIRST  = "first";
	public static final String PRIORITY_LAST  = "last";
	private String dedupBy = Config.Groupings.BY_PARTNER_ID;
	private String priority = PRIORITY_FIRST;

	String DUPLICATE_RECORDS_ERROR_MSG = "%d records were found in harvest for identifier " +
	    "%s. Only the %s record will processed and the rest will be ignored.";

	public void initialize(Workspace workspace) throws HarvestException
	{
		super.initialize(workspace);
		if(!(this.dedupBy.equals(Config.Groupings.BY_PARTNER_ID)||
				this.dedupBy.equals(Config.Groupings.BY_URL)))
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
				String.format("%s is not a valid grouping for dedup records.", this.dedupBy), 
				"DedupRecords.initialize()");
	}
	public void run() throws HarvestException {
		ResultsWrapper countsDataResultsWrapper = null;
		
		if (this.dedupBy.equals(Config.Groupings.BY_PARTNER_ID))
			countsDataResultsWrapper = workspace.getDuplicateRecordCountsByPartnerID();
		else if(this.dedupBy.equals(Config.Groupings.BY_URL))
			countsDataResultsWrapper = workspace.getDuplicateRecordCountsByURL();

		Iterator<Object[]> countDataResults = countsDataResultsWrapper.getResults();
		if(countDataResults==null)
			return;
		
		// We use a list to keep track of what records that we want to mark invalid.
		// To make it so we only have to call one update query
		List<String> invalidRecordsIdList = new ArrayList<String>();
		while (countDataResults.hasNext())
		{
			Object[] countData = countDataResults.next();
			
			if(countData==null)
				break;
			String identifier = (String)countData[0];
			int count = ((Long)countData[1]).intValue();
			// If results came back it should be greater then, this is a double
			// check just in case one forgot the having part of the sql
			if(count>1)
			{
				// now that we know there is more then one. We know get the records
				// have that partner id, add report a warning
				this.addWarning(identifier, String.format(
						DUPLICATE_RECORDS_ERROR_MSG, count, identifier, this.priority ));
				
				ResultsWrapper recordDataResultsWrapper = null;
				
				String order = Config.DatabaseOptions.ASCENDING;
				if(this.priority.equals(PRIORITY_LAST))
					order = Config.DatabaseOptions.DESCENDING;
					
				if (this.dedupBy.equals(Config.Groupings.BY_PARTNER_ID))
					recordDataResultsWrapper = workspace.getRecordIdsByPartnerId(identifier, order);
				else if(this.dedupBy.equals(Config.Groupings.BY_URL))
					recordDataResultsWrapper = workspace.getRecordIdsByURL(identifier, order);
				
				Iterator<Object[]> recordDataResult = recordDataResultsWrapper.getResults();
				
				if(!recordDataResult.hasNext())
				{
					throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
				          "Querying sql to find duplicate records by identifier is wrong." +
						"or has some issues, since there should have been countData Results.",
						"DedupRecords.run()");
				}
				
				// The first we want to ignore, that is the one we want to stay
				recordDataResult.next();

				// the rest are invalid, add them to the invalid record list
				while (recordDataResult.hasNext())
				{
					Object[] recordData = recordDataResult.next();
					String recordId = ((Integer)recordData[0]).toString();
					invalidRecordsIdList.add(recordId);
				}
				recordDataResultsWrapper.clean();
			}
			HarvestRequest.checkForInterruption(
				"DedupRecords.run()");
		}
		countsDataResultsWrapper.clean();
		
		// If any are in the invalid list mark them as invalid in the workspace
		if(invalidRecordsIdList.size()>0)
		{
			String[] invalidRecordIds = (String[]) invalidRecordsIdList.toArray(
				new String[invalidRecordsIdList.size()]);
		
			workspace.markInvalidRecords(invalidRecordIds);
		}
	}
	
	@XmlElement(name = "dedup-by")
	public void setDedupBy(String dedupBy) {
		this.dedupBy = dedupBy;
	}
	
	@XmlElement(name = "priority")
	public void setPriority(String priority) {
		this.priority = priority;
	}
}