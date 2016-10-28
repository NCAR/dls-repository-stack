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
 *  Copyright 2002-2012 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */
package edu.ucar.dls.repository.indexing;

import javax.servlet.ServletContext;

/**
 *  Example/test IndexFieldPreprocessor that illustrates how the Interface might be used. Simply takes the
 *  content and appends the string 'MyText' at the begenning and adds another stand-alone token 'MyToken'.
 *
 * @author    John Weatherley
 */
public class TestIndexFieldPreprocessor implements IndexFieldPreprocessor {
	/**
	 *  Simply takes the content and appends the string 'MyText' at the begenning and adds another stand-alone
	 *  token 'MyToken'.
	 *
	 * @param  content       The existing content for a given field
	 * @param  newLuceneDoc  The new Lucene Document
	 * @param  xmlDoc        The XML Document
	 * @return               An array of length 2 with the stated content
	 */
	public String[] processFieldContent(String content, org.apache.lucene.document.Document newLuceneDoc, org.dom4j.Document xmlDoc) {
		String[] contentArray = new String[2];
		contentArray[0] = "MyToken";
		contentArray[1] = "MyText " + content;
		return contentArray;
	}


	/**
	 *  Checks if a ServletContext is availble and grabs config settings from it.
	 *
	 * @param  contextConfig  The context configuration object, which is an instance of ServletContext when
	 *      running in a webapp.
	 */
	public void contextConfigListener(Object contextConfig) {
		if (contextConfig != null && contextConfig instanceof ServletContext) {
			// Grab settings as needed from the servlet context...
			String myConfigSetting = ((ServletContext) contextConfig).getInitParameter("myConfigSetting");
		}
	}

}

