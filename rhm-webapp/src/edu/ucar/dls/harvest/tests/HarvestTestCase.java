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
