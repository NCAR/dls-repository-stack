<%-- Render the related resources, etc. link when needed --%>

<c:if test="${ relatedDocReader.numCompletedAnnos > 1 }">	
	See <a href='view_resource.do?reviews=${ relatedDocReader.id }'>reviews,
			teaching tips, related resources, etc</a>
</c:if>
<c:if test="${ relatedDocReader.numCompletedAnnos == 1 }">
	<c:if test="${ relatedDocReader.hasRelatedResource == 'true' }">	
		See <a href='view_resource.do?reviews=${ relatedDocReader.id }'>reviews,
				teaching tips, related resources, etc</a>				
	</c:if>
	<c:if test="${ relatedDocReader.hasRelatedResource == 'false' }">				
		<c:if test="${ not empty relatedDocReader.completedAnnos[0].url }">
			See <a href='${ relatedDocReader.completedAnnos[0].url }' 
				target="_blank">reviews, teaching tips, related resources, etc</a>	
		</c:if>	
	</c:if>						
</c:if>
<c:if test="${ relatedDocReader.numCompletedAnnos < 1 }">	
	<c:if test="${ relatedDocReader.hasRelatedResource == 'true' }">
		See <a href='view_resource.do?reviews=${ relatedDocReader.id }'>reviews,
				teaching tips, related resources, etc</a>				
	</c:if>
</c:if>
			
