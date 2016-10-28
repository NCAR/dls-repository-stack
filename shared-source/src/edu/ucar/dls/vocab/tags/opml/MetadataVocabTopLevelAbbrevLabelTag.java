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
public class MetadataVocabTopLevelAbbrevLabelTag extends MetadataVocabTag {
	String fieldId = "";
	String valueId = "";
	String fieldName = "";

	/**
	 *  Sets the system attribute of the MetadataVocabGetFieldValueUiLabel object
	 *
	 * @param  fieldId  The new fieldId value
	 */
	public void setFieldId( String fieldId ) {
		this.fieldId = fieldId;
	}

	/**
	 *  Sets the fieldName attribute of the MetadataVocabTopLevelAbbrevLabelTag
	 *  object
	 *
	 * @param  fieldName  The new fieldName value
	 */
	public void setFieldName( String fieldName ) {
		this.fieldName = fieldName;
	}

	/**
	 *  Sets the system attribute of the MetadataVocabGetFieldValueUiLabel object
	 *
	 * @param  valueId  The new valueId value
	 */
	public void setValueId( String valueId ) {
		this.valueId = valueId;
	}

	/**
	 *  Start tag
	 *
	 * @return
	 * @exception  JspException
	 */
	public int doStartTag() throws JspException {
		try {
			setupTag( pageContext );
			StringBuffer outStr = new StringBuffer();
			pageContext.getOut().print( vocab.getTopLevelAbbrevLabelOf( system, fieldName, fieldId, valueId ) );
		}
		catch ( java.io.IOException ex ) {
			throw new JspException( ex.getMessage() );
		}
		return SKIP_BODY;
	}
}

