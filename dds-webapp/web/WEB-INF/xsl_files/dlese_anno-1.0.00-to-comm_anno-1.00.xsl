<?xml version="1.0"?>
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:a="http://www.dlese.org/Metadata/annotation" 
	xmlns:adn="http://adn.dlese.org" 
	xmlns:dds="http://www.dlese.org/Metadata/ddsws" 
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes=" xsi a adn dds xsd" 
	version="1.0">
		

<!--PURPOSE-->
<!-- **************************************-->
<!--To transform selected data (content) of certain (selected) annotation collections within the Digital Library for Earth System Education (DLESE) to NSDL-DC-->

<!--LICENSE INFORMATION and CREDITS-->
<!-- *****************************************************-->
<!--Date created: 2011-03-09 by Katy Ginger, Integrated Information Services, University Corporation for Atmospheric Research (UCAR)-->
<!--LICENSE AND COPYRIGHT
The contents of this file are subject to the Educational Community License v1.0 (the "License"); you may not use this file except in compliance with the License. You should obtain a copy of the License from http://www.opensource.org/licenses/ecl1.php. Files distributed under the License are distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the specific language governing rights and limitations under the License. Copyright 2002-2011 by Integrated Information Services, University Corporation for Atmospheric Research (UCAR). All rights reserved.-->

<!--ASSUMPTIONS-->
<!-- **************************************-->
<!--This transform uses the dlese_anno itemID to do a look up at DLESE to get the URL and title from the appropriate ADN 0.6.50 record; If the URL to the annotated resource is not available, then a URL of http://url.notin.dlese/ is used-->
<!--Assumes dlese_anno records are XML valid and therefore expects required dlese_anno metadata to be present and appropriate. Thus, does not check for the presence of required metadata-->
<!--Assumes annotations are textual-->

	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>


<!--VARIABLES used throughout the transform-->
<!-- *****************************************************-->
<!--variables for accessing DLESE id prefixes files-->
<xsl:variable name="DDSWSID">http://www.dlese.org/dds/services/ddsws1-1?verb=GetRecord&amp;id=</xsl:variable>	


<!--TRANSFORMATION CODE-->
<!-- **************************************-->
<xsl:template match="a:annotationRecord">
	<comm_anno xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://ns.nsdl.org/ncs/comm_anno" xsi:schemaLocation="http://ns.nsdl.org/ncs/comm_anno http://ns.nsdl.org/ncs/comm_anno/1.00/schemas/comm_anno.xsd">

<!--VARIABLE-->
<!-- itemID indicates the DLESE resource being annotated; this is the resource ID (later turned into a URL) that is used in the annotatedID field-->
	<xsl:variable name="id">
		<xsl:value-of select="a:itemID"/>
	</xsl:variable>

<!--VARIABLE-->
<!--check to see if in DLESE as a ADN record-->
<!--reading ADN records in webservices-->					
	<xsl:variable name="adn">
		<xsl:value-of select="document(concat($DDSWSID, $id))//adn:metaMetadata/adn:catalogEntries/adn:catalog/@entry" />

<!--if using ddsws1-0, then no namespaces are need and you would use the command below-->
<!--		<xsl:value-of select="document(concat($DDSWSID, $id))//metaMetadata/catalogEntries/catalog/@entry" />-->
	</xsl:variable>
		
<!--VARIABLE-->
<!--if in DLESE, then grab ADN primary URL because it resolves the itemID above to a URL-->
	<xsl:variable name="identifier">
		<xsl:choose>
			<xsl:when test="$id = $adn">
				<xsl:value-of select="document(concat($DDSWSID, $id))//adn:technical/adn:online/adn:primaryURL"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>NaN</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

<!--VARIABLE-->
<!--type of annotation-->
	<xsl:variable name="annoType">
		<xsl:value-of select="a:annotation/a:type"/>
	</xsl:variable>


<!--start writing elements below-->

<!--recordID using dlese_anno record id-->
		<xsl:element name="recordID" namespace="http://ns.nsdl.org/ncs/comm_anno">
			<xsl:value-of select="a:service/a:recordID"/>
		</xsl:element>

<!--date using dlese_anno date-->
		<xsl:element name="date" namespace="http://ns.nsdl.org/ncs/comm_anno">
			<xsl:attribute name="created">
				<xsl:value-of select="a:service/a:date/@created"/>
			</xsl:attribute>
		</xsl:element>

<!--annotatedID using dlese_anno id and look up for the ADN URL-->
		<xsl:element name="annotatedID" namespace="http://ns.nsdl.org/ncs/comm_anno">
			<xsl:attribute name="idType">URL</xsl:attribute>
			<xsl:choose>
				<xsl:when test="$identifier ='NaN' ">
					<xsl:text>http://url.notin.dlese/</xsl:text>
				</xsl:when>
				<xsl:otherwise> 
					<xsl:value-of select="$identifier"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
		
<!--title using adn title-->
		<xsl:choose>
			<xsl:when test="$identifier ='NaN' ">
<!--do nothing because there is no data to complete a title field and title is optional in comm_anno-->
			</xsl:when>
			<xsl:otherwise> 
				<xsl:element name="title" namespace="http://ns.nsdl.org/ncs/comm_anno">
<!--					<xsl:value-of select="document(concat($DDSWSID, $id))//general/title"/>-->
					<xsl:value-of select="concat('Annotation for ', document(concat($DDSWSID, $id))//adn:general/adn:title )"/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
		
<!--ratingInfo and rating using dlese_anno rating-->
		<xsl:apply-templates select="a:annotation/a:content/a:rating"/>
	
<!--text using dlese_anno description-->
<!--all dlese_anno types are in comm_anno types except for misconception which needs to be mapped to conception-->
		<xsl:if test="string-length(a:annotation/a:content/a:description) > 0">
			<xsl:choose>
				<xsl:when test="$annoType = 'Educational standard' ">
					<xsl:element name="standardText" namespace="http://ns.nsdl.org/ncs/comm_anno">
						<xsl:attribute name="standardsName">Standard</xsl:attribute>
						<xsl:value-of select="a:annotation/a:content/a:description"/>
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="text" namespace="http://ns.nsdl.org/ncs/comm_anno">
						<xsl:attribute name="type">
							<xsl:choose>
								<xsl:when test="$annoType = 'Misconception' ">
									<xsl:text>Conception</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$annoType"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:value-of select="a:annotation/a:content/a:description"/>
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
			
<!--url using dlese_anno url-->
<!--all dlese_anno types are in comm_anno types except for misconception which needs to be mapped to conception-->
<!--check to see if the URL is an ASN identifier URL and then assign educational standard as the type of annotation-->
		<xsl:if test="string-length(a:annotation/a:content/a:url) > 0">
			<xsl:choose>
			<!--if an ASN id URL is used map it to the comm_anno ASNstandard element rather than the url element-->
				<xsl:when test="contains(a:annotation/a:content/a:url, 'http://purl.org/ASN')">
					<xsl:element name="ASNstandard" namespace="http://ns.nsdl.org/ncs/comm_anno">
						<xsl:value-of select="a:annotation/a:content/a:url"/>	
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="url" namespace="http://ns.nsdl.org/ncs/comm_anno">
						<xsl:attribute name="type">
							<xsl:choose>
							<!--mapping dlese_anno Misconception to comm_anno Conception-->
								<xsl:when test="$annoType = 'Misconception' ">
									<xsl:text>Conception</xsl:text>
								</xsl:when>
							<!--all other dlese_anno annotation type are in comm_anno and just take the value of them-->
								<xsl:otherwise>
									<xsl:value-of select="$annoType"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:value-of select="a:annotation/a:content/a:url"/>
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>

<!--contributors using dlese_anno contributors-->
<!--check to see if dlese_anno contributors content is present; if so then write the comm_anno contributors tag but in order to share it, it must contain a share=true somewhere; if no share=true on any contributors then the comm_anno contributors tag may not need to be written-->
		<xsl:variable name="share">
			<xsl:for-each select="a:annotation/a:contributors/a:contributor">
				<xsl:value-of select="./@share"/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="contains($share, 'true')">
			<xsl:element name="contributors" namespace="http://ns.nsdl.org/ncs/comm_anno">
		<!--contributors using dlese_anno contributors-->
				<xsl:apply-templates select="a:annotation/a:contributors/a:contributor/a:person"/>
		<!--contributors using dlese_anno contributors-->
				<xsl:apply-templates select="a:annotation/a:contributors/a:contributor/a:organization"/>
			</xsl:element>
		</xsl:if>

<!--end comm_anno-->
		</comm_anno>
	</xsl:template>

<!--TEMPLATES TO APPLY-->
<!--*********************************-->
<!--organized in alphabetical order-->

<!--CONTRIBUTOR - ORGANIZATION template-->
<!--NOTE: dlese_anno has a share attribute for contributors; only share those contributors that have share=true-->
<!--NOTE: no need to map role vocabs between dlese_anno and comm_anno since they are the same as of 2011-03-09-->
<!--NOTE: since dlese_anno only required instName need to check for the presence of content in the other tags-->
<xsl:template match="a:annotation/a:contributors/a:contributor/a:organization">
	<xsl:if test="../@share = 'true' ">
		<xsl:element name="contributor" namespace="http://ns.nsdl.org/ncs/comm_anno">
			<xsl:attribute name="role">
				<xsl:value-of select="../@role"/>
			</xsl:attribute>
			<xsl:attribute name="date">
				<xsl:value-of select="../@date"/>
			</xsl:attribute>
			<xsl:attribute name="share">true</xsl:attribute>
			<xsl:element name="organization" namespace="http://ns.nsdl.org/ncs/comm_anno" >
				<xsl:element name="instName" namespace="http://ns.nsdl.org/ncs/comm_anno">
					<xsl:value-of select="a:instName"/>
				</xsl:element>
				<xsl:if test="string-length(a:instDept) > 0">
					<xsl:element name="instDept" namespace="http://ns.nsdl.org/ncs/comm_anno">
						<xsl:value-of select="a:instDept"/>
					</xsl:element>
				</xsl:if>
				<xsl:if test="string-length(a:instPosition) > 0">
					<xsl:element name="instPosition" namespace="http://ns.nsdl.org/ncs/comm_anno">
						<xsl:value-of select="a:instPosition"/>
					</xsl:element>
				</xsl:if>
				<xsl:if test="string-length(a:url) > 0">
					<xsl:element name="url" namespace="http://ns.nsdl.org/ncs/comm_anno">
						<xsl:value-of select="a:url"/>
					</xsl:element>
				</xsl:if>
				<xsl:if test="string-length(a:email) > 0">
					<xsl:element name="email" namespace="http://ns.nsdl.org/ncs/comm_anno">
						<xsl:value-of select="a:email"/>
					</xsl:element>
				</xsl:if>
			</xsl:element>
		</xsl:element>
	</xsl:if>
</xsl:template>

<!--CONTRIBUTOR - PERSON template-->
<!--NOTE: dlese_anno has a share attribute for contributors; only share those contributors that have share=true-->
<!--NOTE: no need to map role vocabs between dlese_anno and comm_anno since they are the same as of 2011-03-09-->
<!--NOTE: since dlese_anno only required nameFirst, nameLast and instName need to check for the presence of content in the other tags-->
<!--NOTE: dlese_anno has emailAlt but comm_anno does not; so do not map-->
<xsl:template match="a:annotation/a:contributors/a:contributor/a:person">
	<xsl:if test="../@share = 'true' ">
		<xsl:element name="contributor" namespace="http://ns.nsdl.org/ncs/comm_anno">
			<xsl:attribute name="role">
				<xsl:value-of select="../@role"/>
			</xsl:attribute>
			<xsl:attribute name="date">
				<xsl:value-of select="../@date"/>
			</xsl:attribute>
			<xsl:attribute name="share">true</xsl:attribute>
			<xsl:element name="person" namespace="http://ns.nsdl.org/ncs/comm_anno" >
				<xsl:if test="string-length(a:nameTitle) > 0">
					<xsl:element name="nameTitle" namespace="http://ns.nsdl.org/ncs/comm_anno">
						<xsl:value-of select="a:nameTitle"/>
					</xsl:element>
				</xsl:if>
				<xsl:element name="nameFirst" namespace="http://ns.nsdl.org/ncs/comm_anno">
					<xsl:value-of select="a:nameFirst"/>
				</xsl:element>
				<xsl:if test="string-length(a:nameMiddle) > 0">
					<xsl:element name="nameMiddle" namespace="http://ns.nsdl.org/ncs/comm_anno">
						<xsl:value-of select="a:nameMiddle"/>
					</xsl:element>
				</xsl:if>
				<xsl:element name="nameLast" namespace="http://ns.nsdl.org/ncs/comm_anno">
					<xsl:value-of select="a:nameLast"/>
				</xsl:element>
				<xsl:element name="instName" namespace="http://ns.nsdl.org/ncs/comm_anno">
					<xsl:value-of select="a:instName"/>
				</xsl:element>
				<xsl:if test="string-length(a:instDept) > 0">
					<xsl:element name="instDept" namespace="http://ns.nsdl.org/ncs/comm_anno">
						<xsl:value-of select="a:instDept"/>
					</xsl:element>
				</xsl:if>
				<xsl:if test="string-length(a:instPosition) > 0">
					<xsl:element name="instPosition" namespace="http://ns.nsdl.org/ncs/comm_anno">
						<xsl:value-of select="a:instPosition"/>
					</xsl:element>
				</xsl:if>
					<xsl:element name="email" namespace="http://ns.nsdl.org/ncs/comm_anno">
						<xsl:value-of select="a:email"/>
					</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:if>
</xsl:template>

<!--RATING template-->
<!--dlese_anno uses text words and not numbers for rating info; need to map these to the numbers used in comm_anno-->
<xsl:template match="a:annotation/a:content/a:rating">
	<xsl:element name="ratingInfo" namespace="http://ns.nsdl.org/ncs/comm_anno">
		<xsl:element name="rating" namespace="http://ns.nsdl.org/ncs/comm_anno">
			<xsl:attribute name="min">1</xsl:attribute>
			<xsl:attribute name="max">5</xsl:attribute>
			<xsl:choose>
				<xsl:when test="contains(., 'One')">
					<xsl:text>1</xsl:text>
				</xsl:when>
				<xsl:when test="contains(., 'Two')">
					<xsl:text>2</xsl:text>
				</xsl:when>
				<xsl:when test="contains(., 'Three')">
					<xsl:text>3</xsl:text>
				</xsl:when>
				<xsl:when test="contains(., 'Four')">
					<xsl:text>4</xsl:text>
				</xsl:when>
				<xsl:when test="contains(., 'Five')">
					<xsl:text>5</xsl:text>
				</xsl:when>
			</xsl:choose>
		</xsl:element>
	</xsl:element>
</xsl:template>
<!--end of templates-->	

</xsl:stylesheet>