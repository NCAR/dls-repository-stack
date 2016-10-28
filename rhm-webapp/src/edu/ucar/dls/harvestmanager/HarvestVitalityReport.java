package edu.ucar.dls.harvestmanager;

import java.util.Date;
import java.io.*;

/**
 *  Bean that holds and manages data about the vitality of an OAI data provider.
 *
 * @author    John Weatherley
 */
public class HarvestVitalityReport implements Serializable {
	private String collectionId;
	private String collectionName;
	private String[] previousSetsFound;
	private ResponseFailure[] responseFailures;
	private MissingFormatReport[] missingFormatReports;
	private MissingSetsReport[] missingSetsReports;
	private SetsChangedReport[] setsChangedReports;

	
	/**  Constructor for the HarvestVitalityReport object */
	public HarvestVitalityReport() { }	

	/**  Constructor for the HarvestVitalityReport object */
	public HarvestVitalityReport(String collectionId, String collectionName) {
		this.collectionId = collectionId;
		this.collectionName = collectionName;
	}
		
	/**
	 *  Returns the value of collectionId.
	 *
	 * @return    The collectionId value
	 */
	public String getCollectionId() {
		return collectionId;
	}


	/**
	 *  Sets the value of collectionId.
	 *
	 * @param  collectionId  The value to assign collectionId.
	 */
	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}


	/**
	 *  Returns the value of collectionName.
	 *
	 * @return    The collectionName value
	 */
	public String getCollectionName() {
		return collectionName;
	}


	/**
	 *  Sets the value of collectionName.
	 *
	 * @param  collectionName  The value to assign collectionName.
	 */
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	
	
	/**
	 *  Returns the value of responseFailures.
	 *
	 * @return    The responseFailures value
	 */
	public ResponseFailure[] getResponseFailures() {
		return responseFailures;
	}


	/**
	 *  Sets the value of responseFailures.
	 *
	 * @param  responseFailures  The value to assign responseFailures.
	 */
	public void setResponseFailures(ResponseFailure[] responseFailures) {
		this.responseFailures = responseFailures;
	}


	/**
	 *  Returns the value of missingFormatReports.
	 *
	 * @return    The missingFormatReports value
	 */
	public MissingFormatReport[] getMissingFormatReports() {
		return missingFormatReports;
	}


	/**
	 *  Sets the value of missingFormatReports.
	 *
	 * @param  missingFormatReports  The value to assign missingFormatReports.
	 */
	public void setMissingFormatReports(MissingFormatReport[] missingFormatReports) {
		this.missingFormatReports = missingFormatReports;
	}


	/**
	 *  Returns the value of missingSetsReports.
	 *
	 * @return    The missingSetsReports value
	 */
	public MissingSetsReport[] getMissingSetsReports() {
		return missingSetsReports;
	}


	/**
	 *  Sets the value of missingSetsReports.
	 *
	 * @param  missingSetsReports  The value to assign missingSetsReports.
	 */
	public void setMissingSetsReports(MissingSetsReport[] missingSetsReports) {
		this.missingSetsReports = missingSetsReports;
	}


	/**
	 *  Returns the value of setsChangedReports.
	 *
	 * @return    The setsChangedReports value
	 */
	public SetsChangedReport[] getSetsChangedReports() {
		return setsChangedReports;
	}


	/**
	 *  Sets the value of setsChangedReports.
	 *
	 * @param  setsChangedReports  The value to assign setsChangedReports.
	 */
	public void setSetsChangedReports(SetsChangedReport[] setsChangedReports) {
		this.setsChangedReports = setsChangedReports;
	}



	/**
	 *  Returns the value of previousSetsFound.
	 *
	 * @return    The previousSetsFound value
	 */
	public String[] getPreviousSetsFound() {
		return previousSetsFound;
	}


	/**
	 *  Sets the value of previousSetsFound.
	 *
	 * @param  previousSetsFound  The value to assign previousSetsFound.
	 */
	public void setPreviousSetsFound(String[] previousSetsFound) {
		this.previousSetsFound = previousSetsFound;
	}


	/**
	 *  Holds data about when an OAI data provider fails to respond to a request.
	 *
	 * @author    John Weatherley
	 */
	public class ResponseFailure implements Serializable {
		private Date failureDate = null;
		private String requestMade = null;


		/**
		 *  Returns the value of failureDate.
		 *
		 * @return    The failureDate value
		 */
		public Date getFailureDate() {
			return failureDate;
		}


		/**
		 *  Sets the value of failureDate.
		 *
		 * @param  failureDate  The value to assign failureDate.
		 */
		public void setFailureDate(Date failureDate) {
			this.failureDate = failureDate;
		}


		/**
		 *  Returns the value of requestMade.
		 *
		 * @return    The requestMade value
		 */
		public String getRequestMade() {
			return requestMade;
		}


		/**
		 *  Sets the value of requestMade.
		 *
		 * @param  requestMade  The value to assign requestMade.
		 */
		public void setRequestMade(String requestMade) {
			this.requestMade = requestMade;
		}
	}


	/**
	 *  Holds data about when an OAI data provider does not have the configured sets.
	 *
	 * @author    John Weatherley
	 */
	public class MissingSetsReport implements Serializable {
		private Date failureDate;
		private String[] missingSets;


		/**
		 *  Returns the value of failureDate.
		 *
		 * @return    The failureDate value
		 */
		public Date getFailureDate() {
			return failureDate;
		}


		/**
		 *  Sets the value of failureDate.
		 *
		 * @param  failureDate  The value to assign failureDate.
		 */
		public void setFailureDate(Date failureDate) {
			this.failureDate = failureDate;
		}


		/**
		 *  Returns the value of missingSets.
		 *
		 * @return    The missingSets value
		 */
		public String[] getMissingSets() {
			return missingSets;
		}


		/**
		 *  Sets the value of missingSets.
		 *
		 * @param  missingSets  The value to assign missingSets.
		 */
		public void setMissingSets(String[] missingSets) {
			this.missingSets = missingSets;
		}
	}


	/**
	 *  Holds data about when an OAI data provider does not have the configured format.
	 *
	 * @author    John Weatherley
	 */
	public class MissingFormatReport implements Serializable {
		private Date failureDate;
		private String missingFormat;


		/**
		 *  Constructor for the MissingFormatReport object
		 *
		 * @param  failureDate    NOT YET DOCUMENTED
		 * @param  missingFormat  NOT YET DOCUMENTED
		 */
		public MissingFormatReport(Date failureDate, String missingFormat) {
			this.failureDate = failureDate;
			this.missingFormat = missingFormat;
		}


		/**
		 *  Returns the value of failureDate.
		 *
		 * @return    The failureDate value
		 */
		public Date getFailureDate() {
			return failureDate;
		}


		/**
		 *  Sets the value of failureDate.
		 *
		 * @param  failureDate  The value to assign failureDate.
		 */
		public void setFailureDate(Date failureDate) {
			this.failureDate = failureDate;
		}


		/**
		 *  Returns the value of missingFormat.
		 *
		 * @return    The missingFormat value
		 */
		public String getMissingFormat() {
			return missingFormat;
		}


		/**
		 *  Sets the value of missingFormat.
		 *
		 * @param  missingFormat  The value to assign missingFormat.
		 */
		public void setMissingFormat(String missingFormat) {
			this.missingFormat = missingFormat;
		}

	}



	/**
	 *  Holds data about when an OAI data provider's sets have changed.
	 *
	 * @author    John Weatherley
	 */
	public class SetsChangedReport implements Serializable {
		private Date dateChanged;
		private String[] setsAdded;
		private String[] setsRemoved;


		/**
		 *  Returns the value of dateChanged.
		 *
		 * @return    The dateChanged value
		 */
		public Date getDateChanged() {
			return dateChanged;
		}


		/**
		 *  Sets the value of dateChanged.
		 *
		 * @param  dateChanged  The value to assign dateChanged.
		 */
		public void setDateChanged(Date dateChanged) {
			this.dateChanged = dateChanged;
		}


		/**
		 *  Returns the value of setsAdded.
		 *
		 * @return    The setsAdded value
		 */
		public String[] getSetsAdded() {
			return setsAdded;
		}


		/**
		 *  Sets the value of setsAdded.
		 *
		 * @param  setsAdded  The value to assign setsAdded.
		 */
		public void setSetsAdded(String[] setsAdded) {
			this.setsAdded = setsAdded;
		}


		/**
		 *  Returns the value of setsRemoved.
		 *
		 * @return    The setsRemoved value
		 */
		public String[] getSetsRemoved() {
			return setsRemoved;
		}


		/**
		 *  Sets the value of setsRemoved.
		 *
		 * @param  setsRemoved  The value to assign setsRemoved.
		 */
		public void setSetsRemoved(String[] setsRemoved) {
			this.setsRemoved = setsRemoved;
		}
	}

}

