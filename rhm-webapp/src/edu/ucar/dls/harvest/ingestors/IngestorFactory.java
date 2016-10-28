package edu.ucar.dls.harvest.ingestors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.helpers.XMLReaderFactory;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.processors.harvest.DedupRecords;
import edu.ucar.dls.harvest.processors.record.*;
import edu.ucar.dls.harvest.processors.record.containers.Replacement;

/**
 *  Factory class that tries to find a config file for a specified native format.
 *  This config file contains the instantiation params for the ingestor that needs
 *  to be ran. To add more processors that can be defined in the config files. You
 *  must add the classes to the JAXB_REGISTERED_CLASSES as well as adding the header
 *  @XmlRootElement(name=<name_you_want>) to above the class
 */

public class IngestorFactory {

	// Registered classes for use within jaxb. You must add any processors here
	// that you want to be enabled to be used in the config files
	public static Class [] JAXB_REGISTERED_CLASSES = 
		{Ingestor.class, EncodedCharsTransform.class, RecordSchemaValidator.class,
		RemoveDuplicateElementsTransform.class, RemoveEmptyElementsTransform.class,
		RemoveLineBreaksFromTextTransform.class, URINormalizer.class, XSLTransform.class,
		PrettyPrint.class, RestrictElementCount.class, ReplaceStrings.class, 
		Replacement.class, SubscriptTransform.class, SuperscriptTransform.class,
		RequireElement.class, DataFormatTransform.class, AddElement.class,
		ASNConversion.class, DedupRecords.class, AddNSDLElementsFromASN.class,
		AddLARReadinessElement.class, AddComment.class, TransformViaGroupFiles.class,
		EducationLevelsVsAlignmentsReport.class};
	
	/**
	 *  Main method for creating the ingestor to be used for a native format
	 * @return ingestor intialized ingestor that should be used for harvesting a native format
	 */
	public static Ingestor createIngestor(String nativeFormat) throws HarvestException
	{
		URL possibleConfig = null;
		try {
			// Assume that the config file is just native format.xml
			possibleConfig = new URL(new URL(
					Config.INGESTOR_CONFIGS_URI), nativeFormat+".xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(
					JAXB_REGISTERED_CLASSES);
			
			XMLReader xmlreader = XMLReaderFactory.createXMLReader();
	       
	        xmlreader.setEntityResolver(new EntityResolver2() {
	            public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId)  throws SAXException, IOException {
	        		try {
	        			URI sourceURI = new URI(Config.INGESTOR_CONFIGS_URI).resolve(systemId);
	        			return new InputSource(sourceURI.toURL().openStream());
	        		} catch (MalformedURLException e) {
	        			throw new SAXException(e);
	        		} catch (IOException e) {
	        			throw new SAXException(e);
	        		} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
	        			throw new SAXException(e);
					}
	            }

				public InputSource resolveEntity(String publicId,
						String systemId) throws SAXException, IOException {
					return null;
				}

				public InputSource getExternalSubset(String arg0, String arg1)
						throws SAXException, IOException {
					return null;
				}
	        });

	        InputSource xml = new InputSource(possibleConfig.openStream());
	        Source source = new SAXSource(xmlreader, xml);
			
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Ingestor ingestor = (Ingestor) jaxbUnmarshaller.unmarshal(
					source);

			return ingestor;
		} 
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					String.format("Native format %s is not implemented in Ingestor Factory nor "+
							"found at path %s. Please create a new config called %s at path", 
							nativeFormat, possibleConfig, nativeFormat+".xml"),
					"IngestorFactory.createIngestor()");
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE, 
					String.format("Native format %s.xml config file is not valid at %s.", 
							nativeFormat, possibleConfig ),
					e);
		} 
		
	}
}
