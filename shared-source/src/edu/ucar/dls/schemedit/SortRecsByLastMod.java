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
package edu.ucar.dls.schemedit;

import java.util.Comparator;

/**
 *  Comparator for sorting {@link edu.ucar.dls.schemedit.Record} instances in the StandAlone configuration metadata editor.
 *
 * @author    Jonathan Ostwald
 */
public class SortRecsByLastMod implements Comparator {

	
	/**
	 *  Compare InputField objects by their XPath fields (in reverse of "natural order")
	 *
	 * @param  o1  InputField 1
	 * @param  o2  InputField 2
	 * @return     comparison by reverse of "natural order"
	 */
	public int compare(Object o1, Object o2) {

		
		Record r1 = (Record) o1;
		Record r2 = (Record) o2;
		
		long lm1 = r1.getLastModified();
		long lm2 = r2.getLastModified();

		if (lm2 > lm1) {
			return 1;
		}
		if (lm2 == lm1) {
			return 0;
		}
		return -1;
		
		
	}
}


