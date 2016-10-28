<%--
	From JSP client version 2.5.		
--%>

<%-- Create and store the HTML for the pager in variable 'pager' --%>
<c:set var="pager">
	<nobr>
	<c:if test="${(startingOffset - resultsPerPage) >= 0}">	
		<%-- Construct a URL to this page that gets the previous page of results --%>
		<c:url value="" var="prevResultsUrl">
			<c:param name="s" value="${startingOffset - resultsPerPage}"/>
			
			<%-- Echo all params except those indicated --%> 
			<c:forEach items="${paramValues}" var="pname">
				<c:if test="${ignoreParams[pname.key] == null}">
					<c:forEach items="${pname.value}" var="pval">
						<c:param name="${pname.key}" value="${pval}"/>
					</c:forEach>
				</c:if>
			</c:forEach>														
		</c:url>						
		<a href="${prevResultsUrl}" class="dleseTmpl" title="Previous page of resources">&lt;&lt;</a> &nbsp;
	</c:if>
	<%-- For older JSTL engines, explicitly cast to int and make sure end is > zero --%>
	<c:set var="endVal"
		 value="${((numResults-1)/resultsPerPage) - 4 >= (startingOffset/resultsPerPage) ? (startingOffset/resultsPerPage) + 4 : ( (numResults-1)/resultsPerPage)}"/>
	<c:set var="endVal" value="${ endVal+0 < 0 ? 0 : endVal+0}"/>
	<c:if test="${endVal >= 1}"><c:set var="hasPages" value="t"/></c:if>				
	<c:forEach	begin="${ (startingOffset/resultsPerPage) - 4 >= 0 ? (startingOffset/resultsPerPage) - 4 : 0}" 
				end="${endVal}" 
				varStatus="status">
		<c:choose>
			<c:when test="${ status.index == (startingOffset/resultsPerPage)}">
				<%-- Only display the current page number if there are more than one pages --%>
				<c:if test="${not empty hasPages}">
					<b>${ status.index + 1 }</b>
				</c:if>
			</c:when>
			<c:otherwise>
				<%-- Construct a URL to this page that gets the nth page of results --%>
				<c:url value="" var="pageUrl">
					<c:param name="s" value="${resultsPerPage*status.index}"/>
					
					<%-- Echo all params except those indicated --%> 
					<c:forEach items="${paramValues}" var="pname">
						<c:if test="${ignoreParams[pname.key] == null}">
							<c:forEach items="${pname.value}" var="pval">
								<c:param name="${pname.key}" value="${pval}"/>
							</c:forEach>
						</c:if>
					</c:forEach>						
				</c:url>							
				<a href="${pageUrl}" class="dleseTmpl">${ status.index + 1 }</a>
			</c:otherwise>
		</c:choose>
	</c:forEach>
	<c:if test="${(startingOffset + resultsPerPage) < numResults}">					
		<%-- Construct a URL to this page that gets the next page of results --%>
		<c:url value="" var="nextResultsUrl">
			<c:param name="s" value="${startingOffset + resultsPerPage}"/>
			
			<%-- Echo all params except those indicated --%> 
			<c:forEach items="${paramValues}" var="pname">
				<c:if test="${ignoreParams[pname.key] == null}">
					<c:forEach items="${pname.value}" var="pval">
						<c:param name="${pname.key}" value="${pval}"/>
					</c:forEach>
				</c:if>
			</c:forEach>							
		</c:url>							
		&nbsp; <a href="${nextResultsUrl}" class="dleseTmpl" title="Next page of resources">&gt;&gt;</a>
	</c:if>
	</nobr>
</c:set>		
		



