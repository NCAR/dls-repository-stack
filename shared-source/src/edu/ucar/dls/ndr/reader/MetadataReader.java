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
package edu.ucar.dls.ndr.reader;

import edu.ucar.dls.ndr.apiproxy.NDRConstants;
import edu.ucar.dls.schemedit.SchemEditUtils;
import edu.ucar.dls.xml.Dom4jUtils;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import java.util.*;

/**
 *  Extension of NdrObjectReader for accessing properties, dataStreams, and
 *  relationships of NDR Metadata Objects.<p>
 *
 *  More Info:
 *  <li> Metadata overview: http://wiki.nsdl.org/index.php/Community:NDR/ObjectTypes#Metadata
 *
 *  <li> Metadata data model: http://wiki.nsdl.org/index.php/Community:NCore/Model/Objects/Metadata
 *
 *  <li> Metadata API requests: http://wiki.nsdl.org/index.php/Community:NDR/APIRequestsByObject#Metadata_requests
 *
 * @author    ostwald
 */
public class MetadataReader extends NdrObjectReader {

	private static boolean debug = true;
	private String pid = null;
	private final static NDRConstants.NDRObjectType MYTYPE = NDRConstants.NDRObjectType.METADATA;


	/**
	 *  Constructor for the MetadataReader object
	 *
	 * @param  handle         handle to MetadataObject in the NDR
	 * @exception  Exception  if handle does not correspond to existing Metatadata
	 *      object
	 */
	public MetadataReader(String handle) throws Exception {
		this(handle, null);
	}


	/**
	 *  Constructor for the MetadataReader object with nativeFormat specified
	 *
	 * @param  handle                  handle to MetadataObject in the NDR
	 * @param  nativeDataStreamFormat  nativeFormat of the collection to which this
	 *      metata belongs
	 * @exception  Exception           if handle does not correspond to existing
	 *      Metatadata object
	 */
	public MetadataReader(String handle, String nativeDataStreamFormat) throws Exception {
		super(handle, nativeDataStreamFormat);
		if (getObjectType() != MYTYPE)
			throw new Exception("Provided handle (" + handle +
				") does not refer to a metadata object (" + getObjectType() + ")");
	}


	/**
	 *  Constructor for the MetadataReader object
	 *
	 * @param  response       NOT YET DOCUMENTED
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public MetadataReader(Document response) throws Exception {
		this(response, null);
	}


	/**
	 *  Constructor for the MetadataReader object
	 *
	 * @param  response                NOT YET DOCUMENTED
	 * @param  nativeDataStreamFormat  NOT YET DOCUMENTED
	 * @exception  Exception           NOT YET DOCUMENTED
	 */
	public MetadataReader(Document response, String nativeDataStreamFormat) throws Exception {
		super(response, nativeDataStreamFormat);
		if (getObjectType() != MYTYPE)
			throw new Exception("Provided handle (" + handle +
				") does not refer to a metadata object (" + getObjectType() + ")");
	}


	/**
	 *  Gets the ncs:recordId property of the Metadata object, which is defined
	 *  only if this object is managed by the NCS.
	 *
	 * @return    The recordId value
	 */
	public String getRecordId() {
		return getProperty("ncs:recordId");
	}


	/**
	 *  Gets the nsdl:uniqueID property of the Metadata object.
	 *
	 * @return    The uniqueID value
	 */
	public String getUniqueID() {
		return this.getProperty("uniqueID");
	}


	/**
	 *  Gets the nsdl:itemId property of the Metadata object
	 *
	 * @return    The itemId value
	 */
	public String getItemId() {
		return this.getProperty("itemId");
	}


	/**
	 *  Gets the ncs:status property of the Metadata object, which is defined only
	 *  if this object is managed by the NCS
	 *
	 * @return    The status value
	 */
	public String getStatus() {
		return getProperty("ncs:status");
	}


	/**
	 *  Returns true if the ncs:isValid property of the Metadata object, which is
	 *  defined only if this object is managed by the NCS, is "true", and false
	 *  otherwise.
	 *
	 * @return    The isValid value
	 */
	public boolean getIsValid() {
		String valid = getProperty("ncs:isValid");
		return (valid != null && valid.equals("true"));
	}


	/**
	 *  Returns true if the ncs:status property of the Metadata object (which is
	 *  only present if this object is managed by the NCS) is NCS_FINAL_STATUS.
	 *
	 * @return    The isFinal value
	 */
	public boolean getIsFinal() {
		String status = getProperty("ncs:status");
		return (status != null && status.equals(NDRConstants.NCS_FINAL_STATUS));
	}


	/**
	 *  Returns true if this Metadata object is managed by the NCS.
	 *
	 * @return    The ncsMetadata value
	 */
	public boolean isNcsMetadata() {
		return getProperty("ncs:status") != null;
	}


	/**
	 *  Gets the native datastream as a dom4j.Document (using the nativeDataStream
	 *  attribute of this reader).
	 *
	 * @return                the native datastream
	 * @exception  Exception  if the native datastream could not be read or
	 *      processed
	 */
	public Document getItemRecord() throws Exception {
		Element root = this.getNativeDataStream(this.nativeDataStreamFormat);
		if (root == null)
			throw new Exception("itemRecord not found in " + this.getHandle());
		try {
			return DocumentHelper.createDocument(root);
		} catch (Throwable t) {
			throw new Exception("could not create itemRecord for " + this.getHandle() + ": " + t.getMessage());
		}
	}


	/**
	 *  Gets the "nsdl_dc" datastream as a dom4j.Document
	 *
	 * @return                The canonicalNsdlDcItemRecord value
	 * @exception  Exception  NOT YET DOCUMENTED
	 */
	public Document getCanonicalNsdlDcItemRecord() throws Exception {
		Element root = this.getCanonicalNsdlDcDataStream();
		if (root == null)
			throw new Exception("canonicalNsdlDcItemRecord not found in " + this.getHandle());
		try {
			return DocumentHelper.createDocument(root);
		} catch (Throwable t) {
			throw new Exception("could not create canonicalNsdlDcItemRecord for " + this.getHandle() + ": " + t.getMessage());
		}
	}


	protected static void pp(Node node) {
		prtln(Dom4jUtils.prettyPrint(node));
	}


	/**
	 *  Description of the Method
	 *
	 * @param  s  Description of the Parameter
	 */
	private static void prtln(String s) {
		if (debug) {
			SchemEditUtils.prtln(s, "MetadataReader");
			// SchemEditUtils.prtln(s, "");
		}
	}

}

