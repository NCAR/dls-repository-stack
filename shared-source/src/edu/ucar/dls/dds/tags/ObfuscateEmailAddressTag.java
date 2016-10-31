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

import edu.ucar.dls.util.strings.Rot13;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import java.io.*;

/**
 *  Apply DLESE JavaScript rot13 email address encryption (for detering
 *  spambots)
 *
 * @author    Ryan Deardorff
 */
public class ObfuscateEmailAddressTag extends BodyTagSupport {

	/**
	 *  Return EVAL_BODY_BUFFERED for evaluation of body text
	 *
	 * @return
	 * @exception  JspException
	 */
	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	/**
	 *  Description of the Method
	 *
	 * @return
	 * @exception  JspException
	 */
	public int doEndTag() throws JspException {
		BodyContent body = getBodyContent();
		try {
			JspWriter out = body.getEnclosingWriter();
			String address = body.getString().trim();
			int ind = address.indexOf( "@" );
			if ( ind > -1 ) {
				String prefix = address.substring( 0, ind );
				String postfix = address.substring( ind + 1, address.length() );
				out.print( "<script type='text/javascript'><!--\n\tdlese_rea13( '"
					 + Rot13.crypt( prefix ) + "', '" + Rot13.crypt( postfix )
					 + "' );\n// -->\n</script>\n" );
			}
		}
		catch ( IOException ioe ) {
			System.out.println( "Error in ObfuscateEmailAddressTag: " + ioe );
		}
		return SKIP_BODY;
	}
}
