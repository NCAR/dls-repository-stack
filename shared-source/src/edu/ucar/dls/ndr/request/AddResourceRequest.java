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

import edu.ucar.dls.ndr.apiproxy.NDRConstants.NDRObjectType;

import org.dom4j.Element;
import org.dom4j.DocumentHelper;

/**
 *  *  Convenience class for creating NdrRequest to add a Resource
 *
 * @author     Jonathan Ostwald
 * @version    $Id: AddResourceRequest.java,v 1.6 2009/03/20 23:33:53 jweather Exp $
 */
public class AddResourceRequest extends SignedNdrRequest {

	/**  Constructor for the AddResourceRequest object */
	public AddResourceRequest() {
		super("addResource");
		this.setObjectType(NDRObjectType.RESOURCE);
	}


	/**
	 *  Sets the identifier attribute of the AddResourceRequest object
	 *
	 * @param  id  The new identifier value
	 */
	public void setIdentifier(String id) {
		setIdentifier(id, "URL");
	}


	/**
	 *  Sets the identifier attribute of the AddResourceRequest object
	 *
	 * @param  id    The new identifier value
	 * @param  type  The new identifier value
	 */
	public void setIdentifier(String id, String type) {
		Element identifier = DocumentHelper.createElement("identifier");
		identifier.setText(id);
		if (type != null)
			identifier.addAttribute("type", type);
		this.addCommand("property", identifier);
	}


	/**
	 *  Creates "memberOf" relationship to provided aggregatorHandle.
	 *
	 * @param  aggregatorHandle  The new memberOf value
	 */
	public void setMemberOf(String aggregatorHandle) {
		this.addCommand("relationship", "memberOf", aggregatorHandle);
	}

}

