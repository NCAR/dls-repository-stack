package edu.ucar.dls.harvestmanager;

import java.util.Date;

/**
 *  DAO that holds data about the last harvest triggered for a given collection and uuid.
 *
 * @author    John Weatherley
 */
public final class LastTriggeredDAO {

	private Date _lastTriggeredDate = null;
	private String _lastTriggeredStatus = null;
	private String _lastTriggeredTimeStamp = null;
	private String _runType = null;
	private String _harvestType = null;
	private String _uuid;
	private Date _lastReprocessDate = null;

	/**
	 *  Constructor for the LastTriggeredDAO object
	 *
	 * @param  lastTriggeredDate       Last trigger date
	 * @param  lastTriggeredStatus     Status for this harvest
	 * @param  lastTriggeredTimeStamp  Timstamp for status
	 * @param  uuid                    UUID of this harvest
	 */
	public LastTriggeredDAO(Date lastTriggeredDate, String lastTriggeredStatus, 
			String lastTriggeredTimeStamp, String uuid, String runType, String harvestType,
			Date lastReprocessDate) {
		_lastTriggeredDate = lastTriggeredDate;
		_lastTriggeredStatus = lastTriggeredStatus;
		_lastTriggeredTimeStamp = lastTriggeredTimeStamp;
		_runType = runType;
		_harvestType = harvestType;
		_uuid = uuid;
		
		// Even if a re-process date is sent in that doesn't necessarily its a 
		// re-process date. Because its only a date that should be seen if
		// its after the last succesfull harvest. The details to actually figure
		// that out are complicated and unnecessary. Since for the most part always
		// showing it if there was a failure or if the lastReprocess date is after
		// the last triggered date which in this case would be successful
		if(lastTriggeredStatus.equals("4")||lastTriggeredStatus.equals("5")||
			(lastReprocessDate!=null && lastTriggeredDate!=null && 
					lastReprocessDate.after(lastTriggeredDate)))
		{
			_lastReprocessDate = lastReprocessDate;			
		}
		
	}


	/**
	 *  Gets the lastTriggeredDate attribute of the LastTriggeredDAO object
	 *
	 * @return    The lastTriggeredDate value
	 */
	public Date getLastTriggeredDate() {
		return _lastTriggeredDate;
	}


	/**
	 *  Gets the lastTriggeredStatus attribute of the LastTriggeredDAO object
	 *
	 * @return    The lastTriggeredStatus value
	 */
	public String getLastTriggeredStatus() {
		return _lastTriggeredStatus;
	}


	/**
	 *  Gets the lastTriggeredTimeStamp attribute of the LastTriggeredDAO object
	 *
	 * @return    The lastTriggeredTimeStamp value
	 */
	public String getLastTriggeredTimeStamp() {
		return _lastTriggeredTimeStamp;
	}


	/**
	 *  Gets the uuid attribute of the LastTriggeredDAO object
	 *
	 * @return    The uuid value
	 */
	public String getUuid() {
		return _uuid;
	}
	public String getRunType() {
		return _runType;
	}


	public String getHarvestType() {
		return _harvestType;
	}
	
	public Date getLastReprocessDate() {
		return _lastReprocessDate;
	}

}

