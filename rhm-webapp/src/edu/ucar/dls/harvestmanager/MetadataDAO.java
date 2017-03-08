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
package edu.ucar.dls.harvestmanager;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import org.dlese.dpc.util.Utils;
import org.dlese.dpc.xml.Dom4jUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import edu.ucar.dls.harvest.Config;

public class MetadataDAO {
	private String metadatahandle=null;
	private String partnerid=null;
	private String nativeformat=null;
	private String targetformat=null;
	private String target_xml=null;
	private String resourceUrl = null;
	private String resourcehandle = null;
	private String sessionid = null;
	private String collectionName = null;
	private Timestamp created_date = null;

	private String setspec = null;
	private Element targetXMLRootElement = null;
	public MetadataDAO(ResultSet rs, Map<String, String>collectionNameMap, boolean containsResource)
	{
		try {
			this.metadatahandle = rs.getString("metadatahandle");
		
			this.partnerid = rs.getString("partnerid");
			this.nativeformat = rs.getString("nativeformat");
			this.targetformat = rs.getString("targetformat");
			this.setspec = rs.getString("setspec");
            this.created_date = rs.getTimestamp("created_date");
			if(this.setspec!=null && collectionNameMap.containsKey(this.setspec))
				this.collectionName = collectionNameMap.get(this.setspec);
			
			this.sessionid = rs.getString("sessionid");
			if(containsResource)
			{
				this.resourceUrl = rs.getString("url");
				this.resourcehandle = rs.getString("resourcehandle");
			}
			byte[] blob = rs.getBytes("target_xml");
			if(blob!=null)
				try {
					this.target_xml = new String(blob, Config.ENCODING);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	public String getMetadatahandle() {
		return metadatahandle;
	}

	public String getPartnerid() {
		return partnerid;
	}

    public String getCreateddate() {
        try {
            return Utils.convertDateToString(created_date, "EEE, MMM d, yyyy h:mm:ss a zzz");
        } catch (Throwable t) {
            return created_date.toString();
        }
    }

	public String getNativeformat() {
		return nativeformat;
	}

	public String getTargetformat() {
		return targetformat;
	}

	public String getTarget_xml() {
		return target_xml;
	}
	public String getResourceUrl() {
		return resourceUrl;
	}
	public String getResourcehandle() {
		return resourcehandle;
	}
	
	public String getSessionid() {
		return sessionid;
	}
	public String getSetspec() {
		return setspec;
	}
	
	public String getTitle()
	{
		Element rootElement = this.getTargetXMLDomRootElement();
		if(rootElement != null)
		{
			if(this.targetformat.equals("nsdl_dc"))
			{
				String value = rootElement.valueOf("*[name()='dc:title'][@xml:lang='en']");
				if (value==null||value=="")
					value= rootElement.valueOf("*[name()='dc:title']");
				return value;
			}
			else if(this.targetformat.equals("comm_para"))
				return rootElement.valueOf("*[name()='paradataTitle']"); 
			else if(this.targetformat.equals("comm_anno"))
				return rootElement.valueOf("*[name()='title']"); 
		}
		return null;
	}
	
	public String getDescription()
	{
		Element rootElement = this.getTargetXMLDomRootElement();
		if(rootElement != null)
		{
			if(this.targetformat.equals("nsdl_dc"))
			{
				String value = rootElement.valueOf("*[name()='dc:description'][@xml:lang='en'] ");
				if (value==null||value=="")
					value= rootElement.valueOf("*[name()='dc:description']");
				return value;
			}
			else if(this.targetformat.equals("comm_para"))
				return rootElement.valueOf("*[name()='paradataDescription'] "); 
		}
		return null;
	}
	
	public String getDocumentUrl()
	{
		Element rootElement = this.getTargetXMLDomRootElement();
		if(rootElement != null)
		{
			if(this.targetformat.equals("nsdl_dc"))
				return rootElement.valueOf("*[name()='dc:identifier'][@xsi:type='dct:URI']");
			else if(this.targetformat.equals("comm_para"))
				return rootElement.valueOf("*[name()='usageDataResourceURL'][contains(.,'http:') or contains(.,'https:') or contains(.,'ftp:') or contains(.,'www')]");
			else if(this.targetformat.equals("comm_anno"))
				return rootElement.valueOf("*[name()='annotatedID'][@idType='URL']"); 
		
		}
		return null;
	}
	
	private Element getTargetXMLDomRootElement()
	{
		if( targetXMLRootElement==null && this.target_xml!=null)
		{
			Document document;
			try {
				document = Dom4jUtils.getXmlDocument(this.target_xml);
				this.targetXMLRootElement = document.getRootElement();
			}
			catch (DocumentException e) {
				// do nothing just don't show anything
			}
		}
		return this.targetXMLRootElement;
	}
	public String getCollectionName() {
		return collectionName;
	}
	
}
