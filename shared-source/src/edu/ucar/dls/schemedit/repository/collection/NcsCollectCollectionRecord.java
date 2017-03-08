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
import edu.ucar.dls.schemedit.repository.RepositoryService;
import edu.ucar.dls.schemedit.dcs.DcsDataRecord;
import edu.ucar.dls.schemedit.ndr.util.NCSCollectReader;
import edu.ucar.dls.index.reader.XMLDocReader;
import edu.ucar.dls.xml.Dom4jUtils;

/**
 *  Extends AbstractCollectionRecord to provide functionality specific to
 *  ncs_collect records with help from the NCSCollectReader class.
 *
 * @author    Jonathan Ostwald
 */
public class NcsCollectCollectionRecord extends AbstractCollectionRecord {

	private static boolean debug = false;

	private NCSCollectReader ncsCollectReader = null;


	/**
	 *  Constructor for the NcsCollectCollectionRecord object given an id, a
	 *  ncs_collect record, and a baseUrl to a handle service.
	 *
	 * @param  doc                   the ncs_collect record DOM
	 * @param  handleServiceBaseUrl  handleService
	 */
	public NcsCollectCollectionRecord(Document doc, String handleServiceBaseUrl) {
		super(doc, handleServiceBaseUrl);
		this.ncsCollectReader = new NCSCollectReader(Dom4jUtils.localizeXml(doc));
	}


	/**
	 *  Gets the setSpec attribute of the NcsCollectCollectionRecord object
	 *
	 * @return    The setSpec value
	 */
	public String getSetSpec() {
		return this.ncsCollectReader.getSetSpec();
	}


	/**
	 *  Gets the id attribute of the NcsCollectCollectionRecord object
	 *
	 * @return    The id value
	 */
	public String getId() {
		return this.ncsCollectReader.getRecordID();
	}


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		System.out.println(s);
	}
}

