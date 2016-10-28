<%@ include file="/lib/includes.jspf" %>
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title" value="Change Status" />
<html>
<head>
	
	<%@ include file="/baseHTMLIncludes.jsp" %>
	
	<style>
		#statusDescription {
			padding:3px;
			font-style:italic;
		}
		.history-status-note {
			padding:2px 0px 0px 15px;
		}
	</style>
	<script language="JavaScript">
		var selectOptions = $H({});
		
		<c:forEach var="statusFlag" items="${statusForm.statusFlags}">
			selectOptions["${f:jsEncode(statusFlag.value)}"] = {
				value : "${f:jsEncode(statusFlag.label)}",
				description : "${f:jsEncode(statusFlag.description)}"
			};
		</c:forEach>
		
		
		function showStatusDescription () {
			var selected = $F('status-select')
			$('statusDescription').update (selectOptions[selected].description);
		}
		
		function statusOptionsInit () {
			$$(".status-option").each (function (option) {
				option.observe ("mouseover", function (evnt) {
					$("statusDescription").update (selectOptions[option.value].description);
				});
			});
		}
		
		
		function doSubmit(command) {
			document.statusForm.command.value = command;
			document.statusForm.method = "post";
			document.statusForm.submit();
			// return true;
		}
		
		/* scroll window to desired position */
		function pageInit () {
			var hash = '${statusForm.hash}';
			if (hash != null && hash != "") window.location.hash=hash;
						
			showStatusDescription();
			statusOptionsInit();
			$('status-select').observe ("change", function (evnt) {
				showStatusDescription();
			});
			$('status-select').observe ("blur", function (evnt) {
				showStatusDescription();
			});
			return;
		}
		
		Event.observe (window, "load", pageInit);
		
	</script>
	<title><st:pageTitle title="${title}" /></title>
</head>
<body bgcolor="white" onload="pageInit('${statusForm.hash}')">
<a name="top"></a>
<st:pageHeader toolLabel="${title}" />

<st:pageMessages />

					
<html:form action="/record_op/status.do" method="Post" onsubmit="return false;">
	<html:hidden property="command" value="" />
	<html:hidden property="recId" value="${statusForm.recId}" />
	
<%-- <p>The information entered in this form provides "status" information that
aids in the workflow and management of the record. It is not part of the metadata record.
</p> --%>


<c:set var="leftColWidth" value="100" />

<%-- <h3>New Status Entry</h3> --%>
<p>Please <%-- enter your name as <b>Editor</b>, --%> assign a <b>workflow status</b> to the record
(${statusForm.recId}). Explain the status in the <b>status note</b> field.</p>

<table cellpadding="4" cellspacing="0" border="0" width="95%">
		<tr valign="top">
			<td align="right" nowrap="1"><b>Status:</b></td>
			<td>
				<table cellspacing="0" cellspacing="0" border="0">
					<tr valign="top">
						<td>
							<st:fieldMessage propertyId="status"/>
							<select name="status"  id="status-select">
							<c:forEach items="${statusForm.statusFlags}" var="statusFlag">
								<option value="${statusFlag.value}" class="status-option"
									<c:if test="${statusFlag.value == statusForm.status}">SELECTED</c:if> >
									${statusFlag.label}
								</option>
							</c:forEach>
							</select>
						</td>
						<td width="30px">&nbsp;</td>
						<td>
							<div id="statusDescription"></div>
						</td>
					</tr>
				</table>
	
			</td>
		</tr>
		<tr valign="top">
			<td align="right" nowrap="1"><b>Status note:</b></td>
			<td>
				<st:fieldMessage propertyId="statusNote"/>
				<html:textarea property="statusNote" cols="60" rows="2"/>
			</td>
		</tr>	
		<tr>
			<td>&nbsp;</td>
			<td align="left">
				<input type="button" value="submit" onClick="doSubmit('updateStatus')" />
				<input type="button" value="cancel" onClick="window.history.back()" />
			</td>
		</tr>

</table>
<h3>Status History</h3>
<hr color="#999999"/>
<c:choose>
	<c:when test="${empty statusForm.dcsDataRecord.entryList}">
				<div class="searchResultValues"><em>no status history</em></div>
	</c:when>
			
	<c:otherwise>
		<c:set var="entryList" value="${statusForm.dcsDataRecord.entryList}" />

		<c:forEach items="${statusForm.dcsDataRecord.entryList}" var="statusEntry" varStatus="i">

				<div>
						<fmt:formatDate value="${statusEntry.date}" 
							pattern="yyyy-MM-dd h:mm a" /> - 
							
						<b>${sf:getStatusLabel(statusEntry.status, statusForm.collection, sessionBean)}</b>
						
						<c:if test="${not empty statusEntry.editor && statusEntry.editor != 'Unknown'}">
							(by ${sf:getFullName (statusEntry.editor, userManager)})
						</c:if>
					
						<c:if test="${not empty statusEntry.statusNote}">
							<div class="history-status-note">${statusEntry.statusNote}</div>
						</c:if>
				</div>
				
				<c:if test="${fn:length(entryList) > 1 && not i.last}"><hr color="#999999"/></c:if>
				
		</c:forEach>
	</c:otherwise>
</c:choose>
</html:form>
</body>
</html>
