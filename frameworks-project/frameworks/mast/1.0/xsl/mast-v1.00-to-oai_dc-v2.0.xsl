<?xml version="1.0"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:mast="http://www.dlsciences.org/frameworks/mast"
	xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="xsi mast"
    version="1.0">

<!--PURPOSE: To transform mast version 1.00 metadata records to the oai_dc 2.0 format at http://www.openarchives.org/OAI/2.0/oai_dc.xsd-->
<!--CREDITS: Date created: 2008-02-29 by Katy Ginger, University Corporation for Atmospheric Research (UCAR)-->
<!--LICENSE INFORMATION: See the end of this file.-->

	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>


<!--VARIABLES used throughout the transform-->
	<xsl:variable name="asnText">http://purl.org/ASN/</xsl:variable>
	
<!--TRANSFORMATION CODE-->
	<xsl:template match="*|/">
		<xsl:apply-templates select="mast:record"/>
	</xsl:template>

<!--creating ROOT element of the new instance XML document to be created-->
	<xsl:template match="mast:record">
		<oai_dc:dc xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/">

<!--dc:identifier using url; doesn't really need a template but easy to do-->
		<xsl:apply-templates select="mast:general/mast:url" mode="process"> 
			<xsl:with-param name="tag">dc:identifier</xsl:with-param>
		</xsl:apply-templates>

<!--dc:title using title-->
		<xsl:apply-templates select="mast:general/mast:title" mode="process">
			<xsl:with-param name="tag">dc:title</xsl:with-param>
		</xsl:apply-templates>

<!--dc:description using description-->
		<xsl:apply-templates select="mast:general/mast:description" mode="process">
			<xsl:with-param name="tag">dc:description</xsl:with-param>
		</xsl:apply-templates>

<!--dc:description using essential resource-->
		<xsl:apply-templates select="mast:educational/mast:essentials/mast:essentialResource" mode="process">
			<xsl:with-param name="tag">dc:description</xsl:with-param>
		</xsl:apply-templates>

<!--dc:subject using gemSubject-->
		<xsl:apply-templates select="mast:general/mast:subjects/mast:gemSubject" mode="process">
			<xsl:with-param name="tag">dc:subject</xsl:with-param>
		</xsl:apply-templates>

<!--dc:subject using otherSubject-->
		<xsl:apply-templates select="mast:general/mast:subjects/mast:otherSubject" mode="process">
			<xsl:with-param name="tag">dc:subject</xsl:with-param>
		</xsl:apply-templates>

<!--dc:language using language-->
		<xsl:apply-templates select="mast:general/mast:language" mode="process">
			<xsl:with-param name="tag">dc:language</xsl:with-param>
		</xsl:apply-templates>

<!--dc:educationLevel using gemEdLevel-->
<!--since oai_dc does not contain an educationl level element, this template is commented out-->
<!--
		<xsl:apply-templates select="mast:educational/mast:educationLevels/mast:gemEdLevel" mode="process">
			<xsl:with-param name="tag">dc:educationLevel</xsl:with-param>
		</xsl:apply-templates>
-->		
<!--dc:educationLevel using otherEdLevel-->
<!--since oai_dc does not contain an educationl level element, this template is commented out-->
<!--
		<xsl:apply-templates select="mast:educational/mast:educationLevels/mast:otherEdLevel" mode="process"> 
			<xsl:with-param name="tag">dc:educationLevel</xsl:with-param>
		</xsl:apply-templates>
-->

<!--dc:type using dcmiType-->
		<xsl:apply-templates select="mast:educational/mast:types/mast:dcmiType" mode="process"> 
			<xsl:with-param name="tag">dc:type</xsl:with-param>
		</xsl:apply-templates>

<!--dc:type using gemType-->
		<xsl:apply-templates select="mast:educational/mast:types/mast:gemType" mode="process"> 
			<xsl:with-param name="tag">dc:type</xsl:with-param>
		</xsl:apply-templates>

<!--dc:type using otherType-->
		<xsl:apply-templates select="mast:educational/mast:types/mast:otherType" mode="process"> 
			<xsl:with-param name="tag">dc:type</xsl:with-param>
		</xsl:apply-templates>

<!--dc:audience using audience-->
<!--since oai_dc does not contain an audience element, this template is commented out-->
<!--
		<xsl:apply-templates select="mast:educational/mast:audiences/mast:audience" mode="process"> 
			<xsl:with-param name="tag">dcterms:audience</xsl:with-param>
		</xsl:apply-templates>
-->
<!--dc:instructionalMethod-->
<!--since oai_dc does not contain an instructional method element, this template is commented out-->
<!--
		<xsl:apply-templates select="mast:educational/mast:instructionalMethods/mast:gemMethod" mode="process"> 
			<xsl:with-param name="tag">dcterms:instructionalMethod</xsl:with-param>
		</xsl:apply-templates>
-->
<!--dc:instructionalMethod-->
<!--since oai_dc does not contain an instructional method element, this template is commented out-->
<!--
		<xsl:apply-templates select="mast:educational/mast:instructionalMethods/mast:otherMethod" mode="process"> 
			<xsl:with-param name="tag">dcterms:instructionalMethod</xsl:with-param>
		</xsl:apply-templates>
-->
<!--dc:relation (think conformsTo) using asnID-->
<!--since oai_dc does not contain an conforms to educational standard element, this template is commented out-->
<!--
		<xsl:apply-templates select="mast:educational/mast:standards/mast:asnID" mode="process"> 
			<xsl:with-param name="tag">dc:relation</xsl:with-param>
		</xsl:apply-templates>
-->
<!--dc:relation (think conformsTo) using standard-->
<!--since oai_dc does not contain a conforms to educational standard element, this template is commented out-->
<!--
		<xsl:apply-templates select="mast:educational/mast:standards/mast:standard" mode="process"> 
			<xsl:with-param name="tag">dc:relation</xsl:with-param>
		</xsl:apply-templates>
-->

<!--dc:contributor using contributor-->
		<xsl:apply-templates select="mast:contributions/mast:contributors/mast:contributor" mode="process"> 
			<xsl:with-param name="tag">dc:contributor</xsl:with-param>
		</xsl:apply-templates>

<!--dc:creator using creator-->
		<xsl:apply-templates select="mast:contributions/mast:creators/mast:creator" mode="process"> 
			<xsl:with-param name="tag">dc:creator</xsl:with-param>
		</xsl:apply-templates>

<!--dc:publisher using publisher-->
		<xsl:apply-templates select="mast:contributions/mast:publishers/mast:publisher" mode="process"> 
			<xsl:with-param name="tag">dc:publisher</xsl:with-param>
		</xsl:apply-templates>

<!--dc:rights using rights-->
		<xsl:apply-templates select="mast:general/mast:rights" mode="process"> 
			<xsl:with-param name="tag">dc:rights</xsl:with-param>
		</xsl:apply-templates>

<!--dc:relation using relation-->
		<xsl:apply-templates select="mast:general/mast:relation" mode="process"> 
			<xsl:with-param name="tag">dc:relation</xsl:with-param>
		</xsl:apply-templates>

<!--dc:source using source-->
		<xsl:apply-templates select="mast:general/mast:source" mode="process"> 
			<xsl:with-param name="tag">dc:source</xsl:with-param>
		</xsl:apply-templates>

<!--dc:format using format-->
		<xsl:apply-templates select="mast:general/mast:format" mode="process"> 
			<xsl:with-param name="tag">dc:format</xsl:with-param>
		</xsl:apply-templates>

<!--dc:date using date-->
		<xsl:apply-templates select="mast:general/mast:date" mode="process"> 
			<xsl:with-param name="tag">dc:date</xsl:with-param>
		</xsl:apply-templates>

<!--dc:coverage using coverage-->
		<xsl:apply-templates select="mast:general/mast:coverage" mode="process"> 
			<xsl:with-param name="tag">dc:coverage</xsl:with-param>
		</xsl:apply-templates>

<!--dc:coverage using duration-->
		<xsl:apply-templates select="mast:educational/mast:essentials/mast:duration" mode="process"> 
			<xsl:with-param name="tag">dc:coverage</xsl:with-param>
		</xsl:apply-templates>

		</oai_dc:dc><!--end oai_dc:dc element-->
	</xsl:template>



<!--TEMPLATES-->
<!-- ****************************************-->
<!--PROCESS:writes all tag sets that are not a content standard, box or point-->

<!--PROCESS template-->
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
</xsl:stylesheet>
<!--LICENSE AND COPYRIGHT
The contents of this file are subject to the Educational Community License v1.0 (the "License"); you may not use this file except in compliance with the License. You should obtain a copy of the License from http://www.opensource.org/licenses/ecl1.php. Files distributed under the License are distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the specific language governing rights and limitations under the License. Copyright 2002-2009 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR). All rights reserved.-->