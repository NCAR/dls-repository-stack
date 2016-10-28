<%@ include file="/include/setup.jsp" %>
<jsp:useBean id="ddsViewResourceForm" class="edu.ucar.dls.dds.action.form.DDSViewResourceForm" scope="session"/>

<%-- Redirect DDS v3.2 and earlier URLs to new DSV equivalent --%>
<c:redirect url="${ domain }${ ddsViewResourceForm.forwardUrl }" />


