package edu.ucar.dls.harvest.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.processors.Processor;
import edu.ucar.dls.harvest.processors.harvest.DedupRecords;
import edu.ucar.dls.harvest.processors.record.URINormalizer;
import edu.ucar.dls.harvest.workspaces.DBWorkspace;
import edu.ucar.dls.harvest.workspaces.Workspace;
import junit.framework.*;

public class WorkspaceTests extends HarvestTestCase
{
	public WorkspaceTests(String name)
	{
		super(name, "workspace");
	}
	
	public void testCreationAndClean()
	{
		//Workspace ws = new DBWorkspace("adfadf34rdxvc34rt");
		//ws.clean();
	}
	
	
	public void testUrlInsert() throws IOException, HarvestException
	{
		HarvestRequest hr = new HarvestRequest();
		hr.setUuid("TestForInsert");
		Workspace workspace = new DBWorkspace();
		workspace.initialize(hr);
		
		File testXml = this.getTestFile("para_w_amp.xml");
		
		String record = FileUtils.readFileToString(testXml, "UTF-8");
		workspace.populateRecord("1", record, null, null);
		
		String [] xpaths = new String[]{"*[name()='usageDataResourceURL']"};
		
		URINormalizer uv = new URINormalizer();
		uv.setURIRequired(true);
		uv.setUriXPaths(xpaths);
		uv.initialize(workspace);
		
		uv.run("1", record);
		System.out.println(uv.getErrorCount());

	}
}