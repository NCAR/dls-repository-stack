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

