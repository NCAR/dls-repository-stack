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

package edu.ucar.dls.dds;

import edu.ucar.dls.index.*;
import org.apache.lucene.queryParser.ParseException;

/**
 *  Structure that holds the results of a standard DDS text/field search. This Object is returned by {@link
 *  edu.ucar.dls.dds.action.DDSQueryAction#ddsStandardQuery} and is used by {@link
 *  edu.ucar.dls.dds.action.DDSQueryAction} and {@link edu.ucar.dls.services.dds.action.DDSServicesAction}.
 *
 * @author    John Weatherley
 * @see       edu.ucar.dls.dds.action.DDSQueryAction
 * @see       edu.ucar.dls.services.dds.action.DDSServicesAction
 */
public final class DDSStandardSearchResult {
	private String forwardName = null;
	private ResultDocList results = null;
	private Exception ex = null;



	/**  Constructor for the DDSStandardSearchResult object */
	public DDSStandardSearchResult() { }


	/**
	 *  Constructor for the DDSStandardSearchResult object
	 *
	 * @param  results         The search results.
	 * @param  forwardName     The name that describes which page to forward to to render the results.
	 * @param  exception  		An Exception if one occured, or null
	 */
	public DDSStandardSearchResult(ResultDocList results, Exception exception, String forwardName) {
		ex = exception;
		this.results = results;
		this.forwardName = forwardName;
	}


	/**
	 *  Gets the search results.
	 *
	 * @return    The results value
	 */
	public ResultDocList getResults() {
		return results;
	}


	/**
	 *  Gets the Exception if one occured, or null if none.
	 *
	 * @return    An Exception or null
	 */
	public Exception getException() {
		return ex;
	}


	/**
	 *  Gets the name of the page to forward to, which is one of 'simple.query' or 'whats.new.query'.
	 *
	 * @return    The forwardName value
	 */
	public String getForwardName() {
		return forwardName;
	}
}



