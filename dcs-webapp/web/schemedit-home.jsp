<%@ page isELIgnored ="false" %>
<%@ page language="java" %>
<jsp:useBean id="sef" class="edu.ucar.dls.schemedit.action.form.SchemEditForm"  scope="session"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:setProperty name="sef" property="pageTitle" value="Schemedit Home Page"/>
<html>
<head>
		<link rel="stylesheet" href="styles.css" type="text/css">
		<title>Schemedit Home Page</title>
</head>

<body bgcolor=white>
<table border="0">
	<tr valign="middle">
		<td align=center><img src="images/DLESE_logo_sm.gif"></td>
		<td width="50px">&nbsp;</td>
		<td align="center"><h1>${sef.frameworkName} Metadata Editor</h1>
		</td>
	</tr>
</table>

<h3>About SchemEdit</h3>

<p>SchemEdit is a subproject of the <a href="http://swiki.dlese.org/Project-DCS">CDS (DLESE Cataloging System) Project</a> within 
<a href="http://www.dlese.org">DLESE</a>.

<p>SchemEdit is a substrate for Schema-based XML editors. Based on an XML Schema, and some configuration information,
SchemEdit automatically constructs an editor as a series of JSP pages. This website provides access to three SchemEdit editors
supporting three different metadata frameworks (represented by three XML Schemas).

<ul>
	<li><a href="adn/adn.do">ADN Editor</a></li>
	<li><a href="news_opps/news_opps.do">News &amp; Opps Editor</a></li>
	<li><a href="dlese_collect/dlese_collect.do">Collection Record Editor</a></li>
	<li><a href="admin/browse.do">Collection Manager (<span style="color:#cc0000">new!</span>)</a></li>
</ul>

<p>A <a href="schema/schema.do">SchemaViewer tool</a> is also provided to facilitate understanding of complex schemas.</p>

<h3>The ADN Editor</h3>
<p>The main application of SchemEdit is the ADN Editor, which will be used in the next-generation DLESE Cataloging System (DCS).<p>

	<ul>
		<li>More information about the ADN Editor project can be found 
		<a href="http://swiki.dlese.org/Project-DCS/98">here</a> 
		(a page within the DCS project swiki).</li>
		<li>Information about the ADN metadata framework can be found
		<a href="http://www.dlese.org/Metadata/adn-item/index.htm">here</a> (a page within the DLESE Metadata site)</li>
	</ul>

<h3>SchemEdit Functionality</h3>
<p>SchemEdit currently supports the following functionality:
	<ul>
		<li> Field Validation against Schema Data Types</li>
		<li> Validation/notification of completed or missing required metadata</li>
		<li> Access to all metadata fields in framework, indicating required and optional</li>
		<li> Controlled vocabulary enforcement</li>
		<li> Edit existing records and create new ones</li>
		<li> Get Records using <a href="http://swiki.dpc.ucar.edu/Project-Discovery/108">DDS web services</a></li>
	</ul>
</p>

<h4>Coming Soon</h4>
<p>
	<ul>
		<li> "Bad character" (unencoded special characters) checking</li>
		<li> Spell checking </li>
	</ul>
</p>
<p>We are working to incorporate more <i>framework-specific information</i> into SchemEdit to augment the information contained in
an XML Schema. The following types of information will allow the editors to provide more support for users to create and edit
records:
<ul>
	<li> UI Field Labels - to replace the often-cryptic element names used in schema definitions</li>
	<li> Field grouping - break up long lists into hierarchies to help users understand and make choices</li>
	<li> Vocabulary Definitions - to define fields and controlled vocabulary terms</li>
	<li> Best Practices information - to guide the user in creating "good" metadata</li>
</ul>
</p>

<h3>Feedback and suggestions are most welcome!</h3>
Please either email any feedback (including comments, suggestions, related work, bug reports) to 
<a href="mailto:ostwald@ucar.edu">ostwald@ucar.edu</a> or
make them via the <a href="http://swiki.dlese.org/Project-DCS/101">ADN Editor Discussion Page</a> of 
the <a href="http://swiki.dlese.org/Project-DCS">DCS Project Swiki</a>. Thanks in advance for your input!
</body>
</html>
<html>
