<%--
	From JSP client version 2.5.		
--%>

<%-- Display teaching tips and reviews, if present --%>
<c:set var="myTextAnnos" value="${docReader.completedAnnos}"/>
<c:set var="numTextAnnos">${fn:length(docReader.completedAnnos)}</c:set>
<c:choose>													
	<c:when test="${numTextAnnos == 1}">
		<div style="padding-top:6px">
		<img src="${urlToMyDir}/images/paper.gif" style="margin-right:3px;border:0px"/>
		
		See <a href='${myTextAnnos[0].url}' class="dleseTmpl" target='_blank'>reviews, comments, teaching tips, etc.</a> 
		for this resource.
		</div>
	</c:when>
	<c:when test="${numTextAnnos > 1}">
		<div style="padding-top:6px">
			<img src="${urlToMyDir}/images/paper.gif" style="margin-right:0px;border:0px"/>		
			<a 	href="javascript:toggleVisibility('anno_${docReader.id}');" 
				title="open/close" class="annos"><img src='${urlToMyDir}/images/btnExpandClsd.gif' 
							alt="open/close" border="0" hspace="2" width="11" hight="11" name="anno_${docReader.id}_arrow"></a>						
			See 
			<a 	href="javascript:toggleVisibility('anno_${docReader.id}');" 
				title="open/close" 
				class="annos">reviews, comments, teaching tips, etc.</a> for this resource
			(<a href="javascript:toggleVisibility('anno_${docReader.id}');" class="annos" id="anno_${docReader.id}_oc">show</a>)	
			<div style="display:none; padding-top:2px;" id='anno_${docReader.id}'>
				<ul class="annos">
				<c:forEach items="${myTextAnnos}" varStatus="status" var="annotation">
					<li class="dleseTmpl"><a href='${annotation.url}' class="dleseTmpl" target='_blank'>${annotation.type}</a> <i>from ${annotation.collectionLabel}</i></li>
				</c:forEach>
				</ul>
			</div>
		</div>
	</c:when>	
</c:choose>
	
	
<%-- Provide a link to submit teaching tips, comments and reviews --%>
<div style="padding-top:4px">  
	<c:choose>
		<%-- If this resources is currently under review, add a link to the review system and the comment submission form --%>
		<c:when test="${docReader.hasInProgressAnno}">
			<a href="${annoSumbissionUrl}?id=${docReader.textAnnosInProgress[0].itemId}" class="dleseTmpl" target="_blank"><img src="${urlToMyDir}/images/pencil-gray.gif" style="margin-right:3px;border:0px"/>Submit a comment or teaching tip</a>,
			or
			<a href='${docReader.textAnnosInProgress[0].url}' class="dleseTmpl" target="_blank">submit a review</a>
		</c:when>
		<%-- If this resources is not under review, just link to the comment submission form --%>
		<c:otherwise>
			<a href="${annoSumbissionUrl}?id=${docReader.id}" class="dleseTmpl" target="_blank"><img src="${urlToMyDir}/images/pencil-gray.gif" style="margin-right:3px;border:0px"/>Submit a comment or teaching tip</a>
		</c:otherwise>
	</c:choose>
	for this resource.
</div>

