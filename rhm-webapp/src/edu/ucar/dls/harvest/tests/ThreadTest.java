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

import org.apache.commons.io.FileUtils;

import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.ingestors.Ingestor;
import edu.ucar.dls.harvest.ingestors.IngestorFactory;
import edu.ucar.dls.harvest.processors.record.RecordSchemaValidator;
import edu.ucar.dls.harvest.reporting.HarvestReport;
import edu.ucar.dls.harvest.workspaces.DBWorkspace;
import edu.ucar.dls.harvest.workspaces.Workspace;

public class ThreadTest implements Runnable {
	private File aFile = null;
	public ThreadTest(File aFile)
	{
		this.aFile = aFile;
	}
	public void run() {
		Ingestor ingestor;
		try {
			System.out.println("here");
			HarvestRequest hr = new HarvestRequest();
			hr.setUuid("TestForDedup");
			Workspace workspace = new DBWorkspace();
			System.out.println("testing");
			File testXml = this.aFile;
			RecordSchemaValidator validator = new RecordSchemaValidator();
			
			validator.initialize(workspace);
			
			validator.run("55", FileUtils.readFileToString(testXml, "UTF-8"));
			validator.run("55", FileUtils.readFileToString(testXml, "UTF-8"));

			
		} catch (HarvestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
