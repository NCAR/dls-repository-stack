<?xml version="1.0" encoding="UTF-8"?>
<!-- =============================================================  
 "replaceString" stylesheet 

replace a string within another string value and output the result.

based on http://xml.web.cern.ch/XML/www.dpawson.co.uk/xsl/sect4/uri.html

 
Author:  Naomi Dushay 
updated: 
Created: 02/11/21 Naomi Dushay
================================================================ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="replaceString">
		<xsl:param name="origString"/>
		<xsl:param name="strToReplace"/>
		<xsl:param name="strReplacedWith"/>

		<!-- ensure origString is not empty -->
		<xsl:if test="string($origString)">
	
			<xsl:choose>

				<xsl:when test="$origString = $strToReplace">
					<!-- whole origString needs to be replaced in one lump -->
					<xsl:value-of select="$strReplacedWith"/>
				</xsl:when>
	
				<xsl:when test="contains($origString, $strToReplace)">

					<xsl:value-of select="concat(substring-before($origString, $strToReplace), $strReplacedWith)"/>
					
					<xsl:variable name="strAfter" select="substring-after($origString, $strToReplace)"/>
					
					<xsl:if test="string($strAfter)">
						<xsl:call-template name="replaceString">
							<xsl:with-param name="origString" select="substring-after($origString, $strToReplace)"/>
							<xsl:with-param name="strToReplace" select="$strToReplace"/>
							<xsl:with-param name="strReplacedWith" select="$strReplacedWith"/>
						</xsl:call-template>
					</xsl:if>

				</xsl:when>

				<xsl:otherwise>
					<xsl:value-of select="$origString"/>
				</xsl:otherwise>
	
			</xsl:choose>

		</xsl:if>

	</xsl:template>
	
</xsl:stylesheet>