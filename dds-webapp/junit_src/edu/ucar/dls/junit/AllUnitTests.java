
/**
 * Copyright 2002, 2003 DLESE Program Center/University
 * Corporation for Atmospheric Research (UCAR), P.O. Box 3000,
 * Boulder, CO 80307, support@dlese.org.
 * 
 * This file is part of the DLESE Discovery Project.
 * 
 * The DLESE Discovery Project is free software; you can
 * redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * The DLESE Discovery Project is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with The DLESE System; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 */

package edu.ucar.dls.junit;

import junit.framework.*;

//import edu.ucar.dls.datamgr.*;
import java.util.*;
import java.text.*;

/**
This class packages and runs unit tests for all classes
that have unit tests written for them. 
*/
public class AllUnitTests
{
	
	public static Test suite() {
		
		String start = new SimpleDateFormat("MMM d, h:mm:ss a zzz").format(new Date());		
		prtln("\n\n##### Running all JUnit tests (" + start + ") #####\n");	 
		    	
		TestSuite suite = new TestSuite();
		
		// Add test suites for each class here:
		//suite.addTest(OAILuceneDataManagerTest.suite());
		//suite.addTest(SerializedDataManagerTest.suite());
		//suite.addTest(SerializedDataManagerPerformanceTests.suite());
		//suite.addTest(OAIRecordTest.suite());
		
		return suite;
	} 
	
	/* Main method for running this set of suites from the command line */
	public static void main(String [] args)
	{
		junit.textui.TestRunner.run(suite());				
	}
	
	private static void prtln(String s)
	{
		System.out.println(s);
	}
	
}