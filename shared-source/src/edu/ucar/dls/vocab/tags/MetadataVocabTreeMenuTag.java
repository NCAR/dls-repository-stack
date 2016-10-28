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
import java.io.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.http.*;

/**
 *  Tag handler for rendering vocabulary as a collapsable tree menu
 *
 * @author    Ryan Deardorff
 */
public class MetadataVocabTreeMenuTag extends MetadataVocabTag {
	String x;
	String y;
	String nsWidth = "0";
	String nsHeight = "0";

	/**
	 *  Sets the x attribute of the MetadataVocabTreeMenuTag object
	 *
	 * @param  x  The new x value
	 */
	public void setX( String x ) {
		this.x = x;
	}

	/**
	 *  Sets the y attribute of the MetadataVocabTreeMenuTag object
	 *
	 * @param  y  The new y value
	 */
	public void setY( String y ) {
		this.y = y;
	}

	/**
	 *  Sets the nsWidth attribute of the MetadataVocabTreeMenuTag object
	 *
	 * @param  nsWidth  The new nsWidth value
	 */
	public void setNsWidth( String nsWidth ) {
		this.nsWidth = nsWidth;
	}

	/**
	 *  Sets the nsHeight attribute of the MetadataVocabTreeMenuTag object
	 *
	 * @param  nsHeight  The new nsHeight value
	 */
	public void setNsHeight( String nsHeight ) {
		this.nsHeight = nsHeight;
	}

	/**
	 *  Render vocab as collapsable tree menu
	 *
	 * @return
	 * @exception  JspException
	 */
	public int doStartTag() throws JspException {
		try {
			setupTag( pageContext );
			pageContext.getOut().print( vocab.getVocabTreeMenu( system, language, group, pageContext ) );
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

