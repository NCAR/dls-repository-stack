<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<jsp:useBean id="collectionForm" 
	class="edu.ucar.dls.dds.action.form.DDSViewCollectionForm" scope="request"/>
<bean:write name="collectionForm" property="docReader.description" />