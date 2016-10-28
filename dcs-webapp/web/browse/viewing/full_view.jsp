<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>
<jsp:useBean id="collapseBean" class="edu.ucar.dls.schemedit.display.CollapseBean" scope="session"/>
<resp:setHeader name="Cache-Control">cache</resp:setHeader>

<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<%-- Indent the check box sub-menus by this many pixels --%>
<c:set var="indentAmount" value="18"/>
<c:set var="urlTruncateLen" value="80"/> 
<c:set var="controlBoxWidth" value="470"/> 

<html:html>
<head>
	<META http-equiv="Content-Type" content="text/html; charset=UTF-8" >
	<title><st:pageTitle title="${viewForm.docReader.id}" /></title>

<%@ include file="/baseHTMLIncludes.jsp" %>
<link rel="stylesheet" href="../lib/autoform-styles.css" type="text/css">
<script language="JavaScript" src="${contextPath}/lib/best-practices-link-support.js"></script>
<script type="text/javascript">
<!--	

	var arrowUp = "";
	var arrowDown = "";
	if(document.images) {
		arrowUp = new Image;
		arrowDown = new Image;
		arrowUp.src = "../images/btnExpandClsd.gif";
		arrowDown.src = "../images/btnExpand.gif";
	}  

	function toggleStatusVisibility( elementID ) {
		var duration = 0.4;
		var showEffect = Effect.Appear; // BlindDown
		var hideEffect = Effect.Fade; // BlindUp
		var objElement = $(elementID);
		if (objElement) {
			if (objElement.visible()) {
				hideEffect (elementID, {duration:duration});
			}
			else {
				showEffect (elementID, {duration:duration});
			}
			var arrowObj = $(elementID+'_arrow')
			if (arrowObj) {
					arrowObj.src = (objElement.visible() ? arrowDown.src : arrowUp.src);
			}
		}
	}
	
	function toggleVisibilitySimple( elementID ) {
		$(elementID).toggle();
	}
	
	/* controls the open/close of search critieria selections */
	function toggleVisibility( elementID ) {
		var duration = 0.2;
		var showEffect = Effect.Appear; // BlindDown
		var hideEffect = Effect.Fade; // BlindUp
		var objElement = $(elementID);
		var arrowObj = $(elementID+'_arrow')
		if (objElement) {
			if (true) { // use effects?
				if (objElement.visible()) {
					hideEffect (objElement, {duration:duration});
					arrowObj.src = arrowUp.src;
				}
				else {
					showEffect (objElement, {duration:duration});
					arrowObj.src = arrowDown.src;
					}
			}
			else {
				objElement.toggle();
				if (arrowObj)
						arrowObj.src = (objElement.visible() ? arrowDown.src : arrowUp.src);
			}
		}
	}

	
	function pageInit () {
		positionControlBox();
		try {
			Event.observe (window, 'resize', positionControlBox);
		} catch (error) { alert (error) }
	}

	/* 	item-controls box is absolutely positioned using
			a reference object (the item-header element).
			A floating mask is positioned behind it to flow output
			around the controls
	*/
	function positionControlBox() {
		var controls_box = $('item-controls');
		var header = $('item-header');
		var c_offset = header.cumulativeOffset();
		var headerDims = header.getDimensions();
		var actionsBox = controls_box.down();
		var actionsWidth = actionsBox.getDimensions().width;
		log ('actionsWidth: ' + actionsWidth);
		controls_box.setStyle ({
				width: actionsWidth,
				top: c_offset.top + headerDims.height,
				left: c_offset.left + headerDims.width - (actionsWidth + 10)
				});
		// set mask height to control-box height		
		$('item-controls-mask').setStyle({
			width:actionsWidth,
			height:controls_box.getDimensions().height
		});
		controls_box.show()
	}
		
	document.observe ('dom:loaded', pageInit)
//-->
</script>
<style type="text/css">
#item-controls {
	position:absolute;
	top:-100;
	padding:0px 0px 6px 5px;
	background-color:white;
	z-index:2;
}

#item-controls-mask {
 	float:right;
	padding:0px 0px 0px 5px;
	background-color:white;
}
</style>
</head>
<body>
		<%-- controls are placed by javascript --%>
		<div id="item-controls">
			<div align="right" style="white-space:nowrap;padding:0px 0px 5px 0px;">
		 		<%@ include file="viewing_record_actions.jspf" %>
			</div>
			<st:workFlowStatusNote result="${viewForm.result}" sessionBean="${sessionBean}" />
		</div>

<%@ include file="/locked_record_status.jspf" %>

<st:pageHeader toolLabel="View Record" />

<%-- breadcrumbs --%>
<st:breadcrumbs>
	<c:if test="${not empty viewForm.docReader.myCollectionDoc}">
		${viewForm.docReader.myCollectionDoc.shortTitle}
		<st:breadcrumbArrow />
	</c:if>
	<c:if test="${not empty viewForm.docReader.id}" >
		<span class="current">${viewForm.docReader.id}</span>
	</c:if>
</st:breadcrumbs>

<st:pageMessages />

<logic:notEmpty name="viewForm" property="docReader">

<c:set var="pagingLinks">
	<c:if test="${not viewForm.results.isEmpty}" >
	<div style="margin:0px 0px 10px 0px">
		<table width="100%" cellpadding="0" cellspacing="0">	
			<tr valign="bottom">
				<td align="left" width="33%">
					<c:if test="${not empty viewForm.prevId}">
						<a href="${contextPath}/browse/view.do?id=${viewForm.prevId}"
							title="View previous item in search results"><img 
								src="${contextPath}/images/arrow_left.gif" border="0">&nbsp;
							Previous item</a>
					</c:if>
				</td>
				<td align="center"  width="33%">
					<c:if test="${viewForm.resultIndex > -1}">
						Showing item ${viewForm.resultIndex + 1} of ${viewForm.numResults}
					</c:if>
				</td>
				<td align="right"  width="33%">
					<c:if test="${not empty viewForm.nextId}">
						<a href="${contextPath}/browse/view.do?id=${viewForm.nextId}"
							title="View next item in search results">Next item&nbsp;
						<img src="${contextPath}/images/arrow_right.gif" border="0"></a>
					</c:if>
				</td>
			</tr>
		</table>
		</div>
	</c:if>
</c:set>

${pagingLinks}

<table width="100%" cellpadding="5" cellspacing="0" border="0">	
	<%-- item-header is positioning reference for control-box --%>
	<tr valign="top"  id="item-header"> 
		<td>
			<c:set var="docMap" value="${viewForm.result.docMap}" />
			<%@ include file="viewItemHeader.jspf" %>
		</td>
	</tr>

	<tr>
		<td>
			<%-- floating div serves as mask for item-controls box --%>
			<div id="item-controls-mask"></div>
			
			<%-- item-specific information --%>
			<div id="item-info">
			
				<%-- framework-specific item information --%>
				<c:choose>
					<c:when test="${viewForm.docReader.readerType == 'ItemDocReader'}" >
						<%@ include file="synopses/adn_view_info.jspf" %>
					</c:when>	
					
					<c:when test="${viewForm.docReader.readerType == 'NewsOppsDocReader'}" >
						<%@ include file="synopses/news_opps_view_info.jspf" %>
					</c:when>
					
					<c:when test="${viewForm.docReader.readerType == 'DleseAnnoDocReader'}" >
						<%@ include file="synopses/dlese_anno_view_info.jspf" %>
					</c:when>			
					
					<c:when test="${viewForm.docReader.readerType == 'DleseCollectionDocReader'}" >
						<%@ include file="synopses/dlese_collect_view_info.jspf" %>
					</c:when>	

					<c:otherwise>
						<c:set var="item_format" value="${viewForm.docReader.nativeFormat}" />
						<c:set var="relativePath">synopses/${item_format}_view_info.jsp</c:set>
						<c:if test="${sf:fileExists (relativePath, pageContext.request)}">
							<c:set var="xml"> <%--the xml source for this item --%>
								<c:choose>
									<c:when test="${item_format == 'oai_dc'}">
										<c:out value="${viewForm.docReader.oaiDublinCoreXml}" escapeXml="false" />
									</c:when>
									<c:otherwise>
										<c:out value="${viewForm.docReader.xml}" escapeXml="false" />
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
				
				<%-- framework-independent item-info --%>
				
				<%@ include file="annotationsDisplay.jspf" %>
				
				<logic:notEmpty name="viewForm" property="docReader.myCollectionDoc">
					<div class="searchResultValues"><em>Collection:</em> 
						&nbsp;${viewForm.docReader.myCollectionDoc.shortTitle}</div>
				</logic:notEmpty>	
				
				<div class="searchResultValues"><em>Record format:</em> 
					<bean:write name="viewForm" property="docReader.nativeFormat" filter="false" />
				</div>
				<div class="searchResultValues"><em>File location:</em> 
					<bean:write name="viewForm" property="docReader.docsource" filter="false" />
				</div>	
						
				<%-- Show lucene indexing to admin users (or if authentication is disabled)--%>
				<c:if test="${(not applicationScope['authenticationEnabled']) || sessionBean.user.isAdminUser}" >
					<div style="margin:0px 0px 3px 0px">
						<%@ include file="../luceneEntryDisplay.jspf" %>
					</div>
					
					<%-- Set up relations params for DDSWS requests --%>
					<c:set var="relationRequestParms"><c:forEach items="${viewForm.docReader.relatedRecordsMap}" var="relatedRecordsEntry" 
						varStatus="i">&amp;relation=${relatedRecordsEntry.key}</c:forEach></c:set>
					<div class="searchResultValues"><em>Web service requests:</em>
					[ <a href='<c:url value="/services/ddsws1-1?verb=GetRecord&amp;id=${viewForm.docReader.id}${relationRequestParms}"/>' title='GetRecord service request'>GetRecord</a> 
					| <a href='<c:url value="/services/ddsws1-1?verb=Search&amp;q=idvalue:&quot;${viewForm.docReader.id}&quot;${relationRequestParms}&amp;s=0&amp;n=10"/>' title='Search service request'>Search</a> ]
					</div>
				</c:if>

				
				
				
			</div> <%-- end of item-info --%>
				
			<jsp:include page="${viewForm.recordViewPage}" />
			
			<%-- Display NDR info if NDR Service is enabled --%>
			<c:if test="${ndrServiceEnabled && ndrServiceActive}">
				<c:if test="${(not applicationScope['authenticationEnabled']) || 
							sf:hasCollectionRole(sessionBean.user,'manager',viewForm.docReader.collection)}">
					<div style="margin-top:5px">
							<st:recordNdrStatus result="${viewForm.result}" sessionBean="${sessionBean}" />
					</div>
				</c:if>
			</c:if>
			
		</td>
	</tr>
</table>

<hr color="#333366" />

${pagingLinks}

</logic:notEmpty>

</body>
</html:html>
