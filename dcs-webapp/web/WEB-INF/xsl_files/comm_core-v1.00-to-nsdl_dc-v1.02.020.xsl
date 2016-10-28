<?xml version="1.0"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:msp="http://ns.nsdl.org/ncs/msp2"
    xmlns:dct="http://purl.org/dc/terms/"
    xmlns:nsdl_dc="http://ns.nsdl.org/nsdl_dc_v1.02/"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="xsi msp xs"
    version="1.1">

<!--PURPOSE: to transform MSP2 version 1.00 records into the nsdl_dc version 1.02.020 metadata records-->
<!--CREATION: 2009-05-20 by Katy Ginger, University Corporation for Atmospheric Research (UCAR)-->
<!--HISTORY: none-->

	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>

<!--VARIABLES used throughout the transform-->
<!-- ************************************************** -->
	<xsl:variable name="asnIdsURL">http://ns.nsdl.org/ncs/xml/NCS-to-ASN-NSES-mappings.xml</xsl:variable>
	<xsl:variable name="asnText">http://purl.org/ASN/</xsl:variable>

<!--TRANSFORMATION CODE-->
<!-- ************************************************** -->
	<xsl:template match="*|/">
		<xsl:apply-templates select="msp:record"/>
	</xsl:template>

<!--TRANSFORMATION CODE for MSP to MSP2-->
<!-- ********************************************************-->
	<xsl:template match="msp:record">
		<nsdl_dc:nsdl_dc schemaVersion="1.02.020" xmlns:nsdl_dc="http://ns.nsdl.org/nsdl_dc_v1.02/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dct="http://purl.org/dc/terms/" xmlns:ieee="http://www.ieee.org/xsd/LOMv1p0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://ns.nsdl.org/nsdl_dc_v1.02/ http://ns.nsdl.org/schemas/nsdl_dc/nsdl_dc_v1.02.xsd">

<!--dc:identifier; doesn't really need a template but easy to do-->
		<xsl:apply-templates select="msp:general/msp:url" mode="process">
<!--		<xsl:apply-templates select="local-name() = 'url' " mode="process"> -->
			<xsl:with-param name="tag">dc:identifier</xsl:with-param>
			<xsl:with-param name="att">dct:URI</xsl:with-param>
		</xsl:apply-templates>

<!--dc:title-->
		<xsl:apply-templates select="msp:general/msp:title" mode="process">
			<xsl:with-param name="tag">dc:title</xsl:with-param>
		</xsl:apply-templates>

<!--dct:alternative: does not have-->
<!--dct:abstract: does not have-->

<!--dc:description-->
		<xsl:apply-templates select="msp:general/msp:description" mode="process">
			<xsl:with-param name="tag">dc:description</xsl:with-param>
		</xsl:apply-templates>

<!--dc:subject using keyword-->
		<xsl:apply-templates select="msp:general/msp:keyword" mode="process">
			<xsl:with-param name="tag">dc:subject</xsl:with-param>
		</xsl:apply-templates>

<!--dc:subject scienceSubject-->
		<xsl:apply-templates select="msp:general/msp:subjects/msp:scienceSubject" mode="subject">
			<xsl:with-param name="tag">dc:subject</xsl:with-param>
		</xsl:apply-templates>

<!--dc:subject mathSubject-->
		<xsl:apply-templates select="msp:general/msp:subjects/msp:mathSubject" mode="subject">
			<xsl:with-param name="tag">dc:subject</xsl:with-param>
		</xsl:apply-templates>

<!--dc:subject educationalSubject-->
		<xsl:apply-templates select="msp:general/msp:subjects/msp:educationalSubject" mode="subject">
			<xsl:with-param name="tag">dc:subject</xsl:with-param>
		</xsl:apply-templates>

<!--dc:subject gsdlSubject-->
		<xsl:apply-templates select="msp:general/msp:gsdlSubject" mode="subject">
			<xsl:with-param name="tag">dc:subject</xsl:with-param>
		</xsl:apply-templates>

<!--dct:abstract: does not have-->
<!--dct:tableOfContents: does not have-->
<!--dct:bibliographicCitation: does not have-->

<!--dc:language-->
		<xsl:apply-templates select="msp:general/msp:language" mode="process">
			<xsl:with-param name="tag">dc:language</xsl:with-param>
		</xsl:apply-templates>

<!--dct:educationLevel-->
<!--MSP2 uses the NSDL 1.02.020 vocab-->
		<xsl:apply-templates select="msp:educational/msp:educationLevel" mode="process">
			<xsl:with-param name="tag">dct:educationLevel</xsl:with-param>
			<xsl:with-param name="att">nsdl_dc:NSDLEdLevel</xsl:with-param>
		</xsl:apply-templates>

<!--dc:type-->
<!--MSP2 uses the NSDL 1.02.020 vocab-->
		<xsl:apply-templates select="msp:educational/msp:resourceType" mode="process"> 
			<xsl:with-param name="tag">dc:type</xsl:with-param>
			<xsl:with-param name="att">nsdl_dc:NSDLType</xsl:with-param>
		</xsl:apply-templates>

<!--dct:audience-->
<!--MSP2 uses the NSDL 1.02.020 vocab-->
		<xsl:apply-templates select="msp:educational/msp:audience" mode="process"> 
			<xsl:with-param name="tag">dct:audience</xsl:with-param>
			<xsl:with-param name="att">nsdl_dc:NSDLAudience</xsl:with-param>
		</xsl:apply-templates>

<!--dct:mediator: does not have-->
<!--ieee:interactivityLevel: does not have-->
<!--ieee:interactivityType: does not have-->
<!--ieee:typicalLearningTime: does not have-->

<!--dct:accessibility-->
		<xsl:apply-templates select="msp:general/msp:gsdlSubject" mode="process"> 
			<xsl:with-param name="tag">dct:accessibility</xsl:with-param>
		</xsl:apply-templates>

<!--dct:instructionalMethod: does not have-->

<!--dct:conformsTo using standard_ASN_ID-->
		<xsl:apply-templates select="msp:educational/msp:standard_ASN_ID" mode="process"> 
			<xsl:with-param name="tag">dct:conformsTo</xsl:with-param>
			<xsl:with-param name="att">dct:URI</xsl:with-param>
		</xsl:apply-templates>

<!--dct:conformsTo using standard-->
		<xsl:apply-templates select="msp:educational/msp:math_COMM_CORE" mode="process"> 
			<xsl:with-param name="tag">dct:conformsTo</xsl:with-param>
		</xsl:apply-templates>

<!--dct:conformsTo using standard-->
		<xsl:apply-templates select="msp:educational/msp:standard" mode="process"> 
			<xsl:with-param name="tag">dct:conformsTo</xsl:with-param>
		</xsl:apply-templates>

<!--dc:contributor-->
		<xsl:apply-templates select="msp:lifecycle/msp:contributor[not(@role='Author' or @role='Publisher')] " mode="process"> 
			<xsl:with-param name="tag">dc:contributor</xsl:with-param>
		</xsl:apply-templates>

<!--dc:creator-->
		<xsl:apply-templates select="msp:lifecycle/msp:contributor[@role='Author'] " mode="process"> 
			<xsl:with-param name="tag">dc:creator</xsl:with-param>
		</xsl:apply-templates>

<!--dc:publisher-->
		<xsl:apply-templates select="msp:lifecycle/msp:contributor[@role='Publisher'] " mode="process"> 
			<xsl:with-param name="tag">dc:publisher</xsl:with-param>
		</xsl:apply-templates>

<!--dc:rights--><!--acts when there is an MSP2 attribute of url-->
		<xsl:apply-templates select="msp:lifecycle/msp:rights" mode="rights"> 
			<xsl:with-param name="tag">dc:rights</xsl:with-param>
			<xsl:with-param name="att">dct:URI</xsl:with-param>
		</xsl:apply-templates>

<!--dct:rightsHolders: does not have-->

<!--dct:accessRights using accessRights-->
<!--MSP2 uses the NSDL 1.02.020 vocab-->
		<xsl:apply-templates select="msp:lifecycle/msp:access" mode="process"> 
			<xsl:with-param name="tag">dct:accessRights</xsl:with-param>
			<xsl:with-param name="att">nsdl_dc:NSDLAccess</xsl:with-param>
		</xsl:apply-templates>


<!--dct:provenance: does not have-->
<!--dct:license using url: does not have-->
<!--dct:license using description: does not have-->
<!--dct:accrualMethod: does not have-->
<!--dct:accrualPeriodicity: does not have-->
<!--dct:accrualPolicy: does not have-->
<!--dc:source: does not have-->

<!--dc:relation-->
		<xsl:apply-templates select="msp:educational/msp:relatedResource[@type='Has a Related Resource Of' or @type='Is a Related Resource Of'] " mode="relation"> 
			<xsl:with-param name="tag">dc:relation</xsl:with-param>
			<xsl:with-param name="att">dct:URI</xsl:with-param>
		</xsl:apply-templates>

<!--dct:isPartOf-->
		<xsl:apply-templates select="msp:educational/msp:relatedResource[@type='Is Part Of'] " mode="relation"> 
			<xsl:with-param name="tag">dct:isPartOf</xsl:with-param>
			<xsl:with-param name="att">dct:URI</xsl:with-param>
		</xsl:apply-templates>

<!--dct:hasPart-->
		<xsl:apply-templates select="msp:educational/msp:relatedResource[@type='Has Part'] " mode="relation"> 
			<xsl:with-param name="tag">dct:hasPart</xsl:with-param>
			<xsl:with-param name="att">dct:URI</xsl:with-param>
		</xsl:apply-templates>

<!--dct:isReferencedBy-->
		<xsl:apply-templates select="msp:educational/msp:relatedResource[@type='Is Referenced By'] " mode="relation"> 
			<xsl:with-param name="tag">dct:isReferencedBy</xsl:with-param>
			<xsl:with-param name="att">dct:URI</xsl:with-param>
		</xsl:apply-templates>

<!--dct:references-->
		<xsl:apply-templates select="msp:educational/msp:relatedResource[@type='References'] " mode="relation"> 
			<xsl:with-param name="tag">dct:references</xsl:with-param>
			<xsl:with-param name="att">dct:URI</xsl:with-param>
		</xsl:apply-templates>

<!--dct:isFormatOf: does not have-->
<!--dct:hasFormat: does not have-->
<!--dct:isReplacedBy: does not have-->
<!--dct:replaces: does not have-->
<!--dct:isRequiredBy: does not havel-->
<!--dct:requires: does not have-->
<!--dct:isVersionOf: does not have-->
<!--dct:hasVersion: does not have-->

<!--dct:extent-->
		<xsl:apply-templates select="msp:educational/msp:duration" mode="process"> 
			<xsl:with-param name="tag">dct:extent</xsl:with-param>
		</xsl:apply-templates>

<!--dct:medium: does not have-->

<!--dc:format using mimetype-->
		<xsl:apply-templates select="msp:general/msp:mimeType" mode="process"> 
			<xsl:with-param name="tag">dc:format</xsl:with-param>

<!--
Do not include the following attribute because it initiates a call to the NSDL mimetype controlled vocabulary and some of the MSP2 mimetypes are not in the NSDL controlled vocabulary (e.g. application/ms-excel)
			<xsl:with-param name="att">dct:IMT</xsl:with-param>
-->
		</xsl:apply-templates>

<!--dc:format using format-->
		<xsl:apply-templates select="msp:general/msp:format" mode="process"> 
			<xsl:with-param name="tag">dc:format</xsl:with-param>
		</xsl:apply-templates>

<!--dc:date-->
		<xsl:apply-templates select="msp:lifecycle/msp:publicationDate" mode="process"> 
			<xsl:with-param name="tag">dc:date</xsl:with-param>
		</xsl:apply-templates>

<!--dct:created: does not have-->
<!--dct:available: does not have-->
<!--dct:issued: does not have-->
<!--dct:modified: does not have-->
<!--dct:valid: does not have-->
<!--dct:dateAccepted: does not have-->
<!--dct:dateCopyrighted: does not have-->
<!--dct:dateSubmitted: does not have-->

<!--dct:temporal when start and end date are present-->
		<xsl:apply-templates select="msp:coverage/msp:date" mode="date"> 
			<xsl:with-param name="tag">dct:temporal</xsl:with-param>
		</xsl:apply-templates>


<!--dc:coverage using description-->
		<xsl:apply-templates select="msp:coverage/msp:description" mode="process"> 
			<xsl:with-param name="tag">dc:coverage</xsl:with-param>
		</xsl:apply-templates>

<!--dc:coverage using location-->
		<xsl:apply-templates select="msp:coverage/msp:location">
			<xsl:with-param name="tag">dc:coverage</xsl:with-param>
			<xsl:with-param name="att">dct:Box</xsl:with-param>
		</xsl:apply-templates>


		</nsdl_dc:nsdl_dc><!--end nsdl_dc:nsdl_dc element-->
	</xsl:template>



<!--TEMPLATES for nsdl_ncs to nsdl_dc-->
<!-- ****************************************-->
<!--PROCESS:writes all tag sets that are not a content standard, box or point-->
<!--BOX or POINT: writes the coverages.box and coverages.point tag sets-->
<!--NSESCONTENTSTANDARD: writes the educational.standards.NSESstandard and sometimes the educational.standards.asnID tag sets-->

<!--PROCESS template-->
	<xsl:template match="node()" name="process" mode="process">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="att"/>
		<xsl:param name="string" select="."/>

		<xsl:if test="string-length($string) > 0">
			<xsl:element name="{$tag}">
				<xsl:if test="string-length($att) > 0">
					<xsl:attribute name="xsi:type">
						<xsl:value-of select="$att"/>
					</xsl:attribute>	
				</xsl:if>
				<xsl:value-of select="$string"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>	

<!--BOX or POINT template-->
<xsl:template match="msp:coverage/msp:location">
	<!--nsdl_ncs does not have an element for units (for indicating the units for northlimit, southlimit etc), it is assumed that the units are then 'signed decimal degrees'. Since nsdl_ncs enforces this in the cataloging tool, do no create or write out a variable for units--> 
	<!--the translate function is used to force the element names of northLimit and southLimit etc. to be lowercase like northlimit and southlimit etc. because the words used in the Dublin Core BOX encoding scheme are all lowercase.-->
		<xsl:param name="tag"/>
		<xsl:param name="att"/>		

<!--< character not allowed in test statements, so use a combinatio of not and ceiling functions-->
		<xsl:variable name="boxORpoint">
			<xsl:for-each select="./@*"> <!--because they are attributes-->
				<xsl:choose>
					<xsl:when test="local-name()='northLimit' or local-name()='southLimit'  ">
						<xsl:if test="floor(.)>=-90 and not(ceiling(.)>90)">
							<xsl:value-of select="concat(translate(local-name(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '=', ., ';')"/>
						</xsl:if>
					</xsl:when>
					<xsl:when test="local-name()='westLimit'  or local-name()='eastLimit'  ">
						<xsl:if test="floor(.)>=-180 and not(ceiling(.)>180)">
							<xsl:value-of select="concat(translate(local-name(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '=', ., ';')"/>
						</xsl:if>
					</xsl:when>
					<xsl:when test="local-name()='upLimit'  or local-name()='downLimit'  ">
						<xsl:if test="boolean(number(.))">
							<xsl:value-of select="concat(translate(local-name(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '=', ., ';')"/>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:if test="string-length(.)>0"><!--would apply to the remaining box/location elements-->
							<xsl:value-of select="concat(translate(local-name(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '=', ., ';')"/>
						</xsl:if>
					</xsl:otherwise>	
				</xsl:choose>
			</xsl:for-each>
<!--to pick up the location name use the next line but skip the local name() call in order to have the placename use name=xxx rather than location=xxx-->
<!--			<xsl:value-of select="concat(translate(local-name(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '=', ., ';')"/>-->
			<xsl:value-of select="concat('name=', ., ';')"/>
		</xsl:variable>

		<xsl:if test="string-length($boxORpoint)>0">
			<xsl:element name="{$tag}">
				<xsl:attribute name="xsi:type">
					<xsl:value-of select="$att"/>
				</xsl:attribute>	
				<xsl:value-of select="$boxORpoint"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>	

<!--RELATION template-->
	<xsl:template match="node()" mode="relation">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="att"/>
		<xsl:param name="string" select="./@url"/>
		<xsl:if test="string-length($string) > 0">
			<xsl:element name="{$tag}">
				<xsl:if test="string-length($att) > 0">
					<xsl:attribute name="xsi:type">
						<xsl:value-of select="$att"/>
					</xsl:attribute>	
				</xsl:if>
				<xsl:value-of select="$string"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>	

<!--RIGHTS template-->
	<xsl:template match="node()" mode="rights">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="att"/>
		<xsl:param name="string" select="."/>
		<xsl:param name="string2" select="./@url"/>
		<xsl:if test="string-length($string) > 0">
			<xsl:element name="{$tag}">
				<xsl:value-of select="$string"/>
			</xsl:element>
		</xsl:if>
		<xsl:if test="string-length($string2) > 0">
			<xsl:element name="{$tag}">
				<xsl:attribute name="xsi:type">
					<xsl:value-of select="$att"/>
				</xsl:attribute>	
				<xsl:value-of select="$string2"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>	

<!--DATE template-->
	<xsl:template match="node()" mode="date">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
<!--when both start and end are present, write as: 
<dct:temporal xsi:type="dct:Period">start=1996-09-03;end=1996-09-06;scheme=W3CDTF</dct:temporal> 
otherwise just write as <dct:temporal>-->
		<xsl:param name="tag"/>
		<xsl:param name="start" select="./@start"/>
		<xsl:param name="end" select="./@end"/>
		<xsl:choose>
			<xsl:when test="string-length($start) > 0 and string-length($end) > 0">
				<xsl:element name="{$tag}">
					<xsl:attribute name="xsi:type">dct:Period</xsl:attribute>
						<xsl:value-of select="concat('start=', $start, ';end=', $end, ';scheme=W3CDTF')"/>
				</xsl:element>			
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="{$tag}">
					<xsl:value-of select="$start"/>
				</xsl:element>			
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>	


<!--SUBJECT template-->
	<xsl:template match="node()" mode="subject">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
<!--over the math, science and education subject, there is a maximum of 4 levels  like (Number and operations:Arithmetic:Decimals:Addition) so just brute these out-->
		<xsl:param name="tag"/>
		<xsl:param name="string" select="."/>
		<xsl:choose>
			<xsl:when test="contains($string, ':')"> <!--level 1-->
				<xsl:element name="{$tag}">
					<xsl:value-of select="substring-before($string, ':' )"/>
				</xsl:element>
				<xsl:choose>
					<xsl:when test="contains(substring-after($string, ':'), ':')"> <!--2 levels-->
						<xsl:element name="{$tag}">
							<xsl:value-of select="substring-before(substring-after($string, ':'), ':')"/>
						</xsl:element>
						<xsl:choose>
							<xsl:when test="contains(substring-after(substring-after($string, ':'), ':'), ':')"> <!--3 levels-->
								<xsl:element name="{$tag}">
									<xsl:value-of select="substring-before(substring-after(substring-after($string, ':'), ':'), ':')"/>
								</xsl:element>
								<xsl:element name="{$tag}"><!--4 levels-->
									<xsl:value-of select="substring-after(substring-after(substring-after($string, ':'), ':'), ':')"/>
								</xsl:element>
							</xsl:when>
							<xsl:otherwise> <!--3 levels-->
								<xsl:element name="{$tag}">
									<xsl:value-of select="substring-after(substring-after($string, ':'), ':')"/>
								</xsl:element>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise> <!--2 levels-->
						<xsl:element name="{$tag}">
							<xsl:value-of select="substring-after($string, ':')"/>
						</xsl:element>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="{$tag}"> <!--1 level-->
					<xsl:value-of select="$string"/>
				</xsl:element>				
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>	
	
</xsl:stylesheet>
<!--LICENSE AND COPYRIGHT
The contents of this file are subject to the Educational Community License v1.0 (the "License"); you may not use this file except in compliance with the License. You should obtain a copy of the License from http://www.opensource.org/licenses/ecl1.php. Files distributed under the License are distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the specific language governing rights and limitations under the License. Copyright 2002-2009 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR). All rights reserved.-->