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

package edu.ucar.dls.util.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 *  Description of the Class
 *
 *@author     ryandear
 */
public class StoreBodyTag extends BodyTagSupport {
	private String save, print;
	private boolean isSave = false;

	/**
	 *  Sets the save attribute of the StoreBodyTag object
	 *
	 *@param  save  The new save value
	 */
	public void setSave( String save ) {
		this.save = save;
		isSave = true;
	}

	/**
	 *  Sets the print attribute of the StoreBodyTag object
	 *
	 *@param  print  The new print value
	 */
	public void setPrint( String print ) {
		this.print = print;
	}

	/**
	 *  Description of the Method
	 *
	 *@return                   Description of the Return Value
	 *@exception  JspException  Description of the Exception
	 */
	public int doAfterBody() throws JspException {
		try {
			if ( isSave ) {
				String body = bodyContent.getString();
				pageContext.setAttribute( "StoreBodyTag" + save, body );
				bodyContent.clearBody();
				bodyContent.print( body );
				bodyContent.writeOut( getPreviousOut() );
			}
			else {
				String body = (String)pageContext.getAttribute( "StoreBodyTag" + print );
				if ( body == null ) {
					body = "";
				}
				bodyContent.clearBody();
				bodyContent.print( body );
				bodyContent.writeOut( getPreviousOut() );
			}
		}
		catch ( java.io.IOException e ) {
			throw new JspException( e.getMessage() );
		}
		return SKIP_BODY;
	}
}

