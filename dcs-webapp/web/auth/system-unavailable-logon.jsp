<%-- 
This page was used when NSDL system was unavailable. Saved as template for
similar circumstances in the future.
--%>
<%@ include file="/JSTLTagLibIncludes.jsp" %>

<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<c:set var="title">Login</c:set>
<c:set var="guestUser" value="${applicationScope['guestUser']}" />
<html:html locale="true">
<head>
<script type="text/javascript" src="prototype.js"></script>
<title><st:pageTitle title="${title}" /></title>

<%-- <%@ include file="/baseHTMLIncludes.jsp" %> --%>
<link rel='stylesheet' type='text/css' href='auth-styles.css'>
<script type="text/javascript">

function doLoginAsGuest () {
		var params = {dest:"${param.dest}", username:"guest", password:"guest"};
		href = "${contextPath}/auth/logon.do?" + $H(params).toQueryString();
		window.location = href;
}
	
Event.observe (window, 'load', function (evnt) { 
	if ($('gestLoginButton'))
		Event.observe ($('gestLoginButton'), 'click', doLoginAsGuest, false); 
});

</script>
<style type="text/css">
#notice-box {
	margin-top:50px;
	width:500px;
	padding:20px;
	border:2px solid #990033;
}

#notice-box div {
	text-align:left;
	font-size:1.1em;
	line-height:1.3em;
	margin-bottom:10px;
}

#notice-box .title {
	text-decoration:underline;
	text-align:center;
	font-size:2em;
	font-weight:bold;
	color:#990033;;
	margin:10px 0px 15px 0px;
}



</style>
</head>

<body>

<%-- <%@ include file="header.jspf" %> --%>
<st:authPageHeader toolLabel="${title}" logoPath="${applicationScope.logo}"/>

<st:pageMessages />


<div align="center"><div id="notice-box">
<div class="title">System Unavailable</div>
<div>This system is unavailable due to a serious failure in a back-end hardware component.</div>

<div>We are working with the vendor to address this problem but do not expect this
system to be available in the next 24 hours.</div>

<div>Any change in this situation will be posted here as soon as possible.</div>

<div>Sorry for any inconvenience!</div>

<div>NSDL Technical Network Services - 1/24/2011 12:40am ET</div>
</div>
</div>


</body>
</html:html>
