<?xml version="1.0" encoding="UTF-8"?>
<!-- =============================================================  
 "crsd105ToDbi107" stylesheet 

transform carressed file containing OAI2.0 ListRecords response with 
		nsdl_dc version 1.00    or
		nsdl_dc version 1.01    or
		nsdl_dc version 1.02    or
		oai_dc version 2.0 
ITEM records into a a db_insert file containing normalized and native item records 
ready for input into the Metadata Repository (database).

Author:  Naomi Dushay 
updated: 04/01/09 properly handle case of file with only deleted records 
updated: 03/10/03 nsdl_dc v1.02  and dbinsert version 1.07
updated: 03/09/02 crsd version 1.05  and dbinsert version 1.06
Created: 03/06/04  
================================================================ -->
<xsl:stylesheet version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xmlns:crsd="http://ns.nsdl.org/MRingest/crsd_v1.05/"
    xmlns:OAI2.0="http://www.openarchives.org/OAI/2.0/"
    xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
    xmlns:native_nsdl_dc_v1.00="http://ns.nsdl.org/nsdl_dc_v1.00"     
    xmlns:native_nsdl_dc_v1.01="http://ns.nsdl.org/nsdl_dc_v1.01"
    xmlns:nsdl_dc="http://ns.nsdl.org/nsdl_dc_v1.02/"
    xmlns:dc="http://purl.org/dc/elements/1.1/" 
    xmlns:dct="http://purl.org/dc/terms/"
    xmlns:ieee="http://www.ieee.org/xsd/LOMv1p0" 
    xmlns:lar="http://ns.nsdl.org/schemas/dc/lar"
    
    exclude-result-prefixes="OAI2.0 crsd oai_dc native_nsdl_dc_v1.00 native_nsdl_dc_v1.01">
<!-- 
    exclude-result-prefixes="#default OAI2.0 crsd oai_dc native_nsdl_dc_v1.00 native_nsdl_dc_v1.01">
-->

	<xsl:strip-space elements="*"/>
	<xsl:output omit-xml-declaration="yes" indent="yes" encoding="UTF-8"/>
	<xsl:include href="include/addDCMIType.xsl"/>
	<xsl:include href="include/addIMT.xsl"/>
	<xsl:include href="include/addURI.xsl"/>
	<xsl:include href="include/addW3CDTF.xsl"/>
	<xsl:include href="include/addRFC3066.xsl"/>
	<xsl:include href="include/copy-noNS.xsl"/>
	<xsl:include href="include/removeFieldsWithoutInfo.xsl"/>
<!--  NOTE:  not yet ready for prime time, as need way to "recognize" appropriate values ...
	<xsl:include href="addISSN.xsl"/>
	<xsl:include href="addISBN.xsl"/>
-->
	    
  <!-- root element-->
  <xsl:template match="/node()">
  	
	<nsdl_dc:nsdl_dc schemaVersion="1.02.000" xsi:schemaLocation="http://ns.nsdl.org/nsdl_dc_v1.02/ http://ns.nsdl.org/schemas/nsdl_dc/nsdl_dc_v1.02.xsd">

					 <xsl:apply-templates select="dc:identifier" mode="addURI"/>
					 <xsl:apply-templates select="dc:source" mode="addURI"/>
					 <xsl:apply-templates select="dc:relation" mode="addURI"/>
					 <xsl:apply-templates select="dct:isVersionOf" mode="addURI"/>
					 <xsl:apply-templates select="dct:hasVersion" mode="addURI"/>
					 <xsl:apply-templates select="dct:isReplacedBy" mode="addURI"/>
					 <xsl:apply-templates select="dct:replaces" mode="addURI"/>
					 <xsl:apply-templates select="dct:isRequiredBy" mode="addURI"/>
					 <xsl:apply-templates select="dct:requires" mode="addURI"/>
					 <xsl:apply-templates select="dct:isPartOf" mode="addURI"/>
					 <xsl:apply-templates select="dct:hasPart" mode="addURI"/>
					 <xsl:apply-templates select="dct:isReferencedBy" mode="addURI"/>
					 <xsl:apply-templates select="dct:references" mode="addURI"/>
					 <xsl:apply-templates select="dct:isFormatOf" mode="addURI"/>
					 <xsl:apply-templates select="dct:hasFormat" mode="addURI"/>
					 <xsl:apply-templates select="dct:conformsTo" mode="addURI"/>

					 <xsl:apply-templates select="dc:type" mode="addDCMIType"/>

					 <xsl:apply-templates select="dc:format" mode="addIMT"/>

					 <xsl:apply-templates select="dc:language" mode="addRFC3066"/>

					 <xsl:apply-templates select="dc:date" mode="addW3CDTF"/>
					 <xsl:apply-templates select="dct:created" mode="addW3CDTF"/>
					 <xsl:apply-templates select="dct:valid" mode="addW3CDTF"/>
					 <xsl:apply-templates select="dct:available" mode="addW3CDTF"/>
					 <xsl:apply-templates select="dct:issued" mode="addW3CDTF"/>
					 <xsl:apply-templates select="dct:modified" mode="addW3CDTF"/>
					 <xsl:apply-templates select="dct:dateAccepted" mode="addW3CDTF"/>
					 <xsl:apply-templates select="dct:dateCopyrighted" mode="addW3CDTF"/>
					 <xsl:apply-templates select="dct:dateSubmitted" mode="addW3CDTF"/>
					 <xsl:apply-templates select="dc:coverage" mode="addW3CDTF"/>
					 <xsl:apply-templates select="dct:temporal" mode="addW3CDTF"/>

					<!-- NOTE: the following expression selects the top level *element* children in the metadata -->

					<!-- NOTE:  tried to check namespace-uri() as well, but got "arrayOutOfBounds" error -->
					<!-- local name only should be fine because we should only be processing oai_dc 2.0, nsdl_dc 1.00 or 1.01. -->
					 <xsl:apply-templates mode="removeFieldsWithoutInfo"
 							select="*[ not ( self::node()[local-name() = 'identifier']  or
													         self::node()[local-name() = 'source']  or
													         self::node()[local-name() = 'relation']  or
													         self::node()[local-name() = 'isVersionOf']  or
													         self::node()[local-name() = 'hasVersion']  or
													         self::node()[local-name() = 'isReplacedBy']  or
													         self::node()[local-name() = 'replaces']  or
													         self::node()[local-name() = 'isRequiredBy']  or
													         self::node()[local-name() = 'requires']  or
													         self::node()[local-name() = 'isPartOf']  or
													         self::node()[local-name() = 'hasPart']  or
													         self::node()[local-name() = 'isReferencedBy']  or
													         self::node()[local-name() = 'references']  or
													         self::node()[local-name() = 'isFormatOf']  or
													         self::node()[local-name() = 'hasFormat']  or
													         self::node()[local-name() = 'conformsTo']  or
													         self::node()[local-name() = 'type']  or
													         self::node()[local-name() = 'format']  or
													         self::node()[local-name() = 'language']  or
													         self::node()[local-name() = 'date']  or
													         self::node()[local-name() = 'created']  or
													         self::node()[local-name() = 'valid']  or
													         self::node()[local-name() = 'available']  or
													         self::node()[local-name() = 'issued']  or
													         self::node()[local-name() = 'modified']  or
													         self::node()[local-name() = 'dateAccepted']  or
													         self::node()[local-name() = 'dateCopyrighted']  or
													         self::node()[local-name() = 'dateSubmitted']  or
													         self::node()[local-name() = 'coverage']  or
													         self::node()[local-name() = 'temporal']  )  ]" />
				</nsdl_dc:nsdl_dc>
  </xsl:template>

</xsl:stylesheet>
