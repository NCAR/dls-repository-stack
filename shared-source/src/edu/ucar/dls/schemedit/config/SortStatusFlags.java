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
 *  
 Comparator to sort {@link edu.ucar.dls.schemedit.config.StatusFlag}
	  instances natural order of their "label" property. <p>

 * @author    Jonathan Ostwald
  $Id: SortStatusFlags.java,v 1.3 2009/03/20 23:33:56 jweather Exp $
 */
public class SortStatusFlags implements Comparator {
	
	/**
	 *  Provide comparison for sorting StatusFlag by their label property
	 *
	 * @param  o1  StatusFlag 1
	 * @param  o2  StatusFlag 2
	 * @return     DESCRIPTION
	 */
	public int compare(Object o1, Object o2) {
			String string1 = ((StatusFlag) o1).getLabel().toLowerCase();
			String string2 = ((StatusFlag) o2).getLabel().toLowerCase();
		return string1.compareTo (string2);
	}

}

