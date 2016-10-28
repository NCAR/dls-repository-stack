<%-- 
This page renders the search result for a single record in either the search
results page or the full description page. Assumens bean 'result' has 'docReader'
and 'docMap' methods to access the single record's info.

Set the variable 'displayType' to 'full' or 'brief' to display all or some of the
info available e.g. <c:if test="${displayType == 'full'}">
--%>
<%@ include file="/ddswsBaseUrl.jsp" %><%-- Sets the DDSWS 1-1 baseURL into variable 'ddsws11BaseUrl' --%>

<%-- This DocMap may be a multi doc: --%>
<c:set var="myDocMap" value="${result.lazyDocMap}"/> 

<%-- This DocMap is always de duped: --%>
<c:set var="myDocMapDeDuped" value="${result.docReader.lazyDocMap}"/>

<%-- The max number of annos/relations to display in-line --%>
<c:set var="MAX_ANNOS" value="10"/>

<%-- The max number of fields to display in the lucene data --%>
<c:set var="MAX_FIELDS" value="120"/>

<%-- Common JavaScript for the toggle display --%>
<script type="text/javascript">
<!--	
	if(document.images) {
		arrowUp = new Image;
		arrowDown = new Image;
		arrowUp.src = "images/btnExpandClsd.gif";
		arrowDown.src = "images/btnExpand.gif";
	} else {
		arrowUp = "";
		arrowDown = "";
	}
	
	function toggleArrow( elementID ) {
		var objElement = document.getElementById( elementID );
		var arrow = document[ elementID + '_arrow' ];
		if ( objElement != null ) {
			if ( objElement.style.display == '' ) {
				objElement.style.display = 'none';
				if(arrow)
					arrow.src = arrowUp.src;
			}
			else {
				objElement.style.display = '';
				if(arrow)
					arrow.src = arrowDown.src;
			}
		}
	}
-->
</script>

<%-- Save the display for the full field data from the Lucene Document Map --%>
<c:set var="luceneEntryDisplay">
	<c:if test="${not empty myDocMap}">
		<c:choose>
			<c:when test="${not empty result.docReader.deDupedResultDocs}">
				<c:set var="dataTitle">INDEX RECORD (COMBINED FROM ${fn:length(result.docReader.deDupedResultDocs)} RECORDS)</c:set>
			</c:when>
			<c:otherwise>
				<c:set var="dataTitle">INDEX RECORD</c:set>
			</c:otherwise>
		</c:choose>
		<div>
			<a 	href="javascript:toggleArrow('luceneDoc_${status.index}');" 
				title="Click to show/hide" 
				class="vocabHeading"><img src='images/btnExpandClsd.gif' 
				alt="Click to show/hide" border="0" hspace="5" width="11" hight="11" name="luceneDoc_${status.index}_arrow">${dataTitle}</a>							
		</div>					
		
		<table bgcolor="#666666" class="indexFieldsTable" cellpadding="6" cellspacing="1" border="0" align="center" id="luceneDoc_${status.index}" style="display:none; width:95%;margin-top:8px;">
			<catch var="docMapErr">
				<c:forEach items="${myDocMap}" var="field" varStatus="i" end="${MAX_FIELDS}"> 
					<tr bgcolor='#DDDFF7'> 
						<td>						
							<div class="searchResultValues"><b>${field.key}</b> &nbsp; <c:out value="${field.value}" escapeXml="true"/></div>
						</td>
					</tr>
				</c:forEach>
				<c:if test="${fn:length(myDocMap) > MAX_FIELDS-1}">
					<tr bgcolor='#DDDFF7'> 
						<td>						
							<div class="searchResultValues"><b>Plus ${fn:length(myDocMap) - MAX_FIELDS} fields not shown...</b></div>
						</td>
					</tr>
				</c:if>
			</catch>
			<c:if test="${not empty docMapErr}">
				<!-- docMapErr: ${docMapErr} -->
			</c:if>
		</table>
		
		<%-- Display the full field data for each de-dup doc --%>
		<c:if test="${result.docReader.multiRecordStatus == 'true'}">
			<div style="padding-left:16px">
			<c:forEach items="${result.docReader.deDupedResultDocs}" var="deDupResult" varStatus="dupsStatus"> 
				<c:if test="${not empty deDupResult.docMap}">
					<div>
						<a 	href="javascript:toggleArrow('luceneDoc_${status.index}_${dupsStatus.index}');" 
							title="Click to show/hide" 
							class="vocabHeading"><img src='images/btnExpandClsd.gif' 
							alt="Click to show/hide" border="0" hspace="5" width="11" hight="11" name='luceneDoc_${status.index}_${dupsStatus.index}_arrow'>RECORD ${deDupResult.docReader.id}</a>							
					</div>					
				
					<table bgcolor="#666666" class="indexFieldsTable" cellpadding="6" cellspacing="1" border="0" align="center" id="luceneDoc_${status.index}_${dupsStatus.index}" style="display:none; width:95%;margin-top:8px;">
						<c:forEach items="${deDupResult.docMap}" var="field2" varStatus="i"> 
							<tr bgcolor='#DDDFF7'> 
								<td>						
									<div class="searchResultValues"><b>${field2.key}</b> &nbsp;<c:out value="${field2.value}" escapeXml="true"/></div>
								</td>
							</tr>
						</c:forEach>					
					</table>
				</c:if>
			</c:forEach>
			</div>
		</c:if>		
	</c:if>
</c:set>

<%-- Edit record button display --%>
<c:if test="${initParam['showEditButtonForAdmins'] == 'true'}">
	<c:set var="editRecordButton">
		<div style="white-space:nowrap;">
			<c:if test="${param.action == 'save'}">
				<span style="color:green; font-weight: bold; background-color: #DFD5B6; padding: 2px; margin-bottom: 2px; display: block; text-align: center;">Record Saved</span>
			</c:if>
			<form name="def" method="post" action="editing.do">  
				<input type="hidden" name="verb" value="GetRecord">
				<input type="hidden" name="recordId" value="${result.docReader.id}">
				<input type="hidden" name="id" value="${result.docReader.id}">
				<input type="submit" name="button" value="Edit">
			</form>
			<form name="def" method="post" action="editing.do" onsubmit="JavaScript:return confirm('Delete this record?')">  
				<input type="submit" name="button" value="Delete">
				<input type="hidden" name="verb" value="DeleteRecord">
				<input type="hidden" name="deleteRecord" value="${result.docReader.id}">
			</form>				
		</div>
	</c:set>
</c:if>

<c:choose>
	
	<%-- =========================== Display item-level records =========================== --%>
	<c:when test="${result.docReader.readerType == 'ItemDocReader'}">
		<tr> 
			<td class='admin_blue1'>
				<font size=+1><b><a href='<bean:write name="result" property="docReader.url" filter="false" />' target="_blank" style="text-decoration: none; color: #000000; font-size:14pt"><bean:write name="result" property="docReader.title" filter="false" /></a></b></font>
				<logic:equal name="result" property="docReader.partOfDRC" value="true">
					<img alt="" src="../images/DRC_icon.gif">
				</logic:equal>
				<br>	
				<a href='<bean:write name="result" property="docReader.url" filter="false" />' target="_blank"><bean:write name="result" property="docReader.url" filter="false" /></a>
			</td>				
			<td align="right" class='admin_blue1'> <font color="#787DBC"><b>Item</b></font>${editRecordButton}</td>	
		</tr>
		<tr colspan="2"> 
			<td>
				<bean:write name="result" property="docReader.description" filter="false" />
			</td>					
		</tr>
		<tr colspan="2"> 
			<td>
				
				<%-- Display the Web-crawled content --%>
				<c:if test="${displayType == 'full'}">
					<c:if test='${not empty myDocMap["itemContentTitle"] || not empty myDocMap["itemContentHeaders"] || not empty myDocMap["itemContent"]}'>
						<div>
							<a 	href="javascript:toggleArrow('indexedContent_${result.docReader.id}');" 
								title="Click to show/hide" 
								class="vocabHeading"><img src='images/btnExpandClsd.gif' 
								alt="Click to show/hide" border="0" hspace="5" width="11" hight="11"  name="indexedContent_${result.docReader.id}_arrow">CONTENT EXTRACTED FROM RESOURCE</a>							
						</div>					
					
						<table bgcolor="#666666" class="indexFieldsTable" cellpadding="6" cellspacing="1" border="0" align="center" id="indexedContent_${result.docReader.id}" style="display:none; width:95%;margin-top:8px;margin-bottom:4px;">
							
						<c:if test='${not empty myDocMap["itemContentType"]}'>
							<tr bgcolor='#DDDFF7'> 
								<td>						
									<div class="searchResultValues"><b>Conent type:</b> &nbsp;<c:out value='${myDocMap["itemContentType"]}' escapeXml='true'/></div>
								</td>
							</tr>
						</c:if>						
						
						<c:if test='${not empty myDocMap["itemContentTitle"]}'>
							<tr bgcolor='#DDDFF7'> 
								<td>							
									<div class="searchResultValues" style="padding-top:4px"><b>HTML title:</b> &nbsp;<c:out value='${myDocMap["itemContentTitle"]}' escapeXml='true'/></div>
								</td>
							</tr>						
						</c:if>
						<c:if test='${not empty myDocMap["itemContentHeaders"]}'>		
							<tr bgcolor='#DDDFF7'> 
								<td>							
									<div class="searchResultValues" style="padding-top:4px"><b>HTML header text:</b> &nbsp;<c:out value='${myDocMap["itemContentHeaders"]}' escapeXml='true'/></div>
								</td>
							</tr>			
						</c:if>
						<c:if test='${not empty myDocMap["itemContent"]}'>					
							<tr bgcolor='#DDDFF7'> 
								<td>							
									<div class="searchResultValues" style="padding-top:4px"><b>Full content:</b> &nbsp;<c:out value='${myDocMap["itemContent"]}' escapeXml='true'/></div>
									<div class="searchResultValues" style="padding-top:3px">
										<nobr>[ <a 	href="javascript:toggleArrow('indexedContent_${result.docReader.id}');" 
											title="Click to close content display" 
											class="vocabHeading">close</a> ]</nobr>
									</div>								
								</td>
							</tr>						
						</c:if>
						</table>
					</c:if>
				</c:if>

				<%-- Display the full field data from the Lucene Document Map --%>
				${luceneEntryDisplay}
				
				</td>					
			</tr>					
			<tr>
			<td colspan="2">					
								
				<c:if test='${not empty myDocMap["emailPrimary"]}'>
					<div class="searchResultValues"><em>Primary e-mail:</em>&nbsp;
						<c:forTokens items='${myDocMap["emailPrimary"]}' delims=" " var="item" varStatus="i">
							${item}${i.last == true ? "" : ","}
						</c:forTokens>
					</div>
				</c:if>
				<c:if test='${not empty myDocMap["emailAlt"]}'>
					<div class="searchResultValues"><em>Alternate e-mail:</em>&nbsp;
						<c:forTokens items='${myDocMap["emailAlt"]}' delims=" " var="item" varStatus="i">
							${item}${i.last == true ? "" : ","}
						</c:forTokens>
					</div>						
				</c:if>	
				<c:if test='${not empty myDocMap["emailOrganization"]}'>
					<div class="searchResultValues"><em>Organization e-mail:</em>&nbsp;
						<c:forTokens items='${myDocMap["emailOrganization"]}' delims=" " var="item" varStatus="i">
							${item}${i.last == true ? "" : ","}
						</c:forTokens>
					</div>						
				</c:if>
					
				<div class="searchResultValues"><em>Accession status:</em> &nbsp;<bean:write name="result" property="docReader.accessionStatus" filter="false" /></div>
				<div class="searchResultValues"><em>Accession date:</em> &nbsp;<bean:write name="result" property="docReader.accessionDate" filter="false" /></div>
				<div class="searchResultValues"><em>Creation date:</em> &nbsp;<bean:write name="result" property="docReader.creationDate" filter="false" /></div>										
				<div class="searchResultValues"><em>Whats new date:</em> &nbsp;<bean:write name="result" property="docReader.whatsNewDate" filter="false" /></div>					
				<div class="searchResultValues"><em>Whats new type:</em> &nbsp;<bean:write name="result" property="docReader.whatsNewType" filter="false" /></div>
				<div class="searchResultValues"><em>Whats new date (muiti-doc):</em> &nbsp;<bean:write name="result" property="docReader.multiWhatsNewDate" filter="false" /></div>					
				<div class="searchResultValues"><em>Whats new type (muiti-doc):</em> &nbsp;<bean:write name="result" property="docReader.multiWhatsNewType" filter="false" /></div>
				
				<c:if test="${displayType == 'full'}">
					<c:if test='${not empty result.docReader.beneficiary}'>					
						<div class="searchResultValues"><em>Beneficiary:</em> &nbsp;
						<c:forEach items="${result.docReader.beneficiary}" var="item" varStatus="i">
							${f:replaceFirst(item,"GEM:|DLESE:","")}${i.last == true ? "" : ","}
						</c:forEach>
						</div>
					</c:if>
					<c:if test='${not empty result.docReader.toolFor}'>					
						<div class="searchResultValues"><em>Tool for:</em> &nbsp;
						<c:forEach items="${result.docReader.toolFor}" var="item" varStatus="i">
							${f:replaceFirst(item,"GEM:|DLESE:","")}${i.last == true ? "" : ","}
						</c:forEach>
						</div>
					</c:if>
					<c:if test='${not empty result.docReader.instructionalGoal}'>					
						<div class="searchResultValues"><em>Instructional goal:</em> &nbsp;
						<c:forEach items="${result.docReader.instructionalGoal}" var="item" varStatus="i">
							${f:replaceFirst(item,"Goals: |GEM:|DLESE:","")}${i.last == true ? "" : ","}
						</c:forEach>
						</div>
					</c:if>
					<c:if test='${not empty result.docReader.teachingMethod}'>					
						<div class="searchResultValues"><em>Teaching method:</em> &nbsp;
						<c:forEach items="${result.docReader.teachingMethod}" var="item" varStatus="i">
							${f:replaceFirst(item,"GEM:|DLESE:","")}${i.last == true ? "" : ","}
						</c:forEach>
						</div>
					</c:if>
					<c:if test='${fn:length(result.docReader.typicalAgeRange) > 0}'>					
						<div class="searchResultValues"><em>Typical age range:</em> &nbsp;
						<c:forEach items="${result.docReader.typicalAgeRange}" var="item" varStatus="i">
							${f:replaceFirst(item,"GEM:|DLESE:","")}${i.last == true ? "" : ","}
						</c:forEach>
						</div>
					</c:if>	
								
					<logic:notEmpty name="result" property="docReader.keywordsDisplay">
						<div class="searchResultValues"><em>Keywords:</em> &nbsp;<bean:write name="result" property="docReader.keywordsDisplay" filter="false" /></div>				
					</logic:notEmpty> 				
					<logic:notEmpty name="result" property="docReader.gradeRanges">
						<div class="searchResultValues"><em>Grade ranges:</em> &nbsp;
							<c:forEach items="${result.docReader.gradeRanges}" var="vocabId" varStatus="i">
								<vocabs:uiLabel 
									metaFormat="adn" 
									audience="${initParam.metadataVocabAudience}" 
									language="${initParam.metadataVocabLanguage}" 
									field="gr" value="${vocabId}" />${i.last ? '' : ','}
							</c:forEach>
						</div>				
					</logic:notEmpty>					
					<logic:notEmpty name="result" property="docReader.resourceTypes">
						<div class="searchResultValues"><em>Resource types:</em> &nbsp;
							<c:forEach items="${result.docReader.resourceTypes}" var="vocabId" varStatus="i">
								<vocabs:uiLabel 
									metaFormat="adn" 
									audience="${initParam.metadataVocabAudience}" 
									language="${initParam.metadataVocabLanguage}" 
									field="re" value="${vocabId}" />${i.last ? '' : ','}
							</c:forEach>						
						</div>				
					</logic:notEmpty>					
					<logic:notEmpty name="result" property="docReader.subjects">
						<div class="searchResultValues"><em>Subjects:</em> &nbsp;
							<c:forEach items="${result.docReader.subjects}" var="vocabId" varStatus="i">
								<vocabs:uiLabel 
									metaFormat="adn" 
									audience="${initParam.metadataVocabAudience}" 
									language="${initParam.metadataVocabLanguage}" 
									field="su" value="${vocabId}" />${i.last ? '' : ','}
							</c:forEach>
						</div>				
					</logic:notEmpty>
								
					<%-- Show related resources --%>
					<div class="searchResultValues"><em>Has related resource:</em> &nbsp; ${result.docReader.hasRelatedResource}</div>
					<c:forEach items="${result.docReader.relatedResourceIds}" varStatus="status" var="value"> 
						<c:if test="${status.first}"><div class="searchResultValues"><em>Related resources by ID:</em> &nbsp;</c:if>
						<c:if test="${not status.last}"><a href='display.do?fullview=${value}'>${value}</a>,</c:if>
						<c:if test="${status.last}"><a href='display.do?fullview=${value}'>${value}</a></div></c:if>
					</c:forEach>					
					<c:forEach items="${result.docReader.relatedResourceUrls}" varStatus="status" var="value"> 
						<c:if test="${status.first}"><div class="searchResultValues"><em>Related resources by URL:</em> &nbsp;</c:if>
						<c:if test="${not status.last}"><a href="${value}" target="_blank">${value}</a>,</c:if>
						<c:if test="${status.last}"><a href="${value}" target="_blank">${value}</a></div></c:if>
					</c:forEach>
				</c:if>
				
				<div class="searchResultValues"><em>File indexed on:</em> ${result.docReader.dateFileWasIndexedString}</div>
				<logic:notEmpty name="result" property="docReader.errorStrings">
					<div class="searchResultValues"><em>ID mapper errors:</em> &nbsp;
					<bean:write name="result" property="docReader.errorStrings[0]" filter="false" /><logic:iterate id="errorString" 
					name="result" property="docReader.errorStrings" offset="1">,
					<bean:write name="errorString" filter="false" /></logic:iterate></div>				
				</logic:notEmpty>
				<logic:present name="result" property="docReader.validationReport">
					<div class="searchResultValues"><em>Validation error:</em> <font color=red><bean:write name="result" property="docReader.validationReport" filter="false" /></font></div>
				</logic:present>				
				
				<c:if test="${displayType == 'full'}">
					<div class="searchResultValues"><em>ID Mapper data:</em>
						<%@ include file="id_mapper_data.jsp" %>
					</div>
				</c:if>
				
				
				<c:if test="${displayType == 'full'}">
					
					<%-- Annotations for this item-level record --%>
					<logic:notEmpty name="result" property="docReader.annotationResultDocs">
					<br><br>
					<div class="searchResultValues"><em>Annotations for this resource:</em></div>
						<br>
						<c:forEach items="${result.docReader.annotationResultDocs}" var="annotationResult" end="${MAX_ANNOS-1}">
							
							<logic:notEmpty name="annotationResult" property="docReader.title">
								<div class="searchResultValues2"><font color="#333333">
									<b><bean:write name="annotationResult" property="docReader.title" filter="false" /></b>
								</font></div>
							</logic:notEmpty>
							<logic:empty name="annotationResult" property="docReader.title">
								<div class="searchResultValues2"><font color="#333333">
									<b>Annotation</b>
								</font></div>
							</logic:empty>							
							<logic:notEmpty name="annotationResult" property="docReader.url"> 
								<div class="searchResultValues2"><a href='<bean:write name="annotationResult" property="docReader.url" filter="false" />' target=blank><bean:write name="annotationResult" property="docReader.url" filter="false" /></a></div>					
							</logic:notEmpty>
							<logic:notEmpty name="annotationResult" property="docReader.description">
								<div class="searchResultValues2"><bean:write name="annotationResult" property="docReader.description" filter="false" /></div>
							</logic:notEmpty>
							<div class="searchResultValues2"><em>Annotation pathway:</em> &nbsp;${empty annotationResult.docReader.pathway ? '(none)' : annotationResult.docReader.pathway}</div>
							<div class="searchResultValues2"><em>Annotation collection:</em> &nbsp;
								<c:set var="annoCollection">
									<vocabs:uiLabel 
										metaFormat="dlese_collect" 
										audience="${initParam.metadataVocabAudience}" 
										language="${initParam.metadataVocabLanguage}" 
										field="ky" value="${annotationResult.docReader.collectionKey}" />
								</c:set>
								${fn:contains(annoCollection,'getUiValueLabel is NULL') ? annotationResult.docReader.collectionKey : annoCollection}
							</div>	
							<div class="searchResultValues2"><em>Annotation type:</em> &nbsp;<bean:write name="annotationResult" property="docReader.type" filter="false" /></div>				
							<div class="searchResultValues2"><em>Annotation ID:</em> &nbsp;<a href='display.do?fullview=<bean:write name="annotationResult" property="docReader.id" filter="false" />' ><bean:write name="annotationResult" property="docReader.id" filter="false" /></a></div>	
							<br>
						</c:forEach>
						<c:if test="${fn:length(result.docReader.annotationResultDocs) > MAX_ANNOS-1}">
							<div class="searchResultValues2">
								<font color="#333333">
									Plus ${fn:length(result.docReader.annotationResultDocs) - MAX_ANNOS} more...
								</font>
							</div>
						</c:if>
						
					</logic:notEmpty>   
					
					
					<%-- Associated records for this item-level record --%>
					<%-- <logic:notEmpty name="result" property="docReader.associatedIds">
					<p><div class="searchResultValues"><em>Records that refer to the same resource:</em> &nbsp;
						<logic:iterate id="associatedId" name="result" property="docReader.associatedIds">
							[ <bean:write name="associatedId" filter="false" /> ]
						</logic:iterate></p>
					</logic:notEmpty>  --%>
					
					<%-- Show the available associated records --%>
					<logic:notEmpty name="result" property="docReader.associatedItemResultDocs">
						<br>
						<div class="searchResultValues"><em>Additional records that catalog this resource:</em></div>
						<br>
						<logic:iterate id="associatedItemResults" name="result" property="docReader.associatedItemResultDocs">				
							<div class="searchResultValues2">
								<a href='<bean:write name="associatedItemResults" property="docReader.url" filter="false" />' target="_blank" style="text-decoration: none; color: #333333"><b><bean:write name="associatedItemResults" property="docReader.title" filter="false" /></b></a>
							</div>
							<div class="searchResultValues2"><a href='<bean:write name="associatedItemResults" property="docReader.url" filter="false" />' target="_blank"><bean:write name="associatedItemResults" property="docReader.url" filter="false" /></a></div>
							<div class="searchResultValues2"><bean:write name="associatedItemResults" property="docReader.description" filter="false" /></div>
							<div class="searchResultValues2"><em>Id:</em> &nbsp;<a href='display.do?fullview=<bean:write name="associatedItemResults" property="docReader.id" filter="false" />' ><bean:write name="associatedItemResults" property="docReader.id" filter="false" /></a></div>
							<div class="searchResultValues2"><em>Collection:</em> &nbsp;
								<vocabs:uiLabel 
									metaFormat="dlese_collect" 
									audience="${initParam.metadataVocabAudience}" 
									language="${initParam.metadataVocabLanguage}" 
									field="ky" value="${associatedItemResults.docReader.collectionKey}" /></div>
							<br>
						</logic:iterate>
					
					</logic:notEmpty> 
					
					<%-- Report any associated records that are missing in the index --%>
					<%-- <logic:notEmpty name="result" property="docReader.missingAssociatedItemIds">
					<table width="95%" align="center" cellpadding="8" cellspacing="1" border="0">
						<tr><td>					
						The following IDs were identified as referencing the same resource but do not exist in the index:</div>
						<logic:iterate id="missingAssocItem" name="result" property="docReader.missingAssociatedItemIds">
							&nbsp; <bean:write name="missingAssocItem" filter="false" /></div>hi
						</logic:iterate>
						</td></tr>
					</table>
					</logic:notEmpty> --%>	
				</c:if>
				
			</td>
		</tr>
	</c:when><%-- End item-level record --%>		

	
	<%-- =========================== Display annotation records =========================== --%>
	<c:when test="${result.docReader.readerType == 'DleseAnnoDocReader'}">
		<logic:notEmpty name="result" property="docReader.title">
			<tr> 
				<td class='admin_blue1'> 
					<font size="+1"><b><bean:write name="result" property="docReader.title" filter="false" /></b></font>
					<logic:equal name="result" property="docReader.partOfDrc" value="true">
						<img alt="" src="../images/DRC_icon.gif">
					</logic:equal>							
					<logic:notEmpty name="result" property="docReader.url"><br>		 
						<a href='<bean:write name="result" property="docReader.url" filter="false" />' target="_blank"><bean:write name="result" property="docReader.url" filter="false" /></a>										
						<logic:match name="result" property="docReader.status" value="completed">
							(completed)
						</logic:match>
						<logic:match name="result" property="docReader.status" value="in progress">
							(in progress)
						</logic:match>						
					</logic:notEmpty>						
				</td>					
				<td align="right" class='admin_blue1'> 
					<font color="#7DAF4B"><b>Annotation</b></font>
					${editRecordButton} 
				</td>	
			</tr>									
		</logic:notEmpty>
		<logic:empty name="result" property="docReader.title">
			<tr> 
				<td class='admin_blue1'> 
					<font size=+1><b>Annotation</b></font>
					<logic:notEmpty name="result" property="docReader.url"><br>		 
						<a href='<bean:write name="result" property="docReader.url" filter="false" />' target="_blank"><bean:write name="result" property="docReader.url" filter="false" /></a>					
					</logic:notEmpty>							
				</td>
				<td align="right" class='admin_blue1'> <font color="#7DAF4B"><b>Annotation</b></font> </td>	
			</tr>
		</logic:empty>
			<tr>
				<td colspan="2">
					<logic:notEmpty name="result" property="docReader.description">
						<div class="searchResultValues"><bean:write name="result" property="docReader.description" filter="false" /></div>
					</logic:notEmpty>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<%-- Display the full field data from the Lucene Document Map --%>
					${luceneEntryDisplay}					
				</td>
			</tr>
			<tr>
				<td colspan="2">								
					<div class="searchResultValues"><em>Pathway:</em> &nbsp;${empty result.docReader.pathway ? '(none)' : result.docReader.pathway}</div>
					<div class="searchResultValues"><em>Type:</em> &nbsp;<bean:write name="result" property="docReader.type" filter="false" /></div>				
					<div class="searchResultValues"><em>Format:</em> &nbsp;${result.docReader.format}</div>
					<div class="searchResultValues"><em>Status:</em> &nbsp;<bean:write name="result" property="docReader.status" filter="false" /></div>
					<c:if test="${not empty result.docReader.rating}">
						<div class="searchResultValues"><em>Star rating:</em> &nbsp;${result.docReader.rating} star${result.docReader.rating > 1 ? 's' : ''}</div>
					</c:if>
					<div class="searchResultValues"><em>Whats new date:</em> &nbsp;<bean:write name="result" property="docReader.whatsNewDate" filter="false" /></div>
					<div class="searchResultValues"><em>Whats new type:</em> &nbsp;<bean:write name="result" property="docReader.whatsNewType" filter="false" /></div>
					<div class="searchResultValues"><em>Part of DRC:</em> &nbsp;<bean:write name="result" property="docReader.partOfDrc" filter="false" /></div>
					<div class="searchResultValues"><em>Record last modified:</em> <bean:write name="result" property="docReader.lastModifiedString" filter="false" /></div>
					<div class="searchResultValues"><em>Record content last modified (OAI mod time):</em> <bean:write name="result" property="docReader.oaiLastModifiedString" filter="false" /></div>									
					<div class="searchResultValues"><em>File location:</em> 
						<c:choose>
							<c:when test="${result.docReader.fileExists == 'true'}">
								<bean:write name="result" property="docReader.docsource" filter="false" />
							</c:when>
							<c:otherwise>
								No cached file exists. Record content resides in the index only.
							</c:otherwise>
						</c:choose>
					</div>
					<logic:present name="result" property="docReader.validationReport">
						<div class="searchResultValues"><em>Validation error:</em> <font color=red><bean:write name="result" property="docReader.validationReport" filter="false" /></font></div>
					</logic:present>					
					<logic:match name="result" property="docReader.deleted" value="true">
						<div class="searchResultValues"><em>Status:</em> <font color="red">DELETED</font></div>
					</logic:match>
				
					<logic:notEmpty name="result" property="docReader.annotatedItemResultDoc">
				</td>
			</tr>
			<tr>
				<td>	
						<div class="searchResultValues"><em>Annotated resource:</em></div>
						<div class="searchResultValues2">
							<a href='<bean:write name="result" property="docReader.annotatedItemResultDoc.docReader.url" filter="false" />' target="_blank" style="text-decoration: none; color: #333333"><b><bean:write name="result" property="docReader.annotatedItemResultDoc.docReader.title" filter="false" /></b></a>
						</div>
						<div class="searchResultValues2"><a href='<bean:write name="result" property="docReader.annotatedItemResultDoc.docReader.url" filter="false" />' target="_blank"><bean:write name="result" property="docReader.annotatedItemResultDoc.docReader.url" filter="false" /></a></div>
						<div class="searchResultValues2"><em>Description:</em> &nbsp;<bean:write name="result" property="docReader.annotatedItemResultDoc.docReader.description" filter="false" /></div>
						<div class="searchResultValues2"><em>ID:</em> &nbsp;<a href='display.do?fullview=<bean:write name="result" property="docReader.itemId" filter="false" />' ><bean:write name="result" property="docReader.itemId" filter="false" /></a></div>		
					</logic:notEmpty>							
				</td>
			</tr>
	</c:when>		

	<%-- =========================== Display collection records =========================== --%>
	<c:when test="${result.docReader.readerType == 'DleseCollectionDocReader'}">
		<tr> 
			<td class='admin_blue1'>
				<font size=+1><b><bean:write name="result" property="docReader.shortTitle" filter="false" /></b></font>	
				<logic:equal name="result" property="docReader.partOfDRC" value="true">
					<img alt="" src="../images/DRC_icon.gif">
				</logic:equal>
				<br>						
				<logic:notEmpty name="result" property="docReader.collectionUrl">
					<a href='<bean:write name="result" property="docReader.collectionUrl" filter="false" />' target="_blank"><bean:write name="result" property="docReader.collectionUrl" filter="false" /></a> (collection URL)<br>			
				</logic:notEmpty>
				<logic:notEmpty name="result" property="docReader.scopeUrl">
					<a href='<bean:write name="result" property="docReader.scopeUrl" filter="false" />' target="_blank"><bean:write name="result" property="docReader.scopeUrl" filter="false" /></a> (scope statement)
				</logic:notEmpty>
			</td>
			<td align="right" class='admin_blue1'> 
				<font color="#EC9F00"><b>Collection</b></font> 
				${editRecordButton}
			</td>	
		</tr>
		<tr> 
			<td colspan="2">
				<bean:write name="result" property="docReader.description" filter="false" />
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<%-- Display the full field data from the Lucene Document Map --%>
				${luceneEntryDisplay}					
			</td>
		</tr>			
		<tr>
			<td>
				<%-- <div class="searchResultValues"><em>Member of collection:</em> &nbsp;<a href='display.do?fullview=<bean:write name="result" 
						property="docReader.myCollectionDoc.id" filter="false" />'><bean:write name="result" 
						property="docReader.myCollectionDoc.shortTitle" filter="false" /></a></div>	 --%>						
				<div class="searchResultValues"><em>Accession status:</em> &nbsp;<bean:write name="result" property="docReader.accessionStatus" filter="false" /></div>
				<c:set var="date" value="${result.docReader.accessionDateDate}"/>
				<c:if test="${not empty date}">
					<div class="searchResultValues"><em>Accession date:</em> &nbsp;
						<fmt:formatDate value="${date}" pattern="MMMM dd, yyyy"/> 
					</div>
				</c:if>					
				<div class="searchResultValues"><em>Collection key:</em> &nbsp;<bean:write name="result" property="docReader.key" filter="false" /></div>
				 <logic:notEmpty name="result" property="docReader.reviewProcess">
					<div class="searchResultValues"><em>Review process:</em> &nbsp;<bean:write name="result" property="docReader.reviewProcess" filter="false" /></div>				
				</logic:notEmpty> 
				 <logic:notEmpty name="result" property="docReader.reviewProcessUrl">
					<div class="searchResultValues"><em>Review process URL:</em> &nbsp;<a href='<bean:write name="result" property="docReader.reviewProcessUrl" filter="false" />' target="_blank"><bean:write name="result" property="docReader.reviewProcessUrl" filter="false" /></a></div>				
				</logic:notEmpty> 										
				 <logic:notEmpty name="result" property="docReader.keywordsDisplay">
					<div class="searchResultValues"><em>Keywords:</em> &nbsp;<bean:write name="result" property="docReader.keywordsDisplay" filter="false" /></div>				
				</logic:notEmpty> 				
				 <logic:notEmpty name="result" property="docReader.gradeRanges">
					<div class="searchResultValues"><em>Grade ranges:</em> &nbsp;
						<c:forEach items="${result.docReader.gradeRanges}" var="vocabId" varStatus="i">
							<vocabs:uiLabel 
								metaFormat="dlese_collect" 
								audience="${initParam.metadataVocabAudience}" 
								language="${initParam.metadataVocabLanguage}" 
								field="gr" value="${vocabId}" />${i.last ? '' : ','}
						</c:forEach>					
					</div>				
				</logic:notEmpty>
				<logic:notEmpty name="result" property="docReader.subjects">
					<div class="searchResultValues"><em>Subjects:</em> &nbsp;
						<c:forEach items="${result.docReader.subjects}" var="vocabId" varStatus="i">
							<vocabs:uiLabel 
								metaFormat="dlese_collect" 
								audience="${initParam.metadataVocabAudience}" 
								language="${initParam.metadataVocabLanguage}" 
								field="su" value="${vocabId}" />${i.last ? '' : ','}
						</c:forEach>					
					</div>				
				</logic:notEmpty>
			</td>
			
			<%-- Display collection summary for the 'collect' records: --%>
			<c:if test="${result.docReader.collection == 'collect'}">
			<td valign="top">
				
				<table bgcolor="eeeeee" border="0" cellpadding="0" cellspacing="1">
					<tr>
						<td colspan="2">
							<div class="searchResultValues">
							<font color="#333333"><b>Collection summary</b></font>
							</div>
						</td>
					</tr>						
					<tr>
						<td nowrap>
							<div class="searchResultValues">
								<em>Format of files:</em> 
							</div>
						</td>
						<td>
							<div class="searchResultValues">
								<bean:write name="result" property="docReader.formatOfRecords" filter="false" />
							</div>
						</td>
					</tr>
					<c:if test="${!isIndexedByIndexingManager}">
						<tr>
							<td nowrap>
								<div class="searchResultValues">
									<em>Num files:</em> 
								</div>
							</td>
							<td>
								<div class="searchResultValues">
									<bean:write name="result" property="docReader.numFiles" filter="false" />
								</div>
							</td>
						</tr>
					</c:if>
					<tr>
						<td nowrap>
							<div class="searchResultValues">
								<em>Num indexed:</em> 
							</div>
						</td>
						<td>
							<c:choose>
								<c:when test="${result.docReader.numIndexed == 0}">
									<div class="searchResultValues">0</div>
								</c:when>
								<c:when test="${empty queryForm}">
									<div class="searchResultValues">Not available</div>
								</c:when>
								<c:otherwise>
									<div class="searchResultValues">	
									 
										<c:set value="key" target="${queryForm}" property="field" />
										<c:set value="dlese_collect" target="${queryForm}" property="metaFormat" />
										<c:set value="${result.docReader.key}" target="${queryForm}" property="value" />									
	
										<c:set value="${fn:trim(queryForm.vocabTerm.id)}" var="vocabKyId"/>
										<a href='query.do?q=&s=0&ky=${vocabKyId}' title='Browse records for "${result.docReader.shortTitle}"'><fmt:formatNumber type="number" value="${result.docReader.numIndexed}"/></a>									
										<c:set value="adn" target="${queryForm}" property="metaFormat" />
										
									</div>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>	
					<tr>
						<td nowrap>
							<div class="searchResultValues">
								<em>Num indexing errors:</em> 
							</div>
						</td>
						<td>
							<logic:equal name="result" property="docReader.numIndexingErrors" value="0">
								<div class="searchResultValues">0</div>
							</logic:equal>							
							<logic:notEqual name="result" property="docReader.numIndexingErrors" value="0">
								<div class="searchResultValues">
									<a href='report.do?q=error:true+AND+docdir:&quot;${f:escape(result.docReader.locationOfFiles)}&quot;&s=0&report=Indexing+errors+for+collection+<bean:write name="result" 
									property="docReader.shortTitle"/>' title="Show indexing errors for this collection"><bean:write name="result" property="docReader.numIndexingErrors"/></a>
								</div>
							</logic:notEqual>
						</td>
					</tr>						
				</table>
			</td>
			</c:if>
		</tr>
	</c:when>
	
	<%-- =========================== Display news and opportunities XML formats =========================== --%>  
	<c:when test="${result.docReader.readerType == 'NewsOppsDocReader'}">
		<tr> 
			<td class='admin_blue1'>		
				<font size=+1><b>${result.docReader.title}</b></font>					
				
				<c:if test="${not empty result.docReader.announcementUrl}">
				<br>
					<a href='${result.docReader.announcementUrl}' target="_blank">${result.docReader.announcementUrl}</a><br>			
				</c:if>					
			</td>
			<td align="right" class='admin_blue1'> 
				<nobr>
				<font color="#CC99CC"><b>News &amp; opportunities</b></font>
				${editRecordButton}
				</nobr>
			</td>					
		</tr> 
		<tr colspan="2"> 
			<td>${result.docReader.description}</td>
		</tr>
		<tr> 
			<td colspan="2">
				${luceneEntryDisplay}					
			</td>
		</tr> 				
		<tr>
			<td colspan="2">
				<c:forEach items="${result.docReader.keywords}" varStatus="status" var="value"> 
					<c:if test="${status.first}"><div class="searchResultValues"><em>Keywords:</em> &nbsp;</c:if>
					<c:if test="${not status.last}">${value},</c:if>
					<c:if test="${status.last}">${value}</div></c:if>
				</c:forEach>
				<c:forEach items="${result.docReader.topics}" varStatus="status" var="value"> 
					<c:if test="${status.first}"><div class="searchResultValues"><em>Topics:</em> &nbsp;</c:if>
					<c:if test="${not status.last}">${value},</c:if>
					<c:if test="${status.last}">${value}</div></c:if>
				</c:forEach>
				<c:forEach items="${result.docReader.announcements}" varStatus="status" var="value"> 
					<c:if test="${status.first}"><div class="searchResultValues"><em>Announcement type:</em> &nbsp;</c:if>
					<c:if test="${not status.last}">${value},</c:if>
					<c:if test="${status.last}">${value}</div></c:if>
				</c:forEach>
				<c:forEach items="${result.docReader.audiences}" varStatus="status" var="value"> 
					<c:if test="${status.first}"><div class="searchResultValues"><em>Audience:</em> &nbsp;</c:if>
					<c:if test="${not status.last}">${value},</c:if>
					<c:if test="${status.last}">${value}</div></c:if>
				</c:forEach>
				<c:forEach items="${result.docReader.diversities}" varStatus="status" var="value"> 
					<c:if test="${status.first}"><div class="searchResultValues"><em>Diversities:</em> &nbsp;</c:if>
					<c:if test="${not status.last}">${value},</c:if>
					<c:if test="${status.last}">${value}</div></c:if>
				</c:forEach>
				<c:forEach items="${result.docReader.sponsors}" varStatus="status" var="value"> 
					<c:if test="${status.first}"><div class="searchResultValues"><em>Sponsors:</em> &nbsp;</c:if>
					<c:if test="${not status.last}">${value},</c:if>
					<c:if test="${status.last}">${value}</div></c:if>
				</c:forEach>					
				<c:forEach items="${result.docReader.cityStates}" varStatus="status" var="value"> 
					<c:if test="${status.first}"><div class="searchResultValues"><em>Locations:</em> &nbsp;</c:if>
					<c:if test="${not status.last}">${value},</c:if>
					<c:if test="${status.last}">${value}</div></c:if>
				</c:forEach>
				 <c:set var="date" value="${result.docReader.recordCreationtDate}"/>
				<c:if test="${not empty date}">
					<div class="searchResultValues"><em>Record creation date:</em> &nbsp;
						<fmt:formatDate value="${date}" pattern="yyyy-MM-dd"/>
					</div>
				</c:if>						
				<c:set var="date" value="${result.docReader.eventStartDate}"/>
				<c:if test="${not empty date}">
					<div class="searchResultValues"><em>Event start date:</em> &nbsp;
						<fmt:formatDate value="${date}" pattern="yyyy-MM-dd"/>
					</div>
				</c:if>
				<c:set var="date" value="${result.docReader.eventStopDate}"/>
				<c:if test="${not empty date}">
					<div class="searchResultValues"><em>Event stop date:</em> &nbsp;
						<fmt:formatDate value="${date}" pattern="yyyy-MM-dd"/>
					</div>
				</c:if>									
			</td>
		</tr>			
	</c:when>

	<%-- =========================== Display DC formats =========================== --%>
	<c:when test="${fn:contains(result.docReader.availableFormats,'oai_dc') || fn:contains(result.docReader.availableFormats,'nsdl_dc')}">
		<c:set var="dcXml">
			<c:choose>
				<c:when test="${result.docReader.nativeFormat == 'oai_dc' || result.docReader.nativeFormat == 'nsdl_dc'}">
					${result.docReader.xml}
				</c:when>
				<c:when test="${fn:contains(result.docReader.availableFormats,'oai_dc')}">
					<c:set target="${result.docReader}" property="requestedXmlFormat" value="oai_dc"/>
					${result.docReader.requestedXmlFormat}
				</c:when>				
				<c:otherwise>
					<c:set target="${result.docReader}" property="requestedXmlFormat" value="nsdl_dc"/>
					${result.docReader.requestedXmlFormat}
				</c:otherwise>
			</c:choose>
		</c:set>
		<%-- Grab a DC DOM  --%>
		<x:transform xslt="${f:removeNamespacesXsl()}" xml="${dcXml}" var="dcDom"/>		
		<c:set var="dcTitle"><x:out select="$dcDom//title"/></c:set>
		<c:set var="dcUrl">
			<x:out select="$dcDom//identifier[starts-with(text(),'http')] | $dcDom//identifier[starts-with(text(),'ftp')]"/>
		</c:set>		
		<tr> 
			<td class='admin_blue1'>				
				<font size=+1><b>${empty dcTitle ? 'No title provided' : dcTitle}</b></font>					
				<c:if test="${not empty dcUrl}">
					<br/><a href='${dcUrl}' target="_blank">${dcUrl}</a><br/>			
				</c:if>
			</td>
			<td align="right" class='admin_blue1'> 
				<font color="#787DBC"><b><nobr>DC compatible (${result.docReader.nativeFormat})</nobr></b></font>
				${editRecordButton}
			</td>					
		</tr>
		<x:if select="$dcDom//description">
			<tr>
				<td colspan="2">
					<x:forEach select="$dcDom//description">
						<div style="padding-bottom:4px"><x:out select="."/></div>
					</x:forEach>
				</td>
			</tr>
		</x:if>
				
		<tr>
			<td colspan="2">
				<%-- Display the full field data from the Lucene Document Map --%>
				${luceneEntryDisplay}					
			</td>
		</tr>			
		<tr colspan="2"> 
			<td>
				<c:set var="dcField" value=""/>
				<x:forEach select="$dcDom//identifier" varStatus="i">
					<c:set var="dcValue"><x:out select="."/></c:set>
					<c:if test="${fn:startsWith(dcValue,'http') || fn:startsWith(dcValue,'ftp')}">
						<c:set var="dcValue"><a href="${dcValue}" target="_blank">${dcValue}</a></c:set>
					</c:if>
					<c:if test="${not empty fn:trim(dcValue)}">
						<c:set var="dcField">${dcField} ${dcValue}${i.last? '' : ','}</c:set>
					</c:if>
				</x:forEach>
				<c:if test="${not empty fn:trim(dcField)}">
					<div class="searchResultValues"><em>Identifier:</em> &nbsp;${dcField}</div>
				</c:if>

				<c:set var="nsdlResourceHandleDisp">
					<x:forEach select="$dcDom//identifier[@type='nsdl_dc:ResourceHandle']" varStatus="i">
						<c:set var="handleId"><x:out select="substring-after(.,'hdl:')"/></c:set>
						<span style="white-space:nowrap;">
							${handleId}${i.last ? '' : ','}
						</span>
					</x:forEach>
				</c:set>
				<c:if test="${not empty fn:trim(nsdlResourceHandleDisp)}">
					<div class="searchResultValues"><em>Resource handle:</em> ${nsdlResourceHandleDisp}</div>
				</c:if>

				<c:set var="nsdlMetaHandleDisp">
					<x:forEach select="$dcDom//identifier[@type='nsdl_dc:MetadataHandle']" varStatus="i">
						<c:set var="handleId"><x:out select="substring-after(.,'hdl:')"/></c:set>
						<span style="white-space:nowrap;">
							${handleId}${i.last ? '' : ','}
						</span>						
					</x:forEach>
				</c:set>
				<c:if test="${not empty fn:trim(nsdlMetaHandleDisp)}">
					<div class="searchResultValues"><em>Metadata handle:</em> ${nsdlMetaHandleDisp}</div>
				</c:if>

				<c:set var="nsdlPartnerId">
					<x:forEach select="$dcDom//identifier[@type='nsdl_dc:NSDLPartnerID']" varStatus="i">
						<x:out select="."/>${i.last ? '' : ','}
					</x:forEach>
				</c:set>
				<c:if test="${not empty fn:trim(nsdlPartnerId)}">
					<div class="searchResultValues"><em>Partner ID:</em> ${nsdlPartnerId}</div>
				</c:if>
				
				<%-- Display for native nsdl_dc data --%>
				<c:if test="${result.docReader.nativeFormat == 'nsdl_dc'}">
					<%-- Weeding status: (values are: IN,OUT,NEEDS_QA,NEEDS_REVIEW,NONE) --%>
					<div class="searchResultValues">
						<em>Weeding status:</em>&nbsp;
						
						<c:set var="weedingStatus" value="${myDocMapDeDuped['weeding.statusCode']}"/>
						<c:choose>
							<c:when test="${empty weedingStatus}">
								None specified (empty)
							</c:when>					
							<c:otherwise>
								<span style="text-transform:capitalize;">
									<a href="<c:url value='/admin/query.do'/>?q=weeding.statusCode:&quot;${weedingStatus}&quot;&sifmts=0nsdl_dc&s=0">${fn:toLowerCase(f:replaceAll(weedingStatus,'_',' '))}</a>
								</span>
							</c:otherwise>
						</c:choose>	
					</div>
	
					<%-- Weeding Reason: --%>
					<div class="searchResultValues">
						<em>Weeding reason code:</em>&nbsp;
						<c:set var="weedingReason" value="${myDocMapDeDuped['weeding.reasonCode']}"/>
						<c:choose>
							<c:when test="${empty weedingReason}">
								None specified (empty)
							</c:when>					
							<c:otherwise>
								<a href="<c:url value='/admin/query.do'/>?q=weeding.reasonCode:&quot;${weedingReason}&quot;&sifmts=0nsdl_dc&s=0">${weedingReason == 'NONE' ? 'None' : weedingReason}</a>
							</c:otherwise>
						</c:choose>
					</div>
	
					<%-- LAR readyness: --%>
					<div class="searchResultValues">
						<em>LAR readiness:</em>&nbsp;
						<c:set var="larStatus" value="${myDocMapDeDuped['/text//nsdl_dc/readiness']}"/>
						<c:choose>
							<c:when test="${empty larStatus}">
								None specified (empty)
							</c:when>						
							<c:otherwise>
								<a href="<c:url value='/admin/query.do'/>?q=/key//nsdl_dc/readiness:&quot;${larStatus}&quot;&s=0">${larStatus}</a>
							</c:otherwise>
						</c:choose>
					</div>
				</c:if>
				
				<c:set var="dcField" value=""/>
				<x:forEach select="$dcDom//isPartOf" varStatus="i">
					<c:set var="dcValue"><x:out select="."/></c:set>
					<c:if test="${fn:startsWith(dcValue,'http') || fn:startsWith(dcValue,'ftp')}">
						<c:set var="dcValue"><a href="${dcValue}" target="_blank">${dcValue}</a></c:set>
					</c:if>
					<c:if test="${not empty fn:trim(dcValue)}">
						<c:set var="dcField">${dcField} ${dcValue}${i.last? '' : ','}</c:set>
					</c:if>
				</x:forEach>
				<c:if test="${not empty fn:trim(dcField)}">
					<div class="searchResultValues"><em>Is Part Of:</em> &nbsp;${dcField}</div>
				</c:if>				
				
				<c:set var="dcField" value=""/>
				<x:forEach select="$dcDom//isRequiredBy" varStatus="i">
					<c:set var="dcValue"><x:out select="."/></c:set>
					<c:if test="${fn:startsWith(dcValue,'http') || fn:startsWith(dcValue,'ftp')}">
						<c:set var="dcValue"><a href="${dcValue}" target="_blank">${dcValue}</a></c:set>
					</c:if>
					<c:if test="${not empty fn:trim(dcValue)}">
						<c:set var="dcField">${dcField} ${dcValue}${i.last? '' : ','}</c:set>
					</c:if>
				</x:forEach>
				<c:if test="${not empty fn:trim(dcField)}">
					<div class="searchResultValues"><em>Is Required By:</em> &nbsp;${dcField}</div>
				</c:if>	

				
				<c:set var="dcField" value=""/>
				<x:forEach select="$dcDom//requires" varStatus="i">
					<c:set var="dcValue"><x:out select="."/></c:set>
					<c:if test="${fn:startsWith(dcValue,'http') || fn:startsWith(dcValue,'ftp')}">
						<c:set var="dcValue"><a href="${dcValue}" target="_blank">${dcValue}</a></c:set>
					</c:if>
					<c:if test="${not empty fn:trim(dcValue)}">
						<c:set var="dcField">${dcField} ${dcValue}${i.last? '' : ','}</c:set>
					</c:if>
				</x:forEach>
				<c:if test="${not empty fn:trim(dcField)}">
					<div class="searchResultValues"><em>Requires:</em> &nbsp;${dcField}</div>
				</c:if>	
								
				<c:set var="dcField" value=""/>
				<x:forEach select="$dcDom//isReferencedBy" varStatus="i">
					<c:set var="dcValue"><x:out select="."/></c:set>
					<c:if test="${fn:startsWith(dcValue,'http') || fn:startsWith(dcValue,'ftp')}">
						<c:set var="dcValue"><a href="${dcValue}" target="_blank">${dcValue}</a></c:set>
					</c:if>
					<c:if test="${not empty fn:trim(dcValue)}">
						<c:set var="dcField">${dcField} ${dcValue}${i.last? '' : ','}</c:set>
					</c:if>
				</x:forEach>
				<c:if test="${not empty fn:trim(dcField)}">
					<div class="searchResultValues"><em>Is Referenced By:</em> &nbsp;${dcField}</div>
				</c:if>	
				
				<c:set var="dcField" value=""/>
				<x:forEach select="$dcDom//conformsTo" varStatus="i">
					<c:set var="dcValue"><x:out select="."/></c:set>
					<c:if test="${fn:startsWith(dcValue,'http') || fn:startsWith(dcValue,'ftp')}">
						<c:set var="dcValue"><a href="${dcValue}" target="_blank">${dcValue}</a></c:set>
					</c:if>
					<c:if test="${not empty fn:trim(dcValue)}">
						<c:set var="dcField">${dcField} ${dcValue}${i.last? '' : ','}</c:set>
					</c:if>
				</x:forEach>
				<c:if test="${not empty fn:trim(dcField)}">
					<div class="searchResultValues"><em>Conforms To:</em> &nbsp;${dcField}</div>
				</c:if>	
													
				<c:set var="dcField" value=""/>
				<x:forEach select="$dcDom//subject" varStatus="i">
					<c:set var="dcValue"><x:out select="."/></c:set>
					<c:if test="${not empty fn:trim(dcValue)}">
						<c:set var="dcField">${dcField} ${dcValue}${i.last? '' : ','}</c:set>
					</c:if>
				</x:forEach>
				<c:if test="${not empty fn:trim(dcField)}">
					<div class="searchResultValues"><em>Subject:</em> &nbsp;${dcField}</div>
				</c:if>
				
				<c:set var="dcField" value=""/>
				<x:forEach select="$dcDom//audience" varStatus="i">
					<c:set var="dcValue"><x:out select="."/></c:set>
					<c:if test="${not empty fn:trim(dcValue)}">
						<c:set var="dcField">${dcField} ${dcValue}${i.last? '' : ','}</c:set>
					</c:if>
				</x:forEach>
				<c:if test="${not empty fn:trim(dcField)}">
					<div class="searchResultValues"><em>Audience:</em> &nbsp;${dcField}</div>
				</c:if>

				<c:set var="dcField" value=""/>
				<x:forEach select="$dcDom//educationLevel" varStatus="i">
					<c:set var="dcValue"><x:out select="."/></c:set>
					<c:if test="${not empty fn:trim(dcValue)}">
						<c:set var="dcField">${dcField} ${dcValue}${i.last? '' : ','}</c:set>
					</c:if>
				</x:forEach>
				<c:if test="${not empty fn:trim(dcField)}">
					<div class="searchResultValues"><em>Education Level:</em> &nbsp;${dcField}</div>
				</c:if>
				
				<c:set var="dcField" value=""/>
				<x:forEach select="$dcDom//type" varStatus="i">
					<c:set var="dcValue"><x:out select="."/></c:set>
					<c:if test="${not empty fn:trim(dcValue)}">
						<c:set var="dcField">${dcField} ${dcValue}${i.last? '' : ','}</c:set>
					</c:if>
				</x:forEach>
				<c:if test="${not empty fn:trim(dcField)}">
					<div class="searchResultValues"><em>Type:</em> &nbsp;${dcField}</div>
				</c:if>	
				
				<c:set var="dcField" value=""/>
				<x:forEach select="$dcDom//format" varStatus="i">
					<c:set var="dcValue"><x:out select="."/></c:set>
					<c:if test="${not empty fn:trim(dcValue)}">
						<c:set var="dcField">${dcField} ${dcValue}${i.last? '' : ','}</c:set>
					</c:if>
				</x:forEach>
				<c:if test="${not empty fn:trim(dcField)}">
					<div class="searchResultValues"><em>Format:</em> &nbsp;${dcField}</div>
				</c:if>				
				
				<c:set var="dcField" value=""/>
				<x:forEach select="$dcDom//creator" varStatus="i">
					<c:set var="dcValue"><x:out select="."/></c:set>
					<c:if test="${not empty fn:trim(dcValue)}">
						<c:set var="dcField">${dcField} ${dcValue}${i.last? '' : ','}</c:set>
					</c:if>
				</x:forEach>
				<c:if test="${not empty fn:trim(dcField)}">
					<div class="searchResultValues"><em>Creator:</em> &nbsp;${dcField}</div>
				</c:if>		
				
				<c:set var="dcField" value=""/>
				<x:forEach select="$dcDom//publisher" varStatus="i">
					<c:set var="dcValue"><x:out select="."/></c:set>
					<c:if test="${not empty fn:trim(dcValue)}">
						<c:set var="dcField">${dcField} ${dcValue}${i.last? '' : ','}</c:set>
					</c:if>
				</x:forEach>
				<c:if test="${not empty fn:trim(dcField)}">
					<div class="searchResultValues"><em>Publisher:</em> &nbsp;${dcField}</div>
				</c:if>	

				<c:set var="dcField" value=""/>
				<x:forEach select="$dcDom//rights" varStatus="i">
					<c:set var="dcValue"><x:out select="."/></c:set>
					<c:if test="${fn:startsWith(dcValue,'http') || fn:startsWith(dcValue,'ftp')}">
						<c:set var="dcValue"><a href="${dcValue}" target="_blank">${dcValue}</a></c:set>
					</c:if>
					<c:if test="${not empty fn:trim(dcValue)}">
						<c:set var="dcField">${dcField} ${dcValue}${i.last? '' : ','}</c:set>
					</c:if>
				</x:forEach>
				<c:if test="${not empty fn:trim(dcField)}">
					<div class="searchResultValues"><em>Rights:</em> &nbsp;${dcField}</div>
				</c:if>					
									
				<c:set var="dcField" value=""/>
				<x:forEach select="$dcDom//date" varStatus="i">
					<c:set var="dcValue"><x:out select="."/></c:set>
					<c:if test="${not empty fn:trim(dcValue)}">
						<c:set var="dcField">${dcField} ${dcValue}${i.last? '' : ','}</c:set>
					</c:if>
				</x:forEach>
				<c:if test="${not empty fn:trim(dcField)}">
					<div class="searchResultValues"><em>Date:</em> &nbsp;${dcField}</div>
				</c:if>				
			</td>					
		</tr>

	</c:when>	
	
	<%-- =========================== Display generic XML, non-DC formats =========================== --%>
	<c:when test="${result.docReader.readerType == 'XMLDocReader'}">
		<x:transform xslt="${f:removeNamespacesXsl()}" xml="${result.docReader.xml}" var="xDocDom"/>
		
		<%-- Look for Title in some generic locations, plus known locations for various formats --%>
		<c:set var="title"><x:out select="$xDocDom//title[1]"/></c:set>
		<c:if test="${empty title}">
			<c:set var="title"><x:out select="$xDocDom//shortTitle[1]"/></c:set>
		</c:if>
		<c:if test="${empty title}">
			<c:set var="title"><x:out select="$xDocDom//longTitle[1]"/></c:set>
		</c:if>
		<%-- Check userdata format --%>
		<c:if test="${empty title}">
			<c:set var="title">
				<x:out select="$xDocDom/java/object/void[@property='firstName']/string"/> 
				<x:out select="$xDocDom/java/object/void[@property='lastName']/string"/>
			</c:set>
		</c:if>		
		<c:if test="${empty title}">
			<c:set var="title">${result.docReader.title}</c:set>
		</c:if>
		<c:if test="${empty title}">
			<c:set var="title">XML document in the ${result.docReader.nativeFormat} format</c:set>
		</c:if>
		
		<c:set var="description"><x:out select="$xDocDom//description[1]"/></c:set>
		<c:if test="${empty description}">
			<c:set var="description">${result.docReader.description}</c:set>
		</c:if>
		<c:if test="${empty description}">
			<c:set var="description"><i>No description available. All content within this XML document has been indexed for textual searching.</i></c:set>
		</c:if>			

		<c:set var="url">${result.docReader.url}</c:set>
		<c:if test="${empty url}">
			<c:set var="url"><x:out select="$xDocDom//url[1]"/></c:set>	
		</c:if>
		<c:if test="${empty url}">
			<c:set var="url"><x:out select="$xDocDom//identifier[1]"/></c:set>
			<c:if test="${!fn:startsWith(url,'http')}">
				<c:remove var="url"/>
			</c:if>
		</c:if>
		
		<tr> 
			<td class='admin_blue1'>				
				<font size=+1><b>${title}</b></font>					
				<c:if test="${not empty url}">
					<br/><a href='${url}' target="_blank">${url}</a><br/>			
				</c:if>
			</td>
			<td align="right" class='admin_blue1'> 
				<font color="#CC99CC"><b><nobr>${result.docReader.nativeFormat} XML document</nobr></b></font>
				${editRecordButton}
			</td>					
		</tr>				
		<tr colspan="2"> 
			<td>
				${description}
			</td>					
		</tr>
		<tr>
			<td colspan="2">
				<%-- Display the full field data from the Lucene Document Map --%>
				${luceneEntryDisplay}					
			</td>
		</tr>
	</c:when>
	
	<%-- =========================== Display errors =========================== --%>
	<c:when test="${result.docReader.readerType == 'ErrorDocReader'}">		
		<tr> 
			<td class='admin_blue1'>				
				<font size=+1><b>File: <bean:write name="result" property="docReader.fileName" filter="false" /></b></font><br>
				This record had errors during the indexing process and is not available for discovery
			</td>
			<td align="right" class='admin_blue1'> 
				<font color="red"><b>Error</b></font> 
			</td>					
		</tr>
		<tr>
			<td colspan="2">
				<%-- Display the full field data from the Lucene Document Map --%>
				${luceneEntryDisplay}					
			</td>
		</tr>				
		<tr> 
			<td colspan="2">				
				Message: <bean:write name="result" property="docReader.errorMsg" filter="false" />
				<logic:empty name="result" property="docReader.errorMsg">
					<bean:write name="result" property="docReader.exceptionName" filter="false" />
				</logic:empty>
			</td>
		</tr>			
	</c:when>	
	
	<%-- =========================== Display unknown formats =========================== --%>
	<c:otherwise>
		<tr> 
			<td class='admin_blue1'>				
				Information not available
			</td>
			<td align="right" class='admin_blue1'> 
				<font color="#CC99CC"><b><nobr>Unknown fromat</nobr></b></font>
				${editRecordButton}
			</td>					
		</tr>
		<tr>
			<td colspan="2">
				<%-- Display the full field data from the Lucene Document Map --%>
				${luceneEntryDisplay}					
			</td>
		</tr>			
		<tr>
			<td colspan=2>
				<div class="searchResultValues">
					This format is unknown and appears not to be derived from XML. 
					No further information is available for display.
				</div>					
			</td>
		</tr>			
	</c:otherwise>	
</c:choose>


<%-- Display Global attributes for all Doc Types --%>
<tr>
	<td colspan=2>
		<%-- Annotation info **Dev note 5/9/2009: this was added for anno support on all types.. need to debug/cleanup *** --%>
		<logic:notEmpty name="result" property="docReader.annotationResultDocs">	
		<div class="searchResultValues"><em>Annotated by:</em>
			<a href='display.do?fullview=<bean:write name="result" property="docReader.annotationResultDocs[0].docReader.id"
			filter="false"/>'><bean:write name="result" property="docReader.annotationResultDocs[0].docReader.id"
			filter="false"/></a><c:forEach items="${result.docReader.annotationResultDocs}" var="annotationResult" begin="1" end="${MAX_ANNOS-1}">,
			<a href='display.do?fullview=<bean:write name="annotationResult" 
			property="docReader.id" filter="false" />'><bean:write name="annotationResult" 
			property="docReader.id" filter="false" /></a></c:forEach><c:if 
			test="${fn:length(result.docReader.annotationResultDocs) > MAX_ANNOS}">, plus ${fn:length(result.docReader.annotationResultDocs) - MAX_ANNOS} more...</c:if>.
			</div>
		</logic:notEmpty>
		<logic:notEmpty name="result" property="docReader.annoCollectionKeys">
			<div class="searchResultValues"><em>Annotated in collection:</em>
				<c:forEach items="${result.docReader.annoCollectionKeys}" var="vocabId" varStatus="i">				
					<c:set var="collDisplay">
						<vocabs:uiLabel 
							metaFormat="dlese_collect" 
							audience="${initParam.metadataVocabAudience}" 
							language="${initParam.metadataVocabLanguage}" 
							field="ky" value="${vocabId}" />
					</c:set>					
					<c:if test="${fn:contains(collDisplay,'getUiValueLabel is NULL')}">
						<c:set var="collDisplay">
							${vocabId}
						</c:set>
					</c:if>
					${collDisplay}${i.last ? '' : ','}
				</c:forEach>				
			</div>							
		</logic:notEmpty>				
		<logic:notEmpty name="result" property="docReader.annoTypes">				
			<div class="searchResultValues"><em>Has annotations of type:</em>
			<bean:write name="result" property="docReader.annoTypes[0]" filter="false" /><logic:iterate id="annoType" 
				name="result" property="docReader.annoTypes" offset="1">,
				<bean:write name="annoType" filter="false" />
			</logic:iterate></div>
		</logic:notEmpty>	
		<logic:notEmpty name="result" property="docReader.completedAnnoCollectionKeys">
			<div class="searchResultValues"><em>Completed annotations exist in:</em>
				<c:forEach items="${result.docReader.completedAnnoCollectionKeys}" var="vocabId" varStatus="i">
					<c:set var="collDisplay">
						<vocabs:uiLabel 
							metaFormat="dlese_collect" 
							audience="${initParam.metadataVocabAudience}" 
							language="${initParam.metadataVocabLanguage}" 
							field="ky" value="${vocabId}" />
					</c:set>
					<c:if test="${fn:contains(collDisplay,'getUiValueLabel is NULL')}">
						<c:set var="collDisplay">
							${vocabId}
						</c:set>
					</c:if>
					${collDisplay}${i.last ? '' : ','}						
				</c:forEach>						
			</div>							
		</logic:notEmpty>				
		<logic:notEmpty name="result" property="docReader.annoPathways">				
			<div class="searchResultValues"><em>Annotation pathways:</em>
			<bean:write name="result" property="docReader.annoPathways[0]" filter="false" /><logic:iterate id="annoPathway" 
				name="result" property="docReader.annoPathways" offset="1">, 
				<bean:write name="annoPathway" filter="false" />
				</logic:iterate></div>
		</logic:notEmpty>		
		<logic:notEmpty name="result" property="docReader.annoStatus">				
			<div class="searchResultValues"><em>Annotation status:</em>
			<bean:write name="result" property="docReader.annoStatus[0]" filter="false" /><logic:iterate id="annoStatus" 
				name="result" property="docReader.annoStatus" offset="1">, 
				<bean:write name="annoStatus" filter="false" />
				</logic:iterate></div>
		</logic:notEmpty>
		<logic:notEmpty name="result" property="docReader.annoFormats">				
			<div class="searchResultValues"><em>Annotation formats:</em>
			<bean:write name="result" property="docReader.annoFormats[0]" filter="false" /><logic:iterate id="annoFormats" 
				name="result" property="docReader.annoFormats" offset="1">, 
				<bean:write name="annoFormats" filter="false" />
				</logic:iterate></div>
		</logic:notEmpty>				
		<c:if test='${not empty result.docReader.annoRatings}'>
			<div class="searchResultValues"><em>Annotation star ratings:</em>
				A total of ${result.docReader.numAnnoRatingsInt} rating${result.docReader.numAnnoRatingsInt > 1 ? 's' : ''}:
				<i>
					<c:forEach items='${result.docReader.annoRatings}' var="item" varStatus="i">
						${item} star${item > 1 ? 's' : ''}${i.last == true ? "" : ","}
					</c:forEach>
				</i>
			</div>
		</c:if>
		<c:if test='${not empty result.docReader.averageAnnoRating}'>
			<div class="searchResultValues"><em>Annotation average rating:</em>
				${result.docReader.averageAnnoRating} (float: ${result.docReader.averageAnnoRatingFloat})
			</div>
		</c:if>
	</td>
</tr>



<%-- Global attributes of all search results... --%>
<tr>
	<td colspan="2">
		<div style="background-color:#F2F0FF; margin: 0px 10px 10px 5px; border: solid 1px #999;">
			<div class="searchResultValues">
				<em>ID:</em>
					<c:choose>
						<c:when test="${param.fullview == result.docReader.id}">
							${result.docReader.id}
						</c:when>
						<c:otherwise>
							<a href='display.do?fullview=${result.docReader.id}' title='Full view for this record'>${result.docReader.id}</a>
						</c:otherwise>
					</c:choose>
			</div>
			<c:set var="myCollectionDoc" value="${result.docReader.myCollectionDoc}"/>
			<c:if test="${not empty myCollectionDoc}">
				<div class="searchResultValues">
					<em>Collection:</em> &nbsp;<a href='display.do?fullview=${myCollectionDoc.id}' title='View record for this collection'>${myCollectionDoc.shortTitle}</a>
				</div>						
			</c:if>
			
			<%-- Show the associated records - Put in catch, since not all docReaders have these methods. --%>
			<c:catch>			
				<%-- Show the available associated records --%>
				<c:if test='${not empty result.docReader.associatedIds}'>
					<div class="searchResultValues" style="padding-left:25px;"><em>Also cataloged by:</em>
						<c:forEach items="${result.docReader.associatedIds}" var="item" varStatus="i">
							<c:choose>
								<c:when test="${f:contains(result.docReader.missingAssociatedItemIds,item)}">
									${item}${i.last == true ? "" : ","}
								</c:when>
								<c:otherwise>
									<a href='display.do?fullview=${item}' title='View this record'>${item}</a>${i.last == true ? "" : ","}
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</div>
				</c:if>
				<c:if test='${not empty result.docReader.missingAssociatedItemIds}'>					
					<div class="searchResultValues" style="padding-left:25px;"><em>ID not found in index:</em>
						<c:forEach items="${result.docReader.missingAssociatedItemIds}" var="item" varStatus="i">
							${item}${i.last == true ? "" : ","}
						</c:forEach>
					</div>
				</c:if>			
				<c:if test='${fn:length(result.docReader.myCollectionDocs) > 1}'>
					<div class="searchResultValues" style="padding-left:25px;"><em>Also part of collection(s):</em> 
						<c:forEach items="${result.docReader.myCollectionDocs}" var="collDoc" varStatus="i" begin="1">
							<a href='display.do?fullview=${collDoc.id}' title='View record for this collection'>${collDoc.shortTitle}</a>${i.last ? '' : ','}
						</c:forEach>					
					</div>
				</c:if>		
			</c:catch>
			
			
			<c:if test='${not empty result.docReader.relatedRecordsMap}'>
				<div class="searchResultValues"><em>Relationships:</em>
					<c:forEach items="${result.docReader.relatedRecordsMap}" var="relatedRecordsEntry" varStatus="i">
						${relatedRecordsEntry.key}${i.last ? '' : ','}
					</c:forEach>
					<div style="padding-left:25px;">
						<c:forEach items="${result.docReader.relatedRecordsMap}" var="relatedRecordsEntry" varStatus="i">
							<div>
								<em>${relatedRecordsEntry.key}:</em>
								<c:set var="numRelations" value="${fn:length(relatedRecordsEntry.value)}"/>
								<c:forEach items="${relatedRecordsEntry.value}" var="relatedRecordsResultDoc" varStatus="j" end="${MAX_ANNOS-1}">
									<a 	href='display.do?fullview=${relatedRecordsResultDoc.docReader.id}' 
										title='View this record'>${relatedRecordsResultDoc.docReader.id}</a>${j.last ? '' : ','}</c:forEach><c:if 
										test="${numRelations > MAX_ANNOS}">, plus ${numRelations - MAX_ANNOS} more...</c:if>
								${empty relatedRecordsEntry.value ? 'No records found for this relation' : ''}
							</div>
						</c:forEach>					
					</div>
				</div>
			</c:if>
			
			<c:if test='${not empty result.docReader.assignedByIdRelatedRecordsMap}'>
				<div class="searchResultValues"><em>Assignes relationships by ID:</em>
					<c:forEach items="${result.docReader.assignedByIdRelatedRecordsMap}" var="relatedRecordsEntry" varStatus="i">
						${relatedRecordsEntry.key}${i.last ? '' : ','}
					</c:forEach>
					<div style="padding-left:25px;">
						<c:forEach items="${result.docReader.assignedByIdRelatedRecordsMap}" var="relatedRecordsEntry" varStatus="i">
							<div>
								Assigns <em>${relatedRecordsEntry.key}</em> relationship by ID (to this) for:
								<c:set var="numRelations" value="${fn:length(relatedRecordsEntry.value)}"/>
								<c:forEach items="${relatedRecordsEntry.value}" var="relatedRecordsResultDoc" varStatus="j" end="${MAX_ANNOS-1}">
									<a 	href='display.do?fullview=${relatedRecordsResultDoc.docReader.id}' 
										title='View this record'>${relatedRecordsResultDoc.docReader.id}</a>${j.last ? '' : ','}</c:forEach><c:if 
										test="${numRelations > MAX_ANNOS}">, plus ${numRelations - MAX_ANNOS} more...</c:if>
								${empty relatedRecordsEntry.value ? 'No records found for this relation' : ''}
							</div>
						</c:forEach>					
					</div>
				</div>
			</c:if>			

			<c:if test='${not empty result.docReader.assignedByUrlRelatedRecordsMap}'>
				<div class="searchResultValues"><em>Assignes relationships by URL:</em>
					<c:forEach items="${result.docReader.assignedByUrlRelatedRecordsMap}" var="relatedRecordsEntry" varStatus="i">
						${relatedRecordsEntry.key}${i.last ? '' : ','}
					</c:forEach>
					<div style="padding-left:25px;">
						<c:forEach items="${result.docReader.assignedByUrlRelatedRecordsMap}" var="relatedRecordsEntry" varStatus="i">
							<div>
								Assigns <em>${relatedRecordsEntry.key}</em> relationship by URL (to this) for:
								<c:set var="numRelations" value="${fn:length(relatedRecordsEntry.value)}"/>
								<c:forEach items="${relatedRecordsEntry.value}" var="relatedRecordsResultDoc" varStatus="j" end="${MAX_ANNOS-1}">
									<a 	href='display.do?fullview=${relatedRecordsResultDoc.docReader.id}' 
										title='View this record'>${relatedRecordsResultDoc.docReader.id}</a>${j.last ? '' : ','}</c:forEach><c:if 
										test="${numRelations > MAX_ANNOS}">, plus ${numRelations - MAX_ANNOS} more...</c:if>
								${empty relatedRecordsEntry.value ? 'No records found for this relation' : ''}
							</div>
						</c:forEach>					
					</div>
				</div>
			</c:if>				
			
			<div class="searchResultValues"><em>XML Format:</em> ${result.docReader.nativeFormat}</div>
			<div class="searchResultValues"><em>Record last indexed:</em> <bean:write name="result" property="docReader.lastModifiedString" filter="false" /></div>						
			<div class="searchResultValues"><em>Record content last modified (OAI mod time):</em> <bean:write name="result" property="docReader.oaiLastModifiedString" filter="false" /></div>							
			<div class="searchResultValues"><em>File location:</em> 
				<c:choose>
					<c:when test="${result.docReader.fileExists == 'true'}">
						<bean:write name="result" property="docReader.docsource" filter="false" />
					</c:when>
					<c:otherwise>
						No cached file exists. Record content resides in the index only.
					</c:otherwise>
				</c:choose>
			</div>
			<div class="searchResultValues">
				<c:if test="${empty param.fullview}">
					[ <a href='display.do?fullview=${result.docReader.id}' title='View this record'>Full view</a> ]
				</c:if>
				[ <a href='display.do?fileid=${result.docReader.id}' title='View record XML'>View XML</a> ]
				[ <a href='display.do?fileid=${result.docReader.id}&rt=validate' title='Validate record XML'>Validate XML</a> ]
				
				<%-- Set up relations params for DDSWS requests --%>
				<c:set var="relationRequestParms"><c:forEach items="${result.docReader.relatedRecordsMap}" var="relatedRecordsEntry" 
					varStatus="i">&amp;relation=${relatedRecordsEntry.key}</c:forEach></c:set>
				<c:set var="storedContentParms">&amp;storedContent=title&amp;storedContent=description&amp;storedContent=url</c:set>
				<c:if test="${result.docReader.isMultiRecord}">
					<c:set var="alsoCatalogedByrelationRequestParm">&amp;relation=alsoCatalogedBy</c:set>
				</c:if>
				[ <a href='${ddsws11BaseUrl}?verb=GetRecord&amp;id=${result.docReader.id}${relationRequestParms}${alsoCatalogedByrelationRequestParm}${storedContentParms}' title='GetRecord service request'>GetRecord</a> ]
				[ <a href='${ddsws11BaseUrl}?verb=Search&amp;q=idvalue:&quot;${result.docReader.id}&quot;${relationRequestParms}${alsoCatalogedByrelationRequestParm}${storedContentParms}&amp;s=0&amp;n=10' title='Search service request'>Search</a> ]
			</div>
		</div>
	</td>
</tr>
