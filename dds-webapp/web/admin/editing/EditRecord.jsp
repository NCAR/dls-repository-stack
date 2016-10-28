<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglibs-application.tld" prefix="app" %>
<%@ taglib uri='/WEB-INF/tlds/response.tld' prefix='res' %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored ="false" %>

<%-- Redirect if requested... --%>
<logic:messagesPresent>
	<html:messages id="msg" property="redirect.fullview">
		<c:redirect url="/admin/display.do">
			<c:param name="fullview" value="${def.id}"/>
			<c:param name="action" value="save"/>
		</c:redirect>
	</html:messages>
</logic:messagesPresent>
			
<html:html>
<head>
<title>Edit Record</title>
<link rel='stylesheet' type='text/css' href='dds_admin_styles.css'>

<%@ include file="/nav/head.jsp" %>
</head>
<body>
<!-- Sub-title just underneath DLESE logo -->
<div class="dlese_sectionTitle" id="dlese_sectionTitle">
   Edit<br/> Record
</div>
<%@ include file="/nav/top.jsp" %>
	
	<h1>Edit Record</h1>
	
	<c:url value="/admin/display.do" var="fullViewUrl">
		<c:param name="fullview" value="${def.id}"/>
	</c:url>
		
	<%-- ####### Display messages, if present ####### --%>
	<logic:messagesPresent> 		
		<div style="border: 1px solid #444; padding: 6px; margin: 6px; background-color: #eee; width: 400px;">
				<b>Messages:</b>
				<ul>
					<html:messages id="msg" property="message"> 
						<li><bean:write name="msg"/></li>									
					</html:messages>
					<html:messages id="msg" property="error"> 
						<li><font color=red>Error: <bean:write name="msg"/></font></li>									
					</html:messages>
				</ul>
				[ <a href="editing.do">OK</a> ]
		</div>
	</logic:messagesPresent>
	
	<%-- <html:form action="/admin/editing" method="post" 
		onsubmit="JavaScript:return confirm('Would you like to insert the XML for this record from the data base into the editing form below?')">  
		<table border=0 cellpadding=4 cellspacing=0>						
			<tr>
				<td>
					Get a record for editing: 
					<html:text property="id" name="def" size="60"/>					
					<html:submit property="button" value="Get record" />
					<input type="hidden" name="verb" value="GetRecord">
				</td>
			</tr>
		</table>
	</html:form> --%>	
	
	<html:form action="/admin/editing" method="post"> 
		<div>
			<html:textarea property="recordXml" name="def" cols="105" rows="28" />
		</div>
		<div style="margin-top: 4px">
			<span style="font-size: 110%; font-weight: bold;">Record ID</span> (Required only if can't be determined from the record XML, otherwise ignored): <br/><html:text property="recordId" name="def" size="100"/>					
		</div>
		<div style="font-size: 110%; font-weight: bold; margin:10px 0px 10px 0px;">
			Collection: <span style="font-style:italic; color: #444">${def.collection}</span>, 
			XML format: <span style="font-style:italic; color: #444">${def.xmlFormat}</span>
		</div>
		<html:hidden property="collection" name="def" />
		<html:hidden property="xmlFormat" name="def" />
		<input type="hidden" name="verb" value="PutRecord">
		<br/><html:submit property="button" value="Save record" />
		<input name="cancelButton" value="Cancel" type="button" onclick="window.location='${f:jsEncode(fullViewUrl)}'"/>
	</html:form>	
	
	<%-- <h2>Delete record</h2>
	<html:form action="/admin/editing" method="post" onsubmit="JavaScript:return confirm('Delete record?')">  
		<table border=0 cellpadding=4 cellspacing=0>						
			<tr>
				<td>
					ID of record to delete: <br/><html:text property="deleteRecord" name="def" size="40"/>					
					<html:submit property="button" value="Delete record" />
					<input type="hidden" name="verb" value="DeleteRecord">
				</td>
			</tr>
		</table>
	</html:form> --%>	
	<br/><br/><br/><br/><br/><br/>
<%@ include file="/nav/bottom.jsp" %>	
</body>
</html:html>


