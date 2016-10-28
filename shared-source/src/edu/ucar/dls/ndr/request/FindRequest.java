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
import edu.ucar.dls.ndr.apiproxy.NDRConstants.NDRObjectType;

import java.util.List;

/**
 *  Convenience method for constructing "find" NdrRequest.<p>
 *
 *  See <a href="http://ndr.comm.nsdl.org/cgi-bin/wiki.pl?find">NDR API
 *  documentation</>.
 *
 * @author     Jonathan Ostwald
 */
public class FindRequest extends NdrRequest {

	/**  Constructor for the FindRequest object */
	public FindRequest() {
		this.verb = "find";
	}


	/**
	 *  Constructor for the FindRequest to restrict hits to objects of specified type.
	 *
	 * @param  objectType  NOT YET DOCUMENTED
	 */
	public FindRequest(NDRObjectType objectType) {
		this();
		this.setObjectType(objectType);
	}


	/**
	 *  Gets the first handle (of potentially many) found.
	 *
	 * @return                The resultHandle value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public String getResultHandle() throws Exception {

		InfoXML response = this.submit();

		if (response.hasErrors())
			throw new Exception(response.getError());
		else
			return response.getHandle();
	}
	
	/**
	 *  Gets the resultHandle attribute of the ListMembersRequest object
	 *
	 * @return                The resultHandle value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public List getResultHandles() throws Exception {
		InfoXML response = this.submit();
		if (response.hasErrors())
			throw new Exception(response.getError());
		else
			return response.getHandleList();
	}

}

