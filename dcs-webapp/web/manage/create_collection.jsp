<%@ include file="/JSTLTagLibIncludes.jsp" %>
<resp:setHeader name="Cache-Control">cache</resp:setHeader>
<c:set var="contextPath"><%@ include file="/ContextPath.jsp" %></c:set>
<html:html>
<head>
<title><st:pageTitle title="Create Collection Form" /></title>

<%@ include file="/baseHTMLIncludes.jsp" %>
<script language="JavaScript" src="${contextPath}/lib/best-practices-link-support.js"></script>
<script>
	// Override the method below to set the focus to the appropriate field.
	function sf(){}
	
	var infos = new Array ("noInfo", "adn", "dlese_anno", "news_opps");
		
	/*
		control display of format-specific questions
		- explanation is only displayed when the selected format has questions
	*/
	function showInfo (format) {
		var focus_obj;
		var explanation = $("explanation");
		explanation.setStyle ({display:'none'});
		if (format == "") focus_obj == null;
		else {
			focus_obj = $(format+"_info");
			}
		for (var i=0;i<infos.length;i++) {
			var id = infos[i] + "_info";
			var obj = $( id );
			if (obj != null && obj != focus_obj)
				obj.setStyle ({display:'none'});
			if (obj != null && obj == focus_obj) {
				explanation.setStyle({display:'none'});
				obj.setStyle ({display:''});
			}
		}
	}
	
</script>

<c:set var="left_col_width" value="150" />
<c:set var="allowProvidedCollectionKeys" value="${initParam['allowProvidedCollectionKeys'] == 'true'}" />
</head>
<body onload="showInfo('${csForm.formatOfRecords}');">

<%-- page header --%>
<st:pageHeader currentTool="manage" toolLabel="Create Collection" />

<p>Create a new collection by completing the form below. Please note that 
the <b><em>ID Prefix</em></b> 
<c:choose>
<c:when test="${allowProvidedCollectionKeys}"> and <b><em>Collection Key</em></b> fields</c:when>
<c:otherwise>field</c:otherwise>
</c:choose>
can only contain alphanumeric characters, hyphens and underscores. </p>

<p>You must Click on the <b><em><a href="#submit">submit button</a></em></b> at the
bottom of the form to create the new collection.</p>

<st:pageMessages />
	
<html:form action="/manage/collections" method="GET">
	<html:hidden property="status"/>
	<html:hidden property="policyUrl"/>
	<html:hidden property="policyType"/>
	<table cellspacing="5px">

		<tr valign="top">
			<td>Collection title</td>
			<td>		             
				<st:fieldMessage propertyId="shortTitle"/>
                <html:text property="shortTitle" size="40"/>
			</td>
		</tr>	
 		<tr valign="top">
			<td>Item Record Format</td>
			<td>
				<st:fieldMessage propertyId="formatOfRecords"/>
          <html:select property="formatOfRecords"  styleId="formatOfRecords" onchange="showInfo(this.form.formatOfRecords.value)">
					<html:optionsCollection property="formats" />
				</html:select>
			</td>
		</tr>	 
		
		<tr valign="top">
			<td>ID Prefix</td>
			<td>
				<st:fieldMessage propertyId="idPrefix"/>
				<html:text property="idPrefix" size="20"/>
			</td>
		</tr>	
		
	<c:if test="${allowProvidedCollectionKeys}">
		<tr valign="top">
			<td>Collection Key</td>
			<td>
				<st:fieldMessage propertyId="collectionKey"/>
				<html:text property="collectionKey" size="20"/>
			</td>
		</tr>
	</c:if>
		
	</table>
	
	<div id="explanation" style="display:none">
	<h3>Item Record Format-specific Questions</h3>
	<p>The boxes below allow you to define values that will be automatically inserted into each item record
	you create for this collection. These fields are optional -- the values for these fields can be defined 
	or modified at a later time via the <i>Settings</i> page.</p>
	</div>
	
	<table id="adn_info" style="display:none">
		<tr valign="top">
			<td colspan="2"><h4>ADN questions</h4></td>
		</tr>
		<tr valign="top">
			<td width="${left_col_width}px">
				Terms of Use
				<div>
					<st:bestPracticesLink4NamedPath pathName="termsOfUse" xmlFormat="adn"/>
				</div>
			</td>
			<td>
				<div>What is the metadata terms of use of the collection?</div>
				<html:textarea rows="5" cols="50" property="termsOfUse">${csForm.termsOfUse}</html:textarea>
			</td>
		</tr>
		<tr valign="top">
			<td width="${left_col_width}px">
				Terms of Use URI
				<div>
					<st:bestPracticesLink4NamedPath pathName="termsOfUseURI" xmlFormat="adn" />
				</div>
			</td>
			<td>
				<div>Is there a URL to the metadata terms of use?</div>
				<html:text size="70" property="termsOfUseURI" />
			</td>
		</tr>
		<tr valign="top">
			<td width="${left_col_width}px">
				Copyright
				<div>
					<st:bestPracticesLink4NamedPath pathName="copyright" xmlFormat="adn" />
				</div>
			</td>
			<td>
				<div>What is the copyright of the metadata?</div>
				<html:textarea rows="3" cols="50" property="copyright">${csForm.copyright}</html:textarea>
			</td>
		</tr>
	</table>
	
	<table id="dlese_anno_info"  style="display:none">
		<tr valign="top">
			<td colspan="2"><h4>Annotation questions</h4></td>
		</tr>
		<tr valign="top">
			<td width="${left_col_width}px">
				Annotation Service
				<div class="best-practices">
					<st:bestPracticesLink4NamedPath pathName="serviceName" xmlFormat="dlese_anno" />
				</div>
			</td>
			<td>
				<div>What is the name of the annotation service?</div>
				<html:textarea rows="5" cols="50" property="serviceName">${csForm.termsOfUse}</html:textarea>
			</td>
		</tr>
	</table>
	<br />
	<table cellspacing="0" cellpadding="5" width="100%">
		<tr class='admin_blue1'>
			<td width="${left_col_width}px">&nbsp;</td>
			<td>
				<a name="submit"></a>
				<html:submit property="command" value="submit" />
				<html:submit property="command" value="cancel" />
			</td>
		</tr>
	</table>
	
</html:form>

</body>
</html:html>
