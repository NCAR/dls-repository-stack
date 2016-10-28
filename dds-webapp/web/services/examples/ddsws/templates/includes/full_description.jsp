<%--
	This JSP page displays the full description of a resource. It is meant to be included inside a
	JSP page that has just performed a ddsws GetRecord request and stored the XML result in
	variable 'getRecordXmlDom'
	Requires web_service_connection.jsp to be included prior to using this page.
	
	Example use:

	<!-- Include the required web service connection page prior to use -->
	<%@ include file="web_service_connection.jsp" %>
	
	...
	
	<!-- Full description has just been requested -->
	<c:when test="${not empty param.fullDescription}">
		<%@ include file="full_description.jsp" %>
	</c:when>

	This is JSP client version @DDSWS_CLIENT_VERSION@.		
--%>

<c:choose>
	<%-- If a we were not able to get the record (web service was down), send a message to the user --%>
	<c:when test="${not empty getRecordXmlDomError}">
		<br><br>
		Sorry, our system has encountered a problem and we were unable to display
		the information at this time. Please try again later.		
	</c:when>
	<c:otherwise>
	
		<x:choose>		
			<%-- Display the record metadata if it has been returned successfully by the web service --%>
			<x:when select="$getRecordXmlDom/DDSWebService/GetRecord/record/metadata/itemRecord/general/title">		
				<%-- Store the full metadata variable 'record' for use below --%>
				<x:set select="$getRecordXmlDom/DDSWebService/GetRecord/record" var="record"/>				

				<%-- Construct the redirect URL to the resource --%>
				<c:set var="resourceURL">
					<x:out select="$record/metadata/itemRecord/technical/online/primaryURL"/>
				</c:set>
				<c:set var="redirectURL">
					<c:choose>
						<c:when test="${not empty redirectBaseUrl}">
							${redirectBaseUrl}/T=view&Q=${f:URLEncoder(param.q)}${f:URLEncoder(param.qh)}&SC=${clientName}&ID=<x:out select="$record/head/id"/>*${resourceURL}
						</c:when>
						<c:otherwise>
							${resourceURL}
						</c:otherwise>
					</c:choose>
				</c:set>
				
				<%-- Set which keywords to highlight --%>
				<dt:setKeywordsHighlight keywords="${param.q}" highlightColor="${keywordHighlightColor}" />				
				
				<%-- Display a heading for the description --%>
				<table class="banner" cellpadding="0" cellspacing="1" border="0">
					<tr class="banner" valign="center">
						<td class="banner"><div class="bannerTxt">Resource description</div></td>
					</tr>
				</table>  
					
				<table class="dleseTmpl" width="100%" cellpadding="4" cellspacing="0">														
					<tr class="dleseTmpl">
						<td class="dleseTmpl">
							<a href="${redirectURL}" target="_blank" 
								class="blacklnk"><x:out select="$record/metadata/itemRecord/general/title" escapeXml="false"/></a><br>
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
								<c:param name="collectionDescription"><x:out select="$record/head/collection/@recordId"/></c:param>
								<%-- Echo params --%> 
								<c:forEach items="${paramValues}" var="pname">
									<c:if test="${pname.key != 'fullDescription' && pname.key != 'rt'}">
										<c:forEach items="${pname.value}" var="pval">
											<c:param name="${pname.key}" value="${pval}"/>
										</c:forEach>
									</c:if>
								</c:forEach>									
							</c:url>														
							<div style="padding-top:2px">From 
							<a href="${collectionDescUrl}" class="dleseTmpl"><x:out select="$record/head/collection"/></a></div>								
						</td>			
					</tr>		
					<tr class="dleseTmpl">
						<td class="dleseTmpl">					
							<div class="description">
								<dt:keywordsHighlight>
									<x:out select="$record/metadata/itemRecord/general/description" escapeXml="false"/>
								</dt:keywordsHighlight>
								
								<%-- Display teaching tips and reviews, if present --%>
								<c:if test="${showReviews == 'true'}">
									<%@ include file="links_to_annotations.jsp" %>
								</c:if>
								
								<div class="edTop">Grade level:</div>
								<x:forEach select="$record/metadata/itemRecord/educational/audiences/audience/gradeRange">
									<%-- Store the grade range vocab entry for use in the XPath expression below --%>
									<c:set var="gradeRangeVocabEntry">
										<x:out select="."/>
									</c:set>									
									<div class="edEn">
										<%-- Display the vocab label to the user --%>
										${gradeRangeLabels[gradeRangeVocabEntry]}
									</div>
								</x:forEach>
								
								<div class="edTop">Type of resource:</div>
								<x:forEach select="$record/metadata/itemRecord/educational/resourceTypes/resourceType">
									<%-- Store the resource type vocab entry for use in the XPath expression below --%>
									<c:set var="resourceTypeVocabEntry">
										<x:out select="."/>
									</c:set>							
									<div class="edEn">
										<%-- Display the vocab label to the user --%>
										<dt:keywordsHighlight>
											${resourceTypeLabels[resourceTypeVocabEntry]}
										</dt:keywordsHighlight>
									</div>										
								</x:forEach>
								
								<div class="edTop">Subject:</div>
																
								<x:forEach select="$record/metadata/itemRecord/general/subjects/subject">								
									<%-- Store the grade range vocab entry for use in the XPath expression below --%>
									<c:set var="subjectVocabEntry">
										<x:out select="."/>
									</c:set>									
									<div class="edEn">
										<%-- Display the vocab label to the user --%>
										<dt:keywordsHighlight>
											${subjectLabels[subjectVocabEntry]}
										</dt:keywordsHighlight>
									</div>								
								</x:forEach>
								
								<x:if select="$record/metadata/itemRecord/general/keywords/*">
									<div class="edTop">Keywords:</div>
									<x:forEach select="$record/metadata/itemRecord/general/keywords/keyword">
										<div class="edEn">
											<dt:keywordsHighlight>
												<x:out select="."  escapeXml="false"/>
											</dt:keywordsHighlight>
										</div>
									</x:forEach>	
								</x:if>
								
								<div class="edTop">Technical requirements:</div>
								<x:forEach select="$record/metadata/itemRecord/technical/online/requirements/requirement/reqType">
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
									<x:set var="cost" select="$record/metadata/itemRecord/rights/cost" />
									<x:choose>
										<x:when select="starts-with($record/metadata/itemRecord/rights/cost,'DLESE:No')">
											No cost
										</x:when>
										<x:otherwise>
											<x:out select="substring-after($record/metadata/itemRecord/rights/cost,'DLESE:')" escapeXml="false"/>
										</x:otherwise>
									</x:choose>
									<br>
									<x:out select="$record/metadata/itemRecord/rights/description" escapeXml="false"/>
								</div>		 					
								
								<div class="edTop">DLESE catalog ID:</div>
								<div class="edEn">
									<x:out select="$record/metadata/itemRecord/metaMetadata/catalogEntries/catalog/@entry" />
								</div>					
									
							</div>						
						</td>
					</tr>				
				</table>					
			</x:when>
		
			<%-- Display the web service error message if one was provided --%>
			<x:when select="$getRecordXmlDom/DDSWebService/error">
				<br><br>
				Sorry, we were unable to display the full record you requested. 
				The reason: <i><x:out select="$getRecordXmlDom/DDSWebService/error"/></i>	
			</x:when>
			
			<%-- If we couldn't find the title and there wasn't an error, this must not be an ADN record --%>
			<x:otherwise>
				<br><br>Sorry, the record you requested, '${param.fullDescription}' cannot be displayed.
			</x:otherwise>	
		</x:choose>	
	</c:otherwise>
</c:choose>

