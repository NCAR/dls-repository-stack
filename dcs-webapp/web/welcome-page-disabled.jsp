<%@ page isELIgnored ="false" %>
<%@ page language="java" %>
<jsp:useBean id="sef" class="edu.ucar.dls.schemedit.action.form.SchemEditForm"  scope="session"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set target="${sef}" property="pageTitle" value="DCS Prototype Home Page"/>
<html>
<head>
		<link rel="stylesheet" href="styles.css" type="text/css">
		<title>${sef.pageTitle}</title>
</head>

<body bgcolor="white">
<div align="center">
<table border="0" width="67%%">
	<tr valign="middle" align="center">
		<td>
			<div style="margin-top:100px;"><img src="images/DLESE_logo_sm.gif"></img>
			<h1>Welcome to the DLESE Collection System
				<div style="font-size:300%;font-weight:bold">DCS</div>
			</h1>
		</td>
	</tr>
	<tr align="center">
		<td>
			<h3>Feedback and suggestions are most welcome!</h3>
			<p>Please either email any feedback (including comments, suggestions, related work, bug reports) to 
			<a href="mailto:ostwald@ucar.edu">ostwald@ucar.edu</a> or
			make them via the <a href="http://swiki.dlese.org/Project-DCS/101">DCS Prototype Discussion Page</a> of 
			the <a href="http://swiki.dlese.org/Project-DCS">DCS Project Swiki</a>.</p>
			
			<p>Thanks in advance for your input!</p>
		</td>
	</tr>
	<tr align="center">
		<td>
			<div style="width:300;margin-top:30px;padding:15px;font-weight:bold;font-size:180%;font-style:italic;border:2px solid #333366;">
				<a href="admin/browse.do" style="color:green">Click Here to Enter</a>
			</div>
		</td>
	</tr>
</table>
</div>


</body>
</html>
<html>
