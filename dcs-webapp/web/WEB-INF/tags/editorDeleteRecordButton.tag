<%-- editorDeleteRecordButton.tag
	- requires recId argument
	- optional label argument defaults to "Copy"
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>

<%@ attribute name="recId" required="true" %>
<%@ attribute name="label" %>

<c:set var="authorized" value="${sf:isAuthorized ('deleteRecord', sessionBean)}" />

<input type="button" 
		value="${not empty label ? label : 'Delete'}"
		title="Delete this record and return to search"
		<c:choose>
			<c:when test="${authorized}">
				class="editor-action-button"
				onclick="alert('Sorry - not yet implemented. To delete this record exit the editor and then delete \
					it from either Search or Full View page.')"
			</c:when>
			<c:otherwise>
				class="editor-action-button disabled"
				onclick="alert ('You do not have permission to delete records');"
			</c:otherwise>
		</c:choose>  />
	   

