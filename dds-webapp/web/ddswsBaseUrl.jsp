<%-- 
	Sets the DDSWS 1-1 baseURL into variable 'ddsws11BaseUrl' 
	
--%><c:set var="ddsws11BaseUrl"><c:choose><c:when test="${not empty ddsws11BaseUrlOverride}">${ddsws11BaseUrlOverride}</c:when><c:otherwise>${f:contextUrl(pageContext.request)}/services/ddsws1-1</c:otherwise></c:choose></c:set>
