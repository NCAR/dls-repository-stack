<%@ include file="JSTLTagLibIncludes.jsp" %>
<%-- <%@ page import="edu.ucar.dls.repository.*" %> --%>

<c:set var="contextPath"><%@ include file="ContextPath.jsp" %></c:set>

<html:html>

<head>
<title>Collection System Message Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<%@ include file="/baseHTMLIncludes.jsp" %>
</head>

<body>

<st:pageHeader toolLabel="Error" />
<table width="100%" border="0" align="center">
  <tr> 
    <td> 
  
	<%-- ####### Display messages, if present ####### --%>
	<logic:messagesPresent>
	<table width="100%" bgcolor="#000000" cellspacing="1" cellpadding="8">
	  <tr bgcolor="ffffff"> 
		<td>
			<div style="font-size:200%;color:#333366;font-weight:bold">Sorry!...</div>
			<logic:messagesPresent property="message">
			<h3>Messages</h3>
				<ul>
					<html:messages id="msg" property="message"> 
						<li><bean:write name="msg"/></li>
					</html:messages>
				</ul>
			</logic:messagesPresent>
			
			<logic:messagesPresent property="error">
			<h3>Errors</h3>
				<ul>
					<html:messages id="msg" property="error"> 
						<li><bean:write name="msg"/></li>
					</html:messages>
				</ul>
			</logic:messagesPresent>			
			
			<logic:messagesPresent property="recordLocked">
				<ul>
					<html:messages id="msg" property="recordLocked"> 
						<li><font color=blue><bean:write name="msg"/></font></li>
					</html:messages>
				</ul>
			</logic:messagesPresent>
			
			<logic:messagesPresent property="missingLock">
				<ul>
				<html:messages id="msg" property="missingLock"> 
					<li><font color=red><bean:write name="msg"/></font></li>
				</html:messages>
				</ul>				
				<p><a href="${contextPath}/browse/home.do">To the Collections page</a></p>
			</logic:messagesPresent>

			<logic:messagesPresent property="actionSetupError">
			<h3>Configuration Errors</h3>
			<p>Please notify your NCS administrator</p>
				<ul>
					<html:messages id="msg" property="actionSetupError"> 
						<li><font color=red><bean:write name="msg"/></font></li>
					</html:messages>
				</ul>
			</logic:messagesPresent>

			<%-- don't present back button for missingLock --%>
			<logic:messagesNotPresent property="missingLock">
				<p><input type="button" value=" Return to the previous page " onclick="history.back()" /> </p>
			</logic:messagesNotPresent>

		</td>
	  </tr>
	</table>
	</logic:messagesPresent>
	</td>
  </tr>
  </table>

</body>
</html:html>

