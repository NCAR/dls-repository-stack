<%-- renders the check boxes used for res_qual standards browse and suggestions --%>

<%@ tag isELIgnored ="false" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>
<%@ taglib tagdir="/WEB-INF/tags/std" prefix="std" %>
<%@ tag import="edu.ucar.dls.schemedit.standards.StandardsNode" %>

<%@ attribute name="stdNode" required="true" type="edu.ucar.dls.schemedit.standards.StandardsNode"%>
<%@ attribute name="stdIsSuggested" required="true" type="java.lang.Boolean"%>
<%@ attribute name="stdIsSelected" required="true" type="java.lang.Boolean"%>
<%@ attribute name="elementPath" required="true" type="java.lang.String"%>
<%@ attribute name="dleseId" required="false" type="java.lang.String"%>

<div class="suggestion-box ${stdIsSuggested ? 'suggested' : ''}	${stdIsSelected ? 'checked' : ''}" >
	<input type="checkbox" id="<c:out value="${stdNode.id}"/>_id" 
		name="${elementPath}"
		value="<c:out value="${stdNode.id}"/>"
		<c:if test="${stdIsSelected}">checked="1"</c:if>
	/>
	<label 
		<c:if test="${not empty stdNode.definition}">title="${stdNode.definition}"</c:if>
		for="<c:out value="${stdNode.id}"/>_id"><c:out 
			value="${dleseId == 'true' ? stdNode.id : stdNode.label}" escapeXml="false" />
	</label>
</div>
<script type="text/javascript">
	/* what ALSO want to disable the click listener! */
	<c:if test="${stdIsSelected}">
		$("<c:out value='${stdNode.id}'/>_id").disable();
	</c:if>
</script>
