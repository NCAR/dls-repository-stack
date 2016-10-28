<%-- dcsPageHeader.tag
	- takes two optional attributes
	-- currentTool - the name of the current tool (e.g., Home (Collections), Search, Settings)
	-- toolLabel - Basically, the page title
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="html" uri="/WEB-INF/tlds/struts-html.tld" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>

<%@ attribute name="currentTool" %>
<%@ attribute name="toolLabel" %>
<%@ attribute name="editorMode" %>

<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>

<script type="text/javascript">
	if ($ === undefined)
		throw new Error ("WARNING: prototype library not found");
		
	var editorMode = "${editorMode}" == "true";
	var contextPath = "${contextPath}";
	var auth = {
		manager : "${sf:isAuthorized ('manage', sessionBean)}" == "true",
		admin : "${sf:isAuthorized ('settings', sessionBean)}" == "true"
		}
	var searchParams = unescape("${sessionBean.searchParams}");
	var paigingParam = unescape("${sessionBean.paigingParam}");
	
	document.observe ("dom:loaded", function () { menuInit ($('pageMenu'), "${currentTool}") });
</script>

<%-- pageBanner contains the contents of a file that is specified as init param named "pageBanner" --%>
<c:if test="${not empty pageBanner}">${pageBanner}</c:if>

<%-- <div align="right" style="font-size:8pt">searchParams: ${sessionBean.searchParams}</div> --%>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr valign="middle">
		<td align="left" valign="top">
			
			<div id="pageMenu"></div>
		
			<c:if test="${not empty toolLabel}">
				<div class="tool-label">${toolLabel}</div>
			</c:if>
		</td>
		<td align="center" valign="top" width="98%">
			<div class="dcs-instance">${applicationScope.instanceName}</div>
			<div class="session-data"><%@ include file="/lib/catalogingInfoLink.jspf" %></div>
			<div class="session-data"><%@ include file="/lib/user-status.jspf" %></div>		
		</td>
		<td align="center" nowrap="1">
			<img src="${contextPath}/images/${applicationScope.logo}" height="50px">
			<div class="system-label">Collection System</div>
		</td>
	</tr>
	<tr><td bgcolor="#333366" height="2px" colspan="3"></td></tr>

</table>


