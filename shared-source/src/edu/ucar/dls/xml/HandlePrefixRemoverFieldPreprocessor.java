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
package edu.ucar.dls.xml;

import edu.ucar.dls.repository.indexing.IndexFieldPreprocessor;

/**
 *  Strips the handle prefix 'hdl:' from the beginning of a String.
 *
 * @author    John Weatherley
 */
public class HandlePrefixRemoverFieldPreprocessor implements IndexFieldPreprocessor {
	/**
	 *  Strips the handle prefix 'hdl:' from the beginning of a String.
	 *
	 * @param  content       The existing content for a given field
	 * @param  newLuceneDoc  Not used
	 * @param  xmlDoc        Not used
	 * @return               An array of length 1 with stripped handle prefx
	 */
	public String[] processFieldContent(String content, org.apache.lucene.document.Document newLuceneDoc, org.dom4j.Document xmlDoc) {
		if (content == null)
			return null;
		return new String[]{content.replaceFirst("hdl:", "")};
	}


	/**
	 *  Does nothing.
	 *
	 * @param  contextConfig  The context configuration object, which is an instance of ServletContext when
	 *      running in a webapp.
	 */
	public void contextConfigListener(Object contextConfig) { }

}

