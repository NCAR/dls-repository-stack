<?xml version="1.0" encoding="UTF-8"?>
<!-- =============================================================  
 "addDCMIType" stylesheet 

add DCMIType encoding scheme to elements if appropriate 

	Also 
		don't output anything if element is empty
		get rid of excess whitespace in value with normalize-space
		don't output extra namespace declarations
 
Author:  Naomi Dushay
updated: 03/07/09 only perform special capitalization if it's a DCMIType value
updated: 03/06/01 PhysicalObject now included, fix case when DCMIType already specified, use more elegant code from addURI
updated: 02/11/22 check for appropriateness, and add only if okay 
updated: 02/11/20 add named template 
Created: 02/11/19  Naomi Dushay
================================================================ -->
<xsl:stylesheet version="1.0" 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		>

	<xsl:template match="*" mode="addDCMIType">	
		<!-- ensure element has a value -->
		<xsl:variable name="normSpace" select="normalize-space(.)"/>
		<xsl:if test="string($normSpace)">
			
			<xsl:variable name="lowercase" select="translate($normSpace, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>

			<xsl:element name="{name()}" namespace="{namespace-uri()}">
				<xsl:choose>
					<!-- DCMIType encoding scheme already specified -->
					<xsl:when test="self::node()[@xsi:type='dct:DCMIType']">
						<xsl:apply-templates select="@*|node()" mode="addDCMIType"/>
					</xsl:when>

					<!-- add DCMIType encoding scheme when appropriate -->
					<xsl:when test="$lowercase = 'collection' or
									$lowercase = 'dataset' or
									$lowercase = 'event' or
									$lowercase = 'image 'or
									$lowercase = 'interactiveresource' or $lowercase = 'interactive resource' or
									$lowercase = 'physicalobject' or $lowercase = 'physical object' or
									$lowercase = 'service' or
									$lowercase = 'software' or
									$lowercase = 'sound' or
									$lowercase = 'text'">
						<xsl:attribute name="xsi:type">dct:DCMIType</xsl:attribute>
						<xsl:apply-templates select="@*|node()" mode="addDCMIType"/>
					</xsl:when>
					
					<!-- we didn't find a DCMIType value -->
					<xsl:otherwise>
						<xsl:apply-templates select="@* | * | comment() | processing-instruction()" mode="addDCMIType"/>
						<xsl:apply-templates select="text()" mode="notDCMIType"/>
					</xsl:otherwise>				
				</xsl:choose>

			</xsl:element>
		</xsl:if>
	</xsl:template>


	<xsl:template match="text()" mode="addDCMIType">
		<!-- NOTE:: might want to remove useless values here, though that hasn't been a big problem with type field -->
		<xsl:call-template name="outputDCMITypeValue">
			<xsl:with-param name="value" select="normalize-space(.)"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="text()" mode="notDCMIType">
		<!-- NOTE:: might want to remove useless values here, though that hasn't been a big problem with type field -->
		<xsl:value-of select="normalize-space(.)"/>
	</xsl:template>


	<xsl:template match="@*" mode="addDCMIType">
		<xsl:copy>
			<xsl:apply-templates select="node()" mode="addDCMIType"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="comment()" mode="addDCMIType">
		<xsl:comment>
			<xsl:value-of select="."/>
		</xsl:comment>
	</xsl:template>


	<!-- named template assumes no other child nodes (attributes, elements, comments, etc) -->
	<xsl:template name="addDCMIType">
		<xsl:param name="qname" select="name()"/>
		<xsl:param name="nsURI" select="namespace-uri()"/>
		<xsl:param name="value" select="."/>

		<!-- ensure element has a value -->
		<xsl:variable name="normSpace" select="normalize-space($value)"/>
		<xsl:if test="string($normSpace)">
			
			<xsl:variable name="lowercase" select="translate($normSpace, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>

			<xsl:element name="{$qname}" namespace="{$nsURI}">

				<xsl:choose>
					<!-- NOTE:  would need to pass in attribs to know if DCMIType was already specified -->

					<!-- add DCMIType encoding shceme when appropriate -->
					<xsl:when test="$lowercase = 'collection' or
									$lowercase = 'dataset' or
									$lowercase = 'event' or
									$lowercase = 'image 'or
									$lowercase = 'interactiveresource' or $lowercase = 'interactive resource' or
									$lowercase = 'physicalobject' or $lowercase = 'physical object' or
									$lowercase = 'service' or
									$lowercase = 'software' or
									$lowercase = 'sound' or
									$lowercase = 'text'">
						<xsl:attribute name="xsi:type">dct:DCMIType</xsl:attribute>
						<xsl:call-template name="outputDCMITypeValue">
							<xsl:with-param name="value" select="$normSpace"/>
						</xsl:call-template>
					</xsl:when>
					
					<!-- we didn't find a DCMIType value -->
					<xsl:otherwise>
						<xsl:value-of select="$normSpace"/>
					</xsl:otherwise>

				</xsl:choose>
			</xsl:element>
		</xsl:if>
	</xsl:template>



	<!-- output DCMIType value that schema validates -->
	<xsl:template name="outputDCMITypeValue">
		<xsl:param name="value"/>
		<xsl:choose>
			<xsl:when test="$value='Interactive Resource'">InteractiveResource</xsl:when>
			<xsl:when test="$value='Interactive resource'">InteractiveResource</xsl:when>
			<xsl:when test="$value='interactive resource'">InteractiveResource</xsl:when>
			<xsl:when test="$value='interactive Resource'">InteractiveResource</xsl:when>
			<xsl:when test="$value='InteractiveResource'">InteractiveResource</xsl:when>
			<xsl:when test="$value='Interactiveresource'">InteractiveResource</xsl:when>
			<xsl:when test="$value='interactiveresource'">InteractiveResource</xsl:when>
			<xsl:when test="$value='interactiveResource'">InteractiveResource</xsl:when>
			<xsl:when test="$value='Physical Object'">PhysicalObject</xsl:when>
			<xsl:when test="$value='Physical object'">PhysicalObject</xsl:when>
			<xsl:when test="$value='physical object'">PhysicalObject</xsl:when>
			<xsl:when test="$value='physical Object'">PhysicalObject</xsl:when>
			<xsl:when test="$value='PhysicalObject'">PhysicalObject</xsl:when>
			<xsl:when test="$value='Physicalobject'">PhysicalObject</xsl:when>
			<xsl:when test="$value='physicalobject'">PhysicalObject</xsl:when>
			<xsl:when test="$value='physicalObject'">PhysicalObject</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="firstChar" select="translate(substring($value, 1, 1), 'abcdefghijklmnopqrstuvwxyz', 		'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
				<xsl:variable name="rest" select="substring($value, 2)"/>
				<!-- output the value -->
				<xsl:value-of select="$firstChar"/>
				<xsl:value-of select="$rest"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
