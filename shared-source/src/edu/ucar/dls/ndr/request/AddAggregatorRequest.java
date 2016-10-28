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

/**
 *  NOT YET DOCUMENTED
 *
 * @author     Jonathan Ostwald
 * @version    $Id: AddAggregatorRequest.java,v 1.7 2009/03/20 23:33:53 jweather Exp $
 */
public class AddAggregatorRequest extends SignedNdrRequest {

	/**  Constructor for the AddAggregatorRequest object */
	public AddAggregatorRequest() {
		super("addAggregator");
		this.setObjectType(NDRObjectType.AGGREGATOR);
	}


	/**
	 *  Convenience constructor that initializes the aggregatorFor releationship with provided agentHandle.
	 *
	 * @param  agentHandle  NOT YET DOCUMENTED
	 */
	public AddAggregatorRequest(String agentHandle) {
		this();
		setAggregatorFor(agentHandle);
	}


	/**
	 *  Sets the aggregatorFor attribute of the AddAggregatorRequest object
	 *
	 * @param  agentHandle  The new aggregatorFor value
	 */
	public void setAggregatorFor(String agentHandle) {
		this.addCommand("relationship", "aggregatorFor", agentHandle);
	}


	/**
	 *  Addes "associatedWith" relationship to provided resourceHandle.<p>
	 This relationship is used to connect a Collection Aggregator with the Collection Resource.
	 *
	 * @param  resourceHandle  NOT YET DOCUMENTED
	 */
	public void associateWith(String resourceHandle) {
		this.addCommand("relationship", "associatedWith", resourceHandle);
	}

}

