<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="title" value="Edit OAI Response Length"/>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>

<%-- Create a scripting variable named "index" from the parameter "currentIndex" --%>
<bean:parameter name="currentIndex" id="index" value="0"/>

<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<%-- Include style/menu templates --%>
<%@ include file="/baseHTMLIncludes.jsp" %>
<link rel="stylesheet" type="text/css" href='${contextPath}/admin/oai/oai_styles.css'>

<script type="text/javascript">
	function sf(){document.raf.numRecordsResults.focus();}
</script>
</head>

<body onLoad=sf()>
	<st:pageHeader toolLabel="${title}" currentTool="services" />
	
	<st:breadcrumbs>
		Services                                            
		<st:breadcrumbArrow />
		<a  href="${contextPath}/admin/data-provider-info.do">OAI Services</a>
		<st:breadcrumbArrow />
		<span class="current">${title}</span>
	</st:breadcrumbs>

	<html:form action="/admin/data-provider-info.do" method="POST">

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
		<table bgcolor="#666666" cellpadding="6" cellspacing="1" border="0">
			<input type="hidden" name="numRecordsPerToken" value="t">
			<tr>	
				<td id="headrow" nowrap><b>Maximum number of files <br/>
				    returned in ListRecords:</b></td>
				<td id="formrow">
					<html:text property="numRecordsResults" size="15"/>
					<div><font color="red"><html:errors property="numRecordsResults"/></font></div>
				</td>
			</tr>
			<tr>	
				<td id="headrow" nowrap><b>Maximum number of identifiers<br/>returned in ListIdentifiers:</b></td>
				<td id="formrow">
					<html:text property="numIdentifiersResults" size="15"/>
					<div><font color="red"><html:errors property="numIdentifiersResults"/></font></div>
				</td>
			</tr>
		</table>
	
		<%-- Footer --%>
		<br>
		<table>			
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
<%-- <%@ include file="../../bottom.jsp" %> --%>
</body>

</html:html>
