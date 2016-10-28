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

package edu.ucar.dls.dds.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.PageContext;
import edu.ucar.dls.dds.KeywordsHighlight;

/**
 *  Provide keyword highlighting of the body content of this tag.
 *
 *@author    Ryan Deardorff
 */
public class SetKeywordsHighlightTag extends TagSupport {
	String keywords;
	String highlightColor = null;
	String cssClassName = null;

	/**
	 *  Sets the keywords attribute of the KeywordsHighlightTag object
	 *
	 *@param  keywords  The new keywords value
	 */
	public void setKeywords( String keywords ) {
		this.keywords = keywords;
	}

	/**
	 *  Sets the highlightColor attribute of the SetKeywordsHighlightTag object
	 *
	 *@param  highlightColor  The new highlightColor value
	 */
	public void setHighlightColor( String highlightColor ) {
		this.highlightColor = highlightColor;
	}

	/**
	 *  Sets the cssClassName attribute of the SetKeywordsHighlightTag object
	 *
	 *@param  cssClassName  The new cssClassName value
	 */
	public void setCssClassName( String cssClassName ) {
		this.cssClassName = cssClassName;
	}	
	
	/**
	 *  Description of the Method
	 *
	 *@exception  JspException
	 */
	public int doStartTag() throws JspException {
		KeywordsHighlight keysHighlight = new KeywordsHighlight( keywords, highlightColor, cssClassName );
		pageContext.getSession().setAttribute( "KeywordsHighlight", keysHighlight );
		return SKIP_BODY;
	}
}

