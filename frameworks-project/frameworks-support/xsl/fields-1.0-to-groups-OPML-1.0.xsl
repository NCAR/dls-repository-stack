<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:d="http://www.dlsciences.org/frameworks/fields"
    xmlns:groups="http://www.dlsciences.org/frameworks/groups"
    exclude-result-prefixes="xsi d" 
    version="1.0">

<!--ORGANIZATION OF THIS FILE-->
<!-- **************************************-->
<!--This file is organized into the following sections:
A. Purpose
B. License information and credits
C. Assumptions
D. Transformation code
E. Templates to apply (in alphabetical order)-->

<!--A. PURPOSE-->
<!-- **************************************-->
<!--To transform the Digital Library for Earth System Education (DLESE) fields metadata records to groups OPML 2.0 records-->


<!--B. LICENSE INFORMATION and CREDITS-->
<!-- *****************************************************-->
<!--Date created: 2005-10-10 by Katy Ginger, DLESE Program Center, University Corporation for Atmospheric Research (UCAR)-->
<!--2008-05-21: modified schema line below to recognize new namespaces and schema locations-->
<!--2008-05-21: modified groups namespace from http://www.dlese.org/Metadata/ui/groups to http://www.dlsciences.org/frameworks/groups-->
<!--2008-05-21: modified d namespace from http://www.dlese.org/Metadata/fields to http://www.dlsciences.org/frameworks/fields-->
<!--2005-10-14: took out id numbers if not present-->
<!--2005-10-14: added a comment section so CVS can add date and version info to each file-->
<!--2006-03-30: do not use deftn attribute in groups file if content is 'self-explanatory'; write attribution attribute to groups-->
<!--2006-05-02: create instance doc using a schema; make concept element use groups namespace prefix; make non-OPML outline attributes use a groups namespace prefix; add the the required attribute type (its in the OPML namespace)-->
<!--License information:
		Copyright (c) 2002-2006, 2005 DLESE Program Center
		University Corporation for Atmospheric Research (UCAR)
		P.O. Box 3000, Boulder, CO 80307, United States of America
		email: support@dlese.org. 
		All rights reserved
This XML tranformation, written in XSLT 1.0 and XPATH 1.0, are free software; you can redistribute them and/or modify them under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.  These XML instance documents are distributed in the hope that they will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this project; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA -->

<!--C. ASSUMPTIONS-->
<!-- **************************************-->
<!--Applies to DLESE fields metadata format, version 1.0.00 records-->
<!--Assumes content is present in required fields and does not check for the presence of it-->


	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>

<!--D. TRANSFORMATION CODE mode one-->
<!-- **************************************-->

	<xsl:template match="*|/">
		<xsl:comment>$Id: fields-1.0-to-groups-OPML-1.0.xsl,v 1.2 2008/05/21 19:24:46 ginger Exp $</xsl:comment>
		<opml xmlns="http://www.dlsciences.org/frameworks/opml" xmlns:groups="http://www.dlsciences.org/frameworks/groups" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.dlsciences.org/frameworks/opml http://www.dlsciences.org/frameworks/groups/1.0/schemas/groups-opml.xsd" version="2.0">


<!--no work because name of xmlns:groups is not allowed per W3C XSLT 1.0 spec
		<xsl:element name="opml" namespace="http://www.dlese.org/Metadata/opml">
			<xsl:attribute name="xsi:schemaLocation">http://www.dlese.org/Metadata/opml http://www.dlese.org/Metadata/ui/groups/1.0/groups-opml.xsd</xsl:attribute>
			<xsl:attribute name="version">2.0</xsl:attribute>
			<xsl:attribute name="xmlns:groups">http://www.dlese.org/Metadata/ui/groups</xsl:attribute>-->

			<xsl:element name="head">
				<xsl:element name="title">
					<xsl:value-of select="d:metadataFieldInfo/d:field/@name"/>
				</xsl:element>

				<xsl:element name="groups:concept">
					<xsl:if test="string-length(d:metadataFieldInfo/d:field/@id) > 0">
						<xsl:attribute name="id">
							<xsl:value-of select="d:metadataFieldInfo/d:field/@id"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="language">
						<xsl:value-of select="d:metadataFieldInfo/d:field/@language"/>
					</xsl:attribute>
					<xsl:attribute name="metaFormat">
						<xsl:value-of select="d:metadataFieldInfo/d:field/@metaFormat"/>
					</xsl:attribute>
					<xsl:attribute name="metaVersion">
						<xsl:value-of select="d:metadataFieldInfo/d:field/@metaVersion"/>
					</xsl:attribute>
					<xsl:attribute name="text">
						<xsl:value-of select="d:metadataFieldInfo/d:field/@name"/>
					</xsl:attribute>
					<xsl:attribute name="audience">default</xsl:attribute>
					<xsl:attribute name="path">
						<xsl:value-of select="d:metadataFieldInfo/d:field/@path"/>
					</xsl:attribute>
					<xsl:attribute name="deftn">
						<xsl:value-of select="d:metadataFieldInfo/d:field/d:definition"/>
					</xsl:attribute>
				</xsl:element><!--ends concept element-->
			</xsl:element> <!--ends head element-->

			<xsl:element name="body">
				<!--determine which outline element to write based on whether the terms element exists in the field file-->
				<!--if the terms element exists in the fields file, write the outline element with the attributes vocab, text, id, and deftn-->
				<!--if the terms element does not exist in the fields files, write the outline element with the attributes of text, abbrev and deftn-->
				<xsl:choose>
					<xsl:when test="//*[name()='terms']">
					<!--tests for the presence of the terms element in the fields file-->
						<xsl:apply-templates select="d:metadataFieldInfo/d:field/d:terms/d:termAndDeftn"/>
					</xsl:when>
					<xsl:otherwise>
					<!--assumes the terms element does not exist in the fields file-->
						<xsl:element name="outline">
							<xsl:attribute name="text">see concept</xsl:attribute>
							<xsl:attribute name="type">field</xsl:attribute>
						</xsl:element>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element> <!--ends body element-->
			
</opml>
<!--		</xsl:element> --><!--ends opml element-->
		<xsl:comment>*** LICENSE INFORMATION *****
		Copyright  2002-2006 DLESE Program Center
		University Corporation for Atmospheric Research (UCAR)
		P.O. Box 3000, Boulder, CO 80307, United States of America
		email: support@dlese.org. 
These XML instance documents are free software; you can redistribute them and/or modify them under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.  These XML instance documents are distributed in the hope that they will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this project; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA</xsl:comment>
	</xsl:template>

<!--E. TEMPLATES TO APPLY-->
<!--*********************************-->
<!--organized in alphabetical order-->
<!--1. parseString template - parse the vocabulary string-->
<!--2. termAnd Deftn template-->

<!--1. parseString template-->
<!--assumes that words within the string are separated by colons-->
		<xsl:template match="node()" mode="parseString" name="parseString">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="string" select="normalize-space(./@vocab)"/>
<!--		<xsl:param name="string" select="normalize-space(.)"/>-->
		<xsl:choose> 
			<xsl:when test="contains($string, ':')">
				<!--test to see if string contains a colon; if so, the string is a vocab term like: 'DLESE:Learning materials:Lesson plan'-->
				<!--peel off string part after first colon and then call the template recursively on the rest of the string-->
				<xsl:call-template name="parseString">
					<xsl:with-param name="string" select="normalize-space(substring-after($string, ':'))"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$string"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

<!--2. termAnd Deftn template-->
	<xsl:template match="d:termAndDeftn">
		<xsl:element name="outline" namespace="http://www.dlsciences.org/frameworks/opml">
			<xsl:attribute name="groups:vocab">
				<xsl:value-of select="./@vocab"/>
			</xsl:attribute>

			<xsl:attribute name="type">vocab</xsl:attribute>
			
			<xsl:if test="string-length(./@id) > 0">
				<xsl:attribute name="groups:id">
					<xsl:value-of select="./@id"/>
				</xsl:attribute>
			</xsl:if>

			<xsl:attribute name="text">
	<!--		<xsl:apply-templates select="./@vocab" mode="parseString"/> -->
	<!-- the line above does not work because its an attribute???; so use call template instead-->
	
				<!--see the parseString template-->
				<xsl:call-template name="parseString"/> <!--see the parseString template-->
			</xsl:attribute>

			<xsl:if test="not(contains(., 'self-ex'))">
				<xsl:attribute name="groups:deftn">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:if>

			<xsl:if test="string-length(./@attribution) > 0">
				<xsl:attribute name="groups:attribution">
					<xsl:value-of select="./@attribution"/>
				</xsl:attribute>
			</xsl:if>
		
		</xsl:element> 
	</xsl:template>



</xsl:stylesheet>
<!--	*** LICENSE INFORMATION *****
		Copyright 2002, 2003, 2004, 2005 DLESE Program Center
		University Corporation for Atmospheric Research (UCAR)
		P.O. Box 3000, Boulder, CO 80307, United States of America
		email: support@dlese.org. 
This XML stylesheet is free software; you can redistribute them and/or modify them under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.  These XML instance documents are distributed in the hope that they will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this project; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA -->
