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

package edu.ucar.dls.vocab;

import edu.ucar.dls.vocab.MetadataVocab;
import javax.servlet.jsp.PageContext;
import java.io.Serializable;
import java.util.HashMap;

/**
 *  Stores a list of metadata values that come from a services/indexer response,
 *  for reproducing with OPML-defined order/groupings/labels
 *
 * @author    Ryan Deardorff
 */
public class MetadataVocabResponseMap implements Serializable {

	public String audience = "";
	public String metaFormat = "";
	public String metaVersion = "";
	public String language = "";
	public String field = "";
	protected HashMap map = new HashMap();

	/**
	 *  Audience is a user group, i.e. "default", "community", or "cataloger"
	 *
	 * @param  audience     The new audience value
	 * @param  metaFormat
	 * @param  metaVersion
	 * @param  language
	 * @param  field
	 */
	public MetadataVocabResponseMap( String metaFormat, String metaVersion, String audience, String language, String field ) {
		this.metaFormat = metaFormat;
		this.metaVersion = metaVersion;
		this.audience = audience;
		this.language = language;
		this.field = field;
	}

	/**
	 *  Indicates a particular value in a list of response values
	 *
	 * @param  value  The new value value
	 */
	public void setValue( String value ) {
		map.put( value, new Boolean( true ) );
	}

	/**
	 *  Has the specified value been cached in the response?
	 *
	 * @param  value
	 * @return
	 */
	public boolean hasValue( String value ) {
		if ( value == null ) {
			return false;
		}
		if ( map.get( value ) != null ) {
			return true;
		}
		return false;
	}
}

