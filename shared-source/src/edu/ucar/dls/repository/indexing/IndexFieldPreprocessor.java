/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
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

