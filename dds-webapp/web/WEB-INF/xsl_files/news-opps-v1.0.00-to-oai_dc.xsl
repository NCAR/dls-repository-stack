<?xml version="1.0"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:n="http://newsopps.dlese.org"
    xmlns:d="http://metadatafields.dlese.org"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:nsdl_dc="http://ns.nsdl.org/nsdl_dc_v1.01"
    xmlns:dct="http://purl.org/dc/terms/"
    xmlns:ieee="http://www.ieee.org/xsd/LOMv1p0"
    exclude-result-prefixes="xsi d dc xsi nsdl_dc dct ieee" 
    version="1.0">
    
<!--ORGANIZATION OF THIS FILE-->
<!--This file is organized into the following sections:
A. ASSUMPTIONS and PURPOSE
B. VARIABLES
C. TRANSFORMATION CODE
D. TEMPLATES TO APPLY-->

<!--Date created 2005-02-16 by Katy Ginger, DLESE Program Center, University Corporation for Atmospheric Research (UCAR)-->


<!--A. ASSUMPTIONS and PURPOSE-->
<!--assumption: Works on news and opps version 1.0.00 records-->
<!--purpose: transforms new and opps metadata records into simple Dublin Core metadata records suitable for use over OAI-PMH (Open Archives Initiative - Protocol for Metadata Harvesting-->

	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>


<!--B. VARIABLES used throughout the transform-->
<!--variable for adding a line return-->
	<xsl:variable name="newline">
<xsl:text>
</xsl:text>
	</xsl:variable>
<!--variable for accessing news and opps term definition information-->
<xsl:variable name="vocabsURL">http://www.dlese.org/Metadata/news-opps/1.0.00/</xsl:variable>	


<!--C. TRANSFORMATION CODE-->
	<xsl:template match="n:news-oppsRecord">
		<xsl:text disable-output-escaping="yes">&lt;oai_dc:dc xmlns:oai_dc=&quot;http://www.openarchives.org/OAI/2.0/oai_dc/&quot;
xsi:schemaLocation=&quot;http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd&quot; xmlns:dc=&quot;http://purl.org/dc/elements/1.1/&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;&gt;
</xsl:text>

<!--dc:title-->
<!--since title is required metadata in news-opps, no need to check to see if data is present-->
		<xsl:element name="dc:title">
			<xsl:value-of select="n:title"/>
		</xsl:element>	

<!--dc:creator, dc:publisher and dc:contributor from the contributor tag set-->
		<xsl:apply-templates select="n:contributors/n:contributor"/>
		<!--see template CONTRIBUTOR-->

<!--dc:subject - from topic, keyword, annoucement, audience, diversity-->
		<!--see template DC:SUBJECT-->
		<xsl:apply-templates select="n:topics/n:topic"/>
		<xsl:apply-templates select="n:keywordsn/n:keyword"/>
		<xsl:apply-templates select="n:announcements/n:announcement"/>
		<xsl:apply-templates select="n:audiences/n:audience"/>
		<xsl:apply-templates select="n:diversities/n:diversity"/>
		<xsl:apply-templates select="n:keywords/n:keyword"/>

<!--dc:description-->
<!--since description is required metadata in news-opps, no need to check to see if data is present-->
		<xsl:element name="dc:description">
			<xsl:value-of select="n:description"/>
		</xsl:element>	
	
<!--dc:format -->
<!--dc:format allows the terms: text, multipart, message, application, image, audio, video or model to be used by themselves. Generally, select the mime type from the NSDL mime type list at: http://ns.nsdl.org/schemas/mime_type/mime_type_v1.00.xsd-->	
		
<!--determine dc:format using announcementURL-->
		<xsl:element name="dc:format">
			<xsl:apply-templates select="n:announcementURL"/>
			<!--see template ANNOUNCEMENTURL-->
		</xsl:element>

<!--dc:type-->
<!--vocabulary mapping is necessary-->
<!--using the Dublin Core Metadata Initiative (DCMI) type vocabulary at: http://dublincore.org/schemas/xmls/qdc/2004/06/14/dcmitype.xsd, the best mapping for the news and opps announcement type is to use Text or Event (note: NSDL has News as a type)-->
<!--to differeniate between  Text and Event, make a variable containing all the announcement types and then test them-->
		<xsl:variable name="allTypes">
			<xsl:for-each select="n:annoucements/n:announcement">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
	
		<xsl:choose>
			<!--dc:type: Event-->
			<xsl:when test="contains($allTypes, 'Workshop') or contains($allTypes, 'Conference')">
				<xsl:element name="dc:type">
					<xsl:text>Event</xsl:text>
				</xsl:element>
			</xsl:when>

			<!--dc:type: Text-->
			<xsl:otherwise>
				<xsl:element name="dc:type">
					<xsl:text>Text</xsl:text>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>

<!--dc:identifier - using news-opps id numbers-->
<!--all news-opps records have a catalog record number that can be used as an indentifier for that collection-->
<!--	<xsl:element name="dc:identifier">
			<xsl:value-of select="n:recordID"/>
		</xsl:element> -->

<!--dc:identifier - using news-opps urls-->
<!--simple Dublin Core does not deal with the attribute dct:URI, so no worry about spaces in urls-->
		<xsl:element name="dc:identifier">
			<xsl:value-of select="n:announcementURL"/>
		</xsl:element>

<!--dc:source-->		
<!--news-opps does not collect dc:source information-->

<!--dc:language-->
		<xsl:element name="dc:language">
			<xsl:value-of select="n:language/@resource"/>
		</xsl:element>

<!--dc:rights-->
<!--new-opps does not collect rights information. So do not create the dc:rights element because its value is basically unknown.-->

	
<!--dc:coverage and dc:spatial general information-->
<!--use news-opps location information-->
		<xsl:apply-templates select="n:locations/n:location"/>
		<!--see template LOCATION-->


<!--dc:coverage time information-->
 		<xsl:apply-templates select="n:otherDates/n:otherDate"/>
		<!--see template OTHERDATE-->
		

<!--end oai_dc:dc-->
		<xsl:value-of select="$newline"/>
		<xsl:text disable-output-escaping="yes">&lt;/oai_dc:dc&gt;</xsl:text>
	</xsl:template>


<!--D. TEMPLATES TO APPLY-->
<!--organized in the following order-->
<!--1. ANNOUNCEMENTURL - determines the mime type of the URL-->
<!--2. CONTRIBUTOR selects DC creator, publisher or contributor based on the news and opps contributor role-->
<!--3. DC:SUBJECT writes DC subject from news and opps keywords, audiene, topic, diversity and announcement-->
<!--4. LOCATION translates the encoded country codes into formal country names -->
<!--5. ORGANIZATION - writes the organization -->
<!--6. OTHERDATE writes date information plus any descriptive information associated with the dates-->
<!--7. PERSON writes person information-->
<!--8. SOURCE writes source information-->

<!--1. ANNOUNCEMENTURL template  writes mime type based on the extenstion of the URL-->
<!--makes an assumption of text/html if no match is found-->
	<xsl:template match="n:announcementURL">
		<xsl:choose>
			<xsl:when test="contains(., '.pdf')">
				<xsl:text>application/pdf</xsl:text>
			</xsl:when>											
			<xsl:when test="contains(., '.mov')">
				<xsl:text>video/quicktime</xsl:text>
			</xsl:when>											
			<xsl:when test="contains(., '.mpg')">
				<xsl:text>video/quicktime</xsl:text>
			</xsl:when>											
			<xsl:when test="contains(., '.jpg')">
				<xsl:text>image/jpeg</xsl:text>
			</xsl:when>											
			<xsl:when test="contains(., '.jpeg')">
				<xsl:text>image/jpeg</xsl:text>
			</xsl:when>											
			<xsl:when test="contains(., '.JPG')">
				<xsl:text>image/jpeg</xsl:text>
			</xsl:when>											
			<xsl:when test="contains(., '.gif')">
				<xsl:text>image/gif</xsl:text>
			</xsl:when>											
			<xsl:when test="contains(., '.txt')">
				<xsl:text>text/plain</xsl:text>
			</xsl:when>											
			<xsl:otherwise>
				<xsl:text>text/html</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>			

<!--2. CONTRIBUTOR template-->
	<xsl:template match="n:contributor">
		<xsl:choose>
			<xsl:when test="./@role='Contact'">
				<xsl:element name="dc:contributor">
					<xsl:apply-templates select="n:person"/>
					<xsl:apply-templates select="n:organization"/>
					<xsl:apply-templates select="n:source"/>
				</xsl:element>
			</xsl:when>

			<xsl:when test="./@role='Author'">
				<xsl:element name="dc:creator">
					<xsl:apply-templates select="n:person"/>
					<xsl:apply-templates select="n:organization"/>
					<xsl:apply-templates select="n:source"/>
				</xsl:element>
			</xsl:when>

			<xsl:when test="./@role='Publisher' or ./@role='Sponsor' or ./@role='Newsfeed' ">
				<xsl:element name="dc:publisher">
					<xsl:apply-templates select="n:person"/>
					<xsl:apply-templates select="n:organization"/>
					<xsl:apply-templates select="n:source"/>
				</xsl:element>
			</xsl:when>
		</xsl:choose>
	</xsl:template>			

<!--3. DC:SUBJECT template-->
	<xsl:template match="n:keyword | n:audience | n:topic | n:diversity | n:announcement">
		<xsl:element name="dc:subject">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>			

<!--4. LOCATION template-->
	<xsl:template match="n:location">
		<xsl:variable name="countryCode">
			<xsl:value-of select="."/>
		</xsl:variable>
		<xsl:variable name="city">
			<xsl:value-of select="./@city"/>
		</xsl:variable>
		<xsl:for-each select="document(concat($vocabsURL, 'fields/location-no-fields-en-us.xml') )/d:metadataFieldInfo/d:field/d:terms/d:termAndDeftn">
			<xsl:variable name="vocabTerm">
				<xsl:value-of select="./@vocab" /> 
			</xsl:variable>
			<xsl:variable name="country">
				<xsl:value-of disable-output-escaping="yes" select="."/>
			</xsl:variable>
			<xsl:if test="$vocabTerm = $countryCode">
				<xsl:element name="dc:coverage">
					<xsl:choose>
						<xsl:when test="string-length($city) > 0">
							<xsl:value-of disable-output-escaping="no" select="concat($city, ', ', $country)" /> 
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of disable-output-escaping="no" select="$country" /> 
						</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

<!--5. ORGANIZATION template-->
	<xsl:template match="n:organization">
		<xsl:choose>
			<xsl:when test="string-length(n:instDept)>0">
				<xsl:value-of select="concat(n:instDept,', ',n:instName)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="n:instName"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>			

<!--6. OTHERDATE template-->
	<xsl:template match="n:otherDate">
		<xsl:element name="dc:coverage">
			<xsl:choose>
				<xsl:when test="contains(./@type, 'eventStart')">
					<xsl:value-of select="concat(./@descr, 'Starts ', .)"/>
				</xsl:when>
				<xsl:when test="contains(./@type, 'eventStop')">
					<xsl:value-of select="concat(./@descr, 'Stops ', .)"/>
				</xsl:when>
				<xsl:when test="contains(./@type, 'applyBy')">
					<xsl:value-of select="concat(./@descr, 'Apply by ', .)"/>
				</xsl:when>
				<xsl:when test="contains(./@type, 'due')">
					<xsl:value-of select="concat(./@descr, ' ', ./@type, ' ', .)"/>
				</xsl:when>
			</xsl:choose>
		</xsl:element>
	</xsl:template>			

<!--7. PERSON template-->
	<xsl:template match="n:person">
		<xsl:value-of select="concat(n:nameFirst,' ',n:nameLast)"/>
	</xsl:template>			

<!--8. SOURCE template -->
	<xsl:template match="n:source">
		<xsl:value-of select="."/>
	</xsl:template>			

</xsl:stylesheet>