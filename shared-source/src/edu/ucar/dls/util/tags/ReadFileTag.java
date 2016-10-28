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
import javax.servlet.jsp.tagext.TagSupport;
import java.io.*;

/**
 *  Tag for displaying the contents of a system file
 *
 *@author    ryandear
 */
public class ReadFileTag extends TagSupport {
	private String filename;


	/**
	 *  Sets the filename attribute of the ReadFileTag object
	 *
	 *@param  filename  The new filename value
	 */
	public void setFilename( String filename ) {
		this.filename = filename;
	}

	/**
	 *  Read the file, spit it out
	 *
	 *@exception  JspException  Description of the Exception
	 *@return                   Description of the Return Value
	 */
	public int doStartTag() throws JspException {
		try {
			FileReader file = new FileReader( filename );
			BufferedReader in = new BufferedReader( file );
			String s = null;
			while ( ( s = in.readLine() ) != null ) {
				pageContext.getOut().print( s );
			}
		}
		catch ( java.io.IOException e ) {
			throw new JspException( e.getMessage() );
		}
		return SKIP_BODY;
	}
}

