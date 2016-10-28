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

package edu.ucar.dls.oai;

/**
 *  An internal data structure that holds an individual OAI error code and a
 *  human-readable description that accompanies it.
 *
 * @author    John Weatherley
 */
public class OAIError {
	private String errorCode, message;


	/**
	 *  Constructor for the OAIError object
	 *
	 * @param  message    A human-readable message that describes this error.
	 * @param  errorCode  The OAI ErrorCode for this error.
	 */
	public OAIError(String errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}


	/**
	 *  Gets the errorCode attribute of the OAIError object
	 *
	 * @return    The errorCode value
	 */
	public String getErrorCode() {
		return errorCode;
	}


	/**
	 *  Gets the message attribute of the OAIError object
	 *
	 * @return    The message value
	 */
	public String getMessage() {
		return message;
	}
}

