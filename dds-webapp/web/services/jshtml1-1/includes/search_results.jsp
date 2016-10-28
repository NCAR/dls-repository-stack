
<%-- Set up some variables that we will use below to display to the user --%>
<c:if test="${not empty param.q}">										
	<%-- Store the text the user entered for display below --%>	
	<c:set var="usersText">
		<b>'<i>${fn:trim( param.q )}</i>'</b>
	</c:set>
</c:if> 						
			
<%-- Save the number of results in variable 'numResults' for use later --%>
<c:set var="numResults" value="${jsformv11.numResults}"/>	

<%-- Set which keywords to highlight --%>
<dt:setKeywordsHighlight keywords="${param.q}" cssClassName="${keywordHighlightClass}" />

<%-- Render a thin line --%>		
${thinLine}
											
<table class="stdTable" width="100%">
	<c:choose>
		<c:when test="${numResults > 0 && hide.pager == null}">
			<tr class="stdTable">
				<td class="stdTable" style="${empty userSelection ? '' : 'padding-bottom:2;'}">
					<%-- Display the search options the user entered --%>
					<nobr>
					<c:if test="${ (param.d == null || param.slc == 'f') || not empty param.q }">
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
					<c:if test="${not empty param.d && param.slc != 'f'}">
						${empty usersText ? 'for' : 'in'} <b>${param.d}</b>
					</c:if>							
				</td>
				<td class="stdTable" align="right">
					<%-- Include pager.jsp, which creates and stores the HTML for the pager in variable 'pager' --%>
					<%@ include file="pager.jsp" %>									
					<%-- Then output the pager HTML here (and again further below) --%>
					${pager}						
				</td>				
			</tr>
		</c:when>
		<%-- When zero results --%>
		<c:otherwise>
			<c:if test="${not empty usersText}">
				<tr class="stdTable">
					<td class="stdTable" colspan="2">
						You searched for ${usersText}
						<c:if test="${not empty param.d && param.slc != 'f'}">
							in <b>${param.d}</b>
						</c:if>
					</td>
				</tr>
			</c:if>
		</c:otherwise>
	</c:choose>	
	<c:if test="${not empty userSelections}">
		<tr class="stdTable">
			<td class="stdTable" colspan="2" style="padding-top:2">
				<%-- Display the selections the user choose when performing the search --%>
				${numResults == 0 && empty param.q ? 'You searched for' : ''} <b>${userSelections}</b>
				<c:if test="${not empty param.d && param.slc != 'f' && numResults == 0 && empty param.q}">
					in <i>${param.d}</i>
				</c:if>
			</td>
		</tr>
	</c:if>
</table>
				
<%-- Render a thin line --%>		
<c:if test="${hide.pager == null}">
	${thinLine}
</c:if>

<%-- Display the search results... --%>
<table class="stdTable">	
	<c:choose> 
		<%-- If the total number of search results is greater than zero, display them --%>
		<c:when test="${numResults > 0}">

		
		<%-- Loop through each of the record nodes --%>
		<c:forEach items="${jsformv11.results}" begin="${jsformv11.s}" end="${jsformv11.s + jsformv11.n - 1}" var="result" varStatus="status">
		<c:set var="docReader" value="${result.docReader}"/>
		<c:set var="myId">${docReader.id}</c:set>
		<tr class="resourceTitle">
			<td class="stdTable" colspan="2">
				<c:set var="resourceURL">
					${docReader.url}
				</c:set>
				<c:set var="redirectURL">
					<c:choose>
						<c:when test="${not empty redirectBaseUrl}">
							${redirectBaseUrl}/T=search&Q=${f:URLEncoder(param.q)}${f:URLEncoder(param.qh)}&R=${startingOffset + status.index}&SC=${HTMLClientName}&ID=${myId}*${resourceURL}
						</c:when>
						<c:otherwise>
							${resourceURL}
						</c:otherwise>
					</c:choose>
				</c:set>									
				<a href="${redirectURL}" target="_blank" 
					class="blacklnk">${docReader.title}</a><br>
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
		<tr class="resourceBody">
			<td class="stdTable" colspan="2">
				<div class="description">
					<c:set var="description">
						${docReader.description}
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
						
						<%-- Echo all params except those indicated --%> 
						<c:forEach items="${paramValues}" var="pname">
							<c:if test="${ignoreParams[pname.key] == null}">
								<c:forEach items="${pname.value}" var="pval">
									<c:param name="${pname.key}" value="${pval}"/>
								</c:forEach>
							</c:if>
						</c:forEach>										
					</c:url>								
					<nobr><a href="${fullDescUrl}" class="dleseTmpl">Full description</a></nobr>

					<%-- Display the collection --%>
					<c:if test="${show.collectionSR != null}">
						<%-- Construct a link to this page to pull up the collection description --%>
						<c:url value="" var="collectionDescUrl">
							<c:param name="collectionDescription">${docReader.myCollectionsRecordId}</c:param>
							<%-- Echo all params except those indicated --%> 
							<c:forEach items="${paramValues}" var="pname">
								<c:if test="${ignoreParams[pname.key] == null}">
									<c:forEach items="${pname.value}" var="pval">
										<c:param name="${pname.key}" value="${pval}"/>
									</c:forEach>
								</c:if>
							</c:forEach>									
						</c:url>														
						<div style="padding-top:9px">
							<i>From:</i>
							<a href="${collectionDescUrl}" class="dleseTmpl">${docReader.collectionLabel}</a>							
						</div>
					</c:if>							
					
					
					<%-- Display the Grade level --%>
					<c:if test="${show.gradeLevelSR != null}">
						<div style="padding-top:9px">
							<i>Grade level:</i>
							<c:forEach items="${docReader.multiGradeRangeLabels}" var="myVocabLabel" varStatus="i">
								${myVocabLabel}${i.last ? '' : ','}
							</c:forEach>
						</div>
					</c:if>
					
					<%-- Display the Type of resource --%>
					<c:if test="${show.resourceTypeSR != null}">
						<div style="padding-top:2px">
							<i>Type of resource:</i>
							<c:forEach items="${docReader.multiResourceTypeLabels}" var="myVocabLabel" varStatus="i">
								${myVocabLabel}${i.last ? '' : ','}
							</c:forEach>
						</div>
					</c:if>							

					<%-- Display the Subjects --%>
					<c:if test="${show.subjectSR != null}">
						<div style="padding-top:2px">
							<i>Subject:</i>
							<c:forEach items="${docReader.multiSubjectLabels}" var="myVocabLabel" varStatus="i">
								${myVocabLabel}${i.last ? '' : ','}
							</c:forEach>
						</div>
					</c:if>

					<%-- Display the accession date --%>
					<c:if test="${show.accessionDateSR != null}">
						<div style="padding-top:2px">
							<i>Date added to library:</i> ${docReader.multiAccessionDate}
						</div>
					</c:if>							

					<%-- Display teaching tips and reviews, if present --%>							
					<c:if test="${show.annotations != null && not empty annoSumbissionUrl}">
						<div style="padding-top:6px">
							<%@ include file="links_to_annotations.jsp" %>
						</div>
					</c:if>								
					
				</div>
			</td>			
		</tr>		
		</c:forEach>		
	</c:when>
	
	<%-- If there were no search results, display a message --%>
	<c:otherwise>
		<tr class="stdTable">
			<td class="stdTable" colspan="2" style="padding-top:24px; padding-bottom:20px;">				
			<c:choose>
				<c:when test="${ (not empty param.d && empty param.q && empty userSelections) }">
					There are currently no resources available for <i>${param.d}</i>.
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${empty param.q && empty param.qh}">
							There were no matches for your search. 
							<c:choose>
								<c:when test="${show.searchBox != null && not empty hasMenu}">
									Try using different terms in your search or 
									modifying your selections in the menu(s) above.
								</c:when>
								<c:when test="${show.searchBox != null}">
									Try using different terms in your search.
								</c:when>
								<c:when test="${not empty hasMenu}">
									Try modifying your selections in the menu(s) above.
								</c:when>						
							</c:choose>									
						</c:when>
						<c:when test="${param.slc == 't'}">
							There were no matches for your search in <i>${param.d}</i>.
							Try searching all records, using different terms in your search, 
							or modifying your selections in the menu(s) above.
						</c:when>								
						<c:otherwise>
							There were no matches for your search.
							<c:choose>
								<c:when test="${show.searchBox != null && not empty hasMenu}">
									Try using different terms in your search or 
									modifying your selections in the menu(s) above.
								</c:when>
								<c:when test="${show.searchBox != null}">
									Try using different terms in your search.
								</c:when>
								<c:when test="${not empty hasMenu}">
									Try modifying your selections in the menu(s) above.
								</c:when>						
							</c:choose>	
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
			</td>
		</tr>
	</c:otherwise>						
</c:choose>	
</table>

<%-- Put another pager on the bottom if there were some some search results --%>
<c:if test="${not empty hasPages && hide.pager == null}">
	<%-- Render a thin line --%>							
	${thinLine}
	
	<table class="stdTable" width="100%">
		<tr class="stdTable">
			<td class="stdTable">
				&nbsp;
			</td>
			<td class="stdTable" align="right">
				${pager}
			</td>				
		</tr>
	</table>
</c:if>			



