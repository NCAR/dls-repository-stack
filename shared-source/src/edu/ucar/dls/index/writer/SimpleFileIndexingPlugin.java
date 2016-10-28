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

package edu.ucar.dls.index.writer;

import java.io.*;
import java.util.*;
import java.text.*;

import edu.ucar.dls.xml.*;
import edu.ucar.dls.index.*;
import edu.ucar.dls.index.reader.*;
import edu.ucar.dls.util.*;
import org.apache.lucene.document.*;
import edu.ucar.dls.repository.*;
import edu.ucar.dls.vocab.*;

/**
 *  A FileIndexingPlugin example that indexes a single field with a single
 *  value.
 *
 * @author    John Weatherley
 * @see       FileIndexingServiceWriter
 */
public class SimpleFileIndexingPlugin implements FileIndexingPlugin {
	private final static String FIELD_NS = "simplePluginData";


	/**
	 *  Indexes a single field 'simplePluginDataIsPluggedIn' with the value 'true'.
	 *  The index may be searched using this field/value to determine which records
	 *  have been indexed using this plugin.
	 *
	 * @param  file           The file that is being indexed
	 * @param  newDoc         The new Lucene Document that will be inserted in the index for
	 *      this file
	 * @param  existingDoc    The previous Lucene Document that existed for this record, or
	 *      null if not available
	 * @param  docType        The docType for this file, for example 'adn', 'dlese_collect'
	 *      (equivalent to XML format in the DLESE metadata repository)
	 * @param  docGroup       The docGroup associated with this file, for example 'dcc',
	 *      'comet', or null if none is associated (equivalent to the collection key in the DLESE metadata
	 *      repository)
	 * @exception  Exception  Exception should be thrown to index this Document as an error
	 * @see                   org.apache.lucene.document.Document
	 */
	public void addFields(File file, Document newDoc, Document existingDoc, String docType, String docGroup)
			 throws Exception {
		String fieldValue = "true";
		newDoc.add(new Field(FIELD_NS + "IsPluggedIn", fieldValue, Field.Store.YES, Field.Index.NOT_ANALYZED));
	}

}

