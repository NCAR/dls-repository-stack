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

package edu.ucar.dls.vocab.tags;

import edu.ucar.dls.vocab.MetadataVocab;
import edu.ucar.dls.vocab.MetadataVocabInputState;
import java.util.*;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.*;

/**
 *  Tag handler for rendering vocabulary select list inputs
 *
 * @author    Ryan Deardorff
 */
public class MetadataVocabSelectListTag extends MetadataVocabTag {
	private int size = 1;                          // Set default height of 1 for 'drop-down' list

	/**
	 *  Sets the size attribute of the MetadataVocabSelectListTag object
	 *
	 * @param  size  The new size value
	 */
	public void setSize( String size ) {
		this.size = Integer.parseInt( size );
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
			pageContext.getOut().print( vocab.getVocabSelectList( system, group, size, inputState ) );
		}
		catch ( java.io.IOException ex ) {
			throw new JspException( ex.getMessage() );
		}
		return SKIP_BODY;
	}
}

