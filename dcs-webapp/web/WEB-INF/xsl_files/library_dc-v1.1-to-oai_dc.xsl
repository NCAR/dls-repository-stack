<?xml version="1.0"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:lib="http://nldr.library.ucar.edu/metadata/library_dc"
	xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="xsi lib"
    version="1.1">

<!--PURPOSE-->
<!-- **************************************-->
<!--To transform library_dc version 1.1 metadata records to the oai_dc format.-->

<!--HISTORY-->
<!-- **************************************-->
<!---->

<!--LICENSE INFORMATION and CREDITS-->
<!-- *****************************************************-->
<!--Date created: 2009-02-23 by Katy Ginger, University Corporation for Atmospheric Research (UCAR)-->
<!--License information:
		Copyright (c) 2009 University Corporation for Atmospheric Research (UCAR)
		P.O. Box 3000, Boulder, CO 80307, United States of America
		All rights reserved-->

	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>

<!--TRANSFORMATION CODE-->
<!-- **************************************-->
	<xsl:template match="*|/">
		<xsl:apply-templates select="lib:record"/>
	</xsl:template>

<!--TRANSFORMATION CODE for library_dc to oai_dc-->
<!-- ********************************************************-->
	<xsl:template match="lib:record">
		<oai_dc:dc xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/">

<!--dc:identifier using URL-->
		<xsl:apply-templates select="lib:URL" mode="process"> 
			<xsl:with-param name="tag">dc:identifier</xsl:with-param>
		</xsl:apply-templates>

<!--dc:identifier using identifier-->
		<xsl:apply-templates select="lib:identifier" mode="process"> 
			<xsl:with-param name="tag">dc:identifier</xsl:with-param>
		</xsl:apply-templates>

<!--dc:title using title-->
		<xsl:apply-templates select="lib:title" mode="process">
			<xsl:with-param name="tag">dc:title</xsl:with-param>
		</xsl:apply-templates>

<!--dc:title using altTitle-->
		<xsl:apply-templates select="lib:altTitle" mode="process">
			<xsl:with-param name="tag">dc:title</xsl:with-param>
		</xsl:apply-templates>

<!--dc:title using earlierTitle-->
		<xsl:apply-templates select="lib:earlierTitle" mode="process">
			<xsl:with-param name="tag">dc:title</xsl:with-param>
		</xsl:apply-templates>

<!--dc:title using laterTitle-->
		<xsl:apply-templates select="lib:laterTitle" mode="process">
			<xsl:with-param name="tag">dc:title</xsl:with-param>
		</xsl:apply-templates>

<!--dc:description using description-->
		<xsl:apply-templates select="lib:description" mode="process">
			<xsl:with-param name="tag">dc:description</xsl:with-param>
		</xsl:apply-templates>

<!--dc:description using issue-->
<!--since volume and issue only appear once, there is no need to use apply-templates-->
		<xsl:if test="string-length(lib:issue) > 0">
			<xsl:element name="dc:description">
				<xsl:value-of select="concat('Issue: ', lib:issue)"/>
			</xsl:element>
		</xsl:if>

<!--dc:description using volume-->
<!--since volume and issue only appear once, there is no need to use apply-templates-->
		<xsl:if test="string-length(lib:volume) > 0">
			<xsl:element name="dc:description">
				<xsl:value-of select="concat('Volume: ', lib:volume)"/>
			</xsl:element>
		</xsl:if>

<!--dc:subject using subject-->
		<xsl:apply-templates select="lib:subject" mode="process">
			<xsl:with-param name="tag">dc:subject</xsl:with-param>
		</xsl:apply-templates>

<!--dc:language using language-->
		<xsl:apply-templates select="lib:language" mode="process">
			<xsl:with-param name="tag">dc:language</xsl:with-param>
		</xsl:apply-templates>

<!--dc:type using libraryType-->
		<xsl:apply-templates select="lib:libraryType" mode="process"> 
			<xsl:with-param name="tag">dc:type</xsl:with-param>
		</xsl:apply-templates>

<!--dc:type using otherType-->
		<xsl:apply-templates select="lib:otherType" mode="process"> 
			<xsl:with-param name="tag">dc:type</xsl:with-param>
		</xsl:apply-templates>

<!--dc:contributor using contributor-->
		<xsl:apply-templates select="lib:contributor" mode="process"> 
			<xsl:with-param name="tag">dc:contributor</xsl:with-param>
		</xsl:apply-templates>

<!--dc:contributor using instName-->
		<xsl:apply-templates select="lib:instName" mode="process"> 
			<xsl:with-param name="tag">dc:contributor</xsl:with-param>
		</xsl:apply-templates>

<!--dc:contributor using instDivision-->
		<xsl:apply-templates select="lib:instDivision" mode="process"> 
			<xsl:with-param name="tag">dc:contributor</xsl:with-param>
		</xsl:apply-templates>

<!--dc:creator using creator-->
		<xsl:apply-templates select="lib:creator" mode="process"> 
			<xsl:with-param name="tag">dc:creator</xsl:with-param>
		</xsl:apply-templates>

<!--dc:publisher using publisher-->
		<xsl:apply-templates select="lib:publisher" mode="process"> 
			<xsl:with-param name="tag">dc:publisher</xsl:with-param>
		</xsl:apply-templates>

<!--dc:rights using rights-->
		<xsl:apply-templates select="lib:rights" mode="process"> 
			<xsl:with-param name="tag">dc:rights</xsl:with-param>
		</xsl:apply-templates>

<!--dc:relation using relation-->
		<xsl:apply-templates select="lib:relation" mode="relation"> 
			<xsl:with-param name="tag">dc:relation</xsl:with-param>
		</xsl:apply-templates>

<!--dc:source using source-->
		<xsl:apply-templates select="lib:source" mode="process"> 
			<xsl:with-param name="tag">dc:source</xsl:with-param>
		</xsl:apply-templates>

<!--dc:format using format-->
		<xsl:apply-templates select="lib:format" mode="process"> 
			<xsl:with-param name="tag">dc:format</xsl:with-param>
		</xsl:apply-templates>

<!--dc:date using date-->
		<xsl:apply-templates select="lib:date" mode="process"> 
			<xsl:with-param name="tag">dc:date</xsl:with-param>
		</xsl:apply-templates>

<!--dc:date using dateDigitized-->
		<xsl:apply-templates select="lib:dateDigitized" mode="process"> 
			<xsl:with-param name="tag">dc:date</xsl:with-param>
		</xsl:apply-templates>

<!--dc:coverage using coverage-->
		<xsl:apply-templates select="lib:coverage" mode="process"> 
			<xsl:with-param name="tag">dc:coverage</xsl:with-param>
		</xsl:apply-templates>

		</oai_dc:dc><!--end oai_dc:dc element-->
	</xsl:template>



<!--TEMPLATES-->
<!-- ****************************************-->
<!--PROCESS template writs all tag sets-->
	<xsl:template match="node()" name="process" mode="process">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="att"/>
		<xsl:param name="string" select="."/>
		<xsl:if test="string-length($string) > 0">
			<xsl:element name="{$tag}">
				<xsl:if test="string-length($att) > 0">
					<xsl:attribute name="xsi:type">
						<xsl:value-of select="$att"/>
					</xsl:attribute>	
				</xsl:if>
				<xsl:value-of select="$string" /> 
			</xsl:element>
		</xsl:if>
	</xsl:template>	

	<xsl:template match="node()" name="relation" mode="relation">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="att"/>
		<xsl:param name="string1" select="@url"/>
		<xsl:param name="string2" select="@title"/>
		<xsl:if test="string-length($string1) > 0">
			<xsl:element name="{$tag}">
				<xsl:if test="string-length($att) > 0">
					<xsl:attribute name="xsi:type">
						<xsl:value-of select="$att"/>
					</xsl:attribute>	
				</xsl:if>
				<xsl:value-of select="$string1" /> 
			</xsl:element>
		</xsl:if>
		<xsl:if test="string-length($string2) > 0">
			<xsl:element name="{$tag}">
				<xsl:if test="string-length($att) > 0">
					<xsl:attribute name="xsi:type">
						<xsl:value-of select="$att"/>
					</xsl:attribute>	
				</xsl:if>
				<xsl:value-of select="$string2" /> 
			</xsl:element>
		</xsl:if>
	</xsl:template>	


</xsl:stylesheet>
<!--LICENSE AND COPYRIGHT
The contents of this file are subject to the Educational Community License v1.0 (the "License"); you may not use this file except in compliance with the License. You should obtain a copy of the License from http://www.opensource.org/licenses/ecl1.php. Files distributed under the License are distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the specific language governing rights and limitations under the License. Copyright 2002-2009 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR). All rights reserved.-->