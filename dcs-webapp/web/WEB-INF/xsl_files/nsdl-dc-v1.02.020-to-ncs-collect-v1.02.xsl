<?xml version="1.0"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:nsdl_dc="http://ns.nsdl.org/nsdl_dc_v1.02/"
    xmlns:ncs="http://ns.nsdl.org/ncs"
    xmlns:dct="http://purl.org/dc/terms/"
    xmlns:ieee="http://www.ieee.org/xsd/LOMv1p0"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="xsi dc nsdl_dc dct ieee"
    version="1.0">

<!--PURPOSE-->
<!-- **************************************-->
<!--To transform nsdl_dc version 1.02.020 metadata records to the ncs_collect version 1.02 format and vice versa.-->

<!--LICENSE INFORMATION and CREDITS-->
<!-- *****************************************************-->
<!--Date created: 2006-12-12 by Katy Ginger, University Corporation for Atmospheric Research (UCAR)-->
<!--Last modified: 2007-12-1820 by Katy Ginger-->
<!--Edlevel stuff and schema version-->
<!--Changes: to break nsdl-ncs-to-dc-and-dc-to-ncs-v1.02.xsl into 2 separate transforms and remove the extra resulting namespaces-->
<!--License information:
		Copyright (c) 2006 University Corporation for Atmospheric Research (UCAR)
		P.O. Box 3000, Boulder, CO 80307, United States of America
		email: support@nsdl.org. 
		All rights reserved-->

	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>


<!--VARIABLES used throughout the transform-->
<!-- **********************************************************-->
	<xsl:variable name="asnIdsURL">http://ns.nsdl.org/ncs/xml/NCS-to-ASN-NSES-mappings.xml</xsl:variable>
	<xsl:variable name="asnText">http://purl.org/ASN/</xsl:variable>
	
<!--TRANSFORMATION CODE-->
<!-- **************************************-->
	<xsl:template match="*|/">
		<xsl:apply-templates select="nsdl_dc:nsdl_dc"/>
	</xsl:template>


<!--TRANSFORMATION CODE for nsdl_dc to nsdl_ncs-->
<!-- ********************************************************-->
	<xsl:template match="nsdl_dc:nsdl_dc">
	<record xmlns="http://ns.nsdl.org/ncs" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://ns.nsdl.org/ncs http://ns.nsdl.org/ncs/ncs_collect/1.02/schemas/ncs-collect.xsd">

<!--the general field must be written in the following sequence: recordsID, url, title, alternative, description, subjects, abstract, tableOfContents, bibliographicCitation, languages-->
<!--do not need to determine if educational content is present, because there are elements within educational that are required-->

		<xsl:element name="general" namespace="http://ns.nsdl.org/ncs">
			<xsl:element name="recordID" namespace="http://ns.nsdl.org/ncs">needsNCSrecordID</xsl:element>

<!--takes first identifier value to be a URL and does not process any other identifier fields-->
			<xsl:element name="url" namespace="http://ns.nsdl.org/ncs">
				<xsl:value-of select="dc:identifier"/>
			</xsl:element>

<!--title-->
			<xsl:apply-templates select="dc:title" mode="toncs">
				<xsl:with-param name="tag">title</xsl:with-param>
			</xsl:apply-templates>

<!--alternative-->
			<xsl:apply-templates select="dc:alternative" mode="toncs">
				<xsl:with-param name="tag">alternative</xsl:with-param>
			</xsl:apply-templates>

<!--description-->
			<xsl:apply-templates select="dc:description" mode="toncs">
				<xsl:with-param name="tag">description</xsl:with-param>
			</xsl:apply-templates>

<!--subject-->
			<xsl:apply-templates select="dc:subject" mode="toncs">
				<xsl:with-param name="tag">subject</xsl:with-param>
			</xsl:apply-templates>

<!--subjects when using the NSDL_DC:GEM subject; stoppped using this 2007-10-22-->
<!--determine if content in subject exists-->
<!--			<xsl:variable name="content">
				<xsl:for-each select="dc:subject">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="subjects" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dc:subject[@xsi:type='nsdl_dc:GEM']">-->
<!--match to <dc:subject xsi:type="nsdl_dc:GEM">-->
<!--						<xsl:with-param name="tag">gemSubject</xsl:with-param>
						<xsl:with-param name="url">http://ns.nsdl.org/schemas/gem_type/gem_type_v1.01.xsd</xsl:with-param>
						<xsl:with-param name="altTag">otherSubject</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dc:subject[@xsi:type='dct:LCC']" mode="toncs">
						<xsl:with-param name="tag">otherSubject</xsl:with-param>
						<xsl:with-param name="att">dct:LCC</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dc:subject[@xsi:type='dct:MESH']" mode="toncs">
						<xsl:with-param name="tag">otherSubject</xsl:with-param>
						<xsl:with-param name="att">dct:MESH</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dc:subject[@xsi:type='dct:LCSH']" mode="toncs">
						<xsl:with-param name="tag">otherSubject</xsl:with-param>
						<xsl:with-param name="att">dct:LCSH</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dc:subject[@xsi:type='dct:DDC']" mode="toncs">
						<xsl:with-param name="tag">otherSubject</xsl:with-param>
						<xsl:with-param name="att">dct:DDC</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dc:subject[@xsi:type='dct:UDC']" mode="toncs">
						<xsl:with-param name="tag">otherSubject</xsl:with-param>
						<xsl:with-param name="att">dct:UDC</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dc:subject[not(@xsi:type='dct:UDC') and not(@xsi:type='dct:DDC') and not(@xsi:type='dct:LCSH') and not(@xsi:type='dct:MESH') and not(@xsi:type='dct:LCC') and not(@xsi:type='nsdl_dc:GEM')]" mode="toncs">
						<xsl:with-param name="tag">otherSubject</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>-->	
<!--end subjects-->
		
<!--abstract-->
			<xsl:apply-templates select="dc:abstract" mode="toncs">
				<xsl:with-param name="tag">abstract</xsl:with-param>
			</xsl:apply-templates>

<!--tableOfContents-->
			<xsl:apply-templates select="dc:tableOfContents" mode="toncs">
				<xsl:with-param name="tag">tableOfContents</xsl:with-param>
			</xsl:apply-templates>

<!--bibliographicCitation-->
			<xsl:apply-templates select="dc:bibliographicCitation" mode="toncs">
				<xsl:with-param name="tag">bibliographicCitation</xsl:with-param>
			</xsl:apply-templates>
		
<!--languages, ISOcode, RFC code and otherLanguage-->
<!--determine if content in language exists-->
			<xsl:variable name="content">
				<xsl:for-each select="dc:language">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="languages" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dc:language[@xsi:type='dct:ISO639-2']">
	<!--match to <dc:language xsi:type="dct:ISO639-2">-->
						<xsl:with-param name="tag">ISOcode</xsl:with-param>
						<xsl:with-param name="url">http://www.it.ojp.gov/jxdm/iso_639-2b/1.0/iso_639-2b.xsd</xsl:with-param>
						<xsl:with-param name="altTag">otherLanguage</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dc:language[not(@xsi:type='dct:RFC3066') and not(@xsi:type='dct:ISO639-2')]" mode="toncs">
						<xsl:with-param name="tag">otherLanguage</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dc:language[@xsi:type='dct:RFC3066']" mode="toncs">
	<!--match to <dc:language xsi:type="dct:RFC3066">-->
						<xsl:with-param name="tag">RFCcode</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>		
			
		</xsl:element><!--end general element-->

<!--begin educational element-->
<!--educational elements can be written in any order-->
		<xsl:element name="educational" namespace="http://ns.nsdl.org/ncs">

<!--begin educationLevels-->
<!--2007-12-18: for NSDLEdLevel there are 2 schemas
http://ns.nsdl.org/schemas/ed_type/ed_type_v1.00.xsd (has deprecated terms included)
http://ns.nsdl.org/schemas/ed_type/ed_type_v1.01.xsd (excludes deprecated terms)-->
<!--since ncs_item excludes the deprecated terms, crosswalk using vocab terms in v1.01and treat deprecated and all other terms as 'otherEdLevel' terms-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:educationLevel">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="educationLevels" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:educationLevel[@xsi:type='nsdl_dc:NSDLEdLevel']">
	<!--match to <dct:educationLevel xsi:type="nsdl_dc:NSDLEdLevel">-->
						<xsl:with-param name="tag">nsdlEdLevel</xsl:with-param>
						<xsl:with-param name="url">http://ns.nsdl.org/schemas/ed_type/ed_type_v1.01.xsd</xsl:with-param>
						<xsl:with-param name="altTag">otherEdLevel</xsl:with-param>
					</xsl:apply-templates>

<!--2007-07-03: new stuff specific to nsdl_dc to ncs_collect only-->
<!--2007-07-03: this apply template was added to address make the ncs_collect term of Pre-K to 12 look like an NSDL term; this means that <dct:educationLevel>Pre-K to 12</dct:educationLevel> gets transformed to <nsdlEdLevel>Pre-K to 12</nsdlEdLevel>-->
					<xsl:apply-templates select="dct:educationLevel[not(@xsi:type='nsdl_dc:NSDLEdLevel') and (. = 'Pre-K to 12')]" mode="toncs">
						<xsl:with-param name="tag">nsdlEdLevel</xsl:with-param>
					</xsl:apply-templates>					
									
<!--2007-07-03: this apply template was updated to include the 'not' so that <dct:educationLevel>Pre-K to 12</dct:educationLevel> does not get transformed to <otherEdLevel>Pre-K to 12</otherEdLevel> but that <dct:educationLevel>5th grade</dct:educationLevel> will get transformed to <otherEdLevel>5th grade</otherEdLevel>-->
					<xsl:apply-templates select="dct:educationLevel[not(@xsi:type='nsdl_dc:NSDLEdLevel') and not(. = 'Pre-K to 12')]" mode="toncs">
						<xsl:with-param name="tag">otherEdLevel</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element><!--end educationLevels element-->
			</xsl:if>	
<!--ed educationLevels-->

<!--begin types-->
			<xsl:variable name="content">
				<xsl:for-each select="dc:type">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="types" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dc:type[@xsi:type='dct:DCMIType']">
	<!--match to <dc:type xsi:type="dct:DCMIType">-->
						<xsl:with-param name="tag">dcmiType</xsl:with-param>
						<xsl:with-param name="url">http://dublincore.org/schemas/xmls/qdc/2004/06/14/dcmitype.xsd</xsl:with-param>
						<xsl:with-param name="altTag">otherType</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dc:type[@xsi:type='nsdl_dc:NSDLType']">
	<!--match to <dc:type xsi:type="nsdl_dc:NSDLType">-->
						<xsl:with-param name="tag">nsdlType</xsl:with-param>
						<xsl:with-param name="url">http://ns.nsdl.org/schemas/nsdltype/nsdltype_v1.00.xsd</xsl:with-param>
						<xsl:with-param name="altTag">otherType</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dc:type[not(@xsi:type='dct:DCMIType') and not(@xsi:type='nsdl_dc:NSDLType')]" mode="toncs">
						<xsl:with-param name="tag">otherType</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element><!--end types element-->
			</xsl:if>	
<!--end types-->

<!--begin audiences-->
<!--waiting for an approved controlled vocabulary-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:audience">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="audiences" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:audience[@xsi:type='nsdl_dc:NSDLAudience']">
	<!--match to <dc:type xsi:type="nsdl_dc:NSDLAudience">-->
						<xsl:with-param name="tag">nsdlAudience</xsl:with-param>
						<xsl:with-param name="url">http://ns.nsdl.org/schemas/audience_type/audience_type_v1.00.xsd</xsl:with-param>
						<xsl:with-param name="altTag">otherAudience</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:audience[not(@xsi:type='nsdl_dc:NSDLAudience')]" mode="toncs">
						<xsl:with-param name="tag">otherAudience</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--end audiences-->

<!--begin mediators-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:mediator">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="mediators" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:mediator" mode="toncs">
						<xsl:with-param name="tag">mediator</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--end mediators-->

<!--begin instructionalMethods-->
<!--waiting for an approved controlled vocabulary-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:instructionalMethod">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="instructionalMethods" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:instructionalMethod" mode="toncs">
						<xsl:with-param name="tag">method</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--end instructionalMethod-->

<!--begin interactivityLevel-->
			<xsl:variable name="content">
				<xsl:for-each select="ieee:interactivityLevel">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="interactivityLevel" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="ieee:interactivityLevel">
						<xsl:with-param name="tag">LOM</xsl:with-param>
						<xsl:with-param name="url">http://ns.nsdl.org/ncs/ncs_item/1.02/schemas/vocabs/interactivityLevelLOM.xsd</xsl:with-param>
						<xsl:with-param name="altTag">description</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--end interactivityLevel-->

<!--begin interactivityType-->
			<xsl:variable name="content">
				<xsl:for-each select="ieee:interactivityType">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="interactivityType" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="ieee:interactivityType">
						<xsl:with-param name="tag">LOM</xsl:with-param>
						<xsl:with-param name="url">http://ns.nsdl.org/ncs/ncs_item/1.02/schemas/vocabs/interactivityTypeLOM.xsd</xsl:with-param>
						<xsl:with-param name="altTag">description</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--end interactivityType-->

<!--begin typicalLearningTime-->
<!--not checking to see if content value is of type duration; therefore only write the typicalLearningTime.description tag and not typicalLearningTime.duration tag-->
			<xsl:variable name="content">
				<xsl:for-each select="ieee:typicalLearningTime">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="typicalLearningTime" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="ieee:typicalLearningTime">
						<xsl:with-param name="tag">description</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--end typicalLearningTime-->

<!--begin standards-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:conformsTo">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="standards" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:conformsTo"/>
				</xsl:element>
			</xsl:if>	
<!--end standards-->
		</xsl:element><!--end educational element-->

<!--begin contributions element-->
<!--do not need to determine if contributions content is present, because contributions is optional and can be empty-->
<!--contributions elements can be written in any order-->
		<xsl:element name="contributions" namespace="http://ns.nsdl.org/ncs">
<!--creators-->
			<xsl:variable name="content">
				<xsl:for-each select="dc:creator">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="creators" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dc:creator" mode="toncs">
						<xsl:with-param name="tag">creator</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	

<!--contributors-->
			<xsl:variable name="content">
				<xsl:for-each select="dc:contributor">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="contributors" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dc:contributor" mode="toncs">
						<xsl:with-param name="tag">contributor</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	

<!--publishers-->
			<xsl:variable name="content">
				<xsl:for-each select="dc:publisher">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="publishers" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dc:publisher" mode="toncs">
						<xsl:with-param name="tag">publisher</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
		</xsl:element><!--end contributions element-->

<!--begin rights element-->
<!--do not need to determine if contributions content is present, because rights is optional and can be empty-->
<!--rights elements can be written in any order but most elements can only appear once so need to concatenate-->
		<xsl:element name="rights" namespace="http://ns.nsdl.org/ncs">
<!--rights-->
			<xsl:variable name="content">
				<xsl:for-each select="dc:rights">
					<xsl:choose >
						<xsl:when test="string-length(.)>0">
							<xsl:choose>
								<xsl:when test="position()=last()">
									<xsl:value-of select="."/>
								</xsl:when>
								<xsl:otherwise><!--add a semicolon and space when concatenating-->
									<xsl:value-of select="concat(., '; ')"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise/>
					</xsl:choose>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="rights" namespace="http://ns.nsdl.org/ncs">
					<xsl:value-of select="$content"/>
				</xsl:element>
			</xsl:if>

<!--accessRights-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:accessRights">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="accessRights" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:accessRights[@xsi:type='nsdl_dc:NSDLAccess']">
						<xsl:with-param name="tag">meansOfAccess</xsl:with-param>
						<xsl:with-param name="url">http://ns.nsdl.org/schemas/access_rights_type/access_type_v1.00.xsd</xsl:with-param>
						<xsl:with-param name="altTag">description</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:accessRights[@xsi:type='dct:URI']" mode="toncs">
	<!--match to <dct:accessRights xsi:type="dct:URI">-->
						<xsl:with-param name="tag">url</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:accessRights[not(@xsi:type='dct:URI') and not(@xsi:type='nsdl_dc:NSDLAccess')]" mode="toncs">
						<xsl:with-param name="tag">description</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--provenance-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:provenance">
					<xsl:choose >
						<xsl:when test="string-length(.)>0">
							<xsl:choose>
								<xsl:when test="position()=last()">
									<xsl:value-of select="."/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="concat(., '; ')"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise/>
					</xsl:choose>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="provenance" namespace="http://ns.nsdl.org/ncs">
					<xsl:value-of select="$content"/>
				</xsl:element>
			</xsl:if>
<!--accrualMethod-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:accrualMethod">
					<xsl:choose >
						<xsl:when test="string-length(.)>0">
							<xsl:choose>
								<xsl:when test="position()=last()">
									<xsl:value-of select="."/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="concat(., '; ')"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise/>
					</xsl:choose>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="accrualMethod" namespace="http://ns.nsdl.org/ncs">
					<xsl:value-of select="$content"/>
				</xsl:element>
			</xsl:if>
<!--accrualPeriodicity-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:accrualPeriodicity">
					<xsl:choose >
						<xsl:when test="string-length(.)>0">
							<xsl:choose>
								<xsl:when test="position()=last()">
									<xsl:value-of select="."/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="concat(., '; ')"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise/>
					</xsl:choose>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="accrualPeriodicity" namespace="http://ns.nsdl.org/ncs">
					<xsl:value-of select="$content"/>
				</xsl:element>
			</xsl:if>
<!--accrualPolicy-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:accrualPolicy">
					<xsl:choose >
						<xsl:when test="string-length(.)>0">
							<xsl:choose>
								<xsl:when test="position()=last()">
									<xsl:value-of select="."/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="concat(., '; ')"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise/>
					</xsl:choose>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="accrualPolicy" namespace="http://ns.nsdl.org/ncs">
					<xsl:value-of select="$content"/>
				</xsl:element>
			</xsl:if>
<!--rightsHolders-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:rightsHolder">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="rightsHolders" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:rightsHolder" mode="toncs">
						<xsl:with-param name="tag">rightsHolder</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--license-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:license">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="license" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:license[@xsi:type='dct:URI']" mode="toncs">
	<!--match to <dct:license xsi:type="dct:URI">-->
						<xsl:with-param name="tag">url</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:license[not(@xsi:type='dct:URI')]" mode="toncs">
						<xsl:with-param name="tag">description</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
		</xsl:element><!--end rights element-->
		
<!--begin relations element-->
<!--do not need to determine if relations content is present, because relations is optional and can be empty-->
<!--relation elements can be written in any order-->
		<xsl:element name="relations" namespace="http://ns.nsdl.org/ncs">
<!--sources-->
			<xsl:variable name="content">
				<xsl:for-each select="dc:source">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="sources" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dc:source[@xsi:type='dct:URI']" mode="toncs">
						<xsl:with-param name="tag">url</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dc:source[not(@xsi:type='dct:URI')]" mode="toncs">
						<xsl:with-param name="tag">other</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	

<!--relations-->
			<xsl:variable name="content">
				<xsl:for-each select="dc:relation">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="relations" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dc:relation[@xsi:type='dct:URI']" mode="toncs">
						<xsl:with-param name="tag">url</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dc:relation[not(@xsi:type='dct:URI')]" mode="toncs">
						<xsl:with-param name="tag">other</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--isFormatOf-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:isFormatOf">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="isFormatOf" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:isFormatOf[@xsi:type='dct:URI']" mode="toncs">
						<xsl:with-param name="tag">url</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:isFormatOf[not(@xsi:type='dct:URI')]" mode="toncs">
						<xsl:with-param name="tag">other</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--hasFormat-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:hasFormat">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="hasFormat" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:hasFormat[@xsi:type='dct:URI']" mode="toncs">
						<xsl:with-param name="tag">url</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:hasFormat[not(@xsi:type='dct:URI')]" mode="toncs">
						<xsl:with-param name="tag">other</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--isPartOf-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:isPartOf">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="isPartOf" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:isPartOf[@xsi:type='dct:URI']" mode="toncs">
						<xsl:with-param name="tag">url</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:isPartOf[not(@xsi:type='dct:URI')]" mode="toncs">
						<xsl:with-param name="tag">other</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--hasPart-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:hasPart">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="hasPart" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:hasPart[@xsi:type='dct:URI']" mode="toncs">
						<xsl:with-param name="tag">url</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:hasPart[not(@xsi:type='dct:URI')]" mode="toncs">
						<xsl:with-param name="tag">other</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--isReferencedBy-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:isReferencedBy">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="isReferencedBy" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:isReferencedBy[@xsi:type='dct:URI']" mode="toncs">
						<xsl:with-param name="tag">url</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:isReferencedBy[not(@xsi:type='dct:URI')]" mode="toncs">
						<xsl:with-param name="tag">other</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--references-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:references">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="references" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:references[@xsi:type='dct:URI']" mode="toncs">
						<xsl:with-param name="tag">url</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:references[not(@xsi:type='dct:URI')]" mode="toncs">
						<xsl:with-param name="tag">other</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--isReplacedBy-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:isReplacedBy">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="isReplacedBy" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:isReplacedBy[@xsi:type='dct:URI']" mode="toncs">
						<xsl:with-param name="tag">url</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:isReplacedBy[not(@xsi:type='dct:URI')]" mode="toncs">
						<xsl:with-param name="tag">other</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--replaces-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:replaces">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="replaces" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:replaces[@xsi:type='dct:URI']" mode="toncs">
						<xsl:with-param name="tag">url</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:replaces[not(@xsi:type='dct:URI')]" mode="toncs">
						<xsl:with-param name="tag">other</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--isRequiredBy-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:isRequiredBy">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="isRequiredBy" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:isRequiredBy[@xsi:type='dct:URI']" mode="toncs">
						<xsl:with-param name="tag">url</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:isRequiredBy[not(@xsi:type='dct:URI')]" mode="toncs">
						<xsl:with-param name="tag">other</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--requires-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:requires">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="requires" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:requires[@xsi:type='dct:URI']" mode="toncs">
						<xsl:with-param name="tag">url</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:requires[not(@xsi:type='dct:URI')]" mode="toncs">
						<xsl:with-param name="tag">other</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
<!--isVersionOf-->
			</xsl:if>	
			<xsl:variable name="content">
				<xsl:for-each select="dct:isVersionOf">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="isVersionOf" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:isVersionOf[@xsi:type='dct:URI']" mode="toncs">
						<xsl:with-param name="tag">url</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:isVersionOf[not(@xsi:type='dct:URI')]" mode="toncs">
						<xsl:with-param name="tag">other</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
<!--hasVersion-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:hasVersion">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="hasVersion" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:hasVersion[@xsi:type='dct:URI']" mode="toncs">
						<xsl:with-param name="tag">url</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:hasVersion[not(@xsi:type='dct:URI')]" mode="toncs">
						<xsl:with-param name="tag">other</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
		</xsl:element><!--end relations element-->

<!--begin technical element-->
<!--do not need to determine if technical content is present, because tehnical is optional and can be empty-->
<!--technical elements must be written in order but some element can only be written once so need to concatenate-->
		<xsl:element name="technical" namespace="http://ns.nsdl.org/ncs">
<!--extent-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:extent">
					<xsl:choose >
						<xsl:when test="string-length(.)>0">
							<xsl:choose>
								<xsl:when test="position()=last()">
									<xsl:value-of select="."/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="concat(., '; ')"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise/>
					</xsl:choose>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="extent" namespace="http://ns.nsdl.org/ncs">
					<xsl:value-of select="$content"/>
				</xsl:element>
			</xsl:if>
<!--medium-->
			<xsl:apply-templates select="dct:medium" mode="toncs">
				<xsl:with-param name="tag">medium</xsl:with-param>
			</xsl:apply-templates>
<!--format-->
			<xsl:apply-templates select="dc:format[not(@xsi:type='dct:IMT')]" mode="toncs">
				<xsl:with-param name="tag">medium</xsl:with-param>
			</xsl:apply-templates>
<!--mimeTypes-->
			<xsl:variable name="content">
				<xsl:for-each select="dc:format[@xsi:type='dct:IMT']">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="mimeTypes" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dc:format[@xsi:type='dct:IMT']">
						<xsl:with-param name="tag">mimeType</xsl:with-param>
						<xsl:with-param name="url">http://ns.nsdl.org/schemas/mime_type/mime_type_v1.00.xsd</xsl:with-param>
						<xsl:with-param name="altTag">otherMimeType</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>	
		</xsl:element><!--end technical element-->

<!--begin dates element-->
<!--do not need to determine if dates content is present, because dates is optional and can be empty-->
<!--dates elements must be written in order-->
<!--do not check if content values follow dct:W3CDTF format; just write the field of element.W3Cdate; it the data is not correct, the W3Cdate will be caught as invalid; this is probably a good thing for nsdl_ncs; when the file is then transformed to nsdl_dc, the data is reasonably checked as to wether it is W3CDTF compliant; if not compliant then the dct:W3CDTF attribute is not written in nsdl_dc-->
<!--no checks for content values dct:ISO8601 format--> 
		<xsl:element name="dates" namespace="http://ns.nsdl.org/ncs">
<!--date-->
			<xsl:variable name="content">
				<xsl:for-each select="dc:date">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="date" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dc:date[@xsi:type='dct:W3CDTF']" mode="toncs">
						<xsl:with-param name="tag">W3Cdate</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dc:date[@xsi:type='dct:ISO8601']" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
						<xsl:with-param name="att">dct:ISO8601</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dc:date[not(@xsi:type='dct:W3CDTF') and not(@xsi:type='dct:ISO8601')]" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>
<!--created-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:created">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="created" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:created[@xsi:type='dct:W3CDTF']" mode="toncs">
						<xsl:with-param name="tag">W3Cdate</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:created[@xsi:type='dct:ISO8601']" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
						<xsl:with-param name="att">dct:ISO8601</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:created[not(@xsi:type='dct:W3CDTF') and not(@xsi:type='dct:ISO8601')]" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>
<!--available-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:available">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="available" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:available[@xsi:type='dct:W3CDTF']" mode="toncs">
						<xsl:with-param name="tag">W3Cdate</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:available[@xsi:type='dct:ISO8601']" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
						<xsl:with-param name="att">dct:ISO8601</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:available[not(@xsi:type='dct:W3CDTF') and not(@xsi:type='dct:ISO8601')]" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>
<!--issued-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:issued">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="issued" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:issued[@xsi:type='dct:W3CDTF']" mode="toncs">
						<xsl:with-param name="tag">W3Cdate</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:issued[@xsi:type='dct:ISO8601']" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
						<xsl:with-param name="att">dct:ISO8601</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:issued[not(@xsi:type='dct:W3CDTF') and not(@xsi:type='dct:ISO8601')]" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>
<!--modified -->
			<xsl:variable name="content">
				<xsl:for-each select="dct:modified ">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="modified" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:modified [@xsi:type='dct:W3CDTF']" mode="toncs">
						<xsl:with-param name="tag">W3Cdate</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:modified [@xsi:type='dct:ISO8601']" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
						<xsl:with-param name="att">dct:ISO8601</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:modified [not(@xsi:type='dct:W3CDTF') and not(@xsi:type='dct:ISO8601')]" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>
<!--valid-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:valid">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="valid" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:valid[@xsi:type='dct:W3CDTF']" mode="toncs">
						<xsl:with-param name="tag">W3Cdate</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:valid[@xsi:type='dct:ISO8601']" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
						<xsl:with-param name="att">dct:ISO8601</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:valid[not(@xsi:type='dct:W3CDTF') and not(@xsi:type='dct:ISO8601')]" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>
<!--dateAccepted-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:dateAccepted">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="dateAccepted" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:dateAccepted[@xsi:type='dct:W3CDTF']" mode="toncs">
						<xsl:with-param name="tag">W3Cdate</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:dateAccepted[@xsi:type='dct:ISO8601']" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
						<xsl:with-param name="att">dct:ISO8601</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:dateAccepted[not(@xsi:type='dct:W3CDTF') and not(@xsi:type='dct:ISO8601')]" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>
<!--dateCopyrighted-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:dateCopyrighted">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="dateCopyrighted" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:dateCopyrighted[@xsi:type='dct:W3CDTF']" mode="toncs">
						<xsl:with-param name="tag">W3Cdate</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:dateCopyrighted[@xsi:type='dct:ISO8601']" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
						<xsl:with-param name="att">dct:ISO8601</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:dateCopyrighted[not(@xsi:type='dct:W3CDTF') and not(@xsi:type='dct:ISO8601')]" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>
<!--dateSubmitted-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:dateSubmitted">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="dateSubmitted" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:dateSubmitted[@xsi:type='dct:W3CDTF']" mode="toncs">
						<xsl:with-param name="tag">W3Cdate</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:dateSubmitted[@xsi:type='dct:ISO8601']" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
						<xsl:with-param name="att">dct:ISO8601</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:dateSubmitted[not(@xsi:type='dct:W3CDTF') and not(@xsi:type='dct:ISO8601')]" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>
<!--temporal-->
			<xsl:variable name="content">
				<xsl:for-each select="dct:temporal[@xsi:type='dct:W3CDTF' and @xsi:type='dct:ISO8601']">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="temporal" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dct:temporal[@xsi:type='dct:W3CDTF']" mode="toncs">
						<xsl:with-param name="tag">W3Cdate</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dct:temporal[@xsi:type='dct:ISO8601']" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
						<xsl:with-param name="att">dct:ISO8601</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>
<!--coverage-->
			<xsl:variable name="content">
				<xsl:for-each select="dc:coverage[@xsi:type='dct:W3CDTF' and @xsi:type='dct:ISO8601']">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:if test="string-length($content) > 0">
				<xsl:element name="coverage" namespace="http://ns.nsdl.org/ncs">
					<xsl:apply-templates select="dc:coverage[@xsi:type='dct:W3CDTF']" mode="toncs">
						<xsl:with-param name="tag">W3Cdate</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="dc:coverage[@xsi:type='dct:ISO8601']" mode="toncs">
						<xsl:with-param name="tag">otherDate</xsl:with-param>
						<xsl:with-param name="att">dct:ISO8601</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:if>
		</xsl:element><!--end dates element-->
	
<!--begin coverages element-->
<!--do not need to determine if coverage content is present, because coverages is optional and can be empty-->
<!--coverages elements can be written in any order-->
<!--since the different encoding schemes for point, box etc. in dc:spatial can be entered in a variety of ways (e.g. southlimit=-90.0;westlimit=-110.8;eastlimit=-100.5; versuses placename=chicago;downlimit=10;zunits=kilometers;southlimit=-90.0;westlimit=-110.8;eastlimit=-100.5;) do not write nsdl_ncs elements of coverages.box or coverages.point; just write coverages.spatial, coverages.temporal and coverages.coverage-->
		<xsl:element name="coverages" namespace="http://ns.nsdl.org/ncs">

<!--for dc:coverage, the valid encoding schemes are: dct:Box, dct:Point, dct:TGN, dct:Period, dct:ISO8601, dct:W3CDTF, dct:ISO3166; note that dct:ISO8601 and dct:W3CDTF are handled under dates-->
<!--coverage using dct:Box-->
			<xsl:apply-templates select="dc:coverage[@xsi:type='dct:Box']" mode="toncs">
				<xsl:with-param name="tag">coverage</xsl:with-param>
				<xsl:with-param name="att">dct:Box</xsl:with-param>
			</xsl:apply-templates>
<!--coverage using dct:Point-->
			<xsl:apply-templates select="dc:coverage[@xsi:type='dct:Point']" mode="toncs">
				<xsl:with-param name="tag">coverage</xsl:with-param>
				<xsl:with-param name="att">dct:Point</xsl:with-param>
			</xsl:apply-templates>
<!--coverage using dct:Period-->
			<xsl:apply-templates select="dc:coverage[@xsi:type='dct:Period']" mode="toncs">
				<xsl:with-param name="tag">coverage</xsl:with-param>
				<xsl:with-param name="att">dct:Period</xsl:with-param>
			</xsl:apply-templates>
<!--coverage using dct:TGN-->
			<xsl:apply-templates select="dc:coverage[@xsi:type='dct:TGN']" mode="toncs">
				<xsl:with-param name="tag">coverage</xsl:with-param>
				<xsl:with-param name="att">dct:TGN</xsl:with-param>
			</xsl:apply-templates>
<!--coverage using dct:ISO3166-->
			<xsl:apply-templates select="dc:coverage[@xsi:type='dct:ISO3166']" mode="toncs">
				<xsl:with-param name="tag">coverage</xsl:with-param>
				<xsl:with-param name="att">dct:ISO3166</xsl:with-param>
			</xsl:apply-templates>
<!--coverage-->
			<xsl:apply-templates select="dc:coverage[not(@xsi:type='dct:ISO3166') and not(@xsi:type='dct:TGN') and not(@xsi:type='dct:Period') and not(@xsi:type='dct:Point') and not(@xsi:type='dct:Box') and not(@xsi:type='dct:ISO8601') and not(@xsi:type='dct:W3CDTF')]" mode="toncs">
				<xsl:with-param name="tag">coverage</xsl:with-param>
			</xsl:apply-templates>

<!--for dct:spatial the valid encoding schemes are: dct:Box, dct:Point, dct:TGN, and dct:ISO3166-->
<!--spatial using dct:Box-->
			<xsl:apply-templates select="dct:spatial[@xsi:type='dct:Box']" mode="toncs">
				<xsl:with-param name="tag">spatial</xsl:with-param>
				<xsl:with-param name="att">dct:Box</xsl:with-param>
			</xsl:apply-templates>
<!--spatial using dct:Point-->
			<xsl:apply-templates select="dct:spatial[@xsi:type='dct:Point']" mode="toncs">
				<xsl:with-param name="tag">spatial</xsl:with-param>
				<xsl:with-param name="att">dct:Point</xsl:with-param>
			</xsl:apply-templates>
<!--spatial using dct:TGN-->
			<xsl:apply-templates select="dct:spatial[@xsi:type='dct:TGN']" mode="toncs">
				<xsl:with-param name="tag">spatial</xsl:with-param>
				<xsl:with-param name="att">dct:TGN</xsl:with-param>
			</xsl:apply-templates>
<!--spatial using dct:ISO3166-->
			<xsl:apply-templates select="dct:spatial[@xsi:type='dct:ISO3166']" mode="toncs">
				<xsl:with-param name="tag">spatial</xsl:with-param>
				<xsl:with-param name="att">dct:ISO3166</xsl:with-param>
			</xsl:apply-templates>
<!--spatial-->
			<xsl:apply-templates select="dct:spatial[not(@xsi:type='dct:ISO3166') and not(@xsi:type='dct:TGN') and not(@xsi:type='dct:Point') and not(@xsi:type='dct:Box')]" mode="toncs">
				<xsl:with-param name="tag">spatial</xsl:with-param>
			</xsl:apply-templates>

<!--for dct:temporal, the valid encoding schemes are: dct:Period, dct:ISO8601 and dct:W3CDTF; note that dct:ISO8601 and dct:W3CDTF are handled under dates-->
<!--temporal using dct:Period-->
			<xsl:apply-templates select="dct:temporal[@xsi:type='dct:Period']" mode="toncs">
				<xsl:with-param name="tag">temporal</xsl:with-param>
				<xsl:with-param name="att">dct:Period</xsl:with-param>
			</xsl:apply-templates>
<!--temporal-->
			<xsl:apply-templates select="dct:temporal[not(@xsi:type='dct:Period') and not(@xsi:type='dct:ISO8601') and not(@xsi:type='dct:W3CDTF')]" mode="toncs">
				<xsl:with-param name="tag">temporal</xsl:with-param>
			</xsl:apply-templates>
		</xsl:element><!--end coverages element-->
	</record><!--end record element-->
	</xsl:template>



<!--TEMPLATES for nsdl_dc to nsdl_ncs-->
<!-- ****************************************-->
<!--NODE with selected controlled vocabularies: writes tag sets that make use of selected controlled vocabularies (that are available as an XML schema at a URL)-->
<!--TONCS (NODE) without controlled vocabularies: writes all other tag sets that do not use a controlled vocabulary -->
<!--LANGUAGE: writes the general.languages tag set-->
<!--CONFORMSTO: writes the educational.standards tag set-->

<!--NODE TEMPLATE with selected controlled vocabularies-->
	<xsl:template match="node()[@xsi:type='nsdl_dc:GEM'] | node()[@xsi:type='nsdl_dc:NSDLEdLevel'] | node()[@xsi:type='dct:DCMIType'] | node()[@xsi:type='nsdl_dc:NSDLType'] | ieee:interactivityType | ieee:interactivityLevel | node()[@xsi:type='dct:IMT'] | node()[@xsi:type='dct:ISO639-2'] | node()[@xsi:type='dct:RFC3066'] | node()[@xsi:type='nsdl_dc:NSDLAudience'] | node()[@xsi:type='nsdl_dc:NSDLAccess']">

<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="url"/>
		<xsl:param name="tag"/>
		<xsl:param name="string" select="."/>
		<xsl:param name="altTag"/>

		<xsl:if test="string-length($string) > 0">
<!--if string exists test whether it is part of the controlled vocabulary-->	
<!--need to create a variable (match) to test if there was a vocabulary match after cycling through the vocabulary comparisons-->
<!--if there is no vocabulary match do not write the (nsdlSubject, nsdlEdLevel) elements; rather write the (otherSubject, otherEdLevel) elements-->	
			<xsl:variable name="match">
				<xsl:for-each select="document($url)//xs:restriction/xs:enumeration">
					<xsl:variable name="vocab">
						<xsl:value-of select="@value"/>
					</xsl:variable>
					<xsl:if test="$string = $vocab">
						<xsl:value-of select="$string"/>
					</xsl:if>
				</xsl:for-each>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="string-length($match)>0">
					<xsl:element name="{$tag}" namespace="http://ns.nsdl.org/ncs">
						<xsl:value-of select="$match"/>
					</xsl:element>	
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="toncs">
						<xsl:with-param name="tag">
							<xsl:value-of select="$altTag"/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

<!--	do not need a separate <xsl:template match="node()[@xsi:type='dct:LCC'] | node()[@xsi:type='dct:MESH'] | node()[@xsi:type='dct:LCSH'] | node()[@xsi:type='dct:DDC'] | node()[@xsi:type='dct:UDC'] | dc:subject[not(@xsi:type='dct:UDC') and not(@xsi:type='dct:DDC') and not(@xsi:type='dct:LCSH') and not(@xsi:type='dct:MESH') and not(@xsi:type='dct:LCC') and not(@xsi:type='nsdl_dc:GEM')]">  template tag-->


<!--TONCS (NODE) TEMPLATE without controlled vocabularies-->
	<xsl:template match="node()" name="toncs" mode="toncs">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="att"/>
		<xsl:param name="string" select="."/>

		<xsl:if test="string-length($string) > 0">
			<xsl:element name="{$tag}" namespace="http://ns.nsdl.org/ncs">
				<xsl:if test="string-length($att) > 0">
					<xsl:attribute name="type">
						<xsl:value-of select="$att"/>
					</xsl:attribute>	
				</xsl:if>
				<xsl:value-of select="$string"/>
			</xsl:element>	
		</xsl:if>
	</xsl:template>


<!--CONFORMSTO TEMPLATE-->	
	<xsl:template match="dct:conformsTo">
		<xsl:param name="string" select="."/>
		<xsl:if test="$string">
<!--if string exists test whether the string is of type dct:URI or not-->
<!--if string is of type dct:URI, then determine whether to write standards.asnID, standards.url or standards.NSESstandard-->
<!--if string is not of type dct:URI, the write standards.otherStandard-->
			<xsl:choose>
				<xsl:when test="./@xsi:type='dct:URI' ">
					<xsl:choose>
					<!--test whether the URL uses ASN text for the beginning part of the URL or not-->
					<!--if ASN is used, check to see if it is an NSES standard-->
						<xsl:when test="contains($string, $asnText)">
							<xsl:variable name="match">
								<xsl:for-each select="document($asnIdsURL)//mapping">
									<xsl:variable name="vocab">
										<xsl:value-of select="@asnIdentifier"/>
									</xsl:variable>
									<xsl:if test="$string = $vocab">
										<xsl:value-of select="$string"/>
									</xsl:if>
								</xsl:for-each>
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="$match">
									<xsl:element name="NSESstandard" namespace="http://ns.nsdl.org/ncs">
										<xsl:value-of select="document($asnIdsURL)//mapping[@asnIdentifier=$match]"/>
									</xsl:element>	
								</xsl:when>
								<xsl:otherwise>
									<xsl:element name="asnID" namespace="http://ns.nsdl.org/ncs">
										<xsl:value-of select="$string"/>
									</xsl:element>	
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:element name="url" namespace="http://ns.nsdl.org/ncs">
								<xsl:value-of select="$string"/>
							</xsl:element>						
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="toncs">
						<xsl:with-param name="tag">otherStandard</xsl:with-param>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>