<%--
	This JSP page displays the search results. It is meant to be included inside a
	JSP page that has just performed a ddsws Search or UserSearch request and stored the XML results in
	variable 'searchXmlDom'
	Requires web_service_connection.jsp to be included prior to using this page.
	
	Example use:
	
	<!-- Search has just been performed -->
	<c:when test="${ (not empty param.q || not empty param.qh || not empty param.su || 
					not empty param.gr || not empty param.re || not empty param.ky || not empty param.cs) 
					&& empty param.fullDescription }">
		<%@ include file="search_results.jsp" %>
	</c:when>
	
	This is JSP client version @DDSWS_CLIENT_VERSION@.		
--%>

<c:import var="transformVocabsCommaSeparated" url="xsl_files/vocabs_comma_separated.xsl" />


<c:choose>
	<%-- If a web service connection error has occured, send a message to the user --%>
	<c:when test="${not empty searchXmlDomError}">
		<br><br>Sorry, our system has encountered a problem and we were unable to perform your 
		search at this time. Please try again later.		
	</c:when>
	
	<%-- Display the record metadata to the user --%> 
	<c:otherwise>	
		<%-- Set up some variables that we will use below to display to the user --%>
		<c:if test="${not empty param.q}">										
			<%-- Store the text the user entered for display below --%>	
			<c:set var="usersText">
				<b><i>${fn:trim( param.q )}</i></b>
			</c:set>
		</c:if> 						
					
		<%-- Save the number of results in variable 'numResults' for use later --%>
		<c:set var="numResults">
			<x:out select="$searchXmlDom/DDSWebService/Search/resultInfo/totalNumResults"/> 
		</c:set>	

		<%-- Set which keywords to highlight --%>
		<dt:setKeywordsHighlight keywords="${param.q}" highlightColor="${keywordHighlightColor}" />

		
		<%-- Display a heading for the search results --%>
		<table class="banner" cellpadding="0" cellspacing="1" border="0">
			<tr class="banner" valign="center">
				<td class="banner dleseTmpl">
					<div class="bannerTxt">
					<c:choose>
						<c:when test="${not empty param.d || not empty selectedSmartLink}">
							Resources about: ${selectedSmartLink} ${param.d} 
						</c:when>
						<c:otherwise>
							Resources
						</c:otherwise>
					</c:choose>
					</div>
				</td>
			</tr>
		</table>  
		
		<table class="dleseTmpl" width="100%" cellpadding="4" cellspacing="0">													
			<c:choose>
				<c:when test="${numResults > 0}">
					<tr class="dleseTmpl">
						<td class="dleseTmpl">
							<%-- Display the search options the user entered --%>
							<nobr>
							<c:if test="${empty param.d && empty selectedSmartLink}">
								Results
							</c:if>						
							<c:out value="${startingOffset +1}"/> 
							-
							<c:if test="${startingOffset + resultsPerPage > numResults}">
								<c:out value="${numResults}"/>
							</c:if>
							<c:if test="${startingOffset + resultsPerPage <= numResults}">
								<c:out value="${startingOffset + resultsPerPage}"/>
							</c:if>							
							</nobr>					
							<nobr>
							of <c:out value="${numResults}"/>							
							</nobr>
							${empty usersText ? '' : 'for'} ${usersText}								
						</td>
						<td class="dleseTmpl" align="right">
							<%-- Include pager.jsp, which creates and stores the HTML for the pager in variable 'pager' --%>
							<%@ include file="pager.jsp" %>									
							<%-- Then output the pager HTML here (and again further below) --%>
							<p class="dleseTmpl">${pager}</p>
						</td>				
					</tr>
				</c:when>
				<c:otherwise>
					<c:if test="${empty param.d}">
						<tr class="dleseTmpl">
							<td class="dleseTmpl" colspan="2">
								${empty usersText ? '' : 'You searched for'} ${usersText}
							</td>
						</tr>
					</c:if>
				</c:otherwise>
			</c:choose>	
			<c:if test="${not empty userSelections}">
				<tr class="dleseTmpl">
					<td class="dleseTmpl" colspan="2">
						<%-- Display the selections the user choose when performing the search --%>
						${numResults == 0 && empty param.q && empty selectedSmartLink ? 'You searched for' : ''} <b>${userSelections}</b>
					</td>
				</tr>
			</c:if>							
			<%-- Render a gray line --%>
			<tr>
				<td colspan="2">
					<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0">
						<tr><td bgcolor="#999999" height="1"></td></tr>
					</table>
				</td>
			</tr>

			<c:choose> 
				<%-- If the total number of search results is greater than zero, display them --%>
				<c:when test="${numResults > 0}">
	
				
				<%-- Loop through each of the record nodes --%>
				<x:forEach select="$searchXmlDom/DDSWebService/Search/results/record" varStatus="status">
				<c:set var="myId"><x:out select="head/id"/></c:set>
				<tr class="dleseTmpl">
					<td class="dleseTmpl" colspan="2">
						<%-- We are already at XPath $searchXmlDom/DDSWebService/Search/results/record
						so we can continue our path from there --%>
						<c:set var="resourceURL">
							<x:out select="metadata/itemRecord/technical/online/primaryURL"/>
						</c:set>
						<c:set var="redirectURL">
							<c:choose>
								<c:when test="${not empty redirectBaseUrl}">
									${redirectBaseUrl}/T=search&Q=${f:URLEncoder(param.q)}${f:URLEncoder(param.qh)}&R=${startingOffset + status.index}&SC=${clientName}&ID=${myId}*${resourceURL}
								</c:when>
								<c:otherwise>
									${resourceURL}
								</c:otherwise>
							</c:choose>
						</c:set>									
						<a href="${redirectURL}" target="_blank" 
							class="blacklnk"><x:out select="metadata/itemRecord/general/title" escapeXml="false"/></a><br>
						<c:choose>
							<c:when test="${ fn:length(resourceURL) > maxUrlLength }">
								<a href="${redirectURL}" class="dleseTmpl" target="_blank">${ fn:substring(resourceURL,0,maxUrlLength)} ...</a>	
							</c:when>
							<c:otherwise>
								<a href="${redirectURL}" class="dleseTmpl" target="_blank">${resourceURL}</a>
							</c:otherwise>
						</c:choose>												
					</td>			
				</tr>
				<tr class="dleseTmpl">
					<td class="dleseTmpl" colspan="2">
						<div class="description">
							<c:set var="description">
								<x:out select="metadata/itemRecord/general/description" escapeXml="false"/>
							</c:set>
							<%-- Truncate the description text if necessary --%>
							<c:if test="${not empty maxDescriptionLength && fn:length(description) > maxDescriptionLength}">	
								<%-- Make sure the truncation occurs on white space and not in the middle of a word --%>
								<c:set var="numChars" value="0"/>
								<c:set var="description">	
									<c:set var="words" value="${fn:split( description, ' ')}"/>
									<c:forEach items="${words}" var="word"><c:if test="${numChars < maxDescriptionLength}">${word} <c:set 
										var="numChars" value="${numChars + fn:length( word ) + 1}"/></c:if></c:forEach> ...
								</c:set>
							</c:if>
							<%-- Hightlight the text entered by the user during the search --%>
							<dt:keywordsHighlight>
								${description}	
							</dt:keywordsHighlight>
							
							<%-- Construct a link to this page to pull up the full description --%>
							<c:url value="" var="fullDescUrl">
								<c:param name="fullDescription">${myId}</c:param>
								
								<%-- Echo all params --%> 
								<c:forEach items="${paramValues}" var="pname">
									<c:if test="${pname.key != 'rt'}">								
										<c:forEach items="${pname.value}" var="pval">
											<c:param name="${pname.key}" value="${pval}"/>
										</c:forEach>
									</c:if>
								</c:forEach>										
							</c:url>								
							<nobr><a href="${fullDescUrl}" class="dleseTmpl">Full description</a></nobr>
						
							<c:if test="${showVocabs == 'true'}">
								<%-- Display the Grade level --%>
								<div style="padding-top:9px">
									<i>Grade level:</i>
									<x:forEach select="metadata/itemRecord/educational/audiences/audience/gradeRange" varStatus="i">
										<c:set var="gradeRangeVocabEntry"><x:out select="."/></c:set>
										${gradeRangeLabels[gradeRangeVocabEntry]}${i.last ? '' : ','}		
									</x:forEach>
								</div>
								<div style="padding-top:9px">
									<i>Grade level OPML:</i>								
									<c:catch var="xslError">						
										<x:set select="head/renderingGuidelines/gradeRangeOPML" var="vocabOPML"/>
										<%@ include file="opml_vocabs_comma_separated.jsp" %>
									</c:catch>
									<c:if test="${not empty xslError}">
										Error: ${xslError} 
									</c:if>	
								</div>								
								
								<%-- Display the Type of resource --%>
								<div style="padding-top:2px">
									<i>Type of resource:</i>
									<x:forEach select="metadata/itemRecord/educational/resourceTypes/resourceType" varStatus="i">
										<c:set var="resourceTypeVocabEntry"> <x:out select="."/> </c:set>														
										<dt:keywordsHighlight>
											${resourceTypeLabels[resourceTypeVocabEntry]}${i.last ? '' : ','}
										</dt:keywordsHighlight>													
									</x:forEach>
								</div>
								<div style="padding-top:9px">
									<i>Type of resource OPML:</i>								
									<c:catch var="xslError">						
										<x:set select="head/renderingGuidelines/resourceTypeOPML" var="vocabOPML"/>
										<%@ include file="opml_vocabs_comma_separated.jsp" %>
									</c:catch>
									<c:if test="${not empty xslError}">
										Error: ${xslError} 
									</c:if>	
								</div>									
	
								<%-- Display the Subjects --%>
								<div style="padding-top:2px">
									<i>Subjects:</i>
									<x:forEach select="metadata/itemRecord/general/subjects/subject" varStatus="i">
										<c:set var="subjectsVocabEntry"><x:out select="."/></c:set>														
										<dt:keywordsHighlight>
											${subjectLabels[subjectsVocabEntry]}${i.last ? '' : ','}
										</dt:keywordsHighlight>													
									</x:forEach>
								</div>
								<div style="padding-top:9px">
									<i>Subjects OPML:</i>								
									<c:catch var="xslError">						
										<x:set select="head/renderingGuidelines/subjectOPML" var="vocabOPML"/>
										<%@ include file="opml_vocabs_comma_separated.jsp" %>
									</c:catch>
									<c:if test="${not empty xslError}">
										Error: ${xslError} 
									</c:if>	
								</div>									
							</c:if>

							<%-- Display teaching tips and reviews, if present --%>							
							<c:if test="${showReviews == 'true'}">
								<div style="padding-top:6px">
									<x:set var="record" select="."/>
									<%@ include file="links_to_annotations.jsp" %>
								</div>
							</c:if>								
							
						</div>
					</td>			
				</tr>		
				</x:forEach>		
			</c:when>
			
			<%-- If there were no search results, display a message --%>
			<c:otherwise>
				<tr class="dleseTmpl">
					<td class="dleseTmpl" colspan="2" style="padding-top:24px; padding-bottom:20px;">				
					<c:choose>
						<c:when test="${not empty param.d || not empty selectedSmartLink}">
							There are currently no resources available for <i>${param.d}${selectedSmartLink}</i>.
						</c:when>
						<c:otherwise>
							There were no matches for your search.
							<c:choose>
								<c:when test="${empty param.q && empty param.qh}">
									Try modifying your selections from the menus above.
								</c:when>
								<c:otherwise>
									Try using different terms in your search, or modify your selections in the menus above.
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
					</td>
				</tr>
			</c:otherwise>						
		</c:choose>	
		
		<%-- Put another pager on the bottom if there were some some search results --%>
		<c:if test="${numResults > 0}">
			<%-- Render a gray line --%>
			<tr>
				<td colspan="2">
					<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0">
						<tr><td bgcolor="#999999" height="1"></td></tr>
					</table>
				</td>
			</tr>							
			<tr class="dleseTmpl">
				<td class="dleseTmpl">
					&nbsp;
				</td>
				<td class="dleseTmpl" align="right">
					<p class="dleseTmpl">${pager}</p>
				</td>				
			</tr>
		</c:if>			
		</table>						
					
	</c:otherwise>
</c:choose>


