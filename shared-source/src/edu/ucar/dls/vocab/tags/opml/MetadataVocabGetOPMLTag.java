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

package edu.ucar.dls.vocab.tags.opml;

import edu.ucar.dls.vocab.*;
import java.util.*;
import java.io.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.http.*;

/**
 *  Tag handler for retreiving a vocabulary format/version/audience/language
 *  grouping as OPML
 *
 * @author    Ryan Deardorff
 */
public class MetadataVocabGetOPMLTag extends MetadataVocabTag {

	String field = "";

	/**
	 *  Sets the field attribute of the MetadataVocabSetResponseGroupTag object
	 *
	 * @param  field  The new field value
	 */
	public void setField( String field ) {
		this.field = field;
	}

	/**
	 *  Set one of a potential list of response values
	 *
	 * @return
	 * @exception  JspException
	 */
	public int doStartTag() throws JspException {
		try {
			setupTag( pageContext );
			pageContext.getOut().print( vocab.getOPML( metaFormat, metaVersion, audience, language, field ) );
		}
		catch ( java.io.IOException ex ) {
			throw new JspException( ex.getMessage() );
		}
		return SKIP_BODY;
	}

	/**
	 *  Description of the Method
	 *
	 * @return
	 * @exception  JspException
	 */
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	/**
	 *  Description of the Method
	 */
	public void release() {
		super.release();
	}
}

