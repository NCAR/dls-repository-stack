<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="baseUrl" required="true" %>


	<c:if test="${not empty frameworkRegistry.loadErrors.entries ||
								not empty frameworkRegistry.loadWarnings.entries}" >
	
		<div class="page-error-box">
								
		<c:if test="${not empty frameworkRegistry.loadErrors.entries}">
			<b class="error-msg">ERRORS: The following Frameworks could not be loaded due to fatal errors:</b>
			<ul>
				<c:forEach items="${frameworkRegistry.loadErrors.entries}" var="entry">
					<li class="error-msg"><b>${entry.name}</b> - ${entry.msg}</li>
				</c:forEach>
			</ul>
		</c:if>
		
		<c:if test="${not empty frameworkRegistry.loadWarnings.entries}">
			<b>WARNING: The following Frameworks had non-fatal errors:</b>
			<ul>
				<c:forEach items="${frameworkRegistry.loadWarnings.entries}" var="entry">
					<li><b>${entry.name}</b> - ${entry.msg}</li>
				</c:forEach>
			</ul>
		</c:if>
			
		<input type="button" value="okay (clear messages)"
			onclick="window.location='${baseUrl}?command=clearFrameworkMessages'"/>
		</div>
	</c:if>
