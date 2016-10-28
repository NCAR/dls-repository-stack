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

package edu.ucar.dls.suggest.resource.urlcheck;

import edu.ucar.dls.schemedit.url.DupSim;
import java.util.Comparator;


/**
 *  Comparator to sort DupSim instances by order of their IDs.
 *
 *@author    ostwald<p>
 $Id $
 */
public class SimSorter implements Comparator {

	public static boolean debug = true;
	/**
	 *  Description of the Method
	 *
	 *@param  o1  Description of the Parameter
	 *@param  o2  Description of the Parameter
	 *@return     Description of the Return Value
	 */
	public int compare(Object o1, Object o2) {
		String string1 = ((DupSim) o1).getId().toLowerCase();
		String string2 = ((DupSim) o2).getId().toLowerCase();
		return string1.compareTo(string2);
	}
}

