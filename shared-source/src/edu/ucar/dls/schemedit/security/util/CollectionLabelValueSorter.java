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

package edu.ucar.dls.schemedit.security.util;

import java.util.Comparator;
import java.io.Serializable;
import org.apache.struts.util.LabelValueBean;
import edu.ucar.dls.schemedit.security.access.Roles;

/**
 *  Sort LabelValueBeans representing [collectionName, collectionKey] by the label (collectionName).
 *
 * @author    Jonathan Ostwald
 */
public class CollectionLabelValueSorter implements Comparator, Serializable {

	/**
	 *  Compare the labels of the provided LabelValueBeans.
	 *
	 * @param  o1  NOT YET DOCUMENTED
	 * @param  o2  NOT YET DOCUMENTED
	 * @return     NOT YET DOCUMENTED
	 */
	public int compare(Object o1, Object o2) {

		LabelValueBean lvb1 = (LabelValueBean) o1;
		LabelValueBean lvb2 = (LabelValueBean) o2;

		String s1 = lvb1.getLabel();
		String s2 = lvb2.getLabel();

		if (s1 == null)
			s1 = "";
		if (s2 == null)
			s2 = "";

		if (s1.equals(s2))
			return 0;

		return s1.compareTo(s2);
	}
}

