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
package edu.ucar.dls.harvest.tools;

/**
 * This class is based off the one from DLESE tools except that it uses pools
 * for caching the .xsd and can validate Strings.
 */

//Imported JAXP classes
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;


//SAX import

import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import java.io.StringReader;

public class XMLDocumentValidator{
	// Grammar pool for this instance:
	private Object grammarPool = null;
	public StringBuffer outputBuffer = null;
	
	// returned result values
	public static final int VALID_RESULT = 1;
	public static final int VALID_W_WARNINGS_RESULT = 2;
	public static final int NOT_VALID_RESULT = 3;
	public static final int NOT_WELL_FORMED_RESULT = 4;
	
	// default settings
	
	protected final static String NAMESPACES_FEATURE_ID = "http://xml.org/sax/features/namespaces";

	/**  Namespace prefixes feature id (http://xml.org/sax/features/namespace-prefixes). */
	protected final static String NAMESPACE_PREFIXES_FEATURE_ID = "http://xml.org/sax/features/namespace-prefixes";

	/**  Validation feature id (http://xml.org/sax/features/validation). */
	protected final static String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";

	/**  Schema validation feature id (http://apache.org/xml/features/validation/schema). */
	protected final static String SCHEMA_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/schema";

	/**  Schema full checking feature id (http://apache.org/xml/features/validation/schema-full-checking). */
	protected final static String SCHEMA_FULL_CHECKING_FEATURE_ID = "http://apache.org/xml/features/validation/schema-full-checking";

	/**  Dynamic validation feature id (http://apache.org/xml/features/validation/dynamic). */
	protected final static String DYNAMIC_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/dynamic";
	
	/**  Default parser name. */
	protected final static String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";

	/**  Default namespaces support (true). */
	protected final static boolean DEFAULT_NAMESPACES = true;

	/**  Default namespace prefixes (false). */
	protected final static boolean DEFAULT_NAMESPACE_PREFIXES = false;

	/**  Default validation support (false). */
	protected final static boolean DEFAULT_VALIDATION = true;

	/**  Default Schema validation support (false). */
	protected final static boolean DEFAULT_SCHEMA_VALIDATION = true;

	/**  Default Schema full checking support (false). */
	protected final static boolean DEFAULT_SCHEMA_FULL_CHECKING = true;

	/**  Default dynamic validation support (false). */
	protected final static boolean DEFAULT_DYNAMIC_VALIDATION = false;

	/**  Default memory usage report (false). */
	protected final static boolean DEFAULT_MEMORY_USAGE = false;

	/**  Default "tagginess" report (false). */
	protected final static boolean DEFAULT_TAGGINESS = false;

	
	public int validateString(String s, boolean showWarnings, 
			boolean enableSchemaCaching) throws DocumentException {
		
		int index = 0;
		
		while(index<10)
		{
			try{
				InputSource input = new InputSource(new StringReader(s));
				return this.parseAndValidate(input, showWarnings, enableSchemaCaching);
			}
			catch(java.lang.StackOverflowError e)
			{
				Document document = Dom4jUtils.getXmlDocument(s);
				Element rootElement = document.getRootElement();
				Element longestElement = this.findLongestTextElement(rootElement);
				
				int longestElementDesiredLength = longestElement.getText().length() / 2;
				String newText = longestElement.getText().substring(0, longestElementDesiredLength);
				longestElement.setText(newText);
				s = rootElement.asXML();
				index = index + 1;
			}
		}
		return NOT_VALID_RESULT;
	}
	
	private Element findLongestTextElement(Element element)
	{
		Element longestElement = null;
		Element elementToCompare = null;
		
		for(Object childElementObj: element.elements())
		{
			Element childElement = (Element)childElementObj;
			
			if(childElement.elements().size()>0)
			{
				elementToCompare = findLongestTextElement(childElement);
			}
			else
			{
				elementToCompare = childElement;
			}
			// compare the text
			if(longestElement==null || elementToCompare.getText().length() > 
											longestElement.getText().length())
			{
				longestElement = elementToCompare;
			}
		}
		return longestElement;
	}
	
	protected int parseAndValidate(InputSource input, boolean showWarnings, 
			boolean enableSchemaCaching) {
		try {
			//prtln("parse: dir = " + dir + " filename = " + filename );


			//prtln("parse: f.getAbsolutePath(): " + f.getAbsolutePath());

			StringBuffer errorBuff = new StringBuffer();

			//prtln("parse: f.getAbsolutePath(): " + f.getAbsolutePath());

			StringBuffer warningBuff = new StringBuffer();
			
			outputBuffer = new StringBuffer();
			
			boolean namespaces = DEFAULT_NAMESPACES;
			boolean namespacePrefixes = DEFAULT_NAMESPACE_PREFIXES;
			boolean validation = DEFAULT_VALIDATION;
			boolean schemaValidation = DEFAULT_SCHEMA_VALIDATION;
			boolean schemaFullChecking = DEFAULT_SCHEMA_FULL_CHECKING;
			boolean dynamicValidation = DEFAULT_DYNAMIC_VALIDATION;

			SAXParserFactory spfact = SAXParserFactory.newInstance();
			SAXParser parser = spfact.newSAXParser();
			XMLReader reader = parser.getXMLReader();

			// Turn on schema/grammar caching (see http://www.ibm.com/developerworks/xml/library/x-perfap3/index.html for details):
			if (enableSchemaCaching) {
				try {
					if (this.grammarPool == null) {
						Class poolClass = Class.forName("org.apache.xerces.util.XMLGrammarPoolImpl");
						this.grammarPool = poolClass.newInstance();
					}
					parser.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.grammarPool);
				} catch (Throwable e) {
					//prtlnErr("Not able to cache schemas/grammars while validating: " + e);
				}
			}

			//reader.setFeature("http://xml.org/sax/features/validation", true);
			//reader.setFeature("http://apache.org/xml/features/validation/schema", true);
			//reader.setFeature("http://apache.org/xml/features/validation/schema-full-checking",true);

			// set parser features
			try {
				reader.setFeature(NAMESPACES_FEATURE_ID, namespaces);
			} catch (SAXException e) {
				System.err.println("warning: Parser does not support feature (" + NAMESPACES_FEATURE_ID + ")");
			}
			try {
				reader.setFeature(NAMESPACE_PREFIXES_FEATURE_ID, namespacePrefixes);
			} catch (SAXException e) {
				System.err.println("warning: Parser does not support feature (" + NAMESPACE_PREFIXES_FEATURE_ID + ")");
			}
			try {
				reader.setFeature(VALIDATION_FEATURE_ID, validation);
			} catch (SAXException e) {
				System.err.println("warning: Parser does not support feature (" + VALIDATION_FEATURE_ID + ")");
			}
			try {
				reader.setFeature(SCHEMA_VALIDATION_FEATURE_ID, schemaValidation);
			} catch (SAXNotRecognizedException e) {
				// ignore
			} catch (SAXNotSupportedException e) {
				System.err.println("warning: Parser does not support feature (" + SCHEMA_VALIDATION_FEATURE_ID + ")");
			}
			try {
				reader.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, schemaFullChecking);
			} catch (SAXNotRecognizedException e) {
				// ignore
			} catch (SAXNotSupportedException e) {
				System.err.println("warning: Parser does not support feature (" + SCHEMA_FULL_CHECKING_FEATURE_ID + ")");
			}
			try {
				reader.setFeature(DYNAMIC_VALIDATION_FEATURE_ID, dynamicValidation);
			} catch (SAXNotRecognizedException e) {
				// ignore
			} catch (SAXNotSupportedException e) {
				System.err.println("warning: Parser does not support feature (" + DYNAMIC_VALIDATION_FEATURE_ID + ")");
			}

			SimpleErrorHandler handler = new SimpleErrorHandler(errorBuff, warningBuff);

			// Do the parse and capture validation errors in the handler
			parser.parse(input, handler);

			if (handler.hasErrors()) {
				// not valid
				outputBuffer.append("NOT VALID: \n");
				outputBuffer.append(errorBuff.toString());
				return NOT_VALID_RESULT;
			}
			
			if (handler.hasWarnings())
			{
				outputBuffer.append("Warning: \n");
				outputBuffer.append(warningBuff.toString());
				return VALID_W_WARNINGS_RESULT;
				
			}
			return VALID_RESULT;
		} catch (Exception e) {
			// Serious problem!
			outputBuffer.append("NOT WELL-FORMED: \n " + e.getMessage() + "\n");
			return NOT_WELL_FORMED_RESULT;
		} 
		
	}

}
