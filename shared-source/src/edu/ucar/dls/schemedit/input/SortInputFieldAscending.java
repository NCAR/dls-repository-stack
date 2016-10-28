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
package edu.ucar.dls.schemedit.input;

import java.util.Comparator;
import edu.ucar.dls.xml.XPathUtils;

/**
 *  Comparator to order input fields in "natural order" of their xpath
 *  attribute.
 *
 * @author    ostwald<p>
 *
 *
 */
public class SortInputFieldAscending implements Comparator {

	/**
	 *  Compare InputField objects by their XPath fields (in "natural order"), only
	 *  taking indexing into account (e.g., "/record/general[1] comes before
	 *  "record/general[10]").
	 *
	 * @param  o1  InputField 1
	 * @param  o2  InputField 2
	 * @return     comparison of "xpath order"
	 */
	public int compare(Object o1, Object o2) {

		String s1 = ((InputField) o1).getXPath();
		String s2 = ((InputField) o2).getXPath();

		return XPathUtils.compare(s1, s2);
	}
}

