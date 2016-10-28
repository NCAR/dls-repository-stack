<%@ page language="java" %>
<%@ page import="org.apache.lucene.document.Document" %>
<%@ page import="edu.ucar.dls.vocab.MetadataVocab" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglibs-application.tld" prefix="app" %>
<%@ taglib uri='/WEB-INF/tlds/vocabulary.tld' prefix='vocab' %>
<%@ taglib uri='/WEB-INF/tlds/utils.tld' prefix='util' %>
<%@ taglib uri='/WEB-INF/tlds/dds.tld' prefix='dds' %>
<%@ taglib uri="/WEB-INF/tlds/taglibs-xtags.tld" prefix="xtags" %>
<%@ include file="JSTLTagLibIncludes.jsp" %>


<jsp:useBean id="ddsViewResourceForm" class="edu.ucar.dls.dds.action.form.DDSViewResourceForm" scope="session"/>
<xtags:parse>
	<util:readFile filename="<%= ddsViewResourceForm.getRecordFilename() %>" />
</xtags:parse>

<html:html>
<head>
<title>DLESE Reviews &amp; notes for 
	<bean:write name="ddsViewResourceForm" property="resourceTitle"/>
</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel='stylesheet' type='text/css' href='/dlese_shared/dlese_styles.css'>
<link rel='stylesheet' type='text/css' href='<%= request.getContextPath() %>/discovery_styles.css'>
<script type="text/javascript" src="/dlese_shared/dlese_script_nav.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/dlese_script_vocab.js"></script>
<script type="text/javascript"><!--
dlese_DISCOVERY_ROOT = '<%= request.getContextPath() %>/';
//dlese_navSelected = 'dlese_edres';	// Which navigation button is selected?
//-->
</script> 
<script type="text/javascript" src="/dlese_shared/dlese_site_menus.js"></script>
</head>
<body bgcolor="#FFFFFF" text="#142929" link="#B75B00" vlink="#4E9A9A" alink="#FFBA04" margin="0"
onLoad="dlese_searchOnLoad()">	
<a name="top"></a>

<%@ include file="search_navigation.jsp" %>

<div class="dlese_pageContentHighBanner"> 
<!-- Breadcrumb banner: -->
<div class="breadcrumbBanner">
	<div class="breadcrumbs">
		Educational resources &gt; Find a resource &gt; Reviews &amp; notes 
	</div>
</div>
<table border="0" cellspacing="0" cellpadding="0" width="100%" height="1" hspace="0"> <!-- Grey line -->
<td bgcolor="#999999" height="1"></td></table>
<!-- Start page content -->


<div class="pageTitleNoTable">
	Reviews, teaching tips, and related resources for: <a href='<bean:write name="ddsViewResourceForm" property="resourceResultLinkRedirectURL" filter="false" 
		/>T=reviews*<bean:write name="ddsViewResourceForm" 
		property="resourceUrl"/>' target='_blank'><bean:write name="ddsViewResourceForm" property="resourceTitle"/></a>
</div>

<logic:match name="ddsViewResourceForm" property="primaryResultDoc.docReader.partOfDRC" value="true">
<div class="reviewBlock">
	This resource is part of the <a href='collection.do?key=drc'>DLESE Reviewed Collection</a>
	<a href='collection.do?key=drc'><img alt="DLESE Reviewed Collection"
		border="0" src="images/DRC_icon.gif"></a>	
	<c:forEach var="pathway" items="${ ddsViewResourceForm.primaryResultDoc.docReader.annoPathways }">
		<logic:equal name="pathway" value="CRS (Community Review System)">
			admitted by way of the <a href="/dds/collection.do?key=crs">CRS Annotated Collection</a>
		</logic:equal>
	</c:forEach>	
</div>
</logic:match>

<style type="text/css"><!--
	.reviewBlock {
		margin-left: 25px; 
		margin-right: 5px;
		margin-top: 8px; 
		margin-bottom: 10px;
		font-family: arial, helvetica, sans-serif;
	}
-->
</style>

<logic:iterate name="ddsViewResourceForm" property="itemDocReader.completedReviews" id="annotation">
	<logic:empty name="annotation" property="url">
	<div class="blueBand">Review</div>
	</logic:empty>
	<logic:notEmpty name="annotation" property="url">
	<div class="blueBand"><a href='<bean:write name="annotation" property="url"/>' target='_blank'>Review</a></div>
	</logic:notEmpty>
	<div class="reviewBlock">
		<div>Presents a formal or informal evaluation of a resource.</div>
		<bean:define name="annotation" property="collectionKey" id="myAnKey" />
		<div><span style="font-style: italic;">Provided via:
			<a href='collection.do?ky=<%= myAnKey %>'><vocab:uiLabel 
				system="dds" interface="descr" language="en-us" 
				getAbbreviated="false" field="ky" value="<%= (String)myAnKey %>" /></a>					
		</span>		
		</div><div>
			<logic:notEmpty name="annotation" property="description">										
				<br><bean:write name="annotation" property="description"/></logic:notEmpty>					
		</div>
	</div>	
</logic:iterate>



<!-- Note: this block repeats twice: hear and near the end of this page -->

<c:set var="submitAnnotationLinks">
	<logic:iterate name="ddsViewResourceForm" property="itemDocReader.textAnnosInProgress" id="annotation">		
		<%-- Make sure CRS submission link only appears once --%>
		<c:if test="${annotation.collectionKey != '06' || hasCrsLink != 'true' }">
			<c:if test="${annotation.collectionKey == '06'}">
				<c:set var="hasCrsLink" value="true"/>
			</c:if>	
			<div class="blueBand">Currently open for review</div>
			<div class="reviewBlock">
				<bean:define name="annotation" property="collectionKey" id="myAnKey" />
				<div><span style="font-style: italic;">Being Provided via:
					<a href='collection.do?ky=<%= (String)myAnKey %>'><vocab:uiLabel 
						system="dds" interface="descr" language="en-us" 
						getAbbreviated="false" field="ky" value="<%= (String)myAnKey %>" /></a><logic:notEmpty 
						name="annotation" property="url">.</span>&nbsp;
						<a href='<bean:write name="annotation" property="url"/>' target="_blank">Submit a review</a>
						</logic:notEmpty>					
				</div><div>
					<logic:notEmpty name="annotation" property="description">										
						<br><bean:write name="annotation" property="description"/></logic:notEmpty>					
				</div>
			</div>	
		</c:if>
	</logic:iterate>
</c:set>

<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.completedReviews">
	${submitAnnotationLinks}
</logic:notEmpty>
	

<%-- Find out if a teaching tip *or* comment exists: --%>
<c:set var="hasCommentOrTeachingTip" value="false" />

<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.completedTeachingTips">
	<c:set var="hasCommentOrTeachingTip" value="true" />
</logic:notEmpty> 
<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.completedComment">
	<c:set var="hasCommentOrTeachingTip" value="true" />
</logic:notEmpty>

<c:if test="${hasCommentOrTeachingTip == 'true'}">
	<%-- TT or comment exists, now find out the URL: --%>
	<c:set var="annoUrl" value="unknown" />
	<logic:iterate name="ddsViewResourceForm" property="itemDocReader.completedTeachingTips" id="annotation">
		<logic:notEmpty name="annotation" property="url">
			<c:set var="annoUrl"><bean:write name="annotation" property="url"/></c:set>
		</logic:notEmpty>
	</logic:iterate>
	<logic:iterate name="ddsViewResourceForm" property="itemDocReader.completedComment" id="annotation">
		<logic:notEmpty name="annotation" property="url">
			<c:set var="annoUrl"><bean:write name="annotation" property="url"/></c:set>
		</logic:notEmpty>
	</logic:iterate>
	<%-- Render blue band header, with or without URL link: --%>
	<c:choose>
		<c:when test="${annoUrl == 'unknown'}">
			<div class="blueBand">Comments and teaching tips</div>
		</c:when>
		<c:otherwise>
			<div class="blueBand"><a href='<c:out value="${annoUrl}"/>' target='_blank'>Comments and teaching tips</a></div>
		</c:otherwise>
	</c:choose>
	<%-- Iterate through *both* comments, and teaching tip annos, but only render description *once*: --%>
	<c:set var="descriptionRendered" value="false" />
	<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.completedTeachingTips">
	<logic:iterate name="ddsViewResourceForm" property="itemDocReader.completedTeachingTips" id="annotation">
		<c:set var="descriptionRendered" value="true" />
		<div class="reviewBlock">
			<div>Shows general statements from DLESE users about this resource and may include 
			information for using the resource within a certain teaching or learning
			environment.</div>
			<bean:define name="annotation" property="collectionKey" id="myAnKey" />
			<div><span style="font-style: italic;">Provided via:
				<a href='collection.do?ky=<%= (String)myAnKey %>'><vocab:uiLabel 
					system="dds" interface="descr" language="en-us" 
					getAbbreviated="false" field="ky" value="<%= (String)myAnKey %>" /></a>					
			</span>		
			</div><div>
				<logic:notEmpty name="annotation" property="description">										
					<br><bean:write name="annotation" property="description"/></logic:notEmpty>					
			</div>
		</div>	
	</logic:iterate>	
	</logic:notEmpty>
	<c:if test="${descriptionRendered == 'false'}">
		<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.completedComment">
		<logic:iterate name="ddsViewResourceForm" property="itemDocReader.completedComment" id="annotation">
			<div class="reviewBlock">
				<div>Shows general statements from DLESE users about this resource and may include 
					information for using the resource within a certain teaching or learning
					environment.</div>
				<bean:define name="annotation" property="collectionKey" id="myAnKey" />
				<div><span style="font-style: italic;">Provided via:
					<a href='collection.do?ky=<%= (String)myAnKey %>'><vocab:uiLabel 
						system="dds" interface="descr" language="en-us" 
						getAbbreviated="false" field="ky" value="<%= (String)myAnKey %>" /></a>					
				</span>		
				</div><div>
					<logic:notEmpty name="annotation" property="description">										
						<br><bean:write name="annotation" property="description"/></logic:notEmpty>					
				</div>
			</div>	
		</logic:iterate>	
		</logic:notEmpty>
	</c:if>
</c:if>

<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.completedEditorSummaries">
<logic:iterate name="ddsViewResourceForm" property="itemDocReader.completedEditorSummaries" id="annotation">
	<logic:empty name="annotation" property="url">
	<div class="blueBand">Editor's summary</div>
	</logic:empty>
	<logic:notEmpty name="annotation" property="url">
	<div class="blueBand"><a href='<bean:write name="annotation" property="url"/>' target='_blank'>Editor's summary</a></div>
	</logic:notEmpty>
	<div class="reviewBlock">
		<div>Shows that the resource meets, supports or aligns
		with an educational standard, that is, a level of achievement determined by
		a professional group to which learners are expected to aspire.</div>
		<bean:define name="annotation" property="collectionKey" id="myAnKey" />
		<div><span style="font-style: italic;">Provided via:
			<a href='collection.do?ky=<%= (String)myAnKey %>'><vocab:uiLabel 
				system="dds" interface="descr" language="en-us" 
				getAbbreviated="false" field="ky" value="<%= (String)myAnKey %>" /></a>					
		</span>		
		</div><div>
			<logic:notEmpty name="annotation" property="description">										
				<br><bean:write name="annotation" property="description"/></logic:notEmpty>					
		</div>
	</div>	
</logic:iterate>		
</logic:notEmpty>
	
<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.completedChallengingSituations">
<logic:iterate name="ddsViewResourceForm" property="itemDocReader.completedChallengingSituations" id="annotation">
	<logic:empty name="annotation" property="url">
	<div class="blueBand">Information on challenging teaching and learning situations </div>
	</logic:empty>
	<logic:notEmpty name="annotation" property="url">
	<div class="blueBand"><a href='<bean:write name="annotation" property="url"/>' target='_blank'>Information on 
	challenging teaching and learning situations</a></div>
	</logic:notEmpty>
	<div class="reviewBlock">
		<div>Gives information
		for using the resource within challenging or non-typical teaching or
		learning environments.</div>
		<bean:define name="annotation" property="collectionKey" id="myAnKey" />
		<div><span style="font-style: italic;">Provided via:
			<a href='collection.do?ky=<%= (String)myAnKey %>'><vocab:uiLabel 
				system="dds" interface="descr" language="en-us" 
				getAbbreviated="false" field="ky" value="<%= (String)myAnKey %>" /></a>					
		</span>		
		</div><div>
			<logic:notEmpty name="annotation" property="description">										
				<br><bean:write name="annotation" property="description"/></logic:notEmpty>					
		</div>
	</div>	
</logic:iterate>
</logic:notEmpty>

<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.completedAverageScores">
<logic:iterate name="ddsViewResourceForm" property="itemDocReader.completedAverageScores" id="annotation">
	<logic:empty name="annotation" property="url">
	<div class="blueBand">Quantitative assessment</div>
	</logic:empty>
	<logic:notEmpty name="annotation" property="url">
	<div class="blueBand"><a href='<bean:write name="annotation" property="url"/>' target='_blank'>Quantitative assessment</a></div>
	</logic:notEmpty>
	<div class="reviewBlock">
		<div>Presents a numerical representation of
		quantitative information about a resource.</div>
		<bean:define name="annotation" property="collectionKey" id="myAnKey" />
		<div><span style="font-style: italic;">Provided via:
			<a href='collection.do?ky=<%= (String)myAnKey %>'><vocab:uiLabel 
				system="dds" interface="descr" language="en-us" 
				getAbbreviated="false" field="ky" value="<%= (String)myAnKey %>" /></a>					
		</span>		
		</div><div>
			<logic:notEmpty name="annotation" property="description">										
				<br><bean:write name="annotation" property="description"/></logic:notEmpty>					
		</div>
	</div>	
</logic:iterate>
</logic:notEmpty>	

<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.completedAdvice">
<logic:iterate name="ddsViewResourceForm" property="itemDocReader.completedAdvice" id="annotation">
	<logic:empty name="annotation" property="url">
	<div class="blueBand">Advice</div>
	</logic:empty>
	<logic:notEmpty name="annotation" property="url">
	<div class="blueBand"><a href='<bean:write name="annotation" property="url"/>' target='_blank'>Advice</a></div>
	</logic:notEmpty>
	<div class="reviewBlock">
		<div>A suggestion for working with the resource.</div>
		<bean:define name="annotation" property="collectionKey" id="myAnKey" />
		<div><span style="font-style: italic;">Provided via:
			<a href='collection.do?ky=<%= (String)myAnKey %>'><vocab:uiLabel 
				system="dds" interface="descr" language="en-us" 
				getAbbreviated="false" field="ky" value="<%= (String)myAnKey %>" /></a>					
		</span>		
		</div><div>
			<logic:notEmpty name="annotation" property="description">										
				<br><bean:write name="annotation" property="description"/></logic:notEmpty>					
		</div>
	</div>	
</logic:iterate>
</logic:notEmpty>	

<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.completedAnnotation">
<logic:iterate name="ddsViewResourceForm" property="itemDocReader.completedAnnotation" id="annotation">
	<logic:empty name="annotation" property="url">
	<div class="blueBand">Other notes</div>
	</logic:empty>
	<logic:notEmpty name="annotation" property="url">
	<div class="blueBand"><a href='<bean:write name="annotation" property="url"/>' target='_blank'>Other notes</a></div>
	</logic:notEmpty>
	<div class="reviewBlock">
		<div>Additional information about a resource expressed in any manner.</div>
		<bean:define name="annotation" property="collectionKey" id="myAnKey" />
		<div><span style="font-style: italic;">Provided via:
			<a href='collection.do?ky=<%= (String)myAnKey %>'><vocab:uiLabel 
				system="dds" interface="descr" language="en-us" 
				getAbbreviated="false" field="ky" value="<%= (String)myAnKey %>" /></a>					
		</span>		
		</div><div>
			<logic:notEmpty name="annotation" property="description">										
				<br><bean:write name="annotation" property="description"/></logic:notEmpty>					
		</div>
	</div>	
</logic:iterate>	
</logic:notEmpty>

<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.completedBias">
<logic:iterate name="ddsViewResourceForm" property="itemDocReader.completedBias" id="annotation">
	<logic:empty name="annotation" property="url">
	<div class="blueBand">Bias</div>
	</logic:empty>
	<logic:notEmpty name="annotation" property="url">
	<div class="blueBand"><a href='<bean:write name="annotation" property="url"/>' target='_blank'>Bias</a></div>
	</logic:notEmpty>
	<div class="reviewBlock">
		<div>An indication that the resource content may have any of the
		following: (1) factual errors (e.g. not just scientific), (2) be in conflict
		with generally accepted scientific thought, (3) seems self-promoting or (4)
		seems to have a political, social, or religious agenda.</div>
		<bean:define name="annotation" property="collectionKey" id="myAnKey" />
		<div><span style="font-style: italic;">Provided via:
			<a href='collection.do?ky=<%= (String)myAnKey %>'><vocab:uiLabel 
				system="dds" interface="descr" language="en-us" 
				getAbbreviated="false" field="ky" value="<%= (String)myAnKey %>" /></a>					
		</span>		
		</div><div>
			<logic:notEmpty name="annotation" property="description">										
				<br><bean:write name="annotation" property="description"/></logic:notEmpty>					
		</div>
	</div>	
</logic:iterate>
</logic:notEmpty>

<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.completedChange">
<logic:iterate name="ddsViewResourceForm" property="itemDocReader.completedChange" id="annotation">
	<logic:empty name="annotation" property="url">
	<div class="blueBand">Change</div>
	</logic:empty>
	<logic:notEmpty name="annotation" property="url">
	<div class="blueBand"><a href='<bean:write name="annotation" property="url"/>' target='_blank'>Change</a></div>
	</logic:notEmpty>
	<div class="reviewBlock">
		<div>Annotations that document or propose change to a source document.</div>
		<bean:define name="annotation" property="collectionKey" id="myAnKey" />
		<div><span style="font-style: italic;">Provided via:
			<a href='collection.do?ky=<%= (String)myAnKey %>'><vocab:uiLabel 
				system="dds" interface="descr" language="en-us" 
				getAbbreviated="false" field="ky" value="<%= (String)myAnKey %>" /></a>					
		</span>		
		</div><div>
			<logic:notEmpty name="annotation" property="description">										
				<br><bean:write name="annotation" property="description"/></logic:notEmpty>					
		</div>
	</div>	
</logic:iterate>
</logic:notEmpty>

<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.completedEducationalStandard">
<logic:iterate name="ddsViewResourceForm" property="itemDocReader.completedEducationalStandard" id="annotation">
	<logic:empty name="annotation" property="url">
	<div class="blueBand">Educational standard</div>
	</logic:empty>
	<logic:notEmpty name="annotation" property="url">
	<div class="blueBand"><a href='<bean:write name="annotation" property="url"/>' target='_blank'>Educational standard</a></div>
	</logic:notEmpty>
	<div class="reviewBlock">
		<div>An indication that the resource meets, supports or aligns with an educational standard, that is, a level of
		achievement determined by a professional group to which learners are expected to aspire.</div>
		<bean:define name="annotation" property="collectionKey" id="myAnKey" />
		<div><span style="font-style: italic;">Provided via:
			<a href='collection.do?ky=<%= (String)myAnKey %>'><vocab:uiLabel 
				system="dds" interface="descr" language="en-us" 
				getAbbreviated="false" field="ky" value="<%= (String)myAnKey %>" /></a>					
		</span>		
		</div><div>
			<logic:notEmpty name="annotation" property="description">										
				<br><bean:write name="annotation" property="description"/></logic:notEmpty>					
		</div>
	</div>	
</logic:iterate>
</logic:notEmpty>

<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.completedExample">
<logic:iterate name="ddsViewResourceForm" property="itemDocReader.completedExample" id="annotation">
	<logic:empty name="annotation" property="url">
	<div class="blueBand">Example</div>
	</logic:empty>
	<logic:notEmpty name="annotation" property="url">
	<div class="blueBand"><a href='<bean:write name="annotation" property="url"/>' target='_blank'>Example</a></div>
	</logic:notEmpty>
	<div class="reviewBlock">
		<div>A description or link to another resource that illustrates the
			resource content in a demonstrative way.</div>
		<bean:define name="annotation" property="collectionKey" id="myAnKey" />
		<div><span style="font-style: italic;">Provided via:
			<a href='collection.do?ky=<%= (String)myAnKey %>'><vocab:uiLabel 
				system="dds" interface="descr" language="en-us" 
				getAbbreviated="false" field="ky" value="<%= (String)myAnKey %>" /></a>					
		</span>		
		</div><div>
			<logic:notEmpty name="annotation" property="description">										
				<br><bean:write name="annotation" property="description"/></logic:notEmpty>					
		</div>
	</div>	
</logic:iterate>
</logic:notEmpty>

<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.completedExplanation">
<logic:iterate name="ddsViewResourceForm" property="itemDocReader.completedExplanation" id="annotation">
	<logic:empty name="annotation" property="url">
	<div class="blueBand">Explanation</div>
	</logic:empty>
	<logic:notEmpty name="annotation" property="url">
	<div class="blueBand"><a href='<bean:write name="annotation" property="url"/>' target='_blank'>Explanation</a></div>
	</logic:notEmpty>
	<div class="reviewBlock">
		<div>Information for understanding the resource, especially within
			its context of use but does not generally express opinions or attitudes.</div>
		<bean:define name="annotation" property="collectionKey" id="myAnKey" />
		<div><span style="font-style: italic;">Provided via:
			<a href='collection.do?ky=<%= (String)myAnKey %>'><vocab:uiLabel 
				system="dds" interface="descr" language="en-us" 
				getAbbreviated="false" field="ky" value="<%= (String)myAnKey %>" /></a>					
		</span>		
		</div><div>
			<logic:notEmpty name="annotation" property="description">										
				<br><bean:write name="annotation" property="description"/></logic:notEmpty>					
		</div>
	</div>	
</logic:iterate>
</logic:notEmpty>

<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.completedQuestion">
<logic:iterate name="ddsViewResourceForm" property="itemDocReader.completedQuestion" id="annotation">
	<logic:empty name="annotation" property="url">
	<div class="blueBand">Question</div>
	</logic:empty>
	<logic:notEmpty name="annotation" property="url">
	<div class="blueBand"><a href='<bean:write name="annotation" property="url"/>' target='_blank'>Question</a></div>
	</logic:notEmpty>
	<div class="reviewBlock">
		<div>A request for more information, explanation of why something was done, or
			raising a point of discussion.</div>
		<bean:define name="annotation" property="collectionKey" id="myAnKey" />
		<div><span style="font-style: italic;">Provided via:
			<a href='collection.do?ky=<%= (String)myAnKey %>'><vocab:uiLabel 
				system="dds" interface="descr" language="en-us" 
				getAbbreviated="false" field="ky" value="<%= (String)myAnKey %>" /></a>					
		</span>		
		</div><div>
			<logic:notEmpty name="annotation" property="description">										
				<br><bean:write name="annotation" property="description"/></logic:notEmpty>					
		</div>
	</div>	
</logic:iterate>
</logic:notEmpty>

<!-- Note: this block repeats near the top of this page -->
<logic:empty name="ddsViewResourceForm" property="itemDocReader.completedReviews">
	${submitAnnotationLinks}
</logic:empty>

<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.graphicalAnnosInProgress">
<logic:iterate name="ddsViewResourceForm" property="itemDocReader.graphicalAnnosInProgress" id="annotation">
	<div class="blueBand">Currently open for annotation</div>
	<div class="reviewBlock">
		<bean:define name="annotation" property="collectionKey" id="myAnKey" />
		<div style="font-style: italic;">Via:
			<a href='collection.do?ky=<%= (String)myAnKey %>'><vocab:uiLabel 
				system="dds" interface="descr" language="en-us" 
				getAbbreviated="false" field="ky" value="<%= (String)myAnKey %>" /></a><logic:notEmpty 
				name="annotation" property="url">.</span>&nbsp;
				<a href='<bean:write name="annotation" property="url"/>' target="_blank">Draw an annotation</a>
				</logic:notEmpty>								
		</div>
		<div>
			<logic:notEmpty name="annotation" property="description">										
				<br><bean:write name="annotation" property="description"/></logic:notEmpty>					
		</div>
	</div>	
</logic:iterate>
</logic:notEmpty>

<logic:notEmpty name="ddsViewResourceForm" property="itemDocReader.completedSeeAlso">
<logic:iterate name="ddsViewResourceForm" property="itemDocReader.completedSeeAlso" id="annotation">
	<logic:empty name="annotation" property="url">
	<div class="blueBand">Related resource</div>
	</logic:empty>
	<logic:notEmpty name="annotation" property="url">
	<div class="blueBand"><a href='<bean:write name="annotation" property="url"/>' target='_blank'>Related resource</a></div>
	</logic:notEmpty>
	<div class="reviewBlock">
		<div>A reference to another resource.</div>
		<bean:define name="annotation" property="collectionKey" id="myAnKey" />
		<div><span style="font-style: italic;">Contributed by:
			<a href='collection.do?ky=<%= (String)myAnKey %>'><vocab:uiLabel 
				system="dds" interface="descr" language="en-us" 
				getAbbreviated="false" field="ky" value="<%= (String)myAnKey %>" /></a>					
		</span>		
		</div><div>
			<logic:notEmpty name="annotation" property="description">										
				<br><bean:write name="annotation" property="description"/></logic:notEmpty>					
		</div>
	</div>	
</logic:iterate>
</logic:notEmpty>


<x:parse var="xmlDoc">
	<util:readFile filename="<%= ddsViewResourceForm.getRecordFilename() %>" />
</x:parse>
<x:set var="relations" select="$xmlDoc//*[local-name()='itemRecord']/*[local-name()='relations']" />
<x:if select="$relations">
	<jsp:setProperty name="ddsViewResourceForm" property="relDisplayed" value="f"/>
	<x:forEach select="$relations/*[local-name()='relation']">
		<logic:notEqual name="ddsViewResourceForm" property="relDisplayed" value="t">
			<div class="blueBand">Related resources:</div>
			<jsp:setProperty name="ddsViewResourceForm" property="relDisplayed" value="t"/>
		</logic:notEqual>
		<x:if select="*[local-name()='idEntry']/@kind">
			<c:set var="idEntry">
				<x:out select="*[local-name()='idEntry']/@entry" />
			</c:set>		
			<jsp:setProperty name="ddsViewResourceForm" property="idSearch" value="${idEntry}"/>
			<logic:notEmpty name="ddsViewResourceForm" property="idSearchTitle">
				<div class="reviewBlock">			
				This resource
					<c:set var="metaVal">
						<x:out select="*[local-name()='idEntry']/@kind" />
					</c:set>
					<vocab:uiLabel system="dds" interface="descr" language="en-us" 
						useMetaNames="true" field="kind" value="${metaVal}" />						
				&quot;<bean:write name="ddsViewResourceForm" property="idSearchTitle"/>&quot;<br />
				<a href='<bean:write name="ddsViewResourceForm" property="idSearchUrl"/>' 
					target='_blank'><dds:keywordsHighlight addWbr="true"><bean:write 
						name="ddsViewResourceForm" property="idSearchUrl"/></dds:keywordsHighlight></a>
				</div>
			</logic:notEmpty>
		</x:if>
		<x:if select="*[local-name()='urlEntry']/@kind">
			<div class="reviewBlock">				
			This resource
				<c:set var="metaVal">
					<x:out select="*[local-name()='urlEntry']/@kind" />
				</c:set>
				<vocab:uiLabel system="dds" interface="descr" language="en-us" 
					useMetaNames="true" field="kind" value="${metaVal}"/><br />
				<a href='<x:out select="*[local-name()='urlEntry']/@url"/>'
					target='_blank'><dds:keywordsHighlight addWbr="true"><x:out 
						select="*[local-name()='urlEntry']/@url"/></dds:keywordsHighlight></a>
			</div>
		</x:if>
	</x:forEach>
</x:if>

<%@ include file="footer.jsp" %>			
<!-- End page content -->
</div> 

</body>
</html:html> 