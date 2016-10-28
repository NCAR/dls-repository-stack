<%--
	This JSP page displays the links to annotations (teaching tips, reviews). 
	It is meant to be included inside a JSP page that has just performed a ddsws 
	Search, UserSearch or GerRecord request and stored the ADN XML 
	record node in a variable named 'record'. 	
	
	Example use:
	
	<!-- Search has just been performed and the ADN XML record node has 
			been stored in variable 'record' -->
	<%@ include file="links_to_annotations.jsp" %>
	
	This is JSP client version @DDSWS_CLIENT_VERSION@.		
--%>


							
<%-- Display teaching tips and reviews, if present --%>
<c:set var="numTextAnnos">
	<x:out select="count( $record/head/additionalMetadata/annotatedBy/record/metadata/annotationRecord/item/statusOf[@status='Text annotation completed'] )"/>
</c:set>
<c:choose>													
	<%-- <c:when test="${numTextAnnos == 1}">
		<div style="padding-top:6px">
		<img src="${urlToMyDir}/images/paper.gif" style="margin-right:3px;border:0px"/>
		<x:set select="$record/head/additionalMetadata/annotatedBy/record/metadata/annotationRecord[item/statusOf[@status='Text annotation completed']]" var="annotationRecord"/> 
		See <a href='<x:out select="$annotationRecord/item/content/url"/>' class="dleseTmpl" target='_blank'>reviews, comments, teaching tips, etc.</a>	for this resource.
		</div>
	</c:when> --%>
	<c:when test="${numTextAnnos > 0}">
		<div style="padding-top:6px">
		<img src="${urlToMyDir}/images/paper.gif" style="margin-right:3px;border:0px"/>
		See 
		<a 	href="javascript:toggleVisibility('anno_<x:out select="$record/head/id"/>');" 
			title="Click to show/hide" 
			class="annos">reviews, comments, teaching tips, etc.</a>	for this resource.
		<div style="display:none; padding-top:2px;" id='anno_<x:out select="$record/head/id"/>'>
			<ul class="annos">
			<x:forEach select="$record/head/additionalMetadata/annotatedBy/record[metadata/annotationRecord/item/statusOf[@status='Text annotation completed']]" varStatus="status" var="annoRecord">
				<li class="dleseTmpl"><a href='<x:out select="$annoRecord/metadata/annotationRecord/item/content/url"/>' class="dleseTmpl" target='_blank'><x:out select="$annoRecord/metadata/annotationRecord/item/type"/></a> <i>from <x:out select="$annoRecord/head/collection"/></i></li>
			</x:forEach>
			</ul>
		</div>
		</div>
	</c:when>	
</c:choose>
	
	
<%-- Provide a link to submit teaching tips, comments and reviews --%>
<div style="padding-top:4px">  
	<x:choose>
		<%-- If this resources is currently under review, add a link to the review system and the comment submission form --%>
		<x:when select="$record/head/additionalMetadata/annotatedBy/record/metadata/annotationRecord/item/statusOf[@status='Text annotation in progress'][../type='Review']">
			<a href="http://www.dlese.org/suggest/comments.do?id=<x:out select='$record/head/id'/>" class="dleseTmpl" target="_blank"><img src="${urlToMyDir}/images/pencil-gray.gif" style="margin-right:3px;border:0px"/>Submit a comment or teaching tip</a>,
			or
			<a href='<x:out select="$record/head/additionalMetadata/annotatedBy/record/metadata/annotationRecord/item/statusOf[@status='Text annotation in progress'][../type='Review']/../content/url"/>' class="dleseTmpl" target="_blank">submit a review</a>
		</x:when>
		<%-- If this resources is not under review, just link to the comment submission form --%>
		<x:otherwise>
			<a href="http://www.dlese.org/suggest/comments.do?id=<x:out select='$record/head/id'/>" class="dleseTmpl" target="_blank"><img src="${urlToMyDir}/images/pencil-gray.gif" style="margin-right:3px;border:0px"/>Submit a comment or teaching tip</a>
		</x:otherwise>
	</x:choose>
	for this resource.
</div>	

