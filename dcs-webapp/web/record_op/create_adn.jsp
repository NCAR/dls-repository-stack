<%@ include file="/JSTLTagLibIncludes.jsp" %>
<resp:setHeader name="Cache-Control">cache</resp:setHeader>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<html:html>
<head>
	<title><st:pageTitle title="New Record Form" /></title>
	
	<%@ include file="/baseHTMLIncludes.jsp" %>
	
	<script>
		// Override the method below to set the focus to the appropriate field.
		function sf() {
			$('primaryUrl').focus();
		}
	
		function doView (href) {
			var features = "innerHeight=500,height=500,innerWidth=650,width=650,resizable=yes,scrollbars=yes";
			features += ",locationbar=yes,menubar=yes,location=yes,toolbar=yes";
			resourcewin = window.open (href, "resourcewin", features);
			resourcewin.focus();
			return false;
		}
		
	</script>
</head>
<body onload="sf()">

<%-- page header --%>
<st:pageHeader currentTool="home" toolLabel="Create ADN Record" />
<st:breadcrumbs>
	<span class="current">Collection: ${carForm.collectionName}</span>
</st:breadcrumbs>

<st:pageMessages okPath="" />
	
	<html:form action="/record_op/adn" method="GET">
	<html:hidden property="collection"/>
	<table cellspacing="0" cellpadding="4">
	<tr>
		<td colspan="2">
			<p>To create a record, enter a URL and title. 
			After submitting a URL and title, you will be able to access all fields for editing.</p>
		</td>
	</tr>

	<tr valign="top">
		<td>Primary URL</td>
		<td>		             
			<st:fieldMessage propertyId="primaryUrl"/>
			<html:text styleId="primaryUrl" property="primaryUrl" size="70"/>
		</td>
	</tr>	
	<tr valign="top">
		<td>Title</td>
		<td>		             
			<st:fieldMessage propertyId="title"/>
			<html:text property="title" size="70"/>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>
			<html:submit property="command" value="submit" />
			<html:submit property="command" value="cancel" />
			<%-- <html:cancel/> --%>
		</td>
	</tr>
	<c:if test="${not empty carForm.sims}">
	<tr>
		<td colspan="2">
			<h3>Similar Urls in this collection</h3>
			<p>To continue to catalog the current url 
			(<a href="${carForm.primaryUrl}" onclick="return (doView('${carForm.primaryUrl}'))">${carForm.primaryUrl}</a>),
			click on the <span class="doc-em">submit</span> button above.
			<blockquote>
			<c:forEach items="${carForm.sims}" var="sim" varStatus="status">
				<li style="margin-bottom:4pt"><a 
					href="javascript:doView('${sim.url}');"
					onclick="return (doView('${sim.url}'));"
					title="open this url in a separate window">${sim.url}</a>
					 - cataloged in 
						<a href="../admin/display.do?fileid=${sim.id}&rt=text"
						 target="_blank"
						 title="see record xml in new window">${sim.id}</a></li>
			</c:forEach>
			</blockquote>
		</td>
	</tr>
	</c:if>
	<c:if test="${not empty carForm.dups}">
	<tr>
		<td colspan="2">
			<hr><div><b>Duplicate primary URL detected</b></div>
			<p>Your URL is already cataloged as either a primary or mirror URL:</p>
			<ul>
			<c:forEach items="${carForm.dups}" var="dup" varStatus="status">
				<li style="margin-bottom:4pt"><a 
					href="javascript:doView('${dup.url}');"
					onclick="return (doView('${dup.url}'));"
					title="open this url in a separate window">${dup.url}</a>
					 - cataloged in 
					 <a href="../admin/display.do?fileid=${dup.id}&rt=text"
						target="_blank"
						title="see record xml in new window">${dup.id}</a></li>
			</c:forEach>
			</ul>
			
			<p>Duplicate URLs within a collection are not allowed.<br>
			Please supply a different URL to continue</p>

		</td>
	</tr>
	</c:if>
</table>
	
</html:form>

</body>
</html:html>
