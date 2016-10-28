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

package edu.ucar.dls.oai.harvester;

/**
 *  Indicates an <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions">
 *  OAI protocol error code</a> was returned by the data provider during a harvest.
 *
 * @author    John Weatherley
 */
public class OAIErrorException extends Exception {
	String errorCode = null;
	String errorMsg = null;


	/**
	 *  Constructor for the OAIErrorException object
	 *
	 * @param  errorCode  The OAI error code.
	 * @param  errorMsg   Description of the error.
	 */
	public OAIErrorException(String errorCode, String errorMsg) {
		super("OAI code '" + errorCode + "' was returned by the data provider. Message: " + errorMsg);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}


	/**
	 *  Gets the oAIErrorCode attribute of the OAIErrorException object
	 *
	 * @return    The oAIErrorCode value
	 */
	public String getOAIErrorCode() {
		return errorCode;
	}


	/**
	 *  Gets the oAIErrorMessage attribute of the OAIErrorException object
	 *
	 * @return    The oAIErrorMessage value
	 */
	public String getOAIErrorMessage() {
		return errorMsg;
	}

}

