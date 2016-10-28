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
package edu.ucar.dls.xml;

import edu.ucar.dls.oai.OAIUtils;

// Imported JAXP classes
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;

import org.apache.xerces.util.XMLGrammarPoolImpl;

// SAX import
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
//import org.xml.sax.Parser;
//import org.xml.sax.helpers.ParserFactory;
import org.xml.sax.helpers.ParserAdapter;

// Imported java.io classes
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.*;
import java.text.*;

/**
 *  Validate an individual XML file, a group of XML files in a directory, or an XML String. Each XML file or
 *  String should contain a DOCTYPE declaration or a schema reference, which is used to perform the
 *  validation. XMLValidator can cache schemas/grammars for efficiency when validating across multiple files.
 *  A human-readable report of XML errors and/or warnings is returned.
 *
 * @author    John Weatherley
 */
public class XMLValidator {

	// feature ids

	/**  Namespaces feature id (http://xml.org/sax/features/namespaces). */
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

	// Grammar pool for this instance:
	private Object grammarPool = null;

	// Global static grammar pool (disabled - use the non-static methods instead):
	private static boolean enableStaticSchemaCaching = false;
	private static Object staticGrammarPool = null;

	// default settings

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

	private int numXMLFiles = 0;
	private int numInvalidFiles = 0;
	private int numWarningFiles = 0;
	private int numMalformedFiles = 0;
	private int numUpToDateFiles = 0;

	private boolean useLogFile = false;
	private StringBuffer buff = new StringBuffer();


	// ----------------------------------------------------------------------------

	/**
	 *  The main program for the XMLValidator class
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(
	                        String[] args) {
		try {
			if (args.length == 0 || args.length > 2) {
				System.out.println("Usage: java ValidateXMLFiles file|direcory [modifiedSinceDate]");
				System.exit(1);
			}
			else {
				String input = args[0];
				String reportFilePath = null;

				Date modifiedSinceDate = null;
				if (args.length == 2)
					modifiedSinceDate = OAIUtils.getDateFromDatestamp(args[1]);

				// ********** Validate file(s) ************
				System.out.println("Starting validation of file(s)...");

				XMLValidator validator = new XMLValidator();

				boolean enableSchemaCaching = true;
				StringBuffer reportTxt = validator.validate(input, modifiedSinceDate, enableSchemaCaching);

				// Write report to file or system out
				if (reportTxt != null) {

					if (reportFilePath == null)
						System.out.print(reportTxt + "\n\n");
					else {
						// Write validation report:
						FileWriter writer = new FileWriter(reportFilePath, true);
						writer.write(reportTxt.toString());
						writer.write("\n\n");
						writer.close();
					}
				}
			}
		} catch (ParseException e) {
			prtlnErr("Error found in modifiedSinceDate. " + e.getMessage() +
				".\nParameter modifiedSinceDate must be a UTCdatetime of the form YYYY-MM-DDThh:mm:ssZ .");
			System.exit(1);
		} catch (Exception e) {
			prtlnErr("Error: " + e.getMessage());
			System.exit(1);
		}
	}


	// ----------------------------------------------------------------------------

	/**
	 *  Validate a single XML file or directory of XML files. Same as calling validate(String, null).
	 *
	 * @param  filePath                          A single XML file or directory contianing XML files to be
	 *      validated.
	 * @param  enableSchemaCaching               True to cache schemas/grammars between invocations
	 * @return                                   StringBuffer A StringBuffer containing the validation report.
	 * @exception  FileNotFoundException         If FileNotFoundException
	 * @exception  IOException                   If IOException
	 * @exception  ParserConfigurationException  If ParserConfigurationException
	 * @exception  SAXException                  If SAXException
	 * @exception  Exception                     If Exception
	 */
	public StringBuffer validate(String filePath, boolean enableSchemaCaching)
		 throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, Exception {
		return validate(filePath, null, null, null, enableSchemaCaching);
	}


	/**
	 *  Valdate an XML file at the specified path.
	 *
	 * @param  filePath                          Path to an XML file
	 * @param  modifiedSinceDate                 Only files newer than this date are validated, or null for all
	 * @param  enableSchemaCaching               True to cache schemas/grammars between invocations
	 * @return                                   A StringBuffer with error/warning messages
	 * @exception  FileNotFoundException         If FileNotFoundException
	 * @exception  IOException                   If IOException
	 * @exception  ParserConfigurationException  If ParserConfigurationException
	 * @exception  SAXException                  If SAXException
	 * @exception  Exception                     If Exception
	 */
	public StringBuffer validate(String filePath, Date modifiedSinceDate, boolean enableSchemaCaching)
		 throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, Exception {
		return validate(filePath, null, null, modifiedSinceDate, enableSchemaCaching);
	}


	// ----------------------------------------------------------------------------

	/**
	 *  Validate a single XML file or directory of XML files, processing only the xml files that match the ID
	 *  list provided. Same as calling validate(String, String [], null).
	 *
	 * @param  filePath                          A single XML file or directory contianing XML files to be
	 *      validated.
	 * @param  inputFileNames                    An array of all the ID names found in the directory indicated in
	 *      filePath to be validated. If null, then the list of files is obtained from the filePath directory.
	 * @param  enableSchemaCaching               True to cache schemas/grammars between invocations
	 * @return                                   StringBuffer A StringBuffer containing the validation report.
	 * @exception  FileNotFoundException         If FileNotFoundException
	 * @exception  IOException                   If IOException
	 * @exception  ParserConfigurationException  If ParserConfigurationException
	 * @exception  SAXException                  If SAXException
	 * @exception  Exception                     If Exception
	 */
	public StringBuffer validate(String filePath, String[] inputFileNames, boolean enableSchemaCaching)
		 throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, Exception {
		return validate(filePath, inputFileNames, null, null, enableSchemaCaching);
	}


	// ----------------------------------------------------------------------------

	/**
	 *  Validate a single XML file or directory of XML files. A report of what was found will be output to the
	 *  given file specified.
	 *
	 * @param  filePath             A single XML file or directory contianing XML files to be validated.
	 * @param  inputFileNames       An array of all the ID names found in the directory indicated in filePath to
	 *      be validated. If null, then the list of files is obtained from the filePath directory. the file list
	 *      is obtained
	 * @param  reportFilePath       The file where a validation report will be appended. If null then nothing is
	 *      written.
	 * @param  modifiedSinceDate    Only files newer than this date are validated, or null for all
	 * @param  enableSchemaCaching  True to cache schemas/grammars between invocations
	 * @return                      StringBuffer A StringBuffer containing the validation report.
	 * @exception  Exception        If Exception
	 */
	public StringBuffer validate(
	                             String filePath,
	                             String[] inputFileNames,
	                             String reportFilePath,
	                             Date modifiedSinceDate,
	                             boolean enableSchemaCaching)
		 throws Exception {

		buff.append(new java.util.Date().toString() + " Starting validation\n");

		//prtln("validate: filePath is: " + filePath);

		//final JFileChooser chooser = new JFileChooser();
		//File curDir = chooser.getCurrentDirectory();
		//prtln("System.getProperty('user.dir'): " + System.getProperty("user.dir"));
		//prtln("System.getProperty('user.home'): " + System.getProperty("user.home"));

		/*
		 *  Enumeration e = System.getProperties().propertyNames();
		 *  while (e.hasMoreElements()) {
		 *  String key = (String)e.nextElement();
		 *  System.out.println(key +"\t"+ System.getProperty(key));
		 *  }
		 */
		Date start = new Date();

		File dir = new File(filePath);

		//prtln("validate: dir.getAbsolutePath() is: " + dir.getAbsolutePath());

		// May include a 2nd argument for the log file.
		useLogFile = (reportFilePath != null);

		if (dir.isFile()) {
			// Just validating one file.

			buff.append(" ---- Input file: ----\n");
			buff.append(dir.getAbsolutePath() + "\n");

			if (modifiedSinceDate != null && (dir.lastModified() < modifiedSinceDate.getTime())) {
				buff.append("File is up-to-date - no validation was performed\n");
				numUpToDateFiles++;
			}
			else
				parseAndValidate(null, filePath, enableSchemaCaching);
		}
		else if (dir.isDirectory()) {
			// Validating all .xml files in a directory.

			String[] files = null;
			String dirName = dir.toString();

			// If a list of input files was provided, use it. Otherwise, get list of
			// files from the input directory.
			if (inputFileNames == null)
				files = dir.list(new XMLFileFilter());
			// .xml files only
			// Process only the files in the directory that match the inputFileNames provided
			else
				files = inputFileNames;

			buff.append(" ---- Input files directory: ----\n");
			buff.append(dir.getAbsolutePath() + "\n");
			buff.append(" ---- List of any non-valid files is shown below: ----\n");
			for (int i = 0; i < files.length; i++) {
				if (modifiedSinceDate != null && (new File(dirName, files[i]).lastModified() < modifiedSinceDate.getTime()))
					numUpToDateFiles++;
				else
					parseAndValidate(dirName, files[i], enableSchemaCaching);
			}
		}
		else {
			// File path argument is no good!

			throw new Exception("Input file path \"" + filePath + "\" not found");
		}

		int numValidFiles = numXMLFiles - numInvalidFiles - numMalformedFiles;

		// Provide user with a summary.
		buff.append(" ---- Validation summary: ----\n");

		Date end = new Date();

		long ms = (end.getTime() - start.getTime()) % 1000;
		long sec1 = (long) Math.floor((end.getTime() - start.getTime()) / 1000);
		long min = (long) Math.floor(sec1 / 60);
		long sec = sec1 - 60 * min;
		long tms = end.getTime() - start.getTime();

		String timeMsg = min + " min " + sec + " sec and " + ms + " ms.\n";

		if (numXMLFiles == 1)
			buff.append(numXMLFiles + " total file was processed in " + timeMsg);
		else
			buff.append(numXMLFiles + " total files were processed in " + timeMsg);

		if (numValidFiles == 1)
			buff.append(numValidFiles + " file was valid.\n");
		else
			buff.append(numValidFiles + " files were valid.\n");

		/*
		 *  if (numWarningFiles == 1)
		 *  buff.append( numWarningFiles + " file has a warning.\n");
		 *  else
		 *  buff.append(numWarningFiles + " files have warnings.\n");
		 */
		if (numInvalidFiles == 1)
			buff.append(numInvalidFiles + " file was not valid.\n");
		else
			buff.append(numInvalidFiles + " files were not valid.\n");

		if (numMalformedFiles > 1)
			buff.append(numMalformedFiles + " files were not well-formed.\n");
		else if (numMalformedFiles == 1)
			buff.append(numMalformedFiles + " file was not well-formed.\n");

		if (numUpToDateFiles == 1)
			buff.append(numUpToDateFiles + " file was up-to-date and was not validated.\n");
		if (numUpToDateFiles > 1)
			buff.append(numUpToDateFiles + " files were up-to-date and was not validated.\n");

		buff.append(new java.util.Date().toString() + " Validation completed\n");

		// Write output file if appropriate:
		if (reportFilePath != null && !reportFilePath.equals("")) {
			FileWriter writer = new FileWriter(reportFilePath, true);
			writer.write(buff.toString());
			writer.write("\n\n");
			writer.close();
		}

		return buff;
	}


	/**
	 *  Validates an XML String to the external DTD or schema that is present in the String. The String must
	 *  contain a DTD or schema declaration within. Returns null if the String is valid or an appropriate error
	 *  message if not.
	 *
	 * @param  s  An XML String, which must include a DTD or schema declaration.
	 * @return    Null if valid, else a String containing an appropriate error message.
	 */
	public final static String validateString(String s) {
		return validateString(s, false);
	}


	/**
	 *  Validates an XML String to the external DTD or schema that is present in the String. The String must
	 *  contain a DTD or schema declaration within. Returns null if the String is valid or an appropriate error
	 *  message if not. The output message may contain only warnings if showWarnings is set to true.
	 *
	 * @param  s             An XML String, which must include a DTD or schema declaration.
	 * @param  showWarnings  Set to true to check for warnings as well as errors.
	 * @return               Null if valid, else a String containing an appropriate error and/or warning message.
	 */
	public final static String validateString(String s, boolean showWarnings) {
		StringBuffer messages = new StringBuffer();
		InputSource input = new InputSource(new StringReader(s));
		if (doValidateStatic(input, s, messages, showWarnings))
			return null;
		else
			return messages.toString();
	}


	/**
	 *  Validates an XML document at the given URI. URI's must be of the form:
	 *  <ul>
	 *    <li> <code>file:///usr/local/nonesuch.xml</code> - an XML document on the local system.
	 *    <li> <code>http://www.nonesuch.com/nonsuch.xml</code> - an XML document on the Internet.
	 *  </ul>
	 *
	 *
	 * @param  URI  A URI path to an XML document.
	 * @return      Null if valid, else a String containing an appropriate error message.
	 */
	public final static String validateUri(String URI) {
		return validateUri(URI, false);
	}


	/**
	 *  Validates an XML document at the given URI. URI's must be of the form:
	 *  <ul>
	 *    <li> <code>file:///usr/local/nonesuch.xml</code> - an XML document on the local system.
	 *    <li> <code>http:///www.nonesuch.com/nonsuch.xml</code> - an XML document on the Internet.
	 *  </ul>
	 *
	 *
	 * @param  URI           A URI path to an XML document.
	 * @param  showWarnings  Set to true to check for warnings as well as errors.
	 * @return               Null if valid, else a String containing an appropriate error message.
	 */
	public final static String validateUri(String URI, boolean showWarnings) {
		try {
			StringBuffer messages = new StringBuffer();
			InputSource input = new InputSource(URI);
			if (doValidateStatic(input, URI, messages, showWarnings))
				return null;
			else
				return messages.toString();
		} catch (Throwable e) {
			return "Unable to validate: " + e;
		}
	}



	/**
	 *  Validates an XML File to the external DTD or schema that is present in the File. The File must contain a
	 *  DTD or schema declaration within. Returns null if the File is valid or an appropriate error message if
	 *  not.
	 *
	 * @param  f  An XML File, which must include a DTD or schema declaration.
	 * @return    Null if valid, else a String containing an appropriate error message.
	 */
	public final static String validateFile(File f) {
		return validateFile(f, false);
	}


	/**
	 *  Validates an XML File to the external DTD or schema that is present in the File. The File must contain a
	 *  DTD or schema declaration within. Returns null if the File is valid or an appropriate error message if
	 *  not. The output message may contain only warnings if showWarnings is set to true.
	 *
	 * @param  f             An XML File, which must include a DTD or schema declaration.
	 * @param  showWarnings  Set to true to check for warnings as well as errors.
	 * @return               Null if valid, else a String containing an appropriate error and/or warning message.
	 */
	public final static String validateFile(File f, boolean showWarnings) {
		try {
			StringBuffer messages = new StringBuffer();
			InputSource input = new InputSource(new FileInputStream(f));
			if (doValidateStatic(input, f.toString(), messages, showWarnings))
				return null;
			else
				return messages.toString();
		} catch (Throwable e) {
			return "Unable to validate: " + e;
		}
	}


	/**
	 *  Perform the XML validation.
	 *
	 * @param  input         Input
	 * @param  source        Source
	 * @param  messages      Buffer to hold the error message
	 * @param  showWarnings  True to include warning messages
	 * @return               True if the document is valide, false otherwise
	 */
	private final static boolean doValidateStatic(InputSource input, String source, StringBuffer messages, boolean showWarnings) {
		boolean isValid = true;
		try {
			// Set systemID so parser can find the dtd with a relative URL in the source document.
			input.setSystemId(source);

			boolean namespaces = DEFAULT_NAMESPACES;
			boolean namespacePrefixes = DEFAULT_NAMESPACE_PREFIXES;
			boolean validation = DEFAULT_VALIDATION;
			boolean schemaValidation = DEFAULT_SCHEMA_VALIDATION;
			boolean schemaFullChecking = DEFAULT_SCHEMA_FULL_CHECKING;
			boolean dynamicValidation = DEFAULT_DYNAMIC_VALIDATION;
			boolean memoryUsage = DEFAULT_MEMORY_USAGE;
			boolean tagginess = DEFAULT_TAGGINESS;

			SAXParserFactory spfact = SAXParserFactory.newInstance();
			SAXParser parser = spfact.newSAXParser();
			XMLReader xmlReader = parser.getXMLReader();

			StringBuffer errorBuff = new StringBuffer();

			StringBuffer warningBuff = new StringBuffer();

			// Turn on schema/grammar caching (see http://www.ibm.com/developerworks/xml/library/x-perfap3/index.html for details):
			if (enableStaticSchemaCaching) {
				try {
					if (staticGrammarPool == null) {
						Class poolClass = Class.forName("org.apache.xerces.util.XMLGrammarPoolImpl");
						staticGrammarPool = poolClass.newInstance();
					}
					parser.setProperty("http://apache.org/xml/properties/internal/grammar-pool", staticGrammarPool);
				} catch (Throwable e) {
					//prtlnErr("Not able to cache schemas/grammars while validating: " + e);
				}
			}

			//reader.setFeature("http://xml.org/sax/features/validation", true);
			//reader.setFeature("http://apache.org/xml/features/validation/schema", true);
			//reader.setFeature("http://apache.org/xml/features/validation/schema-full-checking",true);



			// set parser features
			try {
				xmlReader.setFeature(NAMESPACES_FEATURE_ID, namespaces);
			} catch (SAXException e) {
				System.err.println("warning: Parser does not support feature (" + NAMESPACES_FEATURE_ID + ")");
			}
			try {
				xmlReader.setFeature(NAMESPACE_PREFIXES_FEATURE_ID, namespacePrefixes);
			} catch (SAXException e) {
				System.err.println("warning: Parser does not support feature (" + NAMESPACE_PREFIXES_FEATURE_ID + ")");
			}
			try {
				xmlReader.setFeature(VALIDATION_FEATURE_ID, validation);
			} catch (SAXException e) {
				System.err.println("warning: Parser does not support feature (" + VALIDATION_FEATURE_ID + ")");
			}
			try {
				xmlReader.setFeature(SCHEMA_VALIDATION_FEATURE_ID, schemaValidation);
			} catch (SAXNotRecognizedException e) {
				// ignore
			} catch (SAXNotSupportedException e) {
				System.err.println("warning: Parser does not support feature (" + SCHEMA_VALIDATION_FEATURE_ID + ")");
			}
			try {
				xmlReader.setFeature(SCHEMA_FULL_CHECKING_FEATURE_ID, schemaFullChecking);
			} catch (SAXNotRecognizedException e) {
				// ignore
			} catch (SAXNotSupportedException e) {
				System.err.println("warning: Parser does not support feature (" + SCHEMA_FULL_CHECKING_FEATURE_ID + ")");
			}
			try {
				xmlReader.setFeature(DYNAMIC_VALIDATION_FEATURE_ID, dynamicValidation);
			} catch (SAXNotRecognizedException e) {
				// ignore
			} catch (SAXNotSupportedException e) {
				System.err.println("warning: Parser does not support feature (" + DYNAMIC_VALIDATION_FEATURE_ID + ")");
			}

			SimpleErrorHandler handler = new SimpleErrorHandler(errorBuff, warningBuff);

			// Do the parse and capture validation errors in the handler
			parser.parse(input, handler);

			if (showWarnings && handler.hasWarnings()) {
				// warnings

				messages.append("WARNING: ");
				messages.append(warningBuff.toString());
			}
			if (handler.hasErrors()) {
				// not valid

				messages.append("NOT VALID: ");
				messages.append(errorBuff.toString());
				isValid = false;
			}
		} catch (Exception e) {
			// Serious problem!

			messages.append("NOT WELL-FORMED: " + e.getMessage());
			isValid = false;
		}
		return isValid;
	}


	// ----------------------------------------------------------------------------

	/**
	 *  Parse each XML file and perform the validation.
	 *
	 * @param  dir                  Base directory
	 * @param  filename             File name
	 * @param  enableSchemaCaching  True to cache schemas/grammars between invocations
	 */
	private void parseAndValidate(String dir, String filename, boolean enableSchemaCaching) {
		try {
			//prtln("parse: dir = " + dir + " filename = " + filename );

			File f = new File(dir, filename);

			//prtln("parse: f.getAbsolutePath(): " + f.getAbsolutePath());

			StringBuffer errorBuff = new StringBuffer();

			//prtln("parse: f.getAbsolutePath(): " + f.getAbsolutePath());

			StringBuffer warningBuff = new StringBuffer();
			InputSource input = new InputSource(new FileInputStream(f));

			// Set systemID so parser can find the dtd with a relative URL in the source document.
			input.setSystemId(f.toString());

			boolean namespaces = DEFAULT_NAMESPACES;
			boolean namespacePrefixes = DEFAULT_NAMESPACE_PREFIXES;
			boolean validation = DEFAULT_VALIDATION;
			boolean schemaValidation = DEFAULT_SCHEMA_VALIDATION;
			boolean schemaFullChecking = DEFAULT_SCHEMA_FULL_CHECKING;
			boolean dynamicValidation = DEFAULT_DYNAMIC_VALIDATION;
			boolean memoryUsage = DEFAULT_MEMORY_USAGE;
			boolean tagginess = DEFAULT_TAGGINESS;

			SAXParserFactory spfact = SAXParserFactory.newInstance();
			SAXParser parser = spfact.newSAXParser();
			XMLReader reader = parser.getXMLReader();

			// Turn on schema/grammar caching (see http://www.ibm.com/developerworks/xml/library/x-perfap3/index.html for details):
			if (enableSchemaCaching) {
				try {
					if (grammarPool == null) {
						Class poolClass = Class.forName("org.apache.xerces.util.XMLGrammarPoolImpl");
						grammarPool = poolClass.newInstance();
					}
					parser.setProperty("http://apache.org/xml/properties/internal/grammar-pool", grammarPool);
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

				buff.append("NOT VALID: " + filename + "\n");
				buff.append(errorBuff.toString());
				//if(handler.warning)
				//	buff.append(warningBuff.toString());
				numInvalidFiles++;
			}
			/*
			 *  if (handler.warning && !handler.containsDTD) // warning
			 *  {
			 *  buff.append ("WARNING: " + filename + "\n");
			 *  buff.append(warningBuff.toString());
			 *  numWarningFiles++;
			 *  }
			 */
		} catch (Exception e) {
			// Serious problem!

			buff.append("NOT WELL-FORMED: " + filename + "\n " + e.getMessage() + "\n");
			numMalformedFiles++;
		} finally {
			numXMLFiles++;
		}
	}


	// ----------------------------------------------------------------------------

	// Only interested in parsing .xml files.
	/**
	 *  DESCRIPTION
	 *
	 * @author    jweather
	 */
	class XMLFileFilter implements FilenameFilter {
		/**
		 *  DESCRIPTION
		 *
		 * @param  dir       DESCRIPTION
		 * @param  fileName  DESCRIPTION
		 * @return           DESCRIPTION
		 */
		public boolean accept(File dir, String fileName) {
			return fileName.toLowerCase().endsWith(".xml") && new File(dir.toString(), fileName).isFile();
		}
	}


	// ----------------------------------------------------------------------------


	/**
	 *  DESCRIPTION
	 *
	 * @param  s  DESCRIPTION
	 */
	private static void prtln(String s) {
		System.out.println(s);
	}


	/**
	 *  Print an error message to systems.err.
	 *
	 * @param  s  The message
	 */
	private static void prtlnErr(String s) {
		System.err.println(s);
	}

}


