<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib tagdir="/WEB-INF/tags/std" prefix="std" %>

<%@ attribute name="standardsDocument" required="true" 
	type="edu.ucar.dls.schemedit.standards.asn.AsnStandardsDocument"%>

<c:if test="${not empty standardsDocument.author}">
	${standardsDocument.author}
</c:if>
<c:if test="${not empty standardsDocument.title}">
	- ${standardsDocument.title}
</c:if>
<c:if test="${not empty standardsDocument.created}">
	(${standardsDocument.created})
</c:if>

