/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
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

