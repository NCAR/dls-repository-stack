<%@ include file="AdminTagLibIncludes.jsp" %>

<c:set var="mmdRec" value="${result.mmdRec}"/>
<div style="padding-left:20px">
<c:choose>
	<c:when test="${empty mmdRec || mmdRec == null}">
		No data available
	</c:when>
	<c:otherwise>
			<div>
				<em>Data for:</em>
			</div>
			<div style="padding-left:20px">
				<%@ include file="mmd_rec_data.jsp" %>
			</div>

			<div>
				<em>Duplicate records in the same collection:</em>
			</div>			
			<c:set var="mmdDupesInSameCollection" value="${result.mmdDupesInSameCollection}"/>
			<div style="padding-left:20px">
				<c:choose>
					<%-- Only display if there is more than one since one will be this record... --%>
					<c:when test="${empty mmdDupesInSameCollection || mmdDupesInSameCollection == null || fn:length(mmdDupesInSameCollection) <= 1 }">
						No duplicate records were found in the same collection
					</c:when>
					<c:otherwise>
						<c:forEach var="mmdRec" items="${mmdDupesInSameCollection}">
							<%@ include file="mmd_rec_data.jsp" %>
						</c:forEach>
					</c:otherwise>
				</c:choose>				
			</div>
			
			
			<div>
				<em>Duplicate records in other collections:</em>
			</div>			
			<c:set var="mmdDupesInOtherCollections" value="${result.mmdDupesInOtherCollections}"/>
			<div style="padding-left:20px">
				<c:choose>
					<c:when test="${empty mmdDupesInOtherCollections || mmdDupesInOtherCollections == null}">
						No duplicates were found in other collections
					</c:when>
					<c:otherwise>
						<c:forEach var="mmdRec" items="${mmdDupesInOtherCollections}">
								<%@ include file="mmd_rec_data.jsp" %>
						</c:forEach>
					</c:otherwise>					
				</c:choose>
			</div>
		
	</c:otherwise>
</c:choose>
</div>
