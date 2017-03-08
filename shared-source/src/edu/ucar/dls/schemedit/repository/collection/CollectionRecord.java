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
package edu.ucar.dls.schemedit.repository.collection;

import java.util.*;

import org.dom4j.Document;

import edu.ucar.dls.schemedit.*;
import edu.ucar.dls.schemedit.dcs.DcsDataRecord;

/**
 *  Interface for classes that wrap a Collection Record (an Item in a Collection
 *  of Collection Records) in a repository that assignes handles to its contents
 *  (including Collection Records).
 *
 * @author    Jonathan Ostwald
 */
public interface CollectionRecord {

	/**
	 *  Gets the id attribute of the CollectionRecord object
	 *
	 * @return    The id value
	 */
	public String getId();


	/**
	 *  Gets the setSpec attribute of the CollectionRecord object
	 *
	 * @return    The setSpec value
	 */
	public String getSetSpec();


	/**
	 *  Gets the metadataHandle attribute of the CollectionRecord object
	 *
	 * @param  collectionSetSpec  setSpec (e.g., "dcc")
	 * @return                    The metadataHandle value
	 * @exception  Exception      if there is a webservice error
	 */
	public String getMetadataHandle(String collectionSetSpec) throws Exception;


	/**
	 *  Gets the handleServiceBaseUrl attribute of the CollectionRecord object
	 *
	 * @return    The handleServiceBaseUrl value
	 */
	public String getHandleServiceBaseUrl();

	/* 	public String getNativeFormat();
	public DcsDataRecord getDcsDataRecord();
	public MetaDataFramework getMetaDataFramework();
	public Document getLocalizedDocument(); */
}

