<?xml version="1.0" encoding="UTF-8"?>
<!-- Transform an OPML document into an indented vertical tree (nested HTML unordered lists) -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" omit-xml-declaration="yes" />	

	<xsl:template match="/opml">	
		<xsl:apply-templates select="body"/>
	</xsl:template>	
	
	<xsl:template match="body">	
		<div class="dlese_vocabs_tab_indented">
			<ul>
				<xsl:apply-templates select="outline"/>
			</ul>			
		</div>
	</xsl:template>
	
	<xsl:template match="outline">
		<xsl:if test="not( @display = 'false' )">
			<xsl:choose>
				<xsl:when test="outline">
					<li>
						<xsl:value-of select="@text"/>:
						<ul>
							<xsl:apply-templates select="outline"/>
						</ul>
					</li>
				</xsl:when>
				<xsl:otherwise>
					<li><xsl:value-of select="@text"/></li>
					<xsl:apply-templates select="outline"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>	

</xsl:stylesheet>
