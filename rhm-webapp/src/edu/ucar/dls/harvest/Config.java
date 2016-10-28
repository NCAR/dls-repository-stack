package edu.ucar.dls.harvest;

import edu.ucar.dls.harvestmanager.HarvestManager;

public class Config {
	public static String BASE_FILE_PATH_STORAGE = null;
	public static String HARVESTED_FILES_DIRECTORY_NAME = "harvested_files";
	public static String HARVESTED_FILES_ZIPFILE_NAME = "harvested_files.zip";
	public static String INGESTOR_CONFIGS_URI = null;
	public static String HANDLE_SERVICE_URL = null;
	public static final String ENCODING = "UTF-8";
	public static boolean ZIP_HARVEST_FILES = false;
	public static String SMS_INFO_JSON_URL_FORMAT = null;
	
	public static class DBRepository{
		
		public static String DB_URL = null;
		public static String DB_USER = null;
		public static String DB_PASS = null;
		
		public static void setDB(String url, String user, String password)
		{
			DB_URL = url+"/hm_repository";
			DB_USER = user;
			DB_PASS = password;
		}
	}
	
	public static class DBWorkspace{
		public static String DB_URL = null;
		public static String DB_USER = null;
		public static String DB_PASS = null;
		
		public static void setDB(String url, String user, String password)
		{
			DB_URL = url+"/harvest_workspace";
			DB_USER = user;
			DB_PASS = password;
		}
	}
	
	public static class DBHarvest{
		public static String DB_URL = null;
		public static String DB_USER = null;
		public static String DB_PASS = null;
		
		public static void setDB(String url, String user, String password)
		{
			DB_URL = url+"/harvest";
			DB_USER = user;
			DB_PASS = password;
		}
	}
	
	// Todo remove this, this is just for testing before push to prod. After that this doesn't matter
	public class DBConnectionForTestCompares{
		public static final String DB_URL = "";
		public static final String DB_USER = "";
		public static final String DB_PASS = "";
	}
	
	public static double MAX_ERROR_THRESHOLD = .2;
	public static double MIN_RECORD_COUNT_DISCREPANCY_THRESHOLD = .5;
	
	public static class Exceptions{
		public static final String MAX_ERROR_THRESHOLD_ERROR_CODE = 
							"Max Error Threshold has been reached";
		public static final String MAX_ERROR_THRESHOLD_ERROR_MSG = "The maximum threshold of errored records has been reached. Current harvest was %.2f and the maximum is " + MAX_ERROR_THRESHOLD;
		
		public static final String OAI_SERVER_ERROR_CODE = "Server Error";
		public static final String OAI_RESPONSE_ERROR_CODE = "Server Response Error";
		
		public static final String RESOURCE_UNAVAILABLE_FOR_RECORD_ERROR_CODE = "Resource Unavailable for Record Error";
		public static final String RESOURCE_DEPENDENCY_ERROR_CODE = "Resource Dependency Error";
		
		public static final String THREAD_INTERRUPTED = "Thread was interrupted by System";
		public static final String PROGRAMMER_ERROR_CODE = "Programmer Error";
		
		// This is only used when log in results back to servelet. Which 
		// means its an exception that we didn't except to see and we should
		// add code to handle it and create a HarvestException instead
		public static final String UNHANDLED_ERROR_CODE = "Unhandled Error";
		
		
		public static final String MAX_RECORD_COUNT_ERROR_MSG = 
			"Min Record Count descrepancy has been exceded. Current record count " +
			"of %d was attempted to be overridden by %d records. Min threshold is %.2f, " +
			"current descrepancy is %.2f";

	}
	
	public static class Reporting
	{
		public static int maxNumberOfExamples = 2;
		public static HarvestManager harvestManager = null;
		public static int MAX_STORAGE_OF_SUCCESS_SESSIONS = 2;
		public static int MAX_STORAGE_OF_FAILURE_SESSIONS = 1;
		public static void setHarvestManager(HarvestManager harvestManager) {
			Reporting.harvestManager = harvestManager;
		}
		public static void setMaxNumberOfExamples(int maxNumberOfExamples) {
			Reporting.maxNumberOfExamples = maxNumberOfExamples;
		}
	}

	public static class DataFormats
	{
		public static String XML = "XML";
		public static String JSON = "JSON";
	}
	
	public static class Groupings
	{
		public static String BY_URL = "URL";
		public static String BY_PARTNER_ID = "PARTNER_ID";
	}
	
	public static class DatabaseOptions
	{
		public static String DESCENDING = "DESC";
		public static String ASCENDING = "ASC";
	}
	
	public static class Protocols
	{
		public static final String OAI = "oai";
		public static final String LR_SLICE = "lrSlice";
		public static final String HARVESTED_RECORDS_DB = "harvestedRecordsDB";
	}
	
	public static class ASN
	{
		public static final String ASN_BASE_URL = "http://asn.jesandco.org";
		public static final String OLD_ASN_BASE_URL = "http://purl.org/ASN";
		
		//Any added standard just need to placed here. The RDF;s can be found
		// at http://asn.jesandco.org/resources/ASNJurisdiction.
		public static final String[] RDF_CONVERSION_STMT_URLS = {
				"http://s3.amazonaws.com/asnstatic/data/rdf/D10003FB.xml",
				"http://s3.amazonaws.com/asnstatic/data/rdf/D10003FC.xml",
				"http://s3.amazonaws.com/asnstatic/data/rdf/D2365735.xml",
				"http://s3.amazonaws.com/asnstatic/data/rdf/D1000152.xml",
				
		};
		public static String ASN_GET_STANDARD_RESOLVER_URL_FORMAT = 
				null;
		
		public static void setAsnStandardResolverUrlFormat(String asnStandardResolverUrlFormat) {
			ASN.ASN_GET_STANDARD_RESOLVER_URL_FORMAT = asnStandardResolverUrlFormat;
		}
	}
	
	public static void setBASE_FILE_PATH_STORAGE(String bASE_FILE_PATH_STORAGE) {
		BASE_FILE_PATH_STORAGE = bASE_FILE_PATH_STORAGE;
	}


	public static void setINGESTOR_CONFIGS_URI(String iNGESTOR_CONFIGS_URI) {
		INGESTOR_CONFIGS_URI = iNGESTOR_CONFIGS_URI;
	}
	
	public static void setSMS_INFO_JSON_URL_FORMAT(String sMS_INFO_JSON_URL_FORMAT) {
		SMS_INFO_JSON_URL_FORMAT = sMS_INFO_JSON_URL_FORMAT;
	}


	public static void setMAX_ERROR_THRESHOLD(double mAX_ERROR_THRESHOLD) {
		MAX_ERROR_THRESHOLD = mAX_ERROR_THRESHOLD;
	}

	public static void setMIN_RECORD_COUNT_DISCREPANCY_THRESHOLD(
			double mIN_RECORD_COUNT_DISCREPANCY_THRESHOLD) {
		MIN_RECORD_COUNT_DISCREPANCY_THRESHOLD = mIN_RECORD_COUNT_DISCREPANCY_THRESHOLD;
	}
	
	public static void setHANDLE_SERVICE_URL(String hANDLE_SERVICE_URL) {
		HANDLE_SERVICE_URL = hANDLE_SERVICE_URL;
	}
	public static void setZIP_HARVEST_FILES(boolean zIP_HARVEST_FILES) {
		ZIP_HARVEST_FILES = zIP_HARVEST_FILES;
	}
	public static void setDB(String url, String user, String password)
	{
		DBRepository.setDB(url, user, password);
		DBWorkspace.setDB(url, user, password);
		DBHarvest.setDB(url, user, password);
	}
	
}
