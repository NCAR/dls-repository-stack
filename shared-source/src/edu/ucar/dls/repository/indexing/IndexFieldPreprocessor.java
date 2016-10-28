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

/**
 *  Interface used to pre-process content for a custom field prior to indexing it. A single instance of the
 *  concrete class will be created and reused when processing the configured field(s) for each {@link
 *  org.apache.lucene.document.Document} that is added to the index.
 *
 * @author    John Weatherley
 */
public interface IndexFieldPreprocessor {
	/**
	 *  Implement this method to pre-process the configured custom XPath field content for each Lucene Document.
	 *  The existing content is passed in as a String and the resulting preprocessed content must be passed back
	 *  as an array of Strings. For each element in the array, a saparate call to {@link
	 *  org.apache.lucene.document.Document#add(field)} will be made. Return null to have no content indexed for
	 *  the given Document. As a convenience the Lucene Document is passed in so additional custom fields can be
	 *  written if needed. The XML Document is passed in to allow access to other content as needed.
	 *
	 * @param  fieldContent  The existing content found at the configured XPath for a given custom field
	 * @param  newLuceneDoc  The new Lucene Document that will be inserted in the index for this record
	 * @param  xmlDoc        The dom4j Document that is being indexed in this context
	 * @return               The processed content that will be used by the indexer to index the given field, or
	 *      null to indicate none
	 */
	public String[] processFieldContent(String fieldContent, org.apache.lucene.document.Document newLuceneDoc, org.dom4j.Document xmlDoc);


	/**
	 *  Implement this method to receive the context configuration object, which may be used to fetch
	 *  configuration settings. For webapps, a instance of ServletContext is passeed in. This method is called
	 *  once after the class is first instantiated and before {@link #processFieldContent} first called.
	 *
	 * @param  contextConfig  The context configuration object, for example and instance of ServletContext
	 */
	public void contextConfigListener(Object contextConfig);
}

