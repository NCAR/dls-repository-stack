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

import edu.ucar.dls.ndr.apiproxy.*;
import edu.ucar.dls.ndr.connection.NDRConnection;
import edu.ucar.dls.xml.Dom4jUtils;
import org.dom4j.*;
import java.util.*;

/**
 *  Class to communiate directly with NDR via {@link edu.ucar.dls.ndr.connection.NDRConnection}.
 *  Builds the inputXML parameter that is sent as part a POST request.
 *
 * @author     Jonathan Ostwald
 * @version    $Id: SignedNdrRequest.java,v 1.4 2009/03/20 23:33:53 jweather Exp $
 */
public class SignedNdrRequest extends NdrRequest {

	/**  NOT YET DOCUMENTED */
	protected InputXML inputXML = null;
	/**  NOT YET DOCUMENTED */
	protected NDRConstants.NDRObjectType objectType = null;
	private String payload = null;


	/**  Constructor for the SignedNdrRequest object */
	public SignedNdrRequest() {
		super();
	}


	/**
	 *  Constructor for the SignedNdrRequest object with specified verb.
	 *
	 * @param  verb  NOT YET DOCUMENTED
	 */
	public SignedNdrRequest(String verb) {
		this();
		this.verb = verb;
	}


	/**
	 *  Constructor for the SignedNdrRequest object with specified verb and handle.
	 *
	 * @param  verb    NOT YET DOCUMENTED
	 * @param  handle  NOT YET DOCUMENTED
	 */
	public SignedNdrRequest(String verb, String handle) {
		this(verb);
		this.handle = handle;
	}

	public void authorizeToChange (String agentHandle) {
		this.authorizeToChange(agentHandle, null);
	}

	public void authorizeToChange (String agentHandle, String action) {
		this.addQualifiedCommand(NDRConstants.AUTH_NAMESPACE, 
								 "relationship", 
								 "authorizedToChange", 
								 agentHandle, 
								 action);
	}
	
	/**
	 *  Creates connection and adds payload in the form of inputXML parameter.<p>
	 *
	 *  Payload is the request objects's inputXML attribute, which is overidden by
	 *  the inputXMLStr parameter if present. This allows a caller to create an
	 *  inputXMLStr external to the request, which is helpful in debugging.
	 *
	 * @param  path           NOT YET DOCUMENTED
	 * @param  inputXMLStr    NOT YET DOCUMENTED
	 * @return                The nDRConnection value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	protected NDRConnection getNDRConnection(String path, String inputXMLStr) throws Exception {
		NDRConnection connection = super.getNDRConnection(path, inputXMLStr);

		connection.setKeyFile(NDRConstants.getPrivateKeyFile());
		connection.setAgentHandle(this.getRequestAgent());
		connection.setCanonicalHeader(true);
		
		return connection;
	}

	/**  NOT YET DOCUMENTED */
	public void report(String path) {
		prtln("SignedNdrRequest: submit");
		prtln("\t path: " + path);
		prtln("\t requestAgent: " + this.requestAgent);
		prtln("\t verbose: " + getVerbose());

	}

}

