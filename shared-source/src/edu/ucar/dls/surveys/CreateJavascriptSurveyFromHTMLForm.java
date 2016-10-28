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

package edu.ucar.dls.surveys;

import edu.ucar.dls.util.*;
import java.util.*;
import java.io.*;

/**
 *  Parse the HTML form found (hopefully) at the given URL, and turn it into a
 *  Javascript-rendered version that can easily be included in the DLESE site.
 *  This assumes a Perseus processor as the form submission handler.
 *
 * @author    Ryan Deardorff
 */
public class CreateJavascriptSurveyFromHTMLForm {

	/**
	 *  Constructor for the CreateJavascriptSurveyFromHTMLForm object
	 *
	 * @param  URL
	 * @param  saveFile
	 */
	public CreateJavascriptSurveyFromHTMLForm( String URL, String saveFile ) {
		String html = GetURL.getURL( URL, true );
		html = html.replaceAll( "[\\s\\S]*(<[Ff][Oo][Rr][Mm][\\s\\S]*</[Ff][Oo][Rr][Mm]>)[\\s\\S]*", "\n$1" );
		html = html.replaceAll( "'", "\\\\'" );
		html = html.replaceAll( "\n([^\n]+)", "\n\t\thtm += '$1';" );
		String jsOut = "function renderSurvey() {\n\tvar htm = '';" + html + "\n\t"
			 + "var obj = document.getElementById( 'dleseSurvey' ); if ( obj != null ) obj.innerHTML = htm; \n}\n";
		jsOut = jsOut.replaceFirst( "(<form[^>]+)>", "$1 onSubmit=\"dlese_surveySubmitted()\">" );
		jsOut = jsOut.replaceFirst( "value=\\\"Location:http:[^\\\"]+\\\"", "value=\"Location:**DLESE_REDIRECT**\"" );
		try {
			Files.writeFile( jsOut, saveFile );
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 *  Description of the Method
	 *
	 * @param  args
	 */
	public static void main( String[] args ) {
		if ( args.length < 2 ) {
			System.out.println( "Format: CreateJavascriptSurveyFromHTMLForm [inputUrl] [outputFilename]" );
		}
		else {
			new CreateJavascriptSurveyFromHTMLForm( args[0], args[1] );
		}
	}
}

