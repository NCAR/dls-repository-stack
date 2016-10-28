<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:d="http://adn.dlese.org"
    xmlns:c="http://collection.dlese.org"
    xmlns:o="http://objects.dlese.org"
    xmlns:n="http://newsopps.dlese.org"
    exclude-result-prefixes="d c o n"
    version="1.0">

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<xsl:variable name="newline">
<xsl:text>
</xsl:text>
	</xsl:variable>

	<xsl:template match="*|/">
		<xsl:apply-templates/>
	</xsl:template>   
	
	<xsl:template match="*|/">
		<xsl:copy>
			<xsl:for-each select="@*">
			<!-- to preserve attributes in the input xml document; so that they appear in the output xml documet; e.g. URI -->     
				<xsl:copy/>
			</xsl:for-each>
		<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>

     <xsl:template match="d:metaMetadata/d:contributors"/>
     <!--use a blank template to prevent metaMetadata.contributors from being copied over-->
     
     <xsl:template match="c:metaMetadata/d:contributors"/>
     <!--use a blank template to prevent metaMetadata.contributors from being copied over-->

     <xsl:template match="o:contributors/o:contributor">
     	<xsl:choose>
			<xsl:when test="@role='Cataloger' or @role='Validator'">
			</xsl:when>
			<xsl:otherwise>
                 	<xsl:copy-of select="."/>
     			<!--use copy-of to get attributes and child nodes-->
			</xsl:otherwise>
		</xsl:choose>
     </xsl:template>

     <xsl:template match="n:contributors/n:contributor">
     	<xsl:choose>
			<xsl:when test="@role='Cataloger' or @role='Validator' or @role='Editor'">
			</xsl:when>
			<xsl:otherwise>
                 	<xsl:copy-of select="."/>
     			<!--use copy-of to get attributes and child nodes-->
			</xsl:otherwise>
		</xsl:choose>
     </xsl:template>

</xsl:stylesheet>
