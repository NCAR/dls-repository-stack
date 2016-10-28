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

package edu.ucar.dls.schemedit.vocab;

import java.util.Comparator;

/**
 *  Comparator to sort Records in reverse order of their creation date
 *
 * @author    Jonathan Ostwald
 */
public class SortTermAndDeftns implements Comparator {
	
	/**
	 *  Provide comparison for sorting Sugest a URL Records by reverse "creation Date"
	 *
	 * @param  o1  document 1
	 * @param  o2  document 2
	 * @return     DESCRIPTION
	 */
	public int compare(Object o1, Object o2) {
		String s1;
		String s2;
		try {
			s1 = ((TermAndDeftn) o1).getTerm();
			s2 = ((TermAndDeftn) o2).getTerm();
			return s1.compareTo(s2);
		} catch (Exception e) {
			System.out.println ("SortTermAndDeftns error: " + e.getMessage());
			return 0;
		}
	}
}

