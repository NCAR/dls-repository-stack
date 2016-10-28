<?xml version="1.0"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi ="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:osm="http://nldr.library.ucar.edu/metadata/osm"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="xsi xs osm"
    version="1.1">

<!--PURPOSE: to transform OSM version 1.1 records into the DataCite Kernel 2.2 metadata records-->
<!--CREATION: 2012-05-24 by Katy Ginger, University Corporation for Atmospheric Research (UCAR)-->
<!--HISTORY: none-->

	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>

<!--VARIABLES-->
<!-- ************************************************** -->
	<xsl:variable name="datacite">http://datacite.org/schema/kernel-2.2</xsl:variable>


<!--TRANSFORMATION CODE-->
<!-- ************************************************** -->
	<xsl:template match="*|/">
		<xsl:apply-templates select="osm:record"/>
	</xsl:template>

<!--TRANSFORMATION CODE-->
<!-- ********************************************************-->
	<xsl:template match="osm:record">
		<resource xmlns="http://datacite.org/schema/kernel-2.2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://datacite.org/schema/kernel-2.2 http://schema.datacite.org/meta/kernel-2.2/metadata.xsd">

<!--identifier using idNumber/@type = DOI-->
		<xsl:apply-templates select="osm:classify/osm:idNumber[@type='DOI'] " mode="process">
			<xsl:with-param name="tag">identifier</xsl:with-param>
			<xsl:with-param name="att">identifierType</xsl:with-param>
			<xsl:with-param name="attcon">DOI</xsl:with-param>
		</xsl:apply-templates>				


<!--creators, creator, creatorName using osm:contributors/osm:person and organization[@role='Author']-->
<!--	variables to determine if any creators are present-->
		<xsl:variable name="allcreators">
			<xsl:for-each select="osm:contributors/osm:person/@role">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="allcreatororgs">
			<xsl:for-each select="osm:contributors/osm:organization/@role">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<!-- only OSM role that is a creator is Author-->
		<!--need to account for if no author is present in the OSM data because creators is required in DataCite-->
		<!--3 cases are present-->

		<creators>
			<xsl:choose>
			<!--CASE 1: if there are OSM people authors, then do people authors only-->
				<xsl:when test="contains($allcreators, 'Author')">
					<xsl:apply-templates select="osm:contributors/osm:person[@role='Author'] " mode="name">
						<xsl:with-param name="tag1">creator</xsl:with-param>
						<xsl:with-param name="tag2">creatorName</xsl:with-param>
					</xsl:apply-templates>
				</xsl:when>
			<!--CASE 2: if there are no OSM people authors, check to see if OSM org authors are present and process them-->
				<xsl:when test="not(contains($allcreators, 'Author')) and contains($allcreatororgs, 'Author')">
					<xsl:apply-templates select="osm:contributors/osm:organization[@role='Author'] " mode="org">
						<xsl:with-param name="tag1">creator</xsl:with-param>
						<xsl:with-param name="tag2">creatorName</xsl:with-param>
					</xsl:apply-templates>
				</xsl:when>
			<!--CASE 3: if there are no OSM people authors and no OSM org authors, then assign UCAR as creator-->
				<xsl:otherwise>
					<creator>
							<creatorName>University Corporation for Atmospheric Rsearch (UCAR)</creatorName>
					</creator>
				</xsl:otherwise>
			</xsl:choose>
		</creators>
		
<!--titles, title using title-->
<!--OSM title is required; so no need to determine if any titles are present, just write it-->
	<titles>
		<xsl:apply-templates select="osm:general/osm:title" mode="process">
			<xsl:with-param name="tag">title</xsl:with-param>
		</xsl:apply-templates>
	</titles>

<!--publisher using assigned value; do not use first occurrence: osm:contributors/osm:organization[@role='Publisher']/osm:affiliation/osm:instName"/-->
<!--publisher occurs only once in DataCite-->
	<publisher>UCAR/NCAR</publisher>

	
<!--publicationYear using first occurrence of coverage/date[@type='Published'] and fails gracefully if no published value is present-->
<!--DataCite publicationYear in only YYYY; so peel off these digits from the OSM date info-->

<!--both scenarios need to account for embargoes-->
		<xsl:variable name="alldateRange">
			<xsl:for-each select="osm:coverage/osm:dateRange/@type">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:choose> 
		<!--note: only case 1a or 1b will be executed even if both embargoes are present; this is good because DataCite only allows 1 publicationYear-->
		<!--case 1a: author embargo present-->
			<xsl:when test="contains($alldateRange, 'Author embargo')">
			<!--OSM dateRange/@end is optional; so if no value assign 5000 in a template-->
				<xsl:apply-templates select="osm:coverage/osm:dateRange[@type='Author embargo']" mode="dateEnd">
					<xsl:with-param name="tag">publicationYear</xsl:with-param>
				</xsl:apply-templates>
			</xsl:when>
		<!--case 1b: donor embargo present-->
			<xsl:when test="contains($alldateRange, 'Donor embargo')">
			<!--OSM dateRange/@end is optional; so if no value assign 5000 in a template-->
				<xsl:apply-templates select="osm:coverage/osm:dateRange[@type='Donor embargo']" mode="dateEnd">
					<xsl:with-param name="tag">publicationYear</xsl:with-param>
				</xsl:apply-templates>
			</xsl:when>
		<!--case 1c: publisher embargo present-->
			<xsl:when test="contains($alldateRange, 'Publisher embargo')">
			<!--OSM dateRange/@end is optional; so if no value assign 5000 in a template-->
				<xsl:apply-templates select="osm:coverage/osm:dateRange[@type='Publisher embargo']" mode="dateEnd">
					<xsl:with-param name="tag">publicationYear</xsl:with-param>
				</xsl:apply-templates>
			</xsl:when>
<!--case 2: no embargoes-->			
			<xsl:otherwise>
<!--non-graceful fail to force an invalid record-->
				<publicationYear>
					<xsl:value-of select="substring(osm:coverage/osm:date[@type='Published'], 1, 4)"/>
				</publicationYear>

<!--fail gracefully so that a valid record will always be created even if there is no 'Published' date-->
<!--
				<xsl:variable name="allpubyr">
					<xsl:for-each select="osm:coverage/osm:date/@type">
						<xsl:value-of select="."/>
					</xsl:for-each>
				</xsl:variable>
				<xsl:element name="publicationYear">
					<xsl:choose>
						<xsl:when test="contains($allpubyr, 'Published')">
							<xsl:value-of select="substring(osm:coverage/osm:date[@type='Published'], 1, 4)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>2012</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
-->		
			</xsl:otherwise>
		</xsl:choose>

<!--subject using keyword, LCSHsubject, OSMsubject, eventName-->
<!--determine if any keywords or other subjects are present-->
<!--not using DataCite subjectScheme on subject-->
		<xsl:variable name="allkeys">
			<xsl:for-each select="osm:general/osm:keyword">
				<xsl:value-of select="."/>
			</xsl:for-each>
			<xsl:for-each select="osm:general/osm:LCSHsubject">
				<xsl:value-of select="."/>
			</xsl:for-each>
			<xsl:for-each select="osm:general/osm:OSMsubject">
				<xsl:value-of select="."/>
			</xsl:for-each>
			<xsl:for-each select="osm:general/osm:eventName">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="string-length($allkeys) > 0">
			<subjects>
				<xsl:apply-templates select="osm:general/osm:keyword | osm:general/osm:LCSHsubject | osm:general/osm:OSMsubject | osm:general/osm:eventName" mode="process">
					<xsl:with-param name="tag">subject</xsl:with-param>
				</xsl:apply-templates>				
			</subjects>
		</xsl:if>

<!--contributors can be 1) osm contributors, 2) assigned hosting institutions based on the type of object (e.g. tech note) or 3) assigned contacts based on object type (e.g. Tech Notes)-->
<!--only OSM roles that are DataCite contributors are Editor (OSM contributor is not anything in DataCite so ignore OSM contributor)-->
<!--process both OSM person and org; do not follow hierarchy logic like done for creators. That is, creators worked on people only if people were present. Here contributors will work on both people and orgs-->
<!--2 cases-->
<!--Case 1: there are osm people or organization editors to process and the NCAR Library should also be assigned as the hosting institution-->
<!--Case 2: there are NO osm people or organization editors to process and the NCAR Library should also be assigned as the hosting institution-->
		<contributors>
			<contributor contributorType="HostingInstitution">
			<!--use fully spelled out name for the NCAR Library-->
				<contributorName>University Corporation For Atmospheric Research (UCAR):National Center for Atmospheric Research (NCAR):NCAR Library (NCARLIB)</contributorName>
			</contributor>
			<xsl:apply-templates select="osm:contributors/osm:organization[@role='Editor']" mode="org">
				<xsl:with-param name="tag1">contributor</xsl:with-param>
				<xsl:with-param name="tag2">contributorName</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select="osm:contributors/osm:person[@role='Editor'] " mode="name">
				<xsl:with-param name="tag1">contributor</xsl:with-param>
				<xsl:with-param name="tag2">contributorName</xsl:with-param>
			</xsl:apply-templates>
		</contributors>

<!--dates using coverage.date-->
<!--date is required in OSM so something will be present-->
		<dates>
			<xsl:apply-templates select="osm:coverage/osm:date[@type='Accepted']" mode="process">
				<xsl:with-param name="tag">date</xsl:with-param>
				<xsl:with-param name="att">dateType</xsl:with-param>
				<xsl:with-param name="attcon">Accepted</xsl:with-param>
			</xsl:apply-templates>				
			<xsl:apply-templates select="osm:coverage/osm:date[@type='Submitted']" mode="process">
				<xsl:with-param name="tag">date</xsl:with-param>
				<xsl:with-param name="att">dateType</xsl:with-param>
				<xsl:with-param name="attcon">Submitted</xsl:with-param>
			</xsl:apply-templates>				
			<xsl:apply-templates select="osm:coverage/osm:date[@type='Created']" mode="process">
				<xsl:with-param name="tag">date</xsl:with-param>
				<xsl:with-param name="att">dateType</xsl:with-param>
				<xsl:with-param name="attcon">Created</xsl:with-param>
			</xsl:apply-templates>				
			<xsl:apply-templates select="osm:coverage/osm:date[@type='Modified']" mode="process">
				<xsl:with-param name="tag">date</xsl:with-param>
				<xsl:with-param name="att">dateType</xsl:with-param>
				<xsl:with-param name="attcon">Updated</xsl:with-param>
			</xsl:apply-templates>				
<!--since Available also handles embargoes, do not write circa and digitized as Available until checking to see if an embargo is present. See new code later down-->
<!--
			<xsl:apply-templates select="osm:coverage/osm:date[@type='Circa'] | osm:coverage/osm:date[@type='Digitized'] | osm:coverage/osm:date[@type='Presented']" mode="process">
				<xsl:with-param name="tag">date</xsl:with-param>
				<xsl:with-param name="att">dateType</xsl:with-param>
				<xsl:with-param name="attcon">Available</xsl:with-param>
			</xsl:apply-templates>		
-->		
			<xsl:apply-templates select="osm:coverage/osm:date[@type='Published'] | osm:coverage/osm:date[@type='In press']" mode="process">
				<xsl:with-param name="tag">date</xsl:with-param>
				<xsl:with-param name="att">dateType</xsl:with-param>
				<xsl:with-param name="attcon">Issued</xsl:with-param>
			</xsl:apply-templates>
<!--since Available also handles embargoes, do not write Circa and Digitized as Available until checking to see if an embargo is present. If embargo is present do not transform Circa and Digitized. If no embargo, go ahead and write as Available.-->
			<xsl:choose>
				<xsl:when test="osm:coverage/osm:dateRange [@type='Author embargo'] ">
					<xsl:element name="date">
						<xsl:attribute name="dateType">Available</xsl:attribute>
						<xsl:value-of select="osm:coverage/osm:dateRange/@end"/>
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="osm:coverage/osm:date[@type='Circa'] | osm:coverage/osm:date[@type='Digitized'] | osm:coverage/osm:date[@type='Presented']" mode="process">
					<xsl:with-param name="tag">date</xsl:with-param>
					<xsl:with-param name="att">dateType</xsl:with-param>
					<xsl:with-param name="attcon">Available</xsl:with-param>
					</xsl:apply-templates>				
				</xsl:otherwise>
			</xsl:choose>				
		</dates>

<!--language using language-->
<!--OSM language is required and generally uses 2-letter encoding while DataCite recommends using the 3-letter encodings but it is valid with the 2-letter encodings because the DataCite schema uses xs:language-->
<!--so just use the 2-letter encodings directly from OSM-->
<!--do not map the OSM value of 'not applicable' because DataCite xml would be invalid; map it to 'en'-->
<!--DataCite language occurs once while OSM is unbounded-->
<!--code below maps any occurrence of 'not applicable' to 'en' or maps the first OSM language occurrence regardless of how many language elements are present-->
		<xsl:variable name="alllang">
			<xsl:for-each select="osm:classify/osm:language">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:element name="language">
			<xsl:choose>
				<xsl:when test="contains($alllang, 'not applicable')">
					<xsl:text>en</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="osm:classify/osm:language"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>

<!--resourceType using general.title@type-->
<!--OSM title type is required so something will be present-->
<!--OSM title type occurs only once so Datacite resource type will be written only once-->
		<xsl:apply-templates select="osm:general/osm:title[@type='Animation/moving image']" mode="process">
			<xsl:with-param name="tag">resourceType</xsl:with-param>
			<xsl:with-param name="att">resourceTypeGeneral</xsl:with-param>
			<xsl:with-param name="attcon">Film</xsl:with-param>
			<xsl:with-param name="elem">title</xsl:with-param>
		</xsl:apply-templates>				
		<xsl:apply-templates select="osm:general/osm:title[@type='Artifact/physical object']" mode="process">
			<xsl:with-param name="tag">resourceType</xsl:with-param>
			<xsl:with-param name="att">resourceTypeGeneral</xsl:with-param>
			<xsl:with-param name="attcon">PhysicalObject</xsl:with-param>
			<xsl:with-param name="elem">title</xsl:with-param>
		</xsl:apply-templates>				
		<xsl:apply-templates select="osm:general/osm:title[@type='Bibliography/index'] |osm:general/osm:title[@type='Collection'] " mode="process">
			<xsl:with-param name="tag">resourceType</xsl:with-param>
			<xsl:with-param name="att">resourceTypeGeneral</xsl:with-param>
			<xsl:with-param name="attcon">Collection</xsl:with-param>
			<xsl:with-param name="elem">title</xsl:with-param>
		</xsl:apply-templates>				
		<xsl:apply-templates select="osm:general/osm:title[@type='Blog/forum/email list'] | osm:general/osm:title[@type='Book chapter'] | osm:general/osm:title[@type='Book review'] | osm:general/osm:title[@type='Book/monograph'] | osm:general/osm:title[@type='Correspondence'] | osm:general/osm:title[@type='Encyclopedia'] | osm:general/osm:title[@type='Journal/magazine/periodical'] | osm:general/osm:title[@type='Manual'] | osm:general/osm:title[@type='Manuscript'] | osm:general/osm:title[@type='Newsletter'] | osm:general/osm:title[@type='Notes'] | osm:general/osm:title[@type='Poster'] | osm:general/osm:title[@type='Report'] | osm:general/osm:title[@type='Transcript'] | osm:general/osm:title[@type='Article'] " mode="process">
			<xsl:with-param name="tag">resourceType</xsl:with-param>
			<xsl:with-param name="att">resourceTypeGeneral</xsl:with-param>
			<xsl:with-param name="elem">title</xsl:with-param>
			<xsl:with-param name="attcon">Text</xsl:with-param>
		</xsl:apply-templates>				
		<xsl:apply-templates select="osm:general/osm:title[@type='Presentation/webcast'] " mode="process">
			<xsl:with-param name="tag">resourceType</xsl:with-param>
			<xsl:with-param name="att">resourceTypeGeneral</xsl:with-param>
			<xsl:with-param name="elem">title</xsl:with-param>
			<xsl:with-param name="attcon">Event</xsl:with-param>
		</xsl:apply-templates>				
		<xsl:apply-templates select="osm:general/osm:title[@type='Image']" mode="process">
			<xsl:with-param name="tag">resourceType</xsl:with-param>
			<xsl:with-param name="att">resourceTypeGeneral</xsl:with-param>
			<xsl:with-param name="elem">title</xsl:with-param>
			<xsl:with-param name="attcon">Image</xsl:with-param>
		</xsl:apply-templates>				
		<xsl:apply-templates select="osm:general/osm:title[@type='Interactive resource']" mode="process">
			<xsl:with-param name="tag">resourceType</xsl:with-param>
			<xsl:with-param name="att">resourceTypeGeneral</xsl:with-param>
			<xsl:with-param name="elem">title</xsl:with-param>
			<xsl:with-param name="attcon">InteractiveResource</xsl:with-param>
		</xsl:apply-templates>				
		<xsl:apply-templates select="osm:general/osm:title[@type='Software']" mode="process">
			<xsl:with-param name="tag">resourceType</xsl:with-param>
			<xsl:with-param name="att">resourceTypeGeneral</xsl:with-param>
			<xsl:with-param name="elem">title</xsl:with-param>
			<xsl:with-param name="attcon">Software</xsl:with-param>
		</xsl:apply-templates>				
		<xsl:apply-templates select="osm:general/osm:title[@type='Sound']" mode="process">
			<xsl:with-param name="tag">resourceType</xsl:with-param>
			<xsl:with-param name="att">resourceTypeGeneral</xsl:with-param>
			<xsl:with-param name="elem">title</xsl:with-param>
			<xsl:with-param name="attcon">Sound</xsl:with-param>
		</xsl:apply-templates>				
	
<!--alternateIdentifier using OSM idNumber and urlOfRecord-->
<!--determine if any OSM idNumbers are present-->
<!--not all vocab terms are mapped because WOS, CALL and CISL are not useful; so need to test for the ones that will be mapped (so use contains rather than string-length)-->
		<xsl:variable name="allnums">
			<xsl:for-each select="osm:classify/osm:idNumber/@type">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="contains($allnums, 'arXiv') or contains($allnums, 'Citation/Article') or contains($allnums, 'EPRINTS') or contains($allnums, 'ISBN') or contains($allnums, 'ISSN') or contains($allnums, 'REPORT') or contains($allnums, 'SRef-ID') or contains($allnums, 'WMO')">
				<alternateIdentifiers>
					<xsl:apply-templates select="osm:classify/osm:idNumber[@type='arXiv'] " mode="process">
						<xsl:with-param name="tag">alternateIdentifier</xsl:with-param>
						<xsl:with-param name="att">alternateIdentifierType</xsl:with-param>
						<xsl:with-param name="attcon">arXiv</xsl:with-param>
					</xsl:apply-templates>				
					<xsl:apply-templates select="osm:classify/osm:idNumber[@type='Citation/Article'] " mode="process">
						<xsl:with-param name="tag">alternateIdentifier</xsl:with-param>
						<xsl:with-param name="att">alternateIdentifierType</xsl:with-param>
						<xsl:with-param name="attcon">Citation/Article</xsl:with-param>
					</xsl:apply-templates>				
					<xsl:apply-templates select="osm:classify/osm:idNumber[@type='EPRINTS'] " mode="process">
						<xsl:with-param name="tag">alternateIdentifier</xsl:with-param>
						<xsl:with-param name="att">alternateIdentifierType</xsl:with-param>
						<xsl:with-param name="attcon">EPRINTS</xsl:with-param>
					</xsl:apply-templates>				
					<xsl:apply-templates select="osm:classify/osm:idNumber[@type='ISBN'] " mode="process">
						<xsl:with-param name="tag">alternateIdentifier</xsl:with-param>
						<xsl:with-param name="att">alternateIdentifierType</xsl:with-param>
						<xsl:with-param name="attcon">ISBN</xsl:with-param>
					</xsl:apply-templates>				
					<xsl:apply-templates select="osm:classify/osm:idNumber[@type='ISSN'] " mode="process">
						<xsl:with-param name="tag">alternateIdentifier</xsl:with-param>
						<xsl:with-param name="att">alternateIdentifierType</xsl:with-param>
						<xsl:with-param name="attcon">ISSN</xsl:with-param>
					</xsl:apply-templates>				
					<xsl:apply-templates select="osm:classify/osm:idNumber[@type='REPORT'] " mode="process">
						<xsl:with-param name="tag">alternateIdentifier</xsl:with-param>
						<xsl:with-param name="att">alternateIdentifierType</xsl:with-param>
						<xsl:with-param name="attcon">REPORT</xsl:with-param>
					</xsl:apply-templates>				
					<xsl:apply-templates select="osm:classify/osm:idNumber[@type='SRef-ID'] " mode="process">
						<xsl:with-param name="tag">alternateIdentifier</xsl:with-param>
						<xsl:with-param name="att">alternateIdentifierType</xsl:with-param>
						<xsl:with-param name="attcon">SRef-ID</xsl:with-param>
					</xsl:apply-templates>				
					<xsl:apply-templates select="osm:classify/osm:idNumber[@type='WMO'] " mode="process">
						<xsl:with-param name="tag">alternateIdentifier</xsl:with-param>
						<xsl:with-param name="att">alternateIdentifierType</xsl:with-param>
						<xsl:with-param name="attcon">World Meteorological Organization Report</xsl:with-param>
					</xsl:apply-templates>
					<!--and do URLofRecord-->				
					<xsl:apply-templates select="osm:general/osm:urlOfRecord" mode="process">
						<xsl:with-param name="tag">alternateIdentifier</xsl:with-param>
						<xsl:with-param name="att">alternateIdentifierType</xsl:with-param>
						<xsl:with-param name="attcon">URL</xsl:with-param>
					</xsl:apply-templates>				
				</alternateIdentifiers>
			</xsl:when>
			<!--if no idNumbers, then just do URL of record; verfiy its presence-->
			<xsl:otherwise>
				<xsl:if test="string-length(osm:general/osm:urlOfRecord) > 0">
					<alternateIdentifiers>
						<xsl:apply-templates select="osm:general/osm:urlOfRecord" mode="process">
							<xsl:with-param name="tag">alternateIdentifier</xsl:with-param>
							<xsl:with-param name="att">alternateIdentifierType</xsl:with-param>
							<xsl:with-param name="attcon">URL</xsl:with-param>
						</xsl:apply-templates>				
					</alternateIdentifiers>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>

<!--relatedIdentifiers using OSM relation-->
<!--do not put OSM idNumbers: ISSN, ISBN and DOI here because they are truly alternateIdentifiers about the same thing not related resources-->
<!--OSM relation has terms: Is part of, Is version of and Has version -->
<!--only OSM logical for mapping: Is part of because Is version and Has version is not represented in DataCite rather previous and is new version are-->
<!--determine if there are any related identifiers to write, that is the OSM combo is present
    relation type="Is part of" url="http" -->
		<xsl:variable name="allrels">
			<xsl:for-each select="osm:resources/osm:relation[@type='Is part of'] ">
				<xsl:value-of select="./@url"/>
			</xsl:for-each>
		</xsl:variable>

		<xsl:if test="contains($allrels, 'http')">
			<relatedIdentifiers>
				<xsl:apply-templates select="osm:resources/osm:relation[@type='Is part of'] " mode="relation">
					<xsl:with-param name="tag">relatedIdentifier</xsl:with-param>
					<xsl:with-param name="att">relatedIdentifierType</xsl:with-param>
					<xsl:with-param name="attcon">URL</xsl:with-param>
					<xsl:with-param name="att2">relationType</xsl:with-param>
					<xsl:with-param name="attcon2">IsPartOf</xsl:with-param>
				</xsl:apply-templates>				
			</relatedIdentifiers>
		</xsl:if>

<!--size using size-->
<!--determine if any size information is present-->
		<xsl:variable name="allsizes">
			<xsl:for-each select="osm:resources/osm:primaryAsset/osm:size">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="string-length($allsizes) > 0">
			<sizes>
				<xsl:apply-templates select="osm:resources/osm:primaryAsset/osm:size" mode="process">
					<xsl:with-param name="tag">size</xsl:with-param>
				</xsl:apply-templates>				
			</sizes>
		</xsl:if>

<!--format using mimetype-->
<!--determine if any mimetype information is present-->
		<xsl:variable name="allmimes">
			<xsl:for-each select="osm:resources/osm:primaryAsset/osm:mimeType">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="string-length($allmimes) > 0">
			<formats>
				<xsl:apply-templates select="osm:resources/osm:primaryAsset/osm:mimeType" mode="process">
					<xsl:with-param name="tag">format</xsl:with-param>
				</xsl:apply-templates>				
			</formats>
		</xsl:if>
			
<!--version: no map-->
<!--there is no logical version numbr in OSM except to say preprint, postprint, publisher's final and we would not submit those to DataCite-->


<!--rights using copyrightNotice-->
<!--for OSM, copyrightNotice and holder are required, so no need to check if present before writing DataCite tag-->
<!--rights only occurs once in DataCite so chunk OSM info together as appropriate-->
<!--4 cases-->
		<xsl:element name="rights">
		<xsl:choose>
			<xsl:when test="string-length(osm:rights/osm:copyrightNotice/@url) = 0 and string-length(osm:rights/osm:copyrightNotice/@type) > 0">
			<!--don't want to write opt out type-->
				<xsl:choose>
					<xsl:when test="osm:rights/osm:copyrightNotice/@type ='Opt out' ">
<!--CASE 1 type has content and url is empty-->
						<xsl:value-of select="concat('The rights holder is: ', osm:rights/osm:copyrightNotice/@holder, '. The rights notice is: ', osm:rights/osm:copyrightNotice)"/>
					</xsl:when>
					<xsl:otherwise>
<!--CASE 1 type has content and url is empty-->
						<xsl:value-of select="concat('The rights holder is: ', osm:rights/osm:copyrightNotice/@holder, '. The type of right license is: ', osm:rights/osm:copyrightNotice/@type, '. The rights notice is: ', osm:rights/osm:copyrightNotice)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="string-length(osm:rights/osm:copyrightNotice/@url) > 0 and string-length(osm:rights/osm:copyrightNotice/@type) > 0">
			<!--don't want to write opt out type-->
				<xsl:choose>
					<xsl:when test="osm:rights/osm:copyrightNotice/@type = 'Opt out' ">
<!--CASE 2 type has content and url has content-->
						<xsl:value-of select="concat('The rights holder is: ', osm:rights/osm:copyrightNotice/@holder, '. The rights notice is: ', osm:rights/osm:copyrightNotice, ' More information can be found at: ', osm:rights/osm:copyrightNotice/@url, '.')"/>
					</xsl:when>
					<xsl:otherwise>
<!--CASE 2 type has content and url has content-->
						<xsl:value-of select="concat('The rights holder is: ', osm:rights/osm:copyrightNotice/@holder, '. The type of right license is: ', osm:rights/osm:copyrightNotice/@type, '. The rights notice is: ', osm:rights/osm:copyrightNotice, ' More information can be found at: ', osm:rights/osm:copyrightNotice/@url, '.')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="string-length(osm:rights/osm:copyrightNotice/@url) > 0 and string-length(osm:rights/osm:copyrightNotice/@type) = 0">
<!--CASE 3 type is empty and url has content-->
				<xsl:value-of select="concat('The rights holder is: ', osm:rights/osm:copyrightNotice/@holder, '. The rights notice is: ', osm:rights/osm:copyrightNotice, ' More information can be found at: ', osm:rights/osm:copyrightNotice/@url, '.')"/>
			</xsl:when>
			<xsl:otherwise>
<!--CASE 4 both type and url are empty-->
				<xsl:value-of select="concat('The rights holder is: ', osm:rights/osm:copyrightNotice/@holder, '. The rights notice is: ', osm:rights/osm:copyrightNotice)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:element> <!--end rights element-->

		
<!--description using description and abstract-->
<!--OSM description is unbounded and the DataCite description is unbounded-->
<!--OSM abstract occurs only once--> 
<!--determine if any description information is present-->
		<xsl:variable name="alldes">
			<xsl:for-each select="osm:general/osm:description">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="abstract">
			<xsl:value-of select="osm:general/osm:abstract"/>
		</xsl:variable>

<!--2 cases-->
<!--Case 1: description is present and if abstract information is present, it is acted upon and if abstract says 'needs absract' this is suppressed-->		
<!--Case 2: description is not present and abstract information is present but if abstract says 'needs abstract this is suppressed'-->
		<xsl:choose>
			<xsl:when test="string-length($alldes) > 0  and string-length($abstract) > 0" >
				<descriptions>
					<xsl:apply-templates select="osm:general/osm:description" mode="process">
						<xsl:with-param name="tag">description</xsl:with-param>
						<xsl:with-param name="att">descriptionType</xsl:with-param>
						<xsl:with-param name="attcon">Other</xsl:with-param>
					</xsl:apply-templates>
					<xsl:apply-templates select="osm:general/osm:abstract[not(.='needs abstract')]" mode="process">
						<xsl:with-param name="tag">description</xsl:with-param>
						<xsl:with-param name="att">descriptionType</xsl:with-param>
						<xsl:with-param name="attcon">Abstract</xsl:with-param>
					</xsl:apply-templates>
				</descriptions>
			</xsl:when>
			<xsl:when test="string-length($alldes) = 0  and string-length($abstract) > 0 and $abstract != 'needs abstract' ">
				<descriptions>
					<xsl:apply-templates select="osm:general/osm:abstract[not(.='needs abstract')]" mode="process">
						<xsl:with-param name="tag">description</xsl:with-param>
						<xsl:with-param name="att">descriptionType</xsl:with-param>
						<xsl:with-param name="attcon">Abstract</xsl:with-param>
					</xsl:apply-templates>			
				</descriptions>
			</xsl:when>
		</xsl:choose>
	</resource><!--end resource element-->

</xsl:template>




<!--TEMPLATES for lar to nsdl_dc-->
<!-- ****************************************-->
<!--PROCESS write all tag sets that do not have their own templates-->

<!--DATEEND template-->
	<xsl:template match="node()" name="dateEnd" mode="dateEnd">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
<!--OSM dateRange/@end is optional; so if no value assign 5000 in a template-->

		<xsl:param name="tag"/>
		<xsl:param name="string" select="substring(./@end, 1, 4)"/>
		<xsl:element name="{$tag}" namespace="{$datacite}">
			<xsl:choose>
				<xsl:when test="string-length($string) > 0">
					<xsl:value-of select="$string"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>5000</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>	

<!--NAME template without type-->
	<xsl:template match="node()" name="name" mode="name">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag1"/>
		<xsl:param name="tag2"/>
		<xsl:param name="contribType" select="./@role"/>
		<xsl:param name="stringLast" select="osm:lastName"/>
		<xsl:param name="stringFirst" select="osm:firstName"/>
		
		<xsl:if test="string-length($stringLast) > 0">
			<xsl:element name="{$tag1}" namespace="{$datacite}">
			<!--DataCite contributor element requires and attribute of contributorType while the creator element does not require an attribute-->
				<xsl:if test="$contribType = 'Editor' ">
					<xsl:attribute name="contributorType">Editor</xsl:attribute>
				</xsl:if>
				<xsl:element name="{$tag2}" namespace="{$datacite}">
					<xsl:value-of select="concat($stringLast, ', ', $stringFirst)"/>
				</xsl:element>
			</xsl:element>
		</xsl:if>		
	</xsl:template>	

<!--ORG template without type-->
	<xsl:template match="node()" name="org" mode="org">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag1"/>
		<xsl:param name="tag2"/>
		<xsl:param name="contribType" select="./@role"/>
		<xsl:param name="string" select="osm:affiliation/osm:instName"/>
		
		<xsl:if test="string-length($string) > 0">
			<xsl:element name="{$tag1}" namespace="{$datacite}">
			<!--DataCite contributor element requires and attribute of contributorType while the creator element does not require an attribute-->
				<xsl:if test="$contribType = 'Editor' ">
					<xsl:attribute name="contributorType">Editor</xsl:attribute>
				</xsl:if>
				<xsl:element name="{$tag2}" namespace="{$datacite}">
					<xsl:value-of select="$string"/>
				</xsl:element>
			</xsl:element>
		</xsl:if>		
	</xsl:template>	

<!--PUBLISHER template without type-->
	<xsl:template match="node()" name="publisher" mode="publisher">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="string" select="osm:affiliation/osm:instName"/>
		
		<xsl:if test="position() = 1">
			<xsl:element name="{$tag}" namespace="{$datacite}">
				<xsl:value-of select="$string"/>
			</xsl:element>
		</xsl:if>		
	</xsl:template>	

<!--PROCESS template without type-->
	<xsl:template match="node()" name="process" mode="process">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="string" select="."/>
		<xsl:param name="att"/>
		<xsl:param name="attcon"/>
		<xsl:param name="elem"/>

		<xsl:if test="string-length($string) > 0">
			<xsl:element name="{$tag}" namespace="{$datacite}">
				<xsl:if test="string-length($att) > 0">
					<xsl:attribute name="{$att}">
						<xsl:value-of select="$attcon"/>
					</xsl:attribute>	
				</xsl:if>
				<xsl:choose>
					<xsl:when test="$elem = 'title' ">
						<xsl:value-of select="./@type"/>
					</xsl:when>					
					<xsl:otherwise>
						<xsl:value-of select="$string"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
		</xsl:if>		
	</xsl:template>	

<!--RELATION template-->
	<xsl:template match="node()" name="relation" mode="relation">
<!--note: in templates, params must be declared before variables-->
<!--note: for the with-param tag to work above, a param tag must exist and be present here in the template being called-->
		<xsl:param name="tag"/>
		<xsl:param name="string" select="./@url"/>
		<xsl:param name="att"/>
		<xsl:param name="attcon"/>
		<xsl:param name="att2"/>
		<xsl:param name="attcon2"/>

		<xsl:if test="contains($string, 'http')">
			<xsl:element name="{$tag}" namespace="{$datacite}">
				<xsl:attribute name="{$att}">
					<xsl:value-of select="$attcon"/>
				</xsl:attribute>	
				<xsl:attribute name="{$att2}">
					<xsl:value-of select="$attcon2"/>
				</xsl:attribute>	
				<xsl:value-of select="$string"/>
			</xsl:element>
		</xsl:if>		
	</xsl:template>	

</xsl:stylesheet>
<!--LICENSE AND COPYRIGHT
The contents of this file are subject to the Educational Community License v1.0 (the "License"); you may not use this file except in compliance with the License. You should obtain a copy of the License from http://www.opensource.org/licenses/ecl1.php. Files distributed under the License are distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the specific language governing rights and limitations under the License. Copyright 2002-2009 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR). All rights reserved.-->