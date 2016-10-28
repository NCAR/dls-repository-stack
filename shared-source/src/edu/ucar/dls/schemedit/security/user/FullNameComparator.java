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

package edu.ucar.dls.schemedit.security.user;

import java.util.Comparator;
import java.io.Serializable;

/**
 *  Sort Users by fullName.
 *
 * @author    Jonathan Ostwald
 */

public class FullNameComparator implements Comparator, Serializable {

	/**
	 *  Create a string representing a User's full name (lastName first), using username if
	 * unable to use lastName and firstName.
	 *
	 * @param  user  NOT YET DOCUMENTED
	 * @return       NOT YET DOCUMENTED
	 */
	private String makeFullName(User user) {
		try {
			return user.getLastName().toUpperCase() + user.getFirstName().toUpperCase();
		} catch (Throwable t) {}
		return user.getUsername().toUpperCase();
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  o1  NOT YET DOCUMENTED
	 * @param  o2  NOT YET DOCUMENTED
	 * @return     NOT YET DOCUMENTED
	 */
	public int compare(Object o1, Object o2) {
		User u1 = (User) o1;
		User u2 = (User) o2;

		return makeFullName(u1).compareTo(makeFullName(u2));
	}
}

