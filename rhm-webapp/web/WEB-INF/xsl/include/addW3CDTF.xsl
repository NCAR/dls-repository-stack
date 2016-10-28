<?xml version="1.0" encoding="UTF-8"?>
<!-- =============================================================  
 "addW3CDTF" stylesheet 

add W3CDTF encoding scheme to elements if appropriate  

	Also 
		don't output anything if element is empty
		get rid of excess whitespace in value with normalize-space
		don't output extra namespace declarations
 
Author:  Naomi Dushay 
updated 03/07/08 add unknown to useless values
updated 03/07/07 add W3CDTF when appropriate rather than indiscriminately
updated: 02/11/20 add named template 
Created: 02/11/19  Naomi Dushay
================================================================ -->
<xsl:stylesheet version="1.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    >
     
	<xsl:template match="*" mode="addW3CDTF">

		<!-- ensure element has a value -->
		<xsl:variable name="normSpaceStr" select="string(normalize-space(.))"/>
		<xsl:if test="$normSpaceStr">
			<xsl:variable name="lowercase" select="translate($normSpaceStr, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>

	  		<xsl:choose>
				<!-- don't output useless values -->
				<xsl:when test="$lowercase = '-'"/>
				<xsl:when test="$lowercase = '[-]'"/>
				<xsl:when test="$lowercase = 'c'"/>
				<xsl:when test="$lowercase = '[c]'"/>
				<xsl:when test="$lowercase = 'c.'"/>
				<xsl:when test="$lowercase = '[c.]'"/>
				<xsl:when test="$lowercase = 'c-'"/>
				<xsl:when test="$lowercase = '[c-]'"/>
				<xsl:when test="$lowercase = 'unknown'"/>
				<xsl:when test="$lowercase = 'unknown.'"/>
				
				<xsl:otherwise>
					<!-- it's a useful value -->

					<!-- variables for format checking -->
					<xsl:variable name="yearStr" select="substring($lowercase, 1, 4)"/>
					<xsl:variable name="firstSep" select="substring($lowercase, 5, 1)"/>
					<xsl:variable name="monthStr" select="substring($lowercase, 6, 2)"/>
					<xsl:variable name="secondSep" select="substring($lowercase, 8, 1)"/>
					<xsl:variable name="dayStr" select="substring($lowercase, 9, 2)"/>

					<xsl:element name="{name()}" namespace="{namespace-uri()}">
		
						<xsl:choose>
							<!-- W3CDTF encoding scheme already specified -->
							<xsl:when test="self::node()[@xsi:type='dct:W3CDTF']">
								<xsl:apply-templates select="@*|node()" mode="addW3CDTF"/>
							</xsl:when>
							
							<!-- add W3CDTF encoding scheme if appropriate per our dct XML schema -->
						        <!-- sources: -->
						        <!-- DCMI info: http://www.dublincore.org/documents/dcmi-terms/#W3CDTF -->
						        <!-- ISO 8601: http://www.w3.org/TR/NOTE-datetime -->
						        <!-- NSDL dct XML schema:  http://ns.nsdl.org/schemas/dc/dcterms_v1.01.xsd -->

							<!-- "xsd:gYear" (but strictly in CCYY format: too hard to do more) -->
							<xsl:when test="string-length($lowercase) = '4' and
											not (string(number($yearStr)) = 'NaN') ">
				  				<xsl:attribute name="xsi:type">dct:W3CDTF</xsl:attribute>
								 <xsl:apply-templates select="@*|node()" mode="addW3CDTF"/>
							</xsl:when>

							<!-- "xsd:gYearMonth" (but strictly in CCYY-MM format: too hard to do more) -->
							<xsl:when test="string-length($lowercase) = '7' and
											not (string(number($yearStr)) = 'NaN')  and 
											($firstSep = '-') and
											 not (string(number($monthStr)) = 'NaN') and
											 ($monthStr &gt; '0' ) and
											 ($monthStr &lt; '13' ) ">
					  			<xsl:attribute name="xsi:type">dct:W3CDTF</xsl:attribute>
								 <xsl:apply-templates select="@*|node()" mode="addW3CDTF"/>
							</xsl:when>

							<!-- "xsd:date" (but strictly in CCYY-MM-DD format: too hard to do more) 
							NOTE:  not checking for correct association of day with month (2-30, 9-31 are 'valid' here )-->
							<xsl:when test="string-length($lowercase) = '10' and
											not (string(number($yearStr)) = 'NaN')  and 
											($firstSep = '-') and
											 not (string(number($monthStr)) = 'NaN') and
											 ($monthStr &gt; '0' ) and
											 ($monthStr &lt; '13' ) and
											($secondSep = '-') and
											 not (string(number($dayStr)) = 'NaN') and
											 ($dayStr &gt; '0' ) and
											 ($dayStr &lt; '32' ) ">
					  			<xsl:attribute name="xsi:type">dct:W3CDTF</xsl:attribute>
								 <xsl:apply-templates select="@*|node()" mode="addW3CDTF"/>
							</xsl:when>

							<!-- NOTE:  "xsd:dateTime" is valid, but I'm not bothering to check for it! -->

							<!-- didn't recognize as W3CDTF date -->
							<xsl:otherwise>
								<xsl:apply-templates select="@*|node()" mode="addW3CDTF"/>
							</xsl:otherwise>				
						</xsl:choose>
					</xsl:element>
				</xsl:otherwise> <!-- output it, adding W3CDTF where appropriate -->
			</xsl:choose> <!-- only output if useful value -->
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="text()" mode="addW3CDTF">
		<xsl:value-of select="normalize-space(.)"/>
	</xsl:template>

	<xsl:template match="@*" mode="addW3CDTF">
		<xsl:copy>
			<xsl:apply-templates select="node()" mode="addW3CDTF"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="comment()" mode="addW3CDTF">
		<xsl:comment><xsl:value-of select="."/></xsl:comment>
	</xsl:template>
	
	<!-- named template assumes no other child nodes (attributes, elements, comments, etc) -->
	<xsl:template name="addW3CDTF">
		<xsl:param name="qname" select="name()"/>
		<xsl:param name="nsURI" select="namespace-uri()"/>
		<xsl:param name="value" select="."/>

		<!-- ensure element has a value -->
		<xsl:variable name="normSpaceStr" select="string(normalize-space($value))"/>
		<xsl:if test="$normSpaceStr">
			<xsl:variable name="lowercase" select="translate($normSpaceStr, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>

	  		<xsl:choose>
				<!-- don't output useless values -->
				<xsl:when test="$lowercase = '-'"/>
				<xsl:when test="$lowercase = '[-]'"/>
				<xsl:when test="$lowercase = 'c'"/>
				<xsl:when test="$lowercase = '[c]'"/>
				<xsl:when test="$lowercase = 'c.'"/>
				<xsl:when test="$lowercase = '[c.]'"/>
				<xsl:when test="$lowercase = 'c-'"/>
				<xsl:when test="$lowercase = '[c-]'"/>
				<xsl:when test="$lowercase = 'unknown'"/>
				<xsl:when test="$lowercase = 'unknown.'"/>
				
				<xsl:otherwise>
					<!-- it's a useful value -->

					<xsl:element name="{$qname}" namespace="{$nsURI}">
		
						<!-- variables for format checking -->
						<xsl:variable name="yearStr" select="substring($lowercase, 1, 4)"/>
						<xsl:variable name="firstSep" select="substring($lowercase, 5, 1)"/>
						<xsl:variable name="monthStr" select="substring($lowercase, 6, 2)"/>
						<xsl:variable name="secondSep" select="substring($lowercase, 8, 1)"/>
						<xsl:variable name="dayStr" select="substring($lowercase, 9, 2)"/>
	
						<xsl:choose>
							<!-- NOTE:  would need to pass in attribs to know if W3CDTF was already specified -->
		
							<!-- add W3CDTF encoding scheme if appropriate per our dct XML schema -->
						        <!-- sources: -->
						        <!-- DCMI info: http://www.dublincore.org/documents/dcmi-terms/#W3CDTF -->
						        <!-- ISO 8601: http://www.w3.org/TR/NOTE-datetime -->
						        <!-- NSDL dct XML schema:  http://ns.nsdl.org/schemas/dc/dcterms_v1.01.xsd -->

							<!-- "xsd:gYear" (but strictly in CCYY format: too hard to do more) -->
							<xsl:when test="string-length($lowercase) = '4' and
											not (string(number($yearStr)) = 'NaN') ">
				  				<xsl:attribute name="xsi:type">dct:W3CDTF</xsl:attribute>
							</xsl:when>

							<!-- "xsd:gYearMonth" (but strictly in CCYY-MM format: too hard to do more) -->
							<xsl:when test="string-length($lowercase) = '7' and
											not (string(number($yearStr)) = 'NaN')  and 
											($firstSep = '-') and
											 not (string(number($monthStr)) = 'NaN') and
											 ($monthStr &gt; '0' ) and
											 ($monthStr &lt; '13' ) ">
					  			<xsl:attribute name="xsi:type">dct:W3CDTF</xsl:attribute>
							</xsl:when>

							<!-- "xsd:date" (but strictly in CCYY-MM-DD format: too hard to do more) 
							NOTE:  not checking for correct association of day with month (2-30, 9-31 are 'valid' here )-->
							<xsl:when test="string-length($lowercase) = '10' and
											not (string(number($yearStr)) = 'NaN')  and 
											($firstSep = '-') and
											 not (string(number($monthStr)) = 'NaN') and
											 ($monthStr &gt; '0' ) and
											 ($monthStr &lt; '13' ) and
											($secondSep = '-') and
											 not (string(number($dayStr)) = 'NaN') and
											 ($dayStr &gt; '0' ) and
											 ($dayStr &lt; '32' ) ">
					  			<xsl:attribute name="xsi:type">dct:W3CDTF</xsl:attribute>
							</xsl:when>

							<!-- NOTE:  "xsd:dateTime" is valid, but I'm not bothering to check for it! -->

							<!-- otherwise didn't recognize as W3CDTF date -->
						</xsl:choose>
						<!-- output value -->
						<xsl:value-of select="normalize-space($value)"/>
					</xsl:element>
				</xsl:otherwise> <!-- output it, adding W3CDTF where appropriate -->
			</xsl:choose> <!-- only output if useful value -->
		</xsl:if>

	</xsl:template>

</xsl:stylesheet>


