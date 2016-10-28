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

package edu.ucar.dls.util.strings;

/**
 *  Description of the Class
 *
 * @author    ryandear
 */
public final class FindAndReplace
{

	/**
	 *  Given an input string, replace all occurences of a string sequence (reg.
	 *  ex.) with a given replacement string.
	 *
	 * @param  in               input string
	 * @param  find             regular expression to match against
	 * @param  replace          string to replace matches with
	 * @param  caseInsensitive  should matching be case insensitive?
	 * @return                  the newly altered string public static String
	 */
	public static String replace(String in,
			String find,
			String replace,
			boolean caseInsensitive) {
		String match;
		if (caseInsensitive) {
			match = in.toLowerCase();
			find = find.toLowerCase();
		}
		else {
			match = in;
		}
		StringBuffer ret = new StringBuffer();
		int ind1 = 0;
		int ind2 = match.indexOf(find);
		int findLength = find.length();
		while (ind2 > -1) {
			ret.append(in.substring(ind1, ind2))
					.append(replace);
			ind1 = ind2 + findLength;
			ind2 = match.indexOf(find, ind1);
		}
		ret.append(in.substring(ind1));
		return ret.toString();
	}
}

