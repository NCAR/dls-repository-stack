<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0" 
    			xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    			
                xmlns:gco="http://www.isotc211.org/2005/gco"
                xmlns:gmd="http://www.isotc211.org/2005/gmd"
                xmlns:gml="http://www.opengis.net/gml/3.2"
                xmlns:gmx="http://www.isotc211.org/2005/gmx"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xsi:schemaLocation="http://www.isotc211.org/2005/gco http://www.isotc211.org/2005/gco/gco.xsd
					http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd
					http://www.isotc211.org/2005/gmx http://www.isotc211.org/2005/gmx/gmx.xsd"
	
				xmlns:lar="http://ns.nsdl.org/ncs/lar"
    				exclude-result-prefixes="lar xsi xsl">
	
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	
	<xsl:strip-space elements="*"/>	
	
	<xsl:template match="lar:record">
		<gmd:MD_Metadata xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xmlns:gco="http://www.isotc211.org/2005/gco"
                 xmlns:gmd="http://www.isotc211.org/2005/gmd"
                 xmlns:gml="http://www.opengis.net/gml/3.2"
                 xmlns:gmx="http://www.isotc211.org/2005/gmx"
                 xmlns:xlink="http://www.w3.org/1999/xlink"
                 xsi:schemaLocation="http://www.isotc211.org/2005/gco http://www.isotc211.org/2005/gco/gco.xsd
						http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd
						http://www.isotc211.org/2005/gmx http://www.isotc211.org/2005/gmx/gmx.xsd">
						

		<xsl:element name="gmd:fileIdentifier">
			<xsl:element name="gco:CharacterString">
       			<xsl:value-of select="lar:recordID"/>
       		</xsl:element>	
       	</xsl:element>
       	
       	<gmd:language>
      <gmd:LanguageCode codeList="http://www.loc.gov/standards/iso639-2/" codeListValue="eng"/>
	  </gmd:language>
	  
	  <gmd:characterSet>
	      <gmd:MD_CharacterSetCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/ML_gmxCodelists.xml#MD_CharacterSetCode" codeListValue="utf8"/>
	  </gmd:characterSet>
	  
	  <gmd:hierarchyLevel>
	      <gmd:MD_ScopeCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/ML_gmxCodelists.xml#MD_ScopeCode" codeListValue="dataset"/>
	  </gmd:hierarchyLevel>
	
	  <gmd:contact>
	      <gmd:CI_ResponsibleParty>
	         <gmd:organisationName>
	            <gco:CharacterString><xsl:value-of select="lar:contributor"/></gco:CharacterString>
	         </gmd:organisationName>
	
	         <gmd:role>
					<gmd:CI_RoleCode codeList="http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_RoleCode" codeListValue="resourceProvider">resourceProvider</gmd:CI_RoleCode>
			</gmd:role>		
	      </gmd:CI_ResponsibleParty>
	  </gmd:contact>
	  
	  <gmd:dateStamp>
	      <gco:DateTime><xsl:value-of select="lar:recordDate"/>T00:00:00</gco:DateTime>
	  </gmd:dateStamp>

		<gmd:metadataStandardName>
		      <gco:CharacterString>WMO Core Metadata Profile of ISO 19115 (WMO Core), 2003/Cor.1:2006 (ISO 19115), 2007 (ISO/TS 19139)</gco:CharacterString>
		  </gmd:metadataStandardName>
		  
		  <gmd:metadataStandardVersion>
		      <gco:CharacterString>1.3</gco:CharacterString>
		  </gmd:metadataStandardVersion>
		  
		  <gmd:identificationInfo>
		      <gmd:MD_DataIdentification>
		      
		         <gmd:citation>
		            <gmd:CI_Citation>
		               <gmd:title>
		                  <gco:CharacterString><xsl:value-of select="lar:title"/></gco:CharacterString>
		               </gmd:title>
		               <gmd:date>
		                  <gmd:CI_Date>
		                     <gmd:date>
		                        <gco:Date><xsl:value-of select="lar:recordDate"/></gco:Date>
		                     </gmd:date>
		                     <gmd:dateType>
		                        <gmd:CI_DateTypeCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/ML_gmxCodelists.xml#CI_DateTypeCode" codeListValue="publication"/>
		                     </gmd:dateType>
		                  </gmd:CI_Date>
		               </gmd:date>
		              
		               <gmd:presentationForm>
		                  <gmd:CI_PresentationFormCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/ML_gmxCodelists.xml#CI_PresentationFormCode" codeListValue="documentDigital"/>
		               </gmd:presentationForm>
		            </gmd:CI_Citation>
		         </gmd:citation>
		         
		         <gmd:abstract>
		            <gco:CharacterString><xsl:value-of select="lar:description"/></gco:CharacterString>
		         </gmd:abstract>
		         
		         <gmd:pointOfContact>
						<gmd:CI_ResponsibleParty>
							<gmd:individualName>
								<gco:CharacterString><xsl:value-of select="lar:contributor"/> Support</gco:CharacterString>
							</gmd:individualName>
							<gmd:role>
								<gmd:CI_RoleCode codeList="http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_RoleCode" codeListValue="pointOfContact">pointOfContact</gmd:CI_RoleCode>
							</gmd:role>
						</gmd:CI_ResponsibleParty>
				</gmd:pointOfContact>
					
		         
		         <gmd:descriptiveKeywords uuidref="wmoscope">
		            <gmd:MD_Keywords>
		            	<xsl:for-each select="lar:keyword">
		            		<gmd:keyword>
		                  		<gco:CharacterString><xsl:value-of select="."/></gco:CharacterString>
		               		</gmd:keyword>
		            	</xsl:for-each>
		                           
		               <gmd:type>
		                  <gmd:MD_KeywordTypeCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/ML_gmxCodelists.xml#MD_KeywordTypeCode" codeListValue="theme"/>
		               </gmd:type>
		              
		            </gmd:MD_Keywords>
		         </gmd:descriptiveKeywords>
		        
		         
		         <gmd:resourceConstraints>
		            <gmd:MD_LegalConstraints>
		               <gmd:accessConstraints>
		                  <gmd:MD_RestrictionCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/ML_gmxCodelists.xml#MD_RestrictionCode" codeListValue="otherRestrictions"/>
		               </gmd:accessConstraints>
		               <gmd:useConstraints>
		                  <gmd:MD_RestrictionCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/ML_gmxCodelists.xml#MD_RestrictionCode" codeListValue="otherRestrictions"/>
		               </gmd:useConstraints>  
		            </gmd:MD_LegalConstraints>
		         </gmd:resourceConstraints>
		         
		     
		         <gmd:language>
		            <gmd:LanguageCode codeList="http://www.loc.gov/standards/iso639-2/" codeListValue="eng"/>
		         </gmd:language>
		      
		         <gmd:topicCategory>
		            <gmd:MD_TopicCategoryCode>climatologyMeteorologyAtmosphere</gmd:MD_TopicCategoryCode>
		         </gmd:topicCategory>
		         
		         <gmd:extent>
		            <gmd:EX_Extent id="boundingExtent">
		               <gmd:description>
		                  <gco:CharacterString>Applicable Everywhere</gco:CharacterString>
		               </gmd:description>
		               <gmd:geographicElement>
		                  <gmd:EX_GeographicBoundingBox id="boundingGeographicBoundingBox">
		                     <gmd:westBoundLongitude>
		                        <gco:Decimal>180</gco:Decimal>
		                     </gmd:westBoundLongitude>
		                     <gmd:eastBoundLongitude>
		                        <gco:Decimal>180</gco:Decimal>
		                     </gmd:eastBoundLongitude>
		                     <gmd:southBoundLatitude>
		                        <gco:Decimal>90</gco:Decimal>
		                     </gmd:southBoundLatitude>
		                     <gmd:northBoundLatitude>
		                        <gco:Decimal>90</gco:Decimal>
		                     </gmd:northBoundLatitude>
		                  </gmd:EX_GeographicBoundingBox>
		               </gmd:geographicElement>
		               
		            </gmd:EX_Extent>
		         </gmd:extent>
		      </gmd:MD_DataIdentification>
		  </gmd:identificationInfo>
		   
		  <gmd:distributionInfo>
		      <gmd:MD_Distribution>
		         <gmd:distributionFormat>
		            <gmd:MD_Format>
						<gmd:name>
							<gco:CharacterString>HTML</gco:CharacterString>
						</gmd:name>
						<gmd:version gco:nilReason="unknown"/>
					</gmd:MD_Format>
		         </gmd:distributionFormat>
		         
		         <gmd:distributor>
		            <gmd:MD_Distributor>
		               <gmd:distributorContact>
		                  <gmd:CI_ResponsibleParty>
		                     <gmd:organisationName>
		                        <gco:CharacterString><xsl:value-of select="lar:contributor"/></gco:CharacterString>
		                     </gmd:organisationName>
		                     <gmd:role>
								<gmd:CI_RoleCode codeList="http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#CI_RoleCode" codeListValue="resourceProvider">resourceProvider</gmd:CI_RoleCode>
							</gmd:role>
		                  </gmd:CI_ResponsibleParty>
		               </gmd:distributorContact>
		              
		               <gmd:distributorTransferOptions>
		                  <gmd:MD_DigitalTransferOptions>
		                     <gmd:onLine>
		                        <gmd:CI_OnlineResource>
		                           <gmd:linkage>
		                              <gmd:URL><xsl:value-of select="lar:identifier"/></gmd:URL>
		                           </gmd:linkage>
		                           <gmd:protocol>
		                              <gco:CharacterString>WWW:LINK-1.0-http--link</gco:CharacterString>
		                           </gmd:protocol>	                       
		                           <gmd:function>
		                              <gmd:CI_OnLineFunctionCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/ML_gmxCodelists.xml#CI_OnLineFunctionCode" codeListValue="search"/>
		                           </gmd:function>
		                        </gmd:CI_OnlineResource>
		                     </gmd:onLine>
		                  </gmd:MD_DigitalTransferOptions>
		               </gmd:distributorTransferOptions>
		              
		              
		            </gmd:MD_Distributor>
		         </gmd:distributor>
		         
		         
		         
		      </gmd:MD_Distribution>
		  </gmd:distributionInfo>									
				
						
		</gmd:MD_Metadata>
	</xsl:template>
		
</xsl:stylesheet>