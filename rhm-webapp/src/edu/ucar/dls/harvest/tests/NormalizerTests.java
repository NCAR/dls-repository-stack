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
