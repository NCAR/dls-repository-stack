<%-- $Id: view_framework_config.jsp,v 1.2 2008/10/14 17:41:03 ostwald Exp $ --%>
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">${viewForm.frameworkConfigFormat} Framework</c:set>
<%-- Indent the check box sub-menus by this many pixels --%>
<c:set var="indentAmount" value="18"/>
<c:set var="urlTruncateLen" value="80"/>

<resp:setHeader name="Cache-Control">cache</resp:setHeader>

<html:html>
<head>

		<%@ include file="/baseHTMLIncludes.jsp" %>
		<link rel="stylesheet" href="${contextPath}/lib/autoform-styles.css" type="text/css">

<script type="text/javascript">
<!--	

	function toggleVisibility( elementID ) { 
		$( elementID ).toggle();
	}
	
	/*
		WORKING bestPractices handler. Url opens best practices page in new window
	*/
	function doBestPractices (url) {
		var features = "innerHeight=500,height=500,innerWidth=550,width=550,resizable=yes,scrollbars=yes";
		features += ",locationbar=no,menubar=no,location=no,toolbar=no";
		// alert ("url: " + url);
		// bpwin = window.open (url);
		var bpwin = window.open (url, "bpwin", features);
		bpwin.focus();
	}
	
//-->
</script>


</head>
<title><st:pageTitle title="${title}" /></title>
<body>

<st:pageHeader currentTool="settings" toolLabel="${title}" />

<%-- breadcrumbs --%>
<st:breadcrumbs>
	<a href="../admin/admin.do">Settings</a>
	<st:breadcrumbArrow />
	<a  href="../admin/frameworks.do">Framework Settings</a>
	<st:breadcrumbArrow />
	<span class="current">${title}</span>
</st:breadcrumbs>

<%-- <st:settingsPageMenu  currentPage="collections"/> --%>

<table width="100%" cellpadding="5" cellspacing="0">
	<tr>
		<td colspan="2">

			<%-- ######## Display messages, if present ######## --%>
			<%-- messages from schemedit--%>
			<logic:messagesPresent  message="true">
				<div class="confirm-msg-box">
					<div class="confirm-msg"><b>
						<html:messages id="msg"  message="true"><li>${msg}</li></html:messages>
					</b></div>
				</div>
			  <%-- <HR> --%>
			</logic:messagesPresent>
			
			<logic:messagesPresent> 		
			<table width="90%" bgcolor="#000000" cellspacing="1" cellpadding="8">
			  <tr bgcolor="ffffff"> 
				<td>
					<h3>Unable to display full view</h3>
					<ul>
						<html:messages id="msg" property="message"> 
							<li><bean:write name="msg"/></li>									
						</html:messages>
						<html:messages id="msg" property="error"> 
							<li><font color=red>Error: <bean:write name="msg"/></font></li>									
						</html:messages>
					</ul>
				</td>
			  </tr>
			</table>		
			<br><br>
			</logic:messagesPresent>
			
		</td>
	</tr>
</table>
<div>
	<c:set var="editorHref" 
		  value="../editor/framework_config.do?recId=${viewForm.frameworkConfigFormat}&command=edit&src=local"/>
	<input type="button" value="Edit Settings" class="record-action-button"
		title='edit the settings show on this page'
		onclick="window.location='${editorHref}';" />	
		<span style="padding: 0px 0px 0px 20px">
			Edit this configuration record to change collection settings.
		</span>
</div>
	
<br/>

<%@ include file="/browse/viewing/framework_config_record.jsp" %>

<table width="100%" cellpadding="5" cellspacing="0" border="0">	
	<tr>
		<td colspan="2">		  
			<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0">
				<td bgcolor="#999999" height="1"></td>
			</table>
		</td>
	</tr>
</table>

</body>
</html:html>
