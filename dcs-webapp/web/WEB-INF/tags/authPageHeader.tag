<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="html" uri="/WEB-INF/tlds/struts-html.tld" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>

<%@ attribute name="toolLabel" %>
<%@ attribute name="logoPath" %>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr valign="middle">
		<td align="left">
			<c:if test="${not empty toolLabel}">
				<div class="tool-label">${toolLabel}</div>
			</c:if>
		</td>
		<td align="center" valign="top" width="98%">
			<div class="dcs-instance">${applicationScope.instanceName}</div>	
		</td>
		<td align="center" nowrap="1">
			<img src="${logoPath}" height="50">
			<div class="system-label">Collection System
				<span style="font-size:.85em;font-weight:normal;">
					(<a href="${contextPath}/docs/dcs-users-guide.pdf" target="_blank">User Guide</a>)
				</span>
			</div>
		</td>
	</tr>
	<tr><td bgcolor="#333366" height="2px" colspan="3"></td></tr>

</table>
