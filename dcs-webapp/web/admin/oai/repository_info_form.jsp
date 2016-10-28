<%@ include file="/JSTLTagLibIncludes.jsp" %>
<c:set var="rm" value="${applicationScope.repositoryManager}"/>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">
	Repository Information
</c:set>

<html:html>
  <head>
    <title><st:pageTitle title="${title}" /></title>
    <html:base />
	<%@ include file="/baseHTMLIncludes.jsp" %>
	<script language="JavaScript" src="${contextPath}/admin/oai/oai_script.js"></script>
	
  <link rel="stylesheet" type="text/css" href='${contextPath}/admin/oai/oai_styles.css'>
  </head>

  <body>
  
	<st:pageHeader toolLabel="${title}" currentTool="services" />
	
	<st:breadcrumbs>
		Services                                            
		<st:breadcrumbArrow />
		<a  href="${contextPath}/admin/data-provider-info.do">OAI Services</a>
		<st:breadcrumbArrow />
		<span class="current">${title}</span>
	</st:breadcrumbs>
  	
	<noscript>
		<p>Note: This page uses JavaScript for certain features. 
			Please enable JavaScript in your
			browser to take advantage of these features.</p>
	</noscript> 
	
	<logic:messagesPresent>
		<c:set var="errorMsg">There was an error. Please correct the problem below:</c:set>	
		<p><font color="red"><b>${errorMsg}</b></font></p>
	</logic:messagesPresent>
			
	
  	<html:form action="admin/repository_info-validate" method="POST" focus="repositoryName">
	<table id="form" cellpadding="6" cellspacing="1" border="0">
		<tr id="headrow">
			<td>
				<div><b>Repository information</b></div>
			</td>
		</tr>
		<p>
		Complete the required fields of repository name and administrator's email address.
		<br>
		<tr id="formrow">
			<td>		
				<div><b>Repository name:</b> 
					<a href="javascript:popupHelp('<c:out value="${contextPath}/admin/oai"/>','repinfo_repname')" 
						 class="helpp">   
						 <img src='<c:out value="${pageContext.request.contextPath}" />/images/help.gif' border=0></a>
				</div>
				<div><html:text property="repositoryName" size="80" maxlength="80" /></div>
				<font color="red"><html:errors property="repositoryName"/></font>
			</td>
		</tr>
		<tr id="formrow">
			<td>
				<div><b>Administrator's e-mail address:</b> 
					<a href="javascript:popupHelp('<c:out value="${contextPath}/admin/oai"/>','repinfo_adminemail')" 
						 class="helpp">   
						 <img src='<c:out value="${pageContext.request.contextPath}" />/images/help.gif' border=0></a>
					</div>
				<div><html:text property="adminEmail" size="35"/></div>
				<font color="red"><html:errors property="adminEmail"/></font>
			</td>
		</tr>		
		<tr id="formrow">
			<td>
				<div><b>Repository description (optional):</b> 
					<a href="javascript:popupHelp('<c:out value="${contextPath}/admin/oai"/>','repinfo_repdesc')" 
						 class="helpp">   
						 <img src='<c:out value="${pageContext.request.contextPath}" />/images/help.gif' border=0></a>
					</div>
				<div><b><html:textarea property="repositoryDescription" cols="60" rows="3" /></b></div>
				<font color="red"><html:errors property="repositoryDescription"/></font>
			</td> 
		</tr>
		<tr id="formrow">
			<td>
				<div><b>Namespace identifier (optional):</b> 
					<a href="javascript:popupHelp('<c:out value="${contextPath}/admin/oai"/>','repinfo_namespace')"
						 class="helpp">   
						 <img src='<c:out value="${pageContext.request.contextPath}" />/images/help.gif' border=0></a>
					</div>
				<div><b><html:text property="namespaceIdentifier" size="35"/></b></div>
				<font color="red"><html:errors property="namespaceIdentifier"/></font>
			</td>
		</tr>
		
	</table>
				
	<table cellpadding="6" cellspacing="1" border="0">	
		<tr>
			<td>	
				<c:choose>
					<c:when test="${not empty param.edit}">
						<input type="hidden" name="edit" value="${param.edit}">
					</c:when>
					<c:otherwise>
						
					</c:otherwise>
				</c:choose>
				
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
