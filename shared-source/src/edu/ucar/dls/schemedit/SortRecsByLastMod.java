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


