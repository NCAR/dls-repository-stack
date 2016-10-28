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

package edu.ucar.dls.dds;

import edu.ucar.dls.index.*;
import edu.ucar.dls.index.reader.*;
import edu.ucar.dls.vocab.MetadataVocab;
import java.util.*;

/**
 *  Utility class for working with DDS resources and collections
 *
 *@author    Ryan Deardorff
 */
public final class DDSUtils {
	ResultDoc currentResultDoc = null;
	MetadataVocab vocab;

	/**
	 *  Constructor for the DDSUtils object
	 */
	public DDSUtils() { }

	/**
	 *  Sets the metadataVocab attribute of the DDSUtils object
	 *
	 *@param  vocab  The new metadataVocab value
	 */
	public void setMetadataVocab( MetadataVocab vocab ) {
		this.vocab = vocab;
	}

	/**
	 *  Sets the currentResultDoc attribute of the DDSQueryForm object
	 *
	 *@param  result  The new currentResultDoc value
	 */
	public void setCurrentResultDoc( ResultDoc result ) {
		currentResultDoc = result;
	}

	/**
	 *  Gets the contentStandards attribute of the DDSQueryForm object
	 */
	public String[] getContentStandards() {
		HashMap seenStandard = new HashMap();
		ItemDocReader reader = (ItemDocReader)currentResultDoc.getDocReader();
		String[] standards = reader.getContentStandards();
		for ( int i = 0; i < standards.length; i++ ) {
			String uiLabel = vocab.getTopLevelAbbrevLabelOf( "dds.descr.en-us", "contentStandard", "cs", standards[i] );
			if ( seenStandard.get( uiLabel ) == null ) {
				seenStandard.put( uiLabel, new Boolean( true ) );
			}
		}
		Set s = seenStandard.keySet();
		Iterator iter = s.iterator();
		String[] ret = new String[s.size()];
		int i = 0;
		while ( iter.hasNext() ) {
			ret[i++] = (String)iter.next();
		}
		return ret;
	}
}


