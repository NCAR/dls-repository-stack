<?xml version="1.0"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:osm="http://nldr.library.ucar.edu/metadata/osm_collect"
    xmlns:dl="http://collection.dlese.org"
	exclude-result-prefixes="osm dl"
    version="1.0">

<!--PURPOSE-->
<!-- **************************************-->
<!--To transform osm_collect version 1.0 metadata records to the dlese_collect 1.0.00 format.-->


<!--LICENSE INFORMATION and CREDITS-->
<!-- *****************************************************-->
<!--Date created: 2010-11-11 by Katy Ginger, University Corporation for Atmospheric Research (UCAR)-->
<!--License information: See below.-->

	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>


<!--TRANSFORMATION CODE-->
<!-- ********************************************************-->
	<xsl:template match="osm:collectionRecord">
		<collectionRecord xmlns="http://collection.dlese.org" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://collection.dlese.org http://www.dlese.org/Metadata/collection/1.0.00/collection.xsd">
	
			<general>

<!--fullTitle using title-->
				<fullTitle>
					<xsl:value-of select="osm:longTitle"/>
				</fullTitle>

<!--shortTitle using title-->
				<shortTitle>
					<xsl:value-of select="osm:shortTitle"/>
				</shortTitle>

<!--description using description-->
				<description>
					<xsl:value-of select="osm:description"/>
				</description>
				<language>en</language>
				<subjects>
					<subject>DLESE:Other</subject>
				</subjects>
				<gradeRanges>
					<gradeRange>DLESE:To be supplied</gradeRange>
				</gradeRanges>
				<cost>DLESE:Unknown</cost>
				<policies>
					<policy/>
				</policies>
			</general>
			<lifecycle>
				<contributors>
					<contributor date="2010-02-14" role="Contact">
						<organization>
							<instName>University Corporation for Atmospheric Research (UCAR)</instName>
						</organization>
					</contributor>
				</contributors>
		</lifecycle>
		<approval>
			<collectionStatuses>
				<xsl:element name="collectionStatus" namespace="http://collection.dlese.org">
					<xsl:attribute name="date">
						<xsl:value-of select="osm:accessionDate"/>
					</xsl:attribute>
					<xsl:attribute name="state">Accessioned</xsl:attribute>
				</xsl:element>
			</collectionStatuses>
			<contributors>
				<contributor date="2010-02-14" role="Collection start approver">
					<organization>
						<instName>University Corporation for Atmospheric Research (UCAR)</instName>
					</organization>
				</contributor>
			</contributors>
		</approval>
		<access>
			<xsl:element name="key" namespace="http://collection.dlese.org">
				<xsl:attribute name="static">false</xsl:attribute>
				<xsl:attribute name="redistribute">
					<xsl:value-of select="osm:redistribute"/>
				</xsl:attribute>
				<xsl:attribute name="libraryFormat">
					<xsl:value-of select="osm:itemFormat"/>
				</xsl:attribute>
				<xsl:value-of select="osm:key"/>
			</xsl:element> <!--end key element-->
			<drc>false</drc>
		</access>
			<metaMetadata>
				<catalogEntries>
<!--enter the OSM collection record id as the DLESE collection record id but need to add a preface label like OSM to make the DDS not think there are 2 records with the same id #-->
					<xsl:element name="catalog" namespace="http://collection.dlese.org">
						<xsl:attribute name="entry">
							<xsl:value-of select="concat('DDS-', osm:recordID)"/>
						</xsl:attribute>
					</xsl:element><!--end catalog element-->
				</catalogEntries>
				<dateInfo created="2010-02-09" lastModified="2010-02-14T09:30:47" deaccessioned="2010-02-14" accessioned="2010-02-14"/>
				<statusOf status="Done"/>
				<language>en-us</language>
				<scheme>Digital Library for Earth System Education (DLESE) Collection Metadata</scheme>
				<copyright/>
				<termsOfUse/>
			</metaMetadata>
		</collectionRecord>
	</xsl:template>
</xsl:stylesheet>
<!--LICENSE AND COPYRIGHT
The contents of this file are subject to the Educational Community License v1.0 (the "License"); you may not use this file except in compliance with the License. You should obtain a copy of the License from http://www.opensource.org/licenses/ecl1.php. Files distributed under the License are distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the specific language governing rights and limitations under the License. Copyright 2002-2009 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR). All rights reserved.-->
