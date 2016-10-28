<%@ include file="/include/setup.jsp" %>
<%@ page import="java.util.Enumeration" %>
<html>
	<head>
		<title>Browse resources &amp; collections</title>
		<script type="text/javascript"><!--
			document.dlese_surveyIsPresent = ${ initParam.showSurvey };
		//-->
		</script> 
		<%-- DLESE standard (shared) styles and script --%>
		<c:import url="${domain}/dlese_shared/templates/header_refs.html"/>
		<link rel="stylesheet" type="text/css" href="/dlese_shared/dlese_styles_screen.css"/>
		<%-- Vocabs styles and script not needed as of initial Browse design, but might be used in the future and are 
			probably already cached	in the user's browser: --%>
		<link rel="stylesheet" type="text/css" href="${ jsVocabsUrl }/dlese_styles_vocabs.css"/>
		<script type="text/javascript" src="${ jsVocabsUrl }/dlese_script_vocabs.js"></script>		
		<script type="text/javascript"><!--
			dlese_DISCOVERY_ROOT = '<%= request.getContextPath() %>/';
			dlese_pageHasVocabs = false;
			dlese_pageShowsVocabSelected = false;
			function newCollSelected() {
				//var obj = document.getElementById( "collExplained" );
				//obj.style.display = "none";
				//obj = document.getElementById( "pleaseWait" );
				//obj.style.display = "block";				
				var forward = 'histogram.do?group=' + document.forms.histogramForm.group.value;
				if ( document.forms.histogramForm.key.value != 'all' )
					forward += '&key=' + document.forms.histogramForm.key.value;
				if ( document.forms.histogramForm.subgroup.value != '' )
					forward += '&subgroup=' + document.forms.histogramForm.subgroup.value;
				document.location.href = forward;
			} 						
		//-->
		</script> 		
		<%-- Discovery-specific styles and script --%>
		<link rel='stylesheet' type='text/css' href='/library/discovery_styles_screen.css'>
		<style type="text/css">
			.breadcrumbs {
				top: 105px;
			}
			.histForm {
				margin: 0px;
				padding: 0px;
			}
			.navTabs {
				margin-left: 7px;
				margin-right: 0px;
				margin-top: 10px; 
				margin-bottom: 10px; 
				padding-left: 5px;
				padding-right: 0px;
				padding-bottom: 4px;
				white-space: nowrap;
				font-size: 115%;
				text-align: center;
				width: 435;
				font-variant: small-caps;
			}
			.tab {
				border: none;
				margin: 0px;
				padding: 2px;
			}
			.tabSelected {
				border: none;
				margin: 0px;
				padding: 2px;
				font-weight: bold;
			}
			.subtabs {
				margin-top: 5px;
				font-size: 95%;
			}
			.subtab {
				border: none;
				margin: 0px;
				padding: 2px;
				padding-top: 5px;
			}
			.subtabSelected {
				border: none;
				margin: 0px;
				padding: 2px;
				font-weight: bold;
				padding-top: 5px;
			}
			.vocabLabels {
				margin: 0px; 
				padding-bottom: 0px; 
				padding-top: 3px;
			}
		</style>
		<%-- Print styles always go last! --%>
		<style type="text/css" media="print">
			BODY { padding-top: 32px; }
		</style>
		<link rel='stylesheet' type='text/css' href='/library/discovery_styles_print.css' media='print'>
	</head>
	<body>	
		<div class="dlese_sectionTitle" id="dlese_sectionTitle">
			Browse<br>resources
		</div>	
		<%-- DLESE template banner --%>
		<c:import url="${domain}/dlese_shared/templates/content_top.html"/>
		<c:choose>
			<c:when test="${ histogramForm.hasCollectionSpecified == 'true' }">
				<c:forEach var="collection" items="${ histogramForm.collections }">
					<c:if test="${ collection.vocabNode.name == param.key }">
						<c:set var="collectionName">${ collection.vocabNode.label }</c:set>
						<c:set var="collectionTotal">${ collection.libraryTotal }</c:set>							
					</c:if>		
				</c:forEach>
			</c:when>
			<c:otherwise>
				<c:set var="collectionName">All resources</c:set>
				<c:set var="collectionTotal">${ histogramForm.collections[0].libraryTotal }</c:set>		
			</c:otherwise>
		</c:choose>
		<div class="breadcrumbs">
			<div class="breadcrumbsText">Educational resources &gt; Browse resources &amp; collections</div>
		</div>
		<div style="position: relative; left: -7px; margin-bottom: 0px; padding-bottom: 0px;">
			<h3 class="hasSubhead">${ collectionName }</h3>
			<h4 style="margin-top: 0px; padding-top: 0px;">
				Total resources: ${ collectionTotal }
			</h4>	
		</div>
	<c:set var="noWrap"><c:if test="${ param.group != 'contentStandard' }"> nowrap</c:if></c:set>		
	<form name="histogramForm" id="histogramForm" action="histogram.do" class="histForm">
	<table border="0" cellpadding="0" cellspacing="0" style="margin-top: 0px; padding-top: 0px;">
		<td width="440" valign="top" style="margin-top: 0px; padding-top: 0px;">
			<div class="navTabs">
				<c:choose>
					<c:when test="${ empty param.key }">
						<%-- Use URL spoofing links when no collection is specified --%> 
						<c:if test="${ param.group == 'subject' }">
							<span class="tabSelected">Subject</span> |
							<span class="tab"><a title="Browse by Grade Level" href="browsenav_gradeRange.htm">Grade Level</a></span> |
							<span class="tab"><a title="Browse by Resource Type" href="browsenav_resourceType.htm">Resource Type</a></span> |
							<span class="tab"><a title="Browse by Standards" href="histogram.do?group=contentStandard&subgroup=National%20Science%20Education%20Standards%20(NSES)">Standards</a></span>
						</c:if>
						<c:if test="${ param.group == 'gradeRange' }">
							<span class="tab"><a title="Browse by Subject" href="browsenav_subject.htm">Subject</a></span> |
							<span class="tabSelected">Grade Level</span> |
							<span class="tab"><a title="Browse by Resource Type" href="browsenav_resourceType.htm">Resource Type</a></span> |
							<span class="tab"><a title="Browse by Standards" href="histogram.do?group=contentStandard&subgroup=National%20Science%20Education%20Standards%20(NSES)">Standards</a></span>
						</c:if>
						<c:if test="${ param.group == 'resourceType' }">
							<span class="tab"><a title="Browse by Subject" href="browsenav_subject.htm">Subject</a></span> |
							<span class="tab"><a title="Browse by Grade Level" href="browsenav_gradeRange.htm">Grade Level</a></span> | 
							<span class="tabSelected">Resource Type</span> |
							<span class="tab"><a title="Browse by Standards" href="histogram.do?group=contentStandard&subgroup=National%20Science%20Education%20Standards%20(NSES)">Standards</a></span>
						</c:if>
						<c:if test="${ ( param.group == 'contentStandard' ) && ( param.subgroup == 'National Science Education Standards (NSES)' ) }">
							<span class="tab"><a title="Browse by Subject" href="browsenav_subject.htm">Subject</a></span> |
							<span class="tab"><a title="Browse by Grade Level" href="browsenav_gradeRange.htm">Grade Level</a></span> | 
							<span class="tab"><a title="Browse by Resource Type" href="browsenav_resourceType.htm">Resource Type</a></span> |
							<span class="tabSelected">Standards</span>
							<div class='subtabs'>
								<span class="subtabSelected">Natl Science Edu Standards</span> |
								<span class="subtab"><a title="Browse by National Geography Standards" href="histogram.do?group=contentStandard&subgroup=National%20Geography%20Standards">Natl Geography Standards</a></span>
							</div>							
						</c:if>
						<c:if test="${ ( param.group == 'contentStandard' ) && ( param.subgroup == 'National Geography Standards' ) }">
							<span class="tab"><a title="Browse by Subject" href="browsenav_subject.htm">Subject</a></span> |
							<span class="tab"><a title="Browse by Grade Level" href="browsenav_gradeRange.htm">Grade Level</a></span> | 
							<span class="tab"><a title="Browse by Resource Type" href="browsenav_resourceType.htm">Resource Type</a></span> |
							<span class="tabSelected">Standards</span>
							<div class='subtabs'>
								<span class="subtab"><a title="Browse by National Science Education Standards" href="histogram.do?group=contentStandard&subgroup=National%20Science%20Education%20Standards%20(NSES)">Natl Science Edu Standards</a></span> |
								<span class="subtabSelected">Natl Geography Standards</span>
							</div>							
						</c:if>
					</c:when>
					<c:otherwise>
						<c:if test="${ param.group == 'subject' }">
							<span class="tabSelected">Subject</span> |
							<span class="tab"><a title="Browse by Grade Level" href="histogram.do?group=gradeRange&key=${ param.key }">Grade Level</a></span> |
							<span class="tab"><a title="Browse by Resource Type" href="histogram.do?group=resourceType&key=${ param.key }">Resource Type</a></span> |
							<span class="tab"><a title="Browse by Standards" href="histogram.do?group=contentStandard&key=${ param.key }&subgroup=National%20Science%20Education%20Standards%20(NSES)">Standards</a></span>
						</c:if>
						<c:if test="${ param.group == 'gradeRange' }">
							<span class="tab"><a title="Browse by Subject" href="histogram.do?group=subject&key=${ param.key }">Subject</a></span> |
							<span class="tabSelected">Grade Level</span> |
							<span class="tab"><a title="Browse by Resource Type" href="histogram.do?group=resourceType&key=${ param.key }">Resource Type</a></span> |
							<span class="tab"><a title="Browse by Standards" href="histogram.do?group=contentStandard&key=${ param.key }&subgroup=National%20Science%20Education%20Standards%20(NSES)">Standards</a></span>
						</c:if>
						<c:if test="${ param.group == 'resourceType' }">
							<span class="tab"><a title="Browse by Subject" href="histogram.do?group=subject&key=${ param.key }">Subject</a></span> |
							<span class="tab"><a title="Browse by Grade Level" href="histogram.do?group=gradeRange&key=${ param.key }">Grade Level</a></span> | 
							<span class="tabSelected">Resource Type</span> |
							<span class="tab"><a title="Browse by Standards" href="histogram.do?group=contentStandard&key=${ param.key }&subgroup=National%20Science%20Education%20Standards%20(NSES)">Standards</a></span>
						</c:if>
						<c:if test="${ ( param.group == 'contentStandard' ) && ( param.subgroup == 'National Science Education Standards (NSES)' ) }">
							<span class="tab"><a title="Browse by Subject" href="histogram.do?group=subject&key=${ param.key }">Subject</a></span> |
							<span class="tab"><a title="Browse by Grade Level" href="histogram.do?group=gradeRange&key=${ param.key }">Grade Level</a></span> | 
							<span class="tab"><a title="Browse by Resource Type" href="histogram.do?group=resourceType&key=${ param.key }">Resource Type</a></span> |
							<span class="tabSelected">Standards</span>
							<div class='subtabs'>
								<span class="subtabSelected">Natl Science Edu Standards</span> |
								<span class="subtab"><a title="Browse by National Geography Standards" href="histogram.do?group=contentStandard&key=${ param.key }&subgroup=National%20Geography%20Standards">Natl Geography Standards</a></span>
							</div>									
						</c:if>
						<c:if test="${ ( param.group == 'contentStandard' ) && ( param.subgroup == 'National Geography Standards' ) }">
							<span class="tab"><a title="Browse by Subject" href="histogram.do?group=subject&key=${ param.key }">Subject</a></span> |
							<span class="tab"><a title="Browse by Grade Level" href="histogram.do?group=gradeRange&key=${ param.key }">Grade Level</a></span> | 
							<span class="tab"><a title="Browse by Resource Type" href="histogram.do?group=resourceType&key=${ param.key }">Resource Type</a></span> |
							<span class="tabSelected">Standards</span>
							<div class='subtabs'>
								<span class="subtab"><a title="Browse by National Science Education Standards" href="histogram.do?group=contentStandard&key=${ param.key }&subgroup=National%20Science%20Education%20Standards%20(NSES)">Natl Science Edu Standards</a></span> |
								<span class="subtabSelected">Natl Geography Standards</span>
							</div>							
						</c:if>
					</c:otherwise>
				</c:choose>
			</div><table width="440" cellspacing="0" cellpadding="0" style="margin-left: 7px; margin-right: 0px; padding-right: 0px;">	
				<jsp:setProperty name="histogramForm" property="currentVocabName" value="${ param.group }"/>
				<c:if test="${ not empty param.subgroup }">
					<jsp:setProperty name="histogramForm" property="currentVocabGroup" value="${ param.subgroup }"/>
				</c:if>
				<c:set var="histogramOut">
				<c:forEach items="${ histogramForm.vocabList }" var="vocabGroup" varStatus="status">
					<c:if test="${ vocabGroup.vocabNode.noDisplay == 'false' }">
						<c:if test="${ vocabGroup.hasSubtotalsGreaterThanZero }">
							<c:if test="${ status.index != 0 }">
								<tr>
									<td colspan="2" height="10"><span style="font-size: 2px">&nbsp;</span></td>
								</tr>
							</c:if>
							<tr class="blueBand">
								<td align=right width="40%" style="padding: 0px; margin: 0px;">
									<div class="vocabSubHeader">
										<nobr>${ vocabGroup.vocabNode.label }</nobr>
									</div>
								</td>
								<td width="60%" style="padding: 0px; margin: 0px;">&nbsp;</td>
							</tr>
						</c:if>
						<c:if test="${ empty vocabGroup.vocabNode.subList }">
							<c:if test="${ vocabGroup.libraryTotal != 0 }">	
								<tr>
									<td align="right" width="40%" ${ noWrap } class="vocabLabels">					
											<a href='${ vocabGroup.histogramQuery }'
												onclick='clearPopupsStatus();'
												title='Search by ${ vocabGroup.vocabNode.label }'>${ vocabGroup.vocabNode.label }</a>				
									</td>
									<td width="60%" background="images/hist_ruler.gif" nowrap style="padding: 0px; margin: 0px; padding-top: 3px;">
										<nobr><span style="font-size: 5px;">&nbsp;</span>
											<jsp:setProperty name="vocabGroup" property="largestTotalInThisGroup" 
												value="${ histogramForm.largestTotal }" />
											<c:set var="titleText">Search by ${ vocabGroup.vocabNode.label }</c:set>
											<a href='${ vocabGroup.histogramQuery }' 
												onclick='clearPopupsStatus();'
												title='${ titleText }'><img 
												src='images/hist_bar.gif' border='0' height='7' width='${ vocabGroup.vocabTotalBarPercent }%'
												alt='${ titleText }' /></a>
											${ vocabGroup.libraryTotal }
										</nobr>
									</td>
								</tr>		
							</c:if>
						</c:if>
						<c:if test="${ vocabGroup.isLastInSublist == 'true' }">
							<tr>
								<td colspan="2" height="10"><span style="font-size: 2px">&nbsp;</span></td>
							</tr>
						</c:if>
					</c:if>
				</c:forEach>
				</c:set>
				<c:choose>
					<c:when test="${ empty histogramOut }">
						<p>Information about <vocabs:uiLabel metaFormat="adn" audience="${ initParam.metadataVocabAudience }" 
							language="${ initParam.metadataVocabLanguage }" field="${ param.group }" />
							is unavailable for this collection.
						</p>
					</c:when>
					<c:otherwise>
						${ histogramOut }
					</c:otherwise>
				</c:choose>
			</table>
		</td>
		<td valign=top>
			<div style="padding-left: 8px; margin-left: 15px; margin-top: 8px; margin-bottom: 4px;">
				<select name="key" onChange="newCollSelected()">			
					<option value="all">All resources</option>
					<c:forEach items="${ histogramForm.collectionsVocab }" var="vocab" varStatus="status">
						<c:if test="${ vocab.noDisplay == 'false' }">
							<option value='${ vocab.name }'<c:if test="${ histogramForm.hasCollectionSpecified == 'true' }">
								<c:if test="${ vocab.name == param.key }"> selected</c:if></c:if>>${ vocab.label }</option>
						</c:if>
					</c:forEach>
				</select>
				<noscript>
					<input type='submit' value='Go'>
				</noscript>	
			</div>
			<input type=hidden name='group' value='${ param.group }'>		
			<input type=hidden name='subgroup' value='${ param.subgroup }'>		
	
	<c:choose>
		<c:when test="${ empty param.key }">
		
			<div id="collExplained" style="position: relative; display: block;">
				<h3>Browse all the resources in the library </h3>
				<p>The graph at left shows a broad range of subjects covered by all
				the resources accessible in the library. You can change the graph as you wish to show
				the resources by grade level or by type of resource.
				To do this, click the links above the graph.</p>
				
				<h3>Collections</h3>
				<p>Items in the library are also organized into themes or collections. Pick
				a collection from the drop-down list shown above. Its description will
				display here, and the graph at left will change to show only what is
				in that collection. A special collection is the DLESE Reviewed Collection
				(DRC)
				consisting of resources that have met a set of review criteria.
				Select it from the list for more information.</p>
				
				<h3>Searching further</h3>
				<p>To see a list of library resource descriptions, click one of the links
				along the left side. From the page that displays you can refine your
				search further using keywords and the criteria buttons for Grade Level,
				Resource Type, Collection, and education Standards.</p>
				
				<h3>Suggesting resources</h3>
				<p>Anyone can <a href="http://www.dlese.org/suggest/resources/">Suggest a Resource</a> to be considered for the
				library. </p>
			</div>
		</c:when>
		<c:otherwise>
			<div id="collExplained" style="position: relative; display: block;">
				<p>
					${ histogramForm.collectionDocReader.description }
				</p>
				<p><em>Collection is intended for</em>:
					<vocabs:setResponseGroup metaFormat="adn" audience="${ initParam.metadataVocabAudience }" 
						language="${ initParam.metadataVocabLanguage }" field="gr" />
					<c:forEach var="gr" items="${ histogramForm.collectionDocReader.gradeRanges }"> 
						<vocabs:setResponseValue value="${ gr }" />
					</c:forEach>
					<c:catch var="xslError">				
						<c:set var="transformXml"><vocabs:getResponseOPML/></c:set>
						<c:import var="transformXsl" url="include/vocabs_comma_separated.xsl" />
						<x:transform xml="${ transformXml }" xslt="${ transformXsl }" />
					</c:catch>
					<c:if test="${ not empty xslError }">
						<!-- ${xslError} -->
					</c:if>	
				</p>
				<p><em>Try searching on these terms (type in keyword box):</em>
					<c:forEach var="su" items="${ histogramForm.collectionDocReader.subjects }"><vocabs:uiLabel 
						metaFormat="adn" audience="${ initParam.metadataVocabAudience }" 
						language="${ initParam.metadataVocabLanguage }" field="su" value="${ su }" />,
					</c:forEach>
					${ histogramForm.collectionDocReader.keywordsDisplay }							
				</p>
				<p>
					<em>Cost:</em>
					<vocabs:uiLabel metaFormat="adn" audience="${ initParam.metadataVocabAudience }" 
						language="${ initParam.metadataVocabLanguage }" 
						field="cost" value="${ histogramForm.collectionDocReader.cost }" />
					<c:if test="${ not empty histogramForm.collectionDocReader.scopeUrl }">
						<br><a href='${ histogramForm.collectionDocReader.scopeUrl }' target='_blank'>Collection Scope and 
							Policy Statement</a>
					</c:if>
					<c:if test="${ not empty histogramForm.collectionDocReader.collectionUrl }">
						<br><a href="${ histogramForm.collectionDocReader.collectionUrl }" target="_blank">Collection website</a>
					</c:if>
				</p>	
			</div>
		</c:otherwise>
	</c:choose>
			<div id="pleaseWait" style="position: relative; display: none;">
				<h4>Please wait: loading...</h4>
			</div>
		</td>
	</table>
	
<br clear='all'>
<p class="topLink"><a href="#top"><img src='/dlese_shared/images/arrowup.gif' border='0' 
alt='Return to the top of the page'></a></p>

</form>		
		
		<%-- DLESE template bottom (footer links and logo) --%>
		<c:import url="${domain}/dlese_shared/templates/content_bottom.html"/>
	</body>
</html>