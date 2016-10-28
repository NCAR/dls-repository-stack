package edu.ucar.dls.harvest.tools;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

/**
 * URI resolover that is used during XSL Transformations in order to 
 * find where the included templates are. The default is resolved at 
 * the same bath as the base. This can be case with urls and files
 *
 */
public class CustomURIResolver implements URIResolver{
	private URI baseXSLURI = null;
	
	public CustomURIResolver(URI xslURI) {
		this.baseXSLURI = xslURI;
	}
	
	/**
	 * Resolve the URI by using the base uri to resolve it
	 */
	public Source resolve(String href, String base) throws TransformerException {
		URI sourceURI = this.baseXSLURI.resolve(href);
		try {
			return new StreamSource(sourceURI.toURL().openStream());
		} catch (MalformedURLException e) {
			throw new TransformerException(e);
		} catch (IOException e) {
			throw new TransformerException(e);
		}
	}
}