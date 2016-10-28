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
	
	This is JSP client version 2.2.		
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
				<%-- Store the gradeRanges XML element for use in the XPath expressions below --%>
				<x:set var="gradeRangesElement" select="$gradeRangesXmlDom/DDSWebService/ListGradeRanges/gradeRanges"/>
				<%-- Store the subjects XML element for use in the XPath expressions below --%>
				<x:set var="subjectsElement" select="$subjectsXmlDom/DDSWebService/ListSubjects/subjects"/>						
				<%-- Store the resource types XML element for use in the XPath expressions below --%>
				<x:set var="resourceTypesElement" select="$resourceTypesXmlDom/DDSWebService/ListResourceTypes/resourceTypes"/>
				<%-- Store the collections XML element for use in the XPath expressions below --%>
				<x:set var="collectionsElement" select="$collectionsXmlDom/DDSWebService/ListCollections/collections"/>	
					
				<table  width="100%" cellpadding="4" cellspacing="0">				
									
					<%-- Display a heading for the collection display --%>
					<tr>
						<td>
							<table width="100%" bgcolor="#777777" cellpadding="0" cellspacing="1" border="0" class="resultSectionHeading">
								<tr bgcolor='#aaaadd' valign="center">
									<td><div class="resultSectionHeading">Collection description</div></td>
								</tr>
							</table>  
						</td>
					</tr>
					<tr>
						<td>
							<%-- Store the collection vocab entry for use in the XPath expression below --%>
							<c:set var="collectionVocabEntry">
								<x:out select="$record/metadata/collectionRecord/access/key"/>
							</c:set>									

							<%-- Display the vocab label to the user --%>
							<span class="collectionTitle">
								<x:out select="$collectionsElement/collection[vocabEntry=$collectionVocabEntry]/renderingGuidelines/label"/>
							</span>		
						</td>			
					</tr>						
					<tr>
						<td>					
							<div class="description">
								<x:out select="$record/metadata/collectionRecord/general/description"/>
								<br><a href='<x:out select="$record/metadata/collectionRecord/general/policies/policy[@type='Collection scope']/@url"/>' 
									target="_blank">Collection Scope and Policy Statement</a>
								
								
								<div class="edTop">This collection is intended for:</div>
								<div class="edEn">
								<x:forEach select="$record/metadata/collectionRecord/general/gradeRanges/gradeRange" varStatus="i">
									<%-- Store the grade range vocab entry for use in the XPath expression below --%>
									<c:set var="gradeRangeVocabEntry">
										<x:out select="."/>
									</c:set>									
									
										<%-- Display the vocab label to the user --%>
										<x:out select="$gradeRangesElement/gradeRange[vocabEntry=$gradeRangeVocabEntry]/renderingGuidelines/label"/><c:if test="${!i.last}">,</c:if>
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
										<x:out select="$subjectsElement/subject[vocabEntry=$subjectVocabEntry]/renderingGuidelines/label"/><c:if test="${!i.last}">,</c:if>
																
								</x:forEach>
								</div>	
								<x:if select="$record/metadata/collectionRecord/general/keywords/*">
									<div class="edTop">Terms and keywords associated with this collection:</div>
									<div class="edEn">
									<x:forEach select="$record/metadata/collectionRecord/general/keywords/keyword" varStatus="i">
											<x:out select="." /><c:if test="${!i.last}">,</c:if>
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

