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

package edu.ucar.dls.schemedit.dcs;

import java.util.Comparator;
import java.util.Date;
import edu.ucar.dls.util.MetadataUtils;
import java.text.*;

/**
 *  
 Comparator to sort {@link edu.ucar.dls.schemedit.dcs.StatusEntry}
	  elements in reverse order of their "dateChanged" property. <p>

 * @author    Jonathan Ostwald
  $Id: SortStatusEntries.java,v 1.2 2009/03/20 23:33:56 jweather Exp $
 */
public class SortStatusEntries implements Comparator {
	
	/**
	 *  Provide comparison for sorting Sugest a URL Records by  "lastModified" property
	 *
	 * @param  o1  document 1
	 * @param  o2  document 2
	 * @return     DESCRIPTION
	 */
	public int compare(Object o1, Object o2) {
		Date dateOne;
		Date dateTwo;
		try {
			dateOne = ((StatusEntry) o1).getDate();
			dateTwo = ((StatusEntry) o2).getDate();
		} catch (Exception e) {
			prtlnErr("Error: unable to find last modified date: " + e.getMessage());
			return 0;
		}
		return dateTwo.compareTo (dateOne);
	}
	
	private static void prtln(String s) {
		System.out.println(s);
	}


	private static void prtlnErr(String s) {
		System.err.println(s);
	}
	
}

