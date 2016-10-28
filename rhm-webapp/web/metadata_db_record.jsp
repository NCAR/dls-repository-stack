<%@ include file="TagLibIncludes.jsp" %>
<%@ taglib prefix="custom" uri="/WEB-INF/tlds/custom.tld"%>
<%@ page trimDirectiveWhitespaces="true" %>
<c:set var="setSpec" value="${param.setSpec}"/>
<c:set var="metadatahandle" value="${param.metadatahandle}"/>
<c:set var="recordType" value="${param.recordType}"/>
<c:set var="responseType" value="${param.responseType}"/>

<c:choose>
	<c:when test="${recordType=='native'}">
		<c:set var="dataColumn" value="native_xml"/>
		<c:set var="dataFormat" value="nativeformat"/>
	</c:when>
	<c:otherwise>
		<c:set var="dataColumn" value="target_xml"/>
		<c:set var="dataFormat" value="targetformat"/>
	</c:otherwise>
</c:choose>

<c:catch var="sqlDbError">
	<sql:setDataSource 
		var="metadataDB"
		user="${initParam.harvestDbUser}" 
		password="${initParam.harvestDbPwd}"
		url="${initParam.harvestDbUrl}/hm_repository?zeroDateTimeBehavior=convertToNull"
		driver="com.mysql.jdbc.Driver" />
		
	<sql:query var="metadataEntries" dataSource="${metadataDB}">
		SELECT ${dataColumn} as record, ${dataFormat} as format
		FROM `metadata` 
		WHERE setSpec = '${setSpec}' and metadatahandle='${metadatahandle}'

	</sql:query>
</c:catch>

<c:choose>
	<c:when test="${not empty metadataEntries && metadataEntries.rowCount > 0}">
		<c:set var="metadataRow" value="${metadataEntries.rows[0]}"/>
		<c:choose>
			<c:when test="${responseType=='json' && (metadataRow.format=='lrmi' || metadataRow.format=='lr_paradata')}">
				<% response.setContentType("application/json; charset=utf-8");%>
			</c:when>
			<c:when test="${(metadataRow.format=='oai_dc' || metadataRow.format=='nsdl_dc' || metadataRow.format=='lar' ||  metadataRow.format=='comm_para' || metadataRow.format=='comm_anno')}">
				<% response.setContentType("text/xml; charset=utf-8");%>
			</c:when>
			<c:otherwise>
				<% response.setContentType("text/plain; charset=utf-8");%>
			</c:otherwise>
		</c:choose>
		<custom:display-blob blob="${metadataRow.record}"/>
	</c:when>
	<c:otherwise>
		<% response.setContentType("text/xml; charset=utf-8");%>
		<p>No records were found.</p>
	</c:otherwise>
</c:choose>