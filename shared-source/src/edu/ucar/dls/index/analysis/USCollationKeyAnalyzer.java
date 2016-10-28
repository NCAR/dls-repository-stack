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
 *  Copyright 2002-2012 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */

package edu.ucar.dls.index.analysis;

import org.apache.lucene.collation.CollationKeyAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import java.io.Reader;
import java.io.IOException;
import java.text.Collator;
import java.util.Locale;

/**
 *  A CollationKeyAnalyzer for the US english language, used for collating fields with sensitivity to special characters. 
 *
 * @author     John Weatherley
 */
public final class USCollationKeyAnalyzer extends Analyzer {
	private CollationKeyAnalyzer collationKeyAnalyzer = null;
	
	public USCollationKeyAnalyzer() {
		Collator collator = Collator.getInstance(Locale.US);
		collator.setDecomposition(Collator.FULL_DECOMPOSITION);
		collator.setStrength(Collator.PRIMARY);
		collationKeyAnalyzer = new CollationKeyAnalyzer(collator);
	}
	
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return collationKeyAnalyzer.tokenStream(fieldName, reader);	
	}
	
	public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
		return collationKeyAnalyzer.reusableTokenStream(fieldName, reader);	
	}	
	
}


