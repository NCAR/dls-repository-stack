package edu.ucar.dls.harvest.tests;

import java.io.File;

import edu.ucar.dls.harvest.HarvestRequest;
import junit.framework.*;

public class HarvestTestCase extends TestCase
{
	protected File testFileDirectory = null;
	public HarvestTestCase(String name, String testFileDirectory)
	{
		super(name);
		String rootPath = System.getProperty("user.dir");
		this.testFileDirectory = new File(String.format("%s/web/WEB-INF/unit-testing/harvest/%s", 
				rootPath, testFileDirectory));
	}
	
	protected File getTestFile(String fileName)
	{
		return new File(testFileDirectory, fileName);
	}
	
	
}