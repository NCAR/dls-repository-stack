package edu.ucar.dls.harvest.tests;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.ingestors.IngestorFactory;
import edu.ucar.dls.harvest.repository.DBRepository;
import edu.ucar.dls.harvest.resources.LRVerify;
import edu.ucar.dls.harvest.tools.JsonUtil;
import edu.ucar.dls.harvest.workspaces.DBWorkspace;

public class LRTests extends HarvestTestCase
{
	
	
	public LRTests(String name) {
		super(name, "transform");
		// TODO Auto-generated constructor stub
	}


	private List<Map<String, Object>> createPublicKeys(String [] publicKeysStringArray)
	{
		List<Map<String, Object>> publicKeys = new ArrayList<Map<String, Object>>();
		for(String publicKeyString:publicKeysStringArray)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("url", publicKeyString);
			map.put("checkHash", new Boolean(false));
			publicKeys.add(map);
		}
		return publicKeys;
	}
	
	public void ntestPBS() throws IOException, HarvestException
	{
		HarvestRequest harvestRequest = new HarvestRequest();
		harvestRequest.setBaseUrl("http://node02.public.learningregistry.net/slice");
		
		String [] publicKeys = {"http://dev2.learngauge.com/MORE-public-key.txt"};
		String [] filters = {"resource_locator"};
		
		harvestRequest.setPublicKeys(this.createPublicKeys(publicKeys));
		
		List<String> schemaFilters = new ArrayList<String>();
		//harvestRequest.setPayloadSchemaFilters(filters);
		String setSpec = "pbs";
		String [] tags = {"any_tags=pbs&from=2013-1-1"};
		harvestRequest.setSetSpec(setSpec);
		harvestRequest.setUuid(setSpec);
		harvestRequest.setCollection_sets(tags);
		harvestRequest.setNativeFormat("nsdl_dc");
		harvestRequest.setProtocol("lr");
		harvestRequest.setMdpHandle(setSpec);
		this.cleanTestRepos(harvestRequest);
		harvestRequest.run();
		
		
		
	}
	
	
	public void ntestOAI_DC() throws IOException, HarvestException
	{
		HarvestRequest harvestRequest = new HarvestRequest();
		harvestRequest.setBaseUrl("http://node02.public.learningregistry.net/slice");
		
		String [] publicKeys = {"http://phet.colorado.edu/gpg/public-key"};
		//harvestRequest.setPublicKeyURLs(publicKeys);

		String setSpec = "lr phet";
		String [] tags = {"any_tags=oai_dc"};
		harvestRequest.setSetSpec(setSpec);
		harvestRequest.setUuid(setSpec);
		harvestRequest.setCollection_sets(tags);
		harvestRequest.setNativeFormat("oai_dc");
		harvestRequest.setProtocol("lr");
		harvestRequest.setMdpHandle(setSpec);
		
		this.cleanTestRepos(harvestRequest);
		harvestRequest.run();
		
	}
	
	public void ntestLRMIHarvest() throws IOException, HarvestException
	{
		HarvestRequest harvestRequest = new HarvestRequest();
		harvestRequest.setBaseUrl("http://node01.public.learningregistry.net/slice");
		String [] publicKeys = {"http://pool.sks-keyservers.net:11371/pks/lookup?op=get&search=0x1AEF8390FDB23BFF"};
		//harvestRequest.setPublicKeyURLs(publicKeys);
		String setSpec = "khan academy";
		String [] tags = {"identity=Jim Klo DBA Khan Academy"};
		harvestRequest.setSetSpec(setSpec);
		harvestRequest.setUuid(setSpec);
		harvestRequest.setCollection_sets(tags);
		harvestRequest.setNativeFormat("lrmi");
		harvestRequest.setProtocol("lr");
		harvestRequest.setMdpHandle(setSpec);
		
		this.cleanTestRepos(harvestRequest);
		harvestRequest.run();
		
	}
	
	public void testErrorOut() throws IOException, HarvestException
	{
		HarvestRequest harvestRequest = new HarvestRequest();
		harvestRequest.setBaseUrl("http://sandbox.learningregistry.org/slice");
		String [] publicKeys = {"http://pool.sks-keyservers.net:11371/pks/lookup?op=get&search=0x46980D3600138235"};
		//harvestRequest.setPublicKeyURLs(publicKeys);
		String setSpec = "BYU";
		String [] tags = {"any_tags=LRMI&from=2013-6-13"};
		harvestRequest.setSetSpec(setSpec);
		harvestRequest.setUuid(setSpec);
		harvestRequest.setCollection_sets(tags);
		harvestRequest.setNativeFormat("lrmi");
		harvestRequest.setProtocol("lr");
		harvestRequest.setMdpHandle(setSpec);
		
		this.cleanTestRepos(harvestRequest);
		harvestRequest.run();
		
	}
	
	
	public void ntestParadataHarvest() throws IOException, HarvestException
	{
		HarvestRequest harvestRequest = new HarvestRequest();
		/*harvestRequest.setBaseUrl("http://sandbox.learningregistry.org/slice");
		harvestRequest.setPublicKeyURL("https://keyserver2.pgp.com/vkd/DownloadKey.event?keyid=0x0C9700F7B292EAAA");
		harvestRequest.setKeyOwner("Dave Finke (Second pgp key) <dfinke@ucar.edu>");
		String setSpec = "daves paradata";
		String [] tags = {"any_tags=paradata&from=2013-04-08"};*/
		
		harvestRequest.setBaseUrl("http://node02.public.learningregistry.net/slice");
		
		String [] publicKeys = {"http://www.oercommons.org/public-key.txt"};
		String [] filters = {"LR Paradata"};
		//harvestRequest.setPayloadSchemaFilters(filters);
		//harvestRequest.setPublicKeyURLs(publicKeys);
		String setSpec = "oer paradata";
		String [] tags = {"identity=OER Commons"};
		
		
		
		harvestRequest.setSetSpec(setSpec);
		harvestRequest.setUuid(setSpec);
		harvestRequest.setCollection_sets(tags);
		harvestRequest.setNativeFormat("lr_paradata");
		harvestRequest.setProtocol("lr");
		harvestRequest.setMdpHandle(setSpec);
		
		this.cleanTestRepos(harvestRequest);
		harvestRequest.run();


		
	}
	
	public void ntestOER_OAI() throws IOException, HarvestException
	{
		System.out.println(IngestorFactory.createIngestor("oai_dc"));
		HarvestRequest harvestRequest = new HarvestRequest();		
		harvestRequest.setBaseUrl("http://node02.public.learningregistry.net/slice");
		
		String [] publicKeys = {"http://www.oercommons.org/public-key.txt"};
			
		//harvestRequest.setPublicKeyURLs(publicKeys);
		String setSpec = "oer oaidata";
		String [] tags = {"identity=OER Commons"};
		
		
		harvestRequest.setSetSpec(setSpec);
		harvestRequest.setUuid(setSpec);
		harvestRequest.setCollection_sets(tags);
		harvestRequest.setNativeFormat("oai_dc");
		harvestRequest.setProtocol("lr");
		harvestRequest.setMdpHandle(setSpec);
		this.cleanTestRepos(harvestRequest);
		harvestRequest.run();
	}
	

	
	private void cleanTestRepos(HarvestRequest harvestRequest) throws HarvestException
	{
		DBWorkspace workspace = new DBWorkspace();
		workspace.initialize(harvestRequest);
		DBRepository testRepository = new DBRepository();
		testRepository.initialize(workspace, false);
		
		//Connection dbConnection;
		try {
			testRepository.removeMetadataBySessionId( 
				harvestRequest.getUuid());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			workspace.clean();
			testRepository.clean();
		}
		testRepository.clean();
		
		
		
	}
	
	public void ntestVerify() throws Exception
	{
		String document = "{\"doc_ID\": \"02a6e9432c09481cb6c342ef3c7e7280\", \"resource_data_description\": {\"doc_type\": \"resource_data\", \"resource_locator\": \"http://www.teachersdomain.org/resource/psu08-liq.sci.wastewater/\", \"digital_signature\": {\"key_location\": [\"https://keyserver2.pgp.com/vkd/DownloadKey.event?keyid=0x0C9700F7B292EAAA\"], \"key_owner\": \"Dave Finke (Second pgp key) <dfinke@ucar.edu>\", \"signing_method\": \"LR-PGP.1.0\", \"signature\": \"-----BEGIN PGP SIGNED MESSAGE-----\\nHash: SHA1\\n\\n12a74f9fb236445c7a27b1b68699ba50ad0b28057e62849ad6497f9260f8f902\\n-----BEGIN PGP SIGNATURE-----\\nVersion: GnuPG v1.4.13 (Cygwin)\\n\\niQEcBAEBAgAGBQJRknFiAAoJEAyXAPeykuqqDlYH/0CinFgCv/GfmIlI+b4e/tcO\\nMv0yc1vZ/25UC4KY9G3k2AxIx19USWvfSFyuQ+KS5jWfmEq7g/jnZ8N4F5I7E06N\\ne67qlgktqmTiNSdXuX283bX8Ss7DdJICeI0UR0LAcCltJyixWmJU0a38w6jLyo3k\\n9olYZkUJS3brBoVaG3VrpyFl6TJioEQK9LH07VFjDcTBx9Sp1/buZkUG2ljXm0h+\\nswAnV88lpVKvzgo8g5bI0tEWlUqDZkABsMFgV1acHk7oBDAbn/b0jb0tKC/d76Ju\\nsgY19OVLyFl8puKtMIBq4h8gADhYu5FOaXCTrYGKyn69yC7ROtHNmnyPe7mZ2PM=\\n=04Rt\\n-----END PGP SIGNATURE-----\\n\"}, \"resource_data\": {\"items\": [{\"id\": \"oai:nsdl.org:2200/20120828120523541T\", \"activity\": {\"content\": \"Paradata for Liquid Assets: Wastewater\", \"verb\": {\"action\": \"commented\", \"date\": \"2008-11-20T00:00:00/2012-08-01T15:41:05.677196\", \"measure\": {\"measureType\": \"count\", \"value\": \"1.0\"}}, \"actor\": {\"objectType\": \"Educator\"}, \"object\": \"http://www.teachersdomain.org/resource/psu08-liq.sci.wastewater/\"}}, {\"id\": \"oai:nsdl.org:2200/20120828120523541T\", \"activity\": {\"content\": \"Paradata for Liquid Assets: Wastewater\", \"verb\": {\"action\": \"rating\", \"date\": \"2008-11-20T00:00:00/2012-08-01T15:41:05.677196\", \"measure\": {\"scaleMin\": 1, \"measureType\": \"star average\", \"scaleMax\": 5, \"sampleSize\": 1, \"value\": \"5.0\"}}, \"actor\": {\"objectType\": \"Educator\"}, \"object\": \"http://www.teachersdomain.org/resource/psu08-liq.sci.wastewater/\"}}]}, \"keys\": [\"paradata\", \"NSDL_COLLECTION_ncs-NSDL-COLLECTION-000-003-112-089\", \"oai:nsdl.org:2200/20120828120523541T\"], \"TOS\": {\"submission_TOS\": \"http://www.learningregistry.org/information-assurances/open-information-assurances-1-0\"}, \"_rev\": \"1-88c455a4b5f6aedc99e166c9459e7a71\", \"resource_data_type\": \"metadata\", \"payload_placement\": \"inline\", \"payload_schema\": [\"LR Paradata 1.0\", \"application/microdata+json\"], \"node_timestamp\": \"2013-04-08T11:50:51.234547Z\", \"doc_version\": \"0.49.0\", \"create_timestamp\": \"2013-04-08T11:50:51.234547Z\", \"update_timestamp\": \"2013-04-08T11:50:51.234547Z\", \"active\": true, \"publishing_node\": \"bababe6a3288497fb7a46d454154f5db\", \"_id\": \"02a6e9432c09481cb6c342ef3c7e7280\", \"doc_ID\": \"02a6e9432c09481cb6c342ef3c7e7280\", \"identity\": {\"signer\": \"NSDL\", \"submitter\": \"NSDL\", \"submitter_type\": \"agent\", \"curator\": \"NSDL\"}}}";
		
		
		Map<String, Object> documentMap = JsonUtil.convertToMap(document);
		Map resourceDataDescriptionJson = (Map)documentMap.get(
		"resource_data_description");
		
		String[] publicKeys = {"https://keyserver2.pgp.com/vkd/DownloadKey.event?keyid=0x0C9700F7B292EAAA"};
		
		//LRVerify lrVerify = new LRVerify(publicKeys, true);
		
		//boolean isValidDocument  = lrVerify.verify(resourceDataDescriptionJson);
		System.out.println("the document is");
		//System.out.println(isValidDocument);
		
	}
	
}