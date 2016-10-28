<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">ASN Standards Catalog</c:set>
<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>

<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<%@ include file="/manage/manageHTMLIncludes.jsp" %>

<script>

function pageInit () {
	$$('li.subject-listing-header').each (function (subjHdr) {
		var subject = subjHdr.identify();
		subjHdr.observe ('click', function (event) {
			log (subject + " clicked");
			toggleSubjectHeader (subjHdr);
		});
		subjHdr.observe ('mouseover', function (event) {
			subjHdr.setStyle({cursor:'pointer'});
		});
		
	});
}

function toggleSubjectHeader (subjHdr) {
	var items = subjHdr.next();
	if (items.visible()) {
		subjHdr.removeClassName ("subject-listing-open");
		items.hide();
	}
	else {
		subjHdr.addClassName ("subject-listing-open");
		items.show();
	}
}	

document.observe ("dom:loaded", pageInit);
</script>
<style type="text/css">

/* closed.gif, opened.gif */
ul.subject-listing {
	list-style-image: url("${contextPath}/images/closed.gif");
	/* list-style-type:none; */
}

ul.subject-listing-open {
	list-style-image: url("${contextPath}/images/opened.gif");
}

li.subject-listing-header {
	font-weight:bold;
	font-size:1.1em;
	padding:5px;
	border:thin blue solid;
}

ul.item-listing {
	list-style-image: none;
}

ul.item-listing li {
	font-weight:normal;
	font-size:1em;
	border:none;
}

</style>

</head>

<body text="#000000" bgcolor="#ffffff">

<st:pageHeader currentTool="manage" toolLabel="${title}" />

<%@ include file="stds-nav.jsp" %>

<h2>Asn Catalog</h2>
<ul class="subject-listing">	
	<c:forEach var="subjectMapping" items="${asnForm.allDocsSubjectMap}" >
		<li class="subject-listing-header" id="${subjectMapping.key}">${subjectMapping.key} (${fn:length(subjectMapping.value)} items)</li>
		<ul class="item-listing" style="display:none;">
			<c:forEach var="docInfo" items="${subjectMapping.value}">
				<li>${docInfo.title}</li>
			</c:forEach>
		</ul>
		
	</c:forEach>
</ul>

<hr style="margin:20px" />

<h2>StandardsManagers</h2>
<div style="margin:20px 10% 20px 10%;padding:15px;border:thin solid blue">
	<div style="padding-bottom:10px;">This information is obtained from asnForm.standardsManagers, whicn maps from
	xmlFormat to AsnStandardsManager instance for that framework.</div>
	
	<div>From the standardsManager we obtain availableDocs, which are the std docs
	associated with the standardManager's xmlFormat
	</div>
</div>

<c:forEach var="stdMgrMapping" items="${asnForm.standardsManagers}">
	
	<c:set var="xmlFormat" value="${stdMgrMapping.key}" />
	<c:set var="stdMgr" value="${stdMgrMapping.value}" />
	
	<h3>Framework: ${stdMgrMapping.key}</h3>
	<div><i>Available docs for this framework</i></div>
	<ul>
	<c:forEach var="stdDoc" items="${stdMgr.availableDocs}" >
		<%-- stdDoc is a AsnDocInfo --%>
		<li>${stdDoc.title} - ${stdDoc.created} - ${stdDoc.key}</l>
	</c:forEach>
	</ul>
	
</c:forEach>

</body>
</html:html>

