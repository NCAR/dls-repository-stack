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
package edu.ucar.dls.standards.asn;

// import javax.servlet.*;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.repository.indexing.IndexFieldPreprocessor;
import edu.ucar.dls.serviceclients.asn.CachingAsnResolutionServiceClient;
import org.dom4j.Document;
import java.util.regex.*;

/**
 *  Interface used to pre-process content for a custom field prior to indexing it. A single instance of the
 *  concrete class will be instantiated and reused when processing the configured field(s) for each {@link
 *  org.apache.lucene.document.Document} that is added to the index.
 *
 * @author    Jonathan Ostwald
 */
public class AsnStandardsIdFieldPreprocessor implements IndexFieldPreprocessor {

	/**
	 *  Implement this method to pre-process the configured custom XPath field content for each Lucene Document.
	 *  The existing content is passed in as a String and the resulting preprocessed content must be passed back
	 *  as an array of Strings. For each element in the array, a saparate call to {@link
	 *  org.apache.lucene.document.Document#add(field)} will be made. Return null to have no content indexed for
	 *  the given Document.
	 *
	 * @param  content  The existing content found at the configured XPath for a given custom field
	 * @return          The processed content that will be used by the indexer, or null to indicate none
	 */

	private static boolean debug = false;
	private static CachingAsnResolutionServiceClient asnResolver = null;

	/**
	 *  Sets the AsnResolutionServiceClient to make it available to this plugin during the indexing process.
	 *
	 * @param  client  The new asnResolutionServiceClient value
	 */
	public static void setAsnResolutionServiceClient(CachingAsnResolutionServiceClient client) {
		asnResolver = client;
	}


	/**
	 *  Gets the AsnResolutionServiceClient for use during the indexing process.
	 *
	 * @return    The AsnResolutionServiceClient
	 */
	public static CachingAsnResolutionServiceClient getAsnResolutionServiceClient() {
		return asnResolver;
	}

	/**
	 *  Generate ASN standards ID hierarchies for indexing. String is of the form grandparentID:parentID:childID, for
	 *  example StateStandardBodyID:scienceID:chemistryID:conceptID.
	 *
	 * @param    The standards content, which may be an ASN standard or may be other text.
	 * @param  newLuceneDoc  New Lucene doc being generated
	 * @param  xmlDoc        The XML Document
	 * @return               ASN standards text or null if n/a
	 */
	public String[] processFieldContent(String fieldContent, org.apache.lucene.document.Document newLuceneDoc, org.dom4j.Document xmlDoc) {

		// If a resolver has not been configured, do nothing:
		if (getAsnResolutionServiceClient() == null)
			return null;

		prtln("\nprocess ID FieldContent: " + fieldContent);
		
		// make sure this is an ASN ID
		String asnId = AsnConstants.normalizeAsnId (fieldContent);
		
		if (!AsnConstants.isNormalizedAsnId (asnId)) {
			prtln("Got a non-ASN value for fieldContent: " + fieldContent);
			return new String[]{};
		}

		String fieldValue = "";
		try {
			fieldValue = getAsnResolutionServiceClient().getAsnIdIndexFieldValue(asnId);
			// prtln("- indexFieldValue: " + fieldValue);
		} catch (Throwable t) {
			prtlnErr("AsnResoloutionServiceClient Error: " + t.getMessage());
			return new String[]{};
		}
		return new String[]{fieldValue};
	}


	/**
	 *  Does nothing.
	 *
	 * @param  contextConfig  The context configuration object, which is an instance of ServletContext when
	 *      running in a webapp.
	 */
	public void contextConfigListener(Object contextConfig) { }


	/**
	 *  NOT YET DOCUMENTED
	 *
	 * @param  s  NOT YET DOCUMENTED
	 */
	private static void prtln(String s) {
		if (debug) {
			System.out.println(s);
		}
	}


	private static void prtlnErr(String s) {
		System.out.println(s);
	}

}

