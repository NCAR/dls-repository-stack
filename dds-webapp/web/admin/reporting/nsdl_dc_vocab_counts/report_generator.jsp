<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%-- 
	This page takes generates NSDL_DC vocab reports for each collection and saves them to disk, one
	file per report.
--%>

<%-- The directory where the files will be written to: --%>
<%-- <c:set var="outputDir" value="C:\cygwin\home\jweather\devel\nsdl_dc_vocab_reports"/>--%>
<%-- <c:set var="outputDir" value="C:\Documents and Settings\Sara and John\My Documents\devel\nsdl_assessment_reports"/> --%> 

<%-- The directory where the files will be written to: --%>
<c:set var="outputDir" value="/tomcat/tomcat_data/nsdl_dc_vocab_reports"/>



<c:if test="${param.doParse == 't'}">
	
	<%-- ------------------------ Begin parsing and processing code ------------------------	 --%>

	<c:set var="vocabsIndex">${initParam.nsdlDcVocabsConfig}vocab_selections.xml</c:set>
	<c:set var="collectionGroups">${initParam.nsdlDcVocabsConfig}pathway-groups.xml</c:set>	
	
	<c:set var="numReports" value="0"/>

	<%-- Grab the Collections DOM --%>
	<c:url value="${f:serverUrl(pageContext.request)}/${pageContext.request.contextPath}/services/ddsws1-1" var="listCollsReqUrl">
		<c:param name="verb" value="ListCollections"/>
	</c:url>
	<xtags:parse id="listCollectionsDom">
		<xtags:style xml="${listCollsReqUrl}" xsl="/include/remove_namespace.xsl"/>
	</xtags:parse>
	
	<c:import url="${collectionGroups}" charEncoding="UTF-8" var="xmlOutput"/>
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${xmlOutput}" var="collectionGroupsDom"/>		

	<%-- Set up a Map with all the collections... --%>
	<c:set var="collectionsMap" value="${f:map(collectionsMap,'-all-','All Collections')}" />
	<x:forEach var="group1" varStatus="i" select="$collectionGroupsDom/opml/body/outline[@type = 'group']">
		<c:set var="collName">Collection Group <x:out select="$group1/@text"/></c:set>
		<c:set var="collKy">GRP-<x:out select="$group1/@text"/></c:set>
		
		<c:set var="collectionsMap" value="${f:map(collectionsMap,collKy,collName)}" />
	</x:forEach>				
	<xtags:forEach id="coll" select="$listCollectionsDom/DDSWebService/ListCollections/collections/collection">
		<xtags:variable id="collKy" select="$coll/searchKey"/>
		<xtags:variable id="collName" select="$coll/renderingGuidelines/label"/>
		<xtags:variable id="xmlFormat" select="$coll/additionalMetadata/dlese_collect/formatOfRecords"/>
	
		<c:if test="${xmlFormat == 'nsdl_dc'}">
			<c:set var="collectionsMap" value="${f:map(collectionsMap,collKy,collName)}" />
		</c:if>
	</xtags:forEach>

	<%-- Fetch the report for each collection and save it to disk... --%>
	<c:forEach items="${collectionsMap}" var="collection">
		<c:set var="collKy">${collection.key}</c:set>
		<c:set var="collName">${collection.value}</c:set>
		
		<c:if test="${empty fileWriteError}">
			<c:catch var="fileWriteError">
				<c:set var="fileName" value="${f:replaceAll(collName,'\\\W+','_')}"/>
				<c:set var="fileName" value="${f:replaceAll(fileName,'_\\\z','')}"/>
				
				<c:set var="filePath" value="${outputDir}/${fileName}.html"/>
				<c:set var="msg" value="Processing collection ${collName} (File path ${filePath})..."/>
				${f:systemOut(msg)}							
				<c:url value="./" var="reportUrl">
					<c:param name="ky" value="${collKy}"/>
					<c:param name="viewContext" value="reportFile"/>
				</c:url>
	
				<c:set var="msg" value="Import url: ${reportUrl}"/>
				${f:systemOut(msg)}
				
				<c:import url="${reportUrl}" var="output"/>
				<c:set var="output" value="${f:replaceAll(output,'\\\s\\\s+',' ')}"/>
				
				${f:writeFile(output,filePath)}
			</c:catch>
			<c:if test="${not empty fileWriteError}">
				<c:set var="msg" value="Error fetching or writing report: ${fileWriteError}"/>
				${f:systemOut(msg)}					
			</c:if>				
			
			<c:set var="msg" value="File written for ${collName}..."/>
			${f:systemOut(msg)}	
	
			<fmt:formatNumber var="numReports" value="${numReports+1}" pattern="000"/>
		</c:if>
	
	</c:forEach>

	${f:systemOut('Finished processing all collection reports')}	
	
	</c:if>

<%-- ------------------------ End processing code ------------------------	 --%>



<%-- ------------------------ Begin display code ------------------------	 --%>


<html>

<head>
	<title>Report file generator</title>
	
	<!-- The Prototype JavaScript framework (http://www.prototypejs.org/) (via Google APIs): -->
	<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/prototype/1.6.1.0/prototype.js'></script>
	<script>
		var hbCount = 0;
		function showProgressBar() {
			if( confirm('Process the reports (this may take a LONG while)?') ) {
			
				// Animate the loading msg:
				new PeriodicalExecuter(function(pe) {
					var url = 'report_generator.jsp';
					new Ajax.Request(url, {
						method: 'get',
						onSuccess: function(transport) {
							$('loadMsg').update('Heartbeat (do not close this window until complete) &#187; buhBump (' + (++hbCount) + ')');
						},
						onFailure: function(transport) {
							$('loadMsg').update('Heartbeat failure...');
						}						
					});
				}, 5);			
			
				if($('progressBar'))
					$('progressBar').toggle();
				if($('doParseForm'))
					$('doParseForm').toggle();			
				return true;
			}
			else
				return false;
		}	
	</script>
	
	<style type="text/css">
	  <!--
		BODY {
			margin-left: auto;
			margin-right: auto;
			margin-top: 7px;
			margin-bottom: 1px;
			padding: 10px;
			font-family: Arial, Helvetica, sans-serif; 
			font-size: 10pt; 			
		} 
		
		H1 { 
			Font-Family : Arial, Helvetica, sans-serif ; 
			Font-Size : 14pt ;
			Color : #333366; 
		} 

		H2 { 
			Font-Family : Arial, Helvetica, sans-serif ; 
			Font-Size : 12pt ;
			Color : #333366; 
		} 
			  
		TD { 
			font-family: Arial, Helvetica, sans-serif; 
			font-size: 10pt; 
		} 
				  
		A { text-decoration: underline; } 
		
		A:link { 
			Font-Family: Arial, Helvetica, sans-serif;
			Color: #333366
		} 
		
		A:active {
			Color: #333366;
			text-decoration: none;
		} 
		
		A:visited { Color: #333366 } 
		
		/* A pseudo class 'blackul' for the A tag */
		A.blackul:link {
			color: #333333; 
			text-decoration: none; 
			font-weight: bold;
			font-size: 11pt; 	
		}
		
		A.blackul:visited {
			color: #333333; 
			text-decoration: none; 
			font-weight: bold;
			font-size: 11pt; 	
		}

		.description { 
			padding-left: 10px; 	
			padding-right: 14px; 		
			padding-bottom: 8px; 
		} 
		.edTop { 
			font-weight: bold;
			color: #333333; 
			padding-left: 4px; 	
			padding-top: 10px; 		
		} 
		.edEn { 
			padding-left: 12px; 	
		} 		
	  -->
	</style>
</head>

<body>


<div style="padding-left:6px;">
	
	<table width="100%" cellpadding="6" cellspacing="0">
		<tr>
			<td>		
				<h2>Report generator</h2>
				<p>
					Use this page to generate individual vocab reports for the NSDL collecitons.
				</p>
			 </td>
			 <td valign="top">
			 	<a href="./">Report page</a>
			</td>
		</tr>
	</table>
	
	<c:choose>
		<c:when test="${not empty fileWriteError || not empty parseXmlError}">
				<div style="display:true">
					<p style="color:red">
						There was an error:
						${not empty fileWriteError ? 'Check the file directory path and permissions.':''}
						<br/>
						Message: ${fileWriteError}
					</p>
					 <p>
						Files were attempted to be written to
						<nobr><code>${outputDir}</code></nobr>
					</p>
					<p>
						Number of collection reports successfully generated: ${numReports}
					</p>
					<p>
						<form name='parseForm' method='GET' action=''>
							<input type="submit" value="OK"/>
						</form>
					</p>			
				</div>		
		
		</c:when>
		<c:otherwise>
			
			<div style="display:none" id="progressBar">
				<p>Processing files. Please wait...</p>
				<p><img src="../../images/progress-bar.gif"/></p>
				<p id="loadMsg">Heartbeat (do not close this window until complete)</p>
				 <p>
					Files are being saved to
					<nobr><code>${outputDir}</code></nobr>
				</p>
				<p>
					Status message are being written to the tomcat standard output {tomcat}/logs/catalina.out
				</p>				
			</div>
		
			<c:choose>
				<c:when test="${empty param.doParse}">
				<div id="doParseForm" style="display:true">
				
					 <p>
							Files will be save to
							<nobr><code>${outputDir}</code></nobr>
					</p>
				
					<form name='parseForm' method='POST' action='' onsubmit="return showProgressBar()">
					 <p>
							<input type="submit" value="Generate report files"/>
							<input type="hidden" name='doParse' value='t'/>
					 </p>
					</form>	 
			
				</div>
			</c:when>
			<c:otherwise>
				<div id="msg" style="display:true">
					<p style="color:green">
						 Number of collection reports generated: ${numReports}
					</p>
					 <p>
						Files were saved to
						<nobr><code>${outputDir}</code></nobr>
					</p>
					<p>
						<form name='parseForm' method='GET' action=''>
							<input type="submit" value="OK"/>
						</form>
					</p>			
				</div>		
			</c:otherwise>
			</c:choose>
	
		</c:otherwise>
	</c:choose>
 
</div>

</body>
</html>

