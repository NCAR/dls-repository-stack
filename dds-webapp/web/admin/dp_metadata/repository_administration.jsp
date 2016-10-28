<%@ include file="/admin/AdminTagLibIncludes.jsp" %>
<%@ include file="/baseUrl.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>

<%-- This needs to be saved here since var 'index' is conflicted below --%>
<c:set var="indexLocation" value="${index.indexLocation}"/>

<c:set var="rm" value="${applicationScope.repositoryManager}"/>

<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>
<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>

<c:set var="myBaseUrl" value="${initParam.oaiBaseUrlDisplay}"/>
<c:if test="${!isDeploayedAtDL || empty myBaseUrl}">
	<c:set var="myBaseUrl"><%=((RepositoryManager)getServletContext().getAttribute("repositoryManager")).getProviderBaseUrl(request)%></c:set>
</c:if>

<c:set var="title" value="OAI Data Provider Settings"/>

<html:html>

<head>
<title>${title}</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<link rel='stylesheet' type='text/css' href='<c:url value="/admin/dds_admin_styles.css"/>'>
<style type="text/css">
	/* Override the 'suggested' background color behind the resources's title */
	LI.actions {
		padding-bottom:8px;
		list-style-type:disc;
	}
	LI {
		padding-bottom:3px;
		list-style-type:circle;
	}	
</style>

<%-- Include style/menu templates --%>
<%@ include file="/nav/head.jsp" %>
<style type="text/css">
<!--
.style1 {color: #FF0000}
-->
</style>
</head>

<body>

<%@ include file="/nav/top.jsp" %>

<%-- Include style/menu templates --%>
<%-- <c:import url="/top.jsp?sec=provider" /> --%>
	
	<h1>${title}</h1>

	  
	<%-- ####### Display messages, if present ####### --%>
	<logic:messagesPresent> 		
	<table width="90%" bgcolor="#000000" cellspacing="1" cellpadding="8" style="margin-top:10px">
	  <tr bgcolor="ffffff"> 
		<td>
			<div style="padding:10px">				
				<c:if test="${param.command == 'showIndexingMessages'}">
					<b>Actions:</b>
					
					<ul>
						<html:messages id="msg" property="showIndexMessagingLink"> 
								<li class="actions"><bean:write name="msg"/><a href="data-provider-info.do?command=showIndexingMessages">Check and refresh indexing status messages again</a></li>								
						</html:messages>			
						<li class="actions"><a href="data-provider-info.do">OK</a> (close messages)</li>
					</ul>
				</c:if>
				
				<b>${param.command == 'showIndexingMessages' ? 'Indexing status messages:' : 'Messages'}</b>			
				<ul>
					<html:messages id="msg" property="message"> 
						<li><bean:write name="msg"/></li>									
					</html:messages>
					<html:messages id="msg" property="error"> 
						<li><font color=red>Error: <bean:write name="msg"/></font></li>									
					</html:messages>
					<html:messages id="msg" property="showIndexMessagingLink">
						
						<li>
							<a href="data-provider-info.do?command=showIndexingMessages">Check most recent indexing status</a> for information about the progress of the indexing process. 
						</li>
					</html:messages>		
				</ul>
				
				<c:if test="${param.command != 'showIndexingMessages'}">
					<b>Actions:</b>
					
					<ul>
						<li class="actions"><a href="data-provider-info.do">OK</a> (close messages)</li>
					</ul>
				</c:if>			
			</div>
		</td>
	  </tr>
	</table><br><br>
	</logic:messagesPresent>	
	
	<p> 
		Items in the repository are available to harvest using the <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm">Open Archives Initiative Protocol for Metadata Harvesting</a> (OAI-PMH), 
		allowing the repository to interoperate with other OAI-enabled repository systems.
		All items in the repository may be harvested as a whole or each collection may be harvested individually as
		an OAI <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Set">set</a>.
	</p>
	<p>
		Edit OAI settings using the controls below.
	</p>
 
	<p><a name="repository"></a><h3>Repository information</h3>
	
		Repository information is returned in response to the OAI
		<a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Identify"><code>Identify</code></a> request.
		<a href="${myBaseUrl}?verb=Identify">View the Identify response</a> from the data provider to verify your settings.
	</p>
	
	<p>
  <form method="post" action="update_repository_info.do" style="margin-bottom:8px">
			<input type="submit" value="Edit repository info"/>
				<input type="hidden" name="command" value="updateRepositoryInfo"/>
				Update repository name, administrator's email, repository description or namespace identifier.
				
  </form>
	</p>		

	
	<table class="form" cellpadding="6" cellspacing="1" border="0" style="margin:0px">
		<tr> 
			  <td align="left" class="headrow" style="font-weight:bold;" nowrap>
				Repository name (required):
				<a href="javascript:popupHelp('<c:out value="${pageContext.request.contextPath}"/>/docs','repinfo_repname')" class="helpp">   <img src='<c:out value="${pageContext.request.contextPath}" />/images/help.gif' border=0></a>			  </td>
			  <td class="formrow">
			  	<c:choose>
					<c:when test="${empty rm.repositoryName}">
						<b style="color:grey">No repository name specified</b>
					</c:when>
					<c:otherwise>
						${rm.repositoryName}
					</c:otherwise>
				</c:choose>			  
			  </td>
		</tr>
		<tr>
			  <td align="left" class="headrow" style="font-weight:bold;"  nowrap>
				Repository administrator's e-mail (required):
				<a href="javascript:popupHelp('<c:out value="${pageContext.request.contextPath}"/>/docs','repinfo_adminemail')" class="helpp">   <img src='<c:out value="${pageContext.request.contextPath}" />/images/help.gif' border=0></a>
			  </td>
			  <td class="formrow">
			  	<c:choose>
					<c:when test="${empty rm.adminEmails[0]}">
						<b style="color:grey">No e-mail specified</b>
					</c:when>
					<c:otherwise>
						${rm.adminEmails[0]}
					</c:otherwise>
				</c:choose>
			  </td>
		</tr>
		<tr>
			  <td align="left" class="headrow" style="font-weight:bold;"  nowrap>
				Repository description (optional):
				<a href="javascript:popupHelp('<c:out value="${pageContext.request.contextPath}"/>/docs','repinfo_repdesc')" class="helpp">   <img src='<c:out value="${pageContext.request.contextPath}" />/images/help.gif' border=0></a>
			  </td>
			  <td class="formrow">
			  	<c:choose>
					<c:when test="${empty rm.descriptions[0]}">
						<b style="color:grey">No description specified</b>
					</c:when>
					<c:otherwise>
						<c:out value="${rm.descriptions[0]}" escapeXml="true"/>
					</c:otherwise>
				</c:choose>					  
			  </td>	
		</tr>
		<tr>
			  <td align="left" class="headrow" style="font-weight:bold;"  nowrap>
				Repository namespace identifier (optional):
				<a href="javascript:popupHelp('<c:out value="${pageContext.request.contextPath}"/>/docs','repinfo_namespace')" class="helpp">   <img src='<c:out value="${pageContext.request.contextPath}" />/images/help.gif' border=0></a>
			  </td>
			  <td class="formrow">
			  	<c:choose>
					<c:when test="${empty rm.repositoryIdentifier}">
						<b style="color:grey">No namespace identifier specified</b>
					</c:when>
					<c:otherwise>
						${rm.repositoryIdentifier}
					</c:otherwise>
				</c:choose>				  
			  </td>			  
	  </tr>
	  		<tr>
				<td align="left" class="headrow" style="font-weight:bold;"  nowrap>
				Repository base URL (non-editable):
				<a href="javascript:popupHelp('<c:out value="${pageContext.request.contextPath}"/>/docs','repinfo_baseurl')" class="helpp">   <img src='<c:out value="${pageContext.request.contextPath}" />/images/help.gif' border=0></a>
				</td>
				<td class="formrow">
				${myBaseUrl}			  
				</td>			  
	  </tr>
	</table>
	
	  
	<%-- Turn the data provider on/off (enable/disable) --%>
	<table cellpadding="6" cellspacing="1" border="0" style="margin:0px">       
	  <tr>
		<td>
			<br/>  
			<a name="access"></a><h3>Access to the data provider</h3>
		</td>
	  </tr>	
	  <tr>
		<td>
		
			<logic:equal value="ENABLED" property="providerStatus" name="raf">
				<html:form 	action="/admin/data-provider-info.do" 
					onsubmit="return confirm( 'Are you sure you want to disable access to the data provider?' )" 
					method="GET" style="padding:0px; margin:0px">
					<p>
						Access to the data provider is currently <font color="green"><b>ENABLED</b></font>.
						Records are available for harvesting.					</p>
					<p nobr><html:submit property="statusButton" value="Disable data provider" title="Click to prevent records in the repository from being harvested" />
				      </p>
					<input type="hidden" name="providerStatus" value="DISABLED">
					<input type="hidden" name="command" value="changeProviderStatus">
				</html:form>
			</logic:equal>
			<logic:equal value="DISABLED" property="providerStatus" name="raf">		
				<html:form 	action="/admin/data-provider-info.do" 
					onsubmit="return confirm( 'Are you sure you want to enable access to the data provider?' )" 
					method="GET">
					<p>
						Access to the data provider is currently <font color="red"><b>DISABLED</b></font>.
						Records are not available for harvesting.
					</p>
					<p nobr><html:submit property="statusButton" value="Enable data provider" title="Click to allow records in the repository to be harvested"/> 
						</p>
						<input type="hidden" name="providerStatus" value="ENABLED">
					<input type="hidden" name="command" value="changeProviderStatus">
				</html:form>
			</logic:equal>		
		
	   </td>
	  </tr>
  </table>	  
	  
	
	<%-- Num records per resumption token --%>
	<a name="response"></a><h3 style="margin-top:25px">OAI response length</h3>	
	<p> 
		The following values define the maximum length of the 
		data provider's response to the OAI 
		<a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords"><code>ListRecords</code></a>
		and
		<a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers"><code>ListIdentifiers</code></a>
		requests. 
		  
		Reduce the length of these responses if a large number of sets have been defined (which requires more processing), 
		system memory is limited or if records are large.			
		Smaller numbers mean that harvesters must make more requests to harvest the entire metadata repository. 
		Larger numbers mean fewer requests are necessary but require greater system resources by both the data provider and the harvester.
		  
	    <html:form 	action="/admin/data-provider-info.do" method="POST" style="margin-bottom:8px">			
			<input type="hidden" name="numRecordsPerToken" value="t">
			<html:submit property="editNumResults" value="Edit response length" />&nbsp;&nbsp; Change the number of returns in the ListRecords and ListIdentifiers
			responses.
		</html:form>
	</p>	
	
	<table class="form" width="40%" border="0" cellpadding="6" cellspacing="1" style="margin:0px">
		<tr>
			<td class="headrow"><b>
			<div align="center">OAI Request</div>
			</b></td>
			<td class="headrow"><b><div align="center">Maximum<br/>
			Response Length</div>
			</b></td>
		</tr>
		<tr nowrap>
			<td class="formrow">
			  <div align="center"><a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords"><code>ListRecords</code></a> </div></td>
			<td class="formrow"><div align="center">${rm.numRecordsResults} records</div></td>		
		</tr>
		<tr nowrap>
			<td class="formrow">
			  <div align="center"><a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers"><code>ListIdentifiers</code></a></div></td>
			<td class="formrow"><div align="center">${rm.numIdentifiersResults} identifiers</div></td>		
		</tr>		
	</table>

	 <br/><br/> 
	 
	 
<table height="100">
  <tr> 
    <td>&nbsp;</td>
  </tr>
</table>

<%-- Include style/menu templates --%>
<%@ include file="/nav/bottom.jsp" %>
</body>
</html:html>



