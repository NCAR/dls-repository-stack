<%@ include file="/JSTLTagLibIncludes.jsp" %>
<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>
<resp:setHeader name="Cache-Control">cache</resp:setHeader>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Batch Status Change</c:set>
<html:html>
<head>
	<title><st:pageTitle title="${title}" /></title>

	<%@ include file="/baseHTMLIncludes.jsp" %>
	
	<style>
		#statusDescription {
			padding:3px;
			font-style:italic;
		}
	</style>
	
	<script language="JavaScript">
		var selectOptions = $H({});
		
		<c:forEach var="statusFlag" items="${bof.statusFlags}">
			selectOptions["${f:jsEncode(statusFlag.value)}"] = $H ({
				value : "${f:jsEncode(statusFlag.label)}",
				description : "${f:jsEncode(statusFlag.description)}"
			});
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
			
		function pageInit() {
			showStatusDescription();
			statusOptionsInit();
			$('status-select').observe ("change", function (evnt) {
				showStatusDescription();
			});
			$('status-select').observe ("blur", function (evnt) {
				showStatusDescription();
			});
		}
		
		// Override the method below to set the focus to the appropriate field.
		function sf(){}
		
		function doSubmit(form) {
			form.method = "post";
			form.command.value = "status";
			form.submit();
		}
		
		function doExit (form) {
			form.op.value = "exit";
			form.submit();
		}

		Event.observe (window, "load", pageInit);
			
	</script>

</head>
<body>

<st:pageHeader toolLabel="${title}" />

<st:pageMessages okPath="" />

<c:choose>
	<c:when test="${bof.recordList.isEmpty}">
		<st:backToSearchButton label=" Back to Search " sessionBean="${sessionBean}" />
	</c:when>
	
	<c:otherwise>
	
		<html:form action="/record_op/batch" method="POST">
			<html:hidden property="op" value="batchStatus" />
			<html:hidden property="command" value="" />
			
			<p>The information entered in this form provides "workflow status" information that
			aids in the management of the record within the DCS. 
			It is not part of the metadata record.</p>
			
			<p>Please assign a <b>Status</b> to the record you are editing, 
			and explain the status in the <b>Status note</b> field.</p>
	
			<table cellpadding="4" cellspacing="0" border="0">

				<tr valign="top">
					<td align="right" nowrap="1"><b>Status:</b></td>
					<td>
						<table cellspacing="0" cellspacing="0" border="0">
							<tr valign="top">
								<td>
									<st:fieldMessage propertyId="status"/>
									<select name="status" id="status-select">
									<c:forEach items="${bof.statusFlags}" var="statusFlag" varStatus="i">
										<option value="${statusFlag.value}" class="status-option"
											<c:if test="${statusFlag.value == bof.status}">SELECTED</c:if> >
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
						<html:textarea property="statusNote" cols="60" rows="4"/>
					</td>
				</tr>
				
			
				<tr>
					<td>&nbsp;</td>
					<td align="left">
						<input type="button" value="submit"  title="Assign status info to displayed records" 
							 onclick="doSubmit(this.form)"/>
						<input type="button" value="Cancel" title="Exit without updating record statuses" 
							 onclick="doExit(this.form)" />
					</td>
				</tr>
				<tr><td colspan="2"><hr></td></tr>
			</table>
	
			<%@ include file="/browse/searching/search_user_selections.jsp" %>
			
			<h3>Records to Update (${bof.recordList.size})</h3>

			<%@ include file="batch_rec_list.jspf" %>
				
		</html:form>
	</c:otherwise>
</c:choose>

<c:if test="${not sessionBean.failedBatchLocks.isEmpty}">
	<c:set var="batchRecordList" value="${sessionBean.failedBatchLocks}" />
	<h3>Records that are currently unavailable</h3>
	<%@ include file="batch_rec_list.jspf" %>
</c:if>

</body>
</html:html>
