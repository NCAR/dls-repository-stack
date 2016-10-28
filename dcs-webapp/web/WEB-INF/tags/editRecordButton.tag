<%-- editRecordButton.tag
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="reader" required="true"  type="edu.ucar.dls.index.reader.XMLDocReader" %>
<%@ attribute name="label" %>
<%@ attribute name="disabled"%>

<input type="button" value="${not empty label ? label : 'edit'}" 
	class="record-action-button"
	onclick="window.location='../editor/edit.do?command=edit&recId=${reader.id}'"
	title='edit this record in metadata editor'
	<c:if test="${disabled}">disabled="true"</c:if>
/>
