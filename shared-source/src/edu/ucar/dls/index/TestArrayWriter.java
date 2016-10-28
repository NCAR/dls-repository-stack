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

public class TestArrayWriter {

	public TestArrayWriter() {
	}

	static String [] getArray(File arrayFile) {
		String [] array = null;
		try {
			FileInputStream f = new FileInputStream(arrayFile);
			ObjectInputStream p = new ObjectInputStream(f);
			try {
				array = (String [])p.readObject();
				for (int i=0; i<array.length; i++) {
					System.out.println("read string = " + array[i]);
				}
			}
			catch (Exception e) {
				array = null;
			}
			finally {
				f.close();
				p.close();
			}
		}
		catch (Exception e) {
			// Will always happen if file doesn't exist.
			// monitor.logError("Exception reading record file: " + file.getName());
		}
		return array;
	}

	static void setArray(File arrayFile, String [] array) {
	
		try {
			FileOutputStream f = new FileOutputStream(arrayFile);
			ObjectOutputStream p = new ObjectOutputStream(f);

			try {
				p.writeObject(array);
				p.flush();
			}
			catch (Exception e) {
				//
			}
			finally {
				f.close();
				p.close();
			}
		}
		catch (Exception e) {}
	}


	public static void main(String[] args) {
		Tester tst = new Tester();
		try {
			File arrayFile = new File("k:/deniman/testfile.obj");
			String [] array = { "one", "two", "three" };
			
			setArray(arrayFile, array);
			
			array = getArray(arrayFile);
			
		}
		catch (Exception e) {
			System.err.println("Exception: " + e.getClass() + " with message: " + e.getMessage()); 
		}
	}

}
