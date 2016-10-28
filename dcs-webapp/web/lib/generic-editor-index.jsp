<%@ page isELIgnored ="false" %>

<%@ page language="java" %>

<jsp:useBean id="sef" class="edu.ucar.dls.schemedit.action.form.SchemEditForm"  scope="session"/>

<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>

<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>

<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>

<%@ taglib uri="/WEB-INF/tlds/taglibs-application.tld" prefix="app" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="st" %>

<%@ taglib uri="/WEB-INF/tlds/schemedit-functions.tld" prefix="sf" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>



<html>

<head>

	<link rel="stylesheet" href="../styles.css" type="text/css">

	<link rel="stylesheet" href="../lib/autoform-styles.css" type="text/css">

	<script language="JavaScript">

		var submitAction="${sef.submitAction}";

	</script>

	<script language="JavaScript" src="../lib/manage-support.js"></script>

<title>${sef.frameworkName} Editor</title>

</head>

<body bgcolor=white>



<st:pageHeader />



<st:editorPageMessages/>



<html:form action="${sef.formActionPath}" method="Post" onsubmit="return (doFetchById(this.recId.value))">

	<input type="hidden" name="command" value="edit"/>

	<input type="hidden" name="src" value=""/>



<div class="nav-menu-box" align="center">

	<table width="95%" border="0">

		<tr>

			<td nowrap><div class="page-head">${sef.frameworkName} Records</div></td>

			<td align="center" width="25%"><input type="button" value="New record" onClick="doNewRecord()"/></td>

			<td align="center" width="25%"><input type="button" value="Edit sample record" onClick="doEditSample()"/></td>

			<td nowrap align="right">

				<a href="/schemedit/index.jsp">SchemEdit Home</a><br>

				<a href="/schemedit/editor_admin.do?xmlFormat=${sef.xmlFormat}">Editor Admin</a>

			</td>

		</tr>

	</table>

</div>

</html:form>



<blockquote>

<%@ include file="../lib/toc.jspf" %>

</blockquote>



</body>

</html>

