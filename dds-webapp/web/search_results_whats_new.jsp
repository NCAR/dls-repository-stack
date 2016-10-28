<%@ page language="java" %>
<%@ page import="org.apache.lucene.document.Document" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglibs-application.tld" prefix="app" %>
<%@ taglib uri='/WEB-INF/tlds/vocabulary.tld' prefix='vocab' %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<%@ taglib uri='/WEB-INF/tlds/request.tld' prefix="req" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored ="false" %>

<%-- GetURL bean used in place of IO taglib: --%>
<jsp:useBean id="io" class="edu.ucar.dls.util.GetURL" scope="application"/>
<bean:define id="thisServer" value='<%= "http://" + request.getServerName() + ":" + request.getServerPort() %>' />

<jsp:useBean id="ddsQueryForm" class="edu.ucar.dls.dds.action.form.DDSQueryForm" scope="session"/>
	
<dds:setKeywordsHighlight keywords="<%= ddsQueryForm.getQ() %>" highlightColor="#000099" />

<html:html>  
<head>
	<title>DLESE: New Resources</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<link rel='stylesheet' type='text/css' href='<%= request.getContextPath() %>/discovery_styles.css'>
	 
	<%-- DLESE templates header: --%>
	<jsp:setProperty name="io" property="address" 
		value='<%= thisServer + "/dlese_shared/templates/header_refs.html"%>' />
	<bean:write name="io" property="url" filter="false" />
	
	<script type="text/javascript" src="<%= request.getContextPath() %>/dlese_script_vocab.js"></script>
	<script type="text/javascript"><!--
	dlese_DISCOVERY_ROOT = '<%= request.getContextPath() %>/';
	dlese_isSearchPage = true;
	dlese_pageHasVocabs = false;
	//-->
	</script>
	<style type='text/css'><!--
		.tabLinks { font-size: 0.9em; font-weight: bold; }
		TD H1 { font-size: 1.4em; }
	-->
	</style>
		
	<!-- RSS links here for compatibility with browser subscription mechanisms: -->
	<link rel="alternate" type="application/rss+xml" title="New resources" 
		href="${ thisServer }/dds/services/rss2-0?q=&s=0&sortby=wndate&wntype=itemnew&wnfrom=rss&show=200" />

</head>
<body bgcolor="#FFFFFF" text="#142929" link="#B75B00" vlink="#4E9A9A" alink="#FFBA04" margin="0" 
onLoad="dlese_pageOnLoad();">

<div class="dlese_sectionTitle" id="dlese_sectionTitle">
	New <br />
	Resources
</div>	

<%-- DLESE templates top: --%>
<jsp:setProperty name="io" property="address" 
	value='<%= thisServer + "/dlese_shared/templates/content_top.html"%>' />
<bean:write name="io" property="url" filter="false" />

<table width=100% border=0 cellpadding=0 cellspacing=0 style="padding-bottom: 12px"><td>

<h1>New resources in DLESE</h1>

<%-- 10-2007: Removed logic to show new reviews and new resources under review. Functionality is still in place if below is uncommented --%> 
<%-- <p style="text-align: center">
<req:equalsParameter name="wnfrom" match="recent">
	<req:equalsParameter name="wntype" match="itemannocomplete">
		<a href="new.htm">NEW RESOURCES IN DLESE</a> |
		<b>NEWLY REVIEWED RESOURCES</b> | 
		<a href="newToReview.htm">NEW RESOURCES UNDER REVIEW</a> 
	</req:equalsParameter>
	<req:equalsParameter name="wntype" match="itemannoinprogress">
		<a href="new.htm">NEW RESOURCES IN DLESE</a> |
		<a href="newReviewed.htm">NEWLY REVIEWED RESOURCES</a> | 
		<b>NEW RESOURCES UNDER REVIEW</b>
	</req:equalsParameter>
	<req:equalsParameter name="wntype" match="itemnew">
		<b>NEW RESOURCES IN DLESE</b> |
		<a href="newReviewed.htm">NEWLY REVIEWED RESOURCES</a> | 
		<a href="newToReview.htm">NEW RESOURCES UNDER REVIEW</a>
	</req:equalsParameter>
</req:equalsParameter>
<req:equalsParameter name="wnfrom" match="recent" value="false">
	<req:equalsParameter name="wntype" match="itemannocomplete">
		<a href="query.do?q=&s=&sortby=wndate&wnfrom=<req:parameter name='wnfrom' />&wntype=itemnew">NEW RESOURCES IN DLESE</a> |
		<b>NEWLY REVIEWED RESOURCES</b> | 
		<a href="query.do?q=&s=&sortby=wndate&wnfrom=<req:parameter name='wnfrom' />&wntype=itemannoinprogress">NEW RESOURCES UNDER REVIEW</a> 
	</req:equalsParameter>
	<req:equalsParameter name="wntype" match="itemannoinprogress">
		<a href="query.do?q=&s=&sortby=wndate&wnfrom=<req:parameter name='wnfrom' />&wntype=itemnew">NEW RESOURCES IN DLESE</a> |
		<a href="query.do?q=&s=&sortby=wndate&wnfrom=<req:parameter name='wnfrom' />&wntype=itemannocomplete">NEWLY REVIEWED RESOURCES</a> | 
		<b>NEW RESOURCES UNDER REVIEW</b>
	</req:equalsParameter>
	<req:equalsParameter name="wntype" match="itemnew">
		<b>NEW RESOURCES IN DLESE</b> |
		<a href="query.do?q=&s=&sortby=wndate&wnfrom=<req:parameter name='wnfrom' />&wntype=itemannocomplete">NEWLY REVIEWED RESOURCES</a> | 
		<a href="query.do?q=&s=&sortby=wndate&wnfrom=<req:parameter name='wnfrom' />&wntype=itemannoinprogress">NEW RESOURCES UNDER REVIEW</a>
	</req:equalsParameter>
</req:equalsParameter>
</p> --%>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td>
			<form name='dateRanges' action='query.do?' style='margin: 0px; padding: 0px;'>
				<input type='hidden' name='q' value=''>
				<input type='hidden' name='s' value='0'>
				<input type='hidden' name='sortby' value='wndate'>
				<input type='hidden' name='wntype' value='<req:parameter name="wntype" />'>
				<p>
				<b>Time period:</b> 
					<select name="wnfrom" onChange="document.forms.dateRanges.submit()">
						<option value="recent">The last six weeks</option>
						<logic:iterate id="date" name="ddsQueryForm" property="dateStrings" indexId="index">
							<logic:equal name="ddsQueryForm" property="wnfrom" value="<%= (String)date %>">
								<option value="<bean:write name='date' />" selected><bean:write 
									name="ddsQueryForm" property="dateStringsForUI" /></option>
							</logic:equal>
							<logic:notEqual name="ddsQueryForm" property="wnfrom" value="<%= (String)date %>">
								<option value="<bean:write name='date' />"><bean:write 
									name="ddsQueryForm" property="dateStringsForUI" /></option>
							</logic:notEqual>
						</logic:iterate>
					</select>
					(<bean:write name="ddsQueryForm" property="numResults"/> resources)
				</p>	
			</form>	
		</td>
		<td align="right">
			<%-- XML button/link to the RSS version of this news page --%>
			<c:set var="xmlUrl">
				${ thisServer }/dds/services/rss2-0?q=&s=0&sortby=wndate&wntype=${ param.wntype }&wnfrom=rss&show=200
			</c:set>		
			<a href="${ xmlUrl }" 
				title="RSS 2.0 feed for this page" style="text-decoration: none;"><span class="dlese_xmlButton">RSS</span></a>&nbsp;
		</td>
	</tr>
</table>

</td></table>

<logic:notEqual name="ddsQueryForm" property="numResults" value="0">
	
	<logic:iterate id="result" name="ddsQueryForm" property="results" indexId="index" >  			
		<logic:match name="result" property="docReader.readerType" value="ItemDocReader">			
			<table border="0" cellspacing="0" cellpadding="2" width="100%" hspace="0">
				<tr class='dlese_blue'>
					<td>
						<div class='resourceTitle'>
							<a href='<bean:write name="ddsQueryForm" property="resourceResultLinkRedirectURL" filter="false" 
							/>T=<bean:write name="ddsQueryForm" property="searchType" filter="false" 
							/>&amp;Q=<bean:write name="ddsQueryForm" property="qe"
							/>&amp;R=<bean:write name="index" 
							/>&amp;ST=<bean:write name="ddsQueryForm" property="numResults" filter="false" 
							/>&amp;LT=<bean:write name="ddsQueryForm" property="totalNumResources" filter="false" 
							/>&amp;ID=<bean:write name="result" property="docReader.id" filter="false" 
							/>*<bean:write name="result" property="docReader.url" filter="false" 
							/>' 
							target='_blank' class='resourceTitle'><bean:write name="result" property="docReader.title" 
							filter="false" /></a>
							<logic:equal name="result" property="docReader.partOfDRC" value="true">
								<a href='collection.do?key=drc'><img alt="DLESE Reviewed Collection"
									border="0" src="<%= request.getContextPath() %>/images/DRC_icon.gif"></a>
							</logic:equal>	
						</div>										
						<div class='resourceURL'>
							<a href='<bean:write name="ddsQueryForm" property="resourceResultLinkRedirectURL" filter="false" 
							/>T=<bean:write name="ddsQueryForm" property="searchType" filter="false" 
							/>&amp;Q=<bean:write name="ddsQueryForm" property="qe"
							/>&amp;R=<bean:write name="index" 
							/>&amp;ST=<bean:write name="ddsQueryForm" property="numResults" filter="false" 
							/>&amp;LT=<bean:write name="ddsQueryForm" property="totalNumResources" filter="false" 
							/>&amp;ID=<bean:write name="result" property="docReader.id" filter="false" 
							/>*<bean:write name="result" property="docReader.url" filter="false" 
							/>' 
								target='_blank'><bean:write name="result" property="docReader.urlTruncated" 
								filter="false" /></a>
						</div>
					</td>
					<td align="right" valign="top" nowrap> 
						<div>
							New: <bean:write name="result" property="docReader.whatsNewDate" />&nbsp;
						</div>
						<logic:greaterThan name="result" property="docReader.numTextAnnosInProgress" value="0">
							<div>							
								<a href='<bean:write name="result" property="docReader.textAnnosInProgress[0].url"/>' 
									target="_blank">Submit a review</a>&nbsp;	
							</div>
						</logic:greaterThan>				
					</td>
				</tr>
			</table>
			<%@ include file="description_very_brief.jsp" %>
		</logic:match>								  
	</logic:iterate>
	
	<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0">
		<td bgcolor="#999999" height="1"></td>
	</table>

</logic:notEqual>

<logic:equal name="ddsQueryForm" property="numResults" value="0">
	<table width=100% border=0 cellpadding=0 cellspacing=0 style="padding-bottom: 12px"><td>
		<p class="blueBand">None in this time period.</p>
	</td></table>
</logic:equal>


<%-- DLESE templates bottom: --%>
<jsp:setProperty name="io" property="address" 
	value='<%= thisServer + "/dlese_shared/templates/content_bottom.html"%>' />
<bean:write name="io" property="url" filter="false" />

</body>
</html:html>