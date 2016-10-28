<%@ include file="/JSTLTagLibIncludes.jsp" %>
<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>
<resp:setHeader name="Cache-Control">cache</resp:setHeader>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<html:html>
<head>
	<title><st:pageTitle title="Copy and Move Record" /></title>
	<%@ include file="/baseHTMLIncludes.jsp" %>
	
	<script>
		// Override the method below to set the focus to the appropriate field.
		function sf(){}
			
		function doSubmit( form ) {
			if (form.collection.value == "") {
				alert ("please select a destination collection");
				return false;
				}
			form.command.value = "copymove";
			form.submit();
		}
		
		function doExit () {
			window.location = '${contextPath}/record_op/single.do?command=copymove&recId=${rof.recId}&exit=true'
			}
			
		function toggleVisibility( elementID ) {
			$(elementID).toggle();
		}
		
	</script>
</head>
<body>

<st:pageHeader toolLabel="Copy and Move Record" />

<st:breadcrumbs>
	${rof.recId}
	<st:breadcrumbArrow />
	<span class="current">Copy and Move</span>
</st:breadcrumbs>

<st:pageMessages okPath="" />

<c:if test="${empty rof.docReader}">
	<input type="button" value=" OK " onclick="doExit()" />
		<input type="button" value=" to ${rof.dcsSetInfo.name} collection" 
			onclick="window.location='${contextPath}/browse/query.do?q=&scs=0${rof.dcsSetInfo.setSpec}'" />
</c:if>

<c:if test="${not empty rof.docReader}">

	<html:form action="/record_op/single" method="GET">
		<html:hidden property="recId" value="${rof.recId}" />
		<%-- <html:hidden property="editRec" value="${rof.editRec}" /> --%>
		<html:hidden property="command" value="" />
		<table>
			<tr valign="bottom">
				<td align="center">
					<div class="input-label">Select destination</div>
					<html:select property="collection">
					<option value="" selected="true">-- Destination Collection --</option>
						<c:forEach var="set" items="${rof.sets}" varStatus="i">
							<c:if test="${set.format == rof.dcsSetInfo.format && set.setSpec != rof.dcsSetInfo.setSpec}">
								<option value="${set.setSpec}">${set.name}</option>
							</c:if>
						</c:forEach>
					</html:select>
				</td>
				<td width="50">&nbsp;</td>
				<td >
					<input type="button" value="Copy and Move" 
						   onclick="doSubmit(this.form)" />
					<input type="button" value="Cancel" title="Exit without moving record" 
						   onclick="doExit()" />
				</td>
			</tr>
		</table>
	</html:form>

	<h3>Record to Copy and Move</h3>
	
	<c:choose>
		<c:when test="${rof.docReader.readerType == 'DleseCollectionDocReader'}">
			<c:if test="${not empty rof.docReader.shortTitle}">
				<div class="searchResultValues">
					<em>Title:</em> &nbsp; ${rof.docReader.shortTitle}
				</div>
			</c:if>
		</c:when>
		<c:otherwise>
			<c:catch var="err">
				<c:if test="${not empty rof.docReader.title}">
					<div class="searchResultValues">
						<em>Title:</em> &nbsp; ${rof.docReader.title}
					</div>
				</c:if>
			</c:catch>
		</c:otherwise>
	</c:choose>

	<div class="searchResultValues">
		<em>ID:</em> &nbsp; ${rof.recId}
	</div>

	<div class="searchResultValues">
		<em>Current collection:</em> &nbsp; ${rof.dcsSetInfo.name} (${rof.dcsSetInfo.format})
	</div>
	
	<%-- workFlowStatusBox --%>
	<c:set var="docMap" value="${rof.resultDoc.docMap}" />
	<c:if test="${not empty docMap['dcsstatus']}">
		<div style="font-weight:bold;margin-top:3px">Workflow Status</div>
		<div style="margin-left:15px">
			<div class="searchResultValues">
				<em>Last Touch:</em> &nbsp;
				<fmt:formatDate value="${f:luceneDate(docMap['dcslastTouchDate'])}" 
					pattern="yyyy-MM-dd h:mm a" />
			</div>
			
			<div class="searchResultValues">
				<em>Last Editor:</em> &nbsp;${docMap['dcslastEditor']}
			</div>
			<div class="searchResultValues">
				<em>Status:</em> &nbsp;${docMap['dcsstatus']}
			</div>
			<div class="searchResultValues">
				<em>Status Note:</em> &nbsp;${docMap['dcsstatusNote']}
			</div>
		</div>
	
	</c:if>
	
</c:if>

</body>
</html:html>
