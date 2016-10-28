<%-- deleteRecordLink.tag
	- requires that a propertyId param is set in calling page 
	- diplays error messages relevant to a particular field (identified by propertyId)
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="id" required="true" %>
<%@ attribute name="label" %>
<%@ attribute name="context" %>
<%@ attribute name="disabled"%>

<input type="button" value="${not empty label ? label : 'copy + move'}" 
			 class="record-action-button"
			 onclick="window.location='../record_op/single.do?command=copymove&recId=${id}'"
			 title="move a copy of this record to another collection"
			 <c:if test="${disabled}">disabled="true"</c:if>
/>
