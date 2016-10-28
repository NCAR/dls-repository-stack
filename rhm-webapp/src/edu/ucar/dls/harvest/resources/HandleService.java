package edu.ucar.dls.harvest.resources;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringEscapeUtils;
import org.dlese.dpc.util.TimedURLConnection;
import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;

/**
 * Wrapper class that interfaces with the handle services to gather metadata handles and resource
 * handles for records
 * @author dfinke
 *
 */
public class HandleService {
	private static int SERVER_MAX_TRIES = 3;
	private static int TIMEOUT_MS = 6000;
	
	private static final String METADATA_HANDLE_URL_FORMAT = "%s?verb=GetMetadataHandle&partnerId=%s&setSpec=%s";
	private static final String RESOURCE_HANDLE_URL_FORMAT = "%s?verb=GetResourceHandle&resourceUrl=%s";
	
	/**
	 * Fetch metadata handle for given partner id and setSpec using the handle service specified
	 * by the web.xml param handleServiceURL
	 * @param partnerId
	 * @param setSpec
	 * @return
	 * @throws HarvestException
	 */
	public static String fetchMetadataHandle(String partnerId, String setSpec) 
		throws HarvestException
	{
		String handleUrl;
		try {
			// encode the paramters to make sure they are passed correctly
			handleUrl = String.format(METADATA_HANDLE_URL_FORMAT, Config.HANDLE_SERVICE_URL,
									  URLEncoder.encode(partnerId, Config.ENCODING), 
									  URLEncoder.encode(setSpec, Config.ENCODING)  
									 );
		} catch (UnsupportedEncodingException e1) {
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					String.format("URL %s for metadata handle was not formatted correctly "+
								  "for setSpec %s and partnerId %s", METADATA_HANDLE_URL_FORMAT, setSpec, partnerId), e1);

		}
		
		int serverTryIndex = 0;
		try {
			
			while(true)
			{
				try
				{
					// If the server is down try a few times
					serverTryIndex++;
					Document document = Dom4jUtils.getXmlDocument(
							Dom4jUtils.localizeXml(TimedURLConnection.importURL(
									handleUrl, TIMEOUT_MS)));
					String metadataHandle = document.valueOf(
				"/HandleResolutionService/GetMetadataHandle/metadataHandle");
					if (metadataHandle==null || metadataHandle.trim()=="")
					{
						// Means there was an error found in the resoinse throw harvest exception
						String msg = "";
						String error = document.valueOf("/HandleResolutionService/error");
						if(error!=null)
							msg = error;
						throw new HarvestException(Config.Exceptions.RESOURCE_UNAVAILABLE_FOR_RECORD_ERROR_CODE, 
								String.format("Error fetching metadata handle for setSpec %s and partnerId %s: %s",
										setSpec, partnerId, msg), 
									"HandleService.fetchResourceHandle()");
					}
					// If we got here that means the handle was found
					return metadataHandle;
				} catch (MalformedURLException e) {
					throw e;
				} catch (HarvestException e) {
					throw e;
				}
				catch (Exception e) {
					// If an exception gets here its probably a server error.
					// keep looping until the max tries have been tried
					if(serverTryIndex>=SERVER_MAX_TRIES)
						throw e;
				} 
				HarvestRequest.checkForInterruption(
					"HandleService.fetchMetadataHandle()");
			}
		} catch (MalformedURLException e) {
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					String.format("URL %s for metadata handle was not formatted correctly "+
								  "for setSpec %s and partnerId %s", METADATA_HANDLE_URL_FORMAT, setSpec, partnerId), e);
		} catch (HarvestException e) {
			throw e;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE, 
					String.format("Error fetching metadata handle from URL %s", handleUrl), e);
		} 
	}
	
	/**
	 * Fetch the resource handle for the specified url
	 * @param resourceURL
	 * @return
	 * @throws HarvestException
	 */
	public static String fetchResourceHandle(String resourceURL) throws HarvestException
	{
		if(resourceURL==null)
			return null;
		
		// This is the way the old ingest processes sent the url. First it unescapes
		// the xml then it encodes like per spec
		String unescapeURL = StringEscapeUtils.unescapeXml(resourceURL);
		String encodedURL = null;
  	  	try {
			encodedURL = URLEncoder.encode(unescapeURL, Config.ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE, 
					String.format("Error fetching resource handle from url: %s", resourceURL), e);

		}
		String handleUrl = String.format(RESOURCE_HANDLE_URL_FORMAT,
				Config.HANDLE_SERVICE_URL
				, encodedURL);
		int serverTryIndex = 0;
		try {
			// If the server is done tru a few times
			while(true)
			{
				try
				{
					serverTryIndex++;
					Document document = Dom4jUtils.getXmlDocument(
							Dom4jUtils.localizeXml(TimedURLConnection.importURL(
									handleUrl, TIMEOUT_MS)));
					String resourceHandle = document.valueOf("/HandleResolutionService/GetResourceHandle/resourceHandle");
					
					if (resourceHandle==null || resourceHandle.trim()=="")
					{
						// If it gets here that means that a valid response was brought back
						// but its an error response. 
						String msg = "";
						String error = document.valueOf("/HandleResolutionService/error");
						if(error!=null)
							msg = error;
						throw new HarvestException(Config.Exceptions.RESOURCE_UNAVAILABLE_FOR_RECORD_ERROR_CODE, 
								String.format("Error fetching resource handle from url: %s: %s", handleUrl, msg), 
									"HandleService.fetchResourceHandle()");
					}
					return resourceHandle;
				} catch (MalformedURLException e) {
					throw e;
				} catch (HarvestException e) {
					throw e;
				}
				catch (Exception e) {
					if(serverTryIndex>=SERVER_MAX_TRIES)
						throw e;
				} 
				HarvestRequest.checkForInterruption(
					"HandleService.fetchResourceHandle");
			}
		} catch (MalformedURLException e) {
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					String.format("URL %s for resource handle was not formatted correctly "+
								  "for url: %s", RESOURCE_HANDLE_URL_FORMAT, resourceURL), e);
		} catch (HarvestException e) {
			throw e;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new HarvestException(Config.Exceptions.RESOURCE_DEPENDENCY_ERROR_CODE, 
					String.format("Error fetching resource handle from URL %s", handleUrl), e);
		} 

	}
}
