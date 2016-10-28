<%@ page import="edu.ucar.dls.schemedit.dcs.*" %>
<%@ page import="edu.ucar.dls.repository.RepositoryManager" %>
<c:set var="collectionRecord" value="${headDocReader.id}" />

<% 
	RepositoryManager rm = (RepositoryManager)application.getAttribute ("repositoryManager");
	DcsDataManager dcsDataManager = (DcsDataManager) application.getAttribute ("dcsDataManager");
	DcsDataRecord dcsDataRecord = 
		dcsDataManager.getDcsDataRecord ((String)pageContext.getAttribute ("collectionRecord"), rm);
	pageContext.setAttribute ("dcsDataRecord", dcsDataRecord);
%>
	<c:if test="${not empty dcsDataRecord}">
<%-- 			
			<c:if test="${not empty dcsDataRecord.nsdlItemId}" >
			<additionalMetadata>
				<ndr_info>
					<lastSyncDate>${dcsDataRecord.lastSyncDate}</lastSyncDate>
					<metadataHandle>${dcsDataRecord.ndrHandle}</metadataHandle>
					<metadataProviderHandle>${dcsDataRecord.metadataProviderHandle}</metadataProviderHandle>
					<setSpec>${dcsDataRecord.setSpec}</setSpec>
					<nsdlItemId>${dcsDataRecord.nsdlItemId}</nsdlItemId>
					<ncsStatus dcsSearchKey="${dcsDataRecord.status}">${dcsDataRecord.ndrNormalizedStatus}</ncsStatus>
				</ndr_info>
			</additionalMetadata>
			</c:if> --%>
			
			<c:if test="${not empty dcsDataRecord.setSpec}">
			<additionalMetadata>
				<collection_info>
					<metadataHandle>${dcsDataRecord.collectionMetadataHandle}</metadataHandle>
					<setSpec>${dcsDataRecord.setSpec}</setSpec>
					<ncsStatus dcsSearchKey="${dcsDataRecord.status}">${dcsDataRecord.ndrNormalizedStatus}</ncsStatus>
				</collection_info>
			</additionalMetadata>
			</c:if>				
	</c:if>
