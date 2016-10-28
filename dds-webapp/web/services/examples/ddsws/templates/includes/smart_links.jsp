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

	
	This is JSP client version @DDSWS_CLIENT_VERSION@.		
--%>
<c:set var="smartLinks" value="smartLinks${clientName}"/>
<c:if test="${(empty applicationScope.smartLinkResources[smartLinks] || param.reload == 'true') && not empty mySmartMenuDom}">
	<c:if test="${empty numSmartLinksPerColumn}">
		<c:set var="numSmartLinksPerColumn" value="3"/>
	</c:if>
	<c:set target="${applicationScope.smartLinkResources}" property="${smartLinks}">
		<table class="dleseTmpl" cellspacing="5">
			<tr class="dleseTmpl" valign="top">
				<x:forEach select="$mySmartMenuDom/root/menu[@smartLinkLabel]" var="menu" varStatus="j">
					<c:set var="hasSmartLinks" value="true"/>
					<td class="dleseTmpl" nowrap style="padding-left:${j.count == 1 ? '0' : '12'}px">	 			
						<b><x:out select="$menu/@smartLinkLabel"/></b>
						<div style="margin-left:4px">
						
						<c:set var="id"><x:out select="$menu/@id"/></c:set>
						<c:set var="myParamName" value="slm${id}"/>
							<x:forEach select="$menu/menuItem" var="item" varStatus="i">
								<c:url value="" var="smartUrl">
									<c:param name="${myParamName}"><x:out select="$item/@id"/></c:param>
								</c:url>
								<a href="${smartUrl}" class="dleseTmpl"><x:out select="$item/label"/></a><br>
								<c:if test="${(i.count % numSmartLinksPerColumn == 0) && !i.last}">
									</div>
									</td>
									<td nowrap>
									&nbsp;<br>
									<div style="margin-left:8px">
								</c:if>
							</x:forEach>
						</div>
					</td>
				</x:forEach> 
			</tr>
		</table>
	</c:set>
	<c:if test="${hasSmartLinks != 'true'}">
		<c:set target="${applicationScope.smartLinkResources}" property="${smartLinks}" value=""/>
	</c:if>
</c:if>


