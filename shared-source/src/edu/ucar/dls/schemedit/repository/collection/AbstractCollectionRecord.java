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
import edu.ucar.dls.serviceclients.handle.HandleServiceClient;
import edu.ucar.dls.serviceclients.handle.HandleServiceErrorResponseException;

/**
 *  Defines common functionality for Collection Records (items in a Collection
 *  of Collection Records), including ability to obtain a handle from the HRS.
 *
 * @author    Jonathan Ostwald
 */
public abstract class AbstractCollectionRecord implements CollectionRecord {

	private String id;
	private String handleServiceBaseUrl = null;
	private Document doc = null;


	/**
	 *  Constructor for the AbstractCollectionRecord object
	 *
	 * @param  id                    NOT YET DOCUMENTED
	 * @param  doc                   NOT YET DOCUMENTED
	 * @param  handleServiceBaseUrl  NOT YET DOCUMENTED
	 */
	public AbstractCollectionRecord(Document doc, String handleServiceBaseUrl) {
		this.doc = doc;	
		this.handleServiceBaseUrl = handleServiceBaseUrl;
	}


	/**
	 *  Gets the id attribute of the AbstractCollectionRecord object
	 *
	 * @return    The id value
	 */
	public abstract String getId();

	/**
	 *  Gets the setSpec attribute of the AbstractCollectionRecord object
	 *
	 * @return    The setSpec value
	 */
	public abstract String getSetSpec();


	/**
	 *  Gets the handleServiceBaseUrl attribute of the AbstractCollectionRecord
	 *  object
	 *
	 * @return    The handleServiceBaseUrl value
	 */
	public String getHandleServiceBaseUrl() {
		return this.handleServiceBaseUrl;
	}


	/**
	 *  Gets the metadataHandle from a HandleServiceClient
	 *
	 * @param  collectionSetSpec  NOT YET DOCUMENTED
	 * @return                    The metadataHandle value or null if one can't be found
	 */
	public String getMetadataHandle(String collectionSetSpec) throws Exception {
		String mdHandle = null;
		String errMsg = null;
		try {
			HandleServiceClient client = new HandleServiceClient(getHandleServiceBaseUrl());
			mdHandle = client.getMetadataHandle(getId(), collectionSetSpec);
		} catch (HandleServiceErrorResponseException e) {
			// for some reason, e.getServiceResponseMessage() is not returning message!?
			// errMsg = e.getServiceResponseCode() + ": " + e.getServiceResponseMessage();
			errMsg = "Unable to obtain metadataHandle: " + e.getMessage();
			System.out.println("caught HandleServiceErrorResponseException");
			throw new Exception(errMsg);
		} catch (Throwable t) {
			errMsg = "Unable to obtain metadataHandle: " + t.getMessage();
			// System.out.println(msg);
			throw new Exception(errMsg);
		}
		System.out.println("mdHandle: " + mdHandle);

		return mdHandle;
	}


	/**
	 *  Gets the document attribute of the AbstractCollectionRecord object
	 *
	 * @return    The document value
	 */
	public Document getDocument() {
		return this.doc;
	}

}

