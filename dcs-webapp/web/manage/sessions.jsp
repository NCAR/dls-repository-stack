<%-- $Id: sessions.jsp,v 1.6 2011/02/16 17:32:34 ostwald Exp $ --%>
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<%@ page import="edu.ucar.dls.schemedit.SessionBean" %>

<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Sessions</c:set>

<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<%@ include file="/admin/adminHTMLIncludes.jsp" %>
<script type="text/javascript">

	function toggleVisibility( elementID ) {
		$( elementID ).toggle();
	}

	function doUnlockRecords (form) {
		form = document.forms.sessionsForm;
		form.command.value="unlockRecords";
		form.submit();
	}
	
  function toggleAll (id) {
		$(id).select('input[type="checkbox"]').each (function (checkbox) {
			checkbox.checked = !checkbox.checked;
		});
	}
	
</script>

<c:set var="indentAmount" value="100" />

</head>

<%-- <% session.invalidate(); %> --%>

<body text="#000000" bgcolor="#ffffff">

<st:pageHeader toolLabel="${title}" currentTool="manage" />

<table width="100%" border="0" align="center">
  <tr> 
    <td> 
			<st:pageMessages okPath="${contextPath}/manage/sessions.do" />
    </td>
  </tr>
  
  <tr>
  	<td>

	<h3>Locked Records</h3>
			
	<p>A record is locked to prevent two users from editing the same record.
	When users quit their browser before releasing their locked record, the record remains locked until 
	the user's session times out. For example, if someone is editing a record and quits their browser without first
	exiting the Metadata Editor, the record they is locked and unaccessible to other users.</p>
	
	<%-- <p>There are currently ${fn:length(sessionsForm.lockedRecords)} locked records.</p> --%>
	<p>As of 
		<b><dt:format pattern="h:mm:ss a"><dt:currentTime/></dt:format></b>
		(<dt:format pattern="yyyy-MM-dd"><dt:currentTime/></dt:format>)
		there are 
		<b>${fn:length(sessionsForm.lockedRecords)}</b> locked records.</p>
	
	<h3>Sessions</h3>

	<p>The table below lists the active sessions and locked records (your session is highlighted in yellow).
	To unlock records, click the checkbox of the record and then click <i>Unlock selected records</i>.
	</p>
	
	<p>
	<input type="button" value="Refresh" 
		onclick="location='${contextPath}/manage/sessions.do?showAnonymousSessions=${sessionsForm.showAnonymousSessions}';" />
	Update the sessions table to reflect changes that have occurred since it was loaded.</p>
	
	<%-- provide a choice to see "user sessions only if auth is enabled" --%>
	<c:if test="${authenticationEnabled}">
		<c:choose>
			<c:when test="${not sessionsForm.showAnonymousSessions}" >
				<p>
				<input type="button" value="All sessions" 
					onclick="location='${contextPath}/manage/sessions.do?showAnonymousSessions=true';" />
				Show all sessions whether or not they are associated with a known user.</p>
			</c:when>
			<c:otherwise>		<p>
				<input type="button" value="User sessions only" 
					onclick="location='${contextPath}/manage/sessions.do?showAnonymousSessions=false';" />
				Show only sessions for which a user is known.</p>
			</c:otherwise>
		</c:choose>
	</c:if>
	
		<html:form action="/manage/sessions" method="GET">
			<html:hidden property="command" value="" />
			<html:hidden property="showAnonymousSessions" value="${sessionsForm.showAnonymousSessions}" />
			<table cellspacing="1" cellpadding="2" width="100%" border="0" bgcolor="#666666">
					<tr bgcolor="#CCCEE6" align="center">
						<td>&nbsp;</td>						
						<th>User login</th>
						<th>Locked Records</th>
						<th>Session Age</th>
						<th>Idle Time</th>
						<th>Time Left</th>

						<th>Session IP</th>
						<th>Session ID</th>
					</tr>
					<c:forEach var="sb" items="${sessionsForm.sessionBeans}" varStatus="i">
					
					<c:if test="${sessionsForm.showAnonymousSessions or not empty sb.user}">
					
					<tr bgcolor="${sb.id == sessionBean.id ? '#ffffcc' : '#E8ECF4'}" align="center">
						<td><b>Session ${i.count}</b></td>
						<td>
							<div class="session-data">
								<c:choose>
									<c:when test="${not empty sb.user}">${sb.user.username}</c:when>
									<c:otherwise><i>unknown</i></c:otherwise>
								</c:choose>
							</div>
						</td>
						
						<td>
							<div class="session-data">
								<c:choose>
									<c:when test="${not empty sb.lockedRecords}">
									<nobr>
										<a 	href="javascript:toggleVisibility('${sb.id}_id');" 
											title="Click to show/hide" 
											class="vocabHeading"><img src='../images/btnExpand.gif' 
											alt="Click to show/hide" border="0" hspace="5" width="11" height="11" />${fn:length(sb.lockedRecords)}</a>
									</nobr>
									<c:if test="${fn:length(sb.lockedRecords) > 3}">
										<div><a href="javascript:toggleAll('${sb.id}_id')">toggle all</a></div>
									</c:if>									
									</c:when>
									<c:otherwise><i>none</i></c:otherwise>
								</c:choose>
							</div>
						</td>
						<td><div class="session-data">${sb.timeSinceCreation}</div></td>
						<td><div class="session-data">${sb.timeSinceLastAccessed}</div></td>
						<td><div class="session-data">${sb.inactiveIntervalRemaining}</div></td>

						<td><div class="session-data">${sb.ip}</div></td>
						<td><div class="session-data">${sb.id}</div></td>
					</tr>
					<c:if test="${not empty sb.lockedRecords}">
						<tr bgcolor="white" id="${sb.id}_id" style=" width:100%;display:">
							<td colspan="8">
								<div >
									<div style="margin-left:${indentAmount}px">
										<c:forEach var="recId" items="${sb.lockedRecords}">
											<div style="margin:3px 0px 0px 0px;">
												<input type="checkbox" name="ids" id="${recId}_id" value="${recId}" /><label
												for="${recId}_id">${recId}</label>
											</div>
										</c:forEach>
									</div>
								</div>
							</td>
						</tr>
					</c:if>     <%-- not empty sb.lockedRecords --%>

					</c:if>   <%-- showAnonymousUsers or not empty sb.user --%>
					
				</c:forEach>
			</table>
			<c:if test="${not empty sessionsForm.lockedRecords}">
				<div style="margin:5px 0px 0px ${indentAmount}px">
					<input type="button" value="Unlock selected records" 
						onclick="doUnlockRecords ()"/>
				</div>
			</c:if>
			</html:form>
		</td>
	</tr>
  
</table>

<%-- Debugging info --%>

<%-- <c:if test="${not empty sessionsForm.lockedRecords}">
	<h3>Debugging: All locked records</h3>
	<c:forEach var="rec" items="${sessionsForm.lockedRecords}">
		RecId: ${rec.key}: ${rec.value}<br />
	</c:forEach>
</c:if> --%>

<%-- <h3>SessionScope attributes</h3>

	<c:forEach var="attName" items="${sessionBean.sessionAttributeNames}">
	  <li class="foo">${attName}</li>
   </c:forEach>

<h3>Request headers</h3>

	<c:forEach var="headerName" items="${header}">
	  <li class="foo">${headerName}</li>
   </c:forEach>
 --%>
			
</body>
</html:html>

