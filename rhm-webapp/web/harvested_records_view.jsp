<%@ include file="TagLibIncludes.jsp" %>
<c:choose>
	<c:otherwise>
		<c:set var="dir">
			<c:choose>
				<c:when test="${empty param.dir}">/</c:when>
				<c:otherwise>${param.dir}</c:otherwise>
			</c:choose>
		</c:set>
		<c:catch var="importErr">
			<c:import url="/${param.collectionNA}/${param.uuid}${dir}" context="/${initParam.harvestRecordsContext}" var="dirListing"/>
		</c:catch>
		<c:choose>
			<c:when test="${fn:contains(dirListing,'is not available')}">
				<p>There are no records available for this session due to the fact that we only keep files around for the last 2 successful harvests and the latest failed harvest if applicable.
				<!-- (Records not available because the requested file directory is empty) -->
			</c:when>
			<c:otherwise>
				<div style="padding-top:10px">
					Directory Listing For:
					/<c:forEach items="${fn:split(dir,'/')}" var="subDir">${subDir}/<c:set var="lastDir" value="${subDir}"/></c:forEach>
					<c:if test="${not empty lastDir}">
						 <nobr> &nbsp;&nbsp;&nbsp;[ <a href="javascript:void(0)" onclick="doRecordsDisplay('${fn:substringBefore(dir,lastDir)}')">up one directory</a> ]</nobr>
					</c:if>
				</div>
				<div>
					${dirListing}
				</div>
			</c:otherwise>
		</c:choose>
		<c:if test="${not empty importErr}">
			<!--  Error: ${importErr} -->
		</c:if>
	</c:otherwise>
</c:choose>
