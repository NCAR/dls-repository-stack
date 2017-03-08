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

import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.schemedit.standards.asn.AsnDocKey;
import org.dom4j.*;
import java.io.*;

/**
 *  Represents a single ASN Catalog item (an ASN Document) with attributes
 *  obtained from mapping file
 *
 * @author    Jonathan Ostwald
 */
public interface AsnCatItemInterface {

	/**
	 *  hack to get the status out of the acsrInfo branch, which is a sibling to
	 *  this.element
	 *
	 * @return    The status value
	 */
	public String getStatus();


	/**
	 *  Gets the uid attribute of the AsnCatItem object
	 *
	 * @return    The uid value
	 */
	public String getUid();

	/**
	 *  Gets the key attribute of the AsnCatItem object
	 *
	 * @return    The key value
	 */
	public String getKey();


	/**
	 *  Gets the id attribute of the AsnCatItem object
	 *
	 * @return    The id value
	 */
	public String getId();


	/**
	 *  Gets the title attribute of the AsnCatItem object
	 *
	 * @return    The title value
	 */
	public String getTitle();


	/**
	 *  Gets the created attribute of the AsnCatItem object
	 *
	 * @return    The created value
	 */
	public String getCreated();


	/**
	 *  Gets the topic attribute of the AsnCatItem object
	 *
	 * @return    The topic value
	 */
	public String getTopic();


	/**
	 *  Gets the author attribute of the AsnCatItem object
	 *
	 * @return    The author value
	 */
	public String getAuthor();
	public String getAuthorName();


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @return    NOT YET DOCUMENTED
	 */
	public String toString();

}


