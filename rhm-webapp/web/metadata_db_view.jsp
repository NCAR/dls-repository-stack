<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/TagLibIncludes.jsp" %>
<% response.setContentType("text/html; charset=utf-8");%>
<html>
<head>
	<title>Repository Search</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<%@ include file="/head.jsp" %>
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
	
	function clearAllSelections( ) {
		window.location.href = $("searchForm").action=$("searchForm").action + "?clear=true";
	}	
	function refresh_page(page)
	{
		if(page>1)
			$("searchForm").action=$("searchForm").action + "?page="+page;
		$("searchForm").submit();
	}
	
	function displaySelections()
	{
		var selectedCollections = null
		$("Collections").select(".checkboxMenuItem").each(function(e){ 

				if(e.select('input')[0].checked)
				{	
					label = e.select('label')[0].innerHTML;
					
					if(selectedCollections==null)
						selectedCollections = label;
					else
						selectedCollections = 	selectedCollections + ", " + label;
				}
		})
		if(selectedCollections!=null)
			$("selectedCollections").update("<label>Selected Collections:</label> " + selectedCollections+"<br/><br/>");
	}
// -->
</script>
	
</head>
<body onload="javascript:displaySelections()">
	<%@ include file="/top.jsp" %>
	<h1>Repository Search</h1>

<c:set var="startingOffset" value="${empty param.s ? '0' : param.s}"/>
<c:set var="numberOnPage" value="${metadataSearchBean.resultsPerPage}"/>



<c:set var="totalReturned" value="${empty metadataEntries ? 0 : metadataEntries.rowCount}"/>

<%-- ------ Begin paging logic ------ --%>

<c:set value="nextPage()" var="nextJs" />
<c:set value="prevPage()" var="prevJs"/>


<html-el:form action="/MetadataSearch.do" styleId="searchForm">
	
	<fieldset style="width:600px">
		<legend>Search</legend>
		
		<table width="100%" >
			<tr>
				<td width="15%"><label>Search Over:</label></td>
				<td width="56%">
					<html-el:radio property="searchOver" value="target">Target</html-el:radio>
					<html-el:radio property="searchOver" value='native'>Native</html-el:radio>
					<html-el:radio property="searchOver" value='metadatahandle'>Metadata Handle</html-el:radio>
				</td>
				<td width="29%">&nbsp;</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>	
					<html-el:radio property="searchOver" value='resourcehandle'>Resource Handle</html-el:radio>
					<html-el:radio property="searchOver" value='partnerid'>Partner ID</html-el:radio>
					<html-el:radio property="searchOver" value='resourceurl'>Resource URL</html-el:radio>
				</td>
			</tr>
			<tr>
				<td colspan="2"  align="right"><input class="clearbutton" type="button" onclick="clearAllSelections();" value="Clear search form"></td>
			</tr>
			<tr>
				<td colspan="2"><html-el:text property="keyword" size="65" styleId="id_keyword"/></td>
				<td><input type="button" onclick="refresh_page(0)" value="Go" width="15%" style="width:90px;display:inline-block;"/></td>
			</tr>
			<tr>
				<td colspan="2" valign=top>
					<div>
						<nobr>
						<a href="javascript:toggleVisibility('Collections');"
						title="Click to show/hide"
						class="vocabHeading"><img src='images/btnExpandClsd.gif'
						id="Collections_img"
						alt="Click to show/hide" border="0" hspace="5" width="11" height="11">COLLECTIONS</a>
						</nobr>
						
						<c:set var="collections" value="${harvestManager.collectionsDocument}"/>
						<c:set var="indentAmount" value="18"/>
					
						<div id="Collections" style="display:none; width:100%;">
							<div style="margin-left:${indentAmount}px">
							<table style="width:100%;"><tr><td>
										<c:forEach var="collection" items="${harvestManager.ingestableCollections}">		
											<div><a href="javascript:void(0)" class="checkboxMenuItem">
												<html:multibox property="setSpec" value="${collection.setSpec}" styleId='${collection.setSpec}_ky_id'/><label for="${collection.setSpec}_ky_id">${collection.title}</label></a>
											</div> 
										</c:forEach>							
							</td></tr></table></div></div>
							<input type="hidden" name="setSpec" value="false"/> 
					</div>
				</td>
				<td valign=top>
					<label>Results per page</label>
					<br/>
					<html:select property="resultsPerPage">
						<c:forEach var="resultsPerPage" items="${metadataSearchBean.resultsPerPageOptions}">
							<html:option value="${resultsPerPage}">${resultsPerPage}</html:option>
						</c:forEach>
					</html:select>
					<br/>
					<br/>
					<a href="javascript:void(0)" class="checkboxMenuItem"><html-el:checkbox property="showResourceInfo" value="true" styleId='id_show_resource_info'/><input type='hidden' name='showResourceInfo' value='false'/><label for="id_show_resource_info">Show Resource Info</label></a>
						
				</td>
			</tr>
			
		</table>
		
	</fieldset>
	

	

</html-el:form>

<br/>
<br/>

<div id="selectedCollections"></div>

<c:set var="pager">
	<div style="padding-bottom: 5px">
		<c:if test="${(lowLimit) > 0}">
			<nobr><a href="javascript:refresh_page(${page-1})">&lt;&lt; Prev</a> &nbsp;</nobr>
		</c:if>
		Records
		${lowLimit+1}
		-
		<c:if test="${lowLimit + numberOnPage > totalCount}">
			<c:out value="${totalCount}"/>
		</c:if>
		<c:if test="${lowLimit + numberOnPage <= totalCount}">
			<c:out value="${lowLimit + numberOnPage}"/>
		</c:if>
		<c:if test="${isExactCount == 'true'}">
			out of <c:out value="${totalCount}"/>
		</c:if>
		<c:if test="${(lowLimit + numberOnPage) < totalCount}">
			<nobr>&nbsp; <a href="javascript:refresh_page(${page+1})">Next &gt;&gt;</a></nobr>
		</c:if>
	</div>
</c:set>

<%-- ------ end paging logic ------ --%>

<%-- <p>Harvest logs for UUID: ${uuid}</p> --%>
	
	<c:choose>
		<c:when test="${not empty metadataResults}">
			<div style="margin-left:6px">${pager}</div>
			<div id='results'>
			
			<c:forEach var="metadata" items="${metadataResults}" varStatus="loopStatus">
			
				<div class="metadata_header">
					<span class="metadata_header_title">
						<c:choose>
							<c:when test="${not empty metadata.title}">
								${metadata.title}
							</c:when>
							<c:otherwise>
								Metadata Record for ${metadata.metadatahandle}
							</c:otherwise>
						</c:choose>
					</span>
					<br/>
					<div><a href="${metadata.documentUrl}" target="_new">${metadata.documentUrl}</a></div>
				</div>
				<div class="metadata_result">
				<div><label><em>Partner ID:</em></label> ${metadata.partnerid}</div>
				<div><label><em>Metadata saved/updated on:</em></label> ${metadata.createddate}</div>
				<div><label><em>Metadata Handle:</em></label> ${metadata.metadatahandle}</div>
				<div><label><em>Description:</em></label> 
					<c:choose>
						<c:when test="${not empty metadata.description and fn:length(metadata.description)>1000}">
							${fn:substring(metadata.description, 0, 500)}...
						</c:when>
						<c:when test="${not empty metadata.description}">
							${metadata.description }
						</c:when>
						<c:otherwise>
							N/A
						</c:otherwise>
					</c:choose>
				</div>
				<div><label>Collection:</label> ${metadata.collectionName}</div>
                <div><label>Harvest Session ID:</label> ${metadata.sessionid}</div>
				<div><label>View Record (available native/target format):</label>
					<c:choose>
						<c:when test="${metadata.nativeformat=='lrmi'|| metadata.nativeformat=='lr_paradata'}">
							${metadata.nativeformat} [ <a href="javascript:displayRecord('${metadata.setspec}', '${metadata.metadatahandle}', 'native', 'json')">json</a> | <a href="javascript:displayRecord('${metadata.setspec}', '${metadata.metadatahandle}', 'native', 'text')">text</a>]
						</c:when>
	
						<c:otherwise>
							<a href="javascript:displayRecord('${metadata.setspec}', '${metadata.metadatahandle}', 'native', 'xml')">${metadata.nativeformat}</a>
						</c:otherwise>
					</c:choose>
					/ <a href="javascript:displayRecord('${metadata.setspec}', '${metadata.metadatahandle}', 'target', 'xml')">${metadata.targetformat}</a>
					
				</div>
				<c:if test="${metadataSearchBean.evalShowResourceInfo}">
						<div>
							<label>Resource Handle:</label>
							<c:choose>
								<c:when test="${not empty metadata.resourcehandle}">
									${metadata.resourcehandle}
								</c:when>
								<c:otherwise>
									N/A
								</c:otherwise>
							</c:choose>
						</div>
						<div>
							<label>Resource URL:</label>
							<c:choose>
								<c:when test="${not empty metadata.resourceUrl}">
									<a href='${metadata.resourceUrl}'>${metadata.resourceUrl}</a>
								</c:when>
								<c:otherwise>
									N/A
								</c:otherwise>
							</c:choose>
						</div>
				</c:if>	
				<%--<div>--%>
					<%--<label>Other Views</label>--%>
					<%--[<a href="http://ucar_connect_placeholder/resource/${metadata.metadatahandle}" target="_new">UCAR Connect</a> | <a href="http://ucar_connect_placeholder/oai_dds/admin/query.do?s=0&q=${metadata.metadatahandle}" target="_new">DDS</a>]--%>
				<%--</div>--%>
			</div>
			<br/>
			</c:forEach>
			
			<br/>
			
			</div>
			<div style="margin-left:6px">${pager}</div>
		</c:when>
		<c:otherwise>
			<div style="padding-left: 9px">No records were found.</div>
		</c:otherwise>
	</c:choose>



<c:if test="${not empty sqlDbError}">
			<div>Error:</div>
			<pre>${sqlDbError}</pre> 
	</c:if>
	<%@ include file="/bottom.jsp" %>
</body>
</html>