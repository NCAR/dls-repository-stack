<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ taglib uri='/WEB-INF/tlds/vocabulary_opml.tld' prefix='vocabs' %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<resp:setHeader name="Cache-Control">cache</resp:setHeader>

<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>

<html:html>
<head>

<title><st:pageTitle title="Search" /></title>

<%@ include file="/baseHTMLIncludes.jsp" %>

<link rel='stylesheet' type='text/css' href='${contextPath}/browse/searching/pager.css'>
<script type="text/javascript" src="${contextPath}/lib/javascript/stemmer-highlighter.js"></script>
<script type="text/javascript" src="${contextPath}/browse/searching/pager.js"></script>
<script type="text/javascript" src="${contextPath}/lib/edit-status.js"></script>

<%@ include file="search_checkbox_menus.jsp" %>
<%-- Indent the check box sub-menus by this many pixels --%>
<c:set var="indentAmount" value="18"/>

<script type="text/javascript">
<!--
	// Override the method below to set the focus to the appropriate field.
	function sf(){
		if (document.queryForm)
			document.queryForm.q.focus();
	}
	
	function pageInit () {
		// window.loadFirebugConsole();
		
		/* if there is a current record id stored in the sessionBean, scroll to it and then
		 	 catch its value as "currentRecId" before clearing the sessionBean.
		*/
		 <c:if test="${not empty sessionBean.recId}">
			window.location.hash = "${sessionBean.recId}";
			<c:set var="currentRecId" value="${sessionBean.recId}" scope="page" />
		</c:if>
		<c:if test="${empty sessionBean.recId}">
			sf();
		</c:if>
		<jsp:setProperty name="sessionBean" property="recId" value="" />
		
		/* set up the pager if appropriate */
		var numResults = ${queryForm.numResults};
		var numPagingRecords = ${queryForm.numPagingRecords};
		var paigingParam = ${queryForm.start};
		if (numResults > numPagingRecords) {
			var page = (paigingParam / numPagingRecords) + 1;
			var numPages = Math.ceil(numResults / numPagingRecords)
			var pager = new DcsPager (page, numPages, unescape("${queryForm.nonPaigingParams}"), numPagingRecords, "${contextPath}");
			pager.render ($('pager-1'));
			pager.render ($('pager-2'));
		}
		
		/* Hide the batch operation controller if there is not only 1 collection specified
				(batch ops are limited to operating over a single collection)
		*/
		// log ("there are ${fn:length (paramValues.scs)} scs values");
			<c:if test="${fn:length (paramValues.scs) != 1}">
			/*
				$('batch-op-controller').disable();
				var msg = new Element ("div").update ("Batch operations only available<br/>over a single collection");
				msg.setStyle ({fontSize:"80%",fontStyle:"italic"});
				$('batch-op-controller').insert ({before: msg});
			*/
			if ($('batch-op-controller')) {
				$('batch-op-controller').hide();
			}
		</c:if>
		
		
		var showStatusNotes = "${queryForm.searchMode == 'status_note'}" == "true";
 		$$('.status-visibility-widget').each (function (widget) {
			widget.observe ('click', handleStatusVisibilityWidget);
			if (showStatusNotes) {
				var id = getId4StatusNoteWidget(widget);
				log (id);
				if (id)
					$('statusNote_'+id).show();
			}
				
		});
		
		
		$$('.dcs-id').each (function (el) {
			el = $(el);
/* 			el.observe ('mouseover', function (event) {
				el.setStyle ({cursor:'pointer',textDecoration:'underline'});
			}); */
			el.observe ('click', function (event) {
				var url = "${contextPath}/browse/view.do?id="+el.innerHTML;
				log ("url: " + url);
				window.location = url;
			});
		});		
		
	}
	

	
	Event.observe (window, "load", pageInit);

//-->
</script>
</head>
<body>

<%@ include file="/locked_record_status.jspf" %>

<st:pageHeader currentTool="search" toolLabel="Search" />

<%-- ######## Display messages, if present ######## --%>
<logic:messagesPresent> 		
	<table width="90%" bgcolor="#000000" cellspacing="1" cellpadding="8" bgcolor="blue">
	  <tr bgcolor="ffffff"> 
		<td>
			<b>Messages:</b>
			<ul>
				<html:messages id="msg" property="message"> 
					<li><bean:write name="msg"/></li>									
				</html:messages>
				<html:messages id="msg" property="error"> 
					<li><font color=red>Error: <bean:write name="msg"/></font></li>									
				</html:messages>
				<html:messages id="msg" property="deleteConfirmation">
					<li><bean:write name="msg"/></li>									
				</html:messages>
				
			</ul>
<%-- 			<blockquote>
				<c:if test="${not empty sessionBean.searchParams}">
					<c:set var="href" value="${contextPath}/browse/query.do?&s=${sessionBean.paigingParam}${sessionBean.searchParams}"/>
				</c:if>
				<c:if test="${empty sessionBean.searchParams}">
					<c:set var="href" value="${contextPath}/browse/query.do"/>
				</c:if>
				<input type="button" value=" OK " onclick="location='${href}'"/>
			</blockquote> --%>
		</td>
	  </tr>
	</table>		
	<br><br>

</logic:messagesPresent>
	
<%-- <%@ include file="debugging.jspf" %> --%>


<%-- Search box --%>
<div id="search-box" style="margin-left:50px">
	<%@ include file="search_box.jspf" %>
</div>
 
<%-- user selections --%>
<logic:present parameter="q">
<%@ include file="search_user_selections.jsp" %>
</logic:present>

<div style="padding:3px 0px 0px 0px">
<table width="100%" cellpadding="2" cellspacing="0">
<%-- No search results --%>
<c:choose>
	<c:when test="${queryForm.numResults == '0'}">
		<logic:present parameter="q">
			<tr>
				<td>
				Your search 
				<logic:notEmpty name="queryForm" property="q">
					for 
						<c:choose>
							<c:when test="${queryForm.searchMode == 'id'}">
								<b>id:</b>
							</c:when>
							<c:when test="${queryForm.searchMode == 'url'}">
								<b>url:</b>
							</c:when>
						</c:choose>
					<font color="#787DBC">${queryForm.q}</font>
				</logic:notEmpty>
				had no matches.
				</td>
			</tr>
		</logic:present>
	</c:when>

<c:otherwise><%-- search results present --%>
  <tr>
	<td colspan="2">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
		 <tr valign="middle">
			<td align="left" width="33%">
				Your search 
				<logic:notEmpty name="queryForm" property="q">
					for 
						<c:choose>
							<c:when test="${queryForm.searchMode == 'id'}">
								<b>id:</b>
							</c:when>
							<c:when test="${queryForm.searchMode == 'url'}">
								<b>url:</b>
							</c:when>
						</c:choose>
						<font color="#787DBC">${queryForm.q}</font>
				</logic:notEmpty>
				had <bean:write name="queryForm" property="numResults"/> matches.
			</td>
			<td align="center" width="33%"><div id="pager-1"></div></td>

			<td align="right" width="33%">
				<select id="batch-op-controller" title="Operate over all matches" 
								<%-- name="batch" --%>
								<c:if test="${readOnlyMode}">disabled="true"</c:if>
								onchange="doBatchOp (this.value)">
					<option value="" selected>-- Batch Operation --</option>
					<option value="batchStatus">Set Status</option>
					<option value="batchMove">Move</option>
					<option value="batchCopyMove">Copy and Move</option>
					<option value="batchDelete">Delete</option>
				</select>
			</td>
		</tr>
		</table>
	</td>
  </tr>
</table>
</div>
	
<table width="100%" cellpadding="2" cellspacing="0">
	
	<tr>
		<td colspan="2">
			<%@ include file="dcsSearchResultsHeader.jspf" %>
		</td>
	</tr>

	<%-- establish the words to be highlighted in search results --%>
	<dds:setKeywordsHighlight keywords="${queryForm.q}" highlightColor="#000099" />
	
  <c:forEach var="result" items="${queryForm.hits}"	varStatus="status">		
			<tr valign="top" class="item-header" id="${result.docReader.id}"> 
				<td colspan="2">
					<%@ include file="dcsSearchItemHeader.jspf" %>
				</td>
			</tr>
			<c:set var="show_default_info" value="${true}" />
			<c:set var="urlTruncateLen" value="90"/>
			<c:set var="item_info">
				<c:choose>
					<c:when test="${result.docReader.readerType == 'ItemDocReader'}">
						<%@ include file="synopses/adn_search_info.jspf" %>
					</c:when>		
			
					<c:when test="${result.docReader.readerType == 'NewsOppsDocReader'}">
						<%@ include file="synopses/news_opps_search_info.jspf" %>
					</c:when>
			
					<c:when test="${result.docReader.readerType == 'DleseAnnoDocReader'}">
						<%@ include file="synopses/dlese_anno_search_info.jspf" %>
					</c:when>
			
					<c:when test="${result.docReader.readerType == 'DleseCollectionDocReader'}">
						<%@ include file="synopses/dlese_collect_search_info.jspf" %>
					</c:when>
									
					<%-- search info for other frameworks uses xml source 
						determine dynamically if this framework has a custom "search_info" JSP file
						if so, use it and pass xml source
					--%>
					<c:otherwise>
						<c:set var="item_format" value="${result.docReader.nativeFormat}" />
						<c:set var="relativePath">synopses/${item_format}_search_info.jsp</c:set>
						<c:if test="${sf:fileExists (relativePath, pageContext.request)}">
							<c:set var="xml"> <%--the xml source for this item --%>
								<c:choose>
									<c:when test="${item_format == 'oai_dc'}">
										<c:out value="${result.docReader.oaiDublinCoreXml}" escapeXml="false" />
									</c:when>
									<c:otherwise>
										<c:out value="${result.docReader.xml}" escapeXml="false" />
									</c:otherwise>
								</c:choose>
							</c:set>
							<jsp:include page="${relativePath}">
								<jsp:param name="urlTruncateLen" value="${urlTruncateLen}"/>
								<jsp:param name="xml" value="${xml}" />
							</jsp:include>
						</c:if>
					</c:otherwise>
				</c:choose>
			</c:set>
			
			<%@ include file="item_search_template.jspf" %>
			
  </c:forEach>
 </table>
 <hr size="1" color="#999999" />
 <div id="pager-2" align="center"></div>
</c:otherwise>
</c:choose>

</body>
</html:html>
