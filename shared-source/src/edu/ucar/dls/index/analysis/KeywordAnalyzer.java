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

package edu.ucar.dls.index.analysis;

import org.apache.lucene.analysis.*;
import java.io.*;

/**
 *  An Analyzer that includes all characters in its tokens. Used for searching fields that are indexed using
 *  the {@link org.apache.lucene.document.Field#Keyword(String,String)} method. Note: Deprecated as of Lucene
 *  v1.9 org.apache.lucene.analysis.KeywordAnalyzer is now available.
 *
 * @author     John Weatherley
 */
public final class KeywordAnalyzer extends Analyzer {

	/**
	 *  A TokenStream that includes all characters.
	 *
	 * @param  field   The field
	 * @param  reader  The Reader
	 * @return         A TokenStream that includes all characters
	 */
	public TokenStream tokenStream(String field, final Reader reader) {
		return new KeywordTokenizer(reader);
	}
}


