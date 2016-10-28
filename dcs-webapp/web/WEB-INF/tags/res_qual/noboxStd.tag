<%-- noboxStd.tag
	displays a standards node (just like std/display.tag) but without checkbox
	note: std is id attribute of div element to enable DOM manipulation 
--%>

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

<div class="suggestion-box ${stdIsSuggested ? 'suggested' : ''}	${stdIsSelected ? 'checked' : ''}" >
	<div id="<c:out value="${stdNode.id}"/>_id" />${stdNode.label}</div>
</div>

