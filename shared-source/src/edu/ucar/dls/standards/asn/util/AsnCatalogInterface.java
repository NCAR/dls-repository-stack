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
package edu.ucar.dls.standards.asn.util;

import java.util.*;
import org.dom4j.*;

/**
 *
 * @author    Jonathan Ostwald
 */
public interface AsnCatalogInterface {


	/**
	 *  Gets the item attribute of the AsnCatalog object
	 *
	 * @param  asnId  NOT YET DOCUMENTED
	 * @return        The item value
	 */
	public AsnCatItemInterface getItem(String asnId);


	/**
	 *  Gets the status attribute of the AsnCatalog object
	 *
	 * @param  asnId  NOT YET DOCUMENTED
	 * @return        The status value
	 */
	public String getStatus(String asnId);

	/**
	 *  Gets the subjects attribute of the AsnCatalog object
	 *
	 * @return    The subjects value
	 */
	public List getSubjects();

	/**
	 *  Gets the subjectItems attribute of the AsnCatalog object
	 *
	 * @return    The subjectItems value
	 */
	public Map getSubjectItemsMap();

}

