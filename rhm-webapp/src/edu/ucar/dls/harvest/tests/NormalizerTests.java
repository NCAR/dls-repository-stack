package edu.ucar.dls.harvest.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentException;

import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.ingestors.Ingestor;
import edu.ucar.dls.harvest.ingestors.IngestorFactory;
import edu.ucar.dls.harvest.processors.record.ASNConversion;
import edu.ucar.dls.harvest.processors.record.TransformViaGroupFiles;
import edu.ucar.dls.harvest.resources.ASNHelper;

public class NormalizerTests extends HarvestTestCase
{	
	public NormalizerTests(String name) {
		super(name, "transform");
		// TODO Auto-generated constructor stub
	}
	
	
	public void testTransformComplex() throws Exception
	{
		try {

			File testXml = this.getTestFile("asn_lots.xml");
			String originalRecord =  FileUtils.readFileToString(testXml, "UTF-8");
			
			Ingestor ingestor = IngestorFactory.createIngestor("nsdl_dc");
			ingestor.initForSingleTransforms("nsdl_dc", null);
			System.out.println(ingestor.normalizeRecord("1", originalRecord));
			ingestor.clean();
		} catch (HarvestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
	
	}
	
}