<?xml version="1.0" encoding="UTF-8"?>
<!-- =============================================================  
 "nsdl_dc-v1.02-to-nsdl_dc-v1.03.xsl" stylesheet 

- converts nsdl_dc 1.02 metadata records to nsdl_dc 1.03 metadata records
- upgrages old 1.02 education level vocab terms to new 1.03 terms
 
Author:  Katy Ginger 
updated: 2007-08-09
Created: 2007-08-08
================================================================ -->

<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:nsdl_dcold="http://ns.nsdl.org/nsdl_dc_v1.02/"
	xmlns:nsdl_dc="http://ns.nsdl.org/nsdl_dc_v1.03/"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dct="http://purl.org/dc/terms/" 
	xmlns:ieee="http://www.ieee.org/xsd/LOMv1p0"
    exclude-result-prefixes="xs nsdl_dcold "
    version="1.0">

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
<!--handle each namespace separately-->
<!--apply a template to handle the changes in the educationLevel controlled vocabulary between nsdl_dc version 1.02 and nsdl_dc version 1.03-->
<!--then apply templates to rest of document in order to copy it over as is but don't re-apply a template to dct:educationLevel-->

<!--nsdl_dc namespace-->
	<xsl:template match="nsdl_dcold:nsdl_dc">
		<nsdl_dc:nsdl_dc schemaVersion="1.03.000" xmlns:nsdl_dc="http://ns.nsdl.org/nsdl_dc_v1.03/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dct="http://purl.org/dc/terms/" xmlns:ieee="http://www.ieee.org/xsd/LOMv1p0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://ns.nsdl.org/nsdl_dc_v1.03/ http://ns.nsdl.org/schemas/nsdl_dc/nsdl_dc_v1.03.xsd">
			<xsl:apply-templates select="dct:educationLevel[@xsi:type='nsdl_dc:NSDLEdLevel']" mode="edu"/>
			<xsl:apply-templates select=" @* | node()[not(@xsi:type='nsdl_dc:NSDLEdLevel' ) ]"/>
		</nsdl_dc:nsdl_dc>
	</xsl:template>

<!--dc namespace-->
	<xsl:template match="*[namespace-uri() = 'http://purl.org/dc/elements/1.1/']">
		<xsl:element name="{name()}" namespace="http://purl.org/dc/elements/1.1/">

			<xsl:apply-templates select="@* | node()" />
		</xsl:element>
	</xsl:template>

<!--ieee namespace-->
	<xsl:template match="*[namespace-uri() = 'http://www.ieee.org/xsd/LOMv1p0']">
		<xsl:element name="{name()}" namespace="http://www.ieee.org/xsd/LOMv1p0">
			<xsl:apply-templates select="@* | node()" />
		</xsl:element>
	</xsl:template>

<!--dct namespace-->
	<xsl:template match="*[namespace-uri() = 'http://purl.org/dc/terms/']">
		<xsl:element name="{name()}" namespace="http://purl.org/dc/terms/">
			<xsl:apply-templates select="@* | node()" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*" />
		</xsl:copy>
	 </xsl:template>

<!--Education Level Changes-->
<!--Change nsdl_dc v1.02 terms Grades Pre-K to 12, Graduate and Postsecondary  because they do no exist in nsdl_dc 1.03-->
<!--Graduate goes to Higher Education, Graduate/Professional-->
<!--Postsecondary goes to Higher Education-->
<!--Grades Pre-K to 12 goes to Pre-Kindergarten, Elementary School, Middle School, High School-->
	<xsl:template match="node()" mode="edu">
		<xsl:param name="newEdLevel"/>
		<xsl:param name="string" select="."/>
		<xsl:choose>
			<xsl:when test="$string = 'Graduate' ">
				<xsl:apply-templates mode="writeEdLevel">
					<xsl:with-param name="newEdLevel">Higher Education</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates mode="writeEdLevel">
					<xsl:with-param name="newEdLevel">Graduate/Professional</xsl:with-param>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="$string = 'Postsecondary' ">
				<xsl:apply-templates mode="writeEdLevel">
					<xsl:with-param name="string">Higher Education</xsl:with-param>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="$string = 'Grades Pre-K to 12' ">
				<xsl:apply-templates mode="writeEdLevel">
					<xsl:with-param name="newEdLevel">Pre-Kindergarten</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates mode="writeEdLevel">
					<xsl:with-param name="newEdLevel">Elementary School</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates mode="writeEdLevel">
					<xsl:with-param name="newEdLevel">Middle School</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates mode="writeEdLevel">
					<xsl:with-param name="newEdLevel">High School</xsl:with-param>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates mode="writeEdLevel">
					<xsl:with-param name="newEdLevel">
						<xsl:value-of select="$string"/>
					</xsl:with-param>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="node()" mode="writeEdLevel">
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="newEdLevel"/>
		<xsl:element name="dct:educationLevel">
			<xsl:attribute name="xsi:type">nsdl_dc:NSDLEdLevel</xsl:attribute>
			<xsl:value-of select="$newEdLevel"/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
