<%@ include file="/lib/includes.jspf" %>
<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title" value="Set Status" />
<html>
<head>
	
	<%@ include file="/baseHTMLIncludes.jsp" %>
	<%-- <script language="JavaScript" src="${contextPath}/lib/javascript/pcw/window.js"></script> --%>
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

		function doCancel () {
			// Windows.close("edit_status_modal_win");
			var win = this.parent.Windows.focusedWindow;
			win.close();
		}
		
		function doSubmit(command) {
			document.statusForm.command.value = command;
			document.statusForm.method = "post";
			document.statusForm.submit();
			var win = this.parent.Windows.focusedWindow;
			win.close();
		}
		
		/* scroll window to desired position */
		function editStatusInit () {
			//alert ("initializing");
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
		
		Event.observe (window, "load", editStatusInit);
		
	</script>
	<title><st:pageTitle title="${title}" /></title>
</head>
<body bgcolor="white">
<a name="top"></a>
<%-- <st:pageHeader toolLabel="${title}" />

<st:pageMessages /> --%>

					
<h1 align="center" style="margin:0px">Change Status</h1>

<html:form action="/record_op/status.do" method="Post" onsubmit="return false;">
	<html:hidden property="command" value="" />
	<html:hidden property="modal" value="true" />
	<html:hidden property="recId" value="${statusForm.recId}" />
	<input type="hidden" name="entryKey" value="${statusForm.entryKey}" />
	
<%-- <p>The information entered in this form provides "status" information that
aids in the workflow and management of the record. It is not part of the metadata record.
</p> --%>


<%-- <h3>New Status Entry</h3> --%>
<p>Please <%-- enter your name as <b>Editor</b>, --%> assign a <b>workflow status</b> to your record
(${statusForm.recId}). <br />
Explain the status in the <b>status note</b> field.</p>

<table cellpadding="4" cellspacing="0" border="0" width="95%">
		<tr valign="top">
			<td align="right" nowrap="1"><b>Status:</b></td>
			<td>
				<table width="100%" cellspacing="0" cellspacing="0" border="0">
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
						<td width="20px">&nbsp;</td>
						<td width="320px">
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
				<html:textarea property="statusNote" cols="40" rows="2"/>
			</td>
		</tr>	
		<tr>
			<td>&nbsp;</td>
			<td align="left">
				<input type="button" value="submit" onClick="doSubmit('updateStatus')" />
				<input type="button" value="cancel" onClick="doCancel()" />
			</td>
		</tr>
</table>
 <%-- Status History --%>
<%-- <hr/> --%>
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
