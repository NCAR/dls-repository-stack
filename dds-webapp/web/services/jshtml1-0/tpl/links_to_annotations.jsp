<%--
	This JSP page displays the links to annotations (teaching tips, reviews). 
	It is meant to be included inside a JSP page that has just performed a ddsws 
	Search, UserSearch or GerRecord request and stored the ADN XML 
	record node in a variable named 'record'. 	
	
	Example use:
	
	<!-- Search has just been performed and the ADN XML record node has 
			been stored in variable 'record' -->
	<%@ include file="links_to_annotations.jsp" %>
	
	This is JSP client version 2.2.		
--%>

								
<%-- Display teaching tips and reviews, if present --%>
<c:set var="numTips">
	<x:out select="count( $record/head/additionalMetadata/annotatedBy[@status='Text annotation completed'][@type='Teaching tip'] )"/>
</c:set>								
<c:set var="numTextAnnos">
	<x:out select="count( $record/head/additionalMetadata/annotatedBy[@status='Text annotation completed'] )"/>
</c:set>														
<c:if test="${numTextAnnos > 0}">
	<div style="padding-top:6px">
		<c:choose>
			<c:when test="${numTips > 0 && numTips == numTextAnnos}">
				<a href='<x:out select="$record/head/additionalMetadata/annotatedBy[@status='Text annotation completed'][@type='Teaching tip']/url"/>' target="_blank"><img src="${thisDirUrl}/images/paper.gif" style="margin-right:3px;border:0px"/>Read teaching tips submitted by others</a>
			</c:when>
			<c:when test="${numTips > 0 && numTips != numTextAnnos}">
				<a href='<x:out select="$record/head/additionalMetadata/annotatedBy[@status='Text annotation completed'][@type='Teaching tip']/url"/>' target="_blank"><img src="${thisDirUrl}/images/paper.gif" style="margin-right:3px;border:0px"/>Read teaching tips submitted by others</a>
				or
				<a href="http://www.dlese.org/library/view_annotation.do?type=tt&id=<x:out select='$record/head/id'/>" target="_blank">view a summary of reviews</a>
			</c:when>									
			<c:otherwise>
				<a href="http://www.dlese.org/library/view_annotation.do?type=tt&id=<x:out select='$record/head/id'/>" target="_blank">View a summary of reviews</a>
			</c:otherwise>
		</c:choose>
		for this resource.
	</div>
</c:if>
	
<%-- Provide a link to submit teaching tips, comments and reviews --%>
<div style="padding-top:4px">  
	<x:choose>
		<%-- If this resources is currently under review, add a link to the review system and the comment submission form --%>
		<x:when select="$record/head/additionalMetadata/annotatedBy[@status='Text annotation in progress'][@type='Review']">
			<a href="http://www.dlese.org/suggest/comments.do?id=<x:out select='$record/head/id'/>" target="_blank"><img src="${thisDirUrl}/images/pencil-gray.gif" style="margin-right:3px;border:0px"/>Submit a comment or teaching tip</a>,
			or
			<a href='<x:out select="$record/head/additionalMetadata/annotatedBy[@status='Text annotation in progress'][@type='Review']/url"/>' target="_blank">submit a review</a>
		</x:when>
		<%-- If this resources is not under review, just link to the comment submission form --%>
		<x:otherwise>
			<a href="http://www.dlese.org/suggest/comments.do?id=<x:out select='$record/head/id'/>" target="_blank"><img src="${thisDirUrl}/images/pencil-gray.gif" style="margin-right:3px;border:0px"/>Submit a comment or teaching tip</a>
		</x:otherwise>
	</x:choose>
	for this resource.
</div>
