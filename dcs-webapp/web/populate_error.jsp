<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>

<c:set var="contextPath"><%@ include file="ContextPath.jsp" %></c:set>
<c:set var="title">Metadata Editor Error</c:set>
<html:html>

<head>
<title>${title}</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<%@ include file="/baseHTMLIncludes.jsp" %>

</head>

<body>

<st:pageHeader toolLabel="${title}" />

<table width="100%" border="0" align="center">
  <tr> 
    <td> 
  
<p>The system is unable to accomodate your request. Likely causes include:
<li>Your session has timed out due to activity</li>
<li>You have used the back button to return to a page that is no longer valid.</li>


<blockquote>Please use the links at the top of this page to navigate to your desired location</blockquote>
</p>
  
	</td>
  </tr>
  </table>

</body>
</html:html>

