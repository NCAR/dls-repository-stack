<%--
	This JSP page constructs a display of the grade ranges, subjects, resource
	types, content standards and collections selected by the user for their 
	search. It is meant to be included inside an encompassing search JSP page.
	
	Example use:
	
	... 
	<%@ include file="user_selections.jsp" %>
	...	
--%>


<%-- Store the selected status selections for display below --%>
<c:set var="allstring" value="all"/>

<%-- Collections --%>
<c:set var="collectionsDisplay">
	<span class="selectionsTitle">Collection${fn:length(queryForm.scs) > 1 ? 's' : ''}:</span>
	<span class="${fn:length(queryForm.scs) == 1 ? 'selectionsHighlightBig' : 'selectionsHighlight'}">
	<c:choose>
		<c:when test="${not empty queryForm.scs}">
			<c:forEach var="paramValue" items="${queryForm.scs}" varStatus="status">
				<c:forEach var="labelValueBean" items="${sessionBean.collectionLabelValues}">
					<c:if test="${labelValueBean.value eq paramValue}">${labelValueBean.label}</c:if>
				</c:forEach><c:if test="${!status.last}">;</c:if></c:forEach>
		</c:when>
		<c:otherwise>${allstring}</c:otherwise>
	</c:choose>			
	</span>
</c:set>

<%-- Last Editor --%>
<c:set var="selectionsDisplay">
	<c:if test="${not empty selectionsDisplay}">${selectionsDisplay} + </c:if>
	<span class="selectionsTitle">Last Editor${fn:length(queryForm.ses) > 1 ? 's' : ''}:</span> 
	<span class="selectionsHighlight">
	<c:choose>
		<c:when test="${not empty queryForm.ses}">
			<c:forEach var="paramValue" items="${queryForm.ses}" varStatus="status">
				${sf:getFullName (paramValue, userManager)}<c:if test="${!status.last}">;</c:if>					
			</c:forEach>
		</c:when>
		<c:otherwise>${allstring}</c:otherwise>
	</c:choose>			
	</span>
</c:set>			

<%-- Record Creator --%>
<c:set var="selectionsDisplay">
	<c:if test="${not empty selectionsDisplay}">${selectionsDisplay} + </c:if>
	<span class="selectionsTitle">Record Creator${fn:length(queryForm.ses) > 1 ? 's' : ''}:</span> 
	<span class="selectionsHighlight">
	<c:choose>
		<c:when test="${not empty queryForm.srcs}">
			<c:forEach var="paramValue" items="${queryForm.srcs}" varStatus="status">
				${sf:getFullName (paramValue, userManager)}<c:if test="${!status.last}">;</c:if>					
			</c:forEach>
		</c:when>
		<c:otherwise>${allstring}</c:otherwise>
	</c:choose>			
	</span>
</c:set>	

<%-- Formats --%>
<c:set var="selectionsDisplay">
	<c:if test="${not empty selectionsDisplay}">${selectionsDisplay} + </c:if>
	<span class="selectionsTitle">Format${fn:length(queryForm.sfmts) > 1 ? 's' : ''}:</span> 
	<span class="selectionsHighlight">
	<c:choose>
		<c:when test="${not empty queryForm.sfmts}">
			<c:forEach var="paramValue" items="${queryForm.sfmts}" varStatus="status">
				${fn:substringAfter(paramValue,'0')}<c:if test="${!status.last}">;</c:if>					
			</c:forEach>
		</c:when>
		<c:otherwise>${allstring}</c:otherwise>
	</c:choose>
	</span>
</c:set>			

<%-- Validity --%>
<c:set var="selectionsDisplay">
	<c:if test="${not empty selectionsDisplay}">${selectionsDisplay} + </c:if>
	<span class="selectionsTitle">Validity:</span>
	<span class="selectionsHighlight">
	<c:choose>
		<c:when test="${not empty queryForm.vld}">${queryForm.vld};</c:when>
		<c:otherwise>${allstring}</c:otherwise>
	</c:choose>
	</span>
</c:set>

<%-- Statuses --%>
<c:set var="selectionsDisplay">
	<c:if test="${not empty selectionsDisplay}">${selectionsDisplay} + </c:if>
	<span class="selectionsTitle">Status${fn:length(queryForm.sss) > 1 ? 'es' : ''}:</span> 
	<span class="selectionsHighlight">
	<c:choose>
		<c:when test="${not empty queryForm.sss}">	
			<c:forEach var="paramValue" items="${queryForm.sss}" varStatus="status">
				${paramValue}<c:if test="${!status.last}">;</c:if>					
			</c:forEach>
		</c:when>
		<c:otherwise>${allstring}</c:otherwise>
	</c:choose>
	</span>
</c:set>			

<%-- Edit below to change the layout, look or feel as desired --%>
<%-- <c:if test="${not empty selectionsDisplay}"> 
	<tr id="yourSelections">
		<td colspan=2>
			<nobr>Your selections:</nobr> 
			${selectionsDisplay}
		</td>
	</tr>
	
</c:if> --%>
<c:if test="${not empty selectionsDisplay || not empty collectionsDisplay}"> 
	<div id="yourSelections">
		<table cellspacing="0" cellpadding="0">
			<tr>
				<td nowrap>Your selections: </td>
				<td><c:if test="${not empty collectionsDisplay}">${collectionsDisplay}</c:if>&nbsp;</td>
			</tr>
			<c:if test="${not empty selectionsDisplay}">
			<tr>
				<td>&nbsp;</td>
				<td>${selectionsDisplay}</td>
			</tr>
			</c:if>
		</table>
	</div>
</c:if>


