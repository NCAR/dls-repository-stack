<%-- $Id: session_info.jsp,v 1.3 2008/10/14 17:41:03 ostwald Exp $ --%>
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
		form = document.forms.daf;
		form.command.value="unlockRecords";
		form.submit();
	}
</script>

<c:set var="indentAmount" value="100" />

</head>

<%-- <% session.invalidate(); %> --%>

<body text="#000000" bgcolor="#ffffff">

<st:pageHeader toolLabel="${title}" currentTool="settings" />

<table width="100%" border="0" align="center">
  <tr> 
    <td> 
  
		<st:pageMessages okPath="admin.do?page=sessions" />
		
    </td>
  </tr>
  
  <tr>
  	<td>

	<h3>Locked Records</h3>
			
	<p>Records are locked by the DCS to prevent two users from changing them simultaneously.
	When users quit their browser before releasing their locked record(s), the record remain locked until 
	the user's session times out. For example, if someone were editing a record and quit their browser without first
	exiting the Metadata Editor, the record they were editing is locked and unaccessible to other users.</p>
	
	<%-- <p>There are currently ${fn:length(daf.lockedRecords)} locked records.</p> --%>
	<p>As of 
		<b><dt:format pattern="h:mm:ss a"><dt:currentTime/></dt:format></b>
		(<dt:format pattern="yyyy-MM-dd"><dt:currentTime/></dt:format>)
		there are 
		<b>${fn:length(daf.lockedRecords)}</b> locked records.</p>
	
	<h3>Sessions &nbsp;&nbsp; <input type="button" value="Refresh" onclick="location='admin.do?page=sessions';" /></h3>
	<p>The table below lists the active sessions (your session is highlighted in yellow). Use this table to unlock records
	(using caution not to unlock a record someone is editing!).</p>
		<html:form action="/admin/admin" method="GET">
			<html:hidden property="command" value="" />
			<table cellspacing="1" cellpadding="2" width="100%" border="0" bgcolor="#666666">
					<tr bgcolor="#CCCEE6" align="center">
						<td>&nbsp;</td>						
						<th>User</th>
						<th>Locked Records</th>
						<th>Session age</th>
						<th>Idle time</th>
						<th>Time left</th>

						<th>Session IP</th>
						<th>Session ID</th>
					</tr>
					<c:forEach var="sb" items="${daf.sessionBeans}" varStatus="i">
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
					</c:if>

				</c:forEach>
			</table>
			<c:if test="${not empty daf.lockedRecords}">
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

<%-- <c:if test="${not empty daf.lockedRecords}">
	<h3>Debugging: All locked records</h3>
	<c:forEach var="rec" items="${daf.lockedRecords}">
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

