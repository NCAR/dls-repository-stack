/*
 *  License and Copyright:
 *
 *  The contents of this file are subject to the Educational Community License v1.0 (the "License"); you may
 *  not use this file except in compliance with the License. You should have received a copy of the License
 *  along with this software; if not, you may obtain a copy of the License at
 *  http://www.opensource.org/licenses/ecl1.php.
 *
 *  Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 *  either express or implied. See the License for the specific language governing rights and limitations
 *  under the License.
 *
 *  Copyright 2002-2009 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */

package edu.ucar.dls.junit;

import junit.framework.*;
import edu.ucar.dls.datamgr.*;
import java.util.*;
import java.text.*;

/**
 *  This class packages and runs unit tests for all DLESE Tools classes that
 *  have unit tests written for them.
 *
 * @author    John Weatherley
 */
public class AllToolsUnitTests
{

	/**
	 *  A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
	public static Test suite() {

		String start = new SimpleDateFormat("MMM d, h:mm:ss a zzz").format(new Date());

		prtln("\n\n##### Running all JUnit tests for shared-source ... (" + start + ") #####\n");

		TestSuite suite = new TestSuite();
		
		suite.addTest(NSDLDDSRepoTests.suite());
		//suite.addTest(DDSPerformanceTests.suite());
		//suite.addTest(OAIRecordTest.suite());
		// Add test suites for each class here:
		//suite.addTest(OAILuceneDataManagerTest.suite());
		//suite.addTest(SerializedDataManagerTest.suite());
		//suite.addTest(SerializedDataManagerPerformanceTests.suite());

		return suite;
	}


	/*
	 *  Main method for running this set of suites from the command line
	 */
	/**
	 *  The main program for the AllToolsUnitTests class
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}


	/**
	 *  DESCRIPTION
	 *
	 * @param  s  DESCRIPTION
	 */
	private static void prtln(String s) {
		System.out.println(s);
	}

}

