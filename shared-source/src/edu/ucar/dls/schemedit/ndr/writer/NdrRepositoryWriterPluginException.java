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

package edu.ucar.dls.schemedit.ndr.writer;

import edu.ucar.dls.schemedit.repository.RepositoryWriterPluginException;


/**
 *  Indicates a problem occured when attempting to interact with a RepositoryWriterPlugin.
 *
 * @author    Jonathan Ostwald
 * @see       RepositoryWriter
 */
public class NdrRepositoryWriterPluginException extends RepositoryWriterPluginException {
	
	private String pluginName = "";
	private String details = "";
	
	public NdrRepositoryWriterPluginException(String operation){
		this(operation, null, null);	
	}
	
	public NdrRepositoryWriterPluginException(Throwable cause) {
		this(null, cause, null);
	}
	
	public NdrRepositoryWriterPluginException(String operation, String details) {
		this(operation, null, details);
	}
	
	public NdrRepositoryWriterPluginException(String operation, Throwable cause) {
		this (operation, cause, null);
	}
	
	public NdrRepositoryWriterPluginException(String operation, Throwable cause, String details) {
		super("NdrPlugin", operation, cause, details);
	}	

	// public RepositoryWriterPluginException(){}
}

