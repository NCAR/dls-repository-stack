<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ include file="../../JSTLTagLibIncludes.jsp" %>



<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>

<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>





<c:if test="${not empty param.anchor}">

	<c:set var="anchor" value="${param.anchor}" scope="session"/>

</c:if>

<c:if test="${not empty param.command}">

	<c:set var="commandViewerCommand" value='${fn:replace(param.command,"%27","\'")}' scope="session"/>

	<c:remove var="viewCode" scope="session"/>

</c:if>



<c:if test="${param.viewCode == 't'}">

	<c:set var="viewCode" value="t" scope="session"/>

</c:if>

<c:if test="${param.viewCode == 'f'}">

	<c:remove var="viewCode" scope="session"/>

</c:if>





<html>

<head>

	<c:choose>

		<c:when test="${anchor == 'simplePage'}">

			<c:set var="title" value="Sample DLESE search page"/>

		</c:when>

		<c:otherwise>

			<c:set var="title" value="JavaScript Search Service: Command Viewer"/>

		</c:otherwise>

	</c:choose>

	

	<title>${title}</title>

	

	<LINK REL="stylesheet" TYPE="text/css" HREF="${contextUrl}/services/jshtml1-1/suggested_styles.css">

	<style type="text/css">

		<!--

			/* Override the 'suggested' background color behind the resources's title */

			TR.resourceTitle {

				background-color:#E8ECF4;

			}			

			PRE {

				font-size:11px;

				padding-left:15px;

			}

		-->

	</style>

	

	<%-- DLESE template header (CSS styles and JavaScript references) --%>

	<link rel="stylesheet" type="text/css" href="${domain}/dlese_shared/dlese_styles.css"/>

	<%-- <c:import url="${domain}/dlese_shared/templates/header_refs.html" /> --%> 



	<!--  The JavaScript search service suggested CSS styles -->

	<LINK REL="stylesheet" TYPE="text/css" HREF="${contextUrl}/services/jshtml1-1/documentation_styles.css">

</head>



<body bgcolor="#FFFFFF" text="#000000" 

link="#220066" vlink="#006600" alink="#220066">



<h1>${title}</h1>



<c:if test="${anchor != 'simplePage'}">



<c:choose>

	<c:when test="${empty viewCode}">

		<p><nobr>[ <a href="?viewCode=t">Click to view the service commands</a> ]</nobr></p>

		

	</c:when>

	<c:otherwise>

		<p><nobr>[ <a href="?viewCode=f">Click to hide the service commands</a> ]</nobr></p>

		

	<table border="0" cellspacing="0" cellpadding="4" style="margin-left:5px; margin-bottom:4px">

	   <tr>

		<td>The following commands are being rendered below:</td>

	  </tr> 

	  <tr>

		<td>

			<table class="codeTB">

				<tr class="codeTB">

				<td class="codeTB"><pre><c:out value="${commandViewerCommand}" escapeXml="true"/></pre></td>

				</tr>

			</table>

		</td>		

	  </tr>

	   <tr>

		<td>To use this code, simply copy and paste it into your web page.</td>

	  </tr> 

	   <tr>

		<td>Note that the <a href="view_suggested_styles.jsp">suggested CSS styles</a> are being applied to this page, and the resourceTitle background color has been changed to blue - view page source for details.</td>

	  </tr>   

	</table>



	</c:otherwise>

</c:choose>



<p>Tip: If a search box is displayed below, try performing a search to see how the search results are displayed.</p>

<p><strong>The service commands are rendered below this line: </strong></p>



</c:if>



<hr width="100%" size="1" noshade>

<div style="margin-top:20px">



<!-- DLESE JavaScript Search Service code is below: -->

${commandViewerCommand}



<NOSCRIPT>

	This page requires that JavaScript be turned on. Please activate JavaScript in your browser.

</NOSCRIPT> 



</div>

</body>

</html>





