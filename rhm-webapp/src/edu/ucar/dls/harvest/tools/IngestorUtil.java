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
package edu.ucar.dls.harvest.tools;

import java.util.List;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.reporting.HarvestReport;
import edu.ucar.dls.harvest.resources.DBLogger;
import edu.ucar.dls.harvest.workspaces.DBWorkspace;

/** 
 * Helper class used by code outside of harvest to update and clean
 * harvests, since they have no access to the threads that were initially
 * created for harvesting
 *
 */
public class IngestorUtil{
	public static final String HARVEST_TIMEOUT_MESSAGE = "Harvest has timed out, which " +
	"is set for %d days. Chances are its either a deadlock or the server crashed " +
	"while the harvest was going on.";
	
	/** 
	 * Method that is called when another process realizes that thread should
	 * be dead and cleaned up but possibly isn't. So this will try to clean
	 * up its workspace table and log a message in the reporting.
	 * @param uuid
	 * @param timeout
	 */
	public static void timoutOccuredCleanupUuid(String uuid, String mdpHandle, int timeout)
	{
		// First try to remove the uuid workspace table
		DBWorkspace workspace = new DBWorkspace();
		try {
			workspace.initialize(mdpHandle, uuid);
			workspace.clean();
			HarvestReport harvestReport = new HarvestReport(uuid, false);
			harvestReport.reportHarvestTimeout(new HarvestException(
					Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE,
					String.format(HARVEST_TIMEOUT_MESSAGE, timeout),
					"Cleanup.cleanup"));
			harvestReport.clean();
		} catch (HarvestException e) {
			// can't really do anything besides log and say its good,
			// since it had to be the reporting code that broke
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that gets the log deails based on a uuid. These details
	 * will be sorted by timestamp desc
	 * @param uuid
	 * @return
	 */
	public static List<String> getLogDetailList(String uuid)
	{
		DBLogger dbLogger = null; 
		try {
			dbLogger = new DBLogger(uuid);
			return dbLogger.getLogDetails();
		} catch (HarvestException e) {
			// Nothing we can do here besides log the error since it was
			// the reporting that threw the error
			e.printStackTrace();
		}
		finally
		{
			dbLogger.clean();
		}
		return null;
	}
	
}
