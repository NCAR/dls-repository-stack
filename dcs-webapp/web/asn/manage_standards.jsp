<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Manage Standards</c:set>
<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>

<c:set var="manageBean" value="${asnForm.manageStandardsBean}" />

<html:html>

<head>
<title><st:pageTitle title="${title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<%@ include file="/manage/manageHTMLIncludes.jsp" %>
<link rel='stylesheet' type='text/css' href='${contextPath}/asn/manage_standards.css'>
<script type="text/javascript" src="${contextPath}/asn/manage_standards.js"></script>

<script type="text/javascript">
var ALL_SUBJECTS = [];
var FORMAT_SUBJECT_MAP = {}
var CONTEXT_PATH = '${contextPath}';
var CURRENT_FRAMEWORK = '${param.xmlFormat}';
var CURRENT_SUBJECT = '${param.subject}';

<c:forEach var="subject" items="${asnForm.asnCatalog.subjects}">
	ALL_SUBJECTS.push("${subject}");
	// log ('${subject}');
</c:forEach>
// log ("all subjects has " + ALL_SUBJECTS.length + " members");


var xmlFormat, subjects;

// log ("initializing FORMAT_SUBJECT_MAP");
<c:forEach var="xmlFormat" items="${manageBean.xmlFormats}">
xmlFormat = "${xmlFormat}";
subjects = [];
	<c:set var="stdMgrBean" value="${manageBean.standardsManagerBeanMap[xmlFormat]}" />
	// log ('there are ${fn:length(stdMgrBean.subjects)} subjects');
	<c:forEach var="subject" items="${stdMgrBean.subjects}">
	// log ("${subject}");
	subjects.push ("${subject}");
	</c:forEach>
	FORMAT_SUBJECT_MAP[xmlFormat] = subjects; 
</c:forEach>
	// log ("FORMAT_SUBJECT_MAP has " + $H(FORMAT_SUBJECT_MAP).keys().size());

/* pageInit defined in manage_standards.js */
Event.observe (window, 'load', pageInit);
</script>

</head>

<body text="#000000" bgcolor="#ffffff">

<st:pageHeader currentTool="manage" toolLabel="${title}" />

<%-- <%@ include file="stds-nav.jsp" %> --%>
<p>Specify the standards documents that will be available to catalogers for each Framework.
</p>
<p>NOTE: To Disable standards display for a particular <i>collection</i>, 
go to "Settings -> Collection Settings" 
and select the collection to configure. Then edit the collection config, and set the "services/allowSuggestions"
to false. 
</p>
<st:pageMessages />

<table cellpadding="0px" cellspacing="0px" width="100%">
	<tr valign="middle">
		<td>
			<h1>Metadata Frameworks</h1>
		</td>
		<td style="padding-left:10px">
			<input type="button" value="open all" id="open-frameworks-button" 
						 class="small-button"/>
			<input type="button" value="close all" id="close-frameworks-button" 
					   class="small-button" />
		</td>
		<td align="right">
			<table id="key-table">
				<tr>
					<th>key</th>
				</tr>
				<tr>
					<td class="registered-doc">registered standard doc</td>
				</tr>
				<tr>
					<td class="extra-doc">not registered but used-</td>
				</tr>
				<tr>
					<td class="default-doc">default doc</td>
				</tr>
			</table>
	</tr>
</table>
<%-- 		
<h1 style="display:inline">Metadata Frameworks</h1>
<input type="button" value="open all" id="open-frameworks-button" class="small-button"/>
<input type="button" value="close all" id="close-frameworks-button" class="small-button" />
 --%>


<c:choose>
	<c:when test="${empty manageBean.xmlFormats}">
		<div class="confirm-msg-box"><div class="confirm-msg">
		<i>There are no frameworks currently configured for the ASN Standards Service.</i><br/> 
		See the Installation instructions (available via the Help menu at the top of this page)
		for information about how to configure the ASN Standards Service</div>
		</div>
	</c:when>
	<c:otherwise>

<div style="padding:0px 0px 5px 0px;border-bottom:thin solid #333366">Active Standards Documents for each configured Metadata Framework, grouped by
subject.</div>
	
<div id="frameworks">
<c:forEach var="xmlFormat" items="${manageBean.xmlFormats}">
	
	<div class="framework">

		<%-- <c:set var="stdMgrBean" value="${manageBean.standardsManagerMap[xmlFormat]}" /> --%>
		<c:set var="stdMgrBean" value="${manageBean.standardsManagerBeanMap[xmlFormat]}" />
		<c:set var="extraDocIds" value="${stdMgrBean.extraDocIds}" />
		<c:choose>
			<c:when test="${not empty stdMgrBean}">
			
			<%-- FRAMEWORK HEADER --%>
			
			<table class="framework-header-table">
				<td style="white-space:nowrap">
					<div class="collapsible-button" id="framework_${xmlFormat}">
						<span class="framework-name">${xmlFormat}</span>
						<%-- <span style="margin-left:5px">framework</span> --%>
					</div>
				</td>
<%-- 				<td width="95%" align="center" style="white-space:nowrap">
					<div>
						<input type="button" value="open all subjects" class="open-subjects small-button"/>
						<input type="button" value="close all subjects" class="close-subjects small-button" />
					</div>
				</td> --%>
	<%-- 			
				<td align="center" width="95%">default doc: <span id="default-doc_${xmlFormat}">${stdMgrBean.defaultDocKey}</span>
					<input type="button" id="default_${xmlFormat}" class="default-doc-button small-button" value="change" /></td>
		
 --%>					<td align="right">
					<%-- <select id="subject-select_${xmlFormat}" class="subject-select" >
						<option value=""> -- add a subject -- </option>
					</select> --%>
				</td>
			</table>
	
			<%-- FRAMEWORK CONTENT --%>
			<div id="framework_${xmlFormat}_content" class="framework_content" style="display:none">
			
					<select id="subject-select_${xmlFormat}" style="float:right" class="subject-select" >
						<option value=""> -- add a subject -- </option>
					</select>
			
				<c:choose>
					<c:when test="${not empty stdMgrBean.allSubjects}">
				

						<div class="default-doc-control">
							<span style="color:#333366;font-weight:bold">default document: </span>
							<span id="default-doc_${xmlFormat}">${stdMgrBean.defaultDocKey}</span>
							<input type="button" id="default_${xmlFormat}" class="default-doc-button small-button" value="change" />	
							<div align="center">
							<div class="set-default-prompt" style="display:none">Click on any standards doc below for this framework to
							set the default. <br/>Click anywhere else to keep current default.</div>
							</div>
						</div>
					
						<c:forEach var="subject" items="${stdMgrBean.allSubjects}">
						
							<c:set var="subjectItems" value="${stdMgrBean.subjectAllDocsMap[subject]}" />
							<c:set var="subjectIds" value="${stdMgrBean.subjectIdsMap[subject]}" />
						
							<div class="subject">						
						
								<table class="subject-header-table">
									<td style="white-space:nowrap">
										<div class="collapsible-button" id="${xmlFormat}_${subject}">
											<span class="subject-name">${subject} (${fn:length(subjectItems)})</span>
										</div>
									</td>
								
									<td align="right" width="99%">
<%-- 											<span class="command-button edit-button" style="float:right"><a href='#' 
												onclick="editSubjectItems('${subject}', '${xmlFormat}')">edit</a></span> --%>
												<input type="button" class="small-button" style="float:right" 
															 onclick="editSubjectItems('${subject}', '${xmlFormat}')"
															 value="edit"/>
												
												</td>
									</tr>
								</table> <%-- subject-header-table --%>
								
								<table id="${xmlFormat}_${subject}_content"
											 class="subject_content"
											 style="display:none">
								
								<c:forEach var="docInfo" items="${subjectItems}" >
									
									<%-- stdDoc is a AsnDocInfo --%>
									<c:set var="extra_doc" value="${sf:listContains(extraDocIds, docInfo.docId)}" />
									<c:set var="registered_doc" value="${sf:listContains(subjectIds, docInfo.docId)}" />
									<c:set var="default_doc" value="${stdMgrBean.defaultDocKey == docInfo.key}" />
									<c:set var="classNames">std-doc-item ${registered_doc ? 'registered-doc' : ''} ${extra_doc ? 'extra-doc' : ''}
												${default_doc ? 'default-doc' : ''}</c:set>
									<tr class="${classNames}" id="${docInfo.key}">
										<td class="author">${docInfo.authorName}</td>
										<td class="created">${docInfo.created}</td>
										<td class="title">${docInfo.title}
										<%-- <span style="float:right;display:none" class="default-link">set as default</span></td> --%>
										<%-- <td class="default-cell">set as default</td> --%>
										<%-- <td class="key" align="right">${docInfo.key}</td> --%>
									</tr>
							</c:forEach>
						</table>
								
						</div> <%-- subject --%>
						
					</c:forEach> <%-- subjects --%>
				
				</c:when>
				
				<c:otherwise>
					<div style="padding:10px"><i>There are no standards documents registered for the ${xmlFormat} framework.</i>
					<br/>
					To register standards
					documents, select a subject from the pulldown above, and then select the standards documents
					from the provided list</div>
				</c:otherwise>
			</c:choose>
		
		</div> <%-- framework-content --%>
		
<%-- 		<c:set var="extraDocs" value="${stdMgrBean.extraDocs}" />
		<c:choose>
			<c:when test="${not empty extraDocs}">
				<h3>${fn:length(extraDocs)} extraDocs for ${xmlFormat} (source: extraDocIds)</h3>
				<c:forEach var="docInfo" items="${extraDocs}">
					<div style="margin-left:10px;">${docInfo.key}</div>
				</c:forEach>
			</c:when>
			<c:otherwise><div>There are NO extradocs for ${xmlFormat}</div></c:otherwise>
		</c:choose> --%>
		
		
		</c:when>
		<c:otherwise><h1>stdMgrBean not found</h1></c:otherwise>
	</c:choose>
	
	</div> <%-- framework --%>
</c:forEach>
</div><%-- frameworks --%>
</c:otherwise>
</c:choose>

</body>
</html:html>

