<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:osm="http://nldr.library.ucar.edu/metadata/osm"
	exclude-result-prefixes="osm" version="1.0">
	<xsl:output method="text" indent="no" encoding="UTF-8"/>

<!--variable for adding a line return-->
	<xsl:variable name="newline">
<xsl:text>
</xsl:text>
	</xsl:variable>



	<xsl:template match="*|/">
		<xsl:apply-templates select="osm:record"/>
	</xsl:template>
	<xsl:template match="osm:record">
		<xsl:apply-templates select="osm:contributors/osm:person"/>
	</xsl:template>
	<xsl:template match="osm:person">
		<xsl:if test="osm:affiliation/osm:instName = 'University Corporation for Atmospheric Research' ">
			<xsl:if test="string-length(./@UCARid) = 0">
				<xsl:value-of select="concat(../../osm:general/osm:recordID, ':', osm:lastName, ':', osm:firstName,  ':', $newline)"/>
			</xsl:if>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>