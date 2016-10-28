<%@ include file="/JSTLTagLibIncludes.jsp" %>
<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>
<resp:setHeader name="Cache-Control">cache</resp:setHeader>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Batch Copy and Move Records</c:set>
<c:set var="maxRecordsToList" value="10" />
<html:html>
<head>
<title><st:pageTitle title="${title}" /></title>

<%@ include file="/baseHTMLIncludes.jsp" %>

<script>
	// Override the method below to set the focus to the appropriate field.
	function sf(){}
		
	function doSubmit( form ) {
		if (form.collection.value == "") {
			alert ("please select a destination collection");
			return false;
			}
		form.command.value = "copyMove";
		form.submit();
	}
	
 	function doExit (form) {
		form.op.value = "exit";
		form.submit();
		}
		
	function toggleVisibility( elementID ) {
		$( elementID ).toggle();
	}
	
</script>
</head>
<body>

<st:pageHeader toolLabel="${title}" />

<st:pageMessages okPath="" />

<c:choose>
	<c:when test="${bof.recordList.isEmpty}">
		<st:backToSearchButton label="  Back to Search  " sessionBean="${sessionBean}" />
		<input type="button" class="record-action-button"
				value=" to ${bof.dcsSetInfo.name} collection" 
				onclick="window.location='${contextPath}/browse/query.do?q=&scs=0${bof.dcsSetInfo.setSpec}'" />
	</c:when>
	
	<c:otherwise>
	
		<html:form action="/record_op/batch" method="GET" styleId="batch-move-form">
			<html:hidden property="op" value="batchCopyMove" />
			<html:hidden property="command" value="" />
			<html:hidden property="scs" value="${param.scs}" />
			<table>
				<tr valign="bottom">
					<td align="center">
						<div class="input-label">Select destination</div>
						<html:select property="collection">
						<option value="" selected="true">-- Destination Collection --</option>
							<c:forEach var="set" items="${bof.sets}" varStatus="i">
								<c:if test="${set.format == bof.formatOfRecords &&
															set.setSpec != param.scs}">
									<option value="${set.setSpec}">${set.name}</option>
								</c:if>
							</c:forEach> 
						</html:select>
					</td>
					<td width="50">&nbsp;</td>
					<td >
						<input type="button" value="Copy and Move" o
								 onclick="doSubmit(this.form)" />
						<input type="button" value="Cancel" title="Exit without moving records" 
								 onclick="doExit(this.form)" />
					</td>
				</tr>
			</table>
	
			<%@ include file="/browse/searching/search_user_selections.jsp" %>
				
			<h3>Records to Copy and Move (${bof.recordList.size})</h3>

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
