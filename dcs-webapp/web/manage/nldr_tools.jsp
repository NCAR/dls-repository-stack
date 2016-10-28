<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">NLDR Tools</c:set>
<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>

<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<%@ include file="/manage/manageHTMLIncludes.jsp" %>
<script type="text/javascript" src="${contextPath}/lib/progress-indicator/progress-script.js" ></script>

<script type="text/javascript"> 

function doClosePageMsg () {
	$('page-message').hide();
}

function doUpdateAffected (collection) {
	var params  = {
		command : 'UpdateVocabAffectedRecords',
		collection : collection
	}
	
	var url = "${contextPath}/manage/nldr.do?" + Object.toQueryString(params);
	// log (url);
	window.location = url;
}

function pageInit() {
	if ("${nldrToolsForm.isUpdating}" == "true") {
	
		var finalize = function () {
			window.location = "${contextPath}/manage/nldr.do?command=finalizeUpdate";
			// alert ("done");
		}
		var progressUrl = "${contextPath}/manage/nldr.do?command=progress";
		
		new PeriodicalExecuter(function(pe) {
			updateProgress (pe, progressUrl, finalize);
		}, 3); 
	}
}

document.observe ("dom:loaded", pageInit);

</script>
</head>

<body text="#000000" bgcolor="#ffffff">
<st:pageHeader currentTool="nldr" toolLabel="${title}" />

<c:if test="${not empty requestScope.pageMessage}">
<div id="page-message">
	<div class="closeBx" style="float:right" onclick="doClosePageMsg()" title="close"></div>
	${requestScope.pageMessage}</div>
</c:if>


<h3>Update Affected Records for Vocabs</h3>

		<c:if test="${nldrToolsForm.isUpdating}">
			<h3>Update in progress</h3>		
			<p>
				<c:if test="${sessionBean.id != nldrToolsForm.updatingSession}">
					Another user is currently updating a collection.</c:if>
				<%@ include file="/lib/progress-indicator/progress-widget.jsp" %>
			</p>
			
			<p><c:choose>
				<c:when test="${sessionBean.id == nldrToolsForm.updatingSession}">
					<%-- <html:submit property="Update" value="haltUpdate" /> --%>
					<input type="button" value="Stop update" 
							onclick="location='${contextPath}/manage/nldr.do?command=stopUpdate'" />
				</c:when>
				<c:otherwise>
					<input type="button" value="Check update progress" 
							onclick="location='${contextPath}/manage/nldr.do?showUpdateMessages=true'" />
				</c:otherwise>
			</c:choose>			
			</p>

		</c:if>
		
		<c:if test="${not nldrToolsForm.isUpdating}">
		<%-- <div>We're not updating at the moment ...</div> --%>
		</c:if>

<c:set var="collections" value="${fn:split('pubname event inst', ' ')}" />
<div style="margin:10px">
<table cellpadding="4px">
	<c:forEach var="collection" items="${collections}" > 
		<tr>
			<td>${collection}</td>
			<td><input type="button" value="update" 
				onclick="doUpdateAffected('${collection}')" />
			</td>
		</tr>
	</c:forEach>
</table>
</div>


</body>
</html:html>

