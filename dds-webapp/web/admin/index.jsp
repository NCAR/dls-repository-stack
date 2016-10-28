<%@ page language="java" %>
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>

<%-- Redirect to the Collection Manager for now... --%>
<c:redirect url="/admin/admin.do"/>



<html>

<head>

<script language="javascript"><!--

	//document.location.href = "admin.do";

//-->

</script>

<title>DDS System Administration</title>



<link rel='stylesheet' type='text/css' href='dds_admin_styles.css'>

<%@ include file="/nav/head.jsp" %>

</head>

<body>

<%@ include file="/nav/top.jsp" %>

<br><br><br>

 DDS system administration is available through the <a href="admin.do">Collection manager</a>.

  
<%@ include file="/nav/bottom.jsp" %> 
</body>

</html>

