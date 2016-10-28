<%--
	From JSP client version 2.5.		
--%>

<c:choose>
	<c:when test="${jsformv11.numResults == 0}">
		${thinLine}
		<table class="stdTable">														
			<tr class="stdTable">
				<td class="stdTable">	
					<br><br>
					We are unable to find information for record ID '${param.fullDescription}'.	
					Please make sure the ID is correct.
				</td>
			</tr>
		</table>
	</c:when>		
	<c:when test="${not empty getRecordXmlDomError}">
		${thinLine}
		<table class="stdTable">														
			<tr class="stdTable">
				<td class="stdTable">			
					<br><br>
					Sorry, our system has encountered a problem and we were unable to display
					the information at this time. Please try again later.	
				</td>
			</tr>
		</table>					
	</c:when>
	<c:otherwise>
		<x:choose>		
			<%-- Display the record from XML if it's available--%>
			<x:when select="$getRecordXmlDom/itemRecord/general/title">		

				<%-- Construct the redirect URL to the resource --%>
				<c:set var="resourceURL" value="${jsformv11.docReader.url}"/>
				<c:set var="redirectURL">
					<c:choose>
						<c:when test="${not empty redirectBaseUrl}">
							${redirectBaseUrl}/T=view&Q=${f:URLEncoder(param.q)}${f:URLEncoder(param.qh)}&SC=${HTMLClientName}&ID=${jsformv11.docReader.id}*${resourceURL}
						</c:when>
						<c:otherwise>
							${resourceURL}
						</c:otherwise>
					</c:choose>
				</c:set>
				
				<%-- Set which keywords to highlight --%>
				<dt:setKeywordsHighlight keywords="${param.q}" cssClassName="${keywordHighlightClass}" />				
				
				${thinLine}
					
				<table class="stdTable">														
					<tr class="resourceTitle">
						<td class="stdTable">
							<a href="${redirectURL}" target="_blank" 
								class="blacklnk">${jsformv11.docReader.title}</a><br>
							<c:choose>
								<c:when test="${ fn:length(resourceURL) > maxUrlLength }">
									<a href="${redirectURL}" class="dleseTmpl" target="_blank">${ fn:substring(resourceURL,0,maxUrlLength)} ...</a>	
								</c:when>
								<c:otherwise>
									<a href="${redirectURL}" class="dleseTmpl" target="_blank">${resourceURL}</a>
								</c:otherwise>
							</c:choose>
							<%-- Construct a link to this page to pull up the collection description --%>
							<c:url value="" var="collectionDescUrl">
								<c:param name="collectionDescription">${jsformv11.docReader.myCollectionsRecordId}</c:param>
								<%-- Echo all params except those indicated --%> 
								<c:forEach items="${paramValues}" var="pname">
									<c:if test="${ignoreParams[pname.key] == null}">
										<c:forEach items="${pname.value}" var="pval">
											<c:param name="${pname.key}" value="${pval}"/>
										</c:forEach>
									</c:if>
								</c:forEach>									
							</c:url>																					
						</td>			
					</tr>		
					<tr class="resourceBody">
						<td class="stdTable">						
							<div class="description">
								<dt:keywordsHighlight>
									${jsformv11.docReader.description}
								</dt:keywordsHighlight>	
								
								<%-- Display teaching tips and reviews, if present --%>
								<c:if test="${show.annotations != null && not empty annoSumbissionUrl}">
									<c:set var="docReader" value="${jsformv11.docReader}"/>
									<%@ include file="links_to_annotations.jsp" %>
								</c:if>
								
								<div class="edTop">From:</div>
								<div class="edEn"><a href="${collectionDescUrl}">${jsformv11.docReader.collectionLabel}</a></div>
								
								<div class="edTop">Grade level:</div>
								<c:forEach items="${jsformv11.docReader.gradeRangeLabels}" var="label">
									<div class="edEn">${label}</div>
								</c:forEach>
								
								<div class="edTop">Type of resource:</div>
								<c:forEach items="${jsformv11.docReader.resourceTypeLabels}" var="label">
									<div class="edEn">${label}</div>
								</c:forEach>
								
								<div class="edTop">Subject:</div>
								<c:forEach items="${jsformv11.docReader.subjectLabels}" var="label">
									<div class="edEn"><dt:keywordsHighlight>${label}</dt:keywordsHighlight></div>
								</c:forEach>
								
								<c:if test="${not empty jsformv11.docReader.keywords}">
									<div class="edTop">Keywords:</div>
									<c:forEach items="${jsformv11.docReader.keywords}" var="label">
										<div class="edEn"><dt:keywordsHighlight>${label}</dt:keywordsHighlight></div>
									</c:forEach>	
								</c:if>
								
								<div class="edTop">Technical requirements:</div>
								<x:forEach select="$getRecordXmlDom/itemRecord/technical/online/requirements/requirement/reqType">
									<x:choose>
										<x:when select=". = 'DLESE:General:No specific technical requirements'">
											<div class="edEn">No specific technical requirements, just a browser required</div>
										</x:when>									
										<x:when select=". = 'DLESE:Other:More specific technical requirements'">
											<x:forEach select="../../../otherRequirements/otherRequirement/otherType">
												<div class="edEn"><x:out select="."/></div>
											</x:forEach>
										</x:when>
										<x:otherwise>
											<div class="edEn"><x:out select="substring-after(.,'DLESE:')" escapeXml="false" /></div>
										</x:otherwise>
									</x:choose>							
								</x:forEach>							
	
								<div class="edTop">Cost / Copyright:</div>
								<div class="edEn">
									<x:set var="cost" select="$getRecordXmlDom/itemRecord/rights/cost" />
									<x:choose>
										<x:when select="starts-with($cost,'DLESE:No')">
											No cost
										</x:when>
										<x:otherwise>
											<x:out select="substring-after($cost,'DLESE:')" escapeXml="false"/>
										</x:otherwise>
									</x:choose>
									<br>
									<x:out select="$getRecordXmlDom/itemRecord/rights/description" escapeXml="false"/>
								</div>		 					
								
								<div class="edTop">Date this resource was added to the library:</div>
								<div class="edEn">${jsformv11.docReader.multiAccessionDate}</div>	
								
								<div class="edTop">DLESE catalog ID:</div>
								<div class="edEn">${jsformv11.docReader.id}</div>								
									
							</div>						
						</td>
					</tr>				
				</table>					
			</x:when>
			
			<%-- If we couldn't find the title and there wasn't an error, this must not be an ADN record --%>
			<x:otherwise>
				${thinLine}
				<table class="stdTable">														
					<tr class="stdTable">
						<td class="stdTable">			
							<br><br>Sorry, the record you requested, '${param.fullDescription}' cannot be displayed.
						</td>
					</tr>
				</table>
			</x:otherwise>	
		</x:choose>	
	</c:otherwise>
</c:choose>

