<%--
	This JSP page creates the HTML for smart links from the definitions
	found in the file smart_link_definitions.xml and stores them in 
	a variable named 'smartLinks'. It is meant to be included inside a
	JSP page for searching. Requires web_service_connection.jsp to be
	included in the page prior to including this page.
	
	Example use:
	
	<!-- Include the required web service connection page prior to use -->
	<%@ include file="web_service_connection.jsp" %>
	
	...
	
	<!-- Generate the smart links HTML -->
	<%@ include file="smart_links.jsp" %>
	
	<!-- Then display the smart links at the desired location in your page -->	
	${smartLinks}

	
	This is JSP client version 2.2.		
--%>
<%-- <c:set var="smTemp" value="${param.sl1} ${param.sl2} ${param.sl3} ${param.sl4} ${param.sl5} ${param.sl6} ${param.sl7} ${param.sl8} ${param.sl9} ${param.sl10} ${param.sl11} ${param.sl12} ${param.sl13} ${param.sl14} ${param.sl15} ${param.sl16} ${param.sl17} ${param.sl18} ${param.sl19} ${param.sl20}"/>
<c:set var="smArray" value="${fn:split(smTemp,' ')}"/> --%>

<c:set var="smartLinks">
	<div><b>${empty param.smTitle ? 'Quick links' : param.smTitle}</b></div>
	<div style="margin-left:4px">
		<table cellspacing="5">
			<tr valign="top">
				<td>
					<c:forEach begin="0" end="20" varStatus="j">
						<c:set var="paramName">slm${j.index}</c:set>
						<c:set var="tmp">${params[paramName]}</c:set>
						<c:set var="tmp">${empty tmp ? 'N/A|none' : tmp}</c:set>
						<c:set var="smTuple" value="${fn:split(tmp,'|')}"/>
						${smTuple[0]} is mappged to query '${smTuple[0]}'<br>
					</c:forEach>
				</td>
			</tr>
		</table>
	</div>
</c:set>


