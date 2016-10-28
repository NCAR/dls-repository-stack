package edu.ucar.dls.harvest.processors.repository;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.repository.Repository;
import edu.ucar.dls.harvest.workspaces.ResultsWrapper;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 * Processor that does comparisons against the new session records and the
 * current session records. Coming up with counts of what are new records,
 * changed records and deleted records. Even though in the repository
 * we overwrite the current session. This is useful for the admin team to see which
 * collections are changing every month and which are static
 *
 */
public class UpdatedSessionRecordComparison extends RepositoryProcessor{
	
	private final static String INFO_MSG = "Metadata summary:\n" +
										   "New Records: %d\n"+
										   "Deleted Records: %d\n" +
										   "Changed Records: %d";
	
	/**
	 * Come up with that stats by comparing the current session with the new session
	 * records. The basic idea here, is that we query for all the records and order them
	 * by partner id. Then loop through, if on the same index they are the same we can
	 * compare them and figure out if anything has changed and move on. If they are not
	 * the same we can compare the parnterids between the two figuring out which one
	 * has the extra element. Thus knowing if its a delete or new record. Then moving on
	 * with the one that has the extra item.
	 */
	public void run(Repository repository) throws HarvestException
	{
		String currentSessionId = repository.getCurrentSessionId();

		int newRecordCount = 0;
		int updatedRecordCount = 0;
		int deletedRecordCount = 0;
		
		ResultsWrapper workspaceResultsWrapper = workspace.getRecords(
				Workspace.VALIDATED_RECORD_TYPE);
		ResultsWrapper reposResultsWrapper = repository.getSessionMetadata(currentSessionId);
		
		Iterator workspaceIterator = workspaceResultsWrapper.getResults();
		Iterator reposIterator = reposResultsWrapper.getResults();
		
		// Note we don't call hasNext here. Because of flaws in resultsWrapper.
		// calling next when there is not any returns null
		Object[] workspaceRecordData = (Object[])workspaceIterator.next();
		Object[] reposRecordData = (Object[])reposIterator.next();
		
		
		String workspacePartnerid = null;
		String reposPartnerid = null;
		
		// Keep looping until both the repos and the workspace items have been
		// iterated over
		while(reposRecordData!=null || workspaceRecordData!=null)
		{
			// If there were deletes workspace might have run out of items and
			// the compare keeps going. In this case the rest are deletes
			if(workspaceRecordData!=null)
				workspacePartnerid = ((String)workspaceRecordData[2]).toString();
			else
				workspacePartnerid = null;
			
			// Towards the end iterating the repos might not have the new
			// items so it will be null from here on out Then the workspace
			//records are just new records
			if(reposRecordData!=null)
				reposPartnerid = ((String)reposRecordData[0]).toString();
			else
				reposPartnerid = null;
			
			int compareResult=0;
			
			if(workspacePartnerid!=null&&reposPartnerid!=null)
				compareResult = workspacePartnerid.compareTo(reposPartnerid);
			else
				compareResult = 0;
			
			// if the new record comes before the current session one its a new record
			if(compareResult<0 || reposRecordData==null)
			{
				newRecordCount++;
				// move the iterator forward
				if(workspaceIterator.hasNext())
					workspaceRecordData = (Object[])workspaceIterator.next();
				else
					workspaceRecordData = null;
			}
			
			// If the new record comes after the current session one its a delete
			else if(compareResult>0 || workspaceRecordData==null)
			{
				deletedRecordCount++;
				// Move the iterator forward
				if(reposIterator.hasNext())
					reposRecordData = (Object[])reposIterator.next();
				else
					reposRecordData = null;
			}
			else
			{
				// They have the same partner id. Compare there record
				String workspaceNativeRecord=null;
				String reposNativeRecord = null;
				try {
					workspaceNativeRecord = new String((byte[])workspaceRecordData[1], Config.ENCODING);
					reposNativeRecord = new String((byte[])reposRecordData[1], Config.ENCODING);
				} catch (UnsupportedEncodingException e) {
					throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
							String.format("Encoding %s is unsupported", Config.ENCODING), e);
				}
				
				
				// compare their native record
				if(!workspaceNativeRecord.equals(reposNativeRecord))
					updatedRecordCount++;
				
				// In this case we move both iterators forward
				if(workspaceIterator.hasNext())
					workspaceRecordData = (Object[])workspaceIterator.next();
				else
					workspaceRecordData = null;
				
				if(reposIterator.hasNext())
					reposRecordData = (Object[])reposIterator.next();
				else
					reposRecordData = null;
			}
			HarvestRequest.checkForInterruption(
					"UpdatedSessionRecordComparison.run()");
		}
		workspaceResultsWrapper.clean();
		reposResultsWrapper.clean();
		
		// Add the info to it will be seen by reporting
		this.addInfo(this.name, String.format(INFO_MSG, newRecordCount,
				deletedRecordCount, updatedRecordCount ));
	}
}
