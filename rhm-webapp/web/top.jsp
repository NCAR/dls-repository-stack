<%@ include file="/TagLibIncludes.jsp" %>
<table id="topTable" cellpadding="0" cellspacing="0" style="padding:0; margin:0; border:none; width:100%;">
	<tr style="padding:0px; margin:0px; border:none;" >


		<td style="padding:0px 0px 0px 10px; margin:0px; border:none;" align="left" valign="center" width="50%">
				<h1 style="color:white; font-size:24px;padding:0px; margin:0px;"><nobr>Harvest Repository Manager</nobr></h1>
		</td>
		<td style="color: white; padding:0px 5px 0px 5px; margin:0px; border:none;" align="left" valign="center">
			<div>${initParam.topBannerDisplayText}</div>
		</td>
		<td style="padding: 6px; margin:0px; border:none;" valign="top" align="right" nowrap>
			<c:if test="${pageViewContext != 'noLinks'}">
				<div style="padding-bottom:4px; padding-right:20px"><a style="color:white" href='<c:url value="/"/>'>Home</a></div>
				<div style="padding-bottom:4px; padding-right:20px"><a style="color:white" href='https://wiki.ucar.edu/display/nsdlto/Harvest+Manager+User+Guide'>User Guide</a></div>
			</c:if>
		</td>
	</tr>
</table>

<%-- A shadow effect --%>
<%-- <div id="sh1"></div> --%>

<c:set var="requestURI" value="${pageContext.request.requestURI}"/>
<c:set var="topTab">
	<c:choose>
		<c:when test="${pageViewContext == 'errorPages'}">none</c:when>
		<c:when test="${fn:contains(requestURI,'/explorer/')}">explorer</c:when>
		<c:when test="${fn:contains(requestURI,'metadata_db')}">metadatadb</c:when>
		<c:when test="${fn:contains(requestURI,'admin')}">admin</c:when>
		<c:otherwise>collections</c:otherwise>
	</c:choose>
</c:set>

<c:if test="${pageViewContext != 'noLinks'}">
	<div style="height:${topTab == 'none' ? '27px' : '26px'}; border-bottom: 1px solid black; position:relative; background-color:#BFD6EC; text-align: center;">
		<div style="position:absolute; top: 0px; left: 0px; width: 110px; display: block; height: 27px; border-right: 1px solid black; ${topTab == 'collections' ? 'background-color:white;' : ''}">
			<div style="margin-top: 4px" >
				<a href="<c:url value='/'/>">Collections</a>
			</div>
		</div>

        <div style="position:absolute; top: 0px; left: 111px; width: 150px; height: 27px; border-right: 1px solid black; ${topTab == 'metadatadb' ? 'background-color:white;' : ''}">
            <div style="margin-top: 4px" >
                <a href="<c:url value='/MetadataSearch.do'/>">Repository Search</a>
            </div>
        </div>

		<div style="position:absolute; top: 0px; left: 262px; width: 109px; height: 27px; border-right: 1px solid black; ${topTab == 'explorer' ? 'background-color:white;' : ''}">
			<div style="margin-top: 4px" >
				<a href="<c:url value='/explorer/'/>">OAI Explorer</a>
			</div>
		</div>

		<div style="position:absolute; top: 0px; left: 372px; width: 100px; height: 27px; border-right: 1px solid black; ${topTab == 'admin' ? 'background-color:white;' : ''}">
			<div style="margin-top: 4px" >
				<a href="<c:url value='/admin'/>">Admin</a>
			</div>
		</div>
	</div>
</c:if>

<table id="mainContent" border="0" cellpadding="0" cellspacing="0"><tr><td>
