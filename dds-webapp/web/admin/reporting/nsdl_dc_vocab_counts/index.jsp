<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page language="java" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<%@ page import="edu.ucar.dls.index.*" %>

<%@ include file="/JSTLTagLibIncludes.jsp" %>

<%-- Calculate/show the totals for all terms in the field(s) that were not in the vocabs? --%>
<c:set var="doTermTotals" value="${true}"/>

<%-- Max number of terms to search, otherwise display N/A (Index can process 30,000 max) --%>
<c:set var="maxTermsForSearch" value="29000"/>

<%-- Limit the report to a given collection, if not empty or if not -all- --%>
<c:set var="collectionKey" value="${param.ky == '-all-' ? '' : param.ky}"/>

<c:set var="vocabsIndex">${initParam.nsdlDcVocabsConfig}vocab_selections.xml</c:set>
<c:set var="collectionGroups">${initParam.nsdlDcVocabsConfig}pathway-groups.xml</c:set>

<%-- 
	This page generates reports for the number of records in the repository that catalog
	given NSDL DC vocabularies including Education Level, Audience, Type, etc.
	
	See example groups files and documentation at dds-webapp/web/WEB-INF/conf/nsdlDcNormalizationConfigs
	
	This reporting mechanism can be modified to genarate reports for any term 
	counts across a DDS repository using OPML files for configuration. See
	code below for examples.
--%>

<html>
<head>
<title>NSDL DC Vocab Counts Report</title>

<c:if test="${param.viewContext != 'reportFile'}">
	<link rel='stylesheet' type='text/css' href='../../dds_admin_styles.css'/>
	
	<style type="text/css">
		table{ 
			background-color:#f1f1f1;
		}
		th { 
			text-align: center;
			font-size: 10pt;
			font-weight: bold;
			background-color:#ccc;
		}
		td { 
			text-align: left;
			font-size: 10pt;
			vertical-align: top;
		}	
		BODY{
			padding-left:15px;
			padding-top:15px;
		}	
	</style>
</c:if>

</head>
<body>
	
	<%-- Grab the Collections DOM --%>
	<c:url value="${f:serverUrl(pageContext.request)}/${pageContext.request.contextPath}/services/ddsws1-1" var="listCollsReqUrl">
		<c:param name="verb" value="ListCollections"/>
	</c:url>
	<xtags:parse id="listCollectionsDom">
		<xtags:style xml="${listCollsReqUrl}" xsl="/include/remove_namespace.xsl"/>
	</xtags:parse>
	
	<c:import url="${collectionGroups}" charEncoding="UTF-8" var="xmlOutput"/>
	<x:transform xslt="${f:removeNamespacesXsl()}" xml="${xmlOutput}" var="collectionGroupsDom"/>	

	<c:set var="selectForm">	
		<div style="padding:0px 0px 6px 0px;">
			<form action="" style="padding:0px; margin:0px;">
				<div>Select a collection and click <i>Go</i> to generate the report:</div>
				<select name="ky">
					<option value="-all-">-- All collections --</option>
					<x:forEach var="group1" varStatus="i" select="$collectionGroupsDom/opml/body/outline[@type = 'group']">
						<c:set var="groupName"><x:out select="$group1/@text"/></c:set>
						<c:set var="paramName">GRP-${groupName}</c:set>
						<option value="${paramName}" ${collectionKey == paramName ? 'selected' : ''}>COLLECTION GROUP: ${groupName}</option>
					</x:forEach>				
					<xtags:forEach id="coll" select="$listCollectionsDom/DDSWebService/ListCollections/collections/collection">
						<xtags:variable id="collKy" select="$coll/searchKey"/>
						<xtags:variable id="collName" select="$coll/renderingGuidelines/label"/>
						<xtags:variable id="xmlFormat" select="$coll/additionalMetadata/dlese_collect/formatOfRecords"/>
						<xtags:variable id="numRecs" select="$coll/additionalMetadata/dlese_collect/numRecordsIndexed"/>
						<c:if test="${xmlFormat == 'nsdl_dc'}">
							<c:if test="${collectionKey == collKy}">
								<c:set var="selectedCollectionName" value="${collName}"/>
							</c:if>
							<option value="${collKy}" ${collectionKey == collKy ? 'selected' : ''}>${collName} (${numRecs})</option>
						</c:if>
					</xtags:forEach>
				</select>
				<input type="submit" value="Go" />
				<c:if test="${not empty param.ky}">
					&#187; <a href="./">start over</a>
				</c:if>
				<div><input type="checkbox" name="viewContext" value="reportFile" />
					Output in summary report format (XHTML)
				</div>
			</form>
		</div>
	</c:set>
	
	<c:choose>
		<c:when test="${fn:startsWith(collectionKey,'GRP-')}">
			<c:set var="collectionGroup">${fn:replace(collectionKey,'GRP-','')}</c:set>
			<c:set var="kyQueryClause"><x:forEach var="group1" select="$collectionGroupsDom/opml/body/outline[@text=$collectionGroup]/outline/@vocab" 
					varStatus="i">"<x:out select="$group1"/>"${i.last ? '' : ' OR '}</x:forEach></c:set>
			<c:set var="kyQueryClause">ky:(${kyQueryClause})</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="kyQueryClause">ky:("${collectionKey}")</c:set>
		</c:otherwise>
	</c:choose>	
	
	<%-- Display the Collection name, which will be one of these three... --%>
	<h1>${selectedCollectionName}${collectionGroup}${param.ky == '-all-' ? 'All Collections' : ''}</h1>
	<p>
		<div>Date generated: <%= new java.util.Date().toString() %></div>
		<c:if test="${param.viewContext != 'reportFile'}">
			<div>Using vocab configs at <a href="${vocabsIndex}">${vocabsIndex}</a></div>
			<div>Using collection groups defined at <a href="${collectionGroups}">${collectionGroups}</a></div>
			<div>See the <a href="./report_generator.jsp">Collection Report File Generator</a></div>
		</c:if>
	</p>	
	
	
	<c:if test="${param.viewContext != 'reportFile'}">
		${selectForm}
	</c:if>

	<%-- Only generate a report if a param.ky has been passed in... --%>
	<c:if test="${param.ky != null}">
	
		<%-- List of groups OPML files to process and corresponding field(s) --%>
		<xtags:parse id="groupsFiles">
			${f:timedImport(vocabsIndex, 12000)}
		</xtags:parse>
		
		<xtags:forEach id="groupsFile" select="$groupsFiles/groupsFiles/groupsFile">
			<xtags:variable id="groupsUrl" select="$groupsFile/url"/>
	
			<c:remove var="allTermsMap"/>
			<c:remove var="foundTermsMap"/>
			<c:remove var="notFoundTermsMap"/>
			<c:remove var="allTermsQuery"/>
			<c:remove var="foundTermsQuery"/>
			<c:remove var="notFoundTermsQuery"/>
			
			<c:if test="${doTermTotals}">
			
				<xtags:forEach id="searchFieldNode" select="$groupsFile/field">
					<c:set var="searchField"><xtags:valueOf select="$searchFieldNode"/></c:set>
					<c:url value="${f:serverUrl(pageContext.request)}/${pageContext.request.contextPath}/services/ddsws1-1" var="listTermsReqUrl">
						<c:param name="verb" value="ListTerms"/>
						<c:param name="field" value="${searchField}"/>
					</c:url>
					<xtags:parse id="listTermsDom">
						<xtags:style xml="${listTermsReqUrl}" xsl="/include/remove_namespace.xsl"/>
					</xtags:parse>
									
					<xtags:forEach id="term" select="$listTermsDom/DDSWebService/ListTerms/terms/term">
						<xtags:variable id="theTerm" select="$term"/>
						<%-- <div><b>All terms map:</b><c:out value="${theTerm}" escapeXml="true"/></div> --%>
						<c:set var="allTermsMap" value="${f:map(allTermsMap,theTerm,'')}" /> 
					</xtags:forEach>
					<%-- <c:forEach items="${allTermsMap}" var="allTerms">
						${allTerms.key},
					</c:forEach> --%>
				</xtags:forEach>
			</c:if>
		
			<c:import url="${groupsUrl}" charEncoding="UTF-8" var="xmlOutput"/>
			<x:transform xslt="${f:removeNamespacesXsl()}" xml="${xmlOutput}" var="groupsXml"/>
		
			<table border="1" cellpadding="4" cellspacing="0" width="90%" style="margin-bottom:20px">
				<tr>
					<th colspan="4" style="font-size:14pt">
						<div>
							<x:out select="$groupsXml/opml/head/title"/>
							<c:set var="fieldsSearched"><xtags:forEach id="searchFieldNode" select="$groupsFile/field"><xtags:valueOf select="$searchFieldNode"/>, </xtags:forEach></c:set>
							<c:set var="fieldsSearched" value="${fn:substring(fieldsSearched,0,(fn:length(fieldsSearched) - 1))}"/>
							<span style="font-size:10pt; font-weight: normal">Search field(s): ${fieldsSearched}</span>
						</div>
						<c:if test="${not empty collectionKey}">
							<div style="font-size:10pt;">
								<c:choose>
									<c:when test="${not empty collectionGroup}">
										For collection group ${collectionGroup}									
									</c:when>
									<c:otherwise>
										For collection ${selectedCollectionName}
									</c:otherwise>
								</c:choose>
							</div>
						</c:if>
					</th>
				</tr>
				<tr>
					<th>Normalized vocab term</th>
					<th>Number of records</th>
					<c:if test="${param.viewContext != 'reportFile'}">
						<th>Vocab terms</th>
						<th>Search It</th>
					</c:if>
				</tr>
					<x:forEach var="group1" varStatus="i" select="$groupsXml/opml/body//outline[@type = 'group']">
						<tr>
							<x:choose>
								<x:when select="$group1/@vocab">
									<td>
										<x:out select="$group1/@vocab"/>	
									</td>
									<td>
										<c:set var="termsQuery"><xtags:forEach 
											id="searchFieldNode" select="$groupsFile/field"><xtags:valueOf select="$searchFieldNode"/>:(<x:forEach var="allVocabs" varStatus="i" 
											select="$group1//@vocab"><c:set 
											var="vocabTerm"><x:out 
											select="$allVocabs" escapeXml="false"/></c:set><c:set 
											var="foundTermsMap" value="${f:map(foundTermsMap,vocabTerm,'')}" />"${vocabTerm}"${i.last ? '' : ' OR '}</x:forEach>) OR </xtags:forEach></c:set>
										<c:set var="termsQuery" value="${fn:substring(termsQuery,0,(fn:length(termsQuery) - 3))}"/>
										<c:if test="${not empty collectionKey}">
											<c:set var="termsQuery">${kyQueryClause} AND (${termsQuery})</c:set>
										</c:if>
										<%
											try {
												// Search for the number of records that contain the given terms:
												String termsQuery = (String) pageContext.getAttribute("termsQuery");
												
												ResultDocList resultDocs = null;
												SimpleLuceneIndex index = (SimpleLuceneIndex) application.getAttribute("index");
												
												String query = termsQuery;
							
												//System.out.println("query is: " + query);
												
												resultDocs = index.searchDocs(query);
												pageContext.setAttribute("numResultsTermsQuery", resultDocs.size() );
												
											} catch ( Throwable t ) {
												System.out.println("Error: " + t );
												t.printStackTrace();
											}
										%>
										${numResultsTermsQuery}							
									</td>
									<c:if test="${param.viewContext != 'reportFile'}">
										<td>
											<x:forEach var="allVocabs" varStatus="i" select="$group1//@vocab">
												<nobr>&quot;<x:out select="$allVocabs" escapeXml="true"/>&quot;${i.last ? '' : ','}</nobr>
											</x:forEach>
										</td>
										<td>
											<c:url value="dds_search.jsp" var="searchUrl">
												<c:param name="q" value="${termsQuery}"/>
												<c:param name="action" value="Search"/>
											</c:url>
											<a href="${searchUrl}">Search</a>
										</td>
									</c:if>
								</x:when>
							</x:choose>
						</tr>
					</x:forEach>
					
					
					<c:if test="${empty collectionKey}">
									
						<%-- Generate a query for all terms/keys that were found in the vocab/groups file: --%>
						<c:if test="${not empty foundTermsMap && fn:length(foundTermsMap) < maxTermsForSearch}">
							<c:set var="foundTermsQuery"><xtags:forEach 
									id="searchFieldNode" 
									select="$groupsFile/field"><xtags:valueOf select="$searchFieldNode"/>:(<c:forEach items="${foundTermsMap}" var="foundTerms" varStatus="i">"${foundTerms.key}"${i.last ? '' : ' OR '}</c:forEach>) OR </xtags:forEach></c:set>
							<c:set var="foundTermsQuery" value="${fn:substring(foundTermsQuery,0,(fn:length(foundTermsQuery) - 3))}"/>
						</c:if>
						
						<%-- Debug output... <c:forEach items="${foundTermsMap}" var="foundTerm">
								<div><b>foundTermsMap: </b><c:out value="${foundTerm.key}" escapeXml="true"/></div>					
						</c:forEach> --%>				
						
						<%-- Generate a query for all remaining terms/keys that were not found in the index for this/these field(s): --%>
						<c:forEach items="${allTermsMap}" var="allTerms">
							<c:if test="${!f:contains(foundTermsMap,allTerms.key)}">
								<%-- <div><b>notFoundTermsMap map: </b><c:out value="${allTerms.key}" escapeXml="true"/></div> --%>					
								<c:set var="notFoundTermsMap" value="${f:map(notFoundTermsMap,allTerms.key,'')}" />
							</c:if>
						</c:forEach>
						<c:if test="${not empty notFoundTermsMap && fn:length(notFoundTermsMap) < maxTermsForSearch}">
							<c:set var="notFoundTermsQuery"><xtags:forEach 
									id="searchFieldNode" 
									select="$groupsFile/field"><xtags:valueOf select="$searchFieldNode"/>:(<c:forEach items="${notFoundTermsMap}" var="notFoundTerms" varStatus="i">"${notFoundTerms.key}"${i.last ? '' : ' OR '}</c:forEach>) OR </xtags:forEach></c:set>
							<c:set var="notFoundTermsQuery" value="${fn:substring(notFoundTermsQuery,0,(fn:length(notFoundTermsQuery) - 3))}"/>
						</c:if>
						<c:if test="${not empty collectionKey && not empty foundTermsQuery}">
							<c:set var="foundTermsQuery">${kyQueryClause} AND (${foundTermsQuery})</c:set>
						</c:if>
						<c:if test="${not empty collectionKey && not empty notFoundTermsQuery}">
							<c:set var="notFoundTermsQuery">${kyQueryClause} AND (${notFoundTermsQuery})</c:set>
						</c:if>					
						
						<%-- Display totals/summaries --%>
						<tr>
							<td>All vocabs for this/these field(s) listed above</td>
							<td>
								<c:choose>
									<c:when test="${not empty foundTermsQuery}">
										<%
											try {
												String termsQuery = (String) pageContext.getAttribute("foundTermsQuery");
												
												ResultDocList resultDocs = null;
												SimpleLuceneIndex index = (SimpleLuceneIndex) application.getAttribute("index");
												
												String query = termsQuery;
												
												resultDocs = index.searchDocs(query);
												pageContext.setAttribute("numResultsFoundTermsQuery", resultDocs.size() );
												
											} catch ( Throwable t ) {
												System.out.println("Error: " + t );
												t.printStackTrace();
											}
										%>
										${numResultsFoundTermsQuery}
									</c:when>
									<c:when test="${empty foundTermsMap}">
										N/A
									</c:when>									
									<c:otherwise>
										N/A (Too many terms to process)
									</c:otherwise>
								</c:choose>
							</td>
							<c:if test="${param.viewContext != 'reportFile'}">
								<td>
									<c:forEach items="${foundTermsMap}" var="foundTerms" varStatus="i">
										"<c:out value="${foundTerms.key}" escapeXml="true"/>"${i.last ? '' : ','}
									</c:forEach>
									${empty foundTermsMap ? '[No defined terms were found]' : ''}
								</td>
								<td>
									<c:choose>
										<c:when test="${not empty foundTermsQuery}">
											<c:url value="dds_search.jsp" var="searchUrl">
												<c:param name="q" value="${foundTermsQuery}"/>
												<c:param name="action" value="Search"/>
											</c:url>
											<a href="${searchUrl}">Search</a>
										</c:when>
										<c:otherwise>
											N/A
										</c:otherwise>
									</c:choose>
								</td>
							</c:if>
						</tr>
						
						<tr>
							<td>All terms/vocabs not listed above that were found in this/these field(s)</td>
							<td>
								<c:choose>
									<c:when test="${not empty notFoundTermsQuery}">					
										<%
											try {
												String termsQuery = (String) pageContext.getAttribute("notFoundTermsQuery");
												
												ResultDocList resultDocs = null;
												SimpleLuceneIndex index = (SimpleLuceneIndex) application.getAttribute("index");
												
												String query = termsQuery;
												
												resultDocs = index.searchDocs(query);
												pageContext.setAttribute("numResultsNotFoundTermsQuery", resultDocs.size() );
												
											} catch ( Throwable t ) {
												System.out.println("Error: " + t );
												t.printStackTrace();
											}
										%>
										${numResultsNotFoundTermsQuery}
									</c:when>
									<c:when test="${empty notFoundTermsMap}">
										[No additional terms were found]
									</c:when>
									<c:otherwise>
										N/A (Too many terms to process:${fn:length(notFoundTermsMap)})
									</c:otherwise>
								</c:choose>						
							</td>
							<c:if test="${param.viewContext != 'reportFile'}">
								<td>
									<c:forEach items="${notFoundTermsMap}" var="notFoundTerms" varStatus="i">
										"<c:out value="${notFoundTerms.key}" escapeXml="true"/>"${i.last ? '' : ','}
									</c:forEach>
									${empty notFoundTermsMap ? '[No additional terms were found]' : ''}
								</td>
								<td>
									<c:choose>
										<c:when test="${not empty notFoundTermsQuery}">
											<c:url value="dds_search.jsp" var="searchUrl">
												<c:param name="q" value="${notFoundTermsQuery}"/>
												<c:param name="action" value="Search"/>
											</c:url>
											<a href="${searchUrl}">Search</a>
										</c:when>
										<c:otherwise>
											[No additional terms were found]
										</c:otherwise>
									</c:choose>
								</td>
							</c:if>
						</tr>
					
					</c:if>
					
					
					<%-- Generate a query for all records that have something in this/these field(s): --%>
					<c:set var="indexedXpathQuery"><xtags:forEach 
								id="searchFieldNode" 
								select="$groupsFile/field"><c:set 
								var="searchFieldPath"><xtags:valueOf select="$searchFieldNode"/></c:set>${fn:substring(searchFieldPath,fn:indexOf(searchFieldPath,'//')+1,fn:length(searchFieldPath))} OR </xtags:forEach></c:set>
						<c:set var="indexedXpathQuery" value="${fn:substring(indexedXpathQuery,0,(fn:length(indexedXpathQuery) - 3))}"/>
						<c:set var="indexedXpathQuery" value="indexedXpaths:(${indexedXpathQuery})"/>
					<c:if test="${not empty collectionKey && not empty indexedXpathQuery}">
						<c:set var="indexedXpathQuery">(${indexedXpathQuery}) AND ${kyQueryClause}</c:set>
					</c:if>						
					
					<tr>
						<td>Total records with a vocab cataloged in this/these field(s)</td>
						<td>
							<%
								try {
									String termsQuery = (String) pageContext.getAttribute("indexedXpathQuery");
									
									ResultDocList resultDocs = null;
									SimpleLuceneIndex index = (SimpleLuceneIndex) application.getAttribute("index");
									
									String query = termsQuery;
			
									//System.out.println("query2 is: " + query);								
									
									resultDocs = index.searchDocs(query);
									pageContext.setAttribute("numResultsAllTermsQuery", resultDocs.size() );
									
								} catch ( Throwable t ) {
									System.out.println("Error: " + t );
									t.printStackTrace();
								}
							%>
							${numResultsAllTermsQuery}	
						</td>
						<c:if test="${param.viewContext != 'reportFile'}">
							<td>
								${indexedXpathQuery}
								<%-- <c:forEach items="${allTermsMap}" var="allTerms" varStatus="i">
									"${allTerms.key}"${i.last ? '' : ','}
								</c:forEach>
								${empty allTermsMap ? '[N/A]' : ''} --%>
							</td>
							<td>
								<c:url value="dds_search.jsp" var="searchUrl">
									<c:param name="q" value="${indexedXpathQuery}"/>
									<c:param name="action" value="Search"/>
								</c:url>
								<a href="${searchUrl}">Search</a>
							</td>
						</c:if>
					</tr>
					
					<tr>
						<td>Total num records in ${empty collectionKey ? 'the repository' : 'this collection'}</td>
						<td>
							<c:set var="allRecsQuery">
								<c:choose>
									<c:when test="${not empty collectionKey}">${kyQueryClause}</c:when>
									<c:otherwise>*:*</c:otherwise>
								</c:choose>
							</c:set>
							<%
								try {
									ResultDocList resultDocs = null;
									SimpleLuceneIndex index = (SimpleLuceneIndex) application.getAttribute("index");
									String allRecsQuery = (String) pageContext.getAttribute("allRecsQuery");
									
									String query = allRecsQuery;
									//System.out.println("query is: " + query);								
									
									resultDocs = index.searchDocs(query);
									pageContext.setAttribute("totalNumRecordsInRepository", resultDocs.size() );
									
								} catch ( Throwable t ) {
									System.out.println("Error: " + t );
									t.printStackTrace();
								}
							%>
							${totalNumRecordsInRepository}	
						</td>
						<c:if test="${param.viewContext != 'reportFile'}">
							<td>
								N/A
							</td>
							<td>
								<c:set var="allRecsQuery">
									<c:choose>
										<c:when test="${not empty collectionKey}">${kyQueryClause}</c:when>
										<c:otherwise>*:*</c:otherwise>
									</c:choose>
								</c:set>
								<c:url value="dds_search.jsp" var="searchUrl">
									<c:param name="q" value="${allRecsQuery}"/>
									<c:param name="action" value="Search"/>
								</c:url>
								<a href="${searchUrl}">Search</a>
							</td>
						</c:if>
					</tr>				
				</table>			
			</xtags:forEach>
		</c:if>
	<br/><br/><br/>

	
</body>
</html>

