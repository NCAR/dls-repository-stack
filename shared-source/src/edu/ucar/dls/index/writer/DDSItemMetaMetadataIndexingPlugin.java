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
 *  A FileIndexingPlugin that indexes meta-metadata about items (educational resources) in
 *  the index.
 *
 * @author     John Weatherley
 * @see        FileIndexingServiceWriter <p>
 *
 *
 */
public class DDSItemMetaMetadataIndexingPlugin extends ServletContextFileIndexingPlugin {
	private final static String FIELD_NS = "itemMetaMetadata";

	private static File recordMetaMetadataDir = null;


	/**
	 *  Gets the meta-metadata file associated with the given metadata file. The file that is
	 *  returned is not guaranteed to exist.
	 *
	 * @param  metadataFile  A metadata File
	 * @return               The meta-metadata File associated with the File
	 */
	public final static File getMetaMetadataFile(File metadataFile) {
		if (recordMetaMetadataDir == null)
			recordMetaMetadataDir = (File) getServletContext().getAttribute("recordMetaMetadataDir");

		String filePath = metadataFile.getParentFile().getParentFile().getName() + File.separatorChar +
			metadataFile.getParentFile().getName() + File.separatorChar + metadataFile.getName();

		return new File(recordMetaMetadataDir, filePath);
	}


	/**
	 *  Indexes a single field 'DDSItemMetaMetadataIndexingPlugin' with the value 'true'. The
	 *  index may be searched using this field/value to determine which records have been
	 *  indexed using this plugin.
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

		File myMetaMetadataFile = getMetaMetadataFile(file);

		//System.out.println("myMetaMetadataFile: " + myMetaMetadataFile.getAbsolutePath()
		//	 + " myDocType: " + docType + " myCollection: " + docGroup);

		if (!myMetaMetadataFile.canRead()) {
			newDoc.add(new Field(FIELD_NS + "DiscoverableStatus", "discoverable", Field.Store.YES, Field.Index.ANALYZED));
			return;
		}
	}

}

