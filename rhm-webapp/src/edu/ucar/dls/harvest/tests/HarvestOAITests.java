package edu.ucar.dls.harvest.tests;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.dlese.dpc.email.SendEmail;

import edu.ucar.dls.harvest.HarvestRequest;
import junit.framework.*;

public class HarvestOAITests extends TestCase
{
	public HarvestOAITests(String name)
	{
		super(name);
	}
	
	public void testOAI_PMH()
	{
		HarvestRequest harvestRequest = new HarvestRequest();
		harvestRequest.setBaseUrl("http://oai.serc.carleton.edu/oai/provider");
		
		harvestRequest.setMetadataPrefix("comm_anno");
		harvestRequest.setSetSpec("NSDL-COLLECTION-000-003-112-086 ");
		harvestRequest.setMdpHandle("2200/20100817170649795T ");
		harvestRequest.setUuid("NSDL-COLLECTION-000-003-112-008-13619066162");
		harvestRequest.setRunType("full_harvest");
		String[] sets = {"clean-comment"};
		harvestRequest.setCollection_sets(sets);
		harvestRequest.setNativeFormat("comm_anno");
		harvestRequest.setProtocol("oai");
		harvestRequest.run();
		
		
	}
	
	/*public void testOAI_PMHDeletedRecords()
	{
		HarvestRequest harvestRequest = new HarvestRequest();
		harvestRequest.setBaseUrl("http://oai.serc.carleton.edu/oai/provider");
		
		harvestRequest.setMetadataPrefix("comm_anno");
		harvestRequest.setTransform("temp");
		harvestRequest.setUrlXPATH("temp");
		harvestRequest.setUrlRequired("False");
		harvestRequest.setSetSpec("ncs-NSDL-COLLECTION-000-003-112-008");
		harvestRequest.setMdpHandle("2200-20091124124936530T");
		harvestRequest.setUuid("NSDL-COLLECTION-000-003-112-008-1361906614545");
		harvestRequest.setRunType("full_reharvest");
		String[] sets = {"clean-comment"};
		harvestRequest.setCollection_sets(sets);
		System.out.println(harvestRequest.getRunType());
		harvestRequest.run();
	}*/
	
}