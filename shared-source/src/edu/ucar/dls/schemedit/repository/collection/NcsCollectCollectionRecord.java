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

