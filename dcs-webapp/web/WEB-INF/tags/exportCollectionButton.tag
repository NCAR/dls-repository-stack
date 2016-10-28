<%-- dexportCollectionButton.tag
	- requires that a propertyId param is set in calling page 
	- diplays error messages relevant to a particular field (identified by propertyId)
	--%>
<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>

<%@ attribute name="set" required="true" type="edu.ucar.dls.schemedit.dcs.DcsSetInfo"%>

<c:choose>
	<c:when test="${set.authority == 'ndr'}">
		<a href="../ndr/ndr.do?command=export&pid=${set.setSpec}" 
			 title="Export collection to the NDR and remove from repository">Export to NDR</a>

	</c:when>
	<c:when test="${set.authority == 'dcs'}">
		<a href="../manage/collections.do?command=export&setup=true&collection=${set.setSpec}"
			 title="Export records from this collection to files on disk">Export</a>
	</c:when>
</c:choose>

