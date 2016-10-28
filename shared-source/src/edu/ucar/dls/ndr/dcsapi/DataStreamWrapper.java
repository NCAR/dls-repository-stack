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

package edu.ucar.dls.ndr.dcsapi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.Document;

/**
 * @author Jonathan Ostwald
 */
public class DataStreamWrapper {
	private HashMap dataStreams    = null;

	/**
	 * TODO - make the internal representation nodes, not Strings
	 * 
	 * @param _xml
	 */
	public DataStreamWrapper() {
		
		this.dataStreams = new HashMap ();
	}
	
	public Set getDataStreams () {
		return this.dataStreams.entrySet();
	}
	
	public Iterator getFormats () {
		return this.dataStreams.keySet().iterator();
	}
	
	public String getMeta (String _format) {
		Element meta = DocumentHelper.createElement ("meta");
		meta.add(this.getDataStream( _format).createCopy());
		return meta.asXML();
	}
	
	/**
	 * 
	 * @param _metadataDocument
	 * @throws Exception
	 */
	public void setDataStream( String _format, Element _metadataDocument ) throws Exception {	
		if (_format == null || _format.trim().length() == 0)
			throw new Exception ("setDataStream got empty format");
		if (_metadataDocument == null)
			this.dataStreams.put (_format, null);
		else
			this.dataStreams.put (_format, _metadataDocument.createCopy());
	}
 
	public void setDataStream( String _format, String _metadataXML ) throws Exception {	
		if (_metadataXML == null)
			this.dataStreams.put (_format, null);
		else {
			Document doc = DocumentHelper.parseText(_metadataXML);
			this.setDataStream(_format, doc.getRootElement());
		}
	}
	
	
	/**
	 * Get the metadata for this wrapper object.
	 * 
	 * @return - the Metadata element for this wrapper
	 */
	public Element getDataStream( String _format) {
		return (Element)this.dataStreams.get(_format);
	}
}
