<%--
	This JSP page constructs checkbox menus for grade ranges, subjects, 
	resource types and collections. It is meant to be included inside a
	HTML <form> element named 'searchForm' of an encompassing JSP.
	Requires web_service_connection.jsp to be included prior to using this page.
	
	Example use:
	
	<form action="index.jsp" name="searchForm">
		Enter a search: <br>
		<input size="40" type="text" name="q" value="<c:out value='${param.q}' escapeXml='true'/>">
		<input type="submit" value="Search"> <br>	
		<%@ include file="checkbox_menus.jsp" %> <br>			
	</form>		
--%>
<c:set var="rm" value="${applicationScope.repositoryManager}"/>

<%-- Indent the check box sub-menus by this many pixels --%>
<c:set var="indentAmount" value="18"/>

<%-- Use this parameter to re-generate the menus if needed --%>
<c:if test="${param.reload == 'true'}">
	<c:remove var="menusLoaded_adminSearch" scope="application"/>
</c:if>

<%-- Define JavaScript used to show/hide and make selections in the menus --%>
<script type="text/javascript">
<!--	
	function toggleVisibility( elementID ) {
		var objElement = $( elementID );
		if ( objElement != null ) {
			if ( objElement.visible() ){
				objElement.hide();
				$(elementID+"_img").src = 'images/btnExpandClsd.gif';
			}
			else {
				objElement.show();
				$(elementID+"_img").src = 'images/btnExpand.gif';
			}
		}
	}	
	function showElement( elementID ){
		var objElement = document.getElementById( elementID );
		if ( objElement != null ) {
			objElement.style.display = '';
		}	
	}

	function hideElement( elementID ){
		var objElement = document.getElementById( elementID );
		if ( objElement != null ) {
			objElement.style.display = 'none';
		}	
	}
	
	function clearGrSelections( ) {
		if(document.queryForm.gr != null){
			for(i = 0; i < document.queryForm.gr.length; i++){
				document.queryForm.gr[i].checked = false;
			}	
		}
	}
	function clearSuSelections( ) {
		if(document.queryForm.su != null){					
			for(i = 0; i < document.queryForm.su.length; i++){
				document.queryForm.su[i].checked = false;
			}	
		}
	}
	function clearReSelections( ) {
		if(document.queryForm.re != null){			
			for(i = 0; i < document.queryForm.re.length; i++){
				document.queryForm.re[i].checked = false;
			}	
		}
	}
	function clearCsSelections( ) {
		if(document.queryForm.cs != null){	
			for(i = 0; i < document.queryForm.cs.length; i++){
				document.queryForm.cs[i].checked = false;
			}		
		}
	}
	function clearKySelections( ) {
		if(document.queryForm.ky != null){	
			for(i = 0; i < document.queryForm.ky.length; i++){
				document.queryForm.ky[i].checked = false;
			}		
		}
	}	
	function clearSifmtsSelections( ) {
		if(document.queryForm.sifmts != null){
			document.queryForm.sifmts.checked = false;
			for(i = 0; i < document.queryForm.sifmts.length; i++){
				document.queryForm.sifmts[i].checked = false;
			}		
		}
	}
	function clearIndexedAccessionStatusesSelections( ) {
		if(document.queryForm.indexedAccessionStatuses != null){
			// Case where there is only one checkbox
			document.queryForm.indexedAccessionStatuses.checked = false;
			// Case where there is more than one checkbox
			for(i = 0; i < document.queryForm.indexedAccessionStatuses.length; i++){
				document.queryForm.indexedAccessionStatuses[i].checked = false;
			}		
		}
	}		
	function clearSelectedIdMapperErrorsSelections( ) {
		if(document.queryForm.selectedIdMapperErrors != null){	
			document.queryForm.selectedIdMapperErrors.checked = false;
			for(i = 0; i < document.queryForm.selectedIdMapperErrors.length; i++){
				document.queryForm.selectedIdMapperErrors[i].checked = false;
			}		
		}
	}		
	function checkSelectedItems( ) {
		<c:forEach var="paramValue" items="${paramValues.re}">
			document.getElementById( '${paramValue}_re_id' ).checked = true;	
		</c:forEach>		
		<c:forEach var="paramValue" items="${paramValues.su}">
			document.getElementById( '${paramValue}_su_id' ).checked = true;	
		</c:forEach>
		<c:forEach var="paramValue" items="${paramValues.gr}">
			document.getElementById( '${paramValue}_gr_id' ).checked = true;	
		</c:forEach>
		<c:forEach var="paramValue" items="${queryForm.ky}">
			document.getElementById( '${paramValue}_ky_id' ).checked = true;	
		</c:forEach>
		<c:forEach var="paramValue" items="${paramValues.cs}">
			document.getElementById( '${paramValue}_cs_id' ).checked = true;	
		</c:forEach>

		<c:forEach var="paramValue" items="${queryForm.sifmts}">
			document.getElementById( '${paramValue}_sifmts_id' ).checked = true;	
		</c:forEach>
		<c:forEach var="paramValue" items="${paramValues.indexedAccessionStatuses}">
			document.getElementById( '${paramValue}_indexedAccessionStatuses_id' ).checked = true;	
		</c:forEach>
		<c:forEach var="paramValue" items="${paramValues.selectedIdMapperErrors}">
			document.getElementById( '${paramValue}_selectedIdMapperErrors_id' ).checked = true;	
		</c:forEach>						
	}	
	function clearAllSelections( ) {
		clearIndexedAccessionStatusesSelections();
		clearSelectedIdMapperErrorsSelections();		
		clearSifmtsSelections();
		clearGrSelections();
		clearSuSelections();
		clearReSelections();
		clearCsSelections();
		clearKySelections();
		var objElement = document.getElementById( 'yourSelections' );
		if ( objElement != null ) {
			objElement.style.display = 'none';
		}
	}	
// -->
</script>


<%-- Set up menus that are generated via the bean values --%>
	<%-- Create the grade range menu and cache it for efficiency --%>
<c:set var="xmlFormatsCheckBoxMenu_adminSearch">
	<div>
		<nobr>
		<a 	href="javascript:toggleVisibility('XmlFormats');" 
			title="Click to show/hide" 
			class="vocabHeading"><img src='images/btnExpandClsd.gif'
			id="XmlFormats_img"
			alt="Click to show/hide" border="0" hspace="5" width="11" hight="11">XML FORMATS</a>
		</nobr>							
	</div>
	
	<div id="XmlFormats" style="display:none; width:100%;">
	<div style="margin-left:${indentAmount}px">
	<table style="width:100%;"><tr><td>
	<c:forEach var="xmlFormat" items="${queryForm.indexedFormats}" varStatus="i">
		<div><a href="javascript:void(0)" class="checkboxMenuItem"><input type="checkbox" id="${xmlFormat}_sifmts_id" name="sifmts" value="${xmlFormat}"/><label for="${xmlFormat}_sifmts_id">${fn:substringAfter(xmlFormat,"0")}</label></a></div>		
	</c:forEach>
	</td></tr></table></div></div>
</c:set>


<c:set var="indexedAccessionStatusesCheckBoxMenu_adminSearch">
	<div>
		<nobr>
		<a 	href="javascript:toggleVisibility('indexedAccessionStatusesMenu');" 
			title="Click to show/hide" 
			class="vocabHeading"><img src='images/btnExpandClsd.gif'
			id="indexedAccessionStatusesMenu_img"
			alt="Click to show/hide" border="0" hspace="5" width="11" hight="11">ACCESSION STATUS</a>
		</nobr>							
	</div>
	
	<div id="indexedAccessionStatusesMenu" style="display:none; width:100%;">
	<div style="margin-left:${indentAmount}px">
	<table style="width:100%;"><tr><td>
	<c:forEach var="value" items="${queryForm.indexedAccessionStatuses}" varStatus="i">		
		<input type="checkbox" id="${value}_indexedAccessionStatuses_id" name="indexedAccessionStatuses" 
		value="${value}"/>${value}<br/>				
	</c:forEach>
	</td></tr></table></div></div>
</c:set>


<c:set var="idMapperErrorsCheckBoxMenu_adminSearch">
	<div>
		<nobr>
		<a 	href="javascript:toggleVisibility('idMapperErrorsMenu');" 
			title="Click to show/hide" 
			class="vocabHeading"><img src='images/btnExpandClsd.gif'
			id="idMapperErrorsMenu_img"
			alt="Click to show/hide" border="0" hspace="5" width="11" hight="11">ID MAPPER ERRORS</a>
		</nobr>							
	</div>
	
	<div id="idMapperErrorsMenu" style="display:none; width:100%;">
	<div style="margin-left:${indentAmount}px">
	<table style="width:100%;"><tr><td>
	<c:forEach var="value" items="${queryForm.idMapperErrors}" varStatus="i">		
		<input type="checkbox" id="${value}_selectedIdMapperErrors_id" name="selectedIdMapperErrors" 
		value="${value}"/>${queryForm.idMapperErrorLabels[i.index]}<br/>				
	</c:forEach>
	</td></tr></table></div></div>
</c:set>

<%-- Load the menus if they have not already been loaded and cached --%>
<c:if test="${menusLoaded_adminSearch != 'true' || reload_admin_collection_menus == 'true'}">

	<%-- Create the collections menu and cache it for efficiency --%>
	<c:set var="collectionsCheckBoxMenu_adminSearch" scope="application">	
		<div>
			<nobr>
			<a 	href="javascript:toggleVisibility('Collections');" 
				title="Click to show/hide" 
				class="vocabHeading"><img src='images/btnExpandClsd.gif'
				id="Collections_img"
				alt="Click to show/hide" border="0" hspace="5" width="11" hight="11">COLLECTIONS</a>
			</nobr>							
		</div>
		
		<%-- The collections checkbox list --%>
		<div id="Collections" style="display:none; width:100%;">
		<div style="margin-left:${indentAmount}px">
		<table style="width:100%;"><tr><td>
		<c:set value="key" target="${queryForm}" property="field"  />
		 
		<c:forEach items="${queryForm.collections}" var="key">

			<c:set value="${key}" target="${queryForm}" property="value"  />
			<c:choose>
				<c:when test="${queryForm.isVocabTermAvailable}">
					<c:set var="collKey" value="${queryForm.vocabTerm.id}"/>
					<c:set var="collLabel" value="${queryForm.vocabTerm.label}"/>
				</c:when>
				<c:otherwise>
					<c:set var="collKey" value="${queryForm.vocabTerm.id}"/>
					<c:set var="collLabel" value="${rm.configuredSetInfos[collKey].name}"/>				
				</c:otherwise>
			</c:choose>
			<div><a href="javascript:void(0)" class="checkboxMenuItem"><input type="checkbox" id="${collKey}_ky_id" name="ky" value="${collKey}"/><label for="${collKey}_ky_id">${collLabel}</label></a></div>			
		</c:forEach>
		</td></tr></table></div></div>				
	</c:set>
	
	<%-- Remove the flag to re-build the collections menu... --%>
	<c:remove var="reload_admin_collection_menus" scope="application"/>
</c:if>

<%-- Load the gradeRange, Subject, ResourceTypes, Standards menus if they have not already been loaded and cached --%>
<%-- <c:if test="${ menusLoaded_adminSearch != 'true' }"> --%>

<c:if test="${ false }"><%-- Turned off -- currently not using. Use the above to turn back on... --%>

	<%-- Create the grade range menu and cache it for efficiency --%>
	<c:set var="currentIndent" value="0"/>
	<c:set var="gradeRangesCheckBoxMenu_adminSearch" scope="application">
		<div>
			<nobr>
			<a 	href="javascript:toggleVisibility('GradeRanges');" 
				title="Click to show/hide" 
				class="vocabHeading"><img src='images/btnExpandClsd.gif'
				id="GradeRanges_img"
				alt="Click to show/hide" border="0" hspace="5" width="11" hight="11">GRADE LEVELS</a>
			</nobr>							
		</div>
		
		<%-- The grade levels checkbox list --%>
		<div id="GradeRanges" style="display:none; width:100%;">
		<div style="margin-left:${indentAmount}px">
		<table style="width:100%;"><tr><td>
		<x:forEach select="$gradeRangesXmlDom_adminSearch/DDSWebService/ListGradeRanges/gradeRanges/gradeRange">
			<%-- Render the list items --%>
			<x:if select="contains( renderingGuidelines/noDisplay, 'false' )">
				<c:set var="searchKey"><x:out select="searchKey"/></c:set>		
				<c:if test="${not empty searchKey}">	
					<input type="checkbox" id="${searchKey}_gr_id" name="gr" value="${searchKey}"/><x:out select="renderingGuidelines/label"/><br/>				
				</c:if>
			</x:if>
		</x:forEach>
		</td></tr></table></div></div>
	</c:set>

	
	<%-- Create the subjects menu and cache it for efficiency --%>
	<c:set var="subjectsCheckBoxMenu_adminSearch" scope="application">			
 		<div>
			<nobr>
			<a 	href="javascript:toggleVisibility('Subjects');" 
				title="Click to show/hide" 
				class="vocabHeading"><img src='images/btnExpandClsd.gif'
				id="Subjects_img"
				alt="Click to show/hide" border="0" hspace="5" width="11" hight="11">SUBJECTS</a>
			</nobr>				
		</div> 
		
		<%-- The subjects checkbox list --%>
		<div id="Subjects" style="display:none; width:100%;">
		<div style="margin-left:${indentAmount}px">
		<table style="width:100%;"><tr valign="top"><td>
		<x:forEach select="$subjectsXmlDom_adminSearch/DDSWebService/ListSubjects/subjects/subject" varStatus="status">
			<x:if select="contains( renderingGuidelines/noDisplay, 'false' )">
				<%-- Start a new colum, if appropriate --%>
				<c:if test="${status.count > 20 && empty newCol && currentIndent == 0}">
					</td><td>
					<c:set var="newCol" value="true"/>
				</c:if>
				<x:choose>
					<%-- Render the sub-list headings --%>
					<x:when select="contains( renderingGuidelines/hasSubList, 'true' )">					
						<div>
							<a 	href="javascript:toggleVisibility('<x:out select="vocabEntry"/>');" 
								title="Click to show/hide" 
								class="vocabHeading"><img src='images/btnExpandClsd.gif'
								id='<x:out select="vocabEntry"/>_img'
								alt="Click to show/hide" border="0" hspace="5" width="11" hight="11"><x:out select="renderingGuidelines/label"/></a>
						</div>						
						<c:set var="currentIndent" value="${currentIndent + indentAmount}"/>
						<div id='<x:out select="vocabEntry"/>' style="display:none; width:100%; margin-left:${currentIndent}">
					</x:when>				
					<%-- Render the list items --%>
					<x:otherwise>
						<c:set var="searchKey"><x:out select="searchKey"/></c:set>
						<c:if test="${not empty searchKey}">	
							<div style="margin-left:${currentIndent}" nowrap>
								<input type="checkbox" id="${searchKey}_su_id" name="su" value="${searchKey}"/><x:out select="renderingGuidelines/label"/>
							</div>				
						</c:if>
					</x:otherwise>						
				</x:choose>
				<%-- End of indenting, etc. --%>
				<x:if select="contains( renderingGuidelines/isLastInSubList, 'true' )">
					<c:set var="currentIndent" value="${currentIndent - indentAmount}"/>
					</div>
				</x:if>				
			</x:if>
		</x:forEach>
		</td></tr></table></div></div>
	</c:set>


	<%-- Create the resource type menu and cache it for efficiency --%>
	<c:set var="resourceTypesCheckBoxMenu_adminSearch" scope="application">	
 		<div>
			<nobr>
			<a 	href="javascript:toggleVisibility('ResourceTypes');" 
				title="Click to show/hide" 
				class="vocabHeading"><img src='images/btnExpandClsd.gif'
				id='ResourceTypes_img'
				alt="Click to show/hide" border="0" hspace="5" width="11" hight="11">RESOURCE TYPES</a>
			</nobr>
		</div> 
		
		<%-- The resource types checkbox list --%>		
		<div id="ResourceTypes" style="display:none; width:100%;">
		<div style="margin-left:${indentAmount}px">
		<table style="width:100%;"><tr valign="top"><td>
		<c:set var="subListItemsCount" value="0"/>
		<c:set var="subListCount" value="0"/>
		<x:forEach select="$resourceTypesXmlDom_adminSearch/DDSWebService/ListResourceTypes/resourceTypes/resourceType" varStatus="status">
			<x:if select="contains( renderingGuidelines/noDisplay, 'false' )">
				<x:choose>
					<%-- Render the sub-list headings --%>
					<x:when select="contains( renderingGuidelines/hasSubList, 'true' )">					
						<div>
							<a 	href="javascript:toggleVisibility('<x:out select="vocabEntry"/>');" 
								title="Click to show/hide" 
								class="vocabHeading"><img src='images/btnExpandClsd.gif'
								id='<x:out select="vocabEntry"/>_img'
								alt="Click to show/hide" border="0" hspace="5" width="11" hight="11"><x:out select="renderingGuidelines/label"/></a>
						</div>						
						<c:set var="currentIndent" value="${currentIndent + indentAmount}"/>
						<c:set var="subListCount" value="${subListCount + 1}"/>
						<c:choose>						
							<c:when test="${subListCount == 1 && status.count == 1}">
								<c:set var="listDisplay" value=""/>
							</c:when>
							<c:otherwise>
								<c:set var="listDisplay" value="display:none;"/>
							</c:otherwise>
						</c:choose>
						<div id='<x:out select="vocabEntry"/>' style="${listDisplay} width:100%; margin-left:${currentIndent}">
						<table border="0" cellpadding="4" cellspacing="0"><tr valign="top">
					</x:when>				
					<%-- Render the list items --%>
					<x:otherwise>
						<c:set var="searchKey"><x:out select="searchKey"/></c:set>
						<c:if test="${not empty searchKey}">	
							<c:if test="${subListItemsCount%10 == 0}"><td nowrap></c:if>
							<div>
							<input type="checkbox" id="${searchKey}_re_id" name="re" value="${searchKey}"/><x:out select="renderingGuidelines/label"/>
							</div>
							<c:set var="subListItemsCount" value="${subListItemsCount + 1}"/>
							<c:if test="${subListItemsCount%10 == 0}"></td></c:if>
						</c:if>
					</x:otherwise>						
				</x:choose>
				<%-- End of indenting, etc. --%>
				<x:if select="contains( renderingGuidelines/isLastInSubList, 'true' )">
					<c:set var="currentIndent" value="${currentIndent - indentAmount}"/>
					<c:set var="subListItemsCount" value="0"/>
					</tr></table></div>
				</x:if>				
			</x:if>
		</x:forEach>
		</td></tr></table></div></div>
	</c:set>

	
	<%-- Create the content standards menu and cache it for efficiency --%>
	<c:set var="contentStandardsCheckBoxMenu_adminSearch" scope="application">	
 		<div>
			<nobr>
			<a 	href="javascript:toggleVisibility('ContentStandards');" 
				title="Click to show/hide" 
				class="vocabHeading"><img src='images/btnExpandClsd.gif'
				id='ContentStandards_img'
				alt="Click to show/hide" border="0" hspace="5" width="11" hight="11">CONTENT STANDARDS</a>
			</nobr>
		</div> 		

		<%-- last level - to keep track of the level of the last item displayed --%>
		<c:set var="lastLevel" value="1"/>		
		<c:set var="groupLevel" value="0"/>
				
		<%-- iterationArray is a string array of dummy values that serves as an iterator value. The contents of the array
			are never evaluated, they simply provide the JSP something to iterate over --%>
		<c:set var="iterationArray" value='${fn:split ("1 1 1 1 1 1 1 1 1 ", " ")}' />
		
		<div id="ContentStandards" style="display:none; width:100%;  margin-left:${indentAmount}px;">
		<table style="width:100%;"><tr valign="top"><td>					
		<x:forEach select="$contentStandardsXmlDom_adminSearch/DDSWebService/ListContentStandards/contentStandards/contentStandard" varStatus="status">			
			<x:if select="contains( renderingGuidelines/noDisplay, 'false' )">	
				<%-- Store some variables for convenience --%>
				<c:set var="searchKey"><x:out select="searchKey"/></c:set>
				<c:set var="hasSubList"><x:out select="contains(renderingGuidelines/hasSubList,'true')"/></c:set>
				<c:set var="isLastInSubList"><x:out select="contains(renderingGuidelines/isLastInSubList,'true')"/></c:set>
				<c:set var="wrap"><x:out select="contains(renderingGuidelines/wrap,'true')"/></c:set>
				<c:set var="groupLevel"><x:out select="renderingGuidelines/groupLevel"/></c:set>												

				<c:choose>
					<c:when test="${ !hasSubList }">
						<input type="checkbox" id="${searchKey}_cs_id" name="cs" value="${searchKey}"/><x:out select="renderingGuidelines/label"/><br/>
						<c:if test="${ wrap }">
							</td><td valign=top nowrap>
						</c:if>
						<c:if test="${ isLastInSubList }">
							</td></table></div>
						</c:if>							
					</c:when>
					<c:when test="${ hasSubList }">
						<%-- close open table elements until the groupLevel is reached --%>
					   <c:forEach var="item" items="${iterationArray}" begin="${groupLevel + 1}" end="${lastLevel}">
							</td></table></div>
					   </c:forEach>						
						<div  style="margin:0px; padding:0px;">
						<c:choose>						
							<c:when test="${groupLevel == 2}">
								<c:set var="listDisplay" value="display:none;"/>
								<a 	href="javascript:toggleVisibility('<x:out select="vocabEntry"/>_${status.index}');" 
									title="Click to show/hide" 
									class="vocabHeading"><img src='images/btnExpandClsd.gif'
									id='<x:out select="vocabEntry"/>_${status.index}_img'
									alt="Click to show/hide" border="0" hspace="5" width="11" hight="11"><x:out select="renderingGuidelines/label"/></a>	
							</c:when>
							<c:otherwise>
								<c:set var="listDisplay" value=""/>
								<x:out select="renderingGuidelines/label"/>
							</c:otherwise>
						</c:choose>							
						</div>
						<div style="margin-left:${indentAmount}px;">
						<table style="${listDisplay}width:100%;" id="<x:out select="vocabEntry"/>_${status.index}"><tr><td>
						<c:set var="lastLevel" value="${groupLevel}"/>												
					</c:when>							
				</c:choose>
			</x:if>		
		</x:forEach>
		<%-- close up any open table elements --%>
		 <c:forEach var="item" items="${iterationArray}" begin="1" end="${lastLevel}" >
			</tr></td></table></div>
		</c:forEach>
	</c:set>


	<%-- Flag that the menus are loaded and cached --%>
	<c:set var="menusLoaded_adminSearch" value="true" scope="application"/>
</c:if>
