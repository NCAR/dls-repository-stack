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
import edu.ucar.dls.ndr.apiproxy.NDRConstants;

/**
*  Convenience class for creating NdrRequests to delete an NDR Object.
 *
 * @author     Jonathan Ostwald
 * @version    $Id: DeleteRequest.java,v 1.7 2009/03/20 23:33:53 jweather Exp $
 */
public class DeleteRequest extends SignedNdrRequest {

	private boolean cascade = false;


	/**  Constructor for the DeleteRequest object */
	public DeleteRequest() {
		super("delete");
	}


	/**
	 *  Constructor for the DeleteRequest object with provided handle to ndrObject.
	 *
	 * @param  handle  NOT YET DOCUMENTED
	 */
	public DeleteRequest(String handle) {
		this();
		this.handle = handle;
	}


	/**
	 *  Constructor for the DeleteRequest object
	 *
	 * @param  handle   NOT YET DOCUMENTED
	 * @param  cascade  NOT YET DOCUMENTED
	 */
	public DeleteRequest(String handle, boolean cascade) {
		this(handle);
		this.cascade = cascade;
	}

	protected String makePath () throws Exception {
		String path = super.makePath();
		if (cascade)
			path = path + "?cascade=true";
		return path;
	}

}

