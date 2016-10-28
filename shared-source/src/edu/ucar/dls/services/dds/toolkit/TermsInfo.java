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
package edu.ucar.dls.services.dds.toolkit;

import java.util.*;

/**
 *  Class that holds data about the terms in one or more fields in the index. Wraps the data returned from the
 *  ListTerms request to Java Maps and Objects.
 *
 * @author    John Weatherley
 */
public class TermsInfo {
	private List fields = null;
	private Map terms = null;
	private String indexVersion = null;
	private int totalNumTerms = 0;


	/**
	 *  Constructor for the TermsInfo object
	 *
	 * @param  fields         Fields
	 * @param  terms          Terms
	 * @param  indexVersion   Index version
	 * @param  totalNumTerms  Total num terms in given fields
	 */
	public TermsInfo(List fields,
	                 Map terms,
	                 String indexVersion,
	                 int totalNumTerms) {
		this.fields = fields;
		this.terms = terms;
		this.indexVersion = indexVersion;
		this.totalNumTerms = totalNumTerms;
	}


	/**
	 *  Gets a list of the fields in which the terms reside.
	 *
	 * @return    The fields value
	 */
	public List getFields() {
		return fields;
	}


	/**
	 *  Returns a Map of {@link TermData} Objects, keyed by term String.
	 *
	 * @return    The terms value
	 */
	public Map getTerms() {
		return terms;
	}


	/**
	 *  Returns version of the index at the time the request was made.
	 *
	 * @return    The indexVersion value
	 */
	public String getIndexVersion() {
		return indexVersion;
	}


	/**
	 *  Returns the total number of terms found in the given field(s).
	 *
	 * @return    The totalNumTerms value
	 */
	public int getTotalNumTerms() {
		return totalNumTerms;
	}

	/**
	 *  Debugging
	 *
	 * @return    Stringi representation of the TermsInfo object
	 */
	public String toString() {
		return "fields: " + Arrays.toString((String[])fields.toArray(new String[]{})) + ", totalNumTerms: " + totalNumTerms + ", termsMapSize: " + terms.size() + ", indexVersion: " + indexVersion;
	}

}

