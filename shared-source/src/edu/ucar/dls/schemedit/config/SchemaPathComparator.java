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
package edu.ucar.dls.schemedit.config;

import java.util.Comparator;

/**
*  Class to sort lists of {@link PathMap} instances by their xpath property. 
 *
 * @author    Jonathan Ostwald
 */
public class SchemaPathComparator implements Comparator {
	/**
	 *  sorts by order in which paths are processed by MetadataFramework.populateFields (and
	 *  therefore are added to the docMap) so one value doesn't get stomped by another.
	 *
	 * @param  o1  PathMap1
	 * @param  o2  PathMap2
	 * @return     NOT YET DOCUMENTED
	 */
	public int compare(Object o1, Object o2) {

		String xpath1 = ((SchemaPath) o1).xpath;
		String xpath2 = ((SchemaPath) o2).xpath;

		if (xpath1 == null) {
			return 0;
		}

		if (xpath2 == null) {
			return 0;
		}

		return xpath1.compareTo(xpath2);
	}
}

