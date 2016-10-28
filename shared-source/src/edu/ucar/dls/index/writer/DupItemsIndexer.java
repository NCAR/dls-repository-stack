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
 *  Copyright 2002-2011 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */

package edu.ucar.dls.index.writer;

import java.io.*;
import java.util.*;
import java.text.*;
import java.net.URL;
import org.dom4j.*;

import edu.ucar.dls.index.writer.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.index.*;
import edu.ucar.dls.util.*;
import edu.ucsb.adl.LuceneGeospatialQueryConverter;

import org.apache.lucene.document.*;

/**
 *  Handles inding of duplicate items in the repository.
 *
 * @author    John Weatherley
 */
public class DupItemsIndexer {
	private boolean debug = false;
	
	public DupItemsIndexer(XMLFileIndexingWriter xmlFileIndexingWriter, SimpleLuceneIndex primaryIndex, SimpleLuceneIndex dupItemsIndex){
	
	}

	
	
	// ----- Debug -----------
	private void prtln(String s) {
		if(debug)
			System.out.println("DupItemsIndexer: " + s);
	}
	
	private void prtlnErr(String s) {
		System.err.println("DupItemsIndexer Error: " + s);
	}	
}

