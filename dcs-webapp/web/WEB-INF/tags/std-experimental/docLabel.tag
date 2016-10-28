<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib tagdir="/WEB-INF/tags/std" prefix="std" %>
<%@ tag import="edu.ucar.dls.schemedit.standards.asn.AsnStandardsManager" %>

<%@ attribute name="standardsManager" required="true" type="edu.ucar.dls.schemedit.standards.asn.AsnStandardsManager"%>

<div class="std-doc-label">
<c:if test="${not empty standardsManager.author}">
	${standardsManager.author}
</c:if>
<c:if test="${not empty standardsManager.topic}">
	- ${standardsManager.topic}
</c:if>
<c:if test="${not empty standardsManager.created}">
	- ${standardsManager.created}
</c:if>
</div>

<c:if test="${not empty standardsManager.title}">
<div class="std-doc-title">${standardsManager.title}</div>
</c:if>
