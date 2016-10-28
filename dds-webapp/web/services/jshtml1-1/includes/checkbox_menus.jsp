<%--
	Taken from JSP client version 2.5.		
--%>

<%-- Indent the check box sub-menus by this many pixels --%>
<c:set var="indentAmount" value="18"/>

<%-- Define JavaScript used to show/hide and make selections in the menus --%>
<script type="text/javascript">
<!--	
	if(document.images) {
		arrowUp = new Image;
		arrowDown = new Image;
		arrowUp.src = "${urlToMyDir}/images/btnExpandClsd.gif";
		arrowDown.src = "${urlToMyDir}/images/btnExpand.gif";
	} else {
		arrowUp = "";
		arrowDown = "";
	}

	function toggleVisibility( elementID ) {
		var objElement = document.getElementById( elementID );
		var arrow = document[ elementID + '_arrow' ];
		var oc = document.getElementById( elementID + '_oc' );
		if ( objElement != null ) {
			if ( objElement.style.display == '' ) {
				objElement.style.display = 'none';
				if(arrow)
					arrow.src = arrowUp.src;
				setText(oc,"show");
			}
			else {
				objElement.style.display = '';
				if(arrow)
					arrow.src = arrowDown.src;
				setText(oc,"hide");
			}
		}
	}

	function setText( elm, txt ) {
		if (elm)
		{
			if (elm.childNodes[0])
				elm.childNodes[0].nodeValue=txt;
			else if (elm.value)
				elm.value=txt;
			else if (elm.innerHTML)
				elm.innerHTML=txt;
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
	function clearSmartLinkMenuSelections( ) {
		for(j = 0; j <= 20; j++){
			var checkBoxes = document.searchForm["slm" + j];
			if(checkBoxes != null){	
				for(i = 0; i < checkBoxes.length; i++)
					checkBoxes[i].checked = false;
				checkBoxes.checked = false;
			}
		}		
	}
	function checkSelectedItems( ) {
		var queryString = window.location.search.substring(1);
		if(queryString != null && queryString.length > 0){
			var parms = queryString.split('&');
			for (var i=0; i<parms.length; i++) {
			   var pos = parms[i].indexOf('=');
			   if (pos > 0) {
				  var key = parms[i].substring(0,pos);
				  var val = parms[i].substring(pos+1);
				  var id = val + "_" + key + "_id";
				  var element = document.getElementById( id );
				  if(element != null)
				     element.checked = true;
			   }
			}
		}			
	}	
	function clearAllSelections( ) {
		// Each of these menus can be cleared by themselves too
		clearGrSelections();
		clearSuSelections();
		clearReSelections();
		clearCsSelections();
		clearKySelections();
		clearSmartLinkMenuSelections();
		var objElement = document.getElementById( 'yourSelections' );
		if ( objElement != null ) {
			objElement.style.display = 'none';
		}
		if( document.searchForm.q != null ){
			document.searchForm.q.focus();
		}
	}	
// -->
</script>

<%-- The below defines the unique variable names for the menus in this search page.
		If we change the way the menus are rendered in this client it will not effect other
		clients running in the same context, provided they each have a unique client name. --%>
<c:set var="gradeRanges" value="grBoxesMenu${clientName}"/>
<c:set var="subjects" value="suBoxesMenu${clientName}"/>
<c:set var="resourceTypes" value="reBoxesMenu${clientName}"/>
<c:set var="contentStandards" value="csBoxesMenu${clientName}"/>	
<c:set var="collections" value="kyBoxesMenu${clientName}"/>
<c:set var="smartMenus" value="smartMenus${clientName}"/>

<%-- Make the custom menus --%>
<c:set var="customMenus">
	<c:forEach items="${jsformv11.menuNamesInOrder}" var="menuName" varStatus="i">	
			<c:set var="menu" value="${jsformv11.menus[menuName]}"/>
			<c:set var="menuId">${jsformv11.menuPositions[menuName]}</c:set>	
			<div>
				<a 	href="javascript:toggleVisibility('slm_${menuId}');" 
					title="Click to show/hide" 
					class="cb"><img src='${urlToMyDir}/images/btnExpandClsd.gif' 
					alt="Click to show/hide" border="0" hspace="5" width="11" hight="11" name='slm_${menuId}_arrow'>${fn:toUpperCase(menuName)}</a>							
			</div>						
			<c:set var="myParamName" value="slm${menuId}"/>
			<div id='slm_${menuId}' style="display:none; width:100%;">
				<div class="cbItems" style="margin-left:${indentAmount}px">
					<c:forEach items="${menu}" var="menuItem" varStatus="j">
						<c:set var="itemId" value="${j.index}_slm${menuId}_id"/>
						<input type="checkbox" id="${itemId}" name="${myParamName}" value="${j.index}"/>
						<label for="${itemId}">${menuItem}</label><br/>
					</c:forEach>
				</div>
			</div>				
	</c:forEach>
</c:set>



<%-- Load the menus if they have not already been loaded and cached --%>
<c:if test="${empty applicationScope.checkBoxMenus[gradeRanges] || param.reload == 'true'}">

	<%-- Create a HashMap to cache the check box menus, keyed by their unique name --%>
	<%
		if(getServletContext().getAttribute("checkBoxMenus") == null ) 
			getServletContext().setAttribute("checkBoxMenus",new java.util.HashMap());
	%>

	<%-- Generate the HTML for the smart link menus defined in smart_link_definitions.xml --%>
	<c:set target="${applicationScope.checkBoxMenus}" property="${smartMenus}" value=""/>
	<c:if test="${mySmartMenuDom != null}">
		<c:set target="${applicationScope.checkBoxMenus}" property="${smartMenus}">
			<x:forEach select="$mySmartMenuDom/root/menu[@menuLabel]" var="menu">
			
				<div>
					<a 	href="javascript:toggleVisibility('slm_<x:out select="$menu/@id"/>');" 
						title="Click to show/hide" 
						class="cb"><img src='${urlToMyDir}/images/btnExpandClsd.gif' 
						alt="Click to show/hide" border="0" hspace="5" width="11" hight="11" name='slm_<x:out select="$menu/@id"/>_arrow'><x:out select="$menu/@menuLabel"/></a>							
				</div>	
				<c:set var="id"><x:out select="$menu/@id"/></c:set>
				<c:set var="myParamName" value="slm${id}"/>
				<div id='slm_<x:out select="$menu/@id"/>' style="display:none; width:100%;">
					<div class="cbItems" style="margin-left:${indentAmount}px">
						<x:forEach select="$menu/menuItem" var="item" varStatus="i">
							<c:set var="myParamValue"><x:out select="$item/@id"/></c:set>
							<c:set var="itemId" value="${myParamValue}_slm${id}_id"/>
							<input type="checkbox" id="${itemId}" name="${myParamName}" value="${myParamValue}"/>
							<label for="${itemId}"><x:out select="$item/label"/></label><br/>
							<c:set var="isChecked" value=""/>
						</x:forEach>
					</div>
				</div>
				
			</x:forEach>		
		</c:set>
	</c:if>	
	
	
	<%-- Create the grade range menu and cache it for efficiency --%>
	<c:set var="currentIndent" value="0"/>
	<c:set target="${applicationScope.checkBoxMenus}" property="${gradeRanges}"> 
		<div>
			<a 	href="javascript:toggleVisibility('GradeRanges');" 
				title="Click to show/hide" 
				class="cb"><img src='${urlToMyDir}/images/btnExpandClsd.gif' 
				alt="Click to show/hide" border="0" hspace="5" width="11" hight="11" name='GradeRanges_arrow'>GRADE LEVELS</a>							
		</div>
		
		<%-- The grade levels checkbox list --%>
		<div id="GradeRanges" style="display:none; width:100%;">
		<div style="margin-left:${indentAmount}px">
		<table style="width:100%;"><tr><td>
		<x:forEach select="$gradeRangesXmlDom/DDSWebService/ListGradeRanges/gradeRanges/gradeRange">
			<%-- Render the list items --%>
			<x:if select="contains( renderingGuidelines/noDisplay, 'false' )">
				<c:set var="searchKey"><x:out select="searchKey"/></c:set>		
				<c:if test="${not empty searchKey}">
					<div class="cbItems">					
						<c:set var="itemId" value="${searchKey}_gr_id"/>
						<input type="checkbox" id="${itemId}" name="gr" value="${searchKey}"/>
						<label for="${itemId}"><x:out select="renderingGuidelines/label"/></label><br/>					
					</div>
				</c:if>
			</x:if>
		</x:forEach>
		</td></tr></table></div></div>
	</c:set>
	
	<%-- Create the subjects menu and cache it for efficiency --%>
	<c:set target="${applicationScope.checkBoxMenus}" property="${subjects}"> 
 		<div>
			<a 	href="javascript:toggleVisibility('Subjects');" 
				title="Click to show/hide" 
				class="cb"><img src='${urlToMyDir}/images/btnExpandClsd.gif' 
				alt="Click to show/hide" border="0" hspace="5" width="11" hight="11" name='Subjects_arrow'>SUBJECTS</a>				
		</div> 
		
		<%-- The subjects checkbox list --%>
		<div id="Subjects" style="display:none; width:100%;">
		<div style="margin-left:${indentAmount}px">
		<table style="width:100%;" align="left"><tr valign="top" align="left"><td align="left" nowrap>
		<x:forEach select="$subjectsXmlDom/DDSWebService/ListSubjects/subjects/subject" varStatus="status">
			<x:if select="contains( renderingGuidelines/noDisplay, 'false' )">
				<%-- Start a new colum, if appropriate --%>
				<c:if test="${status.count > 20 && empty newCol && currentIndent == 0}">
					</td><td align="left" nowrap>
					<c:set var="newCol" value="true"/>
				</c:if>
				<x:choose>
					<%-- Render the sub-list headings --%>
					<x:when select="contains( renderingGuidelines/hasSubList, 'true' )">					
						<div>
							<a 	href="javascript:toggleVisibility('<x:out select="vocabEntry"/>');" 
								title="Click to show/hide" 
								class="cb"><img src='${urlToMyDir}/images/btnExpandClsd.gif' 
								alt="Click to show/hide" border="0" hspace="5" width="11" hight="11" name='<x:out select="vocabEntry"/>_arrow'><x:out select="renderingGuidelines/label"/></a>
						</div>						
						<c:set var="currentIndent" value="${currentIndent + indentAmount}"/>
						<div id='<x:out select="vocabEntry"/>' style="display:none; width:100%; margin-left:${currentIndent}">
					</x:when>				
					<%-- Render the list items --%>
					<x:otherwise>
						<c:set var="searchKey"><x:out select="searchKey"/></c:set>
						<c:if test="${not empty searchKey}">	
							<div class="cbItems" style="margin-left:${currentIndent}">							
								<c:set var="itemId" value="${searchKey}_su_id"/>
								<input type="checkbox" id="${itemId}" name="su" value="${searchKey}"/>
								<label for="${itemId}"><x:out select="renderingGuidelines/label"/></label><br/>								
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
	<c:set target="${applicationScope.checkBoxMenus}" property="${resourceTypes}"> 
 		<div>
			<a 	href="javascript:toggleVisibility('ResourceTypes');" 
				title="Click to show/hide" 
				class="cb"><img src='${urlToMyDir}/images/btnExpandClsd.gif' 
				alt="Click to show/hide" border="0" hspace="5" width="11" hight="11" name='ResourceTypes_arrow'>RESOURCE TYPES</a>
		</div> 
		
		<%-- The resource types checkbox list --%>		
		<div id="ResourceTypes" style="display:none; width:100%;">
		<div style="margin-left:${indentAmount}px">
		<table style="width:100%;"><tr valign="top"><td>
		<c:set var="subListItemsCount" value="0"/>
		<c:set var="subListCount" value="0"/>
		<x:forEach select="$resourceTypesXmlDom/DDSWebService/ListResourceTypes/resourceTypes/resourceType" varStatus="status">
			<x:if select="contains( renderingGuidelines/noDisplay, 'false' )">
				<x:choose>
					<%-- Render the sub-list headings --%>
					<x:when select="contains( renderingGuidelines/hasSubList, 'true' )">										
						<c:set var="currentIndent" value="${currentIndent + indentAmount}"/>
						<c:set var="subListCount" value="${subListCount + 1}"/>
						<c:choose>						
							<c:when test="${subListCount == 1 && status.count == 1}">
								<c:set var="listDisplay" value=""/>
								<c:set var="reArrow" value="${urlToMyDir}/images/btnExpand.gif"/>
							</c:when>
							<c:otherwise>
								<c:set var="listDisplay" value="display:none;"/>
								<c:set var="reArrow" value="${urlToMyDir}/images/btnExpandClsd.gif"/>
							</c:otherwise>
						</c:choose>
						<div>
							<a 	href="javascript:toggleVisibility('<x:out select="vocabEntry"/>');" 
								title="Click to show/hide" 
								class="cb"><img src='${reArrow}' 
								alt="Click to show/hide" border="0" hspace="5" width="11" hight="11" name='<x:out select="vocabEntry"/>_arrow'><x:out select="renderingGuidelines/label"/></a>
						</div>							
						<div id='<x:out select="vocabEntry"/>' style="${listDisplay} width:100%; margin-left:${currentIndent}">
						<table border="0" cellpadding="4" cellspacing="0"><tr valign="top">
					</x:when>				
					<%-- Render the list items --%>
					<x:otherwise>
						<c:set var="searchKey"><x:out select="searchKey"/></c:set>
						<c:if test="${not empty searchKey}">	
							<c:if test="${subListItemsCount%6 == 0}"><td nowrap></c:if>
							<div class="cbItems">
								<c:set var="itemId" value="${searchKey}_re_id"/>
								<input type="checkbox" id="${itemId}" name="re" value="${searchKey}"/>
								<label for="${itemId}"><x:out select="renderingGuidelines/label"/></label><br/>								
							</div>
							<c:set var="subListItemsCount" value="${subListItemsCount + 1}"/>
							<c:if test="${subListItemsCount%6 == 0}"></td></c:if>
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
	<c:set target="${applicationScope.checkBoxMenus}" property="${contentStandards}"> 
 		<div>
			<a 	href="javascript:toggleVisibility('ContentStandards');" 
				title="Click to show/hide" 
				class="cb"><img src='${urlToMyDir}/images/btnExpandClsd.gif' 
				alt="Click to show/hide" border="0" hspace="5" width="11" hight="11" name='ContentStandards_arrow'>CONTENT STANDARDS</a>
		</div> 		

		<%-- last level - to keep track of the level of the last item displayed --%>
		<c:set var="lastLevel" value="1"/>		
		<c:set var="groupLevel" value="0"/>
				
		<%-- iterationArray is a string array of dummy values that serves as an iterator value. The contents of the array
			are never evaluated, they simply provide the JSP something to iterate over --%>
		<c:set var="iterationArray" value='${fn:split ("1 1 1 1 1 1 1 1 1 ", " ")}' />
		
		<div id="ContentStandards" style="display:none; width:100%;  margin-left:${indentAmount}px;">
		<table style="width:100%;"><tr valign="top"><td>					
		<x:forEach select="$contentStandardsXmlDom/DDSWebService/ListContentStandards/contentStandards/contentStandard" varStatus="status">			
			<x:if select="contains( renderingGuidelines/noDisplay, 'false' )">	
				<%-- Store some variables for convenience --%>
				<c:set var="searchKey"><x:out select="searchKey"/></c:set>
				<c:set var="hasSubList"><x:out select="contains(renderingGuidelines/hasSubList,'true')"/></c:set>
				<c:set var="isLastInSubList"><x:out select="contains(renderingGuidelines/isLastInSubList,'true')"/></c:set>
				<c:set var="wrap"><x:out select="contains(renderingGuidelines/wrap,'true')"/></c:set>
				<c:set var="groupLevel"><x:out select="renderingGuidelines/groupLevel"/></c:set>												

				<c:choose>
					<c:when test="${ !hasSubList }">
						<div class="cbItems">
							<c:set var="itemId" value="${searchKey}_cs_id"/>
							<input type="checkbox" id="${itemId}" name="cs" value="${searchKey}"/>
							<label for="${itemId}"><x:out select="renderingGuidelines/label"/></label><br/>							
						</div>
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
									class="cb"><img src='${urlToMyDir}/images/btnExpandClsd.gif' 
									alt="Click to show/hide" border="0" hspace="5" width="11" hight="11" name='<x:out select="vocabEntry"/>_${status.index}_arrow'><x:out select="renderingGuidelines/label"/></a>	
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
	
	<%-- Trim unecessary white space... --%>
	<c:set target="${applicationScope.checkBoxMenus}" property="${smartMenus}">${f:replaceAll(applicationScope.checkBoxMenus[smartMenus],'\\n+\\s*\\n+',' ')}</c:set>	
	<c:set target="${applicationScope.checkBoxMenus}" property="${gradeRanges}">${f:replaceAll(applicationScope.checkBoxMenus[gradeRanges],'\\n+\\s*\\n+',' ')}</c:set>
	<c:set target="${applicationScope.checkBoxMenus}" property="${subjects}">${f:replaceAll(applicationScope.checkBoxMenus[subjects],'\\n+\\s*\\n+',' ')}</c:set>
	<c:set target="${applicationScope.checkBoxMenus}" property="${resourceTypes}">${f:replaceAll(applicationScope.checkBoxMenus[resourceTypes],'\\n+\\s*\\n+',' ')}</c:set>
	<c:set target="${applicationScope.checkBoxMenus}" property="${contentStandards}">${f:replaceAll(applicationScope.checkBoxMenus[contentStandards],'\\n+\\s*\\n+',' ')}</c:set>
	
</c:if>


<%-- Create the collections menu - cache for efficiency, 
but re-fresh occasionally to keep up-to-date with the
Collection Manager --%>
<c:choose>
	<c:when test="${empty applicationScope.collectionsRefreshCount}">
		<c:set var="collectionsRefreshCount" value="0" scope="application"/>
	</c:when>
	<c:otherwise>
		<c:set var="collectionsRefreshCount" value="${applicationScope.collectionsRefreshCount + 1}" scope="application"/>
	</c:otherwise>
</c:choose>

<c:if test="${empty applicationScope.checkBoxMenus[collections] || applicationScope.collectionsRefreshCount == 75 || param.reload == 'true'}">
	<c:set var="collectionsRefreshCount" value="0" scope="application"/>
	
	<c:set target="${applicationScope.checkBoxMenus}" property="${collections}">
		<div>
			<a 	href="javascript:toggleVisibility('Collections');" 
				title="Click to show/hide" 
				class="cb"><img src='${urlToMyDir}/images/btnExpandClsd.gif' 
				alt="Click to show/hide" border="0" hspace="5" width="11" hight="11" name='Collections_arrow'>COLLECTIONS</a>							
		</div>
		
		<%-- The collections checkbox list --%>
		<div id="Collections" style="display:none; width:100%;">
		<div style="margin-left:${indentAmount}px">
		<table style="width:100%;"><tr><td>
		<x:forEach select="$collectionsXmlDom/DDSWebService/ListCollections/collections/collection">
			<%-- Render the list items --%>
			<x:if select="contains( renderingGuidelines/noDisplay, 'false' )">
				<c:set var="searchKey"><x:out select="searchKey"/></c:set>		
				<c:if test="${not empty searchKey}">
					<div class="cbItems">					
						<c:set var="itemId" value="${searchKey}_ky_id"/>
						<input type="checkbox" id="${itemId}" name="ky" value="${searchKey}"/>
						<label for="${itemId}"><x:out select="renderingGuidelines/label"/></label><br/>						
					</div>
				</c:if>
			</x:if>
		</x:forEach>
		</td></tr></table></div></div>				
	</c:set>
	<c:set target="${applicationScope.checkBoxMenus}" property="${collections}">${f:replaceAll(applicationScope.checkBoxMenus[collections],'\\n+\\s*\\n+',' ')}</c:set>

</c:if>
