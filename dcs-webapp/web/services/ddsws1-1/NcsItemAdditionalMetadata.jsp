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
			<additionalMetadata>
				<ndr_info>
					<lastSyncDate>${dcsDataRecord.lastSyncDate}</lastSyncDate>
					<metadataHandle>${dcsDataRecord.ndrHandle}</metadataHandle>
					<nsdlItemId>${dcsDataRecord.nsdlItemId}</nsdlItemId>
					<ncsStatus dcsSearchKey="${dcsDataRecord.status}">${dcsDataRecord.ndrNormalizedStatus}</ncsStatus>
				</ndr_info>				
			</additionalMetadata>
		</c:if>


