<%@ include file="/JSTLTagLibIncludes.jsp" %>
<%@ page import="edu.ucar.dls.repository.*" %>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<jsp:useBean id="sessionBean" class="edu.ucar.dls.schemedit.SessionBean" scope="session"/>

<html:html>

<head>
<title>About</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"></meta>

<link rel='stylesheet' type='text/css' href='${contextPath}/styles.css'>
<script type="text/javascript" src="${contextPath}/lib/javascript/prototype.js"></script>
<script type="text/javascript">

function pageInit () {
	$$('img.logo').each ( function (logo) {
		logo.observe ('click', handleLogoClick);
		});
		
	$$('a').each ( function (link) {
		link.observe ('click', handleLinkClick);
		});
}

// get href and call goto
function handleLogoClick (event) {
	var a = event.element().up("a");
	var href = a.readAttribute("href");
	event.stop();
	goto (href);
}

// call goto with this link's href
function handleLinkClick (event) {
	var href = event.element().readAttribute("href");
	event.stop();
	goto (href);
}

// open href in the opener window and close about window
function goto(href) {
	var newwin = window.opener.open (href, "_blank");
	newwin.focus();
	window.close();
}

Event.observe (window, "load", pageInit);

</script>
<style type="text/css">
.logo {
	height:50px;
	border:none;
}

.link {
	font-size:110%;
	margin:10px 0px 0px 10px;
}

.link a {
	padding:3px;
}

.link a:hover {
	background-color:#ffcc00;
}

#logos {
	position:absolute;
	top:405px;
	left:0px;
	text-align:center;
	width:100%;
}
</style>
</head>
<body>

<div align="center" style="margin:20px 0px 30px 0px">
<img src="${contextPath}/images/NSDL_4PMS.gif" width="200px"/>
<h2 style="font-size:250%">Collection System</h2>
<p>Version @VERSION@</p>
</div>

<div class="link"><a href="http://sourceforge.net/project/showfiles.php?group_id=198325&package_id=269885">Download page on Sourceforge</a></div>
<div class="link"><a href="http://wiki.nsdl.org/index.php/Community:NCS">Documentation page on NCore wiki</a></div>

<div id="logos">
<input type="button" value="close" onclick="window.close()" />
<hr size="3" />
<table width="100%">
	<td width="33%" align="left">
		<a href="http://www.fedora-commons.org/confluence/display/EduPak/Home"><img class="logo" src="${contextPath}/images/edupak-ncs.jpg" /></a>
	</td>
	<td width="33%" align="center">
		<a href="http://www.nsf.gov/"><img class="logo" src="${contextPath}/images/nsfLogo.jpg" /></a>
	</td>
	<td width="33%" align="right">
		<a href="http://dlsciences.org/"><img class="logo" src="${contextPath}/images/dlsLogo.png" /></a>
	</td>
</table>

</div>
</body>
</html:html>
