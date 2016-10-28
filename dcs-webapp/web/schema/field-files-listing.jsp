<c:choose>
	<c:when test="${empty fff.fieldsFiles}">
		<div>This framework has no fields files!</div>
	</c:when>
	<c:otherwise>
	
		<table>
			<tr>
				<td>
					<h4>Field File Listing</h4>
				</td>
				<td>
					<div class="small-text" id="sort-listing-menu" style="margin-left:20px">
						sort by: [ 
						<span id="sortListingByName"></span> | <span id="sortListingByPath"></span> ]
<%-- 					sort by: [ 
						<a class="field-link" href="javascript:doSortListingBy ('name');">name</a> |
						<a class="field-link" href="javascript:doSortListingBy ('path');">xpath</a> ] --%>
					</div>
				</td>
			</tr>
		</table>
		
		<ul id="fields-file-listing">
		<c:forEach var="fieldInfo" items="${fff.fieldsFiles}">
			<%-- field info is an instance of FieldInfoReader --%>
			<%-- <c:set var="fieldInfo" value="${fieldInfoEntry.value}" /> --%>
			<li>
			<div><span class="field-name">${fieldInfo.name}</span>
				[<a class="field-link" href="javascript:doBestPractices('${fieldInfo.path}', '${fff.framework.xmlFormat}');">best practices</a> |
				<a class="field-link" href="${fieldInfo.source}">source</a> ]
				<div class="path">path: ${fieldInfo.path}</div>
				<div class="path">source: ${fieldInfo.source}</div>
			</li>
		</c:forEach>
		</ul>
	</c:otherwise>
</c:choose>

