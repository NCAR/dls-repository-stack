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
import org.apache.lucene.analysis.standard.*;
import java.io.Reader;
import java.util.Set;

/**
 *  An Analyzer that uses a LowerCaseFilter to normalize the text to lower case.
 *
 * @author    John Weatherley
 * @see       org.apache.lucene.analysis.LowerCaseFilter
 */
public class LowerCaseAnalyzer extends Analyzer {


	/**
	 *  Normalizes the text to lower case.
	 *
	 * @param  fieldName  Name of the field being tokenized
	 * @param  reader     The Reader
	 * @return            The appropriate TokenStream
	 * @see               org.apache.lucene.analysis.LowerCaseFilter
	 */
	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream result = new KeywordTokenizer(reader);
		result = new LowerCaseFilter(result);
		return result;
	}
}

