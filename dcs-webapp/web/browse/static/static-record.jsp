<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib uri='/WEB-INF/tlds/vocabulary_opml.tld' prefix='vocabs' %>
<%@ page contentType="text/html; charset=UTF-8" %>

<resp:setHeader name="Cache-Control">cache</resp:setHeader>

<c:set var="record" value="${srForm.docReader}" />
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">${record.title}</c:set>


<html:html>
<head>
	<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>${title}</title>

<%@ include file="/baseHTMLIncludes.jsp" %>
<link rel='stylesheet' type='text/css' href='${contextPath}/browse/static/static-styles.css'>

<script type="text/javascript">
<!--	

	
//-->
</script>

</head>
<body>


<%-- <st:pageHeader toolLabel="View Record" /> --%>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr valign="bottom">
		<td align="left">
				<div class="tool-label">${srForm.framework.name} Record</div>
		</td>
		<td align="right" nowrap="1">
			<img align="center" src="${contextPath}/images/${applicationScope.logo}" height="32" width="132">
			<div class="system-label">Collection System</div>
		</td>
	</tr>
	<tr><td bgcolor="#333366" height="2px" colspan="3"></td></tr>
</table>

<%-- breadcrumbs --%>
<st:breadcrumbs>
	<c:if test="${not empty record.myCollectionDoc}">
					${record.myCollectionDoc.shortTitle}
		<st:breadcrumbArrow />
	</c:if>
	<c:if test="${not empty record.id}" >
		<span class="current">${record.id}</span>
	</c:if>
</st:breadcrumbs>

<logic:messagesPresent> 
<table width="100%" cellpadding="5" cellspacing="0">	
	<tr bgcolor="ffffff"> 
		<td>
			<h3>Unable to static view</h3>
			<ul>
				<html:messages id="msg" property="message"> 
					<li><bean:write name="msg"/></li>									
				</html:messages>
				<html:messages id="msg" property="error"> 
					<li><font color=red>Error: <bean:write name="msg"/></font></li>									
				</html:messages>
			</ul>
		</td>
	</tr>
</table>
</logic:messagesPresent>

<%@ include file="static-record-header.jspf" %>

<div class="record-header">
	<c:if test="${not empty record.url}">
		<div class="record-url">
			<a href="${record.url}">${record.url}</a>
		</div>
	</c:if>
</div>



	
<c:choose>
		<c:when test="${record.nativeFormat == 'adn'}">
			<%@ include file="adn_static_view.jspf" %>
		</c:when>
		
		<c:when test="${record.nativeFormat == 'dlese_anno'}">
			<%@ include file="dlese_anno_static_view.jspf" %>
		</c:when>
		
		<c:otherwise>
			<i>Sorry - unable to display details of record in <b>${result.docReader.nativeFormat}</b> ...</i>
		</c:otherwise>
	</c:choose>

</body>
</html:html>
