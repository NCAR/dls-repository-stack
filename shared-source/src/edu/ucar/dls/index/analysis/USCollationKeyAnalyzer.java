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


