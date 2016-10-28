<%@ page language="java" %>
<%@ taglib prefix="c" uri="/WEB-INF/tlds/c.tld" %>

<jsp:useBean id="exampleVocabsForm" class="edu.ucar.dls.dds.action.form.VocabForm" scope="request"/>
<%-- Indicate the UI system.interface.language trio (XML groups file) to be used for generating vocab display: --%>
<jsp:setProperty name="exampleVocabsForm" property="vocabInterface" value="dds.descr.en-us"/>
	
<html>
<head>
	<title>DLESE Controlled Vocabulary Rendering Example</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<link rel='stylesheet' type='text/css' href='/dlese_shared/dlese_styles.css'>
</head>
<body bgcolor="#FFFFFF" text="#142929" link="#B75B00" vlink="#4E9A9A" alink="#FFBA04" margin="0">	

<h3>Attributes of edu.ucar.dls.vocab.VocabNode</h3>
<p>
Each of the following attributes can be accessed in one of two ways:</p>
<ul>
	<li>In the action/form classes using standard bean getter methods, 
		i.e. <tt>vocabTerm.getAttribute();</tt></li>
	<li>In the JSP page (preferred way, as the VMS is primarily a UI facilitator) using
	JSTL tags, i.e. <tt>&lt;c:out value="&#36;{vocabTerm.attribute}" /&gt;</tt></li>
</ul>
<p>
<table>
	<tr>
		<td><b>Attribute</b></td><td><b>Description</b></td>
	</tr>
	<tr>
		<td>fieldId</td><td>Encoded system ID for this term's FIELD (not value!)</td>
	</tr>
	<tr>
		<td>id</td><td>Encoded system ID for this term's VALUE</td>
	</tr>	
	<tr>
		<td>name</td><td>Metadata name for this term's VALUE</td>
	</tr>
	<tr>
		<td>label</td><td>Standard UI label for this term</td>
	</tr>
	<tr>
		<td>labelAbbrev</td><td>Abbreviated UI label for this term</td>
	</tr>
	<tr>
		<td>definition</td><td>Definition for this term</td>
	</tr>
	<tr>
		<td>noDisplay</td><td>Indicates that this term should not be displayed in this UI</td>
	</tr>
	<tr>
		<td>wrap</td><td>If rendering as columns, please start a new column after this term</td>
	</tr>
	<tr>
		<td>divider</td><td>Please place a divider (horizontal rule) after this term</td>
	</tr>
	<tr>
		<td>hasSubList</td><td>True only when the term is a sub-header (grouping)</td>
	</tr>
	<tr>
		<td>isLastInSubList</td><td>True when the term is the last item in a sub-grouping</td>
	</tr>
</table>
</p>

<h3>Example translations</h3>
<p>Given field name "<%= request.getParameter( "field" ) %>", what is it's:</p>
<ul>
	<jsp:setProperty name="exampleVocabsForm" 
		property="field" value="<%= request.getParameter( "field" ) %>" />
	<li>UI label: <c:out value="${exampleVocabsForm.fieldLabel}" /></li>
	<li>Encoded ID: <c:out value="${exampleVocabsForm.fieldId}" /></li>
</ul>

<p>Given field/value term "resourceType=DLESE:Learning materials:Course", what is it's:</p>
<ul>
	<%-- Let the form bean know which vocab term we'd like to examine: --%>
	<jsp:setProperty name="exampleVocabsForm" property="field" value="resourceType" />
	<jsp:setProperty name="exampleVocabsForm" property="value" value="DLESE:Learning materials:Course" />
	<%-- Grab the term: --%>
	<c:set var="term" value="${exampleVocabsForm.vocabTerm}" />
	<%-- Display term properties: --%>
	<li>UI label: <c:out value="${term.label}" /></li>
	<li>Encoded <em>field</em> ID: <c:out value="${term.fieldId}" /></li>
	<li>Encoded value ID: <c:out value="${term.id}" /></li>
	<li>"noDisplay" property: <c:out value="${term.noDisplay}" /></li>
</ul>

<%-- Let the form bean know which vocab field we'd like to iterate over 
	(note that this and UI system trio could be set in the action class instead) --%>
<jsp:setProperty name="exampleVocabsForm" property="field" value="<%= request.getParameter( "field" ) %>" />
<h3>Example iteration over <c:out value="${exampleVocabsForm.fieldLabel}" /></h3>
<p>
<table border=1 cellpadding=2 cellspacing=1>
	<tr>
		<td><b>fieldId</b></td>
		<td><b>id</b></td>
		<td><b>name</b></td>
		<td><b>label</b></td>
		<td><b>labelAbbrev</b></td>
		<td><b>definition</b></td>
		<td><b>noDisplay</b></td>
		<td><b>wrap</b></td>
		<td><b>divider</b></td>
		<td><b>hasSubList</b></td>
		<td><b>isLastInSubList</b></td>
	</tr>
	
	<%-- Iterate over the terms in the field and display their VocabNode properties --%>
	<c:forEach var="vocabTerm" items="${exampleVocabsForm.vocabList}">
		<tr>
			<td><c:out value="${vocabTerm.fieldId}" /></td>
			<td><c:out value="${vocabTerm.id}" /></td>
			<td><c:out value="${vocabTerm.name}" /></td>
			<td><c:out value="${vocabTerm.label}" /></td>
			<td><c:out value="${vocabTerm.labelAbbrev}" /></td>
			<td><c:out value="${vocabTerm.definition}" /></td>
			<td><c:out value="${vocabTerm.noDisplay}" /></td>
			<td><c:out value="${vocabTerm.wrap}" /></td>
			<td><c:out value="${vocabTerm.divider}" /></td>
			<td><c:out value="${vocabTerm.hasSubList}" /></td>
			<td><c:out value="${vocabTerm.isLastInSubList}" /></td>
		</tr>
	</c:forEach>
	
</table>
</p>

</body>
</html> 
