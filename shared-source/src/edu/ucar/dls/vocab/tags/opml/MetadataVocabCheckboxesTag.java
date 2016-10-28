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
import edu.ucar.dls.vocab.MetadataVocabInputState;
import edu.ucar.dls.util.strings.StringUtil;
import java.util.*;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.*;

/**
 *  Tag handler for rendering vocabulary checkboxes
 *
 * @author    Ryan Deardorff
 */
public class MetadataVocabCheckboxesTag extends MetadataVocabTag {
	private String value = null;
	private String label = null;
	private String tdWidth = "";
	private boolean skipTopRow = false;
	private int wrap = 100000;

	/**
	 *  Sets the wrap attribute of the MetadataVocabCheckboxesTag object
	 *
	 * @param  wrap  The new wrap value
	 */
	public void setWrap( String wrap ) {
		this.wrap = Integer.parseInt( wrap );
	}

	/**
	 *  Sets the value attribute of the MetadataVocabCheckboxesTag object
	 *
	 * @param  value  The new value value
	 */
	public void setValue( String value ) {
		this.value = value;
	}

	/**
	 *  Sets the label attribute of the MetadataVocabCheckboxesTag object
	 *
	 * @param  label  The new label value
	 */
	public void setLabel( String label ) {
		this.label = label;
	}

	/**
	 *  Sets the skipTopRow attribute of the MetadataVocabCheckboxesTag object
	 *
	 * @param  skipTopRow  The new skipTopRow value
	 */
	public void setSkipTopRow( String skipTopRow ) {
		if ( skipTopRow.equals( "true" ) ) {
			this.skipTopRow = true;
		}
		else {
			this.skipTopRow = false;
		}
	}

	/**
	 *  Sets the tdWidth attribute of the MetadataVocabCheckboxesTag object
	 *
	 * @param  tdWidth  The new tdWidth value
	 */
	public void setTdWidth( String tdWidth ) {
		this.tdWidth = tdWidth;
	}

	/**
	 *  Description of the Method
	 *
	 * @return
	 * @exception  JspException
	 */
	public int doStartTag() throws JspException {
		try {
			setupTag( pageContext );
			MetadataVocabInputState inputState = (MetadataVocabInputState)pageContext.getSession().getAttribute( "MetadataVocabInputState" );
			if ( inputState == null ) {
				inputState = new MetadataVocabInputState();
			}
			if ( value != null ) {
				pageContext.getOut().print( vocab.getVocabCheckbox( system, value, label, inputState ) );
			}
			else {
				pageContext.getOut().print( vocab.getVocabCheckboxes( system, subGroup, wrap, tdWidth, skipTopRow, inputState ) );
			}
		}
		catch ( java.io.IOException ex ) {
			throw new JspException( ex.getMessage() );
		}
		return SKIP_BODY;
	}
}

