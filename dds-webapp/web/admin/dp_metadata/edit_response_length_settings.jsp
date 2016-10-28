<%@ include file="/admin/AdminTagLibIncludes.jsp" %>

<%-- Create a scripting variable named "index" from the parameter "currentIndex" --%>
<bean:parameter name="currentIndex" id="index" value="0"/>

<html:html>

<head>

<c:set var="title" value="Edit OAI Response Length"/>

<title>${title}</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<link rel='stylesheet' type='text/css' href='<c:url value="/admin/dds_admin_styles.css"/>'>

<%-- Include style/menu templates --%>
<%@ include file="/nav/head.jsp" %>

<script>
	function sf(){document.raf.numRecordsResults.focus();}
</script>

</head>

<body text="#000000" bgcolor="#ffffff" onLoad=sf()>

<%-- Include style/menu templates --%>
<%@ include file="/nav/top.jsp" %>


	<html:form action="/admin/data-provider-info.do" method="POST">
	<h3>${title}</h3>

		<logic:messagesPresent> 		
			<p style="color:red">
				There was an error. These values must contain integers greater than zero and must not be blank.
			</p>
			<p style="color:red">
				Please fix the error and click "Save," or choose "Cancel."
			</p>
		</logic:messagesPresent>
				
<p>
Change these numbers to adjust for system memory capabilities or metadata file size. Smaller numbers mean harvesters must make more requests to harvest the repository. Larger numbers mean fewer requests are necessary but require greater 
system resources by both the data provider and harvester. </p>
		
		
		<%-- ######## Number of results values ######## --%>
		<table bgcolor="#666666" cellpadding="6" cellspacing="1" border="0"  style="margin:0px">
			<input type="hidden" name="numRecordsPerToken" value="t">
			<tr>	
				<td class="headrow" nowrap><b>Maximum number of files <br/>
				    returned in ListRecords:</b></td>
				<td class="formrow">
					<html:text property="numRecordsResults" size="15"/>
					<div><font color="red"><html:errors property="numRecordsResults"/></font></div>
				</td>
			</tr>
			<tr>	
				<td class="headrow" nowrap><b>Maximum number of identifiers<br/>returned in ListIdentifiers:</b></td>
				<td class="formrow">
					<html:text property="numIdentifiersResults" size="15"/>
					<div><font color="red"><html:errors property="numIdentifiersResults"/></font></div>
				</td>
			</tr>
		</table>
	
		<%-- Footer --%>
		<br>
		<table style="margin:0px">
			<tr bgcolor="#ffffff">
				<td colspan=2>
				<html:submit>Save</html:submit>
				<%-- <html:reset>Reset</html:reset> --%>
				<html:cancel>Cancel</html:cancel>
				</td>
			</tr>
		</table>					
	</html:form>		


<%-- Include style/menu templates --%>
<%@ include file="/nav/bottom.jsp" %>
</body>

</html:html>

