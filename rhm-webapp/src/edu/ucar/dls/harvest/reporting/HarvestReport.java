package edu.ucar.dls.harvest.reporting;

import org.apache.commons.lang3.exception.ExceptionUtils;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.resources.DBLogger;
import edu.ucar.dls.harvest.workspaces.Workspace;

public class HarvestReport {
	protected DBLogger logger = null;
	protected String uuid;
	protected boolean isTest = false;
	
	
	public static final String HARVEST_EXCEPTION_MSG = 
			"%s\n" +
			"Info: %s\n" +
			"Context %s.";
	
	public static final String GENERIC_EXCEPTION_MSG = 
		"Un-managed exception was caught\n" +
		"Message: %s\n" +
		"%s";
	
	public static final String HARVEST_SUCCESS_MSG = 
		"Harvest was successful %s.\n" +
		"%d out of %d records were added to the repository.";
	
	public static final String STATUS_CODE_SUCCESS_MSG = 
		"%d out of %d records were added to the repository.";

	public static final String TEST_HARVEST_MSG = 
		"Test mode: This report shows what would occur in a real harvest, "+
		"however no records were modified in the repository database.";
	
	public HarvestReport(Workspace workspace) throws HarvestException
	{
		this.logger = ((DBLogger)workspace.getResource(
				DBLogger.class));
		this.uuid = workspace.harvestRequest.getUuid();
		this.isTest = workspace.harvestRequest.getIsTest();
	}
	
	public HarvestReport(String uuid, boolean isTest) throws HarvestException
	{
		this.logger = new DBLogger(uuid);
		this.uuid = uuid;
		this.isTest = isTest;
	}
	
	public DBLogger getLogger() {
		return this.logger;
	}
	
	private void logGlobalMessages() {
		if(this.isTest)
			this.logger.logInfo(TEST_HARVEST_MSG);
	}
	
	public void reportHarvestFailure(HarvestException e) {
		// If the exception was due to an interruption chances are it is
		// because of a timeout, we are assuming this to be consistent
		// with the harvest manager
		if(e.getErrorCode().equals(Config.Exceptions.THREAD_INTERRUPTED))
			reportHarvestTimeout(e);
		else
		{
			String msg = String.format(HARVEST_EXCEPTION_MSG,
										e.getErrorCode(),
										e.getError(),
										e.getContext());
			this.logger.logFinalResultFailure(msg);
			this.logGlobalMessages();
			this.reportResultsToHarvestManager("4", e.getErrorCode());
		}
	}
	
	public void reportHarvestFailure(Exception e) {
		String msg = String.format(GENERIC_EXCEPTION_MSG,
				e.getMessage(),
				ExceptionUtils.getStackTrace(e));
		this.logger.logFinalResultFailure(msg);
		this.logGlobalMessages();
		this.reportResultsToHarvestManager("4", Config.Exceptions.UNHANDLED_ERROR_CODE);
	}
	
	public void reportHarvestTimeout(HarvestException e) {
		String msg = String.format(HARVEST_EXCEPTION_MSG,
				e.getErrorCode(),
				e.getError(),
				e.getContext());
		this.logger.logFinalResultFailure(msg);
		this.logGlobalMessages();
		this.reportResultsToHarvestManager("5", e.getErrorCode());
	}
	
	
	public void reportHarvestSuccess(Workspace workspace, int[] issueCounts) throws HarvestException {

		String withMsg = "";
		String logResultsCode;
		if(issueCounts[0]>0)
		{
			withMsg = "with record errors";
			logResultsCode = "6";
		}
		else if(issueCounts[1]>0)
		{
			withMsg = "with record warnings";
			logResultsCode = "3";
		}
		else
		{
			logResultsCode = "2";
		}
		int validRecordCount = workspace.getValidRecordCount();
		String msg = String.format(HARVEST_SUCCESS_MSG, withMsg,
			 workspace.getValidRecordCount(), workspace.getRecordCount()
				);

		if(issueCounts[0]>0)
			this.logger.logFinalResultSuccessWithErrors(msg);
		else if(issueCounts[1]>0)
			this.logger.logFinalResultSuccessWithWarnings(msg);
		else
			this.logger.logFinalResultSuccess(msg);
		this.logGlobalMessages();
		String statusCodeDescription = String.format(STATUS_CODE_SUCCESS_MSG, validRecordCount, 
				workspace.getRecordCount());
		this.reportResultsToHarvestManager(logResultsCode, statusCodeDescription);
	}
	
	private void reportResultsToHarvestManager(String logResultsCode, 
				String logResultsCodeDescription) {
		if(Config.Reporting.harvestManager!=null)
		{
			try {
				Config.Reporting.harvestManager.logHarvestStatus(
						logResultsCode, logResultsCodeDescription, this.uuid, 
							new java.util.Date().toString());
			} catch (Exception e1) {
				// Not good to get here, how are we supposed to notify the maganger we are
				// Done, lets just log to the DB and hope for the best. Maybe come up with 
				// another solution
			}
		}
		/*Old way by calling http
		 * java.util.Date now= new java.util.Date();
		ArrayList<String> params_list = new ArrayList<String>();
		params_list.add("uuid="+this.uuid);
		params_list.add("status="+logResultsCode);
		params_list.add("description="+logResultsCodeDescription);
		params_list.add("ts="+now.toString());
		String postData = StringUtils.join(params_list, "&");
		try {
			TimedURLConnection.importURL(Config.LOG_RESULTS_CALLBACK_URL, postData,
					"UTF-8", 10000, null);
		} catch (Exception e) {
			// Not good to get here, how are we supposed to notify the maganger we are
			// Done, lets just log to the DB and hope for the best. Maybe come up with 
			// another solution
		} */
		
	}
	public void clean()
	{
		this.logger.clean();
	}
	
}
