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
package edu.ucar.dls.harvest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.ingestors.Ingestor;
import edu.ucar.dls.harvest.ingestors.IngestorFactory;
import edu.ucar.dls.harvest.reporting.HarvestReport;

public class HarvestRequest implements Runnable {
	
	public final static String FULL_HARVEST_RUN_TYPE = "full_harvest";
	public final static String TEST_RUN_TYPE = "test";
	
	// Full harvest test is one that does not clean the workspace after the full 
	// harvest, so one can view the workspace and files
	public final static String FULL_HARVEST_TEST_RUN_TYPE = "full_harvest_test";

	private String baseUrl = null;
	private String metadataPrefix = null;
	private String mdpHandle = null;
	private String setSpec = "";
	private String uuid = null;
	private String runType = FULL_HARVEST_RUN_TYPE;
	private String[] collection_sets = null;
	private String protocol = null;
	private String nativeFormat = null;
	private String collectionId = null;

	// these are particular for lr
	private List<Map<String, Object>> publicKeys = null;
	private HashMap<String, List<List<String>>> sliceRequests = null;

	// End particular for lr

	public HashMap<String, List<List<String>>> getSliceRequests() {
		return sliceRequests;
	}

	public void setSliceRequests(HashMap<String, List<List<String>>> sliceRequests) {
		this.sliceRequests = sliceRequests;
	}

	public List<Map<String, Object>> getPublicKeys() {
		return publicKeys;
	}

	public void setPublicKeys(List<Map<String, Object>> publicKeys) {
		this.publicKeys = publicKeys;
	}

	public void setNativeFormat(String nativeFormat) {
		this.nativeFormat = nativeFormat;
	}

	public String getNativeFormat() {
		return nativeFormat;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public HarvestRequest() {
		// TODO Auto-generated constructor stub
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getMetadataPrefix() {
		return metadataPrefix;
	}

	public void setMetadataPrefix(String metadataPrefix) {
		this.metadataPrefix = metadataPrefix;
	}

	public String getMdpHandle() {
		return mdpHandle;
	}

	public void setMdpHandle(String mdpHandle) {
		this.mdpHandle = mdpHandle;
	}

	public String getSetSpec() {
		return setSpec;
	}

	public void setSetSpec(String setSpec) {
		this.setSpec = setSpec;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getRunType() {
		return runType;
	}

	public void setRunType(String runType) {
		this.runType = runType;
	}

	public String[] getCollection_sets() {
		return collection_sets;
	}

	public void setCollection_sets(String[] collection_sets) {
		this.collection_sets = collection_sets;
	}
	
	public String getCollectioId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
		
	}
	public boolean getIsTest()
	{
		return this.runType.equals("test");
	}
	
	public String getCollectionName()
	{
		if(Config.Reporting.harvestManager.getCollectionNameMappings().containsKey(
				this.setSpec))
			return Config.Reporting.harvestManager.getCollectionNameMappings().get(
					this.setSpec);
		else
			return null;
	}
	public static void checkForInterruption(String sourceContext) throws HarvestException
	{
		if(Thread.currentThread().isInterrupted())
			throw new HarvestException(Config.Exceptions.THREAD_INTERRUPTED,
					"Thread has been interrupted by system, aborting harvest. " +
					"Last method of execution " + sourceContext, 
					sourceContext);
	}
	
	public void run() {
		Ingestor ingestor;
		try {
			ingestor = IngestorFactory.createIngestor(this.nativeFormat);
			ingestor.setHarvestRequest(this);
			ingestor.ingest();
		} 
		catch(HarvestException e)
		{
			// The only exception that might come here is a a harvest exception
			// Which is ingestor isn't implemented. Rest should be taken care
			// of in ingestor
			
			try {
				HarvestReport hr = new HarvestReport(this.getUuid(), this.getIsTest());
				hr.reportHarvestFailure(e);
			} catch (HarvestException e1) {
				// TODO Auto-generated catch block
				System.out.println("HarvestRequest.run() Error: " + e1);
				e1.printStackTrace();
			}
			
		} 
		catch(Exception e)
		{
			// Should never get here since the ingestor should take care of all
			// exceptions. But just in case
			e.printStackTrace();
			try {
				HarvestReport hr = new HarvestReport(this.getUuid(), this.getIsTest());
				hr.reportHarvestFailure(e);
			} catch (HarvestException e1) {
				// TODO Auto-generated catch block
				System.out.println("HarvestRequest.run() Error: " + e1);
				e1.printStackTrace();
			}
			
		}
	}
}
