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
