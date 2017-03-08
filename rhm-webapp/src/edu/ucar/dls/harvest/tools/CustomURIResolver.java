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
