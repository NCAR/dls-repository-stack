<%--
	This JSP page constructs a pager for clicking through a set of search results. 
	It is meant to be included inside the search results portion of an encompassing JSP.
	Requires web_service_connection.jsp to be included prior to using this page.
	
	Example use:
	
	<!-- When inside a search result block... -->
	<c:when test="${ (not empty param.q || not empty param.qh || not empty param.su || not empty param.gr || 
		not empty param.re || not empty param.ky || not empty param.cs) && empty param.fullDescription }">
	<!-- Include this file -->
	<%@ include file="pager.jsp" %>									
	<!-- Then output the page HTML where desired in your UI -->
	${pager}
	
	Provided by the Digital Library for Earth System Education (DLESE)
	free and with no warranty. This example may be copied and modified.
	e-mail: support@dlese.org
	Information: @DDS_SERVER_BASE_URL@/services/

	These templates are available for download at 
	http://sourceforge.net/project/showfiles.php?group_id=23991&package_id=123037
	
	Requires Apache Tomcat 5 (http://jakarta.apache.org/tomcat/)
	or other JSP 2.0 compliant JSP container and the 
	JSTL v1.1 tag libraries, which are included in the examples download
	or can be obtained from http://java.sun.com (standard.jar and jstl.jar).	
	
	This is JSP client version @DDSWS_CLIENT_VERSION@.		
--%>

<%-- Create and store the HTML for the pager in variable 'pager' --%>
<c:set var="pager">
	<nobr>
	<c:if test="${(startingOffset - resultsPerPage) >= 0}">	
		<%-- Construct a URL to this page that gets the previous page of results --%>
		<c:url value="" var="prevResultsUrl">
			<c:param name="s" value="${startingOffset - resultsPerPage}"/>
			
			<%-- Echo all params except s --%> 
			<c:forEach items="${paramValues}" var="pname">
				<c:if test="${pname.key != 's' && pname.key != 'rt'}">
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
	<c:forEach	begin="${ (startingOffset/resultsPerPage) - 4 >= 0 ? (startingOffset/resultsPerPage) - 4 : 0}" 
				end="${endVal}" 
				varStatus="status">
		<c:choose>
			<c:when test="${ status.index == (startingOffset/resultsPerPage) }">
				<b>${ status.index + 1 }</b>
			</c:when>
			<c:otherwise>
				<%-- Construct a URL to this page that gets the nth page of results --%>
				<c:url value="" var="pageUrl">
					<c:param name="s" value="${resultsPerPage*status.index}"/>
					
					<%-- Echo all params except s --%> 
					<c:forEach items="${paramValues}" var="pname">
						<c:if test="${pname.key != 's' && pname.key != 'rt'}">
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
			
			<%-- Echo all params except s --%> 
			<c:forEach items="${paramValues}" var="pname">
				<c:if test="${pname.key != 's' && pname.key != 'rt'}">
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
		



