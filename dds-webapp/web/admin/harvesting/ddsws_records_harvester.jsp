<%@ page contentType="text/html; charset=UTF-8" %><%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@page import="org.dom4j.Node"%>
<%-- 
	This page takes XML records returned from a DDSWS search request, 
	then writes them to disk one record per file. 
--%>

<%-- The directory where the files will be written to: --%>
<c:set var="outputDir" value="C:\Program Files\Apache Software Foundation\Tomcat 5.5\harvest_ddsws"/>

<%-- The DDSWS service baseURL to harvest from --%>
<%-- <c:set var="ddswsBaseUrl" value="http://www.dlese.org/dds/services/ddsws1-1"/> --%>
<c:set var="ddswsBaseUrl" value="http://ncs.nsdl.org/mgr/services/ddsws1-1"/>

<%-- The http params to send in the Search, UserSearch request --%>
<c:url value="${ddswsBaseUrl}" var="ddswsHarvestRequest">
	<c:param name="verb" value="Search"/>
	<c:param name="q" value="allrecords:true"/>
	<c:param name="xmlFormat" value="ncs_collect"/>
	<c:param name="s" value="0"/>
	<c:param name="n" value="200"/>
</c:url>


<c:if test="${param.doParse == 't'}">
	
	<%-- ------------------------ Begin parsing and processing code ------------------------	 --%>

	<c:catch var="parseXmlError">
		${f:systemOut('Fetching and processing records...')}
		<xtags:parse id="resourcesDom" url="${ddswsHarvestRequest}"/>
	</c:catch>
	
	<c:set var="numRecords" value="0"/>
	
	<%-- Loop through metadata records returned: --%>
	<c:if test="${empty parseXmlError}">
	<xtags:forEach select="$resourcesDom//*[local-name()='results']/*[local-name()='record']/*[local-name()='metadata']" id="metadata">
		
		<xtags:variable id="recordID" select="$metadata/../*[local-name()='head']/*[local-name()='id']"/>		
		
		
		
		<c:if test="${not empty recordID}">
			<fmt:formatNumber var="numRecords" value="${numRecords+1}" pattern="000"/>
			
			<c:set var="msg" value="processing recordID ${recordID}"/>
			${f:systemOut(msg)}
			
			<xtags:variable id="record" select="$metadata/*" type="Node"/>
<c:set var="output">
<?xml version="1.0" encoding="UTF-8" ?>
${f:dom4jToXml(record)}
</c:set>
			
		<c:catch var="fileWriteError">
			<%-- Write the file to disk: --%>
			<c:set var="filePath" value="${outputDir}/${recordID}.xml"/>
			${f:writeFile(output,filePath)}
		</c:catch>
			
		</c:if>
	</xtags:forEach>
	</c:if>
	
	${f:systemOut('Finished harvesting DDSWS records')}
	
	</c:if>

<%-- ------------------------ End parsing and processing code ------------------------	 --%>



<%-- ------------------------ Begin display code ------------------------	 --%>


<html>

<head>
	<title>DDSWS Harvester</title>
	
	<!-- The Prototype JavaScript framework (http://www.prototypejs.org/) (via Google APIs): -->
	<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/prototype/1.6.1.0/prototype.js'></script>
	<script>
		function showProgressBar() {
			if( confirm('Process the records (this may take a while)?') ) {
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
				<h2>DDSWS Harvester</h2>
				<p>
					Use this page to harvest records from any <a href="http://dlese.org/dds/services/">DDSWS search service</a>,
					available from the DDS, DCS or NCS.
				</p>
			 </td>
			 <td valign="top">
			 	<a href="index.jsp">Home</a>
			</td>
		</tr>
	</table>
	
	<c:choose>
		<c:when test="${not empty fileWriteError || not empty parseXmlError}">
				<div style="display:true">
					<p style="color:red">
						There was an error:
						${not empty fileWriteError ? 'Check the file directory path and permissions.':''}
						${not empty parseXmlError ? 'Check the DDSWS request.':''}
						<br/>
						Message: ${fileWriteError} ${parseXmlError}
					</p>
					 <p>
						Files were attempted to be written to
						<nobr><code>${outputDir}</code></nobr>
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
				<p><img src="../images/progress-bar.gif"/></p>
				 <p>
						Files are being saved to
						<nobr><code>${outputDir}</code></nobr>
				</p>		
				
			</div>
		
			<c:choose>
				<c:when test="${empty param.doParse}">
				<div id="doParseForm" style="display:true">
				
					 <p>
							Harvst the following DDSWS request (edit this JSP to change):
							<nobr><a href="${ddswsHarvestRequest}" target="_blank"><code>${ddswsHarvestRequest}</code></a></nobr>
					 </p>
				
					 <p>
							Files will be save to
							<nobr><code>${outputDir}</code></nobr>
					</p>
				
					<form name='parseForm' method='POST' action='' onsubmit="return showProgressBar()">
					 <p>
							<input type="submit" value="Harvest and save to files"/>
							<input type="hidden" name='doParse' value='t'/>
					 </p>
					</form>	 
			
				</div>
			</c:when>
			<c:otherwise>
				<div id="msg" style="display:true">
					<p style="color:green">
						 Number of records processed: ${numRecords}
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

