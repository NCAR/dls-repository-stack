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
				<input type="checkbox" id="${searchKey}_ky_id" name="ky" value="${searchKey}"/><x:out select="renderingGuidelines/label"/><br/>				
			</c:if>
		</x:forEach>
		</td></tr></table></div></div>				
	</c:set>

	<%-- Flag that the menus are loaded and cached --%>
	<c:set var="menusLoaded_DDSExpv11" value="true" scope="application"/>
</c:if>
