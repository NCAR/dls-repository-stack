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

import edu.ucar.dls.junit.TestTools;

import junit.framework.*;
import java.util.Date;
import java.util.Random;
import java.util.Enumeration;
import java.util.*;
//import com.meterware.httpunit.*;

//import edu.ucar.dls.oai.datamgr.*;



public class DDSPerformanceTests extends TestCase
{

	
	public static Test suite() {
		// Use java reflection to run all test methods in this class:
		prtln("Running DDS Performance tests...");
		return new TestSuite(DDSPerformanceTests.class);
	} 
	
	static int recNum = 0;
	
	/* setUp() gets called prior to running EACH test method in this class */
	protected void setUp()
	throws Exception
	{
		prtln(".");// introduce some space between the tests
	}
	
	
	
	public void test_dds_searches()
	throws Exception
	{
		//WebConversation wc = new WebConversation();
		//WebResponse wr = wc.getResponse( "http://quake.dpc.ucar.edu:9187/dds_testing/admin/query.do" );
		//prtln( wr.getText() );		
	}


	
	/* tearDown() gets called after running EACH test method in this class */
	protected void tearDown()
	{
		//prtln("tearDown()");		
	}
		
	
	private static void prtln(String s)
	{
		System.out.println(s);
	}
	
	/* Main method for running this single suite from the command line*/
	public static void main(String [] args)
	{
		junit.textui.TestRunner.run(suite());
	}
	
	
}
