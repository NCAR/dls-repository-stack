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
package edu.ucar.dls.standards.asn;

// import javax.servlet.*;
import edu.ucar.dls.xml.Dom4jUtils;
import edu.ucar.dls.repository.indexing.IndexFieldPreprocessor;
import edu.ucar.dls.serviceclients.asn.CachingAsnResolutionServiceClient;
import org.dom4j.Document;
import java.util.regex.*;

/**
 *  Pre-process content for a custom field prior to indexing it. A single instance of the
 *  concrete class will be instantiated and reused when processing the configured field(s) for each {@link
 *  org.apache.lucene.document.Document} that is added to the index.
 *
 * @author    Jonathan Ostwald
 */
public class AsnStandardsTextFieldPreprocessor implements IndexFieldPreprocessor {

	/**
	 *  Implement this method to pre-process the configured custom XPath field content for each Lucene Document.
	 *  The existing content is passed in as a String and the resulting preprocessed content must be passed back
	 *  as an array of Strings. For each element in the array, a sparate call to {@link
	 *  org.apache.lucene.document.Document#add(field)} will be made. Return null to have no content indexed for
	 *  the given Document.
	 *
	 * @param  content  The existing content found at the configured XPath for a given custom field
	 * @return          The processed content that will be used by the indexer, or null to indicate none
	 */

	private static boolean debug = false;
	private static CachingAsnResolutionServiceClient asnResolver = null;


	/**
	 *  Sets the CachingAsnResolutionServiceClient to make it available to this plugin during the indexing
	 *  process.
	 *
	 * @param  client  The new CachingAsnResolutionServiceClient value
	 */
	public static void setAsnResolutionServiceClient(CachingAsnResolutionServiceClient client) {
		asnResolver = client;
	}


	/**
	 *  Gets the CachingAsnResolutionServiceClient for use during the indexing process.
	 *
	 * @return    The CachingAsnResolutionServiceClient
	 */
	public static CachingAsnResolutionServiceClient getAsnResolutionServiceClient() {
		return asnResolver;
	}

	/**
	 *  Generate ASN standards text for indexing
	 *
	 * @param    The standards content, which may be an ASN standard or may be other text.
	 * @param  newLuceneDoc  New Lucene doc being generated
	 * @param  xmlDoc        The XML Document
	 * @return               ASN standards text or null if n/a
	 */
	public String[] processFieldContent(String fieldContent, org.apache.lucene.document.Document newLuceneDoc, org.dom4j.Document xmlDoc) {
		// If a resolver has not been configured, do nothing:
		if (getAsnResolutionServiceClient() == null) {
			return null;
		}

		prtln("\nprocess TEXT FieldContent - " + fieldContent);
		
		// make sure this is an ASN ID
		String asnId = AsnConstants.normalizeAsnId (fieldContent);
		if (!AsnConstants.isNormalizedAsnId (asnId)) {
			prtln("Got a non-ASN value for fieldContent: " + fieldContent);
			return new String[]{};
		}

		String fieldValue = "";
		try {
			fieldValue = getAsnResolutionServiceClient().getAsnTextIndexFieldValue(asnId);
			// prtln("- indexFieldValue: " + fieldValue);
		} catch (Throwable t) {
			prtlnErr("AsnResoloutionServiceClient Error: " + t.getMessage());
			t.printStackTrace();
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

