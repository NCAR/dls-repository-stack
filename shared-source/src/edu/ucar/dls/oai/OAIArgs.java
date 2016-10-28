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

import java.util.*;

/**
 *  This class contains OAI argument constants that are used throughout the OAI application.
 *
 * @author    John Weatherley
 */
public final class OAIArgs {

	// ---------- All valid OAI-PMH arguments -----------------

	/**  The verb argument */
	public final static String VERB = "verb";
	/**  The identifier argument */
	public final static String IDENTIFIER = "identifier";
	/**  The metadataPrefix argument */
	public final static String METADATA_PREFIX = "metadataPrefix";
	/**  The from argument */
	public final static String FROM = "from";
	/**  The until argument */
	public final static String UNTIL = "until";
	/**  The set argument */
	public final static String SET = "set";
	/**  The resumptionToken argument */
	public final static String RESUMPTION_TOKEN = "resumptionToken";

	/**  Array of all valid arguments. */
	public final static String[] ALL_VALID_OAI_ARGUMENTS =
		{VERB, IDENTIFIER, METADATA_PREFIX, FROM, UNTIL, SET, RESUMPTION_TOKEN};

	/**  Map of all valid arguments. Both key and value contain the same argument String. */
	public static Map ALL_VALID_OAI_ARGUMENTS_MAP;

	static {
		ALL_VALID_OAI_ARGUMENTS_MAP = new HashMap(7);
		for (int i = 0; i < ALL_VALID_OAI_ARGUMENTS.length; i++)
			ALL_VALID_OAI_ARGUMENTS_MAP.put(ALL_VALID_OAI_ARGUMENTS[i], ALL_VALID_OAI_ARGUMENTS[i]);
	}

	// ---------- All valid OAI-PMH argument values -----------------

	/**  The Identify argument value */
	public final static String IDENTIFY = "Identify";
	/**  The GetRecord argument value */
	public final static String GET_RECORD = "GetRecord";
	/**  The ListMetadataFormats argument value */
	public final static String LIST_METADATA_FORMATS = "ListMetadataFormats";
	/**  The ListIdentifiers argument value */
	public final static String LIST_IDENTIFIERS = "ListIdentifiers";
	/**  The ListRecords argument value */
	public final static String LIST_RECORDS = "ListRecords";
	/**  The ListSets argument value */
	public final static String LIST_SETS = "ListSets";

}

