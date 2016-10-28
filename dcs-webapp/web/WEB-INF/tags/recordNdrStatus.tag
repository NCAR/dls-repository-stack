<%@ tag isELIgnored ="false" %>
<%@ tag language="java" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>

<%@ attribute name="result" required="true"  type="edu.ucar.dls.index.ResultDoc" %>
<%@ attribute name="sessionBean" required="true" type="edu.ucar.dls.schemedit.SessionBean" %>

<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
		
<c:if test="${not empty result.docMap['dcsndrHandle']}">
	<bean:define id="dcsData" name="sessionBean" 
							 property="dcsDataRecord(${result.docReader.id})" />
		<div class="searchResultValues">
			<em style="color:green">NDR Handle:</em> 
			<a href="${contextPath}/ndr/ndr.do?command=explore&handle=${dcsData.ndrHandle}"
				 title="View NDR Object in the NDR Object Browser"
				 >${dcsData.ndrHandle}</a>
		</div>
</c:if>

<c:if test="${result.docMap['dcshasSyncError']}">
	<bean:define id="dcsData" name="sessionBean" 
							 property="dcsDataRecord(${result.docReader.id})" />
		<div class="searchResultValues">
			<em style="color:red">Sync Error:</em> 
			${dcsData.ndrSyncError}
		</div>
</c:if>

			
