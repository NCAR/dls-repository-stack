<?xml version="1.0" encoding="UTF-8"?>
<!-- =============================================================  
 "copy-noNS" stylesheet 

Copy elements without extra namespace declarations.

In constract to copy-normalizeSpace, DO:
	copy empty elements
	copy text values exactly 

Author:  Naomi Dushay 
Updated:
Created: 02/11/15 Naomi Dushay
================================================================ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="@*" mode="copy-noNS">
		<xsl:copy>
			<xsl:apply-templates select="node()" mode="copy-noNS"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="*" mode="copy-noNS">
		<xsl:element name="{name()}" namespace="{namespace-uri()}">
			<xsl:apply-templates select="@*|node()" mode="copy-noNS"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="text()" mode="copy-noNS">
		<xsl:value-of select="."/>
	</xsl:template>

	<xsl:template match="comment()" mode="copy-noNS">
		<xsl:comment>
			<xsl:value-of select="."/>
		</xsl:comment>
	</xsl:template>

</xsl:stylesheet>
