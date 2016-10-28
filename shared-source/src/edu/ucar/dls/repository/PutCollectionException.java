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

package edu.ucar.dls.repository;



/**
 *  Indicates a problem occured when attempting to add, modify or delete a
 *  collection in the repository.
 *
 * @author    John Weatherley
 * @see       RepositoryManager
 */
public class PutCollectionException extends Exception {
	
	public static final String ERROR_CODE_COLLECTION_EXISTS_IN_ANOTHER_FORMAT = "COLLECTION_EXISTS_IN_ANOTHER_FORMAT";
	public static final String ERROR_CODE_BAD_FORMAT_SPECIFIER = "BAD_FORMAT_SPECIFIER";
	public static final String ERROR_CODE_BAD_KEY = "BAD_KEY";
	public static final String ERROR_CODE_BAD_TITLE = "BAD_TITLE";
	public static final String ERROR_CODE_BAD_ADDITIONAL_METADATA = "BAD_ADDITIONAL_METADATA";
	public static final String ERROR_CODE_IO_ERROR = "IO_ERROR";
	public static final String ERROR_CODE_INTERNAL_ERROR = "INTERNAL_ERROR";
	
	private String _errorCode;
	PutCollectionException(String message, String errorCode){
		super(message);
		_errorCode = errorCode; 
	}
	
	public String getErrorCode(){
		return _errorCode;
	}
}

