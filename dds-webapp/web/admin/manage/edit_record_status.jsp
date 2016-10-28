<%@ include file="../AdminTagLibIncludes.jsp" %>

<html:html>
<head>
<title>Edit the status of records</title>

<%-- <link rel='stylesheet' type='text/css' href='/dlese_shared/dlese_styles.css'> --%>
<link rel='stylesheet' type='text/css' href='dds_admin_styles.css'>

<script>
	// Override the method below to set the focus to the appropriate field.
	function sf(){}	
</script>
</head>
<body onLoad="sf();">

<div style="margin-left:10px">

  
  <br>

 	<h1>Edit the status of records</h1>
 

	<%-- ######## Display messages, if present ######## --%>
	<logic:messagesPresent> 		
	<table width="90%" bgcolor="#000000" cellspacing="1" cellpadding="8">
	  <tr bgcolor="ffffff"> 
		<td>
			<b>Messages:</b>
			<ul>
				<html:messages id="msg" property="message"> 
					<li><bean:write name="msg"/></li>									
				</html:messages>
				<html:messages id="msg" property="error"> 
					<li><font color=red>Error: <bean:write name="msg"/></font></li>									
				</html:messages>
			</ul>
			<blockquote>[ <a href="query.do">OK</a> ]</blockquote>
		</td>
	  </tr>
	</table>		
	<br><br>
	</logic:messagesPresent>	
	

	<%-- Comments/status entry box --%>
	<html:form action="/admin/manage" method="GET" style="padding-bottom:0px">
		<table align=left cellspacing="6" cellspacing=0>
		  <script>
			function sf(){document.mf.statusComment.focus();}	
		  </script>
		  
		  <tr>
			<td valign=top>
				<nobr></nobr>
			</td>
			<td colspan=5>
				<nobr>
				Enter comments:<br>
				<html:textarea property="statusComment" cols="75" rows="8"/>
				<br>
				<html:submit value="Submit"/>
				</nobr>
			</td>
		  </tr>	 
		</table>
	</html:form>

 <c:forEach items="${paramValues}" var="entry">
 	param: ${entry.key} vals: 
	<c:forEach items="${entry.value}" var="paramVal">
		${paramVal},
	</c:forEach>
 </c:forEach>
	
  <br><br>

</div>  
</body>
</html:html>

