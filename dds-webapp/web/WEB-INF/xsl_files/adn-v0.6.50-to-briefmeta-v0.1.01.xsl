<?xml version="1.0"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:d="http://adn.dlese.org"
    exclude-result-prefixes="xsi d" 
    version="1.0">
    
<!--ORGANIZATION OF THIS FILE-->
<!-- **************************************-->
<!--The top half of this file, before the break with 5 lines of blank comments, is the apply-templates logic. This section is organized in document order of an ADN  item-level metadata record. The bottom half, after the break with 5 lines of blank comments, are the templates. The templates are organized in alphabetical order.-->

<!--ASSUMPTIONS-->
<!-- **************************************-->
<!--1. The transform is run over only ADN version 0.6.50 records a collection considers to be accessioned-->
<!--2. Unless otherwise indicated in this stylesheet, the transform applies to both ADN online and offline resources-->


	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>

<!--VARIABLES used throughout the transform-->
<!-- **********************************************************-->
<!--variable for adding a line return-->
	<xsl:variable name="newline">
		<xsl:text>
		</xsl:text>
	</xsl:variable>


	<xsl:template match="d:itemRecord">
		<xsl:text disable-output-escaping="yes">&lt;briefRecord xmlns=&quot;http://www.dlese.org/Metadata/briefmeta&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xsi:schemaLocation=&quot;http://www.dlese.org/Metadata/briefmeta http://www.dlese.org/Metadata/briefmeta/0.1.01/brief-record.xsd&quot;&gt;</xsl:text>
		<xsl:value-of select="$newline"/>

<!--title-->
		<xsl:element name="title">
			<xsl:value-of select="d:general/d:title"/>
		</xsl:element>	
		<xsl:value-of select="$newline"/>

<!--description-->
<!--determine if the resource is online or offline; if offline, concatenate ADN general.description and ADN technical.offline.accessInformation-->
		<xsl:element name="description">
			<xsl:value-of select="d:general/d:description"/>
		</xsl:element>

<!--record ID-->
<!-- 		<xsl:element name="recordId">
			<xsl:value-of select="d:metaMetadata/d:catalogEntries/d:catalog/@entry"/>
		</xsl:element>	 -->	

<!--url - using ADN primary url-->
<!--only online ADN resource will have a dc:identifier - dct:URI tag-->
		<xsl:element name="url">
			<xsl:value-of select="d:technical/d:online/d:primaryURL"/>
		</xsl:element>

<!--subject - DLESE-->
<!--	variable for ADN general.subjects - in case the only value is the default value-->
			<xsl:variable name="allsubjects">
				<xsl:for-each select="d:general/d:subjects/d:subject">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
	
<!--test for the presence of content in DLESE-IMS general.keywords; do this to prevent the ADN:general.keywords tag from appearing if it doesn't need to-->
			<xsl:choose >
				<xsl:when test="string($allsubjects)='DLESE:To be supplied'"/>
				<xsl:otherwise>
					<xsl:element name="subjects">
						<xsl:apply-templates select="d:general/d:subjects/d:subject" mode="DLESE"/>
						<!--see template SUBJECT mode=DLESE-->
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>

<!--subject - from keywords-->
<!--		<xsl:apply-templates select="d:general/d:keywords/d:keyword" mode="keywords"/>-->
		<!--see template SUBJECT mode=KEYWORDS-->


<!--gradeRanges-->
<!--	variable for ADN educational.audeinces.audience.gradeRange - in case the only value is the default value-->
			<xsl:variable name="allgradeRanges">
				<xsl:for-each select="d:educational/d:audiences/d:audience/d:gradeRange">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:variable>
	
<!--test for the presence of content in DLESE-IMS general.keywords; do this to prevent the ADN:general.keywords tag from appearing if it doesn't need to-->
			<xsl:choose >
				<xsl:when test="string($allgradeRanges)='DLESE:To be supplied'"/>
				<xsl:when test="string($allgradeRanges)='DLESE:Not applicable'"/>
				<xsl:otherwise>
					<xsl:element name="gradeRanges">
						<xsl:apply-templates select="d:educational/d:audiences/d:audience/d:gradeRange"/>
						<!--see template RESOURCETYPE -->
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>


<!--resourceTypes-->
<!--no vocabulary mapping is necessary-->
<!--determine if the ADN metadata record refers to an online or offline resource-->
		<xsl:choose>
			<xsl:when test="string-length(d:technical/d:offline)>0">
				<xsl:element name="resourceTypes">
					<xsl:element name="resourceType">
						<xsl:text>DLESE:Physical object</xsl:text>
					</xsl:element>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>

<!--	variable for ADN educational.resourceTypes.resourceType - in case the only value is the default value-->
				<xsl:variable name="allresourceTypes">
					<xsl:for-each select="d:educational/d:resourceTypes/d:resourceType">
						<xsl:value-of select="."/>
					</xsl:for-each>
				</xsl:variable>
	
<!--test for the presence of content in DLESE-IMS general.keywords; do this to prevent the ADN:general.keywords tag from appearing if it doesn't need to-->
				<xsl:choose >
					<xsl:when test="string($allresourceTypes)='DLESE:To be supplied'"/>
					<xsl:otherwise>
						<xsl:element name="resourceTypes">
							<xsl:apply-templates select="d:educational/d:resourceTypes/d:resourceType"/>
							<!--see template RESOURCETYPE -->
						</xsl:element>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>

<!--end briefRecord-->
		<xsl:value-of select="$newline"/>
		<xsl:text disable-output-escaping="yes">&lt;/briefRecord&gt;</xsl:text>
	</xsl:template>


<!--begin TEMPLATES TO APPLY. In alphabetical order by ADN field name-->
<!--**********************************************************************************************-->
<!--**********************************************************************************************-->
<!--**********************************************************************************************-->
<!--**********************************************************************************************-->
<!--**********************************************************************************************-->
<!--**********************************************************************************************-->

<!--GRADERANGE template-->
	<xsl:template match="d:gradeRange">
		<xsl:choose>
			<xsl:when test="contains(., 'supplied') or contains(., 'Not applicable')"/>
			<xsl:otherwise>
				<xsl:element name="gradeRange">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


<!--RESOURCETYPE template-->
	<xsl:template match="d:resourceType">
		<xsl:choose>
			<xsl:when test="contains(.,'supplied')"/>
			<xsl:otherwise>
				<xsl:element name="resourceType">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

<!--SUBJECT template mode=DLESE-->
	<xsl:template match="d:subject" mode="DLESE">
		<xsl:choose>
			<xsl:when test="contains(.,'supplied')"/>
			<xsl:otherwise>
				<xsl:element name="subject">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
</xsl:template>			

<!--SUBJECT template mode=KEYWORDS-->
	<xsl:template match="d:keyword" mode="keywords">
		<xsl:element name="subject">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>			

</xsl:stylesheet>
