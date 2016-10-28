<%@ include file="/JSTLTagLibIncludes.jsp" %>
<jsp:useBean id="svf" class="edu.ucar.dls.xml.schema.action.form.SchemaViewerForm"  scope="session"/>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglibs-application.tld" prefix="app" %>
<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> --%>

<c:set var="defaultDisplay" value="block"/>
<c:set var="indentAmount" value="18"/>

<html>
<head>
	<link rel="stylesheet" href="styles.css" type="text/css">
	<script type="text/javascript" src="${contextPath}/lib/javascript/prototype.js"></script>
	<script type="text/javascript" src="schema-viewer-support.js"></script>
	<%-- Indent the check box sub-menus by this many pixels --%>
	
	<title>Schema Viewer</title>
</head>
<body bgcolor="white">
<table border="0" width="100%">
	<tr valign="left" width="150px">
		<td align="left" width="33%">
			<img src="../images/DLESE_logo_sm.gif">
		</td>
		<td align="center" width="33%">
			<h1>Schema Viewer</h1>
			
		</td>
		<td align="right">
			<div class="collapsible-heading">Reports</div>
			<div class="nav-link"><a href="schema.do?command=typeReport">Schema Definition Type</a></div>
			<div class="nav-link"><a href="javascript:doFieldsFiles()">Fields Files</a></div>
		</td>
	</tr>
</table>
		
<html:form action="/schema/schema.do" method="Post">
<%-- <html:hidden property="frameworkName"/> --%>

<table width="100%">
	<tr>
		<td align="left"><h1>Framework: ${svf.frameworkName}</h1></td>
		<td align="right">
			<div class="control">
				<div class="collapsible-heading">Change Framework</div>
				<select name="frameworkSelect" id="frameworkSelect" onchange="doSetFramework()">
				<c:forEach var="frame" items="${svf.frameworks}">
						<option value="${frame}" 
							<c:if test="${frame == svf.framework.xmlFormat}">SELECTED</c:if>
						 >${frame}</option>
					</c:forEach> 
				</select>
			</div>
		</td>
	</tr>
</table>
<div class="schema-prop"> Xml Format: ${svf.framework.xmlFormat}</div>
<div class="schema-prop"> Version: ${svf.framework.version}</div>
<div class="schema-prop"> SchemaURI: 
	<a href="${svf.framework.schemaURI}" target="_blank">${svf.framework.schemaURI}</a></div>

<%-- Error messages --%>
<logic:messagesPresent>
	<p>
		<font color=red><b>Oops! - Errors were found in your input.</b> 
			<html:messages id="msg"><li><bean:write name="msg"/></li></html:messages></font>
	</p>
  <hr>
</logic:messagesPresent>

<%-- messages --%>
<logic:messagesPresent  message="true">
	<p>
		<font color=blue><b>
			<html:messages id="msg"  message="true"><li><bean:write name="msg"/></li></html:messages>
		</b></font>
	</p>
  <HR>
</logic:messagesPresent>

<%-- Controls --%>
	<div>
		<nobr>
		<a 	href="javascript:toggleVisibility('controls');" 
			title="Click to show/hide" 
			class="collapsible-heading"><img src='../images/btnExpand.gif' 
			alt="Click to show/hide" border="0" hspace="5" width="11" hight="11" />Controls</a>
		</nobr>							
	</div>

	<div id="controls" style=" width:100%;display:none">
		<div style="margin-left:${indentAmount}px">

			
				<div class="control">
					<html:text property="path" size="100"/>
					<input type="submit" name="command" value="doPath" />
				</div>
			
				<div class="control">
					<html:text property="typeName" size="50"/>
					<input type="submit" name="command" value="doType" />
				</div>	
		</div>
	</div>
</html:form>
<div class="breadcrumbs">${svf.breadCrumbs}</div>

<%-- <h3>Contents</h3> --%>
<%-- <h3>${svf.schemaNode.dataTypeName}</h3> --%>
<div class="info-box">
	<div style="margin-left:${indentAmount}px">
		<pre>${svf.prettyTypeElement}</pre>
	</div>
</div>


<%-- Schema Node --%>
<c:set var="schemaNode" value="${svf.schemaNode}" />
<c:if test="${not empty schemaNode}">

<div class="info-box">
	<div>
		<nobr>
		<a 	href="javascript:toggleVisibility('schemaNode');" 
			title="Click to show/hide" 
			class="collapsible-heading"><img src='../images/btnExpand.gif' 
			alt="Click to show/hide" border="0" hspace="5" width="11" hight="11" />Schema Node - ${svf.schemaNodeName}<c:if 
				test="${svf.schemaNodeIsRequired}"><font color="red"> (REQUIRED)</font></c:if></a>
		</nobr>							
	</div>

	<div id="schemaNode" style=" width:100%;display:${defaultDisplay}">
		<div style="margin-left:${indentAmount}px">
			<table style="width:100%;"><tr><td>
				<div class="schema-info">
					<c:choose>
						<c:when test="${svf.schemaNodeIsElement}">Element</c:when>
						<c:when test="${svf.schemaNodeIsAttribute}">Attribute</c:when> 
						<c:otherwise>${schemaNode.nodeType}</c:otherwise>
					</c:choose>
					- 
					<b>${schemaNode.typeDef.qualifiedName}</b>
					<c:if test="${schemaNode.validatingType.qualifiedName != schemaNode.typeDef.qualifiedName}">
						(<i>${schemaNode.validatingType.qualifiedName}</i>)
					</c:if>
				</div>

				<div class="schema-info"><b>Occurrence Info</b></div>
				<div class="schema-prop">
					<table cellpadding="0" cellspacing="0">
						<tr>
							<td align="right"><div class="attr-name">minOccurs:</div></td>
							<td align="left"><div class="attr-value">${schemaNode.minOccurs}</td>
						</tr>
						<tr>
							<td align="right"><div class="attr-name">maxOccurs:</div></td>
							<td align="left"><div class="attr-value">${schemaNode.maxOccurs}</td>
						</tr>
					</table>
				</div>				
				
				<div class="schema-info"><b>Attributes</b></div>
				<div class="schema-prop">
					<table cellpadding="0" cellspacing="0">
					<c:forEach var="attribute" items="${schemaNode.attributeNames}">
						<tr>
							<td align="right"><div class="attr-name">${attribute}:</div></td>
							<td align="left"><div class="attr-value">
								<bean:write name="schemaNode" property="attr(${attribute})" /></div></td>
						</tr>
					</c:forEach>
					</table>
				</div>
				
				<c:if test="${not empty svf.substitutionGroupMembers}">
					<div class="schema-info"><b>Substitution Elements</b></div>
					<c:forEach var="se" items="${svf.substitutionGroupMembers}">
						<div class="schema-prop">${se}</div>
					</c:forEach>
				</c:if>
				
				<c:if test="${not empty svf.headElement}">
					<div class="schema-info"><b>Head Element</b></div>
					<div class="schema-prop">${svf.headElement}</div>
				</c:if>				
			</td></tr></table>
		</div>
	</div>
	
</div>
</c:if>

<%-- Global Def --%>
<c:set var="def" value="${svf.globalDef}" />
<c:if test="${not empty def}">
<div class="info-box">
	<div>
		<nobr>
		<a 	href="javascript:toggleVisibility('xsdType');" 
			title="Click to show/hide" 
			class="collapsible-heading"><img src='../images/btnExpand.gif' 
			alt="Click to show/hide" border="0" hspace="5" width="11" hight="11" />XSD Type - ${def.qualifiedName}</a>
		</nobr>							
	</div>	
	<div id="xsdType" style=" width:100%;display:${defaultDisplay}">
		<div style="margin-left:${indentAmount}px">
			<table style="width:100%;"><tr><td>
				
				<div class="schema-info">${def.type}</div>
				
				<c:choose>
					<c:when test="${def.type == 'simpleType'}">
						<c:choose>
							<c:when test="${svf.defIsEnumeration}">
								<div class="schema-info">Enumeration</div>
								<c:forEach var="enum" items="${svf.enumerationOptions}" >
									<div class="schema-prop">${enum}</div>
								</c:forEach>
							</c:when>
							<c:when test="${svf.defIsUnion}">
								<div class="schema-info">Union</div>
								<c:forEach var="member" items="${svf.unionMembers}" >
									<div class="schema-prop">${member}</div>
								</c:forEach>
							</c:when>							
							<c:otherwise>
								<div class="schema-info">Unrecognized def style</div>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:when test="${def.type == 'complexType'}">
						<c:choose>
							<c:when test="${not empty def.compositor}">
								<div class="schema-info">compositor: <b>${def.compositor.name}</b></div>
									<div class="schema-prop">
										<table cellpadding="0" cellspacing="0">
											<c:forEach var="prop" items="${fn:split ('minOccurs maxOccurs memberNames', ' ')}">
											<tr>
												<td align="right"><div class="attr-name">${prop}:</div></td>
												<td align="left">
													<div class="attr-value">
													<c:catch var="err">
 														<c:if test="${not empty def.compositor[prop]}">
															${def.compositor[prop]}
														</c:if>
														</c:catch>
														<c:if test="${not empty err}"><i>could not obtain ${prop}</i></c:if>
													</div>
												</td>
											</tr>
											</c:forEach>
										</table>
									</div>	
							</c:when>
							<c:otherwise>
								<div class="schema-info"><i>complex type details go here ...</i></div>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:when test="${svf.defIsBuiltin}">
						<div class="schema-info">Built-in type definitions are not displayed</div>
					</c:when>
					<c:otherwise>
						<div class="schema-info">I don't know how to handle a ${def.type} ...</div>
					</c:otherwise>
				</c:choose>
				
				<%-- <div class="schema-info">location: <a href="${def.location}" target="_blank">${def.location}</a></div> --%>
				<div class="schema-info">location: 
					<span class="nav-link"><a href="${def.location}" target="_blank">${def.location}</a></span>
				</div>
			
			</td></tr></table>
		</div>
	</div>
</div>
</c:if>

<%-- Minimal Tree --%>
<c:if test="${not empty svf.minimalTree}">
<div class="info-box">
	<div>
		<nobr>
		<a 	href="javascript:toggleVisibility('minimalTree');" 
			title="Click to show/hide" 
			class="collapsible-heading"><img src='../images/btnExpand.gif' 
			alt="Click to show/hide" border="0" hspace="5" width="11" hight="11" />Minimal Tree</a>
		</nobr>							
	</div>

	<div id="minimalTree" style=" width:100%;display:${defaultDisplay}">
		<div style="margin-left:${indentAmount}px">
			<table style="width:100%;"><tr><td>
				<pre>${svf.minimalTree}</pre>
			</td></tr></table>
		</div>
	</div>
</div>
</c:if>

</body>
</html>
