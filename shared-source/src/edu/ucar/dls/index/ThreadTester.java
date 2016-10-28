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

package edu.ucar.dls.index;

import java.io.*;
import java.util.*;
import java.text.*;
import java.net.*;
import java.lang.reflect.*;
//import edu.ucar.dls.catalog.*;
//import edu.ucar.dls.catalog.DleseBean;
//import edu.ucar.dls.catalog.DleseCatalog;
//import edu.ucar.dls.catalog.DleseCatalogRecord;

//import com.lucene.index.*;
//import com.lucene.document.*;
//import com.lucene.analysis.*;
//import com.lucene.queryParser.*;
//import com.lucene.search.*;

public class ThreadTester extends Thread {

	boolean queueEmpty;
	
	public ThreadTester() {
		//setDaemon(true);
	}

	int querycount = 0;
	
	public String testQuery() {
		querycount++;
		return "querycount = " + String.valueOf(querycount);
	}
	
	public void testAdd() {
		if (isAlive()) {
			try {
				queueEmpty = false;
				synchronized(this) {
					notify();
				}		
			}
			catch (Exception e) {
				System.err.println("Error occurred in testAdd()");
			}
		}
		else {
			queueEmpty = false;
			start();
		}
	}	

	public void run() {
		System.err.println("thread called...");
		if (Thread.currentThread() == this) {
			for(;;) {
				synchronized(this) {
					while (queueEmpty) {
						try {
							System.err.println("thread is waiting...");
							wait();
						}
						catch (InterruptedException ie) {
						}
					}
					System.err.println("thread is active");
					queueEmpty = true;
				}	
			}
		}
	}	



	public static void main(String[] args) {
		ThreadTester tester = new ThreadTester();
		try {
			try { Thread.sleep(100); } catch (Exception e) {}
			for (int i=0; i<12; i++) {
				try { Thread.sleep(100); } catch (Exception e) {}
				if (i % 5 == 0) {
					tester.testAdd();
				}
				else {
					System.err.println(tester.testQuery());
				}
				
			}
		}
		catch (Exception e) {
			System.err.println("Exception: " + e.getClass() + " with message: " + e.getMessage()); 
		}
	}

}

