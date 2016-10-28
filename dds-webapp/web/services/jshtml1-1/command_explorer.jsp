<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ include file="../../JSTLTagLibIncludes.jsp" %>



<c:set var="domain" value="${f:serverUrl(pageContext.request)}"/>

<c:set var="contextUrl" value="${f:contextUrl(pageContext.request)}"/>



<html>

<head>

	<title>JavaScritpt Search Service: Command Explorer</title>

	

	<LINK REL="stylesheet" TYPE="text/css" HREF="${contextUrl}/services/jshtml1-1/suggested_styles.css">

	<style type="text/css">

		/* Override the 'suggested' background color behind the resources's title */

		TR.resourceTitle {

			background-color:#E8ECF4;

		}			

		PRE {

			font-size:11px;

			padding-left:15px;

		}

	</style>

	

	<%-- DLESE template header (CSS styles and JavaScript references) --%>

	<c:import url="${domain}/dlese_shared/templates/header_refs.html" />

	

	<!--  The JavaScript search service suggested CSS styles -->

	<LINK REL="stylesheet" TYPE="text/css" HREF="${contextUrl}/services/jshtml1-1/documentation_styles.css">



</head>



<body bgcolor="#FFFFFF" text="#000000" 

link="#220066" vlink="#006600" alink="#220066">



<c:if test="${not empty param.command}">

	<c:set var="commandViewerCommand" value='${fn:replace(param.command,"%27","\'")}' scope="session"/>

</c:if>

<c:if test="${not empty param.anchor}">

	<c:set var="anchor" value="${param.anchor}" scope="session"/>

</c:if>





<h1> JavaScript Search Service: Command Explorer</h1>



<table border="0" cellspacing="0" cellpadding="10" style="margin-left:5px;">

  <tr>

    <td bgcolor="#eeeeee">

		<form style="inherit; padding:0; margin:0;">

			<textarea name="command" cols="80" rows="12">${commandViewerCommand}</textarea><br>

			<input type="submit" value="Edit the commands above and click here to see changes" style="margin-top:4px">

		</form>

	</td>

  </tr>

</table> 

 <table border="0" cellspacing="0" cellpadding="4" style="margin-left:5px; margin-bottom:4px"> 

  <tr>

    <td>To use this code, simply copy and paste it into your web page.</td>

  </tr> 

  <tr>

    <td>Note that the <a href="view_suggested_styles.jsp">suggested CSS styles</a> are being applied to this page, and the resourceTitle background color has been changed to blue - view page source for details.</td>

  </tr>    

</table>



<p>Tip: If a search box is displayed below, try performing a search to see how the search results are displayed.</p>

<p><strong>The service commands are rendered below this line: </strong></p>

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





