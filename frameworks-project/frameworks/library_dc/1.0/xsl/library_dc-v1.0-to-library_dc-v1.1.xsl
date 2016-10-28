<?xml version="1.0"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:lib="http://www.dlsciences.org/frameworks/library_dc"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="xsi lib dc xs"
    version="1.1">

<!--PURPOSE-->
<!-- **************************************-->
<!--To transform metadata records in library_dc version 1.0 to the library_dc version 1.1 format.-->

<!--HISTORY-->
<!-- **************************************-->
<!---->

<!--LICENSE INFORMATION and CREDITS-->
<!-- *****************************************************-->
<!--Date created: 2009-02-25 by Katy Ginger, University Corporation for Atmospheric Research (UCAR)-->
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

<!--TRANSFORMATION CODE for library_dc 1.0 to library_dc 1.1-->
<!-- ***********************************************************************-->
	<xsl:template match="lib:record">
		<record xsi:schemaLocation="http://nldr.library.ucar.edu/metadata/library_dc http://nldr.library.ucar.edu/metadata/library_dc/1.1/schemas/library_dc.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://nldr.library.ucar.edu/metadata/library_dc">

<!--recordID-->
		<xsl:apply-templates select="lib:recordID" mode="process"> 
			<xsl:with-param name="tag">recordID</xsl:with-param>
		</xsl:apply-templates>

<!--dateCataloged-->
		<xsl:apply-templates select="lib:dateCataloged" mode="process"> 
			<xsl:with-param name="tag">dateCataloged</xsl:with-param>
		</xsl:apply-templates>

<!--URL-->
		<xsl:apply-templates select="lib:URL" mode="process"> 
			<xsl:with-param name="tag">URL</xsl:with-param>
		</xsl:apply-templates>

<!--issue-->
		<xsl:apply-templates select="lib:issue" mode="process"> 
			<xsl:with-param name="tag">issue</xsl:with-param>
		</xsl:apply-templates>

<!--source-->
		<xsl:apply-templates select="dc:source" mode="process"> 
			<xsl:with-param name="tag">source</xsl:with-param>
		</xsl:apply-templates>

<!--title-->
		<xsl:apply-templates select="dc:title" mode="process">
			<xsl:with-param name="tag">title</xsl:with-param>
		</xsl:apply-templates>

<!--altTitle-->
		<xsl:apply-templates select="lib:altTitle" mode="process">
			<xsl:with-param name="tag">altTitle</xsl:with-param>
		</xsl:apply-templates>

<!--creator-->
		<xsl:apply-templates select="dc:creator" mode="process"> 
			<xsl:with-param name="tag">creator</xsl:with-param>
		</xsl:apply-templates>

<!--contributor-->
		<xsl:apply-templates select="dc:contributor" mode="process"> 
			<xsl:with-param name="tag">contributor</xsl:with-param>
		</xsl:apply-templates>

<!--description-->
		<xsl:apply-templates select="dc:description" mode="process">
			<xsl:with-param name="tag">description</xsl:with-param>
		</xsl:apply-templates>

<!--date-->
		<xsl:apply-templates select="dc:date" mode="process"> 
			<xsl:with-param name="tag">date</xsl:with-param>
		</xsl:apply-templates>

<!--dateDigitized replaces date_digitized-->
		<xsl:apply-templates select="lib:date_digitized" mode="process"> 
			<xsl:with-param name="tag">dateDigitized</xsl:with-param>
		</xsl:apply-templates>

<!--subject-->
		<xsl:apply-templates select="dc:subject" mode="process">
			<xsl:with-param name="tag">subject</xsl:with-param>
		</xsl:apply-templates>

<!--instName-->
		<xsl:apply-templates select="lib:instName" mode="process"> 
			<xsl:with-param name="tag">instName</xsl:with-param>
		</xsl:apply-templates>

<!--instDivision-->
		<xsl:apply-templates select="lib:instDivision" mode="process"> 
			<xsl:with-param name="tag">instDivision</xsl:with-param>
		</xsl:apply-templates>

<!--libraryType-->
		<xsl:apply-templates select="lib:libraryType" mode="process"> 
			<xsl:with-param name="tag">libraryType</xsl:with-param>
		</xsl:apply-templates>

<!--otherType-->
		<xsl:apply-templates select="dc:type" mode="process"> 
			<xsl:with-param name="tag">otherType</xsl:with-param>
		</xsl:apply-templates>

<!--format-->
		<xsl:apply-templates select="dc:format" mode="process"> 
			<xsl:with-param name="tag">format</xsl:with-param>
		</xsl:apply-templates>

<!--identifier-->
		<xsl:apply-templates select="dc:identifier" mode="process">
			<xsl:with-param name="tag">identifier</xsl:with-param>
		</xsl:apply-templates>

<!--language-->
		<xsl:apply-templates select="dc:language" mode="process">
			<xsl:with-param name="tag">language</xsl:with-param>
		</xsl:apply-templates>

<!--relation-->
		<xsl:apply-templates select="dc:relation" mode="process"> 
			<xsl:with-param name="tag">relation</xsl:with-param>
		</xsl:apply-templates>

<!--coverage-->
		<xsl:apply-templates select="dc:coverage" mode="process"> 
			<xsl:with-param name="tag">coverage</xsl:with-param>
		</xsl:apply-templates>

<!--rights-->
		<xsl:apply-templates select="dc:rights" mode="process"> 
			<xsl:with-param name="tag">rights</xsl:with-param>
		</xsl:apply-templates>

<!--publisher-->
		<xsl:apply-templates select="dc:publisher" mode="process"> 
			<xsl:with-param name="tag">publisher</xsl:with-param>
		</xsl:apply-templates>

		</record><!--end record element-->
	</xsl:template>



<!--TEMPLATES -->
<!-- ****************************************-->
<!--PROCESS template writes all tag sets-->
	<xsl:template match="node()" name="process" mode="process">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="att"/>
		<xsl:param name="string" select="."/>
		<xsl:if test="string-length($string) > 0">
			<xsl:element name="{$tag}" namespace="http://nldr.library.ucar.edu/metadata/library_dc">
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
