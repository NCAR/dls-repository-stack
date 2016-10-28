<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" >
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:template match="@*" >
		<xsl:attribute name="{local-name()}" >
			<xsl:value-of select="." />
		</xsl:attribute>
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match ="*" >
		<xsl:element name="{local-name()}" >
			<xsl:apply-templates select="@* | node()" />
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
