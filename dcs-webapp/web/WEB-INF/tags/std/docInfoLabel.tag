<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib tagdir="/WEB-INF/tags/std" prefix="std" %>
<%@ tag import="edu.ucar.dls.schemedit.standards.asn.AsnDocInfo" %>

<%@ attribute name="docInfo" required="true" type="edu.ucar.dls.schemedit.standards.asn.AsnDocInfo"%>


<c:if test="${not empty docInfo.author}">
	${docInfo.author}
</c:if>
<c:if test="${not empty docInfo.title}">
	- ${docInfo.title}
</c:if>
<c:if test="${not empty docInfo.created}">
	(${docInfo.created})
</c:if>

