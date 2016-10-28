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

import edu.ucar.dls.index.SimpleLuceneIndex;

public class TestSimpleIndex {

	public TestSimpleIndex() {
	}

	public static void main(String[] args) {
		//TestSimpleIndex index = new TestSimpleIndex();
		try {
			SimpleLuceneIndex index = new SimpleLuceneIndex("k:/deniman/testIndex",false);
			
			//index.create("k:/deniman/testIndex", index.STANDARD_ANALYSIS, "content");
			
			//index.use("k:/deniman/testIndex");

			//index.useReader(new File("k:/deniman/testIndex"), "reader");
			
		}
		catch (Exception e) {
			System.err.println("Exception: " + e.getClass() + " with message: " + e.getMessage()); 
		}
	}

}
