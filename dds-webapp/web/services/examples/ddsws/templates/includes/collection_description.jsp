<%--
	This JSP page displays the description of a collection. It is meant to be included inside a
	JSP page that has just performed a ddsws GetRecord request and stored the XML result in
	variable 'getRecordXmlDom'
	Requires web_service_connection.jsp to be included prior to using this page.
	
	Example use:

	<!-- Include the required web service connection page prior to use -->
	<%@ include file="web_service_connection.jsp" %>
	
	...
	
	<!-- Collection description has just been requested -->
	<c:when test="${not empty param.collectionDescription}">
		<%@ include file="collection_description.jsp" %>
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
			<x:when select="$getRecordXmlDom/DDSWebService/GetRecord/record/metadata/collectionRecord/general/fullTitle">					
				
				<%-- Store the full metadata variable 'record' for use below --%>
				<x:set select="$getRecordXmlDom/DDSWebService/GetRecord/record" var="record"/>					

				<%-- Construct the redirect URL to the collection, if available --%>
				<x:if select="$record/metadata/collectionRecord/access/collectionLocation"> 
					<c:set var="resourceURL">
						<x:out select="$record/metadata/collectionRecord/access/collectionLocation"/>
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
				</x:if>
							
				<%-- Display a heading for the collection display --%>
				<table class="banner" cellpadding="0" cellspacing="1" border="0">
					<tr class="banner" valign="center">
						<td class="banner"><div class="bannerTxt">Collection description</div></td>
					</tr>
				</table> 
 					
				<table class="dleseTmpl"  width="100%" cellpadding="4" cellspacing="0">				
					<tr class="dleseTmpl">
						<td class="dleseTmpl">
							<%-- Store the collection vocab entry for use in the XPath expression below --%>
							<c:set var="collectionVocabEntry">
								<x:out select="$record/metadata/collectionRecord/access/key"/>
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
					<tr class="dleseTmpl">
						<td class="dleseTmpl">					
							<div class="description">
								<x:out select="$record/metadata/collectionRecord/general/description" escapeXml="false"/>
								<br><a href='<x:out select="$record/metadata/collectionRecord/general/policies/policy[@type='Collection scope']/@url"/>' 
									class="dleseTmpl" target="_blank">Collection Scope and Policy Statement</a>
								
								
								<div class="edTop">This collection is intended for:</div>
								<div class="edEn">
								<x:forEach select="$record/metadata/collectionRecord/general/gradeRanges/gradeRange" varStatus="i">
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
								<x:forEach select="$record/metadata/collectionRecord/general/subjects/subject" varStatus="i">								
									<%-- Store the grade range vocab entry for use in the XPath expression below --%>
									<c:set var="subjectVocabEntry">
										<x:out select="."/>
									</c:set>									
									
										<%-- Display the vocab label to the user --%>
										${subjectLabels[subjectVocabEntry]}<c:if test="${!i.last}">,</c:if>
																
								</x:forEach>
								</div>	
								<x:if select="$record/metadata/collectionRecord/general/keywords/*">
									<div class="edTop">Terms and keywords associated with this collection:</div>
									<div class="edEn">
									<x:forEach select="$record/metadata/collectionRecord/general/keywords/keyword" varStatus="i">
											<x:out select="." escapeXml="false"/><c:if test="${!i.last}">,</c:if>
									</x:forEach>
									</div>
								</x:if>
															
	
								<div class="edTop">Cost / Copyright:</div>
								<div class="edEn">
									<x:set var="cost" select="$record/metadata/collectionRecord/general/cost" />
									<x:choose>
										<x:when select="starts-with($record/metadata/collectionRecord/general/cost,'DLESE:No')">
											No cost
										</x:when>
										<x:otherwise>
											<x:out select="substring-after($record/metadata/collectionRecord/general/cost,'DLESE:')" escapeXml="false"/>
										</x:otherwise>
									</x:choose>
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
				<br><br>Sorry, the record you requested, '${param.collectionDescription}' cannot be displayed.
			</x:otherwise>	
		</x:choose>	
	</c:otherwise>
</c:choose>

