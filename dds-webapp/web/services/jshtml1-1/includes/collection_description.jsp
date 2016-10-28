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
					We are unable to find information for record ID '${param.collectionDescription}'.	
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
			<%-- Display the record XML is available --%>
			<x:when select="$getRecordXmlDom/collectionRecord/general/fullTitle">									

				<%-- Construct the redirect URL to the collection, if available --%>
				<x:if select="$getRecordXmlDom/collectionRecord/access/collectionLocation"> 
					<c:set var="resourceURL">
						<x:out select="$getRecordXmlDom/collectionRecord/access/collectionLocation"/>
					</c:set>
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
				</x:if>

				${thinLine}
 					
				<table class="stdTable">
					<tr class="resourceTitle">
						<td class="stdTable">
							<%-- Store the collection vocab entry for use in the XPath expression below --%>
							<c:set var="collectionVocabEntry">
								<x:out select="$getRecordXmlDom/collectionRecord/access/key"/>
							</c:set>
									
							<%-- Display the collection name and URL (if available) to user --%>
							<c:choose>
								<c:when test="${empty redirectURL}">
									<span class="collectionTitle">
										${collectionLabels[collectionVocabEntry]}
									</span>									
								</c:when>
								<c:otherwise>
									<a href="${redirectURL}" target="_blank" 
										class="blacklnk">${collectionLabels[collectionVocabEntry]}</a><br>
									<c:choose>
										<c:when test="${ fn:length(resourceURL) > maxUrlLength }">
											<a href="${redirectURL}" class="dleseTmpl" target="_blank">${ fn:substring(resourceURL,0,maxUrlLength)} ...</a>	
										</c:when>
										<c:otherwise>
											<a href="${redirectURL}" class="dleseTmpl" target="_blank">${resourceURL}</a>
										</c:otherwise>
									</c:choose>									
								</c:otherwise>
							</c:choose>
						</td>			
					</tr>						
					<tr class="resourceBody">
						<td class="stdTable">					
							<div class="description">
								<x:out select="$getRecordXmlDom/collectionRecord/general/description" escapeXml="false"/>
								<br><a href='<x:out select="$getRecordXmlDom/collectionRecord/general/policies/policy[@type='Collection scope']/@url"/>' 
									class="dleseTmpl" target="_blank">Collection Scope and Policy Statement</a>
								
								
								<div class="edTop">This collection is intended for:</div>
								<div class="edEn">
								<x:forEach select="$getRecordXmlDom/collectionRecord/general/gradeRanges/gradeRange" varStatus="i">
									<%-- Store the grade range vocab entry for use in the XPath expression below --%>
									<c:set var="gradeRangeVocabEntry">
										<x:out select="."/>
									</c:set>									
									
										<%-- Display the vocab label to the user --%>
										${gradeRangeLabels[gradeRangeVocabEntry]}<c:if test="${!i.last}">,</c:if>
								</x:forEach>
								</div>
							
								
								<div class="edTop">This collection contains resources related to these subjects:</div>
								<div class="edEn">								
								<x:forEach select="$getRecordXmlDom/collectionRecord/general/subjects/subject" varStatus="i">								
									<%-- Store the grade range vocab entry for use in the XPath expression below --%>
									<c:set var="subjectVocabEntry">
										<x:out select="."/>
									</c:set>									
									
										<%-- Display the vocab label to the user --%>
										${subjectLabels[subjectVocabEntry]}<c:if test="${!i.last}">,</c:if>
																
								</x:forEach>
								</div>	
								<x:if select="$getRecordXmlDom/collectionRecord/general/keywords/*">
									<div class="edTop">Terms and keywords associated with this collection:</div>
									<div class="edEn">
									<x:forEach select="$getRecordXmlDom/collectionRecord/general/keywords/keyword" varStatus="i">
											<x:out select="." escapeXml="false"/><c:if test="${!i.last}">,</c:if>
									</x:forEach>
									</div>
								</x:if>
															
	
								<div class="edTop">Cost / Copyright:</div>
								<div class="edEn">
									<x:set var="cost" select="$getRecordXmlDom/collectionRecord/general/cost" />
									<x:choose>
										<x:when select="starts-with($cost,'DLESE:No')">
											No cost
										</x:when>
										<x:otherwise>
											<x:out select="substring-after($cost,'DLESE:')" escapeXml="false"/>
										</x:otherwise>
									</x:choose>
								</div>		 										
									
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
							<br><br>Sorry, the record you requested, '${param.collectionDescription}' cannot be displayed.
						</td>
					</tr>
				</table>								
			</x:otherwise>	
		</x:choose>	
	</c:otherwise>
</c:choose>

