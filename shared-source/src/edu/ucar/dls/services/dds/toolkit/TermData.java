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
package edu.ucar.dls.services.dds.toolkit;

import java.util.*;

/**
 *  Class that holds data about term in one or more fields in the index.
 *
 * @author    John Weatherley
 */
public class TermData {
	private int termCount;
	private int docCount;


	/**
	 *  Constructor for the TermData object
	 *
	 * @param  termCount  Number of times the term appears in the given field(s)
	 * @param  doccount   Number of documents (records) in the index in which the term resides in the given
	 *      field(s)
	 */
	public TermData(int termCount, int docCount) {
		this.termCount = termCount;
		this.docCount = docCount;
	}


	/**
	 *  Number of times the term appears in the given field(s).
	 *
	 * @return    The termCount value
	 */
	public int getTermCount() {
		return termCount;
	}


	/**
	 *  Number of documents (records) in the index in which the term resides in the given field(s).
	 *
	 * @return    The docCount value
	 */
	public int getDocCount() {
		return docCount;
	}
}

