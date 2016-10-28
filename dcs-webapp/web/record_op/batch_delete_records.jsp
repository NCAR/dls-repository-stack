<%@ include file="/JSTLTagLibIncludes.jsp" %>
<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>
<resp:setHeader name="Cache-Control">cache</resp:setHeader>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Batch Delete Records</c:set>


<html:html>
<head>
<title><st:pageTitle title="${title}" /></title>

<%@ include file="/baseHTMLIncludes.jsp" %>

<script>
	// Override the method below to set the focus to the appropriate field.
	function sf(){}
		
	function doSubmit( form ) {
		if (confirm ("Are you sure you want to delete ${bof.recordList.size} records?")) {
			form.command.value = "delete";
			form.submit();
			}
	}
	
 	function doExit (form) {
		form.op.value = "exit";
		form.submit();
		}
		
	function toggleVisibility( elementID ) {
		var objElement = document.getElementById( elementID );
		if ( objElement != null ) {
			if ( objElement.style.display == '' )
				objElement.style.display = 'none';
			else
				objElement.style.display = '';
		}
	}
	
</script>
</head>
<body>

<st:pageHeader toolLabel="${title}" />

<st:pageMessages okPath="" />

<c:choose>
	<c:when test="${bof.recordList.isEmpty}">
		<st:backToSearchButton label=" Back to Search Page " sessionBean="${sessionBean}" />
	</c:when>
	
	<c:otherwise>
	
		<html:form action="/record_op/batch" method="GET">
			<html:hidden property="op" value="batchDelete" />
			<html:hidden property="command" value="" />
			
			<p><b>Note:</b> this operation cannot be undone!</p>
			<blockquote>
				<input type="button" value="Delete Records" 
						 onclick="doSubmit(this.form)" />
				<input type="button" value="Cancel" title="Exit without deleting records" 
						 onclick="doExit(this.form)" />
			</blockquote>
	
	
			<%@ include file="/browse/searching/search_user_selections.jsp" %>
			
			<h3>Records to Delete (${bof.recordList.size})</h3>

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
