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

package edu.ucar.dls.ndr.request;

import edu.ucar.dls.ndr.apiproxy.InfoXML;

import java.util.*; 

/**
 *  NOT YET DOCUMENTED
 *
 * @author     Jonathan Ostwald
 * @version    $Id: CountMembersRequest.java,v 1.2 2009/03/20 23:33:53 jweather Exp $
 */
public class CountMembersRequest extends NdrRequest {

	/**  Constructor for the CountMembersRequest object */
	public CountMembersRequest() {
		this.verb = "countMembers";
	}


	/**
	 *  Constructor for the CountMembersRequest object given a handle for a
	 Aggregator or MetadataProvider object.
	 *
	 * @param  objectHandle  handle for Aggregator or MetadataProvider
	 */
	public CountMembersRequest(String objectHandle) {
		this();
		this.setHandle(objectHandle);
	}

	/**
	 *  Gets the resultHandle attribute of the CountMembersRequest object
	 *
	 * @return                The resultHandle value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public int getCount() throws Exception {
		InfoXML response = this.submit();
		if (response.hasErrors())
			throw new Exception(response.getError());
		else
			return response.getCount();
	}

}


