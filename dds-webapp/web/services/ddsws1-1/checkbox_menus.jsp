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

<%-- Indent the check box sub-menus by this many pixels --%>
<c:set var="indentAmount" value="18"/>

<%-- Use this parameter to re-generate the menus if needed --%>
<c:if test="${param.reload == 'true'}">
	<c:remove var="menusLoaded_DDSExpv11" scope="application"/>
</c:if>

<%-- Define JavaScript used to show/hide and make selections in the menus --%>
<script type="text/javascript">
<!--	
	function toggleVisibility( elementID ) {
		var objElement = document.getElementById( elementID );
		if ( objElement != null ) {
			if ( objElement.style.display == '' )
				objElement.style.display = 'none';
			else
				objElement.style.display = '';
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
		if(document.searchForm.gr != null){
			for(i = 0; i < document.searchForm.gr.length; i++){
				document.searchForm.gr[i].checked = false;
			}	
		}
	}
	function clearSuSelections( ) {
		if(document.searchForm.su != null){					
			for(i = 0; i < document.searchForm.su.length; i++){
				document.searchForm.su[i].checked = false;
			}	
		}
	}
	function clearReSelections( ) {
		if(document.searchForm.re != null){			
			for(i = 0; i < document.searchForm.re.length; i++){
				document.searchForm.re[i].checked = false;
			}	
		}
	}
	function clearCsSelections( ) {
		if(document.searchForm.cs != null){	
			for(i = 0; i < document.searchForm.cs.length; i++){
				document.searchForm.cs[i].checked = false;
			}		
		}
	}
	function clearKySelections( ) {
		if(document.searchForm.ky != null){	
			for(i = 0; i < document.searchForm.ky.length; i++){
				document.searchForm.ky[i].checked = false;
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
		<c:forEach var="paramValue" items="${paramValues.ky}">
			document.getElementById( '${paramValue}_ky_id' ).checked = true;	
		</c:forEach>
		<c:forEach var="paramValue" items="${paramValues.cs}">
			document.getElementById( '${paramValue}_cs_id' ).checked = true;	
		</c:forEach>			
	}	
	function clearAllSelections( ) {
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

<%-- Load the menus if they have not already been loaded and cached --%>
<c:if test="${ menusLoaded_DDSExpv11 != 'true' }">

	<%-- Create the grade range menu and cache it for efficiency --%>
	<%-- <c:set var="currentIndent" value="0"/>
	<c:set var="gradeRangesCheckBoxMenu_DDSExpv11" scope="application">
		<div>
			<a 	href="javascript:toggleVisibility('GradeRanges');" 
				title="Click to show/hide" 
				class="vocabHeading"><img src='images/btnExpand.gif' 
				alt="Click to show/hide" border="0" hspace="5" width="11" hight="11">GRADE LEVELS</a>							
		</div>
		
		<div id="GradeRanges" style="display:none; width:100%;">
		<div style="margin-left:${indentAmount}px">
		<table style="width:100%;"><tr><td>
		<x:forEach select="$gradeRangesXmlDom_DDSExpv11/DDSWebService/ListGradeRanges/gradeRanges/gradeRange">
			<x:if select="contains( renderingGuidelines/noDisplay, 'false' )">
				<c:set var="searchKey"><x:out select="searchKey"/></c:set>		
				<c:if test="${not empty searchKey}">	
					<input type="checkbox" id="${searchKey}_gr_id" name="gr" value="${searchKey}"/><x:out select="renderingGuidelines/label"/><br/>				
				</c:if>
			</x:if>
		</x:forEach>
		</td></tr></table></div></div>
	</c:set> --%>

	
	<%-- Create the subjects menu and cache it for efficiency --%>
	<%-- <c:set var="subjectsCheckBoxMenu_DDSExpv11" scope="application">			
 		<div>
			<a 	href="javascript:toggleVisibility('Subjects');" 
				title="Click to show/hide" 
				class="vocabHeading"><img src='images/btnExpand.gif' 
				alt="Click to show/hide" border="0" hspace="5" width="11" hight="11">SUBJECTS</a>				
		</div> 
		
		<div id="Subjects" style="display:none; width:100%;">
		<div style="margin-left:${indentAmount}px">
		<table style="width:100%;"><tr valign="top"><td>
		<x:forEach select="$subjectsXmlDom_DDSExpv11/DDSWebService/ListSubjects/subjects/subject" varStatus="status">
			<x:if select="contains( renderingGuidelines/noDisplay, 'false' )">
				<c:if test="${status.count > 20 && empty newCol && currentIndent == 0}">
					</td><td>
					<c:set var="newCol" value="true"/>
				</c:if>
				<x:choose>

					<x:when select="contains( renderingGuidelines/hasSubList, 'true' )">					
						<div>
							<a 	href="javascript:toggleVisibility('<x:out select="vocabEntry"/>');" 
								title="Click to show/hide" 
								class="vocabHeading"><img src='images/btnExpand.gif' 
								alt="Click to show/hide" border="0" hspace="5" width="11" hight="11"><x:out select="renderingGuidelines/label"/></a>
						</div>						
						<c:set var="currentIndent" value="${currentIndent + indentAmount}"/>
						<div id='<x:out select="vocabEntry"/>' style="display:none; width:100%; margin-left:${currentIndent}">
					</x:when>				
					<x:otherwise>
						<c:set var="searchKey"><x:out select="searchKey"/></c:set>
						<c:if test="${not empty searchKey}">	
							<div style="margin-left:${currentIndent}">
								<input type="checkbox" id="${searchKey}_su_id" name="su" value="${searchKey}"/><x:out select="renderingGuidelines/label"/>
							</div>				
						</c:if>
					</x:otherwise>						
				</x:choose>
				<x:if select="contains( renderingGuidelines/isLastInSubList, 'true' )">
					<c:set var="currentIndent" value="${currentIndent - indentAmount}"/>
					</div>
				</x:if>				
			</x:if>
		</x:forEach>
		</td></tr></table></div></div>
	</c:set> --%>


	<%-- Create the resource type menu and cache it for efficiency --%>
	<%-- <c:set var="resourceTypesCheckBoxMenu_DDSExpv11" scope="application">	
 		<div>
			<a 	href="javascript:toggleVisibility('ResourceTypes');" 
				title="Click to show/hide" 
				class="vocabHeading"><img src='images/btnExpand.gif' 
				alt="Click to show/hide" border="0" hspace="5" width="11" hight="11">RESOURCE TYPES</a>
		</div> 
		
		<div id="ResourceTypes" style="display:none; width:100%;">
		<div style="margin-left:${indentAmount}px">
		<table style="width:100%;"><tr valign="top"><td>
		<c:set var="subListItemsCount" value="0"/>
		<c:set var="subListCount" value="0"/>
		<x:forEach select="$resourceTypesXmlDom_DDSExpv11/DDSWebService/ListResourceTypes/resourceTypes/resourceType" varStatus="status">
			<x:if select="contains( renderingGuidelines/noDisplay, 'false' )">
				<x:choose>
					<x:when select="contains( renderingGuidelines/hasSubList, 'true' )">					
						<div>
							<a 	href="javascript:toggleVisibility('<x:out select="vocabEntry"/>');" 
								title="Click to show/hide" 
								class="vocabHeading"><img src='images/btnExpand.gif' 
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
					<x:otherwise>
						<c:set var="searchKey"><x:out select="searchKey"/></c:set>
						<c:if test="${not empty searchKey}">	
							<c:if test="${subListItemsCount%6 == 0}"><td nowrap></c:if>
							<div>
							<input type="checkbox" id="${searchKey}_re_id" name="re" value="${searchKey}"/><x:out select="renderingGuidelines/label"/>
							</div>
							<c:set var="subListItemsCount" value="${subListItemsCount + 1}"/>
							<c:if test="${subListItemsCount%6 == 0}"></td></c:if>
						</c:if>
					</x:otherwise>						
				</x:choose>
				<x:if select="contains( renderingGuidelines/isLastInSubList, 'true' )">
					<c:set var="currentIndent" value="${currentIndent - indentAmount}"/>
					<c:set var="subListItemsCount" value="0"/>
					</tr></table></div>
				</x:if>				
			</x:if>
		</x:forEach>
		</td></tr></table></div></div>
	</c:set> --%>

	
	<%-- Create the content standards menu and cache it for efficiency --%>
<%-- 	<c:set var="contentStandardsCheckBoxMenuDDSExp" scope="application">	
 		<div>
			<a 	href="javascript:toggleVisibility('ContentStandards');" 
				title="Click to show/hide" 
				class="vocabHeading"><img src='images/btnExpand.gif' 
				alt="Click to show/hide" border="0" hspace="5" width="11" hight="11">CONTENT STANDARDS</a>
		</div> 		

		<c:set var="lastLevel" value="1"/>		
		<c:set var="groupLevel" value="0"/>
				
		<c:set var="iterationArray" value='${fn:split ("1 1 1 1 1 1 1 1 1 ", " ")}' />
		
		<div id="ContentStandards" style="display:none; width:100%;  margin-left:${indentAmount}px;">
		<table style="width:100%;"><tr valign="top"><td>					
		<x:forEach select="$contentStandardsXmlDom_DDSExpv11/DDSWebService/ListContentStandards/contentStandards/contentStandard" varStatus="status">			
			<x:if select="contains( renderingGuidelines/noDisplay, 'false' )">	

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

					   <c:forEach var="item" items="${iterationArray}" begin="${groupLevel + 1}" end="${lastLevel}">
							</td></table></div>
					   </c:forEach>						
						<div  style="margin:0px; padding:0px;">
						<c:choose>						
							<c:when test="${groupLevel == 2}">
								<c:set var="listDisplay" value="display:none;"/>
								<a 	href="javascript:toggleVisibility('<x:out select="vocabEntry"/>_${status.index}');" 
									title="Click to show/hide" 
									class="vocabHeading"><img src='images/btnExpand.gif' 
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
		 <c:forEach var="item" items="${iterationArray}" begin="1" end="${lastLevel}" >
			</tr></td></table></div>
		</c:forEach>
	</c:set> --%>

	<%-- Flag that the menus are loaded and cached --%>
	<c:set var="menusLoaded_DDSExpv11" value="true" scope="application"/>
</c:if>


	<%-- Create the collections menu and cache it for efficiency --%>
	<c:set var="collectionsCheckBoxMenu_DDSExpv11" scope="application">	
		<div>
			<a 	href="javascript:toggleVisibility('Collections');" 
				title="Click to show/hide" 
				class="vocabHeading"><img src='images/btnExpand.gif' 
				alt="Click to show/hide" border="0" hspace="5" width="11" hight="11">COLLECTIONS</a>							
		</div>
		
		<%-- The collections checkbox list --%>
		<div id="Collections" style="display:none; width:100%;">
		<div style="margin-left:${indentAmount}px">
		<table style="width:100%;"><tr><td>
		<x:forEach select="$collectionsXmlDom_DDSExpv11/DDSWebService/ListCollections/collections/collection">
			<%-- Render the list items --%>
			<c:set var="searchKey"><x:out select="searchKey"/></c:set>		
			<c:if test="${not empty searchKey}">	
				<input type="checkbox" id="${searchKey}_ky_id" name="ky" value="${searchKey}"/>
				<label for="${searchKey}_ky_id" class="vocabListLabels"><x:out select="renderingGuidelines/label"/></label><br/>				
			</c:if>
		</x:forEach>
		</td></tr></table></div></div>				
	</c:set>

