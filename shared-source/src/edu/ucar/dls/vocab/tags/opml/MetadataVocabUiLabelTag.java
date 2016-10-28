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

import edu.ucar.dls.vocab.MetadataVocab;
import edu.ucar.dls.util.strings.StringUtil;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.*;

/**
 *  Tag handler for retreiving a UI label from the given field and value
 *
 * @author    Ryan Deardorff
 */
public class MetadataVocabUiLabelTag extends MetadataVocabTag {
	String field = "";
	String value = "";
	boolean getAbbreviated = false;                                // return abbreviated version of label?

	/**
	 *  Sets the system attribute of the MetadataVocabGetFieldValueUiLabel object
	 *
	 * @param  field  The new fieldId value
	 */
	public void setField( String field ) {
		this.field = field;
	}

	/**
	 *  Sets the system attribute of the MetadataVocabGetFieldValueUiLabel object
	 *
	 * @param  value  The new value value
	 */
	public void setValue( String value ) {
		this.value = value;
	}

	/**
	 *  Sets the getAbbreviated attribute of the MetadataVocabUiLabelTag object
	 *
	 * @param  bool  The new getAbbreviated value
	 */
	public void setGetAbbreviated( String bool ) {
		if ( bool.toLowerCase().equals( "true" ) ) {
			getAbbreviated = true;
		}
	}

	/**
	 *  Start tag
	 *
	 * @return
	 * @exception  JspException
	 */
	public int doStartTag() throws JspException {
		setupTag( pageContext );
		StringBuffer outStr = new StringBuffer();
		try {
			if ( value.equals( "" ) ) {
				pageContext.getOut().print( vocab.getUiFieldLabel( metaFormat, metaVersion,
					audience, language, field, getAbbreviated ) );
			}
			else {
				pageContext.getOut().print( vocab.getUiValueLabel( metaFormat, metaVersion,
					audience, language, field, value, getAbbreviated ) );
			}
		}
		catch ( Exception e ) {
			System.out.println( "Exception in MetadataVocabUiLabelTag: " + e.getMessage() );
			throw new JspException( e.getMessage() );
		}
		return SKIP_BODY;
	}
}

