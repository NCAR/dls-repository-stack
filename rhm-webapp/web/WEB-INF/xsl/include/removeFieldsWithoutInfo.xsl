<?xml version="1.0" encoding="UTF-8"?>
<!-- =============================================================  
 "removeFieldsWithoutInfo" stylesheet 

remove fields with "no information value"  
add IMT schema encoding to elements if it's not already present.  

	Also 
		don't output anything if element is empty
		get rid of excess whitespace in value with normalize-space
		don't output extra namespace declarations
 
Author:  Naomi Dushay 
Updated: 05/12/21 added null value
Updated: 03/06/27 added some values
Created: 03/06/03 
================================================================ -->
<xsl:stylesheet version="1.0" 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		>

	<xsl:template match="*" mode="removeFieldsWithoutInfo">
		<xsl:variable name="normValue" select="normalize-space(.)"/>

		<!-- ensure element has a value -->
		<xsl:if test="string($normValue)">

			<xsl:variable name="lowercase" select="translate($normValue, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>

			<!-- FIXME:  would also like to check for parens and brackets around the values, and for "c-" values in date fields ... -->

			<xsl:choose>
				<!-- don't output the field if it has no information value -->
				<xsl:when test=" $lowercase = 'abstract to be added' "/>
				<xsl:when test=" $lowercase = 'abstract to be added.' "/>
				<xsl:when test=" $lowercase = 'n/a' "/>
				<xsl:when test=" $lowercase = 'n/a.' "/>
				<xsl:when test=" $lowercase = 'na' "/>
				<xsl:when test=" $lowercase = 'na.' "/>
				<xsl:when test=" $lowercase = 'no abstract' "/>
				<xsl:when test=" $lowercase = 'no abstract.' "/>
				<xsl:when test=" $lowercase = 'no abstract available' "/>
				<xsl:when test=" $lowercase = 'no abstract available.' "/>
				<xsl:when test=" $lowercase = 'no abstract submitted' "/>
				<xsl:when test=" $lowercase = 'no abstract submitted.' "/>
				<xsl:when test=" $lowercase = 'no information available' "/>
				<xsl:when test=" $lowercase = 'no information available.' "/>
				<xsl:when test=" $lowercase = 'no info available' "/>
				<xsl:when test=" $lowercase = 'no info available.' "/>
				<xsl:when test=" $lowercase = 'none' "/>
				<xsl:when test=" $lowercase = 'none.' "/>
				<xsl:when test=" $lowercase = 'none available' "/>
				<xsl:when test=" $lowercase = 'none available.' "/>
				<xsl:when test=" $lowercase = 'null' "/>
				<xsl:when test=" $lowercase = 'null.' "/>
				<xsl:when test=" $lowercase = 'to be added' "/>
				<xsl:when test=" $lowercase = 'to be added.' "/>
				<xsl:when test=" $lowercase = 'to do' "/>
				<xsl:when test=" $lowercase = 'to do.' "/>
				<xsl:when test=" $lowercase = 'unknown' "/>
				<xsl:when test=" $lowercase = 'unknown.' "/>
				<xsl:when test=" $lowercase = 'uknown' "/>
				<xsl:when test=" $lowercase = 'uknown.' "/>
				<xsl:when test=" $lowercase = 'u' "/>
				<xsl:when test=" $lowercase = 'u.' "/>
				<xsl:when test=" $lowercase = '-u' "/>
				<xsl:when test=" $lowercase = '-u.' "/>
				<!-- NOTE:  I can't think of an elegant way to test for "one or more" of something in this context -->
				<xsl:when test=" $lowercase = '.' "/>
				<xsl:when test=" $lowercase = '..' "/>
				<xsl:when test=" $lowercase = '...' "/>
				<xsl:when test=" $lowercase = '....' "/>
				<xsl:when test=" $lowercase = '-' "/>
				<xsl:when test=" $lowercase = '--' "/>
				<xsl:when test=" $lowercase = '---' "/>
				<xsl:when test=" $lowercase = '----' "/>
				
				<xsl:otherwise>
					<!-- yes, output the value -->
					<xsl:element name="{name()}" namespace="{namespace-uri()}">
						<xsl:apply-templates select="@*|node()" mode="removeFieldsWithoutInfo"/>
					</xsl:element>		
				</xsl:otherwise>
			</xsl:choose>

		</xsl:if>
	</xsl:template>

	<xsl:template match="text()" mode="removeFieldsWithoutInfo">
		<xsl:value-of select="normalize-space(.)"/>
	</xsl:template>

	<xsl:template match="@*" mode="removeFieldsWithoutInfo">
		<xsl:copy>
			<xsl:apply-templates select="node()" mode="removeFieldsWithoutInfo"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="comment()" mode="removeFieldsWithoutInfo">
		<xsl:comment>
			<xsl:value-of select="."/>
		</xsl:comment>
	</xsl:template>


	<!-- named template assumes no other child nodes (attributes, elements, comments, etc) -->
	<xsl:template name="removeFieldsWithoutInfo">
		<xsl:param name="qname" select="name()"/>
		<xsl:param name="nsURI" select="namespace-uri()"/>
		<xsl:param name="value" select="."/>

		<xsl:variable name="normValue" select="normalize-space($value)"/>

		<!-- ensure element has a value -->
		<xsl:if test="string($normValue)">

			<xsl:variable name="lowercase" select="translate($normValue, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>

			<!-- FIXME:  would also like to check for parens and brackets around the values, and for "c-" values in date fields ... -->

			<xsl:choose>
				<!-- don't output the field if it has no information value -->
				<xsl:when test=" $lowercase = 'abstract to be added' "/>
				<xsl:when test=" $lowercase = 'abstract to be added.' "/>
				<xsl:when test=" $lowercase = 'n/a' "/>
				<xsl:when test=" $lowercase = 'n/a.' "/>
				<xsl:when test=" $lowercase = 'na' "/>
				<xsl:when test=" $lowercase = 'na.' "/>
				<xsl:when test=" $lowercase = 'no abstract' "/>
				<xsl:when test=" $lowercase = 'no abstract.' "/>
				<xsl:when test=" $lowercase = 'no abstract available' "/>
				<xsl:when test=" $lowercase = 'no abstract available.' "/>
				<xsl:when test=" $lowercase = 'no abstract submitted' "/>
				<xsl:when test=" $lowercase = 'no abstract submitted.' "/>
				<xsl:when test=" $lowercase = 'no information available' "/>
				<xsl:when test=" $lowercase = 'no information available.' "/>
				<xsl:when test=" $lowercase = 'no info available' "/>
				<xsl:when test=" $lowercase = 'no info available.' "/>
				<xsl:when test=" $lowercase = 'none' "/>
				<xsl:when test=" $lowercase = 'none.' "/>
				<xsl:when test=" $lowercase = 'none available' "/>
				<xsl:when test=" $lowercase = 'none available.' "/>
				<xsl:when test=" $lowercase = 'null' "/>
				<xsl:when test=" $lowercase = 'null.' "/>
				<xsl:when test=" $lowercase = 'to be added' "/>
				<xsl:when test=" $lowercase = 'to be added.' "/>
				<xsl:when test=" $lowercase = 'to do' "/>
				<xsl:when test=" $lowercase = 'to do.' "/>
				<xsl:when test=" $lowercase = 'unknown' "/>
				<xsl:when test=" $lowercase = 'unknown.' "/>
				<xsl:when test=" $lowercase = 'uknown' "/>
				<xsl:when test=" $lowercase = 'uknown.' "/>
				<xsl:when test=" $lowercase = 'u' "/>
				<xsl:when test=" $lowercase = 'u.' "/>
				<xsl:when test=" $lowercase = '-u' "/>
				<xsl:when test=" $lowercase = '-u.' "/>
				<!-- NOTE:  I can't think of an elegant way to test for "one or more" of something in this context -->
				<xsl:when test=" $lowercase = '.' "/>
				<xsl:when test=" $lowercase = '..' "/>
				<xsl:when test=" $lowercase = '...' "/>
				<xsl:when test=" $lowercase = '....' "/>
				<xsl:when test=" $lowercase = '-' "/>
				<xsl:when test=" $lowercase = '--' "/>
				<xsl:when test=" $lowercase = '---' "/>
				<xsl:when test=" $lowercase = '----' "/>
				
				<xsl:otherwise>
					<!-- yes, output the value -->
					<xsl:element name="{$qname}" namespace="{$nsURI}">
						<xsl:apply-templates select="@*|node()" mode="removeFieldsWithoutInfo"/>
					</xsl:element>		
				</xsl:otherwise>
			</xsl:choose>

		</xsl:if>
	</xsl:template>


</xsl:stylesheet>
