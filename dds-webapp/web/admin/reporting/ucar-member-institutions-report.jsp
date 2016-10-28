<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page language="java" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<%@ page import="edu.ucar.dls.index.*" %>

<%@ include file="../../JSTLTagLibIncludes.jsp" %>

<html>
<head>
<title>UCAR Member Institutions and Affiliates: Resource Count Report</title>
<link rel='stylesheet' type='text/css' href='../dds_admin_styles.css'>

<style type="text/css">
	table{ 
		background-color:#f1f1f1;
	}
	th { 
		text-align: center;
		font-size: 10pt;
		font-weight: bold;
	}
	td { 
		text-align: center;
		font-size: 10pt;
	}	
	BODY{
		padding-left:15px;
		padding-top:15px;
	}	
</style>

</head>
<body>
	
	<h1>UCAR Member Institutions and Affiliates: Resource Count Report</h1>
	<p>Date generated: <%= new java.util.Date().toString() %></p>

	<x:parse var="instXml">
		<c:import url="ucar-member-institutions.xml" charEncoding="UTF-8" />
	</x:parse>
	
	<c:set var="instCount" value="0"/>
	<c:set var="concatInstNames" value=""/>
	<c:set var="concatUrls" value=""/>
	
	<table border="1" cellpadding="4" cellspacing="0" width="90%">		
		<tr>
			<th colspan="2">Institution Name</th>
			<th>Institution name found in<br/>contributor field</th>
			<th>Institution name found in<br/>any metadata field</th>
			<th>Institution name found in<br/>site content</th>
			<th>Institution domain found in<br/>URL field</th>			
			<th>Institution name or domain found<br/>anywhere in metadata or content</th>							
		</tr>
		<x:forEach var="inst" varStatus="i" select="$instXml/institutions/table/tr/td">
			<x:choose>
				<x:when select="$inst[@type='headRow']">
					<c:set var="instCount" value="0"/>
					<th colspan="7"><x:out select="$inst"/></th>
				</x:when>
				<x:otherwise>
					<c:set var="instName"><x:out select="$inst/p/a/b|$inst/p/b/a"/></c:set>
					<c:set var="urlQuery" value=""/>
					<x:forEach var="url" varStatus="j" select="$inst//@href">
						<c:set var="urlString"><x:out select="$url"/></c:set>
						<c:set var="urlCurrent">${f:replaceFirst(urlString,'http://[^\\.]+\\.','')}</c:set>
						<c:set var="urlCurrent">h*${f:replaceFirst(urlCurrent,'[/|:].*','')}*</c:set>
						<c:set var="urlQuery">${urlQuery}${j.first ? '' : ' OR '}${urlCurrent}</c:set> 
					</x:forEach>
					<c:set var="urlQuery">${urlQuery}</c:set>
					<%
						try {
							String instName = (String) pageContext.getAttribute("instName");
							String urlQuery = (String) pageContext.getAttribute("urlQuery");

							instName = instName.replaceAll("\\s+", " ");
							
							ResultDoc[] resultDocs = null;
							SimpleLuceneIndex index = (SimpleLuceneIndex) application.getAttribute("index");
							
							String query = null;
							
							query = "itemContent:(\"" + instName + "\")";
							query = "(" + query + ") AND xmlFormat:adn";
							resultDocs = index.searchDocs(query);
							pageContext.setAttribute("numContent", (resultDocs == null ? 0 : resultDocs.length) );
							pageContext.setAttribute("numContentQuery", query );
							
							query = "organizationInstName:(\"" + instName + "\")";
							query = "(" + query + ") AND xmlFormat:adn";
							resultDocs = index.searchDocs(query);
							pageContext.setAttribute("numInst", (resultDocs == null ? 0 : resultDocs.length) );
							pageContext.setAttribute("numInstQuery", query );
		
							query = "admindefault:(\"" + instName + "\")";
							query = "(" + query + ") AND xmlFormat:adn";
							resultDocs = index.searchDocs(query);
							pageContext.setAttribute("numMetadata", (resultDocs == null ? 0 : resultDocs.length) );
							pageContext.setAttribute("numMetadataQuery", query );

							query = "url:(" + urlQuery + ") AND xmlFormat:adn";
							resultDocs = index.searchDocs(query);
							pageContext.setAttribute("numUrl", (resultDocs == null ? 0 : resultDocs.length) );
							pageContext.setAttribute("numUrlQuery", query );
							
							query = "admindefault:(\"" + instName + "\") OR url:(" + urlQuery + ") OR itemContent:(\"" + instName + "\") OR organizationInstName:(\"" + instName + "\")";
							query = "(" + query + ") AND xmlFormat:adn";
							resultDocs = index.searchDocs(query);
							pageContext.setAttribute("numAnywhere", (resultDocs == null ? 0 : resultDocs.length) );
							pageContext.setAttribute("numAnywhereQuery", query );						
							
						} catch ( Throwable t ) {
							System.out.println("Error: " + t );
							t.printStackTrace();
						}
					%>
					<tr>
						<td style="text-align: left;">
							<c:set var="instCount">${instCount+1}</c:set>
							${instCount}
						</td>						
						<td style="text-align: left;">
							<div style="text-transform: capitalize;">
								<c:set var="urlString"><x:out select="$inst//@href"/></c:set>
								<a href='${urlString}' target="_blank">${instName}</a>
							</div>
							
							<%-- Concatinate the search strings for use below --%>
							<c:set var="concatInstNames">${concatInstNames}${i.count < 3 ? ' ' : ' OR '}"${instName}"</c:set>
							<c:set var="concatUrls">${concatUrls}${i.count < 3 ? ' ' : ' OR '}${urlQuery}</c:set>
								
							
							
							<%-- <code><nobr>${urlQuery}</nobr></code> --%>
							<%-- <x:forEach var="url" varStatus="j" select="$inst//@href">
								<c:set var="urlString"><x:out select="$url"/></c:set>
								<a href='${urlString}' target="_blank">${urlString}</a>${j.last ? '' : ','}
							</x:forEach> --%>
							
							
							<%-- <ul>
								<x:forEach var="dept" varStatus="j" select="$inst//ul//li/a">
									<li><x:out select="$dept"/></li>
								</x:forEach>
							</ul> --%>
						</td>
						<td>
							<c:url var="qUrl" value='/admin/query.do'>
								<c:param name="q">${numInstQuery}</c:param>
							</c:url>
							<a href="${qUrl}" target="_blank">${numInst}</a>
						</td>
						<td>
							<c:url var="qUrl" value='/admin/query.do'>
								<c:param name="q">${numMetadataQuery}</c:param>
							</c:url>				
							<a href="${qUrl}" target="_blank">${numMetadata}</a>
						</td>
						<td>
							<c:url var="qUrl" value='/admin/query.do'>
								<c:param name="q">${numContentQuery}</c:param>
							</c:url>			
							<a href="${qUrl}" target="_blank">${numContent}</a>
						</td>
						<td>
							<c:url var="qUrl" value='/admin/query.do'>
								<c:param name="q">${numUrlQuery}</c:param>
							</c:url>			
							<a href="${qUrl}" target="_blank">${numUrl}</a>
						</td>						
						<td>
							<c:url var="qUrl" value='/admin/query.do'>
								<c:param name="q">${numAnywhereQuery}</c:param>
							</c:url>					
							<a href="${qUrl}" target="_blank">${numAnywhere}</a>
						</td>				
					</tr>
				</x:otherwise>
			</x:choose>
		</x:forEach>
			
			<%-- ======================= Search for all institutions ... ======================= --%>
			<%
				try {
					String instName = (String) pageContext.getAttribute("concatInstNames");
					String urlQuery = (String)  pageContext.getAttribute("concatUrls");
					instName = instName.replaceAll("\\s+", " ");
					urlQuery = urlQuery.replaceAll("\\s+", " ");
					
					ResultDoc[] resultDocs = null;
					SimpleLuceneIndex index = (SimpleLuceneIndex) application.getAttribute("index");
					
					String query = null;
					
					query = "itemContent:(" + instName + ")";
					query = "(" + query + ") AND xmlFormat:adn";
					resultDocs = index.searchDocs(query);
					pageContext.setAttribute("numContent", (resultDocs == null ? 0 : resultDocs.length) );
					pageContext.setAttribute("numContentQuery", query );
					
					query = "organizationInstName:(" + instName + ")";
					query = "(" + query + ") AND xmlFormat:adn";					
					resultDocs = index.searchDocs(query);
					pageContext.setAttribute("numInst", (resultDocs == null ? 0 : resultDocs.length) );
					pageContext.setAttribute("numInstQuery", query );
		
					query = "admindefault:(" + instName + ")";
					query = "(" + query + ") AND xmlFormat:adn";
					resultDocs = index.searchDocs(query);
					pageContext.setAttribute("numMetadata", (resultDocs == null ? 0 : resultDocs.length) );
					pageContext.setAttribute("numMetadataQuery", query );

					query = "url:(" + urlQuery + ") AND xmlFormat:adn";
					resultDocs = index.searchDocs(query);
					pageContext.setAttribute("numUrl", (resultDocs == null ? 0 : resultDocs.length) );
					pageContext.setAttribute("numUrlQuery", query );
					
					query = "admindefault:(" + instName + ") OR url:(" + urlQuery + ") OR itemContent:(" + instName + ") OR organizationInstName:(" + instName + ")";
					query = "(" + query + ") AND xmlFormat:adn";
					resultDocs = index.searchDocs(query);
					pageContext.setAttribute("numAnywhere", (resultDocs == null ? 0 : resultDocs.length) );
					pageContext.setAttribute("numAnywhereQuery", query );
					
					query = "xmlFormat:adn";
					resultDocs = index.searchDocs(query);
					pageContext.setAttribute("totalResources", (resultDocs == null ? 0 : resultDocs.length) );
					pageContext.setAttribute("totalResourcesQuery", query );					
					
				} catch ( Throwable t ) {
					System.out.println("Error: " + t );
					t.printStackTrace();
				}
			%>
			
			<th colspan="7">All Institutions Combined</th>
			<tr>
				<td>&nbsp;</td>						
				<td style="text-align: left;">
					Any Institution (At least one or more institution name / URL appears)
				</td>
				<td>
					<c:url var="qUrl" value='/admin/query.do'>
						<c:param name="q">${numInstQuery}</c:param>
					</c:url>
					<a href="${qUrl}" target="_blank">${numInst}</a>
				</td>
				<td>
					<c:url var="qUrl" value='/admin/query.do'>
						<c:param name="q">${numMetadataQuery}</c:param>
					</c:url>				
					<a href="${qUrl}" target="_blank">${numMetadata}</a>
				</td>
				<td>
					<c:url var="qUrl" value='/admin/query.do'>
						<c:param name="q">${numContentQuery}</c:param>
					</c:url>			
					<a href="${qUrl}" target="_blank">${numContent}</a>
				</td>
				<td>
					<c:url var="qUrl" value='/admin/query.do'>
						<c:param name="q">${numUrlQuery}</c:param>
					</c:url>			
					<a href="${qUrl}" target="_blank">${numUrl}</a>
				</td>				
				<td>
					<c:url var="qUrl" value='/admin/query.do'>
						<c:param name="q">${numAnywhereQuery}</c:param>
					</c:url>					
					<a href="${qUrl}" target="_blank">${numAnywhere}</a>
				</td>				
			</tr>

			<tr>
				<td>&nbsp;</td>									
				<td colspan="5" style="text-align: left;">
					Total number of resources in library:
				</td>						
				<td>
					<c:url var="qUrl" value='/admin/query.do'>
						<c:param name="q">${totalResourcesQuery}</c:param>
					</c:url>					
					<a href="${qUrl}" target="_blank">${totalResources}</a>
				</td>	
			</tr>
			
	</table>
	
	
	<br/><br/><br/>
	



	
</body>
</html>

