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

